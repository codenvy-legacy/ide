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
package org.exoplatform.ide.vfs.client.marshal;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;

import org.exoplatform.gwtframework.commons.rest.copy.Unmarshallable;
import org.exoplatform.gwtframework.commons.rest.copy.UnmarshallerException;
import org.exoplatform.ide.vfs.client.JSONDeserializer;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.List;

/**
 * Unmarshaller for the list of virtual file systems.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Sep 27, 2011 9:41:55 AM anya $
 * 
 */
public class VFSListUnmarshaller implements Unmarshallable<List<VirtualFileSystemInfo>>
{
   /**
    * The list of virtual file systems.
    */
   private List<VirtualFileSystemInfo> vfsList;

   /**
    * @param vfsList the list of virtual file systems
    */
   public VFSListUnmarshaller(List<VirtualFileSystemInfo> vfsList)
   {
      this.vfsList = vfsList;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.copy.Unmarshallable#unmarshal(com.google.gwt.http.client.Response)
    */
   @Override
   public void unmarshal(Response response) throws UnmarshallerException
   {
      try
      {
         JSONArray jsonArray = JSONParser.parseLenient(response.getText()).isArray();
         for (int i = 0; i < jsonArray.size(); i++)
         {
            VirtualFileSystemInfo vfsInfo = JSONDeserializer.VFSINFO_DESERIALIZER.toObject(jsonArray.get(i));
            vfsList.add(vfsInfo);
         }
      }
      catch (Exception e)
      {
         throw new UnmarshallerException("Can't parse the list of virtual file systems.");
      }
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.copy.Unmarshallable#getPayload()
    */
   @Override
   public List<VirtualFileSystemInfo> getPayload()
   {
      return vfsList;
   }
}
