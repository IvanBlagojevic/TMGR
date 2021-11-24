package org.gs4tr.termmanager.service.file.manager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.modules.entities.model.LogHelper;
import org.gs4tr.foundation.modules.entities.model.RepositoryItem;
import org.gs4tr.termmanager.model.ImportFileExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Concrete <tt>FileManager</tt> implementation used to store, read and delete
 * files uploaded on import. It also provides automatic cleanup mechanism in
 * order to free up space by removing unnecessary files.
 *
 * @since 5.0
 */
@Service("fileManager")
class ImportFileManager implements FileManager {
    /**
     * Formatter for printing and parsing {@code LocalDate}. This formats the
     * <code>LocalDate</code> to directory name using the {@code yyyy_MM_dd}
     * pattern.
     */
    private static final DateTimeFormatter DATE_DIR_FORMATTER = DateTimeFormatter.ofPattern("yyyy_MM_dd"); //$NON-NLS-1$

    private static final Log LOG = LogFactory.getLog(ImportFileManager.class);

    /**
     * The default temporary-file directory. On UNIX systems the default value of
     * this property is <code>"/tmp"</code> or <code>"/var/tmp"</code>; on Microsoft
     * Windows systems it is typically <code>"C:\\WINNT\\TEMP"</code>.
     * <p>
     * When you startup Tomcat, using startup.bat (Windows) or startup.sh, it calls
     * catalina.bat/catalina.sh respectively. Catalina then needs a temp directory
     * to be set. It does this by setting the CATALINA_TMPDIR variable to
     * TOMCAT_HOME/temp folder and assigns it to java system environment variable as
     * java.io.tmpdir.
     * </p>
     */
    private static final String TMP = System.getProperty("java.io.tmpdir"); //$NON-NLS-1$

    private final ZipFileDecompressor _zipFileDecompressor;

    @Autowired
    public ImportFileManager(ZipFileDecompressor zipFileDecompressor) {
	_zipFileDecompressor = zipFileDecompressor;
    }

    @Override
    @Scheduled(cron = "0 55 23 * * ?")
    public void cleanup() throws IOException {
	LocalDate today = LocalDate.now();
	LogHelper.debug(LOG, String.format(Messages.getString("ImportFileManager.2"), today)); //$NON-NLS-1$
	LocalDate yesterday = today.minus(1, ChronoUnit.DAYS);
	Path path = Paths.get(TMP, FilenameUtils.getName(DATE_DIR_FORMATTER.format(yesterday)));
	LogHelper.debug(LOG, String.format(Messages.getString("ImportFileManager.3"), path)); //$NON-NLS-1$

	FileUtils.deleteDirectory(path.toFile());
    }

    @Override
    public void delete(Collection<String> fileNames, String... folders) throws IOException {
	if (CollectionUtils.isNotEmpty(fileNames)) {
	    Path parent = getPath(folders);
	    for (String pathname : fileNames) {
		Path path = Paths.get(parent.toString(), FilenameUtils.getName(pathname));
		LogHelper.debug(LOG, String.format(Messages.getString("ImportFileManager.4"), pathname)); //$NON-NLS-1$
		Files.delete(path);
	    }
	}
    }

    @Override
    public List<File> read(Collection<String> fileNames, String... folders) throws IOException {
	if (CollectionUtils.isEmpty(fileNames)) {
	    return Collections.emptyList();
	}

	Path parent = getPath(folders);
	List<File> files = new ArrayList<>();
	for (String fileName : fileNames) {
	    Path path = Paths.get(parent.toString(), FilenameUtils.getName(fileName));
	    File file = path.toFile();
	    if (!file.exists()) {
		throw new NoSuchFileException(fileName);
	    }
	    files.add(file);
	}
	return files;
    }

    @Override
    public List<File> read(String folder) throws IOException {
	if (StringUtils.isEmpty(folder)) {
	    return Collections.emptyList();
	}

	Path path = getPath(folder);

	File file = path.toFile();
	if (!file.exists() || !file.isDirectory()) {
	    return Collections.emptyList();
	}

	return Files.list(path).map(Path::toFile).collect(Collectors.toList());
    }

    @Override
    public List<String> store(RepositoryItem repositoryItem) throws IOException {
	String filename = FilenameUtils.getName(repositoryItem.getResourceInfo().getName());
	String subFolder = repositoryItem.getResourceInfo().getPath();

	Path path = Files.createDirectories(getPath(subFolder));

	if (isZipFile(FilenameUtils.getExtension(filename))) {
	    LogHelper.debug(LOG, String.format(Messages.getString("ImportFileManager.5"), path)); //$NON-NLS-1$
	    return getZipFileDecompressor().decompress(repositoryItem.getInputStream(), path.toString());
	}
	File file = new File(path.toString(), ImportFileNameMaker.makeUniqueFrom(filename));
	try (InputStream in = repositoryItem.getInputStream(); OutputStream out = new FileOutputStream(file)) {
	    IOUtils.copyLarge(in, out);
	    LogHelper.debug(LOG, String.format(Messages.getString("ImportFileManager.6"), filename, path)); //$NON-NLS-1$
	}

	return Collections.singletonList(file.getName());
    }

    private Path getPath(String... folders) {
	String parent = DATE_DIR_FORMATTER.format(LocalDate.now());

	List<String> folderList = new ArrayList<>();
	folderList.add(parent);
	folderList.addAll(
		Stream.of(folders).filter(Objects::nonNull).map(FilenameUtils::getName).collect(Collectors.toList()));

	return Paths.get(TMP, folderList.toArray(new String[0]));
    }

    private ZipFileDecompressor getZipFileDecompressor() {
	return _zipFileDecompressor;
    }

    private boolean isZipFile(String extension) {
	return StringUtils.equalsIgnoreCase(extension, ImportFileExtension.ZIP.name());
    }
}
