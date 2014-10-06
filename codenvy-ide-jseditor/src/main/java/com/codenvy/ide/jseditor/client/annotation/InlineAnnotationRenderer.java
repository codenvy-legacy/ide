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
package com.codenvy.ide.jseditor.client.annotation;

import java.util.IdentityHashMap;

import com.codenvy.ide.api.text.Position;
import com.codenvy.ide.api.text.annotation.Annotation;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.jseditor.client.document.DocumentHandle;
import com.codenvy.ide.jseditor.client.document.UseDocumentHandle;
import com.codenvy.ide.jseditor.client.text.TextPosition;
import com.codenvy.ide.jseditor.client.text.TextRange;
import com.codenvy.ide.jseditor.client.texteditor.HasTextMarkers;
import com.codenvy.ide.jseditor.client.texteditor.HasTextMarkers.MarkerRegistration;
import com.codenvy.ide.util.loging.Log;

/**
 * Render the inline marks on the text.
 */
public class InlineAnnotationRenderer implements AnnotationModelHandler, ClearAnnotationModelHandler, UseDocumentHandle {

    /** The currently show markers. */
    private final IdentityHashMap<Annotation, MarkerRegistration> markers = new IdentityHashMap<>();

    /** The component that handles markers. */
    private HasTextMarkers hasTextMarkers;

    /** An handle to the document. */
    private DocumentHandle documentHandle;

    /**
     * Sets the component that handles markers.
     *
     * @param hasTextMarkers the new component
     */
    public void setHasTextMarkers(final HasTextMarkers hasTextMarkers) {
        this.hasTextMarkers = hasTextMarkers;
    }

    @Override
    public void onAnnotationModel(final AnnotationModelEvent event) {
        // remove removed and changed annotations
        for (final Annotation annotation : event.getRemovedAnnotations()) {
            removeAnnotationItem(annotation);
        }
        for (final Annotation annotation : event.getChangedAnnotations()) {
            removeAnnotationItem(annotation);
        }

        // add new and changed (new version) annotation

        final StringMap<String> decorations = event.getAnnotationModel().getAnnotationDecorations();

        for (final Annotation annotation : event.getAddedAnnotations()) {
            addAnnotationItem(event.getAnnotationModel(), annotation, decorations);
        }
        for (final Annotation annotation : event.getChangedAnnotations()) {
            addAnnotationItem(event.getAnnotationModel(), annotation, decorations);
        }
    }

    /**
     * Add an inline annotation.
     *
     * @param annotationModel the annotation model
     * @param annotation the annotation to add
     * @param decorations the available decorations
     */
    private void addAnnotationItem(AnnotationModel annotationModel, Annotation annotation, StringMap<String> decorations) {
        if (this.hasTextMarkers != null) {
            final String className = decorations.get(annotation.getType());
            if (className == null) {
                return;
            }

            final Position position = annotationModel.getPosition(annotation);
            if (position == null) {
                Log.warn(InlineAnnotationRenderer.class, "Can't add annotation with no position");
                return;
            }

            final TextPosition from = documentHandle.getDocument().getPositionFromIndex(position.getOffset());
            final TextPosition to = documentHandle.getDocument().getPositionFromIndex(position.getOffset() + position.getLength());

            final MarkerRegistration registration = this.hasTextMarkers.addMarker(new TextRange(from, to), className);
            if (registration != null) {
                this.markers.put(annotation, registration);
            }
        }
    }

    /**
     * Remove an annotation.
     *
     * @param annotation the annotation to remove
     */
    private void removeAnnotationItem(final Annotation annotation) {
        final MarkerRegistration marker = this.markers.get(annotation);
        if (marker != null) {
            marker.clearMark();
        } else {
            Log.warn(InlineAnnotationRenderer.class, "Inline marker with no handle: " + annotation);
        }
    }

    @Override
    public void setDocumentHandle(final DocumentHandle handle) {
        this.documentHandle = handle;
    }

    @Override
    public DocumentHandle getDocumentHandle() {
        return this.documentHandle;
    }

    @Override
    public void onClearModel(final ClearAnnotationModelEvent event) {
        for (final MarkerRegistration registration: this.markers.values()) {
            registration.clearMark();
        }
    }
}
