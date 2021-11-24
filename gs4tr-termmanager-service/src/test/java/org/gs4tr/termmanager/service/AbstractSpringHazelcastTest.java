package org.gs4tr.termmanager.service;

import org.gs4tr.foundation.modules.spring.TestEnvironmentAwareContextLoader;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-exclusiveWriteLockManager.xml",
	"classpath:org/gs4tr/termmanager/cache/spring/applicationContext-hazelcast.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-init-base.xml" }, loader = TestEnvironmentAwareContextLoader.class)
public abstract class AbstractSpringHazelcastTest {

}
