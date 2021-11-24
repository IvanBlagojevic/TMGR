package org.gs4tr.termmanager.service.utils;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toSet;
import static org.gs4tr.termmanager.model.InputFieldTypeEnum.COMBO;
import static org.gs4tr.termmanager.model.StringConstants.PIPE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation.locale.LocaleException;
import org.gs4tr.foundation.modules.entities.model.UserException;
import org.gs4tr.foundation.modules.webmvc.i18n.MessageResolver;
import org.gs4tr.foundation3.placeables.TextHolder;
import org.gs4tr.foundation3.tbx.TbxAuxInfoIteratorHelper.TbxDescriptionIterator;
import org.gs4tr.foundation3.tbx.TbxAuxInfoIteratorHelper.TbxTermNoteIterator;
import org.gs4tr.foundation3.tbx.TbxDescription;
import org.gs4tr.foundation3.tbx.TbxLanguage;
import org.gs4tr.foundation3.tbx.TbxLanguage.TbxTermIterator;
import org.gs4tr.foundation3.tbx.TbxTerm;
import org.gs4tr.foundation3.tbx.TbxTermEntry;
import org.gs4tr.foundation3.tbx.TbxTermEntry.TbxLanguageIterator;
import org.gs4tr.foundation3.tbx.TbxTermNote;
import org.gs4tr.termmanager.model.Attribute;
import org.gs4tr.termmanager.model.CustomCollectionUtils;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.TmgrStringUtils;
import org.gs4tr.termmanager.model.UpdateCommand;
import org.gs4tr.termmanager.model.UpdateCommand.TypeEnum;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.model.xls.XlsDescription;
import org.gs4tr.termmanager.model.xls.XlsTerm;
import org.gs4tr.termmanager.model.xls.XlsTermEntry;
import org.gs4tr.termmanager.persistence.update.DescriptionImportOption;
import org.gs4tr.termmanager.persistence.update.ImportOptionsModel;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.service.manualtask.Messages;
import org.gs4tr.termmanager.service.termentry.synchronization.SyncOption;
import org.gs4tr.termmanager.service.xls.XlsReaderHelper;

public class TermEntryUtils {

    public static final String ID = "UUID";

    public static final String RELIABILITY_CODE = "reliabilityCode"; //$NON-NLS-1$

    public static final String STATUS = "termStatus";

    private static final String PIPE_REGEX = Pattern.quote(PIPE);

    public static TermEntry createEmptyTermEntry(TmProject project) {
	TermEntry termEntry = new TermEntry();
	String username = TmUserProfile.getCurrentUserName();
	termEntry.setUuId(UUID.randomUUID().toString());
	termEntry.setProjectId(project.getProjectId());
	termEntry.setShortCode(project.getProjectInfo().getShortCode());
	termEntry.setProjectName(project.getProjectInfo().getName());
	termEntry.setUserCreated(username);
	termEntry.setUserModified(username);

	return termEntry;
    }

    public static Map<Locale, Integer> createLanguageMap(Map<String, Integer> languageMap) {

	Map<Locale, Integer> languages = new HashMap<>();
	for (Map.Entry<String, Integer> entry : languageMap.entrySet()) {
	    String code = entry.getKey();
	    String localeCode = null;
	    try {
		localeCode = Locale.makeLocale(code).getCode();
		if (Locale.get(localeCode) == null) {
		    throwUserException(code);
		}
	    } catch (LocaleException e) {
		throwUserException(code);
	    }

	    languages.put(Locale.get(localeCode), entry.getValue());
	}

	return languages;
    }

    public static TermEntry createTermEntry(Object item, ImportOptionsModel importOptions, SyncOption syncOption) {
	if (item instanceof TbxTermEntry) {
	    TbxTermEntry tbxTermEntry = (TbxTermEntry) item;
	    if (tbxTermEntry.getXml() != null) {
		return createTermEntryFromTbx(tbxTermEntry, importOptions);
	    }
	} else if (item instanceof XlsTermEntry) {
	    XlsTermEntry xlsTermEntry = (XlsTermEntry) item;
	    return createTermEntryFromXls(xlsTermEntry, importOptions, syncOption);
	}

	return null;
    }

    // TERII-3101 - this method is going to handle term comment creation
    public static void fillCommentWithParentMarkerId(List<UpdateCommand> updateCommands, String termEntryMarkerId) {
	if (CollectionUtils.isEmpty(updateCommands)) {
	    return;
	}

	Map<String, List<UpdateCommand>> uuidCommandMap = new HashMap<>();

	for (UpdateCommand updateCommand : updateCommands) {
	    if (updateCommand.getType().equals(TypeEnum.TERM.toString())) {
		String markerId = updateCommand.getMarkerId();
		List<UpdateCommand> commands = uuidCommandMap.get(markerId);
		if (commands == null) {
		    commands = new ArrayList<>();
		    uuidCommandMap.put(markerId, commands);
		}
		commands.add(updateCommand);
	    }
	}

	for (UpdateCommand updateCommand : updateCommands) {
	    if (updateCommand.getType().equals(TypeEnum.COMMENT.toString())) {
		String parentMarkerId = updateCommand.getParentMarkerId();
		List<UpdateCommand> commands = uuidCommandMap.get(parentMarkerId);
		if (commands != null) {
		    commands.add(updateCommand);
		}
	    }
	}

	for (Entry<String, List<UpdateCommand>> entry : uuidCommandMap.entrySet()) {
	    String termId = UUID.randomUUID().toString();
	    List<UpdateCommand> commands = entry.getValue();
	    for (UpdateCommand updateCommand : commands) {
		updateCommand.setTermId(termId);
		if (updateCommand.getType().equals(TypeEnum.COMMENT.toString())) {
		    updateCommand.setParentMarkerId(termId);
		}
	    }
	}
    }

    public static void fillWithLanguage(List<UpdateCommand> updateCommands, String languageId) {
	if (CollectionUtils.isEmpty(updateCommands)) {
	    return;
	}

	for (UpdateCommand updateCommand : updateCommands) {
	    if (updateCommand.getLanguageId() == null) {
		updateCommand.setLanguageId(languageId);
	    }
	}
    }

    public static void fillWithParentMarkerId(List<UpdateCommand> updateCommands, String termEntryMarkerId) {
	if (CollectionUtils.isEmpty(updateCommands)) {
	    return;
	}

	for (UpdateCommand updateCommand : updateCommands) {
	    if (updateCommand.getType().equals(TypeEnum.TERM.toString())) {
		updateCommand.setParentMarkerId(termEntryMarkerId);
		updateCommand.setTermId(UUID.randomUUID().toString());
	    }
	}
    }

    public static void fillWithParentMarkerId(List<UpdateCommand> updateCommands, String termEntryMarkerId,
	    String termEntryOldMarkerId) {
	if (CollectionUtils.isEmpty(updateCommands) || StringUtils.isEmpty(termEntryOldMarkerId)) {
	    return;
	}

	for (UpdateCommand updateCommand : updateCommands) {
	    String type = updateCommand.getType();
	    boolean isTerm = type.equals(TypeEnum.TERM.toString());
	    boolean isDescription = TypeEnum.DESCRIP.toString().equals(type);
	    boolean isTermEntryChild = termEntryOldMarkerId.equals(updateCommand.getParentMarkerId());

	    // Note also has to be child of termEntry
	    if (isTerm || (isDescription && isTermEntryChild)) {
		updateCommand.setParentMarkerId(termEntryMarkerId);
		updateCommand.setTermId(UUID.randomUUID().toString());
	    }
	}

    }

    public static List<String> filterSynchronizableAttributes(List<Attribute> attributes) {
	if (CollectionUtils.isEmpty(attributes)) {
	    return null;
	}
	List<String> syncAttributes = new ArrayList<>();
	for (Attribute attribute : attributes) {
	    if (attribute.getSynchronizable()) {
		syncAttributes.add(attribute.getName());
	    }
	}
	return syncAttributes;
    }

    public static Map<String, Set<String>> getComboValuesPerAttribute(ProjectService projectService, Long projectId) {
	Map<String, Set<String>> comboValuesPerAttribute = new HashMap<>();
	Map<Long, List<Attribute>> attributes = projectService.findAttributesByProjectId(singletonList(projectId));
	List<Attribute> projectAttributes = attributes.get(projectId);
	for (final Attribute attribute : projectAttributes) {
	    if (COMBO == attribute.getInputFieldTypeEnum()) {
		String comboValue = attribute.getComboValues();
		Set<String> comboValues = Stream.of(comboValue.split(PIPE_REGEX)).map(String::trim).collect(toSet());
		comboValuesPerAttribute.put(attribute.getName(), comboValues);
	    }
	}
	return comboValuesPerAttribute;
    }

    public static Set<Description> getNullSafeDescriptions(Set<Description> descriptions) {
	Set<Description> copy = new HashSet<>();

	if (CollectionUtils.isNotEmpty(descriptions)) {
	    copy.addAll(descriptions);
	}
	return copy;
    }

    public static Set<Description> getNullSafeDescriptions(Term term) {
	Set<Description> copy = new HashSet<>();
	if (Objects.isNull(term)) {
	    return copy;
	}
	return getNullSafeDescriptions(term.getDescriptions());
    }

    public static Set<Description> getNullSafeDescriptions(TermEntry termEntry) {
	Set<Description> copy = new HashSet<>();
	if (Objects.isNull(termEntry)) {
	    return copy;
	}
	return getNullSafeDescriptions(termEntry.getDescriptions());
    }

    public static List<Term> getNullSafeTerns(TermEntry termEntry) {
	List<Term> copy = new ArrayList<>();
	if (Objects.isNull(termEntry)) {
	    return copy;
	}

	List<Term> terms = termEntry.ggetAllTerms();
	if (CollectionUtils.isNotEmpty(terms)) {
	    copy.addAll(terms);
	}
	return copy;
    }

    public static boolean updateDuplicateTerms(Set<Term> terms, Term term) {
	Iterator<Term> iterator = terms.iterator();
	while (iterator.hasNext()) {
	    Term nextTerm = iterator.next();
	    Long dateModified = nextTerm.getDateModified();
	    Long newDateModified = term.getDateModified();
	    boolean isDifferent = !dateModified.equals(newDateModified);
	    if (isDifferent && term.equals(nextTerm)) {
		nextTerm.setDateModified(newDateModified);
		nextTerm.setDateCreated(term.getDateCreated());
		nextTerm.setUserCreated(term.getUserCreated());
		nextTerm.setUserModified(term.getUserModified());
		return true;
	    }
	}
	return false;
    }

    public static void validateAttributeType(String attributeType) {
	if (StringUtils.isBlank(attributeType)) {
	    throw new RuntimeException(Messages.getString("UploadTermEntryReferencesTaskHandler.0")); //$NON-NLS-1$
	}
    }

    private static Set<Description> convertXlsDescToTermDesc(List<XlsDescription> xlsDescriptions,
	    ImportOptionsModel model) {
	if (CollectionUtils.isEmpty(xlsDescriptions)) {
	    return null;
	}

	Map<String, Map<String, String>> attributeNoteReplacements = getMapOrEmpty(
		model.getAttributeNoteReplacements());

	DescriptionImportOption descriptionImportOption = model.getDescriptionImportOption();

	Set<Description> descriptions = new HashSet<>(xlsDescriptions.size());
	for (XlsDescription xlsDescription : xlsDescriptions) {
	    Description termDescription = new Description();
	    String xlsType = xlsDescription.getType();
	    String baseType = xlsDescription.getBaseType();
	    // [21-March-2017], since TMGR 5.0

	    Map<String, String> replacements = attributeNoteReplacements.getOrDefault(baseType, Collections.emptyMap());
	    String descriptionType = replacements.getOrDefault(xlsType, xlsType);
	    Set<String> allowedTypes = model.getAllowedTermDescriptions().get(baseType);

	    if (isDescriptionAllowed(allowedTypes, descriptionImportOption, descriptionType)) {

		termDescription.setUuid(UUID.randomUUID().toString());
		termDescription.setType(descriptionType);
		termDescription.setValue(xlsDescription.getValue());
		termDescription.setTempValue(xlsDescription.getValue());
		termDescription.setBaseType(baseType);
		descriptions.add(termDescription);
	    }

	}

	return descriptions;

    }

    private static Set<Description> convertXlsDescToTermEntryDesc(List<XlsDescription> xlsDescriptions,
	    ImportOptionsModel model) {
	if (CollectionUtils.isEmpty(xlsDescriptions)) {
	    return null;
	}

	Map<String, String> attributeReplacement = getMapOrEmpty(model.getTermEntryAttributeReplacements());

	DescriptionImportOption descriptionImportOption = model.getDescriptionImportOption();

	Set<Description> descriptions = new HashSet<>(xlsDescriptions.size());
	for (XlsDescription xlsDescription : xlsDescriptions) {
	    Description termEntryDescription = new Description();
	    String xlsDescriptionType = xlsDescription.getType();

	    String descriptionType = attributeReplacement.getOrDefault(xlsDescriptionType, xlsDescriptionType);
	    Set<String> allowedTypes = model.getAllowedTermEntryAttributes();
	    // [21-March-2017], since TMGR 5.0

	    if (isDescriptionAllowed(allowedTypes, descriptionImportOption, descriptionType)) {
		termEntryDescription.setUuid(UUID.randomUUID().toString());
		termEntryDescription.setType(descriptionType);
		termEntryDescription.setValue(xlsDescription.getValue());
		termEntryDescription.setTempValue(xlsDescription.getValue());
		termEntryDescription.setBaseType(xlsDescription.getBaseType());
		descriptions.add(termEntryDescription);
	    }

	}

	return descriptions;
    }

    private static Description createDescriptionFromTbx(String descriptionType, String markerId, String trimedText,
	    String baseType) {

	Description description = new Description();
	description.setBaseType(baseType);
	description.setUuid(StringUtils.isNotEmpty(markerId) ? markerId : UUID.randomUUID().toString());
	description.setType(descriptionType);
	description.setValue(trimedText);
	description.setTempValue(trimedText);

	return description;

    }

    private static TermEntry createTermEntryFromTbx(TbxTermEntry tbxTermEntry, ImportOptionsModel importOptions) {
	String username = TmUserProfile.getCurrentUserName();

	TermEntry termEntry = new TermEntry();

	String markerId = tbxTermEntry.getMarkerID();

	termEntry.setUuId(markerId);
	termEntry.setProjectId(importOptions.getProjectId());
	termEntry.setShortCode(importOptions.getProjectShortCode());
	termEntry.setProjectName(importOptions.getProjectName());
	termEntry.setUserCreated(username);
	termEntry.setUserModified(username);

	TbxDescriptionIterator tbxDescriptionIterator = tbxTermEntry.getDescriptionIterator();
	TbxLanguageIterator tbxLanguageIterator = tbxTermEntry.getLanguageIterator();

	Date date = new Date();

	Map<String, String> termEntryAttributeReplacements = getMapOrEmpty(
		importOptions.getTermEntryAttributeReplacements());

	DescriptionImportOption descriptionImportOption = importOptions.getDescriptionImportOption();

	while (tbxDescriptionIterator.hasNext()) {
	    TbxDescription termEntryDesc = tbxDescriptionIterator.next();
	    String tbxType = termEntryDesc.getType();
	    String termEntryAttributeContent = CustomCollectionUtils.listToString(termEntryDesc.getContent());

	    if (tbxType.equals(ID)) {
		termEntry.setUuId(termEntryAttributeContent);
		continue;
	    }

	    String trimedText = TmgrStringUtils.trimNonBreakingSpace(termEntryAttributeContent).trim();
	    trimedText = XlsReaderHelper.removeMultipleWhiteSpace(trimedText);

	    String descriptionType = termEntryAttributeReplacements.getOrDefault(tbxType, tbxType);
	    Set<String> allowedTypes = importOptions.getAllowedTermEntryAttributes();

	    if (StringUtils.isNotBlank(trimedText)) {

		if (isDescriptionAllowed(allowedTypes, descriptionImportOption, descriptionType)) {
		    Description description = createDescriptionFromTbx(descriptionType, termEntryDesc.getMarkerID(),
			    trimedText, Description.ATTRIBUTE);
		    termEntry.addDescription(description);
		}
	    }
	}

	boolean forbidden = ItemStatusTypeHolder.BLACKLISTED.getName().equals(importOptions.getStatus());

	Map<String, String> languageReplacementByCode = getMapOrEmpty(importOptions.getLanguageReplacementByCode());
	Map<String, Map<String, String>> attributeNoteReplacements = getMapOrEmpty(
		importOptions.getAttributeNoteReplacements());
	Map<String, String> termAttributeReplacements = attributeNoteReplacements.getOrDefault(Description.ATTRIBUTE,
		Collections.emptyMap());
	Map<String, String> termNoteReplacements = attributeNoteReplacements.getOrDefault(Description.NOTE,
		Collections.emptyMap());

	while (tbxLanguageIterator.hasNext()) {
	    TbxLanguage tbxLanguage = tbxLanguageIterator.next();

	    String tbxLanguageCode = tbxLanguage.getLanguage().getCode();

	    TbxTermIterator termIterator = tbxLanguage.getTermIterator();
	    while (termIterator.hasNext()) {
		TbxTerm tbxTerm = termIterator.next();

		List<TextHolder> termContent = tbxTerm.getContent();
		String termName = CustomCollectionUtils.listToString(termContent);
		String trimedText = TmgrStringUtils.trimNonBreakingSpace(termName).trim();
		trimedText = XlsReaderHelper.replaceBrakingLineCharacters(trimedText);
		trimedText = XlsReaderHelper.removeMultipleWhiteSpace(trimedText);

		Term term = new Term();
		term.setUuId(tbxTerm.getMarkerID());
		term.setName(trimedText);
		term.setLanguageId(languageReplacementByCode.getOrDefault(tbxLanguageCode, tbxLanguageCode));
		term.setUserCreated(username);
		term.setUserModified(username);
		term.setStatus(importOptions.getStatus());
		term.setStatusOld(importOptions.getStatus());
		term.setForbidden(forbidden);
		term.setDisabled(Boolean.FALSE);
		term.setDateCreated(date.getTime());
		term.setDateModified(date.getTime());
		term.setDescriptions(new HashSet<>());
		term.setTermEntryId(termEntry.getUuId());

		TbxTermNoteIterator termNoteIterator = tbxTerm.getTermNoteIterator();
		while (termNoteIterator.hasNext()) {
		    TbxTermNote tbxTermNote = termNoteIterator.next();
		    String tbxTermNoteType = tbxTermNote.getType();
		    String content = CustomCollectionUtils.listToString(tbxTermNote.getContent());

		    if (tbxTermNoteType.equals(STATUS)) {
			term.setStatus(ItemStatusTypeHolder.getStatusByDisplayName(content));
			term.setStatusOld(ItemStatusTypeHolder.getStatusByDisplayName(content));
			continue;
		    }

		    String trimedNoteText = TmgrStringUtils.trimNonBreakingSpace(content).trim();
		    trimedNoteText = XlsReaderHelper.replaceBrakingLineCharacters(trimedNoteText);
		    trimedNoteText = XlsReaderHelper.removeMultipleWhiteSpace(trimedNoteText);

		    String noteType = termNoteReplacements.getOrDefault(tbxTermNoteType, tbxTermNoteType);
		    Set<String> allowedTypes = importOptions.getAllowedTermDescriptions().get(Description.NOTE);

		    if (isDescriptionAllowed(allowedTypes, descriptionImportOption, noteType)) {
			Description termNote = createDescriptionFromTbx(noteType, tbxTermNote.getMarkerID(),
				trimedNoteText, Description.NOTE);
			term.getDescriptions().add(termNote);
		    }
		}

		TbxDescriptionIterator termAttributeIterator = tbxTerm.getDescriptionIterator();
		while (termAttributeIterator.hasNext()) {
		    TbxDescription tbxTermAttribute = termAttributeIterator.next();
		    String tbxTermAttributeType = tbxTermAttribute.getType();
		    String content = CustomCollectionUtils.listToString(tbxTermAttribute.getContent());

		    if (tbxTermAttributeType.equals(ID)) {
			term.setUuId(content);
			continue;
		    }

		    String trimedNoteText = TmgrStringUtils.trimNonBreakingSpace(content).trim();
		    trimedNoteText = XlsReaderHelper.replaceBrakingLineCharacters(trimedNoteText);
		    trimedNoteText = XlsReaderHelper.removeMultipleWhiteSpace(trimedNoteText);

		    String descriptionType = termAttributeReplacements.getOrDefault(tbxTermAttributeType,
			    tbxTermAttributeType);
		    Set<String> allowedTypes = importOptions.getAllowedTermDescriptions().get(Description.ATTRIBUTE);

		    if (isDescriptionAllowed(allowedTypes, descriptionImportOption, descriptionType)) {

			Description termNote = createDescriptionFromTbx(descriptionType, tbxTermAttribute.getMarkerID(),
				trimedNoteText, Description.ATTRIBUTE);
			term.getDescriptions().add(termNote);
		    }

		}

		termEntry.addTerm(term);
	    }
	}

	return termEntry;
    }

    private static TermEntry createTermEntryFromXls(XlsTermEntry xlsTermEntry, ImportOptionsModel importOptions,
	    SyncOption syncOption) {
	String username = TmUserProfile.getCurrentUserName();

	TermEntry termEntry = new TermEntry();

	String xlsUuId = xlsTermEntry.getXlsUuId();
	String termEntryId = StringUtils.isNotEmpty(xlsUuId) ? xlsUuId : UUID.randomUUID().toString();

	termEntry.setUuId(termEntryId);
	termEntry.setProjectId(importOptions.getProjectId());
	termEntry.setShortCode(importOptions.getProjectShortCode());
	termEntry.setProjectName(importOptions.getProjectName());

	termEntry
		.setDescriptions(convertXlsDescToTermEntryDesc(xlsTermEntry.getTermEntryDescriptions(), importOptions));
	termEntry.setUserCreated(username);
	termEntry.setUserModified(username);

	Date date = new Date();
	CaseInsensitiveMap statusConverter = ItemStatusTypeHolder.getStatusConverterMap();
	String defaultTermStatus = importOptions.getStatus();

	Map<String, String> languageReplacementByCode = getMapOrEmpty(importOptions.getLanguageReplacementByCode());

	List<XlsTerm> xlsTerms = xlsTermEntry.getTerms();
	for (XlsTerm xlsTerm : xlsTerms) {
	    Term term = new Term();
	    term.setUuId(UUID.randomUUID().toString());
	    term.setDescriptions(convertXlsDescToTermDesc(xlsTerm.getDescriptions(), importOptions));

	    String fileLocaleCode = xlsTerm.getLocaleCode();
	    term.setLanguageId(languageReplacementByCode.getOrDefault(fileLocaleCode, fileLocaleCode));
	    term.setName(xlsTerm.getTerm());

	    // Ignore status from file if sync options is merge
	    String termStatus = SyncOption.MERGE == syncOption ? defaultTermStatus
		    : statusConverter.containsKey(xlsTerm.getXlsStatus())
			    ? (String) statusConverter.get(xlsTerm.getXlsStatus())
			    : defaultTermStatus;

	    boolean forbidden = termStatus.equals(ItemStatusTypeHolder.BLACKLISTED.getName());

	    term.setStatus(termStatus);
	    term.setStatusOld(termStatus);
	    term.setUserCreated(username);
	    term.setUserModified(username);
	    term.setForbidden(forbidden);
	    term.setDisabled(Boolean.FALSE);
	    term.setDateCreated(date.getTime());
	    term.setDateModified(date.getTime());
	    term.setTermEntryId(termEntry.getUuId());
	    termEntry.addTerm(term);
	}

	return termEntry;
    }

    private static <K, V> Map<K, V> getMapOrEmpty(Map<K, V> map) {
	return MapUtils.isNotEmpty(map) ? map : Collections.emptyMap();

    }

    private static boolean isDescriptionAllowed(Set<String> allowedDescriptions,
	    DescriptionImportOption descriptionImportOption, String descriptionType) {

	boolean areAllTypesAllowed = DescriptionImportOption.ADD_ALL == descriptionImportOption;
	boolean isDescriptionTypeAllowed = CollectionUtils.isNotEmpty(allowedDescriptions)
		&& allowedDescriptions.contains(descriptionType);

	return areAllTypesAllowed || isDescriptionTypeAllowed;

    }

    private static void throwUserException(String code) {
	String description = String.format(MessageResolver.getMessage("TermEntryUtils.4"), //$NON-NLS-1$
		code);
	throw new UserException(String.format(MessageResolver.getMessage("TermEntryUtils.5"), code), description); //$NON-NLS-1$
    }
}