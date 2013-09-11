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
package com.google.collide.client.editor.folding;

import org.exoplatform.ide.editor.shared.text.IDocument;

import java.util.List;

/**
 * Interface used to implement in classes that may find fold range occurrences.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: FoldOccurrencesFinder.java Mar 19, 2013 1:28:56 AM azatsarynnyy $
 */
public interface FoldOccurrencesFinder {
    /**
     * Get list of text ranges that may be folded.
     *
     * @param document
     *         document to find fold ranges
     * @return list of text ranges to fold
     */
    List<AbstractFoldRange> findPositions(IDocument document);
}
