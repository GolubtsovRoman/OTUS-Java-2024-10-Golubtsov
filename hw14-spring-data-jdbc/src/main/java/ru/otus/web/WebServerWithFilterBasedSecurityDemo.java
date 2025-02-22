package ru.otus.web;

import ru.otus.jpql.core.repository.DataTemplateHibernate;
import ru.otus.jpql.core.repository.HibernateUtils;
import ru.otus.jpql.core.sessionmanager.TransactionManagerHibernate;
import ru.otus.jpql.crm.model.entity.Address;
import ru.otus.jpql.crm.model.entity.Client;
import ru.otus.jpql.crm.model.entity.Phone;
import ru.otus.jpql.crm.service.DBServiceClient;
import ru.otus.jpql.crm.service.DbServiceClientImpl;
import ru.otus.jpql.crm.utils.DbUtils;
import ru.otus.web.dao.InMemorySingleAdminUserDao;
import ru.otus.web.dao.UserDao;
import ru.otus.web.server.ClientsWebServerWithFilterBasedSecurity;
import ru.otus.web.services.TemplateProcessorImpl;
import ru.otus.web.services.UserAuthService;
import ru.otus.web.services.UserAuthServiceImpl;
import ru.otus.web.utils.GsonUtils;

import java.util.List;

/*
    Полезные для демо ссылки

    // Стартовая страница
    http://localhost:8080

    // Страница клиентов
    http://localhost:8080/clients

    // REST сервис
    http://localhost:8080/api/client
*/
public class WebServerWithFilterBasedSecurityDemo {

    private static final int WEB_SERVER_PORT = 8080;
    private static final String TEMPLATES_DIR = "/templates/";

    private static final String ADMIN_LOGIN = "admin";
    private static final String ADMIN_PASSWORD = "password";



    public static void main(String[] args) throws Exception {
        DbUtils.doDbMigration();

        UserDao userDao = new InMemorySingleAdminUserDao(ADMIN_LOGIN, ADMIN_PASSWORD);
        UserAuthService authService = new UserAuthServiceImpl(userDao);

        var dbServiceClient = getDbServiceClient();
        fillData(dbServiceClient);

        var usersWebServer = new ClientsWebServerWithFilterBasedSecurity(
                WEB_SERVER_PORT,
                authService,
                dbServiceClient,
                GsonUtils.getGson(),
                new TemplateProcessorImpl(TEMPLATES_DIR)
        );
        usersWebServer.start();
        usersWebServer.join();
    }

    private static DBServiceClient getDbServiceClient() {
        var sessionFactory = HibernateUtils.buildSessionFactory(DbUtils.getConfiguration(),
                Client.class, Address.class, Phone.class);

        var transactionManager = new TransactionManagerHibernate(sessionFactory);
        ///
        var clientTemplate = new DataTemplateHibernate<>(Client.class);
        ///
        return new DbServiceClientImpl(transactionManager, clientTemplate);
    }


    private static void fillData(DBServiceClient dbServiceClient) {
        dbServiceClient.saveClient(new Client(
                null,
                "Anna",
                new Address(null, "Nevsky Prospect"),
                List.of(new Phone(null, "123-456-7890"), new Phone(null, "098-765-4321"))
        ));
        dbServiceClient.saveClient(new Client(
                null,
                "Alyona",
                new Address(null, "Rubinstein Street"),
                List.of(new Phone(null, "234-567-8901"), new Phone(null, "876-543-2109"))
        ));
    }

}
