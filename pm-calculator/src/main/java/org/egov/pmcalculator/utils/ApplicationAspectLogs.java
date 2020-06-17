package org.egov.pmcalculator.utils;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Configuration
@Slf4j
public class ApplicationAspectLogs {

	// Controller
	@Before("execution(* org.egov.pmcalculator.web.controllers.*.*(..))")
	public void beforeController(JoinPoint joinPoint) {
		long startTime = System.currentTimeMillis();
		log.info("pm-calculator logs :: beforeController = Method::{}, Execution Time::{}", joinPoint.getSignature(),
				startTime);
	}

	@After("execution(* org.egov.pmcalculator.web.controllers.*.*(..))")
	public void afterController(JoinPoint joinPoint) {
		long startTime = System.currentTimeMillis();
		log.info("pm-calculator logs :: afterController = Method::{}, Execution Time::{}", joinPoint.getSignature(),
				startTime);
	}

	@AfterReturning(value = "execution(* org.egov.pmcalculator.web.controllers.*.*(..))", returning = "result")
	public void afterReturningController(JoinPoint joinPoint, Object result) {
		long startTime = System.currentTimeMillis();
		log.info("pm-calculator logs :: afterReturningController = Method::{}, Execution Time::{}, Results::{}",
				joinPoint.getSignature(), startTime, result);
	}

	@AfterThrowing(value = "execution(* org.egov.pmcalculator.web.controllers.*.*(..))", throwing = "exception")
	public void afterThrowingController(JoinPoint joinPoint, Throwable exception) {
		long startTime = System.currentTimeMillis();
		log.info("pm-calculator logs :: afterThrowingController = Method::{}, Execution Time::{}, Exception::{}",
				joinPoint.getSignature(), startTime, exception.getMessage());
	}

	// Service
	@Before("execution(* org.egov.pmcalculator.service.*.*(..))")
	public void beforeService(JoinPoint joinPoint) {
		long startTime = System.currentTimeMillis();
		log.info("pm-calculator logs :: beforeService = Method::{}, Execution Time::{}", joinPoint.getSignature(),
				startTime);
	}

	@After("execution(* org.egov.pmcalculator.service.*.*(..))")
	public void afterService(JoinPoint joinPoint) {
		long startTime = System.currentTimeMillis();
		log.info("pm-calculator logs :: afterService = Method::{}, Execution Time::{}", joinPoint.getSignature(),
				startTime);
	}

	@AfterReturning(value = "execution(* org.egov.pmcalculator.service.*.*(..))", returning = "result")
	public void afterReturningService(JoinPoint joinPoint, Object result) {
		long startTime = System.currentTimeMillis();
		log.info("pm-calculator logs :: afterReturningService = Method::{}, Execution Time::{}, Results::{}",
				joinPoint.getSignature(), startTime, result);
	}

	@AfterThrowing(value = "execution(* org.egov.pmcalculator.service.*.*(..))", throwing = "exception")
	public void afterThrowingService(JoinPoint joinPoint, Throwable exception) {
		long startTime = System.currentTimeMillis();
		log.info("pm-calculator logs :: afterThrowingService = Method::{}, Execution Time::{}, Exception::{}",
				joinPoint.getSignature(), startTime, exception.getMessage());
	}

	// Repository
	@Before("execution(* org.egov.pmcalculator.repository.*.*(..))")
	public void beforeRepository(JoinPoint joinPoint) {
		long startTime = System.currentTimeMillis();
		log.info("pm-calculator logs :: beforeRepository = Method::{}, Execution Time::{}", joinPoint.getSignature(),
				startTime);
	}

	@After("execution(* org.egov.pmcalculator.repository.*.*(..))")
	public void afterRepository(JoinPoint joinPoint) {
		long startTime = System.currentTimeMillis();
		log.info("pm-calculator logs :: afterRepository = Method::{}, Execution Time::{}", joinPoint.getSignature(),
				startTime);
	}

	@AfterReturning(value = "execution(* org.egov.pmcalculator.repository.*.*(..))", returning = "result")
	public void afterReturningRepository(JoinPoint joinPoint, Object result) {
		long startTime = System.currentTimeMillis();
		log.info("pm-calculator logs :: afterReturningRepository = Method::{}, Execution Time::{}, Results::{}",
				joinPoint.getSignature(), startTime, result);
	}

	@AfterThrowing(value = "execution(* org.egov.pmcalculator.repository.*.*(..))", throwing = "exception")
	public void afterThrowingRepository(JoinPoint joinPoint, Throwable exception) {
		long startTime = System.currentTimeMillis();
		log.info("pm-calculator logs :: afterThrowingRepository = Method::{}, Execution Time::{}, Exception::{}",
				joinPoint.getSignature(), startTime, exception.getMessage());
	}
}
