package org.gs4tr.termmanager.model.dto.converter;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

import java.util.ArrayList;
import java.util.List;

import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;

public class SelectStyleConverter {

    private static final int MULTI_SELECT_STYLE = 2;
    private static final int NOT_SELECTABLE_STYLE = 0;
    private static final int SINGLE_SELECT_STYLE = 1;

    public static SelectStyleEnum fromDtoToInternal(int dtoStyle) {
	switch (dtoStyle) {
	case NOT_SELECTABLE_STYLE:
	    return SelectStyleEnum.NO_SELECT;
	case MULTI_SELECT_STYLE:
	    return SelectStyleEnum.MULTI_SELECT;
	default:
	    return SelectStyleEnum.SINGLE_SELECT;
	}
    }

    public static SelectStyleEnum[] fromDtoToInternal(int[] dtoStyles) {
	if (dtoStyles != null) {
	    List<SelectStyleEnum> resultEntities = new ArrayList<SelectStyleEnum>();
	    for (int style : dtoStyles) {
		resultEntities.add(fromDtoToInternal(style));
	    }

	    if (isEmpty(resultEntities)) {
		return null;
	    } else {
		return resultEntities.toArray(new SelectStyleEnum[resultEntities.size()]);
	    }
	} else {
	    return null;
	}
    }

    public static int fromInternalToDto(SelectStyleEnum internalStyle) {
	switch (internalStyle) {
	case MULTI_SELECT:
	    return MULTI_SELECT_STYLE;
	case NO_SELECT:
	    return NOT_SELECTABLE_STYLE;
	default:
	    return SINGLE_SELECT_STYLE;
	}
    }

    public static int[] fromInternalToDto(SelectStyleEnum[] internalStyles) {
	if (internalStyles != null) {
	    int[] result = new int[internalStyles.length];
	    for (int i = 0; i < internalStyles.length; i++) {
		result[i] = fromInternalToDto(internalStyles[i]);
	    }
	    return result;
	} else {
	    return null;
	}
    }
}