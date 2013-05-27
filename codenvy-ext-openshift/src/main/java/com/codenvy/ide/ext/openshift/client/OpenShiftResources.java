/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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

    @Source("com/codenvy/ide/ext/openshift/images/openshift_Disabled.png")
    ImageResource openShiftDisabled();

    @Source("com/codenvy/ide/ext/openshift/images/openshift_48.png")
    ImageResource openShift48();

    @Source("com/codenvy/ide/ext/openshift/images/openshift_48_Disabled.png")
    ImageResource openShift48Disabled();

    /*
     * Controls
     */
    @Source("com/codenvy/ide/ext/openshift/images/switchAccount.png")
    ImageResource switchAccount();

    @Source("com/codenvy/ide/ext/openshift/images/switchAccount_Disabled.png")
    ImageResource switchAccoundDisabled();

    @Source("com/codenvy/ide/ext/openshift/images/apps-list.png")
    ImageResource showApplications();

    @Source("com/codenvy/ide/ext/openshift/images/apps-list_Disabled.png")
    ImageResource showApplicationsDisabled();

    @Source("com/codenvy/ide/ext/openshift/images/domain-edit.png")
    ImageResource domainEdit();

    @Source("com/codenvy/ide/ext/openshift/images/domain-edit_Disabled.png")
    ImageResource domainEditDisabled();

    @Source("com/codenvy/ide/ext/openshift/images/update-ssh.png")
    ImageResource updateSshPublicKey();

    @Source("com/codenvy/ide/ext/openshift/images/update-ssh_Disabled.png")
    ImageResource updateSshPublicKeyDisabled();


}
