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
package com.codenvy.ide.collaboration.chat.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public interface ChatResources extends ClientBundle {
    public interface ChatCss extends CssResource {
        String chatMessage();

        String chatName();

        String chatTime();

        String messageNotDelivered();

        String chatNotification();

        String link();

        String chatDisabled();

        String chatDissabledMessage();
    }

    @Source("Chat.css")
    ChatCss chatCss();

    @Source("notDelivered.png")
    ImageResource notDelivered();

    @Source("collaborators.png")
    ImageResource collaborators();

    @Source("collaboratorsDisabled.png")
    ImageResource collaboratorsDisabled();

    @Source("animation.gif")
    ImageResource collaboratorsAnimation();

    @Source("blank.png")
    ImageResource blank();
}
