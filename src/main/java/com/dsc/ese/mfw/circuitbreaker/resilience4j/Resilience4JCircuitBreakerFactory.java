package com.dsc.ese.mfw.circuitbreaker.resilience4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;

import com.dsc.ese.mfw.commons.circuitbreaker.CircuitBreakerFactory;
import com.dsc.ese.mfw.commons.circuitbreaker.Customizer;
import org.springframework.util.Assert;

public class Resilience4JCircuitBreakerFactory extends
		CircuitBreakerFactory<Resilience4JConfigBuilder.Resilience4JCircuitBreakerConfiguration, Resilience4JConfigBuilder> {

	private Function<String, Resilience4JConfigBuilder.Resilience4JCircuitBreakerConfiguration> defaultConfiguration = id -> new Resilience4JConfigBuilder(
			id).circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
					.timeLimiterConfig(TimeLimiterConfig.ofDefaults()).build();

	private CircuitBreakerRegistry circuitBreakerRegistry = CircuitBreakerRegistry.ofDefaults();

	private ExecutorService executorService = Executors.newCachedThreadPool();

	private Map<String, Customizer<CircuitBreaker>> circuitBreakerCustomizers = new HashMap<>();

	@Override
	protected Resilience4JConfigBuilder configBuilder(String id) {
		return new Resilience4JConfigBuilder(id);
	}

	@Override
	public void configureDefault(
			Function<String, Resilience4JConfigBuilder.Resilience4JCircuitBreakerConfiguration> defaultConfiguration) {
		this.defaultConfiguration = defaultConfiguration;
	}

	public void configureCircuitBreakerRegistry(CircuitBreakerRegistry registry) {
		this.circuitBreakerRegistry = registry;
	}

	protected CircuitBreakerRegistry getCircuitBreakerRegistry() {
		return circuitBreakerRegistry;
	}

	public void configureExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}

	@Override
	public Resilience4JCircuitBreaker create(String id) {
		Assert.hasText(id, "A CircuitBreaker must have an id.");
		Resilience4JConfigBuilder.Resilience4JCircuitBreakerConfiguration config = getConfigurations()
				.computeIfAbsent(id, defaultConfiguration);
		return new Resilience4JCircuitBreaker(id, config.getCircuitBreakerConfig(), config.getTimeLimiterConfig(),
				circuitBreakerRegistry, executorService, Optional.ofNullable(circuitBreakerCustomizers.get(id)));
	}

	public void addCircuitBreakerCustomizer(Customizer<CircuitBreaker> customizer, String... ids) {
		for (String id : ids) {
			circuitBreakerCustomizers.put(id, customizer);
		}
	}

}
