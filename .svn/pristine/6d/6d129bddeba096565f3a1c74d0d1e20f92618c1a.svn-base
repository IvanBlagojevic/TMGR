package org.gs4tr.termmanager.webmvc.controllers;

import java.lang.management.ManagementFactory;
import java.util.List;

import javax.management.MBeanServer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.modules.entities.model.LogHelper;
import org.gs4tr.foundation.modules.webmvc.rest.AbstractRestController;
import org.gs4tr.termmanager.service.health.HealthValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController("healthCheckController")
public class HealthCheckController extends AbstractRestController {

    private static final Log LOGGER = LogFactory.getLog(HealthCheckController.class);

    private static final String STATUS_OK = "OK";

    private static final String STATUS_UNHEALTHY = "UNHEALTHY";

    private List<HealthValidator> _healthValidators;

    public HealthCheckController(@Autowired List<HealthValidator> healthValidators) {
	_healthValidators = healthValidators;
    }

    @RequestMapping(value = "/health.ter", method = RequestMethod.GET, produces = { "application/json" })
    public String checkHealth() {
	MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

	for (HealthValidator validator : _healthValidators) {
	    boolean healthy = validator.isHealthy(mbs);

	    if (!healthy) {
		LogHelper.error(LOGGER, "Instance health status is %s", STATUS_OK);
		return STATUS_UNHEALTHY;
	    }
	}

	return STATUS_OK;
    }
}
