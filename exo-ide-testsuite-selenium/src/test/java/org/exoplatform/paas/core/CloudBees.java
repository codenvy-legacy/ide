package org.exoplatform.paas.core;

import org.exoplatform.ide.core.AbstractTestModule;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class CloudBees extends AbstractTestModule
{
   
   
      private interface Locators
      {
         String VIEW_ID = "";
      }
      
      @FindBy(xpath = Locators.VIEW_ID)
      private WebElement view;
 
      
}
