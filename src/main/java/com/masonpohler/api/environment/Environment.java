package com.masonpohler.api.environment;

import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.error.MissingEnvironmentVariableException;

@Service
public class Environment {

    public Environment() {
        // instantiation for testing
    }

    public String getEnv(String key) {
        try {
            return System.getenv(key);
        } catch (NullPointerException e) {
            throw new MissingEnvironmentVariableException("Environment variable " + key + " is not set.");
        }
    }
}
