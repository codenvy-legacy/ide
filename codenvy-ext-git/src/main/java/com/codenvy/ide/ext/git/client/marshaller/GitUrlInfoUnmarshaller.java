package com.codenvy.ide.ext.git.client.marshaller;

import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.ext.git.shared.GitUrlVendorInfo;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Unmarshaller for Git Url information.
 */
public class GitUrlInfoUnmarshaller implements Unmarshallable<GitUrlVendorInfo> {

    private GitUrlVendorInfo info;

    /** Create unmarshaller */
    public GitUrlInfoUnmarshaller(GitUrlVendorInfo info) {
        this.info = info;
    }

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        if (response.getText() == null || response.getText().isEmpty()) {
            return;
        }

        JSONObject jsonObject = JSONParser.parseStrict(response.getText()).isObject();
        if (jsonObject == null) {
            return;
        }

        if (jsonObject.containsKey("vendorName") && jsonObject.get("vendorName").isString() != null) {
            info.setVendorName(jsonObject.get("vendorName").isString().stringValue());
        }

        if (jsonObject.containsKey("vendorBaseHost") && jsonObject.get("vendorBaseHost").isString() != null) {
            info.setVendorBaseHost(jsonObject.get("vendorBaseHost").isString().stringValue());
        }

        if (jsonObject.containsKey("givenUrlSSH") && jsonObject.get("givenUrlSSH").isBoolean() != null) {
            info.setGivenUrlSSH(jsonObject.get("givenUrlSSH").isBoolean().booleanValue());
        }

        if (jsonObject.containsKey("oAuthScopes") && jsonObject.get("oAuthScopes").isArray() != null) {
            JSONArray jsonArray = jsonObject.get("oAuthScopes").isArray();
            List<String> oAuthScopes = new ArrayList<String>(jsonArray.size());
            for (int i = 0; i < jsonArray.size(); i++) {
                oAuthScopes.add(jsonArray.get(i).isString().stringValue());
            }

            info.setOauthScopes(oAuthScopes);
        }
    }

    /** {@inheritDoc} */
    @Override
    public GitUrlVendorInfo getPayload() {
        return info;
    }
}
