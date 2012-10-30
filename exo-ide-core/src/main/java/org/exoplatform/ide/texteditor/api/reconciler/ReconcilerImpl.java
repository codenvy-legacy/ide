/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.texteditor.api.reconciler;

import org.exoplatform.ide.json.JsonCollections;
import org.exoplatform.ide.json.JsonStringMap;
import org.exoplatform.ide.json.JsonStringMap.IterationCallback;
import org.exoplatform.ide.text.BadLocationException;
import org.exoplatform.ide.text.Document;
import org.exoplatform.ide.text.DocumentEvent;
import org.exoplatform.ide.text.DocumentListener;
import org.exoplatform.ide.text.Region;
import org.exoplatform.ide.text.RegionImpl;
import org.exoplatform.ide.text.TextUtilities;
import org.exoplatform.ide.text.TypedRegion;
import org.exoplatform.ide.texteditor.api.TextEditorPartDisplay;
import org.exoplatform.ide.texteditor.api.TextInputListener;
import org.exoplatform.ide.util.executor.BasicIncrementalScheduler;
import org.exoplatform.ide.util.executor.IncrementalScheduler.Task;

/**
 * Default implementation of {@link Reconciler}
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class ReconcilerImpl implements Reconciler
{

   protected class Listener implements TextInputListener, DocumentListener
   {

      /**
       * {@inheritDoc}
       */
      @Override
      public void inputDocumentChanged(Document oldDocument, Document newDocument)
      {
         newDocument.addDocumentListener(this);
         document = newDocument;
         reconcilerDocumentChanged();
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public void documentAboutToBeChanged(DocumentEvent event)
      {
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public void documentChanged(DocumentEvent event)
      {
         createDirtyRegion(event);
         scheduler.schedule(task);
      }

   }

   private Task task = new Task()
   {

      @Override
      public boolean run(int workAmount)
      {
         DirtyRegion region = dirtyRegionQueue.removeNextDirtyRegion();
         process(region);
         return dirtyRegionQueue.getSize() > 0;
      }
   };

   private JsonStringMap<ReconcilingStrategy> strategys;

   private String partition;

   private Listener listener;

   private TextEditorPartDisplay textEditor;

   private DirtyRegionQueue dirtyRegionQueue;

   private final BasicIncrementalScheduler scheduler;

   private Document document;

   /**
    * 
    */
   public ReconcilerImpl(String partition, BasicIncrementalScheduler scheduler)
   {
      this.partition = partition;
      this.scheduler = scheduler;
      strategys = JsonCollections.createStringMap();
   }

   /**
    * 
    */
   private void reconcilerDocumentChanged()
   {
      strategys.iterate(new IterationCallback<ReconcilingStrategy>()
      {
         
         @Override
         public void onIteration(String key, ReconcilingStrategy value)
         {
            value.setDocument(document);
         }
      });
      scheduler.schedule(task);
   }

   /**
    * @see org.exoplatform.ide.texteditor.api.reconciler.Reconciler#install(org.exoplatform.ide.texteditor.api.TextEditorPartDisplay)
    */
   @Override
   public void install(TextEditorPartDisplay display)
   {
      this.textEditor = display;
      dirtyRegionQueue = new DirtyRegionQueue();
      listener = new Listener();
      display.addTextInputListener(listener);
   }

   /**
    * @see org.exoplatform.ide.texteditor.api.reconciler.Reconciler#uninstall()
    */
   @Override
   public void uninstall()
   {
      if (listener != null)
      {
         textEditor.removeTextInputListener(listener);
         listener = null;
      }
      if (scheduler != null)
      {
         scheduler.cancel();
      }
   }

   /**
    * Processes a dirty region. If the dirty region is <code>null</code> the whole
    * document is consider being dirty. The dirty region is partitioned by the
    * document and each partition is handed over to a reconciling strategy registered
    * for the partition's content type.
    *
    * @param dirtyRegion the dirty region to be processed
    * @see AbstractReconciler#process(DirtyRegion)
    */
   protected void process(DirtyRegion dirtyRegion)
   {

      Region region = dirtyRegion;

      if (region == null)
         region = new RegionImpl(0, getDocument().getLength());

      TypedRegion[] regions = computePartitioning(region.getOffset(), region.getLength());

      for (int i = 0; i < regions.length; i++)
      {
         TypedRegion r = regions[i];
         ReconcilingStrategy s = getReconcilingStrategy(r.getType());
         if (s == null)
            continue;

         if (dirtyRegion != null)
            s.reconcile(dirtyRegion, r);
         else
            s.reconcile(r);
      }
   }

   /**
    * Computes and returns the partitioning for the given region of the input document
    * of the reconciler's connected text viewer.
    *
    * @param offset the region offset
    * @param length the region length
    * @return the computed partitioning
    */
   private TypedRegion[] computePartitioning(int offset, int length)
   {
      TypedRegion[] regions = null;
      try
      {
         regions = TextUtilities.computePartitioning(getDocument(), getDocumentPartitioning(), offset, length, false);
      }
      catch (BadLocationException x)
      {
         regions = new TypedRegion[0];
      }
      return regions;
   }

   /**
    * Returns the input document of the text display this reconciler is installed on.
    *
    * @return the reconciler document
    */
   protected Document getDocument()
   {
      return document;
   }

   /**
    * Creates a dirty region for a document event and adds it to the queue.
    *
    * @param e the document event for which to create a dirty region
    */
   private void createDirtyRegion(DocumentEvent e)
   {
      if (e.getLength() == 0 && e.getText() != null)
      {
         // Insert
         dirtyRegionQueue.addDirtyRegion(new DirtyRegion(e.getOffset(), e.getText().length(), DirtyRegion.INSERT, e
            .getText()));

      }
      else if (e.getText() == null || e.getText().length() == 0)
      {
         // Remove
         dirtyRegionQueue.addDirtyRegion(new DirtyRegion(e.getOffset(), e.getLength(), DirtyRegion.REMOVE, null));

      }
      else
      {
         // Replace (Remove + Insert)
         dirtyRegionQueue.addDirtyRegion(new DirtyRegion(e.getOffset(), e.getLength(), DirtyRegion.REMOVE, null));
         dirtyRegionQueue.addDirtyRegion(new DirtyRegion(e.getOffset(), e.getText().length(), DirtyRegion.INSERT, e
            .getText()));
      }
   }

   /**
    * @see org.exoplatform.ide.texteditor.api.reconciler.Reconciler#getReconcilingStrategy(java.lang.String)
    */
   @Override
   public ReconcilingStrategy getReconcilingStrategy(String contentType)
   {
      return strategys.get(contentType);
   }

   /**
    * @see org.exoplatform.ide.texteditor.api.reconciler.Reconciler#getDocumentPartitioning()
    */
   @Override
   public String getDocumentPartitioning()
   {
      return partition;
   }

   public void addReconcilingStrategy(String contentType, ReconcilingStrategy strategy)
   {
      strategys.put(contentType, strategy);
   }
}
