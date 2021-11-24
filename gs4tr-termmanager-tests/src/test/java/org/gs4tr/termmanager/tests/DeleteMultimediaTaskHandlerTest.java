package org.gs4tr.termmanager.tests;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.gs4tr.foundation.modules.entities.model.RepositoryItem;
import org.gs4tr.foundation.modules.entities.model.RepositoryPath;
import org.gs4tr.foundation.modules.entities.model.RepositoryTicket;
import org.gs4tr.foundation.modules.entities.model.ResourceInfo;
import org.gs4tr.foundation.modules.repository.RepositoryManager;
import org.gs4tr.foundation.modules.repository.jack.exception.GenericRepositoryException;
import org.gs4tr.termmanager.service.manualtask.ManualTaskHandler;
import org.gs4tr.termmanager.service.model.command.DeleteResourceCommand;
import org.gs4tr.termmanager.tests.model.TestCase;
import org.gs4tr.termmanager.tests.model.TestSuite;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@TestSuite("delete_multimedia")
public class DeleteMultimediaTaskHandlerTest extends AbstractSpringServiceTests {

    private static List<String> _ticketsForRemoval = new ArrayList<String>();

    @Autowired
    private RepositoryManager _repositoryManager;

    private ResourceInfo _resourceInfo = new ResourceInfo();

    public RepositoryManager getRepositoryManager() {
	return _repositoryManager;
    }

    @Test(expected = GenericRepositoryException.class)
    @TestCase("process_tasks")
    public void testProcessTasks() {
	String taskName = "delete multimedia";

	ManualTaskHandler taskHandler = getHandler(taskName);

	DeleteResourceCommand command = (DeleteResourceCommand) getTaskHandlerCommand(taskHandler,
		"deleteMultimedia.json");

	command.setTicketsForRemoval(_ticketsForRemoval);

	RepositoryItem item = getRepositoryManager().read(new RepositoryTicket(_ticketsForRemoval.get(0)));
	assertNotNull(item);
	taskHandler.processTasks(null, null, command, null);

	getRepositoryManager().read(new RepositoryTicket(_ticketsForRemoval.get(0)));
    }

    @Before
    public void uploadMultimedia() {
	RepositoryItem repositoryItem = new RepositoryItem();
	repositoryItem.setInputStream(getInputStreamFile(getResourceInfo()));
	repositoryItem.setResourceInfo(getResourceInfo());
	repositoryItem.setRepositoryPath(new RepositoryPath("fake"));
	_resourceInfo.setName("logo.jpg");

	RepositoryTicket ticket = getRepositoryManager().store(repositoryItem);

	_ticketsForRemoval.add(ticket.getTicket());
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
	return _resourceInfo;
    }
}
