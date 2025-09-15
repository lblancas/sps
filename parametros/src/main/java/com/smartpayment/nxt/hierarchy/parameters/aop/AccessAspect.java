package com.smartpayment.nxt.hierarchy.parameters.aop;

import com.smartpayment.nxt.hierarchy.parameters.exception.HierarchyException;
import com.smartpayment.nxt.hierarchy.parameters.service.impl.AccessService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterConstants.ErrorMessages.ACCESS_DENIED;
import static com.smartpayment.nxt.security.JwtAuthenticationFilter.CONTEXT_ROLE_ID;
import static com.smartpayment.nxt.security.JwtAuthenticationFilter.CONTEXT_USER_ID;

@Aspect
@Component
@RequiredArgsConstructor
public class AccessAspect {

  private final AccessService accessService;

  @Around("@annotation(checkAccess)")
  public Object checkAccess(ProceedingJoinPoint joinPoint, CheckAccess checkAccess) throws Throwable {
    ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    HttpServletRequest request = attrs.getRequest();

    String userId = (String) request.getAttribute(CONTEXT_USER_ID);
    String roleId = (String) request.getAttribute(CONTEXT_ROLE_ID);

    String module = checkAccess.module();
    String activity = checkAccess.activity();

    boolean access = accessService.hasAccess(userId, roleId, module, activity);
    if (!access) {
      throw new HierarchyException(ACCESS_DENIED, "Access denied");
    }

    return joinPoint.proceed();
  }
}
