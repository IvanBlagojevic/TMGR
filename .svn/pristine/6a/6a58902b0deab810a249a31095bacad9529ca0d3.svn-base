package org.gs4tr.termmanager.webmvc.configuration;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.Validate;
import org.gs4tr.foundation.modules.configuration.BaseApplicationConfiguration;
import org.gs4tr.foundation.modules.entities.model.StringConstants;
import org.gs4tr.foundation.modules.entities.model.types.EntityType;
import org.gs4tr.termmanager.model.EntityTypeHolder;
import org.gs4tr.termmanager.model.dto.converter.MultilingualTermConverter;
import org.gs4tr.termmanager.model.search.ItemFolderEnum;
import org.gs4tr.termmanager.solr.plugin.utils.SolrDocHelper;
import org.gs4tr.termmanager.webmvc.model.commands.SearchCommand;
import org.gs4tr.termmanager.webmvc.model.search.LinkedComboBoxDefaultValue;

public class FolderViewConfiguration extends BaseApplicationConfiguration {

    private static final String DEFAULT_FIELD_WIDTH = "preferences.defaultFieldWidth"; //$NON-NLS-1$

    private static final String DEFAULT_VIEW = "preferences.defaultView"; //$NON-NLS-1$

    private static final String FIELD_EXTENDS_FORMAT = "{0}[@extends]"; //$NON-NLS-1$

    private static final String FIELD_INFO_HIDDEN = "{0}.field.hidden";

    private static final String FIELD_INFO_NAME = "{0}.field.name"; //$NON-NLS-1$

    private static final String FIELD_INFO_SORTABLE = "{0}.field.sortable"; //$NON-NLS-1$

    private static final String FIELD_INFO_SORT_PROPERTY = "{0}.field.sortProperty"; //$NON-NLS-1$

    private static final String FIELD_INFO_SYSTEM_HIDDEN = "{0}.field.systemHidden";

    private static final String FIELD_INFO_TITLEKEY = "{0}.field.titleKey";

    private static final String FIELD_INFO_WIDTH = "{0}.field.width"; //$NON-NLS-1$

    private static final String ITEMS_PER_PAGE = "preferences.itemsPerPage"; //$NON-NLS-1$

    private static final String PREFERENCES_ACTION_SIZE = "preferences.actionSize"; //$NON-NLS-1$

    private static final String PREFERENCES_ACTION_TEXT_VISIBLE = "preferences.actionTextVisible"; //$NON-NLS-1$

    private static final String REFRESH_PERIOD = "preferences.refreshPeriod"; //$NON-NLS-1$

    private static final int[] SUBMISSION_TERM_LIST_SOURCE_COLUMNS = { 0, 1, 2, 3 };

    private static final int[] SUBMISSION_TERM_LIST_TARGET_COLUMNS = { 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14 };

    private static final String TARGET_TERMS = "targetTerms."; //$NON-NLS-1$

    private static final int[] TERM_LIST_SOURCE_COLUMNS = { 0, 1, 2, 3, 4, 5 };

    private static final int[] TERM_LIST_TARGET_COLUMNS = { 6, 7, 8, 9, 10, 11 };

    private static final String TIME_ZONE = "preferences.timezone"; //$NON-NLS-1$

    private static final long serialVersionUID = 9055056588520024350L;

    public String getActionSize() {
	return getConfigurationItem(PREFERENCES_ACTION_SIZE);
    }

    public Boolean getActionTextVisible() {
	return Boolean.valueOf(getConfigurationItem(PREFERENCES_ACTION_TEXT_VISIBLE));
    }

    public int getDefaultFieldWidth() {
	return Integer.parseInt(getConfigurationItem(DEFAULT_FIELD_WIDTH));
    }

    public EntityType getDefaultView() {
	return EntityTypeHolder.valueOf(getConfigurationItem(DEFAULT_VIEW));
    }

    public List<FieldInfo> getFieldInfo(String gridConfigKey, SearchCommand command) {
	Validate.notNull(gridConfigKey);

	String[] names = getConfigurationItems(FIELD_INFO_NAME, new String[] { gridConfigKey });

	String[] widths = getConfigurationItems(FIELD_INFO_WIDTH, new String[] { gridConfigKey });

	String[] titleKeys = getConfigurationItems(FIELD_INFO_TITLEKEY, new String[] { gridConfigKey });

	String[] isHidden = getConfigurationItems(FIELD_INFO_HIDDEN, new String[] { gridConfigKey });

	String[] isSystemHidden = getConfigurationItems(FIELD_INFO_SYSTEM_HIDDEN, new String[] { gridConfigKey });

	String[] isSortable = getConfigurationItems(FIELD_INFO_SORTABLE, new String[] { gridConfigKey });

	String[] sortProperties = getConfigurationItems(FIELD_INFO_SORT_PROPERTY, new String[] { gridConfigKey });

	Validate.isTrue(names.length == widths.length, Messages.getString("FolderViewConfiguration.7")); //$NON-NLS-1$

	List<FieldInfo> fieldInfos = new ArrayList<FieldInfo>();

	String sourceLanguage = null;
	List<String> targetLanguages = null;

	LinkedComboBoxDefaultValue languageDirection = command.getLanguageDirection();
	if (languageDirection != null) {
	    sourceLanguage = languageDirection.getValue1();
	    targetLanguages = languageDirection.getValue2();
	}

	// source fieldInfos
	for (int i = 0, size = names.length; i < size; i++) {
	    String sortProperty = sortProperties[i];
	    Boolean sortable = Boolean.valueOf(isSortable[i]);
	    boolean isSource = createSourceColumns(gridConfigKey, i);
	    if (isSource) {
		sortProperty = sortable ? SolrDocHelper.createDynamicFieldName(sourceLanguage, sortProperties[i])
			: sortProperty;
		fieldInfos.add(
			new FieldInfo(names[i], Integer.parseInt(widths[i]), Boolean.valueOf(isHidden[i]), titleKeys[i],
				Boolean.valueOf(isSystemHidden[i]), Boolean.valueOf(isSortable[i]), sortProperty));
	    }
	}

	// target fieldInfos
	if (CollectionUtils.isNotEmpty(targetLanguages)) {
	    for (String targetLanguage : targetLanguages) {
		for (int i = 0, size = names.length; i < size; i++) {
		    String sortProperty = sortProperties[i];
		    Boolean sortable = Boolean.valueOf(isSortable[i]);
		    boolean isTarget = createTargetColumns(gridConfigKey, i);
		    if (isTarget) {
			sortProperty = sortable
				? SolrDocHelper.createDynamicFieldName(targetLanguage, sortProperties[i])
				: sortProperty;
			fieldInfos.add(new FieldInfo(createColumnName(TARGET_TERMS, targetLanguage) + names[i],
				Integer.parseInt(widths[i]), Boolean.valueOf(isHidden[i]), titleKeys[i],
				Boolean.valueOf(isSystemHidden[i]), sortable, sortProperty));
		    }
		}
	    }
	}

	// other fieldInfos
	for (int i = 0, size = names.length; i < size; i++) {
	    String sortProperty = sortProperties[i];
	    boolean isSource = createSourceColumns(gridConfigKey, i);
	    boolean isTarget = createTargetColumns(gridConfigKey, i);
	    if (!isSource && !isTarget) {
		fieldInfos.add(
			new FieldInfo(names[i], Integer.parseInt(widths[i]), Boolean.valueOf(isHidden[i]), titleKeys[i],
				Boolean.valueOf(isSystemHidden[i]), Boolean.valueOf(isSortable[i]), sortProperty));
	    }
	}

	return fieldInfos;
    }

    public int getItemsPerPage() {
	return Integer.parseInt(getConfigurationItem(ITEMS_PER_PAGE));
    }

    public int getRefreshPeriod() {
	return Integer.parseInt(getConfigurationItem(REFRESH_PERIOD));
    }

    public List<FieldInfo> getSourceFieldInfo(String gridConfigKey, String sourceLanguageId) {
	Validate.notNull(gridConfigKey);

	String[] names = getConfigurationItems(FIELD_INFO_NAME, new String[] { gridConfigKey });

	String[] widths = getConfigurationItems(FIELD_INFO_WIDTH, new String[] { gridConfigKey });

	String[] titleKeys = getConfigurationItems(FIELD_INFO_TITLEKEY, new String[] { gridConfigKey });

	String[] isHidden = getConfigurationItems(FIELD_INFO_HIDDEN, new String[] { gridConfigKey });

	String[] isSystemHidden = getConfigurationItems(FIELD_INFO_SYSTEM_HIDDEN, new String[] { gridConfigKey });

	String[] isSortable = getConfigurationItems(FIELD_INFO_SORTABLE, new String[] { gridConfigKey });

	String[] sortProperties = getConfigurationItems(FIELD_INFO_SORT_PROPERTY, new String[] { gridConfigKey });

	Validate.isTrue(names.length == widths.length, Messages.getString("FolderViewConfiguration.7")); //$NON-NLS-1$

	List<FieldInfo> fieldInfos = new ArrayList<FieldInfo>();

	// source fieldInfos
	for (int i = 0, size = names.length; i < size; i++) {
	    String sortProperty = sortProperties[i];
	    Boolean sortable = Boolean.valueOf(isSortable[i]);
	    boolean isSource = createSourceColumns(gridConfigKey, i);
	    if (isSource) {
		sortProperty = sortable ? SolrDocHelper.createDynamicFieldName(sourceLanguageId, sortProperties[i])
			: sortProperty;
		fieldInfos.add(
			new FieldInfo(names[i], Integer.parseInt(widths[i]), Boolean.valueOf(isHidden[i]), titleKeys[i],
				Boolean.valueOf(isSystemHidden[i]), Boolean.valueOf(isSortable[i]), sortProperty));
	    }
	}

	return fieldInfos;
    }

    public List<FieldInfo> getTargetFieldInfo(String gridConfigKey, List<String> targetLanguages) {
	Validate.notNull(gridConfigKey);

	String[] names = getConfigurationItems(FIELD_INFO_NAME, new String[] { gridConfigKey });

	String[] widths = getConfigurationItems(FIELD_INFO_WIDTH, new String[] { gridConfigKey });

	String[] titleKeys = getConfigurationItems(FIELD_INFO_TITLEKEY, new String[] { gridConfigKey });

	String[] isHidden = getConfigurationItems(FIELD_INFO_HIDDEN, new String[] { gridConfigKey });

	String[] isSystemHidden = getConfigurationItems(FIELD_INFO_SYSTEM_HIDDEN, new String[] { gridConfigKey });

	String[] isSortable = getConfigurationItems(FIELD_INFO_SORTABLE, new String[] { gridConfigKey });

	String[] sortProperties = getConfigurationItems(FIELD_INFO_SORT_PROPERTY, new String[] { gridConfigKey });

	Validate.isTrue(names.length == widths.length, Messages.getString("FolderViewConfiguration.7")); //$NON-NLS-1$

	List<FieldInfo> fieldInfos = new ArrayList<FieldInfo>();

	// target fieldInfos
	if (CollectionUtils.isNotEmpty(targetLanguages)) {
	    for (String targetLanguage : targetLanguages) {
		for (int i = 0, size = names.length; i < size; i++) {
		    String sortProperty = sortProperties[i];
		    Boolean sortable = Boolean.valueOf(isSortable[i]);
		    boolean isTarget = createTargetColumns(gridConfigKey, i);
		    if (isTarget) {
			sortProperty = sortable
				? SolrDocHelper.createDynamicFieldName(targetLanguage, sortProperties[i])
				: sortProperty;
			fieldInfos.add(new FieldInfo(createColumnName(TARGET_TERMS, targetLanguage) + names[i],
				Integer.parseInt(widths[i]), Boolean.valueOf(isHidden[i]), titleKeys[i],
				Boolean.valueOf(isSystemHidden[i]), sortable, sortProperty));
		    }
		}
	    }
	}

	return fieldInfos;
    }

    public String getTimezone() {
	return getConfigurationItem(TIME_ZONE);
    }

    private String createColumnName(String prefix, String languageId) {
	return prefix.concat(MultilingualTermConverter.replaceLocaleCodeDash(languageId).concat(StringConstants.DOT));
    }

    private boolean createSourceColumns(String gridConfigKey, int cursor) {
	if (gridConfigKey.equals(ItemFolderEnum.TERM_LIST.name().toLowerCase())
		&& ArrayUtils.contains(TERM_LIST_SOURCE_COLUMNS, cursor)) {
	    return true;
	} else
	    return gridConfigKey.equals(ItemFolderEnum.SUBMISSIONTERMLIST.name().toLowerCase())
		    && ArrayUtils.contains(SUBMISSION_TERM_LIST_SOURCE_COLUMNS, cursor);
    }

    private boolean createTargetColumns(String gridConfigKey, int cursor) {
	if (gridConfigKey.equals(ItemFolderEnum.TERM_LIST.name().toLowerCase())
		&& ArrayUtils.contains(TERM_LIST_TARGET_COLUMNS, cursor)) {
	    return true;
	} else
	    return gridConfigKey.equals(ItemFolderEnum.SUBMISSIONTERMLIST.name().toLowerCase())
		    && ArrayUtils.contains(SUBMISSION_TERM_LIST_TARGET_COLUMNS, cursor);
    }

    @Override
    protected String getExtendsFormat() {
	return FIELD_EXTENDS_FORMAT;
    }
}
