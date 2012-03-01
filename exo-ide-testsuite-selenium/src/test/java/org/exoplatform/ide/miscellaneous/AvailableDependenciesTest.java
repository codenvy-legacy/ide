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
package org.exoplatform.ide.miscellaneous;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class AvailableDependenciesTest extends BaseTest
{

   @Test
   public void testAvailableDependencies() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();

      IDE.MENU.runCommand(MenuCommands.Help.HELP, MenuCommands.Help.AVAILABLE_DEPENDENCIES);
      IDE.AVAILABLE_DEPENDENCIES.waitOpened();
      IDE.AVAILABLE_DEPENDENCIES.waitForDependencies();
      assertEquals(0, IDE.AVAILABLE_DEPENDENCIES.getAttributeCount());
      IDE.AVAILABLE_DEPENDENCIES.selectDependency(1);
      assertTrue(IDE.AVAILABLE_DEPENDENCIES.getAttributeCount() > 0);

      IDE.AVAILABLE_DEPENDENCIES.selectDependency(2);
      assertTrue(IDE.AVAILABLE_DEPENDENCIES.getAttributeCount() > 0);

      IDE.AVAILABLE_DEPENDENCIES.selectDependency(3);
      assertTrue(IDE.AVAILABLE_DEPENDENCIES.getAttributeCount() > 0);

      IDE.AVAILABLE_DEPENDENCIES.clickOkButton();
      IDE.AVAILABLE_DEPENDENCIES.waitClosed();
   }

}
