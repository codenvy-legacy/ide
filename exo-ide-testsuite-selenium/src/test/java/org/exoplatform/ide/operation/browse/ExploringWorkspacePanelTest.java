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
package org.exoplatform.ide.operation.browse;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.TestConstants;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class ExploringWorkspacePanelTest extends BaseTest
{
   /**
    * 
    */
   private static final String FOLDER_2_2 = "folder-2-2";
   /**
    * 
    */
   private static final String FOLDER_2_1 = "folder-2-1";
   /**
    * 
    */
   private static final String FOLDER_2 = "folder-2";
   /**
    * 
    */
   private static final String FOLDER_1_2 = "folder-1-2";
   /**
    * 
    */
   private static final String FOLDER_1_1 = "folder-1-1";
   /**
    * 
    */
   private static final String FOLDER_1 = "folder-1";
   
   private static final String FOLDER_1_URL = WS_URL +  "folder-1" + "/";
 
   private static final String FOLDER_1_2_URL = FOLDER_1_URL +  "folder-1-2" + "/";
   
   private static final String FOLDER_1_1_URL = FOLDER_1_URL + "folder-1-1" + "/";
   
   private static final String FOLDER_2_URL =  WS_URL + "folder-2" + "/";
   
   private static final String FOLDER_2_2_URL = FOLDER_2_URL +  "folder-2-2" + "/";
   
   private static final String FOLDER_2_1_URL = FOLDER_2_URL + "folder-2-1" + "/";

   /**
    *  IDE-2 Exploring "Workspace" panel
    * 
    * @throws Exception
    */
   @Test
   public void testExplodeCollapseFolder() throws Exception
   {
      waitForRootElement();
      createFolder(FOLDER_1);
      createFolder(FOLDER_1_1);
      IDE.NAVIGATION.selectItem(FOLDER_1_URL);
      createFolder(FOLDER_1_2);

      IDE.NAVIGATION.assertItemPresent(FOLDER_1_URL);
      IDE.NAVIGATION.assertItemPresent(FOLDER_1_1_URL);
      IDE.NAVIGATION.assertItemPresent(FOLDER_1_2_URL);

      IDE.NAVIGATION.selectRootOfWorkspace();
      createFolder(FOLDER_2);
      createFolder(FOLDER_2_1);
      IDE.NAVIGATION.selectItem(FOLDER_2_URL);
      createFolder(FOLDER_2_2);
      //Sub folders of folder "folder-1" are hidden
      IDE.NAVIGATION.assertItemPresent(FOLDER_1_URL);
      IDE.NAVIGATION.assertItemNotPresent(FOLDER_1_1_URL);
      IDE.NAVIGATION.assertItemNotPresent(FOLDER_1_2_URL);

      IDE.NAVIGATION.assertItemPresent(FOLDER_2_URL);
      IDE.NAVIGATION.assertItemPresent(FOLDER_2_1_URL);
      IDE.NAVIGATION.assertItemPresent(FOLDER_2_2_URL);

      //Close folder "folder-2"
      IDE.NAVIGATION.clickOpenIconOfFolder(FOLDER_2_URL);
      waitForRootElement();
      IDE.NAVIGATION.assertItemPresent(FOLDER_1_URL);
      IDE.NAVIGATION.assertItemPresent(FOLDER_2_URL);
      //Sub folders of folder "folder-2" are hidden
      IDE.NAVIGATION.assertItemNotPresent(FOLDER_2_1_URL);
      IDE.NAVIGATION.assertItemNotPresent(FOLDER_2_2_URL);
      
      //Open "folder-1"
      IDE.NAVIGATION.clickOpenIconOfFolder(FOLDER_1_URL);
      waitForRootElement();
      IDE.NAVIGATION.assertItemPresent(FOLDER_1_URL);
      IDE.NAVIGATION.assertItemPresent(FOLDER_1_1_URL);
      IDE.NAVIGATION.assertItemPresent(FOLDER_1_2_URL);
      IDE.NAVIGATION.assertItemPresent(FOLDER_2_URL);
      //Sub folders of folder "folder-2" are hidden 
      IDE.NAVIGATION.assertItemNotPresent(FOLDER_2_1_URL);
      IDE.NAVIGATION.assertItemNotPresent(FOLDER_2_2_URL);
      waitForRootElement();

      //Close workspace item
      IDE.NAVIGATION.clickOpenIconOfFolder(WS_URL);
      waitForRootElement();
      //All sub folders hide
      IDE.NAVIGATION.assertItemNotPresent(FOLDER_1_URL);
      IDE.NAVIGATION.assertItemNotPresent(FOLDER_1_1_URL);
      IDE.NAVIGATION.assertItemNotPresent(FOLDER_1_2_URL);
      IDE.NAVIGATION.assertItemNotPresent(FOLDER_2_URL);
      IDE.NAVIGATION.assertItemNotPresent(FOLDER_2_1_URL);
      IDE.NAVIGATION.assertItemNotPresent(FOLDER_2_2_URL);

      //Open workspace item
      IDE.NAVIGATION.clickOpenIconOfFolder(WS_URL);
      waitForRootElement();
      IDE.NAVIGATION.assertItemPresent(FOLDER_1_URL);
      IDE.NAVIGATION.assertItemNotPresent(FOLDER_1_1_URL);
      IDE.NAVIGATION.assertItemNotPresent(FOLDER_1_2_URL);
      IDE.NAVIGATION.assertItemPresent(FOLDER_2_URL);
      IDE.NAVIGATION.assertItemNotPresent(FOLDER_2_1_URL);
      IDE.NAVIGATION.assertItemNotPresent(FOLDER_2_2_URL);
      
      IDE.NAVIGATION.selectItem(FOLDER_1_URL);
      IDE.NAVIGATION.deleteSelectedItems();
      IDE.NAVIGATION.selectItem(FOLDER_2_URL);
      IDE.NAVIGATION.deleteSelectedItems();
   }
}
