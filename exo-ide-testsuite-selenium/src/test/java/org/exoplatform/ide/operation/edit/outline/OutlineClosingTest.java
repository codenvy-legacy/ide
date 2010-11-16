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
package org.exoplatform.ide.operation.edit.outline;

import static org.junit.Assert.*;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 16, 2010 $
 *
 */
//http://jira.exoplatform.org/browse/IDE-417
public class OutlineClosingTest extends BaseTest 
{
   @Test
   public void test() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      runCommandFromMenuNewOnToolbar(MenuCommands.New.REST_SERVICE_FILE);
      
      openAndCloseOutline();
      
      openAndCloseOutline();
      
      openAndCloseOutline();
   }

   /**
    * @throws Exception
    * @throws InterruptedException
    */
   private void openAndCloseOutline() throws Exception, InterruptedException
   {
      // open outline panel
      runToolbarButton(ToolbarCommands.View.SHOW_OUTLINE);
      Thread.sleep(TestConstants.SLEEP);
      
      // check for presence of tab outline
      assertTrue(selenium.isVisible("scLocator=//TabSet[ID=\"ideCodeHelperTabSet\"]"));
      assertEquals("Outline", selenium.getText("scLocator=//TabSet[ID=\"ideCodeHelperTabSet\"]/tab[index=0]/title"));
      
      selenium.click("scLocator=//TabSet[ID=\"ideCodeHelperTabSet\"]/tab[ID=ideOutlineForm]/icon");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      
      assertFalse(selenium.isVisible("scLocator=//TabSet[ID=\"ideCodeHelperTabSet\"]"));
   }
   
}
