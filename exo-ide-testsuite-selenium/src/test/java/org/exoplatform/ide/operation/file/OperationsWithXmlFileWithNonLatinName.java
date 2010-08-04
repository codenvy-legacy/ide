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

import org.exoplatform.ide.BaseTest;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class OperationsWithXmlFileWithNonLatinName extends BaseTest
{
   private static final String FILE_NAME = "ׂוסעמגûיװאיכ.xml";
      
   //IDE-49
   @Ignore
   @Test
   public void delete() throws Exception
   {
      //TODO:
      //change creating of file to this steps:
      //Preparing to test: create ׂוסעמגûיװאיכ.xml on your computer (the file must contains valid xml data).
      //Go to server window and copy ׂוסעמגûיװאיכ.xml from your computer to root folder.
      //Go to gadget window, select root folder in "Workspace" panel and click on "File->Refresh" top menu command.
      
      createFile();
      
      Thread.sleep(1000);
      
      saveAsUsingToolbarButton(FILE_NAME);
      
      Thread.sleep(1000);
      closeTab("0");
      
      Thread.sleep(1000);
      selectItemInWorkspaceTree(FILE_NAME);
      deleteSelectedItem();
      Thread.sleep(2000);
      assertFalse(selenium.isElementPresent("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row[name=" 
         + FILE_NAME + "]/col[1]"));
   }
   
   //IDE-48
   @Ignore
   @Test
   public void openAndSave() throws Exception
   {
      createFile();

      Thread.sleep(1000);

      saveAsUsingToolbarButton(FILE_NAME);

      Thread.sleep(1000);
      
      closeTab("0");
      Thread.sleep(1000);
      
      openFileWithCodeEditor(FILE_NAME);
      
      Thread.sleep(1000);
      
      //change file content
      selenium.keyDown("//body[@class='editbox']/", "\\35");
      selenium.keyDown("//body[@class='editbox']/", "\\13");
      selenium.keyUp("//body[@class='editbox']/", "\\13");
      for (int i = 0; i < 44; i++)
      {
         selenium.keyPress("//body[@class='editbox']/", "\\46");
      }
      Thread.sleep(100);
      selenium.typeKeys("//body[@class='editbox']/", "<test>");
      selenium.keyDown("//body[@class='editbox']/", "\\13");
      selenium.keyUp("//body[@class='editbox']/", "\\13");
      selenium.typeKeys("//body[@class='editbox']/", "<settings>");
      selenium.typeKeys("//body[@class='editbox']/", "param");
      selenium.typeKeys("//body[@class='editbox']/", "</settings>");
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
      
      //Save command enabled
      assertTrue(selenium.isElementPresent("//div[@title='Save']/div[@elementenabled='true']"));
      //File name ends with *
      assertTrue(selenium.isTextPresent(FILE_NAME + " *"));
      
      saveCurrentFile();
      Thread.sleep(1000);
      
      //File name doesn't end with *
      assertFalse(selenium.isTextPresent(FILE_NAME + " *"));
      assertTrue(selenium.isTextPresent(FILE_NAME));
      
      //Save command disabled
      assertTrue(selenium.isElementPresent("//div[@title='Save']/div[@elementenabled='false']"));
      
      selectItemInWorkspaceTree(FILE_NAME);
      Thread.sleep(500);
      deleteSelectedItem();
      Thread.sleep(1000);
      
      //is file deleted
      assertFalse(selenium.isElementPresent("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row[name=" 
         + FILE_NAME + "]/col[1]"));
   }
   
   private void createFile() throws Exception
   {
      selenium.click("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row[name=dev-monit||0]/col[1]");
      selenium.mouseDownAt("//div[@title='New']//img", "");
      selenium.mouseUpAt("//div[@title='New']//img", "");
      selenium.mouseDownAt("//td[@class=\"exo-popupMenuTitleField\"]//nobr[contains(text(), \"XML File\")]", "");
      assertTrue(selenium.isTextPresent("Untitled file.xml"));
      assertTrue(selenium.isElementPresent("//div[@title='Save']/div[@elementenabled='false']"));
      assertTrue(selenium.isElementPresent("//div[@title='Save As...']/div[@elementenabled='true']"));
      Thread.sleep(500);
      selenium.keyDown("//body[@class='editbox']/", "\\35");
      selenium.keyDown("//body[@class='editbox']/", "\\13");
      selenium.keyUp("//body[@class='editbox']/", "\\13");
      Thread.sleep(100);
      selenium.typeKeys("//body[@class='editbox']/", "<test>");
      selenium.keyDown("//body[@class='editbox']/", "\\13");
      selenium.keyUp("//body[@class='editbox']/", "\\13");
      selenium.typeKeys("//body[@class='editbox']/", "<settings>");
      selenium.typeKeys("//body[@class='editbox']/", "value");
      selenium.typeKeys("//body[@class='editbox']/", "</settings>");
      selenium.keyDown("//body[@class='editbox']/", "\\13");
      selenium.keyUp("//body[@class='editbox']/", "\\13");
      selenium.typeKeys("//body[@class='editbox']/", "</test>");
   }

}
