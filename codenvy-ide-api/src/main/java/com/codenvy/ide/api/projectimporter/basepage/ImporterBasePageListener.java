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
package com.codenvy.ide.api.projectimporter.basepage;

/**
 * The listener that provides an ability to detect the moment when property for {@link ImporterBasePageView} was changed.
 *
 * @author Roman Nikitenko
 */
public interface ImporterBasePageListener {

    /** Performs any actions appropriate in response to the user having changed the project's name. */
    void projectNameChanged(String name);

    /** Performs any actions appropriate in response to the user having changed the project's URL. */
    void projectUrlChanged(String url);

    /** Performs any actions appropriate in response to the user having changed the project's description. */
    void projectDescriptionChanged(String projectDescriptionValue);

    /** Performs any actions appropriate in response to the user having changed the project's visibility. */
    void projectVisibilityChanged(Boolean aPublic);
}
