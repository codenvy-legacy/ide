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
package org.exoplatform.ide.client;

import com.google.gwt.i18n.client.Constants;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 * 
 * 
 * Interface to represent the constants contained in resource bundle:
 *      'IdeLocalizationConstant.properties'.
 */

public interface IdeLocalizationConstant extends Constants
{
   @DefaultStringValue("Cancel")
   @Key("cancelButton")
   String cancelButton();
   
   /* Create Folder */
   @DefaultStringValue("Create Folder")
   @Key("createFolderFormTitle")
   String createFolderFormTitle();
   
   @DefaultStringValue("Create")
   @Key("createFolderFormSubmitButtonTitle")
   String createFolderFormSubmitButtonTitle();
   
   @DefaultStringValue("Name of new folder:")
   @Key("createFolderFormFieldTitle")
   String createFolderFormFieldTitle();
   
   @DefaultStringValue("New folder")
   @Key("newFolderName")
   String newFolderName();

}
