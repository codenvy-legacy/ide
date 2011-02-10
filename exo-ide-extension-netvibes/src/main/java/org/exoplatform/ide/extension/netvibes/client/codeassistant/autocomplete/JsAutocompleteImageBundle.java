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
package org.exoplatform.ide.extension.netvibes.client.codeassistant.autocomplete;

import org.exoplatform.ide.extension.netvibes.client.NetvibesCss;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Image bundle with resources for autocomplete form 
 * inside javascript tags in netvibes files.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: JsAutocompleteImageBundle.java Jan 24, 2011 10:37:28 AM vereshchaka $
 *
 */
public interface JsAutocompleteImageBundle extends ClientBundle
{

   public static final JsAutocompleteImageBundle INSTANCE = GWT.create(JsAutocompleteImageBundle.class);

   @Source("org/exoplatform/ide/extension/netvibes/bundle/netvibes.css")
   public NetvibesCss css();
   
   @Source("org/exoplatform/ide/extension/netvibes/bundle/autocomplete-images/method-item.png")
   ImageResource methodItem();
   
   @Source("org/exoplatform/ide/extension/netvibes/bundle/autocomplete-images/function-item.png")
   ImageResource functionItem();
   
   @Source("org/exoplatform/ide/extension/netvibes/bundle/autocomplete-images/property-item.png")
   ImageResource propertyItem();
   
   @Source("org/exoplatform/ide/extension/netvibes/bundle/autocomplete-images/var-item.png")
   ImageResource varItem();
   
   @Source("org/exoplatform/ide/extension/netvibes/bundle/autocomplete-images/keyword.png")
   ImageResource keyword();
   
   @Source("org/exoplatform/ide/extension/netvibes/bundle/autocomplete-images/template.png")
   ImageResource template();
   
   @Source("org/exoplatform/ide/extension/netvibes/bundle/autocomplete-images/class.gif")
   ImageResource classItem();
   
}
