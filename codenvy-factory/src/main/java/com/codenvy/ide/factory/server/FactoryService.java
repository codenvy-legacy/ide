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
package com.codenvy.ide.factory.server;

import org.codenvy.mail.MailSenderClient;

import javax.mail.MessagingException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import java.io.IOException;

/**
 * Service for sharing Factory URL by e-mail messages.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: FactoryService.java Jun 25, 2013 10:50:00 PM azatsarynnyy $
 */
@Path("{ws-name}/factory")
public class FactoryService {

    private final MailSenderClient mailSenderClient;

    /**
     * Constructs a new {@link FactoryService}.
     * 
     * @param mailSenderClient client for sending messages over e-mail
     */
    public FactoryService(MailSenderClient mailSenderClient) {
        this.mailSenderClient = mailSenderClient;
    }

    /**
     * Sends e-mail message to share Factory URL.
     * 
     * @param recipient address to share Factory URL
     * @param message text message that includes Factory URL
     * @return the Response with the corresponded status
     */
    @POST
    @Path("share")
    public Response share(@QueryParam("recipient") String recipient, //
                         @QueryParam("message") String message) {
        final String sender = "Codenvy <noreply@codenvy.com>";
        final String subject = "Check out my Codenvy project";
        final String mimeType = "text/html; charset=utf-8";
        try {
            mailSenderClient.sendMail(sender, recipient, null, subject, mimeType, message);
            return Response.ok().build();
        } catch (MessagingException | IOException e) {
            throw new WebApplicationException(e);
        }
    }
}
