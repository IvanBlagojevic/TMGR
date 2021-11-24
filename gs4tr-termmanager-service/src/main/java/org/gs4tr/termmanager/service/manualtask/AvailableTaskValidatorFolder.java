package org.gs4tr.termmanager.service.manualtask;

import org.gs4tr.termmanager.model.search.ItemFolderEnum;

public interface AvailableTaskValidatorFolder<T> {
    /**
     * 
     * Validates if current system task is available for incoming folder. This
     * is NOT null safe method.
     * 
     * @param folder
     *            for validation
     * @return availability of current system task
     */
    boolean isTaskAvailableForFolder(T entity, ItemFolderEnum folder);
}
