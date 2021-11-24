package org.gs4tr.termmanager.model.dto.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.dto.DtoBaseUserProfile;

public class UserProfileConverter {

    public static DtoBaseUserProfile[] baseFromInternalToDto(Set<TmUserProfile> internalEntities) {
	if (internalEntities == null) {
	    return null;
	}

	List<DtoBaseUserProfile> dtoList = new ArrayList<DtoBaseUserProfile>();

	for (TmUserProfile tmUserProfile : internalEntities) {
	    dtoList.add(baseFromInternalToDto(tmUserProfile));
	}

	return dtoList.toArray(new DtoBaseUserProfile[dtoList.size()]);
    }

    public static DtoBaseUserProfile baseFromInternalToDto(TmUserProfile internalEntity) {
	if (internalEntity == null) {
	    return null;
	}

	DtoBaseUserProfile dtoEntity = new DtoBaseUserProfile();

	dtoEntity.setTicket(TicketConverter.fromInternalToDto(internalEntity.getUserProfileId()));
	dtoEntity.setUserName(internalEntity.getUserName());

	return dtoEntity;
    }

    public static TmUserProfile fromDtoToInternal(org.gs4tr.termmanager.model.dto.UserProfile dtoEntity) {
	if (dtoEntity == null) {
	    return null;
	}

	TmUserProfile internalEntity = new TmUserProfile();

	internalEntity.setSystemRoles(RoleConverter.fromDtoToInternal(dtoEntity.getSystemRoles()));

	internalEntity.setUserInfo(UserInfoConverter.fromDtoToInternal(dtoEntity.getUserInfo()));
	if (dtoEntity.getTicket() != null) {
	    internalEntity.setIdentifier(TicketConverter.fromDtoToInternal(dtoEntity.getTicket(), Long.class));
	}

	return internalEntity;
    }

    public static org.gs4tr.termmanager.model.dto.UserProfile fromInternalToDto(TmUserProfile internalEntity) {
	if (internalEntity == null) {
	    return null;
	}

	org.gs4tr.termmanager.model.dto.UserProfile dtoEntity = new org.gs4tr.termmanager.model.dto.UserProfile();

	if (internalEntity.getSystemRoles() != null) {

	    dtoEntity.setSystemRoles(RoleConverter.fromInternalToDto(internalEntity.getSystemRoles()));
	}

	dtoEntity.setUserInfo(UserInfoConverter.fromInternalToDto(internalEntity.getUserInfo()));
	dtoEntity.setTicket(TicketConverter.fromInternalToDto(internalEntity.getUserProfileId()));

	return dtoEntity;
    }
}
