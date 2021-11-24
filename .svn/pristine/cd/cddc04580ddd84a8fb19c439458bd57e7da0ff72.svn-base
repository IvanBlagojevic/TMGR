package org.gs4tr.termmanager.model.dto.converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.termmanager.model.dto.DtoTermComment;
import org.gs4tr.termmanager.model.glossary.Comment;

public class DtoTermCommentConverter {

    public static DtoTermComment baseFromInternalToDto(Comment internalEntity) {
	if (internalEntity == null) {
	    return null;
	}

	DtoTermComment dtoComment = new DtoTermComment();
	dtoComment.setUser(internalEntity.getUser());
	dtoComment.setText(internalEntity.getText());
	return dtoComment;
    }

    public static DtoTermComment fromInternalToDto(Comment internalEntity) {
	if (internalEntity == null) {
	    return null;
	}

	return baseFromInternalToDto(internalEntity);
    }

    public static DtoTermComment[] fromInternalToDto(Set<Comment> internalEntities) {
	List<DtoTermComment> dtoComments = new ArrayList<DtoTermComment>();

	if (CollectionUtils.isNotEmpty(internalEntities)) {
	    for (Comment termComment : internalEntities) {
		dtoComments.add(fromInternalToDto(termComment));
	    }
	}

	DtoTermComment[] dtoArray = dtoComments.toArray(new DtoTermComment[dtoComments.size()]);
	Arrays.sort(dtoArray, new Comparator<DtoTermComment>() {
	    @Override
	    public int compare(DtoTermComment o1, DtoTermComment o2) {
		return o1.getText().compareTo(o2.getText());
	    }
	});
	return dtoArray;
    }
}
