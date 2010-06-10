package org.exoplatform.ideall.vfs.api;


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
    * @param folder
    * @param text
    * @param mimeType
    * @param path
    */
   public abstract void search(Folder folder, String text, String mimeType, String path);

}
