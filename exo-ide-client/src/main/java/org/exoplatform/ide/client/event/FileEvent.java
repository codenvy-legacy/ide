package org.exoplatform.ide.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event that describes the fact that file is going to be opened
 * 
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 * @version $Id: OpenFileEvent.java 34360 2009-07-22 23:58:59Z nzamosenchuk $
 *
 */
public class FileEvent extends GwtEvent<FileEventHandler>
{
   // TODO : should accept model object, not abstract strings 

   public static Type<FileEventHandler> TYPE = new Type<FileEventHandler>();

   public static enum FileOperation {
      OPEN, SAVE, CLOSE;
   }

   // Replace with resource model
   private String fileName;

   private FileOperation fileOperation;

   /**
    * @param fileName name of the file
    */
   public FileEvent(String fileName, FileOperation fileOperation)
   {
      this.fileOperation = fileOperation;
      this.fileName = fileName;
   }

   /**
   * {@inheritDoc}
   */
   @Override
   public Type<FileEventHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @return the name of the file // should return the model object
    */
   public String getFileName()
   {
      return fileName;
   }

   /**
    * @return the type of operation performed with file
    */
   public FileOperation getOperationType()
   {
      return fileOperation;
   }

   @Override
   protected void dispatch(FileEventHandler handler)
   {
      handler.onFileOperation(this);
   }
}
