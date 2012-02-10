/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.eclipse.jdt.client.outline;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.TreeViewModel;

import org.eclipse.jdt.client.core.dom.ASTNode;
import org.eclipse.jdt.client.core.dom.EnumDeclaration;
import org.eclipse.jdt.client.core.dom.FieldDeclaration;
import org.eclipse.jdt.client.core.dom.ImportDeclaration;
import org.eclipse.jdt.client.core.dom.MethodDeclaration;
import org.eclipse.jdt.client.core.dom.PackageDeclaration;
import org.eclipse.jdt.client.core.dom.TypeDeclaration;

import java.util.ArrayList;
import java.util.List;

/**
 * Outline tree model.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Feb 6, 2012 4:59:06 PM anya $
 * 
 */
public class OutlineTreeViewModel implements TreeViewModel
{

   /**
    * Custom cell for displaying Outline nodes.
    */
   public static class AstCell extends AbstractCell<Object>
   {
      /**
       * Visitor to create widgets.
       */
      private CreateWidgetVisitor createWidgetVisitor = new CreateWidgetVisitor();

      /**
       * @see com.google.gwt.cell.client.AbstractCell#render(com.google.gwt.cell.client.Cell.Context, java.lang.Object,
       *      com.google.gwt.safehtml.shared.SafeHtmlBuilder)
       */
      @Override
      public void render(com.google.gwt.cell.client.Cell.Context context, Object value, SafeHtmlBuilder sb)
      {
         if (value instanceof ASTNode)
         {
            getNodeType((ASTNode)value, sb);
         }
         else if (value instanceof ImportGroupNode)
         {
            createWidgetVisitor.visit((ImportGroupNode)value);
            sb.append(createWidgetVisitor.getHTML().toSafeHtml());
         }

      }

      /**
       * Append widget to cell of the specified AST node.
       * 
       * @param node
       * @param buf
       */
      private void getNodeType(ASTNode node, SafeHtmlBuilder buf)
      {
         if (node instanceof PackageDeclaration)
         {
            createWidgetVisitor.visit((PackageDeclaration)node);
         }
         else if (node instanceof ImportDeclaration)
         {
            createWidgetVisitor.visit((ImportDeclaration)node);
         }
         else if (node instanceof TypeDeclaration)
         {
            createWidgetVisitor.visit((TypeDeclaration)node);
         }
         else if (node instanceof MethodDeclaration)
         {
            createWidgetVisitor.visit((MethodDeclaration)node);
         }
         else if (node instanceof FieldDeclaration)
         {
            createWidgetVisitor.visit((FieldDeclaration)node);
         }
         else if (node instanceof EnumDeclaration)
         {
            createWidgetVisitor.visit((EnumDeclaration)node);
         }

         buf.append(createWidgetVisitor.getHTML().toSafeHtml());
      }
   }

   /**
    * Tree data provider.
    */
   private ListDataProvider<Object> dataProvider = new ListDataProvider<Object>();

   /**
    * Visitor for collecting child nodes.
    */
   private GetChildrenVisitor getChildrenVisitor = new GetChildrenVisitor();

   private SingleSelectionModel<Object> selectionModel;

   /**
    * @param compilationUnit complilation unit to display
    */
   public OutlineTreeViewModel(SingleSelectionModel<Object> selectionModel)
   {
      this.selectionModel = selectionModel;
   }

   /**
    * @see com.google.gwt.view.client.TreeViewModel#getNodeInfo(java.lang.Object)
    */
   @Override
   public <T> NodeInfo<?> getNodeInfo(T value)
   {
      if (value == null)
      {
         return new DefaultNodeInfo<Object>(dataProvider, new AstCell(), selectionModel, null);
      }
      if (value instanceof ImportGroupNode)
      {
         return new DefaultNodeInfo<Object>(new ListDataProvider<Object>(((ImportGroupNode)value).getImports()),
            new AstCell(), selectionModel, null);
      }
      else
      {
         return new DefaultNodeInfo<Object>(new ListDataProvider<Object>(getChildren((ASTNode)value)), new AstCell(),
            selectionModel, null);
      }
   }

   /**
    * @see com.google.gwt.view.client.TreeViewModel#isLeaf(java.lang.Object)
    */
   @Override
   public boolean isLeaf(Object value)
   {
      // root
      if (value == null)
         return false;

      if (value instanceof ASTNode)
      {
         return getChildren((ASTNode)value).isEmpty();
      }
      return false;
   }

   /**
    * Get child nodes of the AST node.
    * 
    * @param parent
    * @return {@link List}
    */
   protected List<Object> getChildren(ASTNode parent)
   {
      if (parent instanceof TypeDeclaration)
      {
         getChildrenVisitor.visit((TypeDeclaration)parent);
         return getChildrenVisitor.getNodes();
      }
      else if (parent instanceof EnumDeclaration)
      {
         getChildrenVisitor.visit((EnumDeclaration)parent);
         return getChildrenVisitor.getNodes();
      }

      return new ArrayList<Object>();
   }

   public ListDataProvider<Object> getDataProvider()
   {
      return dataProvider;
   }
}
