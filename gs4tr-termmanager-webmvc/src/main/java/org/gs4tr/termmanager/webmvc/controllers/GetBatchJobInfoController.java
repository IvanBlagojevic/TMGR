package org.gs4tr.termmanager.webmvc.controllers;

import static org.gs4tr.termmanager.model.dto.converter.BatchJobInfoConverter.fromInternalToDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.modules.webmvc.controllers.AbstractController;
import org.gs4tr.foundation.modules.webmvc.model.ModelMapResponse;
import org.gs4tr.termmanager.model.BatchJobInfo;
import org.gs4tr.termmanager.model.BatchJobName;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.service.batch.info.provider.BatchJobsInfoProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class GetBatchJobInfoController extends AbstractController {

    private static final String KEEP_PINGING_KEY = "keepPinging";
    private static final String PROCESSED_ITEM_KEY = "processedItem";
    private static final String PROCESSING_COMPLEATED = "processingCompleated";
    private static final String PROCESSING_STATS_KEY = "processedStats";
    private static final String PROCESS_NAME_KEY = "processName";

    @Autowired
    private BatchJobsInfoProvider<String> _batchJobsInfoProvider;

    public BatchJobsInfoProvider<String> getBatchJobsInfoProvider() {
	return _batchJobsInfoProvider;
    }

    @RequestMapping(value = "batchProcessController.ter", method = RequestMethod.POST)
    @ResponseBody
    public ModelMapResponse handleRequest(HttpServletRequest req, HttpServletResponse resp) throws Exception {
	ModelMapResponse response = new ModelMapResponse();

	TmUserProfile userProfile = TmUserProfile.getCurrentUserProfile();

	String username = userProfile.getUserName();

	BatchJobInfo batchJobInfo = getBatchJobsInfoProvider().provideCompletedBatchJobInfo(username);
	if (Objects.nonNull(batchJobInfo)) {
	    response.put(PROCESSED_ITEM_KEY, fromInternalToDto(batchJobInfo));
	}
	List<BatchJobName> batchJobsInProcess = getBatchJobsInfoProvider().provideInProcessBatchJobs(username);

	if (CollectionUtils.isEmpty(batchJobsInProcess)) {
	    response.put(PROCESSING_COMPLEATED, Boolean.TRUE);
	    response.put(KEEP_PINGING_KEY, Boolean.FALSE);
	} else {
	    response.put(PROCESSING_STATS_KEY, collect(batchJobsInProcess));
	    response.put(PROCESSING_COMPLEATED, Boolean.FALSE);
	    response.put(KEEP_PINGING_KEY, Boolean.TRUE);
	}
	return response;
    }

    private List<Map<String, Object>> collect(List<BatchJobName> batchJobNames) {
	List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
	for (BatchJobName batchJobName : batchJobNames) {
	    Map<String, Object> map = new HashMap<String, Object>();
	    map.put(PROCESS_NAME_KEY, batchJobName.getProcessDisplayName());
	    result.add(map);
	}
	return result;
    }
}
