//TODO: need rework according new VFS
///*
// * Copyright (C) 2010 eXo Platform SAS.
// *
// * This is free software; you can redistribute it and/or modify it
// * under the terms of the GNU Lesser General Public License as
// * published by the Free Software Foundation; either version 2.1 of
// * the License, or (at your option) any later version.
// *
// * This software is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// * Lesser General Public License for more details.
// *
// * You should have received a copy of the GNU Lesser General Public
// * License along with this software; if not, write to the Free
// * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
// * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
// */
//package org.exoplatform.ide.client.versioning.control;
//
//import org.exoplatform.gwtframework.commons.webdav.Property;
//import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
//import org.exoplatform.ide.client.IDE;
//import org.exoplatform.ide.client.IDEImageBundle;
//import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
//import org.exoplatform.ide.client.framework.control.IDEControl;
//import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
//import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
//import org.exoplatform.ide.client.framework.event.FileSavedEvent;
//import org.exoplatform.ide.client.framework.event.FileSavedHandler;
//import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
//import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
//import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedEvent;
//import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedHandler;
//import org.exoplatform.ide.client.framework.vfs.File;
//import org.exoplatform.ide.client.framework.vfs.ItemProperty;
//import org.exoplatform.ide.client.versioning.VersionContentPresenter;
//import org.exoplatform.ide.client.versioning.event.OpenVersionEvent;
//import org.exoplatform.ide.vfs.client.model.FileModel;
//
//import com.google.gwt.event.shared.HandlerManager;
//
///**
// * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
// * @version $Id: Sep 27, 2010 $
// *
// */
//@RolesAllowed({"administrators", "developers"})
//public class ViewVersionHistoryControl extends SimpleControl implements IDEControl, EditorActiveFileChangedHandler,
//   ViewClosedHandler, ViewOpenedHandler, FileSavedHandler
//{
//
//   private static final String ID = "View/Version History...";
//
//   private final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.viewVersionHistoryTitleControl();
//
//   private final String PROMPT_SHOW = IDE.IDE_LOCALIZATION_CONSTANT.viewVersionHistoryPromptShowControl();
//
//   private final String PROMPT_HIDE = IDE.IDE_LOCALIZATION_CONSTANT.viewVersionHistoryPromptHideControl();
//
//   private boolean versionPanelOpened = false;
//
//   private FileModel activeFile;
//
//   /**
//    * 
//    */
//   public ViewVersionHistoryControl()
//   {
//      super(ID);
//      setTitle(TITLE);
//      setPrompt(PROMPT_SHOW);
//      setEvent(new OpenVersionEvent(true));
//      setImages(IDEImageBundle.INSTANCE.viewVersionContent(), IDEImageBundle.INSTANCE.viewVersionContentDisabled());
//      setDelimiterBefore(true);
//      setCanBeSelected(true);
//   }
//
//   /**
//    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize()
//    */
//   @Override
//   public void initialize()
//   {
//      IDE.addHandler(ViewClosedEvent.TYPE, this);
//      IDE.addHandler(ViewOpenedEvent.TYPE, this);
//      IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
//      IDE.addHandler(FileSavedEvent.TYPE, this);
//   }
//
//   /**
//    * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent)
//    */
//   @Override
//   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
//   {
//      activeFile = event.getFile();
//      if (activeFile == null || !activeFile.isPersisted())
//      {
//         setVisible(false);
//         setEnabled(false);
//         return;
//      }
//      setVisible(true);
//      setEnabled(checkIsVersioned(activeFile));
//   }
//
//   /**
//    * 
//    */
//   private void update()
//   {
//      setSelected(versionPanelOpened);
//
//      if (versionPanelOpened)
//      {
//         setPrompt(PROMPT_HIDE);
//         setEvent(new OpenVersionEvent(false));
//      }
//      else
//      {
//         setPrompt(PROMPT_SHOW);
//         setEvent(new OpenVersionEvent(true));
//      }
//   }
//
//   /**
//    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedHandler#onViewOpened(org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedEvent)
//    */
//   @Override
//   public void onViewOpened(ViewOpenedEvent event)
//   {
//      if (event.getView() instanceof VersionContentPresenter.Display)
//      {
//         setSelected(true);
//         versionPanelOpened = true;
//         update();
//      }
//   }
//
//   /**
//    * @see org.exoplatform.ide.client.framework.ui.api.event.event.ViewClosedHandler#onPanelClosed(org.exoplatform.ide.client.framework.ui.api.event.event.ViewClosedEvent)
//    */
//   @Override
//   public void onViewClosed(ViewClosedEvent event)
//   {
//      if (event.getView() instanceof VersionContentPresenter.Display)
//      {
//         setSelected(false);
//         versionPanelOpened = false;
//         update();
//      }
//   }
//
//   /**
//    * @see org.exoplatform.ide.client.framework.event.FileSavedHandler#onFileSaved(org.exoplatform.ide.client.framework.event.FileSavedEvent)
//    */
//   @Override
//   public void onFileSaved(FileSavedEvent event)
//   {
//      if (activeFile != null && event.getFile().equals(activeFile))
//      {
//         setEnabled(checkIsVersioned(event.getFile()));
//      }
//   }
//
//   /**
//    * Checks whether file is versioned or not. 
//    * 
//    * @param file file to check
//    * @return boolean true if versioned
//    */
//   private boolean checkIsVersioned(FileModel file)
//   {
//      String property = file.getVersionId();
//      //TODO
//      return (property != null && "1".equals(property));
//   }
//}
