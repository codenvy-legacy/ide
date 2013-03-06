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
package com.codenvy.ide.java.client.projectmodel;

import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.rest.Unmarshallable;

import com.google.gwt.json.client.JSONParser;

import com.google.gwt.http.client.Response;


/**
 * Unmarshaller for {@link Package}
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class PackageUnmarshaller implements Unmarshallable<Package>
{

   private final Package pack;

   public PackageUnmarshaller(Package pack)
   {
      this.pack = pack;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void unmarshal(Response response) throws UnmarshallerException
   {
      try
      {
         pack.init(JSONParser.parseLenient(response.getText()).isObject());
      }
      catch (Exception e)
      {
         throw new UnmarshallerException("Can't parse package", e);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Package getPayload()
   {
      return pack;
   }

}
