/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.eclipse.jdt.internal.corext.refactoring.changes;

import com.codenvy.eclipse.core.resources.IProject;
import com.codenvy.eclipse.core.runtime.IPath;
import com.codenvy.eclipse.core.runtime.IProgressMonitor;
import com.codenvy.eclipse.jdt.core.IPackageFragmentRoot;
import com.codenvy.eclipse.jdt.core.JavaModelException;
import com.codenvy.eclipse.jdt.internal.corext.refactoring.RefactoringCoreMessages;
import com.codenvy.eclipse.jdt.internal.corext.refactoring.reorg.INewNameQuery;
import com.codenvy.eclipse.jdt.internal.corext.refactoring.reorg.IPackageFragmentRootManipulationQuery;
import com.codenvy.eclipse.jdt.internal.corext.util.Messages;
import com.codenvy.eclipse.jdt.internal.ui.viewsupport.BasicElementLabels;
import com.codenvy.eclipse.jdt.ui.JavaElementLabels;
import com.codenvy.eclipse.ltk.core.refactoring.Change;

public class CopyPackageFragmentRootChange extends PackageFragmentRootReorgChange
{

   public CopyPackageFragmentRootChange(IPackageFragmentRoot root, IProject destination, INewNameQuery newNameQuery,
      IPackageFragmentRootManipulationQuery updateClasspathQuery)
   {
      super(root, destination, newNameQuery, updateClasspathQuery);
   }

   @Override
   protected Change doPerformReorg(IPath destinationPath, IProgressMonitor pm) throws JavaModelException
   {
      getRoot().copy(destinationPath, getResourceUpdateFlags(), getUpdateModelFlags(true), null, pm);
      return null;
   }

   @Override
   public String getName()
   {
      String rootName = JavaElementLabels.getElementLabel(getRoot(), JavaElementLabels.ALL_DEFAULT);
      String destinationName = BasicElementLabels.getResourceName(getDestination());
      return Messages.format(RefactoringCoreMessages.CopyPackageFragmentRootChange_copy,
         new String[]{rootName, destinationName});
   }
}
