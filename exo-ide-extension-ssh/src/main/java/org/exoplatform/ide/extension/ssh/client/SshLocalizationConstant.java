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
package org.exoplatform.ide.extension.ssh.client;

import com.google.gwt.i18n.client.Constants;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 * 
 */
public interface SshLocalizationConstant extends Constants
{

   @DefaultStringValue("Cancel")
   @Key("cancelButton")
   String cancelButton();

   @DefaultStringValue("Close")
   @Key("closeButton")
   String closeButton();

   @DefaultStringValue("Upload")
   @Key("uploadButton")
   String uploadButton();

   @DefaultStringValue("Browse...")
   @Key("browseButton")
   String browseButton();

   @DefaultStringValue("Host")
   @Key("hostFieldTitle")
   String hostFieldTitle();

   @DefaultStringValue("File name")
   @Key("fileNameFieldTitle")
   String fileNameFieldTitle();

   @Key("host.validation.error")
   String hostValidationError();

   //
   @DefaultStringValue("Upload")
   @Key("key.manager.uploadButton")
   String managerUploadButton();

   @DefaultStringValue("Generate")
   @Key("key.manager.generateButton")
   String managerGenerateButton();
}
