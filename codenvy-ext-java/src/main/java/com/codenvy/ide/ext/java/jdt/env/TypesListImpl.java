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
package com.codenvy.ide.ext.java.jdt.env;

import com.codenvy.dto.shared.JsonArray;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.ext.java.shared.ShortTypeInfo;
import com.codenvy.ide.ext.java.shared.TypesList;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class TypesListImpl implements TypesList {


    private JSONArray json;

    protected TypesListImpl(JSONArray json) {
        this.json = json;
    }

    @Override
    public List<ShortTypeInfo> getTypes() {
        ArrayList<ShortTypeInfo> typeInfos = new ArrayList<ShortTypeInfo>();
        for (int i = 0; i < json.size(); i++) {
            typeInfos.add(new ShortTypeInfoImpl(json.get(i).isObject()));
        }
        return typeInfos;
    }

    @Override
    public void setTypes(List<ShortTypeInfo> types) {
        //nothing todo
    }

    public static TypesListImpl deserialize(String json) {
        JSONValue jsonValue = JSONParser.parseLenient(json);
        return new TypesListImpl(jsonValue.isObject().get("types").isArray());
    }
}
