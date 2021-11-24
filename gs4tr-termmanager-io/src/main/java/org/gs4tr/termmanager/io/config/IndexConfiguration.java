package org.gs4tr.termmanager.io.config;

import java.io.File;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.context.annotation.Configuration;

public class IndexConfiguration extends PropertiesConfiguration {

    private static final String REGULAR_COLLECTIONV2_KEY = "solr.regularV2.terms.path";

    private static final String SUBMISSION_COLLECTIONV2_KEY = "solr.submissionV2.terms.path";

    private static final String ZK_HOST_KEY = "solr.zkhosts";

    public IndexConfiguration(File file) throws ConfigurationException {
	super(file);
    }

    public String getRegular() {
	return System.getProperty("solr.regularV2.terms.path", getString(REGULAR_COLLECTIONV2_KEY, "regularV2"));
    }

    public String getSubmission() {
	return System.getProperty("solr.submissionV2.terms.path",
		getString(SUBMISSION_COLLECTIONV2_KEY, "submissionV2"));
    }

    public String getZooKeeperHost() {
	return System.getProperty("solr.zkhosts", getString(ZK_HOST_KEY, "localhost:9983"));
    }
}
