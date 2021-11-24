package org.gs4tr.termmanager.dao.hibernate;

import java.util.List;

import junit.framework.Assert;

import org.gs4tr.termmanager.dao.TermEntryResourceTrackDAO;
import org.gs4tr.termmanager.model.TermEntryResourceTrack;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TermEntryResourceTrackDAOTest extends AbstractSpringDAOIntegrationTest {

    @Autowired
    private TermEntryResourceTrackDAO _termEntryResourceTrackDAO;

    @Test
    public void findResourceTracksByTermEntryIdTest() {
	List<TermEntryResourceTrack> all = getTermEntryResourceTrackDAO().findAll();

	Assert.assertEquals(3, all.size());

	List<TermEntryResourceTrack> resourceTracks = getTermEntryResourceTrackDAO()
		.findAllByTermEntryId("term-entry-id-01");
	Assert.assertEquals(2, resourceTracks.size());

	TermEntryResourceTrack resourceTrack = new TermEntryResourceTrack();
	resourceTrack.setResourceName("some other resource");
	resourceTrack.setTermEntryId("term-entry-id-01");

	getTermEntryResourceTrackDAO().save(resourceTrack);

	resourceTracks = getTermEntryResourceTrackDAO().findAllByTermEntryId("term-entry-id-01");
	Assert.assertEquals(3, resourceTracks.size());

    }

    private TermEntryResourceTrackDAO getTermEntryResourceTrackDAO() {
	return _termEntryResourceTrackDAO;
    }

}
