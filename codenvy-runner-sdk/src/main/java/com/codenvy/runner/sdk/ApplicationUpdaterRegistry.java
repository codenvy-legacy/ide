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
import com.google.inject.Singleton;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * //
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class ApplicationUpdaterRegistry {
    private final Map<Long, ApplicationUpdater> applicationUpdaters;

    public ApplicationUpdaterRegistry() {
        applicationUpdaters = new ConcurrentHashMap<>();
    }

    public void registerUpdater(ApplicationProcess process, ApplicationUpdater updater) {
        applicationUpdaters.put(process.getId(), updater);
    }

    public ApplicationUpdater unregisterUpdater(long id) {
        return applicationUpdaters.remove(id);
    }

    public ApplicationUpdater getUpdater(long id) {
        return applicationUpdaters.get(id);
    }
}
