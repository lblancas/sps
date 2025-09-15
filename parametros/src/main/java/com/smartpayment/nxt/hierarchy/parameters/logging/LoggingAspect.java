package com.smartpayment.nxt.hierarchy.parameters.logging;

import com.smartpayment.nxt.logging.BaseLoggingAspect;
import com.smartpayment.nxt.logging.service.LoggingService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect extends BaseLoggingAspect {

  @Autowired
  private LoggingService loggingService;

  @Value("${webclient.client.nxt-msa-logger.module}")
  private String module;

  @Pointcut("within(com.smartpayment.nxt.hierarchy.controller..*)"
      + " && !@annotation(org.springframework.web.bind.annotation.GetMapping)")
  public void infoLogPackagePointcut() {}

  @Pointcut("within(com.smartpayment.nxt.hierarchy.controller..*)"
      + " || within(com.smartpayment.nxt.hierarchy.service.impl..*)"
      + " || within(com.smartpayment.nxt.hierarchy.repository..*)"
      + " || within(com.smartpayment.nxt.hierarchy.security..*)")
  public void errorLogPackagePointcut() {}

  @AfterReturning(pointcut = "infoLogPackagePointcut()",
      returning = "result")
  public void logAfterReturning(JoinPoint joinPoint, Object result) {
    super.logAfterReturning(joinPoint, result);
  }

  @AfterThrowing(pointcut = "errorLogPackagePointcut()",
      throwing = "e")
  public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
    super.logAfterThrowing(joinPoint, e);
  }

  @Override
  public LoggingService getLoggingService() {
    return this.loggingService;
  }

  @Override
  public String getModule() {
    return this.module;
  }
}
