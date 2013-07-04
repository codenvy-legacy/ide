/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package com.codenvy.ide.ext.aws.shared.beanstalk;

/**
 * Description of application event.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface Event {
    /**
     * The date when the event occurred.
     *
     * @return The date when the event occurred.
     */
    double getEventDate();

    /**
     * The event message.
     *
     * @return The event message.
     */
    String getMessage();

    /**
     * The application associated with the event.
     * <p/>
     * <b>Constraints:</b><br/>
     * <b>Length: </b>1 - 100<br/>
     *
     * @return The application associated with the event.
     */
    String getApplicationName();

    /**
     * The release label for the application version associated with this
     * event.
     * <p/>
     * <b>Constraints:</b><br/>
     * <b>Length: </b>1 - 100<br/>
     *
     * @return The release label for the application version associated with this
     *         event.
     */
    String getVersionLabel();

    /**
     * The name of the configuration associated with this event.
     * <p/>
     * <b>Constraints:</b><br/>
     * <b>Length: </b>1 - 100<br/>
     *
     * @return The name of the configuration associated with this event.
     */
    String getTemplateName();

    /**
     * The name of the environment associated with this event.
     * <p/>
     * <b>Constraints:</b><br/>
     * <b>Length: </b>4 - 23<br/>
     *
     * @return The name of the environment associated with this event.
     */
    String getEnvironmentName();

    /**
     * The severity level of this event.
     * <p/>
     * <b>Constraints:</b><br/>
     * <b>Allowed Values: </b>TRACE, DEBUG, INFO, WARN, ERROR, FATAL
     *
     * @return The severity level of this event.
     * @see EventsSeverity
     */
    EventsSeverity getSeverity();
}
