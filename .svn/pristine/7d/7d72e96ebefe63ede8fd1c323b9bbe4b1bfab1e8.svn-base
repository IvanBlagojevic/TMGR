package org.gs4tr.termmanager.model.glossary.backup.converter;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.termmanager.model.glossary.Comment;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbComment;

public class DbCommentConverter {

    public static Comment convertToComment(DbComment dbComment) {
	if (dbComment == null) {
	    return null;
	}

	Comment comment = new Comment();
	comment.setText(dbComment.getText());
	comment.setUser(dbComment.getUser());

	return comment;
    }

    public static Set<Comment> convertToComments(Collection<DbComment> dbComments) {
	Set<Comment> comments = new HashSet<Comment>();

	if (CollectionUtils.isNotEmpty(dbComments)) {
	    for (DbComment comment : dbComments) {
		if (comment == null) {
		    continue;
		}

		comments.add(convertToComment(comment));
	    }
	}

	return comments;
    }

    public static DbComment convertToDbComment(String termUuid, Comment comment) {
	if (comment == null) {
	    return null;
	}

	DbComment dbComment = new DbComment();
	dbComment.setSubmissionTermUuid(termUuid);
	dbComment.setText(comment.getText());
	dbComment.setUser(comment.getUser());
	dbComment.setUuid(UUID.randomUUID().toString());

	return dbComment;
    }

    public static Set<DbComment> convertToDbComments(String termId, Collection<Comment> comments) {
	Set<DbComment> dbComments = new HashSet<DbComment>();

	if (CollectionUtils.isNotEmpty(comments)) {
	    for (Comment comment : comments) {
		if (comment == null) {
		    continue;
		}

		dbComments.add(convertToDbComment(termId, comment));
	    }
	}

	return dbComments;
    }

    public static void mergeWithExistingComment(DbComment incoming, DbComment existing) {
	if (incoming == null || existing == null) {
	    return;
	}

	existing.setText(incoming.getText());
	existing.setUser(incoming.getUser());
    }

    public static void mergeWithExistingComments(Collection<DbComment> incoming, Collection<DbComment> existing) {
	if (CollectionUtils.isEmpty(incoming)) {
	    existing.clear();
	    return;
	}

	Set<DbComment> copyOfExisting = nullSafeCopy(existing);

	for (DbComment incomingDesc : incoming) {
	    DbComment existingComment = findExistingCommentByUuid(incomingDesc.getUuid(), copyOfExisting);
	    if (existingComment != null) {
		mergeWithExistingComment(incomingDesc, existingComment);
	    } else {
		existing.add(incomingDesc);
	    }
	}
	if (CollectionUtils.isNotEmpty(copyOfExisting)) {
	    copyOfExisting.forEach(existing::remove);
	}
    }

    private static DbComment findExistingCommentByUuid(String uuid, Collection<DbComment> copyOfExisting) {
	Iterator<DbComment> each = copyOfExisting.iterator();
	while (each.hasNext()) {
	    DbComment candidate = each.next();
	    if (uuid.equals(candidate.getUuid())) {
		each.remove();
		return candidate;
	    }
	}
	return null;
    }

    private static Set<DbComment> nullSafeCopy(final Collection<DbComment> comments) {
	return comments == null ? Collections.emptySet() : new HashSet<>(comments);
    }
}
