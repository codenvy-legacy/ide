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
package com.codenvy.ide.api.texteditor;

import elemental.events.Event;

/** A listener that is called on "keyup" native event. */
public interface NativeKeyUpListener {

    /**
     * @param event
     *         the event for the key up
     * @return true if the event was handled, false to proceed with default
     *         behavior
     */
    boolean onNativeKeyUp(Event event);
}