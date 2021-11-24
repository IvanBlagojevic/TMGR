package groovy.taskController

import org.gs4tr.foundation.modules.entities.model.Ticket
import org.gs4tr.termmanager.model.dto.TmProjectDto
import org.gs4tr.termmanager.model.dto.TmProjectDto.TermStatusDto;
import org.gs4tr.termmanager.service.manualtask.ImportTbxDocumentTaskHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.apache.commons.lang.StringUtils;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.service.termentry.synchronization.SyncOption;
import org.gs4tr.termmanager.service.utils.TaskHandlerConstants;
import org.gs4tr.termmanager.model.ImportErrorAction;

TmProjectDto tmProjectDto = new TmProjectDto("Skype", 1L);
tmProjectDto.addLanguage("en-US");
tmProjectDto.addLanguage("fr-FR");

tmProjectDtos = [tmProjectDto] as List;

tmProjectDto.setDefaultTermStatus(new TermStatusDto(ItemStatusTypeHolder.PROCESSED));

tmProjectDto.addTermStatus(new TermStatusDto(ItemStatusTypeHolder.PROCESSED));
tmProjectDto.addTermStatus(new TermStatusDto(ItemStatusTypeHolder.WAITING));

TaskModel taskModel = new TaskModel();
taskModel.addObject(TaskHandlerConstants.PROJECTS, tmProjectDtos);
taskModel.addObject(ImportTbxDocumentTaskHandler.DEFAULT_IMPORT_TYPE, SyncOption.OVERWRITE);
taskModel.addObject(ImportTbxDocumentTaskHandler.IMPORT_TYPES_KEY, ImportTbxDocumentTaskHandler.IMPORT_TYPES);
taskModel.addObject(ImportTbxDocumentTaskHandler.IGNORE_CASE, true);

taskModels= [taskModel] as TaskModel[]
