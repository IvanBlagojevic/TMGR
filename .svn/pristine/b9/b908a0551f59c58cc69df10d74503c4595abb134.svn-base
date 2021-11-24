package org.gs4tr.termmanager.webmvc.rest.interceptors;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class RestAuthenticationPointcuts {

    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void annotatedRequestMapping() {
    }

    @Pointcut("within(org.gs4tr.termmanager.webmvc.rest.*)")
    public void inWebMvcRestPackage() {
    }

    @Pointcut("execution(* org.gs4tr.termmanager.webmvc.rest.*Controller.*(..))")
    public void namedController() {
    }

    @Pointcut("inWebMvcRestPackage() && namedController() && annotatedRequestMapping()")
    public void restMethods() {
    }

    @Pointcut("restMethods() && !execution(* org.gs4tr.termmanager.webmvc.rest.RestLoginController.*(..))")
    public void restMethodsExceptLogin() {
    }
}
