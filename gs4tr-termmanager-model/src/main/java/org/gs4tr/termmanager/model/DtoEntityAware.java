package org.gs4tr.termmanager.model;

public interface DtoEntityAware<T> {
    T convertToDtoEntity();
}
