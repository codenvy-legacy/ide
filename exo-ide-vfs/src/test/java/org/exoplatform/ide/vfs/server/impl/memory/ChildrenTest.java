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
import org.everrest.core.tools.ByteArrayContainerResponseWriter;
import org.exoplatform.ide.vfs.server.impl.memory.context.MemoryFile;
import org.exoplatform.ide.vfs.server.impl.memory.context.MemoryFolder;
import org.exoplatform.ide.vfs.server.impl.memory.context.MemoryItem;
import org.exoplatform.ide.vfs.shared.AccessControlEntry;
import org.exoplatform.ide.vfs.shared.AccessControlEntryImpl;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemImpl;
import org.exoplatform.ide.vfs.shared.ItemList;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.Principal;
import org.exoplatform.ide.vfs.shared.PrincipalImpl;
import org.exoplatform.ide.vfs.shared.Property;
import org.exoplatform.ide.vfs.shared.PropertyImpl;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfoImpl;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: ChildrenTest.java 79579 2012-02-17 13:27:25Z andrew00x $
 */
public class ChildrenTest extends MemoryFileSystemTest {
    private String folderId;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        String name = getClass().getName();
        MemoryFolder childrenTestFolder = new MemoryFolder(name);

        testRoot.addChild(childrenTestFolder);

        MemoryFolder folder = new MemoryFolder("ChildrenTest_FOLDER");
        childrenTestFolder.addChild(folder);

        MemoryFile file = new MemoryFile("ChildrenTest_FILE01", "text/plain",
                                         new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));
        file.updateProperties(Arrays.<Property>asList(new PropertyImpl("PropertyA", "A"), new PropertyImpl("PropertyB", "B")));
        folder.addChild(file);

        MemoryFolder folder1 = new MemoryFolder("ChildrenTest_FOLDER01");
        folder1.updateProperties(Arrays.<Property>asList(new PropertyImpl("PropertyA", "A"), new PropertyImpl("PropertyB", "B")));
        folder.addChild(folder1);

        MemoryFolder folder2 = new MemoryFolder("ChildrenTest_FOLDER02");
        folder2.updateProperties(Arrays.<Property>asList(new PropertyImpl("PropertyA", "A"), new PropertyImpl("PropertyB", "B")));
        folder.addChild(folder2);

        memoryContext.putItem(childrenTestFolder);
        folderId = folder.getId();
    }

    public void testGetChildren() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String path = SERVICE_URI + "children/" + folderId;
        ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
        assertEquals(200, response.getStatus());
        //log.info(new String(writer.getBody()));
        @SuppressWarnings("unchecked")
        ItemList<Item> children = (ItemList<Item>)response.getEntity();
        List<String> list = new ArrayList<String>(3);
        for (Item i : children.getItems()) {
            validateLinks(i);
            list.add(i.getName());
        }
        assertEquals(3, list.size());
        assertTrue(list.contains("ChildrenTest_FOLDER01"));
        assertTrue(list.contains("ChildrenTest_FOLDER02"));
        assertTrue(list.contains("ChildrenTest_FILE01"));
    }

    public void testGetChildrenNoPermissions() throws Exception {
        AccessControlEntry ace = new AccessControlEntryImpl();
        ace.setPrincipal(new PrincipalImpl("admin", Principal.Type.USER));
        ace.setPermissions(new HashSet<String>(Arrays.asList(VirtualFileSystemInfoImpl.BasicPermissions.ALL.value())));
        memoryContext.getItem(folderId).updateACL(Arrays.asList(ace), true);

        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String path = SERVICE_URI + "children/" + folderId;
        ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
        assertEquals(403, response.getStatus());
        log.info(new String(writer.getBody()));
    }

    public void testGetChildrenNoPermissions2() throws Exception {
        MemoryFolder folder = (MemoryFolder)memoryContext.getItem(folderId);
        MemoryItem protectedItem = folder.getChild("ChildrenTest_FILE01");
        AccessControlEntry ace = new AccessControlEntryImpl();
        ace.setPrincipal(new PrincipalImpl("admin", Principal.Type.USER));
        ace.setPermissions(new HashSet<String>(Arrays.asList(VirtualFileSystemInfoImpl.BasicPermissions.ALL.value())));
        // after that item must not appear in response
        protectedItem.updateACL(Arrays.asList(ace), true);

        // Have permission for read folder but have not permission to read one of its child.
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String path = SERVICE_URI + "children/" + folderId;
        ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
        assertEquals(200, response.getStatus());
        //log.info(new String(writer.getBody()));
        @SuppressWarnings("unchecked")
        ItemList<Item> children = (ItemList<Item>)response.getEntity();
        List<String> list = new ArrayList<String>(2);
        for (Item i : children.getItems()) {
            validateLinks(i);
            list.add(i.getName());
        }
        assertEquals(2, list.size());
        assertTrue(list.contains("ChildrenTest_FOLDER01"));
        assertTrue(list.contains("ChildrenTest_FOLDER02"));
    }

    @SuppressWarnings("unchecked")
    public void testGetChildrenPagingSkipCount() throws Exception {
        // Get all children.
        String path = SERVICE_URI + "children/" + folderId;
        ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, null);
        assertEquals(200, response.getStatus());
        ItemList<Item> children = (ItemList<Item>)response.getEntity();
        List<Object> all = new ArrayList<Object>(3);
        for (Item i : children.getItems()) {
            all.add(i.getName());
        }

        Iterator<Object> iteratorAll = all.iterator();
        iteratorAll.next();
        iteratorAll.remove();

        // Skip first item in result.
        path = SERVICE_URI + "children/" + folderId + "?" + "skipCount=" + 1;
        checkPage(path, "GET", ItemImpl.class.getMethod("getName"), all);
    }

    @SuppressWarnings("unchecked")
    public void testGetChildrenPagingMaxItems() throws Exception {
        // Get all children.
        String path = SERVICE_URI + "children/" + folderId;
        ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, null);
        assertEquals(200, response.getStatus());
        ItemList<Item> children = (ItemList<Item>)response.getEntity();
        List<Object> all = new ArrayList<Object>(3);
        for (Item i : children.getItems()) {
            all.add(i.getName());
        }

        // Exclude last item from result.
        path = SERVICE_URI + "children/" + folderId + "?" + "maxItems=" + 2;
        all.remove(2);
        checkPage(path, "GET", ItemImpl.class.getMethod("getName"), all);
    }

    @SuppressWarnings("unchecked")
    public void testGetChildrenNoPropertyFilter() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        // Get children without filter.
        String path = SERVICE_URI + "children/" + folderId;
        ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
        //log.info(new String(writer.getBody()));
        assertEquals(200, response.getStatus());
        ItemList<Item> children = (ItemList<Item>)response.getEntity();
        assertEquals(3, children.getItems().size());
        for (Item i : children.getItems()) {
            // No properties without filter. 'none' filter is used if nothing set by client.
            assertFalse(hasProperty(i, "PropertyA"));
            assertFalse(hasProperty(i, "PropertyB"));
        }
    }

    @SuppressWarnings("unchecked")
    public void testGetChildrenPropertyFilter() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        // Get children and apply filter for properties.
        String propertyFilter = "PropertyA";
        String path = SERVICE_URI + "children/" + folderId + "?" + "propertyFilter=" + propertyFilter;
        ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
        //log.info(new String(writer.getBody()));
        assertEquals(200, response.getStatus());
        ItemList<Item> children = (ItemList<Item>)response.getEntity();
        assertEquals(3, children.getItems().size());
        for (Item i : children.getItems()) {
            assertTrue(hasProperty(i, "PropertyA"));
            assertFalse(hasProperty(i, "PropertyB")); // must be excluded
        }
    }

    @SuppressWarnings("unchecked")
    public void testGetChildrenTypeFilter() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        // Get children and apply filter for properties.
        String path = SERVICE_URI + "children/" + folderId + "?" + "itemType=" + "folder";
        ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
        //log.info(new String(writer.getBody()));
        assertEquals(200, response.getStatus());
        ItemList<Item> children = (ItemList<Item>)response.getEntity();
        assertEquals(2, children.getItems().size());
        for (Item i : children.getItems()) {
            assertTrue(i.getItemType() == ItemType.FOLDER);
        }
    }

    @SuppressWarnings("rawtypes")
    private boolean hasProperty(Item i, String propertyName) {
        List<Property> properties = i.getProperties();
        if (properties.size() == 0) {
            return false;
        }
        for (Property p : properties) {
            if (p.getName().equals(propertyName)) {
                return true;
            }
        }
        return false;
    }
}
