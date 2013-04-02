/*******************************************************************************
 * Copyright (c) 2005, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.eclipse.jdt.internal.corext.refactoring.scripting;

import com.codenvy.eclipse.jdt.core.IMethod;
import com.codenvy.eclipse.jdt.core.JavaModelException;
import com.codenvy.eclipse.jdt.core.refactoring.IJavaRefactorings;
import com.codenvy.eclipse.jdt.core.refactoring.descriptors.JavaRefactoringDescriptor;
import com.codenvy.eclipse.jdt.internal.core.refactoring.descriptors.RefactoringSignatureDescriptorFactory;
import com.codenvy.eclipse.jdt.internal.corext.refactoring.JavaRefactoringArguments;
import com.codenvy.eclipse.jdt.internal.corext.refactoring.JavaRefactoringDescriptorUtil;
import com.codenvy.eclipse.jdt.internal.corext.refactoring.RefactoringCoreMessages;
import com.codenvy.eclipse.jdt.internal.corext.refactoring.rename.JavaRenameProcessor;
import com.codenvy.eclipse.jdt.internal.corext.refactoring.rename.MethodChecks;
import com.codenvy.eclipse.jdt.internal.corext.refactoring.rename.RenameNonVirtualMethodProcessor;
import com.codenvy.eclipse.jdt.internal.corext.refactoring.rename.RenameVirtualMethodProcessor;
import com.codenvy.eclipse.jdt.internal.corext.util.Messages;
import com.codenvy.eclipse.jdt.internal.ui.viewsupport.BasicElementLabels;
import com.codenvy.eclipse.ltk.core.refactoring.Refactoring;
import com.codenvy.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import com.codenvy.eclipse.ltk.core.refactoring.RefactoringStatus;
import com.codenvy.eclipse.ltk.core.refactoring.participants.RenameRefactoring;

import java.util.Map;

/**
 * Refactoring contribution for the rename method refactoring.
 *
 * @since 3.2
 */
public final class RenameMethodRefactoringContribution extends JavaUIRefactoringContribution {

    /** {@inheritDoc} */
    @Override
    public Refactoring createRefactoring(JavaRefactoringDescriptor descriptor, RefactoringStatus status) throws JavaModelException {
        JavaRefactoringArguments arguments = new JavaRefactoringArguments(descriptor.getProject(), retrieveArgumentMap(descriptor));

        String input = arguments.getAttribute(JavaRefactoringDescriptorUtil.ATTRIBUTE_INPUT);
        IMethod method = (IMethod)JavaRefactoringDescriptorUtil.handleToElement(arguments.getProject(), input);
        if (method == null) {
            status.addFatalError(Messages.format(RefactoringCoreMessages.RenameMethodRefactoringContribution_could_not_create,
                                                 new Object[]{BasicElementLabels.getResourceName(arguments.getProject()), input}));
            return null;
        }

        JavaRenameProcessor processor;
        if (MethodChecks.isVirtual(method)) {
            processor = new RenameVirtualMethodProcessor(method, arguments, status);
        } else {
            processor = new RenameNonVirtualMethodProcessor(method, arguments, status);
        }
        return new RenameRefactoring(processor);
    }

    @Override
    public RefactoringDescriptor createDescriptor() {
        return RefactoringSignatureDescriptorFactory.createRenameJavaElementDescriptor(IJavaRefactorings.RENAME_METHOD);
    }

    @Override
    public RefactoringDescriptor createDescriptor(String id, String project, String description, String comment, Map arguments, int flags) {
        return RefactoringSignatureDescriptorFactory.createRenameJavaElementDescriptor(id, project, description, comment, arguments, flags);
    }
}
