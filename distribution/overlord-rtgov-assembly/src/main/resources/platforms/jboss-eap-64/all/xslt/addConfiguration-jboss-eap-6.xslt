<?xml version="1.0" encoding="UTF-8"?>
<!-- XSLT file to add the security domains to the standalone.xml -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:oc="urn:jboss:domain:overlord-configuration:1.0"
    exclude-result-prefixes="oc">

  <xsl:output xmlns:xalan="http://xml.apache.org/xalan" method="xml" encoding="UTF-8" indent="yes"
    xalan:indent-amount="2" />


  <xsl:template match="/*[name()='server' or name()='domain']//*[name()='profile']/oc:subsystem/oc:configurations/oc:configuration[@name = 'overlord']/oc:properties">
    <xsl:variable name="currentNS" select="namespace-uri(.)" />
    <xsl:element name="properties" namespace="{$currentNS}">
      <xsl:apply-templates select="./node()|./text()" />
      <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
        <xsl:attribute name="name">overlord.headerui.apps.rtgov-ui.href</xsl:attribute>
        <xsl:attribute name="value">/rtgov-ui/</xsl:attribute>
      </xsl:element>
      <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
        <xsl:attribute name="name">overlord.headerui.apps.rtgov-ui.label</xsl:attribute>
        <xsl:attribute name="value">Runtime Governance</xsl:attribute>
      </xsl:element>
      <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
        <xsl:attribute name="name">overlord.headerui.apps.rtgov-ui.primary-brand</xsl:attribute>
        <xsl:attribute name="value">JBoss Overlord</xsl:attribute>
      </xsl:element>
      <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
        <xsl:attribute name="name">overlord.headerui.apps.rtgov-ui.secondary-brand</xsl:attribute>
        <xsl:attribute name="value">Runtime Governance</xsl:attribute>
      </xsl:element>
    </xsl:element>
  </xsl:template>


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
            <xsl:attribute name="name">BatchedActivityUnitLogger.maxUnitCount</xsl:attribute>
            <xsl:attribute name="value">1000</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">BatchedActivityUnitLogger.maxTimeInterval</xsl:attribute>
            <xsl:attribute name="value">500</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">ActiveCollectionManager.houseKeepingInterval</xsl:attribute>
            <xsl:attribute name="value">10000</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">ActivityStore.class</xsl:attribute>
            <xsl:attribute name="value">org.overlord.rtgov.activity.store.elasticsearch.ElasticsearchActivityStore</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">SituationStore.class</xsl:attribute>
            <xsl:attribute name="value">org.overlord.rtgov.analytics.situation.store.elasticsearch.ElasticsearchSituationStore</xsl:attribute>
          </xsl:element>
        </xsl:element>
      </xsl:element>
      <!-- Elasticsearch Config -->
      <xsl:element name="configuration" namespace="urn:jboss:domain:overlord-configuration:1.0">
        <xsl:attribute name="name">overlord-rtgov-elasticsearch</xsl:attribute>
        <xsl:element name="properties" namespace="urn:jboss:domain:overlord-configuration:1.0">
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">path.data</xsl:attribute>
            <xsl:attribute name="value">${jboss.server.data.dir}/elasticsearch</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">node.data</xsl:attribute>
            <xsl:attribute name="value">true</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">node.client</xsl:attribute>
            <xsl:attribute name="value">false</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">node.local</xsl:attribute>
            <xsl:attribute name="value">false</xsl:attribute>
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
