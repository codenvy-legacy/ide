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

import org.exoplatform.ide.commons.NameGenerator;
import org.exoplatform.ide.vfs.server.ContentStream;
import org.exoplatform.ide.vfs.server.DeleteOnCloseFileInputStream;
import org.exoplatform.ide.vfs.server.exceptions.ConstraintException;
import org.exoplatform.ide.vfs.server.exceptions.InvalidArgumentException;
import org.exoplatform.ide.vfs.server.exceptions.ItemAlreadyExistException;
import org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundException;
import org.exoplatform.ide.vfs.server.exceptions.LockException;
import org.exoplatform.ide.vfs.server.exceptions.PermissionDeniedException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemRuntimeException;
import org.exoplatform.ide.vfs.shared.AccessControlEntry;
import org.exoplatform.ide.vfs.shared.AccessControlEntryImpl;
import org.exoplatform.ide.vfs.shared.Project;
import org.exoplatform.ide.vfs.shared.Property;
import org.exoplatform.ide.vfs.shared.PropertyFilter;
import org.exoplatform.ide.vfs.shared.PropertyImpl;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo.BasicPermissions;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class MountPoint
{
   private static final Log LOG = ExoLogger.getLogger(MountPoint.class);

   private static final int CACHE_PARTITIONS_NUM = 1 << 3;
   private static final int CACHE_PROTECTED_SIZE = 100;
   private static final int CACHE_PROBATIONARY_SIZE = 200;
   private static final int MASK = CACHE_PARTITIONS_NUM - 1;
   private static final int PARTITION_PROTECTED_SIZE = CACHE_PROTECTED_SIZE / CACHE_PARTITIONS_NUM;
   private static final int PARTITION_PROBATIONARY_SIZE = CACHE_PROBATIONARY_SIZE / CACHE_PARTITIONS_NUM;

   private static final long LOCK_FILE_TIMEOUT = 30000; // 30 seconds
   private static final int MAX_BUFFER_SIZE = 100 * 1024; // 100k
   private static final int COPY_BUFFER_SIZE = 8 * 1024; // 8k

   static final String SERVICE_DIR = ".vfs";

   static final String ACL_DIR = SERVICE_DIR + java.io.File.separatorChar + "acl";
   static final String ACL_FILE_SUFFIX = "_acl";

   static final String LOCKS_DIR = SERVICE_DIR + java.io.File.separatorChar + "locks";
   static final String LOCK_FILE_SUFFIX = "_lock";

   static final String PROPS_DIR = MountPoint.SERVICE_DIR + java.io.File.separatorChar + "props";
   static final String PROPERTIES_FILE_SUFFIX = "_props";


   /* Hide .vfs directory. */
   private static final java.io.FilenameFilter SERVICE_DIR_FILTER = new java.io.FilenameFilter()
   {
      @Override
      public boolean accept(java.io.File dir, String name)
      {
         return !(SERVICE_DIR.equals(name) || "..".equals(name) || ".".equals(name));
      }
   };


   private class AclCache extends LoadingValueSLRUCache<Path, Map<String, Set<BasicPermissions>>>
   {
      AclCache()
      {
         super(PARTITION_PROTECTED_SIZE, PARTITION_PROBATIONARY_SIZE);
      }

      @Override
      protected Map<String, Set<BasicPermissions>> loadValue(Path key)
      {
         DataInputStream dis = null;
         try
         {
            java.io.File aclFile = getAclFile(key);
            if (aclFile.exists())
            {
               dis = new DataInputStream(new BufferedInputStream(new FileInputStream(aclFile)));
               return aclSerializer.read(dis);
            }

            return Collections.emptyMap();
         }
         catch (IOException e)
         {
            String msg = String.format("Unable read ACL for '%s'. ", key);
            LOG.error(msg + e.getMessage(), e); // More details in log but do not show internal error to caller.
            throw new VirtualFileSystemRuntimeException(msg);
         }
         finally
         {
            closeQuietly(dis);
         }
      }
   }

   // Add in cache if file is not locked to avoid multiple checking the same files.
   private static final String NO_LOCK = "no_lock";

   private class LockTokensCache extends LoadingValueSLRUCache<Path, String>
   {
      LockTokensCache()
      {
         super(PARTITION_PROTECTED_SIZE, PARTITION_PROBATIONARY_SIZE);
      }

      @Override
      protected String loadValue(Path key)
      {
         DataInputStream dis = null;

         try
         {
            java.io.File lockFile = getLockFile(key);
            if (lockFile.exists())
            {
               dis = new DataInputStream(new BufferedInputStream(new FileInputStream(lockFile)));
               return lockTokenSerializer.read(dis);
            }
            return NO_LOCK;
         }
         catch (IOException e)
         {
            String msg = String.format("Unable read lock for '%s'. ", key);
            LOG.error(msg + e.getMessage(), e); // More details in log but do not show internal error to caller.
            throw new VirtualFileSystemRuntimeException(msg);
         }
         finally
         {
            closeQuietly(dis);
         }
      }
   }


   private class FileMetadataCache extends LoadingValueSLRUCache<Path, Map<String, String[]>>
   {
      FileMetadataCache()
      {
         super(PARTITION_PROTECTED_SIZE, PARTITION_PROBATIONARY_SIZE);
      }

      @Override
      protected Map<String, String[]> loadValue(Path key)
      {
         DataInputStream dis = null;
         try
         {
            java.io.File metadataFile = getMetadataFile(key);
            if (metadataFile.exists())
            {
               dis = new DataInputStream(new BufferedInputStream(new FileInputStream(metadataFile)));
               return metadataSerializer.read(dis);
            }
            return Collections.emptyMap();
         }
         catch (IOException e)
         {
            String msg = String.format("Unable read properties for '%s'. ", key);
            LOG.error(msg + e.getMessage(), e); // More details in log but do not show internal error to caller.
            throw new VirtualFileSystemRuntimeException(msg);
         }
         finally
         {
            closeQuietly(dis);
         }
      }
   }

   private final java.io.File ioRoot;
   /*
    * NOTE -- This does not related to virtual file system locking in any kind. --
    * Need this to avoid concurrent read-write or write-write to the same file.
    * It is possible to have few readers.
    */
   private final FileLockFactory fileLockFactory;

   private VirtualFile root;

   /* ----- Access control list feature. ----- */
   private final AclSerializer aclSerializer;
   private final AclCache[] aclCache;
   private final java.util.concurrent.locks.Lock[] aclCacheLocks;

   /* ----- Virtual file system lock feature. ----- */
   private final LockTokenSerializer lockTokenSerializer;
   private final LockTokensCache[] lockTokensCache;
   private final java.util.concurrent.locks.Lock[] lockTokensCacheLocks;

   /* ----- File metadata. ----- */
   private final FileMetadataSerializer metadataSerializer;
   private final FileMetadataCache[] metadataCache;
   private final java.util.concurrent.locks.Lock[] metadataCacheLocks;

   /**
    * @param ioRoot
    *    root directory for virtual file system. Any file in higher level than root are not accessible through
    *    virtual file system API.
    */
   @SuppressWarnings("unchecked")
   MountPoint(java.io.File ioRoot)
   {
      this.ioRoot = ioRoot;
      root = new VirtualFile(ioRoot, Path.ROOT, this);
      fileLockFactory = new FileLockFactory(1024);

      aclSerializer = new AclSerializer();
      aclCache = new AclCache[CACHE_PARTITIONS_NUM];
      aclCacheLocks = new java.util.concurrent.locks.Lock[CACHE_PARTITIONS_NUM];

      lockTokenSerializer = new LockTokenSerializer();
      lockTokensCache = new LockTokensCache[CACHE_PARTITIONS_NUM];
      lockTokensCacheLocks = new java.util.concurrent.locks.Lock[CACHE_PARTITIONS_NUM];

      metadataSerializer = new FileMetadataSerializer();
      metadataCache = new FileMetadataCache[CACHE_PARTITIONS_NUM];
      metadataCacheLocks = new java.util.concurrent.locks.Lock[CACHE_PARTITIONS_NUM];

      for (int i = 0; i < CACHE_PARTITIONS_NUM; i++)
      {
         aclCache[i] = new AclCache();
         aclCacheLocks[i] = new java.util.concurrent.locks.ReentrantLock();
         lockTokensCache[i] = new LockTokensCache();
         lockTokensCacheLocks[i] = new java.util.concurrent.locks.ReentrantLock();
         metadataCache[i] = new FileMetadataCache();
         metadataCacheLocks[i] = new java.util.concurrent.locks.ReentrantLock();
      }
   }


   public VirtualFile getRoot()
   {
      return root;
   }


   public VirtualFile getVirtualFile(String path) throws VirtualFileSystemException
   {
      if (path == null || path.isEmpty() || "/".equals(path))
      {
         return getRoot();
      }

      if (path.charAt(0) == '/')
      {
         path = path.substring(1);
      }

      final Path vfsPath = Path.fromString(path);
      VirtualFile virtualFile = new VirtualFile(new java.io.File(ioRoot, vfsPath.toIoPath()), vfsPath, this);
      if (!virtualFile.exists())
      {
         throw new ItemNotFoundException(String.format("Object '%s' does not exists. ", path));
      }
      if (!hasPermissions(virtualFile, getCurrentUserId(), BasicPermissions.READ))
      {
         throw new PermissionDeniedException(String.format("Access denied to object '%s'. ", vfsPath));
      }

      return virtualFile;
   }

   VirtualFile getParent(VirtualFile virtualFile) throws VirtualFileSystemException
   {
      if (virtualFile.isRoot())
      {
         return null;
      }

      final Path parentPath = virtualFile.getInternalPath().getParent();
      VirtualFile parent = new VirtualFile(new java.io.File(ioRoot, parentPath.toIoPath()), parentPath, this);
      if (!hasPermissions(parent, getCurrentUserId(), BasicPermissions.READ))
      {
         throw new PermissionDeniedException(String.format("Access denied to object '%s'. ", parentPath));
      }

      return parent;
   }


   List<VirtualFile> getChildren(VirtualFile virtualFile) throws VirtualFileSystemException
   {
      if (!virtualFile.isFolder())
      {
         throw new InvalidArgumentException(
            String.format("Unable get children. Item '%s' is not a folder. ", virtualFile.getPath()));
      }

      List<VirtualFile> children = doGetChildren(virtualFile, SERVICE_DIR_FILTER);
      // Always sort to get the exact same order of files for each listing.
      Collections.sort(children);

      return children;
   }


   private List<VirtualFile> doGetChildren(VirtualFile virtualFile, java.io.FilenameFilter ioFileFilter)
      throws VirtualFileSystemException
   {
      String[] names = virtualFile.getIoFile().list(ioFileFilter);
      if (names == null)
      {
         // Something wrong. According to java docs may be null only if i/o error occurs.
         throw new VirtualFileSystemException(String.format("Unable get children '%s'. ", virtualFile.getPath()));
      }
      List<VirtualFile> children = new ArrayList<VirtualFile>(names.length);
      for (String name : names)
      {
         final Path childPath = virtualFile.getInternalPath().newPath(name);
         VirtualFile child = new VirtualFile(new java.io.File(ioRoot, childPath.toIoPath()), childPath, this);
         if (!hasPermissions(child, getCurrentUserId(), BasicPermissions.READ))
         {
            throw new PermissionDeniedException(String.format("Access denied to object '%s'. ", childPath));
         }
         children.add(child);
      }
      return children;
   }


   VirtualFile createFile(VirtualFile parent, String name, String mediaType, InputStream content)
      throws VirtualFileSystemException
   {
      checkName(name);

      if (!parent.isFolder())
      {
         throw new InvalidArgumentException("Unable create new file. Item specified as parent is not a folder. ");
      }

      final Path newPath = parent.getInternalPath().newPath(name);

      if (!hasPermissions(parent, getCurrentUserId(), BasicPermissions.WRITE))
      {
         throw new PermissionDeniedException(
            String.format("Unable create new file '%s'. Operation not permitted. ", newPath));
      }

      VirtualFile newVirtualFile = new VirtualFile(new java.io.File(ioRoot, newPath.toIoPath()), newPath, this);
      try
      {
         if (!newVirtualFile.getIoFile().createNewFile()) // atomic
         {
            throw new ItemAlreadyExistException(String.format("Item '%s' already exists. ", newPath));
         }
      }
      catch (IOException e)
      {
         String msg = String.format("Unable create new file '%s'. ", newPath);
         LOG.error(msg + e.getMessage(), e); // More details in log but do not show internal error to caller.
         throw new VirtualFileSystemException(msg);
      }
      // Update content if any.
      if (content != null)
      {
         doUpdateContent(newVirtualFile, mediaType, content);
      }

      return newVirtualFile;
   }


   VirtualFile createFolder(VirtualFile parent, String name) throws VirtualFileSystemException
   {
      checkName(name);

      if (!parent.isFolder())
      {
         throw new InvalidArgumentException("Unable create folder. Item specified as parent is not a folder. ");
      }

      if (!hasPermissions(parent, getCurrentUserId(), BasicPermissions.WRITE))
      {
         throw new PermissionDeniedException(String.format("Unable create new folder '%s'. Operation not permitted. ",
            parent.getInternalPath().newPath(name)));
      }

      // Name may be hierarchical, e.g. folder1/folder2/folder3.
      // Some folder in hierarchy may already exists but at least one folder must be created.
      // If no one folder created then ItemAlreadyExistException is thrown.
      // Method returns first created folder.
      VirtualFile created = null;
      VirtualFile current = parent;
      String[] elements = Path.fromString(name).elements();
      for (String element : elements)
      {
         final Path newPath = current.getInternalPath().newPath(element);
         current = new VirtualFile(new java.io.File(ioRoot, newPath.toIoPath()), newPath, this);
         if (current.getIoFile().mkdir() && created == null)
         {
            created = current;
         }
      }

      if (created == null)
      {
         // Folder or folder hierarchy already exists.
         throw new ItemAlreadyExistException(
            String.format("Item '%s' already exists. ", parent.getInternalPath().newPath(name)));
      }

      // Return first created folder, e.g. assume we need create: folder1/folder2/folder3 in specified folder.
      // If folder1 already exists then return folder2 as first created in hierarchy.
      return created;
   }


   VirtualFile copy(VirtualFile source, VirtualFile parent) throws VirtualFileSystemException
   {
      if (source.getInternalPath().equals(parent.getInternalPath()))
      {
         throw new InvalidArgumentException("Item cannot be copied to itself. ");
      }

      if (!parent.isFolder())
      {
         throw new InvalidArgumentException("Unable copy item. Item specified as parent is not a folder. ");
      }

      if (parent.isProject() && source.isProject())
      {
         throw new ConstraintException(
            "Unable copy item. Item specified as parent is a project. Project cannot contains another project.");
      }

      if (!hasPermissions(parent, getCurrentUserId(), BasicPermissions.WRITE))
      {
         throw new PermissionDeniedException(String.format("Unable copy item '%s' to %s. Operation not permitted. ",
            source.getPath(), parent.getPath()));
      }

      final Path newPath = parent.getInternalPath().newPath(source.getName());
      VirtualFile destination = new VirtualFile(new java.io.File(ioRoot, newPath.toIoPath()), newPath, this);
      if (destination.exists())
      {
         throw new ItemAlreadyExistException(String.format("Item '%s' already exists. ", newPath));
      }

      doCopy(source, destination);

      return destination;
   }


   private void doCopy(VirtualFile source, VirtualFile destination) throws VirtualFileSystemException
   {
      if (source.isFolder())
      {
         copyTree(source, destination);
      }
      else
      {
         copyFile(source, destination);
      }
   }


   private void copyTree(VirtualFile source, VirtualFile destination) throws VirtualFileSystemException
   {
      if (!destination.getIoFile().mkdir())
      {
         throw new VirtualFileSystemException(String.format("Unable create folder '%s'. ", destination.getPath()));
      }

      // copy metadata for source folder
      Map<String, String[]> sourceMetadata = getFileMetadata(source);
      if (!sourceMetadata.isEmpty())
      {
         saveFileMetadata(destination, sourceMetadata);
      }

      // copy acl for source folder
      Map<String, Set<BasicPermissions>> permissionsMap = getPermissionsMap(source);
      if (!permissionsMap.isEmpty())
      {
         savePermissionMap(destination, permissionsMap);
      }

      final int sourceBasePathLength = source.getInternalPath().length();
      LinkedList<VirtualFile> q = new LinkedList<VirtualFile>();
      q.add(source);
      while (!q.isEmpty())
      {
         VirtualFile currentVirtualFile = q.pop();
         for (VirtualFile sourceVirtualFile : doGetChildren(currentVirtualFile, SERVICE_DIR_FILTER))
         {
            final Path newPath =
               destination.getInternalPath().newPath(sourceVirtualFile.getInternalPath().subPath(sourceBasePathLength));
            VirtualFile newVirtualFile = new VirtualFile(new java.io.File(ioRoot, newPath.toIoPath()), newPath, this);
            if (sourceVirtualFile.isFolder())
            {
               java.io.File newIoFile = newVirtualFile.getIoFile();
               if (!(newIoFile.exists() || newIoFile.mkdirs()))
               {
                  throw new VirtualFileSystemException(String.format("Unable create directory '%s'. ", newPath));
               }

               // copy metadata for newly created folder
               sourceMetadata = getFileMetadata(sourceVirtualFile);
               if (!sourceMetadata.isEmpty())
               {
                  saveFileMetadata(newVirtualFile, sourceMetadata);
               }

               // copy acl for newly created folder
               permissionsMap = getPermissionsMap(sourceVirtualFile);
               if (!permissionsMap.isEmpty())
               {
                  savePermissionMap(newVirtualFile, permissionsMap);
               }

               q.push(sourceVirtualFile);
            }
            else
            {
               copyFile(sourceVirtualFile, newVirtualFile);
            }
         }
      }
   }


   private void copyFile(VirtualFile source, VirtualFile destination) throws VirtualFileSystemException
   {
      FileLockFactory.FileLock sourceLock =
         fileLockFactory.getLock(source.getInternalPath(), false).acquire(LOCK_FILE_TIMEOUT);
      FileLockFactory.FileLock targetLock =
         fileLockFactory.getLock(destination.getInternalPath(), true).acquire(LOCK_FILE_TIMEOUT);
      try
      {
         FileInputStream sourceStream = null;
         FileOutputStream targetStream = null;
         FileChannel sourceChannel = null;
         FileChannel targetChannel = null;
         try
         {
            if (!destination.getIoFile().createNewFile()) // atomic
            {
               throw new ItemAlreadyExistException(String.format("Item '%s' already exists. ", destination.getName()));
            }
            sourceStream = new FileInputStream(source.getIoFile());
            targetStream = new FileOutputStream(destination.getIoFile());
            sourceChannel = sourceStream.getChannel();
            targetChannel = targetStream.getChannel();
            final long size = sourceChannel.size();
            long transferred = 0L;
            while (transferred < size)
            {
               transferred += targetChannel.transferFrom(sourceChannel, transferred, (size - transferred));
            }
         }
         catch (IOException e)
         {
            String msg = String.format("Unable copy '%s' to '%s'. ", source, destination);
            LOG.error(msg + e.getMessage(), e); // More details in log but do not show internal error to caller.
            throw new VirtualFileSystemException(msg);
         }
         finally
         {
            closeQuietly(sourceChannel);
            closeQuietly(targetChannel);
            closeQuietly(sourceStream);
            closeQuietly(targetStream);
         }
      }
      finally
      {
         sourceLock.release();
         targetLock.release();
      }

      // copy metadata for newly created file
      Map<String, String[]> sourceMetadata = getFileMetadata(source);
      if (!sourceMetadata.isEmpty())
      {
         saveFileMetadata(destination, sourceMetadata);
      }
      // copy acl for newly created file
      Map<String, Set<BasicPermissions>> permissionsMap = getPermissionsMap(source);
      if (!permissionsMap.isEmpty())
      {
         savePermissionMap(destination, permissionsMap);
      }
   }


   VirtualFile rename(VirtualFile virtualFile, String newName, String newMediaType, String lockToken)
      throws VirtualFileSystemException
   {
      if (virtualFile.isRoot())
      {
         throw new InvalidArgumentException("Unable rename root folder. ");
      }

      if (!hasPermissions(virtualFile, getCurrentUserId(), BasicPermissions.WRITE))
      {
         throw new PermissionDeniedException(
            String.format("Unable rename item '%s'. Operation not permitted. ", virtualFile.getPath()));
      }

      if (virtualFile.isFile() && !validateLockTokenIfLocked(virtualFile, lockToken))
      {
         throw new LockException(
            String.format("Unable rename file '%s'. File is locked. ", virtualFile.getPath()));
      }

      final VirtualFile parent = getParent(virtualFile);
      if (parent.isProject() && newMediaType != null && Project.PROJECT_MIME_TYPE.equals(newMediaType))
      {
         throw new ConstraintException(
            "Unable change type of item. Item's parent is a project. Project cannot contains another project.");
      }

      String name = virtualFile.getName();
      VirtualFile renamed;
      if (!(newName == null || name.equals(newName)))
      {
         final Path newPath = virtualFile.getInternalPath().getParent().newPath(newName);
         renamed = new VirtualFile(new java.io.File(ioRoot, newPath.toIoPath()), newPath, this);
         if (renamed.exists())
         {
            throw new ItemAlreadyExistException(String.format("Item '%s' already exists. ", renamed.getName()));
         }
         doCopy(virtualFile, renamed);
         delete(virtualFile, lockToken);
      }
      else
      {
         renamed = virtualFile;
      }

      if (newMediaType != null)
      {
         setProperty(renamed, "vfs:mimeType", newMediaType);
      }

      return renamed;
   }


   VirtualFile move(VirtualFile source, VirtualFile parent, String lockToken) throws VirtualFileSystemException
   {
      if (source.isRoot())
      {
         throw new InvalidArgumentException("Unable move root folder. ");
      }

      if (source.getInternalPath().equals(parent.getInternalPath()))
      {
         throw new InvalidArgumentException("Item cannot be moved to itself. ");
      }

      if (!parent.isFolder())
      {
         throw new InvalidArgumentException("Unable move. Item specified as parent is not a folder. ");
      }

      final String userId = getCurrentUserId();
      if (!(hasPermissions(source, userId, BasicPermissions.WRITE)
         && hasPermissions(parent, userId, BasicPermissions.WRITE)))
      {
         throw new PermissionDeniedException(
            String.format("Unable move item '%s' to %s. Operation not permitted. ", source.getPath(), parent.getPath()));
      }

      // Even we check lock before delete original file check it here also to have better behaviour.
      // Prevent even copy original file if we already know it is locked.
      if (source.isFile() && !validateLockTokenIfLocked(source, lockToken))
      {
         throw new LockException(
            String.format("Unable move file '%s'. File is locked. ", source.getPath()));
      }

      if (parent.isProject() && source.isProject())
      {
         throw new ConstraintException("Unable move. Item specified as parent is not a folder. " +
            "Project cannot be moved to another project. ");
      }

      if (source.isFolder() && parent.getInternalPath().isChild(source.getInternalPath()))
      {
         throw new InvalidArgumentException(
            String.format("Unable move item '%s' to '%s'. Item may not have itself as parent. ",
               source.getPath(), parent.getPath()));
      }

      final Path newPath = parent.getInternalPath().newPath(source.getName());
      VirtualFile destination = new VirtualFile(new java.io.File(ioRoot, newPath.toIoPath()), newPath, this);
      if (destination.exists())
      {
         throw new ItemAlreadyExistException(String.format("Item '%s' already exists. ", newPath));
      }
      // use copy and delete
      doCopy(source, destination);
      delete(source, lockToken);
      return destination;
   }

   ContentStream getContent(VirtualFile virtualFile) throws VirtualFileSystemException
   {
      if (!virtualFile.isFile())
      {
         throw new InvalidArgumentException(
            String.format("Unable get content. Item '%s' is not a file. ", virtualFile.getPath()));
      }

      // Lock file to avoid update while we are read it.
      FileLockFactory.FileLock lock =
         fileLockFactory.getLock(virtualFile.getInternalPath(), false).acquire(LOCK_FILE_TIMEOUT);
      try
      {
         java.io.File ioFile = virtualFile.getIoFile();
         FileInputStream fIn = null;
         try
         {
            fIn = new FileInputStream(ioFile);
            if (ioFile.length() <= MAX_BUFFER_SIZE)
            {
               // If file small enough save its content in memory.
               byte[] buff = new byte[(int)ioFile.length()];
               int offset = 0;
               int len = buff.length;
               int r;
               while ((r = fIn.read(buff, offset, len)) > 0)
               {
                  offset += r;
                  len -= r;
               }
               return new ContentStream(virtualFile.getName(), new ByteArrayInputStream(buff),
                  virtualFile.getMediaType(), buff.length, new Date(ioFile.lastModified()));
            }

            // Otherwise copy this file to be able release the file lock before leave this method.
            // TODO : improve to avoid copy of file
            java.io.File f = java.io.File.createTempFile("spool_file", null);
            FileOutputStream fOut = new FileOutputStream(f);
            FileChannel fOutChannel = fOut.getChannel();
            FileChannel fInChannel = fIn.getChannel();
            try
            {
               final long size = fInChannel.size();
               long transferred = 0L;
               while (transferred < size)
               {
                  transferred = fOutChannel.transferFrom(fInChannel, transferred, (size - transferred));
               }
            }
            finally
            {
               closeQuietly(fOutChannel);
               closeQuietly(fOut);
               closeQuietly(fInChannel);
            }

            return new ContentStream(virtualFile.getName(), new DeleteOnCloseFileInputStream(f),
               virtualFile.getMediaType(), ioFile.length(), new Date(ioFile.lastModified()));
         }
         catch (IOException e)
         {
            String msg = String.format("Unable get content of '%s'. ", virtualFile.getPath());
            LOG.error(msg + e.getMessage(), e); // More details in log but do not show internal error to caller.
            throw new VirtualFileSystemException(msg);
         }
         finally
         {
            closeQuietly(fIn);
         }
      }
      finally
      {
         lock.release();
      }
   }


   void updateContent(VirtualFile virtualFile, String mediaType, InputStream content, String lockToken)
      throws VirtualFileSystemException
   {
      if (!virtualFile.isFile())
      {
         throw new InvalidArgumentException(String.format("Object '%s' is not file. ", virtualFile.getPath()));
      }

      if (!hasPermissions(virtualFile, getCurrentUserId(), BasicPermissions.WRITE))
      {
         throw new PermissionDeniedException(
            String.format("Unable update content of file '%s'. Operation not permitted. ", virtualFile.getPath()));
      }

      if (!validateLockTokenIfLocked(virtualFile, lockToken))
      {
         throw new LockException(
            String.format("Unable update content of file '%s'. File is locked. ", virtualFile.getPath()));
      }

      // Get exclusive access to file.
      FileLockFactory.FileLock lock =
         fileLockFactory.getLock(virtualFile.getInternalPath(), true).acquire(LOCK_FILE_TIMEOUT);
      try
      {
         FileOutputStream fOut = null;
         try
         {
            fOut = new FileOutputStream(virtualFile.getIoFile());
            byte[] buff = new byte[COPY_BUFFER_SIZE];
            int r;
            while ((r = content.read(buff)) != -1)
            {
               fOut.write(buff, 0, r);
            }
         }
         catch (IOException e)
         {
            String msg = String.format("Unable set content of '%s'. ", virtualFile.getPath());
            LOG.error(msg + e.getMessage(), e); // More details in log but do not show internal error to caller.
            throw new VirtualFileSystemException(msg);
         }
         finally
         {
            closeQuietly(fOut);
         }

         setProperty(virtualFile, "vfs:mimeType", mediaType);
      }
      finally
      {
         lock.release();
      }
   }


   private void doUpdateContent(VirtualFile virtualFile, String mediaType, InputStream content)
      throws VirtualFileSystemException
   {
      // Get exclusive access to file.
      FileLockFactory.FileLock lock =
         fileLockFactory.getLock(virtualFile.getInternalPath(), true).acquire(LOCK_FILE_TIMEOUT);
      try
      {
         FileOutputStream fOut = null;
         try
         {
            fOut = new FileOutputStream(virtualFile.getIoFile());
            byte[] buff = new byte[COPY_BUFFER_SIZE];
            int r;
            while ((r = content.read(buff)) != -1)
            {
               fOut.write(buff, 0, r);
            }
         }
         catch (IOException e)
         {
            String msg = String.format("Unable set content of '%s'. ", virtualFile.getPath());
            LOG.error(msg + e.getMessage(), e); // More details in log but do not show internal error to caller.
            throw new VirtualFileSystemException(msg);
         }
         finally
         {
            closeQuietly(fOut);
         }

         setProperty(virtualFile, "vfs:mimeType", mediaType);
      }
      finally
      {
         lock.release();
      }
   }

   void delete(VirtualFile virtualFile, String lockToken) throws VirtualFileSystemException
   {
      final String userId = getCurrentUserId();

      if (!hasPermissions(virtualFile, userId, BasicPermissions.WRITE))
      {
         throw new PermissionDeniedException(
            String.format("Unable delete item '%s'. Operation not permitted. ", virtualFile.getPath()));
      }

      if (virtualFile.isFile() && !validateLockTokenIfLocked(virtualFile, lockToken))
      {
         throw new LockException(
            String.format("Unable delete item '%s'. Item is locked. ", virtualFile.getPath()));
      }

      if (virtualFile.isFolder())
      {
         LinkedList<VirtualFile> q = new LinkedList<VirtualFile>();
         q.add(virtualFile);
         while (!q.isEmpty())
         {
            for (VirtualFile child : doGetChildren(q.pop(), SERVICE_DIR_FILTER))
            {
               if (!hasPermissions(child, userId, BasicPermissions.WRITE))
               {
                  throw new PermissionDeniedException(
                     String.format("Unable delete item '%s'. Operation not permitted. ", child.getPath()));
               }
               if (child.isFolder())
               {
                  q.push(child);
               }
               else if (isLocked(child))
               {
                  // Do not check lock token here. It checked only when remove file directly.
                  // If folder contains locked children it may not be deleted.
                  throw new LockException(String.format("Unable delete item '%s'. Child item '%s' is locked. ",
                     virtualFile.getPath(), child.getPath()));
               }
            }
         }
      }

      if (!delete(virtualFile.getIoFile()))
      {
         throw new VirtualFileSystemException(String.format("Unable delete item '%s'. ", virtualFile.getPath()));
      }

      // TODO : delete ACL and lock files
   }

   private boolean delete(java.io.File ioFile)
   {
      if (ioFile.isDirectory())
      {
         java.io.File[] list = ioFile.listFiles();
         if (list == null)
         {
            return false;
         }
         for (java.io.File f : list)
         {
            if (!delete(f))
            {
               return false;
            }
         }
      }
      return !ioFile.exists() || ioFile.delete();
   }

   /* ============ LOCKING ============ */

   String lock(VirtualFile virtualFile) throws VirtualFileSystemException
   {
      if (!virtualFile.isFile())
      {
         throw new InvalidArgumentException(
            String.format("Unable lock '%s'. Locking allowed for files only. ", virtualFile.getPath()));
      }

      if (!hasPermissions(virtualFile, getCurrentUserId(), BasicPermissions.WRITE))
      {
         throw new PermissionDeniedException(
            String.format("Unable lock '%s'. Operation not permitted. ", virtualFile.getPath()));
      }

      return doLock(virtualFile);
   }

   private String doLock(VirtualFile virtualFile) throws VirtualFileSystemException
   {
      final int index = virtualFile.getInternalPath().hashCode() & MASK;
      lockTokensCacheLocks[index].lock();

      try
      {
         if (NO_LOCK.equals(lockTokensCache[index].get(virtualFile.getInternalPath()))) // causes read from file if need.
         {
            final String lockToken = NameGenerator.generate(null, 16);
            DataOutputStream dos = null;
            try
            {
               dos = new DataOutputStream(
                  new BufferedOutputStream(new FileOutputStream(getLockFile(virtualFile.getInternalPath()))));
               lockTokenSerializer.write(dos, lockToken);
            }
            catch (IOException e)
            {
               String msg = String.format("Unable lock file '%s'. ", virtualFile.getPath());
               LOG.error(msg + e.getMessage(), e); // More details in log but do not show internal error to caller.
               throw new VirtualFileSystemRuntimeException(msg);
            }
            finally
            {
               closeQuietly(dos);
            }

            // Save lock token in cache if lock successful.
            lockTokensCache[index].put(virtualFile.getInternalPath(), lockToken);
            return lockToken;
         }

         throw new LockException(
            String.format("Unable lock file '%s'. File already locked. ", virtualFile.getPath()));
      }
      finally
      {
         lockTokensCacheLocks[index].unlock();
      }
   }

   void unlock(VirtualFile virtualFile, String lockToken) throws VirtualFileSystemException
   {
      if (lockToken == null)
      {
         throw new LockException("Null lock token. ");
      }

      if (!virtualFile.isFile())
      {
         // Locks available for files only.
         throw new LockException(String.format("Item '%s' is not locked. ", virtualFile.getPath()));
      }

      doUnlock(virtualFile, lockToken);
   }

   private void doUnlock(VirtualFile virtualFile, String lockToken) throws VirtualFileSystemException
   {
      final int index = virtualFile.getInternalPath().hashCode() & MASK;
      lockTokensCacheLocks[index].lock();

      try
      {
         final String thisLockToken = lockTokensCache[index].get(virtualFile.getInternalPath()); // causes read from file if need.

         if (NO_LOCK.equals(thisLockToken))
         {
            throw new LockException(String.format("File '%s' is not locked. ", virtualFile.getPath()));
         }

         if (!thisLockToken.equals(lockToken))
         {
            throw new LockException(
               String.format("Unable unlock file '%s'. Lock token does not match. ", virtualFile.getPath()));
         }

         java.io.File lockIoFile = getLockFile(virtualFile.getInternalPath());
         if (!lockIoFile.delete())
         {
            // TODO : need to try again after some timeout ??
            throw new IOException(String.format("Unable delete lock file %s. ", lockIoFile));
         }

         // Mark as unlocked in cache.
         lockTokensCache[index].put(virtualFile.getInternalPath(), NO_LOCK);
      }
      catch (IOException e)
      {
         String msg = String.format("Unable unlock file '%s'. ", virtualFile.getPath());
         LOG.error(msg + e.getMessage(), e); // More details in log but do not show internal error to caller.
         throw new VirtualFileSystemRuntimeException(msg);
      }
      finally
      {
         lockTokensCacheLocks[index].unlock();
      }
   }

   boolean isLocked(VirtualFile virtualFile) throws VirtualFileSystemException
   {
      return virtualFile.isFile() && getLockToken(virtualFile) != null;
   }

   private boolean validateLockTokenIfLocked(VirtualFile virtualFile, String checkLockToken) throws VirtualFileSystemException
   {
      final String lockToken = getLockToken(virtualFile);
      return lockToken == null || lockToken.equals(checkLockToken);
   }

   private String getLockToken(VirtualFile virtualFile) throws VirtualFileSystemException
   {
      final int index = virtualFile.getInternalPath().hashCode() & MASK;
      lockTokensCacheLocks[index].lock();
      try
      {
         final String lockToken = lockTokensCache[index].get(virtualFile.getInternalPath()); // causes read from file if need.
         return NO_LOCK.equals(lockToken) ? null : lockToken;
      }
      finally
      {
         lockTokensCacheLocks[index].unlock();
      }
   }

   private java.io.File getLockFile(Path path)
   {
      java.io.File locksDir = path.isRoot()
         ? new java.io.File(ioRoot, LOCKS_DIR)
         : new java.io.File(ioRoot, path.getParent().newPath(LOCKS_DIR).toIoPath());
      locksDir.mkdirs();
      return new java.io.File(locksDir, path.getName() + LOCK_FILE_SUFFIX);
   }

   /* ============ ACCESS CONTROL  ============ */


   List<AccessControlEntry> getACL(VirtualFile virtualFile)
   {
      final Map<String, Set<BasicPermissions>> permissionsMap = getPermissionsMap(virtualFile);
      List<AccessControlEntry> acl = new ArrayList<AccessControlEntry>(permissionsMap.size());
      for (Map.Entry<String, Set<BasicPermissions>> e : permissionsMap.entrySet())
      {
         Set<BasicPermissions> basicPermissions = e.getValue();
         Set<String> plainPermissions = new HashSet<String>(basicPermissions.size());
         for (BasicPermissions permission : e.getValue())
         {
            plainPermissions.add(permission.value());
         }

         acl.add(new AccessControlEntryImpl(e.getKey(), plainPermissions));
      }
      return acl;
   }

   private Map<String, Set<BasicPermissions>> getPermissionsMap(VirtualFile virtualFile)
   {
      final int index = virtualFile.getInternalPath().hashCode() & MASK;
      aclCacheLocks[index].lock();
      try
      {
         return copyPermissionsMap(aclCache[index].get(virtualFile.getInternalPath()));
      }
      finally
      {
         aclCacheLocks[index].unlock();
      }
   }

   void updateACL(VirtualFile virtualFile, List<AccessControlEntry> acl, boolean override, String lockToken)
      throws VirtualFileSystemException
   {
      if (!hasPermissions(virtualFile, getCurrentUserId(), BasicPermissions.WRITE))
      {
         throw new PermissionDeniedException(
            String.format("Unable update ACL for '%s'. Operation not permitted. ", virtualFile.getPath()));
      }

      if (virtualFile.isFile() && !validateLockTokenIfLocked(virtualFile, lockToken))
      {
         throw new LockException(
            String.format("Unable update ACL of item '%s'. Item is locked. ", virtualFile.getPath()));
      }

      doUpdateACL(virtualFile, acl, override);
   }

   private void doUpdateACL(VirtualFile virtualFile, List<AccessControlEntry> acl, boolean override)
      throws VirtualFileSystemException
   {
      if (acl.isEmpty() && !override)
      {
         // Have nothing to do if there is no updates and override flag is not set.
         return;
      }

      final int index = virtualFile.getInternalPath().hashCode() & MASK;
      Map<String, Set<BasicPermissions>> permissionsMap;
      aclCacheLocks[index].lock();
      try
      {
         permissionsMap = copyPermissionsMap(aclCache[index].get(virtualFile.getInternalPath()));
      }
      finally
      {
         aclCacheLocks[index].unlock();
      }

      if (override)
      {
         // remove all existed permissions
         permissionsMap.clear();
      }

      for (AccessControlEntry ace : acl)
      {
         String name = ace.getPrincipal();
         Set<String> plainPermissions = ace.getPermissions();
         if (plainPermissions == null || plainPermissions.isEmpty())
         {
            permissionsMap.remove(name);
         }
         else
         {
            Set<BasicPermissions> basicPermissions = permissionsMap.get(name);
            if (basicPermissions == null)
            {
               basicPermissions = EnumSet.noneOf(BasicPermissions.class);
               permissionsMap.put(name, basicPermissions);
            }
            for (String strPermission : plainPermissions)
            {
               basicPermissions.add(BasicPermissions.fromValue(strPermission));
            }
         }
      }
      aclCacheLocks[index].lock();
      try
      {
         savePermissionMap(virtualFile, permissionsMap);
         // update cache
         aclCache[index].put(virtualFile.getInternalPath(), permissionsMap);
      }
      finally
      {
         aclCacheLocks[index].unlock();
      }
   }

   private void savePermissionMap(VirtualFile virtualFile, Map<String, Set<BasicPermissions>> permissionsMap)
      throws VirtualFileSystemException
   {
      DataOutputStream dos = null;
      try
      {
         java.io.File aclFile = getAclFile(virtualFile.getInternalPath());
         if (permissionsMap.isEmpty())
         {
            if (!aclFile.delete())
            {
               if (aclFile.exists())
               {
                  throw new IOException(String.format("Unable delete file '%s'. ", aclFile));
               }
            }
         }

         dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(aclFile)));
         aclSerializer.write(dos, permissionsMap);
      }
      catch (IOException e)
      {
         String msg = String.format("Unable save ACL for '%s'. ", virtualFile.getPath());
         LOG.error(msg + e.getMessage(), e); // More details in log but do not show internal error to caller.
         throw new VirtualFileSystemException(msg);
      }
      finally
      {
         closeQuietly(dos);
      }
   }

   private boolean hasPermissions(VirtualFile virtualFile, String userId, BasicPermissions p)
   {
      return hasPermissions(virtualFile, userId, EnumSet.of(p));
   }

   private boolean hasPermissions(VirtualFile virtualFile, String userId, BasicPermissions p1, BasicPermissions p2)
   {
      return hasPermissions(virtualFile, userId, EnumSet.of(p1, p2));
   }

   private boolean hasPermissions(VirtualFile virtualFile, String userId, Collection<BasicPermissions> toCheck)
   {
      final int index = virtualFile.getInternalPath().hashCode() & MASK;
      aclCacheLocks[index].lock();
      try
      {
         final Map<String, Set<BasicPermissions>> permissionsMap = aclCache[index].get(virtualFile.getInternalPath());
         if (permissionsMap.isEmpty())
         {
            return true;
         }
         final Set<BasicPermissions> anyUserPermissions = permissionsMap.get(VirtualFileSystemInfo.ANY_PRINCIPAL);
         if (anyUserPermissions != null
            && (anyUserPermissions.contains(BasicPermissions.ALL) || anyUserPermissions.containsAll(toCheck)))
         {
            return true;
         }
         final Set<BasicPermissions> userPermissions = permissionsMap.get(userId);
         return userPermissions != null
            && (userPermissions.contains(BasicPermissions.ALL) || userPermissions.containsAll(toCheck));
      }
      finally
      {
         aclCacheLocks[index].unlock();
      }
   }

   private java.io.File getAclFile(Path path)
   {
      java.io.File aclDir = path.isRoot()
         ? new java.io.File(ioRoot, ACL_DIR)
         : new java.io.File(ioRoot, path.getParent().newPath(ACL_DIR).toIoPath());
      aclDir.mkdirs();
      return new java.io.File(aclDir, path.getName() + ACL_FILE_SUFFIX);
   }

   /* ============ METADATA  ============ */

   List<Property> getProperties(VirtualFile virtualFile, PropertyFilter filter) throws VirtualFileSystemException
   {
      final Map<String, String[]> metadata = getFileMetadata(virtualFile);
      List<Property> result = new ArrayList<Property>(metadata.size());
      for (Map.Entry<String, String[]> e : metadata.entrySet())
      {
         String name = e.getKey();
         if (filter.accept(name))
         {
            if (e.getValue() != null)
            {
               List<String> list = new ArrayList<String>(e.getValue().length);
               Collections.addAll(list, e.getValue());
               result.add(new PropertyImpl(name, list));
            }
            else
            {
               result.add(new PropertyImpl(name, (String)null));
            }
         }
      }
      return result;
   }

   private Map<String, String[]> getFileMetadata(VirtualFile virtualFile)
   {
      final int index = virtualFile.getInternalPath().hashCode() & MASK;
      metadataCacheLocks[index].lock();
      try
      {
         return copyMetadataMap(metadataCache[index].get(virtualFile.getInternalPath()));
      }
      finally
      {
         metadataCacheLocks[index].unlock();
      }
   }

   String[] getPropertyValues(VirtualFile virtualFile, String name)
   {
      final int index = virtualFile.getInternalPath().hashCode() & MASK;
      metadataCacheLocks[index].lock();
      try
      {
         return metadataCache[index].get(virtualFile.getInternalPath()).get(name);
      }
      finally
      {
         metadataCacheLocks[index].unlock();
      }
   }


   String getPropertyValue(VirtualFile virtualFile, String name) throws VirtualFileSystemException
   {
      final int index = virtualFile.getInternalPath().hashCode() & MASK;
      metadataCacheLocks[index].lock();
      try
      {
         String[] values = metadataCache[index].get(virtualFile.getInternalPath()).get(name);
         return values == null || values.length == 0 ? null : values[0];
      }
      finally
      {
         metadataCacheLocks[index].unlock();
      }
   }


   private void setProperty(VirtualFile virtualFile, String name, String value)
   {
      final int index = virtualFile.getInternalPath().hashCode() & MASK;
      metadataCacheLocks[index].lock();
      try
      {
         Map<String, String[]> metadata = copyMetadataMap(metadataCache[index].get(virtualFile.getInternalPath()));
         if (value != null)
         {
            metadata.put(name, new String[]{value});
         }
         else
         {
            metadata.remove(name);
         }
         saveFileMetadata(virtualFile, metadata);
         // update cache
         metadataCache[index].put(virtualFile.getInternalPath(), metadata);
      }
      finally
      {
         metadataCacheLocks[index].unlock();
      }
   }

   private void setProperty(VirtualFile virtualFile, String name, String... value)
   {
      final int index = virtualFile.getInternalPath().hashCode() & MASK;
      metadataCacheLocks[index].lock();
      try
      {
         Map<String, String[]> metadata = copyMetadataMap(metadataCache[index].get(virtualFile.getInternalPath()));
         if (value != null)
         {
            String[] copyValue = new String[value.length];
            System.arraycopy(value, 0, copyValue, 0, value.length);
            metadata.put(name, copyValue);
         }
         else
         {
            metadata.remove(name);
         }
         saveFileMetadata(virtualFile, metadata);
         // update cache
         metadataCache[index].put(virtualFile.getInternalPath(), metadata);
      }
      finally
      {
         metadataCacheLocks[index].unlock();
      }
   }

   private void saveFileMetadata(VirtualFile virtualFile, Map<String, String[]> properties)
   {
      DataOutputStream dos = null;

      try
      {
         java.io.File metadataFile = getMetadataFile(virtualFile.getInternalPath());
         if (properties.isEmpty())
         {
            if (!metadataFile.delete())
            {
               if (metadataFile.exists())
               {
                  throw new IOException(String.format("Unable delete file '%s'. ", metadataFile));
               }
            }
         }
         else
         {
            dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(metadataFile)));
            metadataSerializer.write(dos, properties);
         }
      }
      catch (IOException e)
      {
         String msg = String.format("Unable save properties for '%s'. ", virtualFile.getPath());
         LOG.error(msg + e.getMessage(), e); // More details in log but do not show internal error to caller.
         throw new VirtualFileSystemRuntimeException(msg);
      }
      finally
      {
         closeQuietly(dos);
      }
   }

   private java.io.File getMetadataFile(Path path)
   {
      java.io.File metadataDir = path.isRoot()
         ? new java.io.File(ioRoot, PROPS_DIR)
         : new java.io.File(ioRoot, path.getParent().newPath(PROPS_DIR).toIoPath());
      metadataDir.mkdirs();
      return new java.io.File(metadataDir, path.getName() + PROPERTIES_FILE_SUFFIX);
   }

   /* ============ HELPERS  ============ */

   private String getCurrentUserId()
   {
      ConversationState cs = ConversationState.getCurrent();
      if (cs != null)
      {
         return cs.getIdentity().getUserId();
      }
      return VirtualFileSystemInfo.ANONYMOUS_PRINCIPAL;
   }

   private Map<String, String[]> copyMetadataMap(Map<String, String[]> source)
   {
      Map<String, String[]> copyMap = new HashMap<String, String[]>(source.size());
      for (Map.Entry<String, String[]> e : source.entrySet())
      {
         String[] value = e.getValue();
         String[] copyValue = new String[value.length];
         System.arraycopy(value, 0, copyValue, 0, value.length);
         copyMap.put(e.getKey(), copyValue);
      }
      return copyMap;
   }

   private Map<String, Set<BasicPermissions>> copyPermissionsMap(Map<String, Set<BasicPermissions>> source)
   {
      Map<String, Set<BasicPermissions>> copy = new HashMap<String, Set<BasicPermissions>>(source.size());
      for (Map.Entry<String, Set<BasicPermissions>> e : source.entrySet())
      {
         copy.put(e.getKey(), EnumSet.copyOf(e.getValue()));
      }
      return copy;
   }

   private void closeQuietly(Closeable closeable)
   {
      if (closeable != null)
      {
         try
         {
            closeable.close();
         }
         catch (IOException ignored)
         {
            // ignore
         }
      }
   }

   private void checkName(String name) throws InvalidArgumentException
   {
      if (name == null || name.trim().isEmpty())
      {
         throw new InvalidArgumentException("Item's name is not set. ");
      }
   }
}
