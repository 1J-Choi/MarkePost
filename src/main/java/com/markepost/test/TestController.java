package com.markepost.test;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.v3.oas.annotations.Hidden;

@Controller
@Hidden
public class TestController {
	@ResponseBody
    @GetMapping("/test1")
    public String helloWorld() {
        return "Hello world!";
    }
	
	@ResponseBody
	@GetMapping("/test2")
	public Map<String, Object> returnMap() {
		Map<String, Object> result = new HashMap<>();
		result.put("test", "테스트");
		result.put("code", 1234);
		return result;
	}
	
	@GetMapping("/test3")
	public String htmlTest() {
		return "test/test";
	}
}
