package org.gs4tr.termmanager.webservice.interceptors;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class WsSharedPointcuts {

    /**
     * Any join point (method execution only in Spring AOP) where the executing
     * method has an @RequestMapping annotation
     */
    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void haveRequestMappingAnnotation() {
    }

    /**
     * A join point is in the web service package if the method is defined in a
     * type in the org.gs4tr.termmanager.webservice.controllers package
     */
    @Pointcut("within(org.gs4tr.termmanager.webservice.controllers.*)")
    public void inWebServicePackage() {
    }

    /**
     * This pointcut will match the execution of any class named 'Controller' in
     * org.gs4tr.termmanager.webservice.controllers package
     */
    @Pointcut("execution(* org.gs4tr.termmanager.webservice.controllers.*Controller.*(..))")
    public void namedControllers() {
    }

    @Pointcut("inWebServicePackage() && namedControllers() && haveRequestMappingAnnotation()")
    public void wsMethods() {
    }

    @Pointcut("wsMethods() && !execution(* org.gs4tr.termmanager.webservice.controllers.LoginController.*(..))")
    public void wsMethodsWithoutLoginController() {
    }
}
