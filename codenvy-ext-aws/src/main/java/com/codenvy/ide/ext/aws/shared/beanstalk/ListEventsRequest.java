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
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface ListEventsRequest {

    /**
     * Get name of application.
     *
     * @return name of application
     */
    String getApplicationName();

    /**
     * Get application version label. If <code>null</code> get events for all versions of application
     *
     * @return application version label
     */
    String getVersionLabel();

    /**
     * Get name of configuration template. If <code>null</code> get events for all configuration template of
     * application.
     *
     * @return name of configuration template
     */
    String getTemplateName();

    /**
     * Get id of environment. If <code>null</code> get events for all application environments.
     *
     * @return id of environment
     */
    String getEnvironmentId();

    /**
     * Get events severity. Only events with specified severity or higher will be returned.
     *
     * @return events severity
     */
    EventsSeverity getSeverity();

    /**
     * Get token to get the next batch of results.
     *
     * @return token to get the next batch of results
     * @see EventsList#getNextToken()
     */
    String getNextToken();

    /**
     * The start date from which we should take events list.
     *
     * @return The start date for getting events.
     */
    double getStartTime();

    /**
     * The end date to which we should take events list.
     *
     * @return The end date for getting events.
     */
    double getEndTime();

    /**
     * Get maximum number of items in result. Max value: 1000.
     *
     * @return maximum number of items in result
     */
    int getMaxRecords();
}
