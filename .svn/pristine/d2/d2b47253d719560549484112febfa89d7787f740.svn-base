package org.gs4tr.termmanager.dao.provider;

import java.util.Collection;

import org.gs4tr.foundation.modules.dao.hibernate.AbstractLoadProvider;
import org.gs4tr.foundation.modules.entities.model.StringConstants;
import org.gs4tr.foundation.modules.entities.model.UserTypeEnum;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.hibernate.Hibernate;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

public class UserLoadProvider extends AbstractLoadProvider<TmUserProfile> {

    private static final long serialVersionUID = -3457120749738730733L;

    public static UserLoadProvider create() {
	return new UserLoadProvider();
    }

    private boolean _loadNotificationProfiles;

    private boolean _loadOrganization;

    private boolean _loadSystemRoles;

    private boolean _loadUserGroups;

    private boolean _loadUserLanguageDirections;

    private boolean _loadUserOrganizationChildOrganizations;

    private boolean _loadUserOrganizationParentOrganization;

    private boolean _loadVendor;

    public UserLoadProvider emailAddress(String emailAddress) {
	addCriterion(Restrictions.eq("userInfo.emailAddress", emailAddress));
	return this;
    }

    public UserLoadProvider loadNotificationProfiles() {
	_loadNotificationProfiles = true;
	return this;
    }

    public UserLoadProvider loadOrganization() {
	_loadOrganization = true;
	return this;
    }

    public UserLoadProvider loadSystemRoles() {
	_loadSystemRoles = true;
	return this;
    }

    public UserLoadProvider loadUserGroups() {
	_loadUserGroups = true;
	return this;
    }

    public UserLoadProvider loadUserLanguageDirections() {
	_loadUserLanguageDirections = true;
	return this;
    }

    public UserLoadProvider loadUserOrganizationChildOrganizations() {
	_loadUserOrganizationChildOrganizations = true;
	return this;
    }

    public UserLoadProvider loadUserOrganizationParentOrganization() {
	_loadUserOrganizationParentOrganization = true;
	return this;
    }

    public UserLoadProvider loadVendor() {
	_loadVendor = true;
	return this;
    }

    public UserLoadProvider userContainsName(String userName) {
	addCriterion(Restrictions.like("userInfo.userName", userName, MatchMode.ANYWHERE));
	return this;
    }

    public UserLoadProvider userGroupId(Long userGroupId) {
	addCriterion(Restrictions.eq("userGroup.id", userGroupId));
	return this;
    }

    public UserLoadProvider userId(Collection<Long> userIds) {
	addCriterion(Restrictions.in("userProfileId", userIds));
	return this;
    }

    public UserLoadProvider userId(Long... userIds) {
	addCriterion(Restrictions.in("userProfileId", userIds));
	return this;
    }

    public UserLoadProvider userName(String userName) {
	addCriterion(Restrictions.eq("userInfo.userName", userName));
	return this;
    }

    public UserLoadProvider userNameStartsWith(String username) {
	addCriterion(Restrictions.like("userInfo.userName", username.concat(StringConstants.PERCENT)));
	return this;
    }

    public UserLoadProvider userNames(Collection<String> userNames) {
	addCriterion(Restrictions.in("userInfo.userName", userNames));
	return this;
    }

    public UserLoadProvider userType(UserTypeEnum userType) {
	addCriterion(Restrictions.eq("userInfo.userType", userType));
	return this;
    }

    public UserLoadProvider vendorId(Collection<Long> vendorIds) {
	addCriterion(Restrictions.in("vendor.id", vendorIds));
	return this;
    }

    public UserLoadProvider vendorId(Long... vendorIds) {
	addCriterion(Restrictions.in("vendor.id", vendorIds));
	return this;
    }

    private boolean isLoadNotificationProfiles() {
	return _loadNotificationProfiles;
    }

    private boolean isLoadOrganization() {
	return _loadOrganization;
    }

    private boolean isLoadSystemRoles() {
	return _loadSystemRoles;
    }

    private boolean isLoadUserGroups() {
	return _loadUserGroups;
    }

    private boolean isLoadUserLanguageDirections() {
	return _loadUserLanguageDirections;
    }

    private boolean isLoadUserOrganizationChildOrganizations() {
	return _loadUserOrganizationChildOrganizations;
    }

    private boolean isLoadUserOrganizationParentOrganization() {
	return _loadUserOrganizationParentOrganization;
    }

    private boolean isLoadVendor() {
	return _loadVendor;
    }

    @Override
    protected Class<TmUserProfile> getEntityPersistanceClass() {
	return TmUserProfile.class;
    }

    @Override
    protected void postLoadInitialize(TmUserProfile user) {
	if (isLoadNotificationProfiles()) {
	    Hibernate.initialize(user.getNotificationProfiles());
	}

	if (isLoadOrganization()) {
	    Hibernate.initialize(user.getOrganization());

	    if (user.getOrganization() != null) {
		if (isLoadUserOrganizationParentOrganization()) {
		    Hibernate.initialize(user.getOrganization().getParentOrganization());
		}

		if (isLoadUserOrganizationChildOrganizations()) {
		    Hibernate.initialize(user.getOrganization().getChildOrganizations());
		}
	    }
	}

    }
}