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

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class SwitchAccount extends AbstractTestModule
{
   
   
   private static final String LOGIN_FORM = "//div[@view-id='ideLoginView']";
   
   public static final String EMAIL_FIELD = "ideLoginViewEmailField";
   
   public static final String PASSWORD_FIELD = "ideLoginViewPasswordField";
   
   public static final String LOGIN_BUTTON = "ideLoginViewLoginButton";
      
   public static final String CANCEL_BUTTON = "ideLoginViewCancelButton";
   
   public static final String TRY_AS_DEMO_BUTTON = "ideLoginDemoViewLoginButton";
   
   public void openLoginForm() throws Exception
   {
      IDE().MENU.runCommand(MenuCommands.PaaS.PAAS, MenuCommands.PaaS.Heroku.HEROKU, MenuCommands.PaaS.Heroku.SWITCH_ACCOUNT);
      waitForElementPresent(LOGIN_FORM);
   }
   
   public void typeLogin(String login)
   {
      selenium().type(EMAIL_FIELD, login);
   }
   
   public void typePassword(String pass)
   {
      selenium().type(PASSWORD_FIELD, pass);
   }
   
   public boolean isLoginButtonEnabled()
   {
      return Boolean.valueOf(selenium().getAttribute(LOGIN_BUTTON+"@button-enabled"));
   }
   
   public void clickLoginButton() throws Exception
   {
      selenium().click(LOGIN_BUTTON);
   }
   
   
}
