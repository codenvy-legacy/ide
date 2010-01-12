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
package org.exoplatform.ideall.client.model.template.marshal;

import org.exoplatform.gwt.commons.rest.Unmarshallable;
import org.exoplatform.ideall.client.model.template.Template;
import org.exoplatform.ideall.client.model.template.TemplateList;
import org.exoplatform.ideall.client.model.util.TextUtils;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class TemplateListUnmarshaller implements Unmarshallable, Const
{

   private TemplateList templateList;

   public TemplateListUnmarshaller(TemplateList templateList)
   {
      this.templateList = templateList;
   }

   public void unmarshal(String body)
   {
      Document dom = XMLParser.parse(body);
      Node templatesNode = dom.getElementsByTagName(TEMPLATES).item(0);

      NodeList templateNodes = templatesNode.getChildNodes();
      for (int i = 0; i < templateNodes.getLength(); i++)
      {
         Node templateNode = templateNodes.item(i);
         parseTemplate(templateNode);
      }

   }

   private void parseTemplate(Node templateNode)
   {
      String name = templateNode.getNodeName();

      Node node = getChildNode(templateNode, TEMPLATE);

      Node descriptionNode = getChildNode(node, DESCRIPTION);
      String description = TextUtils.javaScriptDecodeURIComponent(descriptionNode.getChildNodes().item(0).getNodeValue());

      Node mimeTypeNode = getChildNode(node, MIME_TYPE);
      String mimeType = TextUtils.javaScriptDecodeURIComponent(mimeTypeNode.getChildNodes().item(0).getNodeValue());

      Node contentNode = getChildNode(node, CONTENT);
      String content = TextUtils.javaScriptDecodeURIComponent(contentNode.getChildNodes().item(0).getNodeValue());

      Template template = new Template(mimeType, name, description, content);
      templateList.getTemplates().add(template);
   }

   private Node getChildNode(Node node, String name)
   {
      for (int i = 0; i < node.getChildNodes().getLength(); i++)
      {
         Node childNode = node.getChildNodes().item(i);
         if (name.equals(childNode.getNodeName()))
         {
            return childNode;
         }
      }

      return null;
   }

}
