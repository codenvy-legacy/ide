package com.codenvy.ide.ext.gae.client.project.limit;

import com.codenvy.ide.ext.gae.client.GAELocalization;
import com.codenvy.ide.ext.gae.shared.ResourceLimit;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.json.JsonStringMap;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.SelectionModel;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladyslav Zhukovskii</a>
 * @version $Id: 05.08.13 vlad $
 */
@Singleton
public class LimitTabPaneViewImpl extends Composite implements LimitTabPaneView {
    interface LimitTabPaneViewImplUiBinder extends UiBinder<Widget, LimitTabPaneViewImpl> {
    }

    private static LimitTabPaneViewImplUiBinder uiBinder = GWT.create(LimitTabPaneViewImplUiBinder.class);

    @UiField(provided = true)
    CellTable<ResourceLimit> limitsTable = new CellTable<ResourceLimit>();

    @UiField(provided = true)
    GAELocalization constant;

    private ActionDelegate delegate;

    private JsonStringMap<String> limitsName = JsonCollections.createStringMap();

    @Inject
    public LimitTabPaneViewImpl(GAELocalization constant) {
        this.constant = constant;

        Widget widget = uiBinder.createAndBindUi(this);

        initLimitsName();
        initLimitsTable();

        initWidget(widget);
    }

    private void initLimitsName() {
        limitsName.put("max_blob_size", constant.maxBlobSize());
        limitsName.put("max_file_size", constant.maxFileSize());
        limitsName.put("max_file_count", constant.maxFileCount());
        limitsName.put("max_total_file_size", constant.maxTotalFileSize());
    }

    private void initLimitsTable() {
        limitsTable.setWidth("100%", true);
        limitsTable.setAutoHeaderRefreshDisabled(true);
        limitsTable.setAutoFooterRefreshDisabled(true);

        HTMLPanel emptyPanel = new HTMLPanel("No limits.");
        limitsTable.setEmptyTableWidget(emptyPanel);

        final SelectionModel<ResourceLimit> noneSelectionModel = new NoSelectionModel<ResourceLimit>();
        limitsTable.setSelectionModel(noneSelectionModel);

        Column<ResourceLimit, String> resourceColumn = new Column<ResourceLimit, String>(new TextCell()) {
            @Override
            public String getValue(ResourceLimit object) {
                return limitsName.containsKey(object.getName()) ? limitsName.get(object.getName()) : object.getName();
            }
        };

        Column<ResourceLimit, String> limitColumn = new Column<ResourceLimit, String>(new TextCell()) {
            @Override
            public String getValue(ResourceLimit object) {
                return String.valueOf((long)object.getValue());
            }
        };

        limitsTable.addColumn(resourceColumn, constant.resourceColumnTitle());
        limitsTable.addColumn(limitColumn, constant.limitColumnTitle());
    }

    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public void setResourceLimits(JsonArray<ResourceLimit> limits) {
        List<ResourceLimit> resourceLimits = new ArrayList<ResourceLimit>(limits.size());
        for (int i = 0; i < limits.size(); i++) {
            resourceLimits.add(limits.get(i));
        }

        limitsTable.setRowData(resourceLimits);
    }
}
