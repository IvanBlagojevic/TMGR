package org.gs4tr.termmanager.model.dto.converter;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.modules.entities.model.types.ItemStatusType;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;

public class ItemStatusTypeConverter {

    public static List<ItemStatusType> fromDtoToInternal(List<String> statuses) {
	List<ItemStatusType> statusTypes = new ArrayList<ItemStatusType>();
	if (CollectionUtils.isNotEmpty(statuses)) {
	    for (String status : statuses) {
		statusTypes.add(fromDtoToInternal(status));
	    }
	}

	return statusTypes;
    }

    public static ItemStatusType fromDtoToInternal(String status) {
	return ItemStatusTypeHolder.valueOf(status);
    }

    public static String fromInternalToDto(ItemStatusType internalEntity) {
	return internalEntity.getName();
    }
}
