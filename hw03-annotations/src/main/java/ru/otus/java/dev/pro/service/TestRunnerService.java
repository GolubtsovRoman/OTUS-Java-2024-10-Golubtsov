package ru.otus.java.dev.pro.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.java.dev.pro.annotation.After;
import ru.otus.java.dev.pro.annotation.Before;
import ru.otus.java.dev.pro.annotation.Test;
import ru.otus.java.dev.pro.enums.StatusTest;
import ru.otus.java.dev.pro.model.TestResult;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.otus.java.dev.pro.enums.StatusTest.ERROR;
import static ru.otus.java.dev.pro.enums.StatusTest.OK;

public class TestRunnerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestRunnerService.class);


    public TestResult run(String className) {
        printHeadline("Start tests for class \"%s\"".formatted(className));

        Class<?> testClass = getClassByName(className);

        var testMethods = new ArrayList<Method>();
        var beforeMethods = new ArrayList<Method>();
        var afterMethods = new ArrayList<Method>();

        Arrays.stream(testClass.getDeclaredMethods()).forEach(method -> {
            if (method.isAnnotationPresent(Test.class)) {
                testMethods.add(method);
            }
            if (method.isAnnotationPresent(Before.class)) {
                beforeMethods.add(method);
            }
            if (method.isAnnotationPresent(After.class)) {
                afterMethods.add(method);
            }
        });

        Map<String, StatusTest> testingResult = testMethods.stream().collect(Collectors.toMap(
                testMethod -> className + '.' + testMethod.getName(),
                testMethod -> runTest(testClass, testMethod, beforeMethods, afterMethods)
        ));

        printHeadline("End tests for class \"%s\"".formatted(className));
        return new TestResult(testingResult);
    }


    private StatusTest runTest(Class<?> testClass,
                         Method testMethod,
                         List<Method> beforeMethods,
                         List<Method> afterMethods) {
        String methodFullName = testClass.getName() + '.' + testMethod.getName();
        printHeadline("Start test ::: %s".formatted(methodFullName));
        Object instance = getInstance(testClass);

        beforeMethods.forEach(method -> runMethod(method, instance));
        var optionalThrowable = runMethod(testMethod, instance);
        if (optionalThrowable.isPresent()) {
            var throwable = optionalThrowable.get();
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            throwable.printStackTrace(pw);
            String stringStackTrace = sw.toString();
            printHeadline("End test ::: %s ::: %s\nReason: %s\nStacktrace:\n%s"
                    .formatted(ERROR, methodFullName, throwable.getMessage(), stringStackTrace)
            );
            return ERROR;
        } else {
            afterMethods.forEach(method -> runMethod(method, instance));
            printHeadline("End test ::: %s ::: %s".formatted(OK,  methodFullName));
            return OK;
        }
    }

    private Class<?> getClassByName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            var errMsg = "Class for testing not found. Name: %s".formatted(className);
            LOGGER.error(errMsg);
            throw new RuntimeException(errMsg, e);
        }
    }

    private Object getInstance(Class<?> clazz) {
        try {
            Constructor<?> constructor = clazz.getConstructor();
            return constructor.newInstance();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            var errMsg = "Can't get instance for class: %s".formatted(clazz.getName());
            LOGGER.error(errMsg);
            throw new RuntimeException(errMsg, e);
        }
    }

    private Optional<Throwable> runMethod(Method method, Object instance) {
        try {
            method.invoke(instance);
        } catch (IllegalAccessException iae) {
            var errMsg = "Can't run method: %s. Reason: %s".formatted(method.getName(), iae.getMessage());
            LOGGER.error(errMsg);
            throw new RuntimeException(errMsg, iae);
        } catch (InvocationTargetException ite) {
            return Optional.of(ite.getTargetException());
        }
        return Optional.empty();
    }

    private void printHeadline(String message) {
        String border = "=".repeat(20);
        LOGGER.info(border);
        LOGGER.info(message);
        LOGGER.info(border);
    }

}
