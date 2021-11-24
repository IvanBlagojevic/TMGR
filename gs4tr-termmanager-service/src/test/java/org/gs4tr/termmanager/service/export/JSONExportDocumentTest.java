package org.gs4tr.termmanager.service.export;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.gs4tr.termmanager.model.ExportInfo;
import org.gs4tr.termmanager.model.dto.ExportTermModel;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.model.search.TermEntrySearchRequest;
import org.gs4tr.termmanager.service.mocking.AbstractServiceTest;
import org.gs4tr.termmanager.service.mocking.model.TestCase;
import org.gs4tr.termmanager.service.mocking.model.TestSuite;
import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.junit.Test;

@TestSuite("export")
public class JSONExportDocumentTest extends AbstractServiceTest {

    /*
     * TERII-5848 WF3 | Terms added as pending in a project with Approved default
     * status and Share Pending terms UNCHECKED can been seen by everyone
     */
    @Test
    @TestCase("exportJson")
    public void test_TERII_5848() throws Exception {
	TermEntrySearchRequest request = getModelObject("request", TermEntrySearchRequest.class);

	ExportDocumentFactory jsonExportDocument = ExportDocumentFactory.getInstance(ExportFormatEnum.JSON);
	jsonExportDocument.setSearchRequest(request);
	assertNotNull(jsonExportDocument);

	TermEntry entry = getModelObject("termEntry1", TermEntry.class);
	List<Term> terms = entry.ggetTerms();

	assertNotNull(entry.getUuId());
	assertEquals(terms.size(), 2);
	assertNotNull(terms.get(0).getUuId());

	String jsonString = jsonExportDocument.buildTermEntryXml(entry, new ExportInfo(), true, null);
	assertNotNull(jsonString);

	ExportTermModel exportTermModel = JsonUtils.readValue(jsonString, ExportTermModel.class);
	assertNotNull(exportTermModel);
	/*
	 * If exortTermModel operation is equals to "D" user can't see that term-entry
	 */
	assertEquals(exportTermModel.getOperation(), "D");
    }
}
