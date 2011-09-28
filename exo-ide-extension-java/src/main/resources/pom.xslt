<?xml version="1.0" encoding="UTF-8"?>
<xs:stylesheet version="2.0" xmlns:xs="http://www.w3.org/1999/XSL/Transform"
                             xmlns:pom="http://maven.apache.org/POM/4.0.0">

   
   <xs:output method="xml" encoding="UTF-8" />
   <xs:param name="groupId" />
   <xs:param name="artifactId" />
   <xs:param name="version" />
   <xs:param name="name" />

   <xs:template match="@*|node()">
      <xs:copy>
         <xs:apply-templates select="@*|node()" />
      </xs:copy>
   </xs:template>

   <xs:template match="/pom:project/pom:groupId">
      <xs:choose>
         <xs:when test=".='${groupId}'">
            <xs:copy>
               <xs:value-of select="$groupId" />
            </xs:copy>
         </xs:when>
         <xs:otherwise>
            <xs:copy>
               <xs:apply-templates />
            </xs:copy>
         </xs:otherwise>
      </xs:choose>
   </xs:template>

   <xs:template match="/pom:project/pom:artifactId">
      <xs:choose>
         <xs:when test=".='${artifactId}'">
            <xs:copy>
               <xs:value-of select="$artifactId" />
            </xs:copy>
         </xs:when>
         <xs:otherwise>
            <xs:copy>
               <xs:apply-templates />
            </xs:copy>
         </xs:otherwise>
      </xs:choose>
   </xs:template>

   <xs:template match="/pom:project/pom:version">
      <xs:choose>
         <xs:when test=".='${version}'">
            <xs:copy>
               <xs:value-of select="$version" />
            </xs:copy>
         </xs:when>
         <xs:otherwise>
            <xs:copy>
               <xs:apply-templates />
            </xs:copy>
         </xs:otherwise>
      </xs:choose>
   </xs:template>

   <xs:template match="/pom:project/pom:name">
      <xs:choose>
         <xs:when test=".='${name}'">
            <xs:copy>
               <xs:value-of select="$name" />
            </xs:copy>
         </xs:when>
         <xs:otherwise>
            <xs:copy>
               <xs:apply-templates />
            </xs:copy>
         </xs:otherwise>
      </xs:choose>
   </xs:template>

</xs:stylesheet>