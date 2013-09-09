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
