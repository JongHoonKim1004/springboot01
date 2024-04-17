package com.example.firstproject.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class FirstApiController {


    @GetMapping("/api/hello")
    public String hello(){
        return "hello world!";
    }


}
