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