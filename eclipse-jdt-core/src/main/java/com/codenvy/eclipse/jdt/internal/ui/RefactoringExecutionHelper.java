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
package com.codenvy.eclipse.jdt.internal.ui;

import com.codenvy.eclipse.core.resources.IWorkspaceRunnable;
import com.codenvy.eclipse.core.resources.ResourcesPlugin;
import com.codenvy.eclipse.core.runtime.Assert;
import com.codenvy.eclipse.core.runtime.CoreException;
import com.codenvy.eclipse.core.runtime.IProgressMonitor;
import com.codenvy.eclipse.core.runtime.IStatus;
import com.codenvy.eclipse.core.runtime.NullProgressMonitor;
import com.codenvy.eclipse.core.runtime.OperationCanceledException;
import com.codenvy.eclipse.core.runtime.Status;
import com.codenvy.eclipse.core.runtime.SubProgressMonitor;
import com.codenvy.eclipse.core.runtime.jobs.IJobManager;
import com.codenvy.eclipse.core.runtime.jobs.ISchedulingRule;
import com.codenvy.eclipse.core.runtime.jobs.Job;
import com.codenvy.eclipse.jdt.internal.corext.util.Messages;
import com.codenvy.eclipse.ltk.core.refactoring.Change;
import com.codenvy.eclipse.ltk.core.refactoring.PerformChangeOperation;
import com.codenvy.eclipse.ltk.core.refactoring.Refactoring;
import com.codenvy.eclipse.ltk.core.refactoring.RefactoringCore;
import com.codenvy.eclipse.ltk.core.refactoring.RefactoringStatus;
import com.codenvy.eclipse.ltk.core.refactoring.RefactoringStatusEntry;

import java.lang.reflect.InvocationTargetException;


/**
 * A helper class to execute a refactoring. The class takes care of pushing the
 * undo change onto the undo stack and folding editor edits into one editor
 * undo object.
 */
public class RefactoringExecutionHelper {

    private final Refactoring fRefactoring;

    private final int fStopSeverity;

    private final int fSaveMode;

    private class Operation implements IWorkspaceRunnable {
        public Change fChange;

        public PerformChangeOperation fPerformChangeOperation;

        private final boolean fForked;

        private final boolean fForkChangeExecution;

        public Operation(boolean forked, boolean forkChangeExecution) {
            fForked = forked;
            fForkChangeExecution = forkChangeExecution;
        }

        public void run(IProgressMonitor pm) throws CoreException {
            try {
                pm.beginTask("", fForked && !fForkChangeExecution ? 7 : 11); //$NON-NLS-1$
                pm.subTask(""); //$NON-NLS-1$

                final RefactoringStatus status = fRefactoring.checkAllConditions(
                        new SubProgressMonitor(pm, 4, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK));
                if (status.getSeverity() >= fStopSeverity) {
                    final boolean[] canceled = {false};
                    //					if (fForked) {
                    //						fParent.getDisplay().syncExec(new Runnable() {
                    //							public void run() {
                    //								canceled[0]= showStatusDialog(status);
                    //							}
                    //						});
                    //					} else {
                    canceled[0] = showStatusDialog(status);
                    //					}
                    if (canceled[0]) {
                        throw new OperationCanceledException();
                    }
                }

                fChange = fRefactoring.createChange(
                        new SubProgressMonitor(pm, 2, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK));
                fChange.initializeValidationData(
                        new SubProgressMonitor(pm, 1, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK));

                fPerformChangeOperation = new PerformChangeOperation(
                        fChange);//RefactoringUI.createUIAwareChangeOperation(fChange);
                fPerformChangeOperation.setUndoManager(RefactoringCore.getUndoManager(), fRefactoring.getName());
                //				if (fRefactoring instanceof IScheduledRefactoring)
                //					fPerformChangeOperation.setSchedulingRule(((IScheduledRefactoring)fRefactoring).getSchedulingRule());

                //				if (!fForked || fForkChangeExecution)
                fPerformChangeOperation.run(
                        new SubProgressMonitor(pm, 4, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK));
            } finally {
                pm.done();
            }
        }

        /**
         * @param status
         *         the status to show
         * @return <code>true</code> iff the operation should be cancelled
         */
        private boolean showStatusDialog(RefactoringStatus status) throws CoreException {
            //			Dialog dialog= RefactoringUI.createRefactoringStatusDialog(status, fParent, fRefactoring.getName(), false);
            //			return dialog.open() == IDialogConstants.CANCEL_ID;

            if (status.getSeverity() == 3) {
                return false;
            } else {
                StringBuilder b = new StringBuilder();
                for (RefactoringStatusEntry stat : status.getEntries()) {
                    b.append(stat.getMessage()).append('\n');
                }
                throw new CoreException(new Status(IStatus.ERROR, "ide", b.toString()));
            }
        }
    }

    /**
     * Creates a new refactoring execution helper.
     *
     * @param refactoring
     *         the refactoring
     * @param stopSeverity
     *         a refactoring status constant from {@link com.codenvy.eclipse.ltk.core.refactoring.RefactoringStatus}
     * @param saveMode
     *         a save mode from {RefactoringSaveHelper}
     */
    public RefactoringExecutionHelper(Refactoring refactoring, int stopSeverity, int saveMode) {
        super();
        Assert.isNotNull(refactoring);
        fRefactoring = refactoring;
        fStopSeverity = stopSeverity;
        fSaveMode = saveMode;
    }

    /**
     * Must be called in the UI thread.
     *
     * @param fork
     *         if set, the operation will be forked
     * @param cancelable
     *         if set, the operation will be cancelable
     * @throws InterruptedException
     *         thrown when the operation is cancelled
     * @throws java.lang.reflect.InvocationTargetException
     *         thrown when the operation failed to execute
     */
    public void perform(boolean fork, boolean cancelable) throws InterruptedException, InvocationTargetException, CoreException {
        perform(fork, false, cancelable);
    }

    /**
     * Must be called in the UI thread.<br>
     * <strong>Use {@link #perform(boolean, boolean)} unless you know exactly what you are doing!</strong>
     *
     * @param fork
     *         if set, the operation will be forked
     * @param forkChangeExecution
     *         if the change should not be executed in the UI thread: This may not work in any case
     * @param cancelable
     *         if set, the operation will be cancelable
     * @throws InterruptedException
     *         thrown when the operation is cancelled
     * @throws java.lang.reflect.InvocationTargetException
     *         thrown when the operation failed to execute
     */
    public void perform(boolean fork, boolean forkChangeExecution,
                        boolean cancelable) throws InterruptedException, InvocationTargetException, CoreException {
        //		Assert.isTrue(Display.getCurrent() != null);
        final IJobManager manager = Job.getJobManager();
        final ISchedulingRule rule;
        //		if (fRefactoring instanceof IScheduledRefactoring) {
        //			rule= ((IScheduledRefactoring)fRefactoring).getSchedulingRule();
        //		} else {
        rule = ResourcesPlugin.getWorkspace().getRoot();
        //		}
        try {
            try {
                Runnable r = new Runnable() {
                    public void run() {
                        manager.beginRule(rule, null);
                    }
                };
                //				BusyIndicator.showWhile(fParent.getDisplay(), r);
            } catch (OperationCanceledException e) {
                throw new InterruptedException(e.getMessage());
            }

            //			RefactoringSaveHelper saveHelper= new RefactoringSaveHelper(fSaveMode);
            //			if (!saveHelper.saveEditors(fParent))
            //				throw new InterruptedException();
            final Operation op = new Operation(fork, forkChangeExecution);
            //			fRefactoring.setValidationContext(fParent);
            try {
                op.run(new NullProgressMonitor());
                //				fExecContext.run(fork, cancelable, new WorkbenchRunnableAdapter(op, rule, true));
                //				if (fork && !forkChangeExecution && op.fPerformChangeOperation != null)
                //					fExecContext.run(false, false, new WorkbenchRunnableAdapter(op.fPerformChangeOperation, rule, true));

                if (op.fPerformChangeOperation != null) {
                    RefactoringStatus validationStatus = op.fPerformChangeOperation.getValidationStatus();
                    if (validationStatus != null && validationStatus.hasFatalError()) {
                        //						MessageDialog.openError(fParent, fRefactoring.getName(),

                        System.out.println(
                                Messages.format("The operation cannot be performed due to the following problem:\n\n{0}",
                                                validationStatus.getMessageMatchingSeverity(RefactoringStatus.FATAL)));
                        throw new InterruptedException();
                    }
                }
                //			} catch (InvocationTargetException e) {
                //				PerformChangeOperation pco= op.fPerformChangeOperation;
                //				if (pco != null && pco.changeExecutionFailed()) {
                //					ChangeExceptionHandler handler= new ChangeExceptionHandler(fParent, fRefactoring);
                //					Throwable inner= e.getTargetException();
                //					if (inner instanceof RuntimeException) {
                //						handler.handle(pco.getChange(), (RuntimeException)inner);
                //					} else if (inner instanceof CoreException) {
                //						handler.handle(pco.getChange(), (CoreException)inner);
                //					} else {
                //						throw e;
                //					}
                //				} else {
                //					throw e;
                //				}
            } catch (OperationCanceledException e) {
                e.printStackTrace();
                throw new InterruptedException(e.getMessage());
            } finally {
                //				saveHelper.triggerIncrementalBuild();
            }
        } finally {
            //			manager.endRule(rule);
            fRefactoring.setValidationContext(null);
        }
    }
}
