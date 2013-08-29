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
package org.exoplatform.ide.vfs.impl.jcr;

import org.everrest.core.impl.ContainerResponse;
import org.everrest.core.tools.ByteArrayContainerResponseWriter;
import org.exoplatform.ide.vfs.shared.AccessControlEntry;
import org.exoplatform.services.jcr.access.PermissionType;
import org.exoplatform.services.jcr.core.ExtendedNode;
import org.exoplatform.services.jcr.core.ExtendedSession;

import java.io.ByteArrayInputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: GetACLTest.java 75032 2011-10-13 15:24:34Z andrew00x $
 */
public class GetACLTest extends JcrFileSystemTest
{
   private Node getAclTestNode;
   private String fileID;

   /**
    * @see org.exoplatform.ide.vfs.impl.jcr.JcrFileSystemTest#setUp()
    */
   @Override
   protected void setUp() throws Exception
   {
      super.setUp();
      String name = getClass().getName();
      getAclTestNode = testRoot.addNode(name, "nt:unstructured");
      getAclTestNode.addMixin("exo:privilegeable");

      Node fileNode = getAclTestNode.addNode("GetACLTest_FILE", "nt:file");
      Node contentNode = fileNode.addNode("jcr:content", "nt:resource");
      contentNode.setProperty("jcr:mimeType", "text/plain");
      contentNode.setProperty("jcr:lastModified", Calendar.getInstance());
      contentNode.setProperty("jcr:data", new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));
      fileNode.addMixin("exo:privilegeable");
      ((ExtendedNode)fileNode).setPermission("root", PermissionType.ALL);
      ((ExtendedNode)fileNode).setPermission("john", new String[]{PermissionType.READ});
      fileID = ((ExtendedNode)fileNode).getIdentifier();

      session.save();
   }

   public void testGetACL() throws Exception
   {
      String path = SERVICE_URI + "acl/" + fileID;
      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, null);
      assertEquals(200, response.getStatus());
      @SuppressWarnings("unchecked")
      List<AccessControlEntry> acl = (List<AccessControlEntry>)response.getEntity();
      for (AccessControlEntry ace : acl)
      {
         if ("root".equals(ace.getPrincipal()))
            ace.getPermissions().contains("all");
         if ("john".equals(ace.getPrincipal()))
            ace.getPermissions().contains("read");
      }
   }

   public void testGetACLNoPermissions() throws Exception
   {
      Map<String, String[]> permissions = new HashMap<String, String[]>(2);
      permissions.put("root", PermissionType.ALL);
      ((ExtendedNode)((ExtendedSession)session).getNodeByIdentifier(fileID)).setPermissions(permissions);
      session.save();

      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = SERVICE_URI + "acl/" + fileID;
      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
      assertEquals(403, response.getStatus());
      log.info(new String(writer.getBody()));
   }
}
