package org.gs4tr.termmanager.tests.webservices;

public class ConnectionDetails {

    private String _baseURL;

    private String _password;

    private String _projectShortcode;

    private String _username;

    public ConnectionDetails(String connectionString) {

	String[] strings = connectionString.split("\\?");

	_baseURL = strings[0].replace("tmgr", "http").concat("/rest");

	String query = strings[1];
	String[] params = query.split("&");
	for (String s : params) {
	    if (s.contains("prj")) {
		_projectShortcode = (s.substring(s.indexOf("=") + 1));
	    }
	    if (s.contains("usr")) {
		_username = (s.substring(s.indexOf("=") + 1));
	    }
	    if (s.contains("pwd")) {
		_password = (s.substring(s.indexOf("=") + 1));
	    }
	}

    }

    public String getBaseURL() {
	return _baseURL;
    }

    public String getPassword() {
	return _password;
    }

    public String getProjectShortcode() {
	return _projectShortcode;
    }

    public String getUsername() {
	return _username;
    }

    public void setUsername(String username) {
	_username = username;
    }
}
