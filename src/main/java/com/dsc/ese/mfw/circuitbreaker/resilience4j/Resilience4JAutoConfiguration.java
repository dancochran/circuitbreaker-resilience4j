package com.dsc.ese.mfw.circuitbreaker.resilience4j;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import io.github.resilience4j.micrometer.tagged.TaggedCircuitBreakerMetrics;
import io.micrometer.core.instrument.MeterRegistry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import com.dsc.ese.mfw.commons.circuitbreaker.CircuitBreakerFactory;
import com.dsc.ese.mfw.commons.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = { "mfw.circuitbreaker.resilience4j.enabled",
		"mfw.circuitbreaker.resilience4j.blocking.enabled" }, matchIfMissing = true)
public class Resilience4JAutoConfiguration {

	@Autowired(required = false)
	private List<Customizer<Resilience4JCircuitBreakerFactory>> customizers = new ArrayList<>();

	@Bean
	@ConditionalOnMissingBean(CircuitBreakerFactory.class)
	public Resilience4JCircuitBreakerFactory resilience4jCircuitBreakerFactory() {
		Resilience4JCircuitBreakerFactory factory = new Resilience4JCircuitBreakerFactory();
		customizers.forEach(customizer -> customizer.customize(factory));
		return factory;
	}

	@Configuration(proxyBeanMethods = false)
	@ConditionalOnBean({ MeterRegistry.class })
	@ConditionalOnClass(name = { "io.github.resilience4j.micrometer.tagged.TaggedCircuitBreakerMetrics" })
	public static class MicrometerResilience4JCustomizerConfiguration {

		@Autowired(required = false)
		private Resilience4JCircuitBreakerFactory factory;

		@Autowired
		private MeterRegistry meterRegistry;

		@PostConstruct
		public void init() {
			if (factory != null) {
				TaggedCircuitBreakerMetrics.ofCircuitBreakerRegistry(factory.getCircuitBreakerRegistry())
						.bindTo(meterRegistry);
			}
		}

	}

}
