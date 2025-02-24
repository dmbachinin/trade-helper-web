package ru.trade.helper.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/trade")
public class TraderController {

    @GetMapping("/")
    public String stocks() {
        return "stocks";
    }
}
