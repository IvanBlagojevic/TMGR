package org.gs4tr.termmanager.service.health;

import javax.management.MBeanServer;

public interface HealthValidator {

    boolean isHealthy(MBeanServer mbs);
}
