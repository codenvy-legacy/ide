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
 *      'IdePreferencesLocalizationConstant.properties'.
 * <p/>
 * Localization message for form from preferences group, such as
 * SelectWorkspace, CustomizeToolbar, About and others.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: IdePreferencesLocalizationConstant.java Jun 3, 2011 12:58:29 PM vereshchaka $
 *
 */
public interface IdePreferencesLocalizationConstant extends Constants
{
   /*
    * EntryPointListGrid
    */
   @Key("entryPoint.listGrid.name")
   String entryPointListGridName();
   
   /*
    * SelectWorkspaceView
    */
   @Key("workspace.title")
   String workspaceTitle();
   
   /*
    * CustomizeToolbarForm
    */
   @Key("customizeToolbar.title")
   String customizeToolbarTitle();
   
   @Key("customizeToolbar.button.default")
   String customizeToolbarDefaultButton();
   
   @Key("customizeToolbar.button.delimiter")
   String customizeToolbarDelimiterButton();
   
   @Key("customizeToolbar.button.moveUp")
   String customizeToolbarMoveUpButton();
   
   @Key("customizeToolbar.button.moveDown")
   String customizeToolbarMoveDownButton();
   
   /*
    * ToolbarItemListGrid
    */
   @Key("toolbarListGrid.column.toolbar")
   String toolbarListGridToolbarColumn();
   
   /*
    * CommandItemExListGrid
    */
   @Key("commandItemListGrid.column.command")
   String commandListGridCommandColumn();
   
   /*
    * RestServicesDiscoveryView
    */
   @Key("restServicesDiscovery.title")
   String restServicesDiscoveryTitle();
   
   @Key("restServicesDiscovery.path")
   String restServicesDiscoveryPath();
   
   @Key("restServicesDiscovery.requestMediaType")
   String restServicesDiscoveryRequestMediaType();
   
   @Key("restServicesDiscovery.responseMediaType")
   String restServicesDiscoveryResponseMediaType();
   
   /*
    * RestServiceParameterListGrid
    */
   @Key("restServiceListGrid.column.name")
   String restServiceListGridNameColumn();
   
   @Key("restServiceListGrid.column.type")
   String restServiceListGridTypeColumn();
   
   @Key("restServiceListGrid.column.default")
   String restServiceListGridDefaultColumn();
   
   /*
    * AboutIDEView
    */
   @Key("about.copyright")
   String aboutCopyright();
   
   @Key("about.companyName")
   String aboutCompanyName();
   
   @Key("about.ideName")
   String aboutIdeName();
   
   @Key("about.year")
   String aboutYear();
   
   @Key("about.title")
   String aboutTitle();
   
   @Key("about.revision")
   String aboutRevision();
   
   @Key("about.version")
   String aboutVersion();
   
   @Key("about.buildTime")
   String aboutBuildTime();
   
   /*
    * CustomizeHotKeysView
    */
   @Key("customizeHotkeys.title")
   String customizeHotkeysTitle();
   
   /*
    * HotKeyItemListGrid
    */
   @Key("customizeHotkeys.listGrid.command")
   String customizeHotkeysListGridCommand();
   
   @Key("customizeHotkeys.listGrid.binding")
   String customizeHotkeysListGridBinding();
   
   @Key("customizeHotkeys.listGrid.popup")
   String customizeHotkeysListGridPopup();
   
   /*
    * ReservedHotKeys
    */
   @Key("reservedHotkeys.autocomplete")
   String reservedHotkyesAutocomplete();
   
   @Key("reservedHotkeys.bold")
   String reservedHotkeysBold();
   
   @Key("reservedHotkeys.italic")
   String reservedHotkeysItalic();
   
   @Key("reservedHotkeys.undeline")
   String reservedHotkeysUndeline();
   
   @Key("reservedHotkeys.copy")
   String reservedHotkeysCopy();
   
   @Key("reservedHotkeys.paste")
   String reservedHotkeysPaste();
   
   @Key("reservedHotkeys.cut")
   String reservedHotkeysCut();
   
   @Key("reservedHotkeys.undo")
   String reservedHotkeysUndo();
   
   @Key("reservedHotkeys.redo")
   String reservedHotkeysRedo();
   
   @Key("reservedHotkeys.selectAll")
   String reservedHotkeysSelectAll();
   
   @Key("reservedHotkeys.goToStart")
   String reservedHotkeysGoToStart();
   
   @Key("reservedHotkeys.goToEnd")
   String reservedHotkeysGoToEnd();
   
   /*
    * Validation messages
    */
   @Key("hotkeys.firstKeyCtrlOrAlt")
   String msgFirstKeyCtrlOrAlt();
   
   @Key("hotkeys.usedInOtherEditor")
   String msgHotkeyUsedInOtherEditor();
   
   @Key("hotkeys.pressControlKeyThenKey")
   String msgPressControlKeyTheKey();
   
   @Key("hotkeys.boundToAnotherCommand")
   String msgBoundToAnotherCommand();
   
   @Key("hotkeys.boundToTheSameCommand")
   String msgBoundToTheSameCommand();
   
   @Key("hotkeys.tryAnotherHotkey")
   String msgTryAnotherHotkey();

}
