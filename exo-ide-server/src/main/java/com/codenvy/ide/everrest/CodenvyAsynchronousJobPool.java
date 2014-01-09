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
package com.codenvy.ide.everrest;

import com.codenvy.api.core.user.UserState;
import com.codenvy.commons.env.EnvironmentContext;

import org.everrest.core.impl.EverrestConfiguration;
import org.everrest.core.impl.async.AsynchronousJob;
import org.everrest.core.impl.async.AsynchronousJobPool;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.*;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;


/**
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: CodenvyAsynchronousJobPool.java Feb 28, 2013 vetal $
 */
@Provider
public class CodenvyAsynchronousJobPool extends AsynchronousJobPool implements ContextResolver<AsynchronousJobPool> {
    private static final Log LOG = ExoLogger.getLogger(CodenvyAsynchronousJobPool.class);

    public CodenvyAsynchronousJobPool(EverrestConfiguration config) {
        super(config);
    }

    private static Method  mdc_getCopyOfContextMap;
    private static Method  mdc_setContextMap;
    private static Method  mdc_clear;
    private static boolean setUpLogger;

    static {
        try {
            Class<?> c = Thread.currentThread().getContextClassLoader().loadClass("org.slf4j.MDC");
            mdc_getCopyOfContextMap = c.getDeclaredMethod("getCopyOfContextMap");
            mdc_setContextMap = c.getDeclaredMethod("setContextMap", java.util.Map.class);
            mdc_clear = c.getDeclaredMethod("clear");
            setUpLogger = mdc_getCopyOfContextMap != null && mdc_setContextMap != null && mdc_clear != null;
        } catch (Exception ignore) {
        }
    }

    @Override
    protected void initAsynchronousJobContext(AsynchronousJob job) {
        final String internalJobUri =
                UriBuilder.fromPath("/").path(CodenvyAsynchronousJobService.class)
                          .path(CodenvyAsynchronousJobService.class, "get")
                          .build(EnvironmentContext.getCurrent().getVariable(EnvironmentContext.WORKSPACE_NAME), job.getJobId()).toString();
        job.getContext().put("internal-uri", internalJobUri);
    }

    @Override
    protected Callable<Object> newCallable(Object resource, Method method, Object[] params) {
        return new CallableWrapper(super.newCallable(resource, method, params));
    }

    private static class CallableWrapper implements Callable<Object> {
        private final EnvironmentContext envContext;
        private final ConversationState  conversationState;
        private final UserState          userState;
        private final Callable<Object>   callable;
        private       Object             loggerContext;

        public CallableWrapper(Callable<Object> callable) {
            this.callable = callable;
            conversationState = ConversationState.getCurrent();
            userState = UserState.get();
            envContext = EnvironmentContext.getCurrent();
            if (setUpLogger) {
                try {
                    loggerContext = mdc_getCopyOfContextMap.invoke(null);
                } catch (Throwable t) {
                    LOG.error(t.getMessage(), t);
                }
            }
        }

        @Override
        public Object call() throws Exception {
            ConversationState.setCurrent(conversationState == null
                                         ? new ConversationState(new Identity(IdentityConstants.ANONIM)) : conversationState);
            UserState.set(userState == null ? UserState.get() : userState);
            EnvironmentContext.setCurrent(envContext);
            if (loggerContext != null) {
                try {
                    mdc_setContextMap.invoke(null, loggerContext);
                } catch (Throwable t) {
                    LOG.error(t.getMessage(), t);
                }
            }
            try {
                return callable.call();
            } finally {
                EnvironmentContext.reset();
                UserState.reset();
                ConversationState.setCurrent(null);
                if (loggerContext != null) {
                    try {
                        mdc_clear.invoke(null);
                    } catch (Throwable t) {
                        LOG.error(t.getMessage(), t);
                    }
                }
            }
        }
    }
}
