/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.jdt.core;

import com.codenvy.ide.ext.java.jdt.core.compiler.IProblem;
import com.codenvy.ide.ext.java.jdt.internal.compiler.impl.CompilerOptions;
import com.codenvy.ide.ext.java.jdt.internal.compiler.problem.ProblemReporter;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class CorrectionEngine {

    /**
     * Return an array of strings which contains one entry per warning token
     * accepted by the <code>@SuppressWarnings</code> annotation. This array is
     * neither null nor empty, it contains at least the String <code>all</code>.
     * It should not be modified by the caller (please take a copy if modifications
     * are needed).<br>
     * <b>Note:</b> The tokens returned are not necessarily standardized across Java
     * compilers. If you were to use one of these tokens in a <code>@SuppressWarnings</code>
     * annotation in the Java source code, the effects (if any) may vary from
     * compiler to compiler.
     *
     * @return an array of strings which contains one entry per warning token
     *         accepted by the <code>@SuppressWarnings</code> annotation.
     */
    public static String[] getAllWarningTokens() {
        return CompilerOptions.warningTokens;
    }

    /**
     * Returns a token which can be used to suppress a given warning using
     * <code>@SuppressWarnings</code> annotation, for a given problem ID
     * ({@link IProblem }). If a particular problem is not suppressable,
     * <code>null</code> will be returned.
     * <p>
     * <b>Note:</b> <code>@SuppressWarnings</code> can only suppress warnings,
     * which means that if some problems got promoted to ERROR using custom compiler
     * settings ({@link IJavaProject#setOption(String, String)}), the
     * <code>@SuppressWarnings</code> annotation will be ineffective.
     * </p>
     * <p>
     * <b>Note:</b> <code>@SuppressWarnings</code> can be argumented with
     * <code>"all"</code> so as to suppress all possible warnings at once.
     * </p>
     * <p>
     * <b>Note:</b> The tokens returned are not necessarily standardized across Java
     * compilers. If you were to use one of these tokens in an @SuppressWarnings
     * annotation in the Java source code, the effects (if any) may vary from
     * compiler to compiler.
     * </p>
     *
     * @param problemID
     *         the ID of a given warning to suppress
     * @return a String which can be used in <code>@SuppressWarnings</code> annotation,
     *         or <code>null</code> if unable to suppress this warning.
     */
    public static String getWarningToken(int problemID) {
        int irritant = ProblemReporter.getIrritant(problemID);
        if (irritant != 0) {
            return CompilerOptions.warningTokenFromIrritant(irritant);
        }
        return null;
    }
}
