package org.gs4tr.termmanager.dao.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.net.URI;
import java.net.URL;

import javax.sql.DataSource;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.gs4tr.foundation.modules.dao.test.DbUnitHelper;
import org.gs4tr.foundation.modules.security.dao.RoleDAO;
import org.gs4tr.foundation.modules.security.dao.RoleSearchDAO;
import org.gs4tr.foundation.modules.spring.TestEnvironmentAwareContextLoader;
import org.gs4tr.termmanager.dao.OrganizationSearchDAO;
import org.gs4tr.termmanager.dao.PowerUserProjectRoleDAO;
import org.gs4tr.termmanager.dao.ProjectDAO;
import org.gs4tr.termmanager.dao.ProjectSearchDAO;
import org.gs4tr.termmanager.dao.UserProfileDAO;
import org.gs4tr.termmanager.dao.UserProfileSearchDAO;
import org.gs4tr.termmanager.dao.UserProjectRoleSearchDAO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:org/gs4tr/termmanager/dao/spring/applicationContext-dao.xml",
	"classpath:org/gs4tr/termmanager/dao/spring/applicationContext-hibernate.xml",
	"classpath:org/gs4tr/termmanager/dao/spring/applicationContext-init.xml" }, loader = TestEnvironmentAwareContextLoader.class)
@TestExecutionListeners({ TransactionalTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
	DirtiesContextTestExecutionListener.class })
@Transactional
public abstract class AbstractSpringDAOIntegrationTest {

    @BeforeClass
    public static void setUpClass() throws Exception {
	// SimpleNamingContextBuilder builder =
	// SimpleNamingContextBuilder.emptyActivatedContextBuilder();
	// SingleConnectionDataSource ds = new SingleConnectionDataSource();
	// ds.setDriverClassName("org.h2.Driver");
	// ds.setUrl("jdbc:h2:mem:test;create=true");
	// ds.setSuppressClose(true);
	// builder.bind("java:comp/env/jdbc/termmanager", ds);

	SimpleNamingContextBuilder builder = SimpleNamingContextBuilder.emptyActivatedContextBuilder();
	SingleConnectionDataSource ds = new SingleConnectionDataSource();
	ds.setDriverClassName("net.sf.log4jdbc.DriverSpy");
	ds.setUrl("jdbc:log4jdbc:h2:mem:test;create=true;MODE=MySQL;DB_CLOSE_ON_EXIT=FALSE");
	ds.setSuppressClose(true);
	builder.bind("java:comp/env/jdbc/termmanager", ds);
    }

    @Autowired
    private DataSource _dataSource;

    private DbUnitHelper _dbUnitHelper;

    @Autowired
    private LocalSessionFactoryBean _localSessionFactoryBean;

    @Autowired
    private PowerUserProjectRoleDAO _powerUserProjectRoleDAO;

    @Autowired
    private ProjectDAO _projectDAO;

    @Autowired
    private RoleDAO _roleDAO;

    @Autowired
    private RoleSearchDAO _roleSearchDAO;

    @Autowired
    private UserProfileDAO _userProfileDAO;

    @Autowired
    private UserProjectRoleSearchDAO _userProjectRoleSearchDAO;

    @Autowired
    private UserProfileSearchDAO _userSearchDAO;

    @Autowired
    private OrganizationSearchDAO organizationSearchDAO;

    @Autowired
    private ProjectSearchDAO projectSearchDAO;

    public DataSource getDataSource() {
	return _dataSource;
    }

    public OrganizationSearchDAO getOrganizationSearchDAO() {
	return organizationSearchDAO;
    }

    public PowerUserProjectRoleDAO getPowerUserProjectRoleDAO() {
	return _powerUserProjectRoleDAO;
    }

    public ProjectSearchDAO getProjectSearchDAO() {
	return projectSearchDAO;
    }

    public RoleDAO getRoleDAO() {
	return _roleDAO;
    }

    public RoleSearchDAO getRoleSearchDAO() {
	return _roleSearchDAO;
    }

    public UserProfileDAO getUserProfileDAO() {
	return _userProfileDAO;
    }

    public UserProjectRoleSearchDAO getUserProjectRoleSearchDAO() {
	return _userProjectRoleSearchDAO;
    }

    public UserProfileSearchDAO getUserSearchDAO() {
	return _userSearchDAO;
    }

    @Before
    public void setUp() throws Exception {

	_dbUnitHelper = new DbUnitHelper(getDataSource());

	FlatXmlDataSetBuilder builderClean = new FlatXmlDataSetBuilder();
	builderClean.setColumnSensing(true);
	IDataSet cleanDataSet = builderClean
		.build(getClass().getClassLoader().getResourceAsStream("data-sets/cleanDataSet.xml"));

	FlatXmlDataSetBuilder builderData = new FlatXmlDataSetBuilder();
	builderData.setColumnSensing(true);
	IDataSet dataSet = builderData
		.build(getClass().getClassLoader().getResourceAsStream("data-sets/baseDataSet.xml"));

	_dbUnitHelper.cleanDatabase(cleanDataSet);
	_dbUnitHelper.setupFromCleanDatabase(dataSet);
    }

    @After
    public void tearDown() throws Exception {
	Session session = getCurrentSession();

	assertNotNull(session);

	session.clear();
    }

    protected void assertDatabaseTables(String tableName, String expectedDataSetName) throws Exception {
	URL url = getClass().getClassLoader().getResource(TestConstants.DATA_SETS_FOLDER_NAME
		+ TestConstants.EXPECTED_DATA_SETS_FOLDER_NAME + expectedDataSetName + TestConstants.XML_EXTENSION);

	URI uri = new URI(url.toString());

	getDbUnitHelper().assertDatabaseTables(tableName, new File(uri.getPath()));
    }

    protected void assertRowCountInDatabaseTable(String tableName, int rowCount) throws Exception {
	assertEquals(getDbUnitHelper().getRowCountInDatabaseTable(tableName), rowCount);
    }

    protected void flushSession() {
	Session session = getCurrentSession();

	assertNotNull(session);

	session.flush();
    }

    protected Session getCurrentSession() {
	SessionFactory sessionFactory = getLocalSessionFactoryBean().getObject();

	return sessionFactory.getCurrentSession();
    }

    protected DbUnitHelper getDbUnitHelper() {
	return _dbUnitHelper;
    }

    protected LocalSessionFactoryBean getLocalSessionFactoryBean() {
	return _localSessionFactoryBean;
    }

    protected ProjectDAO getProjectDAO() {
	return _projectDAO;
    }

}