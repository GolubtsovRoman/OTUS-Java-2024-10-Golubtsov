package ru.otus.appcontainer;

import org.reflections.Reflections;
import ru.otus.appcontainer.api.AppComponent;
import ru.otus.appcontainer.api.AppComponentsContainer;
import ru.otus.appcontainer.api.AppComponentsContainerConfig;
import ru.otus.appcontainer.exception.AppComponentConstructorException;
import ru.otus.appcontainer.exception.AppComponentException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("squid:S1068")
public class AppComponentsContainerImpl implements AppComponentsContainer {

    private final List<Object> appComponents = new ArrayList<>();
    private final Map<String, Object> appComponentsByName = new HashMap<>();


    public AppComponentsContainerImpl(Class<?> ...initialConfigClasses) {
        processConfigs(Set.of(initialConfigClasses));
    }

    public AppComponentsContainerImpl(String configPackageName) {
        // можно запихнуть валидацию имени пакета
        Set<Class<?>> allClasses = new Reflections(configPackageName)
                .getTypesAnnotatedWith(AppComponentsContainerConfig.class);
        processConfigs(allClasses);
    }

    private void processConfigs(Set<Class<?>> initialConfigClasses) {
        initialConfigClasses.stream()
                .filter(configClass -> configClass.isAnnotationPresent(AppComponentsContainerConfig.class))
                .sorted(Comparator.comparingInt(configClass -> configClass.getAnnotation(AppComponentsContainerConfig.class).order()))
                .forEach(this::processConfig);
    }

    private void processConfig(Class<?> configClass) {
        checkConfigClass(configClass);
        var sortedMethodList = Arrays.stream(configClass.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(AppComponent.class))
                .sorted(Comparator.comparingInt(method -> method.getAnnotation(AppComponent.class).order()))
                .toList();

        for (Method method : sortedMethodList) {
            var appComponentName = method.getAnnotation(AppComponent.class).name();
            if (appComponentsByName.containsKey(appComponentName)) {
                throw new AppComponentConstructorException("AppComponent with name = [%s] already exists"
                        .formatted(appComponentName));
            }

            Constructor<?>[] declaredConstructors = configClass.getDeclaredConstructors();
            if (declaredConstructors.length > 1) {
                throw new AppComponentConstructorException("Class %s have more then one constructor"
                        .formatted(configClass.getName()));
            }
            try {
                var appObject = configClass.getConstructor().newInstance();
                Parameter[] parameters = method.getParameters();
                Object appComponent;
                if (parameters.length == 0) {
                    appComponent = method.invoke(appObject);

                } else {
                    var methodParamObjects = Arrays.stream(parameters)
                            .map(parameter -> getAppComponent(parameter.getType()))
                            .toArray();
                    appComponent = method.invoke(appObject, methodParamObjects);
                }
                appComponents.add(appComponent);
                appComponentsByName.put(appComponentName, appComponent);

            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new AppComponentConstructorException("Can't create AppConfig");
            }
        }
    }

    private void checkConfigClass(Class<?> configClass) {
        if (!configClass.isAnnotationPresent(AppComponentsContainerConfig.class)) {
            throw new IllegalArgumentException(String.format("Given class is not config %s", configClass.getName()));
        }
    }


    @Override
    public <C> C getAppComponent(Class<C> componentClass) {
        var appComponentsByClass = appComponents.stream()
                .filter(appComponent -> componentClass.isAssignableFrom(appComponent.getClass()))
                .toList();
        if (appComponentsByClass.isEmpty()) {
            throw new AppComponentException("No such AppComponent: " + componentClass.getName());
        }
        if (appComponentsByClass.size() > 1) {
            throw new AppComponentConstructorException("There are more one AppComponent: " + componentClass.getName());
        }
        try {
            return (C) appComponentsByClass.getFirst();
        } catch (ClassCastException cce) {
            throw new AppComponentException("Can't cast to " + componentClass.getName(), cce);
        }
    }

    @Override
    public <C> C getAppComponent(String componentName) {
        var object = appComponentsByName.get(componentName);
        if (object != null) {
            try {
                return (C) object;
            } catch (ClassCastException cce) {
                throw new AppComponentException("Can't cast component: " + componentName, cce);
            }
        } else {
            throw new AppComponentException("No such AppComponent: " + componentName);
        }
    }

}
