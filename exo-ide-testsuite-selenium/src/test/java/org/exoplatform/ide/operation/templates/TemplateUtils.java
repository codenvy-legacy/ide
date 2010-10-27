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

import org.exoplatform.ide.TestConstants;

import com.thoughtworks.selenium.Selenium;

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
   
   static void checkCreateProjectFromTemplateForm(Selenium selenium)
   {
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFileFromTemplateForm\"]/"));
      assertEquals("Create project", selenium.getText("scLocator=//Window[ID=\"ideCreateFileFromTemplateForm\"]/header/"));
      assertTrue(selenium.isElementPresent(DELETE_BUTTON_LOCATOR));
      assertTrue(selenium.isElementPresent(CREATE_BUTTON_LOCATOR));
      assertTrue(selenium.isElementPresent(CANCEL_BUTTON_LOCATOR));
      assertTrue(selenium.isElementPresent("scLocator=//DynamicForm[ID=\"ideCreateFileFromTemplateFormDynamicForm\"]/item[name=ideCreateFileFromTemplateFormFileNameField]/element"));
   }
   
   static void selectProjectTemplate(Selenium selenium, String projectTemplateName) throws Exception
   {
      selenium.mouseDownAt("//div[@eventproxy='ideCreateFileFromTemplateFormTemplateListGrid_body']//span[@title='" 
         + projectTemplateName + "']", "");
      selenium.mouseUpAt("//div[@eventproxy='ideCreateFileFromTemplateFormTemplateListGrid_body']//span[@title='"
         + projectTemplateName + "']", "");
      
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
   }
   
   static void selectItemInTemplateList(Selenium selenium, String templateName) throws Exception
   {
      assertTrue(selenium.isElementPresent("//div[@class='windowBody']//table[@class='listTable']//nobr/span[@title='" + templateName + "']"));
      selenium.mouseDownAt("//div[@class='windowBody']//table[@class='listTable']//nobr/span[@title='" + templateName + "']", "2,2");
      selenium.mouseUpAt("//div[@class='windowBody']//table[@class='listTable']//nobr/span[@title='" + templateName + "']", "2,2");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }
   
   static void checkSaveAsTemplateWindow(Selenium selenium)
   {
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideSaveAsTemplateForm\"]/header/"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideSaveAsTemplateFormCancelButton\"]/"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideSaveAsTemplateFormSaveButton\"]/"));
      assertTrue(selenium.isElementPresent("scLocator=//DynamicForm[ID=\"ideSaveAsTemplateFormDynamicForm\"]/item[name=ideSaveAsTemplateFormTypeField]/element"));
      assertTrue(selenium.isElementPresent("scLocator=//DynamicForm[ID=\"ideSaveAsTemplateFormDynamicForm\"]/item[name=ideSaveAsTemplateFormNameField]/element"));
      assertTrue(selenium.isElementPresent("scLocator=//DynamicForm[ID=\"ideSaveAsTemplateFormDynamicForm\"]/item[name=ideSaveAsTemplateFormDescriptionField]/element"));
      assertTrue(selenium.isTextPresent("Type:"));
      assertTrue(selenium.isTextPresent("Name:"));
      assertTrue(selenium.isTextPresent("Description:"));
   }
   
   static void checkCreateFileFromTemplateWindow(Selenium selenium)
   {
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFileFromTemplateForm\"]"));
      assertEquals("Create file", selenium.getText("scLocator=//Window[ID=\"ideCreateFileFromTemplateForm\"]/header"));
      assertTrue(selenium.isElementPresent("scLocator=//DynamicForm[ID=\"ideCreateFileFromTemplateFormDynamicForm\"]/item[name=ideCreateFileFromTemplateFormFileNameField]/element"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormDeleteButton\"]/"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormCreateButton\"]/"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormCancelButton\"]/"));
      //check that Delete and Create buttons are disabled and Cancel is enabled
      assertTrue(selenium.isElementPresent("//div[@class='windowBody']//td[@class='buttonTitleDisabled']/table//td[text()='Delete']"));
      assertTrue(selenium.isElementPresent("//div[@class='windowBody']//td[@class='buttonTitleDisabled']/table//td[text()='Create']"));
      assertTrue(selenium.isElementPresent("//div[@class='windowBody']//td[@class='buttonTitle']/table//td[text()='Cancel']"));
      //assert templates present
      assertTrue(selenium.isElementPresent("//div[@class='windowBody']//table[@class='listTable']//nobr/span[@title='" + "Groovy REST Service" + "']"));
      assertTrue(selenium.isElementPresent("//div[@class='windowBody']//table[@class='listTable']//nobr/span[@title='" + "Empty XML" + "']"));
      assertTrue(selenium.isElementPresent("//div[@class='windowBody']//table[@class='listTable']//nobr/span[@title='" + "Empty HTML" + "']"));
      assertTrue(selenium.isElementPresent("//div[@class='windowBody']//table[@class='listTable']//nobr/span[@title='" + "Empty TEXT" + "']"));
      assertTrue(selenium.isElementPresent("//div[@class='windowBody']//table[@class='listTable']//nobr/span[@title='" + "Google Gadget" + "']"));
   }
}
