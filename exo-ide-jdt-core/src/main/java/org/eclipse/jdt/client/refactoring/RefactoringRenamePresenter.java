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
package org.eclipse.jdt.client.refactoring;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;

import org.eclipse.jdt.client.UpdateOutlineEvent;
import org.eclipse.jdt.client.UpdateOutlineHandler;
import org.eclipse.jdt.client.core.dom.ASTNode;
import org.eclipse.jdt.client.core.dom.CompilationUnit;
import org.eclipse.jdt.client.core.dom.NodeFinder;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileContentChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileContentChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.project.ActiveProjectChangedEvent;
import org.exoplatform.ide.client.framework.project.ActiveProjectChangedHandler;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
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
import java.util.Map;

/**
 * Presenter for rename a Java element using refactoring.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: RefactoringRenamePresenter.java Jan 17, 2013 4:07:09 PM azatsarynnyy $
 *
 */
public class RefactoringRenamePresenter implements RefactoringRenameHandler, ViewClosedHandler, VfsChangedHandler,
   ProjectOpenedHandler, ProjectClosedHandler, ActiveProjectChangedHandler, EditorActiveFileChangedHandler,
   UpdateOutlineHandler, EditorFileOpenedHandler, EditorFileClosedHandler, EditorFileContentChangedHandler
{

   public interface Display extends IsView
   {
      TextFieldItem getNewNameField();

      HasClickHandlers getRenameButton();

      void setEnableStateRenameButton(boolean enabled);

      HasClickHandlers getCancelButton();

      /**
       * Set value for 'New Name' field.
       * 
       * @param value new value for 'New Name' field
       */
      void setNewNameFieldValue(String value);

      /**
       * Give focus to 'New Name' field.
       */
      void setFocusOnNewNameField();

      /**
       * Select all text in 'New Name' field.
       */
      void selectAllTextInNewNameField();
   }

   /**
    * Default Maven 'sourceDirectory' value.
    */
   private static final String DEFAULT_SOURCE_FOLDER = "src/main/java";

   private Display display;

   private VirtualFileSystemInfo vfsInfo;

   /**
    * Project which is currently opened.
    */
   private ProjectModel openedProject;

   private Map<String, FileModel> openedFiles = new HashMap<String, FileModel>();

   private Map<String, Editor> openedEditors = new HashMap<String, Editor>();

   /**
    * Current editor.
    */
   private Editor editor;

   private CompilationUnit compilationUnit;

   private FileModel activeFile;

   public RefactoringRenamePresenter()
   {
      IDE.addHandler(RefactoringRenameEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
      IDE.addHandler(VfsChangedEvent.TYPE, this);
      IDE.addHandler(ProjectOpenedEvent.TYPE, this);
      IDE.addHandler(ProjectClosedEvent.TYPE, this);
      IDE.addHandler(ActiveProjectChangedEvent.TYPE, this);
      IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      IDE.addHandler(UpdateOutlineEvent.TYPE, this);
      IDE.addHandler(EditorFileOpenedEvent.TYPE, this);
      IDE.addHandler(EditorFileClosedEvent.TYPE, this);
      IDE.addHandler(EditorFileContentChangedEvent.TYPE, this);
   }

   public void bindDisplay()
   {
      display.setEnableStateRenameButton(false);

      display.getNewNameField().addValueChangeHandler(new ValueChangeHandler<String>()
      {

         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            display.setEnableStateRenameButton(event.getValue() != null && !event.getValue().isEmpty());
         }
      });

      display.getNewNameField().addKeyPressHandler(new KeyPressHandler()
      {

         @Override
         public void onKeyPress(KeyPressEvent event)
         {
            if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER)
            {
               // TODO
               doRenameREST();
            }
         }
      });

      display.getCancelButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
      });

      display.getRenameButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            // TODO
            doRenameREST();
         }
      });

   }

   /**
    * @see org.eclipse.jdt.client.refactoring.RefactoringRenameHandler#onRename(org.eclipse.jdt.client.refactoring.RefactoringRenameEvent)
    */
   @Override
   public void onRename(RefactoringRenameEvent event)
   {
      if (isUnsavedFilesExist())
      {
         Dialogs.getInstance().showInfo("Rename", "You should save all unsaved files to continue.");
         return;
      }

      ASTNode element = getElementToRename();
      if (element == null)
      {
         Dialogs
            .getInstance()
            .showError("Rename",
               "Rename operation unavailable on the current selection.<br>Select a local variable, parameter, method, or a type.");
         return;
      }

      String elementName = null;
      try
      {
         elementName = editor.getDocument().get(element.getStartPosition(), element.getLength());
      }
      catch (BadLocationException e)
      {
         e.printStackTrace();
      }

      openView();
      display.setNewNameFieldValue(elementName);
      display.selectAllTextInNewNameField();
      display.setFocusOnNewNameField();
   }

   /**
    * Checks whether there are any unsaved files.
    * 
    * @return <code>true</code> if there are any unsaved files are exist,
    *          <code>false</code> if all opened files already saved
    */
   private boolean isUnsavedFilesExist()
   {
      for (FileModel file : openedFiles.values())
      {
         if (file.isContentChanged())
         {
            return true;
         }
      }
      return false;
   }

   /**
    * Returns Java-element to rename.
    * 
    * @return Java-element, or <code>null</null>
    *          if rename operation unavailable on the current text selection
    */
   private ASTNode getElementToRename()
   {
      try
      {
         IDocument document = editor.getDocument();
         int offset = document.getLineOffset(editor.getCursorRow() - 1) + editor.getSelectionRange().getStartSymbol();
         NodeFinder nf = new NodeFinder(compilationUnit, offset, 0);
         ASTNode coveringNode = nf.getCoveringNode();

         if (coveringNode == null)
         {
            return null;
         }

         if (coveringNode.getNodeType() == ASTNode.SIMPLE_NAME)
         {
            IDE.fireEvent(new OutputEvent(coveringNode.getLocationInParent().toString()));
            return coveringNode;
            //            ASTNode parentNode = coveringNode.getParent();
            //            StructuralPropertyDescriptor descriptor = coveringNode.getLocationInParent();
            //
            //            if (parentNode instanceof SingleVariableDeclaration)
            //            {
            //               if (descriptor == SingleVariableDeclaration.NAME_PROPERTY)
            //               {
            //                  return coveringNode;
            //               }
            //            }
            //            else if (parentNode instanceof VariableDeclarationFragment)
            //            {
            //               if (descriptor == VariableDeclarationFragment.NAME_PROPERTY)
            //               {
            //                  return coveringNode;
            //               }
            //            }
            //            else if (parentNode instanceof MethodDeclaration)
            //            {
            //               MethodDeclaration p = (MethodDeclaration)parentNode;
            //               // could be the name of the method or constructor
            //               if (!p.isConstructor() && (descriptor == MethodDeclaration.NAME_PROPERTY))
            //               {
            //                  return coveringNode;
            //               }
            //            }
            //            else if (parentNode instanceof MethodInvocation)
            //            {
            //               if (descriptor == MethodInvocation.NAME_PROPERTY)
            //               {
            //                  return coveringNode;
            //               }
            //            }
            //            else if (parentNode instanceof TypeDeclaration)
            //            {
            //               if (descriptor == TypeDeclaration.NAME_PROPERTY)
            //               {
            //                  return coveringNode;
            //               }
            //            }
            //            else if (parentNode instanceof SimpleType)
            //            {
            //               if (descriptor == SimpleType.NAME_PROPERTY)
            //               {
            //                  return coveringNode;
            //               }
            //            }
            //            else if (parentNode instanceof ClassInstanceCreation)
            //            {
            //               if (descriptor == ClassInstanceCreation.NAME_PROPERTY)
            //               {
            //                  return coveringNode;
            //               }
            //            }
         }
      }
      catch (BadLocationException e)
      {
         e.printStackTrace();
      }
      return null;
   }

   /**
    * Opens view for rename Java element.
    */
   private void openView()
   {
      if (display == null)
      {
         display = GWT.create(Display.class);
         IDE.getInstance().openView(display.asView());
         bindDisplay();
      }
   }

   /**
    * Sends request for rename refactoring to the server (over WebSocket).
    */
   private void doRename()
   {
      try
      {
         String fqn = getFqn();
         int offset =
            editor.getDocument().getLineOffset(editor.getCursorRow() - 1) + editor.getSelectionRange().getStartSymbol();
         String newName = display.getNewNameField().getValue();

         RefactoringClientService.getInstance().renameWS(vfsInfo.getId(), openedProject.getId(), fqn, offset, newName,
            new RequestCallback<Object>()
            {

               @Override
               protected void onSuccess(Object result)
               {
                  reloadOpenedFilesContent();
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  IDE.eventBus().fireEvent(new ExceptionThrownEvent(exception));
               }
            });
      }
      catch (WebSocketException e)
      {
         doRenameREST();
      }
      catch (BadLocationException e)
      {
         IDE.eventBus().fireEvent(new ExceptionThrownEvent(e));
      }

      IDE.getInstance().closeView(display.asView().getId());
   }

   /**
    * Sends request for rename refactoring to the server (over HTTP).
    */
   private void doRenameREST()
   {
      try
      {
         String fqn = getFqn();
         int offset =
            editor.getDocument().getLineOffset(editor.getCursorRow() - 1) + editor.getSelectionRange().getStartSymbol();
         String newName = display.getNewNameField().getValue();

         RefactoringClientService.getInstance().rename(vfsInfo.getId(), openedProject.getId(), fqn, offset, newName,
            new AsyncRequestCallback<Object>()
            {

               @Override
               protected void onSuccess(Object result)
               {
                  reloadOpenedFilesContent();
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  // TODO
                  reloadOpenedFilesContent();
                  IDE.eventBus().fireEvent(new ExceptionThrownEvent(exception));
               }
            });
      }
      catch (RequestException e)
      {
         IDE.eventBus().fireEvent(new ExceptionThrownEvent(e));
      }
      catch (BadLocationException e)
      {
         IDE.eventBus().fireEvent(new ExceptionThrownEvent(e));
      }

      IDE.getInstance().closeView(display.asView().getId());
   }

   /**
    * Reload (re-read) content of all opened files.
    */
   private void reloadOpenedFilesContent()
   {
      for (FileModel file : openedFiles.values())
      {
         try
         {
            VirtualFileSystem.getInstance().getContent(
               new AsyncRequestCallback<FileModel>(new FileContentUnmarshaller(file))
               {

                  @Override
                  protected void onSuccess(FileModel result)
                  {
                     Editor editor = openedEditors.get(result.getId());
                     if (editor != null)
                     {
                        editor.setText(result.getContent());
                     }
                  }

                  @Override
                  protected void onFailure(Throwable exception)
                  {
                     // nothing to do
                  }
               });
         }
         catch (RequestException e)
         {
            IDE.eventBus().fireEvent(new ExceptionThrownEvent(e));
         }

         IDE.fireEvent(new RefreshBrowserEvent(file.getParent()));
      }
   }

   /**
    * Returns the fully qualified name of the top-level class from <code>activeFile</code>.
    * 
    * @return FQN of the top-level class from <code>activeFile</code>
    */
   private String getFqn()
   {
      ProjectModel project = activeFile.getProject();
      String sourcePath =
         project.hasProperty("sourceFolder") ? (String)project.getPropertyValue("sourceFolder") : DEFAULT_SOURCE_FOLDER;
      String fqn = activeFile.getPath().substring((project.getPath() + "/" + sourcePath + "/").length());
      fqn = fqn.replaceAll("/", ".");
      fqn = fqn.substring(0, fqn.lastIndexOf('.'));
      return fqn;
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent)
    */
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework.application.event.VfsChangedEvent)
    */
   @Override
   public void onVfsChanged(VfsChangedEvent event)
   {
      vfsInfo = event.getVfsInfo();
   }

   /**
    * @see org.exoplatform.ide.client.framework.project.ActiveProjectChangedHandler#onActiveProjectChanged(org.exoplatform.ide.client.framework.project.ActiveProjectChangedEvent)
    */
   @Override
   public void onActiveProjectChanged(ActiveProjectChangedEvent event)
   {
      openedProject = event.getProject();
   }

   /**
    * @see org.exoplatform.ide.client.framework.project.ProjectClosedHandler#onProjectClosed(org.exoplatform.ide.client.framework.project.ProjectClosedEvent)
    */
   @Override
   public void onProjectClosed(ProjectClosedEvent event)
   {
      openedProject = null;
   }

   /**
    * @see org.exoplatform.ide.client.framework.project.ProjectOpenedHandler#onProjectOpened(org.exoplatform.ide.client.framework.project.ProjectOpenedEvent)
    */
   @Override
   public void onProjectOpened(ProjectOpenedEvent event)
   {
      openedProject = event.getProject();
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent)
    */
   @Override
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      if (event.getFile() == null)
      {
         return;
      }

      activeFile = event.getFile();

      if (event.getFile().getMimeType().equals(MimeType.APPLICATION_JAVA))
      {
         editor = event.getEditor();
      }
      else
      {
         editor = null;
      }
   }

   /**
    * @see org.eclipse.jdt.client.UpdateOutlineHandler#onUpdateOutline(org.eclipse.jdt.client.UpdateOutlineEvent)
    */
   @Override
   public void onUpdateOutline(UpdateOutlineEvent event)
   {
      compilationUnit = event.getCompilationUnit();
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler#onEditorFileClosed(org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent)
    */
   @Override
   public void onEditorFileClosed(EditorFileClosedEvent event)
   {
      openedFiles = event.getOpenedFiles();
      openedEditors.remove(event.getFile().getId());
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler#onEditorFileOpened(org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent)
    */
   @Override
   public void onEditorFileOpened(EditorFileOpenedEvent event)
   {
      openedFiles = event.getOpenedFiles();
      openedEditors.put(event.getFile().getId(), event.getEditor());
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorFileContentChangedHandler#onEditorFileContentChanged(org.exoplatform.ide.client.framework.editor.event.EditorFileContentChangedEvent)
    */
   @Override
   public void onEditorFileContentChanged(EditorFileContentChangedEvent event)
   {
      FileModel file = event.getFile();
      openedFiles.put(file.getId(), file);
   }

}
