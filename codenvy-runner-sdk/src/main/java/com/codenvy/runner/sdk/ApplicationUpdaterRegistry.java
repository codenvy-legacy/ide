/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2014] Codenvy, S.A.
 *  All Rights Reserved.
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
package com.codenvy.runner.sdk;

import com.codenvy.api.runner.internal.ApplicationProcess;

import javax.inject.Singleton;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry for {@link ApplicationUpdater}s.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class ApplicationUpdaterRegistry {
    private final Map<Long, ApplicationUpdater> applicationUpdaters;

    public ApplicationUpdaterRegistry() {
        applicationUpdaters = new ConcurrentHashMap<>();
    }

    /**
     * Register {@link ApplicationUpdater}.
     *
     * @param process
     * @param updater
     *         {@link ApplicationUpdater} to register
     */
    public void registerUpdater(ApplicationProcess process, ApplicationUpdater updater) {
        applicationUpdaters.put(process.getId(), updater);
    }

    /**
     * Unregister {@link ApplicationUpdater} for {@link ApplicationProcess} with the specified id.
     *
     * @param id
     *         id of the {@link ApplicationProcess}
     * @return removed {@link ApplicationUpdater}
     */
    public ApplicationUpdater unregisterUpdater(long id) {
        return applicationUpdaters.remove(id);
    }

    /**
     * Get {@link ApplicationUpdater} by the specified {@link ApplicationProcess}'s id.
     *
     * @param id
     *         id of the {@link ApplicationProcess}
     * @return {@link ApplicationUpdater}
     */
    public ApplicationUpdater getUpdaterByApplicationProcessId(long id) {
        return applicationUpdaters.get(id);
    }
}
