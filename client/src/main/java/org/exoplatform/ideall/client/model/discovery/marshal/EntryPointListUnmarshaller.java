/**
 * Copyright (C) 2009 eXo Platform SAS.
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
 *
 */
package org.exoplatform.ideall.client.model.discovery.marshal;

import java.util.List;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class EntryPointListUnmarshaller implements Unmarshallable
{

   private List<String> entryPoints;

   public EntryPointListUnmarshaller(List<String> entryPoints)
   {
      this.entryPoints = entryPoints;
   }

   public void unmarshal(String body) throws UnmarshallerException
   {
      Document dom = XMLParser.parse(body);

      NodeList nodes = dom.getElementsByTagName("entrypoint");
      for (int i = 0; i < nodes.getLength(); i++)
      {
         Element entryPointElement = (Element)nodes.item(i);
         entryPoints.add(entryPointElement.getChildNodes().item(0).getNodeValue());
      }
   }

}
