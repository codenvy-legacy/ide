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
    *  IDE-2 Exploring "Workspace" panel
    * 
    * @throws Exception
    */
   @Test
   public void testExplodeCollapseFolder() throws Exception
   {
      Thread.sleep(1000);
      createFolder("folder-1");
      createFolder("folder-1-1");
      selectItemInWorkspaceTree("folder-1");
      createFolder("folder-1-2");

      assertElementPresentInWorkspaceTree("folder-1");
      assertElementPresentInWorkspaceTree("folder-1-1");
      assertElementPresentInWorkspaceTree("folder-1-2");

      selectRootOfWorkspaceTree();
      createFolder("folder-2");
      createFolder("folder-2-1");
      selectItemInWorkspaceTree("folder-2");
      createFolder("folder-2-2");
      //Sub folders of folder "folder-1" are hidden
      assertElementPresentInWorkspaceTree("folder-1");
      assertElementNotPresentInWorkspaceTree("folder-1-1");
      assertElementNotPresentInWorkspaceTree("folder-1-2");

      assertElementPresentInWorkspaceTree("folder-2");
      assertElementPresentInWorkspaceTree("folder-2-1");
      assertElementPresentInWorkspaceTree("folder-2-2");

      //Close folder "folder-2"
      openOrCloseFolder("folder-2");
      Thread.sleep(1000);
      assertElementPresentInWorkspaceTree("folder-1");
      assertElementPresentInWorkspaceTree("folder-2");
      //Sub folders of folder "folder-2" are hidden 
      assertElementNotPresentInWorkspaceTree("folder-2-1");
      assertElementNotPresentInWorkspaceTree("folder-2-2");
      
      //Open "folder-1"
      openOrCloseFolder("folder-1");
      Thread.sleep(1000);
      assertElementPresentInWorkspaceTree("folder-1");
      assertElementPresentInWorkspaceTree("folder-1-1");
      assertElementPresentInWorkspaceTree("folder-1-2");
      assertElementPresentInWorkspaceTree("folder-2");
      //Sub folders of folder "folder-2" are hidden 
      assertElementNotPresentInWorkspaceTree("folder-2-1");
      assertElementNotPresentInWorkspaceTree("folder-2-2");
      Thread.sleep(5000);

      //Close workspace item
      openCloseRootWorkspace();
      Thread.sleep(1000);
      //All sub folders hide
      assertElementNotPresentInWorkspaceTree("folder-2");
      assertElementNotPresentInWorkspaceTree("folder-2-2");
      assertElementNotPresentInWorkspaceTree("folder-2-1");
      assertElementNotPresentInWorkspaceTree("folder-1");
      assertElementNotPresentInWorkspaceTree("folder-1-1");
      assertElementNotPresentInWorkspaceTree("folder-1-2");

      //Open workspace item
      openCloseRootWorkspace();
      Thread.sleep(1000);
      assertElementPresentInWorkspaceTree("folder-1");
      assertElementNotPresentInWorkspaceTree("folder-1-1");
      assertElementNotPresentInWorkspaceTree("folder-1-2");
      assertElementPresentInWorkspaceTree("folder-2");
      assertElementNotPresentInWorkspaceTree("folder-2-1");
      assertElementNotPresentInWorkspaceTree("folder-2-2");
      
      selectItemInWorkspaceTree("folder-1");
      deleteSelectedItem();
      selectItemInWorkspaceTree("folder-2");
      deleteSelectedItem();
   }
}
