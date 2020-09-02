package com.topcoder.api.service.login;

import com.topcoder.api.exception.ApiException;
import com.topcoder.common.model.LoginRequest;
import com.topcoder.common.model.LoginResponse;

public interface LoginHandler {

    public String getECSite();

    public LoginResponse loginInit(int userId, Integer siteId, String uuid) throws ApiException;

    public LoginResponse login(int userId, LoginRequest request) throws ApiException;
}
