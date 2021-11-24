package org.gs4tr.termmanager.webmvc.controllers;

import org.gs4tr.foundation.modules.webmvc.model.ModelMapResponse;
import org.gs4tr.termmanager.service.ImportTermService;
import org.gs4tr.termmanager.webmvc.model.commands.CancelImportRequestCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CancelImportController {

    @Autowired
    private ImportTermService _importTermService;

    @RequestMapping(value = "cancelImportRequest.ter", method = RequestMethod.POST)
    @ResponseBody
    public ModelMapResponse handle(@ModelAttribute CancelImportRequestCommand command) {

	getImportTermService().cancelImportRequest(command.getThreadNames());

	return new ModelMapResponse();
    }

    private ImportTermService getImportTermService() {
	return _importTermService;
    }

}
