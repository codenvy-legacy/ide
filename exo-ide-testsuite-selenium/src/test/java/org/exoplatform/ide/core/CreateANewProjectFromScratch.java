/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ide.core;

import org.exoplatform.ide.TestConstants;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author <a href="mailto:riuvshin@exoplatform.com">Roman Iuvshin</a>
 * @version 24.12.2012 22:23:22
 *
 */
public class CreateANewProjectFromScratch extends AbstractTestModule
{
   private interface Locators
   {
      String CREATE_PROJECT_FROM_SCRATCH_ID = "eXoCreateNewProjectView-window";

      String INTPUT_PROJECT_NAME =
         "//div[@id='eXoCreateNewProjectView-window']//input[@name='eXoCreateNewProjectViewNameField']";

      String CREATE_PROJECT_JAR_ID = "CREATE-PROJECT-JAR";

      String CREATE_PROJECT_JSP_ID = "CREATE-PROJECT-JSP";

      String CREATE_PROJECT_SPRING_ID = "CREATE-PROJECT-SPRING";

      String CREATE_PROJECT_JAVASCRIPT_ID = "CREATE-PROJECT-JAVASCRIPT";

      String CREATE_PROJECT_RUBY_ON_RAILS_ID = "CREATE-PROJECT-RUBY_ON_RAILS";

      String CREATE_PROJECT_PYTHON_ID = "CREATE-PROJECT-PYTHON";

      String CREATE_PROJECT_PHP_ID = "CREATE-PROJECT-PHP";

      String CREATE_PROJECT_MULTI_MODULE_ID = "CREATE-PROJECT-MultiModule";

      String CREATE_PROJECT_PAAS_AWS_ID = "CREATE-PROJECT-PAAS-AWS";

      String CREATE_PROJECT_PAAS_APPFOG_ID = "CREATE-PROJECT-PAAS-AppFog";

      String CREATE_PROJECT_PAAS_CLOUDFOUNDRY_ID = "CREATE-PROJECT-PAAS-CloudFoundry";

      String CREATE_PROJECT_PAAS_CLOUDBEES_ID = "CREATE-PROJECT-PAAS-CloudBees";

      String CREATE_PROJECT_PAAS_GAE_ID = "CREATE-PROJECT-PAAS-GAE";

      String CREATE_PROJECT_PAAS_HEROKU_ID = "CREATE-PROJECT-PAAS-Heroku";

      String CREATE_PROJECT_PAAS_OPENSHIFT_ID = "CREATE-PROJECT-PAAS-OpenShift";

      String CREATE_PROJECT_PAAS_NONE_ID = "CREATE-PROJECT-PAAS-none";

      String NEXT_BUTTON_ID = "eXoCreateNewProjectViewNextButton";

      String FINISH_BUTTON_ID = "eXoCreateNewProjectViewFinishButton";

      String CANCEL_BUTTON_ID = "eXoCreateNewProjectViewCancelButton";

      String BACK_BUTTON_ID = "eXoCreateNewProjectViewBackButton";

      String PROJECT_TEMPLATE_BY_NAME = "//div[@id='eXoCreateNewProjectView-window']//div[text()='%s']";

      String JREBEL_CHECKBOX = "//div[@id='eXoCreateNewProjectView-window']//input[@type='checkbox']";

      String JREBEL_FIRST_NAME_ID = "jrebelprofilefirstname";

      String JREBEL_PHONE_ID = "jrebelprofilephone";

      String JREBEL_LAST_NAME_ID = "jrebelprofilelastname";

   }

   @FindBy(id = Locators.CREATE_PROJECT_PAAS_AWS_ID)
   WebElement createProjectPaasAWS;

   @FindBy(id = Locators.CREATE_PROJECT_PAAS_APPFOG_ID)
   WebElement createProjectPaasAppFog;

   @FindBy(id = Locators.CREATE_PROJECT_PAAS_CLOUDFOUNDRY_ID)
   WebElement createProjectPaasCloudfoundry;

   @FindBy(id = Locators.CREATE_PROJECT_PAAS_CLOUDBEES_ID)
   WebElement createProjectPaasCloudbees;

   @FindBy(id = Locators.CREATE_PROJECT_PAAS_GAE_ID)
   WebElement createProjectPaasGAE;

   @FindBy(id = Locators.CREATE_PROJECT_PAAS_HEROKU_ID)
   WebElement createProjectPaasHeroku;

   @FindBy(id = Locators.CREATE_PROJECT_PAAS_OPENSHIFT_ID)
   WebElement createProjectPaasOpenShift;

   @FindBy(id = Locators.CREATE_PROJECT_PAAS_NONE_ID)
   WebElement createProjectPaasNone;

   @FindBy(id = Locators.CREATE_PROJECT_JAR_ID)
   WebElement createProjectJar;

   @FindBy(id = Locators.CREATE_PROJECT_JSP_ID)
   WebElement createProjectJsp;

   @FindBy(id = Locators.CREATE_PROJECT_SPRING_ID)
   WebElement createProjectSpring;

   @FindBy(id = Locators.CREATE_PROJECT_JAVASCRIPT_ID)
   WebElement createProjectJS;

   @FindBy(id = Locators.CREATE_PROJECT_RUBY_ON_RAILS_ID)
   WebElement createProjectRuby;

   @FindBy(id = Locators.CREATE_PROJECT_PYTHON_ID)
   WebElement createProjectPython;

   @FindBy(id = Locators.CREATE_PROJECT_PHP_ID)
   WebElement createProjectPhp;

   @FindBy(id = Locators.CREATE_PROJECT_MULTI_MODULE_ID)
   WebElement createProjectMultiModule;

   @FindBy(id = Locators.CREATE_PROJECT_FROM_SCRATCH_ID)
   WebElement createFromScratchForm;

   @FindBy(xpath = Locators.INTPUT_PROJECT_NAME)
   WebElement inputProjectName;

   @FindBy(xpath = Locators.JREBEL_CHECKBOX)
   WebElement jrebelCheckbox;

   @FindBy(id = Locators.JREBEL_FIRST_NAME_ID)
   WebElement jrebelFirstName;

   @FindBy(id = Locators.JREBEL_PHONE_ID)
   WebElement jrebelPhone;

   @FindBy(id = Locators.JREBEL_LAST_NAME_ID)
   WebElement jrebelLastName;

   @FindBy(id = Locators.NEXT_BUTTON_ID)
   WebElement nextButtonId;

   @FindBy(id = Locators.BACK_BUTTON_ID)
   WebElement backButtonId;

   @FindBy(id = Locators.CANCEL_BUTTON_ID)
   WebElement cancelButtonId;

   @FindBy(id = Locators.FINISH_BUTTON_ID)
   WebElement finishButtonId;

   /**
    * wait for opening Create project from scratch form
    */
   public void waitCreateProjectFromScratch()
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            IDE().LOADER.waitClosed();
            try
            {
               return createFromScratchForm.isDisplayed() && createFromScratchForm != null
                  && inputProjectName.isDisplayed() && inputProjectName != null && createProjectJar.isDisplayed()
                  && createProjectJsp.isDisplayed() && createProjectSpring.isDisplayed()
                  && createProjectJS.isDisplayed() && createProjectRuby.isDisplayed()
                  && createProjectPython.isDisplayed() && createProjectPhp.isDisplayed() && nextButtonId.isDisplayed()
                  && cancelButtonId.isDisplayed() && finishButtonId.isDisplayed();
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
            catch (Exception e)
            {
               return false;
            }
         }
      });
   }

   /**
    * type in project name
    */
   public void typeProjectName(String value) throws InterruptedException
   {
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      inputProjectName.clear();
      inputProjectName.sendKeys(value);
   }

   /**
    * select Java Library (JAR) technology
    */
   public void selectJavaLibraryTechnology()
   {
      createProjectJar.click();
   }

   /**
    * select Java Web Application (WAR) technology
    */
   public void selectJavaWebApplicationTechnology()
   {
      createProjectJsp.click();
   }

   /**
    * select Java Spring technology
    */
   public void selectJavaSpringTechnology()
   {
      createProjectSpring.click();
   }

   /**
    * select JavaScript technology
    */
   public void selectJavaScriptTechnology()
   {
      createProjectJS.click();
   }

   /**
    * select Ruby on Rails technology
    */
   public void selectRubyTechnology()
   {
      createProjectRuby.click();
   }

   /**
    * select Python technology
    */
   public void selectPythonTechnology()
   {
      createProjectPython.click();
   }

   /**
    * select PHP technology
    */
   public void selectPHPTechnology()
   {
      createProjectPhp.click();
   }

   /**
    * select Maven Multi-module technology
    */
   public void selectMavenMultiModuleTechnology()
   {
      createProjectMultiModule.click();
   }

   /**
    * select AWS Elastic Beanstalk PaaS
    */
   public void selectAWSPaaS()
   {
      createProjectPaasAWS.click();
   }

   /**
    * select AppFog PaaS
    */
   public void selectAppFogPaaS()
   {
      createProjectPaasAppFog.click();
   }

   /**
    * select Cloud Foundry PaaS
    */
   public void selectCloudFoundryPaaS()
   {
      createProjectPaasCloudfoundry.click();
   }

   /**
    * select CloudBees PaaS
    */
   public void selectCloudBeesPaaS()
   {
      createProjectPaasCloudbees.click();
   }

   /**
    * select Google App Engine PaaS
    */
   public void selectGAEPaaS()
   {
      createProjectPaasGAE.click();
   }

   /**
    * select Heroku PaaS
    */
   public void selectHerokuPaaS()
   {
      createProjectPaasHeroku.click();
   }

   /**
    * select OpenShift PaaS
    */
   public void selectOpenShiftPaaS()
   {
      createProjectPaasOpenShift.click();
   }

   /**
    * select None PaaS
    */
   public void selectNonePaaS()
   {
      createProjectPaasNone.click();
   }

   /**
    * click next button
    */
   public void clickNextButton()
   {
      nextButtonId.click();
   }

   /**
    * click back button
    */
   public void clickBackButton()
   {
      backButtonId.click();
   }

   /**
    * click cancel button
    */
   public void clickCancelButton()
   {
      cancelButtonId.click();
   }

   /**
    * click finish button
    */
   public void clickFinishButton()
   {
      finishButtonId.click();
   }

   /**
    * select project template
    */
   public void selectProjectTemplate(String value)
   {
      driver().findElement(By.xpath(String.format(Locators.PROJECT_TEMPLATE_BY_NAME, value))).click();
   }

   /**
   * wait for project template form
   */

   public void waitProjectTemplateForm()
   {

      (new WebDriverWait(driver(), 30)).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver d)
         {
            try
            {
               WebElement item =
                  driver().findElement(
                     By.xpath(String.format(Locators.PROJECT_TEMPLATE_BY_NAME, "Choose project template")));
               return item != null && item.isDisplayed();
            }
            catch (Exception e)
            {
               return false;
            }
         }
      });
   }

   /**
    * click on JRebel check box
    */
   public void clickOnJRebelCheckbox()
   {
      jrebelCheckbox.click();
   }

   /**
    * Type first name in JRebel field
    */
   public void typeJRebelFirstName(String value)
   {
      jrebelFirstName.sendKeys(value);
   }

   /**
    * Type phone name in JRebel field
    */
   public void typeJRebelPhoneName(String value)
   {
      jrebelPhone.sendKeys(value);
   }

   /**
    * Type last name name in JRebel field
    */
   public void typeJRebelLastName(String value)
   {
      jrebelLastName.sendKeys(value);
   }

   /**
    * wait for JRebel checkbox
    */

   public void waitForJRebelCheckbox()
   {

      (new WebDriverWait(driver(), 30)).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver d)
         {
            try
            {
               return jrebelCheckbox.isDisplayed() && jrebelCheckbox != null && jrebelCheckbox.isSelected();
            }
            catch (Exception e)
            {
               return false;
            }
         }
      });
   }

   /**
    * check that JRebel input fields is present.
    * 
    * @return
    */
   public boolean isJRebelInputsVisible()
   {
      return jrebelFirstName.isDisplayed() && jrebelPhone.isDisplayed() && jrebelLastName.isDisplayed();
   }
}
