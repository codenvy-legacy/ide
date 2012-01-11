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
package org.eclipse.jdt.client.core.util;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}:  Jan 10, 2012 9:45:02 AM evgen $
 *
 */
public class MathUtil
{
   static final double LN2 = Math.log(2);
   
   public static long floatToIntBits(float f)
   {
      // Check for +0 or -0
      if (f == 0.0f)
      {
         return 0;
      }
      else
      {
         return doubleToLongBitsImpl(f);
      }
   }

   public static float intBitsToFloat(int i)
   {
      int exponent = (i >>> 23) & 255;
    int significand = i & 0x007fffff;
    float result;
    if (exponent == 0) {
      result = (float) (Math.exp((-126 - 23) * LN2) * significand);
    } else if (exponent == 255) {
      result = significand == 0 ? Float.POSITIVE_INFINITY : Float.NaN;
    } else {
      result = (float) (Math.exp((exponent - 127 - 23) * LN2) * (0x00800000 | significand));
    }
    
    return (i & 0x80000000) == 0 ? result : -result;
   }

   public static long doubleToLongBits(double d)
   {
      // Check for +0 or -0
      if (d == 0.0)
      {
         return 0L;
      }
      else
      {
         return doubleToLongBitsImpl(d);
      }
   }

   /**
    * Implementation of doubleToLongBits as GWT does not provide that.
    * 
    * @param v
    * @return
    */
   private static long doubleToLongBitsImpl(double v)
   {
      if (Double.isNaN(v))
      {
         // IEEE754, NaN exponent bits all 1s, and mantissa is non-zero
         return 0x0FFFl << 51;
      }

      long sign = (v < 0 ? 0x1l << 63 : 0);
      long exponent = 0;

      double absV = Math.abs(v);
      if (Double.isInfinite(v))
      {
         exponent = 0x07FFl << 52;
      }
      else
      {
         if (absV == 0.0)
         {
            // IEEE754, exponent is 0, mantissa is zero
            // we don't handle negative zero at the moment, it is treated as
            // positive zero
            exponent = 0l;
         }
         else
         {
            // get an approximation to the exponent
            int guess = (int)Math.floor(Math.log(absV) / Math.log(2));
            // force it to -1023, 1023 interval (<= -1023 = denorm/zero)
            guess = Math.max(-1023, Math.min(guess, 1023));

            // divide away exponent guess
            double exp = Math.pow(2, guess);
            absV = absV / exp;

            // while the number is still bigger than a normalized number
            // increment exponent guess
            while (absV > 2.0)
            {
               guess++;
               absV /= 2.0;
            }
            // if the number is smaller than a normalized number
            // decrement exponent
            while (absV < 1 && guess > 1024)
            {
               guess--;
               absV *= 2;
            }
            exponent = (guess + 1023l) << 52;
         }
      }
      // if denorm
      if (exponent <= 0)
      {
         absV /= 2;
      }

      // the input value has now been stripped of its exponent
      // and is in the range [0,2), we strip off the leading decimal
      // and use the remainer as a percentage of the significand value (2^52)
      long mantissa = (long)((absV % 1) * Math.pow(2, 52));
      return sign | exponent | (mantissa & 0xfffffffffffffl);
   }

}
