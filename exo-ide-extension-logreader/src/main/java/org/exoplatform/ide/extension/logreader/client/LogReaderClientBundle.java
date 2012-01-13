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
package org.exoplatform.ide.extension.logreader.client;

import com.google.gwt.core.client.GWT;

import com.google.gwt.resources.client.ImageResource;

import com.google.gwt.resources.client.ClientBundle;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 * 
 */
public interface LogReaderClientBundle extends ClientBundle
{

   LogReaderClientBundle INSTANCE = GWT.create(LogReaderClientBundle.class);

   @Source("org/exoplatform/ide/extension/logreader/client/images/log-reader-disabled.png")
   ImageResource logReaderDisabled();

   @Source("org/exoplatform/ide/extension/logreader/client/images/log-reader.png")
   ImageResource logReader();

   @Source("org/exoplatform/ide/extension/logreader/client/images/clearOutput.png")
   ImageResource clearOutput();

   @Source("org/exoplatform/ide/extension/logreader/client/images/log-reder-settings.png")
   ImageResource logRederSettings();

   @Source("org/exoplatform/ide/extension/logreader/client/images/next_Disabled.png")
   ImageResource next_Disabled();

   @Source("org/exoplatform/ide/extension/logreader/client/images/next.png")
   ImageResource next();

   @Source("org/exoplatform/ide/extension/logreader/client/images/prev_Disabled.png")
   ImageResource prev_Disabled();

   @Source("org/exoplatform/ide/extension/logreader/client/images/prev.png")
   ImageResource prev();

   @Source("org/exoplatform/ide/extension/logreader/client/images/refresh_Disabled.png")
   ImageResource refresh_Disabled();

   @Source("org/exoplatform/ide/extension/logreader/client/images/refresh.png")
   ImageResource refresh();

}
