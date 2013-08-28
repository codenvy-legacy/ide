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
package org.exoplatform.ide.client.framework.editor;

import org.exoplatform.ide.editor.client.api.SelectionRange;
import org.exoplatform.ide.editor.shared.text.IDocument;
import org.exoplatform.ide.editor.shared.text.edits.TextEdit;

/**
 * Code comments modifier.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Apr 6, 2012 2:59:01 PM anya $
 */
public interface CommentsModifier {
    /**
     * @param document
     * @return {@link TextEdit}
     */
    TextEdit addBlockComment(SelectionRange selectionRange, IDocument document);

    /**
     * @param document
     * @return {@link TextEdit}
     */
    TextEdit removeBlockComment(SelectionRange selectionRange, IDocument document);

    /**
     * @param selectionRange
     * @param document
     * @return {@link TextEdit}
     */
    TextEdit toggleSingleLineComment(SelectionRange selectionRange, IDocument document);
}
