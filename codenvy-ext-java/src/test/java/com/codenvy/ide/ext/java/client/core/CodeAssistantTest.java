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
package com.codenvy.ide.ext.java.client.core;

import com.codenvy.ide.ext.java.emul.FileSystem;
import com.codenvy.ide.ext.java.jdt.compiler.batch.CompilationUnit;
import com.codenvy.ide.ext.java.jdt.core.CompletionProposal;
import com.codenvy.ide.ext.java.jdt.core.CompletionRequestor;
import com.codenvy.ide.ext.java.jdt.core.JavaCore;
import com.codenvy.ide.ext.java.jdt.core.compiler.IProblem;
import com.codenvy.ide.ext.java.jdt.internal.codeassist.CompletionEngine;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}: Jan 16, 2012 3:35:16 PM evgen $
 */
public class CodeAssistantTest extends ParserBaseTest {

    private CARequestor requestor;

    private CompletionEngine e;

    @Before
    public void init() {
        requestor = new CARequestor();
        e = new CompletionEngine(new FileSystem(new String[]{System.getProperty("java.home") + "/lib/rt.jar"}, null, "UTF-8"), requestor,
                                 JavaCore.getOptions());

    }

    @Test
    public void testCodeAssistantOnInnerInterface() {
        e.complete(new CompilationUnit(javaFiles, "CreateJavaClassPresenter", "UTF-8"),
                   getCompletionPosition(javaFiles, 452, 19), 0);
        assertEquals(2, requestor.proposals.size());
    }

    @Test
    public void testLocalVariables() {
        e.complete(new CompilationUnit(javaFiles, "CreateJavaClassPresenter", "UTF-8"),
                   getCompletionPosition(javaFiles, 481, 7), 0);
        assertTrue(requestor.proposals.size() > 30);
    }

    private static class CARequestor extends CompletionRequestor {

        private List<CompletionProposal> proposals = new ArrayList<CompletionProposal>();

        /** @see com.codenvy.ide.ext.java.jdt.core.CompletionRequestor#accept(com.codenvy.ide.ext.java.jdt.core.CompletionProposal) */
        @Override
        public void accept(CompletionProposal proposal) {
            proposals.add(proposal);
        }

        /** @see com.codenvy.ide.ext.java.jdt.core.CompletionRequestor#completionFailure(com.codenvy.ide.ext.java.jdt.core.compiler
         * .IProblem) */
        @Override
        public void completionFailure(IProblem problem) {
            System.out.println(problem.getMessage());
            super.completionFailure(problem);
        }
    }

    private int getCompletionPosition(char[] content, int row, int col) {
        String s = new String(content);
        String[] strings = s.split("\n");
        if (strings.length < row)
            fail("content length less than parameter 'row'");
        int pos = 0;

        for (int i = 0; i < row - 1; i++) {
            pos += strings[i].length() + 1;
        }
        return pos + col - 1;
    }

}
