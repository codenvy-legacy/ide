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
package com.google.collide.client.editor.gutter;

import com.google.gwt.resources.client.CssResource;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public interface NotificationCss extends CssResource {
    @ClassName("warning-mark")
    String markWarning();

    @ClassName("mark-error")
    String markError();

    @ClassName("overview-mark-error")
    String overviewMarkError();

    @ClassName("overview-bottom-mark-error")
    String overviewBottomMarkError();

    @ClassName("overview-mark-warning")
    String overviewMarkWarning();

    @ClassName("overview-bottom-mark-warning")
    String overviewBottomMarkWarning();

    @ClassName("popup-notification")
    String popupNotification();

    @ClassName("mark-task")
    String markTask();

    @ClassName("overview-mark-task")
    String overviewMarkTask();
}
