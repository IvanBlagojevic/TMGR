package org.gs4tr.termmanager.webmvc.controllers;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation.modules.configuration.ApplicationConfigurationFactory;
import org.gs4tr.foundation.modules.entities.model.Identifiable;
import org.gs4tr.foundation.modules.entities.model.PagedList;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.entities.model.SortDirection;
import org.gs4tr.foundation.modules.entities.model.StringConstants;
import org.gs4tr.foundation.modules.entities.model.TaskPagedList;
import org.gs4tr.foundation.modules.entities.model.types.EntityType;
import org.gs4tr.foundation.modules.webmvc.controllers.AbstractController;
import org.gs4tr.foundation.modules.webmvc.model.ModelMapResponse;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.feature.TmgrFeatureConstants;
import org.gs4tr.termmanager.service.TermService;
import org.gs4tr.termmanager.service.UserProfileService;
import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.gs4tr.termmanager.solr.plugin.utils.SolrDocHelper;
import org.gs4tr.termmanager.webmvc.configuration.FieldInfo;
import org.gs4tr.termmanager.webmvc.configuration.FolderViewConfiguration;
import org.gs4tr.termmanager.webmvc.model.commands.SearchCommand;
import org.gs4tr.termmanager.webmvc.model.commands.SearchGridCommand;
import org.gs4tr.termmanager.webmvc.model.search.InputTextAndComboItem;
import org.gs4tr.termmanager.webmvc.model.search.LinkedComboBoxDefaultValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public abstract class AbstractSearchGridController<T, G, A, B extends SearchGridCommand> extends AbstractController {

    private static final String DEFAULT_SORT_PROPERTY = "defaultSortProperty";

    private static final boolean GRID_FEATURE = Arrays.asList(TmgrFeatureConstants.getEnabledFeatures())
	    .contains(TmgrFeatureConstants.GRID_FEATURE);

    private static final String SOURCE_TERM_FIELD = "sourceTerm"; //$NON-NLS-1$

    private static final String TARGET_TERM_FIELD = "targetTerms."; //$NON-NLS-1$

    @Autowired
    private ApplicationConfigurationFactory _applicationConfigurationFactory;
    private final Class<G> _dtoEntityClazz;
    private final Class<T> _entityClazz;
    protected EntityType _view;

    protected AbstractSearchGridController(EntityType view, Class<T> entityClazz, Class<G> dtoEntityClazz) {
	_view = view;
	_entityClazz = entityClazz;
	_dtoEntityClazz = dtoEntityClazz;
    }

    public ApplicationConfigurationFactory getApplicationConfigurationFactory() {
	return _applicationConfigurationFactory;
    }

    @RequestMapping
    @ResponseBody
    public final ModelMapResponse handle(HttpServletRequest request, HttpServletResponse response,
	    @ModelAttribute B command) {
	ModelMapResponse mapResponse = handleInternal(request, response, command);
	return mapResponse;
    }

    @SuppressWarnings("unchecked")
    private PagedList<G> convertToDtoEntities(PagedList<T> entityPagedList,
	    List<org.gs4tr.termmanager.model.dto.Task> dtoUnionTasks) {
	T[] entities = entityPagedList.getElements();

	PagedList<G> dtoEntityPagedList = new PagedList<G>();

	if (entities != null && entities.length > 0) {
	    T firstEntity = entities[0];

	    if (!(firstEntity instanceof Identifiable<?>)) {
		throw new RuntimeException(String.format(Messages.getString("AbstractSearchGridController.0"), //$NON-NLS-1$
			firstEntity.getClass().getName()));
	    }

	    Map<String, Integer> dtoUnionTaskMap = getDtoTaskMap(dtoUnionTasks);

	    G[] dtoEntities = (G[]) Array.newInstance(getDtoEntityClazz(), entities.length);
	    for (int i = 0, size = entities.length; i < size; i++) {
		T entity = entities[i];

		G dtoEntity = createDtoEntityFromEntity(entity);
		setTaskHolderFields(dtoEntity, entity, dtoUnionTaskMap);

		dtoEntities[i] = dtoEntity;
	    }

	    dtoEntityPagedList.setElements(dtoEntities);
	}

	return dtoEntityPagedList;
    }

    private List<FieldMapping> createFieldMappings(List<FieldInfo> fieldInfos) {
	if (fieldInfos == null || fieldInfos.isEmpty()) {
	    return null;
	}

	List<FieldMapping> fieldMappings = new ArrayList<FieldMapping>();

	for (FieldInfo fieldInfo : fieldInfos) {
	    fieldMappings.add(new FieldMapping(fieldInfo.getName(), fieldInfo.getName()));
	}

	return fieldMappings;
    }

    private GridConfig createGridConfig(String fieldName, FieldInfo fieldInfo, Integer defaultFieldWidth) {
	GridConfig gridConfig = new GridConfig();

	gridConfig.setDataIndex(fieldName);

	if (fieldInfo != null) {
	    gridConfig.setWidth(fieldInfo.getWidth());
	    gridConfig.setHidden(fieldInfo.getHidden());
	    gridConfig.setHeader(fieldInfo.getTitleKey());
	    gridConfig.setSystemHidden(fieldInfo.getSystemHidden());
	    gridConfig.setSortable(fieldInfo.getSortable());
	    gridConfig.setSortProperty(fieldInfo.getSortProperty());
	} else {
	    gridConfig.setWidth(defaultFieldWidth);
	    gridConfig.setHidden(Boolean.TRUE);
	}

	return gridConfig;
    }

    private List<GridConfig> createGridConfigFromFieldInfos(List<FieldInfo> fieldInfos) {

	List<GridConfig> gridConfig = new ArrayList<GridConfig>();
	for (FieldInfo fieldInfo : fieldInfos) {
	    gridConfig.add(createGridConfig(fieldInfo.getName(), fieldInfo, null));
	}
	return gridConfig;
    }

    private List<FieldInfo> findGridConfigMetadata(String gridConfigKey, SearchCommand command) {
	TmUserProfile userProfile = TmUserProfile.getCurrentUserProfile();

	int defaultFieldWidth = getFolderViewConfiguration().getDefaultFieldWidth();

	String metadata = userProfile.getMetadataValue(gridConfigKey);

	String gridJSON = null;
	if (!StringUtils.isEmpty(metadata)) {
	    JsonNode configData = JsonUtils.readValue(metadata, JsonNode.class);
	    gridJSON = configData.get("gridConfig").toString();
	}

	List<FieldInfo> fieldInfos = null;

	if (!StringUtils.isEmpty(gridJSON)) {
	    ArrayNode metadataJsonArray = JsonUtils.readValue(gridJSON, ArrayNode.class);

	    if (metadataJsonArray.size() > 0) {
		fieldInfos = new ArrayList<FieldInfo>();
	    }

	    String sourceLanguage = null;
	    List<String> targetLanguages = null;

	    LinkedComboBoxDefaultValue languageDirection = command.getLanguageDirection();
	    if (languageDirection != null) {
		sourceLanguage = languageDirection.getValue1();
		targetLanguages = languageDirection.getValue2();
	    }

	    boolean defaultSourceConfig = false;
	    boolean defaultTargetConfig = false;

	    Set<String> targets = new HashSet<String>();

	    // source and target columns
	    for (JsonNode jsonNode : metadataJsonArray) {
		String fieldName = jsonNode.get("dataIndex").asText();
		if (StringUtils.isEmpty(fieldName)) {
		    throw new RuntimeException(String.format(Messages.getString("TargetSearchController.8"), jsonNode));
		}

		String titleKey = jsonNode.get("headerKey").asText();

		JsonNode hiddenNode = jsonNode.get("hidden");
		JsonNode systemHiddenNode = jsonNode.get("systemHidden");
		JsonNode widthNode = jsonNode.get("width");
		JsonNode sortableNode = jsonNode.get("sortable");

		Boolean hidden = hiddenNode != null ? Boolean.valueOf(hiddenNode.asBoolean()) : Boolean.TRUE;

		Boolean systemHidden = systemHiddenNode != null ? Boolean.valueOf(systemHiddenNode.asBoolean())
			: Boolean.TRUE;
		Integer width = widthNode != null ? widthNode.asInt() : defaultFieldWidth;

		boolean isTermOrAttributeSearch = false;
		if (this instanceof MultilingualViewSearchController) {
		    isTermOrAttributeSearch = isTermOrAttributeSearch(command);
		}
		Boolean sortable = sortableNode != null
			? Boolean.valueOf(sortableNode.asBoolean() && !isTermOrAttributeSearch)
			: Boolean.FALSE;

		String sortProperty = jsonNode.get("sortProperty").asText();

		if (fieldName.startsWith(SOURCE_TERM_FIELD) && !defaultSourceConfig) {
		    if (StringUtils.isNotEmpty(sortProperty) && sortProperty.contains(StringConstants.UNDERSCORE)) {
			String sourceLocale = sortProperty.split(StringConstants.UNDERSCORE)[0];
			if (!sourceLocale.equals(sourceLanguage)) {
			    fieldInfos.addAll(
				    getFolderViewConfiguration().getSourceFieldInfo(gridConfigKey, sourceLanguage));
			    defaultSourceConfig = true;
			    continue;
			}
		    }
		} else if (fieldName.startsWith(TARGET_TERM_FIELD)) {
		    String[] split = fieldName.split(TARGET_TERM_FIELD);
		    String code = split[1].substring(0, split[1].indexOf(StringConstants.DOT));

		    String targetLocale = Locale.makeLocale(code).getCode();
		    targets.add(targetLocale);

		    if (!targetLanguages.contains(targetLocale)) {
			defaultTargetConfig = true;
			continue;
		    }
		} else {
		    continue;
		}

		FieldInfo fieldInfo = new FieldInfo(fieldName, width, hidden, titleKey, systemHidden, sortable,
			sortProperty);
		fieldInfos.add(fieldInfo);
	    }

	    if (targetLanguages != null) {
		targetLanguages.removeAll(targets);
		if (!targetLanguages.isEmpty()) {
		    defaultTargetConfig = true;
		}
	    }

	    if (fieldInfos != null && defaultTargetConfig) {
		fieldInfos.addAll(getFolderViewConfiguration().getTargetFieldInfo(gridConfigKey, targetLanguages));
	    }

	    // other columns
	    for (JsonNode jsonNode : metadataJsonArray) {
		String fieldName = jsonNode.get("dataIndex").asText();
		if (StringUtils.isEmpty(fieldName)) {
		    throw new RuntimeException(String.format(Messages.getString("TargetSearchController.8"), jsonNode));
		}

		String titleKey = jsonNode.get("headerKey").asText();

		JsonNode hiddenNode = jsonNode.get("hidden");
		JsonNode systemHiddenNode = jsonNode.get("systemHidden");
		JsonNode widthNode = jsonNode.get("width");
		JsonNode sortableNode = jsonNode.get("sortable");

		Boolean hidden = hiddenNode != null ? Boolean.valueOf(hiddenNode.asBoolean()) : Boolean.TRUE;
		Boolean systemHidden = systemHiddenNode != null ? Boolean.valueOf(systemHiddenNode.asBoolean())
			: Boolean.TRUE;
		Integer width = widthNode != null ? widthNode.asInt() : defaultFieldWidth;

		boolean isTermOrAttributeSearch = false;
		if (this instanceof MultilingualViewSearchController) {
		    isTermOrAttributeSearch = isTermOrAttributeSearch(command);
		}
		Boolean sortable = sortableNode != null
			? Boolean.valueOf(sortableNode.asBoolean() && !isTermOrAttributeSearch)
			: Boolean.FALSE;

		String sortProperty = jsonNode.get("sortProperty").asText();

		if (fieldName.startsWith(SOURCE_TERM_FIELD) || fieldName.startsWith(TARGET_TERM_FIELD)) {
		    continue;
		}

		FieldInfo fieldInfo = new FieldInfo(fieldName, width, hidden, titleKey, systemHidden, sortable,
			sortProperty);

		fieldInfos.add(fieldInfo);
	    }
	}

	return fieldInfos;
    }

    private Map<String, Integer> getDtoTaskMap(List<org.gs4tr.termmanager.model.dto.Task> dtoTasks) {
	Map<String, Integer> dtoTaskMap = new LinkedHashMap<String, Integer>();

	for (org.gs4tr.termmanager.model.dto.Task dtoTask : dtoTasks) {
	    dtoTaskMap.put(dtoTask.getTaskName(), dtoTask.getTaskId());
	}

	return dtoTaskMap;
    }

    private List<FieldInfo> getFieldInfos(String gridConfigKey, SearchCommand command) {
	FolderViewConfiguration folderViewConfiguration = getFolderViewConfiguration();

	return folderViewConfiguration.getFieldInfo(gridConfigKey, command);
    }

    private FolderViewConfiguration getFolderViewConfiguration() {
	return getApplicationConfigurationFactory()
		.createApplicationConfiguration(FolderViewConfiguration.class.getName());
    }

    private boolean isTermOrAttributeSearch(SearchCommand command) {
	InputTextAndComboItem inputTextAndComboItem = command.getTermNameAndSearchType();
	return Objects.nonNull(inputTextAndComboItem);
    }

    private SortDirection resolveSortDirection(SearchCommand command, String asceding) {
	SortDirection sortDirection = ControllerConstants.DEFAULT_SORT_DIRECTION;

	if (command.getAscending() != null) {
	    sortDirection = command.getAscending() ? SortDirection.ASCENDING : SortDirection.DESCENDING;
	} else if (asceding != null) {
	    Boolean ascedingFlag = Boolean.valueOf(asceding);
	    sortDirection = ascedingFlag ? SortDirection.ASCENDING : SortDirection.DESCENDING;
	}

	return sortDirection;
    }

    private String resolveSortProperty(SearchCommand command, String gridConfigKey, String sortProperty) {
	if (StringUtils.isNotEmpty(command.getSortProperty())) {
	    sortProperty = command.getSortProperty();
	} else if (StringUtils.isNotEmpty(sortProperty)) {
	    // return sortProperty as it is
	} else {
	    List<FieldInfo> fieldInfos = getFieldInfos(gridConfigKey, command);
	    for (FieldInfo fieldInfo : fieldInfos) {
		if (fieldInfo.getName().equals(DEFAULT_SORT_PROPERTY)) {
		    sortProperty = fieldInfo.getSortProperty();

		    LinkedComboBoxDefaultValue languageDirection = command.getLanguageDirection();
		    if (languageDirection != null) {
			sortProperty = SolrDocHelper.createDynamicFieldName(languageDirection.getValue1(),
				sortProperty);
		    }
		    break;
		}
	    }
	}

	return sortProperty;
    }

    protected abstract G createDtoEntityFromEntity(T entity);

    protected abstract String createGridConfigKey(B searchGridCommand);

    protected GridContentInfo createGridContentInfo(PagedList<T> targetPagedList) {
	GridContentInfo gridContent = new GridContentInfo();

	gridContent.setTotalCount(targetPagedList.getTotalCount());
	gridContent.setHasNext(targetPagedList.getHasNext());
	gridContent.setHasPrevious(targetPagedList.getHasPrevious());
	gridContent.setPageIndexes(targetPagedList.getPageIndexes());
	gridContent.setTotalPageCount(targetPagedList.getTotalPageCount());
	gridContent.setPagedListInfo(targetPagedList.getPagedListInfo());

	return gridContent;
    }

    protected PagedListInfo createPagedListInfoFromCommand(SearchCommand command, String gridConfigKey) {
	TmUserProfile userProfile = TmUserProfile.getCurrentUserProfile();

	PagedListInfo pagedListInfo = new PagedListInfo();

	pagedListInfo
		.setIndex(command.getIndex() != null ? command.getIndex() : ControllerConstants.DEFAULT_PAGE_INDEX);

	pagedListInfo.setSize(command.getSize() != null ? command.getSize() : ControllerConstants.DEFAULT_PAGE_SIZE);

	String metadata = userProfile.getMetadataValue(gridConfigKey);
	String sortProperty = null;
	String asceding = null;
	if (!StringUtils.isEmpty(metadata)) {
	    JsonNode configData = JsonUtils.readValue(metadata, JsonNode.class);
	    JsonNode sortPropertyNode = configData.get(ControllerConstants.SORT_PROPERTY_KEY);
	    JsonNode ascedingNode = configData.get(ControllerConstants.ASCENDING_KEY);

	    if (sortPropertyNode != null) {
		sortProperty = sortPropertyNode.asText();
	    }

	    if (ascedingNode != null) {
		asceding = ascedingNode.asText();
	    }
	}

	// resolve sort direction
	SortDirection sortDirection = resolveSortDirection(command, asceding);
	pagedListInfo.setSortDirection(sortDirection);

	// resolve sort property
	sortProperty = resolveSortProperty(command, gridConfigKey, sortProperty);
	pagedListInfo.setSortProperty(sortProperty);

	return pagedListInfo;
    }

    protected abstract A createSearchRequestFromSearchCommand(B command);

    protected Class<G> getDtoEntityClazz() {
	return _dtoEntityClazz;
    }

    protected String getGroupFieldName() {
	return null;
    }

    protected EntityType getView() {
	return _view;
    }

    protected ModelMapResponse handleInternal(HttpServletRequest request, HttpServletResponse response, B command) {
	ModelMapResponse mapResponse = new ModelMapResponse();

	B searchGridCommand = command;

	String gridConfigKey = createGridConfigKey(searchGridCommand);

	SearchCommand searchCommand = (SearchCommand) command;

	if (GRID_FEATURE) {
	    setCommandFields(searchCommand);
	}

	PagedListInfo pagedListInfo = createPagedListInfoFromCommand(searchCommand, gridConfigKey);

	A searchRequest = createSearchRequestFromSearchCommand(searchGridCommand);

	TaskPagedList<T> entityPagedList = search(searchRequest, searchGridCommand, pagedListInfo);

	List<org.gs4tr.termmanager.model.dto.Task> dtoUnionTasks = ControllerUtils.getDtoTaskUnion(entityPagedList);

	PagedList<G> dtoEntityPagedList = convertToDtoEntities(entityPagedList, dtoUnionTasks);

	dtoEntityPagedList.setTotalCount(entityPagedList.getTotalCount());
	dtoEntityPagedList.setPagedListInfo(entityPagedList.getPagedListInfo());

	List<FieldInfo> fieldInfos = null;

	List<GridConfig> gridConfig = null;

	List<FieldInfo> findInfoList = findGridConfigMetadata(gridConfigKey, searchCommand);
	if (gridConfigKey != null) {
	    fieldInfos = findInfoList;
	    if (fieldInfos == null) {
		fieldInfos = getFieldInfos(gridConfigKey, searchCommand);
	    }
	    gridConfig = createGridConfigFromFieldInfos(fieldInfos);
	}

	mapResponse.put("gridConfig", gridConfig); //$NON-NLS-1$

	mapResponse.put("items", dtoEntityPagedList.getElements()); //$NON-NLS-1$

	GridContentInfo gridContentInfo = createGridContentInfo(entityPagedList);

	mapResponse.put("gridContentInfo", //$NON-NLS-1$
		gridContentInfo);

	mapResponse.put("readerColumns", createFieldMappings(fieldInfos)); //$NON-NLS-1$

	mapResponse.put("readerConfig", new ReaderConfig()); //$NON-NLS-1$

	mapResponse.put("tasks", dtoUnionTasks); //$NON-NLS-1$

	// mapResponse.put("searchBar", searchBarValues(searchGridCommand));

	mapResponse.put("gridConfigFromMetadata", findInfoList != null);

	String groupField = getGroupFieldName();

	if (!StringUtils.isEmpty(groupField)) {
	    mapResponse.put("groupField", groupField);
	}

	mapResponse.put("allProjectsSearch", isAllProjectsSearch(searchGridCommand));

	return mapResponse;
    }

    protected boolean isAllProjectsSearch(B command) {
	return false;
    }

    protected abstract TaskPagedList<T> search(A searchRequest, B command, PagedListInfo pagedListInfo);

    protected abstract void setCommandFields(SearchCommand command);

    protected void setTaskHolderFields(G dtoEntity, T entity, Map<String, Integer> dtoUnionTaskMap) {
    }

    protected void updateUserLatestChangedTerms(UserProfileService userProfileService, TermService termService,
	    List<Long> projectIds) {
	TmUserProfile userProfile = userProfileService
		.findById(TmUserProfile.getCurrentUserProfile().getUserProfileId());
	Boolean hasChangedTerms = userProfile.getHasChangedTerms();
	if (hasChangedTerms != null && hasChangedTerms) {
	    userProfileService.updateHasChangedTerms(Boolean.FALSE);
	    Long userId = userProfile.getUserProfileId();
	    termService.updateUserLatestChangedTerms(userId, projectIds);
	}
    }

    public class ReaderConfig {
	private final String _id = "ticket"; //$NON-NLS-1$

	private final String _root = "items"; //$NON-NLS-1$

	private final String _totalProperty = "gridContentInfo.totalCount"; //$NON-NLS-1$

	public String getId() {
	    return _id;
	}

	public String getRoot() {
	    return _root;
	}

	public String getTotalProperty() {
	    return _totalProperty;
	}
    }
}