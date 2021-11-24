package org.gs4tr.termmanager.service.context;

import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

import javax.naming.InitialContext;
import javax.servlet.ServletContextEvent;
import javax.sql.DataSource;

import org.gs4tr.foundation.modules.entities.model.UserProfileContext;
import org.gs4tr.termmanager.service.concurrency.ServiceThreadPoolHandler;
import org.springframework.security.core.context.SecurityContextHolder;

import com.mchange.v2.c3p0.PooledDataSource;
import com.mysql.jdbc.AbandonedConnectionCleanupThread;

public class ContextLoaderListener extends org.springframework.web.context.ContextLoaderListener {

    private static final String JACK_JNDI = "java:comp/env/jdbc/jackrabbit"; //$NON-NLS-1$

    private static final String TMGR_JNDI = "java:comp/env/jdbc/termmanager"; //$NON-NLS-1$

    @Override
    public void contextDestroyed(ServletContextEvent event) {

	super.contextDestroyed(event);

	try {
	    SecurityContextHolder.clearContext();

	    InitialContext initialContext = new InitialContext();
	    DataSource tmgrDataSource = (DataSource) initialContext.lookup(TMGR_JNDI);

	    if (tmgrDataSource instanceof PooledDataSource) {
		((PooledDataSource) tmgrDataSource).close();
	    }

	    DataSource jackDataSource = (DataSource) initialContext.lookup(JACK_JNDI);

	    if (jackDataSource instanceof PooledDataSource) {
		((PooledDataSource) jackDataSource).close();
	    }

	    AbandonedConnectionCleanupThread.shutdown();

	    Enumeration<Driver> drivers = DriverManager.getDrivers();
	    while (drivers.hasMoreElements()) {
		Driver driver = drivers.nextElement();
		DriverManager.deregisterDriver(driver);
	    }

	    ServiceThreadPoolHandler.shutDown();

	    UserProfileContext.clearContext();
	    SecurityContextHolder.clearContext();

	} catch (Exception e) {
	    throw new RuntimeException(e.getMessage(), e);
	}

    }
}
