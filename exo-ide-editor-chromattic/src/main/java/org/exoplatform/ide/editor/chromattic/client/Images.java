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
package org.exoplatform.ide.editor.chromattic.client;

import com.google.gwt.resources.client.ClientBundle;

import com.google.gwt.resources.client.ImageResource;

import org.exoplatform.gwtframework.ui.client.util.UIHelper;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Images Mar 11, 2011 12:10:25 PM evgen $
 *
 */
public interface Images extends ClientBundle
{
   public static final String IMAGE_URL = UIHelper.getGadgetImagesURL();
   
   static final String CHROMATTIC = IMAGE_URL + "chromattic.png";
   
   @Source("org/exoplatform/ide/editor/chromattic/public/images/chromattic.png")
   ImageResource CHROMATTIC();
}
