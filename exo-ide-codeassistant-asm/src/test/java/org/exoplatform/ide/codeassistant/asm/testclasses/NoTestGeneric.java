package org.exoplatform.ide.codeassistant.asm.testclasses;

import java.util.Collection;
import java.util.HashMap;
import java.lang.String;
import java.lang.Number;

public class NoTestGeneric<T, V>
{

   public T value;

   public NoTestGeneric(T value)
   {
   }

   public Object noGeneric()
   {
      return null;
   }

   public T getGeneric()
   {
      return null;
   }

   public Collection<T> getGenerics(Collection<V> input)
   {
      return null;
   }

   public HashMap<T, V> getTwoGenerics()
   {
      return null;
   }

   public HashMap<String, Object> getHashMap()
   {
      return null;
   }

   public <U extends Number> U getNumber(T value)
   {
      return null;
   }

}
