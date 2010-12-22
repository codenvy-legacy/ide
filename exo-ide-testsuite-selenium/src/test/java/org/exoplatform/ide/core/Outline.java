/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.core;

import org.exoplatform.ide.TestConstants;

import com.thoughtworks.selenium.Selenium;

/**
 * Operations with or in code outline panel.
 * 
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: Dec 22, 2010 $
 *
 */
public class Outline
{
   private Selenium selenium;
   
   public Outline(Selenium selenium)
   {
      this.selenium = selenium;
   }
   
   /**
    * Get title of outline node.
    * 
    * @param row - row number (from 0)
    * @param col - column number (from 0)
    * @return title in (row, col) position in outline tree
    */
   public String getTitle(int row, int col)
   {
      return selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[" 
         + String.valueOf(row) +  "]/col[" + String.valueOf(col) + "]");
   }
   
   /**
    * Click on open icon of outline node.
    * 
    * Can open or close node.
    * 
    * @param row - row number (from 0)
    * @param col - column number (from 0)
    * @throws Exception
    */
   public void clickOpenImg(int row, int col) throws Exception
   {
      selenium.click("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[" 
         + String.valueOf(row) + "]/col[" + String.valueOf(col) + "]/open");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }
   
   /**
    * Select row in outline tree.
    * 
    * Click on 1-st column of row.
    * 
    * @param row - number of row (from 0).
    * @throws Exception
    */
   public void select(int row) throws Exception
   {
      selenium.click("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[" 
         + String.valueOf(row) + "]/col[1]");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

}
