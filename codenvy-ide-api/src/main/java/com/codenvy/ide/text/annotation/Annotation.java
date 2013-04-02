/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.text.annotation;

import com.google.gwt.resources.client.ImageResource;

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
     * Returns whether this annotation is persistent.
     *
     * @return <code>true</code> if this annotation is persistent, <code>false</code>
     *         otherwise
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
     *         otherwise
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
     * Return image resource for this annotation.
     * Note: if this method return <code>null</code>, this annotation not draw in left gutter
     *
     * @return
     */
    public ImageResource getImage() {
        return image;
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
