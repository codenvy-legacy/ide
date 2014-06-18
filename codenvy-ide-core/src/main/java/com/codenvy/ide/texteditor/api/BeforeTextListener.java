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

import com.codenvy.ide.text.store.Line;
import com.codenvy.ide.text.store.TextChange;

/**
 * A listener that is called after the user enters or deletes text and before
 * it is applied to the document.
 */
public interface BeforeTextListener {
    /**
     * Note: You should not mutate the document within this callback, as this is
     * not supported yet and can lead to other clients having stale position
     * information inside the {@code textChange}.
     * <p/>
     * Note: The {@link TextChange} contains a reference to the live
     * {@link Line} from the document model. If you hold on to a reference after
     * {@link #onBeforeTextChange} returns, beware that the contents of the
     * {@link Line} could change, invalidating some of the state in the
     * {@link TextChange}.
     *
     * @param textChange
     *         the text change whose last line will be the same as the
     *         insertion point (since the text hasn't been inserted yet)
     */
    void onBeforeTextChange(TextChange textChange);
}