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
package org.exoplatform.ide.extension.heroku.client.marshaller;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.extension.heroku.client.HerokuExtension;
import org.exoplatform.ide.extension.heroku.shared.Stack;

import java.util.List;

/**
 * Unmarshaller for response with the list of Heroku stacks.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jul 28, 2011 5:38:08 PM anya $
 */
public class StackListUnmarshaller implements Unmarshallable<List<Stack>> {

    /** List of stacks. */
    private List<Stack> stackList;

    /**
     * @param stackList
     *         list of stacks
     */
    public StackListUnmarshaller(List<Stack> stackList) {
        this.stackList = stackList;
    }

    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        try {
            JSONArray jsonArray = JSONParser.parseStrict(response.getText()).isArray();
            if (jsonArray == null || jsonArray.size() <= 0)
                return;

            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.get(i).isObject();
                AutoBean<Stack> stack =
                        AutoBeanCodex.decode(HerokuExtension.AUTO_BEAN_FACTORY, Stack.class, jsonObject.toString());
                stackList.add(stack.as());
            }
        } catch (Exception e) {
            throw new UnmarshallerException(HerokuExtension.LOCALIZATION_CONSTANT.stackListUnmarshalFailed());
        }
    }

    /** @see org.exoplatform.gwtframework.commons.rest.copy.Unmarshallable#getPayload() */
    @Override
    public List<Stack> getPayload() {
        return stackList;
    }
}
