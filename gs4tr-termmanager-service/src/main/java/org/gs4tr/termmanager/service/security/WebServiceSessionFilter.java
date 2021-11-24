package org.gs4tr.termmanager.service.security;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.filter.GenericFilterBean;

public class WebServiceSessionFilter extends GenericFilterBean {

    private static final Pattern REGEX = Pattern.compile("rest|glossary");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
	    throws IOException, ServletException {
	HttpServletRequest servletRequest = (HttpServletRequest) request;

	chain.doFilter(request, response);

	String path = servletRequest.getServletPath();
	Matcher matcher = REGEX.matcher(path);
	if (matcher.find()) {
	    HttpSession session = servletRequest.getSession(false);
	    session.invalidate();
	}
    }
}
