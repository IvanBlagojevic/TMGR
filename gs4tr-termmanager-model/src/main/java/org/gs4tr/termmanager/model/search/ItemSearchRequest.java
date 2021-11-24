package org.gs4tr.termmanager.model.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.gs4tr.termmanager.model.StatusSearchRequest;

public class ItemSearchRequest extends StatusSearchRequest {

    private Set<String> _contextRoles;

    private ItemFolderEnum _folder;

    private boolean _showAutoSaved = false;

    private List<String> _statuses;

    private List<String> _statusRequestList = new ArrayList<String>();

    private boolean _submitterView = false;

    private Long _userId;

    public Set<String> getContextRoles() {
	return _contextRoles;
    }

    public ItemFolderEnum getFolder() {
	return _folder;
    }

    public List<String> getStatuses() {
	return _statuses;
    }

    public List<String> getStatusRequestList() {
	return _statusRequestList;
    }

    public Long getUserId() {
	return _userId;
    }

    public boolean isShowAutoSaved() {
	return _showAutoSaved;
    }

    public boolean isSubmitterView() {
	return _submitterView;
    }

    public void setContextRoles(Set<String> contextRoles) {
	_contextRoles = contextRoles;
    }

    public void setFolder(ItemFolderEnum folder) {
	_folder = folder;
    }

    public void setShowAutoSaved(boolean showAutoSaved) {
	_showAutoSaved = showAutoSaved;
    }

    public void setStatuses(List<String> statuses) {
	_statuses = statuses;
    }

    public void setStatusRequestList(List<String> statusRequestList) {
	_statusRequestList = statusRequestList;
    }

    public void setSubmitterView(boolean submitterView) {
	_submitterView = submitterView;
    }

    public void setUserId(Long userId) {
	_userId = userId;
    }
}