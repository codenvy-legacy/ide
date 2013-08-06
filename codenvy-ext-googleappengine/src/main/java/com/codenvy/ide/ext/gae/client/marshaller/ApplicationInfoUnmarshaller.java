package com.codenvy.ide.ext.gae.client.marshaller;

import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.ext.gae.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.gae.shared.ApplicationInfo;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

/**
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladyslav Zhukovskii</a>
 * @version $Id: 29.07.13 vlad $
 */
public class ApplicationInfoUnmarshaller implements Unmarshallable<ApplicationInfo> {
    private DtoClientImpls.ApplicationInfoImpl applicationInfo;

    public ApplicationInfoUnmarshaller(
            DtoClientImpls.ApplicationInfoImpl applicationInfo) {
        this.applicationInfo = applicationInfo;
    }

    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        String text = response.getText();
        if (text == null || text.isEmpty()) {
            return;
        }

        JSONObject jsonObject = JSONParser.parseStrict(text).isObject();
        if (jsonObject == null) {
            return;
        }

        DtoClientImpls.ApplicationInfoImpl dtoApplicationInfo = DtoClientImpls.ApplicationInfoImpl.deserialize(text);
        applicationInfo.setApplicationId(dtoApplicationInfo.getApplicationId());
        applicationInfo.setWebURL(dtoApplicationInfo.getWebURL());
    }

    @Override
    public ApplicationInfo getPayload() {
        return applicationInfo;
    }
}
