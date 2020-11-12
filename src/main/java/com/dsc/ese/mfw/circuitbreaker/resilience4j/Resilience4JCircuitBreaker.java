package com.dsc.ese.mfw.circuitbreaker.resilience4j;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.function.Supplier;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.vavr.control.Try;

import com.dsc.ese.mfw.commons.circuitbreaker.CircuitBreaker;
import com.dsc.ese.mfw.commons.circuitbreaker.Customizer;

public class Resilience4JCircuitBreaker implements CircuitBreaker {

	private String id;

	private io.github.resilience4j.circuitbreaker.CircuitBreakerConfig circuitBreakerConfig;

	private CircuitBreakerRegistry registry;

	private TimeLimiterConfig timeLimiterConfig;

	private ExecutorService executorService;

	private Optional<Customizer<io.github.resilience4j.circuitbreaker.CircuitBreaker>> circuitBreakerCustomizer;

	public Resilience4JCircuitBreaker(String id,
			io.github.resilience4j.circuitbreaker.CircuitBreakerConfig circuitBreakerConfig,
			TimeLimiterConfig timeLimiterConfig, CircuitBreakerRegistry circuitBreakerRegistry,
			ExecutorService executorService,
			Optional<Customizer<io.github.resilience4j.circuitbreaker.CircuitBreaker>> circuitBreakerCustomizer) {
		this.id = id;
		this.circuitBreakerConfig = circuitBreakerConfig;
		this.registry = circuitBreakerRegistry;
		this.timeLimiterConfig = timeLimiterConfig;
		this.executorService = executorService;
		this.circuitBreakerCustomizer = circuitBreakerCustomizer;
	}

	@Override
	public <T> T run(Supplier<T> toRun, Function<Throwable, T> fallback) {
		TimeLimiter timeLimiter = TimeLimiter.of(timeLimiterConfig);
		Supplier<Future<T>> futureSupplier = () -> executorService.submit(toRun::get);
		Callable restrictedCall = TimeLimiter.decorateFutureSupplier(timeLimiter, futureSupplier);

		io.github.resilience4j.circuitbreaker.CircuitBreaker defaultCircuitBreaker = registry.circuitBreaker(id,
				circuitBreakerConfig);
		circuitBreakerCustomizer.ifPresent(customizer -> customizer.customize(defaultCircuitBreaker));
		Callable<T> callable = io.github.resilience4j.circuitbreaker.CircuitBreaker
				.decorateCallable(defaultCircuitBreaker, restrictedCall);
		return Try.of(callable::call).recover(fallback).get();
	}

}
