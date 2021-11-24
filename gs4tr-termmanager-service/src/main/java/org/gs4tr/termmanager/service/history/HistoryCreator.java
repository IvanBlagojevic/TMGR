package org.gs4tr.termmanager.service.history;

import java.util.List;

import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.model.glossary.backup.DbBaseTermEntry;

public interface HistoryCreator {

    List<TermEntry> createHistory(DbBaseTermEntry dbTermEntry);
}
