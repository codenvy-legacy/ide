/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package org.exoplatform.ide.git.server.nativegit.commands;

import org.exoplatform.ide.git.server.GitException;

import java.io.File;

/**
 * Checkout branch
 *
 * @author <a href="mailto:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class BranchCheckoutCommand extends GitCommand<Void> {

    private boolean createNew;
    private boolean isRemote;
    private String  branchName;
    private String  startPoint;

    public BranchCheckoutCommand(File place) {
        super(place);
    }

    /** @see org.exoplatform.ide.git.server.nativegit.commands.GitCommand#execute() */
    @Override
    public Void execute() throws GitException {
        if (branchName == null) {
            throw new GitException("");
        }
        clear();
        commandLine.add("checkout");
        if (createNew) {
            commandLine.add("-b");
        }
        if (isRemote) {
            commandLine.add("-t");
        }
        commandLine.add(branchName);
        if (startPoint != null) {
            commandLine.add(startPoint);
        }
        start();
        return null;
    }

    /**
     * @param createNew
     *         if <code>true</code> new branch will be created
     * @return BranchCheckoutCommand with established create new branch parameter
     */
    public BranchCheckoutCommand setCreateNew(boolean createNew) {
        this.createNew = createNew;
        return this;
    }

    /**
     * @param branchName
     *         branch to checkout
     * @return BranchCheckoutCommand with established branch to checkout
     */
    public BranchCheckoutCommand setBranchName(String branchName) {
        this.branchName = branchName;
        return this;
    }

    /**
     * @param remote
     *         if <code>true</code> branch will be tracked to remote
     * @return BranchCheckoutCommand with established remote parameter
     */
    public BranchCheckoutCommand setRemote(boolean remote) {
        isRemote = remote;
        return this;
    }

    /**
     * @param startPoint
     *         checkout start point
     * @return BranchCheckoutCommand with start point
     */
    public BranchCheckoutCommand setStartPoint(String startPoint) {
        this.startPoint = startPoint;
        return this;
    }
}
