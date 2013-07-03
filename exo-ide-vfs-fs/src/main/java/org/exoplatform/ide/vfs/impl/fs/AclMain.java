package org.exoplatform.ide.vfs.impl.fs;

import org.exoplatform.ide.vfs.shared.Principal;
import org.exoplatform.ide.vfs.shared.PrincipalImpl;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AclMain {

    public AclMain(java.io.File root, java.io.File list) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(list));
        String line;
        while ((line = reader.readLine()) != null) {
            java.io.File vfsRoot = getVfsRootFolder(root, line);
            java.io.File aclDir = new java.io.File(vfsRoot, MountPoint.ACL_DIR);
            if (!(aclDir.exists() || aclDir.mkdirs())) {
                System.out.println("Cannot create directory for ACL.");
                System.exit(1);
            }

            java.io.File aclFile = new java.io.File(aclDir, MountPoint.ACL_FILE_SUFFIX);
            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(aclFile)));
            getACL().write(dos);
            dos.close();
            System.out.printf("Successfully write ACL for %s%n", line);
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Root directory to all VFS and path to file with list of workspaces required.");
            System.exit(1);
        }
        java.io.File root = new java.io.File(args[0]);
        if (!root.isDirectory()) {
            System.out.println("Directory not exists or not directory.");
            System.exit(1);
        }
        java.io.File list = new java.io.File(args[1]);
        if (!list.isFile()) {
            System.out.println("File not exists or not regular file.");
            System.exit(1);
        }
        new AclMain(root, list);
    }

    private AccessControlList getACL() {
        Map<Principal, Set<VirtualFileSystemInfo.BasicPermissions>> acl = new HashMap<>();
        acl.put(new PrincipalImpl("workspace/developer", Principal.Type.GROUP), EnumSet.of(VirtualFileSystemInfo.BasicPermissions.ALL));
        return new AccessControlList(acl);
    }

    private java.io.File getVfsRootFolder(java.io.File root, String ws) {
        return EnvironmentContextLocalFSMountStrategy.calculateDirPath(root, ws);
    }
}
