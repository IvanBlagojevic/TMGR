package org.gs4tr.termmanager.webmvc.controllers;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation.modules.webmvc.controllers.AbstractController;
import org.gs4tr.foundation.modules.webmvc.model.ModelMapResponse;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.service.TermEntryService;
import org.gs4tr.termmanager.webmvc.model.commands.DeleteResourceCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DeleteResourceController extends AbstractController {

    @Autowired
    private TermEntryService _termEntryService;

    @RequestMapping(value = "deleteResource.ter", method = RequestMethod.POST)
    @ResponseBody
    public ModelMapResponse handle(HttpServletRequest request, HttpServletResponse response,
	    @ModelAttribute DeleteResourceCommand command) throws Exception {

	String termEntryTicket = command.getTermEntryTicket();
	String[] resourceIds = command.getResourceIds();

	if (StringUtils.isBlank(termEntryTicket)) {
	    throw new RuntimeException(Messages.getString("DeleteResourceController.0")); //$NON-NLS-1$
	}

	Long projectId = null;

	String projectTicket = command.getProjectTicket();
	if (StringUtils.isNotEmpty(projectTicket)) {
	    projectId = TicketConverter.fromDtoToInternal(projectTicket, Long.class);
	}

	getTermEntryService().deleteTermEntryResourceTracks(termEntryTicket, Arrays.asList(resourceIds), projectId);

	return new ModelMapResponse();
    }

    private TermEntryService getTermEntryService() {
	return _termEntryService;
    }
}