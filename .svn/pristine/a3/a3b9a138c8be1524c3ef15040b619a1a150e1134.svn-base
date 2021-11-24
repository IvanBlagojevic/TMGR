package org.gs4tr.termmanager.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.gs4tr.foundation.modules.entities.model.types.BaseItemStatusTypeHolder;
import org.gs4tr.foundation.modules.entities.model.types.ItemStatusType;
import org.gs4tr.termmanager.model.glossary.Term;

public class ItemStatusTypeHolder extends BaseItemStatusTypeHolder {

    public static final ItemStatusType BLACKLISTED = createTypeInstance("blacklisted");

    public static final ItemStatusType IN_FINAL_REVIEW = createTypeInstance("inFinalReview");

    public static final ItemStatusType IN_TRANSLATION_REVIEW = createTypeInstance("inTranslationReview");

    public static final ItemStatusType MISSING_TRANSLATION = createTypeInstance("missingTranslation");

    public static final ItemStatusType ON_HOLD = createTypeInstance("onHold");

    public static int getItemStatusTypeLevel(String status) {
	Integer level = getItemStatusTypeLevel().get(status);
	if (Objects.nonNull(level)) {
	    return level;
	} else {
	    return getItemStatusTypeLevel().size();
	}
    }

    public static String getStatusByDisplayName(String displayName) {
	return getDisplayNameMap().entrySet().stream().filter(e -> e.getValue().equals(displayName))
		.map(e -> e.getKey()).findFirst().orElse(null);

    }

    public static CaseInsensitiveMap getStatusConverterMap() {
	return _statusConverterMap;
    }

    public static String getStatusDisplayName(String status) {
	return getDisplayNameMap().get(status);
    }

    public static List<String> getStatusDisplayName(String... statuses) {
	List<String> statusDisplayNames = new ArrayList<>();
	if (Objects.nonNull(statuses)) {
	    for (String status : statuses) {
		statusDisplayNames.add(getDisplayNameMap().get(status));
	    }
	}
	return statusDisplayNames;
    }

    public static List<ItemStatusType> getTermStatusValues() {
	List<ItemStatusType> statusTypes = new ArrayList<>();
	statusTypes.add(PROCESSED);
	statusTypes.add(WAITING);
	statusTypes.add(MISSING_TRANSLATION);
	statusTypes.add(IN_FINAL_REVIEW);
	statusTypes.add(BLACKLISTED);
	statusTypes.add(IN_TRANSLATION_REVIEW);
	statusTypes.add(ON_HOLD);
	return statusTypes;
    }

    public static boolean isInWorkflow(Term term) {
	Boolean inTranslationAsSource = term.getInTranslationAsSource() != null ? term.getInTranslationAsSource()
		: Boolean.FALSE;
	return inTranslationAsSource || isTermInTranslation(term);
    }

    public static boolean isTermInFinalReview(Term term) {
	return IN_FINAL_REVIEW.getName().equals(term.getStatus());
    }

    public static boolean isTermInTranslation(Term term) {
	String status = term.getStatus();
	return IN_FINAL_REVIEW.getName().equals(status) || IN_TRANSLATION_REVIEW.getName().equals(status);
    }

    public static ItemStatusType valueOf(String name) {
	return BaseItemStatusTypeHolder.valueOf(name);
    }

    public static Collection<ItemStatusType> values() {
	return BaseItemStatusTypeHolder.values();
    }

    static {
	initialize();
    }

    private static Map<String, String> _displayNameMap;
    private static Map<String, Integer> _itemStatusTypeLevel;
    private static CaseInsensitiveMap _statusConverterMap;

    private static Map<String, String> createDisplayNameMap() {
	Map<String, String> displayNames = new HashMap<>();
	displayNames.put(PROCESSED.getName(), "Approved");
	displayNames.put(WAITING.getName(), "Pending Approval");
	displayNames.put(BLACKLISTED.getName(), "Blacklisted");
	displayNames.put(IN_FINAL_REVIEW.getName(), "In Final Review");
	displayNames.put(IN_TRANSLATION_REVIEW.getName(), "In Translation Review");
	displayNames.put(MISSING_TRANSLATION.getName(), "Missing Translation");
	displayNames.put(ON_HOLD.getName(), "On Hold");
	return displayNames;
    }

    private static CaseInsensitiveMap createStatusConverterMap() {
	Map<String, String> displayNameMap = getDisplayNameMap();
	CaseInsensitiveMap statusConverter = new CaseInsensitiveMap(MapUtils.invertMap(displayNameMap));

	statusConverter.remove(displayNameMap.get(MISSING_TRANSLATION.getName()));

	return statusConverter;
    }

    private static Map<String, Integer> createStatusTypesLevels() {
	Map<String, Integer> itemStatusTypeLevelHolder = new HashMap<>();
	itemStatusTypeLevelHolder.put(BLACKLISTED.getName(), 1);
	itemStatusTypeLevelHolder.put(PROCESSED.getName(), 2);
	itemStatusTypeLevelHolder.put(WAITING.getName(), 3);
	itemStatusTypeLevelHolder.put(ON_HOLD.getName(), 4);

	return itemStatusTypeLevelHolder;
    }

    private static Map<String, String> getDisplayNameMap() {
	return _displayNameMap;
    }

    private static Map<String, Integer> getItemStatusTypeLevel() {
	return _itemStatusTypeLevel;
    }

    private static void initialize() {
	Map<String, String> displayNames = createDisplayNameMap();
	setDisplayNameMap(displayNames);

	CaseInsensitiveMap statusConverterMap = createStatusConverterMap();
	setStatusConverterMap(statusConverterMap);

	Map<String, Integer> itemStatusTypesLvl = createStatusTypesLevels();
	setItemStatusTypeLevel(itemStatusTypesLvl);

	add(MISSING_TRANSLATION);
	add(IN_FINAL_REVIEW);
	add(BLACKLISTED);
	add(IN_TRANSLATION_REVIEW);
	add(IN_FINAL_REVIEW);
	add(ON_HOLD);
    }

    private static void setDisplayNameMap(Map<String, String> displayNameMap) {
	_displayNameMap = displayNameMap;
    }

    private static void setItemStatusTypeLevel(Map<String, Integer> itemStatusTypeLevel) {
	_itemStatusTypeLevel = itemStatusTypeLevel;
    }

    private static void setStatusConverterMap(CaseInsensitiveMap statusConverterMap) {
	_statusConverterMap = statusConverterMap;
    }
}