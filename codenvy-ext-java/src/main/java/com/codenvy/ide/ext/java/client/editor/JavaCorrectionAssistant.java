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
package com.codenvy.ide.ext.java.client.editor;

import com.codenvy.ide.api.editor.TextEditorPartPresenter;
import com.codenvy.ide.text.BadLocationException;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.text.Position;
import com.codenvy.ide.text.Region;
import com.codenvy.ide.text.annotation.Annotation;
import com.codenvy.ide.text.annotation.AnnotationModel;
import com.codenvy.ide.texteditor.api.TextEditorPartView;
import com.codenvy.ide.texteditor.codeassistant.QuickAssistAssistantImpl;
import com.codenvy.ide.util.loging.Log;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class JavaCorrectionAssistant extends QuickAssistAssistantImpl {

    private TextEditorPartView textView;

    private TextEditorPartPresenter textEditor;

    private Position fPosition;

    private Annotation[] fCurrentAnnotations;

    private boolean fIsCompletionActive;

    /** @param textEditor */
    public JavaCorrectionAssistant(TextEditorPartPresenter textEditor, JavaParserWorker worker) {
        this.textEditor = textEditor;
        JavaCorrectionProcessor processor = new JavaCorrectionProcessor(this, worker);
        setQuickAssistProcessor(processor);
    }

    /** {@inheritDoc} */
    @Override
    public void install(TextEditorPartView textEditor) {
        this.textView = textEditor;
        super.install(textEditor);
    }

    /**
     * Show completions at caret position. If current
     * position does not contain quick fixes look for
     * next quick fix on same line by moving from left
     * to right and restarting at end of line if the
     * beginning of the line is reached.
     * <p/>
     * {@inheritDoc}
     */
    @Override
    public String showPossibleQuickAssists() {
        fPosition = null;
        fCurrentAnnotations = null;

        if (textView == null || textView.getDocument() == null)
            // Let superclass deal with this
            return super.showPossibleQuickAssists();

        ArrayList<Annotation> resultingAnnotations = new ArrayList<Annotation>(20);
        try {
            Position selectedRange = textView.getSelection().getSelectedRange();
            int currOffset = selectedRange.offset;
            int currLength = selectedRange.length;
            boolean goToClosest = (currLength == 0);

            int newOffset =
                    collectQuickFixableAnnotations(textEditor, textView, currOffset, goToClosest, resultingAnnotations);
            if (newOffset != currOffset) {
                storePosition(currOffset, currLength);
                textView.getSelection().selectAndReveal(newOffset, 0);
                if (fIsCompletionActive) {
                    hide();
                }
            }
        } catch (BadLocationException e) {
            Log.error(getClass(), e);
        }
        fCurrentAnnotations = resultingAnnotations.toArray(new Annotation[resultingAnnotations.size()]);

        return super.showPossibleQuickAssists();

    }

    public static int collectQuickFixableAnnotations(TextEditorPartPresenter editor, TextEditorPartView view,
                                                     int invocationLocation, boolean goToClosest,
                                                     ArrayList<Annotation> resultingAnnotations)
            throws BadLocationException {
        AnnotationModel model = editor.getDocumentProvider().getAnnotationModel(editor.getEditorInput());
        if (model == null) {
            return invocationLocation;
        }

        //      ensureUpdatedAnnotations(editor);

        Iterator<Annotation> iter = model.getAnnotationIterator();
        if (goToClosest) {
            Region lineInfo = getRegionOfInterest(view, invocationLocation);
            if (lineInfo == null) {
                return invocationLocation;
            }
            int rangeStart = lineInfo.getOffset();
            int rangeEnd = rangeStart + lineInfo.getLength();

            ArrayList<Annotation> allAnnotations = new ArrayList<Annotation>();
            ArrayList<Position> allPositions = new ArrayList<Position>();
            int bestOffset = Integer.MAX_VALUE;
            while (iter.hasNext()) {
                Annotation annot = iter.next();
                if (JavaCorrectionProcessor.isQuickFixableType(annot)) {
                    Position pos = model.getPosition(annot);
                    if (pos != null && isInside(pos.offset, rangeStart, rangeEnd)) { // inside our range?
                        allAnnotations.add(annot);
                        allPositions.add(pos);
                        bestOffset = processAnnotation(annot, pos, invocationLocation, bestOffset);
                    }
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
            while (iter.hasNext()) {
                Annotation annot = iter.next();
                if (JavaCorrectionProcessor.isQuickFixableType(annot)) {
                    Position pos = model.getPosition(annot);
                    if (pos != null && isInside(invocationLocation, pos.offset, pos.offset + pos.length)) {
                        resultingAnnotations.add(annot);
                    }
                }
            }
            return invocationLocation;
        }
    }

    private static int processAnnotation(Annotation annot, Position pos, int invocationLocation, int bestOffset) {
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

    private void storePosition(int currOffset, int currLength) {
        fPosition = new Position(currOffset, currLength);
    }

    private static Region getRegionOfInterest(TextEditorPartView view, int invocationLocation)
            throws BadLocationException {
        Document document = view.getDocument();
        if (document == null) {
            return null;
        }
        return document.getLineInformationOfOffset(invocationLocation);
    }

    /**
     * Returns the annotations at the current offset
     *
     * @return the annotations at the offset
     */
    public Annotation[] getAnnotationsAtOffset() {
        return fCurrentAnnotations;
    }

    /** @return  */
    public TextEditorPartPresenter getEditor() {
        return textEditor;
    }

    /**
     * Returns true if the last invoked completion was called with an updated offset.
     *
     * @return <code> true</code> if the last invoked completion was called with an updated offset.
     */
    public boolean isUpdatedOffset() {
        return fPosition != null;
    }
}
