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
package org.exoplatform.ide.extension.aws.client.beanstalk.environments.configuration;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.TextInput;

/**
 * @author <a href="mailto:azatsarynnyy@exoplatform.com">Artem Zatsarynnyy</a>
 * @version $Id: ContainerTabPain.java Oct 8, 2012 5:38:06 PM azatsarynnyy $
 */
public class ContainerTabPain extends Composite {

    private static ContainerTabPainUiBinder uiBinder = GWT.create(ContainerTabPainUiBinder.class);

    interface ContainerTabPainUiBinder extends UiBinder<Widget, ContainerTabPain> {
    }

    private static final String INIT_JVM_HEAP_SIZE_FIELD_ID = "ideContainerTabPainInitialJVMHeapSizeField";

    private static final String MAX_JVM_HEAP_SIZE_FIELD_ID = "ideContainerTabPainMaximumJVMHeapSizeField";

    private static final String MAX_PERM_SIZE_FIELD_ID = "ideContainerTabPainMaxPermSizeField";

    private static final String JVM_OPTIONS_FIELD_ID = "ideContainerTabPainJVMOptionsField";

    @UiField
    TextInput initialJVMHeapSizeField;

    @UiField
    TextInput maximumJVMHeapSizeField;

    @UiField
    TextInput maxPermSizeField;

    @UiField
    TextInput jvmOptionsField;

    public ContainerTabPain() {
        initWidget(uiBinder.createAndBindUi(this));

        initialJVMHeapSizeField.setName(INIT_JVM_HEAP_SIZE_FIELD_ID);
        maximumJVMHeapSizeField.setName(MAX_JVM_HEAP_SIZE_FIELD_ID);
        maxPermSizeField.setName(MAX_PERM_SIZE_FIELD_ID);
        jvmOptionsField.setName(JVM_OPTIONS_FIELD_ID);
    }

    /** @return the appHealthCheckUrlField */
    public TextInput getInitialJVMHeapSizeField() {
        return initialJVMHeapSizeField;
    }

    /** @return the healthCheckIntervalField */
    public TextInput getMaximumJVMHeapSizeField() {
        return maximumJVMHeapSizeField;
    }

    /** @return the maxPermSizeField */
    public TextInput getMaxPermSizeField() {
        return maxPermSizeField;
    }

    /** @return the jvmOptionsField */
    public TextInput getJVMOptionsField() {
        return jvmOptionsField;
    }
}
