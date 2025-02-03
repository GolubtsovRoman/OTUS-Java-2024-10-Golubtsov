package ru.otus.orm.homework.jdbc.mapper;

import ru.otus.orm.homework.aop.annotation.Id;
import ru.otus.orm.homework.jdbc.exception.EntityMetadataException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class EntityClassMetaDataImpl<T> implements EntityClassMetaData<T> {

    private final Class<T> entityClass;
    private final List<Field> affFields;

    public EntityClassMetaDataImpl(Class<T> entityClass) {
        this.entityClass = entityClass;
        this.affFields = Arrays.asList(entityClass.getDeclaredFields());
    }


    @Override
    public String getName() {
        return entityClass.getSimpleName();
    }

    @Override
    public Constructor<T> getConstructor() {
        Class<?>[] parameterTypes = affFields.stream()
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
        List<Field> idFields = affFields.stream()
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
        return affFields;
    }

    @Override
    public List<Field> getFieldsWithoutId() {
        return affFields.stream()
                .filter(field -> !field.isAnnotationPresent(Id.class))
                .toList();
    }

}
