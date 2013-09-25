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
package com.codenvy.ide.text;

/**
 * Interface for objects which are interested in getting informed about document changes. A listener is informed about document
 * changes before they are applied and after they have been applied. It is ensured that the document event passed into the
 * listener is the same for the two notifications, i.e. the two document events can be checked using object identity.
 * <p>
 * Clients may implement this interface.
 * </p>
 *
 * @see Document
 */
public interface DocumentListener {

    /**
     * The manipulation described by the document event will be performed.
     *
     * @param event
     *         the document event describing the document change
     */
    void documentAboutToBeChanged(DocumentEvent event);

    /**
     * The manipulation described by the document event has been performed.
     *
     * @param event
     *         the document event describing the document change
     */
    void documentChanged(DocumentEvent event);
}
