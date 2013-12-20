/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.jdt.internal.text.correction.proposals;

import com.codenvy.ide.ext.java.jdt.Images;
import com.codenvy.ide.ext.java.jdt.codeassistant.api.JavaCompletionProposal;
import com.codenvy.ide.ext.java.jdt.codeassistant.ui.StyledString;
import com.codenvy.ide.ext.java.jdt.core.JavaCore;
import com.codenvy.ide.ext.java.jdt.internal.text.correction.ICommandAccess;
import com.codenvy.ide.ext.java.jdt.refactoring.Change;
import com.codenvy.ide.ext.java.jdt.refactoring.NullChange;
import com.codenvy.ide.ext.java.jdt.refactoring.RefactoringStatus;
import com.codenvy.ide.runtime.CoreException;
import com.codenvy.ide.runtime.IStatus;
import com.codenvy.ide.runtime.Status;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.text.Region;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;


/**
 * Implementation of a Java completion proposal to be used for quick fix and quick assist
 * proposals that invoke a {@link Change}. The proposal offers a proposal information but no context
 * information.
 */
public class ChangeCorrectionProposal implements JavaCompletionProposal, ICommandAccess {

    private static final NullChange COMPUTING_CHANGE = new NullChange("ChangeCorrectionProposal computing..."); //$NON-NLS-1$

    private Change fChange;

    private String fName;

    private int fRelevance;

    private Images fImage;

    private String fCommandId;

    /**
     * Constructs a change correction proposal.
     *
     * @param name
     *         The name that is displayed in the proposal selection dialog.
     * @param change
     *         The change that is executed when the proposal is applied or <code>null</code>
     *         if the change will be created by implementors of {@link #createChange()}.
     * @param relevance
     *         The relevance of this proposal.
     * @param image
     *         The image that is displayed for this proposal or <code>null</code> if no
     *         image is desired.
     */
    public ChangeCorrectionProposal(String name, Change change, int relevance, Images image) {
        if (name == null) {
            throw new IllegalArgumentException("Name must not be null"); //$NON-NLS-1$
        }
        fName = name;
        fChange = change;
        fRelevance = relevance;
        fImage = image;
        fCommandId = null;
    }

    /*
     * @see ICompletionProposal#apply(IDocument)
     */
    public void apply(Document document) {
        try {
            performChange(document);
        } catch (CoreException e) {
            Log.error(getClass(), e);
        }
    }

    /**
     * Performs the change associated with this proposal.
     *
     * @param document
     *         The document of the editor currently active or <code>null</code> if
     *         no editor is visible.
     * @throws CoreException
     *         Thrown when the invocation of the change failed.
     */
    protected void performChange(Document document) throws CoreException {
        //		StyledText disabledStyledText= null;
        //		TraverseListener traverseBlocker= null;
        //
        Change change = null;
        //		IRewriteTarget rewriteTarget= null;
        try {
            change = getChange();
            if (change != null) {
                //				if (document != null) {
                //					LinkedModeModel.closeAllModels(document);
                //				}
                //				if (activeEditor != null) {
                //					rewriteTarget= (IRewriteTarget) activeEditor.getAdapter(IRewriteTarget.class);
                //					if (rewriteTarget != null) {
                //						rewriteTarget.beginCompoundChange();
                //					}
            /*
             * Workaround for https://bugs.eclipse.org/bugs/show_bug.cgi?id=195834#c7 :
             * During change execution, an EventLoopProgressMonitor can process the event queue while the text
             * widget has focus. When that happens and the user e.g. pressed a key, the event is prematurely
             * delivered to the text widget and screws up the document. Change execution fails or performs
             * wrong changes.
             * 
             * The fix is to temporarily disable the text widget.
             */
                //					Object control= activeEditor.getAdapter(Control.class);
                //					if (control instanceof StyledText) {
                //						disabledStyledText= (StyledText) control;
                //						if (disabledStyledText.getEditable()) {
                //							disabledStyledText.setEditable(false);
                //							traverseBlocker= new TraverseListener() {
                //								public void keyTraversed(TraverseEvent e) {
                //									e.doit= true;
                //									e.detail= SWT.TRAVERSE_NONE;
                //								}
                //							};
                //							disabledStyledText.addTraverseListener(traverseBlocker);
                //						} else {
                //							disabledStyledText= null;
                //						}
                //					}
            }

            change.initializeValidationData();
            RefactoringStatus valid = change.isValid();
            if (valid.hasFatalError()) {
                IStatus status =
                        new Status(IStatus.ERROR, JavaCore.PLUGIN_ID, IStatus.ERROR,
                                   valid.getMessageMatchingSeverity(RefactoringStatus.FATAL), null);
                throw new CoreException(status);
            } else {
                //					IUndoManager manager= RefactoringCore.getUndoManager();
                Change undoChange;
                boolean successful = false;
                try {
                    //						manager.aboutToPerformChange(change);
                    undoChange = change.perform();
                    successful = true;
                } finally {
                    //						manager.changePerformed(change, successful);
                }
                if (undoChange != null) {
                    undoChange.initializeValidationData();
                    //						manager.addUndo(getName(), undoChange);
                }
            }
            //			}
        } finally {
            //			if (disabledStyledText != null) {
            //				disabledStyledText.setEditable(true);
            //				disabledStyledText.removeTraverseListener(traverseBlocker);
            //			}
            //			if (rewriteTarget != null) {
            //				rewriteTarget.endCompoundChange();
            //			}

            if (change != null) {
                change.dispose();
            }
        }
    }

    /*
     * @see ICompletionProposal#getAdditionalProposalInfo()
     */
    public Widget getAdditionalProposalInfo() {
        Object info = getAdditionalInfo();
        return info == null ? null : new HTML(info.toString());
//      return null;
    }

    public Object getAdditionalInfo() {
        StringBuffer buf = new StringBuffer();
        buf.append("<p>"); //$NON-NLS-1$
        try {
            Change change = getChange();
            if (change != null) {
                String name = change.getName();
                if (name.length() == 0) {
                    return null;
                }
                buf.append(name);
            } else {
                return null;
            }
        } catch (CoreException e) {
            buf.append("Unexpected error when accessing this proposal:<p><pre>"); //$NON-NLS-1$
            buf.append(e.getLocalizedMessage());
            buf.append("</pre>"); //$NON-NLS-1$
        }
        buf.append("</p>"); //$NON-NLS-1$
        return buf.toString();
    }

//   /*
//    * @see ICompletionProposal#getContextInformation()
//    */
//   public ContextInformation getContextInformation()
//   {
//      return null;
//   }

    /*
     * @see ICompletionProposal#getDisplayString()
     */
    public String getDisplayString() {
        //TODO
        //		String shortCutString= CorrectionCommandHandler.getShortCutString(getCommandId());
        //		if (shortCutString != null) {
        //			return Messages.format(CorrectionMessages.ChangeCorrectionProposal_name_with_shortcut, new String[] { getName(),
        // shortCutString });
        //		}
        return getName();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension6#getStyledDisplayString()
     */
    public StyledString getStyledDisplayString() {
        StyledString str = new StyledString(getName());
        //TODO
        //		String shortCutString= CorrectionCommandHandler.getShortCutString(getCommandId());
        //		if (shortCutString != null) {
        //			String decorated= Messages.format(CorrectionMessages.ChangeCorrectionProposal_name_with_shortcut, new String[] { getName(),
        // shortCutString });
        //			return StyledCellLabelProvider.styleDecoratedString(decorated, StyledString.QUALIFIER_STYLER, str);
        //		}
        return str;
    }

    /**
     * Returns the name of the proposal.
     *
     * @return return the name of the proposal
     */
    public String getName() {
        return fName;
    }


    /** {@inheritDoc} */
    public Images getImage() {
        return fImage;
    }

    public Region getSelection(Document document) {
        return null;
    }

    /**
     * Sets the proposal's image or <code>null</code> if no image is desired.
     *
     * @param image
     *         the desired image.
     */
    public void setImage(Images image) {
        fImage = image;
    }

    /**
     * Returns the change that will be executed when the proposal is applied.
     *
     * @return returns the change for this proposal.
     * @throws CoreException
     *         thrown when the change could not be created
     */
    public final Change getChange() throws CoreException {
        //		if (Util.isGtk()) {
        //			// workaround for https://bugs.eclipse.org/bugs/show_bug.cgi?id=293995 :
        //			// [Widgets] Deadlock while UI thread displaying/computing a change proposal and non-UI thread creating image
        //
        //			// Solution is to create the change outside a 'synchronized' block.
        //			// Synchronization is achieved by polling fChange, using "fChange == COMPUTING_CHANGE" as barrier.
        //			// Timeout of 10s for safety reasons (should not be reached).
        //			long end= System.currentTimeMillis() + 10000;
        //			do {
        //				boolean computing;
        //				synchronized (this) {
        //					computing= fChange == COMPUTING_CHANGE;
        //				}
        //				if (computing) {
        //					try {
        //						Display display= Display.getCurrent();
        //						if (display != null) {
        //							while (! display.isDisposed() && display.readAndDispatch()) {
        //							}
        //							display.sleep();
        //						} else {
        //							Thread.sleep(100);
        //						}
        //					} catch (InterruptedException e) {
        //						//continue
        //					}
        //				} else {
        //					synchronized (this) {
        //						if (fChange == COMPUTING_CHANGE) {
        //							continue;
        //						} else if (fChange != null) {
        //							return fChange;
        //						} else {
        //							fChange= COMPUTING_CHANGE;
        //						}
        //					}
        //					Change change= createChange();
        //					synchronized (this) {
        //						fChange= change;
        //					}
        //					return change;
        //				}
        //			} while (System.currentTimeMillis() < end);
        //
        //			synchronized (this) {
        //				if (fChange == COMPUTING_CHANGE) {
        //					return null; //failed
        //				}
        //			}
        //
        //		} else {
        //			synchronized (this) {
        if (fChange == null) {
            fChange = createChange();
        }
        //			}
        //		}
        return fChange;
    }

    /**
     * Creates the text change for this proposal.
     * This method is only called once and only when no text change has been passed in
     * {@link #ChangeCorrectionProposal(String, Change, int, Image)}.
     *
     * @return returns the created change.
     * @throws CoreException
     *         thrown if the creation of the change failed.
     */
    protected Change createChange() throws CoreException {
        return new NullChange();
    }

    /**
     * Sets the display name.
     *
     * @param name
     *         the name to set
     */
    public void setDisplayName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name must not be null"); //$NON-NLS-1$
        }
        fName = name;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.ui.text.java.IJavaCompletionProposal#getRelevance()
     */
    @Override
    public int getRelevance() {
        return fRelevance;
    }

    /**
     * Sets the relevance.
     *
     * @param relevance
     *         the relevance to set
     */
    public void setRelevance(int relevance) {
        fRelevance = relevance;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.ui.text.correction.IShortcutProposal#getProposalId()
     */
    @Override
    public String getCommandId() {
        return fCommandId;
    }

    /**
     * Set the proposal id to allow assigning a shortcut to the correction proposal.
     *
     * @param commandId
     *         The proposal id for this proposal or <code>null</code> if no command
     *         should be assigned to this proposal.
     */
    public void setCommandId(String commandId) {
        fCommandId = commandId;
    }

    /** {@inheritDoc} */
    @Override
    public void apply(Document document, char trigger, int offset) {
    }

    /** {@inheritDoc} */
    @Override
    public boolean isValidFor(Document document, int offset) {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public char[] getTriggerCharacters() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isAutoInsertable() {
        return false;
    }

}
