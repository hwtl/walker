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

    @Override
    public List<WalkerNotify> fetchData(ShardingContext shardingContext) {
       return super.fetchData(shardingContext);
    }

    @Override
    public WalkerNotifyExample buildSelectNotifyExample(ShardingContext shardingContext) {
        WalkerNotifyExample wrapper = super.buildSelectNotifyExample(shardingContext);
        WalkerNotifyExample.Criteria criteria = wrapper.createCriteria();
        criteria.andNotifyTypeEqualTo(Integer.parseInt(shardingContext.getJobParameter()));
        return wrapper;
    }

}
