/*
 * Copyright (C) 2011 eXo Platform SAS.
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

import org.exoplatform.ide.core.project.ClasspathProject;
import org.exoplatform.ide.core.project.CreateProject;
import org.exoplatform.ide.core.project.CreateProjectTemplate;
import org.exoplatform.ide.core.project.OpenProject;
import org.exoplatform.ide.core.project.ProjectExplorer;
import org.openqa.selenium.By;
import org.openqa.selenium.support.PageFactory;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class Project extends AbstractTestModule
{

   public interface Locators
   {

      String VIEW_ID = "ideCreateJavaProjectView";

      String PROJECT_NAME_FIELD_LOCATOR = "//div[@view-id='" + VIEW_ID
         + "']//input[@name='ideCreateJavaProjectViewNameField']";

      String CREATE_BUTTON_ID = "ideCreateJavaProjectViewCreateButton";

      String CANCEL_BUTTON_ID = "ideCreateJavaProjectViewCancelButton";

   }

   public ProjectExplorer EXPLORER;

   public CreateProject CREATE;
   
   public OpenProject OPEN;
   
   public ClasspathProject CLASSPATH = new ClasspathProject();
   
   public CreateProjectTemplate TEMPLATE = new CreateProjectTemplate();

   /**
    * 
    */
   public Project()
   {
      EXPLORER = PageFactory.initElements(driver(), ProjectExplorer.class);
      CREATE = PageFactory.initElements(driver(), CreateProject.class);
      OPEN = PageFactory.initElements(driver(), OpenProject.class);
   }

   public void waitForDialogOpened() throws Exception
   {
      String locator = "//div[@view-id='" + Locators.VIEW_ID + "']";
      waitForElementPresent(locator);
   }

   public void waitForDialogClosed() throws Exception
   {
      String locator = "//div[@view-id='" + Locators.VIEW_ID + "']";
      waitForElementNotPresent(locator, 30000);
   }

   public void typeProjectName(String projectName) throws Exception
   {
      IDE().INPUT.typeToElement(driver().findElement(By.xpath(Locators.PROJECT_NAME_FIELD_LOCATOR)), projectName, true);
   }

   public String getProjectName()
   {
      return "";
   }

   public boolean isCreateButtonEnabled() throws Exception
   {
      String locatorEnabled =
         "//div[@view-id='" + Locators.VIEW_ID + "']//div[@id='" + Locators.CREATE_BUTTON_ID
            + "' and @button-enabled='true']";
      if (selenium().isElementPresent(locatorEnabled))
      {
         return true;
      }

      String locatorDisabled =
         "//div[@view-id='" + Locators.VIEW_ID + "']//div[@id='" + Locators.CREATE_BUTTON_ID
            + "' and @button-enabled='false']";
      if (selenium().isElementPresent(locatorDisabled))
      {
         return false;
      }

      throw new Exception("Unable to find Create button.");
   }

   public void clickCreateButton()
   {
      selenium().click(Locators.CREATE_BUTTON_ID);
   }

   public void clickCancelButton()
   {
      selenium().click(Locators.CANCEL_BUTTON_ID);
   }

}
