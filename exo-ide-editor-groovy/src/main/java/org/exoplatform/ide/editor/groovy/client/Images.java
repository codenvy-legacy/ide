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
package org.exoplatform.ide.editor.groovy.client;

import com.google.gwt.resources.client.ImageResource;

import com.google.gwt.core.client.GWT;

import com.google.gwt.resources.client.ClientBundle;

import org.exoplatform.gwtframework.ui.client.util.UIHelper;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Images Mar 11, 2011 10:54:45 AM evgen $
 *
 */
public interface Images extends ClientBundle
{
   Images INSTANCE = GWT.create(Images.class);
   
   public static final String IMAGE_URL = UIHelper.getGadgetImagesURL();
   
   static final String GROOVY = IMAGE_URL + "groovy.png";
   
   static final String REST_SERVICE = IMAGE_URL + "rest.png";
   
   static final String GROOVY_TAG = IMAGE_URL + "groovy-tag.png";
   
   @Source("org/exoplatform/ide/editor/groovy/public/images/groovy.png")
   ImageResource groovy();
}
