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
package com.codenvy.ide.ext.java.client.core.quickfix;

import com.codenvy.ide.ext.java.client.editor.JavaCorrectionProcessor;
import com.codenvy.ide.ext.java.jdt.codeassistant.api.CompletionProposal;
import com.codenvy.ide.ext.java.jdt.codeassistant.api.IProblemLocation;
import com.codenvy.ide.ext.java.jdt.core.compiler.IProblem;
import com.codenvy.ide.ext.java.jdt.core.dom.AST;
import com.codenvy.ide.ext.java.jdt.core.dom.ASTParser;
import com.codenvy.ide.ext.java.jdt.core.dom.CompilationUnit;
import com.codenvy.ide.ext.java.jdt.internal.text.correction.AssistContext;
import com.codenvy.ide.ext.java.jdt.internal.text.correction.ICommandAccess;
import com.codenvy.ide.ext.java.jdt.internal.text.correction.ProblemLocation;
import com.codenvy.ide.ext.java.jdt.internal.text.correction.proposals.CUCorrectionProposal;
import com.codenvy.ide.ext.java.jdt.quickassist.api.InvocationContext;
import com.codenvy.ide.ext.java.worker.WorkerCorrectionProcessor;
import com.codenvy.ide.ext.java.worker.WorkerMessageHandler;
import com.codenvy.ide.runtime.CoreException;
import com.codenvy.ide.runtime.IStatus;
import com.codenvy.ide.text.BadLocationException;
import com.codenvy.ide.text.Document;
import com.googlecode.gwt.test.GwtModule;
import com.googlecode.gwt.test.GwtTestWithMockito;

import org.junit.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
@GwtModule("com.codenvy.ide.ext.java.Java")
public abstract class QuickFixTest extends GwtTestWithMockito {


    public static AssistContext getCorrectionContext(Document document, int offset, int length, String name) {
        AssistContext context = new AssistContext(document, offset, length);
        context.setASTRoot(getASTRoot(document, name));
        return context;
    }

    protected static final ArrayList collectAssists(InvocationContext context, boolean includeLinkedRename)
            throws CoreException {
        Class[] filteredTypes = includeLinkedRename ? null : new Class[0];
        return collectAssists(context, filteredTypes);
    }

    public static void assertStatusOk(IStatus status) throws CoreException {
        if (!status.isOK()) {
            if (status.getException() == null) { // find a status with an exception
                IStatus[] children = status.getChildren();
                for (int i = 0; i < children.length; i++) {
                    IStatus child = children[i];
                    if (child.getException() != null) {
                        throw new CoreException(child);
                    }
                }
            }
        }
    }

    protected static void assertNumberOfProposals(List proposals, int expectedProposals) {
        if (proposals.size() != expectedProposals) {
            StringBuffer buf = new StringBuffer();
            buf.append("Wrong number of proposals, is: ").append(proposals.size()).append(", expected: ")
               .append(expectedProposals).append('\n');
            for (int i = 0; i < proposals.size(); i++) {
                CompletionProposal curr = (CompletionProposal)proposals.get(i);
                buf.append(" - ").append(curr.getDisplayString()).append('\n');
                if (curr instanceof CUCorrectionProposal) {
                    appendSource(((CUCorrectionProposal)curr), buf);
                }
            }
            Assert.assertTrue(buf.toString(), false);
        }
    }

    private static void appendSource(CUCorrectionProposal proposal, StringBuffer buf) {
        try {
            buf.append(proposal.getPreviewContent());
        } catch (CoreException e) {
            // ignore
        }
    }

    public static void assertCorrectLabels(List proposals) {
        for (int i = 0; i < proposals.size(); i++) {
            CompletionProposal proposal = (CompletionProposal)proposals.get(i);
            String name = proposal.getDisplayString();
            if (name == null || name.length() == 0 || name.charAt(0) == '!' || name.indexOf("{0}") != -1
                || name.indexOf("{1}") != -1) {
                Assert.assertTrue("wrong proposal label: " + name, false);
            }
            if (proposal.getImage() == null) {
                Assert.assertTrue("wrong proposal image", false);
            }
        }
    }

    protected static String getPreviewContent(CUCorrectionProposal proposal) throws CoreException {
        return proposal.getPreviewContent();
    }

    public static void assertEqualStringsIgnoreOrder(String[] actuals, String[] expecteds) {
        StringAsserts.assertEqualStringsIgnoreOrder(actuals, expecteds);
    }

    public static void addPreviewAndExpected(List proposals, StringBuffer expected, ArrayList expecteds,
                                             ArrayList previews) throws CoreException {
        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(expecteds.size());
        previews.add(getPreviewContent(proposal));
        expecteds.add(expected.toString());
    }

    public static void assertEqualStringsIgnoreOrder(Collection actuals, Collection expecteds) {
        String[] act = (String[])actuals.toArray(new String[actuals.size()]);
        String[] exp = (String[])expecteds.toArray(new String[actuals.size()]);
        StringAsserts.assertEqualStringsIgnoreOrder(act, exp);
    }

    public static void assertExpectedExistInProposals(List actualProposals, String[] expecteds) throws CoreException,
                                                                                                       BadLocationException {
        StringAsserts.assertExpectedExistInProposals(getPreviewContents(actualProposals), expecteds);
    }

    protected static String[] getPreviewContents(List proposals) throws CoreException, BadLocationException {
        String[] res = new String[proposals.size()];
        for (int i = 0; i < proposals.size(); i++) {
            Object curr = proposals.get(i);
            //         if (curr instanceof ReorgCorrectionsSubProcessor.ClasspathFixCorrectionProposal) {
            //            // ignore
            //         } else
            if (curr instanceof CUCorrectionProposal) {
                res[i] = getPreviewContent((CUCorrectionProposal)curr);
            }
            //            else if (curr instanceof NewCUUsingWizardProposal) {
            //            res[i]= getWizardPreviewContent((NewCUUsingWizardProposal) curr);
            //         } else if (curr instanceof SurroundWithTemplateProposal) {
            //            res[i]= getTemplatePreviewContent((SurroundWithTemplateProposal) curr);
            //         } else if (curr instanceof SelfEncapsulateFieldProposal) {
            //            res[i]= getSEFPreviewContent((SelfEncapsulateFieldProposal) curr);
            //         }
        }
        return res;
    }

    public static void assertEqualString(String actual, String expected) {
        StringAsserts.assertEqualString(actual, expected);
    }

    protected static final ArrayList collectAssists(InvocationContext context, Class[] filteredTypes)
            throws CoreException {
        ArrayList proposals = new ArrayList();
        IStatus status = WorkerCorrectionProcessor.collectAssists(context, new IProblemLocation[0], proposals);
        assertStatusOk(status);

        if (!proposals.isEmpty()) {
            Assert.assertTrue("should be marked as 'has assist'", WorkerCorrectionProcessor.hasAssists(context));
        }

        if (filteredTypes != null && filteredTypes.length > 0) {
            for (Iterator iter = proposals.iterator(); iter.hasNext(); ) {
                if (isFiltered(iter.next(), filteredTypes)) {
                    iter.remove();
                }
            }
        }
        return proposals;
    }

    private static boolean isFiltered(Object curr, Class[] filteredTypes) {
        for (int k = 0; k < filteredTypes.length; k++) {
            if (filteredTypes[k].isInstance(curr)) {
                return true;
            }
        }
        return false;
    }

    public static void assertProposalDoesNotExist(List actualProposals, String proposalName) {
        Assert.assertTrue(findProposalByName(proposalName, actualProposals) == null);
    }

    protected static CompletionProposal findProposalByName(String name, List proposals) {
        for (int i = 0; i < proposals.size(); i++) {
            Object curr = proposals.get(i);
            if (curr instanceof CompletionProposal && name.equals(((CompletionProposal)curr).getDisplayString())) {
                return (CompletionProposal)curr;
            }
        }
        return null;
    }

    public static void assertCommandIdDoesNotExist(List actualProposals, String commandId) {
        Assert.assertTrue(findProposalByCommandId(commandId, actualProposals) == null);
    }

    protected static ICommandAccess findProposalByCommandId(String commandId, List proposals) {
        for (int i = 0; i < proposals.size(); i++) {
            Object curr = proposals.get(i);
            if (curr instanceof ICommandAccess) {
                if (commandId.equals(((ICommandAccess)curr).getCommandId())) {
                    return (ICommandAccess)curr;
                }
            }
        }
        return null;
    }

    protected static CompilationUnit getASTRoot(Document cu, String name) {
        ASTParser astParser = ASTParser.newParser(AST.JLS4);
        astParser.setSource(cu.get().toCharArray());
        astParser.setResolveBindings(true);
        astParser.setStatementsRecovery(true);
        astParser.setBindingsRecovery(true);
        astParser.setNameEnvironment(WorkerMessageHandler.get().getNameEnvironment());
        astParser.setUnitName(name);
        return (CompilationUnit)astParser.createAST();
    }

    protected static final ArrayList collectCorrections(Document cu, CompilationUnit astRoot) throws CoreException {
        return collectCorrections(cu, astRoot, 1, null);
    }

    protected static final ArrayList collectCorrections(Document cu, CompilationUnit astRoot, int nProblems)
            throws CoreException {
        return collectCorrections(cu, astRoot, nProblems, null);
    }

    protected static final ArrayList collectCorrections(Document cu, CompilationUnit astRoot, int nProblems, int problem)
            throws CoreException {
        return collectCorrections(cu, astRoot, nProblems, problem, null);
    }

    protected static final ArrayList collectCorrections(Document cu, CompilationUnit astRoot, int nProblems,
                                                        AssistContext context) throws CoreException {
        return collectCorrections(cu, astRoot, nProblems, 0, context);
    }

    protected static final ArrayList collectCorrections(Document cu, CompilationUnit astRoot, int nProblems,
                                                        int problem, AssistContext context) throws CoreException {
        IProblem[] problems = astRoot.getProblems();
        assertNumberOfProblems(nProblems, problems);

        return collectCorrections(cu, problems[problem], context);
    }

    protected static ArrayList collectCorrections(InvocationContext context, IProblemLocation problem)
            throws CoreException {
        ArrayList proposals = new ArrayList();
        IStatus status = WorkerCorrectionProcessor.collectCorrections(context, new IProblemLocation[]{problem}, proposals);
        assertStatusOk(status);
        return proposals;
    }

    protected static void assertNumberOfProblems(int nProblems, IProblem[] problems) {
        if (problems.length != nProblems) {
            StringBuffer buf = new StringBuffer("Wrong number of problems, is: ");
            buf.append(problems.length).append(", expected: ").append(nProblems).append('\n');
            for (int i = 0; i < problems.length; i++) {
                buf.append(problems[i]);
                buf.append('[').append(problems[i].getSourceStart()).append(" ,").append(problems[i].getSourceEnd())
                   .append(']');
                buf.append('\n');
            }
            Assert.assertTrue(buf.toString(), false);
        }
    }

    protected static final ArrayList collectCorrections(Document cu, IProblem curr, InvocationContext context)
            throws CoreException {
        int offset = curr.getSourceStart();
        int length = curr.getSourceEnd() + 1 - offset;
        if (context == null) {
            context = new AssistContext(cu, offset, length);
        }

        ProblemLocation problem = new ProblemLocation(curr);
        ArrayList proposals = collectCorrections(context, problem);
        if (!proposals.isEmpty()) {
            assertCorrectContext(context, problem);
        }

        return proposals;
    }

    public static void assertCorrectContext(InvocationContext context, ProblemLocation problem) {
        if (problem.getProblemId() != 0) {
            if (!JavaCorrectionProcessor.hasCorrections(problem)) {
                Assert.assertTrue("Problem type not marked with light bulb: " + problem, false);
            }
        }
    }

    protected static void assertNoErrors(InvocationContext context) {
        IProblem[] problems = context.getASTRoot().getProblems();
        for (int i = 0; i < problems.length; i++) {
            if (problems[i].isError()) {
                Assert.assertTrue("source has error: " + problems[i].getMessage(), false);
            }
        }
    }

}
