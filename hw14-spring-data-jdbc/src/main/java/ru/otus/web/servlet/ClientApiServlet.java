package ru.otus.web.servlet;

import com.google.gson.Gson;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.otus.jpql.crm.model.dto.ClientDto;
import ru.otus.jpql.crm.service.DBServiceClient;

import java.io.IOException;

import static org.eclipse.jetty.http.HttpMethod.POST;

@SuppressWarnings({"java:S1989"})
public class ClientApiServlet extends HttpServlet {

    private final transient DBServiceClient dbServiceClient;
    private final transient Gson gson;

    public ClientApiServlet(DBServiceClient dbServiceClient, Gson gson) {
        this.dbServiceClient = dbServiceClient;
        this.gson = gson;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        var clientList = dbServiceClient.findAll();

        response.setContentType("application/json;charset=UTF-8");
        ServletOutputStream out = response.getOutputStream();
        out.print(gson.toJson(clientList));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (POST.asString().equalsIgnoreCase(request.getMethod())) {
            var clientDto = gson.fromJson( request.getReader(), ClientDto.class);
            var savedClient = dbServiceClient.saveClient(clientDto.toEntity());

            response.setContentType("application/json;charset=UTF-8");
            ServletOutputStream out = response.getOutputStream();
            out.print(gson.toJson(savedClient));
        }
    }
}
