package org.gs4tr.termmanager.service.model.command.dto;

import org.apache.commons.lang.StringUtils;
import org.gs4tr.termmanager.model.BaseTypeEnum;
import org.gs4tr.termmanager.service.model.command.AutoSaveTermCommand;

public class DtoAutoSaveTermCommand implements DtoTaskHandlerCommand<AutoSaveTermCommand> {

    private String _baseType;

    private String _entityTicket;

    private boolean _newAttribute = false;

    private String _parentEntityTicket;

    private boolean _term = true;

    private String _text;

    private String _type;

    @Override
    public AutoSaveTermCommand convertToInternalTaskHandlerCommand() {
	AutoSaveTermCommand command = new AutoSaveTermCommand();
	command.setEntityId(getEntityTicket());
	command.setText(getText());
	command.setTerm(isTerm());
	command.setParentEntityId(getParentEntityTicket());
	command.setType(getType());
	command.setNewAttribute(isNewAttribute());
	String baseType = getBaseType();
	if (StringUtils.isNotEmpty(baseType)) {
	    if (BaseTypeEnum.NOTE.getTypeName().equals(baseType)) {
		command.setBaseType(BaseTypeEnum.NOTE);
	    } else {
		command.setBaseType(BaseTypeEnum.DESCRIPTION);
	    }
	}

	return command;
    }

    public String getBaseType() {
	return _baseType;
    }

    public String getEntityTicket() {
	return _entityTicket;
    }

    public String getParentEntityTicket() {
	return _parentEntityTicket;
    }

    public String getText() {
	return _text;
    }

    public String getType() {
	return _type;
    }

    public boolean isNewAttribute() {
	return _newAttribute;
    }

    public boolean isTerm() {
	return _term;
    }

    public void setBaseType(String baseType) {
	_baseType = baseType;
    }

    public void setEntityTicket(String entityTicket) {
	_entityTicket = entityTicket;
    }

    public void setNewAttribute(boolean newAttribute) {
	_newAttribute = newAttribute;
    }

    public void setParentEntityTicket(String parentEntityTicket) {
	_parentEntityTicket = parentEntityTicket;
    }

    public void setTerm(boolean term) {
	_term = term;
    }

    public void setText(String text) {
	_text = text;
    }

    public void setType(String type) {
	_type = type;
    }
}
