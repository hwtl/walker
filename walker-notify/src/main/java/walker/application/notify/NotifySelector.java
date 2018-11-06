package walker.application.notify;

import com.dangdang.ddframe.job.api.ShardingContext;
import walker.application.notify.entity.WalkerNotifyExample;

public interface NotifySelector {

    WalkerNotifyExample buildSelectNotifyExample(ShardingContext shardingContext);

}
