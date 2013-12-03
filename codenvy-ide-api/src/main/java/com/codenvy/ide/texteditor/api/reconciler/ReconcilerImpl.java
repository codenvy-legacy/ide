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
package com.codenvy.ide.texteditor.api.reconciler;

import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.JsonStringMap;
import com.codenvy.ide.collections.JsonStringMap.IterationCallback;
import com.codenvy.ide.text.*;
import com.codenvy.ide.texteditor.api.TextEditorPartView;
import com.codenvy.ide.texteditor.api.TextInputListener;
import com.codenvy.ide.util.executor.BasicIncrementalScheduler;
import com.google.gwt.user.client.Timer;


/**
 * Default implementation of {@link Reconciler}
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class ReconcilerImpl implements Reconciler {

    private static final int DELAY = 2000;

    protected class Listener implements TextInputListener, DocumentListener {

        /** {@inheritDoc} */
        @Override
        public void inputDocumentChanged(Document oldDocument, Document newDocument) {
            newDocument.addDocumentListener(this);
            document = newDocument;
            reconcilerDocumentChanged();
        }

        /** {@inheritDoc} */
        @Override
        public void documentAboutToBeChanged(DocumentEvent event) {
        }

        /** {@inheritDoc} */
        @Override
        public void documentChanged(DocumentEvent event) {
            createDirtyRegion(event);
            timer.cancel();
            timer.schedule(DELAY);
        }

    }

    private JsonStringMap<ReconcilingStrategy> strategys;

    private String partition;

    private Listener listener;

    private TextEditorPartView textEditor;

    private DirtyRegionQueue dirtyRegionQueue;

    //   //TODO replace with timer
    private Timer timer = new Timer() {

        @Override
        public void run() {

            DirtyRegion region = dirtyRegionQueue.removeNextDirtyRegion();
            process(region);
        }
    };

    private Document document;

    /**
     *
     */
    public ReconcilerImpl(String partition, BasicIncrementalScheduler scheduler) {
        this.partition = partition;
        strategys = Collections.createStringMap();
    }

    /**
     *
     */
    private void reconcilerDocumentChanged() {
        strategys.iterate(new IterationCallback<ReconcilingStrategy>() {

            @Override
            public void onIteration(String key, ReconcilingStrategy value) {
                value.setDocument(document);
            }
        });
        timer.cancel();
        timer.schedule(DELAY);
    }

    /** @see com.codenvy.ide.texteditor.api.reconciler.Reconciler#install(com.codenvy.ide.texteditor.api.TextEditorPartView) */
    @Override
    public void install(TextEditorPartView view) {
        this.textEditor = view;
        dirtyRegionQueue = new DirtyRegionQueue();
        listener = new Listener();
        view.addTextInputListener(listener);
    }

    /** @see com.codenvy.ide.texteditor.api.reconciler.Reconciler#uninstall() */
    @Override
    public void uninstall() {
        if (listener != null) {
            textEditor.removeTextInputListener(listener);
            listener = null;
        }
        timer.cancel();
    }

    /**
     * Processes a dirty region. If the dirty region is <code>null</code> the whole
     * document is consider being dirty. The dirty region is partitioned by the
     * document and each partition is handed over to a reconciling strategy registered
     * for the partition's content type.
     *
     * @param dirtyRegion
     *         the dirty region to be processed
     * @see AbstractReconciler#process(DirtyRegion)
     */
    protected void process(DirtyRegion dirtyRegion) {

        Region region = dirtyRegion;

        if (region == null)
            region = new RegionImpl(0, getDocument().getLength());

        TypedRegion[] regions = computePartitioning(region.getOffset(), region.getLength());

        for (int i = 0; i < regions.length; i++) {
            TypedRegion r = regions[i];
            ReconcilingStrategy s = getReconcilingStrategy(r.getType());
            if (s == null)
                continue;

            if (dirtyRegion != null)
                s.reconcile(dirtyRegion, r);
            else
                s.reconcile(r);
        }
    }

    /**
     * Computes and returns the partitioning for the given region of the input document
     * of the reconciler's connected text viewer.
     *
     * @param offset
     *         the region offset
     * @param length
     *         the region length
     * @return the computed partitioning
     */
    private TypedRegion[] computePartitioning(int offset, int length) {
        TypedRegion[] regions = null;
        try {
            regions = TextUtilities.computePartitioning(getDocument(), getDocumentPartitioning(), offset, length, true);
        } catch (BadLocationException x) {
            regions = new TypedRegion[]{new TypedRegionImpl(offset, length, Document.DEFAULT_CONTENT_TYPE)};
        }
        return regions;
    }

    /**
     * Returns the input document of the text view this reconciler is installed on.
     *
     * @return the reconciler document
     */
    protected Document getDocument() {
        return document;
    }

    /**
     * Creates a dirty region for a document event and adds it to the queue.
     *
     * @param e
     *         the document event for which to create a dirty region
     */
    private void createDirtyRegion(DocumentEvent e) {
        if (e.getLength() == 0 && e.getText() != null) {
            // Insert
            dirtyRegionQueue.addDirtyRegion(new DirtyRegion(e.getOffset(), e.getText().length(), DirtyRegion.INSERT, e
                    .getText()));

        } else if (e.getText() == null || e.getText().length() == 0) {
            // Remove
            dirtyRegionQueue.addDirtyRegion(new DirtyRegion(e.getOffset(), e.getLength(), DirtyRegion.REMOVE, null));

        } else {
            // Replace (Remove + Insert)
            dirtyRegionQueue.addDirtyRegion(new DirtyRegion(e.getOffset(), e.getLength(), DirtyRegion.REMOVE, null));
            dirtyRegionQueue.addDirtyRegion(new DirtyRegion(e.getOffset(), e.getText().length(), DirtyRegion.INSERT, e
                    .getText()));
        }
    }

    /** @see com.codenvy.ide.texteditor.api.reconciler.Reconciler#getReconcilingStrategy(java.lang.String) */
    @Override
    public ReconcilingStrategy getReconcilingStrategy(String contentType) {
        return strategys.get(contentType);
    }

    /** @see com.codenvy.ide.texteditor.api.reconciler.Reconciler#getDocumentPartitioning() */
    @Override
    public String getDocumentPartitioning() {
        return partition;
    }

    public void addReconcilingStrategy(String contentType, ReconcilingStrategy strategy) {
        strategys.put(contentType, strategy);
    }
}
