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
package com.codenvy.ide.toolbar;

/** @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a> */

public interface CloseMenuHandler {

    /** Implement closing of all opened popups when user will click outside of all of them */
    void onCloseMenu();

}
