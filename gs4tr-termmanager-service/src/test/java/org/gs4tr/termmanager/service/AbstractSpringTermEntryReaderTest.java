package org.gs4tr.termmanager.service;

import org.gs4tr.foundation.modules.spring.TestEnvironmentAwareContextLoader;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:org/gs4tr/foundation/modules/webmvc/spring/applicationContext-i18n.xml",
	"classpath:org/gs4tr/termmanager/service/spring/applicationContext-applicationContextLocator.xml" }, loader = TestEnvironmentAwareContextLoader.class)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public abstract class AbstractSpringTermEntryReaderTest {

}
