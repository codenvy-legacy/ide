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
package com.codenvy.ide.logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

/**
 * @author <a href="vsvydenko@codenvy.com">Valeriy Svydenko</a>
 * @version $Id: LogEventService.java 34027 10.06.13 11:42 vsvydenko $
 */
@Path("{ws-name}/event")
public class LogEventService {
    private static final Log LOG = ExoLogger.getLogger(LogEventService.class);

    @Path("user/active")
    @GET
    public void sendUserActiveEvent() {
        LOG.info("EVENT#user-active#");
    }
}
