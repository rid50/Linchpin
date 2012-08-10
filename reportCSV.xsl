<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="text"/>
    <xsl:strip-space elements="*"/>
	
    <xsl:template match="report">
		<xsl:apply-templates select="header"/>
		<xsl:apply-templates select="row"/>
    </xsl:template>

    <xsl:template match="header">
   		<xsl:for-each select="column/name">
       		<xsl:value-of select="." />
       		<xsl:if test="position() != last()">,</xsl:if>
   		</xsl:for-each>
		<xsl:text>&#10;</xsl:text>   		
    </xsl:template>
	
    <xsl:template match="row">
		<xsl:for-each select="value">
			<xsl:variable name="pos"><xsl:value-of select="position()" /></xsl:variable>
			<xsl:variable name="val"><xsl:value-of select="/report/header/column[position()=$pos]/type" /></xsl:variable>
			<xsl:choose>
				<xsl:when test="contains($val, 'String')">
					<xsl:text>&quot;</xsl:text>   		
       				<xsl:value-of select="."/>
					<xsl:text>&quot;</xsl:text>   		
				</xsl:when>
				<xsl:otherwise>
       				<xsl:value-of select="."/>
				</xsl:otherwise>
			</xsl:choose>
       		<xsl:if test="position() != last()">,</xsl:if>
       		<xsl:if test="position() = last()">
				<xsl:text>&#10;</xsl:text>   		
       		</xsl:if>
   		</xsl:for-each>
    </xsl:template>

</xsl:stylesheet>

