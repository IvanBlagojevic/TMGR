package org.gs4tr.termmanager.service.undoterm;

import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTermEntry;

public interface UndoTerm {

    TermEntry undoTerm(DbSubmissionTermEntry entry);
}
