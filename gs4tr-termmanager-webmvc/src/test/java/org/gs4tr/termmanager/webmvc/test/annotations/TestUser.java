package org.gs4tr.termmanager.webmvc.test.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.gs4tr.termmanager.webmvc.test.configuration.RoleNameEnum;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TestUser {

    RoleNameEnum roleName() default RoleNameEnum.SYSTEM_SUPER_USER;
}