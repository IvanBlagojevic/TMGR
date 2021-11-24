package org.gs4tr.termmanager.service.notification;

import org.gs4tr.foundation.modules.mail.model.EMailMessageTemplate;
import org.gs4tr.foundation.modules.mail.service.MailTemplatesService;
import org.springframework.beans.factory.annotation.Autowired;

public class TaskNotification {

    private String _mailTemplate;

    @Autowired
    private MailTemplatesService _mailTemplatesService;

    public EMailMessageTemplate getEMailMessageTemplate() {
	return getMailTemplatesService().getEMailMessageTemplate(getMailTemplate());
    }

    public String getMailTemplate() {
	return _mailTemplate;
    }

    public void setMailTemplate(String mailTemplate) {
	_mailTemplate = mailTemplate;
    }

    private MailTemplatesService getMailTemplatesService() {
	return _mailTemplatesService;
    }
}
