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
package org.exoplatform.ideall.client.outline;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.editor.api.Token;
import org.exoplatform.gwtframework.editor.api.Token.TokenType;
import org.exoplatform.gwtframework.ui.client.smartgwt.component.TreeGrid;
import org.exoplatform.ideall.client.Images;
import org.exoplatform.ideall.client.module.vfs.api.File;

import java.util.List;

import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.TreeModelType;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeNode;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class OutlineTreeGrid<T extends Token> extends TreeGrid<T>
{

   private static final String ICON = "icon";

   private static final String VAR_ICON = Images.Outline.VAR_ITEM;

   private static final String FUNCTION_ICON = Images.Outline.FUNCTION_ITEM;

   private static final String METHOD_ICON = Images.Outline.METHOD_ITEM;

   private static final String PROPERTY_ICON = Images.Outline.PROPERTY_ITEM;
   
   private static final String TAG_ICON = Images.Outline.TAG_ITEM;
   
   private static final String CDATA_ICON = Images.Outline.CDATA_ITEM;
   
   protected static String NAME_ATTRIBUTE;

   private Tree tree;

   private TreeNode rootNode;

   public OutlineTreeGrid()
   {
      setSelectionType(SelectionStyle.SINGLE);
      
      setCanFocus(false);
      setShowConnectors(true);
      setCanSort(false);
      setCanEdit(false);
      setShowRoot(false);
      
      tree = new Tree();
      tree.setModelType(TreeModelType.CHILDREN);
      rootNode = new TreeNode("root");
      tree.setRoot(rootNode);
      setData(tree);
   }

   @Override
   protected void doUpdateValue()
   {
      if (getValue() != null && getValue().getSubTokenList() != null 
               && getValue().getSubTokenList().size() > 0)
      {
         fillTreeItems(rootNode, getValue().getSubTokenList());
//         tree.openAll();
      }
      else
      {
         TreeNode[] oldNodes = tree.getChildren(rootNode);
         tree.removeList(oldNodes);
      }

   }

   private void fillTreeItems(TreeNode parentNode, List<Token> children)
   {
      TreeNode[] oldNodes = tree.getChildren(parentNode);
      tree.removeList(oldNodes);
      for (Token child : children)
      {
         TreeNode newNode = null;
         TreeNode[] nodes = tree.getChildren(parentNode);
         for (TreeNode node : nodes)
         {
            if (node.getAttributeAsObject(getValuePropertyName()) == child)
            {
               newNode = node;
               break;
            }
         }
         
         if (child.getName() != null)
         {
            if (newNode == null)
            {
               newNode = new TreeNode(child.getName());
               newNode.setAttribute(getValuePropertyName(), child);
               if (child.getType().equals(TokenType.FUNCTION))
               {
                  newNode.setAttribute(ICON, FUNCTION_ICON);
               }
               else if (child.getType().equals(TokenType.VARIABLE))
               {
                  newNode.setAttribute(ICON, VAR_ICON);
               }
               else if (child.getType().equals(TokenType.METHOD))
               {
                  newNode.setAttribute(ICON, METHOD_ICON);
               }
               else if (child.getType().equals(TokenType.PROPERTY))
               {
                  newNode.setAttribute(ICON, PROPERTY_ICON);
               }
               else if (child.getType().equals(TokenType.TAG))
               {
                  newNode.setAttribute(ICON, TAG_ICON);
               }
               else if (child.getType().equals(TokenType.CDATA))
               {
                  newNode.setAttribute(ICON, CDATA_ICON);
               }
               tree.add(newNode, parentNode);
            }
            
            if (child.getSubTokenList() != null && child.getSubTokenList().size() > 0)
            {
               fillTreeItems(newNode, child.getSubTokenList());
            }
         }
      }
   }
   
   public void selectToken(Token token)
   {
      if (token.getName() == null) return;
      
      final String name = token.getName(); 
      final int lineNumber = token.getLineNumber();
      
      deselectAllRecords();
      
      for (ListGridRecord record : getRecords())
      {
         if (record.getAttributeAsObject(getValuePropertyName()) instanceof Token)
         {
            Token  currentToken =  (Token)record.getAttributeAsObject(getValuePropertyName());
            if (name.equals(currentToken.getName()) && lineNumber == currentToken.getLineNumber())
            {
               selectRecord(record);
               return;
            }
         }
      }
   }
   
   public void openToken(Token token)
   {
      final String name = token.getName(); 
      final int lineNumber = token.getLineNumber();
      
      for (TreeNode node : tree.getAllNodes())
      {
         Token nodeToken = (Token)node.getAttributeAsObject(getValuePropertyName());
         if (nodeToken.getName().equals(name) && nodeToken.getLineNumber() == lineNumber)
         {
            tree.openFolder(node);
            break;
         }
      }
   }
   
   public static boolean haveOutline(File file)
   {
      return file.getContentType().equals(MimeType.APPLICATION_JAVASCRIPT)
         || file.getContentType().equals(MimeType.APPLICATION_X_JAVASCRIPT)
         || file.getContentType().equals(MimeType.GOOGLE_GADGET)
         || file.getContentType().equals(MimeType.TEXT_JAVASCRIPT)
         || file.getContentType().equals(MimeType.APPLICATION_XML)
         || file.getContentType().equals(MimeType.TEXT_XML)
         || file.getContentType().equals(MimeType.TEXT_HTML);
   }

}
