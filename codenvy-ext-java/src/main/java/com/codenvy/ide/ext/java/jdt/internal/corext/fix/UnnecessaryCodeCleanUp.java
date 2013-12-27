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
package com.codenvy.ide.ext.java.jdt.internal.corext.fix;

import com.codenvy.ide.ext.java.jdt.codeassistant.api.IProblemLocation;
import com.codenvy.ide.ext.java.jdt.core.JavaCore;
import com.codenvy.ide.ext.java.jdt.core.compiler.IProblem;
import com.codenvy.ide.ext.java.jdt.core.dom.CompilationUnit;
import com.codenvy.ide.runtime.CoreException;
import com.codenvy.ide.text.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UnnecessaryCodeCleanUp extends AbstractMultiFix {

    public UnnecessaryCodeCleanUp(Map<String, String> options) {
        super(options);
    }

    public UnnecessaryCodeCleanUp() {
        super();
    }

    /** {@inheritDoc} */
    @Override
    public CleanUpRequirements getRequirements() {
        boolean requireAST = isEnabled(CleanUpConstants.REMOVE_UNNECESSARY_CASTS);
        Map<String, String> requiredOptions = requireAST ? getRequiredOptions() : null;
        return new CleanUpRequirements(requireAST, false, false, requiredOptions);
    }

    /** {@inheritDoc} */
    @Override
    protected ICleanUpFix createFix(CompilationUnit compilationUnit, Document document) throws CoreException {
        return UnusedCodeFix.createCleanUp(compilationUnit, false, false, false, false, false, false,
                                           isEnabled(CleanUpConstants.REMOVE_UNNECESSARY_CASTS), document);
    }

    /** {@inheritDoc} */
    @Override
    protected ICleanUpFix createFix(CompilationUnit compilationUnit, Document document, IProblemLocation[] problems)
            throws CoreException {
        return UnusedCodeFix.createCleanUp(compilationUnit, problems, false, false, false, false, false, false,
                                           isEnabled(CleanUpConstants.REMOVE_UNNECESSARY_CASTS), document);
    }

    private Map<String, String> getRequiredOptions() {
        Map<String, String> result = new HashMap<String, String>();

        if (isEnabled(CleanUpConstants.REMOVE_UNNECESSARY_CASTS))
            result.put(JavaCore.COMPILER_PB_UNNECESSARY_TYPE_CHECK, JavaCore.WARNING);

        return result;
    }

    /** {@inheritDoc} */
    @Override
    public String[] getStepDescriptions() {
        List<String> result = new ArrayList<String>();
        if (isEnabled(CleanUpConstants.REMOVE_UNNECESSARY_CASTS))
            result.add(MultiFixMessages.INSTANCE.UnusedCodeCleanUp_RemoveUnusedCasts_description());
        return result.toArray(new String[result.size()]);
    }

    /** {@inheritDoc} */
    @Override
    public String getPreview() {
        StringBuffer buf = new StringBuffer();

        if (isEnabled(CleanUpConstants.REMOVE_UNNECESSARY_CASTS)) {
            buf.append("Boolean b= Boolean.TRUE;\n"); //$NON-NLS-1$
        } else {
            buf.append("Boolean b= (Boolean) Boolean.TRUE;\n"); //$NON-NLS-1$
        }

        return buf.toString();
    }

    /** {@inheritDoc} */
    public boolean canFix(IProblemLocation problem) {
        if (problem.getProblemId() == IProblem.UnnecessaryCast)
            return isEnabled(CleanUpConstants.REMOVE_UNNECESSARY_CASTS);

        return false;
    }

    /** {@inheritDoc} */
    @Override
    public int computeNumberOfFixes(CompilationUnit compilationUnit) {
        int result = 0;
        IProblem[] problems = compilationUnit.getProblems();
        if (isEnabled(CleanUpConstants.REMOVE_UNNECESSARY_CASTS))
            result += getNumberOfProblems(problems, IProblem.UnnecessaryCast);
        return result;
    }
}
