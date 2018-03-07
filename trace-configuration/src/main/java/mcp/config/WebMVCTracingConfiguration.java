package mcp.config;

import brave.Tracing;
import brave.http.HttpTracing;
import brave.spring.web.TracingClientHttpRequestInterceptor;
import brave.spring.webmvc.TracingHandlerInterceptor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Profile("web")
@Configuration
public class WebMVCTracingConfiguration extends WebMvcConfigurerAdapter {
    private HttpTracing httpTracing;

    public WebMVCTracingConfiguration(Tracing tracing) {
        this.httpTracing = HttpTracing.create(tracing);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(TracingHandlerInterceptor.create(httpTracing));
    }
}
