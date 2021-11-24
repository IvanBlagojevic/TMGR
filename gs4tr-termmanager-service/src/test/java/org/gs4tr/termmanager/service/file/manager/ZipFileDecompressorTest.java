package org.gs4tr.termmanager.service.file.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.ProtectionDomain;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ZipFileDecompressorTest extends AbstractSpringFileManagerTest {

    private static final String MAC_ZIP_FILE_NAME = "mac.zip";

    private static final String ZIP_FILE_NAME = "General Mills_GEN000893.zip";

    @Autowired
    private ZipFileDecompressor _zipFileDecompressor;

    @Test
    @Ignore(value = "TERII-4651 | Postponed by the client")
    public void decompressMacZipInputStreamTest() throws Exception {
	InputStream macZip = Files.newInputStream(Paths.get(getResourceFrom(MAC_ZIP_FILE_NAME).toURI()));

	String destination = getTargetLocation().getPath();

	List<String> pathnames = getZipFileDecompressor().decompress(macZip, destination);

	assertEquals(2, pathnames.size());
    }

    @Test
    public void decompressZipFileTest() throws URISyntaxException, IOException {
	File zip = new File(getResourceFrom(ZIP_FILE_NAME).toURI());

	String destination = getTargetLocation().getPath();

	List<File> files = getZipFileDecompressor().decompress(zip, destination);

	assertEquals(5, files.size());

	for (final File file : files) {
	    assertTrue(file.exists());
	    assertTrue(file.isFile());
	    assertTrue(file.canRead());
	}
    }

    @Test
    public void decompressZipInputStreamTest() throws URISyntaxException, IOException {
	InputStream zip = Files.newInputStream(Paths.get(getResourceFrom(ZIP_FILE_NAME).toURI()));

	String destination = getTargetLocation().getPath();

	List<String> pathnames = getZipFileDecompressor().decompress(zip, destination);

	assertEquals(5, pathnames.size());
    }

    private URL getTargetLocation() {
	Class<ZipFileDecompressorTest> clazz = ZipFileDecompressorTest.class;
	ProtectionDomain protectionDomain = clazz.getProtectionDomain();
	return protectionDomain.getCodeSource().getLocation();
    }

    private ZipFileDecompressor getZipFileDecompressor() {
	return _zipFileDecompressor;
    }
}
