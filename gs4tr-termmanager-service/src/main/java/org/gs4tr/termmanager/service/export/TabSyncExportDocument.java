package org.gs4tr.termmanager.service.export;

import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.glossary.Term;

public class TabSyncExportDocument extends AbstractDelimiterExportDocument<TabLineBuilder> {

    @Override
    TabLineBuilder getBuilderInstance() {
	return new TabLineBuilder();
    }

    @Override
    protected boolean isExportableByForbidden(Term term, Boolean exportForbidden) {
	return (exportForbidden != null && exportForbidden) || !term.isForbidden();
    }

    @Override
    protected boolean isExportableByInTranslation(Term term, Boolean exportable) {
	return exportable || !ItemStatusTypeHolder.isTermInTranslation(term);
    }

    @Override
    protected boolean isExportableByStatus(Term term) {
	String status = term.getStatus();
	return ItemStatusTypeHolder.PROCESSED.getName().equals(status)
		|| ItemStatusTypeHolder.BLACKLISTED.getName().equals(status);
    }
}
