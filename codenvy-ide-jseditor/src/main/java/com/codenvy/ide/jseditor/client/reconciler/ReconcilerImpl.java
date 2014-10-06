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
package com.codenvy.ide.jseditor.client.reconciler;

import java.util.List;

import com.codenvy.ide.api.text.Region;
import com.codenvy.ide.api.text.RegionImpl;
import com.codenvy.ide.api.text.TypedRegion;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.collections.StringMap.IterationCallback;
import com.codenvy.ide.jseditor.client.document.DocumentHandle;
import com.codenvy.ide.jseditor.client.document.EmbeddedDocument;
import com.codenvy.ide.jseditor.client.events.DocumentChangeEvent;
import com.codenvy.ide.jseditor.client.partition.DocumentPartitioner;
import com.codenvy.ide.util.executor.BasicIncrementalScheduler;
import com.google.gwt.user.client.Timer;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;


/**
 * Default implementation of {@link Reconciler}.
 */
public class ReconcilerImpl implements Reconciler {

    private static final int DELAY = 1000;


    private final StringMap<ReconcilingStrategy> strategies;

    private final String partition;

    private final DocumentPartitioner partitioner;

    private DirtyRegionQueue dirtyRegionQueue;

    private final Timer timer = new Timer() {

        @Override
        public void run() {
            final DirtyRegion region = dirtyRegionQueue.removeNextDirtyRegion();
            process(region);
        }
    };

    private DocumentHandle documentHandle;

    @AssistedInject
    public ReconcilerImpl(@Assisted final String partition,
                          @Assisted final BasicIncrementalScheduler scheduler,
                          @Assisted final DocumentPartitioner partitioner) {
        this.partition = partition;
        strategies = Collections.createStringMap();
        this.partitioner = partitioner;
    }

    private void reconcilerDocumentChanged() {
        strategies.iterate(new IterationCallback<ReconcilingStrategy>() {

            @Override
            public void onIteration(final String key, final ReconcilingStrategy value) {
                value.setDocument(documentHandle.getDocument());
            }
        });
        timer.cancel();
        timer.schedule(DELAY);
    }

    @Override
    public void install() {
        this.dirtyRegionQueue = new DirtyRegionQueue();
        reconcilerDocumentChanged();
    }

    @Override
    public void uninstall() {
        timer.cancel();
    }

    /**
     * Processes a dirty region. If the dirty region is <code>null</code> the whole document is consider being dirty. The dirty region is
     * partitioned by the document and each partition is handed over to a reconciling strategy registered for the partition's content type.
     * 
     * @param dirtyRegion the dirty region to be processed
     */
    protected void process(final DirtyRegion dirtyRegion) {

        Region region = dirtyRegion;

        if (region == null) {
            region = new RegionImpl(0, getDocument().getContents().length());
        }

        final List<TypedRegion> regions = computePartitioning(region.getOffset(),
                                                              region.getLength());

        for (final TypedRegion r : regions) {
            final ReconcilingStrategy strategy = getReconcilingStrategy(r.getType());
            if (strategy == null) {
                continue;
            }

            if (dirtyRegion != null) {
                strategy.reconcile(dirtyRegion, r);
            } else {
                strategy.reconcile(r);
            }
        }
    }

    /**
     * Computes and returns the partitioning for the given region of the input document of the reconciler's connected text viewer.
     * 
     * @param offset the region offset
     * @param length the region length
     * @return the computed partitioning
     */
    private List<TypedRegion> computePartitioning(final int offset, final int length) {
        return partitioner.computePartitioning(offset, length);
    }

    /**
     * Returns the input document of the text view this reconciler is installed on.
     * 
     * @return the reconciler document
     */
    protected EmbeddedDocument getDocument() {
        return documentHandle.getDocument();
    }

    /**
     * Creates a dirty region for a document event and adds it to the queue.
     * 
     * @param event the document event for which to create a dirty region
     */
    private void createDirtyRegion(final DocumentChangeEvent event) {
        if (event.getLength() == 0 && event.getText() != null) {
            // Insert
            dirtyRegionQueue.addDirtyRegion(new DirtyRegion(event.getOffset(),
                                                            event.getText().length(),
                                                            DirtyRegion.INSERT,
                                                            event.getText()));

        } else if (event.getText() == null || event.getText().length() == 0) {
            // Remove
            dirtyRegionQueue.addDirtyRegion(new DirtyRegion(event.getOffset(),
                                                            event.getLength(),
                                                            DirtyRegion.REMOVE,
                                                            null));

        } else {
            // Replace (Remove + Insert)
            dirtyRegionQueue.addDirtyRegion(new DirtyRegion(event.getOffset(),
                                                            event.getLength(),
                                                            DirtyRegion.REMOVE,
                                                            null));
            dirtyRegionQueue.addDirtyRegion(new DirtyRegion(event.getOffset(),
                                                            event.getText().length(),
                                                            DirtyRegion.INSERT,
                                                            event.getText()));
        }
    }

    @Override
    public ReconcilingStrategy getReconcilingStrategy(final String contentType) {
        return strategies.get(contentType);
    }

    @Override
    public String getDocumentPartitioning() {
        return partition;
    }

    public void addReconcilingStrategy(final String contentType, final ReconcilingStrategy strategy) {
        strategies.put(contentType, strategy);
    }

    @Override
    public void onDocumentChange(final DocumentChangeEvent event) {
        if (documentHandle == null || !documentHandle.isSameAs(event.getDocument())) {
            return;
        }
        createDirtyRegion(event);
        timer.cancel();
        timer.schedule(DELAY);
    }

    @Override
    public void setDocumentHandle(final DocumentHandle handle) {
        this.documentHandle = handle;
    }

    @Override
    public DocumentHandle getDocumentHandle() {
        return this.documentHandle;
    }

}
