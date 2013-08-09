package com.codenvy.ide.ext.gae.client.project;

import com.codenvy.ide.ext.gae.client.GAELocalization;
import com.codenvy.ide.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The implementation of {@link ProjectView}.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladyslav Zhukovskii</a>
 * @version $Id: 05.08.13 vlad $
 */
@Singleton
public class ProjectViewImpl extends DialogBox implements ProjectView {
    interface ProjectViewImplUiBinder extends UiBinder<Widget, ProjectViewImpl> {
    }

    private static ProjectViewImplUiBinder uiBinder = GWT.create(ProjectViewImplUiBinder.class);

    @UiField
    TabPanel applicationTabPanel;

    @UiField
    Button btnClose;

    @UiField(provided = true)
    GAELocalization constant;

    private boolean isShown;

    private ActionDelegate delegate;

    /**
     * Constructor of View.
     */
    @Inject
    public ProjectViewImpl(GAELocalization constant) {
        this.constant = constant;

        Widget widget = uiBinder.createAndBindUi(this);

        this.setText(constant.manageApplicationViewTitle());
        this.setWidget(widget);
    }

    /** {@inheritDoc} */
    @Override
    public AcceptsOneWidget addTab(String tabTitle) {
        SimplePanel tabPanel = new SimplePanel();
        applicationTabPanel.add(tabPanel, tabTitle);

        return tabPanel;
    }

    /** {@inheritDoc} */
    @Override
    public void focusFirstTab() {
        applicationTabPanel.selectTab(0);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isShown() {
        return isShown;
    }

    /** {@inheritDoc} */
    @Override
    public void showDialog() {
        this.isShown = true;
        this.center();
        this.show();
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        this.isShown = false;
        this.hide();
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @UiHandler("btnClose")
    public void onCloseButtonClicked(ClickEvent event) {
        delegate.onCloseButtonClicked();
    }
}
