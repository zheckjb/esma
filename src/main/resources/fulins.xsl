<?xml version="1.0" encoding="UTF-8" ?>
<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="text" omit-xml-declaration="yes" encoding="UTF-8" indent="no" />
    <xsl:template match="BizData|@*">
		<xsl:variable name="all" select="count(Pyld/Document|@*/FinInstrmRptgRefDataRpt/RefData)" />
		<xsl:for-each select="Pyld/Document/FinInstrmRptgRefDataRpt/RefData">
			<xsl:variable name="i" select="position()" />
			<xsl:element name="card">
    			<xsl:value-of select="FinInstrmGnlAttrbts/Id"/>
			</xsl:element>
            <xsl:text>&#xa;</xsl:text>
			<xsl:if test="$i &lt; $all">
			</xsl:if>
		</xsl:for-each>
	</xsl:template>
</xsl:transform>