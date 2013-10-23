/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
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
package org.exoplatform.ide.extension.samples.client.marshal;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;

import java.util.List;

/**
 * @author <a href="mailto:ashumilova@codenvy.com">Ann Shumilova</a>
 * @version $Id:
 *
 */
public class OrganizationsUnmarshaller implements Unmarshallable<List<String>>{
    
    /**
     * The list of organizations.
     */
    private List<String> organizations;
    
    /**
     * @param organizations
     */
    public OrganizationsUnmarshaller(List<String> organizations){
        this.organizations = organizations;
    }
    
    /**
     * @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response)
     */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        JSONArray array = JSONParser.parseStrict(response.getText()).isArray();
        if (array == null || array.size() <= 0) {
            return;
        }

        for (int i = 0; i < array.size(); i++) {
            organizations.add(array.get(i).isString().stringValue());
        }
    }

    /**
     * @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#getPayload()
     */
    @Override
    public List<String> getPayload() {
        return organizations;
    }

}
