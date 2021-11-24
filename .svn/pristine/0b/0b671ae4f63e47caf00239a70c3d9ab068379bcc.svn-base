package org.gs4tr.termmanager.model.dto.converter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation.modules.entities.model.Ticket;
import org.gs4tr.foundation3.core.exception.InvalidArgumentException;
import org.gs4tr.foundation3.core.utils.IdEncrypter;
import org.gs4tr.termmanager.model.CustomCollectionUtils;

public class TicketConverter {

    public static List<Long> fromDtoToInternal(List<String> dtoEntities) {
	List<Long> ids = new ArrayList<>();
	if (org.apache.commons.collections.CollectionUtils.isNotEmpty(dtoEntities)) {
	    for (String ticket : dtoEntities) {
		ids.add(fromDtoToInternal(ticket, Long.class));
	    }

	}

	return ids;
    }

    @SuppressWarnings("unchecked")
    public static <T> T fromDtoToInternal(String dtoEntity, Class<T> clazz) {
	if (dtoEntity == null) {
	    return null;
	}

	if (clazz.isAssignableFrom(Long.class)) {
	    return (T) IdEncrypter.decryptGenericId(dtoEntity);
	} else if (clazz.isAssignableFrom(Locale.class)) {
	    return (T) Locale.makeLocale(dtoEntity);
	} else if (clazz.isAssignableFrom(String.class)) {
	    return (T) dtoEntity;
	} else {
	    throw new InvalidArgumentException(dtoEntity);
	}
    }

    public static <T> T[] fromDtoToInternal(String[] dtoEntities, Class<T> clazz) {
	return processInternal(dtoEntities, clazz);
    }

    @SuppressWarnings("unchecked")
    public static <T> T fromDtoToInternal(Ticket dtoEntity, Class<T> clazz) {
	if (dtoEntity == null) {
	    return null;
	}

	if (clazz.isAssignableFrom(Long.class)) {
	    return (T) IdEncrypter.decryptGenericId(dtoEntity.getTicketId());
	} else if (clazz.isAssignableFrom(Locale.class)) {
	    return (T) Locale.makeLocale(dtoEntity.getTicketId());
	} else if (clazz.isAssignableFrom(String.class)) {
	    return (T) dtoEntity;
	} else {
	    throw new InvalidArgumentException(dtoEntity);
	}
    }

    public static <T> T[] fromDtoToInternal(Ticket[] dtoEntities, Class<T> clazz) {
	return processInternal(dtoEntities, clazz);
    }

    public static <T> List<T> fromDtoToInternalList(String[] dtoEntities, Class<T> clazz) {
	return CustomCollectionUtils.getArrayList(processInternal(dtoEntities, clazz));
    }

    public static <T> List<T> fromDtoToInternalList(Ticket[] dtoEntities, Class<T> clazz) {
	return CustomCollectionUtils.getArrayList(processInternal(dtoEntities, clazz));
    }

    public static <T> String[] fromInternalToDto(List<T> internalEntities) {
	if (internalEntities != null) {
	    List<String> resultEntities = new ArrayList<String>();
	    for (T id : internalEntities) {
		if (id != null) {
		    resultEntities.add(fromInternalToDto(id));
		}
	    }

	    if (resultEntities.size() == 0) {
		return new String[0];
	    } else {
		return resultEntities.toArray(new String[resultEntities.size()]);
	    }
	} else {
	    return new String[0];
	}
    }

    public static String[] fromInternalToDto(Long[] internalEntities) {
	if (internalEntities != null) {
	    List<String> resultEntities = new ArrayList<String>();
	    for (Long id : internalEntities) {
		if (id != null) {
		    resultEntities.add(fromInternalToDto(id));
		}
	    }

	    if (resultEntities.size() == 0) {
		return null;
	    } else {
		return resultEntities.toArray(new String[resultEntities.size()]);
	    }
	} else {
	    return new String[0];
	}
    }

    public static <T> String fromInternalToDto(T internalEntity) {
	if (internalEntity == null) {
	    return null;
	}

	if (internalEntity instanceof Long) {
	    return IdEncrypter.encryptGenericId((Long) internalEntity);
	} else if (internalEntity instanceof String) {
	    return (String) internalEntity;
	} else {
	    throw new InvalidArgumentException(internalEntity);
	}
    }

    @SuppressWarnings("unchecked")
    private static <T, G> T[] processInternal(G[] dtoEntities, Class<T> clazz) {
	if ((dtoEntities == null) || (dtoEntities.length == 0)
		|| ((dtoEntities.length == 1) && (dtoEntities[0] == null))) {
	    return null;
	}

	T[] internalEntities = (T[]) Array.newInstance(clazz, dtoEntities.length);
	for (int i = 0; i < internalEntities.length; i++) {
	    internalEntities[i] = dtoEntities[i] instanceof String ? fromDtoToInternal((String) dtoEntities[i], clazz)
		    : fromDtoToInternal((Ticket) dtoEntities[i], clazz);
	}

	return internalEntities;
    }

}
