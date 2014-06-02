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

import elemental.dom.Element;

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

    /** Element associated with this annotation */
    protected Element element;

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
     * @param element
     *         element associated with this annotation
     */
    public Annotation(String type, boolean isPersistent, String text, int layer, Element element) {
        this(type, isPersistent, text);
        this.layer = layer;
        this.element = element;
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
    public Element getElement() {
        return element;
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
