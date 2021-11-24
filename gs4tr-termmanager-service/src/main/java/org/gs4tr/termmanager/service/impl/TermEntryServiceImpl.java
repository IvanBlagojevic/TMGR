package org.gs4tr.termmanager.service.impl;

import static java.util.Collections.singletonList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.gs4tr.foundation.modules.entities.model.LogHelper;
import org.gs4tr.foundation.modules.entities.model.RepositoryItem;
import org.gs4tr.foundation.modules.entities.model.RepositoryPath;
import org.gs4tr.foundation.modules.entities.model.RepositoryTicket;
import org.gs4tr.foundation.modules.entities.model.ResourceInfo;
import org.gs4tr.foundation.modules.entities.model.StringConstants;
import org.gs4tr.foundation.modules.entities.model.UserException;
import org.gs4tr.foundation.modules.entities.model.types.ItemStatusType;
import org.gs4tr.foundation.modules.repository.RepositoryManager;
import org.gs4tr.foundation3.tbx.constraint.ConstraintFactory;
import org.gs4tr.termmanager.cache.entry.processor.executor.HzEntryProcessorExecutor;
import org.gs4tr.termmanager.cache.model.CacheName;
import org.gs4tr.termmanager.dao.TermEntryResourceTrackDAO;
import org.gs4tr.termmanager.model.Attribute;
import org.gs4tr.termmanager.model.AttributeLevelEnum;
import org.gs4tr.termmanager.model.ExportInfo;
import org.gs4tr.termmanager.model.ExportLanguageCriteriaEnum;
import org.gs4tr.termmanager.model.ImportSummary;
import org.gs4tr.termmanager.model.Language;
import org.gs4tr.termmanager.model.LastOperationEnum;
import org.gs4tr.termmanager.model.TermEntryResourceTrack;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.TransactionalUnit;
import org.gs4tr.termmanager.model.TranslationUnit;
import org.gs4tr.termmanager.model.UpdateCommand;
import org.gs4tr.termmanager.model.UpdateCommand.CommandEnum;
import org.gs4tr.termmanager.model.UpdateCommand.TypeEnum;
import org.gs4tr.termmanager.model.dto.DateRange;
import org.gs4tr.termmanager.model.dto.ExportTermModel;
import org.gs4tr.termmanager.model.dto.converter.TermDescriptionConverter;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.model.event.EventMessage;
import org.gs4tr.termmanager.model.event.ProjectDetailInfo;
import org.gs4tr.termmanager.model.event.StatisticsInfo;
import org.gs4tr.termmanager.model.glossary.Action;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermEntry;
import org.gs4tr.termmanager.model.search.TermEntrySearchRequest;
import org.gs4tr.termmanager.persistence.solr.faceting.FacetTermCounts;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.termmanager.persistence.solr.util.SolrGlossaryAdapter;
import org.gs4tr.termmanager.persistence.update.DescriptionImportOption;
import org.gs4tr.termmanager.persistence.update.ImportOptionsModel;
import org.gs4tr.termmanager.service.DbTermEntryService;
import org.gs4tr.termmanager.service.ImportTermService;
import org.gs4tr.termmanager.service.TermEntryExporter;
import org.gs4tr.termmanager.service.TermEntryService;
import org.gs4tr.termmanager.service.export.ExportDocumentFactory;
import org.gs4tr.termmanager.service.export.ExportFormatEnum;
import org.gs4tr.termmanager.service.export.ExportTermsCallback;
import org.gs4tr.termmanager.service.history.HistoryCreator;
import org.gs4tr.termmanager.service.manualtask.ManualTaskHandlerUtils;
import org.gs4tr.termmanager.service.persistence.importer.termentry.synchronizer.ITermEntrySynchronizer;
import org.gs4tr.termmanager.service.termentry.synchronization.SyncOption;
import org.gs4tr.termmanager.service.termentry.synchronization.TermEntrySynchronizerFactory;
import org.gs4tr.termmanager.service.utils.PathHelper;
import org.gs4tr.termmanager.service.utils.ServiceUtils;
import org.gs4tr.termmanager.service.utils.TaskHandlerConstants;
import org.gs4tr.termmanager.service.utils.TermEntryUtils;
import org.gs4tr.termmanager.service.utils.UpdateCommandUtils;
import org.gs4tr.tm3.api.Page;
import org.gs4tr.tm3.api.TmException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hazelcast.map.AbstractEntryProcessor;

@Service("termEntryService")
public class TermEntryServiceImpl extends AbstractNotifyingService implements TermEntryService {

    @Autowired
    private DbTermEntryService _dbTermEntryService;

    @Autowired
    @Qualifier("regularHistoryCreator")
    private HistoryCreator _historyCreator;

    @Autowired
    private HzEntryProcessorExecutor _hzEntryProcessorExecutor;

    @Autowired
    private ImportTermService _importTermService;

    @Autowired(required = false)
    private RepositoryManager _repositoryManager;

    @Value("${synonym.number:5}")
    private int _synonymNumber;

    @Autowired
    private TermEntryExporter _termEntryExporter;

    @Autowired
    private TermEntryResourceTrackDAO _termEntryResourceTrackDAO;

    @Override
    public ExportTermModel addTermWS(Long projectId, String sourceLocale, String targetLocale, String sourceTermName,
	    String sourceDescription, String targetTermName, String targetDescription) {
	TmProject project = getProjectService().load(projectId);

	TermEntry termEntry = TermEntryUtils.createEmptyTermEntry(project);
	termEntry.setAction(Action.ADDED_REMOTELY);

	List<UpdateCommand> updateCommands = new ArrayList<>();
	String parentMarkerId = termEntry.getUuId();
	String sourceTermId = UUID.randomUUID().toString();
	UpdateCommand sourceUpdateCommand = new UpdateCommand("add", "term", //$NON-NLS-1$ //$NON-NLS-2$
		null, sourceTermId, parentMarkerId, sourceTermName);
	sourceUpdateCommand.setLanguageId(sourceLocale);
	updateCommands.add(sourceUpdateCommand);

	Set<String> attributeTypes = new HashSet<>();

	if (StringUtils.isNotBlank(sourceDescription)) {
	    List<Description> descriptions = parseDescriptionsLine(sourceDescription);
	    for (Description description : descriptions) {
		String descId = UUID.randomUUID().toString();
		updateCommands.add(new UpdateCommand(CommandEnum.ADD.getName(), TypeEnum.DESCRIP.getName(),
			description.getType(), descId, sourceTermId, description.getValue()));
		attributeTypes.add(description.getType());
	    }
	}

	String targetTermId = UUID.randomUUID().toString();
	UpdateCommand targetUpdateCommand = new UpdateCommand("add", "term", //$NON-NLS-1$ //$NON-NLS-2$
		null, targetTermId, parentMarkerId, targetTermName);
	targetUpdateCommand.setLanguageId(targetLocale);
	updateCommands.add(targetUpdateCommand);

	if (StringUtils.isNotBlank(targetDescription)) {
	    List<Description> descriptions = parseDescriptionsLine(targetDescription);
	    for (Description description : descriptions) {
		String descId = UUID.randomUUID().toString();
		updateCommands.add(new UpdateCommand(CommandEnum.ADD.getName(), TypeEnum.DESCRIP.getName(),
			description.getType(), descId, targetTermId, description.getValue()));
		attributeTypes.add(description.getType());
	    }
	}

	if (!attributeTypes.isEmpty()) {
	    EnumMap<AttributeLevelEnum, Set<String>> projectAttributeMap = new EnumMap<>(AttributeLevelEnum.class);
	    projectAttributeMap.put(AttributeLevelEnum.LANGUAGE, attributeTypes);
	    getProjectService().addOrUpdateProjectAttributesOnImport(projectId, projectAttributeMap, true);
	}

	updateTermEntry(termEntry, updateCommands);

	ExportTermModel exportModel = new ExportTermModel();
	Term sourceTerm = null;
	Term targetTerm = null;

	for (Term term : termEntry.ggetTerms()) {
	    String termLanguage = term.getLanguageId();
	    if (termLanguage.equals(sourceLocale)) {
		sourceTerm = term;
	    } else if (termLanguage.equals(targetLocale)) {
		targetTerm = term;
	    }
	}

	exportModel.setCreationDate(sourceTerm.getDateCreated());
	exportModel.setCreationUser(sourceTerm.getUserCreated());
	exportModel.setOperation(LastOperationEnum.UPDATED.getShortLabel());
	exportModel.setSource(sourceTerm.getName());
	exportModel.setTarget(targetTerm.getName());
	exportModel.setModificationDate(sourceTerm.getDateModified());
	exportModel.setModificationUser(sourceTerm.getUserModified());
	exportModel.setTicket(TicketConverter.fromInternalToDto(sourceTerm.getUuId())
		.concat(TicketConverter.fromInternalToDto(targetTerm.getUuId())));
	exportModel.setSourceAttributes(TermDescriptionConverter.fromInternalToDto(sourceTerm.getDescriptions()));
	exportModel.setTargetAttributes(TermDescriptionConverter.fromInternalToDto(targetTerm.getDescriptions()));
	exportModel.setSuggestions(new String[0]);
	exportModel.setForbidden(sourceTerm.isForbidden() || targetTerm.isForbidden());
	return exportModel;
    }

    @Override
    public void deleteTermEntryDescriptionsByType(List<String> type, Long projectId, List<String> languageIds) {
	TmgrSearchFilter filter = new TmgrSearchFilter();

	List<String> languageResultFields = new ArrayList<>();
	if (CollectionUtils.isNotEmpty(languageIds)) {
	    languageResultFields.addAll(languageIds);
	}
	filter.addLanguageResultField(true, getSynonymNumber(),
		languageResultFields.toArray(new String[languageResultFields.size()]));

	String sourceLanguageId = languageIds.get(0);

	List<String> targetLanguages = new ArrayList<>(languageIds);
	targetLanguages.remove(sourceLanguageId);

	filter.addProjectId(projectId);
	filter.addAttributeType(type.toArray(new String[type.size()]));
	filter.setSourceLanguage(sourceLanguageId);
	filter.setTargetLanguages(targetLanguages);

	List<TermEntry> termEntries = browseTermEntries(filter);
	if (CollectionUtils.isEmpty(termEntries)) {
	    return;
	}

	final String userName = TmUserProfile.getCurrentUserName();
	final Date dateModified = new Date();

	List<TermEntry> updatedTermEntries = new ArrayList<>();
	for (TermEntry termEntry : termEntries) {
	    Set<Description> descriptions = termEntry.getDescriptions();
	    if (ServiceUtils.deleteDescriptions(descriptions, type, null)) {
		termEntry.setAction(Action.EDITED);
		termEntry.setDateModified(dateModified.getTime());
		termEntry.setUserModified(userName);
		updatedTermEntries.add(termEntry);
	    }
	}

	if (CollectionUtils.isNotEmpty(updatedTermEntries)) {
	    updateRegularTermEntries(projectId, Action.EDITED.name(), new TransactionalUnit(updatedTermEntries));
	    // 31-May-2017, as per [Improvement#TERII-4225]:
	    getProjectDetailService().updateDateModifiedByProjectId(projectId, dateModified);
	}
    }

    @Override
    @Transactional
    public void deleteTermEntryResourceTracks(String termEntryId, List<String> resourceIds, Long projectId) {

	TermEntry termEntry = findTermEntryById(termEntryId, projectId);

	for (String resourceId : resourceIds) {
	    removeTermEntryResourceTrack(resourceId, termEntry);
	}
    }

    @Override
    @Transactional(readOnly = true)
    public RepositoryItem downloadResource(Long resourceId) {
	TermEntryResourceTrack resourceTrack = getTermEntryResourceTrackDAO().findById(resourceId);

	if (resourceTrack == null) {
	    throw new RuntimeException(Messages.getString("TermEntryServiceImpl.3")); //$NON-NLS-1$
	}

	RepositoryTicket repositoryTicket = new RepositoryTicket(resourceTrack.getResourceId());

	return getRepositoryManager().read(repositoryTicket);
    }

    @Override
    public ExportInfo exportDocument(TermEntrySearchRequest exportSearchRequest, TmgrSearchFilter filter,
	    String exportFormatName, ExportNotificationCallback notificationCallback) {

	ExportInfo exportInfo = new ExportInfo();

	collectExportInfoData(exportInfo, exportSearchRequest, exportFormatName);

	ExportFormatEnum exportFormat = ExportFormatEnum.valueOf(exportFormatName);

	OutputStream zos = null;
	OutputStream tempXCSFileOutputStream = null;
	File xcsTempFile = null;
	String xcsFileName = null;
	File zipTempFile = null;

	File tempFile = null;

	ExportDocumentFactory exportDocumentFactory;
	OutputStream tempFileOutputStream = null;

	ZipOutputStream zipOutputStream = null;

	boolean isExportCanceled = false;

	boolean isTbx = ExportFormatEnum.TBX == exportFormat;

	try {
	    if (isTbx) {
		xcsFileName = ServiceUtils.generateExportDocumentName(exportInfo.getProjectName(),
			exportInfo.getShortCode(), TaskHandlerConstants.XCS);

		String zipFileName = generateTempFileName();
		zipTempFile = new File(StringConstants.TEMP_DIR, FilenameUtils.getName(zipFileName));
		xcsTempFile = new File(StringConstants.TEMP_DIR, FilenameUtils.getName(xcsFileName));

		tempXCSFileOutputStream = new FileOutputStream(xcsTempFile);
		zos = new FileOutputStream(zipTempFile);
	    }

	    String fileName = ServiceUtils.generateExportDocumentName(exportInfo.getProjectName(),
		    exportInfo.getShortCode(), exportFormat.getFileFormat());

	    tempFile = new File(StringConstants.TEMP_DIR, FilenameUtils.getName(fileName));
	    tempFileOutputStream = new FileOutputStream(tempFile);

	    exportDocumentFactory = ExportDocumentFactory.getInstance(exportFormat);

	    ConstraintFactory constraintFactory = exportDocumentFactory.open(tempFileOutputStream, exportSearchRequest,
		    xcsFileName);

	    Page<TermEntry> termEntryPage = searchTermEntries(filter);

	    int totalResults = termEntryPage.getTotalResults();
	    notificationCallback.init(totalResults);
	    getHzEntryProcessorExecutor().executeOnKey(CacheName.EXPORT_PROGRESS_STATUS,
		    notificationCallback.getThreadName(), new InitTermEntryCountEntryProcessor(totalResults));

	    if (totalResults > 0 && ExportFormatEnum.CSVEXPORT_MS == exportFormat
		    || ExportFormatEnum.TAB_MS == exportFormat) {
		exportDocumentFactory.writeBOM();
	    }

	    List<TermEntry> termEntries = termEntryPage.getResults();
	    while (CollectionUtils.isNotEmpty(termEntries)) {
		if (getTermEntryExporter().exportInternal(termEntries, notificationCallback, exportDocumentFactory,
			exportInfo)) {
		    isExportCanceled = true;
		    cancelExport(notificationCallback, exportInfo);
		    return exportInfo;
		}
		filter.setPageable(filter.getPageable().next());
		termEntryPage = searchTermEntries(filter);
		termEntries = termEntryPage.getResults();

		exportDocumentFactory.flush();
	    }

	    exportDocumentFactory.close();

	    if (isTbx) {
		constraintFactory.save(tempXCSFileOutputStream);
		zipOutputStream = makeZipFile(zos, xcsTempFile, xcsFileName, fileName, tempFile);

		FileUtils.forceDelete(tempFile);
		FileUtils.forceDelete(xcsTempFile);
		exportInfo.setTempFile(zipTempFile);
	    } else {
		exportInfo.setTempFile(tempFile);
	    }

	    notificationCallback.notifyProcessingFinished(exportInfo);
	    getHzEntryProcessorExecutor().executeOnKey(CacheName.EXPORT_PROGRESS_STATUS,
		    notificationCallback.getThreadName(), new NotifyProcessingFinishedEntryProcessor(exportInfo));
	} catch (Exception e) {
	    throw new RuntimeException(e.getMessage(), e);
	} finally {

	    IOUtils.closeQuietly(zipOutputStream);
	    IOUtils.closeQuietly(tempXCSFileOutputStream);
	    IOUtils.closeQuietly(zos);
	    IOUtils.closeQuietly(tempFileOutputStream);

	    if (isExportCanceled) {
		ManualTaskHandlerUtils.forceDeleteFile(xcsTempFile);
		ManualTaskHandlerUtils.forceDeleteFile(zipTempFile);
		ManualTaskHandlerUtils.forceDeleteFile(tempFile);
	    }
	}

	return exportInfo;
    }

    @Override
    public ExportInfo exportDocumentWS(List<TermEntry> termentries, TermEntrySearchRequest searchRequest,
	    ExportNotificationCallback exportTbxNotificationCallback, ExportDocumentFactory exportFactory) {

	ExportInfo exportInfo = new ExportInfo();

	ExportTermsCallback exportTermsCallback = termEntry -> new ArrayList<>(termEntry.ggetTerms());

	for (TermEntry termEntry : termentries) {
	    try {
		exportFactory.write(termEntry, new ExportInfo(), true, exportTermsCallback);
	    } catch (IOException e) {
		throw new RuntimeException(e);
	    }
	}

	if (exportTbxNotificationCallback != null) {
	    exportTbxNotificationCallback.notifyItemProcessingFinished();
	    exportTbxNotificationCallback.notifyProcessingFinished(exportInfo);
	}

	return exportInfo;
    }

    @Override
    public void exportForbiddenTerms(List<Term> terms, ExportDocumentFactory writer) {
	if (CollectionUtils.isEmpty(terms)) {
	    return;
	}

	for (Term term : terms) {
	    try {
		writer.writeForbiddenTerms(term);
	    } catch (IOException e) {
		throw new RuntimeException(e.getMessage(), e);
	    }
	}
    }

    @Override
    public Map<String, List<TermEntry>> findHistoriesByTermEntryIds(Collection<String> termEntryIds) {
	Map<String, List<TermEntry>> result = new HashMap<>();
	List<DbTermEntry> dbEntries = getDbTermEntryService().findByIds(termEntryIds, true);
	for (DbTermEntry dbTermEntry : dbEntries) {
	    result.put(dbTermEntry.getUuId(), createHistory(dbTermEntry));
	}
	return result;
    }

    @Override
    public List<TermEntry> findHistoryByTermEntryId(String termEntryId) {
	DbTermEntry dbEntry = getDbTermEntryService().findById(termEntryId, true);
	return createHistory(dbEntry);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TermEntryResourceTrack> findResourceTracksByTermEntryById(String termEntryId) {
	return getTermEntryResourceTrackDAO().findAllByTermEntryId(termEntryId);
    }

    public List<TermEntry> findTermEntriesByProjectId(Long projectId) {
	try {
	    return getBrowser(getRegularCollection()).findByProjectId(projectId);
	} catch (TmException e) {
	    throw new RuntimeException(e.getMessage(), e);
	}
    }

    @Override
    public List<TermEntry> findTermEntriesByTermIds(Collection<String> termIds, Long projectId) {
	return findTermEntriesByTermIds(getRegularCollection(), termIds, projectId);
    }

    @Override
    public TermEntry findTermEntryById(String termEntryId, Long projectId) {
	return findTermEntryById(getRegularCollection(), termEntryId, projectId);
    }

    @Override
    public List<TermEntry> findTermentriesByIds(List<String> termEntryIds, Long projectId) {
	return findTermEntriesByIds(termEntryIds, projectId);
    }

    @Override
    public Map<Long, Long> getNumberOfTermEntries(TmgrSearchFilter filter) {
	return getSearcher(getRegularCollection()).getNumberOfTermEntries(filter);
    }

    @Override
    public long getNumberOfTerms(TmgrSearchFilter filter, String collection) {
	try {
	    return getSearcher(collection).getNumberOfTerms(filter);
	} catch (TmException e) {
	    throw new RuntimeException(e.getMessage(), e);
	}
    }

    // This method will add all terminology data from incoming term entries
    // into existing, but it will skip duplicate ones
    @Override
    public void mergeTermEntries(TermEntry existing, Collection<TermEntry> incoming) {
	try {
	    Validate.notNull(existing);
	    Validate.notEmpty(incoming);

	    TmUserProfile userProfile = TmUserProfile.getCurrentUserProfile();

	    ITermEntrySynchronizer synchronizer = TermEntrySynchronizerFactory.INSTANCE
		    .createTermEntrySynchronizer(SyncOption.MERGE);

	    Long projectId = existing.getProjectId();

	    ImportOptionsModel options = new ImportOptionsModel();
	    options.setProjectId(projectId);
	    options.setDescriptionImportOption(DescriptionImportOption.ADD_ALL);
	    options.setSynonymNumber(getSynonymNumber());

	    // collect existing languages
	    Set<String> languageIds = new HashSet<>(existing.getLanguageTerms().keySet());

	    ImportSummary importSummary = new ImportSummary();

	    List<TranslationUnit> tusForDelete = new ArrayList<>();

	    // merge each incoming entry with existing
	    for (TermEntry in : incoming) {
		if (StringUtils.isNotEmpty(in.getUuId())) {
		    // create translation unit in order to delete incoming entry
		    List<UpdateCommand> removeCommands = new ArrayList<>();
		    for (Term term : in.ggetTerms()) {
			UpdateCommand command = UpdateCommandUtils.createRemoveCommandFromTerm(term);
			removeCommands.add(command);
		    }

		    TranslationUnit tu = new TranslationUnit();
		    tu.setTermEntryId(in.getUuId());
		    tu.setSourceTermUpdateCommands(removeCommands);

		    tusForDelete.add(tu);
		}

		// collect incoming languages
		languageIds.addAll(in.getLanguageTerms().keySet());

		// sync need to be called after tusForDelete creation otherwise
		// merged terms will be deleted
		synchronizer.synchronizeTermEntries(in, existing, options, importSummary);
	    }
	    existing.setAction(Action.MERGED);
	    existing.setUserModified(userProfile.getUserName());
	    updateRegularTermEntries(projectId, Action.MERGED.name(), new TransactionalUnit(singletonList(existing)));

	    // delete incoming term entries if they exist in data base
	    updateTermEntries(tusForDelete, null, projectId, Action.MERGED);

	    // set import languages in order to update project details properly
	    options.setImportLocales(new ArrayList<>(languageIds));

	    getImportTermService().updateProjectDetailOnImport(options, importSummary);
	    getStatisticsService().updateStatisticsOnImport(projectId, importSummary);
	} catch (Exception e) {
	    throw new RuntimeException(e.getMessage(), e);
	}
    }

    @Override
    public void notifyListeners(TmProject project, ProjectDetailInfo projectDetailInfo, Set<StatisticsInfo> statistics,
	    TermEntry termEntry, String status, List<UpdateCommand> updateCommands, String remoteUser) {
	for (UpdateCommand command : updateCommands) {
	    EventMessage message = EventMessage.createEvent(EventMessage.EVENT_UPDATE_TERMENTRY);

	    message.addContextVariable(EventMessage.VARIABLE_PROJECT, project);
	    message.addContextVariable(EventMessage.VARIABLE_DETAIL_INFO, projectDetailInfo);
	    message.addContextVariable(EventMessage.VARIABLE_PROJECT_ID, project.getProjectId());
	    message.addContextVariable(EventMessage.VARIABLE_COMMAND, command);
	    message.addContextVariable(EventMessage.VARIABLE_STATUS_TYPE, status);
	    message.addContextVariable(EventMessage.VARIABLE_TERM_ENTRY, termEntry);
	    message.addContextVariable(EventMessage.VARIABLE_USER_LATEST_CHANGE, Boolean.TRUE);
	    message.addContextVariable(EventMessage.VARIABLE_STATISTICS, statistics);
	    message.addContextVariable(EventMessage.VARIABLE_REMOTE_USER, remoteUser);

	    getNotificationListenerSupport().notifyListeners(message);
	}
    }

    @Override
    public void renameTermDescriptions(Long projectId, List<Attribute> attributes) {
	List<TermEntry> termEntries = findTermEntriesByProjectId(projectId);
	if (CollectionUtils.isEmpty(termEntries)) {
	    return;
	}

	final String userName = TmUserProfile.getCurrentUserName();
	final Date dateModified = new Date();

	List<TermEntry> updatedTermEntries = new ArrayList<>();
	for (TermEntry termEntry : termEntries) {
	    Set<Description> descriptions = termEntry.getDescriptions();
	    if (CollectionUtils.isEmpty(descriptions)) {
		continue;
	    }

	    for (Description description : descriptions) {
		for (Attribute attribute : attributes) {
		    String renameValue = attribute.getRenameValue();
		    if (StringUtils.isEmpty(renameValue)) {
			continue;
		    }
		    String type = description.getType();
		    if (type.equals(attribute.getName())) {
			LogHelper.debug(LOGGER, String.format(Messages.getString("TermEntryServiceImpl.16"), //$NON-NLS-1$
				type, renameValue));
			description.setType(renameValue);

			if (!updatedTermEntries.contains(termEntry)) {
			    termEntry.setAction(Action.EDITED);
			    termEntry.setDateModified(dateModified.getTime());
			    termEntry.setUserModified(userName);
			    updatedTermEntries.add(termEntry);
			}
		    }
		}
	    }
	}

	if (CollectionUtils.isNotEmpty(updatedTermEntries)) {
	    updateRegularTermEntries(projectId, updatedTermEntries);
	    // 31-May-2017, as per [Improvement#TERII-4225]:
	    getProjectDetailService().updateDateModifiedByProjectId(projectId, dateModified);
	}
    }

    @Override
    public FacetTermCounts searchFacetTermCounts(TmgrSearchFilter filter) {
	return getSearcher(getRegularCollection()).searchFacetTermCounts(filter);
    }

    @Override
    public List<TermEntry> searchSegmentTermEntries(TmgrSearchFilter filter) {
	return searchTermEntries(filter).getResults();
    }

    @Override
    public Page<TermEntry> searchTermEntries(TmgrSearchFilter filter) {
	return searchTermEntries(filter, getRegularCollection());
    }

    @Override
    public Page<TermEntry> searchTermEntries(TmgrSearchFilter filter, String collection) {
	try {
	    return getSearcher(collection).concordanceSearch(filter);
	} catch (TmException e) {
	    throw new RuntimeException(e.getMessage(), e);
	}
    }

    @Override
    public Page<TermEntry> segmentTMSearch(TmgrSearchFilter filter) {
	try {
	    return getSearcher(getRegularCollection()).segmentSearch(filter);
	} catch (TmException e) {
	    throw new RuntimeException(e.getMessage(), e);
	}

    }

    @Override
    public void updateRegularTermEntries(Long projectId, List<TermEntry> termEntries) {
	updateRegularTermEntries(projectId, Action.EDITED.name(), new TransactionalUnit(termEntries));
    }

    @Override
    public void updateSubmissionTermEntries(Long projectId, List<TermEntry> termEntries) {
	updateSubmissionTermEntries(projectId, Action.EDITED.name(), termEntries);
    }

    @Override
    public void updateTermEntries(List<TranslationUnit> translationUnits, String sourceLanguage, Long projectId,
	    Action action) {

	if (CollectionUtils.isEmpty(translationUnits)) {
	    return;
	}

	TmProject project = getProjectService().load(projectId);

	ProjectDetailInfo detailInfo = new ProjectDetailInfo(projectId);

	Set<StatisticsInfo> statisticsInfos = new HashSet<>();

	List<TermEntry> termEntries = new ArrayList<>();

	for (TranslationUnit translationUnit : translationUnits) {

	    createStatistictForTranslationUnits(translationUnit, statisticsInfos, projectId);

	    String termEntryId = translationUnit.getTermEntryId();
	    List<UpdateCommand> sourceTermUpdateCommands = translationUnit.getSourceTermUpdateCommands();
	    List<UpdateCommand> targetTermUpdateCommands = translationUnit.getTargetTermUpdateCommands();
	    if (CollectionUtils.isEmpty(sourceTermUpdateCommands)
		    && CollectionUtils.isEmpty(targetTermUpdateCommands)) {
		continue;
	    }

	    sortUpdateCommands(sourceTermUpdateCommands, targetTermUpdateCommands);

	    TermEntry termEntry = getTermEntry(project, termEntryId, action);

	    validateTermEntryBeforeRemove(termEntry, translationUnit);

	    String termEntryMarkerId = termEntry.getUuId();
	    TermEntryUtils.fillWithParentMarkerId(sourceTermUpdateCommands, termEntryMarkerId, termEntryId);
	    TermEntryUtils.fillWithParentMarkerId(targetTermUpdateCommands, termEntryMarkerId, termEntryId);
	    TermEntryUtils.fillWithLanguage(sourceTermUpdateCommands, sourceLanguage);

	    List<UpdateCommand> updateCommands = new ArrayList<>();
	    if (CollectionUtils.isNotEmpty(sourceTermUpdateCommands)) {
		updateCommands.addAll(sourceTermUpdateCommands);
	    }
	    if (CollectionUtils.isNotEmpty(targetTermUpdateCommands)) {
		updateCommands.addAll(targetTermUpdateCommands);
	    }

	    ItemStatusType status = ServiceUtils.decideTermStatus(project);

	    notifyListeners(project, detailInfo, statisticsInfos, termEntry, status.getName(), updateCommands,
		    translationUnit.getUsername());

	    termEntries.add(termEntry);
	}

	updateRegularTermEntries(projectId, action.name(),
		new TransactionalUnit(termEntries, detailInfo, statisticsInfos));
    }

    @Override
    public void updateTermEntriesLatestChanges(List<TermEntry> termEntries) {
	Long userLatestChange = TmUserProfile.getCurrentUserProfile().getUserProfileId();

	for (TermEntry termEntry : termEntries) {
	    for (Term term : termEntry.ggetTerms()) {
		term.setUserLatestChange(userLatestChange);
	    }
	}
	updateRegularTermEntriesInIndex(termEntries);
    }

    @Override
    public String updateTermEntry(TermEntry termEntry, List<UpdateCommand> updateCommands) {
	Long projectId = termEntry.getProjectId();
	TmProject project = getProjectService().load(projectId);

	ProjectDetailInfo detailInfo = new ProjectDetailInfo(projectId);

	ItemStatusType termStatus = ServiceUtils.decideTermStatus(project);

	Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();

	Set<StatisticsInfo> statistics = new HashSet<>();

	if (Objects.nonNull(languageTerms)) {
	    languageTerms.forEach((s, terms) -> createAndAddStatisticsInfo(termEntry.getProjectId(), s, statistics));
	}

	notifyListeners(project, detailInfo, statistics, termEntry, termStatus.getName(), updateCommands, null);

	updateRegularTermEntries(termEntry.getProjectId(), Action.EDITED.name(),
		new TransactionalUnit(singletonList(termEntry), detailInfo, statistics));

	return termEntry.getUuId();
    }

    @Override
    @Transactional
    public RepositoryTicket updateTermEntryResourceTrack(String termEntryId, String resourceTicket,
	    RepositoryItem repositoryItem) {
	List<TermEntryResourceTrack> resourceTracks = getTermEntryResourceTrackDAO().findAllByTermEntryId(termEntryId);

	for (TermEntryResourceTrack resourceTrack : resourceTracks) {
	    if (resourceTrack.getResourceId().equals(resourceTicket)) {

		getRepositoryManager().delete(new RepositoryTicket(resourceTicket));
		repositoryItem.setRepositoryPath(PathHelper.createRepositoryPath(termEntryId));

		RepositoryTicket repositoryTicket = getRepositoryManager().store(repositoryItem);

		resourceTrack.setResourceId(repositoryTicket.getTicket());
		resourceTrack.setResourceName(repositoryItem.getResourceInfo().getName());
		resourceTrack.setTrackType(repositoryItem.getResourceInfo().getMimeType());
		resourceTrack.setTermEntryId(termEntryId);

		getTermEntryResourceTrackDAO().update(resourceTrack);

		return repositoryTicket;
	    }
	}

	return null;
    }

    @Override
    @Transactional
    public RepositoryTicket uploadBinaryResource(String termEntryId, ResourceInfo resourceInfo, InputStream inputStream,
	    String attributeType) {

	RepositoryTicket repositoryTicket = storeBinaryResourceToRepository(resourceInfo, inputStream,
		PathHelper.createRepositoryPath(termEntryId));
	try {
	    inputStream.close();
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}

	TermEntryResourceTrack resourceTrack = new TermEntryResourceTrack();
	resourceTrack.setResourceId(repositoryTicket.getTicket());
	resourceTrack.setResourceName(resourceInfo.getName());
	resourceTrack.setTrackType(resourceInfo.getMimeType());
	resourceTrack.setTaskName(attributeType);
	resourceTrack.setTermEntryId(termEntryId);

	getTermEntryResourceTrackDAO().save(resourceTrack);

	return repositoryTicket;
    }

    private void cancelExport(ExportNotificationCallback notificationCallback, ExportInfo exportInfo) {
	exportInfo.setProcessingCanceled(true);
	notificationCallback.notifyProcessingFinished(exportInfo);
	getHzEntryProcessorExecutor().executeOnKey(CacheName.EXPORT_PROGRESS_STATUS,
		notificationCallback.getThreadName(), new NotifyProcessingFinishedEntryProcessor(exportInfo));

    }

    private void collectExportInfoData(ExportInfo exportInfo, TermEntrySearchRequest searchRequest,
	    String exportFormat) {
	TmProject project = getProjectService().load(searchRequest.getProjectId());
	exportInfo.setProjectName(project.getProjectInfo().getName());
	exportInfo.setShortCode(project.getProjectInfo().getShortCode());

	DateRange dateCreatedRange = new DateRange();
	Date dateCreatedFrom = searchRequest.getDateCreatedFrom();
	dateCreatedRange.setFromDate(dateCreatedFrom == null ? null : dateCreatedFrom.getTime());
	Date dateCreatedTo = searchRequest.getDateCreatedTo();
	dateCreatedRange.setToDate(dateCreatedTo == null ? null : dateCreatedTo.getTime());

	DateRange dateModifiedRange = new DateRange();
	Date dateModifiedFrom = searchRequest.getDateModifiedFrom();
	dateModifiedRange.setFromDate(dateModifiedFrom == null ? null : dateModifiedFrom.getTime());
	Date dateModifiedTo = searchRequest.getDateModifiedTo();
	dateModifiedRange.setToDate(dateModifiedTo == null ? null : dateModifiedTo.getTime());

	exportInfo.setCreationDateRange(dateCreatedRange);
	exportInfo.setModificationDateRange(dateModifiedRange);

	List<String> languageIds = searchRequest.getLanguagesToExport();

	if (CollectionUtils.isNotEmpty(languageIds)) {
	    Set<String> languageDisplayNames = new HashSet<>();
	    for (String languageId : languageIds) {
		languageDisplayNames.add(Language.valueOf(languageId).getDisplayName());
	    }
	    exportInfo.setLanguagesToExport(languageDisplayNames);
	}

	exportInfo.setLanguageIds(languageIds);

	List<String> descriptionsToExport = new ArrayList<>();

	List<String> termAttributesToExport = searchRequest.getTermAttributes();
	if (CollectionUtils.isNotEmpty(termAttributesToExport)) {
	    descriptionsToExport.addAll(termAttributesToExport);
	}

	// merging notes and attributes
	List<String> notesToExport = searchRequest.getTermNotes();
	if (CollectionUtils.isNotEmpty(notesToExport)) {
	    descriptionsToExport.addAll(notesToExport);
	}

	List<String> termEntriesAttributesToExport = searchRequest.getTermEntriesAttributes();
	if (CollectionUtils.isNotEmpty(termEntriesAttributesToExport)) {
	    descriptionsToExport.addAll(termEntriesAttributesToExport);
	}

	exportInfo.setDescriptionsToExport(descriptionsToExport);

	Set<Description> descriptions = new HashSet<>();

	Set<Description> attributes = searchRequest.getDescriptions();
	if (CollectionUtils.isNotEmpty(attributes)) {
	    for (Description attribute : attributes) {
		attribute.setBaseType(Description.ATTRIBUTE);
		descriptions.add(attribute);
	    }
	}

	Set<Description> notes = searchRequest.getNotes();
	if (CollectionUtils.isNotEmpty(notes)) {
	    for (Description note : notes) {
		note.setBaseType(Description.NOTE);
		descriptions.add(note);
	    }
	}

	exportInfo.setDescriptionFilter(descriptions);

	exportInfo.setExportFormat(exportFormat);
	exportInfo.setExportTermType(searchRequest.getStatusRequestList());

	String sourceLocale = searchRequest.getSourceLocale();
	exportInfo.setLanguageCriteriaEnum(
		ExportLanguageCriteriaEnum.getInstance(sourceLocale, searchRequest.getTargetLocales()));

	exportInfo.setSourceLanguage(Language.valueOf(sourceLocale).getDisplayName());

	List<String> displayNames = new ArrayList<>();
	List<String> targetLocales = searchRequest.getTargetLocales();
	if (CollectionUtils.isNotEmpty(targetLocales)) {
	    for (String target : targetLocales) {
		displayNames.add(Language.valueOf(target).getDisplayName());
	    }
	}

	exportInfo.setTargetLanguages(displayNames);
    }

    private void createAndAddStatisticsInfo(Long projectId, String languageId, Set<StatisticsInfo> statisticsInfos) {
	if (StringUtils.isEmpty(languageId)) {
	    return;
	}
	StatisticsInfo statisticsInfo = new StatisticsInfo(projectId, languageId);
	statisticsInfos.add(statisticsInfo);
    }

    private void createAndAddStatisticsInfo(UpdateCommand updateCommand, Set<StatisticsInfo> statisticsInfos,
	    Long projectId) {
	StatisticsInfo statisticsInfo = statisticsInfos.stream()
		.filter(st -> st.getProjectId().equals(updateCommand.getProjectId())
			&& st.getLanguageId().equals(updateCommand.getLanguageId()))
		.findFirst().orElse(null);
	if (Objects.isNull(statisticsInfo)) {
	    createAndAddStatisticsInfo(projectId, updateCommand.getLanguageId(), statisticsInfos);
	}

    }

    private List<TermEntry> createHistory(DbTermEntry dbTermEntry) {
	return getHistoryCreator().createHistory(dbTermEntry);
    }

    private void createStatistictForTranslationUnits(TranslationUnit translationUnit,
	    Set<StatisticsInfo> statisticsInfos, Long projectId) {

	if (Objects.isNull(translationUnit)) {
	    return;
	}

	List<UpdateCommand> stc = translationUnit.getSourceTermUpdateCommands();
	List<UpdateCommand> ttc = translationUnit.getTargetTermUpdateCommands();

	if (Objects.nonNull(stc)) {
	    stc.forEach(updateCommand -> createAndAddStatisticsInfo(updateCommand, statisticsInfos, projectId));
	}

	if (Objects.nonNull(ttc)) {
	    ttc.forEach(updateCommand -> createAndAddStatisticsInfo(updateCommand, statisticsInfos, projectId));
	}
    }

    private String generateTempFileName() {
	StringBuilder fileName = new StringBuilder();
	fileName.append("tmgr-"); //$NON-NLS-1$
	fileName.append(UUID.randomUUID().toString());
	fileName.append(".tmp"); //$NON-NLS-1$

	return fileName.toString();
    }

    private DbTermEntryService getDbTermEntryService() {
	return _dbTermEntryService;
    }

    private HistoryCreator getHistoryCreator() {
	return _historyCreator;
    }

    private HzEntryProcessorExecutor getHzEntryProcessorExecutor() {
	return _hzEntryProcessorExecutor;
    }

    private ImportTermService getImportTermService() {
	return _importTermService;
    }

    private RepositoryManager getRepositoryManager() {
	return _repositoryManager;
    }

    private int getSynonymNumber() {
	return _synonymNumber;
    }

    private TermEntry getTermEntry(TmProject project, String termEntryId, Action action) {
	Long projectId = project.getProjectId();

	if (StringUtils.isBlank(termEntryId)) {
	    TermEntry newTermEntry = TermEntryUtils.createEmptyTermEntry(project);
	    newTermEntry.setAction(Action.ADDED_REMOTELY == action ? Action.ADDED_REMOTELY : Action.ADDED);
	    return newTermEntry;
	}
	TermEntry termEntry = findTermEntryById(getRegularCollection(), termEntryId, projectId);
	if (termEntry == null) {
	    /*
	     * If term entry is still null after findById, create new one. This is the case
	     * when performing addTerm V2 web service action.
	     */
	    termEntry = TermEntryUtils.createEmptyTermEntry(project);
	}
	termEntry.setAction(action);
	return termEntry;
    }

    private TermEntryExporter getTermEntryExporter() {
	return _termEntryExporter;
    }

    private TermEntryResourceTrackDAO getTermEntryResourceTrackDAO() {
	return _termEntryResourceTrackDAO;
    }

    private ZipOutputStream makeZipFile(OutputStream zos, File XCStempFile, String XCSFileName, String fileName,
	    File tempFile) throws IOException {
	ZipOutputStream zipOutputStream = new ZipOutputStream(zos);
	zipOutputStream.putNextEntry(new ZipEntry(fileName));
	IOUtils.copy(new FileInputStream(tempFile), zipOutputStream);
	zipOutputStream.putNextEntry(new ZipEntry(XCSFileName));
	IOUtils.copy(new FileInputStream(XCStempFile), zipOutputStream);
	return zipOutputStream;
    }

    private List<Description> parseDescriptionsLine(String line) {
	List<Description> descriptions = new ArrayList<>();
	if (StringUtils.isNotBlank(line) && line.contains(SolrGlossaryAdapter.RS)
		&& line.contains(StringConstants.EQUAL)) {
	    String[] descs = line.split(SolrGlossaryAdapter.RS);
	    for (int i = 0; i < descs.length; i++) {
		String[] att = descs[0].split(StringConstants.EQUAL);
		descriptions.add(new Description(att[0], att[1]));
	    }
	}
	return descriptions;
    }

    private void removeTermEntryResourceTrack(String resourceId, TermEntry termEntry) {
	List<TermEntryResourceTrack> resourceTracksForRemoval = new ArrayList<>();

	List<TermEntryResourceTrack> termEntryResourceTracks = getTermEntryResourceTrackDAO()
		.findAllByTermEntryId(termEntry.getUuId());

	for (TermEntryResourceTrack track : termEntryResourceTracks) {
	    if (track.getResourceId().equals(resourceId)) {
		getRepositoryManager().delete(new RepositoryTicket(resourceId));
		resourceTracksForRemoval.add(track);
	    }
	}

	for (TermEntryResourceTrack resourceTrack : resourceTracksForRemoval) {
	    getTermEntryResourceTrackDAO().delete(resourceTrack);
	}

    }

    private void sortUpdateCommands(List<UpdateCommand> sourceTermUpdateCommands,
	    List<UpdateCommand> targetTermUpdateCommands) {
	ServiceUtils.sortUpdateCommandsByType(sourceTermUpdateCommands);
	ServiceUtils.sortUpdateCommands(sourceTermUpdateCommands);
	ServiceUtils.sortUpdateCommandsByType(targetTermUpdateCommands);
	ServiceUtils.sortUpdateCommands(targetTermUpdateCommands);
    }

    private RepositoryTicket storeBinaryResourceToRepository(ResourceInfo resourceInfo, InputStream inputStream,
	    RepositoryPath repositoryPath) {
	RepositoryItem repositoryItem = new RepositoryItem();
	repositoryItem.setRepositoryPath(repositoryPath);

	repositoryItem.setInputStream(inputStream);
	repositoryItem.setResourceInfo(resourceInfo);

	RepositoryTicket repositoryTicket = getRepositoryManager().store(repositoryItem);
	try {
	    inputStream.close();
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}

	return repositoryTicket;
    }

    private void validateByCommand(TermEntry termEntry, List<UpdateCommand> updateCommands) {

	for (UpdateCommand command : updateCommands) {
	    if (!CommandEnum.REMOVE.name().equals(command.getCommand())) {
		continue;
	    }

	    if (termEntry.getLanguageTerms().isEmpty()) {
		throw new UserException(String.format(Messages.getString("SaveDashboard.2")));
	    }

	    String languageId = command.getLanguageId();
	    Set<Term> terms = termEntry.getLanguageTerms().get(languageId);

	    if (CollectionUtils.isEmpty(terms)) {
		throw new UserException(String.format(Messages.getString("SaveDashboard.1"), languageId));
	    }
	}
    }

    private void validateTermEntryBeforeRemove(TermEntry termEntry, TranslationUnit translationUnit) {

	List<UpdateCommand> sourceTermUpdateCommands = translationUnit.getSourceTermUpdateCommands();
	List<UpdateCommand> targetTermUpdateCommands = translationUnit.getTargetTermUpdateCommands();

	if (CollectionUtils.isNotEmpty(sourceTermUpdateCommands)) {
	    validateByCommand(termEntry, sourceTermUpdateCommands);
	}

	if (CollectionUtils.isNotEmpty(targetTermUpdateCommands)) {
	    validateByCommand(termEntry, targetTermUpdateCommands);
	}

    }

    private static class InitTermEntryCountEntryProcessor extends AbstractEntryProcessor<String, ExportAdapter> {

	private static final long serialVersionUID = 9177944112375237873L;

	private final int _termEntryCount;

	public InitTermEntryCountEntryProcessor(int termEntryCount) {
	    _termEntryCount = termEntryCount;
	}

	@Override
	public Object process(Entry<String, ExportAdapter> entry) {
	    ExportAdapter exportAdapter = entry.getValue();
	    exportAdapter.init(_termEntryCount);
	    entry.setValue(exportAdapter);
	    return null;
	}
    }

    private static class NotifyProcessingFinishedEntryProcessor extends AbstractEntryProcessor<String, ExportAdapter> {

	private static final long serialVersionUID = 5678146478149470595L;

	private final ExportInfo _exportInfo;

	public NotifyProcessingFinishedEntryProcessor(ExportInfo exportInfo) {
	    _exportInfo = exportInfo;
	}

	public ExportInfo getExportInfo() {
	    return _exportInfo;
	}

	@Override
	public Object process(Entry<String, ExportAdapter> entry) {
	    ExportAdapter exportAdapter = entry.getValue();
	    exportAdapter.notifyProcessingFinished(getExportInfo());
	    entry.setValue(exportAdapter);
	    return null;
	}
    }
}