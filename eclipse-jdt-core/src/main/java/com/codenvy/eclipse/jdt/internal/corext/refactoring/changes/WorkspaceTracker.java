/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.eclipse.jdt.internal.corext.refactoring.changes;

import com.codenvy.eclipse.core.resources.IResourceChangeEvent;
import com.codenvy.eclipse.core.resources.IResourceChangeListener;
import com.codenvy.eclipse.core.resources.ResourcesPlugin;
import com.codenvy.eclipse.core.runtime.ListenerList;


public class WorkspaceTracker
{

   public final static WorkspaceTracker INSTANCE = new WorkspaceTracker();

   public interface Listener
   {
      public void workspaceChanged();
   }

   private ListenerList fListeners;

   private ResourceListener fResourceListener;

   private WorkspaceTracker()
   {
      fListeners = new ListenerList();
   }

   private class ResourceListener implements IResourceChangeListener
   {
      public void resourceChanged(IResourceChangeEvent event)
      {
         workspaceChanged();
      }
   }

   private void workspaceChanged()
   {
      Object[] listeners = fListeners.getListeners();
      for (int i = 0; i < listeners.length; i++)
      {
         ((Listener)listeners[i]).workspaceChanged();
      }
   }

   public void addListener(Listener l)
   {
      fListeners.add(l);
      if (fResourceListener == null)
      {
         fResourceListener = new ResourceListener();
         ResourcesPlugin.getWorkspace().addResourceChangeListener(fResourceListener);
      }
   }

   public void removeListener(Listener l)
   {
      if (fListeners.size() == 0)
      {
         return;
      }
      fListeners.remove(l);
      if (fListeners.size() == 0)
      {
         ResourcesPlugin.getWorkspace().removeResourceChangeListener(fResourceListener);
         fResourceListener = null;
      }
   }
}
