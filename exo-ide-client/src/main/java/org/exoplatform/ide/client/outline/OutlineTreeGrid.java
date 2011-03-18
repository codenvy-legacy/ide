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
package org.exoplatform.ide.client.outline;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.editor.api.Token;
import org.exoplatform.gwtframework.editor.api.Token.TokenType;
import org.exoplatform.gwtframework.ui.client.util.UIHelper;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.framework.vfs.File;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class OutlineTreeGrid<T extends Token> extends org.exoplatform.gwtframework.ui.client.component.Tree<T>
{

   private static final String VAR_ICON = Images.Outline.VAR_ITEM;

   private static final String FUNCTION_ICON = Images.Outline.FUNCTION_ITEM;

   private static final String METHOD_ICON = Images.Outline.METHOD_ITEM;

   private static final String PRIVATE_METHOD_ICON = Images.Outline.PRIVATE_METHOD;

   private static final String PUBLIC_METHOD_ICON = Images.Outline.PUBLIC_METHOD;

   private static final String PROTECTED_METHOD_ICON = Images.Outline.PROTECTED_METHOD;

   private static final String DEFAULT_METHOD_ICON = Images.Outline.DEFAULT_METHOD;

   private static final String PRIVATE_FIELD_ICON = Images.Outline.PRIVATE_FIELD;

   private static final String PUBLIC_FIELD_ICON = Images.Outline.PUBLIC_FIELD;

   private static final String PROTECTED_FIELD_ICON = Images.Outline.PROTECTED_FIELD;

   private static final String DEFAULT_FIELD_ICON = Images.Outline.DEFAULT_FIELD;

   private static final String PROPERTY_ICON = Images.Outline.PROPERTY_ITEM;

   private static final String TAG_ICON = Images.Outline.TAG_ITEM;

   private static final String CDATA_ICON = Images.Outline.CDATA_ITEM;

   private static final String GROOVY_TAG_ICON = Images.Outline.GROOVY_TAG_ITEM;

   private static final String CLASS_ICON = Images.Outline.CLASS_ITEM;

   private static final String OBJECT_ICON = Images.Outline.OBJECT_ITEM;

   private static final String ARRAY_ICON = Images.Outline.ARRAY_ITEM;

   private static final String DATA_ICON = Images.Outline.DATA_ITEM;

   private static final String ERROR_ICON = Images.Outline.ERROR_ITEM;

   private static final String INTERFACE_ICON = Images.Outline.INTERFACE_ITEM;

   public OutlineTreeGrid(String id)
   {
      getElement().setId(id);
   }

   /**
    * @see org.exoplatform.gwtframework.ui.client.component.Tree#doUpdateValue()
    */
   @Override
   public void doUpdateValue()
   {
      if (value == null)
         return;

      if (value.getName() == null || value.getType() == null)
      {
         fillTreeItems(value.getSubTokenList());
         return;
      }
      else
      {
         TreeItem addItem = tree.addItem(createItemWidget(getTokenIcon(value), getTokenDisplayTitle(value)));
         addItem.setUserObject(value);
         fillTreeItems(addItem, getValue().getSubTokenList());
      }
   }

   /**
    * Create tree nodes of pointed parent and its child nodes.
    * 
    * @param parentNode
    * @param children
    */
   private void fillTreeItems(TreeItem parentNode, List<Token> children)
   {
      if (parentNode == null || children == null)
         return;
      //Clear parent node children:
      parentNode.removeItems();
      for (Token child : children)
      {
         if (child != null && child.getName() != null && child.getType() != null)
         {
            TreeItem node = parentNode.addItem(createItemWidget(getTokenIcon(child), getTokenDisplayTitle(child)));
            node.setUserObject(child);
            if (child.getSubTokenList() != null && child.getSubTokenList().size() > 0)
            {
               fillTreeItems(node, child.getSubTokenList());
            }
         }
      }
   }

   /**
    * Create tree nodes in the root of the tree.
    * 
    * @param children
    */
   private void fillTreeItems(List<Token> children)
   {
      if (children == null)
         return;
      tree.removeItems();
      for (Token child : children)
      {
         if (child != null && child.getName() != null && child.getType() != null)
         {
            TreeItem node = tree.addItem(createItemWidget(getTokenIcon(child), getTokenDisplayTitle(child)));
            node.setUserObject(child);
            if (child.getSubTokenList() != null && child.getSubTokenList().size() > 0)
            {
               fillTreeItems(node, child.getSubTokenList());
            }
         }
      }
   }

   /**
    * Get the string to display token.
    * 
    * @param token token to display
    * @return {@link String} display string of the token
    */
   private String getTokenDisplayTitle(Token token)
   {
      String name = token.getName();
      boolean isDeprecated = isDeprecated(token);
      // add info about java type, parameters and annotations
      if (MimeType.APPLICATION_GROOVY.equals(token.getMimeType()))
      {
         //icon, that displays in right bottom corner, if token is CLASS, 
         //and shows access modifier
         String modfImg = "";

         if (TokenType.CLASS.equals(token.getType()) || TokenType.INTERFACE.equals(token.getType()))
         {
            if (isPrivate(token))
            {
               modfImg =
                  "<img id=\"resourceLocked\" style=\"position:absolute; margin-left:-10px; margin-top:8px;\"  border=\"0\""
                     + " suppress=\"TRUE\" src=\"" + UIHelper.getGadgetImagesURL() + "outline/class-private.png"
                     + "\" />";
            }
            else if (isProtected(token))
            {
               modfImg =
                  "<img id=\"resourceLocked\" style=\"position:absolute; margin-left:-10px; margin-top:8px;\"  border=\"0\""
                     + " suppress=\"TRUE\" src=\"" + UIHelper.getGadgetImagesURL() + "outline/class-protected.png"
                     + "\" />";
            }
            else if (isPublic(token))
            {
            }
            else
            {
               modfImg =
                  "<img id=\"resourceLocked\" style=\"position:absolute; margin-left:-10px; margin-top:8px;\"  border=\"0\""
                     + " suppress=\"TRUE\" src=\"" + UIHelper.getGadgetImagesURL() + "outline/class-default.png"
                     + "\" />";
            }
         }

         String synchImg = "";
         if (isSynchronized(token))
         {
            final String marginLeft = modfImg.length() > 0 ? "-3" : "-10";
            synchImg =
               "<img id=\"resourceLocked\" style=\"position:absolute; margin-left:" + marginLeft
                  + "px; margin-top:8px;\"  border=\"0\"" + " suppress=\"TRUE\" src=\"" + UIHelper.getGadgetImagesURL()
                  + "outline/clock.png" + "\" />";
         }
         String annotationList = getAnnotationList(token);
         String deprecateSign = (isDeprecated) ? "style='text-decoration:line-through;'" : "";
         name =
            getModifiersContainer(token) + modfImg + synchImg + "<span " + deprecateSign + " title=\"" + annotationList
               + "\">&nbsp;&nbsp;" + name + "</span>";

         if (TokenType.METHOD.equals(token.getType()))
         {
            name += getParametersList(token);
         }
         //Field type or method return type:
         name += "<span style='color:#644a17;' title=\"" + annotationList + "\">" + getElementType(token) + "</span>";
      }

      // display type of javascript variables
      else if (MimeType.APPLICATION_JAVASCRIPT.equals(token.getMimeType()) && token.getElementType() != null)
      {
         name += "<span style='color:#644a17;'>" + getElementType(token) + "</span>";
      }
      return name;
   }

   /**
    * Checks, whether method has deprecated annotation.
    * 
    * @param token method
    * @return boolean whether method is deprecated
    */
   private boolean isDeprecated(Token token)
   {
      if (token.getAnnotations() == null)
         return false;

      for (Token annotation : token.getAnnotations())
      {
         if ("@deprecated".equalsIgnoreCase(annotation.getName()))
         {
            return true;
         }
      }
      return false;
   }

   /**
    * Get icon for token.
    * 
    * @param token token
    * @return icon
    */
   private String getTokenIcon(Token token)
   {
      if (MimeType.APPLICATION_GROOVY.equals(token.getMimeType()) && !TokenType.GROOVY_TAG.equals(token.getType()))
      {
         return getIconForJavaFiles(token);
      }
      switch (token.getType())
      {
         case FUNCTION :
            return FUNCTION_ICON;

         case VARIABLE :
            return VAR_ICON;

         case METHOD :
            return METHOD_ICON;

         case PROPERTY :
            return PROPERTY_ICON;

         case TAG :
            return TAG_ICON;

         case CDATA :
            return CDATA_ICON;

         case GROOVY_TAG :
            return GROOVY_TAG_ICON;

         case CLASS :
            return CLASS_ICON;

         case OBJECT :
            return OBJECT_ICON;

         case ARRAY :
            return ARRAY_ICON;

         case NUMBER :
         case BOOLEAN :
         case STRING :
         case NULL :
            return DATA_ICON;

         case ERROR :
            return ERROR_ICON;

         case INTERFACE :
            return INTERFACE_ICON;

         default :
            return "";
      }
   }

   /**
    * Forms the icon for java files (groovy, POJO, etc)
    * 
    * @return {@link String} icon
    */
   private String getIconForJavaFiles(Token token)
   {
      switch (token.getType())
      {
         case VARIABLE :
         case METHOD :
            if (isPrivate(token))
            {
               return PRIVATE_METHOD_ICON;
            }
            else if (isProtected(token))
            {
               return PROTECTED_METHOD_ICON;
            }
            else if (isPublic(token))
            {
               return PUBLIC_METHOD_ICON;
            }
            else
            {
               return DEFAULT_METHOD_ICON;
            }

         case PROPERTY :
            if (isPrivate(token))
            {
               return PRIVATE_FIELD_ICON;
            }
            else if (isProtected(token))
            {
               return PROTECTED_FIELD_ICON;
            }
            else if (isPublic(token))
            {
               return PUBLIC_FIELD_ICON;
            }
            else
            {
               return DEFAULT_FIELD_ICON;
            }

         case CLASS :
            return CLASS_ICON;

         case INTERFACE :
            return INTERFACE_ICON;

         default :
            return "";
      }
   }

   /**
    * @param token {@link Token} 
    * @return html element with modifers
    */
   private String getModifiersContainer(Token token)
   {
      //Get annotation list like string:
      String annotationList = getAnnotationList(token);

      //Count size for better align the html elments:
      int size = (annotationList.length() > 0) ? 28 : 22;

      String span =
         "<span style = \"position: absolute; margin-top: -5px; margin-left: -25px; width: " + size
            + "px; height: 10px; font-family: Verdana,Bitstream Vera Sans,sans-serif; font-size: 9px; \">";
      span += (annotationList.length() > 0) ? "<font color ='#000000' style='float: right;'>@</font>" : "";
      span += (isAbstract(token)) ? "<font color ='#004e00' style='float: right;'>a</font>" : "";
      span += (isFinal(token)) ? "<font color ='#174c83' style='float: right;'>f</font>" : "";
      span += (isStatic(token)) ? "<font color ='#6d0000' style='float: right;'>s</font>" : "";
      span += (isVolative(token)) ? "<font color ='#6d0000' style='float: right;'>v</font>" : "";
      span += (isTransient(token)) ? "<font color ='#6d0000' style='float: right;'>t</font>" : "";
      span += "</span>";
      return span;
   }

   private boolean isFinal(Token token)
   {
      return token.getModifiers() != null && token.getModifiers().contains(Token.Modifier.FINAL);
   }

   private boolean isAbstract(Token token)
   {
      return token.getModifiers() != null && token.getModifiers().contains(Token.Modifier.ABSTRACT);
   }

   private boolean isTransient(Token token)
   {
      return token.getModifiers() != null && token.getModifiers().contains(Token.Modifier.TRANSIENT);
   }

   private boolean isVolative(Token token)
   {
      return token.getModifiers() != null && token.getModifiers().contains(Token.Modifier.VOLATILE);
   }

   private boolean isStatic(Token token)
   {
      return token.getModifiers() != null && token.getModifiers().contains(Token.Modifier.STATIC);
   }

   private boolean isProtected(Token token)
   {
      return token.getModifiers() != null && token.getModifiers().contains(Token.Modifier.PROTECTED);
   }

   private boolean isPrivate(Token token)
   {
      return token.getModifiers() != null && token.getModifiers().contains(Token.Modifier.PRIVATE);
   }

   private boolean isPublic(Token token)
   {
      return token.getModifiers() != null && token.getModifiers().contains(Token.Modifier.PUBLIC);
   }

   private boolean isSynchronized(Token token)
   {
      return token.getModifiers() != null && token.getModifiers().contains(Token.Modifier.SYNCHRONIZED);
   }

   /**
    * @param annotationList 
    * @return HTML code to display "@" sign near the groovy token if annotationList is not empty, or "" otherwise
    */
   private static final String getAnnotationSign(String annotationList)
   {
      return (!annotationList.isEmpty()
         ? "<span style = \"font-family: Verdana, Bitstream Vera Sans, sans-serif; color: #525252; width: 9px; height: 9 px; position: absolute; margin-top: -5px;\">@</span>&nbsp;&nbsp;&nbsp;"
         : "");
   }

   /**
    * Select token in the tree.
    * 
    * @param token
    */
   public void selectToken(Token token)
   {
      if (token.getName() == null)
         return;
      TreeItem nodeToSelect = getTreeItemByToken(token);
      if (nodeToSelect == null)
      {
         return;
      }
      TreeItem parent = nodeToSelect.getParentItem();
      while (parent != null)
      {
         parent.setState(true, true);
         parent = parent.getParentItem();
      }
      tree.setSelectedItem(nodeToSelect);
   }

   /**
    * Checks if file's MIME type gives an opportunity to show outline.
    * 
    * @param file
    * @return
    */
   public static boolean haveOutline(File file)
   {
      return file.getContentType().equals(MimeType.APPLICATION_JAVASCRIPT)
         || file.getContentType().equals(MimeType.APPLICATION_X_JAVASCRIPT)
         || file.getContentType().equals(MimeType.GOOGLE_GADGET)
         || file.getContentType().equals(MimeType.TEXT_JAVASCRIPT)
         || file.getContentType().equals(MimeType.APPLICATION_XML) || file.getContentType().equals(MimeType.TEXT_XML)
         || file.getContentType().equals(MimeType.TEXT_HTML) || file.getContentType().equals(MimeType.GROOVY_SERVICE)
         || file.getContentType().equals(MimeType.APPLICATION_GROOVY)
         || file.getContentType().equals(MimeType.GROOVY_TEMPLATE) || file.getContentType().equals(MimeType.UWA_WIDGET)
         || file.getContentType().equals(MimeType.CHROMATTIC_DATA_OBJECT);
   }

   /**
    * Get the the list of selected tokens in outline tree.
    * 
    * @return {@link List}
    */
   public List<Token> getSelectedTokens()
   {
      List<Token> selectedItems = new ArrayList<Token>();
      if (tree.getSelectedItem() != null)
         selectedItems.add((Token)tree.getSelectedItem().getUserObject());
      return selectedItems;
   }

   /**
    * get formatted string with java type from token.getElementType() like " : java.lang.String"
    * @param token
    * @return string like " : java.lang.String", or "".
    */
   private String getElementType(Token token)
   {
      if (token.getElementType() != null)
      {
         return " : " + token.getElementType();
      }
      return "";
   }

   /**
    * Return parameters list from token.getParameters()
    * @param token
    * @return parameters list like '(String, int)', or '()' if there are no parameters
    */
   private String getParametersList(Token token)
   {
      String parametersDescription = "(";

      if (token.getParameters() != null && token.getParameters().size() > 0)
      {

         List<Token> parameters = token.getParameters();

         for (int i = 0; i < parameters.size(); i++)
         {
            Token parameter = parameters.get(i);
            if (i > 0)
            {
               parametersDescription += ", ";
            }

            String annotationList = getAnnotationList(parameter);

            parametersDescription +=
               "<span title=\"" + annotationList + "\">" + getAnnotationSign(annotationList)
                  + parameter.getElementType() + "</span>";
         }
      }

      return parametersDescription + ")";
   }

   /**
    * Return formatted annotation list from token.getAnnotations()
    * @param token
    * @return annotations like '@Path; @PathParam(&#34;name&#34;)' or "", if there are no annotations in the token
    */
   private String getAnnotationList(Token token)
   {
      if (token.getAnnotations() != null && token.getAnnotations().size() > 0)
      {
         String title = "";

         for (Token annotation : token.getAnnotations())
         {
            title += annotation.getName() + "; ";
         }

         // replace all '"' on HTML Entity "&#34;"
         return title.replaceAll("\"", "&#34;");
      }

      return "";
   }

   /**
    * @see org.exoplatform.gwtframework.ui.client.component.Tree#createItemWidget(java.lang.String, java.lang.String)
    */
   @Override
   protected Widget createItemWidget(String icon, String text)
   {
      Grid grid = new Grid(1, 2);
      grid.setWidth("100%");

      Image i = new Image(icon);
      i.setHeight("16px");
      grid.setWidget(0, 0, i);
      Label l = new Label();
      l.getElement().setInnerHTML(text);
      l.setWordWrap(false);
      grid.setWidget(0, 1, l);

      grid.getCellFormatter().setWidth(0, 0, "16px");
      grid.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
      grid.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_LEFT);
      grid.getCellFormatter().setWidth(0, 1, "100%");
      DOM.setStyleAttribute(grid.getElement(), "display", "block");
      return grid;
   }

   /**
    * Find {@link TreeItem} in the whole of the pointed token.
    * 
    * @param token token
    * @return {@link TreeItem}
    */
   private TreeItem getTreeItemByToken(Token token)
   {
      for (int i = 0; i < tree.getItemCount(); i++)
      {
         TreeItem child = tree.getItem(i);
         if (child.getUserObject() == null)
            continue;
         if (((Token)child.getUserObject()).getName().equals(token.getName())
            && ((Token)child.getUserObject()).getLineNumber() == token.getLineNumber())
         {
            return child;
         }
         TreeItem item = getChild(child, token);
         if (item != null)
            return item;
      }
      return null;
   }

   /**
    * Get child tree node of pointed parent, that represents the pointed token.
    * 
    * @param parent parent
    * @param token token
    * @return {@link TreeItem}
    */
   private TreeItem getChild(TreeItem parent, Token token)
   {
      for (int i = 0; i < parent.getChildCount(); i++)
      {
         TreeItem child = parent.getChild(i);
         if (child.getUserObject() == null)
            continue;
         if (((Token)child.getUserObject()).getName().equals(token.getName())
            && ((Token)child.getUserObject()).getLineNumber() == token.getLineNumber())
         {
            return child;
         }
         TreeItem item = getChild(child, token);
         if (item != null)
            return item;
      }
      return null;
   }
}
