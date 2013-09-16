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
package org.eclipse.jdt.client;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;

import org.eclipse.jdt.client.codeassistant.api.IProblemLocation;
import org.eclipse.jdt.client.codeassistant.ui.CodeAssitantForm;
import org.eclipse.jdt.client.codeassistant.ui.ProposalSelectedHandler;
import org.eclipse.jdt.client.core.compiler.IProblem;
import org.eclipse.jdt.client.core.dom.CompilationUnit;
import org.eclipse.jdt.client.event.ShowQuickFixEvent;
import org.eclipse.jdt.client.event.ShowQuickFixHandler;
import org.eclipse.jdt.client.internal.text.correction.JavaCorrectionProcessor;
import org.eclipse.jdt.client.internal.text.correction.ProblemLocation;
import org.eclipse.jdt.client.internal.text.quickassist.IQuickAssistInvocationContext;
import org.eclipse.jdt.client.runtime.CoreException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.editor.client.api.Editor;
import org.exoplatform.ide.editor.client.api.SelectionRange;
import org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal;
import org.exoplatform.ide.editor.client.api.event.EditorHotKeyPressedEvent;
import org.exoplatform.ide.editor.client.api.event.EditorHotKeyPressedHandler;
import org.exoplatform.ide.editor.client.marking.Markable;
import org.exoplatform.ide.editor.client.marking.Marker;
import org.exoplatform.ide.editor.client.marking.ProblemClickEvent;
import org.exoplatform.ide.editor.client.marking.ProblemClickHandler;
import org.exoplatform.ide.editor.shared.text.BadLocationException;
import org.exoplatform.ide.editor.shared.text.IDocument;
import org.exoplatform.ide.editor.shared.text.IRegion;
import org.exoplatform.ide.editor.shared.text.Position;
import org.exoplatform.ide.vfs.client.model.FileModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class QuickFixPresenter implements IQuickAssistInvocationContext, EditorActiveFileChangedHandler,
                                          ShowQuickFixHandler, UpdateOutlineHandler, ProposalSelectedHandler, EditorHotKeyPressedHandler,
                                          ProblemClickHandler {

    private Editor editor;

    private CompilationUnit compilationUnit;

    private FileModel file;

    private AssistDisplay display;

    private IProblemLocation[] currentAnnotations;

    private IDocument document;

    private int currLength;

    private int currOffset;

    private JavaCorrectionProcessor correctionProcessor;

    private HandlerRegistration keyHandler;

    private HandlerRegistration problemClickHandler;

    private final HandlerManager eventBus;

    private final SupportedProjectResolver resolver;

    /**
     *
     */
    public QuickFixPresenter(HandlerManager eventBus, SupportedProjectResolver resolver) {
        this.eventBus = eventBus;
        this.resolver = resolver;
        correctionProcessor = new JavaCorrectionProcessor();
        eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this);
        eventBus.addHandler(ShowQuickFixEvent.TYPE, this);
        eventBus.addHandler(UpdateOutlineEvent.TYPE, this);
    }

    /** @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform
     * .ide.client.framework.editor.event.EditorActiveFileChangedEvent) */
    @Override
    public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {
        if (problemClickHandler != null) {
            problemClickHandler.removeHandler();
            problemClickHandler = null;
        }
        if (event.getFile() == null || !MimeType.APPLICATION_JAVA.equals(event.getFile().getMimeType()))
            return;

        if (event.getEditor() instanceof Markable) {
            editor = event.getEditor();
            problemClickHandler = ((Markable)editor).addProblemClickHandler(this);
            file = event.getFile();
        }
    }

    /** @see org.eclipse.jdt.client.internal.text.quickassist.IQuickAssistInvocationContext#getOffset() */
    @Override
    public int getOffset() {
        return currOffset;
    }

    /** @see org.eclipse.jdt.client.internal.text.quickassist.IQuickAssistInvocationContext#getLength() */
    @Override
    public int getLength() {
        return currLength;
    }

    /** @see org.eclipse.jdt.client.internal.text.quickassist.IQuickAssistInvocationContext#getDocument() */
    @Override
    public IDocument getDocument() {
        return document;
    }

    /** @see org.eclipse.jdt.client.internal.text.quickassist.IQuickAssistInvocationContext#getProblemsAtOffset() */
    @Override
    public IProblemLocation[] getProblemsAtOffset() {
        return currentAnnotations;
    }

    /** @see org.eclipse.jdt.client.internal.text.quickassist.IQuickAssistInvocationContext#isUpdatedOffset() */
    @Override
    public boolean isUpdatedOffset() {
        return false;
    }

    /** @see org.eclipse.jdt.client.event.ShowQuickFixHandler#onShowQuickFix(org.eclipse.jdt.client.event.ShowQuickFixEvent) */
    @Override
    public void onShowQuickFix(ShowQuickFixEvent event) {
        if (editor == null || compilationUnit == null)
            return;
        if (!resolver.isProjectSupported(file.getProject().getProjectType()))
            return;
        IProblem[] problems = compilationUnit.getProblems();
        new String();
        boolean isReinvoked = false;

        if (display != null) {
            if (isUpdatedOffset()) {
                isReinvoked = true;
            }
        }

        document = editor.getDocument();
        try {
            SelectionRange selectedRange = editor.getSelectionRange();
            currOffset = document.getLineOffset(selectedRange.getStartLine() - 1) + selectedRange.getStartSymbol();
            currLength =
                    document.getLineOffset(selectedRange.getEndLine() - 1) + selectedRange.getEndSymbol() - currOffset;
            boolean goToClosest = (currLength == 0) && !isReinvoked;

            selectProposalPosition(problems, goToClosest);
        } catch (BadLocationException e) {
            e.printStackTrace();
            eventBus.fireEvent(new OutputEvent(e.getMessage(), Type.ERROR));
        }
        Scheduler.get().scheduleDeferred(quickFx);
    }

    /**
     * @param problems
     * @param goToClosest
     * @throws BadLocationException
     */
    private void selectProposalPosition(IProblem[] problems, boolean goToClosest) throws BadLocationException {
        final ArrayList<IProblemLocation> resultingAnnotations = new ArrayList<IProblemLocation>(20);
        int newOffset = collectQuickFixableAnnotations(editor, currOffset, goToClosest, problems, resultingAnnotations);
        if (newOffset != currOffset) {
            int row = document.getLineOfOffset(newOffset) + 1;
            editor.setCursorPosition(row, newOffset - document.getLineOffset(row - 1) + 1);
            currOffset = newOffset;

        }
        currentAnnotations = resultingAnnotations.toArray(new IProblemLocation[resultingAnnotations.size()]);
    }

    private ScheduledCommand quickFx = new ScheduledCommand() {

        @Override
        public void execute() {
            try {
                CompletionProposal[] proposals = correctionProcessor.computeQuickAssistProposals(QuickFixPresenter.this);
                if (display == null) {
                    int posX = editor.getCursorOffsetLeft() + 2;
                    int posY = editor.getCursorOffsetTop() + 15;
                    keyHandler = IDE.addHandler(EditorHotKeyPressedEvent.TYPE, QuickFixPresenter.this);
                    display = new CodeAssitantForm(posX, posY, proposals, QuickFixPresenter.this);
                } else {
                    display.setNewProposals(proposals);
                }
            } catch (CoreException e) {
                eventBus.fireEvent(new OutputEvent(e.getStatus().getMessage(), Type.ERROR));
            } catch (Exception e) {
                e.printStackTrace();
                eventBus.fireEvent(new OutputEvent(e.getMessage(), Type.ERROR));
            }
        }
    };

    /** @see org.eclipse.jdt.client.UpdateOutlineHandler#onUpdateOutline(org.eclipse.jdt.client.UpdateOutlineEvent) */
    @Override
    public void onUpdateOutline(UpdateOutlineEvent event) {
        if (event.getFile().getId().equals(file.getId()))
            compilationUnit = event.getCompilationUnit();
        else
            compilationUnit = null;
    }

    private static IRegion getRegionOfInterest(Editor editor, int invocationLocation) throws BadLocationException {
        IDocument document = editor.getDocument();
        if (document == null) {
            return null;
        }
        return document.getLineInformationOfOffset(invocationLocation);
    }

    public static int collectQuickFixableAnnotations(Editor editor, int invocationLocation, boolean goToClosest,
                                                     IProblem[] problems, ArrayList<IProblemLocation> resultingAnnotations)
            throws BadLocationException {

        if (problems.length == 0) {
            return invocationLocation;
        }

        if (goToClosest) {
            IRegion lineInfo = getRegionOfInterest(editor, invocationLocation);
            if (lineInfo == null) {
                return invocationLocation;
            }
            int rangeStart = lineInfo.getOffset();
            int rangeEnd = rangeStart + lineInfo.getLength();

            ArrayList<Position> allPositions = new ArrayList<Position>();
            List<IProblemLocation> allAnnotations = new ArrayList<IProblemLocation>();
            int bestOffset = Integer.MAX_VALUE;
            for (IProblem problem : problems) {
                Position pos = new Position(problem.getSourceStart(), problem.getSourceEnd() - problem.getSourceStart());
                if (pos != null && isInside(pos.offset, rangeStart, rangeEnd)) { // inside our range?
                    IProblemLocation annot = new ProblemLocation(problem);
                    allAnnotations.add(annot);
                    allPositions.add(pos);
                    bestOffset = processAnnotation(annot, pos, invocationLocation, bestOffset);
                }
            }
            if (bestOffset == Integer.MAX_VALUE) {
                return invocationLocation;
            }
            for (int i = 0; i < allPositions.size(); i++) {
                Position pos = allPositions.get(i);
                if (isInside(bestOffset, pos.offset, pos.offset + pos.length)) {
                    resultingAnnotations.add(allAnnotations.get(i));
                }
            }
            return bestOffset;
        } else {
            for (IProblem problem : problems) {
                Position pos = new Position(problem.getSourceStart(), problem.getSourceEnd() - problem.getSourceStart());
                if (pos != null && isInside(invocationLocation, pos.offset, pos.offset + pos.length)) {
                    resultingAnnotations.add(new ProblemLocation(problem));
                }
            }
            return invocationLocation;
        }
    }

    private static int processAnnotation(IProblemLocation annot, Position pos, int invocationLocation, int bestOffset) {
        int posBegin = pos.offset;
        int posEnd = posBegin + pos.length;
        if (isInside(invocationLocation, posBegin, posEnd)) { // covers invocation location?
            return invocationLocation;
        } else if (bestOffset != invocationLocation) {
            int newClosestPosition = computeBestOffset(posBegin, invocationLocation, bestOffset);
            if (newClosestPosition != -1) {
                if (newClosestPosition != bestOffset) { // new best
                    if (JavaCorrectionProcessor.hasCorrections(annot)) { // only jump to it if there are proposals
                        return newClosestPosition;
                    }
                }
            }
        }
        return bestOffset;
    }

    /**
     * Computes and returns the invocation offset given a new
     * position, the initial offset and the best invocation offset
     * found so far.
     * <p>
     * The closest offset to the left of the initial offset is the
     * best. If there is no offset on the left, the closest on the
     * right is the best.</p>
     *
     * @param newOffset
     *         the offset to llok at
     * @param invocationLocation
     *         the invocation location
     * @param bestOffset
     *         the current best offset
     * @return -1 is returned if the given offset is not closer or the new best offset
     */
    private static int computeBestOffset(int newOffset, int invocationLocation, int bestOffset) {
        if (newOffset <= invocationLocation) {
            if (bestOffset > invocationLocation) {
                return newOffset; // closest was on the right, prefer on the left
            } else if (bestOffset <= newOffset) {
                return newOffset; // we are closer or equal
            }
            return -1; // further away
        }

        if (newOffset <= bestOffset)
            return newOffset; // we are closer or equal

        return -1; // further away
    }

    private static boolean isInside(int offset, int start, int end) {
        return offset == start || offset == end || (offset > start && offset < end); // make sure to handle 0-length ranges
    }

    /**
     * @see org.eclipse.jdt.client.codeassistant.ui.ProposalSelectedHandler#onTokenSelected(org.eclipse.jdt.client.codeassistant.api
     * .IJavaCompletionProposal,
     *      boolean)
     */
    @Override
    public void onTokenSelected(CompletionProposal proposal, boolean editorHasFocus) {
        try {
            proposal.apply(document);
            onCancelAutoComplete(editorHasFocus);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** @see org.eclipse.jdt.client.codeassistant.ui.ProposalSelectedHandler#onCancelAutoComplete(boolean) */
    @Override
    public void onCancelAutoComplete(boolean editorHasFocus) {
        if (!editorHasFocus)
            editor.setFocus();
        keyHandler.removeHandler();
        display = null;
    }

    /** @see org.exoplatform.ide.editor.client.api.event.EditorHotKeyPressedHandler#onEditorHotKeyPressed(org.exoplatform.ide.editor
     * .client.api.event.EditorHotKeyPressedEvent) */
    @Override
    public void onEditorHotKeyPressed(EditorHotKeyPressedEvent event) {
        switch (event.getKeyCode()) {
            case KeyCodes.KEY_DOWN:
                display.moveSelectionDown();
                event.setHotKeyHandled(true);
                break;

            case KeyCodes.KEY_UP:
                display.moveSelectionUp();
                event.setHotKeyHandled(true);
                break;

            case KeyCodes.KEY_ENTER:
                display.proposalSelected();
                event.setHotKeyHandled(true);
                break;

            case KeyCodes.KEY_ESCAPE:
                display.cancelCodeAssistant();
                event.setHotKeyHandled(true);
                break;

            case KeyCodes.KEY_RIGHT:
                if (editor.getCursorColumn() + 1 > editor.getLineText(editor.getCursorRow()).length())
                    display.cancelCodeAssistant();
                else
                    generateNewProposals();
                break;

            case KeyCodes.KEY_LEFT:
                if (editor.getCursorColumn() - 1 <= 0)
                    display.cancelCodeAssistant();
                else
                    generateNewProposals();
                break;

            default:
                display.cancelCodeAssistant();
                break;
        }
    }

    /**
     *
     */
    private void generateNewProposals() {
        onShowQuickFix(null);
    }

    /** @see org.exoplatform.ide.editor.problem.ProblemClickHandler#onProblemClick(org.exoplatform.ide.editor.problem.ProblemClickEvent) */
    @Override
    public void onProblemClick(ProblemClickEvent event) {
        List<IProblem> problems = new ArrayList<IProblem>();
        for (Marker p : event.getProblems()) {
            if (p instanceof ProblemImpl) {
                problems.add(((ProblemImpl)p).getOriginalProblem());
            }
        }
        document = editor.getDocument();
        currLength = 0;
        try {
            currOffset = document.getLineOffset(event.getProblems()[0].getLineNumber() - 1);
            selectProposalPosition(problems.toArray(new IProblem[problems.size()]), true);
            Scheduler.get().scheduleDeferred(quickFx);
        } catch (BadLocationException e) {
            e.printStackTrace();
            eventBus.fireEvent(new OutputEvent(e.getMessage(), Type.ERROR));
        }
    }

}
