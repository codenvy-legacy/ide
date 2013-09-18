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
package com.codenvy.ide.ext.jenkins.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/** @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a> */
public interface JenkinsResources extends ClientBundle {

    @Source("com/codenvy/ide/ext/jenkins/images/build.png")
    ImageResource build();

    @Source("com/codenvy/ide/ext/jenkins/images/blue_anime.gif")
    ImageResource blue_anime();

    @Source("com/codenvy/ide/ext/jenkins/images/blue.png")
    ImageResource blue();

    @Source("com/codenvy/ide/ext/jenkins/images/grey_anime.gif")
    ImageResource grey_anime();

    @Source("com/codenvy/ide/ext/jenkins/images/grey.png")
    ImageResource grey();

    @Source("com/codenvy/ide/ext/jenkins/images/red_anime.gif")
    ImageResource red_anime();

    @Source("com/codenvy/ide/ext/jenkins/images/red.png")
    ImageResource red();

    @Source("com/codenvy/ide/ext/jenkins/images/yellow.png")
    ImageResource yellow();

    @Source("com/codenvy/ide/ext/jenkins/images/transparent.png")
    ImageResource transparent();
}