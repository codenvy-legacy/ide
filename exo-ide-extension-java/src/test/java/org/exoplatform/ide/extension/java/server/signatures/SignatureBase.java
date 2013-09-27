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
package org.exoplatform.ide.extension.java.server.signatures;

import com.thoughtworks.qdox.model.JavaClass;

import org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage;
import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;
import org.exoplatform.ide.extension.java.server.parser.JavaDocBuilderVfs;
import org.exoplatform.ide.extension.java.server.parser.JavaTypeToTypeInfoConverter;
import org.exoplatform.ide.extension.java.server.parser.VfsClassLibrary;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.junit.Before;
import org.mockito.Mock;

import java.io.StringReader;
import java.util.HashSet;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 5:34:24 PM Mar 13, 2012 evgen $
 */
public class SignatureBase {

    protected JavaDocBuilderVfs javaDocBuilderVfs;

    @Mock
    private VirtualFileSystem vfs;

    @Mock
    protected CodeAssistantStorage storage;

    @Before
    public void createParser() {
        VfsClassLibrary vfsClassLibrary = new VfsClassLibrary(vfs);
        vfsClassLibrary.addClassLoader(ClassLoader.getSystemClassLoader());
        javaDocBuilderVfs = new JavaDocBuilderVfs(vfs, vfsClassLibrary);

    }

    /**
     * @param b
     * @return
     */
    protected TypeInfo getTypeInfo(StringBuilder b, String classFqn) {
        StringReader reader = new StringReader(b.toString());
        javaDocBuilderVfs.addSource(reader);
        JavaClass clazz = javaDocBuilderVfs.getClassByName(classFqn);
        TypeInfo typeInfo = new JavaTypeToTypeInfoConverter(storage, new HashSet<String>()).convert(clazz);
        return typeInfo;
    }

}