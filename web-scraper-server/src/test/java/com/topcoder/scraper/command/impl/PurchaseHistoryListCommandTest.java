package com.topcoder.scraper.command.impl;

import com.topcoder.scraper.module.IPurchaseHistoryListModule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Collections;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PurchaseHistoryListCommand.class)
public class PurchaseHistoryListCommandTest {

  @MockBean(name = "module1")
  private IPurchaseHistoryListModule IPurchaseHistoryListModule1;

  @MockBean(name = "module2")
  private IPurchaseHistoryListModule IPurchaseHistoryListModule2;

  @Mock
  private ApplicationArguments args;

  @Autowired
  private PurchaseHistoryListCommand purchaseHistoryListCommand;

  @Before
  public void setUp() {
    when(IPurchaseHistoryListModule1.getECName()).thenReturn("test");
    when(IPurchaseHistoryListModule2.getECName()).thenReturn("test2");
  }

  @Test
  public void testCallModuleOnceForFirstModule() throws IOException {
    when(args.getOptionValues("site")).thenReturn(Collections.singletonList("test"));
    purchaseHistoryListCommand.run(args);
    verify(IPurchaseHistoryListModule1, times(1)).getECName();
    verify(IPurchaseHistoryListModule1, times(1)).fetchPurchaseHistoryList();
    // not called because of find first module named test
    verify(IPurchaseHistoryListModule2, times(0)).getECName();
    verify(IPurchaseHistoryListModule2, times(0)).fetchPurchaseHistoryList();
  }

  @Test
  public void testCallModuleOnceForSecondModule() throws IOException {
    when(args.getOptionValues("site")).thenReturn(Collections.singletonList("test2"));
    purchaseHistoryListCommand.run(args);
    verify(IPurchaseHistoryListModule1, times(1)).getECName();
    verify(IPurchaseHistoryListModule1, times(0)).fetchPurchaseHistoryList();
    verify(IPurchaseHistoryListModule2, times(1)).getECName();
    verify(IPurchaseHistoryListModule2, times(1)).fetchPurchaseHistoryList();
  }

  @Test
  public void testCallAllModules() throws IOException {
    when(args.getOptionValues("site")).thenReturn(null);
    purchaseHistoryListCommand.run(args);
    verify(IPurchaseHistoryListModule1, times(0)).getECName();
    verify(IPurchaseHistoryListModule1, times(1)).fetchPurchaseHistoryList();
    verify(IPurchaseHistoryListModule2, times(0)).getECName();
    verify(IPurchaseHistoryListModule2, times(1)).fetchPurchaseHistoryList();
  }
}
