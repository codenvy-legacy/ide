/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.core;

import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.Utils;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class Workspace extends AbstractTestModule
{
   
   static final String TREE_PREFIX_ID = "navigation-";   
   
   /**
    * Select item in workspace tree
    * @param itemHref Href of item
    * <h1>Folder href MUST ends with "/"</h1>
    */
   public void selectItem(String itemHref) throws Exception
   {
      selenium().clickAt(getItemId(itemHref), "0");
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
   }
   
   /**
    * Selects root item in Workspace tree.
    * 
    * @throws Exception
    */
   public void selectRootItem() throws Exception {
      selectItem(IDE().getWorkspaceURL());
      Thread.sleep(TestConstants.ANIMATION_PERIOD);      
   }
   
   /**
    * Generate item id 
    * @param href of item 
    * @return id of item
    */
   public String getItemId(String href) throws Exception
   {
      return TREE_PREFIX_ID + Utils.md5(href);
   }

}
