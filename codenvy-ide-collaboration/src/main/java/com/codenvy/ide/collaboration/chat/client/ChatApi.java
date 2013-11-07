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

import com.codenvy.ide.collaboration.dto.ChatMessage;

import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.client.framework.websocket.FrontendApi;
import org.exoplatform.ide.client.framework.websocket.MessageBus;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class ChatApi extends FrontendApi {

    public SendApi<ChatMessage> SEND_MESSAGE = makeApi(Utils.getWorkspaceName() + "/collaboration/chat/send/message");

    public ChatApi(MessageBus messageBus) {
        super(messageBus);
    }
}
