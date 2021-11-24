package org.gs4tr.termmanager.service.model.command;

import org.gs4tr.foundation.modules.entities.model.OrganizationInfo;

public class OrganizationCommand {

    private String _name;

    private Long _organizationId;

    private OrganizationInfo _organizationInfo;

    private Long _parentOrganizationId;

    private String currencyCode;

    public String getCurrencyCode() {
	return currencyCode;
    }

    public String getName() {
	return _name;
    }

    public Long getOrganizationId() {
	return _organizationId;
    }

    public OrganizationInfo getOrganizationInfo() {
	return _organizationInfo;
    }

    public Long getParentOrganizationId() {
	return _parentOrganizationId;
    }

    public void setCurrencyCode(String currencyCode) {
	this.currencyCode = currencyCode;
    }

    public void setName(String name) {
	_name = name;
    }

    public void setOrganizationId(Long organizationId) {
	_organizationId = organizationId;
    }

    public void setOrganizationInfo(OrganizationInfo organizationInfo) {
	_organizationInfo = organizationInfo;
    }

    public void setParentOrganizationId(Long parentOrganizationId) {
	_parentOrganizationId = parentOrganizationId;
    }

}
