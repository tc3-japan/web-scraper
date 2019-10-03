package com.topcoder.scraper.command;

import com.topcoder.scraper.module.IBasicModule;
import org.springframework.boot.ApplicationArguments;

import java.util.List;
import java.util.Optional;

/**
 * Abstract command class
 * Subclasses are able to run specific module based on {@link ApplicationArguments}
 * @param <T> class extends from {@link IBasicModule}
 */
public abstract class AbstractCommand<T extends IBasicModule> {

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
   *
   * If no site is provided, simply run all modules
   *
   * @param args arguments from input
   */
  public void run(ApplicationArguments args) {
    this.sites               = args.getOptionValues("site");
    List<String> moduletypes = args.getOptionValues("module");

    if (moduletypes == null || moduletypes.size() == 0 || moduletypes.get(0).equals("unified") ) {
      // TODO: delete
      System.out.println("---unified=general----------------------------------");
      getModule("general").ifPresent(this::process);
      if (sites.size()<1) System.out.println("You must enter --site=xxx parameter");
    } else {
      // TODO: delete
      System.out.println("---isolated----------------------------------");
      System.out.println("---sites:" + sites);
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
   * @param site site name to be found
   * @return Optional module
   */
  private Optional<T> getModule(String site) {
    return modules.stream().filter((ec) -> ec.getModuleType().equalsIgnoreCase(site)).findFirst();
  }
}
