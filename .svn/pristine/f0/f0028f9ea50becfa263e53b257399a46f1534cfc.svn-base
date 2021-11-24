package org.gs4tr.termmanager.service.termentry.reader.factory;

import org.gs4tr.foundation3.reader.TermEntryReader;
import org.gs4tr.termmanager.model.ImportTypeEnum;
import org.gs4tr.termmanager.service.xls.DefaultTmgrTbxTermEntryReader;
import org.gs4tr.termmanager.service.xls.DefaultXlsTermEntryReader;

public enum TermEntryReaderFactory {
    INSTANCE;

    public TermEntryReader createReader(TermEntryReaderConfig c) {
	ImportTypeEnum type = c.getType();
	TermEntryReader reader = null;
	switch (type) {
	case TBX:
	    reader = new DefaultTmgrTbxTermEntryReader(c.getIn(), c.getEncoding(), c.getUserProjectLanguages());
	    break;
	case XLS:
	    reader = new DefaultXlsTermEntryReader(c);
	    break;
	default:
	    reader = new DefaultTmgrTbxTermEntryReader(c.getIn(), c.getUserProjectLanguages());
	    break;
	}
	return reader;
    }
}
