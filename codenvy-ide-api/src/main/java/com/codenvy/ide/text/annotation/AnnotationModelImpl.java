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
package com.codenvy.ide.text.annotation;

import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.runtime.Assert;
import com.codenvy.ide.text.BadLocationException;
import com.codenvy.ide.text.BadPositionCategoryException;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.text.DocumentEvent;
import com.codenvy.ide.text.DocumentListener;
import com.codenvy.ide.text.Position;
import com.codenvy.ide.util.ListenerManager;
import com.codenvy.ide.util.ListenerManager.Dispatcher;
import com.codenvy.ide.util.ListenerRegistrar.Remover;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Default implementation of {@link AnnotationModel}
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class AnnotationModelImpl implements AnnotationModel {

    final class InternalDispatcher implements Dispatcher<AnnotationModelListener> {

        @Override
        public void dispatch(AnnotationModelListener listener) {
            listener.modelChanged(modelEvent);
        }
    }

    /** Iterator that returns the annotations for a given region. */
    static final class RegionIterator implements Iterator<Annotation> {

        private final Iterator<Annotation> parentIterator;

        private final boolean canEndAfter;

        private final boolean canStartBefore;

        private final AnnotationModel model;

        private Annotation next;

        private Position region;

        /**
         * Iterator that returns all annotations from the parent iterator which
         * have a position in the given model inside the given region.
         *
         * @param parentIterator
         *         iterator containing all annotations
         * @param model
         *         the model to use to retrieve positions from for each
         *         annotation
         * @param offset
         *         start position of the region
         * @param length
         *         length of the region
         * @param canStartBefore
         *         include annotations starting before region
         * @param canEndAfter
         *         include annotations ending after region
         */
        public RegionIterator(Iterator<Annotation> parentIterator, AnnotationModel model, int offset, int length,
                              boolean canStartBefore, boolean canEndAfter) {
            this.parentIterator = parentIterator;
            this.model = model;
            this.region = new Position(offset, length);
            this.canEndAfter = canEndAfter;
            this.canStartBefore = canStartBefore;
            next = findNext();
        }

        /** {@inheritDoc} */
        public boolean hasNext() {
            return next != null;
        }

        /** {@inheritDoc} */
        public Annotation next() {
            if (!hasNext())
                throw new NoSuchElementException();

            Annotation result = next;
            next = findNext();
            return result;
        }

        /** {@inheritDoc} */
        public void remove() {
            throw new UnsupportedOperationException();
        }

        private Annotation findNext() {
            while (parentIterator.hasNext()) {
                Annotation next = parentIterator.next();
                Position position = model.getPosition(next);
                if (position != null) {
                    int offset = position.getOffset();
                    if (isWithinRegion(offset, position.getLength()))
                        return next;
                }
            }
            return null;
        }

        private boolean isWithinRegion(int start, int length) {
            if (canStartBefore && canEndAfter)
                return region.overlapsWith(start, length);
            else if (canStartBefore)
                return region.includes(start + length - (length > 0 ? 1 : 0));
            else if (canEndAfter)
                return region.includes(start);
            else
                return region.includes(start) && region.includes(start + length - (length > 0 ? 1 : 0));
        }
    }

    /**
     * An iterator iteration over a Positions and mapping positions to
     * annotations using a provided map if the provided map contains the element.
     *
     */
    /**
     * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
     * @version $Id:
     *
     */
    /**
     * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
     * @version $Id:
     */
    private static final class AnnotationsInterator implements Iterator<Annotation> {

        private Annotation next;

        private final Position[] positions;

        private int index;

        private final Map<Position, Annotation> map;

        /**
         * @param positions
         *         positions to iterate over
         * @param map
         *         a map to map positions to annotations
         */
        public AnnotationsInterator(Position[] positions, Map<Position, Annotation> map) {
            this.positions = positions;
            this.index = 0;
            this.map = map;
            next = findNext();
        }

        /** {@inheritDoc} */
        public boolean hasNext() {
            return next != null;
        }

        /** {@inheritDoc} */
        public Annotation next() {
            Annotation result = next;
            next = findNext();
            return result;
        }

        /** {@inheritDoc} */
        public void remove() {
            throw new UnsupportedOperationException();
        }

        private Annotation findNext() {
            while (index < positions.length) {
                Position position = positions[index];
                index++;
                if (map.containsKey(position))
                    return map.get(position);
            }

            return null;
        }
    }

    private ListenerManager<AnnotationModelListener> listenerManager = ListenerManager.create();

    private Document document;

    /** The list of managed annotations */
    protected Map<Annotation, Position> annotations;

    /** The map which maps {@link Position} to {@link Annotation}. */
    private IdentityHashMap<Position, Annotation> positions;

    /** The current annotation model event. */
    private AnnotationModelEvent modelEvent;

    private InternalDispatcher dispatcher;

    private DocumentListener documentListener;

    private boolean documentChanged;

    /**
     *
     */
    public AnnotationModelImpl() {
        annotations = new HashMap<Annotation, Position>(10);
        positions = new IdentityHashMap<Position, Annotation>(10);
        dispatcher = new InternalDispatcher();
        documentListener = new DocumentListener() {

            public void documentAboutToBeChanged(DocumentEvent event) {
            }

            public void documentChanged(DocumentEvent event) {
                documentChanged = true;
            }
        };
    }

    /** {@inheritDoc} */
    @Override
    public Remover addAnnotationModelListener(AnnotationModelListener listener) {
        return listenerManager.add(listener);
    }

    /** {@inheritDoc} */
    @Override
    public void connect(Document document) {
        this.document = document;
        document.addDocumentListener(documentListener);
    }

    /** {@inheritDoc} */
    @Override
    public void disconnect(Document document) {
        if (document == this.document) {
            document.removeDocumentListener(documentListener);
            this.document = null;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void addAnnotation(Annotation annotation, Position position) {
        addAnnotation(annotation, position, true);
    }

    protected void addAnnotation(Annotation annotation, Position position, boolean fireEvent) {
        try {
            document.addPosition(position);
            annotations.put(annotation, position);
            positions.put(position, annotation);
            getAnnotationModelEvent().annotationAdded(annotation);
            if (fireEvent)
                fireModelChanged();
        } catch (BadLocationException e) {
            //ignore
        }
    }

    /**
     * Returns the current annotation model event. This is the event that will be sent out
     * when calling <code>fireModelChanged</code>.
     */
    protected final AnnotationModelEvent getAnnotationModelEvent() {
        if (modelEvent == null) {
            modelEvent = createAnnotationModelEvent();
            modelEvent.markWorldChange(false);
        }
        return modelEvent;
    }

    /**
     * Creates and returns a new annotation model event. Subclasses may override.
     *
     * @return a new and empty annotation model event
     */
    protected AnnotationModelEvent createAnnotationModelEvent() {
        return new AnnotationModelEvent(this);
    }

    /**
     * Informs all annotation model listeners that this model has been changed
     * as described in the annotation model event.
     */
    protected void fireModelChanged() {
        if (modelEvent.isEmpty())
            return;

        listenerManager.dispatch(dispatcher);
    }

    /** {@inheritDoc} */
    @Override
    public void removeAnnotation(Annotation annotation) {
        if (annotations.containsKey(annotation)) {

            Position p = null;
            p = (Position)annotations.get(annotation);
            if (document != null) {
                removePosition(document, p);
            }

            annotations.remove(annotation);
            positions.remove(p);

            getAnnotationModelEvent().annotationRemoved(annotation, p);

            fireModelChanged();
        }
    }

    /** {@inheritDoc} */
    @Override
    public Iterator<Annotation> getAnnotationIterator() {
        return annotations.keySet().iterator();
    }

    /** {@inheritDoc} */
    @Override
    public Iterator<Annotation> getAnnotationIterator(int offset, int length, boolean canStartBefore, boolean canEndAfter) {
        cleanup(true, true);

        try {
            Position[] positions =
                    document.getPositions(Document.DEFAULT_CATEGORY, offset, length, canStartBefore, canEndAfter);
            return new AnnotationsInterator(positions, this.positions);
        } catch (BadPositionCategoryException e) {
            //can not happen
            Assert.isTrue(false);
            return null;
        }
    }

    /**
     * Removes all annotations from the model whose associated positions have been
     * deleted. If requested inform all model listeners about the change. If requested
     * a new thread is created for the notification of the model listeners.
     *
     * @param fireModelChanged
     *         indicates whether to notify all model listeners
     * @param forkNotification
     *         <code>true</code> iff notification should be done in a new thread
     */
    private void cleanup(boolean fireModelChanged, boolean forkNotification) {
        if (documentChanged) {
            documentChanged = false;

            ArrayList<Annotation> deleted = new ArrayList<Annotation>();
            Iterator<Annotation> e = getAnnotationIterator();
            while (e.hasNext()) {
                Annotation a = e.next();
                Position p = annotations.get(a);
                if (p == null || p.isDeleted())
                    deleted.add(a);
            }

            if (fireModelChanged && forkNotification) {
                removeAnnotations(deleted, false, false);
                if (modelEvent != null) {
                    Scheduler.get().scheduleDeferred(new ScheduledCommand() {

                        @Override
                        public void execute() {
                            fireModelChanged();
                        }
                    });
                }
            } else
                removeAnnotations(deleted, fireModelChanged, false);
        }
    }

    /**
     * Removes the given annotations from this model. If requested all
     * annotation model listeners will be informed about this change.
     * <code>modelInitiated</code> indicates whether the deletion has
     * been initiated by this model or by one of its clients.
     *
     * @param annotations
     *         the annotations to be removed
     * @param fireModelChanged
     *         indicates whether to notify all model listeners
     * @param modelInitiated
     *         indicates whether this changes has been initiated by this model
     */
    protected void removeAnnotations(List<? extends Annotation> annotations, boolean fireModelChanged,
                                     boolean modelInitiated) {
        if (annotations.size() > 0) {
            Iterator<? extends Annotation> e = annotations.iterator();
            while (e.hasNext())
                removeAnnotation(e.next(), false);

            if (fireModelChanged)
                fireModelChanged();
        }
    }

    /**
     * Removes the given position from the default position category of the
     * given document.
     *
     * @param document
     *         the document to which to add the position
     * @param position
     *         the position to add
     */
    protected void removePosition(Document document, Position position) {
        if (document != null)
            document.removePosition(position);
    }

    /**
     * Removes the given annotation from the annotation model.
     * If requested inform all model change listeners about this change.
     *
     * @param annotation
     *         the annotation to be removed
     * @param fireModelChanged
     *         indicates whether to notify all model listeners
     */
    protected void removeAnnotation(Annotation annotation, boolean fireModelChanged) {
        if (annotations.containsKey(annotation)) {

            Position p = null;
            p = annotations.get(annotation);
            if (document != null) {
                removePosition(document, p);
            }

            annotations.remove(annotation);
            positions.remove(p);
            getAnnotationModelEvent().annotationRemoved(annotation, p);

            if (fireModelChanged)
                fireModelChanged();
        }
    }

    /** {@inheritDoc} */
    @Override
    public Position getPosition(Annotation annotation) {
        Position position = (Position)annotations.get(annotation);
        return position;
    }

    /** {@inheritDoc} */
    @Override
    public StringMap<String> getAnnotationDecorations() {
        return Collections.createStringMap();
    }

    @Override
    public StringMap<String> getAnnotationStyle() {
        return Collections.createStringMap();
    }

}
