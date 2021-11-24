package org.gs4tr.termmanager.model.dto.converter;

import java.util.ArrayList;
import java.util.List;

import org.gs4tr.foundation.modules.entities.model.UserProfile;
import org.gs4tr.termmanager.model.Comment;
import org.gs4tr.termmanager.model.ProjectPolicyEnum;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.springframework.util.CollectionUtils;

public class CommentConverter {

    public static org.gs4tr.termmanager.model.dto.Comment fromInternalToDto(Comment internalEntity, Long projectId) {
	if (internalEntity == null) {
	    return null;
	}

	org.gs4tr.termmanager.model.dto.Comment dtoEntity = new org.gs4tr.termmanager.model.dto.Comment();
	dtoEntity.setText(internalEntity.getText());
	UserProfile userProfile = TmUserProfile.getCurrentUserProfile();
	if (userProfile.containsContextPolicies(projectId,
		new String[] { ProjectPolicyEnum.POLICY_TM_TERM_VIEW_TERM_HISTORY_USERS.toString() })) {
	    dtoEntity.setUsername(internalEntity.getUser());
	}

	return dtoEntity;
    }

    public static org.gs4tr.termmanager.model.dto.Comment[] fromInternalToDto(List<Comment> internalEntities,
	    Long projectId) {
	if (internalEntities == null) {
	    return null;
	}

	List<org.gs4tr.termmanager.model.dto.Comment> dtoEntities = new ArrayList<org.gs4tr.termmanager.model.dto.Comment>();
	for (Comment internalEntity : internalEntities) {
	    if (internalEntity != null) {
		dtoEntities.add(fromInternalToDto(internalEntity, projectId));
	    }
	}

	if (CollectionUtils.isEmpty(dtoEntities)) {
	    return null;
	} else {
	    return dtoEntities.toArray(new org.gs4tr.termmanager.model.dto.Comment[dtoEntities.size()]);
	}
    }
}
