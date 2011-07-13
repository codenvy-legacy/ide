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

import org.exoplatform.common.http.client.HTTPConnection;
import org.exoplatform.common.http.client.HTTPResponse;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.Utils;

import java.net.URL;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class Heroku
{
  public interface Messages
  {
     String LOGED_IN = "[INFO] Logged in Heroku successfully.";
     
     String DELETED = "[INFO] Application is successfully deleted on Heroku.";
     
     String LOGIN = "test@test.com";
     
     String PASSWORD = "test";
  }
  
  public final SwitchAccount SWITCH_ACCOUNT = new SwitchAccount();
  
  public final CreateApplication CREATE_APP = new CreateApplication();
  
  public final DeleteApplication DELETE_APP = new DeleteApplication();
  
  public final RenameApplication RENAME_APP = new RenameApplication();
  
  public static final int logout() throws Exception
  {
     URL url = new URL(BaseTest.BASE_URL);
     HTTPConnection connection = Utils.getConnection(url);
     HTTPResponse response = connection.Post(BaseTest.REST_CONTEXT + "/ide/heroku/logout");
     return response.getStatusCode();
  }
  
}
