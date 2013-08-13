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
package com.codenvy.ide.extension.cloudfoundry.client.marshaller;

import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.extension.cloudfoundry.dto.client.DtoClientImpls;
import com.codenvy.ide.extension.cloudfoundry.shared.CloudFoundryApplication;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.http.client.Response;

/**
 * Unmarshaller for CloudFoundry application.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class CloudFoundryApplicationUnmarshaller implements Unmarshallable<CloudFoundryApplication> {
    private DtoClientImpls.CloudFoundryApplicationImpl application;

    /**
     * Create unmarshaller.
     *
     * @param application
     */
    public CloudFoundryApplicationUnmarshaller(DtoClientImpls.CloudFoundryApplicationImpl application) {
        this.application = application;
    }

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        String text = response.getText();

        if (text == null || text.isEmpty()) {
            return;
        }

        DtoClientImpls.CloudFoundryApplicationImpl application = DtoClientImpls.CloudFoundryApplicationImpl.deserialize(text);

        this.application.setName(application.getName());
        this.application.setUris(application.getUris());
        this.application.setInstances(application.getInstances());
        this.application.setRunningInstances(application.getRunningInstances());
        this.application.setState(application.getState());
        this.application.setServices(application.getServices());
        this.application.setVersion(application.getVersion());
        this.application.setEnv(application.getEnv());
        this.application.setResources(application.getResources());
        this.application.setStaging(application.getStaging());
        this.application.setDebug(application.getDebug());
        this.application.setMeta(application.getMeta());
    }

    /** {@inheritDoc} */
    @Override
    public CloudFoundryApplication getPayload() {
        return application;
    }
}