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
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

import org.exoplatform.gwtframework.commons.rest.copy.Unmarshallable;
import org.exoplatform.gwtframework.commons.rest.copy.UnmarshallerException;
import org.exoplatform.ide.vfs.client.JSONDeserializer;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo.ACLCapability;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo.QueryCapability;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: VFSInfoUnmarshaller Feb 2, 2011 2:16:15 PM evgen $
 *
 */
public class VFSInfoUnmarshaller implements Unmarshallable<VirtualFileSystemInfo>
{
 
   private final VirtualFileSystemInfo virtualFileSystemInfo;

   /**
    * @param virtualFileSystemInfo
    */
   public VFSInfoUnmarshaller(VirtualFileSystemInfo virtualFileSystemInfo)
   {
      this.virtualFileSystemInfo = virtualFileSystemInfo;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response)
    */
   @Override
   public void unmarshal(Response response) throws UnmarshallerException
   {      
      JSONObject jsonObject = JSONParser.parseLenient(response.getText()).isObject();
           
      virtualFileSystemInfo.setVersioningSupported(JSONDeserializer.BOOLEAN_DESERIALIZER.
            toObject(jsonObject.get("versioningSupported"))); //
      virtualFileSystemInfo.setLockSupported(JSONDeserializer.BOOLEAN_DESERIALIZER.
            toObject(jsonObject.get("lockSupported"))); //
      virtualFileSystemInfo.setAnonymousPrincipal(JSONDeserializer.STRING_DESERIALIZER.
            toObject(jsonObject.get("anonymousPrincipal"))); //
      virtualFileSystemInfo.setAnyPrincipal(JSONDeserializer.STRING_DESERIALIZER.
            toObject(jsonObject.get("anyPrincipal"))); //
      virtualFileSystemInfo.setPermissions(JSONDeserializer.STRING_DESERIALIZER.
            toSet(jsonObject.get("permissions"))); //
      virtualFileSystemInfo.setAclCapability(ACLCapability.fromValue(JSONDeserializer.STRING_DESERIALIZER.
            toObject(jsonObject.get("aclCapability")).toLowerCase())); //
      virtualFileSystemInfo.setQueryCapability(QueryCapability.fromValue(JSONDeserializer.STRING_DESERIALIZER.
            toObject(jsonObject.get("queryCapability")).toLowerCase()));
      virtualFileSystemInfo.setUrlTemplates(JSONDeserializer.LINK_DESERIALIZER.
            toMap(jsonObject.get("urlTemplates")));
            
      JSONObject root = jsonObject.get("root").isObject();
    
      virtualFileSystemInfo.setRoot(new FolderModel(root)); 
   }

   @Override
   public VirtualFileSystemInfo getPayload()
   {
      return this.virtualFileSystemInfo;
   }
   
   

}
