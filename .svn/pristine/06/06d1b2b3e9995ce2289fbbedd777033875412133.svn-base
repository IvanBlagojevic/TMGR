package org.gs4tr.termmanager.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.gs4tr.foundation.modules.entities.model.RepositoryItem;
import org.gs4tr.foundation.modules.entities.model.RepositoryTicket;
import org.gs4tr.foundation.modules.entities.model.ResourceInfo;
import org.gs4tr.foundation.modules.entities.model.ResourceTrack;
import org.gs4tr.foundation.modules.entities.model.ResourceType;
import org.gs4tr.foundation.modules.repository.RepositoryManager;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TermEntryResourceTrack;
import org.gs4tr.termmanager.model.UpdateCommand;
import org.gs4tr.termmanager.model.UploadedRepositoryItem;
import org.gs4tr.termmanager.service.manualtask.ManualTaskHandler;
import org.gs4tr.termmanager.service.model.command.AssignTermEntryAttributesCommand;
import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.gs4tr.termmanager.tests.model.TestCase;
import org.gs4tr.termmanager.tests.model.TestSuite;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@TestSuite("assign_termentry_attributes")
public class AssignTermEntryAttributesTaskHandlerTest extends AbstractSolrGlossaryTest {

    @Autowired
    RepositoryManager _repositoryManager;

    @Test
    @TestCase("get_task_infos")
    public void assignAttributesGetTest() throws Exception {
	ManualTaskHandler taskHandler = getHandler("assign termentry attributes");

	Object command = getTaskHandlerCommand(taskHandler, "assingTermEntryAttributes.json",
		new String[] { "$termEntryId", TERM_ENTRY_ID_01 });

	TaskModel[] taskInfos = taskHandler.getTaskInfos(new Long[] { new Long(1) }, "assign termentry attributes",
		command);

	String result = JsonUtils.writeValueAsString(taskInfos);

	assertJSONResponse(result, "assingTermEntryAttributesValidation.json");
    }

    @Test
    @TestCase("get_task_infos")
    public void assignAttributesGPostTest() throws Exception {
	ManualTaskHandler taskHandler = getHandler("assign termentry attributes");

	String termentryId = TERM_ENTRY_ID_01;

	AssignTermEntryAttributesCommand command = new AssignTermEntryAttributesCommand();

	List<UpdateCommand> updateCommands = new ArrayList<UpdateCommand>();
	UpdateCommand updateCommand = new UpdateCommand("add", "description", "context", UUID.randomUUID().toString(),
		termentryId, "cont");
	updateCommands.add(updateCommand);
	command.setUpdateCommands(updateCommands);

	command.setTermEntryId(termentryId);

	command.getAttributeTypeFileNameMap().put("logo", "logo.jpg");
	command.getAttributeTypeFileNameMap().put("test", "test.doc");

	RepositoryItem item = new RepositoryItem();
	ResourceInfo resourceInfo = getResourceInfo();
	InputStream inputStreamFile = getInputStreamFile(resourceInfo);
	item.setInputStream(inputStreamFile);
	item.setResourceInfo(resourceInfo);
	item.getResourceInfo().setSize((long) inputStreamFile.available());

	RepositoryItem item2 = new RepositoryItem();
	ResourceInfo newResourceInfo = getNewResourceInfo();
	InputStream newInputStreamFile = getNewInputStreamFile(newResourceInfo);
	item2.setInputStream(newInputStreamFile);
	item2.setResourceInfo(newResourceInfo);
	item2.getResourceInfo().setSize((long) newInputStreamFile.available());

	UploadedRepositoryItem repositoryItem = new UploadedRepositoryItem(item, "logo");
	UploadedRepositoryItem repositoryItem2 = new UploadedRepositoryItem(item2, "test");
	List<UploadedRepositoryItem> items = new ArrayList<UploadedRepositoryItem>();
	items.add(repositoryItem);
	items.add(repositoryItem2);

	taskHandler.processTasks(new Long[] { 1L }, null, command, items);

	List<TermEntryResourceTrack> resourceTracks = getTermEntryService()
		.findResourceTracksByTermEntryById(termentryId);

	Assert.assertTrue(resourceTracks.size() == 2);

	ResourceTrack resourceTrack = findResourceTrackByName("logo.jpg", resourceTracks);
	Assert.assertNotNull(resourceTrack);
	RepositoryTicket repositoryTicket = new RepositoryTicket(resourceTrack.getResourceId());
	RepositoryItem uploadedItem = getRepositoryManager().read(repositoryTicket);

	Assert.assertTrue(uploadedItem != null);

	ResourceTrack resourceTrack2 = findResourceTrackByName("image.jpg", resourceTracks);
	Assert.assertNotNull(resourceTrack2);
	RepositoryTicket repositoryTicket2 = new RepositoryTicket(resourceTrack2.getResourceId());
	RepositoryItem uploadedItem2 = getRepositoryManager().read(repositoryTicket2);

	Assert.assertTrue(uploadedItem2 != null);

	Assert.assertEquals(getRepositoryManager().read(repositoryTicket).getInputStream().available(),
		getInputStreamFile(getResourceInfo()).available());
	Assert.assertEquals(getRepositoryManager().read(repositoryTicket2).getInputStream().available(),
		getNewInputStreamFile(getNewResourceInfo()).available());

    }

    public RepositoryManager getRepositoryManager() {
	return _repositoryManager;
    }

    private TermEntryResourceTrack findResourceTrackByName(String resourceName,
	    List<TermEntryResourceTrack> resourceTracks) {
	for (TermEntryResourceTrack resourceTrack : resourceTracks) {
	    if (resourceTrack.getResourceName().equals(resourceName)) {
		return resourceTrack;
	    }
	}

	return null;
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

    private InputStream getNewInputStreamFile(ResourceInfo resourceInfo) {
	File file = new File("src/test/resources/testfiles/image.jpg");
	resourceInfo.setSize(file.length());

	FileInputStream fis = null;
	try {
	    fis = new FileInputStream(file);
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	}

	return fis;
    }

    private ResourceInfo getNewResourceInfo() {

	ResourceInfo resourceInfo = new ResourceInfo();
	resourceInfo.setName("image.jpg");
	resourceInfo.setType(ResourceType.REFERENCE);
	resourceInfo.setMimeType("image/jpg");

	return resourceInfo;
    }

    private ResourceInfo getResourceInfo() {

	ResourceInfo resourceInfo = new ResourceInfo();
	resourceInfo.setName("logo.jpg");
	resourceInfo.setMimeType("image/jpg");

	return resourceInfo;
    }
}
