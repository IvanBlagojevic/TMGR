package org.gs4tr.termmanager.dao.hibernate;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.termmanager.dao.UserCustomSearchDAO;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.UserCustomSearch;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class UserCustomSearchDAOTest extends AbstractSpringDAOIntegrationTest {

    private static final String CUSTOM_FOLDER_JSON_SEARCH_DATA = "ss";

    private static final String CUSTOM_FOLDER_NAME = "customFolderName";

    private static final String ORIGINAL_FOLDER = "FOLDER";

    private static final String ORIGINAL_URL = "url.ter";

    @Autowired
    private UserCustomSearchDAO _userCustomSearchDAO;

    @Before
    public void configureUserCustomSearch() {
	Long userProfileId = Long.valueOf(1);
	TmUserProfile userProfile = getUserProfileDAO().findById(userProfileId);
	Assert.assertNotNull(userProfile);

	UserCustomSearch userCustomSearch = new UserCustomSearch();
	userCustomSearch.setAdminFolder(Boolean.FALSE);
	userCustomSearch.setUserProfile(userProfile);
	userCustomSearch.setCustomFolder(CUSTOM_FOLDER_NAME);
	userCustomSearch.setSearchJsonData(CUSTOM_FOLDER_JSON_SEARCH_DATA);
	userCustomSearch.setUrl(ORIGINAL_URL);
	userCustomSearch.setOriginalFolder(ORIGINAL_FOLDER);
	userCustomSearch.setAdminFolder(Boolean.TRUE);

	userCustomSearch = getUserCustomSearchDAO().save(userCustomSearch);
	Assert.assertNotNull(userCustomSearch);
	Assert.assertNotNull(userCustomSearch.getCustomSearchId());
    }

    @Test
    public void testFindByUserProfileId() {
	Long userProfileId = Long.valueOf(1);
	List<UserCustomSearch> userCustomSearches = getUserCustomSearchDAO().findByUserProfileId(userProfileId,
		Boolean.TRUE);
	Assert.assertNotNull(userCustomSearches);
	Assert.assertTrue(CollectionUtils.isNotEmpty(userCustomSearches));
    }

    @Test
    public void testFindByUserProfileIdAndFolderName() {
	Long userProfileId = Long.valueOf(1);
	UserCustomSearch userCustomSearche = getUserCustomSearchDAO().findByUserProfileIdAndFolderName(userProfileId,
		CUSTOM_FOLDER_NAME);
	Assert.assertNotNull(userCustomSearche);
	Assert.assertEquals(ORIGINAL_FOLDER, userCustomSearche.getOriginalFolder());
	Assert.assertEquals(ORIGINAL_URL, userCustomSearche.getUrl());
	Assert.assertEquals(CUSTOM_FOLDER_JSON_SEARCH_DATA, userCustomSearche.getSearchJsonData());
    }

    private UserCustomSearchDAO getUserCustomSearchDAO() {
	return _userCustomSearchDAO;
    }
}
