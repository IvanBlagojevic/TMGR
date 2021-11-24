package org.gs4tr.termmanager.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.termmanager.cache.CacheGateway;
import org.gs4tr.termmanager.cache.model.CacheName;
import org.gs4tr.termmanager.model.Attribute;
import org.gs4tr.termmanager.model.AttributeLevelEnum;
import org.gs4tr.termmanager.model.BaseTypeEnum;
import org.gs4tr.termmanager.model.ImportErrorAction;
import org.gs4tr.termmanager.model.ImportSummary;
import org.gs4tr.termmanager.model.ImportTypeEnum;
import org.gs4tr.termmanager.model.InputFieldTypeEnum;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.ProjectLanguage;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.persistence.update.DescriptionImportOption;
import org.gs4tr.termmanager.persistence.update.ImportOptionsModel;
import org.gs4tr.termmanager.service.impl.ImportProgressInfo;
import org.gs4tr.termmanager.service.termentry.synchronization.SyncOption;
import org.gs4tr.termmanager.service.utils.ServiceUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class ImportTermServiceTest extends AbstractSolrGlossaryTest {

    @Autowired
    @Qualifier("guavaCacheGateway")
    private CacheGateway _cacheGateway;

    @Test
    public void importTbxDocumentWithAttributes() throws Exception {

	Long projectId = 1L;
	String syncLang = "en-US";

	TmProject project = getProjectService().findProjectById(projectId, TmProject.class);

	File file = new File("src/test/resources/testfiles/skype.tbx");
	InputStream tbxStream = new FileInputStream(file);

	ImportOptionsModel importOptions = new ImportOptionsModel();
	importOptions.setProjectId(projectId);
	importOptions.setProjectName(project.getProjectInfo().getName());
	importOptions.setProjectShortCode(project.getProjectInfo().getShortCode());
	importOptions.setDescriptionImportOption(DescriptionImportOption.ADD_ALL);
	importOptions.setImportErrorAction(ImportErrorAction.SKIP);
	importOptions.setStatus(ItemStatusTypeHolder.PROCESSED.getName());
	importOptions.setSyncLanguageId(syncLang);

	ImportProgressInfo importProgressInfo = new ImportProgressInfo(3);
	when(getCacheGateway().get(any(CacheName.class), any(String.class))).thenReturn(importProgressInfo);

	ImportSummary importSummary = getImportTermService().importDocumentWS(importOptions, tbxStream,
		System.currentTimeMillis(), ImportTypeEnum.TBX, SyncOption.MERGE);

	assertNotNull(importSummary);
	assertNotNull(importSummary.getNoImportedTermEntries());
	assertTrue(CollectionUtils.isNotEmpty(importSummary.getImportedTermEntryAttributes()));
	assertTrue(CollectionUtils.isNotEmpty(importSummary.getImportedTermDescriptions()));

    }

    @Test
    public void testImportTbxDocument() throws Exception {
	Long projectId = 1L;

	String sourceLanguage = "en-US";
	File file = new File("src/test/resources/testfiles/medtronic.tbx");
	String fileEncoding = ServiceUtils.getFileEncoding(file);
	InputStream tbxStream = new FileInputStream(file);
	ImportProgressInfo importProgressInfo = new ImportProgressInfo(3);
	long startTime = System.currentTimeMillis();
	importProgressInfo.getImportSummary().setStartTime(startTime);
	importProgressInfo.getImportSummary().setImportId("medtronic.tbx");

	TmUserProfile userProfile = TmUserProfile.getCurrentUserProfile();

	final List<String> projectUserLanguages = getProjectService().getProjectUserLanguageCodes(projectId,
		userProfile.getUserProfileId());

	assertNotNull(projectUserLanguages);
	assertTrue(CollectionUtils.isNotEmpty(projectUserLanguages));

	List<ProjectLanguage> newLanguages = new ArrayList<>();

	List<String> importLocales = new ArrayList<>();

	List<ProjectLanguage> projectLanguages = getProjectService().getProjectLanguagesByProjectId(projectId);

	assertNotNull(projectLanguages);
	assertTrue(CollectionUtils.isNotEmpty(projectLanguages));

	for (ProjectLanguage proLang : projectLanguages) {
	    if (!projectUserLanguages.contains(proLang.getLanguage())) {
		newLanguages.add(proLang);
	    }

	    importLocales.add(proLang.getLanguage());
	}

	if (newLanguages.size() > 0) {
	    getProjectService().addNewLanguagesOnImport(projectId, newLanguages);
	}

	ImportOptionsModel importOptions = new ImportOptionsModel();
	importOptions.setProjectId(projectId);
	importOptions.setImportLocales(importLocales);
	importOptions.setSyncLanguageId(sourceLanguage);
	importOptions.setImportErrorAction(ImportErrorAction.SKIP);
	importOptions.setStatus(ItemStatusTypeHolder.PROCESSED.getName());
	importOptions.setDescriptionImportOption(DescriptionImportOption.ADD_ALL);

	when(getCacheGateway().get(any(CacheName.class), any(String.class))).thenReturn(importProgressInfo);

	ImportSummary importSummary = getImportTermService().importDocument(tbxStream, importProgressInfo,
		importOptions, ImportTypeEnum.TBX, fileEncoding, SyncOption.MERGE);

	Set<String> importedTermEntryIds = importSummary.getImportedTermEntryIds();

	String firstImported = importedTermEntryIds.iterator().next();
	TermEntry termEntry = getTermEntryService().findTermEntryById(firstImported, projectId);
	assertNotNull(termEntry);

	List<Term> terms = termEntry.ggetTerms();
	assertNotNull(CollectionUtils.isNotEmpty(terms));

	for (Term term : terms) {
	    assertTrue(term.getStatus() != null);
	}

	assertNotNull(importSummary);
	assertNotNull(importSummary.getTotalTimeForImport());
	assertTrue(importSummary.getNoImportedTerms().intValue() > 0);

	File newFile = new File("src/test/resources/testfiles/medtronic.tbx");
	String newFileEncoding = ServiceUtils.getFileEncoding(newFile);
	InputStream newTbxStream = new FileInputStream(newFile);

	ImportSummary newImportSummary = getImportTermService().importDocument(newTbxStream, importProgressInfo,
		importOptions, ImportTypeEnum.TBX, newFileEncoding, SyncOption.MERGE);

	assertNotNull(newImportSummary);
	assertNotNull(newImportSummary.getTotalTimeForImport());
	assertTrue(newImportSummary.getNoImportedTerms().intValue() > 0);
    }

    @Test
    public void testImportTbxDocumentWS() throws Exception {
	Long projectId = new Long(1);

	String sourceLanguage = "en-US";
	File file = new File("src/test/resources/testfiles/medtronic.tbx");
	InputStream tbxStream = new FileInputStream(file);

	TmUserProfile userProfile = TmUserProfile.getCurrentUserProfile();

	final List<String> projectUserLanguages = getProjectService().getProjectUserLanguageCodes(projectId,
		userProfile.getUserProfileId());

	assertNotNull(projectUserLanguages);
	assertTrue(CollectionUtils.isNotEmpty(projectUserLanguages));

	List<ProjectLanguage> newLanguages = new ArrayList<>();

	List<String> importLocales = new ArrayList<>();

	List<ProjectLanguage> projectLanguages = getProjectService().getProjectLanguagesByProjectId(projectId);

	assertNotNull(projectLanguages);
	assertTrue(CollectionUtils.isNotEmpty(projectLanguages));

	for (ProjectLanguage proLang : projectLanguages) {
	    if (!projectUserLanguages.contains(proLang.getLanguage())) {
		newLanguages.add(proLang);
	    }

	    importLocales.add(proLang.getLanguage());
	}

	if (newLanguages.size() > 0) {
	    getProjectService().addNewLanguagesOnImport(projectId, newLanguages);
	}

	Long startTime = System.currentTimeMillis();

	ImportOptionsModel importOptions = new ImportOptionsModel();
	importOptions.setProjectId(projectId);
	importOptions.setProjectName("Test");
	importOptions.setProjectShortCode("TES000001");
	importOptions.setDescriptionImportOption(DescriptionImportOption.ADD_ALL);
	importOptions.setImportErrorAction(ImportErrorAction.SKIP);
	importOptions.setStatus(ItemStatusTypeHolder.PROCESSED.getName());
	importOptions.setSyncLanguageId(sourceLanguage);

	ImportProgressInfo importProgressInfo = new ImportProgressInfo(3);
	when(getCacheGateway().get(any(CacheName.class), any(String.class))).thenReturn(importProgressInfo);

	ImportSummary importSummary = getImportTermService().importDocumentWS(importOptions, tbxStream, startTime,
		ImportTypeEnum.TBX, SyncOption.MERGE);
	assertNotNull(importSummary);
	assertNotNull(importSummary.getTotalTimeForImport());
	assertTrue(importSummary.getNoImportedTerms().intValue() > 0);
    }

    /* TERII-4791: Wrong combo values should not be imported */
    @Test
    public void testImportXlsDocumentBadAtributeValue() throws Exception {
	Long projectId = new Long(1);

	String termId = TERM_ID_02;

	List<Attribute> attributeList = createComboAttribute("111|222|333");
	getProjectService().addOrUpdateProjectAttributes(projectId, attributeList);

	String sourceLanguage = "de-DE";
	File file = new File("src/test/resources/testfiles/termEntryAttribute.xlsx");
	String fileEncoding = ServiceUtils.getFileEncoding(file);
	InputStream tbxStream = new FileInputStream(file);
	ImportProgressInfo importProgressInfo = new ImportProgressInfo(3);
	long startTime = System.currentTimeMillis();
	importProgressInfo.getImportSummary().setStartTime(startTime);

	TmUserProfile userProfile = TmUserProfile.getCurrentUserProfile();

	final List<String> projectUserLanguages = getProjectService().getProjectUserLanguageCodes(projectId,
		userProfile.getUserProfileId());

	assertNotNull(projectUserLanguages);
	assertTrue(CollectionUtils.isNotEmpty(projectUserLanguages));

	List<ProjectLanguage> newLanguages = new ArrayList<>();

	List<String> importLocales = new ArrayList<>();

	List<ProjectLanguage> projectLanguages = getProjectService().getProjectLanguagesByProjectId(projectId);

	assertNotNull(projectLanguages);
	assertTrue(CollectionUtils.isNotEmpty(projectLanguages));

	for (ProjectLanguage proLang : projectLanguages) {
	    if (!projectUserLanguages.contains(proLang.getLanguage())) {
		newLanguages.add(proLang);
	    }

	    importLocales.add(proLang.getLanguage());
	}

	if (newLanguages.size() > 0) {
	    getProjectService().addNewLanguagesOnImport(projectId, newLanguages);
	}

	ImportOptionsModel importOptions = new ImportOptionsModel();
	importOptions.setProjectId(projectId);
	importOptions.setImportLocales(importLocales);
	importOptions.setSyncLanguageId(sourceLanguage);
	importOptions.setImportErrorAction(ImportErrorAction.SKIP);
	importOptions.setStatus(ItemStatusTypeHolder.PROCESSED.getName());
	importOptions.setDescriptionImportOption(DescriptionImportOption.ADD_ALL);

	when(getCacheGateway().get(any(CacheName.class), any(String.class))).thenReturn(importProgressInfo);

	getImportTermService().importDocument(tbxStream, importProgressInfo, importOptions, ImportTypeEnum.XLS,
		fileEncoding, SyncOption.MERGE);

	Term term = getTermService().findTermById(termId, projectId);

	assertNull(term.getDescriptions());
    }

    /* TERII-4791: Wrong combo values should not be imported */
    @Test
    public void testImportXlsDocumentCorrectAtributeValue() throws Exception {
	Long projectId = new Long(1);

	String termId = TERM_ID_02;

	List<Attribute> attributeList = createComboAttribute("111|222|333|unsuportedAttribute");
	getProjectService().addOrUpdateProjectAttributes(projectId, attributeList);

	String sourceLanguage = "de-DE";
	File file = new File("src/test/resources/testfiles/termEntryAttribute.xlsx");
	String fileEncoding = ServiceUtils.getFileEncoding(file);
	InputStream tbxStream = new FileInputStream(file);
	ImportProgressInfo importProgressInfo = new ImportProgressInfo(3);
	long startTime = System.currentTimeMillis();
	importProgressInfo.getImportSummary().setStartTime(startTime);

	TmUserProfile userProfile = TmUserProfile.getCurrentUserProfile();

	final List<String> projectUserLanguages = getProjectService().getProjectUserLanguageCodes(projectId,
		userProfile.getUserProfileId());

	assertNotNull(projectUserLanguages);
	assertTrue(CollectionUtils.isNotEmpty(projectUserLanguages));

	List<ProjectLanguage> newLanguages = new ArrayList<>();

	List<String> importLocales = new ArrayList<>();

	List<ProjectLanguage> projectLanguages = getProjectService().getProjectLanguagesByProjectId(projectId);

	assertNotNull(projectLanguages);
	assertTrue(CollectionUtils.isNotEmpty(projectLanguages));

	for (ProjectLanguage proLang : projectLanguages) {
	    if (!projectUserLanguages.contains(proLang.getLanguage())) {
		newLanguages.add(proLang);
	    }

	    importLocales.add(proLang.getLanguage());
	}

	if (newLanguages.size() > 0) {
	    getProjectService().addNewLanguagesOnImport(projectId, newLanguages);
	}

	ImportOptionsModel importOptions = new ImportOptionsModel();
	importOptions.setProjectId(projectId);
	importOptions.setImportLocales(importLocales);
	importOptions.setSyncLanguageId(sourceLanguage);
	importOptions.setImportErrorAction(ImportErrorAction.SKIP);
	importOptions.setStatus(ItemStatusTypeHolder.PROCESSED.getName());
	importOptions.setDescriptionImportOption(DescriptionImportOption.ADD_ALL);

	String attributeName = attributeList.get(0).getName();
	Set<String> attributeNames = new HashSet<>();
	attributeNames.add(attributeName);

	Map<String, Set<String>> allowedTermDescriptions = new HashMap<>(2);
	allowedTermDescriptions.put(Description.ATTRIBUTE, attributeNames);
	allowedTermDescriptions.put(Description.NOTE, Collections.emptySet());
	importOptions.setAllowedTermDescriptions(allowedTermDescriptions);

	when(getCacheGateway().get(any(CacheName.class), any(String.class))).thenReturn(importProgressInfo);

	getImportTermService().importDocument(tbxStream, importProgressInfo, importOptions, ImportTypeEnum.XLS,
		fileEncoding, SyncOption.MERGE);

	Term term = getTermService().findTermById(termId, projectId);

	assertNotNull(term);

	Set<Description> descriptions = term.getDescriptions();

	assertNotNull(descriptions);

	assertEquals(1, descriptions.size());

	for (Description description : descriptions) {
	    assertEquals("ATTRIBUTE", description.getBaseType());
	    assertEquals("Custom1", description.getType());
	    assertEquals("unsuportedAttribute", description.getValue());
	}

    }

    private List<Attribute> createComboAttribute(String comboValues) {
	List<Attribute> attributeList = new ArrayList<>();
	Attribute attribute = new Attribute();
	attribute.setName("Custom1");
	attribute.setAttributeLevel(AttributeLevelEnum.LANGUAGE);
	attribute.setBaseTypeEnum(BaseTypeEnum.DESCRIPTION);
	attribute.setInputFieldTypeEnum(InputFieldTypeEnum.COMBO);
	attribute.setComboValues(comboValues);

	attributeList.add(attribute);
	return attributeList;
    }

    private CacheGateway getCacheGateway() {
	return _cacheGateway;
    }
}
