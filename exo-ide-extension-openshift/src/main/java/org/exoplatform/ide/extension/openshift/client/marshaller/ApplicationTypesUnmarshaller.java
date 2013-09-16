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
package org.exoplatform.ide.extension.openshift.client.marshaller;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.extension.openshift.client.OpenShiftExtension;

import java.util.List;

/**
 * Unmarshaller for application types response.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jul 7, 2011 1:03:13 PM anya $
 */
public class ApplicationTypesUnmarshaller implements Unmarshallable<List<String>> {

    /** List of application types. */
    private List<String> applicationTypes;

    /**
     * @param applicationTypes
     *         list of application types
     */
    public ApplicationTypesUnmarshaller(List<String> applicationTypes) {
        this.applicationTypes = applicationTypes;
    }

    /** @see org.exoplatform.gwtframework.commons.rest.copy.Unmarshallable#unmarshal(com.google.gwt.http.client.Response) */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        try {
            if (response.getText() == null || response.getText().isEmpty()) {
                return;
            }

            JSONArray array = JSONParser.parseStrict(response.getText()).isArray();
            if (array == null || array.size() <= 0) {
                return;
            }
            for (int i = 0; i < array.size(); i++) {
                String value = array.get(i).isString().stringValue();
                applicationTypes.add(value);
            }
        } catch (Exception e) {
            throw new UnmarshallerException(OpenShiftExtension.LOCALIZATION_CONSTANT.applicationTypesUnmarshallerFail());
        }
    }

    /** @see org.exoplatform.gwtframework.commons.rest.copy.Unmarshallable#getPayload() */
    @Override
    public List<String> getPayload() {
        return applicationTypes;
    }

}
