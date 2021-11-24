package org.gs4tr.termmanager.model;

import java.util.HashMap;
import java.util.Map;

public class UpdateCommand {

    public enum CommandEnum {

	ADD("add", 3), REMOVE("remove", 1), REVIEW("review", 5), TRANSLATE("translate", 4), UPDATE("update",
		2), UPDATE_TRANSLATION("updateTranslation", 6);

	static private Map<String, CommandEnum> _map = new HashMap<String, CommandEnum>();
	static {
	    for (CommandEnum c : CommandEnum.values()) {
		_map.put(c.value(), c);
	    }
	}

	static public CommandEnum parseToCommandEnum(String value) {
	    CommandEnum command = _map.get(value);
	    if (command == null) {
		throw new RuntimeException(String.format("Bad command name: \"%s\".", value));
	    }
	    return command;
	}

	private int _priority;

	String _value;

	private CommandEnum(String value) {
	    _value = value;
	}

	private CommandEnum(String value, int priority) {
	    _value = value;
	    _priority = priority;
	}

	public int compare(CommandEnum command) {
	    if (command != null) {
		return getPriority() - command.getPriority();
	    } else {
		return getPriority();
	    }
	}

	public String getName() {
	    return _value;
	}

	public int getPriority() {
	    return _priority;
	}

	private String value() {
	    return _value;
	}

    }

    public enum TypeEnum {
	COMMENT("comment", 6), DESCRIP("description", 4), LANG("language", 2), NOTE("note", 5), TERM("term",
		3), TERMENTRY("termEntry", 1);

	static private Map<String, TypeEnum> _map = new HashMap<String, TypeEnum>();
	static {
	    for (TypeEnum t : TypeEnum.values()) {
		_map.put(t.value(), t);
	    }
	}

	static public TypeEnum parseToTypeEnum(String value) {
	    TypeEnum type = _map.get(value);
	    if (type == null) {
		throw new RuntimeException(String.format("Bad command name: \"%s\".", value)); //$NON-NLS-1$
	    }
	    return type;
	}

	private int _priority;

	private String _value;

	private TypeEnum(String value, int priority) {
	    _value = value;
	    _priority = priority;
	}

	public int compare(TypeEnum type) {
	    if (type != null) {
		return getPriority() - type.getPriority();
	    } else {
		return getPriority();
	    }
	}

	public String getName() {
	    return _value;
	}

	public int getPriority() {
	    return _priority;
	}

	private String value() {
	    return _value;
	}
    }

    private String _asssignee;
    private CommandEnum _command;
    private String _languageId;
    private String _markerId;
    private String _oldValue;
    private String _parentMarkerId;
    private Long _projectId;
    private String _status;
    private String _statusOld;
    private String _subType;
    private String _termId;
    private TypeEnum _type;
    private String _value;

    public UpdateCommand() {
    }

    // for termEntry
    public UpdateCommand(String markerId, Long projectId) {
        _markerId = markerId;
        _projectId = projectId;
    }

    // for term
    public UpdateCommand(String languageId, String parentMarkerId, String markerId, String value) {
        _languageId = languageId;
        _parentMarkerId = parentMarkerId;
        _markerId = markerId;
        _value = value;
    }

    // for term in translation
    public UpdateCommand(String languageId, String parentMarkerId, String markerId, String value, String asssignee) {
        _languageId = languageId;
        _parentMarkerId = parentMarkerId;
        _markerId = markerId;
        _value = value;
        _asssignee = asssignee;
    }

    // for description/note
    public UpdateCommand(String command, String itemType, String subType, String markerId, String parentMarkerId,
                         String value) {
        _command = CommandEnum.parseToCommandEnum(command);
        _type = TypeEnum.parseToTypeEnum(itemType);
        _subType = subType;
        _markerId = markerId;
        _parentMarkerId = parentMarkerId;
        _value = value;
    }

    public String getAsssignee() {
	return _asssignee;
    }

    public String getCommand() {
	return _command.toString();
    }

    public CommandEnum getCommandEnum() {
	return _command;
    }

    public String getLanguageId() {
	return _languageId;
    }

    public String getMarkerId() {
	return _markerId;
    }

    public String getOldValue() {
	return _oldValue;
    }

    public String getParentMarkerId() {
	return _parentMarkerId;
    }

    public Long getProjectId() {
	return _projectId;
    }

    public String getStatus() {
	return _status;
    }

    public String getStatusOld() {
	return _statusOld;
    }

    public String getSubType() {
	return _subType;
    }

    public String getTermId() {
	return _termId;
    }

    public String getType() {
	return _type.toString();
    }

    public TypeEnum getTypeEnum() {
	return _type;
    }

    public String getValue() {
	return _value;
    }

    public void setAsssignee(String asssignee) {
	_asssignee = asssignee;
    }

    public void setCommand(String command) {
	_command = CommandEnum.parseToCommandEnum(command);
    }

    public void setItemType(String type) {
	_type = TypeEnum.parseToTypeEnum(type);
    }

    public void setLanguageId(String languageId) {
	_languageId = languageId;
    }

    public void setMarkerId(String markerId) {
	_markerId = markerId;
    }

    public void setOldValue(String oldValue) {
	_oldValue = oldValue;
    }

    public void setParentMarkerId(String parentMarkerId) {
	_parentMarkerId = parentMarkerId;
    }

    public void setProjectId(Long projectId) {
	_projectId = projectId;
    }

    public void setStatus(String status) {
	_status = status;
    }

    public void setStatusOld(String statusOld) {
	_statusOld = statusOld;
    }

    public void setSubType(String subType) {
	_subType = subType;
    }

    public void setTermId(String termId) {
	_termId = termId;
    }

    public void setValue(String value) {
	_value = value;
    }
}
