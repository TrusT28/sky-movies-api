package rustam.fadeev.sky_movies_api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final PasswordAuthInterceptor passwordAuthInterceptor;

    @Autowired
    public WebConfig(PasswordAuthInterceptor passwordAuthInterceptor) {
        this.passwordAuthInterceptor = passwordAuthInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(passwordAuthInterceptor)
                .addPathPatterns("/users/**", "/movies/**", "/ratings/**");
    }
}
