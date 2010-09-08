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
package org.exoplatform.ide.operation.file;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * IDE-48: Opening and Saving new XML file with non-latin name.
 * 
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
//IDE-48: Opening and Saving new XML file with non-latin name
public class OpenAndSaveXmlFileWithNonLatinNameTest extends BaseTest
{
   private static final String FILE_NAME = System.currentTimeMillis() + "ТестовыйФайл.xml";
   
    
    private static String XML_CONTENT = "<?xml version='1.0' encoding='UTF-8'?>\n" +
                                         "<test>\n" +  
                                        "<settings>value</settings>\n" +
                                        "</test>";
    
    
    private static String XML_CONTENT_2 =  "<?xml version='1.0' encoding='UTF-8'?>\n" +
    "<test>\n"+
     "<settings>param</settings>\n" +
    "<bean>\n" +
    "<name>MineBean</name>\n" +
    "</bean>\n" +
     "</test>";
      
   
    
    @AfterClass
    public static void tearDown()
    {
       try
       {
          VirtualFileSystemUtils.delete(BASE_URL + REST_CONTEXT + "/jcr/" + REPO_NAME + "/" + WS_NAME + "/" + FILE_NAME);
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
   public void testOpenAndSaveXmlFileWithNonLatinName() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      selectItemInWorkspaceTree(WS_NAME);
      createFileFromToolbar(MenuCommands.New.XML_FILE);
      Thread.sleep(TestConstants.SLEEP);
      assertTrue(selenium.isTextPresent("Untitled file.xml"));
      assertTrue(selenium.isElementPresent("//div[@title='Save']/div[@elementenabled='false']"));
      assertTrue(selenium.isElementPresent("//div[@title='Save As...']/div[@elementenabled='true']"));
      Thread.sleep(TestConstants.SLEEP_SHORT);
      deleteFileContent();
      typeTextIntoEditor(0, XML_CONTENT);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      saveAsUsingToolbarButton(FILE_NAME);
      Thread.sleep(TestConstants.SLEEP);
      closeTab("0");
      Thread.sleep(TestConstants.SLEEP);
      openFileFromNavigationTreeWithCodeEditor(FILE_NAME, false);
      Thread.sleep(TestConstants.SLEEP);
      deleteFileContent();
      typeTextIntoEditor(0, XML_CONTENT_2);
      Thread.sleep(TestConstants.SLEEP);
     
      //Save command enabled
      assertTrue(selenium.isElementPresent("//div[@title='Save']/div[@elementenabled='true']"));
      //File name ends with *
      assertTrue(selenium.isTextPresent(FILE_NAME + " *"));
      
      saveCurrentFile();
      Thread.sleep(TestConstants.SLEEP);
      
      //File name doesn't end with *
      assertFalse(selenium.isTextPresent(FILE_NAME + " *"));
      assertTrue(selenium.isTextPresent(FILE_NAME));
      
      //Save command disabled
      assertTrue(selenium.isElementPresent("//div[@title='Save']/div[@elementenabled='false']"));
      
      selectItemInWorkspaceTree(FILE_NAME);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      deleteSelectedItems();
      Thread.sleep(TestConstants.SLEEP);
      
      //is file deleted
      assertFalse(selenium.isElementPresent("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row[name=" 
         + FILE_NAME + "]/col[1]"));
   }
   

}
