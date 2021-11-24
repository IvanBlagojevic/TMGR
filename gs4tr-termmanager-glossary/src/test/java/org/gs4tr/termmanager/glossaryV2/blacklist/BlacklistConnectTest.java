package org.gs4tr.termmanager.glossaryV2.blacklist;

import org.gs4tr.foundation.modules.entities.model.test.annotations.TestCase;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestSuite;
import org.gs4tr.termmanager.glossaryV2.AbstractV2GlossaryServiceTest;
import org.gs4tr.tm3.api.ServerInfo;
import org.gs4tr.tm3.httpconnector.resolver.model.OperationsException;
import org.gs4tr.tm3.httpconnector.resolver.model.ResolverContext;
import org.gs4tr.tm3.httpconnector.resolver.model.TmgrKey;
import org.gs4tr.tm3.httpconnector.servlet.ITmgrBlacklistOperations;
import org.gs4tr.tm3.httpconnector.tmgr.TmgrBlacklistHttpConnectionInfo;
import org.junit.Assert;
import org.junit.Test;

@TestSuite("glossary")
public class BlacklistConnectTest extends AbstractV2GlossaryServiceTest {

    @Test
    @TestCase("connect")
    public void testConnect() throws Exception {
	TmgrKey key = createKey();

	ITmgrBlacklistOperations operations = getBlacklistFactory().getOperations(key);

	TmgrBlacklistHttpConnectionInfo connectionInfo = new TmgrBlacklistHttpConnectionInfo();
	connectionInfo.setLoginUser("user");
	connectionInfo.setPassword("pass");

	ServerInfo info = operations.connect(connectionInfo);

	Assert.assertNotNull(info);
	Assert.assertEquals(key.getSource(), info.getSourceLocale().getCode());
	Assert.assertEquals(key.getTarget(), info.getTargetLocale().getCode());
	Assert.assertNotNull(info.getVersion());

	ResolverContext resolverContext = operations.getResolverContext();
	Assert.assertNotNull(resolverContext);
	Assert.assertNotNull(resolverContext.getReadOnly());
    }

    @Test(expected = OperationsException.class)
    @TestCase("connect")
    public void testConnectWithWrongLanguageDirections() throws OperationsException {
	TmgrKey key = createKey();
	key.setSource("ja-JP");
	key.setTarget("zh-CN");

	getBlacklistFactory().getOperations(key);
    }
}
