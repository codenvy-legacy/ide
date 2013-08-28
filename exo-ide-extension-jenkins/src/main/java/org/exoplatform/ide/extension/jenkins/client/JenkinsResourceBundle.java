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
package org.exoplatform.ide.extension.jenkins.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 */
public interface JenkinsResourceBundle extends ClientBundle {

    @Source("org/exoplatform/ide/extension/jenkins/client/images/build_Disabled.png")
    ImageResource build_Disabled();

    @Source("org/exoplatform/ide/extension/jenkins/client/images/build.png")
    ImageResource build();

    @Source("org/exoplatform/ide/extension/jenkins/client/images/blue_anime.gif")
    ImageResource blue_anime();

    @Source("org/exoplatform/ide/extension/jenkins/client/images/blue.png")
    ImageResource blue();

    @Source("org/exoplatform/ide/extension/jenkins/client/images/grey_anime.gif")
    ImageResource grey_anime();

    @Source("org/exoplatform/ide/extension/jenkins/client/images/grey.png")
    ImageResource grey();

    @Source("org/exoplatform/ide/extension/jenkins/client/images/red_anime.gif")
    ImageResource red_anime();

    @Source("org/exoplatform/ide/extension/jenkins/client/images/red.png")
    ImageResource red();

    @Source("org/exoplatform/ide/extension/jenkins/client/images/yellow.png")
    ImageResource yellow();

    @Source("org/exoplatform/ide/extension/jenkins/client/images/transparent.png")
    ImageResource transparent();

}
