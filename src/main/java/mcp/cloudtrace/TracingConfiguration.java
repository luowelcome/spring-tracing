package mcp.cloudtrace;

import brave.Tracing;
import brave.context.slf4j.MDCCurrentTraceContext;
import brave.http.HttpTracing;
import brave.propagation.B3Propagation;
import brave.propagation.ExtraFieldPropagation;
import brave.sampler.Sampler;
import brave.spring.web.TracingClientHttpRequestInterceptor;
import brave.spring.webmvc.TracingHandlerInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import zipkin2.Span;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Sender;
import zipkin2.reporter.okhttp3.OkHttpSender;

/**
 * This adds tracing configuration to any web mvc controllers or rest template clients. This should
 * be configured last.
 */
@Configuration
class TracingConfiguration {

	@Bean
	RestTemplate restTemplate(HttpTracing tracing) {
		return new RestTemplateBuilder()
				.interceptors(TracingClientHttpRequestInterceptor.create(tracing))
				.build();
	}

	@Bean
	HandlerInterceptor serverInterceptor(HttpTracing tracing) {
		return TracingHandlerInterceptor.create(tracing);
	}

	/**
	 * Configuration for how to send spans to Zipkin
	 */
	@Bean
	Sender sender(@Value("${mcp.zipkin.url}") String zipkinSenderUrl) {
		return OkHttpSender.create(zipkinSenderUrl);
	}

	/**
	 * Configuration for how to buffer spans into messages for Zipkin
	 */
	@Bean
	AsyncReporter<Span> spanReporter(Sender sender) {
		return AsyncReporter.create(sender);
	}

	/**
	 * Put all of the application-specific tracing priorities in this method
	 * Controls aspects of tracing such as the name that shows up in the UI
	 */
	@Bean
	Tracing tracing(@Value("${mcp:spring-tracing}") String serviceName,
	                AsyncReporter<Span> spanAsyncReporter) {
		return Tracing.newBuilder()
				.sampler(Sampler.ALWAYS_SAMPLE)
				.localServiceName(serviceName)
				.propagationFactory(ExtraFieldPropagation
						.newFactory(B3Propagation.FACTORY, "client-id"))
				// puts trace IDs into logs
				//.currentTraceContext(ThreadContextCurrentTraceContext.create())
				// subclassed to enable MDC synchronizing
				.currentTraceContext(MDCCurrentTraceContext.create())
				.spanReporter(spanAsyncReporter)
				.build();
	}

	// decides how to name and tag spans. By default they are named the same as the http method.
	@Bean
	HttpTracing httpTracing(Tracing tracing) {
		return HttpTracing.create(tracing);
	}


	@Configuration
	public static class MyWebConfig extends WebMvcConfigurerAdapter {

		private final HandlerInterceptor serverInterceptor;

		public MyWebConfig(HandlerInterceptor serverInterceptor) {
			this.serverInterceptor = serverInterceptor;
		}

		@Override
		public void addInterceptors(InterceptorRegistry registry) {
			registry.addInterceptor(this.serverInterceptor);
		}
	}
}
