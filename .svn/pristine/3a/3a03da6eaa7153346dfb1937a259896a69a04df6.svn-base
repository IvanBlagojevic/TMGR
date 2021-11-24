package org.gs4tr.termmanager.webmvc.test.configuration;

public enum RoleNameEnum {

    ADMIN(AdminRoleFactory.class), SYSTEM_POWER_USER(PowerUserRoleFactory.class), SYSTEM_SUPER_USER(
	    SuperUserRoleFactory.class), SYSTEM_TRANSLATOR_USER(TranslatorUserRoleFactory.class);

    private Class<?> _factoryClazz;

    private RoleNameEnum(Class<?> factoryClazz) {
	_factoryClazz = factoryClazz;
    }

    public Class<?> getFactoryClazz() {
	return _factoryClazz;
    }
}
