package ru.otus.homework.jdbc.mapper;

import ru.otus.core.repository.DataTemplate;
import ru.otus.core.repository.DataTemplateException;
import ru.otus.core.repository.executor.DbExecutor;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/** Сохратяет объект в базу, читает объект из базы */
@SuppressWarnings("java:S1068")
public class DataTemplateJdbc<T> implements DataTemplate<T> {

    private final DbExecutor dbExecutor;
    private final EntitySQLMetaData entitySQLMetaData;
    private final EntityClassMetaData<T> entityClassMetaData;

    public DataTemplateJdbc(DbExecutor dbExecutor,
                            EntitySQLMetaData entitySQLMetaData,
                            EntityClassMetaData<T> entityClassMetaData) {
        this.dbExecutor = dbExecutor;
        this.entitySQLMetaData = entitySQLMetaData;
        this.entityClassMetaData = entityClassMetaData;
    }

    @Override
    public Optional<T> findById(Connection connection, long id) {
        return dbExecutor.executeSelect(connection, entitySQLMetaData.getSelectByIdSql(), List.of(id), rs -> {
            try {
                if (rs.next()) {
                    return newInstance(rs);
                }
                return null;
            } catch (SQLException e) {
                throw new DataTemplateException(e);
            }
        });
    }

    @Override
    public List<T> findAll(Connection connection) {
        return dbExecutor
                .executeSelect(connection, entitySQLMetaData.getSelectAllSql(), Collections.emptyList(), rs -> {
                    var entityList = new ArrayList<T>();
                    try {
                        while (rs.next()) {
                            entityList.add(newInstance(rs));
                        }
                        return entityList;
                    } catch (SQLException e) {
                        throw new DataTemplateException(e);
                    }
                })
                .orElseThrow(() -> new RuntimeException("Unexpected error"));
    }

    @Override
    public long insert(Connection connection, T client) {
        try {
            return dbExecutor.executeStatement(connection, entitySQLMetaData.getInsertSql(), getValues(client));
        } catch (Exception e) {
            throw new DataTemplateException(e);
        }
    }

    @Override
    public void update(Connection connection, T client) {
        List<Object> values = getValues(client);
        values.add(getId(client));
        try {
            dbExecutor.executeStatement(connection, entitySQLMetaData.getUpdateSql(), values);
        } catch (Exception e) {
            throw new DataTemplateException(e);
        }
    }


    private List<Object> getValues(T client) {
        return entityClassMetaData.getFieldsWithoutId()
                .stream()
                .map(field -> {
                    field.setAccessible(true);
                    try {
                        return field.get(client);
                    } catch (IllegalAccessException e) {
                        throw new DataTemplateException(e);
                    }
                })
                .toList();
    }

    private Object getId(T client) {
        var idField = entityClassMetaData.getIdField();
        try {
            idField.setAccessible(true);
            return idField.get(client);
        } catch (IllegalAccessException e) {
            throw new DataTemplateException(e);
        }
    }

    private T newInstance(ResultSet rs) {
        Object[] entityValueArray = entityClassMetaData.getAllFields()
                .stream()
                .map(Field::getName)
                .map(fieldName -> {
                    try {
                        return rs.getObject(fieldName);
                    } catch (SQLException e) {
                        throw new DataTemplateException(e);
                    }
                })
                .toArray();
        try {
            return entityClassMetaData.getConstructor().newInstance(entityValueArray);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new DataTemplateException(e);
        }
    }

}
