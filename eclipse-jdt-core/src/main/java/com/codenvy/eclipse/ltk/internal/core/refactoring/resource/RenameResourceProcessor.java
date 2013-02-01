/*******************************************************************************
 * Copyright (c) 2007, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.eclipse.ltk.internal.core.refactoring.resource;

import com.codenvy.eclipse.core.resources.IContainer;
import com.codenvy.eclipse.core.resources.IProject;
import com.codenvy.eclipse.core.resources.IResource;
import com.codenvy.eclipse.core.resources.mapping.IResourceChangeDescriptionFactory;
import com.codenvy.eclipse.core.runtime.Assert;
import com.codenvy.eclipse.core.runtime.CoreException;
import com.codenvy.eclipse.core.runtime.IProgressMonitor;
import com.codenvy.eclipse.ltk.core.refactoring.Change;
import com.codenvy.eclipse.ltk.core.refactoring.RefactoringChangeDescriptor;
import com.codenvy.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import com.codenvy.eclipse.ltk.core.refactoring.RefactoringStatus;
import com.codenvy.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import com.codenvy.eclipse.ltk.core.refactoring.participants.ParticipantManager;
import com.codenvy.eclipse.ltk.core.refactoring.participants.RefactoringParticipant;
import com.codenvy.eclipse.ltk.core.refactoring.participants.RenameArguments;
import com.codenvy.eclipse.ltk.core.refactoring.participants.RenameProcessor;
import com.codenvy.eclipse.ltk.core.refactoring.participants.ResourceChangeChecker;
import com.codenvy.eclipse.ltk.core.refactoring.participants.SharableParticipants;
import com.codenvy.eclipse.ltk.core.refactoring.resource.RenameResourceChange;
import com.codenvy.eclipse.ltk.core.refactoring.resource.RenameResourceDescriptor;
import com.codenvy.eclipse.ltk.internal.core.refactoring.BasicElementLabels;
import com.codenvy.eclipse.ltk.internal.core.refactoring.Messages;
import com.codenvy.eclipse.ltk.internal.core.refactoring.RefactoringCoreMessages;
import com.codenvy.eclipse.ltk.internal.core.refactoring.Resources;

/**
 * A rename processor for {@link IResource}. The processor will rename the resource and
 * load rename participants if references should be renamed as well.
 *
 * @since 3.4
 */
public class RenameResourceProcessor extends RenameProcessor
{

   private IResource fResource;

   private String fNewResourceName;

   private boolean fUpdateReferences;

   private RenameArguments fRenameArguments; // set after checkFinalConditions

   /**
    * Creates a new rename resource processor.
    *
    * @param resource the resource to rename.
    */
   public RenameResourceProcessor(IResource resource)
   {
      if (resource == null || !resource.exists())
      {
         throw new IllegalArgumentException("resource must not be null and must exist"); //$NON-NLS-1$
      }

      fResource = resource;
      fRenameArguments = null;
      fUpdateReferences = true;
      setNewResourceName(resource.getName()); // Initialize new name
   }

   /**
    * Returns the resource this processor was created on
    *
    * @return the resource to rename
    */
   public IResource getResource()
   {
      return fResource;
   }

   /**
    * Returns the new resource name
    *
    * @return the new resource name
    */
   public String getNewResourceName()
   {
      return fNewResourceName;
   }

   /**
    * Sets the new resource name
    *
    * @param newName the new resource name
    */
   public void setNewResourceName(String newName)
   {
      Assert.isNotNull(newName);
      fNewResourceName = newName;
   }

   /**
    * Returns <code>true</code> if the refactoring processor also updates references
    *
    * @return <code>true</code> if the refactoring processor also updates references
    */
   public boolean isUpdateReferences()
   {
      return fUpdateReferences;
   }

   /**
    * Specifies if the refactoring processor also updates references. The default behaviour is to update references.
    *
    * @param updateReferences <code>true</code> if the refactoring processor should also updates references
    */
   public void setUpdateReferences(boolean updateReferences)
   {
      fUpdateReferences = updateReferences;
   }

   /* (non-Javadoc)
    * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#checkInitialConditions(org.eclipse.core.runtime.IProgressMonitor)
    */
   public RefactoringStatus checkInitialConditions(IProgressMonitor pm) throws CoreException
   {
      return RefactoringStatus.create(Resources.checkInSync(fResource));
   }

   /* (non-Javadoc)
    * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#checkFinalConditions(org.eclipse.core.runtime.IProgressMonitor, org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext)
    */
   public RefactoringStatus checkFinalConditions(IProgressMonitor pm,
      CheckConditionsContext context) throws CoreException
   {
      pm.beginTask("", 1); //$NON-NLS-1$
      try
      {
         fRenameArguments = new RenameArguments(getNewResourceName(), isUpdateReferences());

         ResourceChangeChecker checker = (ResourceChangeChecker)context.getChecker(ResourceChangeChecker.class);
         IResourceChangeDescriptionFactory deltaFactory = checker.getDeltaFactory();

         ResourceModifications.buildMoveDelta(deltaFactory, fResource, fRenameArguments);

         return new RefactoringStatus();
      }
      finally
      {
         pm.done();
      }
   }

   /**
    * Validates if the a name is valid. This method does not change the name settings on the refactoring. It is intended to be used
    * in a wizard to validate user input.
    *
    * @param newName the name to validate
    * @return returns the resulting status of the validation
    */
   public RefactoringStatus validateNewElementName(String newName)
   {
      Assert.isNotNull(newName, "new name"); //$NON-NLS-1$
      IContainer c = fResource.getParent();
      if (c == null)
      {
         return RefactoringStatus.createFatalErrorStatus(
            RefactoringCoreMessages.RenameResourceProcessor_error_no_parent);
      }

      if (!c.getFullPath().isValidSegment(newName))
      {
         return RefactoringStatus.createFatalErrorStatus(
            RefactoringCoreMessages.RenameResourceProcessor_error_invalid_name);
      }

      if (c.findMember(newName) != null)
      {
         return RefactoringStatus.createFatalErrorStatus(
            RefactoringCoreMessages.RenameResourceProcessor_error_resource_already_exists);
      }

      RefactoringStatus result = RefactoringStatus.create(c.getWorkspace().validateName(newName, fResource.getType()));
      if (!result.hasFatalError())
      {
         result.merge(
            RefactoringStatus.create(c.getWorkspace().validatePath(createNewPath(newName), fResource.getType())));
      }
      return result;
   }

   protected RenameResourceDescriptor createDescriptor()
   {
      IResource resource = getResource();

      RenameResourceDescriptor descriptor = new RenameResourceDescriptor();
      descriptor.setProject(resource instanceof IProject ? null : resource.getProject().getName());
      descriptor.setDescription(Messages.format(RefactoringCoreMessages.RenameResourceProcessor_description,
         BasicElementLabels.getResourceName(resource)));
      descriptor.setComment(Messages.format(RefactoringCoreMessages.RenameResourceProcessor_comment,
         new String[]{BasicElementLabels.getPathLabel(resource.getFullPath(),
            false), BasicElementLabels.getResourceName(getNewResourceName())}));
      descriptor.setFlags(
         RefactoringDescriptor.STRUCTURAL_CHANGE | RefactoringDescriptor.MULTI_CHANGE | RefactoringDescriptor.BREAKING_CHANGE);
      descriptor.setResourcePath(resource.getFullPath());
      descriptor.setNewName(getNewResourceName());
      descriptor.setUpdateReferences(isUpdateReferences());
      return descriptor;
   }


   /* (non-Javadoc)
    * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#createChange(org.eclipse.core.runtime.IProgressMonitor)
    */
   public Change createChange(IProgressMonitor pm) throws CoreException
   {
      pm.beginTask("", 1); //$NON-NLS-1$
      try
      {
         RenameResourceChange change = new RenameResourceChange(fResource.getFullPath(), getNewResourceName());
         change.setDescriptor(new RefactoringChangeDescriptor(createDescriptor()));
         return change;
      }
      finally
      {
         pm.done();
      }
   }

   private String createNewPath(String newName)
   {
      return fResource.getFullPath().removeLastSegments(1).append(newName).toString();
   }

   /* (non-Javadoc)
    * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#getElements()
    */
   public Object[] getElements()
   {
      return new Object[]{fResource};
   }

   /* (non-Javadoc)
    * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#getIdentifier()
    */
   public String getIdentifier()
   {
      return "org.eclipse.ltk.core.refactoring.renameResourceProcessor"; //$NON-NLS-1$
   }

   /* (non-Javadoc)
    * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#getProcessorName()
    */
   public String getProcessorName()
   {
      return RefactoringCoreMessages.RenameResourceProcessor_processor_name;
   }

   /* (non-Javadoc)
    * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#isApplicable()
    */
   public boolean isApplicable()
   {
      if (fResource == null)
      {
         return false;
      }
      if (!fResource.exists())
      {
         return false;
      }
      if (!fResource.isAccessible())
      {
         return false;
      }
      return true;
   }

   /* (non-Javadoc)
    * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#loadParticipants(org.eclipse.ltk.core.refactoring.RefactoringStatus, org.eclipse.ltk.core.refactoring.participants.SharableParticipants)
    */
   public RefactoringParticipant[] loadParticipants(RefactoringStatus status,
      SharableParticipants shared) throws CoreException
   {
      String[] affectedNatures = ResourceProcessors.computeAffectedNatures(fResource);
      return ParticipantManager.loadRenameParticipants(status, this, fResource, fRenameArguments, null, affectedNatures,
         shared);
   }

}