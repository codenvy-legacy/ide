/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.cloudfoundry.server;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class CloudfoundryLoginTest
{
   private Auth authenticator;
   private Cloudfoundry cloudfoundry;

   @Before
   public void setUp() throws Exception
   {
      authenticator = new Auth();
      authenticator.setCredentials(new CloudfoundryCredentials());
      cloudfoundry = new Cloudfoundry(authenticator);
   }

   @Test
   public void testLoginDefault() throws Exception
   {
      authenticator.setUsername(LoginInfo.email);
      authenticator.setPassword(LoginInfo.password);
      authenticator.writeTarget(LoginInfo.target);
      // Login with username and password provided by authenticator.
      cloudfoundry.login();
      assertNotNull(authenticator.readCredentials().getToken(authenticator.getTarget()));
   }

   @Test
   public void testLogin() throws Exception
   {
      cloudfoundry.login(LoginInfo.target, LoginInfo.email, LoginInfo.password);
      assertNotNull(authenticator.readCredentials().getToken(LoginInfo.target));
   }

   @Test
   public void testLoginFail() throws Exception
   {
      try
      {
         cloudfoundry.login(LoginInfo.target, LoginInfo.email, LoginInfo.password + "_wrong");
         fail("CloudfoundryException expected");
      }
      catch (CloudfoundryException e)
      {
         assertEquals(200, e.getExitCode());
         assertEquals(403, e.getResponseStatus());
         assertEquals("Operation not permitted", e.getMessage());
         assertEquals("text/plain", e.getContentType());
      }
      assertNull(authenticator.readCredentials().getToken(LoginInfo.target));
   }

   @Test
   public void testLogout() throws Exception
   {
      cloudfoundry.login(LoginInfo.target, LoginInfo.email, LoginInfo.password);
      assertNotNull(authenticator.readCredentials().getToken(LoginInfo.target));
      cloudfoundry.logout(LoginInfo.target);
      assertNull(authenticator.readCredentials().getToken(LoginInfo.target));
   }
}
