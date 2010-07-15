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
package org.exoplatform.ideall.client.plugin.gadget;

import org.exoplatform.gwtframework.ui.client.util.UIHelper;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public interface Images
{
   public static final String imageUrl = UIHelper.getGadgetImagesURL();
   
   public static final String GOOGLE_GADGET = imageUrl +  "/plugin/gadget/gadget.png";
   
   public static final String DEPLOY_GADGET = imageUrl + "/plugin/gadget/bundled/deploy_gadget.png";
   
   public static final String UNDEPLOY_GADGET = imageUrl + "/plugin/gadget/bundled/undeploy_gadget.png";
   
}
