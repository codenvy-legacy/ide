/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ide.vfs.impl.fs;

import org.everrest.core.impl.ContainerResponse;
import org.everrest.core.tools.ByteArrayContainerResponseWriter;
import org.exoplatform.ide.vfs.shared.AccessControlEntry;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo.BasicPermissions;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ACLTest extends LocalFileSystemTest
{
   private final String lockToken = "01234567890abcdef";

   private String fileId;
   private String filePath;

   private String lockedFilePath;
   private String lockedFileId;

   private Map<String, Set<BasicPermissions>> accessList;

   @Override
   protected void setUp() throws Exception
   {
      super.setUp();

      filePath = createFile(testRootPath, "ACLTest_File", DEFAULT_CONTENT_BYTES);
      lockedFilePath = createFile(testRootPath, "ACLTest_LockedFile", DEFAULT_CONTENT_BYTES);

      accessList = new HashMap<String, Set<BasicPermissions>>(3);
      accessList.put("admin", EnumSet.of(BasicPermissions.ALL));
      accessList.put("andrew", EnumSet.of(BasicPermissions.READ, BasicPermissions.WRITE));
      accessList.put("john", EnumSet.of(BasicPermissions.READ));

      writeACL(filePath, accessList);
      createLock(lockedFilePath, lockToken);

      fileId = pathToId(filePath);
      lockedFileId = pathToId(lockedFilePath);
   }

   public void testGetACL() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String requestPath = SERVICE_URI + "acl/" + fileId;
      ContainerResponse response = launcher.service("GET", requestPath, BASE_URI, null, null, writer, null);
      log.info(new String(writer.getBody()));
      assertEquals(200, response.getStatus());
      @SuppressWarnings("unchecked")
      List<AccessControlEntry> acl = (List<AccessControlEntry>)response.getEntity();
      assertEquals(accessList, toMap(acl));
   }

   public void testGetACLNoPermissions() throws Exception
   {
      // Remove permissions for current user, see LocalFileSystemTest.setUp()
      accessList.remove("admin");
      writeACL(filePath, accessList);
      // Request must fail since we have not permissions any more to read ACL.
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String requestPath = SERVICE_URI + "acl/" + fileId;
      ContainerResponse response = launcher.service("GET", requestPath, BASE_URI, null, null, writer, null);
      log.info(new String(writer.getBody()));
      assertEquals(403, response.getStatus());
   }

   @SuppressWarnings("unchecked")
   public void testUpdateACL() throws Exception
   {
      String requestPath = SERVICE_URI + "acl/" + fileId;
      // Give write permission for john. No changes for other users.
      String acl = "[{\"principal\":\"john\",\"permissions\":[\"read\", \"write\"]}]";
      Map<String, List<String>> h = new HashMap<String, List<String>>(1);
      h.put("Content-Type", Arrays.asList("application/json"));
      ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, h, acl.getBytes(), null);
      assertEquals(204, response.getStatus());

      accessList.get("john").add(BasicPermissions.WRITE);
      // check backend
      Map<String, Set<BasicPermissions>> updatedAccessList = readACL(filePath);
      log.info(updatedAccessList);
      assertEquals(accessList, updatedAccessList);

      // check API
      assertEquals(accessList,
         toMap((List<AccessControlEntry>)launcher.service("GET", requestPath, BASE_URI, null, null, null).getEntity()));
   }

   @SuppressWarnings("unchecked")
   public void testUpdateACLOverride() throws Exception
   {
      String requestPath = SERVICE_URI + "acl/" + fileId + '?' + "override=" + true;
      // Give 'all' rights to admin and take away all rights for other users.
      String acl = "[{\"principal\":\"admin\",\"permissions\":[\"all\"]}]";
      Map<String, List<String>> h = new HashMap<String, List<String>>(1);
      h.put("Content-Type", Arrays.asList("application/json"));
      ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, h, acl.getBytes(), null);
      assertEquals(204, response.getStatus());

      accessList.remove("andrew");
      accessList.remove("john");

      // check backend
      Map<String, Set<BasicPermissions>> updatedAccessList = readACL(filePath);
      log.info(updatedAccessList);
      assertEquals(accessList, updatedAccessList);

      // check API
      assertEquals(accessList,
         toMap((List<AccessControlEntry>)launcher.service("GET", requestPath, BASE_URI, null, null, null).getEntity()));
   }

   @SuppressWarnings("unchecked")
   public void testRemoveACL() throws Exception
   {
      // Send empty list of permissions. It means remove all restriction.
      String requestPath = SERVICE_URI + "acl/" + fileId + '?' + "override=" + true;
      String acl = "[]";
      Map<String, List<String>> h = new HashMap<String, List<String>>(1);
      h.put("Content-Type", Arrays.asList("application/json"));
      ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, h, acl.getBytes(), null);
      assertEquals(204, response.getStatus());

      // check backend
      Map<String, Set<BasicPermissions>> updatedPermissions = readACL(filePath);
      log.info(updatedPermissions);
      assertTrue(updatedPermissions.isEmpty());

      // check API
      List<AccessControlEntry> updatedAcl =
         (List<AccessControlEntry>)launcher.service("GET", requestPath, BASE_URI, null, null, null).getEntity();
      assertTrue(updatedAcl.isEmpty());
   }

   @SuppressWarnings("unchecked")
   public void testUpdateACLNoPermissions() throws Exception
   {
      // Remove permissions for current user, see LocalFileSystemTest.setUp()
      accessList.put("admin", EnumSet.of(BasicPermissions.READ));
      writeACL(filePath, accessList);

      String acl = "[{\"principal\":\"admin\",\"permissions\":[\"all\"]}]";
      Map<String, List<String>> h = new HashMap<String, List<String>>(1);
      h.put("Content-Type", Arrays.asList("application/json"));

      // Request must fail since we have not permissions any more to update ACL.
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String requestPath = SERVICE_URI + "acl/" + fileId;
      ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, h, acl.getBytes(), writer, null);
      assertEquals(403, response.getStatus());
      log.info(new String(writer.getBody()));

      // ACL must not be changed.
      // check backend
      Map<String, Set<BasicPermissions>> updatedAccessList = readACL(filePath);
      log.info(updatedAccessList);
      assertEquals(accessList, updatedAccessList);

      // check API
      assertEquals(accessList,
         toMap((List<AccessControlEntry>)launcher.service("GET", requestPath, BASE_URI, null, null, null).getEntity()));
   }

   @SuppressWarnings("unchecked")
   public void testUpdateACLLocked() throws Exception
   {
      String acl = "[{\"principal\":\"john\",\"permissions\":[\"read\", \"write\"]}," +
         "{\"principal\":\"any\",\"permissions\":null},{\"principal\":\"admin\",\"permissions\":[\"read\"]}]";
      Map<String, List<String>> h = new HashMap<String, List<String>>(1);
      h.put("Content-Type", Arrays.asList("application/json"));

      String requestPath = SERVICE_URI + "acl/" + lockedFileId + '?' + "lockToken=" + lockToken;
      ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, h, acl.getBytes(), null);

      assertEquals(204, response.getStatus());

      Map<String, Set<BasicPermissions>> thisTestAccessList = new HashMap<String, Set<BasicPermissions>>(2);
      thisTestAccessList.put("john", EnumSet.of(BasicPermissions.READ, BasicPermissions.WRITE));
      thisTestAccessList.put("admin", EnumSet.of(BasicPermissions.READ));

      // check backend
      Map<String, Set<BasicPermissions>> updatedAccessList = readACL(lockedFilePath);
      log.info(updatedAccessList);
      assertEquals(thisTestAccessList, updatedAccessList);

      // check API
      assertEquals(thisTestAccessList,
         toMap((List<AccessControlEntry>)launcher.service("GET", requestPath, BASE_URI, null, null, null).getEntity()));
   }

   @SuppressWarnings("unchecked")
   public void testUpdateACLLocked_NoLockToken() throws Exception
   {
      String acl = "[{\"principal\":\"john\",\"permissions\":[\"read\", \"write\"]}," +
         "{\"principal\":\"any\",\"permissions\":null},{\"principal\":\"admin\",\"permissions\":[\"read\"]}]";
      Map<String, List<String>> h = new HashMap<String, List<String>>(1);
      h.put("Content-Type", Arrays.asList("application/json"));

      String requestPath = SERVICE_URI + "acl/" + lockedFileId;
      ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, h, acl.getBytes(), null);

      assertEquals(423, response.getStatus());

      // ACL must not be updated.
      List<AccessControlEntry> updatedAcl =
         (List<AccessControlEntry>)launcher.service("GET", requestPath, BASE_URI, null, null, null).getEntity();
      assertTrue(updatedAcl.isEmpty());
   }

   private Map<String, Set<BasicPermissions>> toMap(List<AccessControlEntry> acl)
   {
      Map<String, Set<BasicPermissions>> map = new HashMap<String, Set<BasicPermissions>>(acl.size());
      for (AccessControlEntry ace : acl)
      {
         String principal = ace.getPrincipal();
         Set<BasicPermissions> permissions = map.get(principal);
         if (permissions == null)
         {
            permissions = EnumSet.noneOf(BasicPermissions.class);
            map.put(principal, permissions);
         }
         Set<String> strPermissions = ace.getPermissions();
         if (!(strPermissions == null || strPermissions.isEmpty()))
         {
            for (String strPermission : strPermissions)
            {
               permissions.add(BasicPermissions.fromValue(strPermission));
            }
         }
      }
      return map;
   }
}
