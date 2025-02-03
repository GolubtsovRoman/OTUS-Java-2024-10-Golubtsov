package ru.otus.orm.homework.jdbc.mapper;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

public class EntitySQLMetaDataImpl<T> implements EntitySQLMetaData {

    private final EntityClassMetaData<T> entityClassMetaData;


    public EntitySQLMetaDataImpl(EntityClassMetaData<T> entityClassMetaData) {
        this.entityClassMetaData = entityClassMetaData;
    }


    @Override
    public String getSelectAllSql() {
        String fieldsParam = entityClassMetaData.getAllFields().stream()
                .map(Field::getName)
                .collect(Collectors.joining(", "));
        String tableName = entityClassMetaData.getName().toLowerCase();
        return "select %s from %s".formatted(fieldsParam, tableName);
    }

    @Override
    public String getSelectByIdSql() {
        String fieldsParam = entityClassMetaData.getAllFields().stream()
                .map(Field::getName)
                .collect(Collectors.joining(", "));
        String tableName = entityClassMetaData.getName().toLowerCase();
        String idFieldName = entityClassMetaData.getIdField().getName();
        return "select %s from %s where %s = ?".formatted(fieldsParam, tableName, idFieldName);
    }

    @Override
    public String getInsertSql() {
        String tableName = entityClassMetaData.getName().toLowerCase();
        List<Field> fieldsWithoutId = entityClassMetaData.getFieldsWithoutId();
        String fieldsParam = fieldsWithoutId.stream()
                .map(Field::getName)
                .collect(Collectors.joining(", "));
        String placeholders = fieldsWithoutId.stream()
                .map(field -> "?")
                .collect(Collectors.joining(", "));
        return "insert into %s(%s) values (%s)".formatted(tableName, fieldsParam, placeholders);
    }

    @Override
    public String getUpdateSql() {
        String tableName = entityClassMetaData.getName().toLowerCase();
        String fieldsParamWithPlaceholder = entityClassMetaData.getFieldsWithoutId().stream()
                .map(Field::getName)
                .collect(Collectors.joining(" = ?, "))
                .concat(" = ?"); // потому что последний останется без плейсхолдера
        String idFieldName = entityClassMetaData.getIdField().getName();
        return "update %s set %s where %s = ?".formatted(tableName, fieldsParamWithPlaceholder, idFieldName);
    }

}
