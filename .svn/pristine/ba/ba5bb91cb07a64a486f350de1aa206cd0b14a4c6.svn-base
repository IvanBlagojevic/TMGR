package org.gs4tr.termmanager.service.model.command.dto;

import org.gs4tr.foundation.modules.entities.model.UserTypeEnum;
import org.gs4tr.foundation.modules.security.validation.constraints.NoHtmlCanonicalized;
import org.gs4tr.termmanager.service.model.command.UserInfoCommand;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.Size;

public class DtoUserInfoCommand implements DtoTaskHandlerCommand<UserInfoCommand>{

    @Size(max = 50)
    @NoHtmlCanonicalized
    private String _address;

    @Size(max = 40)
    @NoHtmlCanonicalized
    private String _department;

    @Size(max = 512)
    @NoHtmlCanonicalized
    private String _description;

    @Email
    private String _emailAddress;

    private Boolean _emailNotification;

    private Boolean _enabled;

    @Size(max = 25)
    @NoHtmlCanonicalized
    private String _fax;

    @Size(max = 50)
    @NoHtmlCanonicalized
    private String _firstName;

    @Size(max = 50)
    @NoHtmlCanonicalized
    private String _lastName;

    private String _password;

    @Size(max = 25)
    @NoHtmlCanonicalized
    private String _phone1;

    @Size(max = 25)
    @NoHtmlCanonicalized
    private String _phone2;

    private Boolean _showDescription;

    private Boolean _ssoUser = Boolean.FALSE;

    @Size(max = 30)
    @NoHtmlCanonicalized
    private String _timeZone;

    private String _tptOauthOrganization;

    @Size(max = 255)
    @NoHtmlCanonicalized
    private String _userGroup;

    @Size(max = 128)
    @NoHtmlCanonicalized
    private String _userName;

    @Size(max = 255)
    @NoHtmlCanonicalized
    private String _userType;

    @Override
    public UserInfoCommand convertToInternalTaskHandlerCommand() {

        UserInfoCommand command = new UserInfoCommand();

        command.setFirstName(getFirstName());
        command.setLastName(getLastName());
        command.setUserName(getUserName());
        command.setEmailAddress(getEmailAddress());
        command.setEmailNotification(getEmailNotification());
        command.setTimeZone(getTimeZone());
        command.setAddress(getAddress());
        command.setPhone1(getPhone1());
        command.setPhone2(getPhone2());
        command.setFax(getFax());
        command.setDepartment(getDepartment());
        command.setDescription(getDescription());
        command.setShowDescription(getShowDescription());
        command.setPassword(getPassword());
        command.setSsoUser(getSsoUser());
        command.setEnabled(getEnabled());
        command.setUserGroup(getUserGroup());
        command.setUserType(UserTypeEnum.valueOf(getUserType()));
        command.setTptOauthOrganization(getTptOauthOrganization());

        return command;
    }

    public String getAddress() {
        return _address;
    }

    public String getDepartment() {
        return _department;
    }

    public String getDescription() {
        return _description;
    }

    public String getEmailAddress() {
        return _emailAddress;
    }

    public Boolean getEmailNotification() {
        return _emailNotification;
    }

    public Boolean getEnabled() {
        return _enabled;
    }

    public String getFax() {
        return _fax;
    }

    public String getFirstName() {
        return _firstName;
    }

    public String getLastName() {
        return _lastName;
    }

    public String getPassword() {
        return _password;
    }

    public String getPhone1() {
        return _phone1;
    }

    public String getPhone2() {
        return _phone2;
    }

    public Boolean getShowDescription() {
        return _showDescription;
    }

    public Boolean getSsoUser() {
        return _ssoUser;
    }

    public String getTimeZone() {
        return _timeZone;
    }

    public String getTptOauthOrganization() {
        return _tptOauthOrganization;
    }

    public String getUserGroup() {
        return _userGroup;
    }

    public String getUserName() {
        return _userName;
    }

    public String getUserType() {
        return _userType;
    }

    public void setAddress(String address) {
        _address = address;
    }

    public void setDepartment(String department) {
        _department = department;
    }

    public void setDescription(String description) {
        _description = description;
    }

    public void setEmailAddress(String mail) {
        _emailAddress = mail;
    }

    public void setEmailNotification(Boolean emailNotification) {
        _emailNotification = emailNotification;
    }

    public void setEnabled(Boolean enabled) {
        _enabled = enabled;
    }

    public void setFax(String fax) {
        _fax = fax;
    }

    public void setFirstName(String firstName) {
        _firstName = firstName;
    }

    public void setLastName(String lastName) {
        _lastName = lastName;
    }

    public void setPassword(String password) {
        _password = password;
    }

    public void setPhone1(String phone1) {
        _phone1 = phone1;
    }

    public void setPhone2(String phone2) {
        _phone2 = phone2;
    }

    public void setShowDescription(Boolean showDescription) {
        _showDescription = showDescription;
    }

    public void setSsoUser(Boolean ssoUser) {
        _ssoUser = ssoUser;
    }

    public void setTimeZone(String timeZone) {
        _timeZone = timeZone;
    }

    public void setTptOauthOrganization(String tptOauthOrganization) {
        _tptOauthOrganization = tptOauthOrganization;
    }

    public void setUserGroup(String userGroup) {
        _userGroup = userGroup;
    }

    public void setUserName(String userName) {
        _userName = userName;
    }

    public void setUserType(String userType) {
        _userType = userType;
    }
}
