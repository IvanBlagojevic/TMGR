package org.gs4tr.termmanager.tests;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.modules.entities.model.RepositoryItem;
import org.gs4tr.foundation.modules.entities.model.ResourceInfo;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.service.file.manager.FileManager;
import org.gs4tr.termmanager.service.manualtask.ManualTaskHandler;
import org.gs4tr.termmanager.service.model.command.dto.filemanagment.DtoFile;
import org.gs4tr.termmanager.service.model.command.dto.filemanagment.DtoFileManagmentResponse;
import org.gs4tr.termmanager.tests.model.TestCase;
import org.gs4tr.termmanager.tests.model.TestSuite;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@TestSuite("file_upload_manager")
public class FileManagerTaskHandlerTest extends AbstractSpringServiceTests {

    private static final String FILE_NAME_1 = "import_skype.tbx";

    private static final String FILE_NAME_2 = "medtronic.tbx";

    private static final String FILE_NAME_3 = "ordermedtronic.tbx";

    private static final String TASK = "manage uploads";

    @Autowired
    public FileManager _fileManager;

    @Test
    @TestCase("add")
    public void addFileForImportTest() throws IOException {
	ManualTaskHandler handler = getHandler(TASK);

	Object command = getTaskHandlerCommand(handler, "addFileManager.json");

	List<UploadedRepositoryItem> items = getUploadItems1();

	TaskResponse response = handler.processTasks(null, null, command, items);
	Assert.assertNotNull(response);

	Map<String, Object> model = response.getModel();
	Assert.assertNotNull(model);

	DtoFileManagmentResponse res = (DtoFileManagmentResponse) model.get("uploads");

	String folderName = res.getFolder();
	Assert.assertNotNull(folderName);

	List<DtoFile> files = res.getFiles();

	Assert.assertTrue(CollectionUtils.isNotEmpty(files));
	Assert.assertEquals(1, files.size());

	DtoFile actual = files.get(0);
	Assert.assertEquals(FILE_NAME_1, actual.getDisplayName());
    }

    @Test
    @TestCase("add")
    public void addMultipleFilesForImportTest() throws IOException {
	ManualTaskHandler handler = getHandler(TASK);

	// upload first file
	Object command = getTaskHandlerCommand(handler, "addFileManager.json");
	List<UploadedRepositoryItem> items = getUploadItems1();

	TaskResponse response = handler.processTasks(null, null, command, items);

	String folderName = testResponse(response, 1, null);

	// upload second file in the same folder as first file

	makeDelayBeforeAddNextFile();

	command = getTaskHandlerCommand(handler, "addMultipleFilesManager.json",
		new String[] { "$folder", folderName });
	items = getUploadItems2();

	response = handler.processTasks(null, null, command, items);

	folderName = testResponse(response, 2, folderName);

	// upload third file in the same folder as first file

	makeDelayBeforeAddNextFile();

	command = getTaskHandlerCommand(handler, "addMultipleFilesManager.json",
		new String[] { "$folder", folderName });
	items = getUploadItems3();

	response = handler.processTasks(null, null, command, items);
	testResponse(response, 3, null);
    }

    @Before
    public void beforeCleanup() throws IOException {
	getFileManager().cleanup();
    }

    @After
    public void cleanup() throws IOException {
	getFileManager().cleanup();
    }

    @Test
    @TestCase("remove")
    public void removeFileForImportTest() throws IOException {

	// add

	ManualTaskHandler handler = getHandler(TASK);

	Object command = getTaskHandlerCommand(handler, "addFileManager.json");

	List<UploadedRepositoryItem> items = getUploadItems1();

	TaskResponse response = handler.processTasks(null, null, command, items);
	Assert.assertNotNull(response);

	Map<String, Object> model = response.getModel();
	Assert.assertNotNull(model);

	DtoFileManagmentResponse res = (DtoFileManagmentResponse) model.get("uploads");

	String folderName = res.getFolder();
	Assert.assertNotNull(folderName);

	List<DtoFile> files = res.getFiles();

	Assert.assertTrue(CollectionUtils.isNotEmpty(files));
	Assert.assertEquals(1, files.size());

	DtoFile actual = files.get(0);
	Assert.assertEquals(FILE_NAME_1, actual.getDisplayName());

	// remove

	command = getTaskHandlerCommand(handler, "removeFileManager.json", new String[] { "$folder", folderName },
		new String[] { "$fileName", actual.getFileName() });

	response = handler.processTasks(null, null, command, null);
	Assert.assertNotNull(response);

	model = response.getModel();
	Assert.assertNotNull(model);

	res = (DtoFileManagmentResponse) model.get("uploads");

	folderName = res.getFolder();
	Assert.assertNotNull(folderName);

	files = res.getFiles();
	Assert.assertTrue(CollectionUtils.isEmpty(files));
    }

    private FileManager getFileManager() {
	return _fileManager;
    }

    private List<UploadedRepositoryItem> getUploadItems1() throws IOException {
	InputStream stream = new FileInputStream("src/test/resources/tmp/import_skype.tbx");

	ResourceInfo resourceInfo = new ResourceInfo();
	resourceInfo.setSize(Long.valueOf(stream.available()));
	resourceInfo.setName(FILE_NAME_1);
	resourceInfo.setEncoding("UTF-8");

	RepositoryItem repositoryItem = new RepositoryItem();
	repositoryItem.setInputStream(stream);
	repositoryItem.setResourceInfo(resourceInfo);

	UploadedRepositoryItem item = new UploadedRepositoryItem(repositoryItem, "skype");

	return Arrays.asList(item);
    }

    private List<UploadedRepositoryItem> getUploadItems2() throws IOException {
	InputStream stream = new FileInputStream("src/test/resources/tmp/medtronic.tbx");

	ResourceInfo resourceInfo = new ResourceInfo();
	resourceInfo.setSize(Long.valueOf(stream.available()));
	resourceInfo.setName(FILE_NAME_2);
	resourceInfo.setEncoding("UTF-8");

	RepositoryItem repositoryItem = new RepositoryItem();
	repositoryItem.setInputStream(stream);
	repositoryItem.setResourceInfo(resourceInfo);

	UploadedRepositoryItem item = new UploadedRepositoryItem(repositoryItem, "medtronic");

	return Arrays.asList(item);
    }

    private List<UploadedRepositoryItem> getUploadItems3() throws IOException {
	InputStream stream = new FileInputStream("src/test/resources/tmp/import_medtronic.tbx");

	ResourceInfo resourceInfo = new ResourceInfo();
	resourceInfo.setSize(Long.valueOf(stream.available()));
	resourceInfo.setName(FILE_NAME_3);
	resourceInfo.setEncoding("UTF-8");

	RepositoryItem repositoryItem = new RepositoryItem();
	repositoryItem.setInputStream(stream);
	repositoryItem.setResourceInfo(resourceInfo);

	UploadedRepositoryItem item = new UploadedRepositoryItem(repositoryItem, "ordermedtronic");

	return Arrays.asList(item);
    }

    // Delay for this test should be 2 seconds because linux based systems
    // File.lastModified()
    // is always rounded to the second.
    // TODO: This behavior will be fixed in some of the next java versions, so
    // TODO: this method can be removed after that.
    private void makeDelayBeforeAddNextFile() {
	try {
	    Thread.sleep(2000);
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}
    }

    private String testResponse(TaskResponse response, int expectedFileNum, String commandFoldername) {
	Assert.assertNotNull(response);

	Map<String, Object> model = response.getModel();
	Assert.assertNotNull(model);

	DtoFileManagmentResponse res = (DtoFileManagmentResponse) model.get("uploads");

	String folderName = res.getFolder();
	Assert.assertNotNull(folderName);

	List<DtoFile> files = res.getFiles();

	Assert.assertTrue(CollectionUtils.isNotEmpty(files));
	Assert.assertEquals(expectedFileNum, files.size());
	DtoFile actual1 = files.get(0);

	if (expectedFileNum == 1) {
	    Assert.assertEquals(FILE_NAME_1, actual1.getDisplayName());
	}

	if (expectedFileNum == 2) {
	    Assert.assertEquals(folderName, commandFoldername);

	    DtoFile actual2 = files.get(1);
	    String displayName1 = actual1.getDisplayName();
	    String displayName2 = actual2.getDisplayName();

	    List<String> expendedNames = Arrays.asList(FILE_NAME_1, FILE_NAME_2);
	    Assert.assertTrue(expendedNames.contains(displayName1) && expendedNames.contains(displayName2));
	}

	// Testic if files are ordered by time they are added
	if (expectedFileNum == 3) {
	    String responseFileName = files.get(0).getDisplayName();
	    Assert.assertEquals(FILE_NAME_1, responseFileName);

	    responseFileName = files.get(1).getDisplayName();
	    Assert.assertEquals(FILE_NAME_2, responseFileName);

	    responseFileName = files.get(2).getDisplayName();
	    Assert.assertEquals(FILE_NAME_3, responseFileName);
	}

	return folderName;
    }
}
