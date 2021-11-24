package org.gs4tr.termmanager.service.mocking.utils;

import static org.gs4tr.termmanager.service.utils.TermEntryUtils.ID;
import static org.gs4tr.termmanager.service.utils.TermEntryUtils.STATUS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.gs4tr.foundation3.tbx.TbxTermEntry;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.model.xls.XlsDescription;
import org.gs4tr.termmanager.model.xls.XlsTerm;
import org.gs4tr.termmanager.model.xls.XlsTermEntry;
import org.gs4tr.termmanager.persistence.update.DescriptionImportOption;
import org.gs4tr.termmanager.persistence.update.ImportOptionsModel;
import org.gs4tr.termmanager.service.mocking.AbstractServiceTest;
import org.gs4tr.termmanager.service.termentry.synchronization.SyncOption;
import org.gs4tr.termmanager.service.utils.TermEntryUtils;
import org.junit.Test;

public class TermEntryUtilsTest extends AbstractServiceTest {

    @Test
    public void createTermEntryFromTbxForRestV2() {

	String termDescriptionType = "termDescription";
	String termEntryDescriptionType = "termEntryDescription";

	ImportOptionsModel model = new ImportOptionsModel();
	model.setDescriptionImportOption(DescriptionImportOption.ADD_ALL);

	TbxTermEntry tbxTermEntry = createTbxTermEntryWithTermAttribute("englishTerm", termDescriptionType, "value");
	tbxTermEntry.addDescription(termEntryDescriptionType, "value");

	TermEntry entry = TermEntryUtils.createTermEntry(tbxTermEntry, model, null);
	assertNotNull(entry);

	Set<Description> termEntryDescriptions = entry.getDescriptions();
	assertNotNull(termEntryDescriptions);
	assertEquals(1, termEntryDescriptions.size());
	assertEquals(termEntryDescriptionType, termEntryDescriptions.iterator().next().getType());

	Set<Description> termDescriptions = entry.ggetTerms().get(0).getDescriptions();
	assertNotNull(termDescriptions);
	assertEquals(1, termDescriptions.size());
	assertEquals(termDescriptionType, termDescriptions.iterator().next().getType());

    }

    @Test
    public void createTermEntryFromTbxWithAllowedTermDescriptionTypes() {

	Set<String> allowedTypes = new HashSet<>();
	allowedTypes.add("type");
	allowedTypes.add("context");

	ImportOptionsModel model = createImportOptionModelWithAllowedDescriptionType(allowedTypes,
		Collections.emptySet());
	assertNotNull(model.getAllowedTermDescriptions());
	assertEquals(2, model.getAllowedTermDescriptions().get(Description.ATTRIBUTE).size());
	assertEquals(0, model.getAllowedTermDescriptions().get(Description.NOTE).size());

	TbxTermEntry tbxTermEntry = createTbxTermEntryWithTermAttribute("englishTerm", "type", "value");
	TbxTermEntry tbxTermEntry1 = createTbxTermEntryWithTermAttribute("englishTerm1", "context", "context");
	TbxTermEntry tbxTermEntry2 = createTbxTermEntryWithTermAttribute("englishTerm2", "definition", "definition");

	TermEntry entry = TermEntryUtils.createTermEntry(tbxTermEntry, model, SyncOption.OVERWRITE);
	assertNotNull(entry);
	assertEquals(1, entry.ggetTerms().get(0).getDescriptions().size());

	TermEntry entry1 = TermEntryUtils.createTermEntry(tbxTermEntry1, model, SyncOption.OVERWRITE);
	assertNotNull(entry1);
	assertEquals(1, entry1.ggetTerms().get(0).getDescriptions().size());

	TermEntry entry2 = TermEntryUtils.createTermEntry(tbxTermEntry2, model, SyncOption.OVERWRITE);
	assertNotNull(entry2);
	assertEquals(0, entry2.ggetTerms().get(0).getDescriptions().size());

    }

    @Test
    public void createTermEntryFromTbxWithTermDescriptionReplacement() {

	String attributeType = "context";
	String attributeReplacement = "replacedContext";

	ImportOptionsModel model = createImportModelWithTermDescriptionReplacement(attributeType, attributeReplacement);

	TbxTermEntry tbxTermEntry = createTbxTermEntryWithTermAttribute("englishTerm", attributeType, "value");

	TermEntry entry = TermEntryUtils.createTermEntry(tbxTermEntry, model, SyncOption.OVERWRITE);
	assertNotNull(entry);

	Set<Description> descriptions = entry.ggetTerms().get(0).getDescriptions();

	assertNotNull(descriptions);
	assertEquals(1, descriptions.size());
	assertEquals(descriptions.iterator().next().getType(), attributeReplacement);

    }

    @Test
    public void createTermEntryFromTbxWithTermEntryDescriptionReplacement() {

	String attributeType = "definition";
	String attributeReplacement = "replacedDefinition";

	ImportOptionsModel model = createImportModelWithTermEntryDescriptionReplacement(attributeType,
		attributeReplacement);

	TbxTermEntry tbxTermEntry = createTbxTermEntryWithTermAttribute("englishTerm", "context", "context");
	tbxTermEntry.addDescription(attributeType, "value");

	TermEntry entry = TermEntryUtils.createTermEntry(tbxTermEntry, model, SyncOption.OVERWRITE);
	assertNotNull(entry);

	Set<Description> descriptions = entry.getDescriptions();

	assertNotNull(descriptions);
	assertEquals(1, descriptions.size());
	assertEquals(descriptions.iterator().next().getType(), attributeReplacement);

    }

    @Test
    public void createTermEntryFromTbxWithTermNotes() {

	Set<String> allowedTypes = new HashSet<>();
	allowedTypes.add("type");

	ImportOptionsModel model = createImportOptionModelWithAllowedDescriptionType(Collections.emptySet(),
		allowedTypes);

	TbxTermEntry tbxTermEntry = createTbxTermEntryWithTermNote("englishTerm", "type", "value");

	TermEntry entry = TermEntryUtils.createTermEntry(tbxTermEntry, model, SyncOption.OVERWRITE);
	assertNotNull(entry);

	Set<Description> descriptions = entry.ggetTerms().get(0).getDescriptions();

	assertNotNull(descriptions);
	assertEquals(1, descriptions.size());

	Description description = descriptions.iterator().next();

	assertEquals(Description.NOTE, description.getBaseType());
	assertNotNull(description.getUuid());

    }

    @Test
    public void createTermEntryFromXlsForRestV2() {

	String termDescriptionType = "termDescription";
	String termEntryDescriptionType = "termEntryDescription";

	ImportOptionsModel model = new ImportOptionsModel();
	model.setDescriptionImportOption(DescriptionImportOption.ADD_ALL);

	XlsTermEntry xlsEntry = createXlsEntryWithTermAttribute("englishTerm", termDescriptionType, "value");
	XlsDescription termEntryDescription = new XlsDescription(termEntryDescriptionType, "value");
	List<XlsDescription> descriptions = new ArrayList<>();
	descriptions.add(termEntryDescription);
	xlsEntry.setTermEntryDescriptions(descriptions);

	TermEntry entry = TermEntryUtils.createTermEntry(xlsEntry, model, SyncOption.OVERWRITE);
	assertNotNull(entry);

	Set<Description> termEntryDescriptions = entry.getDescriptions();
	assertNotNull(termEntryDescriptions);
	assertEquals(1, termEntryDescriptions.size());

	Description termEntryDesc = termEntryDescriptions.iterator().next();

	assertEquals(termEntryDescriptionType, termEntryDesc.getType());
	assertNotNull(termEntryDesc.getUuid());

	Set<Description> termDescriptions = entry.ggetTerms().get(0).getDescriptions();
	assertNotNull(termDescriptions);
	assertEquals(1, termDescriptions.size());

	Description termDesc = termDescriptions.iterator().next();

	assertEquals(termDescriptionType, termDesc.getType());
	assertNotNull(termDesc.getUuid());

    }

    @Test
    public void createTermEntryFromXlsWithAllowedTermDescriptionTypes() {

	Set<String> allowedTypes = new HashSet<>();
	allowedTypes.add("type");
	allowedTypes.add("context");

	ImportOptionsModel model = createImportOptionModelWithAllowedDescriptionType(allowedTypes,
		Collections.emptySet());

	assertNotNull(model.getAllowedTermDescriptions());
	assertEquals(2, model.getAllowedTermDescriptions().get(Description.ATTRIBUTE).size());
	assertEquals(0, model.getAllowedTermDescriptions().get(Description.NOTE).size());

	XlsTermEntry xlsEntry = createXlsEntryWithTermAttribute("englishTerm", "type", "value");
	XlsTermEntry xlsEntry1 = createXlsEntryWithTermAttribute("englishTerm1", "context", "context");
	XlsTermEntry xlsEntry2 = createXlsEntryWithTermAttribute("englishTerm2", "definition", "definition");

	TermEntry entry = TermEntryUtils.createTermEntry(xlsEntry, model, SyncOption.OVERWRITE);
	assertNotNull(entry);

	String entryTermName = entry.ggetTerms().get(0).getName();
	String xlsEntryTermName = xlsEntry.getTerms().get(0).getTerm();

	assertEquals(entryTermName, xlsEntryTermName);
	assertEquals(1, entry.ggetTerms().get(0).getDescriptions().size());

	TermEntry entry1 = TermEntryUtils.createTermEntry(xlsEntry1, model, SyncOption.OVERWRITE);
	assertNotNull(entry1);

	String entry1TermName = entry1.ggetTerms().get(0).getName();
	String xlsEntry1TermName = xlsEntry1.getTerms().get(0).getTerm();

	assertEquals(entry1TermName, xlsEntry1TermName);
	assertEquals(1, entry1.ggetTerms().get(0).getDescriptions().size());

	TermEntry entry2 = TermEntryUtils.createTermEntry(xlsEntry2, model, SyncOption.OVERWRITE);
	assertNotNull(entry2);

	String entry2TermName = entry1.ggetTerms().get(0).getName();
	String xlsEntry2TermName = xlsEntry1.getTerms().get(0).getTerm();

	assertEquals(entry2TermName, xlsEntry2TermName);
	assertEquals(0, entry2.ggetTerms().get(0).getDescriptions().size());

    }

    @Test
    public void createTermEntryFromXlsWithAllowedTermNoteTypes() {

	Set<String> allowedTypes = new HashSet<>();
	allowedTypes.add("type");

	ImportOptionsModel model = createImportOptionModelWithAllowedDescriptionType(Collections.emptySet(),
		allowedTypes);

	XlsTermEntry xlsEntry = createXlsEntryWithTermNote("englishTerm", "type", "value");

	TermEntry entry = TermEntryUtils.createTermEntry(xlsEntry, model, SyncOption.OVERWRITE);
	assertNotNull(entry);

	Set<Description> descriptions = entry.ggetTerms().get(0).getDescriptions();

	assertNotNull(descriptions);
	assertEquals(1, descriptions.size());
	assertEquals(descriptions.iterator().next().getBaseType(), Description.NOTE);

    }

    @Test
    public void createTermEntryFromXlsWithTermDescriptionReplacements() {

	String attributeType = "context";
	String attributeReplacement = "replacedContext";

	ImportOptionsModel model = createImportModelWithTermDescriptionReplacement(attributeType, attributeReplacement);

	XlsTermEntry xlsEntry = createXlsEntryWithTermAttribute("englishTerm", attributeType, "value");

	TermEntry entry = TermEntryUtils.createTermEntry(xlsEntry, model, SyncOption.OVERWRITE);
	assertNotNull(entry);

	Set<Description> descriptions = entry.ggetTerms().get(0).getDescriptions();

	assertNotNull(descriptions);
	assertEquals(1, descriptions.size());
	assertEquals(descriptions.iterator().next().getType(), attributeReplacement);

    }

    @Test
    public void createTermEntryFromXlsWithTermEntryDescriptionReplacements() {

	String attributeType = "definition";
	String attributeReplacement = "replacedDefinition";

	ImportOptionsModel model = createImportModelWithTermEntryDescriptionReplacement(attributeType,
		attributeReplacement);

	XlsTermEntry xlsEntry = createXlsEntryWithTermAttribute("englishTerm", null, null);

	XlsDescription description = new XlsDescription(attributeType, "value");
	List<XlsDescription> XlsDescriptions = new ArrayList<>();
	XlsDescriptions.add(description);
	xlsEntry.setTermEntryDescriptions(XlsDescriptions);

	TermEntry entry = TermEntryUtils.createTermEntry(xlsEntry, model, SyncOption.OVERWRITE);
	assertNotNull(entry);

	Set<Description> descriptions = entry.getDescriptions();

	assertNotNull(descriptions);
	assertEquals(1, descriptions.size());
	assertEquals(descriptions.iterator().next().getType(), attributeReplacement);

    }

    @Test
    public void uuidAndStatusDescriptionTypesWillNotBeImportedFromTbx() {

	String status = ItemStatusTypeHolder.getStatusDisplayName("PROCESSED");

	TbxTermEntry termEntry = createTbxTermEntryWithTermAttribute("under the influence", ID, "term-uuid01");
	termEntry.addDescription(ID, "termEntry-uuid1");
	termEntry.getLanguageIterator().next().getTermIterator().next().addTermNote(STATUS, status);

	TermEntry entry = TermEntryUtils.createTermEntry(termEntry, new ImportOptionsModel(), SyncOption.OVERWRITE);
	assertNotNull(entry);

	assertNull(entry.getDescriptions());
	assertTrue(entry.ggetTerms().get(0).getDescriptions().isEmpty());

    }

    @Test
    public void uuidForTermAndTermEntryAndTermStatusWillBeSetFromDescriptionTags() {

	String status = ItemStatusTypeHolder.getStatusDisplayName("PROCESSED");
	String termUUID = "term-uuid01";
	String termEntryUUID = "termEntry-uuid1";

	TbxTermEntry termEntry = createTbxTermEntryWithTermAttribute("under the influence", ID, termUUID);
	termEntry.addDescription(ID, termEntryUUID);
	termEntry.getLanguageIterator().next().getTermIterator().next().addTermNote(STATUS, status);

	TermEntry entry = TermEntryUtils.createTermEntry(termEntry, new ImportOptionsModel(), SyncOption.OVERWRITE);
	assertNotNull(entry);

	Term term = entry.ggetTerms().get(0);
	assertNotNull(term);

	assertEquals(entry.getUuId(), termEntryUUID);
	assertEquals(term.getUuId(), termUUID);
	assertEquals(term.getStatus(), ItemStatusTypeHolder.PROCESSED.getName());

    }

    private ImportOptionsModel createImportModelWithTermDescriptionReplacement(String attributeType,
	    String attributeReplacement) {

	ImportOptionsModel model = new ImportOptionsModel();

	Map<String, String> attributeReplacements = new HashMap<>();
	attributeReplacements.put(attributeType, attributeReplacement);
	Map<String, Map<String, String>> attributeNoteReplacements = new HashMap<>();
	attributeNoteReplacements.put(Description.ATTRIBUTE, attributeReplacements);

	Set<String> allowedAttributes = new HashSet<>();
	allowedAttributes.add(attributeReplacement);

	Map<String, Set<String>> allowedTermDescriptions = new HashMap<>(2);
	allowedTermDescriptions.put(Description.ATTRIBUTE, allowedAttributes);
	allowedTermDescriptions.put(Description.NOTE, Collections.emptySet());
	model.setAllowedTermDescriptions(allowedTermDescriptions);
	model.setAttributeNoteReplacements(attributeNoteReplacements);

	return model;

    }

    private ImportOptionsModel createImportModelWithTermEntryDescriptionReplacement(String attributeType,
	    String attributeReplacement) {

	ImportOptionsModel model = new ImportOptionsModel();

	Set<String> allowedTermEntryAttributes = new HashSet<>();
	allowedTermEntryAttributes.add(attributeReplacement);

	model.setAllowedTermEntryAttributes(allowedTermEntryAttributes);

	Map<String, String> termEntryAttributeReplacements = new HashMap<>();
	termEntryAttributeReplacements.put(attributeType, attributeReplacement);

	model.setTermEntryAttributeReplacements(termEntryAttributeReplacements);

	return model;

    }

    private ImportOptionsModel createImportOptionModelWithAllowedDescriptionType(Set<String> allowedAttributes,
	    Set<String> allowedNotes) {
	ImportOptionsModel model = new ImportOptionsModel();

	Map<String, Set<String>> allowedTermDescriptions = new HashMap<>(2);
	allowedTermDescriptions.put(Description.ATTRIBUTE, allowedAttributes);
	allowedTermDescriptions.put(Description.NOTE, allowedNotes);
	model.setAllowedTermDescriptions(allowedTermDescriptions);

	return model;
    }

    private TbxTermEntry createTbxTermEntryWithTermAttribute(String termName, String termDescriptionType,
	    String termDescriptionValue) {
	TbxTermEntry tbxTermEntry = TbxTermEntry.createEmptyTbxTermEntry();
	tbxTermEntry.addLanguage(org.gs4tr.foundation.locale.Locale.US, true);
	tbxTermEntry.getLanguageIterator().next().addTerm(termName).addDescription(termDescriptionType,
		termDescriptionValue);

	return tbxTermEntry;

    }

    private TbxTermEntry createTbxTermEntryWithTermNote(String termName, String termNoteType, String termNoteValue) {

	TbxTermEntry tbxTermEntry = TbxTermEntry.createEmptyTbxTermEntry();
	tbxTermEntry.addLanguage(org.gs4tr.foundation.locale.Locale.US, true);
	tbxTermEntry.getLanguageIterator().next().addTerm(termName).addTermNote(termNoteType, termNoteValue);

	return tbxTermEntry;

    }

    private XlsTerm createTerm(String termName, String languageId, String status) {
	XlsTerm term = new XlsTerm();
	term.setTerm(termName);
	term.setLocaleCode(languageId);
	term.setXlsStatus(status);

	return term;

    }

    private XlsTermEntry createXlsEntryWithTermAttribute(String termName, String termDescType, String termDescValue) {

	XlsTermEntry xlsEntry = new XlsTermEntry();
	xlsEntry.setXlsUuId(UUID.randomUUID().toString());

	XlsTerm xlsTerm = createTerm(termName, "en-US", "APPROVED");
	XlsDescription description = new XlsDescription(termDescType, termDescValue);
	List<XlsDescription> descriptions = new ArrayList<>();
	descriptions.add(description);

	xlsTerm.setDescriptions(descriptions);
	List<XlsTerm> terms = new ArrayList<>();
	terms.add(xlsTerm);
	xlsEntry.setTerms(terms);

	return xlsEntry;
    }

    private XlsTermEntry createXlsEntryWithTermNote(String termName, String termDescType, String termDescValue) {

	XlsTermEntry xlsEntry = new XlsTermEntry();
	xlsEntry.setXlsUuId(UUID.randomUUID().toString());

	XlsTerm xlsTerm = createTerm(termName, "en-US", "APPROVED");
	XlsDescription description = new XlsDescription(termDescType, termDescValue, Description.NOTE);
	List<XlsDescription> descriptions = new ArrayList<>();
	descriptions.add(description);

	xlsTerm.setDescriptions(descriptions);
	List<XlsTerm> terms = new ArrayList<>();
	terms.add(xlsTerm);
	xlsEntry.setTerms(terms);

	return xlsEntry;

    }

}
