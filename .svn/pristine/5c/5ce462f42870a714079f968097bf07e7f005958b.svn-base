package org.gs4tr.termmanager.service.model.command.dto;

import java.util.LinkedHashSet;
import java.util.Set;

import org.gs4tr.termmanager.service.model.command.SendConnectionCommand;

public class DtoSendConnectionCommand implements DtoTaskHandlerCommand<SendConnectionCommand> {

    private String[] _connectionStrings;

    private String _emails;

    private String _sourceLanguage;

    private String _userTicket;

    @Override
    public SendConnectionCommand convertToInternalTaskHandlerCommand() {
	SendConnectionCommand command = new SendConnectionCommand();

	command.setEmails(getEmails());
	command.setSourceLanguage(getSourceLanguage());

	String[] connectionStrings = getConnectionStrings();
	if (connectionStrings != null) {
	    Set<String> connStringList = new LinkedHashSet<String>();
	    for (int i = 0; i < connectionStrings.length; i++) {
		connStringList.add(connectionStrings[i]);
	    }

	    command.setConnectionStrings(connStringList);
	}

	return command;
    }

    public String[] getConnectionStrings() {
	return _connectionStrings;
    }

    public String getEmails() {
	return _emails;
    }

    public String getSourceLanguage() {
	return _sourceLanguage;
    }

    public String getUserTicket() {
	return _userTicket;
    }

    public void setConnectionStrings(String[] connectionStrings) {
	_connectionStrings = connectionStrings;
    }

    public void setEmails(String emails) {
	_emails = emails;
    }

    public void setSourceLanguage(String sourceLanguage) {
	_sourceLanguage = sourceLanguage;
    }

    public void setUserTicket(String userTicket) {
	_userTicket = userTicket;
    }

}
