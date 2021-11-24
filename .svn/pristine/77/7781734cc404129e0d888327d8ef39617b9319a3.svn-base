package org.gs4tr.termmanager.glossaryV2;

import javax.servlet.ServletContext;

import org.gs4tr.tm3.httpconnector.servlet.ITmgrBlacklistOperationsFactory;
import org.gs4tr.tm3.httpconnector.servlet.ITmgrGlossaryOperationsFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

@Component("glossaryV2ServletManager")
public class GlossaryV2ServletManager implements InitializingBean, ServletContextAware {

    public static final String V2_BLACKLIST_SERVICE_CONTEXT_PATH = "/v2/blacklist"; //$NON-NLS-1$

    public static final String V2_GLOSSSARY_SERVICE_CONTEXT_PATH = "/v2/glossary"; //$NON-NLS-1$

    @Autowired
    private ITmgrBlacklistOperationsFactory _blacklistOperationsFactory;

    @Autowired
    private ITmgrGlossaryOperationsFactory _glossaryOperationsFactory;

    private ServletContext _servletContext;

    @Override
    public void afterPropertiesSet() throws Exception {
	ServletContext servletContext = getServletContext();

	if (servletContext != null) {
	    servletContext.setAttribute(V2_GLOSSSARY_SERVICE_CONTEXT_PATH, getGlossaryOperationsFactory());
	    servletContext.setAttribute(V2_BLACKLIST_SERVICE_CONTEXT_PATH, getBlacklistOperationsFactory());
	}
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
	_servletContext = servletContext;
    }

    private ITmgrBlacklistOperationsFactory getBlacklistOperationsFactory() {
	return _blacklistOperationsFactory;
    }

    private ITmgrGlossaryOperationsFactory getGlossaryOperationsFactory() {
	return _glossaryOperationsFactory;
    }

    private ServletContext getServletContext() {
	return _servletContext;
    }
}
