package org.gs4tr.termmanager.service.healt;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;

import org.gs4tr.foundation.modules.spring.TestEnvironmentAwareContextLoader;
import org.gs4tr.termmanager.service.health.HealthValidator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-health.xml" }, loader = TestEnvironmentAwareContextLoader.class)
public class CpuHealthValidatorTest {

    @Autowired
    @Qualifier("cpuHealthValidator")
    private HealthValidator _healthValidator;

    @Test
    public void testIsHealthy() {
	MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

	boolean healthy = getHealthValidator().isHealthy(mBeanServer);
	Assert.assertTrue(healthy);
    }

    private HealthValidator getHealthValidator() {
	return _healthValidator;
    }
}
