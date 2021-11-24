package org.gs4tr.termmanager.persistence;

import java.io.Serializable;
import java.util.List;

public interface IGenericUpdater<T, ID extends Serializable, EX extends Exception> {

    void delete(T entity) throws EX;

    void save(List<T> entities) throws EX;

    void save(T entity) throws EX;

    void update(List<T> entities) throws EX;

    void update(T entity) throws EX;
}
