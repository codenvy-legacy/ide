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
package org.exoplatform.ide.client.googlecontacts;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.invite.GoogleContact;

import java.util.List;

/**
 * Unmarshaller for unmarshalling Google Contacts as {@link List} of {@link GoogleContact} objects.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: InviteGoogleContactsUnmarshaller.java Aug 20, 2012 3:50:12 PM azatsarynnyy $
 *
 */
public class InviteGoogleContactsUnmarshaller implements Unmarshallable<List<GoogleContact>>
{
   /**
    * {@link List} of {@link GoogleContact}.
    */
   private List<GoogleContact> contacts;

   public InviteGoogleContactsUnmarshaller(List<GoogleContact> contacts)
   {
      this.contacts = contacts;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response)
    */
   @Override
   public void unmarshal(Response response) throws UnmarshallerException
   {
      JSONArray jsonArray = JSONParser.parseStrict(response.getText()).isArray();
      if (jsonArray == null)
      {
         return;
      }

      for (int i = 0; i < jsonArray.size(); i++)
      {
         JSONObject jsonObject = jsonArray.get(i).isObject();
         AutoBean<GoogleContact> contact =
            AutoBeanCodex.decode(IDE.AUTO_BEAN_FACTORY, GoogleContact.class, jsonObject.toString());
         contacts.add(contact.as());
      }
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.copy.Unmarshallable#getPayload()
    */
   @Override
   public List<GoogleContact> getPayload()
   {
      return contacts;
   }
}
