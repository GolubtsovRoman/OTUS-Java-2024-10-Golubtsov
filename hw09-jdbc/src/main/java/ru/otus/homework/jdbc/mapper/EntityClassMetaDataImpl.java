package ru.otus.homework.jdbc.mapper;

import ru.otus.homework.aop.annotation.Id;
import ru.otus.homework.jdbc.exception.EntityMetadataException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class EntityClassMetaDataImpl<T> implements EntityClassMetaData<T> {

    private final Class<T> entityClass;

    public EntityClassMetaDataImpl(Class<T> entityClass) {
        this.entityClass = entityClass;
    }


    @Override
    public String getName() {
        return entityClass.getSimpleName();
    }

    @Override
    public Constructor<T> getConstructor() {
        Class<?>[] parameterTypes = getAllFields().stream()
                .map(Field::getType)
                .toArray(Class<?>[]::new);
        try {
            return entityClass.getConstructor(parameterTypes);
        } catch (NoSuchMethodException e) {
            throw new EntityMetadataException(e);
        }
    }

    @Override
    public Field getIdField() {
        List<Field> idFields = getAllFields().stream()
                .filter(field -> field.isAnnotationPresent(Id.class))
                .toList();
        if (idFields.size() > 1) {
            throw new EntityMetadataException("Field with annotation @Id more then ONE");
        }
        if (idFields.isEmpty()) {
            return null;
        }
        return idFields.getFirst();
    }

    @Override
    public List<Field> getAllFields() {
        return Arrays.asList(entityClass.getDeclaredFields());
    }

    @Override
    public List<Field> getFieldsWithoutId() {
        return getAllFields().stream()
                .filter(field -> !field.isAnnotationPresent(Id.class))
                .toList();
    }

}
