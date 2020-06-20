package com.masonpohler.api.environment;

import org.springframework.stereotype.Service;

@Service
public class Environment {

    public Environment() {
        // instantiation for testing
    }

    public String getEnv(String env) {
        return System.getenv(env);
    }
}
