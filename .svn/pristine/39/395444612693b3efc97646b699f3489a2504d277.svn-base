package org.gs4tr.termmanager.cache.config;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.gs4tr.foundation.modules.usermanager.oauth.DefaultTptOAuthUserManagerClient;
import org.gs4tr.foundation.modules.usermanager.service.impl.AbstractUserProfileServiceImpl;
import org.gs4tr.termmanager.cache.model.CacheName;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.guava.GuavaCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.cache.CacheBuilder;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
	SimpleCacheManager simpleCacheManager = new SimpleCacheManager();

	GuavaCache restTptOauthTokenCache = new GuavaCache(DefaultTptOAuthUserManagerClient.REST_TPT_OAUTH_TOKEN_CACHE,
		CacheBuilder.newBuilder().maximumSize(1).expireAfterWrite(10, TimeUnit.MINUTES).build());

	GuavaCache analysisProgressCache = new GuavaCache(CacheName.ANALYSIS_PROGRESS_STATUS.getValue(),
		CacheBuilder.newBuilder().expireAfterAccess(60, TimeUnit.SECONDS).build());

	GuavaCache importSummaryCache = new GuavaCache(CacheName.IMPORT_PROGRESS_STATUS.getValue(),
		CacheBuilder.newBuilder().expireAfterAccess(300, TimeUnit.SECONDS).build());

	GuavaCache batchProcessingStatusCache = new GuavaCache(CacheName.BATCH_PROCESSING_STATUS.getValue(),
		CacheBuilder.newBuilder().expireAfterAccess(60, TimeUnit.SECONDS).build());

	GuavaCache userProfileCache = new GuavaCache(AbstractUserProfileServiceImpl.USER_PROFILE_CACHE,
		CacheBuilder.newBuilder().maximumSize(10000).expireAfterWrite(4, TimeUnit.HOURS).build());

	simpleCacheManager.setCaches(Arrays.asList(restTptOauthTokenCache, analysisProgressCache, importSummaryCache,
		batchProcessingStatusCache, userProfileCache));

	return simpleCacheManager;
    }
}
