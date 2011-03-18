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
package org.exoplatform.ide.client.restdiscovery;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.exoplatform.gwtframework.commons.wadl.Method;
import org.exoplatform.gwtframework.commons.wadl.Request;
import org.exoplatform.gwtframework.commons.wadl.Resource;
import org.exoplatform.gwtframework.ui.client.component.Tree;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.framework.discovery.RestService;

import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: RestServiceTree Mar 18, 2011 10:11:26 AM evgen $
 *
 */
public class RestServiceTree extends Tree<Object> implements UntypedTreeGrid
{

   private Set<String> restClassPaths;

   /**
    * @see org.exoplatform.ide.client.restdiscovery.UntypedTreeGrid#setRootValue(org.exoplatform.ide.client.framework.discovery.RestService, java.util.Set)
    */
   @Override
   public void setRootValue(RestService item, Set<String> restClassPath)
   {
      this.restClassPaths = restClassPath;
      tree.removeItems();
      for (RestService rs : item.getChildServices().values())
      {
         addRestService(null, rs);
      }
      if(tree.getItemCount() > 0)
      {
         tree.getItem(0).setSelected(true);
      }
   }
   
   /**
    * @see org.exoplatform.gwtframework.ui.client.component.Tree#moveHighlight(com.google.gwt.user.client.ui.TreeItem)
    */
   @Override
   protected void moveHighlight(TreeItem currentItem)
   {
      super.moveHighlight(currentItem);
      if(currentItem.getElement().getFirstChildElement().getOffsetWidth() > tree.getOffsetWidth())
      {
         hiPanel.setWidth(currentItem.getElement().getFirstChildElement().getOffsetWidth() + 10+ "px");
      }
      else
      {
         hiPanel.setWidth("100%");
      }
      ((ScrollPanel)getParent()).scrollToLeft();
   }

   private void addRestService(TreeItem parentNode, RestService children)
   {

      RestService r = null;
      if (parentNode != null)
         r = (RestService)parentNode.getUserObject();

      if (r != null)
         children.setFullPath(r.getFullPath() + children.getPath());
      if (children.getPath().endsWith("/"))
      {
         children.setPath(children.getPath().substring(0, children.getPath().lastIndexOf('/')));
      }

      String icon = Images.MainMenu.GET_URL;
      if (restClassPaths.contains(children.getFullPath()))
      {
         icon = Images.RestService.CLASS;
      }

      TreeItem item = getServiceNode(children, icon);
      item.addItem("");
      if (parentNode == null)
      {
         tree.addItem(item);
      }
      else
      {
         parentNode.addItem(item);
         if (parentNode.getChild(0).getUserObject() == null)
         {
            parentNode.getChild(0).remove();
         }
      }

      for (RestService rs : children.getChildServices().values())
      {
         addRestService(item, rs);
      }
   }

   private TreeItem getServiceNode(RestService rs, String icon)
   {
      TreeItem node = new TreeItem(createItemWidget(icon, rs.getPath()));
      node.setUserObject(rs);
      return node;
   }

   /**
    * 
    */
   private void fillServiceTree(TreeItem parentNode, List<?> children)
   {
      for (Object o : children)
      {
         if (o instanceof Resource)
         {
            Resource r = (Resource)o;
            String title = r.getPath();
            RestService rs = (RestService)parentNode.getUserObject();
            if (rs != null)
            {
               if (rs.getFullPath().endsWith("/"))
                  title = title.substring(rs.getFullPath().length() - 1);
               else
                  title = title.substring(rs.getFullPath().length());
            }

            TreeItem newNode = new TreeItem(createItemWidget(Images.RestService.RESOURCE,  title));
            newNode.setUserObject(r);
            parentNode.addItem(newNode);

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
            Object re = parentNode.getUserObject();
            if (re instanceof Resource)
            {
               Resource res = (Resource)re;

               if (res != null)
                  m.getRequest().getParam().addAll(res.getParam());
               m.setHref(((Resource)re).getPath());
            }
            else if (re instanceof RestService)
            {
               m.setHref(((RestService)re).getFullPath());
            }
            TreeItem newNode = new TreeItem(createItemWidget(Images.RestService.METHOD, m.getName()));
            newNode.setUserObject(m);
            parentNode.addItem(newNode);
         }

      }

   }

   /**
    * @see org.exoplatform.ide.client.restdiscovery.UntypedTreeGrid#setPaths(org.exoplatform.ide.client.framework.discovery.RestService, java.util.List)
    */
   @Override
   public void setPaths(RestService service, List<?> resources)
   {
      TreeItem node = null;
      for (int i = 0; i < tree.getItemCount(); i++)
      {
         TreeItem item = tree.getItem(i);
         node = getNode(item, service.getFullPath());
         if (node != null)
            break;
      }
      
      if (node == null)
         return;
      
      node.removeItems();

      fillServiceTree(node, resources);
      //      sort();
   }

   private TreeItem getNode(TreeItem parent, String name)
   {

      Object o = parent.getUserObject();
      if(o != null && o instanceof RestService)
      {
         if(((RestService)o).getFullPath().equals(name))
         {
            return parent;
         }
      }
      for (int i = 0; i < parent.getChildCount(); i++)
      {
         TreeItem child = parent.getChild(i);
         if (child.getUserObject() == null)
            continue;
         if (!(child.getUserObject() instanceof RestService))
            continue;
         if (((RestService)child.getUserObject()).getFullPath().equals(name))
         {
            return child;
         }
         TreeItem item = getNode(child, name);
         if (item != null)
            return item;
      }

      return null;
   }

   /**
    * @see org.exoplatform.ide.client.component.Tree#doUpdateValue()
    */
   @Override
   public void doUpdateValue()
   {
   }

}
