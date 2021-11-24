package org.gs4tr.termmanager.webmvc.controllers;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation.modules.entities.model.RepositoryItem;
import org.gs4tr.foundation.modules.entities.model.RepositoryTicket;
import org.gs4tr.foundation.modules.entities.model.StringConstants;
import org.gs4tr.foundation.modules.entities.model.UserException;
import org.gs4tr.foundation.modules.repository.RepositoryManager;
import org.gs4tr.foundation.modules.webmvc.controllers.AbstractController;
import org.gs4tr.foundation.modules.webmvc.i18n.MessageResolver;
import org.gs4tr.foundation.modules.webmvc.model.ModelMapResponse;
import org.gs4tr.termmanager.webmvc.model.commands.ViewMultimediaCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ViewMultimediaController extends AbstractController {

    private static int MAX_SIZE = 72;

    @Autowired
    private RepositoryManager _repositoryManager;

    @RequestMapping(value = "multimedia.ter")
    @ResponseBody
    public ModelMapResponse handle(HttpServletRequest request, HttpServletResponse response,
	    @ModelAttribute ViewMultimediaCommand command) throws Exception {
	return handleInternal(response, command);
    }

    private RepositoryManager getRepositoryManager() {
	return _repositoryManager;
    }

    private ModelMapResponse handleInternal(HttpServletResponse response, ViewMultimediaCommand command)
	    throws IOException {
	String resourceId = command.getTicketId();
	Boolean download = command.getDownload();
	Boolean tumbinal = command.getTumb();

	if (StringUtils.isBlank(resourceId)) {
	    throw new UserException(MessageResolver.getMessage("ViewMultimediaController.0"), //$NON-NLS-1$
		    MessageResolver.getMessage("DownloadResourceController.0"));//$NON-NLS-1$
	}

	RepositoryItem repositoryItem = getRepositoryManager().read(new RepositoryTicket(resourceId));

	String mimeType = repositoryItem.getResourceInfo().getMimeType();
	if (tumbinal && mimeType.startsWith("image")) {

	    String format = mimeType.substring(mimeType.indexOf(StringConstants.SLASH) + 1);
	    BufferedImage image = ImageIO.read(repositoryItem.getInputStream());

	    double height = image.getHeight();

	    double scale = MAX_SIZE / height;

	    int type = format.equals("png") ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
	    BufferedImage scaledImage = new BufferedImage((int) (image.getWidth() * scale),
		    (int) (image.getHeight() * scale), type);

	    Graphics2D graphics2D = scaledImage.createGraphics();

	    AffineTransform xform = AffineTransform.getScaleInstance(scale, scale);

	    graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

	    graphics2D.drawImage(image, xform, null);

	    graphics2D.dispose();

	    ByteArrayOutputStream os = new ByteArrayOutputStream();

	    ImageIO.write(scaledImage, format, os);

	    InputStream is = new ByteArrayInputStream(os.toByteArray());

	    repositoryItem.setInputStream(is);

	}

	DownloadUtils.fillDownloadResponse(repositoryItem, response, download);

	return null;
    }

}