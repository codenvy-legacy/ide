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

import java.util.ArrayList;
import java.util.List;

import com.codenvy.ide.api.text.Position;
import com.codenvy.ide.api.text.annotation.Annotation;
import com.codenvy.ide.jseditor.client.document.DocumentHandle;
import com.codenvy.ide.jseditor.client.document.UseDocumentHandle;
import com.codenvy.ide.jseditor.client.text.TextPosition;
import com.codenvy.ide.jseditor.client.texteditor.HasGutter;
import com.codenvy.ide.ui.Tooltip;
import com.codenvy.ide.ui.menu.PositionController;
import com.codenvy.ide.util.loging.Log;

import elemental.dom.Element;
import elemental.events.Event;
import elemental.events.EventListener;

/**
 * Renderer for annotation marks in gutter (on the left margin of the text).
 */
public class GutterAnnotationRenderer implements AnnotationModelHandler, ClearAnnotationModelHandler, UseDocumentHandle {
    /** Logical identifer for the annotation gutter. */
    private static final String ANNOTATION_GUTTER = "annotation";

    /** The component responsible for gutter handling. */
    private HasGutter hasGutter;

    /** The handle on the component. */
    private DocumentHandle documentHandle;

    /**
     * Sets the component responsible for gutter handling.
     * @param hasGutter the component
     */
    public void setHasGutter(final HasGutter hasGutter) {
        this.hasGutter = hasGutter;
    }


    @Override
    public void onAnnotationModel(final AnnotationModelEvent event) {
        // remove removed and changed annotations
        for (final Annotation annotation : event.getRemovedAnnotations()) {
            Log.info(GutterAnnotationRenderer.class, "Remove annotation: " + annotation);
            removeAnnotationItem(event, annotation);
        }
        for (final Annotation annotation : event.getChangedAnnotations()) {
            Log.info(GutterAnnotationRenderer.class, "Remove changed annotation: " + annotation);
            removeAnnotationItem(event, annotation);
        }
        // add new and changed (new version) annotation
        for (final Annotation annotation : event.getAddedAnnotations()) {
            Log.info(GutterAnnotationRenderer.class, "Add annotation: " + annotation);
            addAnnotationItem(event.getAnnotationModel(), annotation);
        }
        for (final Annotation annotation : event.getChangedAnnotations()) {
            Log.info(GutterAnnotationRenderer.class, "Add back changed annotation: " + annotation);
            addAnnotationItem(event.getAnnotationModel(), annotation);
        }
    }

    private void removeAnnotationItem(final AnnotationModelEvent event, final Annotation annotation) {
        final Position position = event.getPositionOfRemovedAnnotation(annotation);
        final TextPosition textPosition = this.documentHandle.getDocument().getPositionFromIndex(position.getOffset());
        final Element annotationItem = this.hasGutter.getGutterItem(textPosition.getLine(), ANNOTATION_GUTTER);
        if (AnnotationGroupImpl.isAnnotation(annotationItem)) {
            final AnnotationGroup group = AnnotationGroupImpl.create(annotationItem);
            group.removeAnnotation(annotation, position.getOffset());
            if (group.getAnnotationCount() != 0) {
                return;
            }
        }
        // else
        this.hasGutter.removeGutterItem(textPosition.getLine(), ANNOTATION_GUTTER);
    }

    private void addAnnotationItem(final AnnotationModel model, final Annotation annotation) {
        final Position position = model.getPosition(annotation);
        if (position == null) {
            Log.warn(GutterAnnotationRenderer.class, "No position for annotation " + annotation);
            return;
        }
        final TextPosition textPosition = this.documentHandle.getDocument().getPositionFromIndex(position.getOffset());

        final Element annotationItem = this.hasGutter.getGutterItem(textPosition.getLine(),
                                                                    ANNOTATION_GUTTER);

        AnnotationGroup annotationGroup;
        if (!AnnotationGroupImpl.isAnnotation(annotationItem)) {
            Log.info(GutterAnnotationRenderer.class, "Create new annotation group for line " + textPosition.getLine());
            final AnnotationGroup newGroup = AnnotationGroupImpl.create();
            newGroup.getElement().addEventListener(Event.MOUSEOVER, new EventListener() {
                @Override
                public void handleEvent(final Event evt) {
                    showToolTip(newGroup, textPosition.getLine());
                }
            }, false);

            this.hasGutter.addGutterItem(textPosition.getLine(), ANNOTATION_GUTTER,
                                                                 newGroup.getElement());
            annotationGroup = newGroup;
        } else {
            Log.info(GutterAnnotationRenderer.class, "Reuse annotation group for line " + textPosition.getLine());
            annotationGroup = AnnotationGroupImpl.create(annotationItem);
        }
        annotationGroup.addAnnotation(annotation, position.getOffset());

    }

    private void showToolTip(AnnotationGroup item, int line) {
        final List<String> messages = new ArrayList<>();
        for (final String message : item.getMessages()) {
            messages.add(message);
        }


        Tooltip tooltip = null;
        if (messages.size() == 1) {
            tooltip = Tooltip.create(item.getElement(),
                                     PositionController.VerticalAlign.MIDDLE,
                                     PositionController.HorizontalAlign.RIGHT,
                                     messages.get(0));

        } else if (messages.size() > 1) {
            String[] messagesArray = new String[messages.size()];
            messagesArray = messages.toArray(messagesArray);
            tooltip = ListTooltipFactory.create(item.getElement(),
                                                "Multiple markers on this line:",
                                                 PositionController.VerticalAlign.MIDDLE,
                                                 PositionController.HorizontalAlign.RIGHT,
                                                 messagesArray);
        }
        if (tooltip != null) {
            tooltip.show();
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
        this.hasGutter.clearGutter(ANNOTATION_GUTTER);
    }
}
