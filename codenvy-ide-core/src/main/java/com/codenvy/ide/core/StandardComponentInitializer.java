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
import com.codenvy.ide.actions.FormatterAction;
import com.codenvy.ide.actions.NewProjectAction;
import com.codenvy.ide.actions.NewResourceAction;
import com.codenvy.ide.actions.OpenProjectAction;
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
import com.codenvy.ide.api.ui.action.Constraints;
import com.codenvy.ide.api.ui.action.DefaultActionGroup;
import com.codenvy.ide.api.ui.action.IdeActions;
import com.codenvy.ide.api.ui.keybinding.KeyBindingAgent;
import com.codenvy.ide.api.ui.keybinding.KeyBuilder;
import com.codenvy.ide.api.ui.wizard.DefaultWizard;
import com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard;
import com.codenvy.ide.api.ui.wizard.newresource.NewResource;
import com.codenvy.ide.toolbar.MainToolbar;
import com.codenvy.ide.toolbar.ToolbarPresenter;
import com.codenvy.ide.welcome.action.ConnectSupportAction;
import com.codenvy.ide.welcome.action.CreateProjectAction;
import com.codenvy.ide.welcome.action.InviteAction;
import com.codenvy.ide.welcome.action.ShowDocumentationAction;
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
import com.google.web.bindery.event.shared.EventBus;

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
    private EventBus eventBus;

    @Inject
    private ActionManager actionManager;

    @Inject
    private NewProjectAction newProjectAction;

    @Inject
    private SaveAction saveAction;

    @Inject
    private SaveAllAction saveAllAction;

    @Inject
    private NewResourceAction newFileAction;

    @Inject
    private OpenProjectAction openProjectAction;

    @Inject
    private ShowPreferencesAction showPreferencesAction;

    @Inject
    private ShowAboutAction showAboutAction;

    // Temporary disable 'Project Properties' feature
//    @Inject
//    private ShowProjectPropertiesAction showProjectPropertiesAction;

    // Temporary disable 'Navigate To File' feature
//    @Inject
//    private NavigateToFileAction navigateToFileAction;

    @Inject
    @MainToolbar
    private ToolbarPresenter toolbarPresenter;

    @Inject
    private CreateProjectAction createProjectAction;

    @Inject
    private ShowDocumentationAction showDocumentationAction;

    @Inject
    private InviteAction inviteAction;

    @Inject
    private ConnectSupportAction connectSupportAction;

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

        DefaultActionGroup window = (DefaultActionGroup)actionManager.getAction(IdeActions.GROUP_WINDOW);
        actionManager.registerAction("showPreferences", showPreferencesAction);
        window.add(showPreferencesAction);

        DefaultActionGroup help = (DefaultActionGroup)actionManager.getAction(IdeActions.GROUP_HELP);
        actionManager.registerAction("showAbout", showAboutAction);
        help.add(showAboutAction);

        DefaultActionGroup project = (DefaultActionGroup)actionManager.getAction(IdeActions.GROUP_PROJECT);
//        actionManager.registerAction("showProjectProperties", showProjectPropertiesAction);
//        project.add(showProjectPropertiesAction);

        DefaultActionGroup fileGroup = (DefaultActionGroup)actionManager.getAction(IdeActions.GROUP_FILE);

        keyBinding.getGlobal().addKey(new KeyBuilder().action().charCode('s').build(), "save");
        keyBinding.getGlobal().addKey(new KeyBuilder().action().charCode('S').build(), "saveAll");
        keyBinding.getGlobal().addKey(new KeyBuilder().action().charCode('F').build(), "format");

        actionManager.registerAction("newProject", newProjectAction);
        actionManager.registerAction("openProject", openProjectAction);
        actionManager.registerAction("uploadFile", uploadFileAction);

//        actionManager.registerAction("navigateToFile", navigateToFileAction);
//        keyBinding.getGlobal().addKey(new KeyBuilder().action().alt().charCode('n').build(), "navigateToFile");

        DefaultActionGroup toolbarGroup = new DefaultActionGroup(actionManager);
        toolbarGroup.addSeparator();
        actionManager.registerAction(IdeActions.GROUP_MAIN_TOOLBAR, toolbarGroup);

        DefaultActionGroup newGroup = new DefaultActionGroup("New", true, actionManager);
        newGroup.getTemplatePresentation().setSVGIcon(resources.newResource());
        newGroup.addAction(newProjectAction, Constraints.FIRST);
        toolbarGroup.add(newGroup);
        toolbarGroup.addSeparator();
        fileGroup.add(newGroup);
        fileGroup.add(openProjectAction);
        fileGroup.add(uploadFileAction);
//        fileGroup.add(navigateToFileAction);
        fileGroup.add(renameResourceAction);
        actionManager.registerAction("newResource", newFileAction);
        newGroup.add(newFileAction);

        DefaultActionGroup saveGroup = new DefaultActionGroup(actionManager);
        actionManager.registerAction("saveGroup", saveGroup);
        actionManager.registerAction("save", saveAction);
        actionManager.registerAction("saveAll", saveAllAction);
        actionManager.registerAction("format", formatterAction);
        saveGroup.addSeparator();
        saveGroup.add(saveAction);
        saveGroup.add(saveAllAction);
        saveGroup.add(formatterAction);
        toolbarGroup.addSeparator();
        toolbarGroup.add(saveAllAction);
        toolbarGroup.addSeparator();
        fileGroup.add(saveGroup);

        DefaultActionGroup changeResourceGroup = new DefaultActionGroup(actionManager);
        actionManager.registerAction("changeResourceGroup", changeResourceGroup);
        actionManager.registerAction("closeProject", closeProjectAction);
        actionManager.registerAction("deleteItem", deleteResourceAction);
        actionManager.registerAction("renameResource", renameResourceAction);
        changeResourceGroup.add(closeProjectAction);
        changeResourceGroup.add(deleteResourceAction);
        changeResourceGroup.addSeparator();
        toolbarGroup.add(changeResourceGroup);

        toolbarPresenter.bindMainGroup(toolbarGroup);

        DefaultActionGroup contextMenuGroup = (DefaultActionGroup)actionManager.getAction(IdeActions.GROUP_MAIN_CONTEXT_MENU);
        contextMenuGroup.add(newGroup);
        contextMenuGroup.addSeparator();

        DefaultActionGroup resourceOperation = new DefaultActionGroup(actionManager);
        resourceOperation.addSeparator();
        actionManager.registerAction("resourceOperation", resourceOperation);
        resourceOperation.add(deleteResourceAction);
        resourceOperation.add(renameResourceAction);
        contextMenuGroup.add(resourceOperation);

        DefaultActionGroup closeProjectGroup = new DefaultActionGroup(actionManager);
        closeProjectGroup.addSeparator();
        actionManager.registerAction("closeProjectGroup", closeProjectGroup);
        closeProjectGroup.add(closeProjectAction);
        contextMenuGroup.add(closeProjectGroup);

        newProjectWizard.addPage(newProjectPageProvider);
        newProjectWizard.addPage(chooseTemplatePageProvider);
        newProjectWizard.addPage(selectPaasPagePresenterProvider);
    }
}