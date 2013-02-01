package org.exoplatform.paas.core;

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
   private static Paas instance;

   public static Paas getInstance()
   {
      return instance;
   }

   public Paas(WebDriver driver)
   {
      instance = this;
      CLOUDBEES = PageFactory.initElements(driver, CloudBees.class);
      
   }

   public CloudBees CLOUDBEES;
   
}
