package ru.otus.orm.crm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.cache.HwCache;
import ru.otus.cache.MyCache;
import ru.otus.cache.listener.HwListener;
import ru.otus.orm.core.repository.DataTemplate;
import ru.otus.orm.core.sessionmanager.TransactionRunner;
import ru.otus.orm.crm.model.Client;

import java.util.List;
import java.util.Optional;

public class DbServiceClientImpl implements DBServiceClient {
    private static final Logger log = LoggerFactory.getLogger(DbServiceClientImpl.class);

    private final DataTemplate<Client> dataTemplate;
    private final TransactionRunner transactionRunner;

    private final HwCache<Long, Client> cache = new MyCache<>();

    @SuppressWarnings("java:S1604")
    public DbServiceClientImpl(TransactionRunner transactionRunner, DataTemplate<Client> dataTemplate) {
        this.transactionRunner = transactionRunner;
        this.dataTemplate = dataTemplate;

        // добавлено только чтобы посмотреть логи
        this.cache.addListener(
                new HwListener<Long, Client>() {
                    @Override
                    public void notify(Long key, Client value, String action) {
                        log.info("key:{}, value:{}, action: {}", key, value, action);
                    }
                }
        );
    }

    @Override
    public Client saveClient(Client client) {
        var savedClient = transactionRunner.doInTransaction(connection -> {
            if (client.getId() == null) {
                var clientId = dataTemplate.insert(connection, client);
                var createdClient = new Client(clientId, client.getName());
                log.info("created client: {}", createdClient);
                return createdClient;
            } else {
                dataTemplate.update(connection, client);
                log.info("updated client: {}", client);
                return client;
            }
        });
        cache.put(savedClient.getId(), savedClient);
        return savedClient;
    }

    @Override
    public Optional<Client> getClient(long id) {
        var cachingClient = cache.get(id);
        if (cachingClient != null) {
            return Optional.of(cachingClient);
        } else {
            return transactionRunner.doInTransaction(connection -> {
                var clientOptional = dataTemplate.findById(connection, id);
                log.info("client: {}", clientOptional);
                return clientOptional;
            });
        }
    }

    @Override
    public List<Client> findAll() {
        // мы не можем гарантировать, что в кеше будут все клиенты как в БД, даже если кол-во в БД и в кэше совпадает
        return transactionRunner.doInTransaction(connection -> {
            var clientList = dataTemplate.findAll(connection);
            log.info("clientList:{}", clientList);
            return clientList;
        });
    }

}
