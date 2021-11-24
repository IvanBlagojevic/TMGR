package org.gs4tr.termmanager.webmvc.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gs4tr.foundation.modules.webmvc.controllers.AbstractController;
import org.gs4tr.foundation.modules.webmvc.model.ModelMapResponse;
import org.gs4tr.termmanager.model.Version;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AboutController extends AbstractController {

    private static final String VERSION = "version";

    @RequestMapping(value = "about.ter", method = RequestMethod.POST)
    @ResponseBody
    public ModelMapResponse handleRequestInternal(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
	ModelMapResponse mapResponse = new ModelMapResponse();

	mapResponse.put(VERSION, Version.getVersion());

	return mapResponse;
    }

}
