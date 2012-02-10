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

import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Image;

import org.eclipse.jdt.client.core.dom.ASTVisitor;
import org.eclipse.jdt.client.core.dom.EnumDeclaration;
import org.eclipse.jdt.client.core.dom.FieldDeclaration;
import org.eclipse.jdt.client.core.dom.ImportDeclaration;
import org.eclipse.jdt.client.core.dom.MethodDeclaration;
import org.eclipse.jdt.client.core.dom.Modifier;
import org.eclipse.jdt.client.core.dom.PackageDeclaration;
import org.eclipse.jdt.client.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.client.core.dom.TypeDeclaration;
import org.eclipse.jdt.client.core.dom.VariableDeclarationFragment;
import org.exoplatform.ide.editor.java.client.JavaClientBundle;

import java.util.Iterator;

/**
 * Visitor is used for creating HTML code of widgets, that represent the AST nodes of different types.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Feb 7, 2012 12:10:16 PM anya $
 * 
 */
public class CreateWidgetVisitor extends ASTVisitor
{
   /**
    * HTML code of the widget.
    */
   private SafeHtmlBuilder html;

   /**
    * @see org.eclipse.jdt.client.core.dom.ASTVisitor#visit(org.eclipse.jdt.client.core.dom.PackageDeclaration)
    */
   @Override
   public boolean visit(PackageDeclaration node)
   {
      html = new SafeHtmlBuilder();
      html.appendHtmlConstant(getMainImage(JavaClientBundle.INSTANCE.packageItem()).toString());
      html.appendHtmlConstant(getTitleElement(node.getName().getFullyQualifiedName()).getString());
      return true;
   }

   /**
    * @see org.eclipse.jdt.client.core.dom.ASTVisitor#visit(org.eclipse.jdt.client.core.dom.TypeDeclaration)
    */
   @Override
   public boolean visit(TypeDeclaration node)
   {
      html = new SafeHtmlBuilder();
      int modifiers = node.getModifiers();

      // Check type is Class or Interface:
      Image image =
         node.isInterface() ? getMainImage(JavaClientBundle.INSTANCE.interfaceItem())
            : getMainImage(JavaClientBundle.INSTANCE.classItem());

      html.appendHtmlConstant(image.toString());
      html.appendHtmlConstant(getModifiersContainer(modifiers));

      Image modifImage = null;

      // Get the access modifier icon:
      if (Modifier.isPrivate(modifiers))
      {
         modifImage = new Image(JavaClientBundle.INSTANCE.classPrivateItem());
      }
      else if (Modifier.isProtected(modifiers))
      {
         modifImage = new Image(JavaClientBundle.INSTANCE.classProtectedItem());
      }
      else if (Modifier.isPublic(modifiers))
      {
      }
      else
      {
         modifImage = new Image(JavaClientBundle.INSTANCE.classDefaultItem());
      }

      if (modifImage != null)
      {
         // TODO
         DOM.setStyleAttribute(modifImage.getElement(), "position", "absolute");
         DOM.setStyleAttribute(modifImage.getElement(), "marginLeft", "-10px");
         DOM.setStyleAttribute(modifImage.getElement(), "marginTop", "8px");
         modifImage.getElement().setAttribute("border", "0");
         html.appendHtmlConstant(modifImage.toString());
      }

      html.appendHtmlConstant(getTitleElement(node.getName().getIdentifier()).getString());
      return true;
   }

   /**
    * @see org.eclipse.jdt.client.core.dom.ASTVisitor#visit(org.eclipse.jdt.client.core.dom.EnumDeclaration)
    */
   @Override
   public boolean visit(EnumDeclaration node)
   {
      html = new SafeHtmlBuilder();
      int modifiers = node.getModifiers();
      Image image = getMainImage(JavaClientBundle.INSTANCE.enumItem());
      html.appendHtmlConstant(image.toString());
      html.appendHtmlConstant(getModifiersContainer(modifiers));

      Image modifImage = null;

      // Get the access modifier icon:
      if (Modifier.isPrivate(modifiers))
      {
         modifImage = new Image(JavaClientBundle.INSTANCE.classPrivateItem());
      }
      else if (Modifier.isProtected(modifiers))
      {
         modifImage = new Image(JavaClientBundle.INSTANCE.classProtectedItem());
      }
      else if (Modifier.isPublic(modifiers))
      {
      }
      else
      {
         modifImage = new Image(JavaClientBundle.INSTANCE.classDefaultItem());
      }

      if (modifImage != null)
      {
         DOM.setStyleAttribute(modifImage.getElement(), "position", "absolute");
         DOM.setStyleAttribute(modifImage.getElement(), "marginLeft", "-10px");
         DOM.setStyleAttribute(modifImage.getElement(), "marginTop", "8px");
         modifImage.getElement().setAttribute("border", "0");
         html.appendHtmlConstant(modifImage.toString());
      }

      html.appendHtmlConstant(getTitleElement(node.getName().getIdentifier()).getString());
      return true;
   }

   /**
    * @see org.eclipse.jdt.client.core.dom.ASTVisitor#visit(org.eclipse.jdt.client.core.dom.ImportDeclaration)
    */
   @Override
   public boolean visit(ImportDeclaration node)
   {
      html = new SafeHtmlBuilder();
      html.appendHtmlConstant(getMainImage(JavaClientBundle.INSTANCE.importItem()).toString());
      html.appendHtmlConstant(getTitleElement(node.getName().getFullyQualifiedName()).getString());
      return true;
   }

   /**
    * @param node import group node
    * @return {@link Boolean}
    */
   public boolean visit(ImportGroupNode node)
   {
      html = new SafeHtmlBuilder();
      html.appendHtmlConstant(getMainImage(JavaClientBundle.INSTANCE.imports()).toString());
      html.appendHtmlConstant(getTitleElement(node.getName()).getString());
      return true;
   }

   /**
    * @see org.eclipse.jdt.client.core.dom.ASTVisitor#visit(org.eclipse.jdt.client.core.dom.MethodDeclaration)
    */
   @Override
   public boolean visit(MethodDeclaration node)
   {
      html = new SafeHtmlBuilder();
      int modifiers = node.getModifiers();
      Image image = null;

      // Get the access modifier icon:
      if (Modifier.isPrivate(modifiers))
      {
         image = getMainImage(JavaClientBundle.INSTANCE.privateMethod());
      }
      else if (Modifier.isProtected(modifiers))
      {
         image = getMainImage(JavaClientBundle.INSTANCE.protectedMethod());
      }
      else if (Modifier.isPublic(modifiers))
      {
         image = getMainImage(JavaClientBundle.INSTANCE.publicMethod());
      }
      else
      {
         image = getMainImage(JavaClientBundle.INSTANCE.defaultMethod());
      }
      html.appendHtmlConstant(image.toString());

      // Add all modifiers container:
      html.appendHtmlConstant(getModifiersContainer(modifiers, node.isConstructor()));

      // Method title consists of method's name and the list of its parameters:
      String method = node.getName().getFullyQualifiedName();
      method += getMethodParams(node);
      html.appendHtmlConstant(getTitleElement(method).getString());

      // Constructor return type is null
      if (!node.isConstructor())
      {
         // Append method's return type:
         html.appendHtmlConstant(getTypeElement(node.getReturnType2().toString()));
      }

      return true;
   }

   /**
    * @see org.eclipse.jdt.client.core.dom.ASTVisitor#visit(org.eclipse.jdt.client.core.dom.FieldDeclaration)
    */
   @Override
   public boolean visit(FieldDeclaration node)
   {
      html = new SafeHtmlBuilder();
      int modifiers = node.getModifiers();
      Image image = null;

      // Get the access modifier icon:
      if (Modifier.isPrivate(modifiers))
      {
         image = getMainImage(JavaClientBundle.INSTANCE.privateField());
      }
      else if (Modifier.isProtected(modifiers))
      {
         image = getMainImage(JavaClientBundle.INSTANCE.protectedField());
      }
      else if (Modifier.isPublic(modifiers))
      {
         image = getMainImage(JavaClientBundle.INSTANCE.publicField());
      }
      else
      {
         image = getMainImage(JavaClientBundle.INSTANCE.defaultField());
      }

      html.appendHtmlConstant(image.toString());
      // Add all modifiers container:
      html.appendHtmlConstant(getModifiersContainer(modifiers));

      // Get field's name and type:
      if (node.fragments().iterator().hasNext())
      {
         String name = ((VariableDeclarationFragment)node.fragments().iterator().next()).getName().getIdentifier();
         html.appendHtmlConstant(getTitleElement(name).getString());
         html.appendHtmlConstant(getTypeElement(node.getType().toString()));
      }

      return true;
   }

   /**
    * @return {@link SafeHtmlBuilder} HTML code of the widget
    */
   public SafeHtmlBuilder getHTML()
   {
      return html;
   }

   /**
    * Get the HTML element for displaying all modifiers.
    * 
    * @param modifiers modifiers
    * @param isConstructor <code>true</code> if method is constructor
    * @return {@link String} HTML code of the element
    */
   protected String getModifiersContainer(int modifiers, boolean isConstructor)
   {
      if (Modifier.isTransient(modifiers) || Modifier.isVolatile(modifiers) || Modifier.isStatic(modifiers)
         || Modifier.isFinal(modifiers) || Modifier.isAbstract(modifiers) || isConstructor)
      {

         String span =
            "<span style = \"position: absolute; top: -3px; margin-left: -16px; font-family: Verdana,Bitstream Vera Sans,sans-serif; font-size: 9px; text-align: right;' \">";
         span += isConstructor ? "<span class='item-modifier' style='color: #317441; font-weight:bold;'>c</span>" : "";
         span += (Modifier.isTransient(modifiers)) ? "<span class='item-modifier' style='color:#6d0000;'>t</span>" : "";
         span += (Modifier.isVolatile(modifiers)) ? "<span class='item-modifier' style='color:#6d0000'>v</span>" : "";
         span += (Modifier.isStatic(modifiers)) ? "<span class='item-modifier' style='color:#6d0000'>s</span>" : "";
         span += (Modifier.isFinal(modifiers)) ? "<span class='item-modifier' style='color:#174c83'>f</span>" : "";
         span += (Modifier.isAbstract(modifiers)) ? "<span class='item-modifier' style='color:#004e00'>A</span>" : "";
         span += "</span>";
         return span;
      }
      return "";
   }

   /**
    * Get the HTML element for displaying all modifiers.
    * 
    * @param modifiers
    * @return {@link String} HTML code of the element
    */
   protected String getModifiersContainer(int modifiers)
   {
      return getModifiersContainer(modifiers, false);
   }

   /**
    * Returns mail image of the node.
    * 
    * @param imageResource
    * @return {@link Image}
    */
   protected Image getMainImage(ImageResource imageResource)
   {
      Image image = new Image(imageResource);
      DOM.setStyleAttribute(image.getElement(), "cssFloat", "left");
      DOM.setStyleAttribute(image.getElement(), "marginRight", "5px");
      return image;
   }

   /**
    * Returns the HTML element with the title of the node.
    * 
    * @param title
    * @return {@link Element}
    */
   protected Element getTitleElement(String title)
   {
      Element span = DOM.createSpan();
      span.setInnerHTML(title);
      return span;
   }

   /**
    * Returns HTML code of the element with node's type.
    * 
    * @param type type's string representation
    * @return {@link String} HTML code of the element with node's type
    */
   protected String getTypeElement(String type)
   {
      Element span = DOM.createSpan();
      span.setInnerText(" : " + type);
      span.getStyle().setColor("#644A17");
      return span.getString();
   }

   /**
    * Returns the string presentation of method's parameters.
    * 
    * @param method
    * @return {@link String} method's parameters comma separated
    */
   @SuppressWarnings("unchecked")
   protected String getMethodParams(MethodDeclaration method)
   {
      if (method.parameters().isEmpty())
      {
         return "()";
      }
      else
      {
         Iterator<SingleVariableDeclaration> paramsIterator = method.parameters().iterator();
         StringBuffer params = new StringBuffer("(");
         while (paramsIterator.hasNext())
         {
            SingleVariableDeclaration variable = paramsIterator.next();
            params.append(variable.getType().toString());
            if (paramsIterator.hasNext())
            {
               params.append(", ");
            }
         }
         params.append(")");
         return params.toString();
      }
   }
}
