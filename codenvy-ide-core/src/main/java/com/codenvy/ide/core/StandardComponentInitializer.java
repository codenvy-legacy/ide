/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.core;

import com.codenvy.ide.Constants;
import com.codenvy.ide.Resources;
import com.codenvy.ide.actions.ChangeProjectTypeAction;
import com.codenvy.ide.actions.CloseProjectAction;
import com.codenvy.ide.actions.DeleteResourceAction;
import com.codenvy.ide.actions.ExpandEditorAction;
import com.codenvy.ide.actions.FindActionAction;
import com.codenvy.ide.actions.FormatterAction;
import com.codenvy.ide.actions.ImportProjectFromLocationAction;
import com.codenvy.ide.actions.NavigateToFileAction;
import com.codenvy.ide.actions.NewProjectWizardAction;
import com.codenvy.ide.actions.OpenProjectAction;
import com.codenvy.ide.actions.RedoAction;
import com.codenvy.ide.actions.RenameResourceAction;
import com.codenvy.ide.actions.SaveAction;
import com.codenvy.ide.actions.SaveAllAction;
import com.codenvy.ide.actions.ShowAboutAction;
import com.codenvy.ide.actions.ShowPreferencesAction;
import com.codenvy.ide.actions.UndoAction;
import com.codenvy.ide.actions.UploadFileAction;
import com.codenvy.ide.api.editor.EditorRegistry;
import com.codenvy.ide.api.filetypes.FileType;
import com.codenvy.ide.api.filetypes.FileTypeRegistry;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.ui.Icon;
import com.codenvy.ide.api.ui.IconRegistry;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.action.DefaultActionGroup;
import com.codenvy.ide.api.ui.action.IdeActions;
import com.codenvy.ide.api.ui.keybinding.KeyBindingAgent;
import com.codenvy.ide.api.ui.keybinding.KeyBuilder;
import com.codenvy.ide.api.ui.wizard.ProjectTypeWizardRegistry;
import com.codenvy.ide.api.ui.wizard.ProjectWizard;
import com.codenvy.ide.image.viewer.ImageViewerProvider;
import com.codenvy.ide.newresource.NewFileAction;
import com.codenvy.ide.newresource.NewFolderAction;
import com.codenvy.ide.toolbar.MainToolbar;
import com.codenvy.ide.toolbar.ToolbarPresenter;
import com.codenvy.ide.xml.NewXmlFileAction;
import com.codenvy.ide.xml.editor.XmlEditorProvider;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import org.vectomatic.dom.svg.ui.SVGResource;

import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_FILE_NEW;

/**
 * Initializer for standard component i.e. some basic menu commands (Save, Save As etc)
 *
 * @author Evgen Vidolob
 */
@Singleton
public class StandardComponentInitializer {
    public interface ParserResource extends ClientBundle {
        @Source("com/codenvy/ide/blank.svg")
        SVGResource  samplesCategoryBlank();
    }

    @Inject
    private EditorRegistry editorRegistry;

    @Inject
    private FileTypeRegistry fileTypeRegistry;

    @Inject
    private XmlEditorProvider xmlEditorProvider;

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
    private OpenProjectAction openProjectAction;

    @Inject
    private FormatterAction formatterAction;

    @Inject
    private UndoAction undoAction;

    @Inject
    private RedoAction redoAction;

    @Inject
    private UploadFileAction uploadFileAction;

    @Inject
    private ImportProjectFromLocationAction importProjectFromLocationAction;

    @Inject
    private NewProjectWizardAction newProjectWizardAction;

    @Inject
    private NewFolderAction newFolderAction;

    @Inject
    private NewFileAction newFileAction;

    @Inject
    private NewXmlFileAction newXmlFileAction;

    @Inject
    private ImageViewerProvider imageViewerProvider;

    @Inject
    private ProjectTypeWizardRegistry wizardRegistry;

    @Inject
    private NotificationManager notificationManager;

    @Inject
    private ChangeProjectTypeAction changeProjectTypeAction;

    @Inject
    private ExpandEditorAction expandEditorAction;

    @Inject
    @Named("XMLFileType")
    private FileType xmlFile;

    @Inject
    @Named("PNGFileType")
    private FileType pngFile;

    @Inject
    @Named("BMPFileType")
    private FileType bmpFile;

    @Inject
    @Named("GIFFileType")
    private FileType gifFile;

    @Inject
    @Named("ICOFileType")
    private FileType iconFile;

    @Inject
    @Named("SVGFileType")
    private FileType svgFile;

    @Inject
    @Named("JPEFileType")
    private FileType jpeFile;

    @Inject
    @Named("JPEGFileType")
    private FileType jpegFile;

    @Inject
    @Named("JPGFileType")
    private FileType jpgFile;

    /** Instantiates {@link StandardComponentInitializer} an creates standard content. */
    @Inject
    public StandardComponentInitializer(IconRegistry iconRegistry, StandardComponentInitializer.ParserResource parserResource) {
        iconRegistry.registerIcon(new Icon("blank.samples.category.icon", parserResource.samplesCategoryBlank()));
    }

    public void initialize() {
        fileTypeRegistry.registerFileType(xmlFile);
        editorRegistry.registerDefaultEditor(xmlFile, xmlEditorProvider);

        fileTypeRegistry.registerFileType(pngFile);
        editorRegistry.registerDefaultEditor(pngFile, imageViewerProvider);

        fileTypeRegistry.registerFileType(bmpFile);
        editorRegistry.registerDefaultEditor(bmpFile, imageViewerProvider);

        fileTypeRegistry.registerFileType(gifFile);
        editorRegistry.registerDefaultEditor(gifFile, imageViewerProvider);

        fileTypeRegistry.registerFileType(iconFile);
        editorRegistry.registerDefaultEditor(iconFile, imageViewerProvider);

        fileTypeRegistry.registerFileType(svgFile);
        editorRegistry.registerDefaultEditor(svgFile, imageViewerProvider);

        fileTypeRegistry.registerFileType(jpeFile);
        editorRegistry.registerDefaultEditor(jpeFile, imageViewerProvider);

        fileTypeRegistry.registerFileType(jpegFile);
        editorRegistry.registerDefaultEditor(jpegFile, imageViewerProvider);

        fileTypeRegistry.registerFileType(jpgFile);
        editorRegistry.registerDefaultEditor(jpgFile, imageViewerProvider);

        // Compose Import Project group
        DefaultActionGroup importProjectGroup = new DefaultActionGroup("Import Project", true, actionManager);
        importProjectGroup.getTemplatePresentation().setSVGIcon(resources.importProject());
        actionManager.registerAction(IdeActions.GROUP_IMPORT_PROJECT, importProjectGroup);
        actionManager.registerAction("importProject", importProjectFromLocationAction);
        importProjectGroup.addAction(importProjectFromLocationAction);

        // Compose New group
        DefaultActionGroup newGroup = new DefaultActionGroup("New", true, actionManager);
        newGroup.getTemplatePresentation().setSVGIcon(resources.newResource());
        actionManager.registerAction(GROUP_FILE_NEW, newGroup);
        actionManager.registerAction("newProject", newProjectWizardAction);
        actionManager.registerAction("newFile", newFileAction);
        actionManager.registerAction("newFolder", newFolderAction);
        actionManager.registerAction("newXmlFile", newXmlFileAction);

        newGroup.addAction(newProjectWizardAction);
        newGroup.addSeparator();
        newGroup.addAction(newFileAction);
        newGroup.addAction(newFolderAction);
        newGroup.addSeparator();
        newGroup.addAction(newXmlFileAction);

        actionManager.registerAction("uploadFile", uploadFileAction);
        actionManager.registerAction("navigateToFile", navigateToFileAction);
        actionManager.registerAction("ChangeProjectType", changeProjectTypeAction);

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
        fileGroup.add(openProjectAction);
        fileGroup.add(closeProjectAction);
        fileGroup.add(changeProjectTypeAction);
        fileGroup.add(uploadFileAction);
        fileGroup.add(navigateToFileAction);
        fileGroup.add(renameResourceAction);
        fileGroup.add(saveGroup);

        // Compose Code menu
        DefaultActionGroup codeGroup = (DefaultActionGroup)actionManager.getAction(IdeActions.GROUP_CODE);
        actionManager.registerAction("format", formatterAction);
        actionManager.registerAction("undo", undoAction);
        actionManager.registerAction("redo", redoAction);
        codeGroup.add(formatterAction);
        codeGroup.add(undoAction);
        codeGroup.add(redoAction);

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

        actionManager.registerAction("expandEditor", expandEditorAction);
        DefaultActionGroup rightMenuGroup = (DefaultActionGroup)actionManager.getAction(IdeActions.GROUP_RIGHT_MAIN_MENU);
        rightMenuGroup.add(expandEditorAction);

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
        keyBinding.getGlobal().addKey(new KeyBuilder().action().charCode('z').build(), "undo");
        keyBinding.getGlobal().addKey(new KeyBuilder().action().charCode('Z').build(), "redo");
        keyBinding.getGlobal().addKey(new KeyBuilder().action().alt().charCode('n').build(), "navigateToFile");
        keyBinding.getGlobal().addKey(new KeyBuilder().action().charCode('s').build(), "save");
        keyBinding.getGlobal().addKey(new KeyBuilder().action().charCode('S').build(), "saveAll");
        keyBinding.getGlobal().addKey(new KeyBuilder().action().charCode('A').build(), "findActionAction");

        wizardRegistry.addWizard(Constants.BLANK_ID, new ProjectWizard(notificationManager));
    }
}
