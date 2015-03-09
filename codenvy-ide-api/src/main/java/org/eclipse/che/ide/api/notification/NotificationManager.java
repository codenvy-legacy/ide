/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.api.notification;

import org.eclipse.che.ide.api.parts.PartPresenter;

import javax.annotation.Nonnull;

/**
 * The manager for notifications. Used to show notifications and change their states.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface NotificationManager extends PartPresenter {
    /**
     * Show notification.
     *
     * @param notification
     *         notification that need to show
     */
    void showNotification(@Nonnull Notification notification);

    /**
     * Show information notification with the specified message.
     * <p/>
     * This is a shortcut for <code>showNotification(new Notification({message}, Notification.Type.INFO))</code>.
     *
     * @param message
     *         notification's message
     */
    void showInfo(@Nonnull String message);

    /**
     * Show warning notification with the specified message.
     * <p/>
     * This is a shortcut for <code>showNotification(new Notification({message}, Notification.Type.WARNING))</code>.
     *
     * @param message
     *         notification's message
     */
    void showWarning(@Nonnull String message);

    /**
     * Show error notification with the specified message.
     * <p/>
     * This is a shortcut for <code>showNotification(new Notification({message}, Notification.Type.ERROR))</code>.
     *
     * @param message
     *         notification's message
     */
    void showError(@Nonnull String message);
}