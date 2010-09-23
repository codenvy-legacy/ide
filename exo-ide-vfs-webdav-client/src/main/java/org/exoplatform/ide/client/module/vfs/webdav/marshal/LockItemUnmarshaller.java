/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.client.module.vfs.webdav.marshal;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.client.module.vfs.api.LockToken;

import com.google.gwt.http.client.Response;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

public class LockItemUnmarshaller implements Unmarshallable
{

   private LockToken lockToken;

   /**
    * @param lockToken
    */
   public LockItemUnmarshaller(LockToken lockToken)
   {
      this.lockToken = lockToken;
   }

   public void unmarshal(Response response) throws UnmarshallerException
   {
      try
      {
         parseLockToken(response.getText());
      }
      catch (Exception e)
      {
         throw new UnmarshallerException("Can't parse lock token");
      }
   }

   private void parseLockToken(String body)
   {
      Document token = XMLParser.parse(body);
      //      NodeList activeLock = token.getElementsByTagName("D:activelock");
      NodeList activeLock =
         token.getChildNodes().item(0).getChildNodes().item(0).getChildNodes().item(0).getChildNodes();

      for (int i = 0; i < activeLock.getLength(); i++)
      {
         Node node = activeLock.item(i);
         if (node.getNodeName().equals("D:owner"))
         {
            if (node.getChildNodes().item(0).getNodeName().equals("D:href"))
            {
               lockToken.setOwner(node.getChildNodes().item(0).getFirstChild().getNodeValue());
            }
            else
               lockToken.setOwner(node.getFirstChild().getNodeValue());
         }
         else if (node.getNodeName().equals("D:timeout"))
         {
            String timeout = node.getFirstChild().getNodeValue();
            int time = Integer.parseInt(timeout.substring(timeout.lastIndexOf("-") + 1, timeout.length()));
            lockToken.setTimeout(time);
         }
         else if (node.getNodeName().equals("D:locktoken"))
         {
            lockToken.setLockToken(node.getFirstChild().getFirstChild().getNodeValue());
         }
      }

   }
}
