/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide;

import org.exoplatform.ide.util.UserActivityManager;

import com.google.gwt.core.client.GWT;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class AppContext
{
   private Resources resources = GWT.create(Resources.class);

   private UserActivityManager userActivityManager;

   /**
    * 
    */
   public AppContext()
   {
      resources.appCss().ensureInjected();
      resources.baseCss().ensureInjected();
      resources.editableContentAreaCss().ensureInjected();
      resources.editorSelectionLineRendererCss().ensureInjected();
      resources.lineNumberRendererCss().ensureInjected();
      resources.workspaceEditorBufferCss().ensureInjected();
      resources.workspaceEditorCss().ensureInjected();
      resources.workspaceEditorCursorCss().ensureInjected();
      resources.treeCss().ensureInjected();
      resources.workspaceNavigationFileTreeNodeRendererCss().ensureInjected();

      this.userActivityManager = new UserActivityManager();
   }

   /**
    * @return the resources
    */
   public Resources getResources()
   {
      return resources;
   }

   /**
    * @return
    */
   public UserActivityManager getUserActivityManager()
   {
      return userActivityManager;
   }
}
