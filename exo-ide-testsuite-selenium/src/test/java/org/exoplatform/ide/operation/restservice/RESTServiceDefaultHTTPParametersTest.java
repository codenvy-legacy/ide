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
public class RESTServiceDefaultHTTPParametersTest extends BaseTest
{

   private final static String FILE_NAME = RESTServiceDefaultHTTPParametersTest.class.getSimpleName();

   private final static String TEST_FOLDER = "DefaultHTTPParameters";

   private final static String URL = BASE_URL +  REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/" + TEST_FOLDER
      + "/";

   @BeforeClass
   public static void setUp()
   {

      String filePath = "src/test/resources/org/exoplatform/ide/operation/restservice/DefaultHTTPParameters.groovy";
      try
      {
         //**************TODO***********change add folder for locked file
         VirtualFileSystemUtils.mkcol(URL);
         //***********************************************************

         VirtualFileSystemUtils.put(filePath, MimeType.GROOVY_SERVICE, URL + FILE_NAME);
         Thread.sleep(TestConstants.SLEEP_SHORT);
         Utils.deployService(BASE_URL, REST_CONTEXT, URL + FILE_NAME);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      catch (ModuleException e)
      {
         e.printStackTrace();
      }
      catch (InterruptedException e)
      {
         e.printStackTrace();
      }
   }

   @Test
   public void testDefaultHTTPParameters() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
       IDE.navigator().selectItem(WS_URL);
      IDE.menu().runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      Thread.sleep(TestConstants.SLEEP);
      IDE.navigator().clickOpenIconOfFolder(URL);
      IDE.navigator().openFileFromNavigationTreeWithCodeEditor(FILE_NAME, false);
      Thread.sleep(TestConstants.SLEEP);
      launchRestService();
      checkParam();
      selenium
         .click("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServiceMethod]/[icon='picker']");
      Thread.sleep(TestConstants.SLEEP);

      selenium.click("//nobr[contains(text(), 'GET')]");

      checkParam();

      selenium.click("scLocator=//IButton[ID=\"ideGroovyServiceCancel\"]/");

   }

   /**
    *  Check Request parameters
    */
   private void checkParam()
   {
      assertEquals("TestQueryParam 1",
         selenium.getText("scLocator=//ListGrid[ID=\"ideGroovyServiceQueryTable\"]/body/row[0]/col[1]"));

      assertEquals("boolean",
         selenium.getText("scLocator=//ListGrid[ID=\"ideGroovyServiceQueryTable\"]/body/row[0]/col[2]"));

      assertEquals("true",
         selenium.getText("scLocator=//ListGrid[ID=\"ideGroovyServiceQueryTable\"]/body/row[0]/col[3]"));

      assertEquals("", selenium.getText("scLocator=//ListGrid[ID=\"ideGroovyServiceQueryTable\"]/body/row[0]/col[4]"));

      selenium.click("scLocator=//TabSet[ID=\"ideGroovyServiceTabSet\"]/tab[ID=ideGroovyServiceHeaderTab]/");

      assertEquals("Test-Header",
         selenium.getText("scLocator=//ListGrid[ID=\"ideGroovyServiceHeaderTable\"]/body/row[0]/col[1]"));

      assertEquals("integer",
         selenium.getText("scLocator=//ListGrid[ID=\"ideGroovyServiceHeaderTable\"]/body/row[0]/col[2]"));

      assertEquals("3", selenium.getText("scLocator=//ListGrid[ID=\"ideGroovyServiceHeaderTable\"]/body/row[0]/col[3]"));

      assertEquals("", selenium.getText("scLocator=//ListGrid[ID=\"ideGroovyServiceHeaderTable\"]/body/row[0]/col[4]"));

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
