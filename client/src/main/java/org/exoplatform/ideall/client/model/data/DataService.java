package org.exoplatform.ideall.client.model.data;

import org.exoplatform.ideall.client.model.File;
import org.exoplatform.ideall.client.model.Item;

public abstract class DataService
{

   private static DataService instance;

   public static DataService getInstance()
   {
      return instance;
   }

   protected DataService()
   {
      instance = this;
   }

   /**
    * Get folder content
    * 
    * @param path
    */
   public abstract void getFolderContent(String path);

   /**
    * Create new folder
    * 
    * @param path
    */
   public abstract void createFolder(String path);

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
