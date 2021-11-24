package org.gs4tr.termmanager.model.glossary.backup.converter;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermDescription;

public class DbTermDescriptionConverter {

    public static DbTermDescription convertToDbTermDescription(String termUuid, Description desc) {
	if (desc == null) {
	    return null;
	}

	DbTermDescription dbDesc = new DbTermDescription();

	String incomingUuId = desc.getUuid();
	if (incomingUuId == null) {
	    incomingUuId = UUID.randomUUID().toString();
	}
	dbDesc.setUuid(incomingUuId);
	dbDesc.setBaseType(desc.getBaseType());
	dbDesc.setType(desc.getType());
	dbDesc.setValueAsBytes(desc.getValue());
	dbDesc.setTermUuid(termUuid);

	return dbDesc;
    }

    public static Set<DbTermDescription> convertToDbTermDescriptions(String termId, Collection<Description> descs) {
	Set<DbTermDescription> dbDescs = new HashSet<DbTermDescription>();

	if (CollectionUtils.isNotEmpty(descs)) {
	    for (Description desc : descs) {
		if (desc == null) {
		    continue;
		}

		dbDescs.add(convertToDbTermDescription(termId, desc));
	    }
	}

	return dbDescs;
    }

    public static Set<Description> convertToTermDescription(Collection<DbTermDescription> dbDescriptions) {

	Set<Description> descriptions = new HashSet<Description>();

	if (CollectionUtils.isNotEmpty(dbDescriptions)) {
	    for (DbTermDescription dbDescription : dbDescriptions) {
		if (dbDescription == null) {
		    continue;
		}

		descriptions.add(convertToTermDescription(dbDescription));
	    }
	}
	return descriptions;
    }

    public static Description convertToTermDescription(DbTermDescription dbDescription) {
	if (dbDescription == null) {
	    return null;
	}
	Description description = new Description();
	description.setUuid(dbDescription.getUuid());
	description.setBaseType(dbDescription.getBaseType());
	description.setType(dbDescription.getType());
	description.setValue(dbDescription.getValueAsString());
	return description;
    }

    public static void mergeWithExistingDescription(DbTermDescription incoming, DbTermDescription existing) {
	if (incoming == null || existing == null) {
	    return;
	}

	existing.setBaseType(incoming.getBaseType());
	existing.setType(incoming.getType());
	existing.setValue(incoming.getValue());
    }

    public static void mergeWithExistingDescriptions(Collection<DbTermDescription> incoming,
	    Collection<DbTermDescription> existing) {
	if (CollectionUtils.isEmpty(incoming)) {
	    existing.clear();
	    return;
	}

	Set<DbTermDescription> copyOfExisting = nullSafeCopy(existing);

	for (DbTermDescription incomingDesc : incoming) {
	    DbTermDescription existingDesc = findExistingDescriptionByUuid(incomingDesc.getUuid(), copyOfExisting);
	    if (existingDesc != null) {
		mergeWithExistingDescription(incomingDesc, existingDesc);
	    } else {
		existing.add(incomingDesc);
	    }
	}

	if (CollectionUtils.isNotEmpty(copyOfExisting)) {
	    copyOfExisting.forEach(existing::remove);
	}
    }

    private static DbTermDescription findExistingDescriptionByUuid(String uuid,
	    Collection<DbTermDescription> copyOfExisting) {
	Iterator<DbTermDescription> each = copyOfExisting.iterator();
	while (each.hasNext()) {
	    DbTermDescription candidate = each.next();
	    if (uuid.equals(candidate.getUuid())) {
		each.remove();
		return candidate;
	    }
	}
	return null;
    }

    private static Set<DbTermDescription> nullSafeCopy(final Collection<DbTermDescription> descriptions) {
	return descriptions == null ? Collections.emptySet() : new HashSet<>(descriptions);
    }
}
