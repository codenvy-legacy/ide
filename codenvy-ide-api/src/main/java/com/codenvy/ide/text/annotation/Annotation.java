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

import elemental.dom.Element;

import com.google.gwt.resources.client.ImageResource;

import org.vectomatic.dom.svg.ui.SVGResource;

/**
 * Annotation managed by an{@link AnnotationModel}.
 * <p>
 * Annotations are typed, can have an associated text and can be marked as persistent and
 * deleted. Annotations which are not explicitly initialized with an annotation
 * type are of type <code>"com.codenvy.ide.text.annotation.unknown"</code>.
 * </p>
 */
public class Annotation {

    /**
     * Constant for unknown annotation types.<p>
     * Value: <code>"com.codenvy.ide.text.annotation.unknown"</code>
     */
    public final static String TYPE_UNKNOWN = "com.codenvy.ide.text.annotation.unknown"; //$NON-NLS-1$

    /** The type of this annotation. */
    private String type;

    /** Indicates whether this annotation is persistent or not. */
    private boolean isPersistent = false;

    /** Indicates whether this annotation is marked as deleted or not. */
    private boolean markedAsDeleted = false;

    /** The text associated with this annotation. */
    private String text;

    /** Annotation drawing layer. */
    protected int layer;

    /** Image associated with this annotation */
    protected ImageResource image;

    /** Image associated with this annotation */
    protected SVGResource imageSVG;

    /** Element associated with this annotation */
    protected Element imageElement;

    /** Creates a new annotation that is not persistent and type less. */
    protected Annotation() {
        this(null, false, null);
    }

    /**
     * Creates a new annotation with the given properties.
     *
     * @param type
     *         the unique name of this annotation type
     * @param isPersistent
     *         <code>true</code> if this annotation is
     *         persistent, <code>false</code> otherwise
     * @param text
     *         the text associated with this annotation
     */
    public Annotation(String type, boolean isPersistent, String text) {
        this.type = type;
        this.isPersistent = isPersistent;
        this.text = text;
    }

    /**
     * Creates a new annotation with the given persistence state.
     *
     * @param isPersistent
     *         <code>true</code> if persistent, <code>false</code> otherwise
     */
    public Annotation(boolean isPersistent) {
        this(null, isPersistent, null);
    }

    /**
     * Creates a new annotation with the given properties.
     *
     * @param type
     *         the unique name of this annotation type
     * @param isPersistent
     *         <code>true</code> if this annotation is
     *         persistent, <code>false</code> otherwise
     * @param text
     *         the text associated with this annotation
     * @param layer
     *         annotation draw layer
     * @param image
     *         image associated with this annotation
     */
    public Annotation(String type, boolean isPersistent, String text, int layer, ImageResource image) {
        this(type, isPersistent, text);
        this.layer = layer;
        this.image = image;
    }

    /**
     * Creates a new annotation with the given properties.
     *
     * @param type
     *         the unique name of this annotation type
     * @param isPersistent
     *         <code>true</code> if this annotation is
     *         persistent, <code>false</code> otherwise
     * @param text
     *         the text associated with this annotation
     * @param layer
     *         annotation draw layer
     * @param imageSVG
     *         image associated with this annotation
     */
    public Annotation(String type, boolean isPersistent, String text, int layer, SVGResource imageSVG) {
        this(type, isPersistent, text);
        this.layer = layer;
        this.imageSVG = imageSVG;
    }

    /**
     * Returns whether this annotation is persistent.
     *
     * @return <code>true</code> if this annotation is persistent, <code>false</code>
     * otherwise
     */
    public boolean isPersistent() {
        return isPersistent;
    }

    /**
     * Sets the type of this annotation.
     *
     * @param type
     *         the annotation type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Returns the type of the annotation.
     *
     * @return the type of the annotation
     */
    public String getType() {
        return type == null ? TYPE_UNKNOWN : type;
    }

    /**
     * Marks this annotation deleted according to the value of the
     * <code>deleted</code> parameter.
     *
     * @param deleted
     *         <code>true</code> if annotation should be marked as deleted
     */
    public void markDeleted(boolean deleted) {
        markedAsDeleted = deleted;
    }

    /**
     * Returns whether this annotation is marked as deleted.
     *
     * @return <code>true</code> if annotation is marked as deleted, <code>false</code>
     * otherwise
     */
    public boolean isMarkedDeleted() {
        return markedAsDeleted;
    }

    /**
     * Sets the text associated with this annotation.
     *
     * @param text
     *         the text associated with this annotation
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Returns the text associated with this annotation.
     *
     * @return the text associated with this annotation or <code>null</code>
     */
    public String getText() {
        return text;
    }

    /**
     * Return element resource for this annotation.
     * Note: if this method return <code>null</code>, this annotation not draw in left gutter
     *
     * @return
     */
    public Element getImageElement() {
        return imageElement;
    }

    /**
     * Return the annotations drawing layer.
     *
     * @return int starting from 0
     */
    public int getLayer() {
        return layer;
    }
}
