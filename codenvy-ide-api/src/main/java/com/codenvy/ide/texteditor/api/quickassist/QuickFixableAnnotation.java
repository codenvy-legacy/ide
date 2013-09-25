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
package com.codenvy.ide.texteditor.api.quickassist;


/**
 * Allows an annotation to tell whether there are quick fixes
 * for it and to cache that state.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public interface QuickFixableAnnotation {

    /**
     * Sets whether there are quick fixes available for
     * this annotation.
     *
     * @param state
     *         <code>true</code> if there are quick fixes available, false otherwise
     */
    void setQuickFixable(boolean state);

    /**
     * Tells whether the quick fixable state has been set.
     * <p>
     * Normally this means {@link #setQuickFixable(boolean)} has been
     * called at least once but it can also be hard-coded, e.g. always
     * return <code>true</code>.
     * </p>
     *
     * @return <code>true</code> if the state has been set
     */
    boolean isQuickFixableStateSet();

    /**
     * Tells whether there are quick fixes for this annotation.
     * <p>
     * <strong>Note:</strong> This method must only be called
     * if {@link #isQuickFixableStateSet()} returns <code>true</code>.</p>
     *
     * @return <code>true</code> if this annotation offers quick fixes
     */
    boolean isQuickFixable();

}
