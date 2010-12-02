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
package org.exoplatform.ide.client.module.netvibes.service.deploy.marshaller;

import com.google.gwt.xml.client.Node;

import com.google.gwt.xml.client.NodeList;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.XMLParser;

import com.google.gwt.http.client.Response;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.client.module.netvibes.model.Categories;

/**
 * Unmarshalls XML response to {@link Categories}.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Nov 30, 2010 $
 *
 */
public class CategoriesUnmarshaller implements Unmarshallable
{

   /**
    * Categories list.
    */
   private Categories categories;

   /**
    * Message to show on {@link UnmarshallerException}.
    */
   public final static String ERROR_MESSAGE = "Can't parse categories list.";

   /**
    * Category tag.
    */
   public final static String CATEGORY = "category";

   /**
    * Attribute id.
    */
   public final static String ID = "id";

   /**
    * @param categories 
    */
   public CategoriesUnmarshaller(Categories categories)
   {
      this.categories = categories;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response)
    */
   @Override
   public void unmarshal(Response response) throws UnmarshallerException
   {
      try
      {
         Document doc = XMLParser.parse(response.getText());
         //Get all "category" tags:
         NodeList categoryList = doc.getElementsByTagName(CATEGORY);
         for (int i = 0; i < categoryList.getLength(); i++)
         {
            Node node = categoryList.item(i);
            String id = "";

            if (node.getAttributes() != null)
            {
               Node idAttribute = node.getAttributes().getNamedItem(ID);
               id = (idAttribute != null && idAttribute.getNodeValue() != null) ? idAttribute.getNodeValue() : "";
            }
            String value =
               (node.getFirstChild() != null && node.getFirstChild().getNodeValue() != null) ? node.getFirstChild()
                  .getNodeValue() : "";
            categories.getCategoryMap().put(id, value);
         }
      }
      catch (Exception e)
      {
         throw new UnmarshallerException(ERROR_MESSAGE);
      }
   }

}
