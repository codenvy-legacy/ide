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
package com.codenvy.ide.ext.tutorials.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

/**
 * Client resources.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: TutorialsResources.java Sep 13, 2013 12:37:19 PM azatsarynnyy $
 */
public interface TutorialsResources extends ClientBundle {
    interface TutorialsCss extends CssResource {
        String scrollPanel();
    }

    @Source("Tutorials.css")
    TutorialsCss tutorialsCss();

    @Source("codenvyTutorialTemplate.png")
    ImageResource codenvyTutorialTemplate();

    @Source("codenvyTutorialProject.png")
    ImageResource codenvyTutorialProject();

    @Source("guide.png")
    ImageResource guide();
}
