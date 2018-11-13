package walker.bridge.dubbo;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import com.alibaba.dubbo.config.spring.context.annotation.EnableDubboConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({ EnableDubbo.class, EnableDubboConfig.class})
@ConditionalOnExpression("'${walker.dubbo.enabled}'=='true'")
public class DubboAutoConfiguration {

    @Bean
    public WalkerDubboConsumerFilter walkerDubboConsumerFilter(){
        return new WalkerDubboConsumerFilter();
    }

    @Bean
    public WalkerDubboProviderFilter walkerDubboProviderFilter(){
        return new WalkerDubboProviderFilter();
    }


}