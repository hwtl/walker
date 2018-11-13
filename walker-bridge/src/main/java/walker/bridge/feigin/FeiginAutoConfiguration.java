package walker.bridge.feigin;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import walker.common.WalkerConst;
import walker.common.context.WalkerContextlManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
@ConditionalOnClass({javax.servlet.http.HttpServletRequest.class, feign.RequestTemplate.class})
@ConditionalOnExpression("'${walker.feigin.enabled}'=='true'")
public class FeiginAutoConfiguration extends WebMvcConfigurationSupport {

    @Bean
    public HandlerInterceptor walkerRestInterceptor() {
        return new HandlerInterceptor(){
            @Override
            public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler) throws Exception {
                String walkerMasterGid = httpServletRequest.getHeader(WalkerConst.WALKER_MASTER_GID);
                if (walkerMasterGid != null) {
                    WalkerContextlManager.inherit(walkerMasterGid);
                }
                return true;
            }

            @Override
            public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler, ModelAndView modelAndView) throws Exception {

            }

            /**
             * 控制器方法抛不抛异常都会被调用
             *
             * @param httpServletRequest
             * @param httpServletResponse
             * @param o
             * @param e
             * @throws Exception
             */
            @Override
            public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
                WalkerContextlManager.clear();
            }
        };
    }

    @Bean
    public WalkerFeignInterceptor walkerFeignInterceptor() {
        return new WalkerFeignInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(walkerRestInterceptor());
    }

}
