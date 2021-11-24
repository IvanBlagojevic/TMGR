package org.gs4tr.termmanager.service.model.command.dto;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.ArrayUtils;
import org.gs4tr.termmanager.model.dto.ImportAttributeReplacement;
import org.gs4tr.termmanager.model.dto.ImportLanguageReplacement;
import org.gs4tr.termmanager.model.dto.NumberOfTermEntriesByFileName;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.service.model.command.ImportCommand;
import org.gs4tr.termmanager.service.model.command.dto.filemanagment.DtoImportLanguages;
import org.gs4tr.termmanager.service.termentry.synchronization.SyncOption;

public class DtoImportCommand implements DtoTaskHandlerCommand<ImportCommand> {

    private ImportAttributeReplacement[] _attributeReplacements;

    private String _defaultTermStatus;

    private boolean _finalyOverwriteByTermEntryId = true;

    private String _folder;

    private boolean _ignoreCase = true;

    private DtoImportLanguages[] _importLanguagesPerFile;

    private ImportLanguageReplacement[] _languageReplacements;

    private NumberOfTermEntriesByFileName[] _numberOfTermEntries;

    private boolean _preImportCheck;

    private String _projectTicket;

    private String _sourceLanguage;

    private String _syncOption;

    private String[] _termAttributeNames;

    private String[] _termEntryAttributeNames;

    private String[] _termNoteNames;

    @Override
    public ImportCommand convertToInternalTaskHandlerCommand() {
	Long projectId = TicketConverter.fromDtoToInternal(getProjectTicket(), Long.class);

	ImportCommand importCommand = new ImportCommand();
	importCommand.setProjectId(projectId);
	importCommand.setFolder(getFolder());
	importCommand.setPreImportCheck(isPreImportCheck());
	importCommand.setIgnoreCase(isIgnoreCase());
	importCommand.setDefaultTermStatus(getDefaultTermStatus());

	Map<String, String> languageReplacementByCode = new HashMap<>();
	ImportLanguageReplacement[] languageReplacements = getLanguageReplacements();
	if (ArrayUtils.isNotEmpty(languageReplacements)) {
	    for (ImportLanguageReplacement languageReplacement : languageReplacements) {
		String importLanguageCode = languageReplacement.getImportLanguageCode();
		String replacement = languageReplacement.getReplacement();
		languageReplacementByCode.put(importLanguageCode, replacement);
	    }
	}
	importCommand.setLanguageReplacementByCode(languageReplacementByCode);

	ImportAttributeReplacement[] attributeReplacements = getAttributeReplacements();
	importCommand
		.setAttributeReplacements(Objects.nonNull(attributeReplacements) ? Arrays.asList(attributeReplacements)
			: Collections.emptyList());

	Map<String, Integer> numberOfTermEntriesByFileName = new HashMap<>();
	NumberOfTermEntriesByFileName[] numberOfTermEntriesArray = getNumberOfTermEntries();
	if (ArrayUtils.isNotEmpty(numberOfTermEntriesArray)) {
	    for (NumberOfTermEntriesByFileName each : numberOfTermEntriesArray) {
		Integer numberOfTermEntries = Integer.valueOf(each.getNumberOfTermEntries());
		numberOfTermEntriesByFileName.put(each.getFileName(), numberOfTermEntries);
	    }
	}
	importCommand.setNumberOfTermEntriesByFileName(numberOfTermEntriesByFileName);

	DtoImportLanguages[] dtoImportLanguagesPerFile = getImportLanguagesPerFile();
	if (Objects.nonNull(dtoImportLanguagesPerFile)) {
	    Map<String, List<String>> importLanguagesPerFile = new HashMap<>();
	    for (DtoImportLanguages l : dtoImportLanguagesPerFile) {
		importLanguagesPerFile.put(l.getFileName(), Arrays.asList(l.getImportLanguages()));
	    }

	    importCommand.setImportLanguagesPerFile(importLanguagesPerFile);
	}

	importCommand.setSourceLanguage(getSourceLanguage());

	importCommand.setTermEntryAttributeNames(convertToSet(getTermEntryAttributeNames()));
	importCommand.setTermAttributeNames(convertToSet(getTermAttributeNames()));
	importCommand.setTermNoteNames(convertToSet(getTermNoteNames()));

	if (Objects.nonNull(getSyncOption())) {
	    SyncOption syncOption = SyncOption.valueOf(getSyncOption());
	    importCommand.setSyncOption(syncOption);
	}

	importCommand.setFinalyOverwriteByTermEntryId(isFinalyOverwriteByTermEntryId());

	return importCommand;
    }

    public ImportAttributeReplacement[] getAttributeReplacements() {
	return _attributeReplacements;
    }

    public String getDefaultTermStatus() {
	return _defaultTermStatus;
    }

    public String getFolder() {
	return _folder;
    }

    public DtoImportLanguages[] getImportLanguagesPerFile() {
	return _importLanguagesPerFile;
    }

    public ImportLanguageReplacement[] getLanguageReplacements() {
	return _languageReplacements;
    }

    public NumberOfTermEntriesByFileName[] getNumberOfTermEntries() {
	return _numberOfTermEntries;
    }

    public String getProjectTicket() {
	return _projectTicket;
    }

    public String getSourceLanguage() {
	return _sourceLanguage;
    }

    public String getSyncOption() {
	return _syncOption;
    }

    public String[] getTermAttributeNames() {
	return _termAttributeNames;
    }

    public String[] getTermEntryAttributeNames() {
	return _termEntryAttributeNames;
    }

    public String[] getTermNoteNames() {
	return _termNoteNames;
    }

    public boolean isFinalyOverwriteByTermEntryId() {
	return _finalyOverwriteByTermEntryId;
    }

    public boolean isIgnoreCase() {
	return _ignoreCase;
    }

    public boolean isPreImportCheck() {
	return _preImportCheck;
    }

    public void setAttributeReplacements(ImportAttributeReplacement[] attributeReplacements) {
	_attributeReplacements = attributeReplacements;
    }

    public void setDefaultTermStatus(String defaultTermStatus) {
	_defaultTermStatus = defaultTermStatus;
    }

    public void setFinalyOverwriteByTermEntryId(boolean finalyOverwriteByTermEntryId) {
	_finalyOverwriteByTermEntryId = finalyOverwriteByTermEntryId;
    }

    public void setFolder(String folder) {
	_folder = folder;
    }

    public void setIgnoreCase(boolean ignoreCase) {
	_ignoreCase = ignoreCase;
    }

    public void setImportLanguagesPerFile(DtoImportLanguages[] importLanguagesPerFile) {
	_importLanguagesPerFile = importLanguagesPerFile;
    }

    public void setLanguageReplacements(ImportLanguageReplacement[] languageReplacements) {
	_languageReplacements = languageReplacements;
    }

    public void setNumberOfTermEntries(NumberOfTermEntriesByFileName[] numberOfTermEntries) {
	_numberOfTermEntries = numberOfTermEntries;
    }

    public void setPreImportCheck(boolean preImportCheck) {
	_preImportCheck = preImportCheck;
    }

    public void setProjectTicket(String projectTicket) {
	_projectTicket = projectTicket;
    }

    public void setSourceLanguage(String sourceLanguage) {
	_sourceLanguage = sourceLanguage;
    }

    public void setSyncOption(String syncOption) {
	_syncOption = syncOption;
    }

    public void setTermAttributeNames(String[] termAttributeNames) {
	_termAttributeNames = termAttributeNames;
    }

    public void setTermEntryAttributeNames(String[] termEntryAttributeNames) {
	_termEntryAttributeNames = termEntryAttributeNames;
    }

    public void setTermNoteNames(String[] termNoteNames) {
	_termNoteNames = termNoteNames;
    }

    private Set<String> convertToSet(String[] elements) {
	return ArrayUtils.isEmpty(elements) ? Collections.emptySet()
		: Arrays.stream(elements).collect(Collectors.toSet());
    }
}
