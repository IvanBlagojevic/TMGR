package org.gs4tr.termmanager.service.health;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.modules.entities.model.LogHelper;
import org.springframework.beans.factory.annotation.Value;

public class CpuHealthValidator implements HealthValidator {

    private static final Log LOGGER = LogFactory.getLog(CpuHealthValidator.class);

    private static final String OBJECT_NAME_OPERATING_SYSTEM = "java.lang:type=OperatingSystem";

    private static final String PROCESS_CPU_LOAD_ATTRIBUTE = "ProcessCpuLoad";

    private Integer _cpuUsageTrashHold;

    public CpuHealthValidator(@Value("${cpu.usage.threshold:80}") Integer cpuUsageTrashHold) {
	_cpuUsageTrashHold = cpuUsageTrashHold;
    }

    @Override
    public boolean isHealthy(MBeanServer mbs) {

	if (_cpuUsageTrashHold <= 0) {
	    return true;
	}

	try {
	    ObjectName name = ObjectName.getInstance(OBJECT_NAME_OPERATING_SYSTEM);
	    Double cpuUsageValue = (Double) mbs.getAttribute(name, PROCESS_CPU_LOAD_ATTRIBUTE);

	    if (cpuUsageValue != null) {
		double cpuUsage = (int) (cpuUsageValue * 1000) / 10.0;

		LogHelper.debug(LOGGER, String.format("CPU usage: %s", cpuUsage));

		if (cpuUsage > _cpuUsageTrashHold) {
		    LogHelper.error(LOGGER,
			    String.format("CPU usage: %s is higher than threshold: %s", cpuUsage, _cpuUsageTrashHold));
		    return false;
		}
	    }

	} catch (JMException e) {
	    throw new RuntimeException(
		    String.format("Unable to create ObjectName for name: %s", OBJECT_NAME_OPERATING_SYSTEM), e);
	}

	return true;
    }
}
