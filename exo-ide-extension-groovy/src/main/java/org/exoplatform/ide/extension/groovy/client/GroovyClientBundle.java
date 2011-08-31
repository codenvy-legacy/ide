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
package org.exoplatform.ide.extension.groovy.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author <a href="mailto:dnochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Id: $
 */

public interface GroovyClientBundle extends ClientBundle
{

   public static final GroovyClientBundle INSTANCE = GWT.create(GroovyClientBundle.class);

   /**
    * To active bundle, call 
    * <code>GroovyPluginImageBundle.INSTANCE.css().ensureInjected()</code>
    * method in your module.
    * 
    * @return {@link GroovyCss}
    */
   @Source("groovy.css")
   public GroovyCss css();
   
   @Source("images/codeassistant/class.gif")
   ImageResource classItem();

   @Source("images/codeassistant/annotation.gif")
   ImageResource annotationItem();

   @Source("images/codeassistant/innerinterface_public.gif")
   ImageResource intrfaceItem();
   
   @Source("images/codeassistant/enum.gif")
   ImageResource enumItem();

   @Source("org/exoplatform/ide/extension/groovy/client/images/codeassistant/class-item.png")
   ImageResource classItem1();

   @Source("org/exoplatform/ide/extension/groovy/client/images/codeassistant/default-field.png")
   ImageResource defaultField();

   @Source("org/exoplatform/ide/extension/groovy/client/images/codeassistant/default-method.png")
   ImageResource defaultMethod();

   @Source("org/exoplatform/ide/extension/groovy/client/images/jarlibrary.png")
   ImageResource jarLibrary();

   @Source("org/exoplatform/ide/extension/groovy/client/images/jarlibrary-disabled.png")
   ImageResource jarLibraryDisabled();   
   
   @Source("org/exoplatform/ide/extension/groovy/client/images/codeassistant/private-field.png")
   ImageResource privateField();

   @Source("org/exoplatform/ide/extension/groovy/client/images/codeassistant/private-method.png")
   ImageResource privateMethod();

   @Source("org/exoplatform/ide/extension/groovy/client/images/codeassistant/protected-field.png")
   ImageResource protectedField();

   @Source("org/exoplatform/ide/extension/groovy/client/images/codeassistant/protected-method.png")
   ImageResource protectedMethod();

   @Source("org/exoplatform/ide/extension/groovy/client/images/codeassistant/public-field.png")
   ImageResource publicField();

   @Source("org/exoplatform/ide/extension/groovy/client/images/codeassistant/public-method.png")
   ImageResource publicMethod();
   
   @Source("org/exoplatform/ide/extension/groovy/client/images/codeassistant/local.png")
   ImageResource variable();

   @Source("org/exoplatform/ide/extension/groovy/client/images/blank.png")
   ImageResource blankImage();

   @Source("org/exoplatform/ide/extension/groovy/client/images/preview.png")
   ImageResource preview();
   
   @Source("org/exoplatform/ide/extension/groovy/client/images/preview_Disabled.png")
   ImageResource previewDisabled();
   
   /*
    * Buttons
    */
   @Source("org/exoplatform/ide/extension/groovy/client/images/buttons/ok.png")
   ImageResource okButton();

   @Source("org/exoplatform/ide/extension/groovy/client/images/buttons/ok_Disabled.png")
   ImageResource okButtonDisabled();
   
   @Source("org/exoplatform/ide/extension/groovy/client/images/buttons/cancel.png")
   ImageResource cancelButton();

   @Source("org/exoplatform/ide/extension/groovy/client/images/buttons/cancel_Disabled.png")
   ImageResource cancelButtonDisabled();
   
   @Source("org/exoplatform/ide/extension/groovy/client/images/buttons/add.png")
   ImageResource addButton();
   
   @Source("org/exoplatform/ide/extension/groovy/client/images/buttons/add_Disabled.png")
   ImageResource addButtonDisabled();

   @Source("org/exoplatform/ide/extension/groovy/client/images/buttons/remove.png")
   ImageResource removeButton();
   
   @Source("org/exoplatform/ide/extension/groovy/client/images/buttons/remove_Disabled.png")
   ImageResource removeButtonDisabled();
   
   @Source("org/exoplatform/ide/extension/groovy/public/images/module/groovy/classpath/folder.png")
   ImageResource folder();
}
