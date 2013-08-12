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
 * A position updater is responsible for adapting document positions. When installed on a document, the position updater updates
 * the document's positions to changes applied to this document. Document updaters can be selective, i.e. they might only update
 * positions of a certain category.
 * <p>
 * Position updaters are of primary importance for the definition of the semantics of positions.
 * <p>
 * Clients may implement this interface or use the standard implementation {@link DefaultPositionUpdater}.
 * </p>
 */
public interface PositionUpdater {

    /**
     * Adapts positions to the change specified by the document event. It is ensured that the document's partitioning has been
     * adapted to this document change and that all the position updaters which have a smaller index in the document's position
     * updater list have been called.
     *
     * @param event
     *         the document event describing the document change
     */
    void update(DocumentEvent event);
}
