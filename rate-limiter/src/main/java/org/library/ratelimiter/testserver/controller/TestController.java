package org.library.ratelimiter.testserver.controller;

import org.library.ratelimiter.annotation.RateLimit;
import org.springframework.beans.factory.annotation.Value;
//import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

//@RefreshScope
@RestController
public class TestController {



    @RateLimit(strategy = "TOKEN_BUCKET", api = "test-api")
    @GetMapping("/test")
    public String test(@RequestHeader(value = "X-USER-ID", required = false) String userId){
        return "Request successful";
    }



//    @Value("${ratelimiter}")
//    private String message;
//    @GetMapping("/message")
//    public String getMessage() {
//        return message;
//    }
}
