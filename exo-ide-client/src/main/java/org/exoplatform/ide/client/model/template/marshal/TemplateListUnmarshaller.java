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
package org.exoplatform.ide.client.model.template.marshal;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.Response;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.model.template.FileTemplate;
import org.exoplatform.ide.client.model.template.FolderTemplate;
import org.exoplatform.ide.client.model.template.ProjectTemplate;
import org.exoplatform.ide.client.model.template.Template;
import org.exoplatform.ide.client.model.template.TemplateList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class TemplateListUnmarshaller implements Unmarshallable, Const
{

   private static final String CANT_PARSE_TEMPLATE = IDE.ERRORS_CONSTANT.templateCantParseTemplate();

   private TemplateList templateList;

   public TemplateListUnmarshaller(HandlerManager eventBus, TemplateList templateList)
   {
      this.templateList = templateList;
   }

   public static native String javaScriptDecodeURIComponent(String text) /*-{
                                                                         return decodeURIComponent(text);
                                                                         }-*/;

   public void unmarshal(Response response) throws UnmarshallerException
   {
      try
      {
         if (response.getText().isEmpty() || response.getStatusCode() == HTTPStatus.NOT_FOUND)
            return;

         Document dom = XMLParser.parse(response.getText());
         Node templatesNode = dom.getElementsByTagName(TEMPLATES).item(0);

         NodeList templateNodes = templatesNode.getChildNodes();
         for (int i = 0; i < templateNodes.getLength(); i++)
         {
            Node templateNode = templateNodes.item(i);
            parseTemplate(templateNode);
         }
      }
      catch (Exception exc)
      {
         throw new UnmarshallerException(CANT_PARSE_TEMPLATE);
      }

   }

   private void parseTemplate(Node templateNode)
   {
      Node node = getChildNode(templateNode, TEMPLATE);
      Node templateTypeNode = getChildNode(node, TEMPLATE_TYPE);

      String templateType = templateTypeNode.getChildNodes().item(0).getNodeValue();

      if (Const.TemplateType.FILE.equals(templateType))
      {
         parseFileTemplate(templateNode);
      }
      else if (Const.TemplateType.PROJECT.equals(templateType))
      {
         parseProjectTemplate(templateNode);
      }

   }

   private void parseFileTemplate(Node templateNode)
   {
      String nodeName = templateNode.getNodeName();

      Node node = getChildNode(templateNode, TEMPLATE);

      Node nameNode = getChildNode(node, NAME);

      String name = javaScriptDecodeURIComponent(nameNode.getChildNodes().item(0).getNodeValue());

      Node descriptionNode = getChildNode(node, DESCRIPTION);
      String description = "";
      if (descriptionNode.getChildNodes().getLength() != 0)
      {
         description = javaScriptDecodeURIComponent(descriptionNode.getChildNodes().item(0).getNodeValue());
      }

      Node mimeTypeNode = getChildNode(node, MIME_TYPE);
      String mimeType = javaScriptDecodeURIComponent(mimeTypeNode.getChildNodes().item(0).getNodeValue());

      Node contentNode = getChildNode(node, CONTENT);
      String content = javaScriptDecodeURIComponent(getNodeText(contentNode));

      Template template = new FileTemplate(mimeType, name, description, content, nodeName);
      templateList.getTemplates().add(template);
   }

   private void parseProjectTemplate(Node templateNode)
   {
      String nodeName = templateNode.getNodeName();
      Node node = getChildNode(templateNode, TEMPLATE);

      Node nameNode = getChildNode(node, NAME);
      String name = javaScriptDecodeURIComponent(nameNode.getChildNodes().item(0).getNodeValue());

      Node classpathNode = getChildNode(node, CLASSPATH);

      Node descriptionNode = getChildNode(node, DESCRIPTION);
      String description = "";
      if (descriptionNode.getChildNodes().getLength() != 0)
      {
         description = javaScriptDecodeURIComponent(descriptionNode.getChildNodes().item(0).getNodeValue());
      }

      ProjectTemplate template = new ProjectTemplate(name, description, nodeName, null);

      if (classpathNode != null)
      {
         String classpath = javaScriptDecodeURIComponent(classpathNode.getChildNodes().item(0).getNodeValue());
         template.setClassPathLocation(classpath);
      }

      appendProjectChildren(template, getChildNode(node, ITEMS));

      templateList.getTemplates().add(template);
   }

   private void appendProjectChildren(FolderTemplate container, Node itemsNode)
   {
      if (itemsNode == null)
      {
         return;
      }
      List<Template> children = new ArrayList<Template>();

      for (int i = 0; i < itemsNode.getChildNodes().getLength(); i++)
      {
         Node itemNode = itemsNode.getChildNodes().item(i);

         if (FILE.equals(itemNode.getNodeName()))
         {
            Node fileTemplateNameNode = getChildNode(itemNode, TEMPLATE_FILE_NAME);
            Node fileNameNode = getChildNode(itemNode, FILE_NAME);

            String fileTemplateName = javaScriptDecodeURIComponent(fileTemplateNameNode.getFirstChild().getNodeValue());
            String fileName = javaScriptDecodeURIComponent(fileNameNode.getFirstChild().getNodeValue());

            children.add(new FileTemplate(fileTemplateName, fileName));
         }
         else if (FOLDER.equals(itemNode.getNodeName()))
         {
            Node folderNameNode = getChildNode(itemNode, NAME);

            String folderName = javaScriptDecodeURIComponent(folderNameNode.getFirstChild().getNodeValue());

            FolderTemplate folder = new FolderTemplate(folderName);
            children.add(folder);

            appendProjectChildren(folder, getChildNode(itemNode, ITEMS));
         }
      }

      if (children.size() > 0)
      {
         container.setChildren(children);
      }
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

   private String getNodeText(Node xmlNode)
   {
      if (xmlNode == null)
         return "";
      StringBuilder result = new StringBuilder(4096);
      for (Node node = xmlNode.getFirstChild(); node != null; node = node.getNextSibling())
         result.append(node.getNodeValue());
      return result.toString();
   }
}
