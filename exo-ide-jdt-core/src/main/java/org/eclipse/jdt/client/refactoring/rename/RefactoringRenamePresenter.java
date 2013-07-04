/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package org.eclipse.jdt.client.refactoring.rename;

import com.codenvy.ide.collaboration.ResourceLockedPresenter;
import com.google.collide.client.CollabEditorExtension;
import com.google.collide.client.collaboration.CollaborationManager;
import com.google.collide.dto.FileOperationNotification.Operation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HasText;

import org.eclipse.jdt.client.JdtExtension;
import org.eclipse.jdt.client.UpdateOutlineEvent;
import org.eclipse.jdt.client.UpdateOutlineHandler;
import org.eclipse.jdt.client.core.JavaConventions;
import org.eclipse.jdt.client.core.JavaCore;
import org.eclipse.jdt.client.core.dom.ASTNode;
import org.eclipse.jdt.client.core.dom.CompilationUnit;
import org.eclipse.jdt.client.core.dom.NodeFinder;
import org.eclipse.jdt.client.event.ReparseOpenedFilesEvent;
import org.eclipse.jdt.client.refactoring.Action;
import org.eclipse.jdt.client.refactoring.ActionListUnmarshaller;
import org.eclipse.jdt.client.refactoring.RefactoringClientService;
import org.eclipse.jdt.client.runtime.IStatus;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorChangeActiveFileEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileContentChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileContentChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorReplaceFileEvent;
import org.exoplatform.ide.client.framework.event.FileSavedEvent;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.project.api.IDEProject;
import org.exoplatform.ide.client.framework.project.api.TreeRefreshedEvent;
import org.exoplatform.ide.client.framework.project.api.TreeRefreshedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.websocket.WebSocketException;
import org.exoplatform.ide.client.framework.websocket.rest.RequestCallback;
import org.exoplatform.ide.editor.client.api.Editor;
import org.exoplatform.ide.editor.shared.text.BadLocationException;
import org.exoplatform.ide.editor.shared.text.IDocument;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.FileContentUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Presenter for rename a Java element using refactoring.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: RefactoringRenamePresenter.java Jan 17, 2013 4:07:09 PM azatsarynnyy $
 */
public class RefactoringRenamePresenter implements RefactoringRenameHandler, ViewClosedHandler, VfsChangedHandler,
                   ProjectOpenedHandler, ProjectClosedHandler,
                   EditorActiveFileChangedHandler,
                   UpdateOutlineHandler, EditorFileOpenedHandler, EditorFileClosedHandler,
                   EditorFileContentChangedHandler, TreeRefreshedHandler {

    public interface Display extends IsView {
        /**
         * Returns name field.
         *
         * @return name field
         */
        TextFieldItem getNameField();

        /**
         * Returns label for warning messages.
         *
         * @return warning label
         */
        HasText getWarningLabel();

        /**
         * Returns label for error messages.
         *
         * @return error label
         */
        HasText getErrorLabel();

        /**
         * Returns rename button.
         *
         * @return rename button
         */
        HasClickHandlers getRenameButton();

        /**
         * Change enable state of the rename button.
         *
         * @param enabled
         *         <code>true</code> - enable, <code>false</code> - disable
         */
        void setRenameButtonEnabled(boolean enabled);

        /**
         * Returns the cancel button.
         *
         * @return cancel button
         */
        HasClickHandlers getCancelButton();

//        /**
//         * Set the new value for the name field.
//         *
//         * @param value
//         *         new value for name field
//         */
//        void setNewNameFieldValue(String value);

        /** Give focus to the name field. */
        void focusNameField();

        /** Select all text in the name field. */
        void selectNameField();
    }

    /** Default Maven 'sourceDirectory' value. */
    private static final String DEFAULT_SOURCE_FOLDER = "src/main/java";

    /** Default Maven 'testSourceDirectory' value. */
    private static final String DEFAULT_TEST_SOURCE_FOLDER = "src/test/java";

    /** Display. */
    private Display display;

    /** Info about current {@link VirtualFileSystem VFS}. */
    private VirtualFileSystemInfo vfsInfo;

    /** Project which is currently opened. */
    private ProjectModel openedProject;

    /** The map of the opened Java files and their id. */
    private Map<String, FileModel> openedFiles = new HashMap<String, FileModel>();

    /** The map of the opened Java files identifiers and their compilation units. */
    private Map<String, CompilationUnit> openedFilesToCompilationUnit = new HashMap<String, CompilationUnit>();

    /** The map of the opened Java files identifiers and their editors. */
    private Map<String, Editor> openedEditors = new HashMap<String, Editor>();

    /** Current compilation unit. */
    private CompilationUnit currentCompilationUnit;

    /** {@link ASTNode} to rename. */
    private ASTNode elementToRename;

    /** Active {@link FileModel file}. */
    private FileModel activeFile;

    /** Active editor. */
    private Editor activeEditor;

    /** Name of element to rename before rename refactoring. */
    private String originElementName;

    private FileModel fileToRenameFromPackageExplorer;

    private Map<Editor, String> editorsToUpdateContent = new HashMap<Editor, String>();

    /** Current cursor offset. */
    private int cursorOffset;

    public RefactoringRenamePresenter() {
        IDE.addHandler(RefactoringRenameEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
        IDE.addHandler(VfsChangedEvent.TYPE, this);
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(ProjectClosedEvent.TYPE, this);
        IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
        IDE.addHandler(UpdateOutlineEvent.TYPE, this);
        IDE.addHandler(EditorFileOpenedEvent.TYPE, this);
        IDE.addHandler(EditorFileClosedEvent.TYPE, this);
        IDE.addHandler(EditorFileContentChangedEvent.TYPE, this);
    }

    public void bindDisplay() {
        display.setRenameButtonEnabled(false);

        display.getNameField().addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                validateName(event.getValue(),
                             elementToRename == null ? ASTNode.TYPE_DECLARATION : elementToRename.getParent().getNodeType());
            }
        });

        display.getNameField().addKeyPressHandler(new KeyPressHandler() {
            @Override
            public void onKeyPress(KeyPressEvent event) {
                if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER && isNameChanged()) {
                    doRename();
                }
            }
        });

        display.getCancelButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        display.getRenameButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                doRename();
            }
        });
    }

    private boolean isNameChanged() {
        String newName = display.getNameField().getValue();
        return (newName != null && !newName.isEmpty() && !newName.equals(originElementName));
    }

    /** @see org.eclipse.jdt.client.refactoring.rename.RefactoringRenameHandler#onRename(org.eclipse.jdt.client.refactoring.rename
     * .RefactoringRenameEvent) */
    @Override
    public void onRename(RefactoringRenameEvent event) {
        fileToRenameFromPackageExplorer = event.getFile();

        FileModel fileToRename = fileToRenameFromPackageExplorer == null ? activeFile : fileToRenameFromPackageExplorer;
        CollaborationManager collaborationManager = CollabEditorExtension.get().getCollaborationManager();
        if (collaborationManager.isFileOpened(fileToRename.getPath())) {
            new ResourceLockedPresenter(
                    new SafeHtmlBuilder().appendHtmlConstant("Can't perform refactoring.").toSafeHtml(), collaborationManager,
                    fileToRename.getPath(), fileToRename, fileToRename.getPath(),
                    Operation.REFACTORING);
            return;
        }

        if (isUnsavedFilesExist()) {
            Dialogs.getInstance().showInfo(JdtExtension.LOCALIZATION_CONSTANT.refactoringRenameViewTitle(),
                                           JdtExtension.LOCALIZATION_CONSTANT.refactoringRenameSaveFiles());
            return;
        }

        if (!isFileReadyToRefactoring()) {
            Dialogs.getInstance().showInfo(JdtExtension.LOCALIZATION_CONSTANT.refactoringRenameViewTitle(),
                                           JdtExtension.LOCALIZATION_CONSTANT.refactoringRenameWait());
            return;
        }

        try {
            cursorOffset = getCursorOffset();
        } catch (BadLocationException e) {
            Dialogs.getInstance().showInfo(JdtExtension.LOCALIZATION_CONSTANT.refactoringRenameViewTitle(),
                                           JdtExtension.LOCALIZATION_CONSTANT.refactoringRenameBadCursorPosition());
            return;
        }

        elementToRename = null;
        if (fileToRenameFromPackageExplorer == null) {
            elementToRename = getElementToRename();
            if (elementToRename == null) {
                Dialogs.getInstance().showError(JdtExtension.LOCALIZATION_CONSTANT.refactoringRenameViewTitle(),
                                                JdtExtension.LOCALIZATION_CONSTANT.refactoringRenameUnavailable());
                return;
            }
        }

//        // temporary block renaming top-level class/interface/enum which is opened
//        if (isNodeDeclarationOpenedInSeparateFile(elementToRename)) {
//            Dialogs.getInstance().showError("Rename refactoring for this node temporary unavailable.");
//            return;
//        }

        originElementName = getElementName(elementToRename);

        openView();
        //display.setNewNameFieldValue(originElementName != null ? originElementName : "");
        display.getNameField().setValue(originElementName != null ? originElementName : "");
        display.focusNameField();
        
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                display.selectNameField();
            }
        });
    }

    /**
     * Checks whether there are any unsaved files.
     *
     * @return <code>true</code> if there are any unsaved files are exist,
     *         <code>false</code> if all opened files already saved
     */
    private boolean isUnsavedFilesExist() {
        for (FileModel file : openedFiles.values()) {
            if (file.isContentChanged()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether file ready to refactoring.
     *
     * @return <code>true</code> if file is ready, or
     *         <code>false</code> if not
     */
    private boolean isFileReadyToRefactoring() {
        if (fileToRenameFromPackageExplorer != null) {
            return true;
        }
        return currentCompilationUnit != null;
    }

    /**
     * Returns existing name of element to rename.
     *
     * @param element
     *         {@link ASTNode}
     * @return name of element or <code>null</code> if any error is occurred
     */
    private String getElementName(ASTNode element) {
        try {
            if (fileToRenameFromPackageExplorer == null) {
                return activeEditor.getDocument().get(element.getStartPosition(), element.getLength());
            }

            String name = fileToRenameFromPackageExplorer.getName();
            return name.substring(0, name.length() - 5); // excluding file extension ".java"
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Returns Java-element to rename.
     *
     * @return Java-element, or <code>null</null>
     *         if rename operation unavailable on the current text selection
     */
    private ASTNode getElementToRename() {
        try {
            IDocument document = activeEditor.getDocument();
            final int offset = document.getLineOffset(activeEditor.getCursorRow() - 1) + activeEditor.getSelectionRange().getStartSymbol();
            NodeFinder nf = new NodeFinder(currentCompilationUnit, offset, 0);
            ASTNode coveringNode = nf.getCoveringNode();
            if (coveringNode == null) {
                return null;
            }
            if (coveringNode.getNodeType() == ASTNode.SIMPLE_NAME) {
                return coveringNode;
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        return null;
    }

//    /** Temporary block renaming top-level class/interface/enum which is opened */
//    private boolean isNodeDeclarationOpenedInSeparateFile(ASTNode astNode) {
//        if (astNode == null) {
//            return false;
//        }
//        if (astNode.getNodeType() == ASTNode.SIMPLE_NAME) {
//            ASTNode parentNode = astNode.getParent();
//            if (parentNode instanceof AbstractTypeDeclaration || parentNode instanceof SimpleType) {
//                String nodeName = ((SimpleName)astNode).getIdentifier();
//                for (FileModel file : openedFiles.values()) {
//                    String fileName = file.getName().substring(0, file.getName().length() - 5); // excluding file extension ".java"
//                    if (nodeName.equals(fileName)) {
//                        return true;
//                    }
//                }
//            }
//        }
//        return false;
//    }

    /** Opens view for rename Java element. */
    private void openView() {
        if (display == null) {
            display = GWT.create(Display.class);
            IDE.getInstance().openView(display.asView());
            bindDisplay();
            postRenameActions = null;
        }
    }

    private List<Action> postRenameActions;
    
    /** Sends request for rename refactoring to the server (over WebSocket). */
    private void doRename() {
        FileModel originalFile = fileToRenameFromPackageExplorer != null ? fileToRenameFromPackageExplorer : activeFile;
        
        try {
            String fqn = getFqn(originalFile);
            ActionListUnmarshaller unmarshaller = new ActionListUnmarshaller();
            RefactoringClientService.getInstance().renameWS(vfsInfo.getId(), openedProject.getId(), fqn, cursorOffset,
                     display.getNameField().getValue(), new RequestCallback<List<Action>>(unmarshaller) {

                @Override
                protected void onSuccess(List<Action> result) {
                    postRenameActions = result;
                    serverRenameComplete();
                }

                @Override
                protected void onFailure(Throwable exception) {
                    IDE.eventBus().fireEvent(new ExceptionThrownEvent(exception));
                }
            });
        } catch (WebSocketException e) {
            doRenameREST();
        }
    }

    /**
     * Sends request for rename refactoring to the server (over HTTP).
     *
     * @param fqn
     */
    private void doRenameREST() {
        FileModel originalFile = fileToRenameFromPackageExplorer != null ? fileToRenameFromPackageExplorer : activeFile;
        
        try {
            String fqn = getFqn(originalFile);
            ActionListUnmarshaller unmarshaller = new ActionListUnmarshaller();
            RefactoringClientService.getInstance().rename(vfsInfo.getId(), openedProject.getId(), fqn, cursorOffset,
                                                          display.getNameField().getValue(), new AsyncRequestCallback<List<Action>>(unmarshaller) {
                @Override
                protected void onSuccess(List<Action> result) {
                    postRenameActions = result;
                    serverRenameComplete();
                }

                @Override
                protected void onFailure(Throwable exception) {
                    IDE.eventBus().fireEvent(new ExceptionThrownEvent(exception));
                }
            });
        } catch (RequestException e) {
            IDE.eventBus().fireEvent(new ExceptionThrownEvent(e));
        }
    }
    
    private void serverRenameComplete() {
        if (display != null) {
            IDE.getInstance().closeView(display.asView().getId());            
        }
        
        IDE.addHandler(TreeRefreshedEvent.TYPE, this);
        IDE.fireEvent(new RefreshBrowserEvent(openedProject));        
    }
    
    private void renameFailed(Throwable error) {
        IDE.eventBus().fireEvent(new ExceptionThrownEvent(error));
        reparseOpenedFiles();
    }
    
    private void onRenameSuccess() {
        reparseOpenedFiles();
    }
       
    @Override
    public void onTreeRefreshed(final TreeRefreshedEvent event) {
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                IDE.removeHandler(TreeRefreshedEvent.TYPE, RefactoringRenamePresenter.this);
                updateFilesAfterRename();
            }
        });
    }
    
    private void updateFilesAfterRename() {
        if (postRenameActions != null && !postRenameActions.isEmpty()) {            
            final Action action = postRenameActions.remove(0);
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override
                public void execute() {
                    if (Action.MOVE.equals(action.getAction())) {
                        move(action.getResource(), action.getDestination());
                    } else if (Action.UPDATE_CONTENT.equals(action.getAction())) {
                        updateContent(action.getResource());
                    } else {
                        renameFailed(new Exception("Unknown action " + action.getAction()));
                    }
                }
            });            
        } else {
            onRenameSuccess();
        }
    }

    private String movedResource;
    
    private void move(String source, String destination) {
        movedResource = null;
        
        final FileModel sourceFile = getOpenedFile(source);
        if (sourceFile == null) {
            updateFilesAfterRename();
            return;
        }
        
        final FileModel destinationFile = (FileModel)((IDEProject)openedProject).getResource(destination);
        
        try {
            VirtualFileSystem.getInstance().getContent(
                   new AsyncRequestCallback<FileModel>(new FileContentUnmarshaller(destinationFile)) {
                       @Override
                       protected void onSuccess(final FileModel result) {
                           FileModel activeFileBeforeMove = activeFile;
                           
                           final Editor editor = openedEditors.get(sourceFile.getId());
                           final int cursorColumn = editor.getCursorColumn();
                           final int cursorRow = editor.getCursorRow();

                           openedEditors.remove(sourceFile.getId());
                           openedEditors.put(destinationFile.getId(), editor);

                           if (activeFile.getId().equals(sourceFile.getId())) {
                               IDE.fireEvent(new EditorReplaceFileEvent(sourceFile, destinationFile));
                           } else {
                               IDE.fireEvent(new EditorReplaceFileEvent(sourceFile, destinationFile, false));
                               editorsToUpdateContent.put(editor, destinationFile.getContent());
                           }
                           
                           movedResource = destinationFile.getPath();
                           
                           if (activeFileBeforeMove != null && activeFile != null &&
                               !activeFileBeforeMove.getPath().equals(activeFile.getPath())) {
                               IDE.fireEvent(new EditorChangeActiveFileEvent(activeFileBeforeMove));
                           }
                           
                           // need some time to update editor's content
                           Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                               @Override
                               public void execute() {
                                   editor.setCursorPosition(cursorRow, cursorColumn);
                                   result.setContentChanged(false);
                                   IDE.fireEvent(new FileSavedEvent(result, null));

                                   updateFilesAfterRename();
                               }
                           });                           
                       }

                       @Override
                       protected void onFailure(Throwable exception) {
                           renameFailed(exception);
                       }
                   });
        } catch (RequestException e) {
            renameFailed(e);
        }        
        
    }
    
    private FileModel getOpenedFile(String path) {
        for (Entry<String, FileModel> entry : openedFiles.entrySet()) {
            if (entry.getValue().getPath().equals(path)) {
                return entry.getValue();
            }
        }
        
        return null;
    }
        
    private void updateContent(String resource) {
        if (movedResource != null && movedResource.equals(resource)) {
            updateFilesAfterRename();
            return;
        }
        
        final FileModel file = getOpenedFile(resource);
        if (file == null) {
            updateFilesAfterRename();
            return;            
        }
        
        final Editor editor = openedEditors.get(file.getId());
        if (editor == null) {
            updateFilesAfterRename();
            return;                       
        }
        
        try {
            VirtualFileSystem.getInstance().getContent(
                   new AsyncRequestCallback<FileModel>(new FileContentUnmarshaller(file)) {
                       @Override
                       protected void onSuccess(final FileModel result) {
                           if (editor != activeEditor) {
                               editorsToUpdateContent.put(editor, file.getContent());
                           } else {
                               updateEditorContent(editor, result, result.getContent());
                           }
                           
                           updateFilesAfterRename();
                       }

                       @Override
                       protected void onFailure(Throwable exception) {
                           renameFailed(exception);
                       }
                   });
        } catch (RequestException e) {
            renameFailed(e);
        }        
    }

    /** Reparse all opened files to get their {@link CompilationUnit}s. */
    private void reparseOpenedFiles() {
        openedFilesToCompilationUnit.clear();
        currentCompilationUnit = null;
        IDE.fireEvent(new ReparseOpenedFilesEvent());
    }

    /**
     * Returns the fully-qualified name of the top-level class from the provided file.
     *
     * @param file
     *         {@link FileModel}
     * @return fqn of the top-level class from provided file,
     *         or <code>null</code> if calculating fqn is missing
     */
    private String getFqn(FileModel file) {
        ProjectModel project = file.getProject();
        String sourcePath =
                project.hasProperty("sourceFolder") ? (String)project.getPropertyValue("sourceFolder") : DEFAULT_SOURCE_FOLDER;
        String fqn = null;
        if (file.getPath().indexOf(sourcePath) != -1) {
            fqn = file.getPath().substring((project.getPath() + "/" + sourcePath + "/").length());
        } else if (file.getPath().indexOf(DEFAULT_TEST_SOURCE_FOLDER) != -1) {
            fqn = file.getPath().substring((project.getPath() + "/" + DEFAULT_TEST_SOURCE_FOLDER + "/").length());
        } else {
            return null;
        }

        fqn = fqn.replaceAll("/", ".");
        fqn = fqn.substring(0, fqn.lastIndexOf('.'));
        return fqn;
    }

    private int getCursorOffset() throws BadLocationException {
        if (fileToRenameFromPackageExplorer != null) {
            return -1;
        }

        return activeEditor.getDocument().getLineOffset(activeEditor.getCursorRow() - 1)
               + activeEditor.getSelectionRange().getStartSymbol();
    }

    /**
     * Validate the given name.
     *
     * @param name
     *         name of a element to check
     * @param type
     *         type of {@link ASTNode} to check name
     */
    private void validateName(String name, int type) {
        String sourceLevel = JavaCore.getOption(JavaCore.COMPILER_SOURCE);
        String complianceLevel = JavaCore.getOption(JavaCore.COMPILER_COMPLIANCE);
        IStatus status = null;

        if (type == ASTNode.TYPE_DECLARATION || type == ASTNode.SIMPLE_TYPE) {
            status = JavaConventions.validateJavaTypeName(name, sourceLevel, complianceLevel);
        } else if (type == ASTNode.PACKAGE_DECLARATION) {
            status = JavaConventions.validatePackageName(name, sourceLevel, complianceLevel);
        } else if (type == ASTNode.COMPILATION_UNIT) {
            status = JavaConventions.validateCompilationUnitName(name, sourceLevel, complianceLevel);
        } else {
            status = JavaConventions.validateIdentifier(name, sourceLevel, complianceLevel);
        }

        switch (status.getSeverity()) {
            case IStatus.WARNING:
                display.setRenameButtonEnabled(isNameChanged());
                display.getWarningLabel().setText(status.getMessage());
                display.getErrorLabel().setText("");
                break;
            case IStatus.OK:
                display.setRenameButtonEnabled(isNameChanged());
                display.getWarningLabel().setText("");
                display.getErrorLabel().setText("");
                break;
            default:
                display.setRenameButtonEnabled(false);
                display.getWarningLabel().setText("");
                display.getErrorLabel().setText(status.getMessage());
                break;
        }
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     * .event.ViewClosedEvent) */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

    /** @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework
     * .application.event.VfsChangedEvent) */
    @Override
    public void onVfsChanged(VfsChangedEvent event) {
        vfsInfo = event.getVfsInfo();
    }

    /** @see org.exoplatform.ide.client.framework.project.ProjectClosedHandler#onProjectClosed(org.exoplatform.ide.client.framework
     * .project.ProjectClosedEvent) */
    @Override
    public void onProjectClosed(ProjectClosedEvent event) {
        openedProject = null;
    }

    /** @see org.exoplatform.ide.client.framework.project.ProjectOpenedHandler#onProjectOpened(org.exoplatform.ide.client.framework
     * .project.ProjectOpenedEvent) */
    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        openedProject = event.getProject();
    }

    /** @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform
     * .ide.client.framework.editor.event.EditorActiveFileChangedEvent) */
    @Override
    public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {
        activeFile = event.getFile();

        if (activeFile == null || !event.getFile().getMimeType().equals(MimeType.APPLICATION_JAVA)) {
            activeEditor = null;
            return;
        }

        activeEditor = event.getEditor();

        // TODO workaround of bug to updating content of editors in inactive tabs in CollabEditor
        final String content = editorsToUpdateContent.remove(activeEditor);
        if (content != null) {
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {

                @Override
                public void execute() {
                    updateEditorContent(activeEditor, activeFile, content);
                    Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                        @Override
                        public void execute() {
                            IDE.fireEvent(new FileSavedEvent(activeFile, null));
                        }
                    });
                }
            });
        }

        currentCompilationUnit = openedFilesToCompilationUnit.get(activeFile.getId());
    }

    /**
     * Updates {@link Editor} content.
     *
     * @param editor
     *         {@link Editor} to update
     * @param content
     *         new content
     * @param file
     *         editing {@link FileModel file}
     */
    private void updateEditorContent(final Editor editor, final FileModel file, String content) {
        final int cursorColumn = editor.getCursorColumn();
        final int cursorRow = editor.getCursorRow();
        editor.getDocument().set(file.getContent());

        // need some time to update editor's content
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                editor.setCursorPosition(cursorRow, cursorColumn);

                file.setContentChanged(false);
                IDE.fireEvent(new FileSavedEvent(file, null));
            }
        });
    }

    /** @see org.eclipse.jdt.client.UpdateOutlineHandler#onUpdateOutline(org.eclipse.jdt.client.UpdateOutlineEvent) */
    @Override
    public void onUpdateOutline(UpdateOutlineEvent event) {
        openedFilesToCompilationUnit.put(event.getFile().getId(), event.getCompilationUnit());

        if (event.getFile().getId().equals(activeFile.getId())) {
            currentCompilationUnit = event.getCompilationUnit();
        }
    }

    /** @see org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler#onEditorFileClosed(org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent) */
    @Override
    public void onEditorFileClosed(EditorFileClosedEvent event) {
        openedFiles = event.getOpenedFiles();
        openedEditors.remove(event.getFile().getId());

        CompilationUnit cUnit = openedFilesToCompilationUnit.remove(event.getFile().getId());
        if (currentCompilationUnit != null && currentCompilationUnit.equals(cUnit)) {
            currentCompilationUnit = null;
        }
    }

    /** @see org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler#onEditorFileOpened(org.exoplatform.ide.client
     * .framework.editor.event.EditorFileOpenedEvent) */
    @Override
    public void onEditorFileOpened(EditorFileOpenedEvent event) {
        openedFiles = event.getOpenedFiles();
        openedEditors.put(event.getFile().getId(), event.getEditor());
    }

    /** @see org.exoplatform.ide.client.framework.editor.event.EditorFileContentChangedHandler#onEditorFileContentChanged(org.exoplatform.ide.client.framework.editor.event.EditorFileContentChangedEvent) */
    @Override
    public void onEditorFileContentChanged(EditorFileContentChangedEvent event) {
        FileModel file = event.getFile();
        openedFiles.put(file.getId(), file);
    }

}
