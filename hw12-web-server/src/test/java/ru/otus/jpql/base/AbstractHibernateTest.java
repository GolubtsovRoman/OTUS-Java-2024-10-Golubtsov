package ru.otus.jpql.base;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.stat.EntityStatistics;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import ru.otus.jpql.core.repository.DataTemplateHibernate;
import ru.otus.jpql.core.repository.HibernateUtils;
import ru.otus.jpql.core.sessionmanager.TransactionManagerHibernate;
import ru.otus.jpql.crm.dbmigrations.MigrationsExecutorFlyway;
import ru.otus.jpql.crm.model.entity.Address;
import ru.otus.jpql.crm.model.entity.Client;
import ru.otus.jpql.crm.model.entity.Phone;
import ru.otus.jpql.crm.service.DBServiceClient;
import ru.otus.jpql.crm.service.DbServiceClientImpl;

import static ru.otus.jpql.crm.utils.DbUtils.HIBERNATE_CFG_FILE;


public abstract class AbstractHibernateTest {
    protected SessionFactory sessionFactory;
    protected TransactionManagerHibernate transactionManager;
    protected DataTemplateHibernate<Client> clientTemplate;
    protected DBServiceClient dbServiceClient;

    private static TestContainersConfig.CustomPostgreSQLContainer CONTAINER;

    @BeforeAll
    public static void init() {
        CONTAINER = TestContainersConfig.CustomPostgreSQLContainer.getInstance();
        CONTAINER.start();
    }

    @AfterAll
    public static void shutdown() {
        CONTAINER.stop();
    }

    @BeforeEach
    public void setUp() {
        String dbUrl = System.getProperty("app.datasource.demo-db.jdbcUrl");
        String dbUserName = System.getProperty("app.datasource.demo-db.username");
        String dbPassword = System.getProperty("app.datasource.demo-db.password");

        var migrationsExecutor = new MigrationsExecutorFlyway(dbUrl, dbUserName, dbPassword);
        migrationsExecutor.executeMigrations();

        Configuration configuration = new Configuration().configure(HIBERNATE_CFG_FILE);
        configuration.setProperty("hibernate.connection.url", dbUrl);
        configuration.setProperty("hibernate.connection.username", dbUserName);
        configuration.setProperty("hibernate.connection.password", dbPassword);

        var sessionFactory = HibernateUtils.buildSessionFactory(configuration,
                Client.class, Address.class, Phone.class);

        transactionManager = new TransactionManagerHibernate(sessionFactory);
        clientTemplate = new DataTemplateHibernate<>(Client.class);
        dbServiceClient = new DbServiceClientImpl(transactionManager, clientTemplate);
    }

    protected EntityStatistics getUsageStatistics() {
        Statistics stats = sessionFactory.getStatistics();
        return stats.getEntityStatistics(Client.class.getName());
    }
}
