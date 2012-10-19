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
package org.exoplatform.ide.java.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 * 
 */
public interface JavaClientBundle extends ClientBundle
{
   JavaClientBundle INSTANCE = GWT.create(JavaClientBundle.class);

//   @Source("org/exoplatform/ide/editor/java/client/styles/java.css")
//   JavaCss css();

   @Source("org/exoplatform/ide/java/client/images/annotation.gif")
   ImageResource annotationItem();

   @Source("org/exoplatform/ide/java/client/images/class.gif")
   ImageResource classItem();

   @Source("org/exoplatform/ide/java/client/images/innerinterface_public.gif")
   ImageResource interfaceItem();

   @Source("org/exoplatform/ide/java/client/images/enum_item.gif")
   ImageResource enumItem();

   @Source("org/exoplatform/ide/java/client/images/default-field.png")
   ImageResource defaultField();

   @Source("org/exoplatform/ide/java/client/images/private-field.png")
   ImageResource privateField();

   @Source("org/exoplatform/ide/java/client/images/protected-field.png")
   ImageResource protectedField();

   @Source("org/exoplatform/ide/java/client/images/public-field.png")
   ImageResource publicField();

   @Source("org/exoplatform/ide/java/client/images/blank.png")
   ImageResource blankImage();

   @Source("org/exoplatform/ide/java/client/images/default-method.png")
   ImageResource defaultMethod();

   @Source("org/exoplatform/ide/java/client/images/private-method.png")
   ImageResource privateMethod();

   @Source("org/exoplatform/ide/java/client/images/protected-method.png")
   ImageResource protectedMethod();

   @Source("org/exoplatform/ide/java/client/images/public-method.png")
   ImageResource publicMethod();

   @Source("org/exoplatform/ide/java/client/images/package.png")
   ImageResource packageItem();

   @Source("org/exoplatform/ide/java/client/images/import.png")
   ImageResource importItem();

   @Source("org/exoplatform/ide/java/client/images/imports.png")
   ImageResource imports();

   @Source("org/exoplatform/ide/java/client/images/local.png")
   ImageResource variable();

   @Source("org/exoplatform/ide/java/client/images/row-selected.png")
   ImageResource itemSelected();

   @Source("org/exoplatform/ide/java/client/images/jsp-tag.png")
   ImageResource jspTagItem();

   @Source("org/exoplatform/ide/java/client/images/class-private.png")
   ImageResource classPrivateItem();

   @Source("org/exoplatform/ide/java/client/images/class-protected.png")
   ImageResource classProtectedItem();

   @Source("org/exoplatform/ide/java/client/images/class-default.png")
   ImageResource classDefaultItem();

   @Source("org/exoplatform/ide/java/client/images/clock.png")
   ImageResource clockItem();

   @Source("org/exoplatform/ide/java/client/images/groovy-tag.png")
   ImageResource groovyTagItem();

   @Source("org/exoplatform/ide/java/client/images/java.png")
   ImageResource java();

   @Source("org/exoplatform/ide/java/client/images/java-disabled.png")
   ImageResource javaDisabled();

   @Source("org/exoplatform/ide/java/client/images/newJavaclass_wiz.gif")
   ImageResource newClassWizz();

   @Source("org/exoplatform/ide/java/client/images/newJavaclass_wiz_Disabled.gif")
   ImageResource newClassWizzDisabled();

   @Source("org/exoplatform/ide/java/client/images/outline.png")
   ImageResource outline();
   
   @Source("org/exoplatform/ide/java/client/images/loader.gif")
   ImageResource loader();
   
   @Source("org/exoplatform/ide/java/client/images/template.png")
   ImageResource template();

   @Source("org/exoplatform/ide/java/client/images/package_Disabled.png")
   ImageResource packageDisabled();

   @Source("org/exoplatform/ide/java/client/images/breakpoint-current.gif")
   ImageResource breakpointCurrent();

   @Source("org/exoplatform/ide/java/client/images/breakpoint.gif")
   ImageResource breakpoint();
   
   @Source("org/exoplatform/ide/java/client/internal/text/correction/proposals/correction_change.gif")
   ImageResource correction_change();
   
   @Source("org/exoplatform/ide/java/client/internal/text/correction/proposals/delete_obj.gif")
   ImageResource delete_obj();
   
   @Source("org/exoplatform/ide/java/client/internal/text/correction/proposals/correction_cast.gif")
   ImageResource correction_cast();
   
   @Source("org/exoplatform/ide/java/client/internal/text/correction/proposals/local.png")
   ImageResource local_var();
   
   @Source("org/exoplatform/ide/java/client/internal/text/correction/proposals/correction_delete_import.gif")
   ImageResource correction_delete_import();
   
   @Source("org/exoplatform/ide/java/client/internal/text/correction/proposals/field_public_obj.gif")
   ImageResource field_public();
   
   @Source("org/exoplatform/ide/java/client/internal/text/correction/proposals/imp_obj.gif")
   ImageResource imp_obj();

   @Source("org/exoplatform/ide/java/client/internal/text/correction/proposals/add_obj.gif")
   ImageResource add_obj();
   
   @Source("org/exoplatform/ide/java/client/internal/text/correction/proposals/remove_correction.gif")
   ImageResource remove_correction();
   
   @Source("org/exoplatform/ide/java/client/internal/text/correction/proposals/jexception_obj.gif")
   ImageResource exceptionProp();
   
   @Source("org/exoplatform/ide/java/client/internal/text/correction/proposals/javadoc.gif")
   ImageResource javadoc();
}
