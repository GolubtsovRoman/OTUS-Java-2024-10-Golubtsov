package ru.otus.controller.page;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ClientPageController {

    @GetMapping("/")
    public String mainPage() {
        return "clients";
    }

}
