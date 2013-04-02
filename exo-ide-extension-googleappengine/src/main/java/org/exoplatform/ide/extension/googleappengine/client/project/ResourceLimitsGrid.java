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
package org.exoplatform.ide.extension.googleappengine.client.project;

import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineExtension;
import org.exoplatform.ide.extension.googleappengine.client.model.ResourceLimit;

import java.util.HashMap;

/**
 * Grid for displaying application's resource limits.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 25, 2012 3:07:57 PM anya $
 */
public class ResourceLimitsGrid extends ListGrid<ResourceLimit> {
    private final String ID = "ideResourceLimitsGrid";

    private final String RESOURCE = GoogleAppEngineExtension.GAE_LOCALIZATION.resourceColumnTitle();

    private final String LIMIT = GoogleAppEngineExtension.GAE_LOCALIZATION.limitColumnTitle();

    private HashMap<String, String> limitNames = new HashMap<String, String>();

    {
        limitNames.put("max_blob_size", GoogleAppEngineExtension.GAE_LOCALIZATION.maxBlobSize());
        limitNames.put("max_file_size", GoogleAppEngineExtension.GAE_LOCALIZATION.maxFileSize());
        limitNames.put("max_file_count", GoogleAppEngineExtension.GAE_LOCALIZATION.maxFileCount());
        limitNames.put("max_total_file_size", GoogleAppEngineExtension.GAE_LOCALIZATION.maxTotalFileSize());
    }

    public ResourceLimitsGrid() {
        super();

        setID(ID);

        Column<ResourceLimit, String> resourceColumn = new Column<ResourceLimit, String>(new TextCell()) {
            @Override
            public String getValue(ResourceLimit resourceLimit) {
                return (limitNames.containsKey(resourceLimit.getName())) ? limitNames.get(resourceLimit.getName())
                                                                         : resourceLimit.getName();
            }
        };

        Column<ResourceLimit, Number> limitColumn = new Column<ResourceLimit, Number>(new NumberCell()) {
            @Override
            public Number getValue(ResourceLimit resourceLimit) {
                return resourceLimit.getValue();
            }
        };

        getCellTable().addColumn(resourceColumn, RESOURCE);
        getCellTable().setColumnWidth(resourceColumn, "50");

        getCellTable().addColumn(limitColumn, LIMIT);
        getCellTable().setColumnWidth(limitColumn, "50%");
    }
}
