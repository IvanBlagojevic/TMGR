package org.gs4tr.termmanager.webmvc.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.modules.entities.model.CleanupCallback;
import org.gs4tr.foundation.modules.entities.model.RepositoryItem;
import org.gs4tr.foundation.modules.entities.model.ResourceInfo;
import org.gs4tr.foundation.modules.service.EntityFinder;
import org.gs4tr.foundation.modules.usermanager.service.SessionService;
import org.gs4tr.foundation.modules.webmvc.controllers.AbstractController;
import org.gs4tr.foundation.modules.webmvc.model.ModelMapResponse;
import org.gs4tr.termmanager.model.Disposition;
import org.gs4tr.termmanager.model.DtoEntityAware;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.service.PluginsService;
import org.gs4tr.termmanager.service.UserProfileService;
import org.gs4tr.termmanager.service.manualtask.ManualTaskHandler;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;
import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.gs4tr.termmanager.webmvc.model.commands.TaskCommand;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@Controller
public class TaskController extends AbstractController {

    private static final String DEFAULT_MIME_TYPE = "text/html;charset=utf-8";

    private final Log LOGGER = LogFactory.getLog(this.getClass());

    @Autowired
    private PluginsService _pluginsService;

    @Autowired
    private SessionService _sessionService;

    @Autowired
    private UserProfileService _userProfileService;

    @RequestMapping(value = "task.ter", method = RequestMethod.POST)
    @ResponseBody
    public ModelMapResponse onSubmit(HttpServletRequest request, HttpServletResponse response,
	    @ModelAttribute TaskCommand taskCommand) throws Exception {

	List<UploadedRepositoryItem> repositoryItems = new ArrayList<UploadedRepositoryItem>();

	if (request instanceof MultipartHttpServletRequest) {
	    MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

	    Map<String, MultipartFile> tempFileMap = multipartRequest.getFileMap();

	    repositoryItems = convertMultipartFilesToRepositoryItems(tempFileMap);
	}

	ManualTaskHandler taskHandler = getPluginsService().getUserTaskHandler(taskCommand.getTaskName());

	logUserAction(taskCommand);

	Object taskHandlerCommand = getTaskHandlerCommand(taskHandler, taskCommand);

	TaskResponse taskResponse = taskHandler.processTasks(
		TicketConverter.fromDtoToInternal(taskCommand.getParentTickets(), Long.class),
		TicketConverter.fromDtoToInternal(taskCommand.getTaskTickets(), Long.class), taskHandlerCommand,
		repositoryItems);

	RepositoryItem outputRepositoryItem = taskResponse.getRepositoryItem();

	if (request instanceof MultipartHttpServletRequest) {
	    // (MM) MultipartHttpServletRequest is used for data upload. In case
	    // of upload we need to return empty page from UI to function
	    // properly. We need to return default mime type in order to have
	    // browser functioning properly

	    response.setContentType(DEFAULT_MIME_TYPE);

	} else if (outputRepositoryItem != null) {
	    return handleRepositoryItem(response, taskHandler, outputRepositoryItem);
	}

	ModelMapResponse modelMap = new ModelMapResponse();
	modelMap.put("taskResponse", taskResponse);

	return modelMap;

    }

    @RequestMapping(value = "task.ter", method = RequestMethod.GET)
    @ResponseBody
    public ModelMapResponse showFormGet(HttpServletRequest request, HttpServletResponse response,
	    @ModelAttribute TaskCommand taskCommand) throws Exception {

	return showForm(request, response, taskCommand);
    }

    @RequestMapping(value = "taskPost.ter", method = RequestMethod.POST)
    @ResponseBody
    public ModelMapResponse showFormPost(HttpServletRequest request, HttpServletResponse response,
	    @ModelAttribute TaskCommand taskCommand) throws Exception {

	return showForm(request, response, taskCommand);
    }

    private ResourceInfo convertMultipartFileIntoResourceInfo(MultipartFile file) {
	ResourceInfo resourceInfo = new ResourceInfo();

	resourceInfo.setName(file.getOriginalFilename());
	resourceInfo.setMimeType(file.getContentType());
	resourceInfo.setSize(file.getSize());

	return resourceInfo;
    }

    private List<UploadedRepositoryItem> convertMultipartFilesToRepositoryItems(Map<String, MultipartFile> fileMap) {
	List<UploadedRepositoryItem> items = new ArrayList<UploadedRepositoryItem>();

	for (Map.Entry<String, MultipartFile> entry : fileMap.entrySet()) {
	    RepositoryItem repositoryItem = new RepositoryItem();

	    MultipartFile file = entry.getValue();

	    if (file.getSize() == 0) {
		continue;
	    }

	    repositoryItem.setResourceInfo(convertMultipartFileIntoResourceInfo(file));

	    try {
		repositoryItem.setInputStream(file.getInputStream());
	    } catch (IOException e) {
		throw new RuntimeException(e.getMessage(), e);
	    }

	    items.add(new UploadedRepositoryItem(repositoryItem, entry.getKey()));
	}

	return items;
    }

    private PluginsService getPluginsService() {
	return _pluginsService;
    }

    private SessionService getSessionService() {
	return _sessionService;
    }

    private ManualTaskHandler getTargetObject(Object proxy) {
	if (AopUtils.isJdkDynamicProxy(proxy)) {
	    try {
		return (ManualTaskHandler) ((Advised) proxy).getTargetSource().getTarget();
	    } catch (Exception e) {
		throw new RuntimeException("Aop proxy object conversion error", e);
	    }
	} else {
	    return (ManualTaskHandler) proxy;
	}
    }

    private Object getTaskHandlerCommand(ManualTaskHandler taskHandler, TaskCommand taskCommand) {
	Class<? extends DtoTaskHandlerCommand<?>> taskHandlerCommandClazz = taskHandler.getCommandClass();

	Object taskHandlerCommand = null;

	if (taskHandlerCommandClazz != null) {
	    String jsonTaskData = taskCommand.getJsonTaskData();

	    if (StringUtils.isBlank(jsonTaskData)) {
		return null;
	    }

	    DtoTaskHandlerCommand<?> dtoTaskHandlerCommand = JsonUtils.readValue(jsonTaskData, taskHandlerCommandClazz);

	    if (dtoTaskHandlerCommand == null) {
		return null;
	    }

	    taskHandlerCommand = dtoTaskHandlerCommand.convertToInternalTaskHandlerCommand();
	}

	return taskHandlerCommand;
    }

    private UserProfileService getUserProfileService() {
	return _userProfileService;
    }

    private ModelMapResponse handleRepositoryItem(HttpServletResponse response, ManualTaskHandler taskHandler,
	    RepositoryItem outputRepositoryItem) throws IOException {
	Boolean disposition = Boolean.FALSE;

	/*
	 * NOTE: Conversion is added because of sumo logic API (taskHandler object can
	 * be in proxy wrapper)
	 */
	taskHandler = getTargetObject(taskHandler);

	if (taskHandler.getClass().isAnnotationPresent(Disposition.class)) {
	    disposition = Boolean.TRUE;
	}
	writeOuputRepositoryItem(response, outputRepositoryItem, disposition);

	return null;
    }

    private void logUserAction(TaskCommand taskCommand) {
	LOGGER.info(taskCommand.toString());
    }

    // (MM) It will only convert first level of the model map into dto entities
    private void postProcessTaskModel(TaskModel taskModel) {
	Map<String, Object> model = taskModel.getModel();

	final List<String> keys = new ArrayList<String>();

	EntityFinder.find(model, new EntityFinder.EntityClosure<DtoEntityAware<?>>() {
	    @Override
	    public boolean appliesTo(Object input) {
		return (input instanceof DtoEntityAware);
	    }

	    @Override
	    public void execute(Object key, DtoEntityAware<?> dtoEntityAware) {
		keys.add((String) key);
	    }
	});

	for (String key : keys) {
	    DtoEntityAware<?> dtoEntityAware = (DtoEntityAware<?>) model.get(key);

	    model.put(key, dtoEntityAware.convertToDtoEntity());
	}
    }

    private ModelMapResponse showForm(HttpServletRequest request, HttpServletResponse response, TaskCommand taskCommand)
	    throws Exception {

	ManualTaskHandler taskHandler = getPluginsService().getUserTaskHandler(taskCommand.getTaskName());

	Object taskHandlerCommand = getTaskHandlerCommand(taskHandler, taskCommand);

	Long[] parentIds = TicketConverter.fromDtoToInternal(taskCommand.getParentTickets(), Long.class);

	ModelMapResponse modelMap = new ModelMapResponse();

	TaskModel[] taskModels = taskHandler.getTaskInfos(parentIds, taskCommand.getTaskName(), taskHandlerCommand);

	if (taskModels != null) {
	    for (TaskModel taskModel : taskModels) {
		postProcessTaskModel(taskModel);
	    }

	    // (MM): We support download on GET only for single item selection.
	    // Multiple item selection should return ZIP archive.
	    if (taskModels.length == 1) {
		RepositoryItem outputRepositoryItem = taskModels[0].getRepositoryItem();

		if (outputRepositoryItem != null) {
		    return handleRepositoryItem(response, taskHandler, outputRepositoryItem);
		}
	    }
	}

	modelMap.put("taskInfos", taskModels); //$NON-NLS-1$

	return modelMap;
    }

    private void writeOuputRepositoryItem(HttpServletResponse response, RepositoryItem outputRepositoryItem,
	    Boolean disposition) throws IOException {
	InputStream in = outputRepositoryItem.getInputStream();

	DownloadUtils.fillDownloadResponse(outputRepositoryItem, response, disposition);

	in.close();
	CleanupCallback cleanupCallback = outputRepositoryItem.getCleanupCallback();
	if (cleanupCallback != null) {
	    cleanupCallback.cleanup();
	}
    }
}