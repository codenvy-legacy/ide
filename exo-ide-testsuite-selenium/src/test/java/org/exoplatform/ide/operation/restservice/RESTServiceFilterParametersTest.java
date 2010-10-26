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
public class RESTServiceFilterParametersTest extends BaseTest
{

   private final static String FILE_NAME = "FilterParametersTest.groovy";

   //TODO*************change*********
   private final static String TEST_FOLDER = "GroovyFolder";

   //***************************** 
   private final static String URL = BASE_URL + REST_CONTEXT + "/jcr/" + REPO_NAME + "/" + WS_NAME + "/" + TEST_FOLDER
      + "/";

   @BeforeClass
   public static void setUp()
   {

      String filePath = "src/test/resources/org/exoplatform/ide/operation/restservice/DefaultValues.groovy";
      try
      {
         //TODO*************change*********
         VirtualFileSystemUtils.mkcol(URL);
         //*************************  
         VirtualFileSystemUtils.put(filePath, MimeType.GROOVY_SERVICE,
            TestConstants.NodeTypes.EXO_GROOVY_RESOURCE_CONTAINER, URL + FILE_NAME);
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
   }

   @Test
   public void testFilterParameters() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      selectItemInWorkspaceTree(WS_NAME);
      runTopMenuCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);

      //TODO*************change
      Thread.sleep(TestConstants.SLEEP);
      openOrCloseFolder(TEST_FOLDER);
      Thread.sleep(TestConstants.SLEEP);
      //****************************

      //TODO*************change
      openFileFromNavigationTreeWithCodeEditor(FILE_NAME, false);
      Thread.sleep(TestConstants.SLEEP);
      launchRestService();
      //************************

      selenium.click("scLocator=//TabSet[ID=\"ideGroovyServiceTabSet\"]/tab[ID=ideGroovyServiceHeaderTab]/");
      selenium.click("scLocator=//ListGrid[ID=\"ideGroovyServiceHeaderTable\"]/body/row[0]/col[fieldName=value||4]");
      selenium.keyPress("scLocator=//ListGrid[ID=\"ideGroovyServiceHeaderTable\"]/body/row[0]/col[fieldName=value||4]",
         "\\13");
      selenium
         .click("scLocator=//ListGrid[ID=\"ideGroovyServiceHeaderTable\"]/editRowForm/item[name=send||title=send||value=true||index=0||Class=CheckboxItem]/textbox");
      selenium.click("scLocator=//IButton[ID=\"ideGroovyServiceSend\"]/");

      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideGroovyServiceOutputPreviewForm\"]"));

      Thread.sleep(TestConstants.SLEEP);
      String mess = selenium.getText("//div[contains(@eventproxy,'Record_0')]");
      assertTrue(mess
         .contains("POST PathParam: {pathParam}; POST Test-Header: 3; POST TestQueryParam: false; POST Body:"));
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
