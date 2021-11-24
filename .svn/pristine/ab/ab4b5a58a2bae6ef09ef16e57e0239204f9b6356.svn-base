package org.gs4tr.termmanager.dao.provider;

import java.util.Date;

import org.gs4tr.foundation.modules.dao.hibernate.AbstractIdLoadProvider;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.hibernate.criterion.Restrictions;

public class UserIdLoadProvider extends AbstractIdLoadProvider<Long, TmUserProfile> {

    public static UserIdLoadProvider create() {
	return new UserIdLoadProvider();
    }

    public UserIdLoadProvider dateLastLoginBefore(Date dateLastBefore) {
	addCriterion(Restrictions.lt("userInfo.dateLastLogin", dateLastBefore));
	return this;
    }

    public UserIdLoadProvider enabled(Boolean enabled) {
	addCriterion(Restrictions.eq("userInfo.enabled", enabled));
	return this;
    }

    public UserIdLoadProvider excludedUserNames(String... userName) {
	addCriterion(Restrictions.not(Restrictions.in("userInfo.userName", userName)));
	return this;
    }

    public UserIdLoadProvider isSsoUser(Boolean isSsoUser) {
	addCriterion(Restrictions.eq("userInfo.ssoUser", isSsoUser));
	return this;
    }

    public UserIdLoadProvider userName(String userName) {
	addCriterion(Restrictions.eq("userName", userName));
	return this;
    }

    @Override
    protected Class<TmUserProfile> getEntityPersistanceClass() {
	return TmUserProfile.class;
    }
}