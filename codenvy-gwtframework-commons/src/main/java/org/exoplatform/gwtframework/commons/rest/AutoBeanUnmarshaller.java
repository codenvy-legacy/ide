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
package org.exoplatform.gwtframework.commons.rest;


import com.google.gwt.http.client.Response;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;

/**
 * Created by The eXo Platform SAS.
 *
 * @param <T>
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class AutoBeanUnmarshaller<T> implements Unmarshallable<T> {
    private AutoBean<T> bean;

    public AutoBeanUnmarshaller(AutoBean<T> autoBean) {
        this.bean = autoBean;
    }

    public void unmarshal(Response response) throws UnmarshallerException {
        if (response.getStatusCode() != 204 && response.getText() != null) {
            Splittable data = StringQuoter.split(response.getText());
            AutoBeanCodex.decodeInto(data, bean);
        }
    }

    public T getPayload() {
        return bean.as();
    }

}
