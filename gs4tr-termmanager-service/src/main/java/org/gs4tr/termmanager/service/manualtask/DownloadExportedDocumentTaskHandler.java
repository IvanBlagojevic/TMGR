package org.gs4tr.termmanager.service.manualtask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.gs4tr.foundation.modules.entities.model.CleanupCallback;
import org.gs4tr.foundation.modules.entities.model.RepositoryItem;
import org.gs4tr.foundation.modules.entities.model.ResourceInfo;
import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;
import org.gs4tr.foundation.modules.entities.model.UserException;
import org.gs4tr.foundation.modules.webmvc.i18n.MessageResolver;
import org.gs4tr.termmanager.cache.CacheGateway;
import org.gs4tr.termmanager.cache.model.CacheName;
import org.gs4tr.termmanager.model.Disposition;
import org.gs4tr.termmanager.model.ExportInfo;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.service.export.ExportFormatEnum;
import org.gs4tr.termmanager.service.impl.ExportAdapter;
import org.gs4tr.termmanager.service.impl.ExportDocumentStatusInfo;
import org.gs4tr.termmanager.service.model.command.DownloadExportedDocumentCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoDownloadExportedDocumentCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;
import org.gs4tr.termmanager.service.utils.ServiceUtils;
import org.gs4tr.termmanager.service.utils.TaskHandlerConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@Disposition
public class DownloadExportedDocumentTaskHandler extends AbstractManualTaskHandler {

    @Autowired
    @Qualifier("hzCacheGateway")
    private CacheGateway<String, ExportAdapter> _cacheGateway;

    @Override
    public Class<? extends DtoTaskHandlerCommand<?>> getCommandClass() {
	return DtoDownloadExportedDocumentCommand.class;
    }

    @Override
    public SelectStyleEnum getSelectStyle() {
	return SelectStyleEnum.NO_SELECT;
    }

    @Override
    public TaskResponse processTasks(Long[] parentIds, Long[] taskIds, Object command,
	    List<UploadedRepositoryItem> files) {
	DownloadExportedDocumentCommand downloadExportedDocumentCommand = (DownloadExportedDocumentCommand) command;
	String threadName = downloadExportedDocumentCommand.getThreadName();

	ExportDocumentStatusInfo statusInfo = getCacheGateway().getAndRemove(CacheName.EXPORT_PROGRESS_STATUS,
		threadName);

	if (statusInfo == null) {
	    throw new UserException(Messages.getString("DownloadExportedDocumentTaskHandler.1")); //$NON-NLS-1$
	}

	if (!statusInfo.isProcessingFinished()) {
	    String message = MessageResolver.getMessage("DownloadExportedDocument.3");//$NON-NLS-1$
	    throw new UserException(MessageResolver.getMessage("DownloadExportedDocumentTaskHandler.0"), message); //$NON-NLS-1$
	}

	TaskResponse taskResponse = new TaskResponse(null);

	addRepositoryItem(taskResponse, statusInfo.getExportInfo(), downloadExportedDocumentCommand.getXslName());

	return taskResponse;
    }

    private void addRepositoryItem(TaskResponse taskResponse, ExportInfo exportInfo, String exportFormat) {
	final File file = exportInfo.getTempFile();
	String projectName = exportInfo.getProjectName();
	String shortCode = exportInfo.getShortCode();

	ExportFormatEnum transformation = ExportFormatEnum.valueOf(exportFormat);

	String fileName = resolveFileName(projectName, shortCode, transformation);

	try {
	    InputStream stream = new FileInputStream(file);
	    RepositoryItem exportItem = createExportItem(fileName, stream, file.length());

	    taskResponse.setRepositoryItem(exportItem);

	    exportItem.setCleanupCallback(new CleanupCallback() {
		@Override
		public void cleanup() {
		    try {
			ServiceUtils.closeInputStream(stream);
			FileUtils.forceDelete(file);
		    } catch (Exception e) {
			throw new RuntimeException(e);
		    }
		}
	    });
	} catch (FileNotFoundException e) {
	    throw new RuntimeException(e);
	}
    }

    private RepositoryItem createExportItem(String fileName, InputStream fileInputStream, long fileSize) {
	RepositoryItem exportItem = new RepositoryItem();
	exportItem.setInputStream(fileInputStream);

	ResourceInfo exportResourceInfo = new ResourceInfo();
	exportResourceInfo.setSize(fileSize);
	exportResourceInfo.setName(fileName);

	exportItem.setResourceInfo(exportResourceInfo);
	return exportItem;
    }

    private CacheGateway<String, ExportAdapter> getCacheGateway() {
	return _cacheGateway;
    }

    private String resolveFileName(String projectName, String shortCode, ExportFormatEnum transformation) {
	String fileFormat = ExportFormatEnum.TBX == transformation ? TaskHandlerConstants.ZIP
		: transformation.getFileFormat();
	return ServiceUtils.generateExportDocumentName(projectName, shortCode, fileFormat);
    }
}
