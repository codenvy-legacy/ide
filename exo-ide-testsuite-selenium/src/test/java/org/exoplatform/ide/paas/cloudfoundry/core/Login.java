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

package org.exoplatform.ide.paas.cloudfoundry.core;

import org.exoplatform.ide.core.AbstractTestModule;
import org.exoplatform.ide.core.Project.Locators;
import org.exoplatform.ide.utils.AbstractTextUtil;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class Login extends AbstractTestModule
{
   
   public interface Locators
   {
      
      String VIEW_ID = "ideLoginView";
      
      String SERVER_FIELD_NAME = "ideLoginViewTargetField";
      
      String EMAIL_FIELD_NAME = "ideLoginViewEmailField";
      
      String PASSWORD_FIELD_NAME = "ideLoginViewPasswordField";
      
      String LOGIN_BUTTON_ID = "ideLoginViewLoginButton";
      
      String CANCEL_BUTTON_ID = "ideLoginViewCancelButton";
      
   }

   public boolean isLoginDialogOpened() {
      String locator = "//div[@view-id='" + Locators.VIEW_ID + "']";
      return selenium().isElementPresent(locator);
   }

   public void waitFormLoginDialogClosed() throws Exception {
      String locator = "//div[@view-id='" + Locators.VIEW_ID + "']";
      waitForElementNotPresent(locator);
   }

   public void typeEmail(String email) throws Exception {
      AbstractTextUtil.getInstance().typeToInput(Locators.EMAIL_FIELD_NAME, email, true);
   }

   public void typePassword(String password) throws Exception {
      AbstractTextUtil.getInstance().typeToInput(Locators.PASSWORD_FIELD_NAME, password, true);
   }

   public void clickLoginButton() {
      selenium().click(Locators.LOGIN_BUTTON_ID);
   }

   public void clickcancelButton() {
      selenium().click(Locators.CANCEL_BUTTON_ID);
   }

}
