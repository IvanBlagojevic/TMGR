package org.gs4tr.termmanager.service.solr;

import java.io.File;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

public class SolrServiceConfiguration extends PropertiesConfiguration {

    private static final String REGULAR_COLLECTIONV2_KEY = "solr.regularV2.terms.path";

    private static final String SUBMISSION_COLLECTIONV2_KEY = "solr.submissionV2.terms.path";

    private static final String ZK_HOST_KEY = "solr.zkhosts";

    public SolrServiceConfiguration(File file) throws ConfigurationException {
	super(file);
    }

    public String getRegularCollection() {
	return System.getProperty("solr.regularV2.terms.path", getString(REGULAR_COLLECTIONV2_KEY, "regularV2"));
    }

    public String getSubmissionCollection() {
	return System.getProperty("solr.submissionV2.terms.path",
		getString(SUBMISSION_COLLECTIONV2_KEY, "submissionV2"));
    }

    public String getZooKeeperHost() {
	return System.getProperty("solr.zkhosts", getString(ZK_HOST_KEY, "localhost:9983"));
    }
}
