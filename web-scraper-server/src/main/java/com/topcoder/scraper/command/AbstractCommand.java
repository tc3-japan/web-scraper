package com.topcoder.scraper.command;

import com.topcoder.scraper.module.IBasicModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;

import java.util.List;
import java.util.Optional;

/**
 * Abstract command class
 * Subclasses are able to run specific module based on {@link ApplicationArguments}
 *
 * @param <T> class extends from {@link IBasicModule}
 */
public abstract class AbstractCommand<T extends IBasicModule> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCommand.class);

    /**
     * Presents list of modules available
     */
    private final List<T> modules;

    protected List<String> sites;

    protected AbstractCommand(List<T> modules) {
        this.modules = modules;
    }

    /**
     * Looks for "site" in arguments
     * and run module only for this site
     * <p>
     * If no site is provided, simply run all modules
     *
     * @param args arguments from input
     */
    public void run(ApplicationArguments args) {
        this.sites = args.getOptionValues("site");
        List<String> moduletypes = args.getOptionValues("module");

        if (moduletypes == null || moduletypes.size() == 0 || moduletypes.get(0).equals("unified")) {
            LOGGER.info("moduletype: unified, site=module: general");
            getModule("general").ifPresent(this::process);
        } else {
            LOGGER.info("moduletype: isolated");
            LOGGER.info("sites=modules: " + sites);
            if (sites != null) {
                sites.forEach(site -> getModule(site).ifPresent(this::process));
            } else {
                modules.forEach(this::process);
            }
        }
    }

    /**
     * abstract method to be implemented in subclass
     *
     * @param module module to be run
     */
    protected abstract void process(T module);

    /**
     * get module from site name
     *
     * @param site site name to be found
     * @return Optional module
     */
    private Optional<T> getModule(String site) {
        return modules.stream().filter((ec) -> ec.getModuleType().equalsIgnoreCase(site)).findFirst();
    }
}
