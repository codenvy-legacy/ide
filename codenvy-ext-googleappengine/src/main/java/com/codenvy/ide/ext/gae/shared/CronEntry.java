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
package com.codenvy.ide.ext.gae.shared;

import com.codenvy.ide.dto.DTO;

/**
 * Information about cron entry.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 23, 2012 4:40:20 PM anya $
 */
@DTO
public interface CronEntry {
    /**
     * Get cron entry description.
     *
     * @return description of cron entry.
     */
    String getDescription();

    /**
     * Get cron entry schedule.
     *
     * @return schedule of cron entry.
     */
    String getSchedule();

    /**
     * Get cron entry time zone.
     *
     * @return time zone.
     */
    String getTimezone();

    /**
     * Get cron entry url.
     *
     * @return cron entry url.
     */
    String getUrl();

    /**
     * Get next time iteration.
     *
     * @return next time iteration.
     */
    Object getNextTimesIterato();
}