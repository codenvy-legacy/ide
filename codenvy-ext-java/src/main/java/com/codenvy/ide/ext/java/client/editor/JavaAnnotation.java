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
package com.codenvy.ide.ext.java.client.editor;

import com.codenvy.ide.ext.java.client.core.compiler.CategorizedProblem;
import com.codenvy.ide.ext.java.client.core.dom.CompilationUnit;

import java.util.Iterator;

/**
 * Interface of annotations representing markers
 * and problems.
 *
 * @see org.eclipse.jdt.core.compiler.IProblem
 */
public interface JavaAnnotation {

    /**
     * Returns the type of the annotation.
     *
     * @return the type of the annotation
     * @see org.eclipse.jface.text.source.Annotation#getType()
     */
    String getType();

    /**
     * Returns whether this annotation is persistent.
     *
     * @return <code>true</code> if this annotation is persistent, <code>false</code> otherwise
     * @see org.eclipse.jface.text.source.Annotation#isPersistent()
     */
    boolean isPersistent();

    /**
     * Returns whether this annotation is marked as deleted.
     *
     * @return <code>true</code> if annotation is marked as deleted, <code>false</code> otherwise
     * @see org.eclipse.jface.text.source.Annotation#isMarkedDeleted()
     */
    boolean isMarkedDeleted();

    /**
     * Returns the text associated with this annotation.
     *
     * @return the text associated with this annotation or <code>null</code>
     * @see org.eclipse.jface.text.source.Annotation#getText()
     */
    String getText();

    /**
     * Returns whether this annotation is overlaid.
     *
     * @return <code>true</code> if overlaid
     */
    boolean hasOverlay();

    /**
     * Returns the overlay of this annotation.
     *
     * @return the annotation's overlay
     * @since 3.0
     */
    JavaAnnotation getOverlay();

    /**
     * Returns an iterator for iterating over the
     * annotation which are overlaid by this annotation.
     *
     * @return an iterator over the overlaid annotations
     */
    Iterator<JavaAnnotation> getOverlaidIterator();

    /**
     * Adds the given annotation to the list of
     * annotations which are overlaid by this annotations.
     *
     * @param annotation
     *         the problem annotation
     */
    void addOverlaid(JavaAnnotation annotation);

    /**
     * Removes the given annotation from the list of
     * annotations which are overlaid by this annotation.
     *
     * @param annotation
     *         the problem annotation
     */
    void removeOverlaid(JavaAnnotation annotation);

    /**
     * Tells whether this annotation is a problem
     * annotation.
     *
     * @return <code>true</code> if it is a problem annotation
     */
    boolean isProblem();

    /**
     * Returns the compilation unit corresponding to the document on which the annotation.
     *
     * @return the compilation unit or <code>null</code> if no corresponding compilation unit exists
     */
    CompilationUnit getCompilationUnit();

    /**
     * Returns the problem arguments or <code>null</code> if no problem arguments can be evaluated.
     *
     * @return returns the problem arguments or <code>null</code> if no problem
     *         arguments can be evaluated.
     */
    String[] getArguments();

    /**
     * Returns the problem id or <code>-1</code> if no problem id can be evaluated.
     *
     * @return returns the problem id or <code>-1</code>
     */
    int getId();

    /**
     * Returns the marker type associated to this problem or <code>null<code> if no marker type
     * can be evaluated. See also {@link CategorizedProblem#getMarkerType()}.
     *
     * @return the type of the marker which would be associated to the problem or
     *         <code>null<code> if no marker type can be evaluated.
     */
    String getMarkerType();
}
