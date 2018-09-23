package com.topcoder.scraper.command.impl;

import com.topcoder.scraper.module.AuthenticationModule;
import java.io.IOException;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AuthenticationCommand.class)
public class AuthenticationCommandTest {

  @MockBean(name = "module1")
  private AuthenticationModule authenticationModule1;

  @MockBean(name = "module2")
  private AuthenticationModule authenticationModule2;

  @Mock
  private ApplicationArguments args;

  @Autowired
  private AuthenticationCommand authenticationCommand;

  @Before
  public void setUp() {
    when(authenticationModule1.getECName()).thenReturn("test");
    when(authenticationModule2.getECName()).thenReturn("test2");
  }

  @Test
  public void testCallModuleOnceForFirstModule() throws IOException {
    when(args.getOptionValues("site")).thenReturn(Collections.singletonList("test"));
    authenticationCommand.run(args);
    verify(authenticationModule1, times(1)).getECName();
    verify(authenticationModule1, times(1)).authenticate();
    // not called because of find first module named test
    verify(authenticationModule2, times(0)).getECName();
    verify(authenticationModule2, times(0)).authenticate();
  }

  @Test
  public void testCallModuleOnceForSecondModule() throws IOException {
    when(args.getOptionValues("site")).thenReturn(Collections.singletonList("test2"));
    authenticationCommand.run(args);
    verify(authenticationModule1, times(1)).getECName();
    verify(authenticationModule1, times(0)).authenticate();
    verify(authenticationModule2, times(1)).getECName();
    verify(authenticationModule2, times(1)).authenticate();
  }

  @Test
  public void testCallAllModules() throws IOException {
    when(args.getOptionValues("site")).thenReturn(null);
    authenticationCommand.run(args);
    verify(authenticationModule1, times(0)).getECName();
    verify(authenticationModule1, times(1)).authenticate();
    verify(authenticationModule2, times(0)).getECName();
    verify(authenticationModule2, times(1)).authenticate();
  }
}
