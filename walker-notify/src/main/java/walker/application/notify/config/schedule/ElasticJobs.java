package walker.application.notify.config.schedule;

import com.dangdang.ddframe.job.api.dataflow.DataflowJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.lite.api.JobScheduler;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import walker.application.notify.job.FetchWaiteStatusProcessor;
import walker.application.notify.job.ShardedNotifyProcessor;
import walker.application.notify.job.ShardedTypeNotifyProcessor;

import javax.annotation.Resource;

@Configuration
@AutoConfigureAfter(ElasticJobConfig.class)
public class ElasticJobs {

    @Autowired
    private ZookeeperRegistryCenter regCenter;

    @Resource
    @Qualifier("fetchWaiteStatusProcessor")
    public FetchWaiteStatusProcessor fetchWaiteStatusProcessor;

    @Resource
    @Qualifier("shardedNotifyProcessor_0")
    public ShardedNotifyProcessor shardedNotifyProcessor_0;

    @Resource
    @Qualifier("shardedNotifyProcessor_1")
    public ShardedNotifyProcessor shardedNotifyProcessor_1;

    @Resource
    @Qualifier("shardedNotifyProcessor_2")
    public ShardedNotifyProcessor shardedNotifyProcessor_2;

    @Resource
    @Qualifier("shardedTypeNotifyProcessor_0")
    public ShardedTypeNotifyProcessor shardedTypeNotifyProcessor_0;

    @Resource
    @Qualifier("shardedTypeNotifyProcessor_1")
    public ShardedTypeNotifyProcessor shardedTypeNotifyProcessor_1;

    @Resource
    @Qualifier("shardedTypeNotifyProcessor_2")
    public ShardedTypeNotifyProcessor shardedTypeNotifyProcessor_2;

    /**
     * 
     * @param cron
     * @param shardingCount
     * @param shardingItemParameters
     * @return
     */
    @Bean(name = "settlementJobScheduler0",initMethod = "init")
    public JobScheduler fetchConvertJobScheduler(@Value("${fetch_waite_status_job.cron}") final String cron,
                                                @Value("${fetch_waite_status_job.shardingCount}") int shardingCount,
                                                @Value("${fetch_waite_status_job.shardingItemParameters}") String shardingItemParameters) {
        return new SpringJobScheduler(fetchWaiteStatusProcessor, regCenter, getLiteJobConfiguration(fetchWaiteStatusProcessor.getClass(), cron, shardingCount, shardingItemParameters));
    }

    /**
     *
     * @param cron
     * @param shardingCount 3
     * @param shardingItemParameters 0=0,1=1,2=2  即表示这个任务扫walker_notify_0,walker_notify_1,walker_notify_2 这3张表的
     * @return
     */
    @Bean(name = "settlementJobScheduler0",initMethod = "init")
    public JobScheduler settlementJobScheduler0(@Value("${shardedNotifyProcessor_0.cron}") final String cron,
                                               @Value("${shardedNotifyProcessor_0.shardingCount}") int shardingCount,
                                               @Value("${shardedNotifyProcessor_0.shardingItemParameters}") String shardingItemParameters) {
        return new SpringJobScheduler(shardedNotifyProcessor_0, regCenter, getLiteJobConfiguration(shardedNotifyProcessor_0.getClass(), cron, shardingCount, shardingItemParameters));
    }

    /**
     *
     * @param cron
     * @param shardingCount 3
     * @param shardingItemParameters 0=3,1=4,2=5
     * @return
     */
    @Bean(name = "settlementJobScheduler1",initMethod = "init")
    public JobScheduler settlementJobScheduler1(@Value("${shardedNotifyProcessor_1.cron}") final String cron,
                                               @Value("${shardedNotifyProcessor_1.shardingCount}") int shardingCount,
                                               @Value("${shardedNotifyProcessor_1.shardingItemParameters}") String shardingItemParameters) {
        return new SpringJobScheduler(shardedNotifyProcessor_1, regCenter, getLiteJobConfiguration(shardedNotifyProcessor_1.getClass(), cron, shardingCount, shardingItemParameters));
    }

    @Bean(name = "settlementJobScheduler2",initMethod = "init")
    public JobScheduler settlementJobScheduler2(@Value("${shardedNotifyProcessor_2.cron}") final String cron,
                                               @Value("${shardedNotifyProcessor_2.shardingCount}") int shardingCount,
                                               @Value("${shardedNotifyProcessor_2.shardingItemParameters}") String shardingItemParameters) {
        return new SpringJobScheduler(shardedNotifyProcessor_2, regCenter, getLiteJobConfiguration(shardedNotifyProcessor_2.getClass(), cron, shardingCount, shardingItemParameters));
    }

    /**
     * 指定推送类型的任务
     * @param cron
     * @param shardingCount
     * @param shardingItemParameters
     * @param jobParameter 推送类型
     * @return
     */
    @Bean(name = "settlementNotifyWithTypeJobScheduler0",initMethod = "init")
    public JobScheduler settlementNotifyWithTypeJobScheduler0(@Value("${shardedWithTypeNotifyProcessor_0.cron}") final String cron,
                                                @Value("${shardedWithTypeNotifyProcessor_0.shardingCount}") int shardingCount,
                                                @Value("${shardedWithTypeNotifyProcessor_0.shardingItemParameters}") String shardingItemParameters,
                                                @Value("${shardedWithTypeNotifyProcessor_0.jobParameter}") String jobParameter) {
        return new SpringJobScheduler(shardedTypeNotifyProcessor_0, regCenter, getLiteJobConfigurationWithParameter(shardedTypeNotifyProcessor_0.getClass(), cron, shardingCount, shardingItemParameters,jobParameter));
    }

    @Bean(name = "settlementNotifyWithTypeJobScheduler1",initMethod = "init")
    public JobScheduler settlementNotifyWithTypeJobScheduler1(@Value("${shardedWithTypeNotifyProcessor_1.cron}") final String cron,
                                                              @Value("${shardedWithTypeNotifyProcessor_1.shardingCount}") int shardingCount,
                                                              @Value("${shardedWithTypeNotifyProcessor_1.shardingItemParameters}") String shardingItemParameters,
                                                              @Value("${shardedWithTypeNotifyProcessor_1.jobParameter}") String jobParameter) {
        return new SpringJobScheduler(shardedTypeNotifyProcessor_1, regCenter, getLiteJobConfigurationWithParameter(shardedTypeNotifyProcessor_1.getClass(), cron, shardingCount, shardingItemParameters,jobParameter));
    }

    @Bean(name = "settlementNotifyWithTypeJobScheduler2",initMethod = "init")
    public JobScheduler settlementNotifyWithTypeJobScheduler2(@Value("${shardedWithTypeNotifyProcessor_2.cron}") final String cron,
                                                              @Value("${shardedWithTypeNotifyProcessor_2.shardingCount}") int shardingCount,
                                                              @Value("${shardedWithTypeNotifyProcessor_2.shardingItemParameters}") String shardingItemParameters,
                                                              @Value("${shardedWithTypeNotifyProcessor_2.jobParameter}") String jobParameter) {
        return new SpringJobScheduler(shardedTypeNotifyProcessor_2, regCenter, getLiteJobConfigurationWithParameter(shardedTypeNotifyProcessor_2.getClass(), cron, shardingCount, shardingItemParameters,jobParameter));
    }

    private LiteJobConfiguration getLiteJobConfiguration(final Class<? extends DataflowJob> jobClass, final String cron, final int shardingTotalCount, final String shardingItemParameters) {
        return LiteJobConfiguration.newBuilder(new SimpleJobConfiguration(
                JobCoreConfiguration.newBuilder(jobClass.getName(), cron, shardingTotalCount)
                .shardingItemParameters(shardingItemParameters).build(), jobClass.getCanonicalName())).overwrite(true).build();
    }

    private LiteJobConfiguration getLiteJobConfigurationWithParameter(final Class<? extends DataflowJob> jobClass, final String cron, final int shardingTotalCount, final String shardingItemParameters, String jobParameter) {
        return LiteJobConfiguration.newBuilder(new SimpleJobConfiguration(
                JobCoreConfiguration.newBuilder(jobClass.getName(), cron, shardingTotalCount).jobParameter(jobParameter)
                        .shardingItemParameters(shardingItemParameters).build(), jobClass.getCanonicalName())).overwrite(true).build();
    }

}
