package org.gs4tr.termmanager.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gs4tr.foundation.modules.entities.model.RepositoryItem;
import org.gs4tr.foundation.modules.entities.model.ResourceInfo;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.service.manualtask.ManualTaskHandler;
import org.gs4tr.termmanager.service.model.command.UploadResourceCommand;
import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.gs4tr.termmanager.tests.model.TestCase;
import org.gs4tr.termmanager.tests.model.TestSuite;
import org.junit.Test;

@TestSuite("upload_multimedia_image")
public class UploadMultimediaImageTest extends AbstractSpringServiceTests {

    @Test
    @TestCase("process_tasks")
    public void assignAttributesGetTest() throws Exception {
	ManualTaskHandler taskHandler = getHandler("upload resource");

	String termentryId = "10";
	UploadResourceCommand command = new UploadResourceCommand();
	command.setTermEntryId(termentryId);
	Map<String, String> fileNameMap = new HashMap<String, String>();
	fileNameMap.put("logo", "logo.jpg");
	command.setAttributeTypeFileNameMap(fileNameMap);
	RepositoryItem item = new RepositoryItem();
	item.setInputStream(getInputStreamFile(getResourceInfo()));
	item.setResourceInfo(getResourceInfo());
	UploadedRepositoryItem repositoryItem = new UploadedRepositoryItem(item, "logo");
	List<UploadedRepositoryItem> items = new ArrayList<UploadedRepositoryItem>();
	items.add(repositoryItem);

	TaskResponse response = taskHandler.processTasks(new Long[] { new Long(1) }, null, command, items);

	String result = JsonUtils.writeValueAsString(response);

	System.out.println(result);

    }

    private InputStream getInputStreamFile(ResourceInfo resourceInfo) {
	File file = new File("src/test/resources/testfiles/logo.jpg");
	resourceInfo.setSize(file.length());

	FileInputStream fis = null;
	try {
	    fis = new FileInputStream(file);
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	}

	return fis;
    }

    private ResourceInfo getResourceInfo() {

	ResourceInfo resourceInfo = new ResourceInfo();
	resourceInfo.setName("logo.jpg");

	return resourceInfo;
    }

}
