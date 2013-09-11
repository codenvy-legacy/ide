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
package com.google.collide.server;

import org.everrest.websockets.WSConnectionContext;
import org.everrest.websockets.message.ChannelBroadcastMessage;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.util.Set;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class WSUtil {
    private static final Log LOG = ExoLogger.getLogger(WSUtil.class);

    private WSUtil() {
    }

    public static void broadcastToClients(String message, Set<String> collaborators) {
        for (String collaborator : collaborators) {
            ChannelBroadcastMessage broadcastMessage = new ChannelBroadcastMessage();
            broadcastMessage.setChannel("collab_editor." + collaborator);
            broadcastMessage.setBody(message);
            try {
                WSConnectionContext.sendMessage(broadcastMessage);
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }
}