package org.gs4tr.termmanager.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.gs4tr.foundation.modules.entities.model.RepositoryItem;
import org.gs4tr.foundation.modules.entities.model.RepositoryTicket;
import org.gs4tr.foundation.modules.entities.model.ResourceInfo;
import org.gs4tr.foundation.modules.entities.model.ResourceType;
import org.gs4tr.foundation.modules.repository.RepositoryManager;
import org.gs4tr.termmanager.model.TermEntryResourceTrack;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class UploadResourceTest extends AbstractSolrGlossaryTest {

    private static final String LOGO = "logo";

    private static final Long PROJECT_ID = 1L;

    @Autowired
    private RepositoryManager _repositoryManager;

    @Test
    public void downloadBinaryReferenceTest() throws IOException {
	String termEntryId = TERM_ENTRY_ID_01;

	TermEntry termEntry = getTermEntryService().findTermEntryById(termEntryId, PROJECT_ID);

	assertNotNull(termEntry);

	ResourceInfo resourceInfo = getResourceInfo();
	InputStream inputStream = getInputStreamFile(resourceInfo);

	RepositoryTicket repositoryTicket = getTermEntryService().uploadBinaryResource(termEntryId, resourceInfo,
		inputStream, LOGO);

	assertNotNull(repositoryTicket);

	termEntry = getTermEntryService().findTermEntryById(termEntryId, PROJECT_ID);

	assertNotNull(termEntry);

	List<TermEntryResourceTrack> termEntryResourceTracks = getTermEntryService()
		.findResourceTracksByTermEntryById(termEntryId);
	assertNotNull(termEntryResourceTracks);

	assertEquals(1, termEntryResourceTracks.size());

	TermEntryResourceTrack resourceTrack = termEntryResourceTracks.get(0);

	RepositoryItem repositoryItem = getTermEntryService().downloadResource(resourceTrack.getResourceTrackId());

	assertNotNull(repositoryItem);

	ResourceInfo resourceInfoResult = repositoryItem.getResourceInfo();

	assertNotNull(resourceInfoResult);

	assertEquals(getResourceInfo().getName(), resourceInfoResult.getName());

	RepositoryItem newRepositoryItem = new RepositoryItem();
	ResourceInfo newResourceInfo = getNewResourceInfo();
	InputStream newInputStreamFile = getNewInputStreamFile(getNewResourceInfo());
	newResourceInfo.setSize(new Long(newInputStreamFile.available()));
	newRepositoryItem.setInputStream(getNewInputStreamFile(newResourceInfo));
	newRepositoryItem.setResourceInfo(newResourceInfo);

	RepositoryTicket newreRepositoryTicket = getTermEntryService().updateTermEntryResourceTrack(termEntryId,
		resourceTrack.getResourceId(), newRepositoryItem);
	TermEntry newTermEntry = getTermEntryService().findTermEntryById(termEntryId, PROJECT_ID);

	termEntryResourceTracks = getTermEntryService().findResourceTracksByTermEntryById(termEntryId);
	assertNotNull(termEntryResourceTracks);

	assertEquals(LOGO, termEntryResourceTracks.get(0).getTaskName());

	assertEquals(getNewInputStreamFile(newResourceInfo).available(),
		getRepositoryManager().read(newreRepositoryTicket).getInputStream().available());
	List<String> resourceIds = new ArrayList<String>();
	resourceIds.add(termEntryResourceTracks.get(0).getResourceId());

	getTermEntryService().deleteTermEntryResourceTracks(newTermEntry.getUuId(), resourceIds, PROJECT_ID);
	termEntryResourceTracks = getTermEntryService().findResourceTracksByTermEntryById(termEntryId);

	assertEquals(termEntryResourceTracks.size(), 0);

    }

    public RepositoryManager getRepositoryManager() {
	return _repositoryManager;
    }

    @Test
    public void uploadBinaryResourceTest() {

	TermEntry termEntry = getTermEntryService().findTermEntryById(TERM_ENTRY_ID_01, PROJECT_ID);

	assertNotNull(termEntry);

	ResourceInfo resourceInfo = getResourceInfo();
	InputStream inputStream = getInputStreamFile(resourceInfo);

	RepositoryTicket repositoryTicket = getTermEntryService().uploadBinaryResource(termEntry.getUuId(),
		resourceInfo, inputStream, LOGO);

	assertNotNull(repositoryTicket);
    }

    private InputStream getInputStreamFile(ResourceInfo resourceInfo) {
	File file = new File("src/test/resources/testfiles/test.doc");
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

    private ResourceInfo getNewResourceInfo() {

	ResourceInfo resourceInfo = new ResourceInfo();
	resourceInfo.setName("logo.jpg");

	return resourceInfo;
    }

    private ResourceInfo getResourceInfo() {

	ResourceInfo resourceInfo = new ResourceInfo();
	resourceInfo.setName("test.doc");
	resourceInfo.setType(ResourceType.REFERENCE);

	return resourceInfo;
    }
}
