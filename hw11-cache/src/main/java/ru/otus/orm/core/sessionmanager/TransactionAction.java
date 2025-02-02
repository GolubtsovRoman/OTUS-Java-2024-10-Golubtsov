package ru.otus.orm.core.sessionmanager;

import java.sql.Connection;
import java.util.function.Function;

public interface TransactionAction<T> extends Function<Connection, T> {}
