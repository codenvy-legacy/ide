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

import org.everrest.core.impl.provider.json.JsonException;
import org.everrest.core.impl.provider.json.JsonGenerator;
import org.everrest.core.impl.provider.json.JsonParser;
import org.everrest.core.impl.provider.json.JsonWriter;
import org.everrest.core.impl.provider.json.ObjectBuilder;
import org.exoplatform.ide.commons.FileUtils;
import org.exoplatform.ide.commons.NameGenerator;
import org.exoplatform.ide.vfs.server.ContentStream;
import org.exoplatform.ide.vfs.server.cache.Cache;
import org.exoplatform.ide.vfs.server.cache.SynchronizedCache;
import org.exoplatform.ide.vfs.server.exceptions.InvalidArgumentException;
import org.exoplatform.ide.vfs.server.exceptions.ItemAlreadyExistException;
import org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundException;
import org.exoplatform.ide.vfs.server.exceptions.LockException;
import org.exoplatform.ide.vfs.server.exceptions.PermissionDeniedException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.server.util.DeleteOnCloseFileInputStream;
import org.exoplatform.ide.vfs.server.util.NotClosableInputStream;
import org.exoplatform.ide.vfs.server.util.ZipContent;
import org.exoplatform.ide.vfs.shared.AccessControlEntry;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo.BasicPermissions;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class MountPoint
{
   private static final Log LOG = ExoLogger.getLogger(MountPoint.class);

   /*
    * Configuration parameters for caches.
    * Caches are split to the few partitions to reduce lock contention.
    * Use SLRU cache algorithm here.
    * This is required some additional parameters, e.g. protected and probationary size.
    * See details about SLRU algorithm: http://en.wikipedia.org/wiki/Cache_algorithms#Segmented_LRU
    */
   private static final int CACHE_PARTITIONS_NUM = 1 << 3;
   private static final int CACHE_PROTECTED_SIZE = 100;
   private static final int CACHE_PROBATIONARY_SIZE = 200;
   private static final int MASK = CACHE_PARTITIONS_NUM - 1;
   private static final int PARTITION_PROTECTED_SIZE = CACHE_PROTECTED_SIZE / CACHE_PARTITIONS_NUM;
   private static final int PARTITION_PROBATIONARY_SIZE = CACHE_PROBATIONARY_SIZE / CACHE_PARTITIONS_NUM;
   // end cache parameters

   private static final int MAX_BUFFER_SIZE = 100 * 1024; // 100k
   private static final int COPY_BUFFER_SIZE = 8 * 1024; // 8k

   private static final long LOCK_FILE_TIMEOUT = 30000; // 30 seconds
   private static final int FILE_LOCK_MAX_THREADS = 1024;

   static final String SERVICE_DIR = ".vfs";

   static final String ACL_DIR = SERVICE_DIR + java.io.File.separatorChar + "acl";
   static final String ACL_FILE_SUFFIX = "_acl";

   static final String LOCKS_DIR = SERVICE_DIR + java.io.File.separatorChar + "locks";
   static final String LOCK_FILE_SUFFIX = "_lock";

   static final String PROPS_DIR = SERVICE_DIR + java.io.File.separatorChar + "props";
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


   // Add in cache if file is not locked to avoid multiple checking the same files.
   private static final String NO_LOCK = "no_lock";

   private class LockTokensCache extends LoadingValueSLRUCache<Path, String>
   {
      LockTokensCache()
      {
         super(PARTITION_PROTECTED_SIZE, PARTITION_PROBATIONARY_SIZE);
      }

      @Override
      protected String loadValue(Path key) throws VirtualFileSystemException
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
            throw new VirtualFileSystemException(msg);
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
      protected Map<String, String[]> loadValue(Path key) throws VirtualFileSystemException
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
            throw new VirtualFileSystemException(msg);
         }
         finally
         {
            closeQuietly(dis);
         }
      }
   }


   private class AccessControlListCache extends LoadingValueSLRUCache<Path, AccessControlList>
   {
      private AccessControlListCache()
      {
         super(PARTITION_PROTECTED_SIZE, PARTITION_PROBATIONARY_SIZE);
      }

      @Override
      protected AccessControlList loadValue(Path key) throws VirtualFileSystemException
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

            return new AccessControlList();
         }
         catch (IOException e)
         {
            String msg = String.format("Unable read ACL for '%s'. ", key);
            LOG.error(msg + e.getMessage(), e); // More details in log but do not show internal error to caller.
            throw new VirtualFileSystemException(msg);
         }
         finally
         {
            closeQuietly(dis);
         }
      }
   }

   private final java.io.File ioRoot;

   /* NOTE -- This does not related to virtual file system locking in any kind. -- */
   private final FileLockFactory fileLockFactory;

   private final VirtualFile root;

   /* ----- Access control list feature. ----- */
   private final AccessControlListSerializer aclSerializer;
   private final Cache<Path, AccessControlList>[] aclCache;

   /* ----- Virtual file system lock feature. ----- */
   private final LockTokenSerializer lockTokenSerializer;
   private final Cache<Path, String>[] lockTokensCache;

   /* ----- File metadata. ----- */
   private final FileMetadataSerializer metadataSerializer;
   private final Cache<Path, Map<String, String[]>>[] metadataCache;

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
      fileLockFactory = new FileLockFactory(FILE_LOCK_MAX_THREADS);

      aclSerializer = new AccessControlListSerializer();
      aclCache = new Cache[CACHE_PARTITIONS_NUM];

      lockTokenSerializer = new LockTokenSerializer();
      lockTokensCache = new Cache[CACHE_PARTITIONS_NUM];

      metadataSerializer = new FileMetadataSerializer();
      metadataCache = new Cache[CACHE_PARTITIONS_NUM];

      for (int i = 0; i < CACHE_PARTITIONS_NUM; i++)
      {
         aclCache[i] = new SynchronizedCache(new AccessControlListCache());
         lockTokensCache[i] = new SynchronizedCache(new LockTokensCache());
         metadataCache[i] = new SynchronizedCache(new FileMetadataCache());
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
      final VirtualFile virtualFile = new VirtualFile(new java.io.File(ioRoot, vfsPath.toIoPath()), vfsPath, this);
      if (!virtualFile.exists())
      {
         throw new ItemNotFoundException(String.format("Object '%s' does not exists. ", path));
      }
      final FileLockFactory.FileLock lock =
         fileLockFactory.getLock(virtualFile.getInternalPath(), false).acquire(LOCK_FILE_TIMEOUT);
      try
      {
         if (!hasPermission(virtualFile, getCurrentUserId(), BasicPermissions.READ))
         {
            throw new PermissionDeniedException(
               String.format("Unable get item '%s'. Operation not permitted. ", virtualFile.getPath()));
         }
      }
      finally
      {
         lock.release();
      }
      return virtualFile;
   }

   /** Call after unmount this MountPoint. Clear all caches. */
   void reset()
   {
      clearMetadataCache();
      clearAclCache();
      clearLockTokensCache();
   }

   // Used in tests. Need this to check state of FileLockFactory.
   // All locks MUST be released at the end of request lifecycle.
   FileLockFactory getFileLockFactory()
   {
      return fileLockFactory;
   }

   /* =================================== INTERNAL =================================== */

   // All methods below designed to be used from VirtualFile ONLY.

   VirtualFile getParent(VirtualFile virtualFile) throws VirtualFileSystemException
   {
      if (virtualFile.isRoot())
      {
         return null;
      }
      final Path parentPath = virtualFile.getInternalPath().getParent();

      return new VirtualFile(new java.io.File(ioRoot, parentPath.toIoPath()), parentPath, this);
   }


   List<VirtualFile> getChildren(VirtualFile virtualFile) throws VirtualFileSystemException
   {
      if (!virtualFile.isFolder())
      {
         throw new InvalidArgumentException(
            String.format("Unable get children. Item '%s' is not a folder. ", virtualFile.getPath()));
      }

      final FileLockFactory.FileLock parentLock =
         fileLockFactory.getLock(virtualFile.getInternalPath(), false).acquire(LOCK_FILE_TIMEOUT);
      try
      {
         final String userId = getCurrentUserId();
         final List<VirtualFile> children = doGetChildren(virtualFile);
         for (VirtualFile child : children)
         {
            // Check permission directly for current file only.
            // Not use method 'hasPermission' here to avoid useless
            // traversing up to root folder for each file.
            if (!aclCache[child.getInternalPath().hashCode() & MASK]
               .get(child.getInternalPath()).hasPermission(userId, BasicPermissions.READ))
            {
               throw new PermissionDeniedException(
                  String.format("Unable get children of '%s'. Operation not permitted for '%s'. ",
                     virtualFile.getPath(), child.getPath()));
            }
         }

         // Always sort to get the exact same order of files for each listing.
         Collections.sort(children);

         return children;
      }
      finally
      {
         parentLock.release();
      }
   }


   private List<VirtualFile> doGetChildren(VirtualFile virtualFile) throws VirtualFileSystemException
   {
      final String[] names = virtualFile.getIoFile().list(SERVICE_DIR_FILTER);
      if (names == null)
      {
         // Something wrong. According to java docs may be null only if i/o error occurs.
         throw new VirtualFileSystemException(String.format("Unable get children '%s'. ", virtualFile.getPath()));
      }
      final List<VirtualFile> children = new ArrayList<VirtualFile>(names.length);
      for (String name : names)
      {
         final Path childPath = virtualFile.getInternalPath().newPath(name);
         children.add(new VirtualFile(new java.io.File(ioRoot, childPath.toIoPath()), childPath, this));
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

      final FileLockFactory.FileLock parentLock =
         fileLockFactory.getLock(parent.getInternalPath(), true).acquire(LOCK_FILE_TIMEOUT);
      try
      {
         if (!hasPermission(parent, getCurrentUserId(), BasicPermissions.WRITE))
         {
            throw new PermissionDeniedException(
               String.format("Unable create new file in '%s'. Operation not permitted. ", parent.getPath()));
         }
         final Path newPath = parent.getInternalPath().newPath(name);
         final VirtualFile newVirtualFile = new VirtualFile(new java.io.File(ioRoot, newPath.toIoPath()), newPath, this);
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
      finally
      {
         parentLock.release();
      }
   }


   VirtualFile createFolder(VirtualFile parent, String name) throws VirtualFileSystemException
   {
      checkName(name);

      if (!parent.isFolder())
      {
         throw new InvalidArgumentException("Unable create folder. Item specified as parent is not a folder. ");
      }

      final FileLockFactory.FileLock parentLock =
         fileLockFactory.getLock(parent.getInternalPath(), true).acquire(LOCK_FILE_TIMEOUT);
      try
      {
         if (!hasPermission(parent, getCurrentUserId(), BasicPermissions.WRITE))
         {
            throw new PermissionDeniedException(
               String.format("Unable create new folder in '%s'. Operation not permitted. ", parent.getPath()));
         }
         // Name may be hierarchical, e.g. folder1/folder2/folder3.
         // Some folder in hierarchy may already exists but at least one folder must be created.
         // If no one folder created then ItemAlreadyExistException is thrown.
         // Method returns first created folder.
         VirtualFile created = null;
         VirtualFile current = parent;
         for (String element : Path.fromString(name).elements())
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
      finally
      {
         parentLock.release();
      }
   }


   VirtualFile createProject(VirtualFile parent, String name, List<Property> properties)
      throws VirtualFileSystemException
   {
      final FileLockFactory.FileLock parentLock =
         fileLockFactory.getLock(parent.getInternalPath(), false).acquire(LOCK_FILE_TIMEOUT);
      try
      {
         final VirtualFile project = createFolder(parent, name);
         updateProperties(project, properties, null);
         return project;
      }
      finally
      {
         parentLock.release();
      }
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
      final FileLockFactory.FileLock parentLock =
         fileLockFactory.getLock(parent.getInternalPath(), false).acquire(LOCK_FILE_TIMEOUT);
      try
      {
         if (!hasPermission(parent, getCurrentUserId(), BasicPermissions.WRITE))
         {
            throw new PermissionDeniedException(String.format("Unable copy item '%s' to %s. Operation not permitted. ",
               source.getPath(), parent.getPath()));
         }
         final Path newPath = parent.getInternalPath().newPath(source.getName());
         final VirtualFile destination = new VirtualFile(new java.io.File(ioRoot, newPath.toIoPath()), newPath, this);
         if (destination.exists())
         {
            throw new ItemAlreadyExistException(String.format("Item '%s' already exists. ", newPath));
         }
         doCopy(source, destination);

         return destination;
      }
      finally
      {
         parentLock.release();
      }
   }


   private void doCopy(VirtualFile source, VirtualFile destination) throws VirtualFileSystemException
   {
      // Source is locked, but destination is not.
      // It looks like not necessary to lock destination path since it does not exists yet.
      final java.io.File sourceMetadataFile = getMetadataFile(source.getInternalPath());
      final java.io.File sourceAclFile = getAclFile(source.getInternalPath());
      final java.io.File destinationMetadataFile = getMetadataFile(destination.getInternalPath());
      final java.io.File destinationAclFile = getAclFile(destination.getInternalPath());
      try
      {
         // First copy metadata (properties) and ACL for source.
         // If we do in this way and fail cause to any i/o or
         // other error client will see error and may try to copy again.
         // But if we successfully copy tree (or single file) and then
         // fail to copy metadata or ACL client may not try to copy again
         // because copy destination already exists.
         if (sourceMetadataFile.exists())
         {
            FileUtils.nioCopy(sourceMetadataFile, destinationMetadataFile, null);
         }
         if (sourceAclFile.exists())
         {
            FileUtils.nioCopy(sourceAclFile, destinationAclFile, null);
         }

         FileUtils.nioCopy(source.getIoFile(), destination.getIoFile(), null);
      }
      catch (IOException e)
      {
         // Try cleanup.
         // Do nothing for file tree. Let client side decide what to do.
         // User may delete copied files (if any) and try copy again.
         destinationMetadataFile.delete();
         destinationAclFile.delete();
         String msg = String.format("Unable copy '%s' to '%s'. ", source, destination);
         LOG.error(msg + e.getMessage(), e); // More details in log but do not show internal error to caller.
         throw new VirtualFileSystemException(msg);
      }
   }


   VirtualFile rename(VirtualFile virtualFile, String newName, String newMediaType, String lockToken)
      throws VirtualFileSystemException
   {
      if (virtualFile.isRoot())
      {
         throw new InvalidArgumentException("Unable rename root folder. ");
      }
      final VirtualFile parent = getParent(virtualFile);
      final FileLockFactory.FileLock parentLock =
         fileLockFactory.getLock(parent.getInternalPath(), true).acquire(LOCK_FILE_TIMEOUT);
      try
      {
         if (!hasPermission(virtualFile, getCurrentUserId(), BasicPermissions.WRITE))
         {
            throw new PermissionDeniedException(
               String.format("Unable rename item '%s'. Operation not permitted. ", virtualFile.getPath()));
         }
         if (virtualFile.isFile() && !validateLockTokenIfLocked(virtualFile, lockToken))
         {
            throw new LockException(
               String.format("Unable rename file '%s'. File is locked. ", virtualFile.getPath()));
         }
         final String name = virtualFile.getName();
         final VirtualFile renamed;
         if (!(newName == null || name.equals(newName)))
         {
            final Path newPath = virtualFile.getInternalPath().getParent().newPath(newName);
            renamed = new VirtualFile(new java.io.File(ioRoot, newPath.toIoPath()), newPath, this);
            if (renamed.exists())
            {
               throw new ItemAlreadyExistException(String.format("Item '%s' already exists. ", renamed.getName()));
            }
            // use copy and delete
            doCopy(virtualFile, renamed);
            doDelete(virtualFile, lockToken);
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
      finally
      {
         parentLock.release();
      }
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
      if (source.isFolder() && parent.getInternalPath().isChild(source.getInternalPath()))
      {
         throw new InvalidArgumentException(
            String.format("Unable move item '%s' to '%s'. Item may not have itself as parent. ",
               source.getPath(), parent.getPath()));
      }

      FileLockFactory.FileLock sourceLock = null;
      FileLockFactory.FileLock parentLock = null;
      try
      {
         sourceLock = fileLockFactory.getLock(source.getInternalPath(), true).acquire(LOCK_FILE_TIMEOUT);
         parentLock = fileLockFactory.getLock(parent.getInternalPath(), true).acquire(LOCK_FILE_TIMEOUT);

         final String userId = getCurrentUserId();
         if (!(hasPermission(source, userId, BasicPermissions.WRITE)
            && hasPermission(parent, userId, BasicPermissions.WRITE)))
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
         final Path newPath = parent.getInternalPath().newPath(source.getName());
         VirtualFile destination = new VirtualFile(new java.io.File(ioRoot, newPath.toIoPath()), newPath, this);
         if (destination.exists())
         {
            throw new ItemAlreadyExistException(String.format("Item '%s' already exists. ", newPath));
         }
         // use copy and delete
         doCopy(source, destination);
         doDelete(source, lockToken);

         return destination;
      }
      finally
      {
         if (sourceLock != null)
         {
            sourceLock.release();
         }
         if (parentLock != null)
         {
            parentLock.release();
         }
      }
   }


   ContentStream getContent(VirtualFile virtualFile) throws VirtualFileSystemException
   {
      if (!virtualFile.isFile())
      {
         throw new InvalidArgumentException(
            String.format("Unable get content. Item '%s' is not a file. ", virtualFile.getPath()));
      }

      final FileLockFactory.FileLock lock =
         fileLockFactory.getLock(virtualFile.getInternalPath(), false).acquire(LOCK_FILE_TIMEOUT);
      try
      {
         java.io.File ioFile = virtualFile.getIoFile();
         FileInputStream fIn = null;
         try
         {
            if (ioFile.length() <= MAX_BUFFER_SIZE)
            {
               // If file small enough save its content in memory.
               fIn = new FileInputStream(ioFile);
               final byte[] buff = new byte[(int)ioFile.length()];
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

            // TODO : improve to avoid copy of file
            // Otherwise copy this file to be able release the file lock before leave this method.
            final java.io.File f = java.io.File.createTempFile("spool_file", null);
            FileUtils.nioCopy(ioFile, f, null);
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
         throw new InvalidArgumentException(
            String.format("Unable get content. Item '%s' is not file. ", virtualFile.getPath()));
      }

      final FileLockFactory.FileLock lock =
         fileLockFactory.getLock(virtualFile.getInternalPath(), true).acquire(LOCK_FILE_TIMEOUT);
      try
      {
         if (!hasPermission(virtualFile, getCurrentUserId(), BasicPermissions.WRITE))
         {
            throw new PermissionDeniedException(
               String.format("Unable update content of file '%s'. Operation not permitted. ", virtualFile.getPath()));
         }
         if (!validateLockTokenIfLocked(virtualFile, lockToken))
         {
            throw new LockException(
               String.format("Unable update content of file '%s'. File is locked. ", virtualFile.getPath()));
         }

         doUpdateContent(virtualFile, mediaType, content);
      }
      finally
      {
         lock.release();
      }
   }


   private void doUpdateContent(VirtualFile virtualFile, String mediaType, InputStream content)
      throws VirtualFileSystemException
   {
      FileOutputStream fOut = null;
      try
      {
         fOut = new FileOutputStream(virtualFile.getIoFile());
         final byte[] buff = new byte[COPY_BUFFER_SIZE];
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


   void delete(VirtualFile virtualFile, String lockToken) throws VirtualFileSystemException
   {
      final FileLockFactory.FileLock lock =
         fileLockFactory.getLock(virtualFile.getInternalPath(), true).acquire(LOCK_FILE_TIMEOUT);
      try
      {
         if (!hasPermission(virtualFile, getCurrentUserId(), BasicPermissions.WRITE))
         {
            throw new PermissionDeniedException(
               String.format("Unable delete item '%s'. Operation not permitted. ", virtualFile.getPath()));
         }
         if (virtualFile.isFile() && !validateLockTokenIfLocked(virtualFile, lockToken))
         {
            throw new LockException(
               String.format("Unable delete item '%s'. Item is locked. ", virtualFile.getPath()));
         }

         doDelete(virtualFile, lockToken);
      }
      finally
      {
         lock.release();
      }
   }


   private void doDelete(VirtualFile virtualFile, String lockToken) throws VirtualFileSystemException
   {
      final String userId = getCurrentUserId();
      if (virtualFile.isFolder())
      {
         final LinkedList<VirtualFile> q = new LinkedList<VirtualFile>();
         q.add(virtualFile);
         while (!q.isEmpty())
         {
            for (VirtualFile child : doGetChildren(q.pop()))
            {
               // Check permission directly for current file only.
               // Not use method 'hasPermission' here to avoid useless
               // traversing up to root folder for each file.
               if (!aclCache[child.getInternalPath().hashCode() & MASK]
                  .get(child.getInternalPath()).hasPermission(userId, BasicPermissions.WRITE))
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

      // unlock file
      if (virtualFile.isFile() && virtualFile.isLocked())
      {
         doUnlock(virtualFile, lockToken);
      }

      // clear caches
      clearAclCache();
      clearLockTokensCache();
      clearMetadataCache();

      if (!FileUtils.deleteRecursive(virtualFile.getIoFile()))
      {
         LOG.error("Unable delete file {}", virtualFile.getIoFile());
         throw new VirtualFileSystemException(String.format("Unable delete item '%s'. ", virtualFile.getPath()));
      }

      // delete ACL file
      final java.io.File aclFile = getAclFile(virtualFile.getInternalPath());
      if (aclFile.delete())
      {
         if (aclFile.exists())
         {
            LOG.error("Unable delete ACL file {}", aclFile);
            throw new VirtualFileSystemException(String.format("Unable delete item '%s'. ", virtualFile.getPath()));
         }
      }

      // delete metadata file
      final java.io.File metadataFile = getMetadataFile(virtualFile.getInternalPath());
      if (metadataFile.delete())
      {
         if (metadataFile.exists())
         {
            LOG.error("Unable delete file metadata {}", metadataFile);
            throw new VirtualFileSystemException(String.format("Unable delete item '%s'. ", virtualFile.getPath()));
         }
      }
   }


   private void clearLockTokensCache()
   {
      for (Cache<Path, String> cache : lockTokensCache)
      {
         cache.clear();
      }
   }


   private void clearAclCache()
   {
      for (Cache<Path, AccessControlList> cache : aclCache)
      {
         cache.clear();
      }
   }


   private void clearMetadataCache()
   {
      for (Cache<Path, Map<String, String[]>> cache : metadataCache)
      {
         cache.clear();
      }
   }


   ContentStream zip(VirtualFile virtualFile) throws IOException, VirtualFileSystemException
   {
      if (!virtualFile.isFolder())
      {
         throw new InvalidArgumentException(
            String.format("Unable export to zip. Item '%s' is not a folder. ", virtualFile.getPath()));
      }
      final FileLockFactory.FileLock lock =
         fileLockFactory.getLock(virtualFile.getInternalPath(), false).acquire(LOCK_FILE_TIMEOUT);
      try
      {
         final java.io.File zipFile = java.io.File.createTempFile("export", ".zip");
         final FileOutputStream out = new FileOutputStream(zipFile);
         final String userId = getCurrentUserId();
         try
         {
            final ZipOutputStream zipOut = new ZipOutputStream(out);
            if (virtualFile.isProject())
            {
               zipOut.putNextEntry(new ZipEntry(".project"));
               JsonWriter jw = new JsonWriter(zipOut);
               JsonGenerator.createJsonArray(virtualFile.getProperties(PropertyFilter.ALL_FILTER)).writeTo(jw);
               jw.flush();
            }
            final LinkedList<VirtualFile> q = new LinkedList<VirtualFile>();
            q.add(virtualFile);
            final int rootZipPathLength = virtualFile.getInternalPath().length();
            final byte[] buff = new byte[COPY_BUFFER_SIZE];
            while (!q.isEmpty())
            {
               for (VirtualFile current : doGetChildren(q.pop()))
               {
                  // Check permission directly for current file only.
                  // Not use method 'hasPermission' here to avoid useless
                  // traversing up to root folder for each file.
                  if (!aclCache[current.getInternalPath().hashCode() & MASK]
                     .get(current.getInternalPath()).hasPermission(userId, BasicPermissions.READ))
                  {
                     throw new PermissionDeniedException(String.format(
                        "Unable export to zip. Cannot read '%s'. Operation not permitted. ", current.getPath()));
                  }

                  final String zipEntryName = current.getInternalPath().subPath(rootZipPathLength).toString();
                  if (current.isFile())
                  {
                     zipOut.putNextEntry(new ZipEntry(zipEntryName));
                     InputStream in = null;
                     try
                     {
                        in = new FileInputStream(current.getIoFile());
                        int r;
                        while ((r = in.read(buff)) != -1)
                        {
                           zipOut.write(buff, 0, r);
                        }
                     }
                     finally
                     {
                        closeQuietly(in);
                     }
                  }
                  else if (current.isFolder())
                  {
                     zipOut.putNextEntry(new ZipEntry(zipEntryName + '/'));
                     if (current.isProject())
                     {
                        zipOut.putNextEntry(new ZipEntry(zipEntryName + "/.project"));
                        JsonWriter jw = new JsonWriter(zipOut);
                        JsonGenerator.createJsonArray(current.getProperties(PropertyFilter.ALL_FILTER)).writeTo(jw);
                        jw.flush();
                     }
                     q.add(current);
                  }
                  zipOut.closeEntry();
               }
            }
            closeQuietly(zipOut);
         }
         catch (IOException ioe)
         {
            zipFile.delete();
            throw ioe;
         }
         catch (JsonException e)
         {
            zipFile.delete();
            throw new VirtualFileSystemException(e.getMessage(), e);
         }
         finally
         {
            closeQuietly(out);
         }

         return new ContentStream(virtualFile.getName() + ".zip", //
            new DeleteOnCloseFileInputStream(zipFile), //
            "application/zip", //
            zipFile.length(), //
            new Date());
      }
      finally
      {
         lock.release();
      }
   }


   void unzip(VirtualFile parent, InputStream zipped, boolean overwrite) throws IOException, VirtualFileSystemException
   {
      if (!parent.isFolder())
      {
         throw new InvalidArgumentException(
            String.format("Unable import zip content. Item '%s' is not a folder. ", parent.getPath()));
      }
      final ZipContent zipContent = ZipContent.newInstance(zipped);
      final FileLockFactory.FileLock lock =
         fileLockFactory.getLock(parent.getInternalPath(), true).acquire(LOCK_FILE_TIMEOUT);
      try
      {
         if (!hasPermission(parent, getCurrentUserId(), BasicPermissions.WRITE))
         {
            throw new PermissionDeniedException(
               String.format("Unable import from zip to '%s'. Operation not permitted. ", parent.getPath()));
         }

         ZipInputStream zip = null;
         try
         {
            zip = new ZipInputStream(zipContent.zippedData);
            // Wrap zip stream to prevent close it. We can pass stream to other method and it can read content of current
            // ZipEntry but not able to close original stream of ZIPed data.
            InputStream noCloseZip = new NotClosableInputStream(zip);
            ZipEntry zipEntry;
            while ((zipEntry = zip.getNextEntry()) != null)
            {
               VirtualFile current = parent;
               final Path relPath = Path.fromString(zipEntry.getName());
               final String name = relPath.getName();
               if (relPath.length() > 1)
               {
                  // create all required parent directories
                  final Path newPath = parent.getInternalPath().newPath(relPath.subPath(0, relPath.length() - 1));
                  current = new VirtualFile(new java.io.File(ioRoot, newPath.toIoPath()), newPath, this);
                  if (!(current.exists() || current.getIoFile().mkdirs()))
                  {
                     throw new VirtualFileSystemException(String.format("Unable create directory '%s' ", newPath));
                  }
               }
               if (zipEntry.isDirectory())
               {
                  final java.io.File dir = new java.io.File(current.getIoFile(), name);
                  if (!(dir.exists() || dir.mkdir()))
                  {
                     throw new VirtualFileSystemException(
                        String.format("Unable create directory '%s' ", current.getInternalPath().newPath(name)));
                  }
               }
               else if (".project".equals(name))
               {
                  final JsonParser parser = new JsonParser();
                  parser.parse(noCloseZip);
                  final Property[] array =
                     (Property[])ObjectBuilder.createArray(PropertyImpl[].class, parser.getJsonObject());
                  if (array.length > 0)
                  {
                     updateProperties(current, Arrays.asList(array), null);
                  }
               }
               else
               {
                  final VirtualFile file = new VirtualFile(
                     new java.io.File(current.getIoFile(), name), current.getInternalPath().newPath(name), this);
                  String mediaType = null;
                  if (file.exists())
                  {
                     if (isLocked(file))
                     {
                        throw new LockException(
                           String.format("File '%s' already exists and locked. ", file.getPath()));
                     }
                     if (!hasPermission(file, getCurrentUserId(), BasicPermissions.WRITE))
                     {
                        throw new PermissionDeniedException(
                           String.format("Unable update file '%s'. Operation not permitted. ", file.getPath()));
                     }
                     mediaType = getPropertyValue(file, "vfs:mimeType");
                  }

                  try
                  {
                     if (!file.getIoFile().createNewFile()) // atomic
                     {
                        if (!overwrite)
                        {
                           throw new ItemAlreadyExistException(
                              String.format("File '%s' already exists. ", file.getPath()));
                        }
                     }
                  }
                  catch (IOException e)
                  {
                     String msg = String.format(
                        "Unable create new file '%s'. ", current.getInternalPath().newPath(name));
                     LOG.error(msg + e.getMessage(), e); // More details in log but do not show internal error to caller.
                     throw new VirtualFileSystemException(msg);
                  }

                  doUpdateContent(file, mediaType, noCloseZip);
               }
               zip.closeEntry();
            }
         }
         catch (JsonException e)
         {
            throw new VirtualFileSystemException(e.getMessage(), e);
         }
         finally
         {
            closeQuietly(zip);
         }
      }
      finally
      {
         lock.release();
      }
   }

   /* ============ LOCKING ============ */

   String lock(VirtualFile virtualFile) throws VirtualFileSystemException
   {
      if (!virtualFile.isFile())
      {
         throw new InvalidArgumentException(
            String.format("Unable lock '%s'. Locking allowed for files only. ", virtualFile.getPath()));
      }

      final FileLockFactory.FileLock lock =
         fileLockFactory.getLock(virtualFile.getInternalPath(), true).acquire(LOCK_FILE_TIMEOUT);
      try
      {
         if (!hasPermission(virtualFile, getCurrentUserId(), BasicPermissions.WRITE))
         {
            throw new PermissionDeniedException(
               String.format("Unable lock '%s'. Operation not permitted. ", virtualFile.getPath()));
         }

         return doLock(virtualFile);
      }
      finally
      {
         lock.release();
      }
   }


   private String doLock(VirtualFile virtualFile) throws VirtualFileSystemException
   {
      final int index = virtualFile.getInternalPath().hashCode() & MASK;
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
            throw new VirtualFileSystemException(msg);
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
      final FileLockFactory.FileLock lock =
         fileLockFactory.getLock(virtualFile.getInternalPath(), true).acquire(LOCK_FILE_TIMEOUT);
      try
      {
         doUnlock(virtualFile, lockToken);
      }
      finally
      {
         lock.release();
      }
   }


   private void doUnlock(VirtualFile virtualFile, String lockToken) throws VirtualFileSystemException
   {
      final int index = virtualFile.getInternalPath().hashCode() & MASK;
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
         final java.io.File lockIoFile = getLockFile(virtualFile.getInternalPath());
         if (!lockIoFile.delete())
         {
            throw new IOException(String.format("Unable delete lock file %s. ", lockIoFile));
         }
         // Mark as unlocked in cache.
         lockTokensCache[index].put(virtualFile.getInternalPath(), NO_LOCK);
      }
      catch (IOException e)
      {
         String msg = String.format("Unable unlock file '%s'. ", virtualFile.getPath());
         LOG.error(msg + e.getMessage(), e); // More details in log but do not show internal error to caller.
         throw new VirtualFileSystemException(msg);
      }
   }


   boolean isLocked(VirtualFile virtualFile) throws VirtualFileSystemException
   {
      if (!virtualFile.isFile())
      {
         return false;
      }
      final FileLockFactory.FileLock lock =
         fileLockFactory.getLock(virtualFile.getInternalPath(), false).acquire(LOCK_FILE_TIMEOUT);
      try
      {
         return getLockToken(virtualFile) != null;
      }
      finally
      {
         lock.release();
      }
   }


   private boolean validateLockTokenIfLocked(VirtualFile virtualFile, String checkLockToken)
      throws VirtualFileSystemException
   {
      final String lockToken = getLockToken(virtualFile);
      return lockToken == null || lockToken.equals(checkLockToken);
   }


   private String getLockToken(VirtualFile virtualFile) throws VirtualFileSystemException
   {
      final int index = virtualFile.getInternalPath().hashCode() & MASK;
      final String lockToken = lockTokensCache[index].get(virtualFile.getInternalPath()); // causes read from file if need.
      return NO_LOCK.equals(lockToken) ? null : lockToken;
   }


   private java.io.File getLockFile(Path path)
   {
      java.io.File locksDir = path.isRoot()
         ? new java.io.File(ioRoot, LOCKS_DIR)
         : new java.io.File(ioRoot, path.getParent().newPath(LOCKS_DIR).toIoPath());
      boolean result = locksDir.mkdirs();
      assert result || locksDir.exists();
      return new java.io.File(locksDir, path.getName() + LOCK_FILE_SUFFIX);
   }

   /* ============ ACCESS CONTROL  ============ */


   List<AccessControlEntry> getACL(VirtualFile virtualFile) throws VirtualFileSystemException
   {
      // Do not check permission here. We already check 'read' permission when get VirtualFile.
      final FileLockFactory.FileLock lock =
         fileLockFactory.getLock(virtualFile.getInternalPath(), false).acquire(LOCK_FILE_TIMEOUT);
      try
      {
         return aclCache[virtualFile.getInternalPath().hashCode() & MASK]
            .get(virtualFile.getInternalPath()).getEntries();
      }
      finally
      {
         lock.release();
      }
   }


   void updateACL(VirtualFile virtualFile, List<AccessControlEntry> acl, boolean override, String lockToken)
      throws VirtualFileSystemException
   {
      final FileLockFactory.FileLock lock =
         fileLockFactory.getLock(virtualFile.getInternalPath(), true).acquire(LOCK_FILE_TIMEOUT);
      try
      {
         final int index = virtualFile.getInternalPath().hashCode() & MASK;
         final AccessControlList actualACL = aclCache[index].get(virtualFile.getInternalPath());

         if (!actualACL.hasPermission(getCurrentUserId(), BasicPermissions.WRITE))
         {
            throw new PermissionDeniedException(
               String.format("Unable update ACL for '%s'. Operation not permitted. ", virtualFile.getPath()));
         }

         if (virtualFile.isFile() && !validateLockTokenIfLocked(virtualFile, lockToken))
         {
            throw new LockException(
               String.format("Unable update ACL of item '%s'. Item is locked. ", virtualFile.getPath()));
         }

         // 1. make copy of ACL
         final AccessControlList copy = new AccessControlList(actualACL);
         // 2. update ACL copy
         copy.update(acl, override);
         // 3. save updated ACL (write in file)
         DataOutputStream dos = null;
         try
         {
            java.io.File aclFile = getAclFile(virtualFile.getInternalPath());
            if (copy.isEmpty())
            {
               if (!aclFile.delete())
               {
                  if (aclFile.exists())
                  {
                     throw new IOException(String.format("Unable delete file '%s'. ", aclFile));
                  }
               }
            }
            else
            {
               dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(aclFile)));
               aclSerializer.write(dos, copy);
            }
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

         // 4. update cache
         aclCache[index].put(virtualFile.getInternalPath(), copy);
      }
      finally
      {
         lock.release();
      }
   }


   // under lock
   private boolean hasPermission(VirtualFile virtualFile, String userId, BasicPermissions p)
      throws VirtualFileSystemException
   {
      Path path = virtualFile.getInternalPath();
      while (path != null)
      {
         final AccessControlList accessControlList = aclCache[path.hashCode() & MASK].get(path);
         if (!accessControlList.isEmpty())
         {
            return accessControlList.hasPermission(userId, p);
         }
         path = path.getParent();
      }
      return true;
   }


   private java.io.File getAclFile(Path path)
   {
      java.io.File aclDir = path.isRoot()
         ? new java.io.File(ioRoot, ACL_DIR)
         : new java.io.File(ioRoot, path.getParent().newPath(ACL_DIR).toIoPath());
      boolean result = aclDir.mkdirs();
      assert result || aclDir.exists();
      return new java.io.File(aclDir, path.getName() + ACL_FILE_SUFFIX);
   }

   /* ============ METADATA  ============ */

   List<Property> getProperties(VirtualFile virtualFile, PropertyFilter filter) throws VirtualFileSystemException
   {
      // Do not check permission here. We already check 'read' permission when get VirtualFile.
      final Map<String, String[]> metadata = getFileMetadata(virtualFile);
      final List<Property> result = new ArrayList<Property>(metadata.size());
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


   void updateProperties(VirtualFile virtualFile, List<Property> properties, String lockToken)
      throws VirtualFileSystemException
   {
      final int index = virtualFile.getInternalPath().hashCode() & MASK;
      final FileLockFactory.FileLock lock =
         fileLockFactory.getLock(virtualFile.getInternalPath(), true).acquire(LOCK_FILE_TIMEOUT);
      try
      {
         if (!hasPermission(virtualFile, getCurrentUserId(), BasicPermissions.WRITE))
         {
            throw new PermissionDeniedException(
               String.format("Unable update properties for '%s'. Operation not permitted. ", virtualFile.getPath()));
         }

         if (virtualFile.isFile() && !validateLockTokenIfLocked(virtualFile, lockToken))
         {
            throw new LockException(
               String.format("Unable update properties of item '%s'. Item is locked. ", virtualFile.getPath()));
         }

         // 1. make copy of properties
         final Map<String, String[]> metadata =
            copyMetadataMap(metadataCache[index].get(virtualFile.getInternalPath()));
         // 2. update
         for (Property property : properties)
         {
            final String name = property.getName();
            final List<String> value = property.getValue();
            if (value != null)
            {
               metadata.put(name, value.toArray(new String[value.size()]));
            }
            else
            {
               metadata.remove(name);
            }
         }

         // 3. save in file
         saveFileMetadata(virtualFile, metadata);
         // 4. update cache
         metadataCache[index].put(virtualFile.getInternalPath(), metadata);
      }
      finally
      {
         lock.release();
      }
   }


   private Map<String, String[]> getFileMetadata(VirtualFile virtualFile) throws VirtualFileSystemException
   {
      final int index = virtualFile.getInternalPath().hashCode() & MASK;
      final FileLockFactory.FileLock lock =
         fileLockFactory.getLock(virtualFile.getInternalPath(), false).acquire(LOCK_FILE_TIMEOUT);
      try
      {
         return copyMetadataMap(metadataCache[index].get(virtualFile.getInternalPath()));
      }
      finally
      {
         lock.release();
      }
   }


   String getPropertyValue(VirtualFile virtualFile, String name) throws VirtualFileSystemException
   {
      // Do not check permission here. We already check 'read' permission when get VirtualFile.
      final int index = virtualFile.getInternalPath().hashCode() & MASK;
      final FileLockFactory.FileLock lock =
         fileLockFactory.getLock(virtualFile.getInternalPath(), false).acquire(LOCK_FILE_TIMEOUT);
      try
      {
         final String[] value = metadataCache[index].get(virtualFile.getInternalPath()).get(name);
         return value == null || value.length == 0 ? null : value[0];
      }
      finally
      {
         lock.release();
      }
   }


   String[] getPropertyValues(VirtualFile virtualFile, String name) throws VirtualFileSystemException
   {
      // Do not check permission here. We already check 'read' permission when get VirtualFile.
      final int index = virtualFile.getInternalPath().hashCode() & MASK;
      final FileLockFactory.FileLock lock =
         fileLockFactory.getLock(virtualFile.getInternalPath(), false).acquire(LOCK_FILE_TIMEOUT);
      try
      {
         final String[] value = metadataCache[index].get(virtualFile.getInternalPath()).get(name);
         String[] copyValue = new String[value.length];
         System.arraycopy(value, 0, copyValue, 0, value.length);
         return copyValue;
      }
      finally
      {
         lock.release();
      }
   }


   private void setProperty(VirtualFile virtualFile, String name, String value) throws VirtualFileSystemException
   {
      setProperty(virtualFile, name, value == null ? null : new String[]{value});
   }


   private void setProperty(VirtualFile virtualFile, String name, String... value) throws VirtualFileSystemException
   {
      final int index = virtualFile.getInternalPath().hashCode() & MASK;
      final FileLockFactory.FileLock lock =
         fileLockFactory.getLock(virtualFile.getInternalPath(), true).acquire(LOCK_FILE_TIMEOUT);
      try
      {
         // 1. make copy of properties
         final Map<String, String[]> metadata =
            copyMetadataMap(metadataCache[index].get(virtualFile.getInternalPath()));
         // 2. update
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
         // 3. save in file
         saveFileMetadata(virtualFile, metadata);
         // 4. update cache
         metadataCache[index].put(virtualFile.getInternalPath(), metadata);
      }
      finally
      {
         lock.release();
      }
   }


   private void saveFileMetadata(VirtualFile virtualFile, Map<String, String[]> properties)
      throws VirtualFileSystemException
   {
      DataOutputStream dos = null;

      try
      {
         final java.io.File metadataFile = getMetadataFile(virtualFile.getInternalPath());
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
         throw new VirtualFileSystemException(msg);
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
      boolean result = metadataDir.mkdirs();
      assert result || metadataDir.exists();
      return new java.io.File(metadataDir, path.getName() + PROPERTIES_FILE_SUFFIX);
   }

   /* ============ HELPERS  ============ */

   private String getCurrentUserId()
   {
      final ConversationState cs = ConversationState.getCurrent();
      if (cs != null)
      {
         return cs.getIdentity().getUserId();
      }
      return VirtualFileSystemInfo.ANONYMOUS_PRINCIPAL;
   }


   private Map<String, String[]> copyMetadataMap(Map<String, String[]> source)
   {
      final Map<String, String[]> copyMap = new HashMap<String, String[]>(source.size());
      for (Map.Entry<String, String[]> e : source.entrySet())
      {
         String[] value = e.getValue();
         String[] copyValue = new String[value.length];
         System.arraycopy(value, 0, copyValue, 0, value.length);
         copyMap.put(e.getKey(), copyValue);
      }
      return copyMap;
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
