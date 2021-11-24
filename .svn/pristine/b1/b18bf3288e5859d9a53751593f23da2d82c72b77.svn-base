package org.gs4tr.termmanager.model.dto.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.gs4tr.termmanager.model.ProjectLanguageDetailInfoIO;
import org.gs4tr.termmanager.model.ProjectUserDetailIO;
import org.gs4tr.termmanager.model.dto.ProjectDetailCountsIO;
import org.gs4tr.termmanager.model.dto.ProjectDetailsIO;
import org.gs4tr.termmanager.model.dto.ProjectInfoDetailsDto;
import org.gs4tr.termmanager.model.event.ProjectDetailInfo;

public class ProjectDetailsIOConverter {

    public static ProjectDetailsIO fromInternalToIo(ProjectDetailInfo info) {

	if (Objects.nonNull(info)) {

	    ProjectInfoDetailsDto infoDto = new ProjectInfoDetailsDto();
	    infoDto.setActiveSubmissionCount(info.getActiveSubmissionCount().longValue());
	    infoDto.setCompletedSubmissionCount(info.getCompletedSubmissionCount().longValue());
	    infoDto.setDateModified(info.getDateModified());
	    infoDto.setLanguageActiveSubmissionCount(convertMapWithStringKey(info.getLanguageActiveSubmissionCount()));
	    infoDto.setLanguageApprovedTermCount(convertMapWithStringKey(info.getLanguageApprovedTermCount()));
	    infoDto.setLanguageCompletedSubmissionCount(
		    convertMapWithStringKey(info.getLanguageCompletedSubmissionCount()));
	    infoDto.setLanguageForbiddenTermCount(convertMapWithStringKey(info.getLanguageForbiddenTermCount()));
	    infoDto.setLanguageOnHoldTermCount(convertMapWithStringKey(info.getLanguageOnHoldTermCount()));
	    infoDto.setLanguagePendingTermCount(convertMapWithStringKey(info.getLanguagePendingTermCount()));
	    infoDto.setLanguageTermCount(convertMapWithStringKey(info.getLanguageTermCount()));
	    infoDto.setLanguageTermInSubmissionCount(convertMapWithStringKey(info.getLanguageTermInSubmissionCount()));
	    infoDto.setProjectId(info.getProjectId());
	    infoDto.setTermEntryCount(info.getTermEntryCount().longValue());
	    infoDto.setUpdatedLanguages(info.getUpdatedLanguages());
	    infoDto.setUpdatedUserIds(info.getUpdatedUserIds());
	    infoDto.setUserActiveSubmissionCount(convertMapWithLongKey(info.getUserActiveSubmissionCount()));
	    infoDto.setUserCompletedSubmissionCount(convertMapWithLongKey(info.getUserCompletedSubmissionCount()));
	    infoDto.setUserLanguageActiveSubmissionCount(
		    convertMapWithMapValue(info.getUserLanguageActiveSubmissionCount()));
	    infoDto.setUserLanguageCompletedSubmissionCount(
		    convertMapWithMapValue(info.getUserLanguageCompletedSubmissionCount()));
	    infoDto.setUserTermEntryCount(convertMapWithLongKey(info.getUserTermEntryCount()));

	    return convertToProjectDetail(infoDto);

	}

	return null;
    }

    private static Map<Long, Long> convertMapWithLongKey(Map<Long, AtomicLong> map) {
	Map<Long, Long> ioMap = new HashMap<>();

	for (Map.Entry<Long, AtomicLong> entry : map.entrySet()) {
	    ioMap.put(entry.getKey(), entry.getValue().longValue());
	}
	return ioMap;

    }

    private static Map<Long, Map<String, Long>> convertMapWithMapValue(Map<Long, Map<String, AtomicLong>> map) {

	Map<Long, Map<String, Long>> ioMap = new HashMap<>();

	for (Map.Entry<Long, Map<String, AtomicLong>> entry : map.entrySet()) {
	    Map<String, Long> value = convertMapWithStringKey(entry.getValue());
	    ioMap.put(entry.getKey(), value);
	}

	return ioMap;
    }

    private static Map<String, Long> convertMapWithStringKey(Map<String, AtomicLong> map) {

	Map<String, Long> ioMap = new HashMap<>();

	for (Map.Entry<String, AtomicLong> entry : map.entrySet()) {
	    ioMap.put(entry.getKey(), entry.getValue().longValue());
	}
	return ioMap;
    }

    private static ProjectDetailsIO convertToProjectDetail(ProjectInfoDetailsDto dtoInfo) {

	long projectId = dtoInfo.getProjectId();

	ProjectDetailCountsIO projectDetailCounts = convertToProjectInfoDetails(dtoInfo, projectId);
	List<ProjectLanguageDetailInfoIO> projectLanguageDetailInfo = convertToProjectLanguageInfo(dtoInfo, projectId);
	List<ProjectUserDetailIO> projectUserDetails = convertToProjectUserInfo(dtoInfo, projectId);

	ProjectDetailsIO details = new ProjectDetailsIO();
	details.setProjectId(projectId);
	details.setProjectInfoDetails(projectDetailCounts);
	details.setProjectLanguageDetails(projectLanguageDetailInfo);
	details.setProjectUserDetails(projectUserDetails);

	return details;

    }

    private static ProjectDetailCountsIO convertToProjectInfoDetails(ProjectInfoDetailsDto dtoInfo, long projectId) {
	ProjectDetailCountsIO projectInfoDetails = new ProjectDetailCountsIO();

	projectInfoDetails.setActiveSubmissionCount(dtoInfo.getActiveSubmissionCount());
	projectInfoDetails.setCompletedSubmissionCount(dtoInfo.getCompletedSubmissionCount());
	projectInfoDetails.setDateModified(dtoInfo.getDateModified());
	projectInfoDetails.setApprovedTermCount(dtoInfo.getTotalCount(dtoInfo.getLanguageApprovedTermCount()));
	projectInfoDetails.setTermInSubmissionCount(dtoInfo.getTotalCount(dtoInfo.getLanguageTermInSubmissionCount()));
	projectInfoDetails.setForbiddenTermCount(dtoInfo.getTotalCount(dtoInfo.getLanguageForbiddenTermCount()));
	projectInfoDetails.setPendingTermCount(dtoInfo.getTotalCount(dtoInfo.getLanguagePendingTermCount()));
	projectInfoDetails.setOnHoldTermCount(dtoInfo.getTotalCount(dtoInfo.getLanguageOnHoldTermCount()));
	projectInfoDetails.setTermEntryCount(dtoInfo.getTermEntryCount());
	projectInfoDetails.setProjectId(projectId);
	projectInfoDetails.setTotalCount();
	return projectInfoDetails;

    }

    private static List<ProjectLanguageDetailInfoIO> convertToProjectLanguageInfo(ProjectInfoDetailsDto dtoInfo,
                                                                                  long projectId) {

	Set<String> languageIds = dtoInfo.getUpdatedLanguages();

	List<ProjectLanguageDetailInfoIO> infos = new ArrayList<>();

	for (String languageId : languageIds) {

	    ProjectLanguageDetailInfoIO info = new ProjectLanguageDetailInfoIO(languageId);
	    info.setProjectId(projectId);
	    info.setActiveSubmissionCount(
		    dtoInfo.getLanguageCount(languageId, dtoInfo.getLanguageActiveSubmissionCount()));
	    info.setApprovedTermCount(dtoInfo.getLanguageCount(languageId, dtoInfo.getLanguageApprovedTermCount()));
	    info.setCompletedSubmissionCount(
		    dtoInfo.getLanguageCount(languageId, dtoInfo.getLanguageCompletedSubmissionCount()));
	    info.setDateModified(dtoInfo.getDateModified());
	    info.setForbiddenTermCount(dtoInfo.getLanguageCount(languageId, dtoInfo.getLanguageForbiddenTermCount()));
	    info.setOnHoldTermCount(dtoInfo.getLanguageCount(languageId, dtoInfo.getLanguageOnHoldTermCount()));
	    info.setPendingTermCount(dtoInfo.getLanguageCount(languageId, dtoInfo.getLanguagePendingTermCount()));
	    info.setTermEntryCount(dtoInfo.getTermEntryCount());
	    info.setTermCount(dtoInfo.getLanguageCount(languageId, dtoInfo.getLanguageTermCount()));
	    info.setTermInSubmissionCount(
		    dtoInfo.getLanguageCount(languageId, dtoInfo.getLanguageTermInSubmissionCount()));
	    info.setActiveSubmissionCount(
		    dtoInfo.getLanguageCount(languageId, dtoInfo.getLanguageActiveSubmissionCount()));
	    infos.add(info);

	}

	return infos;
    }

    private static List<ProjectUserDetailIO> convertToProjectUserInfo(ProjectInfoDetailsDto dtoInfo, long projectId) {

	Set<Long> userIds = dtoInfo.getUpdatedUserIds();

	List<ProjectUserDetailIO> infos = new ArrayList<>();

	for (Long userId : userIds) {

	    ProjectUserDetailIO info = new ProjectUserDetailIO(userId);
	    info.setProjectId(projectId);
	    info.setDateModified(dtoInfo.getDateModified());
	    info.setActiveSubmissionCount(dtoInfo.getUserTotalCount(userId, dtoInfo.getUserActiveSubmissionCount()));
	    info.setCompletedSubmissionCount(
		    dtoInfo.getUserTotalCount(userId, dtoInfo.getUserCompletedSubmissionCount()));
	    infos.add(info);

	}

	return infos;
    }
}
