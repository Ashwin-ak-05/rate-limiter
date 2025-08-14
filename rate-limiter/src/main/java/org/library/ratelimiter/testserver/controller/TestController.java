package org.library.ratelimiter.testserver.controller;

import org.library.ratelimiter.annotation.RateLimit;
import org.springframework.beans.factory.annotation.Value;
//import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

//@RefreshScope
@CrossOrigin(origins = "*")
@RateLimit(api = "global-api")
@RestController

public class TestController {


    @RateLimit(api = "test-api")
    @GetMapping("/test")
    public String test(@RequestHeader(value = "X-USER-ID", required = false) String userId) {
        return "Request successful";
    }




}
