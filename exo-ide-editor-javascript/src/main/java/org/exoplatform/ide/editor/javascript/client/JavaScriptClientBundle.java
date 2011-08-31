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
package org.exoplatform.ide.editor.javascript.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public interface JavaScriptClientBundle extends ClientBundle
{

   @Source("org/exoplatform/ide/editor/javascript/client/styles/javascript.css")
   JavaScriptCss css();

   @Source("org/exoplatform/ide/editor/javascript/client/images/function-item.png")
   ImageResource functionItem();

   @Source("org/exoplatform/ide/editor/javascript/client/images/property-item.png")
   ImageResource propertyItem();

   @Source("org/exoplatform/ide/editor/javascript/client/images/row-selected.png")
   ImageResource itemSelected();

   @Source("org/exoplatform/ide/editor/javascript/client/images/var-item.png")
   ImageResource varItem();

   @Source("org/exoplatform/ide/editor/javascript/client/images/blank.png")
   ImageResource blankImage();

   @Source("org/exoplatform/ide/editor/javascript/client/images/class.gif")
   ImageResource classItem();

   @Source("org/exoplatform/ide/editor/javascript/client/images/template.png")
   ImageResource template();

   @Source("org/exoplatform/ide/editor/javascript/client/images/method-item.png")
   ImageResource methodItem();
   
   @Source("org/exoplatform/ide/editor/javascript/client/images/tag.png")
   ImageResource tag();
   
   @Source("org/exoplatform/ide/editor/javascript/public/images/javascript/javascript.gif")
   ImageResource javaScript();
}
