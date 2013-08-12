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
package com.codenvy.ide.ext.openshift.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface OpenShiftResources extends ClientBundle {
    public interface OpenShiftCSS extends CssResource {
        String login();

        String loginFont();

        String loginErrorFont();
    }

    @Source({"OpenShift.css", "com/codenvy/ide/api/ui/style.css"})
    OpenShiftCSS openShiftCSS();

    @Source("com/codenvy/ide/ext/openshift/images/openshift_36.png")
    ImageResource openShiftLogo();

    /*
     * Buttons
     */

    /*
     * appfog controls
     */
    @Source("com/codenvy/ide/ext/openshift/images/openshift.png")
    ImageResource openShift();

    @Source("com/codenvy/ide/ext/openshift/images/openshift_48.png")
    ImageResource openShift48();

    /*
     * Controls
     */
    @Source("com/codenvy/ide/ext/openshift/images/switchAccount.png")
    ImageResource switchAccount();

    @Source("com/codenvy/ide/ext/openshift/images/apps-list.png")
    ImageResource showApplications();

    @Source("com/codenvy/ide/ext/openshift/images/domain-edit.png")
    ImageResource domainEdit();

    @Source("com/codenvy/ide/ext/openshift/images/update-ssh.png")
    ImageResource updateSshPublicKey();

    @Source("com/codenvy/ide/ext/openshift/images/properties.png")
    ImageResource showProperties();

    @Source("com/codenvy/ide/ext/openshift/images/start.png")
    ImageResource startApp();

    @Source("com/codenvy/ide/ext/openshift/images/stop.png")
    ImageResource stopApp();

    @Source("com/codenvy/ide/ext/openshift/images/restart.png")
    ImageResource restartApp();

}
