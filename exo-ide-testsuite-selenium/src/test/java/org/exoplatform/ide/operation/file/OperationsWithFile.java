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

import static org.junit.Assert.*;

import org.exoplatform.ide.BaseTest;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class OperationsWithFile extends BaseTest
{
   
   private final static String FOLDER_NAME = "Test";
   
   private final static String FILE_NAME = "RepoFile.xml";
   
   //IDE-13:Saving previously edited file.
   @Test
   public void savePreviouslyEditedFile() throws Exception
   {
      createFolder(FOLDER_NAME);
      
      Thread.sleep(1000);
      
      assertElementPresentInWorkspaceTree(FOLDER_NAME);
      
      openNewFileFromToolbar("XML File");
      Thread.sleep(1000);
      
      //is file opened
      assertTrue(selenium.isTextPresent("Untitled file.xml *"));
      
      saveAsFile(FILE_NAME);
      
      Thread.sleep(1000);
      
      //is file saved
      assertFalse(selenium.isTextPresent("Untitled file.xml *"));
      assertTrue(selenium.isTextPresent(FILE_NAME));
      
      assertElementPresentInWorkspaceTree(FILE_NAME);
      
      //close Test folder
      selenium.click("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row[name=" 
         + FOLDER_NAME + "]/col[0]/open");
      Thread.sleep(1000);
      //checks, then new file created in Test folder.
      //When Test folder is closed, RepoFile.xml doesn't shown in Workspace tree
      assertFalse(selenium.isElementPresent("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row[name=" 
         + FILE_NAME + "]/col[0]"));
      
      //open Test folder
      selenium.click("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row[name=" 
         + FOLDER_NAME + "]/col[0]/open");
      
      Thread.sleep(1000);
      
      checkFileOnWebDav();
      
      Thread.sleep(1000);
      
      changeFilecontent();
      
      saveCurrentFile();
      
      selenium.refresh();
      
      selenium.waitForPageToLoad("20000");
      
      //open Test folder
      openOrCloseFolder(FOLDER_NAME);
      
      Thread.sleep(1000);
      
      assertElementPresentInWorkspaceTree(FILE_NAME);
   }
   
   private void changeFilecontent() throws Exception
   {
      //change file content
      selenium.keyDown("//body[@class='editbox']/", "\\35");
      selenium.keyDown("//body[@class='editbox']/", "\\13");
      selenium.keyUp("//body[@class='editbox']/", "\\13");
//      for (int i = 0; i < 44; i++)
//      {
//         selenium.keyPress("//body[@class='editbox']/", "\\46");
//      }
      Thread.sleep(100);
      selenium.typeKeys("//body[@class='editbox']/", "<test>");
      selenium.keyDown("//body[@class='editbox']/", "\\13");
      selenium.keyUp("//body[@class='editbox']/", "\\13");
      selenium.typeKeys("//body[@class='editbox']/", "<war>");
      selenium.typeKeys("//body[@class='editbox']/", "param");
      selenium.typeKeys("//body[@class='editbox']/", "</war>");
      selenium.keyDown("//body[@class='editbox']/", "\\13");
      selenium.keyUp("//body[@class='editbox']/", "\\13");
      selenium.typeKeys("//body[@class='editbox']/", "<bean>");
      selenium.keyDown("//body[@class='editbox']/", "\\13");
      selenium.keyUp("//body[@class='editbox']/", "\\13");
      selenium.typeKeys("//body[@class='editbox']/", "<name>");
      selenium.typeKeys("//body[@class='editbox']/", "MineBean");
      selenium.typeKeys("//body[@class='editbox']/", "</name>");
      selenium.keyDown("//body[@class='editbox']/", "\\13");
      selenium.keyUp("//body[@class='editbox']/", "\\13");
      selenium.typeKeys("//body[@class='editbox']/", "</bean>");
      selenium.keyDown("//body[@class='editbox']/", "\\13");
      selenium.keyUp("//body[@class='editbox']/", "\\13");
      selenium.typeKeys("//body[@class='editbox']/", "</test>");
      Thread.sleep(1000);
   }
   
   private void checkFileOnWebDav() throws Exception
   {
      selenium.openWindow("http://127.0.0.1:8888/rest/private/jcr/repository/dev-monit/", "WEBDAV Browser");
      selenium.waitForPopUp("WEBDAV Browser", "10000");
      selenium.selectPopUp("WEBDAV Browser");
      
      assertTrue(selenium.isElementPresent("link=" + FOLDER_NAME));
      
      selenium.click("link=" + FOLDER_NAME);
      
      Thread.sleep(1000);
      
      assertTrue(selenium.isElementPresent("link=" + FILE_NAME));
      
      selenium.getEval("selenium.browserbot.getCurrentWindow().close()");
      selenium.selectWindow("IDEall");
   }
     

}
