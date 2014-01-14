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
package com.codenvy.ide.navigation;

import com.codenvy.ide.api.resources.FileEvent;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.resources.model.File;
import com.codenvy.ide.resources.model.Folder;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Resource;
import com.google.web.bindery.event.shared.EventBus;
import com.googlecode.gwt.test.GwtModule;
import com.googlecode.gwt.test.GwtTestWithMockito;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test for {@link NavigateToFilePresenter}.
 * 
 * @author Ann Shumilova
 */
@GwtModule("com.codenvy.ide.Core")
public class NavigateToFileTest extends GwtTestWithMockito {
    
    public static final String  PROJECT_ID      = "projectID";
    public static final String  PROJECT_PATH    = "/test";
    public static final String  VFS_ID          = "vfsid";
    public static final String  PROJECT_NAME    = "test";
    public static final String  FOLDER_NAME    = "folder";
    public static final String  FILE_1_IN_FOLDER_NAME    = "file1.txt";
    public static final String  FILE_2_IN_FOLDER_NAME    = "file2.html";
    public static final String  FILE_IN_ROOT_NAME    = "pom.xml";
    
    
    @Mock
    private NavigateToFileView      view;
    @Mock
    private Project                 project;
    @Mock
    private ResourceProvider resourceProvider;
    @Mock
    private EventBus eventBus;
    private NavigateToFilePresenter presenter;
    
    @Before
    public void setUp() {
        Array<Resource> children = Collections.createArray();
        Folder folder = Mockito.mock(Folder.class);
        Mockito.when(folder.getPath()).thenReturn(PROJECT_PATH + "/" + FOLDER_NAME);
       // Mockito.when(folder.getParent()).thenReturn(project);
        
        File fileInFolder1 = Mockito.mock(File.class);
        Mockito.when(fileInFolder1.getName()).thenReturn(FILE_1_IN_FOLDER_NAME);
        Mockito.when(fileInFolder1.getPath()).thenReturn(PROJECT_PATH + "/" + FOLDER_NAME + "/" + FILE_1_IN_FOLDER_NAME);
      //  Mockito.when(fileInFolder1.getParent()).thenReturn(folder);
        
        File fileInFolder2 = Mockito.mock(File.class);
        Mockito.when(fileInFolder2.getName()).thenReturn(FILE_2_IN_FOLDER_NAME);
        Mockito.when(fileInFolder2.getPath()).thenReturn(PROJECT_PATH + "/" + FOLDER_NAME + "/" + FILE_2_IN_FOLDER_NAME);
   //     Mockito.when(fileInFolder2.getParent()).thenReturn(folder);
        
        File fileInRoot = Mockito.mock(File.class);
        Mockito.when(fileInRoot.getName()).thenReturn(FILE_IN_ROOT_NAME);
        Mockito.when(fileInRoot.getPath()).thenReturn(PROJECT_PATH + "/" + FILE_IN_ROOT_NAME);
   //     Mockito.when(fileInRoot.getParent()).thenReturn(project);
        
        when(resourceProvider.getActiveProject()).thenReturn(project);
        when(project.getChildren()).thenReturn(children);
        
        presenter = new NavigateToFilePresenter(view, resourceProvider, eventBus);
    }
    
    @Test
    public void testShowDialog() throws Exception {
        presenter.showDialog();
        
        verify(view).setFiles((Array<String>)anyObject());
        verify(view).showDialog();
        verify(view).clearInput();
        verify(view).focusInput();
    }
    
    @Test
    public void testOnFileSelected() throws Exception {
        presenter.showDialog();
        String displayName = FILE_IN_ROOT_NAME + " ("+ PROJECT_NAME + ")";
        when(view.getFile()).thenReturn(displayName);
        
        presenter.onFileSelected();
        verify(view).getFile();
        verify(view).close();
        verify(view).clearInput();
        verify(eventBus).fireEvent((FileEvent)anyObject());
    }
}
