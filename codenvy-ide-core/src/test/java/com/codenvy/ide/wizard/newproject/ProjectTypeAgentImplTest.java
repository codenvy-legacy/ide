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

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.resources.model.Property;
import com.googlecode.gwt.test.GwtModule;
import com.googlecode.gwt.test.GwtTestWithMockito;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Testing {@link ProjectTypeAgentImpl} functionality.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@GwtModule("com.codenvy.ide.Core")
public class ProjectTypeAgentImplTest extends GwtTestWithMockito {
    private ProjectTypeAgentImpl agent;
    private Array<Property> projectProperties;

    @Before
    public void setUp() {
        agent = new ProjectTypeAgentImpl();
        projectProperties = Collections.createArray();
        projectProperties.add(new Property("propertieName", "propertieValue"));
    }

    @Test
    public void testRegister() throws Exception {
        assertEquals(agent.getProjectTypes().size(), 0);

        Array<Property> projectProperties = Collections.createArray();
        projectProperties.add(new Property("propertieName", "propertieValue"));

        agent.register("type", "title", null, "primaryNature", Collections.createArray("secondaryNature"), projectProperties);

        assertEquals(agent.getProjectTypes().size(), 1);
    }

    @Test
    public void testRegisterWhenProjectTypeIsExist() throws Exception {
        assertEquals(agent.getProjectTypes().size(), 0);

        agent.register("type", "title", null, "primaryNature", Collections.createArray("secondaryNature"), projectProperties);

        assertEquals(agent.getProjectTypes().size(), 1);

        agent.register("type", "title", null, "primaryNature", Collections.createArray("secondaryNature"), projectProperties);

        assertEquals(agent.getProjectTypes().size(), 1);
    }
}