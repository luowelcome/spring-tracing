package mcp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import zipkin2.Span;
import zipkin2.reporter.Reporter;

@Profile("report-to-console")
@Configuration
public class TracingConsoleConfiguration {
    @Bean
    Reporter<Span> spanReporter() {
        return Reporter.CONSOLE;
    }
}
