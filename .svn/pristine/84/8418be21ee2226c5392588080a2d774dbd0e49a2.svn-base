package org.gs4tr.termmanager.service.file.manager;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.gs4tr.foundation.modules.entities.model.RepositoryItem;

public interface FileManager {

    void cleanup() throws IOException;

    void delete(Collection<String> fileNames, String... folders) throws IOException;

    List<File> read(Collection<String> fileNames, String... folders) throws IOException;

    List<File> read(String folder) throws IOException;

    List<String> store(RepositoryItem repositoryItem) throws IOException;
}
