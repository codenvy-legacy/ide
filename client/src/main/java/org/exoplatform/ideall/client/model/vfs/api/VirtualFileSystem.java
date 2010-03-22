package org.exoplatform.ideall.client.model.vfs.api;


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

   public abstract void getFileContent(File file);

   /**
    * Save file content
    * 
    * @param file
    * @param path
    */
   public abstract void saveFileContent(File file, String path);

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
    * Search files
    * 
    * @param content
    * @param path
    */
   
   public abstract void search(String content, String path);
   
   public abstract void search(String folderPath, String contentText, String name, String contentType, String searchPath);

}
