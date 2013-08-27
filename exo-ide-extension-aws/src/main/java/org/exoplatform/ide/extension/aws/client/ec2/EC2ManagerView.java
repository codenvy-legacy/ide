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
package org.exoplatform.ide.extension.aws.client.ec2;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.aws.client.AWSExtension;
import org.exoplatform.ide.extension.aws.shared.ec2.InstanceInfo;

import java.util.List;
import java.util.Map.Entry;

/**
 * View for manage Amazon EC2 virtual sever instances.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.com">Artem Zatsarynnyy</a>
 * @version $Id: EC2ManagerView.java Sep 21, 2012 10:12:25 AM azatsarynnyy $
 */
public class EC2ManagerView extends ViewImpl implements EC2Manager.Display {

    private static final String ID = "ideEC2ManagerView";

    private static final String CLOSE_BUTTON_ID = "ideEC2ManagerViewCloseButton";

    private static final int WIDTH = 1000;

    private static final int HEIGHT = 600;

    @UiField
    ImageButton terminateButton;

    @UiField
    ImageButton rebootButton;

    @UiField
    ImageButton stopButton;

    @UiField
    ImageButton startButton;

    @UiField
    ImageButton closeButton;

    @UiField
    EC2InstancesGrid ec2InstancesGrid;

    @UiField
    EC2InstancesTagsGrid ec2InstancesTagsGrid;

    private static EC2ManagerViewUiBinder uiBinder = GWT.create(EC2ManagerViewUiBinder.class);

    interface EC2ManagerViewUiBinder extends UiBinder<Widget, EC2ManagerView> {
    }

    public EC2ManagerView() {
        super(ID, ViewType.MODAL, AWSExtension.LOCALIZATION_CONSTANT.managementEC2ViewTitle(), null, WIDTH, HEIGHT, false);
        add(uiBinder.createAndBindUi(this));
        closeButton.setButtonId(CLOSE_BUTTON_ID);
    }

    /** @see org.exoplatform.ide.extension.aws.client.ec2.EC2Manager.Display#getTerminateButton() */
    @Override
    public HasClickHandlers getTerminateButton() {
        return terminateButton;
    }

    /** @see org.exoplatform.ide.extension.aws.client.ec2.EC2Manager.Display#getRebootButton() */
    @Override
    public HasClickHandlers getRebootButton() {
        return rebootButton;
    }

    /** @see org.exoplatform.ide.extension.aws.client.ec2.EC2Manager.Display#getStopButton() */
    @Override
    public HasClickHandlers getStopButton() {
        return stopButton;
    }

    /** @see org.exoplatform.ide.extension.aws.client.ec2.EC2Manager.Display#getStartButton() */
    @Override
    public HasClickHandlers getStartButton() {
        return startButton;
    }

    /** @see org.exoplatform.ide.extension.aws.client.ec2.EC2Manager.Display#getInstances() */
    @Override
    public HasSelectionHandlers<InstanceInfo> getInstances() {
        return ec2InstancesGrid;
    }

    /** @see org.exoplatform.ide.extension.aws.client.ec2.EC2Manager.Display#setImages(java.util.List) */
    @Override
    public void setInstances(List<InstanceInfo> instatceList) {
        ec2InstancesGrid.setValue(instatceList);
    }


    /** @see org.exoplatform.ide.extension.aws.client.ec2.EC2Manager.Display#getTags() */
    @Override
    public HasSelectionHandlers<Entry<String, String>> getTags() {
        return ec2InstancesTagsGrid;
    }

    /** @see org.exoplatform.ide.extension.aws.client.ec2.EC2Manager.Display#setTags(java.util.List) */
    @Override
    public void setTags(List<Entry<String, String>> tags) {
        ec2InstancesTagsGrid.setValue(tags);
    }

    /** @see org.exoplatform.ide.extension.aws.client.ec2.EC2Manager.Display#getCloseButton() */
    @Override
    public HasClickHandlers getCloseButton() {
        return closeButton;
    }

    /** @see org.exoplatform.ide.extension.aws.client.ec2.EC2Manager.Display#setAllButtonsEnableState(boolean) */
    @Override
    public void setAllButtonsEnableState(boolean isEnable) {
        terminateButton.setEnabled(isEnable);
        rebootButton.setEnabled(isEnable);
        stopButton.setEnabled(isEnable);
        startButton.setEnabled(isEnable);
    }
}
