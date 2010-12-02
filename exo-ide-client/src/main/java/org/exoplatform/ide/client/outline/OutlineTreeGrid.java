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

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.editor.api.Token;
import org.exoplatform.gwtframework.editor.api.Token.TokenType;
import org.exoplatform.gwtframework.ui.client.smartgwt.component.TreeGrid;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.framework.vfs.File;

import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.TreeModelType;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGridField;
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
   
   private static final String NAME = "name";

   private Tree tree;

   private TreeNode rootNode;

   public OutlineTreeGrid(String id)
   {
      setID(id);
      setSelectionType(SelectionStyle.SINGLE);
      
      // setCanFocus(false);  // to fix bug IDE-258 "Enable navigation by using keyboard in the Navigation, Search and Outline Panel to improve IDE accessibility."
      
      setShowConnectors(true);
      setCanSort(false);
      setCanEdit(false);
      setShowRoot(false);
      setFixedFieldWidths(false);
      setIconSize(16);
      
      tree = new Tree();
      tree.setModelType(TreeModelType.CHILDREN);
      rootNode = new TreeNode("root");
      tree.setRoot(rootNode);
      setData(tree);
      
      TreeGridField nameField = new TreeGridField(NAME);
      //TODO
      //This field need for selenium.
      //We can't select tree node, if click on first column.
      //If you click on second column - tree item is selected.
      TreeGridField mockField = new TreeGridField("mock");
      mockField.setWidth(3);
      setFields(nameField, mockField);
   }

   @Override
   protected void doUpdateValue()
   {
      if (getValue() != null && getValue().getSubTokenList() != null 
               && getValue().getSubTokenList().size() > 0)
      {
         fillTreeItems(rootNode, getValue().getSubTokenList());
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
               newNode = new TreeNode();
               newNode.setAttribute(getValuePropertyName(), child);
               newNode.setAttribute(ICON, getTokenItem(child));
               newNode.setAttribute(NAME, getTokenDisplayTitle(child));
               tree.add(newNode, parentNode);
            }
            
            if (child.getSubTokenList() != null && child.getSubTokenList().size() > 0)
            {
               fillTreeItems(newNode, child.getSubTokenList());
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
         String annotationList = getAnnotationList(token);
         String deprecateSign = (isDeprecated) ? "style='text-decoration:line-through;'" : "";
         name = getModifiersContainer(token) + "<span "+deprecateSign+" title=\"" + annotationList + "\">&nbsp;&nbsp;"  + name + "</span>";
         
         if (TokenType.METHOD.equals(token.getType()))
         {
            name += getParametersList(token);
         }
         //Field type or method return type:
         name += "<span style='color:#644a17;' title=\"" + annotationList + "\">" + getJavaType(token) + "</span>";
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
      if (token.getAnnotations() == null) return false;
      
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
   private String getTokenItem(Token token)
   {
      if (MimeType.APPLICATION_GROOVY.equals(token.getMimeType()))
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
      boolean isPrivate = token.getModifiers().contains(Token.Modifier.PRIVATE);
      boolean isProtected = token.getModifiers().contains(Token.Modifier.PROTECTED);
      boolean isPublic = token.getModifiers().contains(Token.Modifier.PUBLIC);
      
      switch (token.getType())
      {
         case VARIABLE :
         case METHOD :
            if (isPrivate)
            {
               return PRIVATE_METHOD_ICON;
            }
            else if (isProtected)
            {
               return PROTECTED_METHOD_ICON;
            }
            else if (isPublic)
            {
               return PUBLIC_METHOD_ICON;
            }
            else
            {
               return DEFAULT_METHOD_ICON;
            }
         case PROPERTY :
            if (isPrivate)
            {
               return PRIVATE_FIELD_ICON;
            }
            else if (isProtected)
            {
               return PROTECTED_FIELD_ICON;
            }
            else if (isPublic)
            {
               return PUBLIC_FIELD_ICON;
            }
            else
            {
               return DEFAULT_FIELD_ICON;
            }
         case CLASS :
            return CLASS_ICON;
         default :
            return "";
      }
   }
   
   /**
    * @param token {@link Token} 
    * @return html element with modifers
    */
   private String getModifiersContainer(Token token){
      //Get modifiers:
      boolean isStatic = token.getModifiers().contains(Token.Modifier.STATIC);
      boolean isFinal =  token.getModifiers().contains(Token.Modifier.FINAL);
      boolean isAbstract = token.getModifiers().contains(Token.Modifier.ABSTRACT);
      //Get annotation list like string:
      String annotationList = getAnnotationList(token);
      
      //Count size for better align the html elments:
      int size = (annotationList.length() > 0) ? 28: 22;
      
      String span = "<span style = \"position: absolute; margin-top: -5px; margin-left: -25px; width: "+size+"px; height: 10px; font-family: Verdana,Bitstream Vera Sans,sans-serif; font-size: 9px; \">";
      span += (annotationList.length() > 0) ? "<font color ='#000000' style='float: right;'>@</font>" : "";
      span += (isAbstract) ? "<font color ='#004e00' style='float: right;'>A</font>" : "";
      span += (isFinal) ? "<font color ='#174c83' style='float: right;'>f</font>" : "";
      span += (isStatic) ? "<font color ='#6d0000' style='float: right;'>s</font>" : "";
      span += "</span>";
      return span;
   }
   
   
   
   /**
    * @param annotationList 
    * @return HTML code to display "@" sign near the groovy token if annotationList is not empty, or "" otherwise
    */
   private static  final String getAnnotationSign(String annotationList)
   {
      return (! annotationList.isEmpty() ? "<span style = \"font-family: symbol, 'Standard Symbols L' , Verdana; color: #525252; width: 9px; height: 9 px; position: absolute; margin-top: -5px;\">@</span>&nbsp;&nbsp;&nbsp;" : "");
   }
   
   public void selectToken(Token token)
   {
      if (token.getName() == null) return;
      
      final String name = token.getName(); 
      final int lineNumber = token.getLineNumber();
      
      TreeNode selectedNode = null;
      
      //find node and open all parents
      for (TreeNode node : tree.getAllNodes())
      {
         Token nodeToken = (Token)node.getAttributeAsObject(getValuePropertyName());
         if (nodeToken.getName().equals(name) && nodeToken.getLineNumber() == lineNumber)
         {
            tree.openFolder(node);
            TreeNode parent = tree.getParent(node);
            while (parent != null)
            {
               tree.openFolder(parent);
               parent = tree.getParent(parent);
            }
            selectedNode = node;
            break;
         }
      }
      
      //select opened record
      if (selectedNode != null)
      {
         for (ListGridRecord record : getRecords())
         {
            if (record.getAttributeAsObject(getValuePropertyName()) instanceof Token)
            {
               Token  currentToken =  (Token)record.getAttributeAsObject(getValuePropertyName());
               if (name.equals(currentToken.getName()) && lineNumber == currentToken.getLineNumber())
               {
                  selectSingleRecord(record);
                  return;
               }
            }
         }
      }
   }
   
   public static boolean haveOutline(File file)
   {
      return file.getContentType().equals(MimeType.APPLICATION_JAVASCRIPT)
         || file.getContentType().equals(MimeType.APPLICATION_X_JAVASCRIPT)
         || file.getContentType().equals(MimeType.GOOGLE_GADGET)
         || file.getContentType().equals(MimeType.TEXT_JAVASCRIPT)
         || file.getContentType().equals(MimeType.APPLICATION_XML) || file.getContentType().equals(MimeType.TEXT_XML)
         || file.getContentType().equals(MimeType.TEXT_HTML) || file.getContentType().equals(MimeType.GROOVY_SERVICE)
         || file.getContentType().equals(MimeType.APPLICATION_GROOVY)
         || file.getContentType().equals(MimeType.GROOVY_TEMPLATE);
   }
   
   public List<Token> getSelectedTokens()
   {
      List<Token> selectedItems = new ArrayList<Token>();

      for (ListGridRecord record : getSelection())
      {
         selectedItems.add((Token)record.getAttributeAsObject(getValuePropertyName()));
      }

      return selectedItems;
   }

   /**
    * get formatted string with java type from token.getJavaType() like " : java.lang.String"
    * @param token
    * @return string like " : java.lang.String", or "".
    */
   private String getJavaType(Token token)
   {
      if (token.getJavaType() != null)
      {
         return " : " + token.getJavaType();                  
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
            
            parametersDescription += "<span title=\"" + annotationList + "\">" + getAnnotationSign(annotationList) + parameter.getJavaType() + "</span>";
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

}
