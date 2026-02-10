package com.assessment.demo.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PublicService {

    public Map<String, String> health() {
        return Map.of(
                "status", "UP",
                "message", "Application is running"
        );
    }
}
