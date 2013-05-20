/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ide.extension.java.client.datasource.service;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.extension.java.shared.DataSourceOption;
import org.exoplatform.ide.extension.java.shared.DataSourceOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 * 
 */
public class DatasourceListUnmarshaller implements Unmarshallable<List<DataSourceOptions>> {

    private List<DataSourceOptions> datasourceList = new ArrayList<DataSourceOptions>();
    
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        try {
            JSONArray datasourceArray = JSONParser.parseLenient(response.getText()).isArray();
            
            for (int i = 0; i < datasourceArray.size(); i++) {
                JSONObject datasourceObject = datasourceArray.get(i).isObject();

                DataSourceOptions datasource = new DataSourceOptions();
                datasourceList.add(datasource);

                JSONString datasourceName = datasourceObject.get(DatasourceConstants.NAME).isString();
                datasource.setName(datasourceName != null ? datasourceName.stringValue() : null);
                
                List<DataSourceOption> options = new ArrayList<DataSourceOption>();
                datasource.setOptions(options);
                
                JSONArray optionsArray = datasourceObject.get(DatasourceConstants.OPTIONS).isArray();            
                for (int j = 0; j < optionsArray.size(); j++) {
                    JSONObject optionObject = optionsArray.get(j).isObject();

                    JSONString name = optionObject.get(DatasourceConstants.NAME).isString();
                    JSONString value = optionObject.get(DatasourceConstants.VALUE).isString();
                    JSONString description = optionObject.get(DatasourceConstants.DESCRIPTION).isString();
                    JSONBoolean required = optionObject.get(DatasourceConstants.REQUIRED).isBoolean();
                    
                    DataSourceOption option = new DataSourceOption(name != null ? name.stringValue() : null,
                            value != null ? value.stringValue() : null,
                            required != null ? required.booleanValue() : false,
                            description != null ? description.stringValue() : null);
                    options.add(option);
                }                
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new UnmarshallerException("Parsing Datasource list failed");
        }
    }

    @Override
    public List<DataSourceOptions> getPayload() {
        return datasourceList;
    }

}
