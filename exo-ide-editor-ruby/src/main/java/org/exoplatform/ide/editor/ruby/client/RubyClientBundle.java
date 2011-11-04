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
package org.exoplatform.ide.editor.ruby.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public interface RubyClientBundle extends ClientBundle
{

   RubyClientBundle INSTANCE = GWT.create(RubyClientBundle.class);
   
   @Source("org/exoplatform/ide/editor/ruby/client/styles/ruby.css")
   RubyCss css();
   
   @Source("org/exoplatform/ide/editor/ruby/client/images/class.gif")
   ImageResource classItem();

   @Source("org/exoplatform/ide/editor/ruby/client/images/constant-item.png")
   ImageResource rubyConstant();

   @Source("org/exoplatform/ide/editor/ruby/client/images/blank.png")
   ImageResource blankImage();

   @Source("org/exoplatform/ide/editor/ruby/client/images/default-method.png")
   ImageResource defaultMethod();

   @Source("org/exoplatform/ide/editor/ruby/client/images/public-method.png")
   ImageResource publicMethod();

   @Source("org/exoplatform/ide/editor/ruby/client/images/local.png")
   ImageResource variable();

   @Source("org/exoplatform/ide/editor/ruby/client/images/class-variable-item.png")
   ImageResource rubyClassVariable();

   @Source("org/exoplatform/ide/editor/ruby/client/images/global-variable-item.png")
   ImageResource rubyGlobalVariable();

   @Source("org/exoplatform/ide/editor/ruby/client/images/instance-variable-item.png")
   ImageResource rubyObjectVariable();

   @Source("org/exoplatform/ide/editor/ruby/client/images/row-selected.png")
   ImageResource itemSelected();

   @Source("org/exoplatform/ide/editor/ruby/client/images/module-item.png")
   ImageResource module();
   
   @Source("org/exoplatform/ide/editor/ruby/client/images/ruby-file.png")
   ImageResource ruby();

   @Source("org/exoplatform/ide/editor/ruby/client/images/ruby-file-disabled.png")
   ImageResource rubyDisabled();
   
}
