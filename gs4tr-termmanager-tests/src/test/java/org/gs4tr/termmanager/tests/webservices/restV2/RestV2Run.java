package org.gs4tr.termmanager.tests.webservices.restV2;

import static org.gs4tr.termmanager.tests.webservices.restV2.RestV2Client.getProjectMetadata;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.gs4tr.termmanager.model.dto.Description;
import org.gs4tr.termmanager.model.dto.TermV2ModelExtended;
import org.gs4tr.termmanager.service.export.ExportFormatEnum;
import org.gs4tr.termmanager.tests.webservices.ConnectionDetails;
import org.gs4tr.termmanager.webservice.model.request.AddTermsCommand;
import org.gs4tr.termmanager.webservice.model.request.BatchSegmentTermSearchCommand;
import org.gs4tr.termmanager.webservice.model.request.ConcordanceSearchCommand;
import org.gs4tr.termmanager.webservice.model.request.DetailedGlossaryExportCommand;
import org.gs4tr.termmanager.webservice.model.request.GetProjectMetadataCommand;
import org.gs4tr.termmanager.webservice.model.request.SegmentTermSearchCommand;
import org.gs4tr.termmanager.webservice.model.request.WsDateFilter;
import org.gs4tr.termmanager.webservice.model.request.WsPageInfo;

public class RestV2Run {

    public static void main(String[] args) {

	ConnectionDetails connectionDetails = new ConnectionDetails(
		"tmgr://localhost:8080/TMGR?prj=TES000001&usr=power&pwd=password1!&src=en-US&tgt=de-DE");

	Collection<TermV2ModelExtended> terms = new ArrayList<>();
	TermV2ModelExtended t1 = createTerm("frenchTerm", "inTranslationReview", "en-US", false, false);
	addTermDescription(t1, "NOTE", "geographicalUsage", "global");
	addTermDescription(t1, "ATTRIBUTE", "context", "context attribute text");
	terms.add(t1);

	Collection<Description> termEntryDescription = new ArrayList<>();
	Description description = createTermEntryDescription("NOTE", "description", "description");
	termEntryDescription.add(description);

	List<String> targetLocales = new ArrayList<>();
	targetLocales.add("fr-FR");
	targetLocales.add("de-DE");

	DetailedGlossaryExportCommand exportCommand = createDetailedExportRequest(ExportFormatEnum.TBX, false, "en-US",
		targetLocales);

	AddTermsCommand addTermsCommand = createAddTermCommand(terms, termEntryDescription);

	File file = new File("gs4tr-termmanager-tests/target/testFile.tbx");

	List<String> batchSegments = new ArrayList<>();
	batchSegments.add("SMS");
	batchSegments.add("History");

	SegmentTermSearchCommand segmentSearchCommand = createSegmentSearchCommand("SMS hist", null, "en-US",
		targetLocales, true, true, 100);

	BatchSegmentTermSearchCommand batchSegmentTermSearchCommand = createBatchSegmentSearchCommand(batchSegments,
		"en-US", targetLocales, true, false, 100);

	WsDateFilter modificationDate = setModificationDate("28/12/2017", "29/12/2017");
	WsDateFilter creationDate = setCreationDate("10/11/2017", "29/12/2017");

	ConcordanceSearchCommand termEntrySearchCommand = createTermEntrySearchCommand("Skype", "en-US", targetLocales,
		true, false, creationDate, null, true, "creationDate");

	GetProjectMetadataCommand command = createProjectMetadataCommand(null, null, null, null, null);

	String response = getProjectMetadata(command, connectionDetails);
	System.out.println(response);

    }

    private static void addTermDescription(TermV2ModelExtended term, String baseType, String type, String value) {

	Description description = new Description();
	description.setBaseType(baseType);
	description.setType(type);
	description.setValue(value);

	if (term.getDescriptions() != null) {
	    term.getDescriptions().add(description);
	} else {
	    Collection<Description> descriptions = new ArrayList<>();
	    descriptions.add(description);
	    term.setDescriptions(descriptions);
	}
    }

    private static AddTermsCommand createAddTermCommand(Collection<TermV2ModelExtended> terms,
	    Collection<Description> termEntryDescription) {

	AddTermsCommand command = new AddTermsCommand();

	command.setTerms(terms);
	command.setTermEntryDescriptions(termEntryDescription);

	return command;

    }

    private static BatchSegmentTermSearchCommand createBatchSegmentSearchCommand(List<String> batchSegment,
	    String sourceLanguage, List<String> targetLanguages, boolean searchForbidden, boolean fuzzy,
	    int maxNumFound) {

	BatchSegmentTermSearchCommand command = new BatchSegmentTermSearchCommand();
	command.setBatchSegment(batchSegment);
	command.setSourceLanguage(sourceLanguage);
	command.setTargetLanguages(targetLanguages);
	command.setSearchForbidden(searchForbidden);
	command.setFuzzy(fuzzy);
	command.setMaxNumFound(maxNumFound);

	return command;

    }

    private static DetailedGlossaryExportCommand createDetailedExportRequest(ExportFormatEnum fileType,
	    boolean forbidden, String sourceLocale, List<String> targetLocales) {

	DetailedGlossaryExportCommand command = new DetailedGlossaryExportCommand();
	command.setFileType(fileType);
	command.setForbidden(forbidden);
	command.setSourceLocale(sourceLocale);
	command.setTargetLocales(targetLocales);

	return command;
    }

    private static GetProjectMetadataCommand createProjectMetadataCommand(List<String> languages,
	    String organizationName, String projectName, String projectShortcode, String username) {
	GetProjectMetadataCommand command = new GetProjectMetadataCommand();
	command.setLanguages(languages);
	command.setOrganizationName(organizationName);
	command.setProjectName(projectName);
	command.setProjectShortcode(projectShortcode);
	command.setUsername(username);
	return command;

    }

    private static SegmentTermSearchCommand createSegmentSearchCommand(String segment, List<String> batchSegment,
	    String sourceLanguage, List<String> targetLanguages, boolean searchForbidden, boolean fuzzy,
	    int maxNumFound) {

	SegmentTermSearchCommand command = new SegmentTermSearchCommand();
	command.setSegment(segment);
	command.setSourceLanguage(sourceLanguage);
	command.setTargetLanguages(targetLanguages);
	command.setSearchForbidden(searchForbidden);
	command.setFuzzy(fuzzy);
	command.setMaxNumFound(maxNumFound);

	return command;

    }

    private static TermV2ModelExtended createTerm(String termText, String status, String locale, boolean forbidden,
	    boolean fuzzy) {

	TermV2ModelExtended term = new TermV2ModelExtended();
	term.setTermText(termText);
	term.setStatus(status);
	term.setLocale(locale);
	term.setForbidden(forbidden);
	term.setFuzzy(fuzzy);

	return term;
    }

    private static Description createTermEntryDescription(String baseType, String type, String value) {
	Description description = new Description();
	description.setValue(value);
	description.setBaseType(baseType);
	description.setType(type);

	return description;
    }

    private static ConcordanceSearchCommand createTermEntrySearchCommand(String term, String sourceLocale,
	    List<String> targetLocales, boolean caseSensitive, boolean searchForbidden, WsDateFilter creationDate,
	    WsDateFilter modificationDate, boolean matchWholeWords, String sortProperty) {

	ConcordanceSearchCommand command = new ConcordanceSearchCommand();
	command.setTerm(term);
	command.setSourceLocale(sourceLocale);
	command.setTargetLocales(targetLocales);
	command.setCaseSensitive(caseSensitive);
	command.setCreationDateFilter(creationDate);
	command.setModificationDateFilter(modificationDate);
	command.setMatchWholeWords(matchWholeWords);
	command.setSearchForbidden(searchForbidden);

	WsPageInfo info = new WsPageInfo();
	info.setSortProperty(sortProperty);
	command.setPageInfo(info);

	return command;
    }

    private static Long parseDate(String date) {

	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	long milliseconds = 0;

	try {
	    Date d = dateFormat.parse(date);
	    milliseconds = d.getTime();

	} catch (ParseException e) {
	    e.printStackTrace();
	}

	return milliseconds;

    }

    private static WsDateFilter setCreationDate(String dateStart, String dateEnd) {
	WsDateFilter creationDate = new WsDateFilter();
	creationDate.setDateStart(parseDate(dateStart));
	creationDate.setDateEnd(parseDate(dateEnd));
	return creationDate;

    }

    private static WsDateFilter setModificationDate(String dateStart, String dateEnd) {
	WsDateFilter modificationDate = new WsDateFilter();
	modificationDate.setDateStart(parseDate(dateStart));
	modificationDate.setDateEnd(parseDate(dateEnd));
	return modificationDate;

    }

}
