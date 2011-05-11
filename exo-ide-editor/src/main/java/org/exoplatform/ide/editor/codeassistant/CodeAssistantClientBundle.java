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
package org.exoplatform.ide.editor.codeassistant;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: CodeAssistantClientBundle Feb 22, 2011 5:06:30 PM evgen $
 *
 */
public interface CodeAssistantClientBundle extends ClientBundle
{
   CodeAssistantClientBundle INSTANCE = GWT.create(CodeAssistantClientBundle.class);

   @Source("org/exoplatform/ide/editor/public/bundle/codeassistant.css")
   CodeAssistantCss css();

   @Source("org/exoplatform/ide/editor/public/bundle/images/property-item.png")
   ImageResource property();

   @Source("org/exoplatform/ide/editor/public/bundle/images/tag.png")
   ImageResource tag();

   @Source("org/exoplatform/ide/editor/public/bundle/images/attribute.png")
   ImageResource attribute();

   @Source("org/exoplatform/ide/editor/public/bundle/images/row-selected.png")
   ImageResource rowSelected();

   @Source("org/exoplatform/ide/editor/public/bundle/images/blank.png")
   ImageResource blankImage();

   @Source("org/exoplatform/ide/editor/public/bundle/images/template.png")
   ImageResource template();

   @Source("org/exoplatform/ide/editor/public/bundle/images/class.gif")
   ImageResource classItem();

   @Source("org/exoplatform/ide/editor/public/bundle/images/function-item.png")
   ImageResource functionItem();

   @Source("org/exoplatform/ide/editor/public/bundle/images/method-item.png")
   ImageResource methodItem();

   @Source("org/exoplatform/ide/editor/public/bundle/images/var-item.png")
   ImageResource varItem();

   @Source("org/exoplatform/ide/editor/public/bundle/codeassistant/innerinterface_public.gif")
   ImageResource intrfaceItem();

   @Source("org/exoplatform/ide/editor/public/bundle/codeassistant/annotation.gif")
   ImageResource annotationItem();

   @Source("org/exoplatform/ide/editor/public/bundle/codeassistant/public-field.png")
   ImageResource publicField();

   @Source("org/exoplatform/ide/editor/public/bundle/codeassistant/protected-field.png")
   ImageResource protectedField();

   @Source("org/exoplatform/ide/editor/public/bundle/codeassistant/default-field.png")
   ImageResource defaultField();

   @Source("org/exoplatform/ide/editor/public/bundle/codeassistant/private-field.png")
   ImageResource privateField();

   @Source("org/exoplatform/ide/editor/public/bundle/codeassistant/private-method.png")
   ImageResource privateMethod();

   @Source("org/exoplatform/ide/editor/public/bundle/codeassistant/protected-method.png")
   ImageResource protectedMethod();

   @Source("org/exoplatform/ide/editor/public/bundle/codeassistant/public-method.png")
   ImageResource publicMethod();

   @Source("org/exoplatform/ide/editor/public/bundle/codeassistant/default-method.png")
   ImageResource defaultMethod();

   @Source("org/exoplatform/ide/editor/public/bundle/codeassistant/local.png")
   ImageResource variable();

   @Source("org/exoplatform/ide/editor/public/bundle/codeassistant/constant-item.png")
   ImageResource rubyConstant();

   @Source("org/exoplatform/ide/editor/public/bundle/codeassistant/class-variable-item.png")
   ImageResource rubyClassVariable();

   @Source("org/exoplatform/ide/editor/public/bundle/codeassistant/instance-variable-item.png")
   ImageResource rubyObjectVariable();

   @Source("org/exoplatform/ide/editor/public/bundle/codeassistant/global-variable-item.png")
   ImageResource rubyGlobalVariable();
}
