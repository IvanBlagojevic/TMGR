package org.gs4tr.termmanager.service.manualtask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.modules.entities.model.LogHelper;
import org.gs4tr.foundation.modules.entities.model.RepositoryItem;
import org.gs4tr.foundation.modules.entities.model.SelectStyleEnum;
import org.gs4tr.foundation.modules.entities.model.UserException;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.service.file.manager.FileManager;
import org.gs4tr.termmanager.service.file.manager.ImportFileNameMaker;
import org.gs4tr.termmanager.service.model.command.FileManagerCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoFileManagerCommand;
import org.gs4tr.termmanager.service.model.command.dto.DtoTaskHandlerCommand;
import org.gs4tr.termmanager.service.model.command.dto.filemanagment.DtoFile;
import org.gs4tr.termmanager.service.model.command.dto.filemanagment.DtoFileManagmentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

public class FileManagerTaskHandler extends AbstractManualTaskHandler {

    private static final Log LOG = LogFactory.getLog(FileManagerTaskHandler.class);

    private static final String UPLOADS = "uploads";

    @Autowired
    private FileManager _fileManager;

    @Override
    public Class<? extends DtoTaskHandlerCommand<?>> getCommandClass() {
	return DtoFileManagerCommand.class;
    }

    @Override
    public SelectStyleEnum getSelectStyle() {
	return SelectStyleEnum.NO_SELECT;
    }

    @Override
    public TaskResponse processTasks(Long[] parentIds, Long[] taskIds, Object command,
	    List<UploadedRepositoryItem> files) {

	String subFolder = null;

	FileManagerCommand cmd = (FileManagerCommand) command;
	String action = cmd.getAction();
	if (FileManagerCommand.Action.ADD.getActionName().equals(action)) {
	    subFolder = storeFiles(cmd, files);
	} else if (FileManagerCommand.Action.REMOVE.getActionName().equals(action)) {
	    subFolder = removeFile(cmd);
	}

	DtoFileManagmentResponse res = createResponse(subFolder);

	TaskResponse response = new TaskResponse(null);
	response.addObject(UPLOADS, res);

	return response;
    }

    private DtoFileManagmentResponse createResponse(String subFolder) {
	List<DtoFile> dtoFiles = new ArrayList<>();

	DtoFileManagmentResponse res = new DtoFileManagmentResponse();
	res.setFolder(subFolder);
	res.setFiles(dtoFiles);

	Map<String, String> listOfFiles = readFiles(subFolder);
	for (Entry<String, String> entry : listOfFiles.entrySet()) {
	    DtoFile dtoFile = new DtoFile();
	    dtoFile.setFileName(entry.getKey());
	    dtoFile.setDisplayName(entry.getValue());

	    dtoFiles.add(dtoFile);
	}

	return res;
    }

    private FileManager getFileManager() {
	return _fileManager;
    }

    private Map<String, String> readFiles(String folder) {
	try {
	    Map<String, String> fileNames = new LinkedHashMap<>();
	    if (StringUtils.isEmpty(folder)) {
		return fileNames;
	    }

	    List<File> files = getFileManager().read(folder);

	    // Sort files by last modified time to show them by order in dialog
	    files.sort(Comparator.comparing(File::lastModified));

	    for (File file : files) {
		String uniqueFileName = file.getName();
		String originalFileName = ImportFileNameMaker.makeOriginalFrom(uniqueFileName);
		fileNames.put(uniqueFileName, originalFileName);
	    }
	    return fileNames;
	} catch (IOException e) {
	    LogHelper.error(LOG, e.getMessage(), e);
	    throw new UserException(String.format(Messages.getString("FileManagerTaskHandler.2"), //$NON-NLS-1$
		    folder), e);
	}
    }

    private String removeFile(FileManagerCommand cmd) {
	String subFolder = cmd.getFolder();
	List<String> fileNames = cmd.getFileNames();

	try {
	    getFileManager().delete(fileNames, subFolder);
	} catch (IOException e) {
	    LogHelper.error(LOG, e.getMessage(), e);
	    throw new UserException(String.format(Messages.getString("FileManagerTaskHandler.1"), //$NON-NLS-1$
		    fileNames), e);
	}

	return subFolder;
    }

    private String storeFiles(FileManagerCommand cmd, List<UploadedRepositoryItem> files) {
	String subFolder = StringUtils.isEmpty(cmd.getFolder()) ? UUID.randomUUID().toString() : cmd.getFolder();
	if (CollectionUtils.isNotEmpty(files)) {
	    for (UploadedRepositoryItem item : files) {
		RepositoryItem repoItem = item.getRepositoryItem();
		repoItem.getResourceInfo().setPath(subFolder);

		try {
		    getFileManager().store(repoItem);
		} catch (IOException e) {
		    LogHelper.error(LOG, e.getMessage(), e);
		    throw new UserException(String.format(Messages.getString("FileManagerTaskHandler.0"), //$NON-NLS-1$
			    repoItem.getResourceInfo().getName()), e);
		}
	    }
	}
	return subFolder;
    }
}
