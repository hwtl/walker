package walker.application.notify.job;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.dataflow.DataflowJob;
import com.github.pagehelper.PageHelper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import walker.application.notify.CoordinatorConst;
import walker.application.notify.NotifySelector;
import walker.application.notify.config.schedule.NotifyScheduleConst;
import walker.application.notify.entity.WalkerNotify;
import walker.application.notify.entity.WalkerNotifyExample;
import walker.application.notify.entity.WalkerTransaction;
import walker.application.notify.entity.WalkerTransactionExample;
import walker.application.notify.mapper.WalkerNotifyMapper;
import walker.application.notify.mapper.WalkerTransactionMapper;
import walker.common.util.Utility;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import static walker.application.notify.CoordinatorConst.NOTIFY_SUCCESS_CODE;
import static walker.application.notify.CoordinatorConst.NOTIFY_SUCCESS_KEY;
import static walker.application.notify.CoordinatorConst.NotifyStatus.*;

@Slf4j
@Data
public class ShardedNotifyProcessor implements NotifySelector, DataflowJob<WalkerNotify> , InitializingBean {

    @Resource
    private WalkerTransactionMapper walkerTransactionMapper;

    @Resource
    private WalkerNotifyMapper walkerNotifyMapper;

    private RestTemplate restTemplate = new RestTemplate();

    protected Integer shardedTableIndex;

    @Override
    public List<WalkerNotify> fetchData(ShardingContext shardingContext) {

        sleep(NotifyScheduleConst.INTERNAL_SLEEP_MICROSECONDS);

        PageHelper.startPage(1, NotifyScheduleConst.INTERNAL_NOTIFY_FETCH_SIZE);

        return walkerNotifyMapper.selectIndexedTableByExample(shardedTableIndex, buildSelectNotifyExample());
    }

    @Override
    public void processData(ShardingContext shardingContext, List<WalkerNotify> list) {
        if (!CollectionUtils.isEmpty(list)) {
            for (WalkerNotify notify : list) {
                // 应当从redis中获取notify是notifyStatus
                int redisCachedNotifyStatus = notify.getNotifyStatus();
                boolean notifyHasNoLocker = redisCachedNotifyStatus != NOTIFYING;
                if (!notifyHasNoLocker) {
                    log.info(" execute redis.lock({}), lock condition must use global id", notify.getId());
                    process(notify, shardingContext);
                }
            }
        }
    }

    protected void process(WalkerNotify notify, ShardingContext shardingContext) {

        String notifyLockId = ("redis.notify.lock(" + notify.getId() + ")");
        if (StringUtils.isNotEmpty(notifyLockId)) {
            // get notify lock
            try {
                WalkerNotify updateEntity = new WalkerNotify();
                updateEntity.setId(notify.getId());

                Map<String, Object> notifyResponse = doNotify(notify);
                // todo append callback string to url
                log.info("doNotify masterGid:{}, branchGFid:{} URL:{}, BODY:{}, response:{}", notify.getMasterGid(),
                        notify.getBranchGid(), notify.getNotifyUrl(), notify.getNotifyBody(), notifyResponse);
                if (notifyResponse != null) {
                    String returnCode = (String)notifyResponse.get(NOTIFY_SUCCESS_KEY);
                    if (StringUtils.isNoneEmpty(returnCode)) {
                        if (returnCode.equals(NOTIFY_SUCCESS_CODE)) {
                            updateEntity.setNotifyStatus(NOTIFY_SUCCESS);

                            Integer updateTransactionTxStatus = null;
                            if (notify.getNotifyType() == CoordinatorConst.NotifyType.COMMIT.ordinal()) {
                                updateTransactionTxStatus = CoordinatorConst.TransactionTxStatus.COMMITED;
                            } else if (notify.getNotifyType() == CoordinatorConst.NotifyType.CANCEL.ordinal()) {
                                updateTransactionTxStatus = CoordinatorConst.TransactionTxStatus.CANCELED;
                            }
                            updateWalkerTransactionTxStatus(notify, updateTransactionTxStatus);
                        } else {
                            if (notify.getRetryNum() > CoordinatorConst.NOTIFY_RETRY_MAX) {
                                // todo think if notify failure, how to process transaction row txStatus
                                updateEntity.setNotifyStatus(NOTIFY_FAILURE);
                            } else {
                                updateEntity.setRetryNum(notify.getRetryNum() + 1);
                            }
                        }
                    } else {
                        log.info(
                                "doNotify masterGid:{}, branchGFid:{} response not contains key returnCode, will retry");
                        updateEntity.setRetryNum(notify.getRetryNum() + 1);
                    }
                } else {
                    log.info("doNotify response empty, will retry");
                    updateEntity.setRetryNum(notify.getRetryNum() + 1);
                }
                walkerNotifyMapper.updateIndexedTableByPrimaryKeySelective(shardingContext.getShardingItem(),
                        updateEntity);
            } catch (Exception e) {
                log.error("notifyJob process error", e);
            } finally {
                log.info("redis.notify.unlock({})", notifyLockId);
            }
        }
    }

    @Transactional
    public Map<String, Object> doNotify(WalkerNotify notify) {
        return restTemplate.postForObject(notify.getNotifyUrl(), notify.getNotifyBody(), Map.class);
    }

    private int getNotifyTransactionTableIndex(WalkerNotify notify) {
        return System.identityHashCode(notify.getMasterGid()) % CoordinatorConst.TRANSACTION_SHARDING_COUNT;
    }

    private int updateWalkerTransactionTxStatus(WalkerNotify notify, Integer updateTxStatus) {
        int targetTransactionTableIndex = getNotifyTransactionTableIndex(notify);
        WalkerTransaction transaction = new WalkerTransaction();
        transaction.setTxStatus(updateTxStatus);
        transaction.setGmtModified(Utility.unix_timestamp());
        WalkerTransactionExample transactionExample = new WalkerTransactionExample();
        transactionExample.createCriteria().andAppIdEqualTo(notify.getAppId()).andMasterGidEqualTo(notify.getMasterGid()).andBranchGidEqualTo(notify.getBranchGid());
        return walkerTransactionMapper.updateIndexedTableByExampleSelective(targetTransactionTableIndex, transaction, transactionExample);
    }

    void sleep(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            log.error("{} interrupted", getClass().getName(), e);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (shardedTableIndex == null) {
            throw new IllegalArgumentException("shardedTableIndex can't be null");
        }
        if (shardedTableIndex < 0) {
            throw new IllegalArgumentException("shardedTableIndex can't less than 0");
        }
    }

    @Override
    public WalkerNotifyExample buildSelectNotifyExample() {
        WalkerNotifyExample waiteToNotifyExample = new WalkerNotifyExample();
        WalkerNotifyExample.Criteria criteria = waiteToNotifyExample.createCriteria();
        long createTimeBeginFilter = Utility.unix_timestamp() - CoordinatorConst.PROCESS_RECENT_DAY * CoordinatorConst.SECONDS_OF_DAY;
        criteria.andGmtCreateGreaterThanOrEqualTo(createTimeBeginFilter).andNotifyStatusEqualTo(WAITING_EXECUTE);
        return waiteToNotifyExample;
    }
}
