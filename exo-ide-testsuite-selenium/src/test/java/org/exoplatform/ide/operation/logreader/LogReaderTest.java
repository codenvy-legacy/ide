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
package org.exoplatform.ide.operation.logreader;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.junit.Test;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class LogReaderTest extends BaseTest
{

   @Test
   public void testLogReaderOpen() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      
      
      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.LOG_READER);
      IDE.LOG_READER.waitOpened();

      String log = IDE.LOG_READER.getLogContent();

      assertNotNull(log);
      assertFalse(log.isEmpty());
   }

   @Test
   public void testLogReaderNavigation() throws Exception
   {
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      
      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.LOG_READER);
      IDE.LOG_READER.waitOpened();

      String log = IDE.LOG_READER.getLogContent();
      assertTrue(IDE.LOG_READER.isPreviousButtonEnabled());
      assertFalse(IDE.LOG_READER.isNextButtonEnabled());

      IDE.LOG_READER.clickPrevButton();
      IDE.LOADER.waitClosed();
      
      assertFalse(log.equals(IDE.LOG_READER.getLogContent()));
      log = IDE.LOG_READER.getLogContent();
      assertTrue(IDE.LOG_READER.isNextButtonEnabled());

      IDE.LOG_READER.clickNextButton();
      assertFalse(log.equals(IDE.LOG_READER.getLogContent()));
   }

}
