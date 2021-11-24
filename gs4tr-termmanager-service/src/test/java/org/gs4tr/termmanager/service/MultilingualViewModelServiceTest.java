package org.gs4tr.termmanager.service;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.List;

import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.termmanager.model.TermSearchRequest;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.search.ItemFolderEnum;
import org.gs4tr.termmanager.model.search.TypeSearchEnum;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Ignore
public class MultilingualViewModelServiceTest extends AbstractRecodeOrCloneTermsTest {

    @Autowired
    private MultilingualViewModelService _multilingualViewModelService;

    @Test
    public void multilingualViewSearchWithEmptyProjectIdListTest() {

	TermSearchRequest request = createRequestWithEmptyProjectIds();
	PagedListInfo info = createPagedListInfo();

	boolean exceptionIsThrown = false;
	try {
	    getMultilingualViewModelService().search(request, info);
	} catch (Exception e) {
	    exceptionIsThrown = true;
	}

	assertFalse(exceptionIsThrown);

    }

    @Before
    public void setUp() {
	setupUser();
    }

    private PagedListInfo createPagedListInfo() {

	PagedListInfo info = new PagedListInfo();
	info.setIndex(0);
	info.setSize(200);
	return info;
    }

    private TermSearchRequest createRequestWithEmptyProjectIds() {
	TermSearchRequest request = new TermSearchRequest();
	List<Long> projectIds = new ArrayList<>();
	request.setProjectIds(projectIds);
	request.setFolder(ItemFolderEnum.TERM_LIST);
	request.setSource("en");
	request.setSourceAndTargetSearch(true);
	request.setTypeSearch(TypeSearchEnum.TERM);
	request.setTargetTermSearch(true);
	request.setTargetLanguagesList(singletonList("de"));
	return request;

    }

    private MultilingualViewModelService getMultilingualViewModelService() {
	return _multilingualViewModelService;
    }

    private void setupUser() {
	getSessionService().logout();
	getSessionService().login("ivan", "password1!");
	TmUserProfile.getCurrentUserProfile().getPreferences().setDefaultProjectId(2L);
    }
}
