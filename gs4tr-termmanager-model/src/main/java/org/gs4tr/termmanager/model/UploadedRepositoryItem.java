package org.gs4tr.termmanager.model;

import org.gs4tr.foundation.modules.entities.model.RepositoryItem;

public class UploadedRepositoryItem {

    private String _nameParameter;

    private RepositoryItem _repositoryItem;

    public UploadedRepositoryItem(RepositoryItem repositoryItem, String nameParameter) {
	_nameParameter = nameParameter;
	_repositoryItem = repositoryItem;
    }

    public String getNameParameter() {
	return _nameParameter;
    }

    public RepositoryItem getRepositoryItem() {
	return _repositoryItem;
    }

    public void setNameParameter(String nameParameter) {
	_nameParameter = nameParameter;
    }

    public void setRepositoryItem(RepositoryItem repositoryItem) {
	_repositoryItem = repositoryItem;
    }

}