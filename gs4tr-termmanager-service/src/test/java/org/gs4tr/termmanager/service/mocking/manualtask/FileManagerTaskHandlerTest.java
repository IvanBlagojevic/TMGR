package org.gs4tr.termmanager.service.mocking.manualtask;

import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.modules.entities.model.RepositoryItem;
import org.gs4tr.foundation.modules.entities.model.ResourceInfo;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.service.file.manager.FileManager;
import org.gs4tr.termmanager.service.manualtask.FileManagerTaskHandler;
import org.gs4tr.termmanager.service.mocking.model.TestSuite;
import org.gs4tr.termmanager.service.model.command.FileManagerCommand;
import org.gs4tr.termmanager.service.model.command.dto.filemanagment.DtoFile;
import org.gs4tr.termmanager.service.model.command.dto.filemanagment.DtoFileManagmentResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

@TestSuite("manualtask")
public class FileManagerTaskHandlerTest extends AbstractManualtaskTest {

    private static final String FILE_NAME = "skype.tbx";

    @Autowired
    @Mock
    private FileManager _fileManager;

    @Autowired
    private FileManagerTaskHandler _handler;

    @Test
    public void addFileForImportTest() throws IOException {
	FileManagerCommand cmd = new FileManagerCommand();
	cmd.setAction(FileManagerCommand.Action.ADD.getActionName());

	List<UploadedRepositoryItem> items = getUploadItems();

	File file = new File("src/test/resources/testfiles/skype.tbx");
	List<File> files = Arrays.asList(file);

	when(getFileManager().read(Mockito.anyString())).thenReturn(files);

	TaskResponse response = getHandler().processTasks(null, null, cmd, items);

	Map<String, Object> model = response.getModel();
	Assert.assertNotNull(model);

	DtoFileManagmentResponse res = (DtoFileManagmentResponse) model.get("uploads");

	String folderName = res.getFolder();
	Assert.assertNotNull(folderName);

	List<DtoFile> dtoFiles = res.getFiles();

	Assert.assertTrue(CollectionUtils.isNotEmpty(dtoFiles));
	Assert.assertEquals(1, dtoFiles.size());

	DtoFile actual = dtoFiles.get(0);
	Assert.assertEquals(FILE_NAME, actual.getDisplayName());
    }

    @Test
    public void removeFileForImportTest() throws IOException {
	// add
	FileManagerCommand cmd = new FileManagerCommand();
	cmd.setAction(FileManagerCommand.Action.ADD.getActionName());

	List<UploadedRepositoryItem> items = getUploadItems();

	File file = new File("src/test/resources/testfiles/skype.tbx");
	List<File> files = Arrays.asList(file);

	when(getFileManager().read(Mockito.anyString())).thenReturn(files);

	TaskResponse response = getHandler().processTasks(null, null, cmd, items);

	Map<String, Object> model = response.getModel();
	Assert.assertNotNull(model);

	DtoFileManagmentResponse res = (DtoFileManagmentResponse) model.get("uploads");

	String folderName = res.getFolder();
	Assert.assertNotNull(folderName);

	List<DtoFile> dtoFiles = res.getFiles();

	Assert.assertTrue(CollectionUtils.isNotEmpty(dtoFiles));
	Assert.assertEquals(1, dtoFiles.size());

	DtoFile actual = dtoFiles.get(0);
	Assert.assertEquals(FILE_NAME, actual.getDisplayName());

	// remove
	cmd = new FileManagerCommand();
	cmd.setAction(FileManagerCommand.Action.REMOVE.getActionName());
	cmd.setFolder(folderName);
	cmd.setFileNames(Arrays.asList(actual.getFileName()));

	when(getFileManager().read(Mockito.anyString())).thenReturn(Collections.emptyList());

	response = getHandler().processTasks(null, null, cmd, null);

	model = response.getModel();
	Assert.assertNotNull(model);

	res = (DtoFileManagmentResponse) model.get("uploads");

	folderName = res.getFolder();
	Assert.assertNotNull(folderName);

	dtoFiles = res.getFiles();

	Assert.assertTrue(CollectionUtils.isEmpty(dtoFiles));
    }

    @After
    public void reset() {
	Mockito.reset(getFileManager());
    }

    private FileManager getFileManager() {
	return _fileManager;
    }

    private FileManagerTaskHandler getHandler() {
	return _handler;
    }

    private List<UploadedRepositoryItem> getUploadItems() throws IOException {
	InputStream stream = new FileInputStream("src/test/resources/testfiles/skype.tbx");

	ResourceInfo resourceInfo = new ResourceInfo();
	resourceInfo.setSize(Long.valueOf(stream.available()));
	resourceInfo.setName(FILE_NAME);
	resourceInfo.setEncoding("UTF-8");

	RepositoryItem repositoryItem = new RepositoryItem();
	repositoryItem.setInputStream(stream);
	repositoryItem.setResourceInfo(resourceInfo);

	UploadedRepositoryItem item = new UploadedRepositoryItem(repositoryItem, "skype");

	return Arrays.asList(item);
    }
}
