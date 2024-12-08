package ru.otus.java.dev.pro.aop.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.java.dev.pro.aop.annotation.LogParameters;
import ru.otus.java.dev.pro.service.SmokeService;
import ru.otus.java.dev.pro.service.SmokeServiceImpl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Ioc {

    private static final Logger LOG = LoggerFactory.getLogger(Ioc.class);

    private Ioc() {}

    public static SmokeService createSmokeService() {
        InvocationHandler handler = new DemoInvocationHandler(new SmokeServiceImpl());
        return (SmokeService)
                Proxy.newProxyInstance(Ioc.class.getClassLoader(), new Class<?>[] {SmokeService.class}, handler);
    }

    static class DemoInvocationHandler implements InvocationHandler {
        private final SmokeService myClass;

        DemoInvocationHandler(SmokeService myClass) {
            this.myClass = myClass;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            List<String> annotatedSignatureMethodList = Arrays.stream(myClass.getClass().getDeclaredMethods())
                    .filter(myClassMethod -> myClassMethod.isAnnotationPresent(LogParameters.class))
                    .map(this::getSignatureMethod)
                    .toList();

            String proxySignatureMethod = getSignatureMethod(method);
            LOG.debug("invoking method: {}", proxySignatureMethod);

            if (annotatedSignatureMethodList.contains(proxySignatureMethod)) {
                StringBuilder infoMethod = new StringBuilder("executed method: ").append(method.getName());
                if (args != null && args.length > 0) {
                    infoMethod
                            .append(", param(s):")
                            .append(Arrays.stream(args).map(Object::toString).collect(Collectors.joining(", ")));
                } else {
                    infoMethod
                            .append(", no params");
                }
                LOG.info(infoMethod.toString());
            }

            return method.invoke(myClass, args);
        }

        @Override
        public String toString() {
            return "DemoInvocationHandler{" + "myClass=" + myClass + '}';
        }

        private String getSignatureMethod(Method method) {
            return method.getReturnType() + " " + method.getName() + " " + Arrays.toString(method.getParameters());
        }

    }

}
