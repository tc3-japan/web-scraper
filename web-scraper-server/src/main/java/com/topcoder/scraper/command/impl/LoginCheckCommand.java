package com.topcoder.scraper.command.impl;

import com.topcoder.scraper.command.AbstractCommand;
import com.topcoder.scraper.module.ILoginCheckModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Login check
 */
@Component
public class LoginCheckCommand extends AbstractCommand<ILoginCheckModule> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginCheckCommand.class);

    @Autowired
    public LoginCheckCommand(List<ILoginCheckModule> modules) {
        super(modules);
    }

    /**
     * run check login from specific module
     *
     * @param module module to be run
     */
    @Override
    protected void process(ILoginCheckModule module) {
        LOGGER.info("module=site: " + module);
        module.checkLogin();
        LOGGER.info("Successfully check login");
    }
}
