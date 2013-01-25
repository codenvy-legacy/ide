/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.eclipse.resources;

import com.codenvy.eclipse.core.internal.resources.ICoreConstants;
import com.codenvy.eclipse.core.internal.watson.IPathRequestor;
import com.codenvy.eclipse.core.resources.IResource;
import com.codenvy.eclipse.core.resources.IResourceProxy;
import com.codenvy.eclipse.core.resources.ResourcesPlugin;
import com.codenvy.eclipse.core.runtime.IPath;
import com.codenvy.eclipse.core.runtime.QualifiedName;

/**
 * Implements a resource proxy given a path requestor and the resource
 * info of the resource currently being visited.
 */
public class ResourceProxy implements IResourceProxy, ICoreConstants
{
   protected final WorkspaceResource workspace = (WorkspaceResource)ResourcesPlugin.getWorkspace();

   protected IPathRequestor requestor;

   protected ResourceInfo info;

   //cached info
   protected IPath fullPath;

   protected IResource resource;

   /**
    * @see com.codenvy.eclipse.core.resources.IResourceProxy#getModificationStamp()
    */
   public long getModificationStamp()
   {
      return info.getModificationStamp();
   }

   public String getName()
   {
      return requestor.requestName();
   }

   public Object getSessionProperty(QualifiedName key)
   {
      return info.getSessionProperty(key);
   }

   public int getType()
   {
      return info.getType();
   }

   /**
    * @see com.codenvy.eclipse.core.resources.IResourceProxy#isAccessible()
    */
   public boolean isAccessible()
   {
      int flags = info.getFlags();
      if (info.getType() == IResource.PROJECT)
      {
         return flags != NULL_FLAG && ResourceInfo.isSet(flags, M_OPEN);
      }
      return flags != NULL_FLAG;
   }

   /**
    * @see com.codenvy.eclipse.core.resources.IResourceProxy#isDerived()
    */
   public boolean isDerived()
   {
      int flags = info.getFlags();
      return flags != NULL_FLAG && ResourceInfo.isSet(flags, M_DERIVED);
   }

   /**
    * @see com.codenvy.eclipse.core.resources.IResourceProxy#isLinked()
    */
   public boolean isLinked()
   {
      int flags = info.getFlags();
      return flags != NULL_FLAG && ResourceInfo.isSet(flags, M_LINK);
   }

   /**
    * @see com.codenvy.eclipse.core.resources.IResourceProxy#isPhantom()
    */
   public boolean isPhantom()
   {
      int flags = info.getFlags();
      return flags != NULL_FLAG && ResourceInfo.isSet(flags, M_PHANTOM);
   }

   /**
    * @see com.codenvy.eclipse.core.resources.IResourceProxy#isTeamPrivateMember()
    */
   public boolean isTeamPrivateMember()
   {
      int flags = info.getFlags();
      return flags != NULL_FLAG && ResourceInfo.isSet(flags, M_TEAM_PRIVATE_MEMBER);
   }

   /**
    * @see com.codenvy.eclipse.core.resources.IResourceProxy#isHidden()
    */
   public boolean isHidden()
   {
      int flags = info.getFlags();
      return flags != NULL_FLAG && ResourceInfo.isSet(flags, M_HIDDEN);
   }

   /**
    * @see com.codenvy.eclipse.core.resources.IResourceProxy#requestFullPath()
    */
   public IPath requestFullPath()
   {
      if (fullPath == null)
      {
         fullPath = requestor.requestPath();
      }
      return fullPath;
   }

   /**
    * @see com.codenvy.eclipse.core.resources.IResourceProxy#requestResource()
    */
   public IResource requestResource()
   {
      if (resource == null)
      {
         resource = workspace.newResource(requestFullPath(), info.getType());
      }
      return resource;
   }

   protected void reset()
   {
      fullPath = null;
      resource = null;
   }
}
