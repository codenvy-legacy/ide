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
package org.exoplatform.ide.extension.samples.client.github.deploy;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: ImportSampleStep.java Nov 22, 2011 11:53:47 AM vereshchaka $
 */
public interface ImportSampleStep<T> {

    /**
     * What to do, when open this screen.
     *
     * @param value
     *         context data of wizard
     */
    void onOpen(T value);

    /** What to do, when you return to this screen from next. */
    void onReturn();

    void setNextStep(ImportSampleStep<T> step);

    void setPreviousStep(ImportSampleStep<T> step);

}
