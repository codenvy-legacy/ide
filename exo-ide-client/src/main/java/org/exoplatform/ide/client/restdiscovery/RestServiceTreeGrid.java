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
package org.exoplatform.ide.client.restdiscovery;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.commons.wadl.Method;
import org.exoplatform.gwtframework.commons.wadl.Param;
import org.exoplatform.gwtframework.commons.wadl.Resource;
import org.exoplatform.gwtframework.ui.client.smartgwt.component.data.FolderOpenedHandlerImpl;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.model.discovery.marshal.RestService;

import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.TreeModelType;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeGridField;
import com.smartgwt.client.widgets.tree.TreeNode;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Dec 23, 2010 11:29:51 AM evgen $
 *
 */
public class RestServiceTreeGrid extends TreeGrid implements UntypedTreeGrid
{

   private String valuePropertyName = getClass().getName();

   private Tree tree;

   private TreeNode rootNode;

   private static final String NAME = "name";

   private static final String TITLE = "Path";
   
   private static final String ID ="ideRestServiceTreeGrid";

   public RestServiceTreeGrid()
   {
      setID(ID);
      setShowRoot(false);

      tree = new Tree();
      tree.setModelType(TreeModelType.CHILDREN);
      rootNode = new TreeNode("root");
      tree.setRoot(rootNode);
      setData(tree);

      setFixedFieldWidths(false);
      setSelectionType(SelectionStyle.SINGLE);
      setSeparateFolders(false);
      

      // setCanFocus(false);  // to fix bug IDE-258 "Enable navigation by using keyboard in the Navigation, Search and Outline Panel to improve IDE accessibility."  

      setShowConnectors(false);
      setCanSort(true);

      TreeGridField nameField = new TreeGridField(TITLE);

      //TODO
      //This field need for selenium.
      //We can't select tree node, if click on first column.
      //If you click on second column - tree item is selected.
      TreeGridField mockField = new TreeGridField("mock");
      mockField.setWidth(3);
      setFields(nameField, mockField);
   }

   /**
    * @see org.exoplatform.gwtframework.ui.client.smartgwt.component.TreeGrid#doUpdateValue()
    */

   public void setRootValues(List<RestService> items)
   {
      for (RestService r : items)
      {
         TreeNode newNode = getRestServiceNode(r);
         newNode.setIsFolder(true);
         tree.add(newNode, rootNode);
      }
   }

   /**
    * @param r
    * @return
    */
   private TreeNode getRestServiceNode(RestService r)
   {
      TreeNode node = new TreeNode(r.getPath());
      node.setAttribute(TITLE, r.getPath());
      node.setAttribute(getValuePropertyName(), r);
      return node;
   }

   /**
    * 
    */
   private void fillServiceTree(TreeNode parentNode, List<?> children)
   {
      for (Object o : children)
      {
         if (o instanceof Resource)
         {
            Resource r = (Resource)o;
            TreeNode newNode = getResourceNode(r);
            newNode.setCanExpand(false);
            newNode.setIcon(Images.RestService.REST_SERVICE);
            tree.add(newNode, parentNode);

            if (r.getMethodOrResource() != null && !r.getMethodOrResource().isEmpty())
            {
               fillServiceTree(newNode, r.getMethodOrResource());
            }
         }
         if (o instanceof Method)
         {
            Method m = (Method)o;
            TreeNode newNode = new TreeNode(m.getName());
            newNode.setIcon(Images.RestService.METHOD);
            String title = m.getName();
            if (m.getRequest() != null)
            {
               if (!m.getRequest().getRepresentation().isEmpty())
               {
                  title += "&nbsp;<img align=\"absmiddle\" width=\"16\" height=\"16\" border=\"0\" src=\""+Images.RestService.IN+"\"> "+ m.getRequest().getRepresentation().get(0).getMediaType();
                 
               }
            }
            if(m.getResponse()!= null)
            {
               if (!m.getResponse().getRepresentationOrFault().isEmpty())
               {
                  title += "&nbsp;<img align=\"absmiddle\" width=\"16\" height=\"16\" border=\"0\" src=\""+Images.RestService.OUT+"\"> "+ m.getResponse().getRepresentationOrFault().get(0).getMediaType();
               }
            }

            newNode.setAttribute(TITLE, title);
            newNode.setAttribute(getValuePropertyName(), m);
            tree.add(newNode, parentNode);

            if (m.getRequest() != null && !m.getRequest().getParam().isEmpty())
            {
               List<Param> query = new ArrayList<Param>();
               List<Param> headers = new ArrayList<Param>();
               for (Param p : m.getRequest().getParam())
               {
                  switch (p.getStyle())
                  {
                     case HEADER :
                        headers.add(p);
                        break;
                     case QUERY :
                        query.add(p);
                        break;
                  }
               }
               if (!headers.isEmpty())
               {
                  TreeNode headerNode = new TreeNode("Header");
                  headerNode.setAttribute(TITLE, "Header Param");
                  headerNode.setIcon(Images.RestService.PARAMETER);
                  tree.add(headerNode, newNode);
                  
                  addParametes(headerNode, headers);
               }
               if (!query.isEmpty())
               {
                  TreeNode queryNode = new TreeNode("Query");
                  queryNode.setAttribute(TITLE, "Query Param");
                  queryNode.setIcon(Images.RestService.PARAMETER);
                  tree.add(queryNode, newNode);
                  addParametes(queryNode, query);
               }

            }
         }

      }
   }

   /**
    * @param node
    * @param parameters
    */
   private void addParametes(TreeNode node, List<Param> parameters)
   {
      for (Param p : parameters)
      {
         TreeNode pNode = new TreeNode(p.getName());
         pNode.setAttribute(TITLE, p.getName() + ":" + p.getType().getLocalName());
         pNode.setAttribute(getValuePropertyName(), p);
         pNode.setIcon(Images.RestService.VAR);
         tree.add(pNode, node);
      }
   }

   private TreeNode getResourceNode(Resource item)
   {
      TreeNode node = new TreeNode(item.getPath());
      node.setAttribute(TITLE, item.getPath());
      node.setAttribute(getValuePropertyName(), item);

      return node;
   }

   protected String getValuePropertyName()
   {
      return valuePropertyName;
   }

   /**
    * @see com.google.gwt.event.logical.shared.HasOpenHandlers#addOpenHandler(com.google.gwt.event.logical.shared.OpenHandler)
    */
   public HandlerRegistration addOpenHandler(OpenHandler<Object> handler)
   {
      FolderOpenedHandlerImpl<Object> openedHandler =
         new FolderOpenedHandlerImpl<Object>(handler, getValuePropertyName());
      return addFolderOpenedHandler(openedHandler);
   }

   /**
    * @see org.exoplatform.ide.client.restdiscovery.UntypedTreeGrid#setPaths(org.exoplatform.ide.client.model.discovery.marshal.RestService, java.util.List)
    */
   public void setPaths(RestService service, List<?> resources)
   {
      TreeNode node = getNode(rootNode, service.getPath());
      for (TreeNode childNode : tree.getChildren(node))
      {
         tree.remove(childNode);
      }
      fillServiceTree(node, resources);
   }

   private TreeNode getNode(TreeNode parent, String name)
   {
      TreeNode[] rootResources = tree.getChildren(rootNode);
      for (TreeNode node : rootResources)
      {
         if (node.getName().equals(name))
            return node;
      }
      return null;
   }

}
