package org.gs4tr.termmanager.model.dto.converter;

import org.gs4tr.foundation.modules.entities.model.UserInfo;
import org.gs4tr.foundation.modules.entities.model.UserTypeEnum;

public class UserInfoConverter {

    public static UserInfo fromDtoToInternal(org.gs4tr.termmanager.model.dto.UserInfo dtoEntity) {
	if (dtoEntity == null) {
	    return null;
	}

	UserInfo internalEntity = new UserInfo();

	internalEntity.setFirstName(dtoEntity.getFirstName());
	internalEntity.setLastName(dtoEntity.getLastName());
	internalEntity.setUserName(dtoEntity.getUserName());
	internalEntity.setEmailAddress(dtoEntity.getEmailAddress());
	internalEntity.setEmailNotification(dtoEntity.getEmailNotification());
	internalEntity.setAccountNonExpired(dtoEntity.getAccountNonExpired());
	internalEntity.setPassword(dtoEntity.getPassword());
	internalEntity.setEnabled(dtoEntity.getEnabled());
	internalEntity.setTimeZone(dtoEntity.getTimeZone());
	internalEntity.setUserType(UserTypeEnum.valueOf(dtoEntity.getUserType()));
	internalEntity.setUnsuccessfulAuthCount(dtoEntity.getUnsuccessfulAuthCount());
	internalEntity.setSsoUser(dtoEntity.getIsSamlUser());

	return internalEntity;
    }

    public static org.gs4tr.termmanager.model.dto.UserInfo fromInternalToDto(UserInfo internalEntity) {
	if (internalEntity == null) {
	    return null;
	}

	org.gs4tr.termmanager.model.dto.UserInfo dtoEntity = new org.gs4tr.termmanager.model.dto.UserInfo();

	dtoEntity.setFirstName(internalEntity.getFirstName());
	dtoEntity.setLastName(internalEntity.getLastName());
	dtoEntity.setUserName(internalEntity.getUserName());
	dtoEntity.setPassword(internalEntity.getPassword());
	dtoEntity.setEmailAddress(internalEntity.getEmailAddress());
	dtoEntity.setEmailNotification(internalEntity.getEmailNotification());
	dtoEntity.setAccountNonExpired(internalEntity.isAccountNonExpired());
	dtoEntity.setEnabled(internalEntity.isEnabled());
	dtoEntity.setTimeZone(internalEntity.getTimeZone());
	dtoEntity.setUserType(internalEntity.getUserType().name());
	dtoEntity.setUnsuccessfulAuthCount(internalEntity.getUnsuccessfulAuthCount());
	if (internalEntity.getDateLastLogin() != null) {
	    dtoEntity.setDateLastLogin(internalEntity.getDateLastLogin().getTime());
	}
	dtoEntity.setAccountLocked(!internalEntity.isAccountNonLocked());

	Boolean isSsoUser = internalEntity.isSsoUser();

	Boolean ssoUser = isSsoUser != null ? isSsoUser : Boolean.FALSE;
	dtoEntity.setIsSamlUser(ssoUser);
	dtoEntity.setSsoUser(ssoUser);

	return dtoEntity;
    }
}
