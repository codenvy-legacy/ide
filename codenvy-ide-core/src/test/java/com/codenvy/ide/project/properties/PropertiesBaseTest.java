/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
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
package com.codenvy.ide.project.properties;

import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Property;
import com.codenvy.ide.resources.model.VirtualFileSystemInfo;
import com.google.web.bindery.event.shared.EventBus;
import com.googlecode.gwt.test.GwtModule;
import com.googlecode.gwt.test.GwtTestWithMockito;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

/**
 * @author Ann Shumilova
 */
@RunWith(MockitoJUnitRunner.class)
public abstract class PropertiesBaseTest {

    public static final String                      PROJECT_ID            = "projectID";
    public static final String                      PROJECT_PATH          = "/test";
    public static final String                      VFS_ID                = "vfsid";
    public static final boolean                     ENABLE_BUTTON         = true;
    public static final boolean                     DISABLE_BUTTON        = false;
    public static final String                      PROJECT_NAME          = "test";

    public static final String                      PROPERTY_NATURE_MIXIN = "nature.mixin";
    public static final String                      PROPERTY_RUNNER_NAME  = "runner_name";
    public static final String                      PROPERTY_MIMETYPE     = "vfs:mimeType";
    public static final String                      PROPERTY_PROJECT_TYPE = "vfs:projectType";
    public static final String                      VALUE_NATURE_MIXIN    = "Java";
    public static final String                      VALUE_RUNNER_NAME     = "maven";
    public static final String                      VALUE_MIMETYPE        = "text/vnd.ideproject+directory";
    public static final String                      VALUE_PROJECT_TYPE    = "Java";


    @Mock
    protected Project                               project;
    @Mock
    protected ResourceProvider                      resourceProvider;
    @Mock
    protected EventBus                              eventBus;
    @Mock
    protected NotificationManager                   notificationManager;
    @Mock
    protected ProjectPropertiesLocalizationConstant localization;
    @Mock
    protected VirtualFileSystemInfo                 vfsInfo;

    protected Array<Property>                       properties;

    @Before
    public void disarm() {
        properties = Collections.createArray();
        properties.add(new Property(PROPERTY_NATURE_MIXIN, VALUE_NATURE_MIXIN));
        properties.add(new Property(PROPERTY_RUNNER_NAME, VALUE_RUNNER_NAME));
        properties.add(new Property(PROPERTY_MIMETYPE, VALUE_MIMETYPE));
        properties.add(new Property(PROPERTY_PROJECT_TYPE, VALUE_PROJECT_TYPE));

        when(resourceProvider.getActiveProject()).thenReturn(project);
        when(resourceProvider.getVfsInfo()).thenReturn(vfsInfo);
        when(project.getId()).thenReturn(PROJECT_ID);
        when(project.getPath()).thenReturn(PROJECT_PATH);
        when(project.getName()).thenReturn(PROJECT_NAME);
        when(project.getProperties()).thenReturn(properties);
    }
}
