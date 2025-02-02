package ru.otus.db;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.otus.orm.core.repository.DataTemplate;
import ru.otus.orm.core.repository.executor.DbExecutorImpl;
import ru.otus.orm.core.sessionmanager.TransactionRunner;
import ru.otus.orm.core.sessionmanager.TransactionRunnerJdbc;
import ru.otus.orm.crm.datasource.DriverManagerDataSource;
import ru.otus.orm.crm.model.Client;
import ru.otus.orm.crm.service.DBServiceClient;
import ru.otus.orm.crm.service.DbServiceClientImpl;
import ru.otus.orm.homework.jdbc.mapper.DataTemplateJdbc;
import ru.otus.orm.homework.jdbc.mapper.EntityClassMetaData;
import ru.otus.orm.homework.jdbc.mapper.EntityClassMetaDataImpl;
import ru.otus.orm.homework.jdbc.mapper.EntitySQLMetaData;
import ru.otus.orm.homework.jdbc.mapper.EntitySQLMetaDataImpl;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DisplayName("Время чтения данных должно быть")
@Testcontainers
@SuppressWarnings("java:S2699")
public class CacheSpeedTest {

    @Container
    private final PostgreSQLContainer<?> postgresqlContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testDB")
            .withUsername("usr")
            .withPassword("pwd")
            .withClasspathResourceMapping(
                    "/db/migration/00_createTables.sql",
                    "/docker-entrypoint-initdb.d/00_createTables.sql",
                    BindMode.READ_ONLY
            );



    @DisplayName("быстрее из кэша, чем из БД")
    @Test
    void readFromDbAndFromCache() {
        var simpleSaveDBServiceClient = getSimpleSaveDBServiceClient();
        var noCachingClientId = simpleSaveDBServiceClient.saveClient(new Client("testClient1")).getId();

        var dbClientService = getDbClientService();
        var cachingClientId = dbClientService.saveClient(new Client("testClient2")).getId();

        long readNoCacheStartMs = System.currentTimeMillis();
        dbClientService.getClient(noCachingClientId); // не тратим время на Optional и печать Client
        long readNoCacheEndMs = System.currentTimeMillis();

        long readCacheStartMs = System.currentTimeMillis();
        dbClientService.getClient(cachingClientId);
        long readCacheEndMs = System.currentTimeMillis();

        assertThat(readCacheEndMs - readCacheStartMs).isLessThan(readNoCacheEndMs - readNoCacheStartMs);
    }


    private DBServiceClient getDbClientService() {
        return new DbServiceClientImpl(new TransactionRunnerJdbc(getDataSource()), getDataTemplateClient());
    }

    private DBServiceClient getSimpleSaveDBServiceClient() {
        return new SimpleSaveDBServiceClient(new TransactionRunnerJdbc(getDataSource()), getDataTemplateClient());
    }

    private DataSource getDataSource() {
        return new DriverManagerDataSource(
                postgresqlContainer.getJdbcUrl(),
                postgresqlContainer.getUsername(),
                postgresqlContainer.getPassword()
        );
    }

    private DataTemplate<Client> getDataTemplateClient() {
        EntityClassMetaData<Client> entityClassMetaDataClient = new EntityClassMetaDataImpl<>(Client.class);
        EntitySQLMetaData entitySQLMetaDataClient = new EntitySQLMetaDataImpl<>(entityClassMetaDataClient);
        return new DataTemplateJdbc<>(new DbExecutorImpl(), entitySQLMetaDataClient, entityClassMetaDataClient);
    }


    private static class SimpleSaveDBServiceClient implements DBServiceClient {

        private final DataTemplate<Client> dataTemplate;
        private final TransactionRunner transactionRunner;

        public SimpleSaveDBServiceClient(TransactionRunner transactionRunner, DataTemplate<Client> dataTemplate) {
            this.transactionRunner = transactionRunner;
            this.dataTemplate = dataTemplate;
        }

        @Override
        public Client saveClient(Client client) {
            return transactionRunner.doInTransaction(connection -> {
                var clientId = dataTemplate.insert(connection, client);
                return new Client(clientId, client.getName());
            });
        }

        @Override
        public Optional<Client> getClient(long id) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<Client> findAll() {
            throw new UnsupportedOperationException();
        }
    }

}
