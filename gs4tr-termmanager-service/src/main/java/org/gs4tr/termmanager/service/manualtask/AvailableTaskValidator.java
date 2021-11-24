package org.gs4tr.termmanager.service.manualtask;

public interface AvailableTaskValidator<T> {
    /**
     * 
     * Validates if current system task is available for incoming entity. This
     * is NOT null safe method.
     * 
     * @param entity
     *            for task validation
     * @return availability of current system task
     */
    boolean isTaskAvailable(T entity);
}
