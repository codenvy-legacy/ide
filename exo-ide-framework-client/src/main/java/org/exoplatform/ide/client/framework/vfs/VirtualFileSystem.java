package org.exoplatform.ide.client.framework.vfs;


public abstract class VirtualFileSystem
{

   private static VirtualFileSystem instance;

   public static VirtualFileSystem getInstance()
   {
      return instance;
   }

   protected VirtualFileSystem()
   {
      instance = this;
   }

   /**
    * Get folder content
    * 
    * @param path
    */
   public abstract void getChildren(Folder folder);

   /**
    * Create new folder
    * 
    * @param path
    */
   public abstract void createFolder(Folder folder);

   /**
    * Get content of the file
    * 
    * @param file
    */

   public abstract void getContent(File file);

   /**
    * Save file content
    * 
    * @param file
    * @param path
    */
   public abstract void saveContent(File file);
   
   /**
    * Save locked file content
    * 
    * @param file
    * @param lockToken
    */
   public abstract void saveContent(File file, String lockToken);

   /**
    * Delete file or folder
    * 
    * @param path
    */
   public abstract void deleteItem(Item item);

   /**
    * Move existed item to another location as path
    * 
    * @param item
    * @param destination
    */
   public abstract void move(Item item, String destination);
   
   /**
    * Move existed item to another location as path
    * 
    * @param item
    * @param destination
    * @param lockToken
    */
   public abstract void move(Item item, String destination, String lockToken);

   /**
    * Copy item to another locations as path
    * 
    * @param item
    * @param destination
    */
   public abstract void copy(Item item, String destination);
   
   /**
    * Get properties of file or folder
    * 
    * @param item
    */
   public abstract void getProperties(Item item);

   /**
    * Save properties of file or folder
    * 
    * @param item
    */
   public abstract void saveProperties(Item item);
   
   /**
    * Save properties of file or folder
    * 
    * @param item
    * @param lockToken
    */
   public abstract void saveProperties(Item item, String lockToken);

   /**
    * Search files
    * 
    * @param folder
    * @param text
    * @param mimeType
    * @param path
    */
   public abstract void search(Folder folder, String text, String mimeType, String path);
   

   /**
    * Lock item
    * 
    * @param item
    * @param timeout
    * @param userName
    */
   public abstract void lock(Item item, int timeout, String userName);
   
   /**
    * Unlock item
    * 
    * @param item
    * @param lockToken
    */
   public abstract void unlock(Item item, String lockToken);
   
   /**
    * Get item's versions history
    * 
    * @param item
    */
   public abstract void getVersions(Item item);
   
}
