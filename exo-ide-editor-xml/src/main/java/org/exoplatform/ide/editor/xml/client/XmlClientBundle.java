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
package org.exoplatform.ide.editor.xml.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public interface XmlClientBundle extends ClientBundle
{

   @Source("org/exoplatform/ide/editor/xml/client/styles/xml.css")
   XmlCss css();

   @Source("org/exoplatform/ide/editor/xml/client/images/attribute.png")
   ImageResource attribute();

   @Source("org/exoplatform/ide/editor/xml/client/images/tag.png")
   ImageResource tag();
   
   @Source("org/exoplatform/ide/editor/xml/client/images/cdata-item.png")
   ImageResource cdata();

   @Source("org/exoplatform/ide/editor/xml/client/images/row-selected.png")
   ImageResource itemSelected();
   
   @Source("org/exoplatform/ide/editor/xml/public/images/xml/xml.png")
   ImageResource xml();
}
