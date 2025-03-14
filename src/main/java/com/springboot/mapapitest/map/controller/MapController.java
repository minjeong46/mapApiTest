package com.springboot.mapapitest.map.controller;

import com.springboot.mapapitest.map.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class MapController {

    private final TestService testService;


    @GetMapping("/test")
    public String map(@RequestParam(value = "query", defaultValue = "") String query, Model model) {
        List<Map<String,String>> searchData = testService.search(query);
        model.addAttribute("searchData", searchData);
        model.addAttribute("query", query);
        return "map";
    }
}


