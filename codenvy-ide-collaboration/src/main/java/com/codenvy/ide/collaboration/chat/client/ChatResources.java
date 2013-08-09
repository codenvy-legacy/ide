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
