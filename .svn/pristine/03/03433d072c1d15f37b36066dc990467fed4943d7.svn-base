package org.gs4tr.termmanager.persistence.solr.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.gs4tr.foundation3.solr.ICloudHttpSolrClient;
import org.gs4tr.foundation3.solr.util.RouteResolver;

public class RouteHelper {

    public static String getRoute(ICloudHttpSolrClient client, String collection, Object param) {
	RouteResolver instance = getRouteResolver(client, collection);
	return instance.resolve(param);
    }

    public static Set<String> getRoutes(ICloudHttpSolrClient client, String collection, Collection<?> params) {
	RouteResolver instance = getRouteResolver(client, collection);
	return instance.resolveCollection(params);
    }

    private static Map<String, RouteResolver> _routeResolvers = new ConcurrentHashMap<String, RouteResolver>();

    private static RouteResolver getRouteResolver(ICloudHttpSolrClient client, String collection) {
	Map<String, RouteResolver> routeResolvers = getRouteResolvers();
	RouteResolver routeResolver = routeResolvers.get(collection);
	if (routeResolver == null) {
	    List<String> routes = client.routes(collection);
	    routeResolver = RouteResolver.newInstance(routes);
	    routeResolvers.put(collection, routeResolver);
	}

	return routeResolver;
    }

    private static Map<String, RouteResolver> getRouteResolvers() {
	return _routeResolvers;
    }
}
