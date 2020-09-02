package com.topcoder.api.service.login;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LoginHandlerFactory {

    private final Map<String, LoginHandler> handlers = new HashMap<>();

    @Autowired
    public LoginHandlerFactory(List<LoginHandlerBase> loginHandlers) {
        if (loginHandlers != null) {
            loginHandlers.forEach(h -> {
                handlers.put(h.getECSite(), h);
            });
        }
    }

    public LoginHandler getLoginHandler(String ecSite) {
        return handlers.get(ecSite);
    }
}
