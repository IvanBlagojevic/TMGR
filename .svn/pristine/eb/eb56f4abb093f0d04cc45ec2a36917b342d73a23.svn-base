package org.gs4tr.termmanager.webmvc.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.modules.entities.model.RepositoryItem;
import org.gs4tr.foundation.modules.entities.model.UserException;
import org.gs4tr.foundation.modules.webmvc.controllers.AbstractController;
import org.gs4tr.foundation.modules.webmvc.i18n.MessageResolver;
import org.gs4tr.foundation.modules.webmvc.model.ModelMapResponse;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.service.TermEntryService;
import org.gs4tr.termmanager.webmvc.model.commands.DownloadResourceCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DownloadResourceController extends AbstractController {

    private static final String TEMP_FOLDER_PATH = System.getProperty("java.io.tmpdir") //$NON-NLS-1$
	    + System.getProperty("file.separator"); //$NON-NLS-1$

    private Log _logger = LogFactory.getLog(DownloadResourceController.class);

    @Autowired
    private TermEntryService _termEntryService;

    @RequestMapping(value = "deleteTemp.ter", method = RequestMethod.POST)
    @ResponseBody
    public ModelMapResponse deleteTemp(HttpServletRequest request, HttpServletResponse response,
	    @ModelAttribute DownloadResourceCommand command) throws Exception {

	String resourceTicket = command.getDownloadTicket();

	if (StringUtils.isBlank(resourceTicket)) {
	    _logger.error(Messages.getString("DeleteResourceController.1")); //$NON-NLS-1$
	    return null;
	}

	File tempFile = new File(TEMP_FOLDER_PATH + resourceTicket);

	if (tempFile.exists()) {
	    FileUtils.forceDelete(tempFile);
	}

	return new ModelMapResponse();
    }

    @RequestMapping(value = "download.ter", method = RequestMethod.POST)
    @ResponseBody
    public ModelMapResponse downloadResource(HttpServletRequest request, HttpServletResponse response,
	    @ModelAttribute DownloadResourceCommand downloadResourceCommand) throws Exception {

	String resourceTicket = downloadResourceCommand.getDownloadTicket();

	if (StringUtils.isBlank(resourceTicket)) {
	    throw new UserException(MessageResolver.getMessage("DownloadResourceController.1"), //$NON-NLS-1$
		    MessageResolver.getMessage("DownloadResourceController.0"));//$NON-NLS-1$
	}

	Long resourceId = TicketConverter.fromDtoToInternal(resourceTicket, Long.class);
	RepositoryItem repositoryItem = getTermEntryService().downloadResource(resourceId);
	DownloadUtils.fillDownloadResponse(repositoryItem, response, Boolean.TRUE);

	return null;
    }

    @RequestMapping(value = "downloadTemp.ter", method = RequestMethod.POST)
    public ModelMapResponse downloadTempFile(HttpServletRequest request, HttpServletResponse response,
	    @ModelAttribute DownloadResourceCommand command) throws IOException {
	/* requested by UI TERII-4702 */
	if (!command.isDownload()) {
	    return new ModelMapResponse();
	}

	String resourceTicket = command.getDownloadTicket();

	if (StringUtils.isBlank(resourceTicket)) {
	    throw new UserException(MessageResolver.getMessage("DownloadResourceController.1"), //$NON-NLS-1$
		    MessageResolver.getMessage("DownloadResourceController.0"));//$NON-NLS-1$
	}

	File tempFile = new File(TEMP_FOLDER_PATH + resourceTicket);

	InputStream stream = null;
	try {
	    stream = new FileInputStream(tempFile);
	} catch (IOException e) {
	    throw new UserException(MessageResolver.getMessage("DownloadResourceController.1"), //$NON-NLS-1$
		    MessageResolver.getMessage("DownloadResourceController.2"), e); //$NON-NLS-1$
	}

	String fileName = resourceTicket;

	if (command.isSendWithoutDispositionHeader()) {
	    DownloadUtils.fillResponse(fileName, stream, response, DownloadUtils.DEFAULT_MIME_TYPE, false);
	} else {
	    DownloadUtils.fillDownloadResponse(fileName, stream, response);
	}

	if (stream != null) {
	    IOUtils.closeQuietly(stream);
	}

	if (!command.isSendWithoutDispositionHeader()) {
	    try {
		FileUtils.forceDelete(tempFile);
	    } catch (IOException e) {
		_logger.error(String.format(Messages.getString("DownloadResourceController.3"), //$NON-NLS-1$
			resourceTicket));
	    }
	}
	return null;
    }

    private TermEntryService getTermEntryService() {
	return _termEntryService;
    }
}