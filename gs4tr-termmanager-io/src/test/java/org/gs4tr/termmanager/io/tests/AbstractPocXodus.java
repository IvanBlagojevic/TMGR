package org.gs4tr.termmanager.io.tests;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.termmanager.model.glossary.TermEntry;

public class AbstractPocXodus {

    protected static final String STORE_ENTRY = "EntriesStore";

    protected final Log LOG = LogFactory.getLog(this.getClass());

    protected TermEntry createTermEntry() {
	return TestHelper.createTermEntry();
    }
}
