package org.gs4tr.termmanager.model.glossary.backup.converter;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermEntryDescription;

public class DbTermEntryDescriptionConverter {

    public static DbTermEntryDescription convertToDescription(String termEntryUuid, Description desc) {
	if (desc == null) {
	    return null;
	}

	String incomingUuId = desc.getUuid();
	if (incomingUuId == null) {
	    incomingUuId = UUID.randomUUID().toString();
	}

	DbTermEntryDescription dbDesc = new DbTermEntryDescription();
	dbDesc.setUuid(incomingUuId);
	dbDesc.setBaseType(desc.getBaseType());
	dbDesc.setType(desc.getType());
	dbDesc.setValueAsBytes(desc.getValue());
	dbDesc.setTermEntryUuid(termEntryUuid);

	return dbDesc;
    }

    public static Set<DbTermEntryDescription> convertToDescriptions(String termEntryId, Collection<Description> descs) {
	Set<DbTermEntryDescription> dbDescs = new HashSet<>();

	if (CollectionUtils.isNotEmpty(descs)) {
	    for (Description desc : descs) {
		if (desc == null) {
		    continue;
		}

		dbDescs.add(convertToDescription(termEntryId, desc));
	    }
	}

	return dbDescs;
    }

    public static Set<Description> convertToTermDescriptions(Collection<DbTermEntryDescription> dbDescriptions) {
	Set<Description> descs = new HashSet<>();
	if (CollectionUtils.isNotEmpty(dbDescriptions)) {
	    for (DbTermEntryDescription description : dbDescriptions) {
		if (description == null) {
		    continue;
		}
		descs.add(convertToTermDescription(description));
	    }
	}
	return descs;
    }

    public static void mergeWithExistingDescription(DbTermEntryDescription incomming, DbTermEntryDescription existing) {
	if (incomming == null || existing == null) {
	    return;
	}

	existing.setBaseType(incomming.getBaseType());
	existing.setType(incomming.getType());
	existing.setValue(incomming.getValue());
    }

    public static void mergeWithExistingDescriptions(Collection<DbTermEntryDescription> incoming,
	    Collection<DbTermEntryDescription> existing) {
	if (CollectionUtils.isEmpty(incoming)) {
	    existing.clear();
	    return;
	}

	Set<DbTermEntryDescription> copyOfExisting = nullSafeCopy(existing);

	for (DbTermEntryDescription incomingDesc : incoming) {
	    DbTermEntryDescription existingDesc = findExistingDescriptionByUuid(incomingDesc.getUuid(), copyOfExisting);
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

    private static Description convertToTermDescription(DbTermEntryDescription description) {
	Description desc = new Description();
	desc.setUuid(description.getUuid());
	desc.setBaseType(description.getBaseType());
	desc.setType(description.getType());
	desc.setValue(description.getValueAsString());

	return desc;
    }

    private static DbTermEntryDescription findExistingDescriptionByUuid(String uuid,
	    Collection<DbTermEntryDescription> copyOfExisting) {
	Iterator<DbTermEntryDescription> each = copyOfExisting.iterator();
	while (each.hasNext()) {
	    DbTermEntryDescription candidate = each.next();
	    if (uuid.equals(candidate.getUuid())) {
		each.remove();
		return candidate;
	    }
	}
	return null;
    }

    private static Set<DbTermEntryDescription> nullSafeCopy(final Collection<DbTermEntryDescription> descriptions) {
	return descriptions == null ? Collections.emptySet() : new HashSet<>(descriptions);
    }
}
