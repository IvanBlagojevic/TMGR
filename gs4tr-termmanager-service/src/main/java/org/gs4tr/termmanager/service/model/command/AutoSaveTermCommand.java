package org.gs4tr.termmanager.service.model.command;

import org.gs4tr.termmanager.model.BaseTypeEnum;

public class AutoSaveTermCommand {

    private BaseTypeEnum _baseType;

    private String _entityId;

    private boolean _newAttribute;

    private String _parentEntityId;

    private boolean _term;

    private String _text;

    private String _type;

    public BaseTypeEnum getBaseType() {
	return _baseType;
    }

    public String getEntityId() {
	return _entityId;
    }

    public String getParentEntityId() {
	return _parentEntityId;
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

    public void setBaseType(BaseTypeEnum baseType) {
	_baseType = baseType;
    }

    public void setEntityId(String entityId) {
	_entityId = entityId;
    }

    public void setNewAttribute(boolean newAttribute) {
	_newAttribute = newAttribute;
    }

    public void setParentEntityId(String parentEntityId) {
	_parentEntityId = parentEntityId;
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
