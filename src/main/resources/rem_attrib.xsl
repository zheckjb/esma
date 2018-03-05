<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0"
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  exclude-result-prefixes="xsi">
 <xsl:output method="xml" omit-xml-declaration="yes" encoding="UTF-8" indent="no" />


 <xsl:template match="*">
  <xsl:element name="{local-name()}">
   <xsl:apply-templates select="node()"/>
   
  </xsl:element>
 </xsl:template>

</xsl:stylesheet>