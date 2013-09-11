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
package org.exoplatform.ide.extension.samples.client.github.deploy;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.SelectItem;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.samples.client.SamplesExtension;

import java.util.LinkedHashMap;

/**
 * View to deploy samples imported from GitHub.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: DeploySamplesView.java Nov 22, 2011 10:35:27 AM vereshchaka $
 */
public class DeploySamplesView extends ViewImpl implements DeploySamplesPresenter.Display {
    private static final String ID = "DeploySamplesView";

    private static final String TITLE = SamplesExtension.LOCALIZATION_CONSTANT.importLoadDialogTitle();

    private static final int HEIGHT = 365;

    private static final int WIDTH = 580;

    interface DeploySamplesViewUiBinder extends UiBinder<Widget, DeploySamplesView> {
    }

    /** UIBinder instance */
    private static DeploySamplesViewUiBinder uiBinder = GWT.create(DeploySamplesViewUiBinder.class);

    @UiField
    SelectItem selectPaasField;

    @UiField
    ImageButton cancelButton;

    @UiField
    ImageButton finishButton;

    @UiField
    ImageButton backButton;

    @UiField
    FlowPanel paasPanel;

    public DeploySamplesView() {
        super(ID, ViewType.POPUP, TITLE, null, WIDTH, HEIGHT, false);
        add(uiBinder.createAndBindUi(this));
    }

    /** @see org.exoplatform.ide.extension.samples.client.github.deploy.DeploySamplesPresenter.Display#getFinishButton() */
    @Override
    public HasClickHandlers getFinishButton() {
        return finishButton;
    }

    /** @see org.exoplatform.ide.extension.samples.client.github.deploy.DeploySamplesPresenter.Display#getCancelButton() */
    @Override
    public HasClickHandlers getCancelButton() {
        return cancelButton;
    }

    /** @see org.exoplatform.ide.extension.samples.client.github.deploy.DeploySamplesPresenter.Display#getBackButton() */
    @Override
    public HasClickHandlers getBackButton() {
        return backButton;
    }

    /** @see org.exoplatform.ide.extension.samples.client.github.deploy.DeploySamplesPresenter.Display#getSelectPaasField() */
    @Override
    public HasValue<String> getSelectPaasField() {
        return selectPaasField;
    }

    /** @see org.exoplatform.ide.extension.samples.client.github.deploy.DeploySamplesPresenter.Display#enableFinishButton(boolean) */
    @Override
    public void enableFinishButton(boolean enable) {
        finishButton.setEnabled(enable);
    }

    /** @see org.exoplatform.ide.extension.samples.client.github.deploy.DeploySamplesPresenter.Display#setPaaSView(com.google.gwt.user
     * .client.ui.Composite) */
    @Override
    public void setPaaSView(Composite composite) {
        if (paasPanel.getWidgetCount() > 0) {
            paasPanel.remove(0);
        }
        paasPanel.add(composite);
    }

    /** @see org.exoplatform.ide.extension.samples.client.github.deploy.DeploySamplesPresenter.Display#hidePaas() */
    @Override
    public void hidePaas() {
        if (paasPanel.getWidgetCount() > 0) {
            paasPanel.remove(0);
        }
    }

    /** @see org.exoplatform.ide.extension.samples.client.github.deploy.DeploySamplesPresenter.Display#setPaaSValues(java.util
     * .LinkedHashMap) */
    @Override
    public void setPaaSValues(LinkedHashMap<String, String> values) {
        selectPaasField.setValueMap(values);
    }
}
