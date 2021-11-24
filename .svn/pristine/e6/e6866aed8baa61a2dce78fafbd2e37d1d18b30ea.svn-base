package org.gs4tr.termmanager.service.file.manager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

/**
 * <p>
 * Package private supporting class for decompressing a standard zip file to a
 * destination directory.
 * </p>
 * 
 * @since 5.0
 */
@Component
class ZipFileDecompressor {
    /**
     * The default buffer size ({@value}) to use for extracting files from zip.
     */
    private static final int BUFFER_SIZE = 1024 * 4;

    /**
     * <p>
     * Indicates that the end of the <code>ZipEntry</code> is reached.
     * </p>
     */
    private static final int EOF = -1;

    private void exctractFile(ZipInputStream stream, String pathname) throws IOException {
	try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(pathname), BUFFER_SIZE)) {
	    byte[] data = new byte[BUFFER_SIZE];
	    int read = 0;
	    while ((read = stream.read(data, 0, BUFFER_SIZE)) != EOF) {
		out.write(data, 0, read);
	    }
	    // Do NOT close the ZipInputStream.
	}
    }

    List<File> decompress(File zip, String destination) throws IOException {
	List<File> unzippedFiles = new ArrayList<>();

	try (ZipFile zipFile = new ZipFile(zip)) {
	    Enumeration<? extends ZipEntry> entries = zipFile.entries();
	    while (entries.hasMoreElements()) {
		ZipEntry zipEntry = entries.nextElement();
		String zipEntryName = FilenameUtils.getName(zipEntry.getName());

		File unzipped = new File(destination, zipEntryName);

		try (FileOutputStream out = new FileOutputStream(unzipped)) {
		    InputStream in = zipFile.getInputStream(zipEntry);
		    IOUtils.copy(in, out);
		}
		unzippedFiles.add(unzipped);
	    }
	}
	return unzippedFiles;
    }

    List<String> decompress(InputStream zip, String destination) throws IOException {
	List<String> pathnames = new ArrayList<>();
	try (ZipInputStream stream = new ZipInputStream(new BufferedInputStream(zip))) {
	    ZipEntry zipEntry = null;
	    while (Objects.nonNull(zipEntry = stream.getNextEntry())) {
		String zipEntryName = FilenameUtils.getName(zipEntry.getName());
		String pathname = ImportFileNameMaker.makeUniqueFrom(zipEntryName);
		exctractFile(stream, Paths.get(destination, pathname).toString());
		pathnames.add(pathname);
	    }
	}
	return pathnames;
    }
}
