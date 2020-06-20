package com.masonpohler.api.environment;

import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.error.MissingEnvironmentVariableException;

@Service
public class SystemEnvironmentService implements EnvironmentService {

    @Override
    public String getEnv(String key) {
        String env = System.getenv(key);

        if (env == null) {
            throw new MissingEnvironmentVariableException("EnvironmentService variable " + key + " is not set.");
        }

        return env;
    }
}
