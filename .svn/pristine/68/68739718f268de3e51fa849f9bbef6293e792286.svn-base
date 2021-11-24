package org.gs4tr.termmanager.tests;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.modules.entities.model.Metadata;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.service.manualtask.ManualTaskHandler;
import org.junit.Assert;
import org.junit.Test;

public class ClearUserMetadataTaskHandlerTest extends AbstractSpringServiceTests {

    @Test
    public void testProcessTasks() {
	Long genericId = getUserProfileService().addOrUpdateMetadata("key", "value");
	Assert.assertNotNull(genericId);

	TmUserProfile userProfile = TmUserProfile.getCurrentUserProfile();

	List<Metadata> metadata = userProfile.getMetadata();
	Assert.assertTrue(CollectionUtils.isNotEmpty(metadata));
	Assert.assertEquals("key", metadata.get(0).getKey());
	Assert.assertEquals("value", metadata.get(0).getValue());

	String taskName = "clear user metadata";
	ManualTaskHandler taskHandler = getHandler(taskName);
	taskHandler.processTasks(null, null, null, null);
    }
}
