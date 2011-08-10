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
package org.exoplatform.ide.client.messages;

import com.google.gwt.i18n.client.Constants;

/**
 * Interface to represent the constants contained in resource bundle:
 *      'IdeVersionsLocalizationConstant.properties'.
 * <p/>
 * Localization message for form from versions group.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: IdePreferencesLocalizationConstant.java Jun 3, 2011 12:58:29 PM vereshchaka $
 *
 */
public interface IdeVersionsLocalizationConstant extends Constants
{
   /*
    * VersionsGrid
    */
   @Key("versions.grid.name")
   String versionsGridName();
   
   @Key("versions.grid.date")
   String versionsGridDate();
   
   @Key("versions.grid.length")
   String versionsGridLength();
   
   /*
    * ViewVersionsForm
    */
   @Key("viewVersions.title")
   String viewVersionsTitle();
   
   @Key("viewVersions.for")
   String viewVersionsFor();
   
   /*
    * VersionContentPresenter
    */
   @Key("versions.displayPrefix")
   String versionsDisplayPrefix();
   
   /*
    * RestoreToVersionCommandHandler
    */
   @Key("restoreToVersion.dialog.title")
   String restoreToVersionDialogTitle();
   
   /*
    * ShowVersionListCommandHandler
    * VersionHistoryCommandHandler
    */
   @Key("versions.openFile")
   String versionsOpenFile();
   
   @Key("versions.version.title")
   String versionTitle();
   
   /*
    * ViewVersionsPresenter
    */
   @Key("versions.error.openFile")
   String versionErrorOpenFile();

}
