package org.gs4tr.termmanager.model.glossary.backup.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTerm;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermDescription;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermEntry;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermHistory;
import org.gs4tr.termmanager.model.serializer.JsonIO;

public class DbTermConverter {

    public static DbTerm convertToDbTerm(String termEntryUuid, Term term) {
	if (term == null) {
	    return null;
	}

	String termId = term.getUuId();

	DbTerm dbTerm = new DbTerm();
	Long dateCreated = term.getDateCreated();
	if (dateCreated != null) {
	    dbTerm.setDateCreated(new Date(dateCreated));
	}
	Long dateModified = term.getDateModified();
	if (dateCreated != null) {
	    dbTerm.setDateModified(new Date(dateModified));
	}
	dbTerm.setDescriptions(DbTermDescriptionConverter.convertToDbTermDescriptions(termId, term.getDescriptions()));
	dbTerm.setDisabled(term.isDisabled());
	dbTerm.setFirst(term.isFirst());
	dbTerm.setForbidden(term.isForbidden());
	dbTerm.setInTranslationAsSource(term.getInTranslationAsSource());
	dbTerm.setLanguageId(term.getLanguageId());
	dbTerm.setNameAsBytes(term.getName());
	dbTerm.setProjectId(term.getProjectId());
	dbTerm.setStatus(term.getStatus());
	dbTerm.setStatusOld(term.getStatusOld());
	dbTerm.setUserCreated(term.getUserCreated());
	dbTerm.setUserLatestChange(term.getUserLatestChange());
	dbTerm.setUserModified(term.getUserModified());
	dbTerm.setUuId(termId);
	dbTerm.setTermEntryUuid(termEntryUuid);

	return dbTerm;
    }

    public static Set<DbTerm> convertToDbTerms(String termEntryUuid, Collection<Term> terms) {
	if (CollectionUtils.isEmpty(terms)) {
	    return Collections.EMPTY_SET;
	}

	Set<DbTerm> dbTerms = new HashSet<>();
	for (Term term : terms) {
	    if (term == null) {
		continue;
	    }

	    dbTerms.add(term.isDisabled() ? convertToDeletedTerm(term) : convertToDbTerm(termEntryUuid, term));
	}

	return dbTerms;
    }

    public static Term convertToTerm(DbTerm dbTerm) {
	if (dbTerm == null) {
	    return null;
	}
	Term term = new Term();
	boolean disabled = dbTerm.getDisabled();
	if (disabled) {
	    return null;
	}
	term.setTermEntryId(dbTerm.getTermEntryUuid());
	term.setDisabled(disabled);
	Date dateCreated = dbTerm.getDateCreated();
	if (dateCreated != null) {
	    term.setDateCreated(dateCreated.getTime());
	}
	Date dateModified = dbTerm.getDateModified();
	if (dateModified != null) {
	    term.setDateModified(dateModified.getTime());
	}
	term.setDescriptions(DbTermDescriptionConverter.convertToTermDescription(dbTerm.getDescriptions()));

	term.setName(dbTerm.getNameAsString());

	term.setForbidden(dbTerm.getForbidden());
	term.setLanguageId(dbTerm.getLanguageId());

	term.setProjectId(dbTerm.getProjectId());
	term.setStatus(dbTerm.getStatus());
	term.setStatusOld(dbTerm.getStatusOld());
	term.setUserCreated(dbTerm.getUserCreated());
	term.setUserLatestChange(dbTerm.getUserLatestChange());
	term.setUserModified(dbTerm.getUserModified());
	term.setUuId(dbTerm.getUuId());
	return term;
    }

    public static List<Term> convertToTerms(Collection<DbTerm> dbTerms) {
	if (CollectionUtils.isEmpty(dbTerms)) {
	    return Collections.EMPTY_LIST;
	}

	List<Term> terms = new ArrayList<>();
	for (DbTerm dbTerm : dbTerms) {
	    if (dbTerm == null) {
		continue;
	    }
	    Term term = convertToTerm(dbTerm);
	    if (term == null) {
		continue;
	    }
	    terms.add(term);
	}

	return terms;
    }

    public static void mergeWithExistingTerm(DbTerm incoming, DbTerm existing) {
	if (existing.getDateCreated() == null) {
	    existing.setDateCreated(incoming.getDateCreated());
	}
	existing.setDateModified(incoming.getDateModified());

	Set<DbTermDescription> incomingDescs = incoming.getDescriptions();
	Set<DbTermDescription> existingDescs = existing.getDescriptions();
	if (existingDescs != null) {
	    DbTermDescriptionConverter.mergeWithExistingDescriptions(incomingDescs, existingDescs);
	} else {
	    existing.setDescriptions(incomingDescs);
	}

	existing.setDisabled(incoming.getDisabled());
	existing.setFirst(incoming.getFirst());
	existing.setForbidden(incoming.getForbidden());
	existing.setInTranslationAsSource(incoming.getInTranslationAsSource());
	existing.setLanguageId(incoming.getLanguageId());
	existing.setName(incoming.getName());
	existing.setProjectId(incoming.getProjectId());
	existing.setStatus(incoming.getStatus());
	existing.setStatusOld(incoming.getStatusOld());
	if (existing.getUserCreated() == null) {
	    existing.setUserCreated(incoming.getUserCreated());
	}
	existing.setUserLatestChange(incoming.getUserLatestChange());
	existing.setUserModified(incoming.getUserModified());
    }

    public static void mergeWithExistingTerms(Collection<DbTerm> incoming, Collection<DbTerm> existing) {
	if (CollectionUtils.isEmpty(incoming)) {
	    existing.clear();
	    return;
	}
	Set<DbTerm> copyOfExisting = nullSafeCopy(existing);

	for (DbTerm incomingTerm : incoming) {
	    DbTerm existingTerm = findExistingDbTermByUuid(incomingTerm.getUuId(), copyOfExisting);
	    if (existingTerm != null) {
		mergeWithExistingTerm(incomingTerm, existingTerm);
	    } else {
		existing.add(incomingTerm);
	    }
	}

	if (CollectionUtils.isNotEmpty(copyOfExisting)) {
	    existing.removeIf(copyOfExisting::contains);
	}
    }

    private static DbTerm convertToDeletedTerm(Term term) {

	String deletedId = term.getUuId();

	DbTerm deletedTerm = new DbTerm();
	deletedTerm.setUuId(deletedId);
	deletedTerm.setLanguageId(term.getLanguageId());
	deletedTerm.setDisabled(Boolean.TRUE);
	deletedTerm.setNameAsBytes(deletedId);
	deletedTerm.setTermEntryUuid(term.getTermEntryId());

	// fake values
	deletedTerm.setDateCreated(new Date());
	deletedTerm.setDateModified(new Date());
	deletedTerm.setFirst(Boolean.FALSE);
	deletedTerm.setUserCreated(term.getUserCreated());
	deletedTerm.setUserModified(term.getUserModified());
	deletedTerm.setStatus(term.getStatus());

	return deletedTerm;
    }

    private static DbTermHistory createRevision(DbTerm existing, int revisionId) {

	DbTermHistory termHistory = new DbTermHistory();

	termHistory.setRevision(JsonIO.writeValueAsBytes(existing));
	termHistory.setTermEntryUuid(existing.getTermEntryUuid());
	termHistory.setRevisionId(revisionId);

	return termHistory;
    }

    private static DbTerm findExistingDbTermByUuid(String termUuid, Collection<DbTerm> copyOfExisting) {
	Iterator<DbTerm> each = copyOfExisting.iterator();
	while (each.hasNext()) {
	    DbTerm candidate = each.next();
	    if (termUuid.equals(candidate.getUuId())) {
		each.remove();
		return candidate;
	    }
	}
	return null;
    }

    private static boolean hasDifference(DbTerm incoming, DbTerm existing) {
	if (incoming.getDateModified() != null && existing.getDateModified() != null
		&& incoming.getDateModified().compareTo(existing.getDateModified()) != 0) {
	    return true;
	}
	if (!StringUtils.equals(incoming.getUserModified(), existing.getUserModified())) {
	    return true;
	}

	if (!StringUtils.equals(incoming.getNameAsString(), existing.getNameAsString())) {
	    return true;
	}
	    return incoming.getDisabled() != existing.getDisabled();
    }

    private static Map<String, DbTerm> mapTermsByUUID(Set<DbTerm> terms) {
	Map<String, DbTerm> map = new HashMap<String, DbTerm>();
	for (DbTerm dbTerm : terms) {
	    map.put(dbTerm.getUuId(), dbTerm);
	}
	return map;
    }

    private static Set<DbTerm> nullSafeCopy(final Collection<DbTerm> dbTerms) {
	return dbTerms == null ? Collections.emptySet() : new HashSet<>(dbTerms);
    }

    protected static Set<DbTermHistory> findDifferentTerms(DbTermEntry incoming, DbTermEntry existing,
	    int newRevisionId) {
	Set<DbTermHistory> history = new HashSet<DbTermHistory>();

	Set<DbTerm> existingTerms = existing.getTerms();
	if (Objects.isNull(incoming)) {
	    for (DbTerm term : existingTerms) {
		history.add(createRevision(term, newRevisionId));
	    }
	    return history;
	}

	Map<String, DbTerm> existingMaped = mapTermsByUUID(existingTerms);
	for (DbTerm dbTerm : incoming.getTerms()) {
	    DbTerm existingTerm = existingMaped.get(dbTerm.getUuId());
	    if (existingTerm != null) {
		if (hasDifference(dbTerm, existingTerm)) {
		    DbTermHistory revision = createRevision(existingTerm, newRevisionId);
		    history.add(revision);
		}
	    }

	}
	return history;
    }
}
