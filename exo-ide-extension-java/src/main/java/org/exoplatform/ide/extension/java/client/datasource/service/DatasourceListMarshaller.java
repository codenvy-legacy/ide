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
