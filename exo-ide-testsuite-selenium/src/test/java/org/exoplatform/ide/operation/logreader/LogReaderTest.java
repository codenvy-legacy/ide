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

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class LogReaderTest extends BaseTest
{

   private static final String LOG_READER_VIEW = "//div[@view-id='ideExtensionLogReaderView']";

   private static final String PREV_LOG_BUTTON = "Previous Log";

   private static final String NEX_LOG = "Next Log";

   @Test
   public void testLogReaderOpen() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();
      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.LOG_READER);
      waitForElementPresent(LOG_READER_VIEW);

      String log = getLogContent();

      Assert.assertNotNull(log);
      Assert.assertFalse(log.isEmpty());
   }

   @Test
   public void testLogReaderNavigation() throws Exception
   {
      refresh();
      IDE.WORKSPACE.waitForRootItem();
      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.LOG_READER);
      waitForElementPresent(LOG_READER_VIEW);

      String log = getLogContent();
      Assert.assertTrue(isButtonEnabled(PREV_LOG_BUTTON));
      Assert.assertFalse(isButtonEnabled(NEX_LOG));

      clicOnButton(PREV_LOG_BUTTON);
      IDE.LOADER.waitClosed();
      
      Assert.assertFalse(log.equals(getLogContent()));
      log = getLogContent();
      Assert.assertTrue(isButtonEnabled(NEX_LOG));

      clicOnButton(NEX_LOG);
      Assert.assertFalse(log.equals(getLogContent()));
   }

   private void clicOnButton(String name)
   {
      selenium().click(LOG_READER_VIEW + "//div[@title='" + name + "']");
   }

   private boolean isButtonEnabled(String name)
   {
      return Boolean.valueOf(selenium().getAttribute(LOG_READER_VIEW + "//div[@title='" + name + "']@enabled"));
   }

   private String getLogContent()
   {
      return selenium().getText(LOG_READER_VIEW + "//pre");
   }
}
