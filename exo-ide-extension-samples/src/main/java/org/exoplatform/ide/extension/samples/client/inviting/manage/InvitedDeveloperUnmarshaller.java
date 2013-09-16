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
package org.exoplatform.ide.extension.samples.client.inviting.manage;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;

import java.util.List;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class InvitedDeveloperUnmarshaller implements Unmarshallable<List<UserInvitations>> {
    private List<UserInvitations> invites;

    public InvitedDeveloperUnmarshaller(List<UserInvitations> invites) {
        this.invites = invites;
    }

    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        try {
            if (response.getText() == null || response.getText().isEmpty()) {
                return;
            }
            JSONArray array = JSONParser.parseStrict(response.getText()).isArray();

            if (array == null) {
                return;
            }

            for (int i = 0; i < array.size(); i++) {
                JSONObject developer = array.get(i).isObject();
                String id = developer.get("id").isString().stringValue();
                String recipient = developer.get("recipient").isString().stringValue();
                String status = developer.get("status").isString().stringValue();
                UserInvitations invitation = new UserInvitations(id, recipient, status);

                invites.add(invitation);
            }
        } catch (Exception e) {
            throw new UnmarshallerException("Can't parse invites information.");
        }
    }

    @Override
    public List<UserInvitations> getPayload() {
        return invites;
    }
}
