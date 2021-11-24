package org.gs4tr.termmanager.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.io.FileUtils;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.entities.model.RepositoryItem;
import org.gs4tr.foundation.modules.entities.model.RepositoryTicket;
import org.gs4tr.foundation.modules.entities.model.ResourceInfo;
import org.gs4tr.foundation.modules.entities.model.ResourceType;
import org.gs4tr.termmanager.model.ExportInfo;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.TermEntryResourceTrack;
import org.gs4tr.termmanager.model.UpdateCommand;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.model.search.TermEntrySearchRequest;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.termmanager.service.export.ExportDocumentFactory;
import org.gs4tr.termmanager.service.export.ExportFormatEnum;
import org.gs4tr.termmanager.service.impl.ExportAdapter;
import org.gs4tr.termmanager.service.impl.Messages;
import org.gs4tr.termmanager.service.manualtask.ExportDocumentTaskHandler;
import org.gs4tr.tm3.api.Page;
import org.gs4tr.tm3.api.TmException;
import org.junit.Test;

import junit.framework.Assert;

public class TermEntryTest extends AbstractSolrGlossaryTest {

    private static final Long PROJECT_ID = 1L;

    @Test
    public void addTermentryAttributeTest() {
	String termEntryId = TERM_ENTRY_ID_02;
	TermEntry termEntry = getTermEntryService().findTermEntryById(termEntryId, PROJECT_ID);

	assertNotNull(termEntry);

	UpdateCommand updateCommand = new UpdateCommand();
	updateCommand.setCommand("add");
	updateCommand.setValue("attrib");
	updateCommand.setItemType("description");
	updateCommand.setMarkerId(UUID.randomUUID().toString());
	updateCommand.setParentMarkerId(termEntryId);
	updateCommand.setSubType("description");

	List<UpdateCommand> updateCommands = new ArrayList<UpdateCommand>();
	updateCommands.add(updateCommand);

	String termEntryIdResult = getTermEntryService().updateTermEntry(termEntry, updateCommands);

	TermEntry termEntryResult = getTermEntryService().findTermEntryById(termEntryIdResult, PROJECT_ID);

	Set<Description> termEntryDescriptions = termEntryResult.getDescriptions();

	assertNotNull(termEntryDescriptions);
	boolean flag = false;
	for (Description termEntryDescription : termEntryDescriptions) {
	    if (termEntryDescription.getType().equals("description")
		    && termEntryDescription.getValue().equals("attrib")) {
		flag = true;
	    }
	}

	assertTrue(flag);
    }

    @Test
    public void editTermEntryTest() {
	String termEntryId = TERM_ENTRY_ID_01;
	Long projectId = PROJECT_ID;
	TermEntry termEntry = getTermEntryService().findTermEntryById(termEntryId, projectId);

	assertNotNull(termEntry);

	Term termTerm = termEntry.ggetTermById(TERM_ID_01);
	Set<Description> termDescriptions = termTerm.getDescriptions();
	Assert.assertTrue(CollectionUtils.isNotEmpty(termDescriptions));

	String descriptionId = termDescriptions.iterator().next().getUuid();
	Assert.assertNotNull(descriptionId);

	UpdateCommand command = new UpdateCommand();
	command.setCommand("update");
	command.setMarkerId(descriptionId);
	command.setParentMarkerId("");
	command.setItemType("description");
	command.setSubType("definition");
	command.setValue("The program may contain sample source code or programs, "
		+ "which illustrate programming techniques.");

	List<UpdateCommand> updateCommands = new ArrayList<UpdateCommand>();
	updateCommands.add(command);

	String termEntryIdResult = getTermEntryService().updateTermEntry(termEntry, updateCommands);

	TermEntry termEntryResult = getTermEntryService().findTermEntryById(termEntryIdResult, projectId);

	boolean flag = false;

	List<Term> terms = termEntryResult.ggetTerms();
	List<String> termIds = new ArrayList<String>();
	for (Term term : terms) {
	    termIds.add(term.getUuId());
	}

	List<Term> fetchedTerms = getTermService().findTermsByIds(termIds, Arrays.asList(projectId));
	for (Term term : fetchedTerms) {
	    if (CollectionUtils.isEmpty(term.getDescriptions())) {
		continue;
	    }
	    for (Description description : term.getDescriptions()) {
		if (description.getValue().equals("The program may contain sample source code or programs, "
			+ "which illustrate programming techniques.")) {
		    flag = true;
		}
	    }
	}

	assertNotNull(termEntryResult);
	assertTrue(flag);
    }

    @Test
    public void filterSearchProjectId() throws TmException {
	long projectId = 1L;

	TermEntrySearchRequest request = new TermEntrySearchRequest();
	request.setProjectId(projectId);

	TmgrSearchFilter filter = createFilterFromRequest(request);

	List<TermEntry> allTermEntries = getRegularConnector().getTmgrBrowser().findAll();

	boolean flag = false;
	int size = 0;
	for (TermEntry termEntry : allTermEntries) {
	    if (termEntry.getProjectId().equals(projectId)) {
		flag = true;
		size++;
	    }
	}
	Assert.assertTrue(flag);

	Page<TermEntry> results = getRegularConnector().getTmgrSearcher().concordanceSearch(filter);
	Assert.assertEquals(size, results.getTotalResults());
    }

    @Test
    public void testAddUpdateTermNote() {
	String termEntryId = TERM_ENTRY_ID_01;

	TermEntry termEntry = getTermEntryService().findTermEntryById(termEntryId, PROJECT_ID);

	assertNotNull(termEntry);

	// ADD

	List<UpdateCommand> updateCommands = new ArrayList<UpdateCommand>();

	String value = "Term Note";
	String termId = termEntry.ggetTerms().get(0).getUuId();
	String noteId = UUID.randomUUID().toString();
	UpdateCommand command = new UpdateCommand("add", "note", "definition", noteId, termId, value);
	updateCommands.add(command);

	getTermEntryService().updateTermEntry(termEntry, updateCommands);

	Term term = getTermService().findTermById(termId, PROJECT_ID);

	assertNotNull(term);
	Set<Description> termNotes = findDescriptionByBaseType(term, Description.NOTE);

	if (CollectionUtils.isNotEmpty(termNotes)) {
	    for (Description termDescription : termNotes) {
		assertEquals(value, termDescription.getValue());
	    }
	}

	updateCommands = new ArrayList<UpdateCommand>();

	value = "NEW VALUE";
	command = new UpdateCommand("update", "note", "definition", noteId, termId, value);
	updateCommands.add(command);

	// UPDATE
	termEntry = getTermEntryService().findTermEntryById(termEntryId, PROJECT_ID);

	getTermEntryService().updateTermEntry(termEntry, updateCommands);

	term = getTermService().findTermById(termId, PROJECT_ID);

	assertNotNull(term);
	termNotes = findDescriptionByBaseType(term, Description.NOTE);
	if (CollectionUtils.isNotEmpty(termNotes)) {
	    for (Description termDescription : termNotes) {
		assertEquals(value, termDescription.getValue());
	    }
	}
    }

    @Test
    public void testChangeForbiddStatus() {
	String termId = TERM_ID_01;

	List<String> termIds = new ArrayList<String>();
	termIds.add(termId);

	Term term = getTermService().findTermById(termId, PROJECT_ID);

	getTermService().changeForbiddStatus(termIds, term.getProjectId());

	term = getTermService().findTermById(termId, PROJECT_ID);

	assertTrue(term.isForbidden());
	assertEquals(ItemStatusTypeHolder.BLACKLISTED.getName(), term.getStatus());
    }

    @Test
    public void testDownloadTermEntryResourceTrack() {

	RepositoryItem repositoryItem = null;

	try {
	    repositoryItem = getTermEntryService().downloadResource(1L);
	} catch (Exception e) {
	    if (e instanceof RuntimeException) {
		assertEquals(Messages.getString("TermEntryServiceImpl.3"), e.getMessage());
	    }
	} finally {
	    assertEquals(null, repositoryItem);
	}
    }

    @Test
    public void testExportDocumentCase1() throws Exception {
	Long projectId = 1L;

	TermEntrySearchRequest request = new TermEntrySearchRequest();
	List<String> descriptionTypes = new ArrayList<String>();
	String descriptionType = "definition";
	descriptionTypes.add(descriptionType);
	request.setForbidden(Boolean.FALSE);
	request.setProjectId(projectId);

	String sourceLangugae = "en-US";
	String targetLanguage = "de-DE";

	request.setSourceLocale(sourceLangugae);

	List<String> targetLocales = new ArrayList<String>();
	targetLocales.add(targetLanguage);

	request.setTargetLocales(targetLocales);

	List<String> languagesToExport = new ArrayList<String>();
	languagesToExport.add(sourceLangugae);
	languagesToExport.add(targetLanguage);
	request.setLanguagesToExport(languagesToExport);
	request.setTermAttributes(descriptionTypes);

	String xslTransormationName = "TBX";

	TmgrSearchFilter filter = createFilterFromRequest(request);

	String exportUuid = UUID.randomUUID().toString();
	ExportInfo exportInfo = getTermEntryService().exportDocument(request, filter, xslTransormationName,
		new ExportAdapter(exportUuid, System.currentTimeMillis()));

	assertNotNull(exportInfo);
	assertNotNull(exportInfo.getTotalTermEntriesExported());
	assertTrue(exportInfo.getTotalTermEntriesExported() > 0);
	assertNotNull(exportInfo.getTempFile());

	assertNotNull(request);

	File testFile = new File("test");

	OutputStream output = null;
	try {
	    output = new FileOutputStream(testFile);
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	}

	ExportDocumentFactory writer = ExportDocumentFactory.getInstance(ExportFormatEnum.TBX);

	try {
	    writer.open(output, request, null);
	} catch (IOException e1) {
	    e1.printStackTrace();
	}

	List<TermEntry> termEntries = getRegularConnector().getTmgrBrowser().findAll();

	ExportInfo exportInfoWS = getTermEntryService().exportDocumentWS(termEntries, request, new ExportAdapter(),
		writer);
	assertNotNull(exportInfoWS);

	try {
	    writer.close();
	} catch (IOException e1) {
	    e1.printStackTrace();
	}

	try {
	    FileUtils.forceDelete(testFile);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    @Test
    public void testExportDocumentCase2() throws Exception {
	TermEntrySearchRequest request = new TermEntrySearchRequest();
	request.setForbidden(Boolean.FALSE);
	request.setProjectId(1L);

	String sourceLangugae = "en-US";
	String targetLanguage = "de-DE";

	request.setSourceLocale(sourceLangugae);

	List<String> targetLocales = new ArrayList<String>();
	targetLocales.add(targetLanguage);

	request.setTargetLocales(targetLocales);

	List<String> languagesToExport = new ArrayList<String>();
	languagesToExport.add(sourceLangugae);
	languagesToExport.add(targetLanguage);
	request.setLanguagesToExport(languagesToExport);

	String xslTransormationName = "TBX";

	TmgrSearchFilter filter = createFilterFromRequest(request);

	ExportInfo exportInfo = getTermEntryService().exportDocument(request, filter, xslTransormationName,
		new ExportAdapter());

	assertNotNull(exportInfo);
	assertNotNull(exportInfo.getTotalTermEntriesExported());
	assertTrue(exportInfo.getTotalTermEntriesExported() > 0);
	assertNotNull(exportInfo.getTempFile());

	assertNotNull(request);

	File testFile = new File("test");

	OutputStream output = null;
	try {
	    output = new FileOutputStream(testFile);
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	}

	ExportDocumentFactory writer = ExportDocumentFactory.getInstance(ExportFormatEnum.TBX);

	try {
	    writer.open(output, request, null);
	} catch (IOException e1) {
	    e1.printStackTrace();
	}

	List<TermEntry> termEntries = getRegularConnector().getTmgrBrowser().findAll();

	ExportInfo exportInfoWS = getTermEntryService().exportDocumentWS(termEntries, request, new ExportAdapter(),
		writer);
	assertNotNull(exportInfoWS);

	try {
	    writer.close();
	} catch (IOException e1) {
	    e1.printStackTrace();
	}

	try {
	    FileUtils.forceDelete(testFile);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    @Test
    public void testExportDocumentCase3() throws Exception {
	Long projectId = 1L;

	TermEntrySearchRequest request = new TermEntrySearchRequest();
	List<String> descriptionTypes = new ArrayList<String>();
	String descriptionType = "type";
	descriptionTypes.add(descriptionType);
	request.setForbidden(Boolean.FALSE);
	request.setProjectId(projectId);
	String sourceLanguage = "en-US";
	String targetLanguage = "de-DE";
	request.setSourceLocale(sourceLanguage);
	List<String> targetLocales = new ArrayList<String>();
	targetLocales.add(targetLanguage);
	request.setTargetLocales(targetLocales);
	List<String> languagesToExport = new ArrayList<String>();
	languagesToExport.add(sourceLanguage);
	languagesToExport.add(targetLanguage);
	request.setLanguagesToExport(languagesToExport);
	request.setTermAttributes(descriptionTypes);

	String xslTransormationName = "CSVEXPORT";

	TmgrSearchFilter filter = createFilterFromRequest(request);

	ExportInfo exportInfo = getTermEntryService().exportDocument(request, filter, xslTransormationName,
		new ExportAdapter());

	assertNotNull(exportInfo);
	assertNotNull(exportInfo.getTotalTermEntriesExported());
	assertTrue(exportInfo.getTotalTermEntriesExported() > 0);
	assertNotNull(exportInfo.getTempFile());

	assertNotNull(request);

	File testFile = new File("test");

	OutputStream output = null;
	try {
	    output = new FileOutputStream(testFile);
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	}

	ExportDocumentFactory writer = ExportDocumentFactory.getInstance(ExportFormatEnum.TBX);

	try {
	    writer.open(output, request, null);
	} catch (IOException e1) {
	    e1.printStackTrace();
	}

	List<TermEntry> termEntries = getRegularConnector().getTmgrBrowser().findAll();

	ExportInfo exportInfoWS = getTermEntryService().exportDocumentWS(termEntries, request, new ExportAdapter(),
		writer);
	assertNotNull(exportInfoWS);

	try {
	    writer.close();
	} catch (IOException e1) {
	    e1.printStackTrace();
	}

	try {
	    FileUtils.forceDelete(testFile);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    @Test
    public void testFindTermentryById() {
	String termEntryId = TERM_ENTRY_ID_01;
	TermEntry termEntry = getTermEntryService().findTermEntryById(termEntryId, PROJECT_ID);
	assertNotNull(termEntry);
	assertNotNull(termEntry.getDescriptions());
    }

    @Test
    public void testRemoveDescription() {
	String termEntryId = TERM_ENTRY_ID_01;

	TermEntry termEntry = getTermEntryService().findTermEntryById(termEntryId, PROJECT_ID);

	assertNotNull(termEntry);

	Term term = termEntry.ggetTermById(TERM_ID_01);

	Set<Description> termDescriptions = term.getDescriptions();
	assertTrue(CollectionUtils.isNotEmpty(termDescriptions));
	Description description = termDescriptions.iterator().next();

	List<UpdateCommand> updateCommands = new ArrayList<UpdateCommand>();
	String descriptionMarkerId = description.getUuid();
	UpdateCommand command = new UpdateCommand("remove", "description", "definition", descriptionMarkerId,
		term.getUuId(), "deleted");
	updateCommands.add(command);

	getTermEntryService().updateTermEntry(termEntry, updateCommands);

	termEntry = getTermEntryService().findTermEntryById(termEntryId, PROJECT_ID);
	term = termEntry.ggetTermById(TERM_ID_01);

	termDescriptions = term.getDescriptions();
	assertTrue(CollectionUtils.isEmpty(termDescriptions));
    }

    /*
     * TERII-5692 Server - Deleting the attribute deletes main term
     */
    @Test
    public void testRemoveDescriptionMerge() {
	String termEntryId = TERM_ENTRY_ID_01;

	String mergeTermUuId = "c502608e-uuid-term-010";

	TermEntry termEntry = getTermEntryService().findTermEntryById(termEntryId, PROJECT_ID);

	assertNotNull(termEntry);

	Term term = termEntry.ggetTermById(TERM_ID_01);

	Term mergeTermBefore = term.cloneTerm();
	mergeTermBefore.setUuId(mergeTermUuId);
	mergeTermBefore.setDescriptions(new HashSet<>());

	termEntry.addTerm(mergeTermBefore);

	/* Save merge term */
	getTermEntryService().updateRegularTermEntries(PROJECT_ID, Collections.singletonList(termEntry));
	TermEntry termEntryWithMergeTermBefore = getTermEntryService().findTermEntryById(termEntryId, PROJECT_ID);
	mergeTermBefore = termEntryWithMergeTermBefore.ggetTermById(mergeTermUuId);

	/* Test if merge term is saved */
	assertNotNull(mergeTermBefore);

	/* Term entry to compare with after merge action */
	TermEntry termEntryBefore = getTermEntryService().findTermEntryById(termEntryId, PROJECT_ID);

	Set<Description> termDescriptions = term.getDescriptions();
	assertTrue(CollectionUtils.isNotEmpty(termDescriptions));
	Description description = termDescriptions.iterator().next();

	List<UpdateCommand> updateCommands = new ArrayList<UpdateCommand>();
	String descriptionMarkerId = description.getUuid();
	UpdateCommand command = new UpdateCommand("remove", "description", "definition", descriptionMarkerId,
		term.getUuId(), "deleted");
	updateCommands.add(command);

	getTermEntryService().updateTermEntry(termEntry, updateCommands);

	TermEntry termEntryAfter = getTermEntryService().findTermEntryById(termEntryId, PROJECT_ID);
	Term mergeTermAfter = termEntryAfter.ggetTermById(mergeTermUuId);

	/* Term should be null after merge action */
	assertNull(mergeTermAfter);

	Term termAfter = termEntryAfter.ggetTermById(TERM_ID_01);

	/* Term should be unchanged */
	assertNotNull(termAfter);

	/* Only one term should be removed (merge action) */
	int numOfTermsBefore = termEntryBefore.ggetAllTerms().size();
	int numOfTermsAfter = termEntryAfter.ggetAllTerms().size();
	assertTrue(numOfTermsBefore - 1 == numOfTermsAfter);

	Term termBefore = termEntryBefore.ggetTermById(TERM_ID_01);

	termBefore.setDescriptions(null);
	assertTrue(termBefore.equals(termAfter));

	assertTrue(termBefore.getDateModified() < termAfter.getDateModified());
    }

    /*
     * TERII-5692 Server - Deleting the attribute deletes main term
     */
    @Test
    public void testRemoveDescriptionMergeUpdateStatus() {
	String termEntryId = TERM_ENTRY_ID_01;

	String approvedStatus = ItemStatusTypeHolder.PROCESSED.getName();

	String mergeTermUuId = "c502608e-uuid-term-010";

	TermEntry termEntry = getTermEntryService().findTermEntryById(termEntryId, PROJECT_ID);

	assertNotNull(termEntry);

	Term term = termEntry.ggetTermById(TERM_ID_01);

	/* Set status */
	term.setStatus(ItemStatusTypeHolder.ON_HOLD.getName());

	Term mergeTermBefore = term.cloneTerm();
	mergeTermBefore.setUuId(mergeTermUuId);
	mergeTermBefore.setDescriptions(new HashSet<>());

	/* Merged term has blacklisted status */
	mergeTermBefore.setStatus(approvedStatus);

	termEntry.addTerm(mergeTermBefore);

	/* Save merge term */
	getTermEntryService().updateRegularTermEntries(PROJECT_ID, Collections.singletonList(termEntry));
	TermEntry termEntryWithMergeTermBefore = getTermEntryService().findTermEntryById(termEntryId, PROJECT_ID);
	mergeTermBefore = termEntryWithMergeTermBefore.ggetTermById(mergeTermUuId);

	/* Test if merge term is saved */
	assertNotNull(mergeTermBefore);

	Set<Description> termDescriptions = term.getDescriptions();
	assertTrue(CollectionUtils.isNotEmpty(termDescriptions));
	Description description = termDescriptions.iterator().next();

	List<UpdateCommand> updateCommands = new ArrayList<UpdateCommand>();
	String descriptionMarkerId = description.getUuid();
	UpdateCommand command = new UpdateCommand("remove", "description", "definition", descriptionMarkerId,
		term.getUuId(), "deleted");
	updateCommands.add(command);

	getTermEntryService().updateTermEntry(termEntry, updateCommands);

	TermEntry termEntryAfter = getTermEntryService().findTermEntryById(termEntryId, PROJECT_ID);
	Term mergeTermAfter = termEntryAfter.ggetTermById(mergeTermUuId);

	/* Term should be null after merge action */
	assertNull(mergeTermAfter);

	Term termAfter = termEntryAfter.ggetTermById(TERM_ID_01);

	/* Term should be unchanged */
	assertNotNull(termAfter);

	assertTrue(termAfter.getStatus().equals(approvedStatus));
    }

    @Test
    public void testRemoveTermNote() {
	String termEntryId = TERM_ENTRY_ID_01;

	TermEntry termEntry = getTermEntryService().findTermEntryById(termEntryId, PROJECT_ID);

	assertNotNull(termEntry);
	String value = "Term Note";

	Term term = termEntry.ggetTermById(TERM_ID_01);
	assertNotNull(term);
	Set<Description> termNotes = findDescriptionByBaseType(term, Description.NOTE);

	assertTrue(CollectionUtils.isEmpty(termNotes));

	List<UpdateCommand> updateCommands = new ArrayList<UpdateCommand>();
	UpdateCommand command = new UpdateCommand("add", "note", "definition", UUID.randomUUID().toString(), TERM_ID_01,
		value);
	updateCommands.add(command);

	getTermEntryService().updateTermEntry(termEntry, updateCommands);

	termEntry = getTermEntryService().findTermEntryById(termEntryId, PROJECT_ID);

	List<Term> terms = termEntry.ggetTerms();
	assertTrue(CollectionUtils.isNotEmpty(terms));

	term = termEntry.ggetTermById(TERM_ID_01);

	assertNotNull(term);
	termNotes = findDescriptionByBaseType(term, Description.NOTE);
	boolean flag = false;

	String descriptionId = null;

	if (CollectionUtils.isNotEmpty(termNotes)) {
	    for (Description termDescription : termNotes) {
		if (value.equals(termDescription.getValue())) {
		    descriptionId = termDescription.getUuid();
		    flag = true;
		}
	    }
	}

	assertNotNull(descriptionId);
	assertTrue(flag);
	assertEquals(1, term.getDescriptions().size());

	// REMOVE
	updateCommands = new ArrayList<UpdateCommand>();
	command = new UpdateCommand("remove", "note", "definition", descriptionId, null, value);
	updateCommands.add(command);

	getTermEntryService().updateTermEntry(termEntry, updateCommands);

	termEntry = getTermEntryService().findTermEntryById(termEntryId, PROJECT_ID);
	assertTrue(CollectionUtils.isNotEmpty(terms));

	term = termEntry.ggetTermById(TERM_ID_01);

	assertNotNull(term);
	termNotes = term.getDescriptions();

	CollectionUtils.filter(termNotes, new Predicate() {

	    @Override
	    public boolean evaluate(Object object) {
		Description desc = (Description) object;
		boolean isNote = desc.getBaseType().equals(Description.NOTE) ? true : false;
		return isNote;
	    }
	});

	if (CollectionUtils.isNotEmpty(termNotes)) {
	    for (Description termNote : termNotes) {
		if (termNote.getUuid().equals(descriptionId)) {
		    flag = false;
		}
	    }
	}
	assertTrue(flag);
	assertNull(term.getDescriptions());
    }

    @Test
    public void testTermEntryResourceTracks() throws IOException {
	String termEntryId = TERM_ENTRY_ID_01;
	TermEntry termEntry = getTermEntryService().findTermEntryById(termEntryId, PROJECT_ID);
	assertNotNull(termEntry);

	InputStream steam = new FileInputStream(new File("src/test/resources/testfiles/test.doc"));

	ResourceInfo resourceInfo = new ResourceInfo();
	resourceInfo.setClassifier("TEXT");
	resourceInfo.setMimeType("application/octet-stream");
	resourceInfo.setName("test.doc");
	resourceInfo.setType(ResourceType.SOURCE);
	resourceInfo.setResourceInfoId(1L);
	resourceInfo.setSize(new Long(steam.available()));

	String attributeType = "TEXT";

	RepositoryTicket repositoryTicket = getTermEntryService().uploadBinaryResource(termEntryId, resourceInfo, steam,
		attributeType);
	assertNotNull(repositoryTicket);

	TermEntry newTermEntry = getTermEntryService().findTermEntryById(termEntryId, PROJECT_ID);
	assertNotNull(newTermEntry);

	List<String> resourceIds = new ArrayList<String>();

	List<TermEntryResourceTrack> resourceTracks = getTermEntryService()
		.findResourceTracksByTermEntryById(TERM_ENTRY_ID_01);

	Assert.assertTrue(CollectionUtils.isNotEmpty(resourceTracks));
	TermEntryResourceTrack resourceTrack = resourceTracks.get(0);

	String resourceId = resourceTrack.getResourceId();
	resourceIds.add(resourceId);

	assertTrue(CollectionUtils.isNotEmpty(resourceIds));

	RepositoryItem repositoryItem = getTermEntryService().downloadResource(resourceTrack.getResourceTrackId());

	assertNotNull(repositoryItem);
	assertNotNull(repositoryItem.getInputStream());

	getTermEntryService().deleteTermEntryResourceTracks(newTermEntry.getUuId(), resourceIds, PROJECT_ID);

	resourceTracks = getTermEntryService().findResourceTracksByTermEntryById(termEntryId);

	assertTrue(CollectionUtils.isEmpty(resourceTracks));
    }

    @Test
    public void testUpdateRemoveTerm() {
	String termEntryId = TERM_ENTRY_ID_01;

	TermEntry termEntry = getTermEntryService().findTermEntryById(termEntryId, PROJECT_ID);

	assertNotNull(termEntry);

	String newValue = "NEW VALUE";

	// UPDATE
	List<UpdateCommand> updateCommands = new ArrayList<UpdateCommand>();
	UpdateCommand command = new UpdateCommand("update", "term", "definition", TERM_ID_01, termEntry.getUuId(),
		newValue);
	command.setLanguageId("en-US");
	command.setStatus(ItemStatusTypeHolder.PROCESSED.getName());
	updateCommands.add(command);

	getTermEntryService().updateTermEntry(termEntry, updateCommands);
	boolean flag = false;
	termEntry = getTermEntryService().findTermEntryById(termEntryId, PROJECT_ID);

	List<Term> terms = termEntry.ggetTerms();
	for (Term term : terms) {
	    if (term.getName().equals(newValue)) {
		flag = true;
	    }
	}
	// FIXME test update remove term
	assertTrue(flag);
    }

    private TmgrSearchFilter createFilterFromRequest(TermEntrySearchRequest searchRequest) {
	return new ExportDocumentTaskHandler().createFilterFromRequest(searchRequest, new PagedListInfo());
    }

    private Set<Description> findDescriptionByBaseType(Term term, final String type) {
	Set<Description> termNotes = term.getDescriptions();

	CollectionUtils.filter(termNotes, new Predicate() {
	    @Override
	    public boolean evaluate(Object item) {
		Description description = (Description) item;
		if (description.getBaseType().equals(type)) {
		    return true;
		}
		return false;
	    }
	});
	return termNotes;
    }

}