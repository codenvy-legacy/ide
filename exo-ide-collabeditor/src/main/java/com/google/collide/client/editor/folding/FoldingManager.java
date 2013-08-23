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
package com.google.collide.client.editor.folding;

import com.codenvy.ide.client.util.logging.Log;
import com.codenvy.ide.commons.shared.ListenerManager;
import com.codenvy.ide.commons.shared.ListenerRegistrar;
import com.codenvy.ide.commons.shared.ListenerManager.Dispatcher;
import com.codenvy.ide.json.shared.JsonArray;
import com.codenvy.ide.json.shared.JsonCollections;
import com.google.collide.client.Resources;
import com.google.collide.client.editor.Buffer;
import com.google.collide.client.editor.Editor;
import com.google.collide.client.editor.gutter.Gutter;
import com.google.collide.client.editor.gutter.Gutter.ClickListener;
import com.google.collide.shared.document.Document;
import com.google.collide.shared.document.Line;
import com.google.collide.shared.document.TextChange;
import com.google.collide.shared.document.anchor.Anchor;
import com.google.collide.shared.document.anchor.AnchorManager;

import org.exoplatform.ide.editor.shared.text.BadLocationException;
import org.exoplatform.ide.editor.shared.text.IDocument;
import org.exoplatform.ide.editor.shared.text.IDocumentInformationMapping;
import org.exoplatform.ide.editor.shared.text.IRegion;
import org.exoplatform.ide.editor.shared.text.ISlaveDocumentManager;
import org.exoplatform.ide.editor.shared.text.Position;
import org.exoplatform.ide.editor.shared.text.Region;
import org.exoplatform.ide.editor.shared.text.projection.IProjectionPosition;
import org.exoplatform.ide.editor.shared.text.projection.ProjectionDocument;
import org.exoplatform.ide.editor.shared.text.projection.ProjectionDocumentManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

/**
 * A class to manage the editor's code folding functionality.
 * <p/>
 * The lifecycle of this class is tied to the {@link Editor} that owns it.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: FoldingManager.java Mar 2, 2013 6:39:46 PM azatsarynnyy $
 */
public class FoldingManager implements Document.TextListener {
    /** A listener that is called when a text block was collapsed or expanded. */
    public interface FoldingListener {
        /**
         * @param lineNumber the number of the first line in {@code linesToCollapse}
         * @param linesToCollapse a contiguous list of lines that should be collapsed
         */
        void onCollapse(int lineNumber, JsonArray<Line> linesToCollapse);

        /**
         * @param lineNumber the previous line number of the first item in {@code linesToExpand}
         * @param linesToExpand a contiguous list of lines that should be expanded
         */
        void onExpand(int lineNumber, JsonArray<Line> linesToExpand);
    }

    /** A listener that is called when a folds state was changed. */
    public interface FoldsStateListener {
        /** Called when any fold mark added/removed or it state changed (collapsed/expanded). */
        void onFoldsStateChange();
    }

    private final ListenerManager<FoldingListener>    foldingListenerManager;

    private final ListenerManager<FoldsStateListener> foldMarksStateListenerManager;

    /** Manager for anchors within a document. */
    private AnchorManager                             anchorManager;

    private final JsonArray<Anchor>                   anchorsInCollapsedRangeToRemove = JsonCollections.createArray();

    private final JsonArray<Anchor>                   anchorsInCollapsedRangeToShift  = JsonCollections.createArray();

    private final JsonArray<Anchor>                   anchorsLeftoverFromLastLine     = JsonCollections.createArray();

    /** Resources. */
    private final Resources                           resources;

    /** Editor. */
    private final Editor                              editor;

    /** Gutter to display fold markers. */
    private final Gutter                              gutter;

    /** Editor's buffer. */
    private final Buffer                              buffer;

    private HashMap<FoldMarker, AbstractFoldRange>    markerToPositionMap             = new HashMap<FoldMarker, AbstractFoldRange>();

    private HashMap<FoldMarker, AbstractFoldRange>    customMarkerToPositionMap       =
                                                                                        new HashMap<FoldMarker, AbstractFoldRange>();

    /** CollabEditor's document. */
    private Document                                  document;

    /** Slave document. */
    private ProjectionDocument                        slaveDocument;

    /** The slave document manager. */
    private ISlaveDocumentManager                     slaveDocumentManager;

    /** The mapping between model and visible document. */
    private IDocumentInformationMapping               informationMapping;

    private FoldOccurrencesFinder                     foldOccurrencesFinder;

    /** Creates new 'empty' {@link FoldingManager}. */
    public FoldingManager() {
        this(null, null, null, null);
    }

    /**
     * Constructs new {@link FoldingManager} instance.
     * 
     * @param editor {@link Editor}
     * @param gutter {@link Gutter}
     * @param buffer {@link Buffer}
     * @param resources {@link Resources}
     */
    public FoldingManager(Editor editor, Gutter gutter, Buffer buffer, Resources resources) {
        this.editor = editor;
        this.gutter = gutter;
        this.buffer = buffer;
        this.resources = resources;
        foldingListenerManager = ListenerManager.create();
        foldMarksStateListenerManager = ListenerManager.create();
        initializeGutter();
    }

    private void initializeGutter() {
        if (gutter == null) {
            return;
        }

        gutter.setWidth(11);
        gutter.getClickListenerRegistrar().add(new ClickListener() {
            @Override
            public void onClick(int y) {
                final int lineNumber = buffer.convertYToLineNumber(y, true);
                FoldMarker foldMarker = getFoldMarkerOfLine(lineNumber, true);
                if (foldMarker != null) {
                    toggleExpansionState(foldMarker);
                }
            }
        });
    }

    public ListenerRegistrar<FoldingListener> getFoldingListenerRegistrar() {
        return foldingListenerManager;
    }

    public ListenerRegistrar<FoldsStateListener> getFoldMarksStateListenerRegistrar() {
        return foldMarksStateListenerManager;
    }

    /**
     * Expand the specified <code>foldMarker</code>.
     * 
     * @param foldMarker the {@link FoldMarker} to expand
     */
    public void expand(FoldMarker foldMarker) {
        if (foldMarker.isCollapsed()) {
            toggleExpansionState(foldMarker);
        }
    }

    /** Expand all collapsed fold markers. */
    public void expandAll() {
        for (FoldMarker marker : markerToPositionMap.keySet()) {
            if (marker.isCollapsed()) {
                toggleExpansionState(marker);
            }
        }
    }

    /**
     * Collapse the specified <code>foldMarker</code>.
     * 
     * @param foldMarker the {@link FoldMarker} to collapse
     */
    public void collapse(FoldMarker foldMarker) {
        if (!foldMarker.isCollapsed()) {
            toggleExpansionState(foldMarker);
        }
    }

    /** Collapse all expanded fold markers. */
    public void collapseAll() {
        for (FoldMarker marker : markerToPositionMap.keySet()) {
            if (!marker.isCollapsed()) {
                toggleExpansionState(marker);
            }
        }
    }

    /**
     * Fold the specified text region.
     * 
     * @param offset text offset of a region to fold
     * @param length text length of a region to fold
     */
    public void foldCustomRegion(int offset, int length) {
        FoldMarker foldMarker = new FoldMarker(false, resources);
        AbstractFoldRange foldRange = new AbstractFoldRange(offset, length) {
            @Override
            public IRegion[] computeProjectionRegions(IDocument document) throws BadLocationException {
                int captionLineNumber = document.getLineOfOffset(offset);
                int captionLineLength = document.getLineLength(captionLineNumber);
                return new Region[]{new Region(offset + captionLineLength, length - captionLineLength)};
            }

            @Override
            public int computeCaptionOffset(IDocument document) throws BadLocationException {
                return 0;
            }
        };
        customMarkerToPositionMap.put(foldMarker, foldRange);
        markerToPositionMap.put(foldMarker, foldRange);

        try {
            getMasterDocument().addPosition(foldRange);
        } catch (BadLocationException e) {
            Log.error(getClass(), e);
        }

        collapse(foldMarker);
    }

    /**
     * @see com.google.collide.shared.document.Document.TextListener#onTextChange(com.google.collide.shared.document.Document,
     *      com.google.collide.json.shared.JsonArray)
     */
    @Override
    public void onTextChange(Document document, final JsonArray<TextChange> textChanges) {
        updateFoldStructureAndDispatch(foldOccurrencesFinder.findPositions(getMasterDocument()), true);
        revealRegionsWithTextChanges(textChanges);
    }

    /**
     * Expand all collapsed regions where specified <code>textChanged</code> was occurred.
     * 
     * @param textChanges
     */
    private void revealRegionsWithTextChanges(final JsonArray<TextChange> textChanges) {
        for (TextChange textChange : textChanges.asIterable()) {
            for (int i = textChange.getLineNumber(); i <= textChange.getLastLineNumber(); i++) {
                FoldMarker foldMarker = getFoldMarkerOfLine(i, false);
                if (foldMarker != null && foldMarker.isCollapsed()) {
                    expand(foldMarker);
                }
            }
        }
    }

    /**
     * Handle changing editor's document.
     * 
     * @param newDocument new {@link Document}
     */
    public void handleDocumentChanged(final Document newDocument) {
        if (foldOccurrencesFinder == null) {
            return;
        }
        markerToPositionMap.clear();
        if (document != null) {
            document.getTextListenerRegistrar().remove(this);
        }
        document = newDocument;
        anchorManager = document.getAnchorManager();
        document.getTextListenerRegistrar().add(this);

        freeSlaveDocument(slaveDocument);
        IDocument masterDocument = document.<IDocument> getTag("IDocument");
        initializeProjection(masterDocument);

        updateFoldStructureAndDispatch(foldOccurrencesFinder.findPositions(masterDocument), false);
    }

    /**
     * Toggles the expansion state of the given fold marker.
     * 
     * @param foldMarker the fold marker
     */
    private void toggleExpansionState(FoldMarker foldMarker) {
        if (foldMarker.isCollapsed()) {
            foldMarker.markExpanded();
        } else {
            foldMarker.markCollapsed();
        }
        editor.getSelection().deselect();
        modifyFoldMarker(foldMarker);
        dispatchFoldsStateChange();
    }

    /**
     * Modifies the given <code>foldMarker</code> if the <code>foldMarker</code> is managed by this {@link FoldingManager}.
     * 
     * @param foldMarker {@link FoldMarker} to modify
     */
    private void modifyFoldMarker(FoldMarker foldMarker) {
        try {
            IProjectionPosition position = markerToPositionMap.get(foldMarker);
            IRegion[] regions = position.computeProjectionRegions(getMasterDocument());
            for (int i = 0; i < regions.length; i++) {
                final int startOffset = regions[i].getOffset();
                final int length = regions[i].getLength();
                final int firstLineNumber = getMasterDocument().getLineOfOffset(startOffset);
                int lineCount = getMasterDocument().getNumberOfLines(startOffset, length);
                if (!getMasterDocument().get(startOffset, length).endsWith("\n")) {
                    lineCount++;
                }
                Line beginLine = document.getLineFinder().findLine(firstLineNumber).line();

                JsonArray<Line> modifiedLines = JsonCollections.createArray();
                Line nextLine = beginLine;
                modifiedLines.add(nextLine);
                for (int j = 0; j < lineCount - 2; j++) {
                    nextLine = nextLine.getNextLine();
                    modifiedLines.add(nextLine);
                }

                if (foldMarker.isCollapsed()) {
                    slaveDocument.removeMasterDocumentRange(startOffset, length);
                    processAnchorsInCollapsedRange(firstLineNumber, modifiedLines);
                    dispatchCollapse(firstLineNumber, modifiedLines);
                } else {
                    slaveDocument.addMasterDocumentRange(startOffset, length);

                    // collapse nested folds when parent fold is expanded
                    FoldMarker[] collapsedFolds = computeCollapsedNestedFolds(startOffset, length);
                    if (collapsedFolds != null) {
                        for (int m = 0; m < collapsedFolds.length; m++) {
                            IProjectionPosition positionToCollapse = markerToPositionMap.get(collapsedFolds[m]);
                            IRegion[] regionsToCollapse = positionToCollapse.computeProjectionRegions(getMasterDocument());
                            if (regionsToCollapse != null) {
                                for (int n = 0; n < regionsToCollapse.length; n++) {
                                    slaveDocument.removeMasterDocumentRange(regionsToCollapse[n].getOffset(),
                                                                            regionsToCollapse[n].getLength());
                                }
                            }
                        }
                    }
                    dispatchExpand(firstLineNumber, modifiedLines);
                }
            }
        } catch (BadLocationException e) {
            Log.error(getClass(), e);
        }
    }

    private FoldMarker[] computeCollapsedNestedFolds(int offset, int length) {
        List<FoldMarker> folds = new ArrayList<FoldMarker>(5);
        for (Entry<FoldMarker, AbstractFoldRange> entry : markerToPositionMap.entrySet()) {
            FoldMarker fold = entry.getKey();
            if (fold.isCollapsed()) {
                Position position = entry.getValue();
                // if (position == null) {
                // // annotation might already be deleted, we will be informed later on about this deletion
                // continue;
                // }
                if (covers(offset, length, position)) {
                    folds.add(fold);
                }
            }
        }

        if (folds.size() > 0) {
            FoldMarker[] result = new FoldMarker[folds.size()];
            folds.toArray(result);
            return result;
        }

        return null;
    }

    private int computeCollapsedNestedRangesLength(int offset, int length) {
        int summaryLength = 0;
        try {
            for (Entry<FoldMarker, AbstractFoldRange> entry : markerToPositionMap.entrySet()) {
                FoldMarker fold = entry.getKey();
                if (fold.isCollapsed()) {
                    AbstractFoldRange range = entry.getValue();
                    if (covers(offset, length, range)) {
                        IRegion[] regions = range.computeProjectionRegions(getMasterDocument());
                        for (IRegion iRegion : regions) {
                            summaryLength += iRegion.getLength();
                        }
                    }
                }
            }
        } catch (BadLocationException e) {
            Log.error(getClass(), e);
        }
        return summaryLength;
    }

    private boolean covers(int offset, int length, Position position) {
        if (/*!(position.offset == offset && position.length == length) && */!position.isDeleted()) {
            return offset <= position.getOffset() && position.getOffset() + position.getLength() <= offset + length;
        }
        return false;
    }

    private void processAnchorsInCollapsedRange(int lineNumber, JsonArray<Line> linesToCollapse) {
        for (Line line : linesToCollapse.asIterable()) {
            final int deleteCountForLine = line.getText().length();
            boolean isFirstLine = linesToCollapse.indexOf(line) == 0;
            anchorManager.handleTextPredeletionForLine(line, 0, deleteCountForLine, anchorsInCollapsedRangeToRemove,
                                                       anchorsInCollapsedRangeToShift, isFirstLine);
        }

        Line firstLine = linesToCollapse.peek().getNextLine();
        int firstLineNumber = lineNumber + linesToCollapse.size();
        // shift anchors to up from the fold when document's last line is collapsed
        if (firstLine == null) {
            firstLine = linesToCollapse.get(0).getPreviousLine();
            firstLineNumber = lineNumber-1;
        }
        final int numberOfLinesDeleted = 0; // pass '0' because there is no need to change the anchor's line number
        final int lastLineFirstUntouchedColumn = linesToCollapse.peek().getText().length();

        anchorManager.handleTextDeletionFinished(anchorsInCollapsedRangeToRemove, anchorsInCollapsedRangeToShift,
                                                 anchorsLeftoverFromLastLine, firstLine, firstLineNumber, 0, numberOfLinesDeleted,
                                                 lastLineFirstUntouchedColumn);
    }

    private void dispatchCollapse(final int lineNumber, final JsonArray<Line> linesToCollapse) {
        foldingListenerManager.dispatch(new Dispatcher<FoldingListener>() {
            @Override
            public void dispatch(FoldingListener listener) {
                listener.onCollapse(lineNumber, linesToCollapse);
            }
        });
    }

    private void dispatchExpand(final int lineNumber, final JsonArray<Line> linesToExpand) {
        foldingListenerManager.dispatch(new Dispatcher<FoldingListener>() {
            @Override
            public void dispatch(FoldingListener listener) {
                listener.onExpand(lineNumber, linesToExpand);
            }
        });
    }

    private void dispatchFoldsStateChange() {
        foldMarksStateListenerManager.dispatch(new Dispatcher<FoldsStateListener>() {
            @Override
            public void dispatch(FoldsStateListener listener) {
                listener.onFoldsStateChange();
            }
        });
    }

    /**
     * Returns gutter for fold marks.
     * 
     * @return fold marks gutter
     */
    public Gutter getGutter() {
        return gutter;
    }

    /**
     * Returns the {@link FoldMarker} that contains the given line number or <code>null</code>.
     * 
     * @param lineNumber the line number
     * @param exact <code>true</code> if the fold range must match exactly
     * @return the fold marker contains the given line or <code>null</code>
     */
    public FoldMarker getFoldMarkerOfLine(int lineNumber, boolean exact) {
        FoldMarker previousFoldMarker = null;
        int previousDistance = Integer.MAX_VALUE;

        for (Entry<FoldMarker, AbstractFoldRange> entry : markerToPositionMap.entrySet()) {
            FoldMarker foldMarker = entry.getKey();
            AbstractFoldRange position = entry.getValue();
            if (position == null) {
                continue;
            }
            int distance = getDistance(lineNumber, position, foldMarker, getMasterDocument());
            if (distance == -1) {
                continue;
            }
            if (!exact) {
                if (distance < previousDistance) {
                    previousFoldMarker = foldMarker;
                    previousDistance = distance;
                }
            } else if (distance == 0) {
                previousFoldMarker = foldMarker;
            }
        }
        return previousFoldMarker;
    }

    /**
     * Ensures that the specified line number is visible.
     * 
     * @param lineNumber line number to check
     */
    public void ensureLineVisibility(int lineNumber) {
        if (isFoldingModeEnabled() && buffer.modelLine2VisibleLine(lineNumber) == -1) {
            FoldMarker foldMarker = getFoldMarkerOfLine(lineNumber, false);
            if (foldMarker != null && foldMarker.isCollapsed()) {
                expand(foldMarker);
            }
        }
    }

    /**
     * Returns number of the caption line of the given <code>foldMarker</code>.
     * 
     * @param marker fold marker to get caption line number
     * @return
     */
    public int getCaptionLine(FoldMarker marker) {
        if (marker == null) {
            return -1;
        }

        try {
            AbstractFoldRange coveredRange = markerToPositionMap.get(marker);
            final int captionOffset = coveredRange.getOffset() + coveredRange.computeCaptionOffset(getMasterDocument());
            return getMasterDocument().getLineOfOffset(captionOffset);
        } catch (BadLocationException e) {
            Log.error(getClass(), e);
        }
        return -1;
    }

    /**
     * Returns the distance of the given line to the start line of the given position in the given document. The distance is <code>-1</code>
     * when the line is not included in the given position.
     * 
     * @param line the line
     * @param position the position
     * @param foldMarker the fold marker
     * @param document the document
     * @return <code>-1</code> if line is not contained, a position number otherwise
     */
    private int getDistance(int line, AbstractFoldRange position, FoldMarker foldMarker, IDocument document) {
        if (position.getOffset() > -1 && position.getLength() > -1) {
            try {
                int startLine = document.getLineOfOffset(position.getOffset());
                int endLine = document.getLineOfOffset(position.getOffset() + position.getLength());
                if (startLine <= line && line < endLine) {
                    if (foldMarker.isCollapsed()) {
                        int captionOffset = position.computeCaptionOffset(document);
                        int captionLine = document.getLineOfOffset(position.getOffset() + captionOffset);
                        if (startLine <= captionLine && captionLine < endLine)
                            return Math.abs(line - captionLine);
                    }
                    return line - startLine;
                }
            } catch (BadLocationException e) {
                Log.error(getClass(), e);
            }
        }
        return -1;
    }

    /**
     * Updates the folding structure according to the given <code>positions</code> and informs all listeners.
     * 
     * @param positions list of the positions that describes the folding structure
     * @param restoreFoldsState
     */
    private void updateFoldStructureAndDispatch(List<AbstractFoldRange> positions, boolean restoreFoldsState) {
        markerToPositionMap.clear();
        for (AbstractFoldRange range : positions) {
            boolean isCollapsed = false;
            // if (restoreFoldsState) {
            // isCollapsed = isFoldRangeCollapsed(range);
            // }
            markerToPositionMap.put(new FoldMarker(isCollapsed, resources), range);
        }
        cleanUpCustomFolds();
        restoreCustomFolds();

        if (restoreFoldsState) {
            Iterator<Entry<FoldMarker, AbstractFoldRange>> iterator = markerToPositionMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<FoldMarker, AbstractFoldRange> entry = iterator.next();
                if (isFoldRangeCollapsed(entry.getValue())) {
                    entry.getKey().markCollapsed();
                }
            }
        }

        dispatchFoldsStateChange();
    }

    /**
     * Clean-up custom folds that covers less than two lines of text.
     */
    private void cleanUpCustomFolds() {
        try {
            Iterator<Entry<FoldMarker, AbstractFoldRange>> iterator = customMarkerToPositionMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<FoldMarker, AbstractFoldRange> entry = iterator.next();
                AbstractFoldRange foldRange = entry.getValue();

                String foldedText = getMasterDocument().get(foldRange.getOffset(), foldRange.getLength());
                int minNumberOfLines = foldedText.endsWith("\n") ? 3 : 2;
                int numberOfLines = getMasterDocument().getNumberOfLines(foldRange.getOffset(), foldRange.getLength());
                if (numberOfLines < minNumberOfLines) {
                    customMarkerToPositionMap.remove(entry.getKey());
                }
            }
        } catch (BadLocationException e) {
            Log.error(getClass(), e);
        }
    }

    private void restoreCustomFolds() {
        markerToPositionMap.putAll(customMarkerToPositionMap);
    }

    /**
     * Checks whether specified <code>range</code> is collapsed.
     * 
     * @param range the range to check
     * @return <code>true</code> if <code>range</code> is collapsed or if not collapsed
     */
    private boolean isFoldRangeCollapsed(AbstractFoldRange range) {
        boolean collapsed = false;
        try {
            IRegion originRegion = new Region(range.getOffset(), range.getLength());
            IRegion imageRegion = informationMapping.toImageRegion(originRegion);
            if (imageRegion != null) {
                collapsed =
                            originRegion.getLength() != imageRegion.getLength()
                                                        + computeCollapsedNestedRangesLength(originRegion.getOffset(),
                                                                                             originRegion.getLength());
            }
        } catch (BadLocationException e) {
            Log.error(getClass(), e);
        }
        return collapsed;
    }

    /**
     * Initializes the projection document from the master document based on the master's fragments.
     * 
     * @param masterDocument original (master) document
     */
    private void initializeProjection(IDocument masterDocument) {
        try {
            initializeDocumentInformationMapping(masterDocument);
            if (masterDocument.getLength() > 0) {
                slaveDocument.addMasterDocumentRange(0, masterDocument.getLength());
            }
            document.putTag("ProjectionDocument", slaveDocument);
        } catch (BadLocationException e) {
            Log.error(getClass(), e);
        }
    }

    /**
     * Initializes the document information mapping between the given master document and created slave document.
     */
    private void initializeDocumentInformationMapping(IDocument masterDocument) {
        initializeSlaveDocumentManager();
        slaveDocument = (ProjectionDocument)slaveDocumentManager.createSlaveDocument(masterDocument);
        informationMapping = slaveDocumentManager.createMasterSlaveMapping(slaveDocument);
    }

    /** Initializes the slave document manager. */
    private void initializeSlaveDocumentManager() {
        if (slaveDocumentManager == null) {
            slaveDocumentManager = new ProjectionDocumentManager();
        }
    }

    /**
     * Frees the given document if it is a slave document.
     * 
     * @param slave the potential slave document
     */
    private void freeSlaveDocument(IDocument slave) {
        if (slaveDocumentManager != null && slaveDocumentManager.isSlaveDocument(slave)) {
            slaveDocumentManager.freeSlaveDocument(slave);
        }
    }

    public IDocumentInformationMapping getInformationMapping() {
        return informationMapping;
    }

    public IDocument getMasterDocument() {
        return slaveDocumentManager.getMasterDocument(slaveDocument);
    }

    public ProjectionDocument getSlaveDocument() {
        return slaveDocument;
    }

    public void setFoldFinder(FoldOccurrencesFinder foldOccurrencesFinder) {
        this.foldOccurrencesFinder = foldOccurrencesFinder;
    }

    /**
     * Checks whether folding mode enabled.
     * 
     * @return <code>true</code> if folding mode is enabled, <code>false</code> otherwise
     */
    public boolean isFoldingModeEnabled() {
        return informationMapping != null;
    }

}
