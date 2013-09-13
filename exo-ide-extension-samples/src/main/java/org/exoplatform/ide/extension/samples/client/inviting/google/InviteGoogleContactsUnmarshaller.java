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
package org.exoplatform.ide.extension.samples.client.inviting.google;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.client.framework.invite.GoogleContact;
import org.exoplatform.ide.client.framework.util.IDEAutoBeanFactory;

import java.util.List;

/**
 * Unmarshaller for unmarshalling Google Contacts as {@link List} of {@link GoogleContact} objects.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: InviteGoogleContactsUnmarshaller.java Aug 20, 2012 3:50:12 PM azatsarynnyy $
 */
public class InviteGoogleContactsUnmarshaller implements Unmarshallable<List<GoogleContact>> {

    /** The generator of an {@link AutoBean}. */
    private static final IDEAutoBeanFactory AUTO_BEAN_FACTORY = GWT.create(IDEAutoBeanFactory.class);

    /** {@link List} of {@link GoogleContact}. */
    private List<GoogleContact> contacts;

    public InviteGoogleContactsUnmarshaller(List<GoogleContact> contacts) {
        this.contacts = contacts;
    }

    /** @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response) */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        JSONArray jsonArray = JSONParser.parseStrict(response.getText()).isArray();
        if (jsonArray == null) {
            return;
        }

        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.get(i).isObject();
            AutoBean<GoogleContact> contact =
                    AutoBeanCodex.decode(AUTO_BEAN_FACTORY, GoogleContact.class, jsonObject.toString());
            contacts.add(contact.as());
        }
    }

    /** @see org.exoplatform.gwtframework.commons.rest.copy.Unmarshallable#getPayload() */
    @Override
    public List<GoogleContact> getPayload() {
        return contacts;
    }

}
