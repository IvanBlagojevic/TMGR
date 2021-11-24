package org.gs4tr.termmanager.service.model.command;

import java.util.List;

import org.gs4tr.termmanager.model.TermEntryTranslationUnit;

public class BaseDashboardCommand {

    private List<TermEntryTranslationUnit> _termEntryTranslationUnits;

    public List<TermEntryTranslationUnit> getTermEntryTranslationUnits() {
	return _termEntryTranslationUnits;
    }

    public void setTermEntryTranslationUnits(List<TermEntryTranslationUnit> termEntryTranslationUnits) {
	_termEntryTranslationUnits = termEntryTranslationUnits;
    }
}
