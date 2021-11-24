package org.gs4tr.termmanager.service.security;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation.modules.entities.model.StringConstants;
import org.springframework.web.filter.GenericFilterBean;

import com.google.common.base.Splitter;

public class RestResponseModifierFilter extends GenericFilterBean {

    private static final String URL_PATH = "/rest";

    private Map<String, String> _additionalHeaders;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
	    throws IOException, ServletException {

	HttpServletRequest httpServletRequest = (HttpServletRequest) request;

	if (httpServletRequest.getServletPath().contains(URL_PATH)) {

	    HttpServletResponse servletResponse = (HttpServletResponse) response;
	    Map<String, String> additionalHeaders = getAdditionalHeaders();

	    if (additionalHeaders != null) {
		for (Entry<String, String> entry : additionalHeaders.entrySet()) {
		    String key = entry.getKey();
		    String value = entry.getValue();
		    if (StringUtils.isEmpty(key) || StringUtils.isEmpty(value)) {
			continue;
		    }

		    setHeaders(servletResponse, key, value);
		}
	    }
	}

	chain.doFilter(request, response);
    }

    public Map<String, String> getAdditionalHeaders() {
	return _additionalHeaders;
    }

    public void setAdditionalHeaders(Map<String, String> additionalHeaders) {
	_additionalHeaders = additionalHeaders;
    }

    private void setHeaders(HttpServletResponse servletResponse, String key, String value) {
	String comma = StringConstants.COMMA;
	if (value.contains(comma)) {
	    Iterator<String> iterator = Splitter.on(comma).split(value).iterator();
	    while (iterator.hasNext()) {
		servletResponse.addHeader(key, iterator.next());
	    }
	} else {
	    servletResponse.setHeader(key, value);
	}
    }
}
