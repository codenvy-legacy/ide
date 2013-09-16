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
package org.exoplatform.ide.vfs.server.impl.memory;

import org.everrest.core.impl.ContainerResponse;
import org.exoplatform.ide.vfs.server.impl.memory.context.MemoryFile;
import org.exoplatform.ide.vfs.server.impl.memory.context.MemoryFolder;
import org.exoplatform.ide.vfs.shared.Property;
import org.exoplatform.ide.vfs.shared.PropertyFilter;
import org.exoplatform.ide.vfs.shared.PropertyImpl;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: UpdateTest.java 77229 2011-12-03 16:56:34Z andrew00x $
 */
public class UpdateTest extends MemoryFileSystemTest {
    private String fileId;
    private String folderId;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        String name = getClass().getName();

        MemoryFolder updateTestFolder = new MemoryFolder(name);
        testRoot.addChild(updateTestFolder);

        MemoryFile file = new MemoryFile("UpdateTest_FILE", "text/plain",
                                         new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));
        updateTestFolder.addChild(file);
        fileId = file.getId();

        MemoryFolder folder = new MemoryFolder("UpdateTest_FOLDER");
        updateTestFolder.addChild(folder);
        folderId = folder.getId();

        memoryContext.putItem(updateTestFolder);
    }

    public void testUpdatePropertiesFile() throws Exception {
        String properties = "[{\"name\":\"MyProperty\", \"value\":[\"MyValue\"]}]";
        doUpdate(fileId, properties);
        MemoryFile file = (MemoryFile)memoryContext.getItem(fileId);
        List<String> values = file.getProperties(PropertyFilter.valueOf("MyProperty")).get(0).getValue();
        assertEquals("MyValue", values.get(0));
    }

    public void testUpdatePropertiesAndChangeFolderType() throws Exception {
        MemoryFolder folder = (MemoryFolder)memoryContext.getItem(folderId);
        assertFalse(folder.isProject());
        String properties = "[{\"name\":\"vfs:mimeType\", \"value\":[\"text/vnd.ideproject+directory\"]}]";
        doUpdate(folderId, properties);
        folder = (MemoryFolder)memoryContext.getItem(folderId);
        assertTrue("Regular folder must be converted to project. ", folder.isProject());
    }

    public void testUpdatePropertiesAndChangeFolderType2() throws Exception {
        MemoryFolder folder = (MemoryFolder)memoryContext.getItem(folderId);
        folder.updateProperties(Arrays.<Property>asList(new PropertyImpl("vfs:mimeType", "text/vnd.ideproject+directory")));
        assertTrue(folder.isProject());
        String properties = "[{\"name\":\"vfs:mimeType\", \"value\":[\"text/directory\"]}]";
        doUpdate(folderId, properties);
        folder = (MemoryFolder)memoryContext.getItem(folderId);
        assertFalse("Project must be converted to regular folder . ", folder.isProject());
    }

    public void doUpdate(String id, String rawData) throws Exception {
        String path = SERVICE_URI + "item/" + id;
        Map<String, List<String>> h = new HashMap<String, List<String>>(1);
        h.put("Content-Type", Arrays.asList("application/json"));
        ContainerResponse response = launcher.service("POST", path, BASE_URI, h, rawData.getBytes(), null);
        assertEquals(200, response.getStatus());
    }
}
