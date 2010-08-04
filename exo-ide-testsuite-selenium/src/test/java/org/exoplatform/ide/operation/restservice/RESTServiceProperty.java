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

import static org.junit.Assert.assertEquals;

import org.exoplatform.ide.BaseTest;
import org.junit.Test;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class RESTServiceProperty extends BaseTest
{
   
   private static String FILE_NAME = "Example.groovy";
   
   @Test
   public void testProperty() throws Exception
   {
      selenium.refresh();
      selenium.waitForPageToLoad("30000");
      Thread.sleep(1000);
      openNewFileFromToolbar("REST Service");
      Thread.sleep(1000);
      
      checkMenuCommandState("View", "Properties", false);
      checkMenuCommandState("Run", "Set Autoload", false);
      checkMenuCommandState("Run", "Validate", true);
      checkMenuCommandState("Run", "Deploy", false);
      checkMenuCommandState("Run", "Undeploy", false);
      checkMenuCommandState("Run", "Launch REST Service...", false);
      
      saveAsUsingToolbarButton(FILE_NAME);
      Thread.sleep(1000);
      
      checkMenuCommandState("View", "Properties", true);
      checkMenuCommandState("Run", "Set Autoload", true);
      checkMenuCommandState("Run", "Validate", true);
      checkMenuCommandState("Run", "Deploy", true);
      checkMenuCommandState("Run", "Undeploy", true);
      checkMenuCommandState("Run", "Launch REST Service...", true);
      
      selenium.mouseDownAt("//div[@title='Show Properties']//img", "");
      selenium.mouseUpAt("//div[@title='Show Properties']//img", "");
      Thread.sleep(500);
      
      assertEquals("false", selenium.getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextAutoload||title=%3Cb%3EAutoload%3C%24fs%24b%3E||value=false||index=0||Class=StaticTextItem]/textbox"));
      assertEquals("exo:groovyResourceContainer", selenium.getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextContentNodeType||title=%3Cb%3EContent%20Node%20Type%3C%24fs%24b%3E||value=exo%3AgroovyResourceContainer||index=2||Class=StaticTextItem]/textbox"));
      assertEquals("application/x-jaxrs-groovy", selenium.getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextContentType||title=%3Cb%3EContent%20Type%3C%24fs%24b%3E||value=application%24fs%24x-jaxrs-groovy||index=3||Class=StaticTextItem]/textbox"));
      assertEquals(FILE_NAME, selenium.getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextDisplayName||title=%3Cb%3EDisplay%20Name%3C%24fs%24b%3E||value=новий.groove||index=5||Class=StaticTextItem]/textbox"));
      assertEquals("nt:file", selenium.getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextFileNodeType||title=%3Cb%3EFile%20Node%20Type%3C%24fs%24b%3E||value=nt%3Afile||index=6||Class=StaticTextItem]/textbox"));
      
      closeTab("0");
      
      selectItemInWorkspaceTree(FILE_NAME);
      
      deleteSelectedItem();
      
   }
   
}
