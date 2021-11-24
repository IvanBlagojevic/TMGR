package org.gs4tr.termmanager.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.gs4tr.foundation.modules.entities.model.TaskPriority;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SystemTask {

    public static final String ACTION = "action";

    String group() default ACTION;

    TaskPriority priority() default TaskPriority.LEVEL_NINE;
}
