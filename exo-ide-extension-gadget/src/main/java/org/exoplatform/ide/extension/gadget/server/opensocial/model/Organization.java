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
package org.exoplatform.ide.extension.gadget.server.opensocial.model;

import java.util.Date;

/**
 * Describes a current or past organizational affiliation of this contact.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Nov 19, 2010 $
 * 
 */
public class Organization
{
   /**
    * The physical address of this organization.
    */
   private Address address;

   /**
    * The department within this organization.
    */
   private String department;

   /**
    * A textual description of the role this Person played in this organization.
    */
   private String description;

   /**
    * The date this Person left this organization or the role specified by title within this organization.
    */
   private java.util.Date endDate;

   /**
    * The field the Organization is in.
    */
   private String field;

   /**
    * The physical location of this organization.
    */
   private String location;

   /**
    * The name of the organization.
    */
   private String name;

   /**
    * The salary the person receieves from the organization.s
    */
   private String salary;

   /**
    * The date this Person joined this organization.
    */
   private Date startDate;

   /**
    * The subfield the Organization is in.
    */
   private String subField;

   /**
    * The job title or organizational role within this organization.
    */
   private String title;

   /**
    * A webpage related to the organization.
    */
   private String webpage;

   /**
    * The type of organization.
    */
   private String type;

   /**
    * @return the address
    */
   public Address getAddress()
   {
      return address;
   }

   /**
    * @param address the address to set
    */
   public void setAddress(Address address)
   {
      this.address = address;
   }

   /**
    * @return the department
    */
   public String getDepartment()
   {
      return department;
   }

   /**
    * @param department the department to set
    */
   public void setDepartment(String department)
   {
      this.department = department;
   }

   /**
    * @return the description
    */
   public String getDescription()
   {
      return description;
   }

   /**
    * @param description the description to set
    */
   public void setDescription(String description)
   {
      this.description = description;
   }

   /**
    * @return the endDate
    */
   public java.util.Date getEndDate()
   {
      return endDate;
   }

   /**
    * @param endDate the endDate to set
    */
   public void setEndDate(java.util.Date endDate)
   {
      this.endDate = endDate;
   }

   /**
    * @return the field
    */
   public String getField()
   {
      return field;
   }

   /**
    * @param field the field to set
    */
   public void setField(String field)
   {
      this.field = field;
   }

   /**
    * @return the location
    */
   public String getLocation()
   {
      return location;
   }

   /**
    * @param location the location to set
    */
   public void setLocation(String location)
   {
      this.location = location;
   }

   /**
    * @return the name
    */
   public String getName()
   {
      return name;
   }

   /**
    * @param name the name to set
    */
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * @return the salary
    */
   public String getSalary()
   {
      return salary;
   }

   /**
    * @param salary the salary to set
    */
   public void setSalary(String salary)
   {
      this.salary = salary;
   }

   /**
    * @return the startDate
    */
   public Date getStartDate()
   {
      return startDate;
   }

   /**
    * @param startDate the startDate to set
    */
   public void setStartDate(Date startDate)
   {
      this.startDate = startDate;
   }

   /**
    * @return the subField
    */
   public String getSubField()
   {
      return subField;
   }

   /**
    * @param subField the subField to set
    */
   public void setSubField(String subField)
   {
      this.subField = subField;
   }

   /**
    * @return the title
    */
   public String getTitle()
   {
      return title;
   }

   /**
    * @param title the title to set
    */
   public void setTitle(String title)
   {
      this.title = title;
   }

   /**
    * @return the webpage
    */
   public String getWebpage()
   {
      return webpage;
   }

   /**
    * @param webpage the webpage to set
    */
   public void setWebpage(String webpage)
   {
      this.webpage = webpage;
   }

   /**
    * @return the type
    */
   public String getType()
   {
      return type;
   }

   /**
    * @param type the type to set
    */
   public void setType(String type)
   {
      this.type = type;
   }
}
