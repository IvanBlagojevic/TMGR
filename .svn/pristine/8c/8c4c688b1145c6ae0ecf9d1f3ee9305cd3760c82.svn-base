package org.gs4tr.termmanager.service.export;

import static org.gs4tr.termmanager.service.utils.TermEntryUtils.ID;
import static org.gs4tr.termmanager.service.utils.TermEntryUtils.STATUS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.gs4tr.termmanager.model.ExportInfo;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.model.search.TermEntrySearchRequest;
import org.gs4tr.termmanager.service.mocking.AbstractServiceTest;
import org.gs4tr.termmanager.service.mocking.model.TestCase;
import org.gs4tr.termmanager.service.mocking.model.TestSuite;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@TestSuite("export")
public class TbxExportDocumentTest extends AbstractServiceTest {

    public static final String TERM_ENTRY = "termEntry";
    public static final String TIG = "tig";
    public static final String TYPE = "type";

    @Test
    @TestCase("exportTbx")
    public void termEntryUUIDAndTermsUUIDIsExportedInTbx() throws Exception {

	TermEntrySearchRequest request = getModelObject("request", TermEntrySearchRequest.class);
	ExportDocumentFactory tbxExportDocument = ExportDocumentFactory.getInstance(ExportFormatEnum.TBX);
	tbxExportDocument.setSearchRequest(request);

	TermEntry entry = getModelObject("termEntry1", TermEntry.class);
	List<Term> terms = entry.ggetTerms();

	assertNotNull(entry.getUuId());
	assertEquals(terms.size(), 2);
	assertNotNull(terms.get(0).getUuId());
	assertNotNull(terms.get(1).getUuId());

	String tbxString = tbxExportDocument.buildTermEntryXml(entry, new ExportInfo(), true, null);

	Document doc = parseTbxString(tbxString);

	NodeList termEntryNodeList = doc.getElementsByTagName(TERM_ENTRY);
	assertEquals(termEntryNodeList.getLength(), 1);

	Node termEntryNode = termEntryNodeList.item(0).getFirstChild().getNextSibling();
	String termEntryDescriptionType = termEntryNode.getAttributes().getNamedItem(TYPE).getTextContent();
	String termEntryDescriptionValue = termEntryNode.getTextContent();

	assertTrue(termEntryDescriptionType.equals(ID));
	assertEquals(termEntryDescriptionValue, entry.getUuId());

	NodeList termList = doc.getElementsByTagName(TIG);
	assertEquals(termList.getLength(), 2);

	for (int i = 0; i < termList.getLength(); i++) {
	    Node descriptionNode = termList.item(i).getFirstChild().getNextSibling().getNextSibling().getNextSibling()
		    .getNextSibling().getNextSibling();
	    String termDescriptionType = descriptionNode.getAttributes().getNamedItem(TYPE).getTextContent();
	    String termDescriptionValue = descriptionNode.getTextContent();
	    assertEquals(termDescriptionType, ID);
	    assertTrue(StringUtils.isNotEmpty(termDescriptionValue));
	}
    }

    @Test
    @TestCase("exportTbx")
    public void termsWithDifferentStatusesAreExportedInTbx() throws Exception {

	TermEntrySearchRequest request = getModelObject("request", TermEntrySearchRequest.class);
	ExportDocumentFactory tbxExportDocument = ExportDocumentFactory.getInstance(ExportFormatEnum.TBX);
	tbxExportDocument.setSearchRequest(request);

	TermEntry entry = getModelObject("termEntry2", TermEntry.class);
	List<Term> terms = entry.ggetTerms();

	assertEquals(terms.size(), 4);
	assertEquals(terms.get(0).getStatus(), ItemStatusTypeHolder.WAITING.toString());
	assertEquals(terms.get(1).getStatus(), ItemStatusTypeHolder.BLACKLISTED.toString());
	assertEquals(terms.get(2).getStatus(), ItemStatusTypeHolder.PROCESSED.toString());
	assertEquals(terms.get(3).getStatus(), ItemStatusTypeHolder.ON_HOLD.toString());

	String tbxString = tbxExportDocument.buildTermEntryXml(entry, new ExportInfo(), true, null);

	Document doc = parseTbxString(tbxString);

	NodeList termList = doc.getElementsByTagName(TIG);
	assertEquals(termList.getLength(), 4);

	for (int i = 0; i < termList.getLength(); i++) {
	    Node statusNode = termList.item(i).getFirstChild().getNextSibling().getNextSibling().getNextSibling();
	    String type = statusNode.getAttributes().getNamedItem(TYPE).getTextContent();
	    String value = statusNode.getTextContent();
	    assertEquals(type, STATUS);
	    assertTrue(StringUtils.isNotEmpty(value));
	}

    }

    private Document parseTbxString(String tbxString) throws ParserConfigurationException, IOException, SAXException {

	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	DocumentBuilder builder = factory.newDocumentBuilder();
	InputStream in = new ByteArrayInputStream(tbxString.getBytes());
	Document doc = builder.parse(in);
	doc.getDocumentElement().normalize();
	return doc;

    }

}
