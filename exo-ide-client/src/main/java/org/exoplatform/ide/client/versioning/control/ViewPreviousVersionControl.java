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
//import org.exoplatform.ide.client.IDE;
//import org.exoplatform.ide.client.IDEImageBundle;
//import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
//import org.exoplatform.ide.client.framework.vfs.ItemProperty;
//import org.exoplatform.ide.client.framework.vfs.Version;
//import org.exoplatform.ide.client.versioning.event.ShowPreviousVersionEvent;
//import org.exoplatform.ide.client.versioning.event.ShowVersionContentEvent;
//import org.exoplatform.ide.client.versioning.event.ShowVersionContentHandler;
//
///**
// * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
// * @version $Id: Sep 29, 2010 $
// *
// */
//@RolesAllowed({"administrators", "developers"})
//public class ViewPreviousVersionControl extends VersionControl implements ShowVersionContentHandler
//{
//   private static final String ID = "View/Older Version";
//
//   private static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.viewPreviousVersionTitleControl();
//
//   private static final String PROMPT = IDE.IDE_LOCALIZATION_CONSTANT.viewPreviousVersionPromptControl();
//
//   private Version version;
//
//   /**
//    * 
//    */
//   public ViewPreviousVersionControl()
//   {
//      super(ID);
//      setTitle(TITLE);
//      setPrompt(PROMPT);
//      setEvent(new ShowPreviousVersionEvent());
//      setImages(IDEImageBundle.INSTANCE.viewOlderVersion(), IDEImageBundle.INSTANCE.viewOlderVersionDisabled());
//   }
//
//   /**
//    * @see org.exoplatform.ide.client.versioning.control.VersionControl#initialize()
//    */
//   @Override
//   public void initialize()
//   {
//      IDE.addHandler(ShowVersionContentEvent.TYPE, this);
//      super.initialize();
//   }
//
//   /**
//    * @see org.exoplatform.ide.client.versioning.event.ShowVersionContentHandler#onShowVersionContent(org.exoplatform.ide.client.versioning.event.ShowVersionContentEvent)
//    */
//   @Override
//   public void onShowVersionContent(ShowVersionContentEvent event)
//   {
//      version = event.getVersion();
//      boolean isEnabled =
//         (version.getProperty(ItemProperty.PREDECESSOR_SET) != null && version
//            .getProperty(ItemProperty.PREDECESSOR_SET).getChildProperties().size() > 0);
//      setEnabled(isEnabled);
//   }
//
//}
