package ru.otus.web.server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.jpql.crm.model.entity.Address;
import ru.otus.jpql.crm.model.entity.Client;
import ru.otus.jpql.crm.model.entity.Phone;
import ru.otus.jpql.crm.service.DBServiceClient;
import ru.otus.web.services.TemplateProcessor;
import ru.otus.web.utils.GsonUtils;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static ru.otus.web.server.utils.WebServerHelper.buildUrl;

@DisplayName("Тест сервера должен ")
class UsersWebServerImplTest {

    private static final int WEB_SERVER_PORT = 8989;
    private static final String WEB_SERVER_URL = "http://localhost:" + WEB_SERVER_PORT + "/";
    private static final String API_CLIENT_URL = "api/client";


    private static final Client CLIENT_ONE = new Client(
            1L,
            "Anna",
            new Address(1L, "Nevsky Prospect"),
            List.of(new Phone(1L, "123-456-7890"), new Phone(2L, "098-765-4321"))
    );
    private static final Client CLIENT_TWO = new Client(
            2L,
            "Alyona",
            new Address(2L, "Rubinstein Street"),
            List.of(new Phone(3L, "234-567-8901"), new Phone(4L, "876-543-2109"))
    );

    private static Gson gson;
    private static ClientsWebServer webServer;
    private static HttpClient http;

    @BeforeAll
    static void setUp() throws Exception {
        http = HttpClient.newHttpClient();

        TemplateProcessor templateProcessor = mock(TemplateProcessor.class);

        DBServiceClient dbServiceClient = mock(DBServiceClient.class);
        given(dbServiceClient.findAll()).willReturn(List.of(CLIENT_ONE, CLIENT_TWO));

        gson = GsonUtils.getGson();
        webServer = new ClientsWebServerSimple(WEB_SERVER_PORT, dbServiceClient, gson, templateProcessor);
        webServer.start();
    }

    @AfterAll
    static void tearDown() throws Exception {
        webServer.stop();
    }


    @DisplayName("возвращать корректные данные при запросе всех клиентов если вход выполнен")
    @Test
    void shouldReturnCorrectUserWhenAuthorized() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(buildUrl(WEB_SERVER_URL, API_CLIENT_URL)))
                .build();
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(HttpURLConnection.HTTP_OK);

        List<Client> actualClients = gson.fromJson(response.body(), new TypeToken<List<Client>>(){}.getType());

        // потому что мы сравниваем не java-объекты, а их серриализованные версии
        String jsonExpectedClients = gson.toJson(List.of(CLIENT_ONE, CLIENT_TWO));
        List<Client> expectedClients = gson.fromJson(jsonExpectedClients, new TypeToken<List<Client>>(){}.getType());

        assertThat(actualClients).usingRecursiveComparison().isEqualTo(expectedClients);
    }
}
