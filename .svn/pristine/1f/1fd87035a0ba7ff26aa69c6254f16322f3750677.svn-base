package org.gs4tr.termmanager.service.manualtask;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.gs4tr.foundation.modules.entities.model.ResourceInfo;
import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.service.TermEntryService;
import org.gs4tr.termmanager.service.model.command.UploadResourceCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoUploadResourceCommand;
import org.springframework.beans.factory.annotation.Autowired;

public class UploadTermEntryReferencesTaskHandler extends AbstractManualTaskHandler {

    @Autowired
    private TermEntryService _termEntryService;

    @Override
    public Class<? extends DtoTaskHandlerCommand<?>> getCommandClass() {
	return DtoUploadResourceCommand.class;
    }

    @Override
    public SelectStyleEnum getSelectStyle() {
	return SelectStyleEnum.SINGLE_SELECT;
    }

    public TermEntryService getTermEntryService() {
	return _termEntryService;
    }

    @Override
    public TaskResponse processTasks(Long[] parentIds, Long[] taskIds, Object command,
	    List<UploadedRepositoryItem> files) {

	UploadResourceCommand uploadResourceCommand = (UploadResourceCommand) command;
	String termEntryId = uploadResourceCommand.getTermEntryId();

	Validate.notEmpty(termEntryId);

	Map<String, String> attributeTypeFileNameMap = uploadResourceCommand.getAttributeTypeFileNameMap();

	for (UploadedRepositoryItem uploadedItem : files) {

	    String attributeType = null;

	    String nameParameter = uploadedItem.getNameParameter();
	    for (Map.Entry<String, String> map : attributeTypeFileNameMap.entrySet()) {
		if (map.getKey().equals(nameParameter)) {
		    attributeType = map.getValue();
		    break;
		}
	    }

	    validateAttributeType(attributeType);

	    ResourceInfo resourceInfo = uploadedItem.getRepositoryItem().getResourceInfo();

	    Map<String, String> resourceTicketForEditingMap = uploadResourceCommand.getResourceTicketForEditingMap();

	    if (resourceTicketForEditingMap.containsKey(nameParameter)) {
		getTermEntryService().updateTermEntryResourceTrack(termEntryId,
			resourceTicketForEditingMap.get(nameParameter), uploadedItem.getRepositoryItem());

	    }

	    // resourceInfo.setType(ResourceType.REFERENCE);

	    getTermEntryService().uploadBinaryResource(termEntryId, resourceInfo,
		    uploadedItem.getRepositoryItem().getInputStream(), attributeType);
	}

	return new TaskResponse(null);
    }

    private void validateAttributeType(String attributeType) {
	if (StringUtils.isBlank(attributeType)) {
	    throw new RuntimeException(Messages.getString("UploadTermEntryReferencesTaskHandler.0")); //$NON-NLS-1$
	}

    }
}
