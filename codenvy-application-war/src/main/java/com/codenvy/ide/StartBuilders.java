/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 * [2012] - [$today.year] Codenvy, S.A. 
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
package com.codenvy.ide;

import com.codenvy.api.builder.BuildQueue;
import com.codenvy.api.builder.internal.Builder;
import com.codenvy.api.core.config.Configuration;
import com.codenvy.api.core.util.ComponentLoader;

import org.picocontainer.Startable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href="mailto:vparfonov@codenvy.com">Vitaly Parfonov</a>
 * @version $Id:
 */
public class StartBuilders implements Startable {


    private List<Builder> builders;

    private List<BuildQueue> buildQueues;

    private List<WeakReference> lifeCycles;

    @Override
    public void start() {
        Collection<Builder> all = ComponentLoader.all(Builder.class);
        Collection<BuildQueue> queues = ComponentLoader.all(BuildQueue.class);

        buildQueues = new ArrayList<>(queues.size());
        for (BuildQueue queue : queues) {
            queue.start();
            buildQueues.add(queue);
        }

        builders = new ArrayList<>(all.size());
        lifeCycles = new ArrayList<>(all.size());
        for (Builder builder : all) {
            final Configuration configuration = builder.getDefaultConfiguration();
            builder.setConfiguration(configuration);
            builder.start();
            builders.add(builder);
            lifeCycles.add(new WeakReference<>(builder));
        }
    }

    @Override
    public void stop() {
        for (int i = 0; i < builders.size(); i++) {
            builders.get(i).stop();
        }
        for (int i = 0; i < lifeCycles.size(); i++) {
            lifeCycles.get(i).clear();
        }
        for (int i = 0; i < buildQueues.size(); i++) {
            buildQueues.get(i).stop();
        }
    }
}
