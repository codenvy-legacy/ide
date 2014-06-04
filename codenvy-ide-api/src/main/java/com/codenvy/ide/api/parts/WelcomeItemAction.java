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
package com.codenvy.ide.api.parts;

import com.google.gwt.resources.client.ImageResource;

/**
 * The presentation of welcome page item. This class provides general information of item as like title, caption and icon.
 * Also it provides implementation of action what happened when item is clicked.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface WelcomeItemAction {
    /** @return title of item */
    String getTitle();

    /** @return caption of item */
    String getCaption();

    /** @return icon */
    ImageResource getIcon();

    /** Perform some action when item is clicked. */
    void execute();
}