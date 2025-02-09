package ru.otus.jpql.crm.utils;

import org.hibernate.cfg.Configuration;
import ru.otus.jpql.crm.dbmigrations.MigrationsExecutorFlyway;

public class DbUtils {

    public static final String HIBERNATE_CFG_FILE = "hibernate.cfg.xml";


    public static Configuration getConfiguration() {
        return new Configuration().configure(HIBERNATE_CFG_FILE);
    }

    public static void doDbMigration() {
        var configuration = getConfiguration();

        var dbUrl = configuration.getProperty("hibernate.connection.url");
        var dbUserName = configuration.getProperty("hibernate.connection.username");
        var dbPassword = configuration.getProperty("hibernate.connection.password");

        new MigrationsExecutorFlyway(dbUrl, dbUserName, dbPassword).executeMigrations();
    }

}
