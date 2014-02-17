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