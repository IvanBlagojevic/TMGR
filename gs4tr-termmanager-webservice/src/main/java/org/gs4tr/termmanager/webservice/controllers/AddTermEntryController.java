package org.gs4tr.termmanager.webservice.controllers;

import static org.apache.commons.lang.Validate.isTrue;
import static org.apache.commons.lang.Validate.notEmpty;
import static org.gs4tr.termmanager.model.StringConstants.PIPE;
import static org.gs4tr.termmanager.webservice.model.response.ReturnCode.OK;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.eventlogging.spring.aop.EventLogger;
import org.gs4tr.eventlogging.spring.aop.annotation.LogEvent;
import org.gs4tr.termmanager.model.Attribute;
import org.gs4tr.termmanager.model.AttributeLevelEnum;
import org.gs4tr.termmanager.model.BaseTypeEnum;
import org.gs4tr.termmanager.model.InputFieldTypeEnum;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.ProjectPolicyEnum;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.TranslationUnit;
import org.gs4tr.termmanager.model.UpdateCommand;
import org.gs4tr.termmanager.model.dto.Description;
import org.gs4tr.termmanager.model.dto.TermV2ModelExtended;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.model.glossary.Action;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.service.TermEntryService;
import org.gs4tr.termmanager.service.logging.util.EventContextConstants;
import org.gs4tr.termmanager.service.logging.util.TMGREventActionConstants;
import org.gs4tr.termmanager.service.utils.ServiceUtils;
import org.gs4tr.termmanager.webservice.exceptions.UnauthorizedAccessException;
import org.gs4tr.termmanager.webservice.model.request.AddTermsCommand;
import org.gs4tr.termmanager.webservice.model.response.AddTermEntryResponse;
import org.gs4tr.termmanager.webservice.model.response.BaseResponse;
import org.gs4tr.termmanager.webservice.model.response.ErrorResponse;
import org.gs4tr.termmanager.webservice.utils.V2Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * This class provide option to add terms as a part of one term entry in
 * specified Term Manager project.
 *
 * @author TMGR_Backend
 */
@RequestMapping("/rest/v2/addTerm")
@RestController
@Api(value = "Add Term Entry")
public class AddTermEntryController {

    private static final String[] ADD_POLICIES = new String[] {
	    ProjectPolicyEnum.POLICY_TM_TERM_ADD_APPROVED_TERM.toString(),
	    ProjectPolicyEnum.POLICY_TM_TERM_ADD_PENDING_TERM.toString(),
	    ProjectPolicyEnum.POLICY_TM_TERM_ADD_ON_HOLD_TERM.toString(),
	    ProjectPolicyEnum.POLICY_TM_TERM_ADD_BLACKLIST_TERM.toString() };

    private static final Log LOGGER = LogFactory.getLog(AddTermEntryController.class);

    private static final String PIPE_REGEX = Pattern.quote(PIPE);

    @Autowired
    private ProjectService _projectService;

    @Autowired
    private TermEntryService _termEntryService;

    @ApiResponses(value = {
	    @ApiResponse(code = 200, message = "Successful operation.", response = AddTermEntryResponse.class),
	    @ApiResponse(code = 400, message = "Missing required parameter/Invalid parameter.", response = ErrorResponse.class),
	    @ApiResponse(code = 500, message = "Internal server error.") })
    @ApiOperation(value = "Use this method to add or update term entry with terms and attributes/notes to the specified project/glossary.", httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @LogEvent(action = TMGREventActionConstants.ACTION_ADD_TERM, actionCategory = TMGREventActionConstants.ACTION_TYPE_REST_V2)
    public BaseResponse addTermEntry(@RequestBody AddTermsCommand command) throws UnauthorizedAccessException {

	// Project ticket and terms(locale and term text) are mandatory.
	validateCommand(command);

	Long projectId = TicketConverter.fromDtoToInternal(command.getProjectTicket(), Long.class);
	EventLogger.addProperty(EventContextConstants.PROJECT_ID, projectId);

	TmProject project = V2Utils.getProject(projectId, getProjectService());
	EventLogger.addProperty(EventContextConstants.PROJECT_NAME, project.getProjectInfo().getName());
	EventLogger.addProperty(EventContextConstants.PROJECT_SHORT_CODE, project.getProjectInfo().getShortCode());

	logEvent(command, project);

	try {
	    isTrue(validateProjectPolicies(projectId), Messages.getString("UserAddPoliciesError"));

	    isTrue(validateLanguageIds(projectId, command), Messages.getString("ProjectLanguageError"));

	    List<Attribute> attributes = getProjectService().getAttributesByProjectId(projectId);

	    Map<String, Set<String>> comboAttributesByType = collectComboAttributesByType(attributes);

	    isTrue(validateTermEntryDescriptions(command, attributes, comboAttributesByType),
		    Messages.getString("ProjectTermEntryDescriptionError"));
	    isTrue(validateTermDescriptions(command, attributes, comboAttributesByType),
		    Messages.getString("ProjectTermDescriptionError"));
	} catch (Exception e) {
	    // If user doesn't have add policies.
	    // If term description doesn't exist on the project.
	    // If term entry attribute doesn't exist on the project.
	    // If user try to add term for not existing language.
	    throw new UnauthorizedAccessException(e.getMessage(), e);
	}

	List<TranslationUnit> translationUnits = createTranslationUnits(command, project);

	getTermEntryService().updateTermEntries(translationUnits, null, projectId, resolveAction(command));

	return new AddTermEntryResponse(OK, true);
    }

    private Map<String, Set<String>> collectComboAttributesByType(List<Attribute> attributes) {
	if (CollectionUtils.isEmpty(attributes)) {
	    return Collections.EMPTY_MAP;
	}

	Map<String, Set<String>> comboAttributes = new HashMap<>();

	attributes.stream()
		.filter(a -> StringUtils.isNotEmpty(a.getComboValues())
			|| InputFieldTypeEnum.COMBO == a.getInputFieldTypeEnum())
		.collect(Collectors.toList()).forEach(a -> comboAttributes
			.computeIfAbsent(a.getName(), f -> new HashSet<>()).addAll(getComboValues(a)));

	return comboAttributes;
    }

    private Set<String> collectDescriptionAttributeTypes(Collection<Description> descriptions) {
	if (CollectionUtils.isEmpty(descriptions)) {
	    return new HashSet<>();
	}

	return descriptions.stream().filter(d -> Description.ATTRIBUTE.equals(d.getBaseType()))
		.filter(d -> StringUtils.isNotEmpty(d.getType())).map(Description::getType).collect(Collectors.toSet());
    }

    private Set<String> collectDescriptionNoteTypes(Collection<Description> descriptions) {
	if (CollectionUtils.isEmpty(descriptions)) {
	    return new HashSet<>();
	}

	return descriptions.stream().filter(d -> Description.NOTE.equals(d.getBaseType()))
		.filter(d -> StringUtils.isNotEmpty(d.getType())).map(Description::getType).collect(Collectors.toSet());
    }

    private Set<String> collectLanguageIds(AddTermsCommand command) {
	return command.getTerms().stream().map(TermV2ModelExtended::getLocale).collect(Collectors.toSet());
    }

    private Set<String> collectTermAttributeTypes(Collection<Attribute> attributes) {
	if (CollectionUtils.isEmpty(attributes)) {
	    return new HashSet<>();
	}

	return attributes.stream().filter(a -> AttributeLevelEnum.LANGUAGE == a.getAttributeLevel())
		.filter(a -> BaseTypeEnum.DESCRIPTION == a.getBaseTypeEnum()).map(Attribute::getName)
		.collect(Collectors.toSet());
    }

    private Set<Description> collectTermDescriptions(AddTermsCommand command) {
	Set<Description> descriptions = new HashSet<>();
	command.getTerms().stream().filter(t -> CollectionUtils.isNotEmpty(t.getDescriptions()))
		.forEach(t -> descriptions.addAll(t.getDescriptions()));
	return descriptions;
    }

    private Set<String> collectTermEntryAttributeTypes(Collection<Attribute> attributes) {
	if (CollectionUtils.isEmpty(attributes)) {
	    return new HashSet<>();
	}

	return attributes.stream().filter(a -> AttributeLevelEnum.TERMENTRY == a.getAttributeLevel())
		.filter(a -> BaseTypeEnum.DESCRIPTION == a.getBaseTypeEnum()).map(Attribute::getName)
		.collect(Collectors.toSet());
    }

    private Set<String> collectTermNoteTypes(Collection<Attribute> attributes) {
	if (CollectionUtils.isEmpty(attributes)) {
	    return new HashSet<>();
	}

	return attributes.stream().filter(a -> BaseTypeEnum.NOTE == a.getBaseTypeEnum()).map(Attribute::getName)
		.collect(Collectors.toSet());
    }

    private boolean containsAllTypes(Collection<String> attributeTypes, Collection<String> desciptionTypes) {
	return !CollectionUtils.isNotEmpty(desciptionTypes) || attributeTypes.containsAll(desciptionTypes);
    }

    private UpdateCommand createDescriptionUpdateCommand(String termEntryId, String addCommand, String descItem,
	    Description description, String languageId) {
	UpdateCommand cmd = new UpdateCommand();
	cmd.setCommand(addCommand);
	cmd.setMarkerId(resolveDescriptionMarkerId(description.getMarkerId()));
	cmd.setParentMarkerId(termEntryId);
	cmd.setItemType(descItem);
	cmd.setSubType(description.getType());
	cmd.setValue(description.getValue());
	cmd.setLanguageId(languageId);
	return cmd;
    }

    private List<TranslationUnit> createTranslationUnits(AddTermsCommand command, TmProject project) {

	String termEntryId = resolveId(command.getTermEntryId());

	TranslationUnit tu = new TranslationUnit();
	tu.setTermEntryId(termEntryId);

	String descItem = UpdateCommand.TypeEnum.DESCRIP.getName();
	String noteItem = UpdateCommand.TypeEnum.NOTE.getName();
	String termItem = UpdateCommand.TypeEnum.TERM.getName();

	Collection<Description> termEntryDescriptions = command.getTermEntryDescriptions();
	if (CollectionUtils.isNotEmpty(termEntryDescriptions)) {
	    for (Description description : termEntryDescriptions) {

		UpdateCommand cmd = createDescriptionUpdateCommand(termEntryId,
			resolveCommand(description.getMarkerId()), descItem, description, null);
		tu.addSourceTermUpdateCommand(cmd);
	    }
	}

	Collection<TermV2ModelExtended> terms = command.getTerms();

	for (TermV2ModelExtended term : terms) {

	    String termId = term.getTermId();

	    UpdateCommand cmd = new UpdateCommand();
	    cmd.setCommand(resolveCommand(termId));
	    cmd.setMarkerId(resolveId(termId));
	    cmd.setParentMarkerId(termEntryId);
	    cmd.setItemType(termItem);
	    cmd.setValue(term.getTermText());
	    cmd.setLanguageId(term.getLocale());
	    try {
		if (StringUtils.isNotEmpty(term.getStatus())) {
		    cmd.setStatus(term.isForbidden() ? ItemStatusTypeHolder.BLACKLISTED.getName()
			    : ServiceUtils.resolveStatusName(term.getStatus()));
		} else {
		    cmd.setStatus(project.getDefaultTermStatus().getName());
		}
	    } catch (Exception e) {
		throw new IllegalArgumentException(e.getMessage(), e);
	    }

	    tu.addSourceTermUpdateCommand(cmd);

	    Collection<Description> descriptions = term.getDescriptions();
	    if (CollectionUtils.isNotEmpty(descriptions)) {
		for (Description description : descriptions) {

		    String typeItem = resolveDescriptionType(descItem, noteItem, description);
		    UpdateCommand descCmd = createDescriptionUpdateCommand(cmd.getMarkerId(),
			    resolveCommand(description.getMarkerId()), typeItem, description, term.getLocale());
		    tu.addSourceTermUpdateCommand(descCmd);
		}
	    }
	}

	return Collections.singletonList(tu);
    }

    private Set<String> getComboValues(Attribute attribute) {
	if (StringUtils.isEmpty(attribute.getComboValues())) {
	    return Collections.EMPTY_SET;
	}
	String[] comboValues = attribute.getComboValues().split(PIPE_REGEX);
	return new HashSet<>(Arrays.asList(comboValues));
    }

    private ProjectService getProjectService() {
	return _projectService;
    }

    private TermEntryService getTermEntryService() {
	return _termEntryService;
    }

    private void logEvent(AddTermsCommand command, TmProject project) {
	if (LOGGER.isDebugEnabled()) {
	    String projectName = V2Utils.getProjectName(project);
	    LOGGER.debug(String.format(Messages.getString("AddTermEntryController.0"), //$NON-NLS-1$
		    TmUserProfile.getCurrentUserName(), command.toString(), projectName));
	}
    }

    private Action resolveAction(AddTermsCommand command) {
	return StringUtils.isEmpty(command.getTermEntryId()) ? Action.ADDED_REMOTELY : Action.EDITED_REMOTELY;
    }

    private String resolveCommand(String uuid) {
	return StringUtils.isEmpty(uuid) ? UpdateCommand.CommandEnum.ADD.getName()
		: UpdateCommand.CommandEnum.UPDATE.getName();
    }

    private String resolveDescriptionMarkerId(String markerId) {
	return markerId != null ? markerId : UUID.randomUUID().toString();

    }

    private String resolveDescriptionType(String descItem, String noteItem, Description description) {
	String typeItem = Description.NOTE.equals(description.getBaseType()) ? noteItem : descItem;
	return typeItem;
    }

    private String resolveId(String uuid) {
	return StringUtils.isEmpty(uuid) ? UUID.randomUUID().toString() : uuid;
    }

    private boolean validateAllComboAttributes(Map<String, Set<String>> comboAttributesByType,
	    Collection<Description> descriptions) {
	if (Objects.isNull(descriptions)) {
	    return true;
	}
	for (Description description : descriptions) {

	    Set<String> comboValues = comboAttributesByType.get(description.getType());

	    if (Objects.isNull(comboValues)) {
		continue;
	    }

	    if (!comboValues.contains(description.getValue())) {
		return false;
	    }
	}
	return true;
    }

    private void validateCommand(AddTermsCommand command) {
	notEmpty(command.getProjectTicket(), Messages.getString("ProjectTicketError"));

	Collection<TermV2ModelExtended> terms = command.getTerms();
	notEmpty(terms, Messages.getString("TermsError"));

	for (TermV2ModelExtended term : terms) {
	    notEmpty(term.getLocale(), Messages.getString("TermLanguageError"));
	    notEmpty(term.getTermText(), Messages.getString("TermTextError"));

	    Collection<Description> descriptions = term.getDescriptions();
	    if (CollectionUtils.isEmpty(descriptions)) {
		continue;
	    }
	    descriptions.forEach(this::validateDescriptionFields);
	}

	Collection<Description> descriptions = command.getTermEntryDescriptions();
	if (CollectionUtils.isNotEmpty(descriptions)) {
	    descriptions.forEach(this::validateDescriptionFields);
	}
    }

    /*
     * TERII-5116 Rest V2: Able to add terms with null attribute values and
     * attribute definitions
     */
    private void validateDescriptionFields(Description description) {
	if (Objects.nonNull(description)) {

	    String baseType = description.getBaseType();
	    String type = description.getType();
	    String value = description.getValue();

	    if (StringUtils.isEmpty(baseType)) {
		description.setBaseType(Description.ATTRIBUTE);
	    }

	    isTrue(StringUtils.isNotEmpty(type), Messages.getString("DescriptionTypeError"));
	    isTrue(StringUtils.isNotEmpty(value), Messages.getString("DescriptionValueError"));
	}
    }

    private boolean validateLanguageIds(Long projectId, AddTermsCommand command) {
	TmUserProfile user = TmUserProfile.getCurrentUserProfile();

	Map<Long, Set<String>> userLanguages = user.getProjectUserLanguages();
	Set<String> projectUserLanguages = userLanguages.get(projectId);
	return projectUserLanguages != null && projectUserLanguages.containsAll(collectLanguageIds(command));

    }

    private boolean validateProjectPolicies(Long projectId) {
	TmUserProfile user = TmUserProfile.getCurrentUserProfile();
	return user.containsContextPolicies(projectId, ADD_POLICIES);
    }

    @SuppressWarnings("unused")
    private boolean validateTermDescriptions(AddTermsCommand command, List<Attribute> attributes,
	    Map<String, Set<String>> comboAttributesByType) {

	Set<String> projectAttributeTypes = collectTermAttributeTypes(attributes);

	Set<String> projectNoteTypes = collectTermNoteTypes(attributes);

	Set<Description> descriptions = collectTermDescriptions(command);

	Set<String> attributeTypes = collectDescriptionAttributeTypes(descriptions);

	Set<String> noteTypes = collectDescriptionNoteTypes(descriptions);

	boolean isValidAllComboAttributes = validateAllComboAttributes(comboAttributesByType, descriptions);

	return isValidAllComboAttributes && containsAllTypes(projectAttributeTypes, attributeTypes)
		&& containsAllTypes(projectNoteTypes, noteTypes);
    }

    @SuppressWarnings("unused")
    private boolean validateTermEntryDescriptions(AddTermsCommand command, List<Attribute> attributes,
	    Map<String, Set<String>> comboAttributesByType) {

	Set<String> attributeTypes = collectTermEntryAttributeTypes(attributes);

	Set<String> desciptionTypes = collectDescriptionAttributeTypes(command.getTermEntryDescriptions());

	boolean isValidAllComboAttributes = validateAllComboAttributes(comboAttributesByType,
		command.getTermEntryDescriptions());

	return isValidAllComboAttributes && containsAllTypes(attributeTypes, desciptionTypes);
    }
}
