package com.topcoder.scraper.command.impl;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.apache.bsf.BSFManager;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Properties;

// TODO : re-consider whether this class is needed or not.
@Component
public class GroovyDemoCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserEncoderCommand.class);

    public void run(ApplicationArguments args) {

        // >>> BSF
        String myScript = "println('Hello World')\n  return [1, 2, 3]";
        BSFManager manager = new BSFManager();
        try {
            List answer = (List) manager.eval("groovy", "myScript.groovy", 0, 0, myScript);
            LOGGER.info("answer: " + answer);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // >>> GroovyShell simple expression
        GroovyShell shell = new GroovyShell();

        int resInt = (int) shell.evaluate("1 + 2");
        LOGGER.info("resInt: " + resInt);


        // >>> GroovyShell call this.method
        Binding binding = new Binding();
        binding.setProperty("env", this);
        shell = new GroovyShell(binding);

        String srcText = "env.hello(\"Groovy\")";
        String resStr = (String) shell.evaluate(srcText);
        LOGGER.info("resStr: " + resStr);


        // >>> GroovyShell call external methods
        binding = new Binding();

        Properties configProps = new Properties();
        configProps.setProperty("groovy.script.base", GroovyDemoScriptSupport.class.getName());
        CompilerConfiguration config = new org.codehaus.groovy.control.CompilerConfiguration(configProps);

        shell = new GroovyShell(binding, config);
        srcText = "foo(); foo(); foo \"a\", \"b\", \"c\";";
        Script script = shell.parse(srcText);
        resStr = (String) script.run();
        LOGGER.info("resStr 2: " + resStr);

    }

    String hello(String x) {
        LOGGER.info("call hello with " + x);
        return "Hello" + x;
    }
}
