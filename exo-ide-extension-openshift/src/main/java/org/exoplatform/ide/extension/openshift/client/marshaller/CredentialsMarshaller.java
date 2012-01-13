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
package org.exoplatform.ide.extension.openshift.client.marshaller;

import com.google.gwt.json.client.JSONString;

import com.google.gwt.json.client.JSONObject;

import org.exoplatform.gwtframework.commons.rest.Marshallable;

/**
 * Marshaller for login request in JSON format.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jun 6, 2011 5:59:09 PM anya $
 * 
 */
public class CredentialsMarshaller implements Marshallable, Constants
{
   /**
    * User's login.
    */
   private String login;

   /**
    * User's password.
    */
   private String password;

   /**
    * @param login user's login
    * @param password user's password
    */
   public CredentialsMarshaller(String login, String password)
   {
      this.login = login;
      this.password = password;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.Marshallable#marshal()
    */
   @Override
   public String marshal()
   {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put(LOGIN, new JSONString(login));
      jsonObject.put(PASSWORD, new JSONString(password));
      return jsonObject.toString();
   }

}
