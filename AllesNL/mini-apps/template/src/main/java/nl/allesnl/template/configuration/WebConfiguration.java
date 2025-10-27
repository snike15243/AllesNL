package nl.allesnl.template.configuration;

import nl.allesnl.template.component.InternalAuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
class WebConfiguration implements WebMvcConfigurer {

    private final InternalAuthInterceptor internalAuthInterceptor;

    WebConfiguration(InternalAuthInterceptor internalAuthInterceptor) {
        this.internalAuthInterceptor = internalAuthInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(internalAuthInterceptor).addPathPatterns("/internal/**");
    }
}
