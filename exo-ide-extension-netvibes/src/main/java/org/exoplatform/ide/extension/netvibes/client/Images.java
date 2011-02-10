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
package org.exoplatform.ide.extension.netvibes.client;

import org.exoplatform.gwtframework.ui.client.util.UIHelper;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public interface Images
{
   
   public static final String IMAGE_URL = UIHelper.getGadgetImagesURL();
   
   public static final String UWA_WIGET =  IMAGE_URL + "module/netvibes/uwa-widget.png";
   
   public interface Controls
   {

      public static String DEPLOY_WIDGET = IMAGE_URL + "module/netvibes/controls/deploy-widget.png";

   }
   
   public interface Buttons
   {

      public static String NEXT_STEP = IMAGE_URL + "module/netvibes/buttons/next-step.png";
      
      public static String PREV_STEP = IMAGE_URL + "module/netvibes/buttons/prev-step.png";
      
      public static String CANCEL = IMAGE_URL + "module/netvibes/buttons/cancel.png";
      
      public static String OK = IMAGE_URL + "module/netvibes/buttons/ok.png";
   }
}
