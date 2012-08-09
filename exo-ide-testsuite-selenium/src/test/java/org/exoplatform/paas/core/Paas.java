package org.exoplatform.paas.core;
import com.thoughtworks.selenium.Selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;


/**
 * Git extension test module.
 * 
 * @author <a href="@gmail.com">Musienko Maxim</a>
 * @version $Id:  Jun 23, 2011 10:39:29 AM anya $
 *
 */

public class Paas
{
   private Selenium selenium;

   private static Paas instance;

   public static Paas getInstance()
   {
      return instance;
   }

   public Paas(Selenium selenium, WebDriver driver)
   {
      this.selenium = selenium;
      instance = this;
      CLOUDBEES = PageFactory.initElements(driver, CloudBees.class);
      
   }

   public Selenium getSelenium()
   {
      return selenium;
   }
 
   public CloudBees CLOUDBEES;
   
}
