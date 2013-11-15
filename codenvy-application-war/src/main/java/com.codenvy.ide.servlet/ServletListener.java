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

package com.codenvy.ide.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import com.codenvy.api.builder.internal.Builder;
import com.codenvy.api.builder.internal.BuilderRegistry;
import com.codenvy.api.core.Lifecycle;
import com.codenvy.api.core.util.ComponentLoader;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href="mailto:vparfonov@codenvy.com">Vitaly Parfonov</a>
 * @version $Id:
 */
public class ServletListener implements ServletContextListener {

    private List<Builder> builders;

    private List<WeakReference<Builder>> lifeCycles;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();
        Collection<Builder> all = ComponentLoader.all(Builder.class);
        builders = new ArrayList<>(all.size());
        lifeCycles = new ArrayList<>(all.size());
        for (Builder builder : all) {
            builder.start();
            builders.add(builder);
            lifeCycles.add(new WeakReference<>(builder));
        }
        servletContext.setAttribute(BuilderRegistry.class.getName(), builders);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        for (WeakReference<Builder> reference : lifeCycles) {
            final Lifecycle lifecycle = reference.get();
            if (lifecycle != null) {
                lifecycle.stop();
            }
        }
        lifeCycles.clear();
    }
}
