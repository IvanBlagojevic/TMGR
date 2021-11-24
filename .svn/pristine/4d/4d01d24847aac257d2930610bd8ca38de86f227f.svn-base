package org.gs4tr.termmanager.model.glossary.backup.converter;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTermEntryDescription;

public class DbSubmissionTermEntryDescriptionConverter {

    public static DbSubmissionTermEntryDescription convertToDescription(String submissionTermEntryUuid,
	    Description desc) {
	if (desc == null) {
	    return null;
	}

	DbSubmissionTermEntryDescription dbDesc = new DbSubmissionTermEntryDescription();
	dbDesc.setUuid(desc.getUuid());
	dbDesc.setBaseType(desc.getBaseType());
	dbDesc.setType(desc.getType());
	dbDesc.setValueAsBytes(desc.getValue());
	dbDesc.setSubmissionTermEntryUuid(submissionTermEntryUuid);

	return dbDesc;
    }

    public static Set<DbSubmissionTermEntryDescription> convertToDescriptions(String termEntryId,
	    Collection<Description> descs) {
	Set<DbSubmissionTermEntryDescription> dbDescs = new HashSet<DbSubmissionTermEntryDescription>();

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

    public static Set<Description> convertToDescriptions(Set<DbSubmissionTermEntryDescription> dbDescs) {

	Set<Description> descs = new HashSet<Description>();

	if (CollectionUtils.isNotEmpty(dbDescs)) {
	    for (DbSubmissionTermEntryDescription desc : dbDescs) {
		if (desc == null) {
		    continue;
		}

		descs.add(convertToTermDescription(desc));
	    }
	}

	return descs;
    }

    public static Description convertToTermDescription(DbSubmissionTermEntryDescription dbDesc) {
	if (dbDesc == null) {
	    return null;
	}

	Description desc = new Description();
	desc.setUuid(dbDesc.getUuid());
	desc.setBaseType(dbDesc.getBaseType());
	desc.setType(dbDesc.getType());
	desc.setValue(dbDesc.getValueAsString());

	return desc;
    }

    public static void mergeWithExistingDescription(DbSubmissionTermEntryDescription incomming,
	    DbSubmissionTermEntryDescription existing) {
	if (incomming == null || existing == null) {
	    return;
	}

	existing.setBaseType(incomming.getBaseType());
	existing.setType(incomming.getType());
	existing.setValue(incomming.getValue());
    }

    public static void mergeWithExistingDescriptions(Collection<DbSubmissionTermEntryDescription> incoming,
	    Collection<DbSubmissionTermEntryDescription> existing) {
	if (CollectionUtils.isEmpty(incoming)) {
	    existing.clear();
	    return;
	}

	Set<DbSubmissionTermEntryDescription> copyOfExisting = nullSafeCopy(existing);

	for (DbSubmissionTermEntryDescription incomingDesc : incoming) {
	    DbSubmissionTermEntryDescription existingDesc = findExistingDescriptionByUuid(incomingDesc.getUuid(),
		    copyOfExisting);
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

    private static DbSubmissionTermEntryDescription findExistingDescriptionByUuid(String uuid,
	    Collection<DbSubmissionTermEntryDescription> copyOfExisting) {
	Iterator<DbSubmissionTermEntryDescription> each = copyOfExisting.iterator();
	while (each.hasNext()) {
	    DbSubmissionTermEntryDescription candidate = each.next();
	    if (uuid.equals(candidate.getUuid())) {
		each.remove();
		return candidate;
	    }
	}
	return null;
    }

    private static Set<DbSubmissionTermEntryDescription> nullSafeCopy(
	    final Collection<DbSubmissionTermEntryDescription> descriptions) {
	return descriptions == null ? Collections.emptySet() : new HashSet<>(descriptions);
    }
}
