package org.gs4tr.termmanager.service.model.command;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gs4tr.termmanager.model.dto.ImportAttributeReplacement;
import org.gs4tr.termmanager.model.dto.ImportLanguageReplacement;
import org.gs4tr.termmanager.service.termentry.synchronization.SyncOption;

public class ImportCommand {

    private List<ImportAttributeReplacement> _attributeReplacements;

    private String _defaultTermStatus;

    private boolean _finalyOverwriteByTermEntryId = true;

    private String _folder;

    private boolean _ignoreCase = true;

    /**
     * Original languages from the file that Server Side returns on file analysis,
     * not {@link ImportLanguageReplacement#getReplacement()} (Language replacements
     * are similar project language that user choose to import instead of original).
     */
    private Map<String, List<String>> _importLanguagesPerFile;

    private Map<String, String> _languageReplacementByCode;

    private Map<String, Integer> _numberOfTermEntriesByFileName;

    private boolean _preImportCheck;

    private Long _projectId;

    private String _sourceLanguage;

    private SyncOption _syncOption = SyncOption.OVERWRITE;

    private Set<String> _termAttributeNames;

    private Set<String> _termEntryAttributeNames;

    private Set<String> _termNoteNames;

    public List<ImportAttributeReplacement> getAttributeReplacements() {
	return _attributeReplacements;
    }

    public String getDefaultTermStatus() {
	return _defaultTermStatus;
    }

    public String getFolder() {
	return _folder;
    }

    public Map<String, List<String>> getImportLanguagesPerFile() {
	return _importLanguagesPerFile;
    }

    public Map<String, String> getLanguageReplacementByCode() {
	return _languageReplacementByCode;
    }

    public Map<String, Integer> getNumberOfTermEntriesByFileName() {
	return _numberOfTermEntriesByFileName;
    }

    public Long getProjectId() {
	return _projectId;
    }

    public String getSourceLanguage() {
	return _sourceLanguage;
    }

    public SyncOption getSyncOption() {
	return _syncOption;
    }

    public Set<String> getTermAttributeNames() {
	return _termAttributeNames;
    }

    public Set<String> getTermEntryAttributeNames() {
	return _termEntryAttributeNames;
    }

    public Set<String> getTermNoteNames() {
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

    public void setAttributeReplacements(List<ImportAttributeReplacement> attributeReplacements) {
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

    public void setImportLanguagesPerFile(Map<String, List<String>> importLanguagesPerFile) {
	_importLanguagesPerFile = importLanguagesPerFile;
    }

    public void setLanguageReplacementByCode(Map<String, String> languageReplacementByCode) {
	_languageReplacementByCode = languageReplacementByCode;
    }

    public void setNumberOfTermEntriesByFileName(Map<String, Integer> numberOfTermEntriesByFileName) {
	_numberOfTermEntriesByFileName = numberOfTermEntriesByFileName;
    }

    public void setPreImportCheck(boolean preImportCheck) {
	_preImportCheck = preImportCheck;
    }

    public void setProjectId(Long projectId) {
	_projectId = projectId;
    }

    public void setSourceLanguage(String sourceLanguage) {
	_sourceLanguage = sourceLanguage;
    }

    public void setSyncOption(SyncOption syncOption) {
	_syncOption = syncOption;
    }

    public void setTermAttributeNames(Set<String> termAttributeNames) {
	_termAttributeNames = termAttributeNames;
    }

    public void setTermEntryAttributeNames(Set<String> termEntryAttributeNames) {
	_termEntryAttributeNames = termEntryAttributeNames;
    }

    public void setTermNoteNames(Set<String> termNoteNames) {
	_termNoteNames = termNoteNames;
    }
}
