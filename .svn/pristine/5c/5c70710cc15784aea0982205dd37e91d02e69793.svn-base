package org.gs4tr.termmanager.tests.controllers.rest.main;

import java.io.IOException;

public class RestTestRunner {

    public static void main(String[] args) throws IOException {

	String userId = null;

	try {

	    userId = RestTestHelper.loginGET();
	    System.out.println(String.format("Login GET response: %s", userId));

	    // String exportResult = RestTestHelper.exportTest(userId);
	    // System.out.println(String.format("Export GET response: %s",
	    // exportResult));

	    // String importResult = RestTestHelper.importTest(userId);
	    // System.out.println(String.format("Import POST response: %s", importResult));

	    // long afterDate = 0L;
	    // String exportResult = RestTestHelper.exportDetailedDocumentTest(userId,
	    // afterDate);
	    // System.out.println(String.format("Detailed Export GET response: %s",
	    // exportResult));
	    // System.out.println(String.format("Sync time: %d",
	    // System.currentTimeMillis()));

	    String addTermResult = RestTestHelper.addTerm(userId);
	    System.out.println(String.format("Add term POST response: %s", addTermResult));

	} finally {
	    if (userId != null) {
		RestTestHelper.logoutGET(userId);
	    }
	}
    }
}
