package org.gs4tr.termmanager.dao.utils;

import java.lang.reflect.Field;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.modules.entities.model.LogHelper;
import org.gs4tr.termmanager.model.StringConstants;
import org.hibernate.Hibernate;

public class DaoUtils {

    private static final String GET_PREFIX = "get"; //$NON-NLS-1$
    private static final Log _logger = LogFactory.getLog(DaoUtils.class);

    public static <T> boolean initializeEntities(T object, Class<?>... classes) {
	if (classes == null || classes.length == 0) {
	    return false;
	}

	Class<?> objectClass = object.getClass();

	List<Field> declaredFields = collectFields(objectClass);

	Set<Class<?>> classesToRemove = new HashSet<Class<?>>();

	Map<String, Boolean> methodNames = collectMethodNames(declaredFields, classesToRemove, classes);

	return initialize(object, objectClass, methodNames, classesToRemove, classes);
    }

    private static List<Field> collectFields(Class<?> clazz) {
	List<Field> fields = new LinkedList<Field>();

	if (clazz == null) {
	    return fields;
	}

	Field[] declaredFields = clazz.getDeclaredFields();

	for (Field declaredField : declaredFields) {
	    Class<?> fieldClass = declaredField.getType();

	    Package actualPackage = null;
	    if (fieldClass.isAssignableFrom(Set.class) || fieldClass.isAssignableFrom(List.class)) {
		Type fieldType = declaredField.getGenericType();

		if (fieldType instanceof ParameterizedType) {
		    Type[] actualTypeArguments = ((ParameterizedType) fieldType).getActualTypeArguments();

		    Type actualType = actualTypeArguments[0];

		    Class<?> actualTypeClass;
		    if (actualType instanceof TypeVariable<?>) {
			GenericDeclaration genericDeclaration = ((TypeVariable<?>) actualType).getGenericDeclaration();
			actualTypeClass = (Class<?>) genericDeclaration;
		    } else {
			actualTypeClass = (Class<?>) actualType;
		    }

		    actualPackage = actualTypeClass.getPackage();
		}
	    } else {
		actualPackage = fieldClass.getPackage();
	    }

	    if (actualPackage != null && actualPackage.getName().contains("org.gs4tr.")) {
		fields.add(declaredField);
	    }
	}

	fields.addAll(collectFields(clazz.getSuperclass()));

	return fields;
    }

    private static Map<String, Boolean> collectMethodNames(List<Field> declaredFields, Set<Class<?>> classesToRemove,
	    Class<?>... classes) {
	Map<String, Boolean> methodNames = new HashMap<String, Boolean>();
	for (Field declaredField : declaredFields) {
	    Class<?> fieldClass = declaredField.getType();

	    if (fieldClass.isAssignableFrom(Set.class) || fieldClass.isAssignableFrom(List.class)) {
		Type fieldType = declaredField.getGenericType();

		if (fieldType instanceof ParameterizedType) {
		    Type[] actualTypeArguments = ((ParameterizedType) fieldType).getActualTypeArguments();

		    Type actualType = actualTypeArguments[0];

		    Class<?> actualTypeClass;
		    if (actualType instanceof TypeVariable<?>) {
			GenericDeclaration genericDeclaration = ((TypeVariable<?>) actualType).getGenericDeclaration();
			actualTypeClass = (Class<?>) genericDeclaration;
		    } else {
			actualTypeClass = (Class<?>) actualType;
		    }

		    if (isWantedClass(actualTypeClass, classes)) {
			String methodName = createMethodName(declaredField);
			methodNames.put(methodName, Boolean.TRUE);

			classesToRemove.add(fieldClass);
		    }
		}
	    } else {
		if (isWantedClass(fieldClass, classes)) {
		    String methodName = createMethodName(declaredField);
		    methodNames.put(methodName, Boolean.FALSE);

		    classesToRemove.add(fieldClass);
		}
	    }
	}
	return methodNames;
    }

    private static String createMethodName(Field declaredField) {
	String fieldName = declaredField.getName();
	if (fieldName.startsWith(StringConstants.UNDERSCORE)) {
	    // remove underscore
	    fieldName = fieldName.substring(1);
	}

	// capture first letter for uppercase
	String firstLetter = String.valueOf(fieldName.charAt(0));

	// remover first letter
	fieldName = fieldName.substring(1);

	return GET_PREFIX + firstLetter.toUpperCase() + fieldName;

    }

    private static <T> boolean initialize(T object, Class<?> objectClass, Map<String, Boolean> methodNames,
	    Set<Class<?>> classesToRemove, Class<?>... classes) {
	boolean initalized = false;

	if (!methodNames.isEmpty()) {
	    Method[] methods = objectClass.getMethods();

	    for (Method method : methods) {
		String methodName = method.getName();
		if (methodNames.containsKey(methodName)) {
		    try {
			if (_logger.isDebugEnabled()) {
			    LogHelper.debug(_logger, String.format(Messages.getString("DaoUtils.7"), //$NON-NLS-1$
				    methodName));
			}

			Object result = method.invoke(object);

			// if method is collection does not initialize further
			// objects
			Boolean isCollection = methodNames.get(methodName);

			if (result != null && isCollection) {
			    Hibernate.initialize(result);

			    initalized = true;
			} else if (result != null) {
			    boolean initalizedRecursive = initializeEntities(result,
				    removeClasses(classesToRemove, classes));

			    if (!initalizedRecursive) {
				Hibernate.initialize(result);
			    }

			    initalized = true;
			}
		    } catch (Exception e) {
			throw new RuntimeException(e);
		    }
		}
	    }
	}

	return initalized;
    }

    private static boolean isWantedClass(Class<?> actualTypeClass, Class<?>... classes) {
	for (Class<?> clazz : classes) {
	    if (actualTypeClass.isAssignableFrom(clazz)) {
		return true;
	    }
	}

	return false;
    }

    private static Class<?>[] removeClasses(Set<Class<?>> classesToRemove, Class<?>... classes) {
	List<Class<?>> newList = new ArrayList<Class<?>>();

	for (Class<?> clazz : classes) {
	    if (!classesToRemove.contains(clazz)) {
		newList.add(clazz);
	    }
	}

	return newList.toArray(new Class<?>[newList.size()]);
    }

}
