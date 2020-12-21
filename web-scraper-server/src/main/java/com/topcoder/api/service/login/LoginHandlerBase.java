package com.topcoder.api.service.login;

import com.topcoder.common.dao.ECSiteAccountDAO;
import com.topcoder.common.dao.UserDAO;
import com.topcoder.common.model.AuthStatusType;
import com.topcoder.common.repository.ECSiteAccountRepository;
import com.topcoder.common.repository.UserRepository;

import java.util.Date;
import java.util.List;

public abstract class LoginHandlerBase implements LoginHandler {

    protected final ECSiteAccountRepository ecSiteAccountRepository;

    protected final UserRepository userRepository;

    public LoginHandlerBase(ECSiteAccountRepository ecSiteAccountRepository, UserRepository userRepository) {
        this.ecSiteAccountRepository = ecSiteAccountRepository;
        this.userRepository = userRepository;
    }

    public void saveSuccessResult(ECSiteAccountDAO ecSiteAccountDAO) {
        saveResult(ecSiteAccountDAO, AuthStatusType.SUCCESS, null);

        UserDAO user = userRepository.findOne(ecSiteAccountDAO.getUserId());
        if (user == null || AuthStatusType.SUCCESS.equals(user.getTotalECStatus())) {
            return;
        }

        List<ECSiteAccountDAO> accounts = ecSiteAccountRepository.findAllByUserId(ecSiteAccountDAO.getUserId());
        boolean allAccountsInSuccess = accounts.stream().allMatch(a -> AuthStatusType.SUCCESS.equals(a.getAuthStatus()));
        if (allAccountsInSuccess) {
            user.setTotalECStatus(AuthStatusType.SUCCESS);
            user.setUpdateAt(new Date());
            userRepository.save(user);
        }
    }

    public void saveFailedResult(ECSiteAccountDAO ecSiteAccountDAO, String message) {
        saveResult(ecSiteAccountDAO, AuthStatusType.FAILED, message);

        UserDAO user = userRepository.findOne(ecSiteAccountDAO.getUserId());
        if (user == null || AuthStatusType.FAILED.equals(user.getTotalECStatus())) {
            return;
        }

        user.setTotalECStatus(AuthStatusType.FAILED);
        user.setUpdateAt(new Date());
        userRepository.save(user);
    }

    protected void saveResult(ECSiteAccountDAO ecSiteAccountDAO, String status, String message) {
        ecSiteAccountDAO.setAuthStatus(status);
        ecSiteAccountDAO.setAuthFailReason(message);
        ecSiteAccountDAO.setUpdateAt(new Date());

        boolean isLogin = status.equals(AuthStatusType.SUCCESS);
        ecSiteAccountDAO.setIsLogin(isLogin);
        if (isLogin) {
            ecSiteAccountDAO.setLastLoginedAt(new Date());
        }
        ecSiteAccountRepository.save(ecSiteAccountDAO);
    }

}
