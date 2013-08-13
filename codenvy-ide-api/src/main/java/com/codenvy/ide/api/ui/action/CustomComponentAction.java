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
package com.codenvy.ide.api.ui.action;

import com.google.gwt.user.client.ui.Widget;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public interface CustomComponentAction {

    String CUSTOM_COMPONENT_PROPERTY = "customComponent";

    /**
     * @return custom Widget that represents action in UI.
     *         You (as a client/implementor) or this interface do not allow to invoke
     *         this method directly. Only action system can invoke it!
     *         <br/>
     *         <br/>
     *         The component should not be stored in the action instance because it may
     *         be shown on several toolbars simultaneously. CustomComponentAction.CUSTOM_COMPONENT_PROPERTY
     *         can be used to retrieve current component from a Presentation in AnAction#update() method.
     */
    Widget createCustomComponent(Presentation presentation);
}
