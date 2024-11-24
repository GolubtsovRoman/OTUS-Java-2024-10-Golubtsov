package ru.otus.java.dev.pro.service;

import ru.otus.java.dev.pro.annotation.After;
import ru.otus.java.dev.pro.annotation.Before;
import ru.otus.java.dev.pro.annotation.Test;
import ru.otus.java.dev.pro.enums.StatusTest;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.otus.java.dev.pro.enums.StatusTest.ERROR;
import static ru.otus.java.dev.pro.enums.StatusTest.OK;

public class TestRunnerService {

    public void run(String className) {
        TestLogger.printHeadline("Start tests for class \"%s\"".formatted(className));

        Class<?> testClass = getClassByName(className);

        var testMethods = new ArrayList<Method>();
        var beforeMethods = new ArrayList<Method>();
        var afterMethods = new ArrayList<Method>();

        Arrays.stream(testClass.getDeclaredMethods()).forEach(method -> {
            if (Objects.nonNull(method.getAnnotation(Test.class))) {
                testMethods.add(method);
            }
            if (Objects.nonNull(method.getAnnotation(Before.class))) {
                beforeMethods.add(method);
            }
            if (Objects.nonNull(method.getAnnotation(After.class))) {
                afterMethods.add(method);
            }
        });

        Map<String, StatusTest> testingResult = testMethods.stream().collect(Collectors.toMap(
                testMethod -> className + '.' + testMethod.getName(),
                testMethod -> runTest(testClass, testMethod, beforeMethods, afterMethods)
        ));

        TestLogger.printHeadline("End tests for class \"%s\"".formatted(className));
        TestLogger.printStatistics(testingResult);
    }


    private StatusTest runTest(Class<?> testClass,
                         Method testMethod,
                         ArrayList<Method> beforeMethods,
                         ArrayList<Method> afterMethods) {
        String methodFullName = testClass.getName() + '.' + testMethod.getName();
        TestLogger.printHeadline("Start test ::: %s".formatted(methodFullName));
        Object instance = getInstance(testClass);

        beforeMethods.forEach(method -> runMethod(method, instance));
        var optionalThrowable = runMethod(testMethod, instance);
        if (optionalThrowable.isPresent()) {
            var throwable = optionalThrowable.get();
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            throwable.printStackTrace(pw);
            String stringStackTrace = sw.toString();
            TestLogger.printHeadline("End test ::: %s ::: %s\nReason: %s\nStacktrace:\n%s"
                    .formatted(ERROR, methodFullName, throwable.getMessage(), stringStackTrace)
            );
            return ERROR;
        } else {
            afterMethods.forEach(method -> runMethod(method, instance));
            TestLogger.printHeadline("End test ::: %s ::: %s".formatted(OK,  methodFullName));
            return OK;
        }
    }

    private Class<?> getClassByName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            var errMsg = "Class for testing not found. Name: %s".formatted(className);
            TestLogger.getLogger().error(errMsg);
            throw new RuntimeException(errMsg, e);
        }
    }

    private Object getInstance(Class<?> clazz) {
        try {
            Constructor<?> constructor = clazz.getConstructor();
            return constructor.newInstance();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            var errMsg = "Can't get instance for class: %s".formatted(clazz.getName());
            TestLogger.getLogger().error(errMsg);
            throw new RuntimeException(errMsg, e);
        }
    }

    private Optional<Throwable> runMethod(Method method, Object instance) {
        try {
            method.invoke(instance);
        } catch (IllegalAccessException iae) {
            var errMsg = "Can't run method: %s. Reason: %s".formatted(method.getName(), iae.getMessage());
            TestLogger.getLogger().error(errMsg);
            throw new RuntimeException(errMsg, iae);
        } catch (InvocationTargetException ite) {
            return Optional.of(ite.getTargetException());
        }
        return Optional.empty();
    }

}
