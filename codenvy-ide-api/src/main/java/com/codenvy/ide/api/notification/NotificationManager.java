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
package com.codenvy.ide.api.notification;

import com.codenvy.ide.api.ui.workspace.PartPresenter;

import javax.validation.constraints.NotNull;

/**
 * The manager for notifications. Used to show notifications and change their states.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface NotificationManager  extends PartPresenter{
    /**
     * Show notification.
     *
     * @param notification
     *         notification that need to show
     */
    void showNotification(@NotNull Notification notification);
}