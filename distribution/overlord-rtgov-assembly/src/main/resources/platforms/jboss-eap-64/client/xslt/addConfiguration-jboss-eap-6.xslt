<?xml version="1.0" encoding="UTF-8"?>
<!-- XSLT file to add the security domains to the standalone.xml -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:oc="urn:jboss:domain:overlord-configuration:1.0"
    exclude-result-prefixes="oc">

  <xsl:param name="serverUsername" />
  <xsl:param name="serverPassword" />
  
  <xsl:output xmlns:xalan="http://xml.apache.org/xalan" method="xml" encoding="UTF-8" indent="yes"
    xalan:indent-amount="2" />

  <xsl:template match="/*[name()='server' or name()='domain']//*[name()='profile']/oc:subsystem/oc:configurations">
    <xsl:variable name="currentNS" select="namespace-uri(.)" />
    <xsl:element name="configurations" namespace="{$currentNS}">
      <xsl:apply-templates select="./node()|./text()" />
      <!-- RTGov Config -->
      <xsl:element name="configuration" namespace="urn:jboss:domain:overlord-configuration:1.0">
        <xsl:attribute name="name">overlord-rtgov</xsl:attribute>
        <xsl:element name="properties" namespace="urn:jboss:domain:overlord-configuration:1.0">
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">ActivityCollector.enabled</xsl:attribute>
            <xsl:attribute name="value">false</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">ActivityServerLogger.maxThreads</xsl:attribute>
            <xsl:attribute name="value">10</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">ActivityServerLogger.durationBetweenFailureReports</xsl:attribute>
            <xsl:attribute name="value">300000</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">ActivityServerLogger.activityListQueueSize</xsl:attribute>
            <xsl:attribute name="value">10000</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">ActivityServerLogger.freeActivityListQueueSize</xsl:attribute>
            <xsl:attribute name="value">100</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">BatchedActivityUnitLogger.maxUnitCount</xsl:attribute>
            <xsl:attribute name="value">1000</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">BatchedActivityUnitLogger.maxTimeInterval</xsl:attribute>
            <xsl:attribute name="value">500</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">RESTActivityServer.serverURL</xsl:attribute>
            <xsl:attribute name="value">https://rtgovserver.com:8443</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">RESTActivityServer.serverUsername</xsl:attribute>
            <xsl:attribute name="value">
              <xsl:value-of select="$serverUsername"></xsl:value-of>
            </xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">RESTActivityServer.serverPassword</xsl:attribute>
            <xsl:attribute name="value">
              <xsl:value-of select="$serverPassword"></xsl:value-of>
            </xsl:attribute>
          </xsl:element>
        </xsl:element>
      </xsl:element>
    </xsl:element>
  </xsl:template>

  <!-- Copy everything else. -->
  <xsl:template match="@*|node()|text()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()|text()" />
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
