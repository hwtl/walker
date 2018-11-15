package walker.logger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class TestConfiguration {

    @Bean
    @Profile("dev")
    public TestService testService1(){
        return new TestService("dev");
    }

    @Bean
    @Profile("prod")
    public TestService testService2(){
        return new TestService("prod");
    }


}
