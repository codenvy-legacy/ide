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
