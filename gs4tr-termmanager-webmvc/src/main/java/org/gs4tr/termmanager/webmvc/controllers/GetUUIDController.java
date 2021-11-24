package org.gs4tr.termmanager.webmvc.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation.modules.webmvc.controllers.AbstractController;
import org.gs4tr.foundation.modules.webmvc.model.ModelMapResponse;
import org.gs4tr.termmanager.service.UuidGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class GetUUIDController extends AbstractController {

    private static final String AVAILABLE_UUIDS = "availableUUIDs";

    private static final int DEFAULT_NUMBER_OF_UUIDS = 10;

    private static final String NUMBER_OF_PARAM = "numberOf";

    private int _numberOfUUIDs = DEFAULT_NUMBER_OF_UUIDS;

    @Autowired
    private UuidGeneratorService _uuidGeneratorService;

    @RequestMapping(value = "getUUIDs.ter", method = RequestMethod.POST)
    @ResponseBody
    public ModelMapResponse handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
	    throws Exception {
	String noParam = request.getParameter(NUMBER_OF_PARAM);
	int no = getNumberOfUUIDs();
	if (StringUtils.isNotBlank(noParam)) {
	    no = Integer.parseInt(noParam);
	}

	String[] uuids = getUuidGeneratorService().generateUUID(no);

	ModelMapResponse mapResponse = new ModelMapResponse();
	mapResponse.put(AVAILABLE_UUIDS, uuids);

	return mapResponse;
    }

    private int getNumberOfUUIDs() {
	return _numberOfUUIDs;
    }

    private UuidGeneratorService getUuidGeneratorService() {
	return _uuidGeneratorService;
    }
}
