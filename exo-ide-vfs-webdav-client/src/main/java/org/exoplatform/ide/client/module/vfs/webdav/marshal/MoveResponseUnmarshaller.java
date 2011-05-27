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
import org.exoplatform.ide.client.framework.vfs.Item;

import com.google.gwt.http.client.Response;

/**
 * Unmarshaller for move operation.
 * 
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class MoveResponseUnmarshaller implements Unmarshallable
{
   /**
    * New href of item.
    */
   private String href;
   
   /**
    * Moved item. After moving its properties must be updated (href and name).
    */
   private Item item;
   
   /**
    * @param item - item, that will be moved (after moving properties must be updated: href and name)
    * @param newHref - new href of item after moving
    */
   public MoveResponseUnmarshaller(Item item, String newHref)
   {
      this.href = newHref;
      this.item = item;
   }
   
   public void unmarshal(Response response) throws UnmarshallerException
   {
      item.setHref(href);
      int beginIndex = href.lastIndexOf("/") + 1;
      String name = href.substring(beginIndex, href.length());
      item.setName(name);
   }

}

