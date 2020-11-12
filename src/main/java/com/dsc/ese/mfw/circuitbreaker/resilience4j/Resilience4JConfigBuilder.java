package com.dsc.ese.mfw.circuitbreaker.resilience4j;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;

import com.dsc.ese.mfw.commons.circuitbreaker.ConfigBuilder;

public class Resilience4JConfigBuilder
		implements ConfigBuilder<Resilience4JConfigBuilder.Resilience4JCircuitBreakerConfiguration> {

	private String id;

	private TimeLimiterConfig timeLimiterConfig = TimeLimiterConfig.ofDefaults();

	private CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.ofDefaults();

	public Resilience4JConfigBuilder(String id) {
		this.id = id;
	}

	public Resilience4JConfigBuilder timeLimiterConfig(TimeLimiterConfig config) {
		this.timeLimiterConfig = config;
		return this;
	}

	public Resilience4JConfigBuilder circuitBreakerConfig(CircuitBreakerConfig circuitBreakerConfig) {
		this.circuitBreakerConfig = circuitBreakerConfig;
		return this;
	}

	@Override
	public Resilience4JCircuitBreakerConfiguration build() {
		Resilience4JCircuitBreakerConfiguration config = new Resilience4JCircuitBreakerConfiguration();
		config.setId(id);
		config.setCircuitBreakerConfig(circuitBreakerConfig);
		config.setTimeLimiterConfig(timeLimiterConfig);
		return config;
	}

	public static class Resilience4JCircuitBreakerConfiguration {

		private String id;

		private TimeLimiterConfig timeLimiterConfig;

		private CircuitBreakerConfig circuitBreakerConfig;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public TimeLimiterConfig getTimeLimiterConfig() {
			return timeLimiterConfig;
		}

		public void setTimeLimiterConfig(TimeLimiterConfig timeLimiterConfig) {
			this.timeLimiterConfig = timeLimiterConfig;
		}

		public CircuitBreakerConfig getCircuitBreakerConfig() {
			return circuitBreakerConfig;
		}

		public void setCircuitBreakerConfig(CircuitBreakerConfig circuitBreakerConfig) {
			this.circuitBreakerConfig = circuitBreakerConfig;
		}

	}

}
