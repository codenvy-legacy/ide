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
package org.exoplatform.gwtframework.ui.client.testcase;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public interface ShowCaseImageBundle extends ClientBundle {

    public static final ShowCaseImageBundle INSTANCE = GWT.create(ShowCaseImageBundle.class);

    @Source("../../public/showcase/images/bundled/remove.png")
    ImageResource remove();

    @Source("../../public/showcase/images/bundled/remove_Disabled.png")
    ImageResource removeDisabled();

    @Source("../../public/showcase/images/bundled/add.png")
    ImageResource add();

    @Source("../../public/showcase/images/bundled/add_Disabled.png")
    ImageResource addDisabled();

    @Source("../../public/showcase/images/bundled/cancel.png")
    ImageResource cancel();

    @Source("../../public/showcase/images/bundled/cancel_Disabled.png")
    ImageResource cancelDisabled();

    @Source("../../public/showcase/images/bundled/ok.png")
    ImageResource ok();

    @Source("../../public/showcase/images/bundled/ok_Disabled.png")
    ImageResource okDisabled();

    @Source("../../public/showcase/images/bundled/xml.png")
    ImageResource xml();

    @Source("../../public/showcase/images/bundled/xml_Disabled.png")
    ImageResource xmlDisabled();

    @Source("../../public/showcase/images/bundled/html.png")
    ImageResource html();

    @Source("../../public/showcase/images/bundled/html_Disabled.png")
    ImageResource htmlDisabled();

    @Source("../../public/showcase/images/bundled/groovy.png")
    ImageResource groovy();

    @Source("../../public/showcase/images/bundled/groovy_Disabled.png")
    ImageResource groovyDisabled();

    @Source("../../public/showcase/images/bundled/search.png")
    ImageResource search();

    @Source("../../public/showcase/images/bundled/search_Disabled.png")
    ImageResource searchDisabled();

    @Source("../../public/showcase/images/bundled/function-item.png")
    ImageResource functionItem();

    @Source("../../public/showcase/images/bundled/method-item.png")
    ImageResource methodItem();

    @Source("../../public/showcase/images/bundled/var-item.png")
    ImageResource varItem();

    @Source("../../public/showcase/images/bundled/property-item.png")
    ImageResource propertyItem();

    @Source("../../public/showcase/images/bundled/template.png")
    ImageResource templateItem();

    @Source("../../public/showcase/images/bundled/keyword.png")
    ImageResource keywordItem();

    @Source("../../public/showcase/images/bundled/item_in_repository.png")
    ImageResource itemInRepository();

    @Source("../../public/showcase/images/bundled/item_in_repository_star.png")
    ImageResource itemChangeRepository();

    @Source("../../public/showcase/images/bundled/item_in_repository_question_blue.png")
    ImageResource itemAddRepository();

}
