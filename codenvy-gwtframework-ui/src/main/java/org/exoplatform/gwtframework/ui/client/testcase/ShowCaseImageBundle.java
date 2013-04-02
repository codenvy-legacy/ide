/**
 * Copyright (C) 2009 eXo Platform SAS.
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
 *
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
