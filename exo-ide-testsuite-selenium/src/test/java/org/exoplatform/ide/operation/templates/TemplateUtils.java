/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.operation.templates;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.thoughtworks.selenium.Selenium;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class TemplateUtils
{
   static final String CREATE_BUTTON_LOCATOR = "scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormCreateButton\"]/";
   
   static final String DELETE_BUTTON_LOCATOR = "scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormDeleteButton\"]/";
   
   static final String CANCEL_BUTTON_LOCATOR = "scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormCancelButton\"]/";
   
   static final String NAME_FIELD_LOCATOR = "scLocator=//DynamicForm[ID=\"ideCreateFileFromTemplateFormDynamicForm\"]/item[name=ideCreateFileFromTemplateFormFileNameField]/element";
   
   public static final String DEFAULT_PROJECT_TEMPLATE_NAME = "ide-project";
   
   public static final String EMPTY_PROJECT_TEMPLATE_NAME = "new-project";
   
   public static void checkCreateProjectFromTemplateForm(Selenium selenium)
   {
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFileFromTemplateForm\"]/"));
      assertEquals("Create project", selenium.getText("scLocator=//Window[ID=\"ideCreateFileFromTemplateForm\"]/header/"));
      assertTrue(selenium.isElementPresent(DELETE_BUTTON_LOCATOR));
      assertTrue(selenium.isElementPresent(CREATE_BUTTON_LOCATOR));
      assertTrue(selenium.isElementPresent(CANCEL_BUTTON_LOCATOR));
      assertTrue(selenium.isElementPresent("scLocator=//DynamicForm[ID=\"ideCreateFileFromTemplateFormDynamicForm\"]/item[name=ideCreateFileFromTemplateFormFileNameField]/element"));
   }
   
   public static void selectProjectTemplate(Selenium selenium, String projectTemplateName) throws Exception
   {
      selenium.mouseDownAt("//div[@eventproxy='ideCreateFileFromTemplateFormTemplateListGrid_body']//span[@title='" 
         + projectTemplateName + "']", "");
      selenium.mouseUpAt("//div[@eventproxy='ideCreateFileFromTemplateFormTemplateListGrid_body']//span[@title='"
         + projectTemplateName + "']", "");
      
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
   }
   
   public static void typeProjectName(Selenium selenium, String projectName) throws Exception
   {
      selenium.type("scLocator=//DynamicForm[ID=\"ideCreateFileFromTemplateFormDynamicForm\"]/item[" 
         + "name=ideCreateFileFromTemplateFormFileNameField]/element", projectName);
      
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
   }
   
   public static void clickCreateProjectButton(Selenium selenium) throws Exception
   {
      selenium.click(CREATE_BUTTON_LOCATOR);
      Thread.sleep(TestConstants.SLEEP);
   }
   
   /**
    * Create project from template: 
    * 1. Call "Project From Template" form
    * 2. Select <code>templateName</code> template in list grid
    * 3. Type project name <code>projectName</code> (if null - leave the default project name)
    * 4. Click Create button
    * 
    * @param selenium - selenium
    * @param templateName - the template name in list grid
    * @param projectName - new project name (if null - project with default name will be created).
    * @throws Exception
    */
   public static void createProjectFromTemplate(Selenium selenium, String templateName, String projectName) throws Exception
   {
      BaseTest.IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.PROJECT_FROM_TEMPLATE);
      
      TemplateUtils.checkCreateProjectFromTemplateForm(selenium);
      TemplateUtils.selectProjectTemplate(selenium, templateName);
      if (projectName != null)
      {
         TemplateUtils.typeProjectName(selenium, projectName);
      }
      TemplateUtils.clickCreateProjectButton(selenium);
      Thread.sleep(TestConstants.SLEEP);
   }
}
