/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package org.exoplatform.ide.client.messages;

import com.google.gwt.i18n.client.Constants;

/**
 * Interface to represent the constants contained in resource bundle: 'IdePreferencesLocalizationConstant.properties'.
 * <p/>
 * Localization message for form from preferences group, such as SelectWorkspace, CustomizeToolbar, About and others.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: IdePreferencesLocalizationConstant.java Jun 3, 2011 12:58:29 PM vereshchaka $
 */
public interface IdePreferencesLocalizationConstant extends Constants {
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

    @Key("select.workspace")
    String selectWorkspace();

    /*
     * SelectWorkspacePresenter
     */
    @Key("selectWorkspaces.dialog.askSaveFileBeforeClosing.title")
    String selectWorkspaceAskSaveFileBeforeClosingDialogTitle();

    /*
     * SelectWorkspacePresenter
     */
    @Key("workspace.closeAllFilesDialog.title")
    String workspaceCloseAllFilesDialogTitle();

    @Key("workspace.closeAllFilesDialog.text")
    String workspaceCloseAllFilesDialogText();

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
     * RestServicesDiscoveryPresenter
     */
    @Key("restServicesDiscovery.param.header")
    String restServicesDiscoveryParamHeader();

    @Key("restServicesDiscovery.param.query")
    String restServicesDiscoveryParamQuery();

    @Key("restServicesDiscovery.param.plain")
    String restServicesDiscoveryParamPlain();

    @Key("restServicesDiscovery.param.path")
    String restServicesDiscoveryParamPath();

    @Key("restServicesDiscovery.param.matrix")
    String restServicesDiscoveryParamMatrix();

    @Key("restServicesDiscovery.param.param")
    String restServicesDiscoveryParam();

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

    @Key("customizeHotkeys.button.bind")
    String customizeHotkeysBindButton();

    @Key("customizeHotkeys.button.unbind")
    String customizeHotkeysUnbindButton();

    @Key("customizeHotkeys.button.defaults")
    String customizeHotkeysDefaultsButton();

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
     * ShowHotKeysView
     */
    @Key("showHotKeys.title")
    String showHotKeysTitle();

    /*
     * ShowHotKeyItemListGrid
     */
    @Key("showHotKeys.listGrid.command")
    String showHotKeysListGridCommand();

    @Key("showHotKeys.listGrid.shortcut")
    String showHotKeysListGridShortcut();

    @Key("showHotKeys.listGrid.popup")
    String showHotKeysListGridPopup();

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

    /*
     * CustomizeHotKeysPresenter
     */
    @Key("hotkeys.group.editor")
    String hotkeysEditorGroup();

    @Key("hotkeys.group.other")
    String hotkeysOtherGroup();

    /*
     * CloseAllFilesEventHandler
     */
    @Key("closeAllFiles.unsavedFilesMayBeLost")
    String unsavedFilesMayBeLost();

    @Key("show.preferences.control.id")
    String showPreferencesControlId();

    @Key("show.preferences.control.title")
    String showPreferencesControlTitle();

    @Key("show.preferences.control.prompt")
    String showPreferencesControlPrompt();

    @Key("show.preferences.view.title")
    String showPreferencesViewTitle();

    /*
     * InviteGoogleContactsView
     */
    @Key("inviteGoogleContactsTitle.title")
    String inviteGoogleContactsTitle();
}
