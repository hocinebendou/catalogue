package bio.tech.catalog.spring;

import bio.tech.catalog.constraints.EmailValidator;
import bio.tech.catalog.constraints.PasswordMatchesValidator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.Locale;

@Configuration
@ComponentScan(basePackages = {"bio.tech.catalog.web"})
@EnableWebMvc
public class MvcConfig extends WebMvcConfigurerAdapter {

    public MvcConfig() { super(); }

    @Autowired
    private MessageSource messageSource;

    @Override
    public void addViewControllers(final ViewControllerRegistry registry) {
        registry.addViewController("/");
        registry.addViewController("/login");
        registry.addViewController("/about.html");
        registry.addViewController("/registration.html");
        registry.addViewController("/successRegister.html");
        registry.addViewController("/invalidSession.html");
        registry.addViewController("/forgetPassword.html");
        registry.addViewController("/updatePassword.html");
        registry.addViewController("/changePassword.html");
        registry.addViewController("/logout.html");
        registry.addViewController("/filter.html");
        registry.addViewController("/dbac.html");
    }

    @Override
    public void configureDefaultServletHandling(final DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**").addResourceLocations("/", "/resources/");
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        final LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("lang");
        registry.addInterceptor(localeChangeInterceptor);
    }

    @Bean
    public LocaleResolver localeResolver() {
        final CookieLocaleResolver cookieLocaleResolver = new CookieLocaleResolver();
        cookieLocaleResolver.setDefaultLocale(Locale.ENGLISH);
        return cookieLocaleResolver;
    }

    @Bean
    public EmailValidator usernameValidator() { return new EmailValidator(); }

    @Bean
    public PasswordMatchesValidator passwordMatchesValidator() { return new PasswordMatchesValidator(); }

    @Bean
    @ConditionalOnMissingBean(RequestContextListener.class)
    public RequestContextListener requestContextListener() { return new RequestContextListener(); }

    @Override
    public Validator getValidator() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.setValidationMessageSource(messageSource);
        return validator;
    }
}

