package walker.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestService {

    static final Logger logger = LoggerFactory.getLogger(TestService.class);

    private String profile;

    public TestService(String profile) {
        this.profile = profile;
    }

    public void hello(String name) {
        logger.info(this.profile + "\t" + name);
    }

}
