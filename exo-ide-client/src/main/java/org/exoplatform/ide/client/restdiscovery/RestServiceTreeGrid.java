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

import java.util.List;
import java.util.Set;

import org.exoplatform.gwtframework.commons.wadl.Method;
import org.exoplatform.gwtframework.commons.wadl.Request;
import org.exoplatform.gwtframework.commons.wadl.Resource;
import org.exoplatform.gwtframework.ui.client.smartgwt.component.data.FolderOpenedHandlerImpl;
import org.exoplatform.gwtframework.ui.client.smartgwt.component.data.SelectionHandlerImpl;
import org.exoplatform.gwtframework.ui.client.smartgwt.component.event.ClickHandlerImpl;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.framework.discovery.RestService;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.smartgwt.client.types.TreeModelType;
import com.smartgwt.client.widgets.grid.ListGridField;
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

   private static final String ID = "ideRestServiceTreeGrid";

   private Set<String> restClassPaths;

   public RestServiceTreeGrid()
   {
      setID(ID);
      setShowRoot(false);

      tree = new Tree();
      tree.setModelType(TreeModelType.CHILDREN);
      rootNode = new TreeNode("root");
      tree.setRoot(rootNode);
      setData(tree);

      setShowConnectors(false);
      setCanSort(true);
      setCanEdit(false);
      setShowRoot(false);
      setFixedFieldWidths(false);
      setIconSize(16);
      setCanFocus(true);
      setShowHeader(false);
      TreeGridField nameField = new TreeGridField(TITLE, 150);
      nameField.setWidth("100%");
      //TODO
      //This field need for selenium.
      //We can't select tree node, if click on first column.
      //If you click on second column - tree item is selected.
      TreeGridField mockField = new TreeGridField("mock");
      mockField.setWidth(3);
      mockField.setHidden(true);
//      setDefaultFields(new ListGridField[]{nameField, mockField});
      setFields(nameField, mockField);
      setFixedFieldWidths(false);

   }

   /**
    * @see org.exoplatform.gwtframework.ui.client.smartgwt.component.TreeGrid#doUpdateValue()
    */

   public void setRootValue(RestService item, Set<String> restClassPaths)
   {
      this.restClassPaths = restClassPaths;
      for (RestService rs : item.getChildServices().values())
      {
         addRestService(rootNode, rs);
      }
      sort();
   }

   private void addRestService(TreeNode parentNode, RestService children)
   {

      RestService r = (RestService)parentNode.getAttributeAsObject(getValuePropertyName());

      if (r != null)
         children.setFullPath(r.getFullPath() + children.getPath());
      if (children.getPath().endsWith("/"))
      {
         children.setPath(children.getPath().substring(0, children.getPath().lastIndexOf('/')));
      }

      TreeNode node = new TreeNode(children.getFullPath());
      node.setAttribute(TITLE, children.getPath());
      node.setAttribute(getValuePropertyName(), children);
      node.setIsFolder(true);
      node.setIcon(Images.MainMenu.GET_URL);
      if (restClassPaths.contains(children.getFullPath()))
      {
         node.setIcon(Images.RestService.CLASS);
      }
      tree.add(node, parentNode);

      for (RestService rs : children.getChildServices().values())
      {
         addRestService(node, rs);
      }
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
            TreeNode newNode = new TreeNode(r.getPath());
            String title = r.getPath();
            RestService rs = (RestService)parentNode.getAttributeAsObject(getValuePropertyName());
            if (rs != null)
            {
               if (rs.getFullPath().endsWith("/"))
                  title = title.substring(rs.getFullPath().length() - 1);
               else
                  title = title.substring(rs.getFullPath().length());
            }
            newNode.setAttribute(TITLE, title);
            newNode.setAttribute(getValuePropertyName(), r);
            newNode.setIcon(Images.RestService.RESOURCE);

            tree.add(newNode, parentNode);

            if (r.getMethodOrResource() != null && !r.getMethodOrResource().isEmpty())
            {
               fillServiceTree(newNode, r.getMethodOrResource());
            }
         }
         if (o instanceof Method)
         {
            Method m = (Method)o;
            if (m.getRequest() == null)
               m.setRequest(new Request());
            Object re = parentNode.getAttributeAsObject(getValuePropertyName());
            if (re instanceof Resource)
            {
               Resource res = (Resource)re;

               if (res != null)
                  m.getRequest().getParam().addAll(res.getParam());
               m.setHref(((Resource)re).getPath());
            }
            else if (re instanceof RestService)
            {
               m.setHref(((RestService) re).getFullPath());
            }
            TreeNode newNode = new TreeNode(m.getName());
            newNode.setIcon(Images.RestService.METHOD);
            String title = m.getName();
            newNode.setAttribute(TITLE, title);
            newNode.setAttribute(getValuePropertyName(), m);
            tree.add(newNode, parentNode);
         }

      }

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
    * @see org.exoplatform.ide.client.restdiscovery.UntypedTreeGrid#setPaths(org.exoplatform.ide.client.framework.discovery.RestService, java.util.List)
    */
   public void setPaths(RestService service, List<?> resources)
   {
      TreeNode node = getNode(rootNode, service.getFullPath());
      //      getNodeByPath(service.getFullPath());
      for (TreeNode childNode : tree.getChildren(node))
      {
         tree.remove(childNode);
      }
      fillServiceTree(node, resources);
      sort();
   }

   private TreeNode getNode(TreeNode parent, String name)
   {
      TreeNode[] rootResources = tree.getChildren(parent);
      for (TreeNode node : rootResources)
      {
         if (node.getName().equals(name))
            return node;
         else
         {
            TreeNode nod = getNode(node, name);
            if (nod != null)
               return nod;
         }
      }
      return null;
   }

   /**
    * @see com.google.gwt.event.logical.shared.HasSelectionHandlers#addSelectionHandler(com.google.gwt.event.logical.shared.SelectionHandler)
    */
   public HandlerRegistration addSelectionHandler(SelectionHandler<Object> handler)
   {
      return super.addSelectionChangedHandler(new SelectionHandlerImpl<Object>(handler, getValuePropertyName()));
   }

   /**
    * @see com.google.gwt.event.dom.client.HasClickHandlers#addClickHandler(com.google.gwt.event.dom.client.ClickHandler)
    */
   public HandlerRegistration addClickHandler(ClickHandler handler)
   {
      return super.addClickHandler(new ClickHandlerImpl(handler));
   }

}
