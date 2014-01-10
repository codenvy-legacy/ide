/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2014] Codenvy, S.A.
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
package com.codenvy.ide.ext.java.jdi.client;

import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.ext.java.jdi.client.debug.DebuggerClientService;
import com.codenvy.ide.ext.java.jdi.shared.DebuggerInfo;
import com.google.web.bindery.event.shared.EventBus;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

/**
 * //
 *
 * @author Artem Zatsarynnyy
 */
@RunWith(MockitoJUnitRunner.class)
public abstract class BaseTest {
    public static final String  DEBUGGER_ID    = "debugger_id";
    public static final boolean DISABLE_BUTTON = false;
    @Mock
    protected DebuggerClientService           service;
    @Mock
    protected DebuggerInfo                    debuggerInfo;
    @Mock
    protected JavaRuntimeLocalizationConstant constants;
    @Mock
    protected EventBus                        eventBus;
    @Mock
    protected NotificationManager             notificationManager;
    @Mock
    protected DtoFactory                      dtoFactory;

    @Before
    public void setUp() {
        when(debuggerInfo.getId()).thenReturn(DEBUGGER_ID);
//        when(resourceProvider.getVfsInfo().getId()).thenReturn(VFS_ID);
//        when(resourceProvider.getActiveProject()).thenReturn(project);
//        when(project.getId()).thenReturn(PROJECT_ID);
//        when(project.getPath()).thenReturn(PROJECT_PATH);
    }
}
