package com.topcoder.common.logging;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Aspect for logging execution of Spring components.
 */
@Aspect
public class LoggingAspect {

    /**
     * The logger.
     */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Point-cut that matches all controllers, services, and daos.
     */
    @Pointcut("within(com.topcoder.api.controller..*) || "
            + "within(com.topcoder.api.service..*)")
    public void loggingPointcut() {
        // Method is empty as this is just a Point-cut, the implementations are in the advises.
    }

    /**
     * Advice that logs methods throwing exceptions.
     *
     * @param joinPoint the join point
     * @param ex        the throwable
     */
    @AfterThrowing(pointcut = "within(com.topcoder.api.service..*)", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        logger.error("Error in method {}.{}(): Cause={}, Details = \'{}\'",
                joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(),
                ex.getCause() != null ? ex.getCause() : "NULL", ex.getMessage());
        logger.error("Exception = ", ex);
    }

    /**
     * Advice that logs when a method is entered and exited.
     * `
     *
     * @param joinPoint the join point
     * @throws Throwable when there is any error
     */
    @Around("loggingPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        if (logger.isDebugEnabled()) {
            logger.debug("Enter: {}.{}() with argument[s] = {}",
                    joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(),
                    Arrays.toString(joinPoint.getArgs()));
        }
        try {
            Object result = joinPoint.proceed();
            if (logger.isDebugEnabled()) {
                logger.debug("Exit: {}.{}() with result = {}",
                        joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(),
                        result);
            }
            return result;
        } catch (IllegalArgumentException ex) {
            logger.error("Illegal argument: {} in {}.{}()", Arrays.toString(joinPoint.getArgs()),
                    joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());

            throw ex;
        }
    }
}
