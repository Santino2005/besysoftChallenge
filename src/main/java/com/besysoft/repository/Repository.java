package com.besysoft.repository;

import com.besysoft.common.result.Result;
import java.util.List;

public interface Repository<T, ID> {

    T save(T entity);

    Result<T> findById(ID id);

    List<T> findAll();
}
