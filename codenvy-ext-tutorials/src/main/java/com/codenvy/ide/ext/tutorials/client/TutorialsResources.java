/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
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
