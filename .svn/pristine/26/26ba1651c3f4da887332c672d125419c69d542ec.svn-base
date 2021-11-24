package org.gs4tr.termmanager.webmvc.configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.gs4tr.termmanager.model.feature.TmgrFeatureConstants;
import org.gs4tr.termmanager.model.search.ItemFolderEnum;
import org.gs4tr.termmanager.model.search.ItemPaneEnum;
import org.gs4tr.termmanager.webmvc.model.response.UiDetailItem;
import org.gs4tr.termmanager.webmvc.model.search.SearchCriteria;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FolderSearchConfiguration {

    private static final boolean GRID_FEATURE = Arrays.asList(TmgrFeatureConstants.getEnabledFeatures())
	    .contains(TmgrFeatureConstants.GRID_FEATURE);

    @Bean(name = "adminSearchCriterias")
    public Map<ItemFolderEnum, List<String>> createAdminMenuFilters() {
	List<String> usersSearchCriterias = new ArrayList<String>();
	usersSearchCriterias.add(SearchCriteria.USER_NAME.toString());
	usersSearchCriterias.add(SearchCriteria.USER_FIRST_NAME.toString());
	usersSearchCriterias.add(SearchCriteria.USER_LAST_NAME.toString());
	usersSearchCriterias.add(SearchCriteria.EMAIL_ADDRESS.toString());

	List<String> projectsSearchCriterias = createProjectSearchCriterias();

	List<String> organizationsSearchCriterias = new ArrayList<String>();
	organizationsSearchCriterias.add(SearchCriteria.ORGANIZATION_NAME.toString());

	List<String> securitySearchCriterias = new ArrayList<String>();
	securitySearchCriterias.add(SearchCriteria.ROLE_NAME.toString());

	EnumMap<ItemFolderEnum, List<String>> config = new EnumMap<>(ItemFolderEnum.class);
	config.put(ItemFolderEnum.USERS, usersSearchCriterias);
	config.put(ItemFolderEnum.PROJECTS, projectsSearchCriterias);
	config.put(ItemFolderEnum.ORGANIZATIONS, organizationsSearchCriterias);
	config.put(ItemFolderEnum.SECURITY, securitySearchCriterias);

	return config;
    }

    @Bean(name = "folderUrls")
    public Map<ItemFolderEnum, String> createBeanFolderUrls() {
	EnumMap<ItemFolderEnum, String> config = new EnumMap<>(ItemFolderEnum.class);

	config.put(ItemFolderEnum.USERS, UrlConstants.USER_PROFILE_SEARCH);
	config.put(ItemFolderEnum.ORGANIZATIONS, UrlConstants.ORGANIZATION_SEARCH);
	config.put(ItemFolderEnum.SECURITY, UrlConstants.ROLE_SEARCH);
	config.put(ItemFolderEnum.PROJECTS, UrlConstants.PROJECT_SEARCH);
	config.put(ItemFolderEnum.PROJECTDETAILS, UrlConstants.PROJECT_DETAIL_SEARCH);
	config.put(ItemFolderEnum.SUBMISSIONDETAILS, UrlConstants.SUBMISSION_DETAIL_SEARCH);
	config.put(ItemFolderEnum.TERM_LIST, UrlConstants.MULTILINGUAL_SEARCH);
	config.put(ItemFolderEnum.SUBMISSIONTERMLIST, UrlConstants.MULTILINGUAL_SEARCH);

	return config;
    }

    @Bean(name = "detailsConfig")
    public Map<ItemFolderEnum, List<UiDetailItem>> createDetailsConfig() {
	UiDetailItem projectDetailItem = new UiDetailItem(ItemPaneEnum.PROJECTLANGUAGEDETAILS.name(),
		UrlConstants.PROJECT_LANGUAGE_DETAIL_SEARCH);

	List<UiDetailItem> projectDetailItems = new ArrayList<UiDetailItem>();
	projectDetailItems.add(projectDetailItem);

	UiDetailItem submissionDetailItem = new UiDetailItem(ItemPaneEnum.SUBMISSIONLANGUAGEDETAILS.name(),
		UrlConstants.SUBMISSION_LANGUAGE_DETAIL_SEARCH);

	List<UiDetailItem> submissionDetailItems = new ArrayList<UiDetailItem>();
	submissionDetailItems.add(submissionDetailItem);

	EnumMap<ItemFolderEnum, List<UiDetailItem>> config = new EnumMap<>(ItemFolderEnum.class);
	config.put(ItemFolderEnum.PROJECTDETAILS, projectDetailItems);
	config.put(ItemFolderEnum.SUBMISSIONDETAILS, submissionDetailItems);

	return config;
    }

    @Bean(name = "userSearchCriterias")
    public Map<ItemFolderEnum, List<String>> createUserMenuFilters() {
	List<String> projectDetailsSearchCriterias = createProjectDetailSearchCriterias();
	List<String> termSearchCriterias = createTermSearchCriterias();
	List<String> submissionSearchCriterias = createSubmissionSearchCriterias();
	List<String> submissionTermCriterias = createSubmissionTermSearchCriterias();

	EnumMap<ItemFolderEnum, List<String>> config = new EnumMap<>(ItemFolderEnum.class);
	config.put(ItemFolderEnum.PROJECTDETAILS, projectDetailsSearchCriterias);
	config.put(ItemFolderEnum.SUBMISSIONDETAILS, submissionSearchCriterias);
	config.put(ItemFolderEnum.TERM_LIST, termSearchCriterias);
	config.put(ItemFolderEnum.SUBMISSIONTERMLIST, submissionTermCriterias);

	return config;
    }

    private List<String> createProjectDetailSearchCriterias() {
	List<String> searchCriterias = new ArrayList<String>();
	searchCriterias.add(SearchCriteria.PROJECT_NAME.toString());
	searchCriterias.add(SearchCriteria.PROJECT_SHORT_CODE.toString());
	return searchCriterias;
    }

    private List<String> createProjectSearchCriterias() {
	List<String> searchCriterias = new ArrayList<String>();
	searchCriterias.add(SearchCriteria.PROJECT_NAME.toString());
	return searchCriterias;
    }

    private List<String> createSubmissionSearchCriterias() {
	List<String> searchCriterias = new ArrayList<String>();
	searchCriterias.add(SearchCriteria.SUBMISSION_NAME.toString());
	searchCriterias.add(SearchCriteria.SUBMISSION_PROJECT_LIST.toString());
	searchCriterias.add(SearchCriteria.LANGUAGE_DIRECTION_SUBMISSION.toString());
	searchCriterias.add(SearchCriteria.SUBMISSION_STATUSES.toString());
	searchCriterias.add(SearchCriteria.DATE_CREATED_RANGE.toString());
	searchCriterias.add(SearchCriteria.DATE_MODIFIED_RANGE.toString());
	searchCriterias.add(SearchCriteria.DATE_COMPLETED_RANGE.toString());
	return searchCriterias;
    }

    private List<String> createSubmissionTermSearchCriterias() {
	List<String> searchCriterias = new ArrayList<String>();
	searchCriterias.add(SearchCriteria.TERM_NAME.toString());
	searchCriterias.add(SearchCriteria.TERM_STATUSES.toString());
	searchCriterias.add(SearchCriteria.SUBMISSION_TERM_LANGUAGE.toString());
	searchCriterias.add(SearchCriteria.ENTITY_TYPE.toString());
	searchCriterias.add(SearchCriteria.DATE_CREATED_RANGE.toString());
	searchCriterias.add(SearchCriteria.DATE_MODIFIED_RANGE.toString());
	searchCriterias.add(SearchCriteria.DATE_COMPLETED_RANGE.toString());
	searchCriterias.add(SearchCriteria.USE_SIMPLE_FILTER.toString());
	return searchCriterias;
    }

    private List<String> createTermSearchCriterias() {
	List<String> searchCriterias = new ArrayList<String>();

	if (!GRID_FEATURE) {
	    searchCriterias.add(SearchCriteria.TERM_NAME.toString());
	    searchCriterias.add(SearchCriteria.PROJECT_LIST.toString());
	    searchCriterias.add(SearchCriteria.LANGUAGE_DIRECTION.toString());
	    searchCriterias.add(SearchCriteria.TERM_STATUSES.toString());
	    searchCriterias.add(SearchCriteria.ENTITY_TYPE.toString());
	    searchCriterias.add(SearchCriteria.DATE_CREATED_RANGE.toString());
	    searchCriterias.add(SearchCriteria.DATE_MODIFIED_RANGE.toString());
	    searchCriterias.add(SearchCriteria.USE_SIMPLE_FILTER.toString());
	    searchCriterias.add(SearchCriteria.HIDE_BLANKS.toString());
	    searchCriterias.add(SearchCriteria.CREATION_USERS.toString());
	    searchCriterias.add(SearchCriteria.MODIFICATION_USERS.toString());
	} else {
	    searchCriterias.add(SearchCriteria.TL_TERM_NAME.toString());
	    searchCriterias.add(SearchCriteria.TL_PROJECT_LIST.toString());
	    searchCriterias.add(SearchCriteria.TL_LANGUAGE_DIRECTION.toString());
	    searchCriterias.add(SearchCriteria.TL_TERM_STATUSES.toString());
	    searchCriterias.add(SearchCriteria.TL_ENTITY_TYPE.toString());
	    searchCriterias.add(SearchCriteria.TL_DATE_CREATED_RANGE.toString());
	    searchCriterias.add(SearchCriteria.TL_DATE_MODIFIED_RANGE.toString());
	    searchCriterias.add(SearchCriteria.TL_HIDE_BLANKS.toString());
	    searchCriterias.add(SearchCriteria.TL_CREATION_USERS.toString());
	    searchCriterias.add(SearchCriteria.TL_MODIFICATION_USERS.toString());
	}
	return searchCriterias;
    }
}
