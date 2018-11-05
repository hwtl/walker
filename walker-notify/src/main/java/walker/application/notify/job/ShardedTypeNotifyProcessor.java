package walker.application.notify.job;

import com.dangdang.ddframe.job.api.ShardingContext;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import walker.application.notify.entity.WalkerNotify;
import walker.application.notify.entity.WalkerNotifyExample;

import java.util.List;

@Slf4j
@Data
public class ShardedTypeNotifyProcessor extends ShardedNotifyProcessor {

    protected Integer shardedNotifyType;

    @Override
    public List<WalkerNotify> fetchData(ShardingContext shardingContext) {
        return super.fetchData(shardingContext);
    }

    @Override
    public WalkerNotifyExample buildSelectNotifyExample() {
        WalkerNotifyExample wrapper = super.buildSelectNotifyExample();
        WalkerNotifyExample.Criteria criteria = wrapper.createCriteria();
        criteria.andNotifyTypeEqualTo(shardedNotifyType);
        return wrapper;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        if (shardedNotifyType == null) {
            throw new IllegalArgumentException("shardedNotifyType can't be null");
        }
        if (shardedNotifyType < 0) {
            throw new IllegalArgumentException("shardedNotifyType can't less than 0");
        }
    }
}
