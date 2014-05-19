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
package com.codenvy.ide.core;

import com.codenvy.ide.MimeType;
import com.codenvy.ide.Resources;
import com.codenvy.ide.actions.CloseProjectAction;
import com.codenvy.ide.actions.DeleteResourceAction;
import com.codenvy.ide.actions.FindActionAction;
import com.codenvy.ide.actions.FormatterAction;
import com.codenvy.ide.actions.ImportProjectFromLocationAction;
import com.codenvy.ide.actions.NavigateToFileAction;
import com.codenvy.ide.actions.NewProjectWizardAction;
import com.codenvy.ide.actions.NewResourceAction;
import com.codenvy.ide.actions.RenameResourceAction;
import com.codenvy.ide.actions.SaveAction;
import com.codenvy.ide.actions.SaveAllAction;
import com.codenvy.ide.actions.ShowAboutAction;
import com.codenvy.ide.actions.ShowPreferencesAction;
import com.codenvy.ide.actions.UploadFileAction;
import com.codenvy.ide.api.editor.EditorRegistry;
import com.codenvy.ide.api.resources.FileType;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.action.DefaultActionGroup;
import com.codenvy.ide.api.ui.action.IdeActions;
import com.codenvy.ide.api.ui.keybinding.KeyBindingAgent;
import com.codenvy.ide.api.ui.keybinding.KeyBuilder;
import com.codenvy.ide.api.ui.wizard.DefaultWizard;
import com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard;
import com.codenvy.ide.api.ui.wizard.newresource.NewResource;
import com.codenvy.ide.toolbar.MainToolbar;
import com.codenvy.ide.toolbar.ToolbarPresenter;
import com.codenvy.ide.wizard.NewResourceAgentImpl;
import com.codenvy.ide.wizard.newproject.pages.paas.SelectPaasPagePresenter;
import com.codenvy.ide.wizard.newproject.pages.start.NewProjectPagePresenter;
import com.codenvy.ide.wizard.newproject.pages.template.ChooseTemplatePagePresenter;
import com.codenvy.ide.wizard.newresource.NewFileProvider;
import com.codenvy.ide.wizard.newresource.NewFolderProvider;
import com.codenvy.ide.wizard.newresource.page.NewResourcePagePresenter;
import com.codenvy.ide.xml.XmlFileProvider;
import com.codenvy.ide.xml.editor.XmlEditorProvider;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * Initializer for standard component i.e. some basic menu commands (Save, Save As etc)
 *
 * @author Evgen Vidolob
 */
@Singleton
public class StandardComponentInitializer {

    @Inject
    @NewResource
    private DefaultWizard newResourceWizard;

    @Inject
    private Provider<NewResourcePagePresenter> chooseResourcePage;

    @Inject
    private EditorRegistry editorRegistry;

    @Inject
    private ResourceProvider resourceProvider;

    @Inject
    private NewFolderProvider folderProvider;

    @Inject
    private NewFileProvider textFileProvider;

    @Inject
    private XmlFileProvider xmlFileProvider;

    @Inject
    private XmlEditorProvider xmlEditorProvider;

    @Inject
    private NewResourceAgentImpl newResourceAgent;

    @Inject
    private Resources resources;

    @Inject
    private KeyBindingAgent keyBinding;

    @Inject
    private ActionManager actionManager;

    @Inject
    private SaveAction saveAction;

    @Inject
    private SaveAllAction saveAllAction;

    @Inject
    private NewResourceAction newFileAction;

    @Inject
    private ShowPreferencesAction showPreferencesAction;

    @Inject
    private ShowAboutAction showAboutAction;

    @Inject
    private FindActionAction findActionAction;

    @Inject
    private NavigateToFileAction navigateToFileAction;

    @Inject
    @MainToolbar
    private ToolbarPresenter toolbarPresenter;

    @Inject
    private DeleteResourceAction deleteResourceAction;

    @Inject
    private RenameResourceAction renameResourceAction;

    @Inject
    private CloseProjectAction closeProjectAction;

    @Inject
    private NewProjectWizard newProjectWizard;

    @Inject
    private Provider<NewProjectPagePresenter> newProjectPageProvider;

    @Inject
    private Provider<ChooseTemplatePagePresenter> chooseTemplatePageProvider;

    @Inject
    private Provider<SelectPaasPagePresenter> selectPaasPagePresenterProvider;

    @Inject
    private FormatterAction formatterAction;

    @Inject
    private UploadFileAction uploadFileAction;

    @Inject
    private ImportProjectFromLocationAction importProjectFromLocationAction;

    @Inject
    private NewProjectWizardAction newProjectWizardAction;

    /** Instantiates {@link StandardComponentInitializer} an creates standard content. */
    @Inject
    public StandardComponentInitializer() {
    }

    public void initialize() {
        newResourceWizard.addPage(chooseResourcePage);

        newResourceAgent.register(folderProvider);
        newResourceAgent.register(textFileProvider);

        FileType xmlFile = new FileType(null, MimeType.TEXT_XML, "xml");
        resourceProvider.registerFileType(xmlFile);
        newResourceAgent.register(xmlFileProvider);
        editorRegistry.register(xmlFile, xmlEditorProvider);


        // Compose Import Project group
        DefaultActionGroup importProjectGroup = new DefaultActionGroup("Import Project", true, actionManager);
        actionManager.registerAction(IdeActions.GROUP_IMPORT_PROJECT, importProjectGroup);
        actionManager.registerAction("importProject", importProjectFromLocationAction);
        importProjectGroup.addAction(importProjectFromLocationAction);

        // Compose New group
        DefaultActionGroup newGroup = new DefaultActionGroup("New", true, actionManager);
        newGroup.getTemplatePresentation().setSVGIcon(resources.newResource());
        actionManager.registerAction("newProject2", newProjectWizardAction);
        actionManager.registerAction("newResource", newFileAction);
        newGroup.addAction(newProjectWizardAction);
        newGroup.add(newFileAction);

        actionManager.registerAction("uploadFile", uploadFileAction);
        actionManager.registerAction("navigateToFile", navigateToFileAction);

        // Compose Save group
        DefaultActionGroup saveGroup = new DefaultActionGroup(actionManager);
        actionManager.registerAction("saveGroup", saveGroup);
        actionManager.registerAction("save", saveAction);
        actionManager.registerAction("saveAll", saveAllAction);
        saveGroup.addSeparator();
        saveGroup.add(saveAction);
        saveGroup.add(saveAllAction);

        // Compose File menu
        DefaultActionGroup fileGroup = (DefaultActionGroup)actionManager.getAction(IdeActions.GROUP_FILE);
        fileGroup.add(importProjectGroup);
        fileGroup.add(newGroup);
        fileGroup.add(uploadFileAction);
        fileGroup.add(navigateToFileAction);
        fileGroup.add(renameResourceAction);
        fileGroup.add(saveGroup);

        // Compose Code menu
        DefaultActionGroup codeGroup = (DefaultActionGroup)actionManager.getAction(IdeActions.GROUP_CODE);
        actionManager.registerAction("format", formatterAction);
        codeGroup.add(formatterAction);

        // Compose Window menu
        DefaultActionGroup windowGroup = (DefaultActionGroup)actionManager.getAction(IdeActions.GROUP_WINDOW);
        actionManager.registerAction("showPreferences", showPreferencesAction);
        windowGroup.add(showPreferencesAction);

        // Compose Help menu
        DefaultActionGroup helpGroup = (DefaultActionGroup)actionManager.getAction(IdeActions.GROUP_HELP);
        actionManager.registerAction("findActionAction", findActionAction);
        actionManager.registerAction("showAbout", showAboutAction);
        helpGroup.add(findActionAction);
        helpGroup.add(showAboutAction);


        // Compose main context menu
        DefaultActionGroup resourceOperation = new DefaultActionGroup(actionManager);
        actionManager.registerAction("resourceOperation", resourceOperation);
        resourceOperation.addSeparator();
        resourceOperation.add(deleteResourceAction);
        resourceOperation.add(renameResourceAction);

        DefaultActionGroup closeProjectGroup = new DefaultActionGroup(actionManager);
        actionManager.registerAction("closeProjectGroup", closeProjectGroup);
        closeProjectGroup.addSeparator();
        closeProjectGroup.add(closeProjectAction);

        DefaultActionGroup mainContextMenuGroup = (DefaultActionGroup)actionManager.getAction(IdeActions.GROUP_MAIN_CONTEXT_MENU);
        mainContextMenuGroup.add(newGroup);
        mainContextMenuGroup.addSeparator();
        mainContextMenuGroup.add(resourceOperation);
        mainContextMenuGroup.add(closeProjectGroup);


        // Compose main toolbar
        DefaultActionGroup changeResourceGroup = new DefaultActionGroup(actionManager);
        actionManager.registerAction("changeResourceGroup", changeResourceGroup);
        actionManager.registerAction("closeProject", closeProjectAction);
        actionManager.registerAction("deleteItem", deleteResourceAction);
        actionManager.registerAction("renameResource", renameResourceAction);
        changeResourceGroup.add(closeProjectAction);
        changeResourceGroup.add(deleteResourceAction);
        changeResourceGroup.addSeparator();

        DefaultActionGroup mainToolbarGroup = (DefaultActionGroup)actionManager.getAction(IdeActions.GROUP_MAIN_TOOLBAR);
        mainToolbarGroup.addSeparator();
        mainToolbarGroup.add(newGroup);
        mainToolbarGroup.addSeparator();
        mainToolbarGroup.addSeparator();
        mainToolbarGroup.add(saveAllAction);
        mainToolbarGroup.addSeparator();
        mainToolbarGroup.add(changeResourceGroup);
        toolbarPresenter.bindMainGroup(mainToolbarGroup);


        // Define hot-keys
        keyBinding.getGlobal().addKey(new KeyBuilder().action().charCode('F').build(), "format");
        keyBinding.getGlobal().addKey(new KeyBuilder().action().alt().charCode('n').build(), "navigateToFile");
        keyBinding.getGlobal().addKey(new KeyBuilder().action().charCode('s').build(), "save");
        keyBinding.getGlobal().addKey(new KeyBuilder().action().charCode('S').build(), "saveAll");
        keyBinding.getGlobal().addKey(new KeyBuilder().action().charCode('A').build(), "findActionAction");

        newProjectWizard.addPage(newProjectPageProvider);
        newProjectWizard.addPage(chooseTemplatePageProvider);
        newProjectWizard.addPage(selectPaasPagePresenterProvider);
    }
}