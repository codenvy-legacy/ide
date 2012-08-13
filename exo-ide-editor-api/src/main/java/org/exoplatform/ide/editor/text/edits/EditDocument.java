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
package org.exoplatform.ide.editor.text.edits;

import org.exoplatform.ide.editor.text.BadLocationException;
import org.exoplatform.ide.editor.text.BadPositionCategoryException;
import org.exoplatform.ide.editor.text.FindReplaceDocumentAdapter;
import org.exoplatform.ide.editor.text.IDocument;
import org.exoplatform.ide.editor.text.IDocumentListener;
import org.exoplatform.ide.editor.text.IDocumentPartitioner;
import org.exoplatform.ide.editor.text.IDocumentPartitioningListener;
import org.exoplatform.ide.editor.text.IPositionUpdater;
import org.exoplatform.ide.editor.text.IRegion;
import org.exoplatform.ide.editor.text.ITypedRegion;
import org.exoplatform.ide.editor.text.Position;

class EditDocument implements IDocument
{

   private StringBuffer fBuffer;

   public EditDocument(String content)
   {
      fBuffer = new StringBuffer(content);
   }

   public void addPosition(Position position) throws BadLocationException
   {
      throw new UnsupportedOperationException();
   }

   public void addPosition(String category, Position position) throws BadLocationException,
      BadPositionCategoryException
   {
      throw new UnsupportedOperationException();
   }

   public void addPositionUpdater(IPositionUpdater updater)
   {
      throw new UnsupportedOperationException();
   }

   //
   // public void addPrenotifiedDocumentListener(IDocumentListener documentAdapter) {
   // throw new UnsupportedOperationException();
   // }
   //
   // public int computeIndexInCategory(String category, int offset) throws BadLocationException, BadPositionCategoryException {
   // throw new UnsupportedOperationException();
   // }

   public int computeNumberOfLines(String text)
   {
      throw new UnsupportedOperationException();
   }

   public ITypedRegion[] computePartitioning(int offset, int length) throws BadLocationException
   {
      throw new UnsupportedOperationException();
   }

   public boolean containsPosition(String category, int offset, int length)
   {
      throw new UnsupportedOperationException();
   }

   public boolean containsPositionCategory(String category)
   {
      throw new UnsupportedOperationException();
   }

   public String get()
   {
      return fBuffer.toString();
   }

   public String get(int offset, int length) throws BadLocationException
   {
      return fBuffer.substring(offset, offset + length);
   }

   public char getChar(int offset) throws BadLocationException
   {
      throw new UnsupportedOperationException();
   }

   public String getContentType(int offset) throws BadLocationException
   {
      throw new UnsupportedOperationException();
   }

   public IDocumentPartitioner getDocumentPartitioner()
   {
      throw new UnsupportedOperationException();
   }

   public String[] getLegalContentTypes()
   {
      throw new UnsupportedOperationException();
   }

   public String[] getLegalLineDelimiters()
   {
      throw new UnsupportedOperationException();
   }

   public int getLength()
   {
      return fBuffer.length();
   }

   public String getLineDelimiter(int line) throws BadLocationException
   {
      throw new UnsupportedOperationException();
   }

   public IRegion getLineInformation(int line) throws BadLocationException
   {
      throw new UnsupportedOperationException();
   }

   public IRegion getLineInformationOfOffset(int offset) throws BadLocationException
   {
      throw new UnsupportedOperationException();
   }

   public int getLineLength(int line) throws BadLocationException
   {
      throw new UnsupportedOperationException();
   }

   public int getLineOffset(int line) throws BadLocationException
   {
      throw new UnsupportedOperationException();
   }

   public int getLineOfOffset(int offset) throws BadLocationException
   {
      throw new UnsupportedOperationException();
   }

   public int getNumberOfLines()
   {
      throw new UnsupportedOperationException();
   }

   public int getNumberOfLines(int offset, int length) throws BadLocationException
   {
      throw new UnsupportedOperationException();
   }

   public ITypedRegion getPartition(int offset) throws BadLocationException
   {
      throw new UnsupportedOperationException();
   }

   public String[] getPositionCategories()
   {
      throw new UnsupportedOperationException();
   }

   public Position[] getPositions(String category) throws BadPositionCategoryException
   {
      throw new UnsupportedOperationException();
   }

   public IPositionUpdater[] getPositionUpdaters()
   {
      throw new UnsupportedOperationException();
   }

   public void insertPositionUpdater(IPositionUpdater updater, int index)
   {
      throw new UnsupportedOperationException();
   }

   public void removeDocumentListener(IDocumentListener listener)
   {
      throw new UnsupportedOperationException();
   }

   public void removeDocumentPartitioningListener(IDocumentPartitioningListener listener)
   {
      throw new UnsupportedOperationException();
   }

   public void removePosition(Position position)
   {
      throw new UnsupportedOperationException();
   }

   public void removePosition(String category, Position position) throws BadPositionCategoryException
   {
      throw new UnsupportedOperationException();
   }

   public void removePositionCategory(String category) throws BadPositionCategoryException
   {
      throw new UnsupportedOperationException();
   }

   public void removePositionUpdater(IPositionUpdater updater)
   {
      throw new UnsupportedOperationException();
   }

   public void removePrenotifiedDocumentListener(IDocumentListener documentAdapter)
   {
      throw new UnsupportedOperationException();
   }

   public void replace(int offset, int length, String text) throws BadLocationException
   {
      fBuffer.replace(offset, offset + length, text);
   }

   /**
    * {@inheritDoc}
    * 
    * @deprecated As of 3.0 search is provided by {@link FindReplaceDocumentAdapter}
    */
   public int search(int startOffset, String findString, boolean forwardSearch, boolean caseSensitive, boolean wholeWord)
      throws BadLocationException
   {
      throw new UnsupportedOperationException();
   }

   public void set(String text)
   {
      throw new UnsupportedOperationException();
   }

   /** @see org.exoplatform.ide.editor.text.IDocument#addPositionCategory(java.lang.String) */
   @Override
   public void addPositionCategory(String category)
   {
      throw new UnsupportedOperationException();
   }

   public void setDocumentPartitioner(IDocumentPartitioner partitioner)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * @see org.exoplatform.ide.editor.text.IDocument#addDocumentListener(org.exoplatform.ide.editor.text.IDocumentListener)
    */
   @Override
   public void addDocumentListener(IDocumentListener listener)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * @see org.exoplatform.ide.editor.text.IDocument#addDocumentPartitioningListener(org.exoplatform.ide.editor.text.IDocumentPartitioningListener)
    */
   @Override
   public void addDocumentPartitioningListener(IDocumentPartitioningListener listener)
   {
      throw new UnsupportedOperationException();
   }
}
