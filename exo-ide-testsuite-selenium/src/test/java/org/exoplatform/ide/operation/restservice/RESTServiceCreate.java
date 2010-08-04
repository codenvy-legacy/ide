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
package org.exoplatform.ide.operation.restservice;

import static org.junit.Assert.*;

import org.exoplatform.ide.BaseTest;
import org.junit.Test;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class RESTServiceCreate extends BaseTest
{
   
   private static String FIRST_NAME= "test.groovy";
   
   private static String SECOND_NAME= "новий.groovy";
   
   @Test
   public void testCreatingRESTService() throws Exception
   {
      selenium.refresh();
      selenium.waitForPageToLoad("30000");
      Thread.sleep(1000);
      openNewFileFromToolbar("REST Service");
      Thread.sleep(1000);
      
      saveAsUsingToolbarButton(FIRST_NAME);
      
      Thread.sleep(500);
      
      selenium.mouseDownAt("//div[@title='Show Properties']//img", "");
      selenium.mouseUpAt("//div[@title='Show Properties']//img", "");
      
     assertTrue(selenium.isElementPresent("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/"));
     
//     selenium.mouseDownAt("scLocator=//TabSet[ID=\"ideOperationFormTabSet\"]/tab[ID=Properties]","");
//     selenium.mouseUpAt("scLocator=//TabSet[ID=\"ideOperationFormTabSet\"]/tab[ID=Properties]", "");
     
     
     assertEquals("false", selenium.getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextAutoload||title=%3Cb%3EAutoload%3C%24fs%24b%3E||value=false||index=0||Class=StaticTextItem]/textbox"));
     assertEquals("exo:groovyResourceContainer", selenium.getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextContentNodeType||title=%3Cb%3EContent%20Node%20Type%3C%24fs%24b%3E||value=exo%3AgroovyResourceContainer||index=2||Class=StaticTextItem]/textbox"));
     assertEquals("application/x-jaxrs-groovy", selenium.getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextContentType||title=%3Cb%3EContent%20Type%3C%24fs%24b%3E||value=application%24fs%24x-jaxrs-groovy||index=3||Class=StaticTextItem]/textbox"));
     assertEquals(FIRST_NAME, selenium.getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextDisplayName||title=%3Cb%3EDisplay%20Name%3C%24fs%24b%3E||value=новий.groove||index=5||Class=StaticTextItem]/textbox"));
     assertEquals("nt:file", selenium.getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextFileNodeType||title=%3Cb%3EFile%20Node%20Type%3C%24fs%24b%3E||value=nt%3Afile||index=6||Class=StaticTextItem]/textbox"));
     
     selenium.click("scLocator=//TabSet[ID=\"ideOperationFormTabSet\"]/tab[ID=Properties]/icon");
     Thread.sleep(500);
     
     assertFalse(selenium.isElementPresent("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/"));
     
     selenium.mouseDownAt("//div[@title='Show Properties']//img", "");
     selenium.mouseUpAt("//div[@title='Show Properties']//img", "");
     Thread.sleep(500);
     
     assertTrue(selenium.isElementPresent("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/"));
     
     saveAsUsingToolbarButton(SECOND_NAME);
     Thread.sleep(1000);
     
     assertEquals("false", selenium.getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextAutoload||title=%3Cb%3EAutoload%3C%24fs%24b%3E||value=false||index=0||Class=StaticTextItem]/textbox"));
     assertEquals("exo:groovyResourceContainer", selenium.getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextContentNodeType||title=%3Cb%3EContent%20Node%20Type%3C%24fs%24b%3E||value=exo%3AgroovyResourceContainer||index=2||Class=StaticTextItem]/textbox"));
     assertEquals("application/x-jaxrs-groovy", selenium.getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextContentType||title=%3Cb%3EContent%20Type%3C%24fs%24b%3E||value=application%24fs%24x-jaxrs-groovy||index=3||Class=StaticTextItem]/textbox"));
     assertEquals(SECOND_NAME, selenium.getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextDisplayName||title=%3Cb%3EDisplay%20Name%3C%24fs%24b%3E||value=новий.groove||index=5||Class=StaticTextItem]/textbox"));
     assertEquals("nt:file", selenium.getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextFileNodeType||title=%3Cb%3EFile%20Node%20Type%3C%24fs%24b%3E||value=nt%3Afile||index=6||Class=StaticTextItem]/textbox"));
     
     closeTab("0");
     selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/noButton");
     
     selectItemInWorkspaceTree(FIRST_NAME);
     Thread.sleep(1000);
     deleteSelectedFileOrFolder();
     Thread.sleep(1000);
     
     selectItemInWorkspaceTree(SECOND_NAME);
     Thread.sleep(1000);
     deleteSelectedFileOrFolder();
     
   }
}
