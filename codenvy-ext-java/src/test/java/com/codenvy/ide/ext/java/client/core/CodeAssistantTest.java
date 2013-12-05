/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.ext.java.client.core;

import com.codenvy.ide.ext.java.client.core.ParserBaseTest;
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
