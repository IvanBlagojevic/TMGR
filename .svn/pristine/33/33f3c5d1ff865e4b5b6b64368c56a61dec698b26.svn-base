package org.gs4tr.termmanager.service;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gs4tr.foundation.modules.entities.model.RepositoryItem;
import org.gs4tr.foundation.modules.entities.model.RepositoryTicket;
import org.gs4tr.foundation.modules.entities.model.ResourceInfo;
import org.gs4tr.termmanager.model.Attribute;
import org.gs4tr.termmanager.model.ExportInfo;
import org.gs4tr.termmanager.model.TermEntryResourceTrack;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TranslationUnit;
import org.gs4tr.termmanager.model.UpdateCommand;
import org.gs4tr.termmanager.model.dto.ExportTermModel;
import org.gs4tr.termmanager.model.event.ProjectDetailInfo;
import org.gs4tr.termmanager.model.event.StatisticsInfo;
import org.gs4tr.termmanager.model.glossary.Action;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.model.search.TermEntrySearchRequest;
import org.gs4tr.termmanager.persistence.solr.faceting.FacetTermCounts;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.termmanager.service.export.ExportDocumentFactory;
import org.gs4tr.termmanager.service.impl.ExportNotificationCallback;
import org.gs4tr.tm3.api.Page;
import org.springframework.security.access.annotation.Secured;

public interface TermEntryService {

    @Secured({ "POLICY_FOUNDATION_SECURITY_GETLOGINDATA" })
    ExportTermModel addTermWS(Long projectId, String sourceLocale, String targetLocale, String sourceTerm,
	    String sourceDescription, String targetTerm, String targetDescription);

    @Secured({ "POLICY_FOUNDATION_SECURITY_GETLOGINDATA" })
    void deleteTermEntryDescriptionsByType(List<String> type, Long projectId, List<String> languageIds);

    @Secured({ "POLICY_FOUNDATION_SECURITY_GETLOGINDATA" })
    void deleteTermEntryResourceTracks(String termentryId, List<String> repositoryTickets, Long projectId);

    @Secured({ "POLICY_FOUNDATION_SECURITY_GETLOGINDATA" })
    RepositoryItem downloadResource(Long resourceId);

    @Secured({ "POLICY_FOUNDATION_SECURITY_GETLOGINDATA" })
    ExportInfo exportDocument(TermEntrySearchRequest termEntrySearchRequest, TmgrSearchFilter filter,
	    String xslTransormationName, ExportNotificationCallback exportTbxNotificationCallback);

    @Secured({ "POLICY_FOUNDATION_SECURITY_GETLOGINDATA", "POLICY_TM_READ" })
    ExportInfo exportDocumentWS(List<TermEntry> termentries, TermEntrySearchRequest termEntryExportSearchRequest,
	    ExportNotificationCallback exportTbxNotificationCallback, ExportDocumentFactory exportFactory);

    @Secured({ "POLICY_FOUNDATION_SECURITY_GETLOGINDATA", "POLICY_TM_READ" })
    void exportForbiddenTerms(List<Term> terms, ExportDocumentFactory transformingTbxTermEntryWriter);

    @Secured({ "POLICY_FOUNDATION_SECURITY_GETLOGINDATA" })
    Map<String, List<TermEntry>> findHistoriesByTermEntryIds(Collection<String> termEntryIds);

    @Secured({ "POLICY_FOUNDATION_SECURITY_GETLOGINDATA" })
    List<TermEntry> findHistoryByTermEntryId(String termEntryId);

    @Secured({ "POLICY_FOUNDATION_SECURITY_GETLOGINDATA" })
    List<TermEntryResourceTrack> findResourceTracksByTermEntryById(String termEntryId);

    @Secured({ "POLICY_FOUNDATION_SECURITY_GETLOGINDATA" })
    List<TermEntry> findTermEntriesByProjectId(Long projectId);

    @Secured({ "POLICY_FOUNDATION_SECURITY_GETLOGINDATA" })
    List<TermEntry> findTermEntriesByTermIds(Collection<String> termIds, Long projectId);

    @Secured({ "POLICY_FOUNDATION_SECURITY_GETLOGINDATA" })
    TermEntry findTermEntryById(String termEntryId, Long projectId);

    @Secured({ "POLICY_FOUNDATION_SECURITY_GETLOGINDATA" })
    List<TermEntry> findTermentriesByIds(List<String> termEntryId, Long projectId);

    @Secured({ "POLICY_FOUNDATION_SECURITY_GETLOGINDATA" })
    Map<Long, Long> getNumberOfTermEntries(TmgrSearchFilter filter);

    @Secured({ "POLICY_FOUNDATION_SECURITY_GETLOGINDATA" })
    long getNumberOfTerms(TmgrSearchFilter filter, String collection);

    @Secured({ "POLICY_FOUNDATION_SECURITY_GETLOGINDATA" })
    void mergeTermEntries(TermEntry existing, Collection<TermEntry> incoming);

    @Secured({ "POLICY_FOUNDATION_SECURITY_GETLOGINDATA" })
    void notifyListeners(TmProject project, ProjectDetailInfo projectDetailInfo, Set<StatisticsInfo> statistics,
	    TermEntry termEntry, String status, List<UpdateCommand> updateCommands, String remoteUser);

    @Secured({ "POLICY_FOUNDATION_SECURITY_GETLOGINDATA" })
    void renameTermDescriptions(Long projectId, List<Attribute> attributes);

    @Secured({ "POLICY_FOUNDATION_SECURITY_GETLOGINDATA" })
    FacetTermCounts searchFacetTermCounts(TmgrSearchFilter filter);

    @Secured({ "POLICY_FOUNDATION_SECURITY_GETLOGINDATA" })
    List<TermEntry> searchSegmentTermEntries(TmgrSearchFilter filter);

    @Secured({ "POLICY_FOUNDATION_SECURITY_GETLOGINDATA" })
    Page<TermEntry> searchTermEntries(TmgrSearchFilter filter);

    @Secured({ "POLICY_FOUNDATION_SECURITY_GETLOGINDATA" })
    Page<TermEntry> searchTermEntries(TmgrSearchFilter filter, String collection);

    Page<TermEntry> segmentTMSearch(TmgrSearchFilter filter);

    void updateRegularTermEntries(Long projectId, List<TermEntry> termEntries);

    void updateSubmissionTermEntries(Long projectId, List<TermEntry> termEntries);

    @Secured({ "POLICY_FOUNDATION_SECURITY_GETLOGINDATA" })
    void updateTermEntries(List<TranslationUnit> translationUnits, String sourceLanguage, Long projectId,
	    Action action);

    @Secured({ "POLICY_FOUNDATION_SECURITY_GETLOGINDATA" })
    void updateTermEntriesLatestChanges(List<TermEntry> termEntries);

    @Secured({ "POLICY_FOUNDATION_SECURITY_GETLOGINDATA" })
    String updateTermEntry(TermEntry termEntry, List<UpdateCommand> updateCommands);

    @Secured({ "POLICY_FOUNDATION_SECURITY_GETLOGINDATA" })
    RepositoryTicket updateTermEntryResourceTrack(String termEntryId, String repositoryTicket,
	    RepositoryItem repositoryItem);

    @Secured({ "POLICY_FOUNDATION_SECURITY_GETLOGINDATA" })
    RepositoryTicket uploadBinaryResource(String termEntryId, ResourceInfo resourceInfo, InputStream inputStream,
	    String attributeType);
}