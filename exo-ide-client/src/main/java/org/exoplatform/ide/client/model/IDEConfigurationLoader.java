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
package org.exoplatform.ide.client.model;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestBuilder;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.configuration.IDEInitialConfiguration;
import org.exoplatform.ide.client.framework.util.Utils;

/**
 * Created by The eXo Platform SAS .
 *
 * @version $Id: $
 */

public class IDEConfigurationLoader {

    /* Consts */
    public static final String APPLICATION_NAME = "IDE"; //$NON-NLS-1$

    /* Error messages */
    private static final String CANT_READ_CONFIGURATION = IDE.ERRORS_CONSTANT.confLoaderCantReadConfiguration();

    private static final String INVALID_CONFIGURATION_TITLE = IDE.ERRORS_CONSTANT.confInvalidConfTitle();

    /* Fields */
    private boolean loaded = false;

    private HandlerManager eventBus;

    private Loader loader;

    public IDEConfigurationLoader(HandlerManager eventBus, Loader loader) {
        this.eventBus = eventBus;
        this.loader = loader;
    }

    public void loadConfiguration(AsyncRequestCallback<IDEInitialConfiguration> callback) {
        try {
            
            String url = Utils.getRestContext() + Utils.getWorkspaceName() + "/configuration/init";
            AsyncRequest.build(RequestBuilder.GET, url).loader(loader).send(callback);
        } catch (Exception e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e, CANT_READ_CONFIGURATION));
        }
    }

    public boolean isLoaded() {
        return loaded;
    }

    public static native JavaScriptObject getAppConfig() /*-{
        return $wnd.appConfig;
    }-*/;
}
