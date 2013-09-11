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
package org.exoplatform.ide.extension.java.server;

import org.exoplatform.ide.codeassistant.jvm.CodeAssistantException;
import org.exoplatform.ide.codeassistant.jvm.shared.FieldInfo;
import org.exoplatform.ide.codeassistant.jvm.shared.MethodInfo;
import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemList;
import org.exoplatform.ide.vfs.shared.PropertyFilter;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}: Nov 28, 2011 4:34:52 PM evgen $
 */
public class JavaDocBuilderVfsTest extends JavaDocBase {

    @Test
    public void projectParserConstruntorTest() throws CodeAssistantException, VirtualFileSystemException {
        TypeInfo clazz =
                javaCa.getClassByFqnFromProject("org.exoplatform.ide.client.autocompletion.AutoCompletionManager",
                                                project.getId(), VFS_ID);
        Assert.assertEquals("org.exoplatform.ide.client.autocompletion.AutoCompletionManager", clazz.getName());
        Assert.assertEquals(1, clazz.getModifiers());
        List<MethodInfo> methods = clazz.getMethods();
        List<MethodInfo> constructors = new ArrayList<MethodInfo>();
        for (MethodInfo methodInfo : methods) {
            if (methodInfo.isConstructor())
                constructors.add(methodInfo);
        }
        Assert.assertEquals(1, constructors.size());
        MethodInfo info = constructors.get(0);
        Assert.assertEquals("AutoCompletionManager", info.getName());
        Assert.assertEquals("org.exoplatform.ide.client.autocompletion.AutoCompletionManager", info.getDeclaringClass());
        Assert.assertEquals("com.google.gwt.event.shared.HandlerManager", info.getParameterTypes().get(0));
    }

    @Test
    public void projectParserFieldsTest() throws CodeAssistantException, VirtualFileSystemException {
        TypeInfo clazz =
                javaCa.getClassByFqnFromProject("org.exoplatform.ide.client.autocompletion.AutoCompletionManager",
                                                project.getId(), VFS_ID);
        Assert.assertEquals("org.exoplatform.ide.client.autocompletion.AutoCompletionManager", clazz.getName());
        List<FieldInfo> fields = clazz.getFields();
        Assert.assertEquals(6, fields.size());
        FieldInfo info = fields.get(0);
        Assert.assertNotNull(info.getName());
        Assert.assertNotNull(info.getType());
        Assert.assertNotNull(info.getDeclaringClass());
        Assert.assertNotNull(info.getModifiers());
        Assert.assertNotNull(info.getDescriptor());
    }

    @Test
    public void projectParserMethodsTest() throws CodeAssistantException, VirtualFileSystemException {
        TypeInfo clazz =
                javaCa.getClassByFqnFromProject("org.exoplatform.ide.client.autocompletion.AutoCompletionManager",
                                                project.getId(), VFS_ID);

        List<MethodInfo> methods = clazz.getMethods();
        Assert.assertNotNull(methods);
        Assert.assertEquals(7, methods.size());
        MethodInfo methodInfo = methods.get(0);
        Assert.assertNotNull(methodInfo.getName());
        Assert.assertNotNull(methodInfo.getModifiers());
        Assert.assertNotNull(methodInfo.getParameterTypes());
        Assert.assertNotNull(methodInfo.getDeclaringClass());
    }

    @Test
    public void classWithDafaultConstructor() throws Exception {
        TypeInfo clazz = javaCa.getClassByFqnFromProject("org.exoplatform.ide.client.IDEShell", project.getId(), VFS_ID);
        List<MethodInfo> constructors = new ArrayList<MethodInfo>();
        for (MethodInfo methodInfo : clazz.getMethods()) {
            if (methodInfo.isConstructor())
                constructors.add(methodInfo);
        }
        Assert.assertEquals(1, constructors.size());
    }
    
    @Test
    public void bugIDE2670() throws Exception {
        Folder project = vfs.createFolder(vfs.getInfo().getRoot().getId(), "bug-ide-2670");
        vfs.importZip(project.getId(), Thread.currentThread().getContextClassLoader().getResourceAsStream("bug-ide-2670.zip"), true);
        TypeInfo clazz = javaCa.getClassByFqnFromProject("dashboard.AmazonLogsReader", project.getId(), VFS_ID);
        Assert.assertNull(clazz);//Looks like bug in QDox http://jira.codehaus.org/browse/QDOX-241 fixed in 2.0 version
    }

}
