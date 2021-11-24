package org.gs4tr.termmanager.io.config;

import org.apache.http.impl.client.CloseableHttpClient;
import org.gs4tr.foundation3.httpclient.impl.HttpClientFactory;
import org.gs4tr.foundation3.httpclient.model.HttpClientConfiguration;
import org.gs4tr.foundation3.solr.impl.CloudHttpSolrClient;
import org.gs4tr.foundation3.solr.impl.SolrClientFactory;
import org.gs4tr.foundation3.solr.model.CloudHttpConfiguration;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SolrClientHandler implements InitializingBean, DisposableBean {

    private static final int TIMEOUT = 1800000; // 30 minutes

    private static final int ZK_TIMEOUT = 60000;

    private CloseableHttpClient _httpClient;

    @Autowired
    private IndexConfiguration _indexConfiguration;

    @Override
    public void afterPropertiesSet() throws Exception {
	HttpClientConfiguration config = new HttpClientConfiguration();
	config.setConnectionTimeout(TIMEOUT);
	config.setSoTimeout(TIMEOUT);

	_httpClient = HttpClientFactory.getInstance().createHttpClient(config);
    }

    @Bean(name = "indexClient")
    public CloudHttpSolrClient createSolrClient() {
	String zooKeeperHost = getIndexConfiguration().getZooKeeperHost();

	CloudHttpConfiguration config = new CloudHttpConfiguration();
	config.setZkHosts(zooKeeperHost);
	config.setHttpClient(getHttpClient());
	config.setZkTimeout(ZK_TIMEOUT);

	SolrClientFactory factory = new SolrClientFactory();

	return (CloudHttpSolrClient) factory.createCloudHttp(config);
    }

    @Override
    public void destroy() throws Exception {
	CloseableHttpClient httpClient = getHttpClient();
	if (httpClient != null) {
	    httpClient.close();
	}
    }

    private CloseableHttpClient getHttpClient() {
	return _httpClient;
    }

    private IndexConfiguration getIndexConfiguration() {
	return _indexConfiguration;
    }
}
