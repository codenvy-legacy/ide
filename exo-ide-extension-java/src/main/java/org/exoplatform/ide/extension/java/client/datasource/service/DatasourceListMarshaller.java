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
package org.exoplatform.ide.extension.java.client.datasource.service;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

import org.exoplatform.gwtframework.commons.rest.Marshallable;
import org.exoplatform.ide.extension.java.shared.DataSourceOption;
import org.exoplatform.ide.extension.java.shared.DataSourceOptions;

import java.util.List;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 * 
 */
public class DatasourceListMarshaller implements Marshallable {
    
    private List<DataSourceOptions> datasourceList;
    
    public DatasourceListMarshaller(List<DataSourceOptions> datasourceList) {
        this.datasourceList = datasourceList;
    }

    @Override
    public String marshal() {
        JSONArray array = new JSONArray();

        if (datasourceList != null) {
            for (int i = 0; i < datasourceList.size(); i++) {
                DataSourceOptions datasource = datasourceList.get(i);
                
                JSONObject datasourceObj = new JSONObject();
                array.set(i, datasourceObj);
                
                datasourceObj.put(DatasourceConstants.NAME, datasource.getName() != null ?
                    new JSONString(datasource.getName()) : JSONNull.getInstance());
                
                JSONArray optionsArray = new JSONArray();
                datasourceObj.put(DatasourceConstants.OPTIONS, optionsArray);

                if (datasource.getOptions() != null) {
                    List<DataSourceOption> options = datasource.getOptions();
                    for (int j = 0; j < options.size(); j++) {
                        DataSourceOption option = options.get(j);

                        JSONObject optionObject = new JSONObject();
                        optionsArray.set(j, optionObject);
                        
                        optionObject.put(DatasourceConstants.NAME, option.getName() != null ?
                            new JSONString(option.getName()) : JSONNull.getInstance());

                        optionObject.put(DatasourceConstants.VALUE, option.getValue() != null ?
                            new JSONString(option.getValue()) : JSONNull.getInstance());
                        
                        optionObject.put(DatasourceConstants.REQUIRED, JSONBoolean.getInstance(option.isRequired()));

                        optionObject.put(DatasourceConstants.DESCRIPTION, option.getDescription() != null ?
                            new JSONString(option.getDescription()) : JSONNull.getInstance());
                    }
                }
            }            
        }
        
        return array.toString();
    }

}
