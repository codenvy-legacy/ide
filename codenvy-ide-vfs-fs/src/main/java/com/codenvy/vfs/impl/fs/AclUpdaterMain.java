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
package com.codenvy.vfs.impl.fs;

import com.codenvy.api.vfs.shared.dto.Principal;
import com.codenvy.api.vfs.shared.dto.VirtualFileSystemInfo.BasicPermissions;
import com.codenvy.dto.server.DtoFactory;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.nio.file.Files;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AclUpdaterMain {

    public AclUpdaterMain(java.io.File root, java.io.File list) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(list));
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            java.io.File vfsRoot = getVfsRootFolder(root, line);
            if (!vfsRoot.exists()) {
                System.out.printf("Directory '%s' does not exists.%n", vfsRoot);
                System.exit(1);
            }
            java.io.File aclDir = new java.io.File(vfsRoot, FSMountPoint.ACL_DIR);
            if (!(aclDir.exists() || aclDir.mkdirs())) {
                System.out.println("Cannot create directory for ACL.");
                System.exit(1);
            }

            java.io.File aclFile = new java.io.File(aclDir, FSMountPoint.ACL_FILE_SUFFIX);
            try (DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(aclFile)))) {
                getACL().write(dos);
            }
            java.io.File cacheResetDir = new java.io.File(vfsRoot, FSMountPoint.SERVICE_DIR + java.io.File.separatorChar + "cache");
            if (!(cacheResetDir.exists() || cacheResetDir.mkdirs())) {
                System.out.println("Cannot create directory.");
                System.exit(1);
            }
            java.nio.file.Path resetFilePath = new java.io.File(cacheResetDir, "reset").toPath();
            if (!Files.exists(resetFilePath)) {
                Files.createFile(resetFilePath);
            }
            System.out.printf("Successfully write ACL for %s at %s%n", line, vfsRoot);
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Root directory to all VFS and path to file with list of workspaces required.");
            System.exit(1);
        }
        java.io.File root = new java.io.File(args[0]);
        if (!root.isDirectory()) {
            System.out.printf("Directory '%s' not exists or not directory.%n", root);
            System.exit(1);
        }
        java.io.File list = new java.io.File(args[1]);
        if (!list.isFile()) {
            System.out.println("File not exists or not regular file.");
            System.exit(1);
        }
        new AclUpdaterMain(root, list);
    }

    private AccessControlList getACL() {
        Map<Principal, Set<BasicPermissions>> acl = new HashMap<>();
        Principal principal = DtoFactory.getInstance().createDto(Principal.class);
        principal.setName("workspace/developer");
        principal.setType(Principal.Type.GROUP);
        acl.put(principal, EnumSet.of(BasicPermissions.ALL));
        return new AccessControlList(acl);
    }

    private java.io.File getVfsRootFolder(java.io.File root, String ws) {
        return EnvironmentContextLocalFSMountStrategy.calculateDirPath(root, ws);
    }
}
