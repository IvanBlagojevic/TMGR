package org.gs4tr.termmanager.service.file.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.modules.entities.model.RepositoryItem;
import org.gs4tr.foundation.modules.entities.model.ResourceInfo;
import org.gs4tr.termmanager.service.mocking.model.TestCase;
import org.gs4tr.termmanager.service.mocking.model.TestSuite;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

@TestSuite("filemanager")
public class ImportFileManagerTest extends AbstractSpringFileManagerTest {

    @Rule
    public final ExpectedException TROWN = ExpectedException.none();

    @Autowired
    private FileManager _manager;

    private List<String> _pathnames;

    @After
    public void manualCleanup() throws IOException {
	getFileManager().cleanup();
    }

    @Before
    public void resetPathnames() {
	_pathnames = new ArrayList<>();
    }

    @Test
    @TestCase("storeDeleteRead")
    public void storeDeleteReadTest() throws URISyntaxException, IOException {
	RepositoryItem repositoryItem = getRepositoryItem();

	_pathnames = getFileManager().store(repositoryItem);

	getFileManager().delete(_pathnames);

	String originalFilename = repositoryItem.getResourceInfo().getName();

	TROWN.expect(NoSuchFileException.class);
	TROWN.expectMessage(originalFilename);

	getFileManager().read(_pathnames);
    }

    @Test
    @TestCase("storeReadDeleteZip")
    public void storeReadDeleteZipTest() throws IOException, URISyntaxException {
	RepositoryItem repositoryItem = getRepositoryItem();

	_pathnames = getFileManager().store(repositoryItem);

	assertEquals(5, _pathnames.size());

	List<File> files = getFileManager().read(_pathnames);
	for (final File file : files) {
	    assertTrue(file.exists());
	    assertTrue(file.isFile());
	    assertTrue(file.canRead());
	}

	getFileManager().delete(_pathnames);

	// To avoid NoSuchFileException in @After
	_pathnames.clear();
    }

    @Test
    @TestCase("storeRead")
    public void storeReadTest() throws URISyntaxException, IOException {
	RepositoryItem repositoryItem = getRepositoryItem();

	_pathnames = getFileManager().store(repositoryItem);

	List<File> files = getFileManager().read(_pathnames);
	assertTrue(CollectionUtils.isNotEmpty(files));
	assertEquals(1, files.size());

	File file = files.get(0);
	assertTrue(file.exists());
	assertTrue(file.isFile());
	assertTrue(file.canRead());

	String originalFilename = repositoryItem.getResourceInfo().getName();
	assertTrue(file.getName().contains(originalFilename));
    }

    @Test
    @TestCase("store")
    public void storeTest() throws URISyntaxException, IOException {
	RepositoryItem repositoryItem = getRepositoryItem();
	ResourceInfo resourceInfo = repositoryItem.getResourceInfo();

	_pathnames = getFileManager().store(repositoryItem);

	assertTrue(_pathnames.get(0).contains(resourceInfo.getName()));
	assertEquals(1, _pathnames.size());
    }

    @Test
    @TestCase("storeDeleteRead")
    public void testStoreDeleteRead_subFolderCase() throws IOException, URISyntaxException {
	RepositoryItem repositoryItem = getRepositoryItem();

	String subFolder = UUID.randomUUID().toString();
	repositoryItem.getResourceInfo().setPath(subFolder);

	FileManager fileManger = getFileManager();

	_pathnames = fileManger.store(repositoryItem);

	fileManger.delete(_pathnames, subFolder);

	String originalFilename = repositoryItem.getResourceInfo().getName();

	TROWN.expect(NoSuchFileException.class);
	TROWN.expectMessage(originalFilename);

	fileManger.read(_pathnames, subFolder);
    }

    @Test
    @TestCase("storeRead")
    public void testStoreRead_subFolderCase() throws IOException, URISyntaxException {
	RepositoryItem repositoryItem = getRepositoryItem();

	String subFolder = UUID.randomUUID().toString();
	repositoryItem.getResourceInfo().setPath(subFolder);

	FileManager fileManger = getFileManager();

	_pathnames = fileManger.store(repositoryItem);

	List<File> files = fileManger.read(_pathnames, subFolder);
	assertTrue(CollectionUtils.isNotEmpty(files));
	assertEquals(1, files.size());

	File file = files.get(0);
	assertTrue(file.exists());
	assertTrue(file.isFile());
	assertTrue(file.canRead());

	String originalFilename = repositoryItem.getResourceInfo().getName();
	assertTrue(file.getName().contains(originalFilename));
    }

    @Test
    @TestCase("storeReadDeleteZip")
    public void testStoreReadDeleteZip_subFolderCase() throws IOException, URISyntaxException {
	RepositoryItem repositoryItem = getRepositoryItem();

	String subFolder = UUID.randomUUID().toString();
	repositoryItem.getResourceInfo().setPath(subFolder);

	FileManager fileManger = getFileManager();

	_pathnames = fileManger.store(repositoryItem);

	assertEquals(5, _pathnames.size());

	List<File> files = fileManger.read(_pathnames, subFolder);
	for (final File file : files) {
	    assertTrue(file.exists());
	    assertTrue(file.isFile());
	    assertTrue(file.canRead());
	}

	fileManger.delete(_pathnames, subFolder);

	TROWN.expect(NoSuchFileException.class);

	fileManger.read(_pathnames, subFolder);
    }

    @Test
    @TestCase("storeRead")
    public void testStoreReadEmptyFolder() throws IOException, URISyntaxException {
	RepositoryItem repositoryItem = getRepositoryItem();

	String subFolder = UUID.randomUUID().toString();
	repositoryItem.getResourceInfo().setPath(subFolder);

	FileManager fileManger = getFileManager();

	_pathnames = fileManger.store(repositoryItem);

	List<File> files = fileManger.read(subFolder);
	assertTrue(CollectionUtils.isNotEmpty(files));

	File file = files.get(0);

	assertTrue(file.exists());
	assertTrue(file.isFile());
	assertTrue(file.canRead());

	String fileName = file.getName();

	String originalFilename = repositoryItem.getResourceInfo().getName();
	assertTrue(fileName.contains(originalFilename));

	fileManger.delete(_pathnames, subFolder);

	files = fileManger.read(subFolder);
	assertTrue(CollectionUtils.isEmpty(files));
    }

    @Test
    @TestCase("storeRead")
    public void testStoreReadFolder() throws IOException, URISyntaxException {
	RepositoryItem repositoryItem = getRepositoryItem();

	String subFolder = UUID.randomUUID().toString();
	repositoryItem.getResourceInfo().setPath(subFolder);

	FileManager fileManger = getFileManager();

	_pathnames = fileManger.store(repositoryItem);

	List<File> files = fileManger.read(subFolder);
	assertTrue(CollectionUtils.isNotEmpty(files));
	assertEquals(1, files.size());

	File file = files.get(0);

	assertTrue(file.exists());
	assertTrue(file.isFile());
	assertTrue(file.canRead());

	String fileName = file.getName();

	String originalFilename = repositoryItem.getResourceInfo().getName();
	assertTrue(fileName.contains(originalFilename));
    }

    private FileManager getFileManager() {
	return _manager;
    }

    private RepositoryItem getRepositoryItem() throws IOException, URISyntaxException {
	RepositoryItem repositoryItem = getModelObject("repositoryItem", RepositoryItem.class);
	String pathname = repositoryItem.getResourceInfo().getName();
	URL resource = getResourceFrom(pathname);
	repositoryItem.setInputStream(Files.newInputStream(Paths.get(resource.toURI())));

	return repositoryItem;
    }
}
