/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.wizard.newproject;

import com.codenvy.api.project.shared.dto.ProjectTypeDescriptor;
import com.googlecode.gwt.test.GwtModule;
import com.googlecode.gwt.test.GwtTestWithMockito;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * Testing {@link ProjectTypeDescriptorRegistryImpl} functionality.
 *
 * @author Artem Zatsarynnyy
 */
@GwtModule("com.codenvy.ide.Core")
public class ProjectTypeDescriptionRegistryImplTest extends GwtTestWithMockito {
    private ProjectTypeDescriptorRegistryImpl registry;

    @Before
    public void setUp() {
        registry = new ProjectTypeDescriptorRegistryImpl();
    }

    @Test
    public void testRegister() throws Exception {
        assertEquals(registry.getDescriptors().size(), 0);

        registry.registerDescriptor(mock(ProjectTypeDescriptor.class));

        assertEquals(registry.getDescriptors().size(), 1);
    }

    @Test
    public void testRegisterWhenProjectTypeIsExist() throws Exception {
        assertEquals(registry.getDescriptors().size(), 0);

        registry.registerDescriptor(mock(ProjectTypeDescriptor.class));

        assertEquals(registry.getDescriptors().size(), 1);

        registry.registerDescriptor(mock(ProjectTypeDescriptor.class));

        assertEquals(registry.getDescriptors().size(), 1);
    }
}