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
package org.exoplatform.ide.extension.gadget.server.opensocial.model;

import java.util.Date;

/**
 * Describes a current or past organizational affiliation of this contact.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Nov 19, 2010 $
 */
public class Organization {
    /** The physical address of this organization. */
    private Address address;

    /** The department within this organization. */
    private String department;

    /** A textual description of the role this Person played in this organization. */
    private String description;

    /** The date this Person left this organization or the role specified by title within this organization. */
    private java.util.Date endDate;

    /** The field the Organization is in. */
    private String field;

    /** The physical location of this organization. */
    private String location;

    /** The name of the organization. */
    private String name;

    /** The salary the person receieves from the organization.s */
    private String salary;

    /** The date this Person joined this organization. */
    private Date startDate;

    /** The subfield the Organization is in. */
    private String subField;

    /** The job title or organizational role within this organization. */
    private String title;

    /** A webpage related to the organization. */
    private String webpage;

    /** The type of organization. */
    private String type;

    /** @return the address */
    public Address getAddress() {
        return address;
    }

    /**
     * @param address
     *         the address to set
     */
    public void setAddress(Address address) {
        this.address = address;
    }

    /** @return the department */
    public String getDepartment() {
        return department;
    }

    /**
     * @param department
     *         the department to set
     */
    public void setDepartment(String department) {
        this.department = department;
    }

    /** @return the description */
    public String getDescription() {
        return description;
    }

    /**
     * @param description
     *         the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /** @return the endDate */
    public java.util.Date getEndDate() {
        return endDate;
    }

    /**
     * @param endDate
     *         the endDate to set
     */
    public void setEndDate(java.util.Date endDate) {
        this.endDate = endDate;
    }

    /** @return the field */
    public String getField() {
        return field;
    }

    /**
     * @param field
     *         the field to set
     */
    public void setField(String field) {
        this.field = field;
    }

    /** @return the location */
    public String getLocation() {
        return location;
    }

    /**
     * @param location
     *         the location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /** @return the name */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *         the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /** @return the salary */
    public String getSalary() {
        return salary;
    }

    /**
     * @param salary
     *         the salary to set
     */
    public void setSalary(String salary) {
        this.salary = salary;
    }

    /** @return the startDate */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * @param startDate
     *         the startDate to set
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /** @return the subField */
    public String getSubField() {
        return subField;
    }

    /**
     * @param subField
     *         the subField to set
     */
    public void setSubField(String subField) {
        this.subField = subField;
    }

    /** @return the title */
    public String getTitle() {
        return title;
    }

    /**
     * @param title
     *         the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /** @return the webpage */
    public String getWebpage() {
        return webpage;
    }

    /**
     * @param webpage
     *         the webpage to set
     */
    public void setWebpage(String webpage) {
        this.webpage = webpage;
    }

    /** @return the type */
    public String getType() {
        return type;
    }

    /**
     * @param type
     *         the type to set
     */
    public void setType(String type) {
        this.type = type;
    }
}
