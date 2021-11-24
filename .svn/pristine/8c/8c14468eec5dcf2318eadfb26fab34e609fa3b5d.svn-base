package org.gs4tr.termmanager.model.dto.converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.termmanager.model.SubmissionLanguageComment;
import org.gs4tr.termmanager.model.dto.DtoSubmissionLanguageComment;

public class SubmissionLanguageCommentConverter {

    public static DtoSubmissionLanguageComment baseFromInternalToDto(SubmissionLanguageComment internalEntity) {
	if (internalEntity == null) {
	    return null;
	}

	DtoSubmissionLanguageComment dtoComment = new DtoSubmissionLanguageComment();
	dtoComment.setCommentTicket(TicketConverter.fromInternalToDto(internalEntity.getCommentId()));
	dtoComment.setMarkerId(internalEntity.getMarkerId());
	dtoComment.setText(internalEntity.getText());
	dtoComment.setUser(internalEntity.getUser());
	dtoComment.setCommentId(internalEntity.getCommentId());
	return dtoComment;
    }

    public static DtoSubmissionLanguageComment[] fromInternalToDto(Set<SubmissionLanguageComment> internalEntities) {
	List<DtoSubmissionLanguageComment> dtoComments = new ArrayList<DtoSubmissionLanguageComment>();

	if (CollectionUtils.isNotEmpty(internalEntities)) {
	    for (SubmissionLanguageComment jobComment : internalEntities) {
		dtoComments.add(fromInternalToDto(jobComment));
	    }
	}

	DtoSubmissionLanguageComment[] dtoArray = dtoComments
		.toArray(new DtoSubmissionLanguageComment[dtoComments.size()]);

	Arrays.sort(dtoArray, new Comparator<DtoSubmissionLanguageComment>() {
	    @Override
	    public int compare(DtoSubmissionLanguageComment o1, DtoSubmissionLanguageComment o2) {
		return o1.getCommentId().compareTo(o2.getCommentId());
	    }
	});
	return dtoArray;
    }

    public static DtoSubmissionLanguageComment fromInternalToDto(SubmissionLanguageComment internalEntity) {
	if (internalEntity == null) {
	    return null;
	}

	return baseFromInternalToDto(internalEntity);
    }

}
