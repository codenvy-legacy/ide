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
package org.exoplatform.ide.maven;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class BuilderBootstrap implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Map<String, Object> config = new HashMap<String, Object>();
        ServletContext ctx = sce.getServletContext();

        final String BUILDER_REPOSITORY =
                (System.getProperty(BuildService.BUILDER_REPOSITORY) != null) ?
                System.getProperty(BuildService.BUILDER_REPOSITORY) :
                ctx.getInitParameter(BuildService.BUILDER_REPOSITORY);

        final String BUILDER_PUBLISH_REPOSITORY =
                (System.getProperty(BuildService.BUILDER_PUBLISH_REPOSITORY) != null) ?
                System.getProperty(BuildService.BUILDER_PUBLISH_REPOSITORY) :
                ctx.getInitParameter(BuildService.BUILDER_PUBLISH_REPOSITORY);

        final String BUILDER_PUBLISH_REPOSITORY_URL =
                (System.getProperty(BuildService.BUILDER_PUBLISH_REPOSITORY_URL) != null) ?
                System.getProperty(BuildService.BUILDER_PUBLISH_REPOSITORY_URL) :
                ctx.getInitParameter(BuildService.BUILDER_PUBLISH_REPOSITORY_URL);

        final Integer BUILDER_TIMEOUT =
                (System.getProperty(BuildService.BUILDER_TIMEOUT) != null) ?
                getNumber(System.getProperty(BuildService.BUILDER_TIMEOUT)) :
                getNumber(ctx.getInitParameter(BuildService.BUILDER_TIMEOUT));

        final Integer BUILDER_WORKERS_NUMBER =
                (System.getProperty(BuildService.BUILDER_WORKERS_NUMBER) != null) ?
                getNumber(System.getProperty(BuildService.BUILDER_WORKERS_NUMBER)) :
                getNumber(ctx.getInitParameter(BuildService.BUILDER_WORKERS_NUMBER));

        final Integer BUILDER_QUEUE_SIZE =
                (System.getProperty(BuildService.BUILDER_QUEUE_SIZE) != null) ?
                getNumber(System.getProperty(BuildService.BUILDER_QUEUE_SIZE)) :
                getNumber(ctx.getInitParameter(BuildService.BUILDER_QUEUE_SIZE));

        final Integer BUILDER_CLEAN_RESULT_DELAY_TIME =
                (System.getProperty(BuildService.BUILDER_CLEAN_RESULT_DELAY_TIME) != null) ?
                getNumber(System.getProperty(BuildService.BUILDER_CLEAN_RESULT_DELAY_TIME)) :
                getNumber(ctx.getInitParameter(BuildService.BUILDER_CLEAN_RESULT_DELAY_TIME));

        config.put(BuildService.BUILDER_REPOSITORY, BUILDER_REPOSITORY);

        config.put(BuildService.BUILDER_PUBLISH_REPOSITORY, BUILDER_PUBLISH_REPOSITORY);

        config.put(BuildService.BUILDER_PUBLISH_REPOSITORY_URL, BUILDER_PUBLISH_REPOSITORY_URL);

        config.put(BuildService.BUILDER_TIMEOUT, BUILDER_TIMEOUT);

        config.put(BuildService.BUILDER_WORKERS_NUMBER, BUILDER_WORKERS_NUMBER);

        config.put(BuildService.BUILDER_QUEUE_SIZE, BUILDER_QUEUE_SIZE);

        config.put(BuildService.BUILDER_CLEAN_RESULT_DELAY_TIME, BUILDER_CLEAN_RESULT_DELAY_TIME);

        BuildService buildService = new BuildService(config);
        ctx.setAttribute(BuildService.class.getName(), buildService);
    }

    private Integer getNumber(String value) {
        if (value != null) {
            try {
                return Integer.valueOf(value);
            } catch (NumberFormatException ignored) {
            }
        }
        return null;
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();
        BuildService buildService = (BuildService)ctx.getAttribute(BuildService.class.getName());
        if (buildService != null) {
            buildService.shutdown();
        }
    }
}
