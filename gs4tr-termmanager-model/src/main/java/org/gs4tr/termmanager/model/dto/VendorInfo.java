package org.gs4tr.termmanager.model.dto;

public class VendorInfo {

    private String _name;

    private String _ticket;

    private Boolean _enabled = Boolean.TRUE;

    public Boolean getEnabled() {
	return _enabled;
    }

    public String getName() {
	return _name;
    }

    public String getTicket() {
	return _ticket;
    }

    public void setEnabled(Boolean enabled) {
	_enabled = enabled;
    }

    public void setName(String name) {
	_name = name;
    }

    public void setTicket(String ticket) {
	_ticket = ticket;
    }
}
