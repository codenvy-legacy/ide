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
package com.codenvy.ide.ext.java.client.core.search;

import com.codenvy.ide.ext.java.shared.ShortTypeInfo;
import com.codenvy.ide.ext.java.shared.TypesList;

import com.codenvy.ide.ext.java.client.core.IPackageFragment;
import com.codenvy.ide.ext.java.client.core.dom.Modifier;


import java.util.ArrayList;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class SearchEngine {

    public interface SearchCallback {
        void searchFinished(ArrayList<TypeNameMatch> typesFound);
    }

    private final String projectId;

    private SearchCallback callback;

    private ArrayList<TypeNameMatch> typesFound;

    private int index = 0;

    private char[][] allTypes;

    private final IPackageFragment currentPackage;

    /** @param projectId */
    public SearchEngine(String projectId, IPackageFragment currentPackage) {
        this.projectId = projectId;
        this.currentPackage = currentPackage;
    }

    /**
     * @param allTypes
     * @param typesFound
     */
    public void searchAllTypeNames(char[][] allTypes, ArrayList<TypeNameMatch> typesFound, SearchCallback callback) {
        this.allTypes = allTypes;
        this.typesFound = typesFound;
        this.callback = callback;
        if (allTypes.length == 0) {
            callback.searchFinished(typesFound);
            return;
        }
        getTypes(allTypes[index]);
    }

    /** @param fqn */
    private void getTypes(char[] fqn) {
        //TODO
//      JavaCodeAssistantService.get().findClassesByPrefix(
//         String.valueOf(fqn),
//         projectId,
//         new AsyncRequestCallback<TypesList>(new AutoBeanUnmarshaller<TypesList>(JavaEditorExtension.AUTO_BEAN_FACTORY
//            .types()))
//         {
//
//            @Override
//            protected void onSuccess(TypesList result)
//            {
//               typeListReceived(result);
//            }
//
//            @Override
//            protected void onFailure(Throwable exception)
//            {
//               exception.printStackTrace();
//            }
//         });
    }

    /** @param result */
    private void typeListReceived(TypesList result) {
        index++;
        for (ShortTypeInfo typeInfo : result.getTypes()) {
            Type type = new Type(typeInfo);
            if (!Modifier.isPublic(typeInfo.getModifiers())) {
                if (!currentPackage.getElementName().equals(type.getPackageFragment().getElementName()))
                    continue;
            }
            typesFound.add(new JavaSearchTypeNameMatch(type, typeInfo.getModifiers()));
        }
        if (index < allTypes.length)
            getTypes(allTypes[index]);
        else
            callback.searchFinished(typesFound);
    }

}
