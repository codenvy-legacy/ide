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
package org.exoplatform.ide.java.client.editor;

import com.google.gwt.resources.client.ImageResource;

import org.exoplatform.ide.core.editor.ResourceDocumentProvider;
import org.exoplatform.ide.editor.EditorInput;
import org.exoplatform.ide.java.client.JavaClientBundle;
import org.exoplatform.ide.java.client.core.IProblemRequestor;
import org.exoplatform.ide.java.client.core.compiler.CategorizedProblem;
import org.exoplatform.ide.java.client.core.compiler.IProblem;
import org.exoplatform.ide.java.client.core.dom.CompilationUnit;
import org.exoplatform.ide.java.client.internal.text.correction.JavaCorrectionProcessor;
import org.exoplatform.ide.json.JsonCollections;
import org.exoplatform.ide.json.JsonStringMap;
import org.exoplatform.ide.runtime.Assert;
import org.exoplatform.ide.text.Position;
import org.exoplatform.ide.text.annotation.Annotation;
import org.exoplatform.ide.text.annotation.AnnotationModel;
import org.exoplatform.ide.text.annotation.AnnotationModelImpl;
import org.exoplatform.ide.texteditor.TextEditorViewImpl;
import org.exoplatform.ide.texteditor.TextEditorViewImpl.Css;
import org.exoplatform.ide.texteditor.api.quickassist.QuickFixableAnnotation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class CompilationUnitDocumentProvider extends ResourceDocumentProvider
{

   /**
    * Annotation representing an <code>IProblem</code>.
    */
   static public class ProblemAnnotation extends Annotation implements JavaAnnotation, QuickFixableAnnotation
   {

      public static final String ERROR_ANNOTATION_TYPE = "org.eclipse.jdt.ui.error"; //$NON-NLS-1$

      public static final String WARNING_ANNOTATION_TYPE = "org.eclipse.jdt.ui.warning"; //$NON-NLS-1$

      public static final String INFO_ANNOTATION_TYPE = "org.eclipse.jdt.ui.info"; //$NON-NLS-1$

      public static final String TASK_ANNOTATION_TYPE = "org.eclipse.ui.workbench.texteditor.task"; //$NON-NLS-1$

      /**
       * The layer in which task problem annotations are located.
       */
      private static final int TASK_LAYER;

      /**
       * The layer in which info problem annotations are located.
       */
      private static final int INFO_LAYER;

      /**
       * The layer in which warning problem annotations representing are located.
       */
      private static final int WARNING_LAYER;

      /**
       * The layer in which error problem annotations representing are located.
       */
      private static final int ERROR_LAYER;

      static
      {
         //TODO configure this
         TASK_LAYER = 0;
         INFO_LAYER = 1;
         WARNING_LAYER = 2;
         ERROR_LAYER = 3;
      }

      //      private static int computeLayer(String annotationType, AnnotationPreferenceLookup lookup) {
      //         Annotation annotation= new Annotation(annotationType, false, null);
      //         AnnotationPreference preference= lookup.getAnnotationPreference(annotation);
      //         if (preference != null)
      //            return preference.getPresentationLayer() + 1;
      //         else
      //            return IAnnotationAccessExtension.DEFAULT_LAYER + 1;
      //      }

      private static ImageResource fgQuickFixImage = JavaClientBundle.INSTANCE.markWarning();

      private static ImageResource fgQuickFixErrorImage = JavaClientBundle.INSTANCE.markError();

      private static ImageResource fgTaskImage = JavaClientBundle.INSTANCE.taskmrk();

      private static ImageResource fgInfoImage = JavaClientBundle.INSTANCE.imp_obj();

      private static ImageResource fgWarningImage = JavaClientBundle.INSTANCE.markWarning();

      private static ImageResource fgErrorImage = JavaClientBundle.INSTANCE.markError();

      private CompilationUnit fCompilationUnit;

      private List<JavaAnnotation> fOverlaids;

      private IProblem fProblem;

      private ImageResource fImage;

      private boolean fImageInitialized = false;

      private int fLayer = 0;

      private boolean fIsQuickFixable;

      private boolean fIsQuickFixableStateSet = false;

      public ProblemAnnotation(IProblem problem, CompilationUnit cu)
      {

         fProblem = problem;
         fCompilationUnit = cu;

         if (IProblem.Task == fProblem.getID())
         {
            setType(TASK_ANNOTATION_TYPE);
            fLayer = TASK_LAYER;
         }
         else if (fProblem.isWarning())
         {
            setType(WARNING_ANNOTATION_TYPE);
            fLayer = WARNING_LAYER;
         }
         else if (fProblem.isError())
         {
            setType(ERROR_ANNOTATION_TYPE);
            fLayer = ERROR_LAYER;
         }
         else
         {
            setType(INFO_ANNOTATION_TYPE);
            fLayer = INFO_LAYER;
         }
      }

      /**
       * @return
       */
      public int getLayer()
      {
         return fLayer;
      }

      private void initializeImage()
      {
         if (!fImageInitialized)
         {
            if (!isQuickFixableStateSet())
               setQuickFixable(isProblem() && JavaCorrectionProcessor.hasCorrections(this)); // no light bulb for tasks
            if (isQuickFixable())
            {
               if (ERROR_ANNOTATION_TYPE.equals(getType()))
                  fImage = fgQuickFixErrorImage;
               else
                  fImage = fgQuickFixImage;
            }
            else
            {
               String type = getType();
               if (TASK_ANNOTATION_TYPE.equals(type))
                  fImage = fgTaskImage;
               else if (INFO_ANNOTATION_TYPE.equals(type))
                  fImage = fgInfoImage;
               else if (WARNING_ANNOTATION_TYPE.equals(type))
                  fImage = fgWarningImage;
               else if (ERROR_ANNOTATION_TYPE.equals(type))
                  fImage = fgErrorImage;
            }
            fImageInitialized = true;
         }
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText()
      {
         return fProblem.getMessage();
      }

      /**
       * {@inheritDoc}
       */
      public String[] getArguments()
      {
         return isProblem() ? fProblem.getArguments() : null;
      }

      /**
       * {@inheritDoc}
       */
      public int getId()
      {
         return fProblem.getID();
      }

      /**
       * {@inheritDoc}
       */
      public boolean isProblem()
      {
         String type = getType();
         return WARNING_ANNOTATION_TYPE.equals(type) || ERROR_ANNOTATION_TYPE.equals(type);
      }

      /**
       * {@inheritDoc}
       */
      public boolean hasOverlay()
      {
         return false;
      }

      /**
       * {@inheritDoc}
       */
      public JavaAnnotation getOverlay()
      {
         return null;
      }

      /**
       * {@inheritDoc}
       */
      public void addOverlaid(JavaAnnotation annotation)
      {
         if (fOverlaids == null)
            fOverlaids = new ArrayList<JavaAnnotation>(1);
         fOverlaids.add(annotation);
      }

      /**
       * {@inheritDoc}
       */
      public void removeOverlaid(JavaAnnotation annotation)
      {
         if (fOverlaids != null)
         {
            fOverlaids.remove(annotation);
            if (fOverlaids.size() == 0)
               fOverlaids = null;
         }
      }

      /**
       * {@inheritDoc}
       */
      public Iterator<JavaAnnotation> getOverlaidIterator()
      {
         if (fOverlaids != null)
            return fOverlaids.iterator();
         return null;
      }

      /**
       * {@inheritDoc}
       */
      public CompilationUnit getCompilationUnit()
      {
         return fCompilationUnit;
      }

      /**
       * {@inheritDoc}
       */
      public String getMarkerType()
      {
         if (fProblem instanceof CategorizedProblem)
            return ((CategorizedProblem)fProblem).getMarkerType();
         return null;
      }

      /**
       * {@inheritDoc}
       */
      public void setQuickFixable(boolean state)
      {
         fIsQuickFixable = state;
         fIsQuickFixableStateSet = true;
      }

      /**
       * {@inheritDoc}
       */
      public boolean isQuickFixableStateSet()
      {
         return fIsQuickFixableStateSet;
      }

      /**
       * {@inheritDoc}
       */
      public boolean isQuickFixable()
      {
         Assert.isTrue(isQuickFixableStateSet());
         return fIsQuickFixable;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public ImageResource getImage()
      {
         if (!fImageInitialized)
            initializeImage();
         return fImage;
      }

   }

   class JavaAnnotationModel extends AnnotationModelImpl implements AnnotationModel, IProblemRequestor
   {

      private List<IProblem> reportedProblems;

      //      private List<JavaMarkerAnnotation> fPreviouslyOverlaid= null;
      //      
      //      private List<JavaMarkerAnnotation> fCurrentlyOverlaid= new ArrayList<JavaMarkerAnnotation>();

      private List<ProblemAnnotation> fGeneratedAnnotations = new ArrayList<ProblemAnnotation>();

      protected Position createPositionFromProblem(IProblem problem)
      {
         int start = problem.getSourceStart();
         int end = problem.getSourceEnd();

         if (start == -1 && end == -1)
            return new Position(0);

         if (start == -1)
            return new Position(end);

         if (end == -1)
            return new Position(start);

         int length = end - start + 1;
         if (length < 0)
            return null;

         return new Position(start, length);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public void acceptProblem(IProblem problem)
      {
         reportedProblems.add(problem);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public void beginReporting()
      {
         reportedProblems = new ArrayList<IProblem>();
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public void endReporting()
      {
         reportProblems(reportedProblems);

      }

      /**
       * @param problems
       */
      private void reportProblems(List<IProblem> problems)
      {
         boolean temporaryProblemsChanged = false;

         //            fPreviouslyOverlaid= fCurrentlyOverlaid;
         //            fCurrentlyOverlaid= new ArrayList<JavaMarkerAnnotation>();

         if (fGeneratedAnnotations.size() > 0)
         {
            temporaryProblemsChanged = true;
            removeAnnotations(fGeneratedAnnotations, false, true);
            fGeneratedAnnotations.clear();
         }

         if (reportedProblems != null && reportedProblems.size() > 0)
         {

            Iterator<IProblem> e = reportedProblems.iterator();
            while (e.hasNext())
            {

               IProblem problem = e.next();
               Position position = createPositionFromProblem(problem);
               if (position != null)
               {

                  //                  try
                  //                  {
                  ProblemAnnotation annotation = new ProblemAnnotation(problem, null);
                  //                     overlayMarkers(position, annotation);
                  addAnnotation(annotation, position, false);
                  fGeneratedAnnotations.add(annotation);

                  temporaryProblemsChanged = true;
                  //                  }
                  //                  catch (BadLocationException x)
                  //                  {
                  //                     // ignore invalid position
                  //                  }
               }
            }

            //            removeMarkerOverlays();
            //            fPreviouslyOverlaid = null;
         }

         if (temporaryProblemsChanged)
            fireModelChanged();
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public boolean isActive()
      {
         // TODO Auto-generated method stub
         return false;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public JsonStringMap<String> getAnnotationDecorations()
      {
         JsonStringMap<String> decorations = JsonCollections.createStringMap();
         //TODO configure this
         decorations.put("org.eclipse.jdt.ui.error", css.lineError());
         decorations.put("org.eclipse.jdt.ui.warning", css.lineWarning());

         return decorations;
      }
   }

   private AnnotationModel annotationModel;

   private TextEditorViewImpl.Css css;

   /**
    * @param css
    */
   public CompilationUnitDocumentProvider(Css css)
   {
      this.css = css;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AnnotationModel getAnnotationModel(EditorInput input)
   {
      if (annotationModel == null)
      {
         annotationModel = new JavaAnnotationModel();
      }
      return annotationModel;
   }

}
