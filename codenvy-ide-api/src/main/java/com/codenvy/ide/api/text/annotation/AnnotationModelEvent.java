/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.api.text.annotation;

import com.codenvy.ide.api.text.Position;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Specification of changes applied to annotation models. The event carries the
 * changed annotation model as well as added, removed, and modified annotations.
 * <p/>
 * An event can be sealed. Afterwards it can not be modified. Thus, the normal
 * process is that an empty event is created, filled with the changed
 * information, and before it is sent to the listeners, the event is sealed.
 */
public class AnnotationModelEvent {

    /** The model this event refers to. */
    private AnnotationModel annotationModel;

    /** The added annotations. */
    private Set<Annotation> addedAnnotations = new HashSet<Annotation>();

    /** The removed annotations. */
    private Map<Annotation, Position> removedAnnotations = new HashMap<Annotation, Position>();

    /** The changed annotations. */
    private Set<Annotation> changedAnnotations = new HashSet<Annotation>();

    /** Indicates that this event does not contain detailed information. */
    private boolean isWorldChange;

    /**
     * Creates a new annotation model event for the given model.
     *
     * @param model
     *         the model
     */
    public AnnotationModelEvent(AnnotationModel model) {
        this(model, true);
    }

    /**
     * Creates a new annotation model event for the given model.
     *
     * @param model
     *         the model
     * @param isWorldChange
     *         <code>true</code> if world change
     */
    public AnnotationModelEvent(AnnotationModel model, boolean isWorldChange) {
        annotationModel = model;
        this.isWorldChange = isWorldChange;
    }

    /**
     * Returns the model this event refers to.
     *
     * @return the model this events belongs to
     */
    public AnnotationModel getAnnotationModel() {
        return annotationModel;
    }

    /**
     * Adds the given annotation to the set of annotations that are reported as
     * being added from the model. If this event is considered a world change,
     * it is no longer so after this method has successfully finished.
     *
     * @param annotation
     *         the added annotation
     */
    public void annotationAdded(Annotation annotation) {
        addedAnnotations.add(annotation);
        isWorldChange = false;
    }

    /**
     * Returns the added annotations.
     *
     * @return the added annotations
     */
    public Annotation[] getAddedAnnotations() {
        int size = addedAnnotations.size();
        Annotation[] added = new Annotation[size];
        addedAnnotations.toArray(added);
        return added;
    }

    /**
     * Adds the given annotation to the set of annotations that are reported as
     * being removed from the model. If this event is considered a world
     * change, it is no longer so after this method has successfully finished.
     *
     * @param annotation
     *         the removed annotation
     */
    public void annotationRemoved(Annotation annotation) {
        annotationRemoved(annotation, null);
    }

    /**
     * Adds the given annotation to the set of annotations that are reported as
     * being removed from the model. If this event is considered a world
     * change, it is no longer so after this method has successfully finished.
     *
     * @param annotation
     *         the removed annotation
     * @param position
     *         the position of the removed annotation
     */
    public void annotationRemoved(Annotation annotation, Position position) {
        removedAnnotations.put(annotation, position);
        isWorldChange = false;
    }

    /**
     * Returns the removed annotations.
     *
     * @return the removed annotations
     */
    public Annotation[] getRemovedAnnotations() {
        int size = removedAnnotations.size();
        Annotation[] removed = new Annotation[size];
        removedAnnotations.keySet().toArray(removed);
        return removed;
    }

    /**
     * Returns the position of the removed annotation at that point in time
     * when the annotation has been removed.
     *
     * @param annotation
     *         the removed annotation
     * @return the position of the removed annotation or <code>null</code>
     */
    public Position getPositionOfRemovedAnnotation(Annotation annotation) {
        return (Position)removedAnnotations.get(annotation);
    }

    /**
     * Adds the given annotation to the set of annotations that are reported as
     * being changed from the model. If this event is considered a world
     * change, it is no longer so after this method has successfully finished.
     *
     * @param annotation
     *         the changed annotation
     */
    public void annotationChanged(Annotation annotation) {
        changedAnnotations.add(annotation);
        isWorldChange = false;
    }

    /**
     * Returns the changed annotations.
     *
     * @return the changed annotations
     */
    public Annotation[] getChangedAnnotations() {
        int size = changedAnnotations.size();
        Annotation[] changed = new Annotation[size];
        changedAnnotations.toArray(changed);
        return changed;
    }

    /**
     * Returns whether this annotation model event is empty or not. If this
     * event represents a world change, this method returns <code>false</code>
     * although the event does not carry any added, removed, or changed
     * annotations.
     *
     * @return <code>true</code> if this event is empty
     */
    public boolean isEmpty() {
        return !isWorldChange && addedAnnotations.isEmpty() && removedAnnotations.isEmpty()
               && changedAnnotations.isEmpty();
    }

    /**
     * Returns whether this annotation model events contains detailed
     * information about the modifications applied to the event annotation
     * model or whether it represents a world change. I.e. everything in the
     * model might have changed.
     *
     * @return <code>true</code> if world change, <code>false</code> otherwise
     */
    public boolean isWorldChange() {
        return isWorldChange;
    }

    /**
     * Marks this event as world change according to the given flag.
     *
     * @param isWorldChange
     *         <code>true</code> if this event is a world change, <code>false</code> otherwise
     */
    void markWorldChange(boolean isWorldChange) {
        this.isWorldChange = isWorldChange;
    }

}
