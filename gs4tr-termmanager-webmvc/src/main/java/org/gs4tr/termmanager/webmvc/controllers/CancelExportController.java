package org.gs4tr.termmanager.webmvc.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gs4tr.foundation.modules.webmvc.controllers.AbstractController;
import org.gs4tr.foundation.modules.webmvc.model.ModelMapResponse;
import org.gs4tr.termmanager.service.TermEntryExporter;
import org.gs4tr.termmanager.webmvc.model.commands.CancelExportCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CancelExportController extends AbstractController {

    @Autowired
    private TermEntryExporter _termEntryExporter;

    public TermEntryExporter getTermEntryExporter() {
	return _termEntryExporter;
    }

    @RequestMapping(value = "cancelExport.ter")
    @ResponseBody
    public ModelMapResponse handle(HttpServletRequest request, HttpServletResponse response,
	    @ModelAttribute CancelExportCommand command) throws Exception {

	String threadName = command.getThreadName();

	getTermEntryExporter().requestStopExport(threadName);

	return new ModelMapResponse();
    }
}
