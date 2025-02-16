package ru.otus;

import ru.otus.appcontainer.AppComponentsContainerImpl;
import ru.otus.appcontainer.api.AppComponentsContainer;
import ru.otus.services.GameProcessor;

@SuppressWarnings({"squid:S125", "squid:S106"})
public class AppLevel3 {

    public static void main(String[] args) {
        // Тут можно использовать библиотеку Reflections (см. зависимости)
//        AppComponentsContainer container = new AppComponentsContainerImpl("ru.otus.config");
        AppComponentsContainer container = new AppComponentsContainerImpl("ru.otus.multiconfig");


        // Приложение должно работать в каждом из указанных ниже вариантов
        GameProcessor gameProcessor = container.getAppComponent(GameProcessor.class);
        //  GameProcessor gameProcessor = container.getAppComponent(GameProcessorImpl.class);
        // GameProcessor gameProcessor = container.getAppComponent("gameProcessor");

        gameProcessor.startGame();
    }

}
