<?xml version="1.0" encoding="UTF-8"?>
<!-- XSLT file to add the security domains to the standalone.xml -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:kc="urn:jboss:domain:keycloak:1.0"
    exclude-result-prefixes="oc">

  <xsl:output xmlns:xalan="http://xml.apache.org/xalan" method="xml" encoding="UTF-8" indent="yes"
    xalan:indent-amount="2" />

  <xsl:template match="/*[name()='server' or name()='domain']//*[name()='profile']/kc:subsystem">
    <xsl:variable name="currentNS" select="namespace-uri(.)" />
    <xsl:element name="subsystem" namespace="{$currentNS}">
      <xsl:apply-templates select="./node()|./text()" />
      <!-- Realm Config -->
      <xsl:element name="realm" namespace="urn:jboss:domain:keycloak:1.0">
        <xsl:attribute name="name">governance</xsl:attribute>
        <xsl:element name="realm-public-key" namespace="urn:jboss:domain:keycloak:1.0">MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCrVrCuTtArbgaZzL1hvh0xtL5mc7o0NqPVnYXkLvgcwiC3BjLGw1tGEGoJaXDuSaRllobm53JBhjx33UNv+5z/UMG4kytBWxheNVKnL6GgqlNabMaFfPLPCF8kAgKnsi79NMo+n6KnSY8YeUmec/p2vjO2NjsSAVcWEQMVhJ31LwIDAQAB</xsl:element>
        <xsl:element name="auth-server-url" namespace="urn:jboss:domain:keycloak:1.0">/auth</xsl:element>
        <xsl:element name="ssl-required" namespace="urn:jboss:domain:keycloak:1.0">none</xsl:element>
      </xsl:element>
      <!-- Secure Deployment Config -->
      <xsl:element name="secure-deployment" namespace="urn:jboss:domain:keycloak:1.0">
        <xsl:attribute name="name">overlord-rtgov-ui.war</xsl:attribute>
        <xsl:element name="realm" namespace="urn:jboss:domain:keycloak:1.0">governance</xsl:element>
        <xsl:element name="resource" namespace="urn:jboss:domain:keycloak:1.0">rtgov-ui</xsl:element>
        <xsl:element name="credential" namespace="urn:jboss:domain:keycloak:1.0">
            <xsl:attribute name="name">secret</xsl:attribute>
            password
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

