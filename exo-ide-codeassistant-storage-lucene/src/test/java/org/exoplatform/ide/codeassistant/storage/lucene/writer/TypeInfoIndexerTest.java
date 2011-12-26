/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.codeassistant.storage.lucene.writer;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.exoplatform.ide.codeassistant.jvm.TypeInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.ObjectOutput;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class TypeInfoIndexerTest
{
   private final TypeInfoIndexer indexer = new TypeInfoIndexer();

   @Mock(answer = Answers.RETURNS_SMART_NULLS)
   private TypeInfo typeInfo;

   @Test
   public void shouldCallPredefinedSetOfFields() throws Exception
   {
      indexer.createDocument(typeInfo);
      verify(typeInfo).getName();
      verify(typeInfo).getModifiers();
      verify(typeInfo).getQualifiedName();
      verify(typeInfo).getType();
      verify(typeInfo).getInterfaces();
      verify(typeInfo).getSuperClass();
      verify(typeInfo).writeExternal(any(ObjectOutput.class));
      verifyNoMoreInteractions(typeInfo);

   }
}
