/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Andrew McCullough - initial API and implementation
 *      IBM Corporation  - general improvement and bug fixes, partial reimplementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.jdt.codeassistant;

import com.codenvy.ide.ext.java.jdt.core.CompletionContext;
import com.codenvy.ide.ext.java.jdt.core.CompletionProposal;
import com.codenvy.ide.ext.java.jdt.core.Signature;
import com.codenvy.ide.ext.java.jdt.internal.corext.util.SignatureUtil;
import com.codenvy.ide.text.BadPositionCategoryException;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.text.Position;
import com.codenvy.ide.text.PositionUpdater;
import com.codenvy.ide.text.Region;
import com.codenvy.ide.text.RegionImpl;


/** This is a which includes templates that represent the best guess completion for each parameter of a method. */
public final class ParameterGuessingProposal extends JavaMethodCompletionProposal {

    /**
     * Creates a {@link ParameterGuessingProposal} or <code>null</code> if the core context isn't available or extended.
     *
     * @param proposal
     *         the original completion proposal
     * @param context
     *         the currrent context
     * @param fillBestGuess
     *         if set, the best guess will be filled in
     * @return a proposal or <code>null</code>
     */
    public static ParameterGuessingProposal createProposal(CompletionProposal proposal,
                                                           JavaContentAssistInvocationContext context, boolean fillBestGuess) {
        CompletionContext coreContext = context.getCoreContext();
        if (coreContext != null && coreContext.isExtended()) {
            return new ParameterGuessingProposal(proposal, context, coreContext, fillBestGuess);
        }
        return null;
    }

    /** Tells whether this class is in debug mode. */
    private static final boolean DEBUG = false;
            //"true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.jdt.ui/debug/ResultCollector"));  //$NON-NLS-1$//$NON-NLS-2$

    private CompletionProposal[][] fChoices; // initialized by guessParameters()

    private Position[] fPositions; // initialized by guessParameters()

    private Region fSelectedRegion; // initialized by apply()

    private PositionUpdater fUpdater;

    private final boolean fFillBestGuess;

    private final CompletionContext fCoreContext;

    private ParameterGuessingProposal(CompletionProposal proposal, JavaContentAssistInvocationContext context,
                                      CompletionContext coreContext, boolean fillBestGuess) {
        super(proposal, context);
        fCoreContext = coreContext;
        fFillBestGuess = fillBestGuess;
    }

    // private ASTNode getEnclosingElement() {
    // return fCoreContext.getEnclosingElement();
    // fInvocationContext.getCompilationUnit()
    // f.findField(fieldHandle)
    // }

    // private IJavaElement[][] getAssignableElements() {
    // char[] signature= SignatureUtil.fix83600(getProposal().getSignature());
    // char[][] types= Signature.getParameterTypes(signature);
    //
    // IJavaElement[][] assignableElements= new IJavaElement[types.length][];
    // for (int i= 0; i < types.length; i++) {
    // assignableElements[i]= fCoreContext.getVisibleElements(new String(types[i]));
    // }
    // return assignableElements;
    // }

    /*
     * @see ICompletionProposalExtension#apply(IDocument, char)
     */
    @Override
    public void apply(Document document, char trigger, int offset) {
        // try {
        super.apply(document, trigger, offset);

        // int baseOffset= getReplacementOffset();
        // String replacement= getReplacementString();
        //
        // if (fPositions != null && getTextViewer() != null) {
        //
        // LinkedModeModel model= new LinkedModeModel();
        //
        // for (int i= 0; i < fPositions.length; i++) {
        // LinkedPositionGroup group= new LinkedPositionGroup();
        // int positionOffset= fPositions[i].getOffset();
        // int positionLength= fPositions[i].getLength();
        //
        // if (fChoices[i].length < 2) {
        // group.addPosition(new LinkedPosition(document, positionOffset, positionLength, LinkedPositionGroup.NO_STOP));
        // } else {
        // ensurePositionCategoryInstalled(document, model);
        // document.addPosition(getCategory(), fPositions[i]);
        // group.addPosition(new ProposalPosition(document, positionOffset, positionLength, LinkedPositionGroup.NO_STOP,
        // fChoices[i]));
        // }
        // model.addGroup(group);
        // }
        //
        // model.forceInstall();
        // JavaEditor editor= getJavaEditor();
        // if (editor != null) {
        // model.addLinkingListener(new EditorHighlightingSynchronizer(editor));
        // }
        //
        // LinkedModeUI ui= new EditorLinkedModeUI(model, getTextViewer());
        // ui.setExitPosition(getTextViewer(), baseOffset + replacement.length(), 0, Integer.MAX_VALUE);
        // ui.setExitPolicy(new ExitPolicy(')', document));
        // ui.setCyclingMode(LinkedModeUI.CYCLE_WHEN_NO_PARENT);
        // ui.setDoContextInfo(true);
        // ui.enter();
        // fSelectedRegion= ui.getSelectedRegion();
        //
        // } else {
        // fSelectedRegion= new Region(baseOffset + replacement.length(), 0);
        // }
        //
        // } catch (BadLocationException e) {
        // ensurePositionCategoryRemoved(document);
        // JavaPlugin.log(e);
        // openErrorDialog(e);
        // } catch (BadPositionCategoryException e) {
        // ensurePositionCategoryRemoved(document);
        // JavaPlugin.log(e);
        // openErrorDialog(e);
        // }
    }

    /*
     * @see org.eclipse.jdt.internal.ui.text.java.JavaMethodCompletionProposal#needsLinkedMode()
     */
    @Override
    protected boolean needsLinkedMode() {
        return false; // we handle it ourselves
    }

    /*
     * @see org.eclipse.jdt.internal.ui.text.java.JavaMethodCompletionProposal#computeReplacementString()
     */
    @Override
    protected String computeReplacementString() {

        if (!hasParameters() || !hasArgumentList())
            return super.computeReplacementString();

        long millis = DEBUG ? System.currentTimeMillis() : 0;
        String replacement;
        // try {
        replacement = computeGuessingCompletion();
        // } catch (JavaModelException x) {
        // fPositions= null;
        // fChoices= null;
        // JavaPlugin.log(x);
        // openErrorDialog(x);
        // return super.computeReplacementString();
        // }
        if (DEBUG)
            System.err.println("Parameter Guessing: " + (System.currentTimeMillis() - millis)); //$NON-NLS-1$

        return replacement;
    }

    /**
     * Creates the completion string. Offsets and Lengths are set to the offsets and lengths of the parameters.
     *
     * @return the completion string
     */
    private String computeGuessingCompletion() {

        StringBuffer buffer = new StringBuffer();
        appendMethodNameReplacement(buffer);

        FormatterPrefs prefs = getFormatterPrefs();

        setCursorPosition(buffer.length());

        if (prefs.afterOpeningParen)
            buffer.append(SPACE);

        char[][] parameterNames = fProposal.findParameterNames();

        fChoices = guessParameters(parameterNames);
        int count = fChoices.length;
        int replacementOffset = getReplacementOffset();

        for (int i = 0; i < count; i++) {
            if (i != 0) {
                if (prefs.beforeComma)
                    buffer.append(SPACE);
                buffer.append(COMMA);
                if (prefs.afterComma)
                    buffer.append(SPACE);
            }

            // ICompletionProposal proposal= fChoices[i][0];
            // String argument= proposal.getDisplayString();
            //
            // Position position= fPositions[i];
            // position.setOffset(replacementOffset + buffer.length());
            // position.setLength(argument.length());
            //
            // if (proposal instanceof JavaCompletionProposal) // handle the "unknown" case where we only insert a proposal.
            // ((JavaCompletionProposal) proposal).setReplacementOffset(replacementOffset + buffer.length());
            buffer.append(parameterNames[i]);
        }

        if (prefs.beforeClosingParen)
            buffer.append(SPACE);

        buffer.append(RPAREN);
        return buffer.toString();
    }

    // /**
    // * Returns the currently active java editor, or <code>null</code> if it
    // * cannot be determined.
    // *
    // * @return the currently active java editor, or <code>null</code>
    // */
    // private JavaEditor getJavaEditor() {
    // IEditorPart part= JavaPlugin.getActivePage().getActiveEditor();
    // if (part instanceof JavaEditor)
    // return (JavaEditor) part;
    // else
    // return null;
    // }

    private CompletionProposal[][] guessParameters(char[][] parameterNames) {
        // find matches in reverse order. Do this because people tend to declare the variable meant for the last
        // parameter last. That is, local variables for the last parameter in the method completion are more
        // likely to be closer to the point of code completion. As an example consider a "delegation" completion:
        //
        // public void myMethod(int param1, int param2, int param3) {
        // someOtherObject.yourMethod(param1, param2, param3);
        // }
        //
        // The other consideration is giving preference to variables that have not previously been used in this
        // code completion (which avoids "someOtherObject.yourMethod(param1, param1, param1)";

        int count = parameterNames.length;
        fPositions = new Position[count];
        fChoices = new CompletionProposal[count][];

        String[] parameterTypes = getParameterTypes();
        // ParameterGuesser guesser= new ParameterGuesser(getEnclosingElement());
        // IJavaElement[][] assignableElements= getAssignableElements();

        // for (int i= count - 1; i >= 0; i--) {
        // String paramName= new String(parameterNames[i]);
        // Position position= new Position(0,0);
        //
        // boolean isLastParameter= i == count - 1;
        // ICompletionProposal[] argumentProposals= guesser.parameterProposals(parameterTypes[i], paramName, position,
        // assignableElements[i], fFillBestGuess, isLastParameter);
        // if (argumentProposals.length == 0) {
        // JavaCompletionProposal proposal= new JavaCompletionProposal(paramName, 0, paramName.length(), null, new
        // StyledString(paramName), 0, false, fInvocationContext);
        // if (isLastParameter)
        // proposal.setTriggerCharacters(new char[] { ',' });
        // argumentProposals= new ICompletionProposal[] { proposal };
        // }
        //
        // fPositions[i]= position;
        // fChoices[i]= argumentProposals;
        // }

        return fChoices;
    }

    private String[] getParameterTypes() {
        char[] signature = SignatureUtil.fix83600(fProposal.getSignature());
        char[][] types = Signature.getParameterTypes(signature);

        String[] ret = new String[types.length];
        for (int i = 0; i < types.length; i++) {
            ret[i] = new String(Signature.toCharArray(types[i]));
        }
        return ret;
    }

    /*
     * @see ICompletionProposal#getSelection(IDocument)
     */
    @Override
    public Region getSelection(Document document) {
        if (fSelectedRegion == null)
            return new RegionImpl(getReplacementOffset(), 0);

        return fSelectedRegion;
    }

    // private void openErrorDialog(Exception e) {
    // Shell shell= getTextViewer().getTextWidget().getShell();
    // MessageDialog.openError(shell, JavaTextMessages.ParameterGuessingProposal_error_msg, e.getMessage());
    // }

    // private void ensurePositionCategoryInstalled(final IDocument document, LinkedModeModel model) {
    // if (!document.containsPositionCategory(getCategory())) {
    // document.addPositionCategory(getCategory());
    // fUpdater= new InclusivePositionUpdater(getCategory());
    // document.addPositionUpdater(fUpdater);
    //
    // model.addLinkingListener(new ILinkedModeListener() {
    //
    // /*
    // * @see org.eclipse.jface.text.link.ILinkedModeListener#left(org.eclipse.jface.text.link.LinkedModeModel, int)
    // */
    // public void left(LinkedModeModel environment, int flags) {
    // ensurePositionCategoryRemoved(document);
    // }
    //
    // public void suspend(LinkedModeModel environment) {}
    // public void resume(LinkedModeModel environment, int flags) {}
    // });
    // }
    // }

    private void ensurePositionCategoryRemoved(Document document) {
        if (document.containsPositionCategory(getCategory())) {
            try {
                document.removePositionCategory(getCategory());
            } catch (BadPositionCategoryException e) {
                // ignore
            }
            document.removePositionUpdater(fUpdater);
        }
    }

    private String getCategory() {
        return "ParameterGuessingProposal_" + toString(); //$NON-NLS-1$
    }

}
