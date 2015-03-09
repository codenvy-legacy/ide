/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.part.editor;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Click handler of the show tabs list button.
 *
 * @author Ann Shumilova
 */
public interface ShowListButtonClickHandler {

    /**
     * @param x
     *         x coordinate of the right bottom corner of the list button
     * @param y
     *         y coordinate of the right bottom corner of the list button
     * @param callback
     *         callback is called when list is closed
     */
    void onShowListClicked(int x, int y, AsyncCallback<Void> callback);
}
