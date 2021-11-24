package org.gs4tr.termmanager.model.dto;

public class Policy {

    private String _policyId;

    private String _category;

    private RoleTypeEnum _policyType;

    public String getCategory() {
	return _category;
    }

    public String getPolicyId() {
	return _policyId;
    }

    public RoleTypeEnum getPolicyType() {
	return _policyType;
    }

    public void setCategory(String category) {
	_category = category;
    }

    public void setPolicyId(String policyId) {
	_policyId = policyId;
    }

    public void setPolicyType(RoleTypeEnum policyType) {
	_policyType = policyType;
    }

}
