package org.gs4tr.termmanager.model.dto.converter;

import org.gs4tr.termmanager.model.BatchJobInfo;

public class BatchJobInfoConverter {

    public static org.gs4tr.termmanager.model.dto.BatchProcessingItem fromInternalToDto(BatchJobInfo internalEntity) {
	if (internalEntity == null) {
	    return null;
	}

	org.gs4tr.termmanager.model.dto.BatchProcessingItem dtoEntity = new org.gs4tr.termmanager.model.dto.BatchProcessingItem();

	dtoEntity.setAlertMessages(internalEntity.getAlertMessages());
	dtoEntity.setBatchProcess(internalEntity.getBatchJobName().getProcessDisplayName());
	dtoEntity.setMsgSubTitle(internalEntity.getMsgSubTitle());
	dtoEntity.setMsgLongSubTitle(internalEntity.getMsgLongSubTitle());
	dtoEntity.setResourceTrack(internalEntity.getResourceTrack());
	dtoEntity.setSucessMessage(internalEntity.getSuccessMessage());
	dtoEntity.setLongSucessMessage(internalEntity.getLongSuccessMessage());
	dtoEntity.setTitle(internalEntity.getTitle());
	dtoEntity.setExceptionMessage(internalEntity.getExceptionMessage());

	return dtoEntity;
    }
}
