package ru.otus.java.dev.pro.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.java.dev.pro.aop.annotation.LogParameters;

public class SmokeServiceImpl implements SmokeService {

    private static final Logger LOG = LoggerFactory.getLogger(SmokeServiceImpl.class);

    private static final String SIMPLE_CLASS_NANE = SmokeServiceImpl.class.getSimpleName();


    @LogParameters
    @Override
    public void smoke() {
        LOG.info("Class {} are smoking without parameters...", SIMPLE_CLASS_NANE);
    }

    @LogParameters
    @Override
    public void smoke(int param1) {
        LOG.info("Class {} are smoking with one parameter...", SIMPLE_CLASS_NANE);
    }

    @LogParameters
    @Override
    public void smoke(int param1, long param2) {
        LOG.info("Class {} are smoking with two parameters...", SIMPLE_CLASS_NANE);
    }

    @LogParameters
    @Override
    public void smoke(int param1, String param2) {
        LOG.info("Class {} are smoking with three parameters...", SIMPLE_CLASS_NANE);
    }

    // здесь намеренно нет аннотации @LogParameters
    @Override
    public void noSmoke(int param1) {
        LOG.info("Class {} aren't smoking! Parameters not logging.", SIMPLE_CLASS_NANE);
    }

}
