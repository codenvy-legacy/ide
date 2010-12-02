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

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

import com.google.gwt.http.client.Response;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.client.module.netvibes.model.DeployResult;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Dec 2, 2010 $
 *
 */
public class DeployResultUnmarshaller implements Unmarshallable
{

   /**
    * Deploy result bean.
    */
   private DeployResult deployResult;

   /**
    * Result XML tag.
    */
   private final String RESULT = "result";

   /**
    * Response XML tag.
    */
   private final String RESPONSE = "response";

   /**
    * Message XML tag.
    */
   private final String MESSAGE = "message";

   /**
    * Error message while parsing the response.
    */
   private final String ERROR_MESSAGE = "Can not get the result of the deploy.";

   /**
    * @param deployResult the result of the deploy
    */
   public DeployResultUnmarshaller(DeployResult deployResult)
   {
      this.deployResult = deployResult;
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
         NodeList resultTags = doc.getElementsByTagName(RESULT);
         if (resultTags != null && resultTags.getLength() > 0)
         {
            Node resultNode = resultTags.item(0);
            NodeList nodes = resultNode.getChildNodes();
            for (int i = 0; i < nodes.getLength(); i++)
            {
               Node node = nodes.item(i);
               String value = (node.getFirstChild() != null) ? node.getFirstChild().getNodeValue() : "";
               if (RESPONSE.equals(node.getNodeName()))
               {
                  deployResult.setSuccess(Boolean.valueOf(value));
               }
               else if (MESSAGE.equals(node.getNodeName()))
               {
                  deployResult.setMessage(value);
               }
            }
         }
      }
      catch (Exception e)
      {
         throw new UnmarshallerException(ERROR_MESSAGE);
      }
   }
}
