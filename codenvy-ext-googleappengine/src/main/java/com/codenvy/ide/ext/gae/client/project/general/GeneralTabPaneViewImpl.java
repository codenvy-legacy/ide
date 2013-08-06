package com.codenvy.ide.ext.gae.client.project.general;

import com.codenvy.ide.ext.gae.client.GAELocalization;
import com.codenvy.ide.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladyslav Zhukovskii</a>
 * @version $Id: 05.08.13 vlad $
 */
@Singleton
public class GeneralTabPaneViewImpl extends Composite implements GeneralTabPaneView {
    interface GeneralTabPaneViewImplUiBinder extends UiBinder<Widget, GeneralTabPaneViewImpl> {}

    private static GeneralTabPaneViewImplUiBinder uiBinder = GWT.create(GeneralTabPaneViewImplUiBinder.class);

    @UiField
    Button btnApplicationUpdate;

    @UiField
    Button btnApplicationRollBack;

    @UiField
    Button btnApplicationLogs;

    @UiField
    Button btnIndexesUpdate;

    @UiField
    Button btnIndexesVacuum;

    @UiField
    Button btnPageSpeedUpdate;

    @UiField
    Button btnQueuesUpdate;

    @UiField
    Button btnDoSUpdate;

    @UiField(provided = true)
    GAELocalization constant;

    private ActionDelegate delegate;

    @Inject
    public GeneralTabPaneViewImpl(GAELocalization constant) {
        this.constant = constant;

        Widget widget = uiBinder.createAndBindUi(this);

        initWidget(widget);
    }

    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @UiHandler("btnApplicationUpdate")
    public void onApplicationUpdateClicked(ClickEvent event) {
        delegate.onUpdateApplicationClicked();
    }

    @UiHandler("btnApplicationRollBack")
    public void onApplicationRollBackClicked(ClickEvent event) {
        delegate.onRollBackApplicationClicked();
    }

    @UiHandler("btnApplicationLogs")
    public void onApplicationLogsClicked(ClickEvent event) {
        delegate.onGetApplicationLogsClicked();
    }

    @UiHandler("btnIndexesUpdate")
    public void onIndexesUpdateClicked(ClickEvent event) {
        delegate.onUpdateIndexesClicked();
    }

    @UiHandler("btnIndexesVacuum")
    public void onIndexesVacuumClicked(ClickEvent event) {
        delegate.onVacuumIndexesClicked();
    }

    @UiHandler("btnPageSpeedUpdate")
    public void onPageSpeedUpdateClicked(ClickEvent event) {
        delegate.onUpdatePageSpeedClicked();
    }

    @UiHandler("btnQueuesUpdate")
    public void onQueuesUpdateClicked(ClickEvent event) {
        delegate.onUpdateQueuesClicked();
    }

    @UiHandler("btnDoSUpdate")
    public void onDoSUpdateClicked(ClickEvent event) {
        delegate.onUpdateDoSClicked();
    }
}
