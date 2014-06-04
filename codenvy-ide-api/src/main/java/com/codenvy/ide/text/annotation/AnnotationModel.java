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
package com.codenvy.ide.text.annotation;

import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.text.Position;
import com.codenvy.ide.util.ListenerRegistrar.Remover;

import java.util.Iterator;

/**
 * This interface defines the model for managing annotations attached to a document.
 * The model maintains a set of annotations for a given document and notifies registered annotation
 * model listeners about annotation model changes. It also provides methods
 * for querying the current position of an annotation managed
 * by this model.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public interface AnnotationModel {

    /**
     * Registers the annotation model listener with this annotation model.
     * After registration listener is informed about each change of this model.
     * If the listener is already registered nothing happens.
     *
     * @param listener
     *         the listener to be registered, may not be <code>null</code>
     * @return Remover that remove added listener
     */
    Remover addAnnotationModelListener(AnnotationModelListener listener);

    /**
     * Connects the annotation model to a document. The annotations managed
     * by this model must subsequently update according to the changes applied
     * to the document. Once an annotation model is connected to a document,
     * all further <code>connect</code> calls must mention the document the
     * model is already connected to. An annotation model primarily uses
     * <code>connect</code> and <code>disconnect</code> for reference counting
     * the document. Reference counting frees the clients from keeping tracker
     * whether a model has already been connected to a document.
     *
     * @param document
     *         the document the model gets connected to,
     *         may not be <code>null</code>
     * @see #disconnect(Document)
     */
    void connect(Document document);

    /**
     * Disconnects this model from a document. After that, document changes no longer matter.
     * An annotation model may only be disconnected from a document to which it has been
     * connected before. If the model reference counts the connections to a document,
     * the connection to the document may only be terminated if the reference count does
     * down to 0.
     *
     * @param document
     *         the document the model gets disconnected from,
     *         may not be <code>null</code>
     * @see #connect(Document) for further specification details
     */
    void disconnect(Document document);

    /**
     * Adds a annotation to this annotation model. The annotation is associated with
     * with the given position which describes the range covered by the annotation.
     * All registered annotation model listeners are informed about the change.
     * If the model is connected to a document, the position is automatically
     * updated on document changes. If the annotation is already managed by
     * this annotation model or is not a valid position in the connected document
     * nothing happens.
     *
     * @param annotation
     *         the annotation to add, may not be <code>null</code>
     * @param position
     *         the position describing the range covered by this annotation,
     *         may not be <code>null</code>
     */
    void addAnnotation(Annotation annotation, Position position);

    /**
     * Removes the given annotation from the model. I.e. the annotation is no
     * longer managed by this model. The position associated with the annotation
     * is no longer updated on document changes. If the annotation is not
     * managed by this model, nothing happens.
     *
     * @param annotation
     *         the annotation to be removed from this model,
     *         may not be <code>null</code>
     */
    void removeAnnotation(Annotation annotation);

    /**
     * Returns all annotations managed by this model.
     *
     * @return all annotations managed by this model (element type: {@link Annotation})
     */
    Iterator<Annotation> getAnnotationIterator();

    /**
     * Returns an iterator over all annotations managed by this model that are
     * inside the given region.
     *
     * @param offset
     *         the start position of the region, must be >= 0
     * @param length
     *         the length of the region, must be >= 0
     * @param canStartBefore
     *         if <code>true</code> then annotations are included
     *         which start before the region if they end at or after the region's start
     * @param canEndAfter
     *         if <code>true</code> then annotations are included
     *         which end after the region if they start at or before the region's end
     * @return all annotations inside the region managed by this model (element type: {@link Annotation})
     */
    Iterator<Annotation> getAnnotationIterator(int offset, int length, boolean canStartBefore, boolean canEndAfter);

    /**
     * Returns the position associated with the given annotation.
     *
     * @param annotation
     *         the annotation whose position should be returned
     * @return the position of the given annotation or <code>null</code> if no
     *         associated annotation exists
     */
    Position getPosition(Annotation annotation);

    /**
     * Returns decorations (CSS styles) mapped to Annotation type
     *
     * @return all decorations
     */
    StringMap<String> getAnnotationDecorations();

    /**
     * Returns styles (CSS styles) mapped to Annotation type
     *
     * @return all decorations
     */
    StringMap<String> getAnnotationStyle();
}
