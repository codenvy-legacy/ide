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
package org.exoplatform.ide.paas.heroku.core;

import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.core.AbstractTestModule;
import org.junit.Assert;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class Rake extends AbstractTestModule
{

   public static final String RAKE_FORM = "//div[@view-id='ideRakeCommandView']";

   public static final String COMMAND_FIELD = "ideRakeCommandViewCommandField";

   public static final String RUN_BUTTON = "ideRakeCommandViewRunButton";

   public static final String HELP_BUTTON = "ideRakeCommandViewHelpButton";

   public static final String CLOSE_BUTTON = "ideRakeCommandViewCloseButton";

   public interface Commands
   {
      String HELP = "rake -H";

      String DB_CREATE = "rake db:create";

      String STATUS = "rake status";
   }

   /**
    * Open Rake Command form by press PaaS -> Heroku -> Rake... menu commands
    * @throws Exception
    */
   public void openRakeConsole() throws Exception
   {
      IDE().MENU.runCommand(MenuCommands.PaaS.PAAS, MenuCommands.PaaS.Heroku.HEROKU, MenuCommands.PaaS.Heroku.RAKE);
      waitForElementPresent(RAKE_FORM);
   }

   public boolean isRunButtonEnabled()
   {
      return Boolean.valueOf(selenium().getAttribute(RUN_BUTTON + "@button-enabled"));
   }

   public void pressHelpButton()
   {
      selenium().click(HELP_BUTTON);
   }

   
   private void clearCommandField()
   {
      selenium().getEval("this.browserbot.findElement('"+COMMAND_FIELD + "').value=''");
   }
   
   public void typeCommand(String command)
   {
      clearCommandField();
      selenium().type(COMMAND_FIELD, command);
   }

   public void runCommand()
   {
      Assert.assertTrue(isRunButtonEnabled());
      selenium().click(RUN_BUTTON);
   }

}
