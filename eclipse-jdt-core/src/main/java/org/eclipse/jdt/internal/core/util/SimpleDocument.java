/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.core.util;

import org.exoplatform.ide.editor.shared.text.IDocument;
import org.exoplatform.ide.editor.shared.text.IDocumentListener;
import org.exoplatform.ide.editor.shared.text.IDocumentPartitioner;
import org.exoplatform.ide.editor.shared.text.IDocumentPartitioningListener;
import org.exoplatform.ide.editor.shared.text.IPositionUpdater;
import org.exoplatform.ide.editor.shared.text.IRegion;
import org.exoplatform.ide.editor.shared.text.ITypedRegion;
import org.exoplatform.ide.editor.shared.text.Position;

/**
 * Minimal implementation of IDocument to apply text edit onto a string.
 */
public class SimpleDocument implements IDocument
{

   private StringBuffer buffer;


   public SimpleDocument(String source)
   {
      this.buffer = new StringBuffer(source);
   }

   /* (non-Javadoc)
    * @see org.exoplatform.ide.editor.shared.text.IDocument#getChar(int)
    */
   public char getChar(int offset)
   {
      return 0;
   }

   /* (non-Javadoc)
    * @see org.exoplatform.ide.editor.shared.text.IDocument#getLength()
    */
   public int getLength()
   {
      return this.buffer.length();
   }

   /* (non-Javadoc)
    * @see org.exoplatform.ide.editor.shared.text.IDocument#get()
    */
   public String get()
   {
      return this.buffer.toString();
   }

   /* (non-Javadoc)
    * @see org.exoplatform.ide.editor.shared.text.IDocument#get(int, int)
    */
   public String get(int offset, int length)
   {
      return this.buffer.substring(offset, offset + length);
   }

   /* (non-Javadoc)
    * @see org.exoplatform.ide.editor.shared.text.IDocument#set(java.lang.String)
    */
   public void set(String text)
   {
      // defining interface method
   }

   /* (non-Javadoc)
    * @see org.exoplatform.ide.editor.shared.text.IDocument#replace(int, int, java.lang.String)
    */
   public void replace(int offset, int length, String text)
   {

      this.buffer.replace(offset, offset + length, text);
   }

   /* (non-Javadoc)
    * @see org.exoplatform.ide.editor.shared.text.IDocument#addDocumentListener(org.exoplatform.ide.editor.shared.text.IDocumentListener)
    */
   public void addDocumentListener(IDocumentListener listener)
   {
      // defining interface method
   }

   /* (non-Javadoc)
    * @see org.exoplatform.ide.editor.shared.text.IDocument#removeDocumentListener(org.exoplatform.ide.editor.shared.text.IDocumentListener)
    */
   public void removeDocumentListener(IDocumentListener listener)
   {
      // defining interface method
   }

   /* (non-Javadoc)
    * @see org.exoplatform.ide.editor.shared.text.IDocument#addPrenotifiedDocumentListener(org.exoplatform.ide.editor.shared.text.IDocumentListener)
    */
   public void addPrenotifiedDocumentListener(IDocumentListener documentAdapter)
   {
      // defining interface method
   }

   /* (non-Javadoc)
    * @see org.exoplatform.ide.editor.shared.text.IDocument#removePrenotifiedDocumentListener(org.exoplatform.ide.editor.shared.text.IDocumentListener)
    */
   public void removePrenotifiedDocumentListener(IDocumentListener documentAdapter)
   {
      // defining interface method
   }

   /* (non-Javadoc)
    * @see org.exoplatform.ide.editor.shared.text.IDocument#addPositionCategory(java.lang.String)
    */
   public void addPositionCategory(String category)
   {
      // defining interface method
   }

   /* (non-Javadoc)
    * @see org.exoplatform.ide.editor.shared.text.IDocument#removePositionCategory(java.lang.String)
    */
   public void removePositionCategory(String category)
   {
      // defining interface method
   }

   /* (non-Javadoc)
    * @see org.exoplatform.ide.editor.shared.text.IDocument#getPositionCategories()
    */
   public String[] getPositionCategories()
   {
      // defining interface method
      return null;
   }

   /* (non-Javadoc)
    * @see org.exoplatform.ide.editor.shared.text.IDocument#containsPositionCategory(java.lang.String)
    */
   public boolean containsPositionCategory(String category)
   {
      // defining interface method
      return false;
   }

   /* (non-Javadoc)
    * @see org.exoplatform.ide.editor.shared.text.IDocument#addPosition(org.eclipse.jface.text.Position)
    */
   public void addPosition(Position position)
   {
      // defining interface method
   }

   /* (non-Javadoc)
    * @see org.exoplatform.ide.editor.shared.text.IDocument#removePosition(org.eclipse.jface.text.Position)
    */
   public void removePosition(Position position)
   {
      // defining interface method
   }

   /* (non-Javadoc)
    * @see org.exoplatform.ide.editor.shared.text.IDocument#addPosition(java.lang.String, org.eclipse.jface.text.Position)
    */
   public void addPosition(String category, Position position)
   {
      // defining interface method
   }

   /* (non-Javadoc)
    * @see org.exoplatform.ide.editor.shared.text.IDocument#removePosition(java.lang.String, org.eclipse.jface.text.Position)
    */
   public void removePosition(String category, Position position)
   {
      // defining interface method
   }

   /* (non-Javadoc)
    * @see org.exoplatform.ide.editor.shared.text.IDocument#getPositions(java.lang.String)
    */
   public Position[] getPositions(String category)
   {
      // defining interface method
      return null;
   }

   /* (non-Javadoc)
    * @see org.exoplatform.ide.editor.shared.text.IDocument#containsPosition(java.lang.String, int, int)
    */
   public boolean containsPosition(String category, int offset, int length)
   {
      // defining interface method
      return false;
   }

   /* (non-Javadoc)
    * @see org.exoplatform.ide.editor.shared.text.IDocument#computeIndexInCategory(java.lang.String, int)
    */
   public int computeIndexInCategory(String category, int offset)
   {
      // defining interface method
      return 0;
   }

   /* (non-Javadoc)
    * @see org.exoplatform.ide.editor.shared.text.IDocument#addPositionUpdater(org.eclipse.jface.text.IPositionUpdater)
    */
   public void addPositionUpdater(IPositionUpdater updater)
   {
      // defining interface method
   }

   /* (non-Javadoc)
    * @see org.exoplatform.ide.editor.shared.text.IDocument#removePositionUpdater(org.eclipse.jface.text.IPositionUpdater)
    */
   public void removePositionUpdater(IPositionUpdater updater)
   {
      // defining interface method
   }

   /* (non-Javadoc)
    * @see org.exoplatform.ide.editor.shared.text.IDocument#insertPositionUpdater(org.eclipse.jface.text.IPositionUpdater, int)
    */
   public void insertPositionUpdater(IPositionUpdater updater, int index)
   {
      // defining interface method
   }

   /* (non-Javadoc)
    * @see org.exoplatform.ide.editor.shared.text.IDocument#getPositionUpdaters()
    */
   public IPositionUpdater[] getPositionUpdaters()
   {
      // defining interface method
      return null;
   }

   /* (non-Javadoc)
    * @see org.exoplatform.ide.editor.shared.text.IDocument#getLegalContentTypes()
    */
   public String[] getLegalContentTypes()
   {
      // defining interface method
      return null;
   }

   /* (non-Javadoc)
    * @see org.exoplatform.ide.editor.shared.text.IDocument#getContentType(int)
    */
   public String getContentType(int offset)
   {
      // defining interface method
      return null;
   }

   /* (non-Javadoc)
    * @see org.exoplatform.ide.editor.shared.text.IDocument#getPartition(int)
    */
   public ITypedRegion getPartition(int offset)
   {
      // defining interface method
      return null;
   }

   /* (non-Javadoc)
    * @see org.exoplatform.ide.editor.shared.text.IDocument#computePartitioning(int, int)
    */
   public ITypedRegion[] computePartitioning(int offset, int length)
   {
      // defining interface method
      return null;
   }

   /* (non-Javadoc)
    * @see org.exoplatform.ide.editor.shared.text.IDocument#addDocumentPartitioningListener(org.exoplatform.ide.editor.shared.text.IDocumentPartitioningListener)
    */
   public void addDocumentPartitioningListener(IDocumentPartitioningListener listener)
   {
      // defining interface method
   }

   /* (non-Javadoc)
    * @see org.exoplatform.ide.editor.shared.text.IDocument#removeDocumentPartitioningListener(org.exoplatform.ide.editor.shared.text.IDocumentPartitioningListener)
    */
   public void removeDocumentPartitioningListener(IDocumentPartitioningListener listener)
   {
      // defining interface method
   }

   /* (non-Javadoc)
    * @see org.exoplatform.ide.editor.shared.text.IDocument#setDocumentPartitioner(org.exoplatform.ide.editor.shared.text.IDocumentPartitioner)
    */
   public void setDocumentPartitioner(IDocumentPartitioner partitioner)
   {
      // defining interface method
   }

   /* (non-Javadoc)
    * @see org.exoplatform.ide.editor.shared.text.IDocument#getDocumentPartitioner()
    */
   public IDocumentPartitioner getDocumentPartitioner()
   {
      // defining interface method
      return null;
   }

   /* (non-Javadoc)
    * @see org.exoplatform.ide.editor.shared.text.IDocument#getLineLength(int)
    */
   public int getLineLength(int line)
   {
      // defining interface method
      return 0;
   }

   /* (non-Javadoc)
    * @see org.exoplatform.ide.editor.shared.text.IDocument#getLineOfOffset(int)
    */
   public int getLineOfOffset(int offset)
   {
      // defining interface method
      return 0;
   }

   /* (non-Javadoc)
    * @see org.exoplatform.ide.editor.shared.text.IDocument#getLineOffset(int)
    */
   public int getLineOffset(int line)
   {
      // defining interface method
      return 0;
   }

   /* (non-Javadoc)
    * @see org.exoplatform.ide.editor.shared.text.IDocument#getLineInformation(int)
    */
   public IRegion getLineInformation(int line)
   {
      // defining interface method
      return null;
   }

   /* (non-Javadoc)
    * @see org.exoplatform.ide.editor.shared.text.IDocument#getLineInformationOfOffset(int)
    */
   public IRegion getLineInformationOfOffset(int offset)
   {
      // defining interface method
      return null;
   }

   /* (non-Javadoc)
    * @see org.exoplatform.ide.editor.shared.text.IDocument#getNumberOfLines()
    */
   public int getNumberOfLines()
   {
      // defining interface method
      return 0;
   }

   /* (non-Javadoc)
    * @see org.exoplatform.ide.editor.shared.text.IDocument#getNumberOfLines(int, int)
    */
   public int getNumberOfLines(int offset, int length)
   {
      // defining interface method
      return 0;
   }

   /* (non-Javadoc)
    * @see org.exoplatform.ide.editor.shared.text.IDocument#computeNumberOfLines(java.lang.String)
    */
   public int computeNumberOfLines(String text)
   {
      // defining interface method
      return 0;
   }

   /* (non-Javadoc)
    * @see org.exoplatform.ide.editor.shared.text.IDocument#getLegalLineDelimiters()
    */
   public String[] getLegalLineDelimiters()
   {
      // defining interface method
      return null;
   }

   /* (non-Javadoc)
    * @see org.exoplatform.ide.editor.shared.text.IDocument#getLineDelimiter(int)
    */
   public String getLineDelimiter(int line)
   {
      // defining interface method
      return null;
   }

   /**
    * @see org.exoplatform.ide.editor.shared.text.IDocument#search(int, java.lang.String, boolean, boolean, boolean)
    * @deprecated
    */
   public int search(int startOffset, String findString, boolean forwardSearch, boolean caseSensitive,
      boolean wholeWord)
   {
      // defining interface method
      return 0;
   }

}
