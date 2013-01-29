/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.eclipse.ltk.core.refactoring.model;

import com.codenvy.eclipse.core.resources.IProject;
import com.codenvy.eclipse.core.resources.IResource;
import com.codenvy.eclipse.core.resources.IWorkspaceRoot;
import com.codenvy.eclipse.core.resources.ResourcesPlugin;
import com.codenvy.eclipse.core.resources.mapping.ModelProvider;
import com.codenvy.eclipse.core.resources.mapping.ResourceMapping;
import com.codenvy.eclipse.core.resources.mapping.ResourceMappingContext;
import com.codenvy.eclipse.core.resources.mapping.ResourceTraversal;
import com.codenvy.eclipse.core.runtime.Assert;
import com.codenvy.eclipse.core.runtime.CoreException;
import com.codenvy.eclipse.core.runtime.IProgressMonitor;
import com.codenvy.eclipse.ltk.core.refactoring.RefactoringDescriptorProxy;
import com.codenvy.eclipse.ltk.core.refactoring.history.RefactoringHistory;
import com.codenvy.eclipse.ltk.internal.core.refactoring.RefactoringCorePlugin;
import com.codenvy.eclipse.ltk.internal.core.refactoring.history.RefactoringHistoryService;

import java.util.HashSet;
import java.util.Set;

/**
 * Partial implementation of a resource mapping for a refactoring history
 * object.
 * <p>
 * Note: this class is intended to be implemented by clients which need to
 * enhance a model provider with a refactoring model.
 * </p>
 *
 * @see ResourceMapping
 * @see ModelProvider
 * @since 3.2
 */
public abstract class AbstractRefactoringHistoryResourceMapping extends ResourceMapping
{

   /**
    * The refactoring history
    */
   private final RefactoringHistory fRefactoringHistory;

   /**
    * The resource traversals
    */
   private ResourceTraversal[] fResourceTraversals = null;

   /**
    * Creates a new abstract refactoring history resource mapping.
    *
    * @param history the refactoring history
    */
   protected AbstractRefactoringHistoryResourceMapping(final RefactoringHistory history)
   {
      Assert.isNotNull(history);
      fRefactoringHistory = history;
   }

   /**
    * {@inheritDoc}
    */
   public boolean equals(final Object object)
   {
      if (object instanceof AbstractRefactoringHistoryResourceMapping)
      {
         final AbstractRefactoringHistoryResourceMapping mapping = (AbstractRefactoringHistoryResourceMapping)object;
         return mapping.fRefactoringHistory.equals(fRefactoringHistory);
      }
      return false;
   }

   /**
    * {@inheritDoc}
    */
   public final Object getModelObject()
   {
      return fRefactoringHistory;
   }

   /**
    * {@inheritDoc}
    */
   public final IProject[] getProjects()
   {
      final Set set = new HashSet();
      final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
      final RefactoringDescriptorProxy[] proxies = fRefactoringHistory.getDescriptors();
      for (int index = 0; index < proxies.length; index++)
      {
         final String name = proxies[index].getProject();
         if (name != null && !"".equals(name)) //$NON-NLS-1$
         {
            set.add(root.getProject(name));
         }
      }
      return (IProject[])set.toArray(new IProject[set.size()]);
   }

   /**
    * Returns the associated resource.
    * <p>
    * This method only returns a meaningful result if the history contains
    * refactorings of a single project.
    * </p>
    *
    * @return the associated resource, or <code>null</code> if the
    *         refactoring history contains workspace refactoring descriptors
    *         only, or if it contains refactoring descriptors from multiple
    *         projects.
    */
   public final IResource getResource()
   {
      try
      {
         final ResourceTraversal[] traversals = getTraversals(null, null);
         if (traversals.length > 0)
         {
            final IResource[] resources = traversals[0].getResources();
            if (resources.length > 0)
            {
               return resources[0];
            }
         }
      }
      catch (CoreException exception)
      {
         RefactoringCorePlugin.log(exception);
      }
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public final ResourceTraversal[] getTraversals(final ResourceMappingContext context,
      final IProgressMonitor monitor) throws CoreException
   {
      if (fResourceTraversals == null)
      {
         final IProject[] projects = getProjects();
         final ResourceTraversal[] traversals = new ResourceTraversal[projects.length];
         for (int index = 0; index < projects.length; index++)
         {
            traversals[index] = new ResourceTraversal(
               new IResource[]{projects[index].getFolder(RefactoringHistoryService.NAME_HISTORY_FOLDER)},
               IResource.DEPTH_INFINITE, IResource.NONE);
         }
         fResourceTraversals = traversals;
      }
      final ResourceTraversal[] traversals = new ResourceTraversal[fResourceTraversals.length];
      System.arraycopy(fResourceTraversals, 0, traversals, 0, fResourceTraversals.length);
      return traversals;
   }

   /**
    * {@inheritDoc}
    */
   public int hashCode()
   {
      return fRefactoringHistory.hashCode();
   }
}
