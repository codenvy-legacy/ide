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
package org.exoplatform.ide.client.restdiscovery.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.Border;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.restdiscovery.ParamExt;
import org.exoplatform.ide.client.restdiscovery.UntypedTreeGrid;

/**
 * View implementation for org.exoplatform.ide.client.restdiscovery.RestServicesDiscoveryPresenter.Display
 * <p/>
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class RestServicesDiscoveryView extends ViewImpl implements
                                                        org.exoplatform.ide.client.restdiscovery.RestServicesDiscoveryPresenter.Display {

    /** ID of this view. */
    private static final String ID = "ideResrServicesDiscoveryView";

    /** Initial width of this view */
    private static final int INITIAL_WIDTH = 500;

    /** Initial height of this view */
    private static final int INITIAL_HEIGHT = 330;

    private static final String TITLE = IDE.PREFERENCES_CONSTANT.restServicesDiscoveryTitle();

    /** UIBinder instance */
    private static RestServicesDiscoveryViewUiBinder uiBinder = GWT.create(RestServicesDiscoveryViewUiBinder.class);

    interface RestServicesDiscoveryViewUiBinder extends UiBinder<Widget, RestServicesDiscoveryView> {
    }

    /** Ok button */
    @UiField
    ImageButton okButton;

    /** Rest services tree */
    @UiField
    RestServiceTree treeGrid;

    /** Rest service parameters table */
    @UiField
    RestServiceParameterListGrid parametersListGrid;

    /** Text field for displaying Path to selected Rest Service */
    @UiField
    TextInput pathField;

    /** Text field that displays type of Request */
    @UiField
    TextInput requestTypeField;

    /** Test field that displays type of Response */
    @UiField
    TextInput responseTypeField;

    /** Border over service parameters table */
    @UiField
    Border parametersListGridContainer;

    /** Creates new instance of this View */
    public RestServicesDiscoveryView() {
        super(ID, "popup", TITLE, new Image(IDEImageBundle.INSTANCE.restServicesDiscovery()), INITIAL_WIDTH,
              INITIAL_HEIGHT);
        setCloseOnEscape(true);
        add(uiBinder.createAndBindUi(this));
    }

    /**
     * Get Ok button
     *
     * @see org.exoplatform.ide.client.restdiscovery.RestServicesDiscoveryPresenter.Display#getOkButton()
     */
    @Override
    public HasClickHandlers getOkButton() {
        return okButton;
    }

    /**
     * Get Rest services tree
     *
     * @see org.exoplatform.ide.client.restdiscovery.RestServicesDiscoveryPresenter.Display#getTreeGrid()
     */
    @Override
    public UntypedTreeGrid getTreeGrid() {
        return treeGrid;
    }

    /**
     * Get Service parameters table
     *
     * @see org.exoplatform.ide.client.restdiscovery.RestServicesDiscoveryPresenter.Display#getParametersListGrid()
     */
    @Override
    public ListGridItem<ParamExt> getParametersListGrid() {
        return parametersListGrid;
    }

    /**
     * Get text field for displaying path to selected rest service
     *
     * @see org.exoplatform.ide.client.restdiscovery.RestServicesDiscoveryPresenter.Display#getPathField()
     */
    @Override
    public HasValue<String> getPathField() {
        return pathField;
    }

    /**
     * Sets visibility of Response type field
     *
     * @see org.exoplatform.ide.client.restdiscovery.RestServicesDiscoveryPresenter.Display#setResponseFieldVisible(boolean)
     */
    @Override
    public void setResponseFieldVisible(boolean visible) {
        responseTypeField.setVisible(visible);
    }

    /**
     * Sets enabling of Response type field
     *
     * @see org.exoplatform.ide.client.restdiscovery.RestServicesDiscoveryPresenter.Display#setResponseFieldEnabled(boolean)
     */
    @Override
    public void setResponseFieldEnabled(boolean enabled) {
        responseTypeField.setEnabled(enabled);
    }

    /**
     * Sets visibility of Request type field
     *
     * @see org.exoplatform.ide.client.restdiscovery.RestServicesDiscoveryPresenter.Display#setRequestFieldVisible(boolean)
     */
    @Override
    public void setRequestFieldVisible(boolean visible) {
        requestTypeField.setVisible(visible);
    }

    /**
     * Sets enabling of Request type field
     *
     * @see org.exoplatform.ide.client.restdiscovery.RestServicesDiscoveryPresenter.Display#setRequestFieldEnabled(boolean)
     */
    @Override
    public void setRequestFieldEnabled(boolean enabled) {
        requestTypeField.setEnabled(enabled);
    }

    /**
     * Sets visibility of border over service parameters table
     *
     * @see org.exoplatform.ide.client.restdiscovery.RestServicesDiscoveryPresenter.Display#setParametersListGridVisible(boolean)
     */
    @Override
    public void setParametersListGridVisible(boolean visible) {
        parametersListGridContainer.setVisible(visible);
    }

    /**
     * Sets enabling of Service parameters table
     *
     * @see org.exoplatform.ide.client.restdiscovery.RestServicesDiscoveryPresenter.Display#setParametersListGridEnabled(boolean)
     */
    @Override
    public void setParametersListGridEnabled(boolean enabled) {
    }

    /**
     * Get Request type text field
     *
     * @see org.exoplatform.ide.client.restdiscovery.RestServicesDiscoveryPresenter.Display#getRequestTypeField()
     */
    @Override
    public HasValue<String> getRequestTypeField() {
        return requestTypeField;
    }

    /**
     * Get Response type text field
     *
     * @see org.exoplatform.ide.client.restdiscovery.RestServicesDiscoveryPresenter.Display#getResponseTypeField()
     */
    @Override
    public HasValue<String> getResponseTypeField() {
        return responseTypeField;
    }

}
