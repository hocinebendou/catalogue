package bio.tech.ystr.spring;

import bio.tech.ystr.security.ActiveUserStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public ActiveUserStore activeUserStore() {
        return new ActiveUserStore();
    }
}
