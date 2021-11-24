package org.gs4tr.termmanager.service.xls;

import java.io.InputStream;
import java.util.List;

import org.gs4tr.foundation3.callback.ImportCallback;
import org.gs4tr.foundation3.tbx.TbxTermEntry;
import org.gs4tr.foundation3.tbx.io.DefaultTbxTermEntryReader;

public class DefaultTmgrTbxTermEntryReader extends DefaultTbxTermEntryReader {

    public DefaultTmgrTbxTermEntryReader(InputStream input, List<String> allowedLanguages) {
	super(input, allowedLanguages);
    }

    public DefaultTmgrTbxTermEntryReader(InputStream input, String encoding, List<String> allowedLanguages) {
	super(input, encoding, allowedLanguages);
    }

    @Override
    public void readTermEntries(ImportCallback callback) {
	int currentTermCount = 1;

	if (callback == null) {
	    return;
	}

	int totalTerms = callback.getTotalTerms();

	int percentage;

	try {
	    TbxTermEntry termEntry = readItem();
	    while (termEntry != null) {

		if (callback.isImportCanceled()) {
		    break;
		}

		callback.handleTermEntry(termEntry);

		percentage = (int) (((float) currentTermCount / (float) totalTerms) * 50);

		callback.handlePercentage(percentage);

		termEntry = readItem();

		currentTermCount++;
	    }
	} finally {
	    stop();
	    callback.handlePercentage(50);
	}
    }
}
