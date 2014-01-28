package org.exoplatform.ide.extension.samples.client.startpage;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.util.StringUnmarshaller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Handler to determine if user has at least one premium account.
 * @author vzhukovskii@codenvy.com
 */
public class SupportPremiumHandler implements RequestPremiumAccountInfoHandler {

    public SupportPremiumHandler() {
        IDE.addHandler(RequestPremiumAccountInfoEvent.TYPE, this);
    }

    /** {@inheritDoc} */
    @Override
    public void onRequestPremiumAccountInfo(RequestPremiumAccountInfoEvent event) {
        getUserFromApi();
    }

    /** Call User API to fetch information about current loggined user. */
    private void getUserFromApi() {
        try {
            AsyncRequestCallback<StringBuilder> callback =
                    new AsyncRequestCallback<StringBuilder>(new StringUnmarshaller(new StringBuilder())) {
                        @Override
                        protected void onSuccess(StringBuilder json) {
                            List<String> accountIds = new ArrayList<String>();
                            JSONValue jsonUserProfile = JSONParser.parseStrict(json.toString());
                            String currentUserId = jsonUserProfile.isObject().get("id").isString().stringValue();
                            if (jsonUserProfile.isObject()!= null && jsonUserProfile.isObject().containsKey("accounts")) {
                                JSONArray jsonAccounts = jsonUserProfile.isObject().get("accounts").isArray();
                                for (int i = 0; i < jsonAccounts.size(); i++) {
                                    JSONObject jsonAccount = jsonAccounts.get(i).isObject();
                                    accountIds.add(jsonAccount.get("id").isString().stringValue());
                                }

                                getAccountPremierProperty(currentUserId, accountIds);
                            }
                        }

                        @Override
                        protected void onFailure(Throwable exception) {
                            IDE.fireEvent(new PremiumAccountInfoReceivedEvent(false));
                        }
                    };
            AsyncRequest.build(RequestBuilder.GET, "/api/user").header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
        } catch (RequestException e) {
            IDE.fireEvent(new PremiumAccountInfoReceivedEvent(false));
        }
    }

    /** Get information about all accounts which user managed. */
    private void getAccountPremierProperty(final String currentUserId, final List<String> accountIds) {
        if (accountIds.isEmpty()) {
            IDE.fireEvent(new PremiumAccountInfoReceivedEvent(false));
            return;
        }

        final String accountId = accountIds.get(0);
        accountIds.remove(0);

        try {
            AsyncRequestCallback<StringBuilder> callback =
                    new AsyncRequestCallback<StringBuilder>(new StringUnmarshaller(new StringBuilder())) {
                        @Override
                        protected void onSuccess(StringBuilder json) {
                            JSONObject jsonAccount = JSONParser.parseStrict(json.toString()).isObject();
                            String accountOwnerId = jsonAccount.get("owner").isObject().get("id").isString().stringValue();
                            if (accountOwnerId.equals(currentUserId)) {
                                JSONObject jsonAttributes = jsonAccount.get("attributes").isObject();
                                if (jsonAttributes.containsKey("tariff_plan")) {
                                    IDE.fireEvent(new PremiumAccountInfoReceivedEvent(true));
                                    return;
                                }
                            }
                            getAccountPremierProperty(currentUserId, accountIds);
                        }

                        @Override
                        protected void onFailure(Throwable exception) {
                            IDE.fireEvent(new PremiumAccountInfoReceivedEvent(false));
                        }
                    };
            AsyncRequest.build(RequestBuilder.GET, "/api/account/" + accountId).header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
                    .send(callback);
        } catch (RequestException e) {
            IDE.fireEvent(new PremiumAccountInfoReceivedEvent(false));
        }
    }
}
