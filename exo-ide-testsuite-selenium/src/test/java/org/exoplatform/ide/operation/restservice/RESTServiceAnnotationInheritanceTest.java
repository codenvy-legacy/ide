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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.Utils;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */

// IDE-26 
public class RESTServiceAnnotationInheritanceTest extends BaseTest
{
   private final static String FILE_NAME = "AnnotationInheritance.groovy";

   private final static String URL = BASE_URL + REST_CONTEXT + "/jcr/" + REPO_NAME + "/" + WS_NAME + "/" + FILE_NAME;
   
   @BeforeClass
   public static void setUp()
   {
      
      String filePath ="src/test/resources/org/exoplatform/ide/operation/restservice/AnnotationInheritance.groovy";
      try
      {
         VirtualFileSystemUtils.put(filePath, MimeType.GROOVY_SERVICE,"exo:groovyResourceContainer", URL);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      catch (ModuleException e)
      {
         e.printStackTrace();
      }
   }
   
   
   @Test
   public void testAnnotationInheritance() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      selectItemInWorkspaceTree(WS_NAME);
      runTopMenuCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      Thread.sleep(TestConstants.SLEEP);
      openFileFromNavigationTreeWithCodeEditor(FILE_NAME, false);
      Thread.sleep(TestConstants.SLEEP);
      runTopMenuCommand(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_REST_SERVICE);
      Thread.sleep(TestConstants.SLEEP);

      //Call the "Run->Launch REST Service" topmenu command
      launchRestService();

      assertEquals("/testAnnotationInheritance/InnerPath/{pathParam}", selenium
         .getValue("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServicePath]/element"));

      assertParameters();

      // is enabled Body tab
      assertFalse(selenium.isElementPresent("//td[@class='tabTitleSelectedDisabled']"));

      selenium
         .click("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServicePath||title=ideGroovyServicePath]/[icon='picker']");
      Thread.sleep(TestConstants.SLEEP);

      assertTrue(selenium.isElementPresent("//nobr[contains(text(), '/testAnnotationInheritance')]"));
      assertTrue(selenium
         .isElementPresent("//nobr[contains(text(), '/testAnnotationInheritance/InnerPath/{pathParam}')]"));

      selenium.click("//nobr[contains(text(), '/testAnnotationInheritance/InnerPath/{pathParam}')]");

      selenium.type("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServicePath]/element",
         "/testAnnotationInheritance/InnerPath/тест");
      Thread.sleep(TestConstants.SLEEP);

      assertParameters();

      selenium.click("scLocator=//IButton[ID=\"ideGroovyServiceSend\"]");
      Thread.sleep(TestConstants.SLEEP_SHORT);  
      String mess = selenium.getText("//div[contains(@eventproxy,'Record_1')]");

      assertTrue(mess.contains("PathParam:тест"));
      
   }

   /**
    * Check parameters
    */
   private void assertParameters()
   {
      assertEquals("POST", selenium
         .getText("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServiceMethod]/textbox"));

      assertEquals("text/plain", selenium
         .getText("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServiceRequest]/textbox"));

      assertEquals("text/html", selenium
         .getText("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServiceResponse]/textbox"));

      assertEquals("No items to show.", selenium
         .getText("scLocator=//ListGrid[ID=\"ideGroovyServiceQueryTable\"]/body"));

      selenium.click("scLocator=//TabSet[ID=\"ideGroovyServiceTabSet\"]/tab[ID=ideGroovyServiceHeaderTab]/");
      assertEquals("No items to show.", selenium
         .getText("scLocator=//ListGrid[ID=\"ideGroovyServiceHeaderTable\"]/body"));

      selenium.click("scLocator=//TabSet[ID=\"ideGroovyServiceTabSet\"]/tab[ID=ideGroovyServiceQueryTab]/");
   }
   
   @AfterClass
   public static void tearDown()
   {
      try
      {
         Utils.undeployService(BASE_URL, REST_CONTEXT, URL);
         VirtualFileSystemUtils.delete(URL);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      catch (ModuleException e)
      {
         e.printStackTrace();
      }
   }
}
