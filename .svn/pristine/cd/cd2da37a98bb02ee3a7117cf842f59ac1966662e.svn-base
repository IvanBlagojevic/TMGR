package org.gs4tr.termmanager.service.manualtask;

import static org.gs4tr.termmanager.model.AttributeLevelEnum.TERMENTRY;
import static org.gs4tr.termmanager.model.BaseTypeEnum.DESCRIPTION;
import static org.gs4tr.termmanager.model.BaseTypeEnum.NOTE;
import static org.gs4tr.termmanager.service.file.analysis.model.ImportAttribute.Level.TERM_ATTRIBUTE;
import static org.gs4tr.termmanager.service.file.analysis.model.ImportAttribute.Level.TERM_ENTRY;
import static org.gs4tr.termmanager.service.file.analysis.model.ImportAttribute.Level.TERM_NOTE;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.gs4tr.foundation.modules.entities.model.LdapUserInfo;
import org.gs4tr.foundation.modules.entities.model.ResourceInfo;
import org.gs4tr.foundation.modules.entities.model.UserException;
import org.gs4tr.foundation.modules.entities.model.UserInfo;
import org.gs4tr.foundation.modules.entities.model.UserTypeEnum;
import org.gs4tr.foundation.modules.security.ldap.LdapUserHandlerInterface;
import org.gs4tr.foundation.modules.security.model.Role;
import org.gs4tr.foundation.modules.spring.ApplicationContextLocator;
import org.gs4tr.foundation.modules.webmvc.i18n.MessageResolver;
import org.gs4tr.foundation3.tbx.TbxDescriptionTypes;
import org.gs4tr.foundation3.tbx.TbxNoteTypes;
import org.gs4tr.termmanager.model.Attribute;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.Language;
import org.gs4tr.termmanager.model.ProjectLanguage;
import org.gs4tr.termmanager.model.StringConstants;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.model.dto.AttributeLevelEnum;
import org.gs4tr.termmanager.model.dto.InputFieldTypeEnum;
import org.gs4tr.termmanager.model.dto.TermEntryAttributeTypeDto;
import org.gs4tr.termmanager.model.dto.TermEntryResourceTrack;
import org.gs4tr.termmanager.model.dto.converter.AttributeLevelConverter;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.search.AbstractSearchRequest;
import org.gs4tr.termmanager.model.search.LanguageSearchEnum;
import org.gs4tr.termmanager.model.search.TypeSearchEnum;
import org.gs4tr.termmanager.service.RoleService;
import org.gs4tr.termmanager.service.UserProfileService;
import org.gs4tr.termmanager.service.file.analysis.model.ImportAttribute.Level;
import org.gs4tr.termmanager.service.utils.TermEntryUtils;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;

public class ManualTaskHandlerUtils {

    public static final String DEFAULT_TERM_STATUS = "defaultTermStatus"; //$NON-NLS-1$

    public static final String GENERIC_USER_ROLE = "generic_user"; //$NON-NLS-1$

    public static final String PROJECT_NAME = "projectName"; //$NON-NLS-1$

    public static final String START_PINGING = "startPinging"; //$NON-NLS-1$

    public static final String TERM_STATUSES = "termStatuses"; //$NON-NLS-1$

    private static final Pattern LDAP_ERROR_PATTERN = Pattern.compile("\\[LDAP:.+?-\\s?(.+?)\\s?\\]"); //$NON-NLS-1$

    private static final String LDAP_USER_HANDLER_BEAN_NAME = "ldapUserHandler"; //$NON-NLS-1$

    public static void checkImageFiles(List<UploadedRepositoryItem> files) {
	for (UploadedRepositoryItem uploadedRepositoryItem : files) {
	    ResourceInfo resourceInfo = uploadedRepositoryItem.getRepositoryItem().getResourceInfo();
	    String mimeType = resourceInfo.getMimeType();
	    if (!mimeType.startsWith(StringConstants.IMAGE)) {
		String message = String.format(MessageResolver.getMessage("UploadMultimediaTaskHandler.5"), //$NON-NLS-1$
			resourceInfo.getName());
		throw new UserException(message);
	    }
	}
    }

    public static List<TermEntryResourceTrack> collectMultimedia(
	    List<org.gs4tr.termmanager.model.TermEntryResourceTrack> resourceTracks) {
	List<TermEntryResourceTrack> tracks = new ArrayList<>();
	if (CollectionUtils.isNotEmpty(resourceTracks)) {
	    for (org.gs4tr.termmanager.model.TermEntryResourceTrack track : resourceTracks) {
		TermEntryResourceTrack dtoResourceTrack = new TermEntryResourceTrack();
		dtoResourceTrack.setFileName(track.getResourceName());
		dtoResourceTrack.setName(track.getTaskName());
		dtoResourceTrack.setResourceTicket(track.getResourceId());
		String[] typeFormat = track.getTrackType().split(StringConstants.SLASH);
		dtoResourceTrack.setMediaType(typeFormat[0]);
		dtoResourceTrack.setFormat(typeFormat[1]);
		tracks.add(dtoResourceTrack);
	    }
	}
	return tracks;
    }

    public static LdapUserInfo createLdapUserInfo(UserInfo userInfo) {
	LdapUserInfo newLdapUserInfo = new LdapUserInfo();
	newLdapUserInfo.setUserName(userInfo.getUserName());
	newLdapUserInfo.setFirstName(userInfo.getFirstName());
	newLdapUserInfo.setLastName(userInfo.getLastName());
	newLdapUserInfo.setEmailAddress(userInfo.getEmailAddress());
	newLdapUserInfo.setPassword(userInfo.getPassword());

	return newLdapUserInfo;
    }

    public static List<Map<String, String>> createTermStatusModel() {
	Map<String, String> approved = new HashMap<>();
	approved.put("name", ItemStatusTypeHolder.PROCESSED.getName()); //$NON-NLS-1$
	approved.put("value", ItemStatusTypeHolder.PROCESSED.getName()); //$NON-NLS-1$

	Map<String, String> pending = new HashMap<>();
	pending.put("name", ItemStatusTypeHolder.WAITING.getName()); //$NON-NLS-1$
	pending.put("value", ItemStatusTypeHolder.WAITING.getName()); //$NON-NLS-1$

	Map<String, String> onHold = new HashMap<>();
	onHold.put("name", ItemStatusTypeHolder.ON_HOLD.getName()); //$NON-NLS-1$
	onHold.put("value", ItemStatusTypeHolder.ON_HOLD.getName()); //$NON-NLS-1$

	List<Map<String, String>> statuses = new ArrayList<>();
	statuses.add(approved);
	statuses.add(pending);
	statuses.add(onHold);

	return statuses;
    }

    public static String extractLdapMessage(String message) {
	Matcher matcher = LDAP_ERROR_PATTERN.matcher(message);
	if (matcher.find()) {
	    return matcher.group(1);
	}
	return null;
    }

    public static void forceDeleteFile(File file) {
	try {
	    if (Objects.nonNull(file)) {
		FileUtils.forceDelete(file);
	    }
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}
    }

    public static List<org.gs4tr.termmanager.model.dto.Attribute> getAllAttributesResponse() {
	Set<String> values = TbxDescriptionTypes.getAttributeTypes();
	values.remove(TermEntryUtils.RELIABILITY_CODE);

	List<org.gs4tr.termmanager.model.dto.Attribute> allAttributes = new ArrayList<>();
	for (String type : values) {
	    org.gs4tr.termmanager.model.dto.Attribute attribute = new org.gs4tr.termmanager.model.dto.Attribute();
	    attribute.setAttributeLevelEnum(getLevelForType(type));
	    attribute.setAttributeName(type);
	    attribute.setAttributeType(TermEntryAttributeTypeDto.TEXT);
	    attribute.setInputFieldTypeEnum(getInputFieldTypeForTbxDescription(type));
	    List<String> predefinedValuesForAttribute = TbxDescriptionTypes.getPredefinedValuesForAttribute(type);
	    attribute.setComboValues(getPredefinedValuesAsString(predefinedValuesForAttribute));
	    attribute.setReadOnly(isReadOnlyAttributeValues(type));

	    allAttributes.add(attribute);

	}
	return allAttributes;
    }

    public static List<org.gs4tr.termmanager.model.dto.ProjectNote> getAllNotesResponse() {
	Set<String> values = TbxNoteTypes.getNoteTypes();
	List<org.gs4tr.termmanager.model.dto.ProjectNote> allNotes = new ArrayList<>();
	for (String type : values) {
	    org.gs4tr.termmanager.model.dto.ProjectNote projectNote = new org.gs4tr.termmanager.model.dto.ProjectNote();
	    projectNote.setNoteName(type);
	    projectNote.setInputFieldTypeEnum(getInputFieldTypeForNotes(type));
	    List<String> predefinedValuesForNote = TbxNoteTypes.getPredefinedValuesForNote(type);
	    projectNote.setComboValues(getPredefinedValuesAsString(predefinedValuesForNote));
	    projectNote.setReadOnly(isReadOnlyNoteValues(type));

	    allNotes.add(projectNote);

	}
	return allNotes;
    }

    public static LdapUserHandlerInterface getLdapUserHandler() {
	return (LdapUserHandlerInterface) ApplicationContextLocator.getContext(null)
		.getBean(LDAP_USER_HANDLER_BEAN_NAME);
    }

    public static Map<Level, Set<String>> groupAttributesByLevel(List<Attribute> attributes) {
	if (CollectionUtils.isEmpty(attributes)) {
	    return Collections.emptyMap();
	}
	Map<Level, Set<String>> result = new HashMap<>(3);
	for (final Attribute attribute : attributes) {
	    ManualTaskHandlerUtils.groupAttributesByLevel(attribute, result);
	}

	return result;
    }

    public static Map<String, Set<Term>> groupTermsByTermEntry(List<Term> terms) {
	Map<String, Set<Term>> map = new LinkedHashMap<>();

	if (CollectionUtils.isEmpty(terms)) {
	    return map;
	}

	for (Term term : terms) {
	    Set<Term> termEntryTerms = map.get(term.getTermEntryId());
	    if (termEntryTerms == null) {
		termEntryTerms = new HashSet<>();
		map.put(term.getTermEntryId(), termEntryTerms);
	    }

	    termEntryTerms.add(term);
	}

	return map;
    }

    public static Set<String> resolveDescriptionTypes(Set<String> incomingTypes, Set<String> allowedTypes,
	    boolean ignoreCase) {
	Set<String> resolvedTypes = new HashSet<>();
	if (CollectionUtils.isEmpty(allowedTypes) && CollectionUtils.isEmpty(incomingTypes)) {
	    return resolvedTypes;
	}

	if (CollectionUtils.isEmpty(allowedTypes)) {
	    resolvedTypes.addAll(incomingTypes);
	    return resolvedTypes;
	}

	for (String incomingType : incomingTypes) {
	    if (ignoreCase) {
		resolvedTypes.add(allowedTypes.stream().filter(t -> t.equalsIgnoreCase(incomingType)).findFirst()
			.orElse(incomingType));
	    } else {
		resolvedTypes.add(incomingType);
	    }
	}

	return resolvedTypes;
    }

    public static void resolveSearchEntityTypes(List<String> entityTypes, List<String> languageLevel,
	    AbstractSearchRequest request) {
	if (CollectionUtils.isNotEmpty(entityTypes)) {
	    if (entityTypes.size() == 1) {
		request.setTypeSearch(TypeSearchEnum.valueOf(entityTypes.get(0)));
	    } else {
		request.setTypeSearch(TypeSearchEnum.ALL);
	    }
	}
	// TODO: @fmiskovic, is this by design?
	if (CollectionUtils.isNotEmpty(entityTypes)) {
	    // If languageLevel is null that means ALL (right now that includes
	    // source and target)
	    if (CollectionUtils.isNotEmpty(languageLevel) && languageLevel.size() == 1) {
		LanguageSearchEnum language = LanguageSearchEnum.valueOf(languageLevel.get(0));
		switch (language) {
		case SOURCE:
		    request.setTargetTermSearch(false);
		    break;
		case TARGET:
		    if (CollectionUtils.isEmpty(request.getTargetLanguagesList())) {
			throw new UserException(Messages.getString("ManualTaskHandlerUtils.15"), //$NON-NLS-1$
				Messages.getString("ManualTaskHandlerUtils.16")); //$NON-NLS-1$
		    }
		    request.setTargetTermSearch(true);
		    request.setSourceAndTargetSearch(false);
		    break;
		default:
		    break;
		}
	    }
	}
    }

    public static void resolveStatusSearch(AbstractSearchRequest request) {
	List<String> statuses = request.getStatuses();
	request.setStatusRequestList(statuses);
	if (CollectionUtils.isNotEmpty(statuses)) {
	    if (statuses.size() == 1 && statuses.contains(ItemStatusTypeHolder.MISSING_TRANSLATION.getName())) {
		request.setStatuses(null);
		request.setMissingTranslation(true);
		// if its source search, remove missingTranslation status
		if (!request.isTargetTermSearch()) {
		    statuses.remove(ItemStatusTypeHolder.MISSING_TRANSLATION.getName());
		}
	    }
	}
    }

    public static void sortLanguages(List<String> languages) {
	Collections.sort(languages, new Comparator<String>() {
	    @Override
	    public int compare(String o1, String o2) {
		Language language1 = Language.valueOf(o1);
		Language language2 = Language.valueOf(o2);
		return language1.getDisplayName().compareTo(language2.getDisplayName());
	    }
	});
    }

    public static void sortProjectLanguages(List<ProjectLanguage> projectLanguages) {
	Collections.sort(projectLanguages, new Comparator<ProjectLanguage>() {
	    @Override
	    public int compare(ProjectLanguage o1, ProjectLanguage o2) {
		Language language1 = Language.valueOf(o1.getLanguage());
		Language language2 = Language.valueOf(o2.getLanguage());
		return language1.getDisplayName().compareTo(language2.getDisplayName());
	    }
	});
    }

    public static boolean supportsLdap() {
	return MapUtils.isNotEmpty(
		ApplicationContextLocator.getContext(null).getBeansOfType(LdapAuthenticationProvider.class));
    }

    public static boolean updateLdapPassword(String userName, String oldPassword, String newPassword,
	    UserInfo userInfo) {
	boolean ldapChanged = false;

	if (supportsLdap() && StringUtils.isNotBlank(newPassword)) {
	    LdapUserHandlerInterface ldapUserHandler = getLdapUserHandler();

	    LdapUserInfo ldapUserInfo = ldapUserHandler.findUser(userName);
	    if (ldapUserInfo != null) {
		Validate.isTrue(ldapUserHandler.allowPasswordChange(), Messages.getString("ManualTaskHandlerUtils.0")); //$NON-NLS-1$
		try {
		    if (oldPassword != null) {
			ldapUserHandler.changeCurrentUserPassword(ldapUserInfo, oldPassword, newPassword);
		    } else {
			ldapUserHandler.changePassword(ldapUserInfo, newPassword);
		    }
		    // to disable changing password of pd user setting userInfos
		    // password to null
		    if (userInfo != null) {
			userInfo.setPassword(StringUtils.EMPTY);
		    }
		    ldapChanged = true;
		} catch (Exception ex) {
		    String extractedLdapMessage = extractLdapMessage(ex.getMessage());
		    String errorMessage = (extractedLdapMessage == null) ? ex.getMessage() : extractedLdapMessage;
		    throw new RuntimeException(errorMessage, ex);
		}
	    }
	}
	return ldapChanged;
    }

    public static void updateUserProjectRole(RoleService roleService, UserProfileService userProfileService,
	    Long projectId, List<Long> userIds, Long userId, String roleName, boolean isGeneric) {
	Role userRole = roleService.findRoleByName(roleName);
	List<Role> roles = new ArrayList<>();
	roles.add(userRole);

	userProfileService.addOrUpdateUserProjectRoles(userId, projectId, roles, userIds, UserTypeEnum.ORGANIZATION,
		isGeneric);

	userProfileService.addOrUpdateProjectUserDetails(userId, projectId, userIds);
    }

    public static boolean validateUserInfo(UserInfo userInfo) {
	return !(StringUtils.isEmpty(userInfo.getUserName()) && StringUtils.isEmpty(userInfo.getPassword()));
    }

    private static InputFieldTypeEnum getInputFieldTypeForNotes(String type) {
	return TbxNoteTypes.hasMultipleValues(type) ? InputFieldTypeEnum.COMBO : InputFieldTypeEnum.TEXT;
    }

    private static InputFieldTypeEnum getInputFieldTypeForTbxDescription(String type) {
	return TbxDescriptionTypes.hasMultipleValues(type) ? InputFieldTypeEnum.COMBO : InputFieldTypeEnum.TEXT;
    }

    private static AttributeLevelEnum getLevelForType(String type) {
	String levelForAttribute = TbxDescriptionTypes.getLevelForAttribute(type);
	if (levelForAttribute != null) {
	    return AttributeLevelConverter
		    .fromInternalToDto(org.gs4tr.termmanager.model.AttributeLevelEnum.valueOf(levelForAttribute));
	}
	return null;
    }

    private static String getPredefinedValuesAsString(List<String> predefinedValues) {
	StringBuilder builder = new StringBuilder();
	for (String value : predefinedValues) {
	    builder.append(value);
	    if (predefinedValues.size() != predefinedValues.indexOf(value) + 1) {
		builder.append(StringConstants.PIPE);
	    }
	}
	return builder.toString();
    }

    /**
     * This method use only BaseTypeEnum to group attributes with
     * AttributeLevelEnum#LANGUAGE because note(s) don't have attribute level in
     * database
     */
    private static void groupAttributesByLevel(Attribute attribute, Map<Level, Set<String>> attributesByLevel) {
	final String name = attribute.getName();
	if (TERMENTRY == attribute.getAttributeLevel()) {
	    attributesByLevel.computeIfAbsent(TERM_ENTRY, k -> new HashSet<>()).add(name);
	} else {
	    if (DESCRIPTION == attribute.getBaseTypeEnum()) {
		attributesByLevel.computeIfAbsent(TERM_ATTRIBUTE, k -> new HashSet<>()).add(name);
	    } else if (NOTE == attribute.getBaseTypeEnum()) {
		attributesByLevel.computeIfAbsent(TERM_NOTE, k -> new HashSet<>()).add(name);
	    }
	}
    }

    private static Boolean isReadOnlyAttributeValues(String type) {
	List<String> predefinedValuesForAttribute = TbxDescriptionTypes.getPredefinedValuesForAttribute(type);
	return CollectionUtils.isNotEmpty(predefinedValuesForAttribute);
    }

    private static Boolean isReadOnlyNoteValues(String type) {
	List<String> predefinedValuesForAttribute = TbxNoteTypes.getPredefinedValuesForNote(type);
	return CollectionUtils.isNotEmpty(predefinedValuesForAttribute);
    }
}
