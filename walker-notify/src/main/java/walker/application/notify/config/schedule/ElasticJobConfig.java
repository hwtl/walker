package walker.application.notify.config.schedule;


import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import walker.application.notify.job.FetchWaiteStatusProcessor;
import walker.application.notify.job.ShardedNotifyProcessor;
import walker.application.notify.job.ShardedTypeNotifyProcessor;


@Configuration
public class ElasticJobConfig {

    @Value("${walker.notify.zkServerLists}")
    private String zkServerLists;

    @Value("${walker.notify.namespace}")
    private String namespace;

    @Value("${walker.notify.maxRetries}")
    private Integer maxRetries;

    @Bean
    public ZookeeperConfiguration zkConfig() {
        ZookeeperConfiguration zookeeperConfiguration = new ZookeeperConfiguration(zkServerLists, namespace);
        zookeeperConfiguration.setMaxRetries(maxRetries);
        return zookeeperConfiguration;
    }

    @Bean(initMethod = "init")
    public ZookeeperRegistryCenter regCenter(ZookeeperConfiguration zkConfig) {
        return new ZookeeperRegistryCenter(zkConfig);
    }

    @Bean(name = "fetchWaiteStatusProcessor")
    @Primary
    public FetchWaiteStatusProcessor fetchWaiteStatusProcessor(){
        return new FetchWaiteStatusProcessor();
    }

    @Bean(name = "shardedNotifyProcessor_0")
    @Primary
    public ShardedNotifyProcessor shardedNotifyProcessor_0(){
        return new ShardedNotifyProcessor();
    }

    @Bean(name = "shardedNotifyProcessor_1")
    public ShardedNotifyProcessor shardedNotifyProcessor_1(){
        return new ShardedNotifyProcessor();
    }

    @Bean(name = "shardedNotifyProcessor_2")
    public ShardedNotifyProcessor shardedNotifyProcessor_2(){
        return new ShardedNotifyProcessor();
    }

    @Bean(name = "shardedTypeNotifyProcessor_0")
    public ShardedTypeNotifyProcessor shardedTypeNotifyProcessor_0(){
        return new ShardedTypeNotifyProcessor();
    }

    @Bean(name = "shardedTypeNotifyProcessor_1")
    public ShardedTypeNotifyProcessor shardedTypeNotifyProcessor_1(){
        return new ShardedTypeNotifyProcessor();
    }

    @Bean(name = "shardedTypeNotifyProcessor_2")
    public ShardedTypeNotifyProcessor shardedTypeNotifyProcessor_2(){
        return new ShardedTypeNotifyProcessor();
    }

}
