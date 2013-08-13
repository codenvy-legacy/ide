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
package com.codenvy.ide.texteditor.api;

import com.codenvy.ide.text.store.DocumentModel;
import com.codenvy.ide.text.store.Line;
import com.codenvy.ide.text.store.TextChange;

/**
 * A listener that is called when the user enters or deletes text.
 * <p/>
 * Similar to {@link DocumentModel.TextListener} except is only called when the
 * text is entered/deleted by the local user.
 */
public interface TextListener {
    /**
     * Note: You should not mutate the document within this callback, as this is
     * not supported yet and can lead to other clients having stale position
     * information inside the {@code textChange}.
     * <p/>
     * Note: The {@link TextChange} contains a reference to the live
     * {@link Line} from the document model. If you hold on to a reference after
     * {@link #onTextChange} returns, beware that the contents of the
     * {@link Line} could change, invalidating some of the state in the
     * {@link TextChange}.
     */
    void onTextChange(TextChange textChange);
}