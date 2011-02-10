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
package org.exoplatform.ide.extension.chromattic.client;

import org.exoplatform.gwtframework.ui.client.util.UIHelper;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Sep 28, 2010 $
 *
 */
public interface Images
{

   public static final String IMAGE_URL = UIHelper.getGadgetImagesURL();

   public interface FileType
   {

      public static final String CHROMATTIC = IMAGE_URL + "chromattic-extension/filetype/chromattic.png";
   }
   
   public interface Buttons
   {
      public static final String OK = IMAGE_URL + "chromattic-extension/buttons/ok.png";
      
      public static final String CANCEL = IMAGE_URL + "chromattic-extension/buttons/cancel.png";
      
   }

   public interface Controls
   {
      public static final String COMPILE_GROOVY = IMAGE_URL + "chromattic-extension/controls/compile.png";

      public static final String PREVIEW_NODE_TYPE = IMAGE_URL + "chromattic-extension/controls/preview-node-type.png";

      public static final String DEPLOY_NODE_TYPE = IMAGE_URL + "chromattic-extension/controls/deploy-node-type.png";
   }
}
