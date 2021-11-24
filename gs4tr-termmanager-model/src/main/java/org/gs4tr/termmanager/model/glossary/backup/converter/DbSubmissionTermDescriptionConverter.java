package org.gs4tr.termmanager.model.glossary.backup.converter;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTermDescription;

public class DbSubmissionTermDescriptionConverter {

    public static DbSubmissionTermDescription convertToDbTermDescription(String termUuid, Description desc) {
	if (desc == null) {
	    return null;
	}

	DbSubmissionTermDescription dbDesc = new DbSubmissionTermDescription();
	dbDesc.setUuid(desc.getUuid());
	dbDesc.setBaseType(desc.getBaseType());
	dbDesc.setType(desc.getType());
	dbDesc.setValueAsBytes(desc.getValue());
	dbDesc.setTempValue(desc.getTempValue());
	dbDesc.setSubmissionTermUuid(termUuid);

	return dbDesc;
    }

    public static Description convertToTermDescription(DbSubmissionTermDescription dbDesc) {
	if (dbDesc == null) {
	    return null;
	}

	Description desc = new Description();
	desc.setUuid(dbDesc.getUuid());
	desc.setBaseType(dbDesc.getBaseType());
	desc.setType(dbDesc.getType());
	desc.setValue(dbDesc.getValueAsString());
	desc.setTempValue(dbDesc.getTempValue());

	return desc;
    }

    public static Set<DbSubmissionTermDescription> convertToDbTermDescriptions(String termId,
	    Collection<Description> descs) {
	Set<DbSubmissionTermDescription> dbDescs = new HashSet<DbSubmissionTermDescription>();

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

    public static Set<Description> convertToTermDescriptions(Collection<DbSubmissionTermDescription> dbDescriptions) {
	Set<Description> descs = new HashSet<Description>();

	if (CollectionUtils.isNotEmpty(dbDescriptions)) {
	    for (DbSubmissionTermDescription desc : dbDescriptions) {
		if (desc == null) {
		    continue;
		}

		descs.add(convertToTermDescription(desc));
	    }
	}

	return descs;
    }

    public static void mergeWithExistingDescription(DbSubmissionTermDescription incoming,
	    DbSubmissionTermDescription existing) {
	if (incoming == null || existing == null) {
	    return;
	}

	existing.setBaseType(incoming.getBaseType());
	existing.setType(incoming.getType());
	existing.setValue(incoming.getValue());
	existing.setTempValue(incoming.getTempValue());
    }

    public static void mergeWithExistingDescriptions(Collection<DbSubmissionTermDescription> incoming,
	    Collection<DbSubmissionTermDescription> existing) {
	if (CollectionUtils.isEmpty(incoming)) {
	    existing.clear();
	    return;
	}

	Set<DbSubmissionTermDescription> copyOfExisting = nullSafeCopy(existing);

	for (DbSubmissionTermDescription incomingDesc : incoming) {
	    DbSubmissionTermDescription existingDesc = findExistingDescriptionByUuid(incomingDesc.getUuid(),
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

    private static DbSubmissionTermDescription findExistingDescriptionByUuid(String uuid,
	    final Collection<DbSubmissionTermDescription> copyOfExisting) {
	Iterator<DbSubmissionTermDescription> each = copyOfExisting.iterator();
	while (each.hasNext()) {
	    DbSubmissionTermDescription candidate = each.next();
	    if (uuid.equals(candidate.getUuid())) {
		each.remove();
		return candidate;
	    }
	}
	return null;
    }

    private static Set<DbSubmissionTermDescription> nullSafeCopy(
	    final Collection<DbSubmissionTermDescription> descriptions) {
	return descriptions == null ? Collections.emptySet() : new HashSet<>(descriptions);
    }
}
