/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.editor.text;

import org.exoplatform.ide.editor.runtime.Assert;

/**
 * Copy-on-write <code>ITextStore</code> wrapper.
 * <p>
 * This implementation uses an unmodifiable text store for the initial content. Upon first modification attempt, the unmodifiable
 * store is replaced with a modifiable instance which must be supplied in the constructor.
 * </p>
 * <p>
 * This class is not intended to be subclassed.
 * </p>
 * 
 * @since 3.2
 * @noextend This class is not intended to be subclassed by clients.
 */
public class CopyOnWriteTextStore implements ITextStore
{

   /**
    * An unmodifiable String based text store. It is not possible to modify the initial content. Trying to {@link #replace} a text
    * range or {@link #set} new content will throw an <code>UnsupportedOperationException</code>.
    */
   private static class StringTextStore implements ITextStore
   {

      /** Minimum text limit whether to enable String copying */
      private static final int SMALL_TEXT_LIMIT = 1024 * 1024;

      /** Represents the content of this text store. */
      private final String fText;

      /**
       * Minimum length limit below which {@link #get(int, int)} will return a String copy
       */
      private final int fCopyLimit;

      /** Create an empty text store. */
      private StringTextStore()
      {
         this(""); //$NON-NLS-1$
      }

      /**
       * Create a text store with initial content.
       * 
       * @param text the initial content
       */
      private StringTextStore(String text)
      {
         super();
         fText = text != null ? text : ""; //$NON-NLS-1$
         fCopyLimit = fText.length() > SMALL_TEXT_LIMIT ? fText.length() / 2 : 0;
      }

      /* @see org.eclipse.jface.text.ITextStore#get(int) */
      public char get(int offset)
      {
         return fText.charAt(offset);
      }

      /* @see org.eclipse.jface.text.ITextStore#get(int, int) */
      public String get(int offset, int length)
      {
         if (length < fCopyLimit)
         {
            // create a copy to avoid sharing of contained char[] - bug 292664
            return new String(fText.substring(offset, offset + length).toCharArray());
         }
         return fText.substring(offset, offset + length);
      }

      /* @see org.eclipse.jface.text.ITextStore#getLength() */
      public int getLength()
      {
         return fText.length();
      }

      /*
       * @see org.eclipse.jface.text.ITextStore#replace(int, int, java.lang.String)
       */
      public void replace(int offset, int length, String text)
      {
         // modification not supported
         throw new UnsupportedOperationException();
      }

      /* @see org.eclipse.jface.text.ITextStore#set(java.lang.String) */
      public void set(String text)
      {
         // modification not supported
         throw new UnsupportedOperationException();
      }

   }

   /** The underlying "real" text store */
   protected ITextStore fTextStore = new StringTextStore();

   /** A modifiable <code>ITextStore</code> instance */
   private final ITextStore fModifiableTextStore;

   /**
    * Creates an empty text store. The given text store will be used upon first modification attempt.
    * 
    * @param modifiableTextStore a modifiable <code>ITextStore</code> instance, may not be <code>null</code>
    */
   public CopyOnWriteTextStore(ITextStore modifiableTextStore)
   {
      Assert.isNotNull(modifiableTextStore);
      fTextStore = new StringTextStore();
      fModifiableTextStore = modifiableTextStore;
   }

   /* @see org.eclipse.jface.text.ITextStore#get(int) */
   public char get(int offset)
   {
      return fTextStore.get(offset);
   }

   /* @see org.eclipse.jface.text.ITextStore#get(int, int) */
   public String get(int offset, int length)
   {
      return fTextStore.get(offset, length);
   }

   /* @see org.eclipse.jface.text.ITextStore#getLength() */
   public int getLength()
   {
      return fTextStore.getLength();
   }

   /* @see org.eclipse.jface.text.ITextStore#replace(int, int, java.lang.String) */
   public void replace(int offset, int length, String text)
   {
      if (fTextStore != fModifiableTextStore)
      {
         String content = fTextStore.get(0, fTextStore.getLength());
         fTextStore = fModifiableTextStore;
         fTextStore.set(content);
      }
      fTextStore.replace(offset, length, text);
   }

   /* @see org.eclipse.jface.text.ITextStore#set(java.lang.String) */
   public void set(String text)
   {
      fTextStore = new StringTextStore(text);
      fModifiableTextStore.set(""); //$NON-NLS-1$
   }

}
