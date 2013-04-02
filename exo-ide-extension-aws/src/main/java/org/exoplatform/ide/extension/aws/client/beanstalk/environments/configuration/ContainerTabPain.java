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
