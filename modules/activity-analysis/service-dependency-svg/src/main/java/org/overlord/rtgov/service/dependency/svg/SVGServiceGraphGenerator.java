/*
 * 2012-3 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.overlord.rtgov.service.dependency.svg;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.overlord.rtgov.analytics.service.InvocationDefinition;
import org.overlord.rtgov.analytics.service.InvocationMetric;
import org.overlord.rtgov.analytics.situation.Situation;
import org.overlord.rtgov.service.dependency.InvocationLink;
import org.overlord.rtgov.service.dependency.OperationNode;
import org.overlord.rtgov.service.dependency.ServiceDependencyBuilder;
import org.overlord.rtgov.service.dependency.ServiceGraph;
import org.overlord.rtgov.service.dependency.ServiceNode;
import org.overlord.rtgov.service.dependency.UsageLink;
import org.overlord.rtgov.service.dependency.layout.ServiceGraphLayout;
import org.overlord.rtgov.service.dependency.presentation.Severity;
import org.overlord.rtgov.service.dependency.presentation.SeverityAnalyzer;

/**
 * This class generates a SVG representation of a
 * service graph.
 *
 */
public class SVGServiceGraphGenerator {
    
    private static final Logger LOG=Logger.getLogger(SVGServiceGraphGenerator.class.getName());

    private SeverityAnalyzer _severityAnalyzer=null;
    
    private static String[] _colorCodes={
        "#00FF00",      // Normal
        "#FF9900",      // Minor Warning
        "#FF6A45",      // Requires Investigation
        "#FF5930",      // Error
        "#FF3300",      // Serious
        "#FF0000"       // Critical
    };
    
    /**
     * The default constructor.
     */
    public SVGServiceGraphGenerator() {
    }
    
    /**
     * This method returns the color selector.
     * 
     * @return The color selector
     */
    public SeverityAnalyzer getSeverityAnalyzer() {
        return (_severityAnalyzer);
    }
    
    /**
     * This method sets the severity analyzer.
     * 
     * @param analyzer The severity analyzer
     */
    public void setSeverityAnalyzer(SeverityAnalyzer analyzer) {
        _severityAnalyzer = analyzer;
    }
    
    /**
     * This method generates the SVG representation of the supplied
     * service graph to the output stream.
     * 
     * @param sg The service graph
     * @param maxWidth The maximum width, or 0 if not relevant
     * @param os The output stream
     * @throws Exception Failed to generate SVG
     */
    public void generate(ServiceGraph sg, int maxWidth, java.io.OutputStream os) 
                        throws Exception {
        double ratio=1.0;
        
        if (maxWidth > 0) {
            int width=(Integer)sg.getProperties().get(ServiceGraphLayout.WIDTH);
            
            ratio = (double)maxWidth / (double)width;
        }
        
        org.w3c.dom.Document doc=loadTemplate(ratio < 0.8 ? "summary" : "main");
        
        if (doc != null) {
            org.w3c.dom.Element container=doc.getDocumentElement();
            
            org.w3c.dom.Node insertPoint=null;
            
            org.w3c.dom.NodeList nl=container.getElementsByTagName("insert");
            if (nl.getLength() == 1) {
                insertPoint = nl.item(0);
            }
            
            // Add description
            if (sg.getDescription() != null && ratio >= 1.0) {
                org.w3c.dom.Element text=
                        container.getOwnerDocument().createElement("text");
                text.setAttribute("x", "10");
                text.setAttribute("y", "20");
                text.setAttribute("font-size", "14");
                text.setAttribute("font-family", "Verdana");
                text.setAttribute("fill", "black");

                container.insertBefore(text, insertPoint);
            
                org.w3c.dom.Text textValue=
                        container.getOwnerDocument().createTextNode(sg.getDescription());
                text.appendChild(textValue);
            }
            
            if (sg.getServiceNodes().size() > 0) {
                // Generate nodes and links
                for (ServiceNode sn : sg.getServiceNodes()) {
                    generateService(sn, ratio, container, insertPoint);
                }
                
                for (UsageLink ul : sg.getUsageLinks()) {
                    generateUsageLink(ul, ratio, container, insertPoint);
                }
    
                for (InvocationLink il : sg.getInvocationLinks()) {
                    generateInvocationLink(il, ratio, container, insertPoint);
                }
            } else {
                org.w3c.dom.Element text=
                        container.getOwnerDocument().createElement("text");
                text.setAttribute("x", "50");
                text.setAttribute("y", "100");
                text.setAttribute("font-size", "30");
                text.setAttribute("font-family", "Verdana");
                text.setAttribute("fill", "#D9D9C2");

                container.insertBefore(text, insertPoint);
            
                org.w3c.dom.Text textValue=
                        container.getOwnerDocument().createTextNode("No Service Activity");
                text.appendChild(textValue);
            }

            // Remove insertion point
            if (insertPoint != null) {
                container.removeChild(insertPoint);
            }
            
            saveDocument(doc, os);
        }
    }
    
    /**
     * This method generates the usage link.
     * 
     * @param ul The usage link
     * @param ratio The ratio
     * @param container The container
     * @param insertPoint The insertion point
     */
    protected void generateUsageLink(UsageLink ul, double ratio,
                    org.w3c.dom.Element container, org.w3c.dom.Node insertPoint) {
        
        // Check that layout information is available
        if (!ul.getSource().getProperties().containsKey(ServiceGraphLayout.WIDTH)) {
            if (LOG.isLoggable(Level.FINER)) {
                LOG.fine("Source node on usage link with definition '"+ul.getSource().getService()+"' does not have layout information");
            }
            return;
        }        
        
        if (!ul.getTarget().getProperties().containsKey(ServiceGraphLayout.WIDTH)) {
            if (LOG.isLoggable(Level.FINER)) {
                LOG.fine("Target node on usage link with definition '"+ul.getTarget().getService()+"' does not have layout information");
            }
            return;
        }        
        
        int x1=(int)(((Integer)ul.getSource().getProperties().get(ServiceGraphLayout.X_POSITION)
                +(Integer)ul.getSource().getProperties().get(ServiceGraphLayout.WIDTH)) * ratio);
        
        int y1=(int)(((Integer)ul.getSource().getProperties().get(ServiceGraphLayout.Y_POSITION)) * ratio);
        
        int x2=(int)(((Integer)ul.getTarget().getProperties().get(ServiceGraphLayout.X_POSITION)) * ratio);
        
        int y2=(int)(((Integer)ul.getTarget().getProperties().get(ServiceGraphLayout.Y_POSITION)) * ratio);
        
        int x3=(int)(((Integer)ul.getTarget().getProperties().get(ServiceGraphLayout.X_POSITION)) * ratio);
        
        int y3=(int)(((Integer)ul.getTarget().getProperties().get(ServiceGraphLayout.Y_POSITION)
                +(Integer)ul.getTarget().getProperties().get(ServiceGraphLayout.HEIGHT)) * ratio);
        
        int x4=(int)(((Integer)ul.getSource().getProperties().get(ServiceGraphLayout.X_POSITION)
                +(Integer)ul.getSource().getProperties().get(ServiceGraphLayout.WIDTH)) * ratio);
        
        int y4=(int)(((Integer)ul.getSource().getProperties().get(ServiceGraphLayout.Y_POSITION)
                +(Integer)ul.getSource().getProperties().get(ServiceGraphLayout.HEIGHT)) * ratio);
        
        org.w3c.dom.Element polygon=
                container.getOwnerDocument().createElement("polygon");
        polygon.setAttribute("points", x1+","+y1+" "+x2+","+y2+" "
                +x3+","+y3+" "+x4+","+y4);

        Severity severity=getInvocationSeverity(ul.getInvocations());
        String color=getColor(severity);
        
        polygon.setAttribute("style", "fill:"+color+";fill-opacity:0.2");
        
        container.insertBefore(polygon, insertPoint);
        
        if (ratio >= 1.0) {
            // Generate tooltip
            generateMetrics(polygon, getDescription(ul),
                    ServiceDependencyBuilder.getMergedMetrics(ul.getInvocations()));
        }
    }
    
    /**
     * This method returns the description to be used for the
     * invocation link.
     * 
     * @param il The invocation link
     * @return The description
     */
    protected String getDescription(UsageLink ul) {
        return (ul.getSource().getService().getServiceType()
                +" -> "+ul.getTarget().getService().getServiceType());
    }
    
    /**
     * This method returns the colour code associated with the severity.
     * 
     * @param severity The severity
     * @return The colour code
     */
    protected String getColor(Severity severity) {
        if (severity == null) {
            return (_colorCodes[0]);
        }
        return (_colorCodes[severity.ordinal()]);
    }
    
    /**
     * This method generates the invocation link.
     * 
     * @param il The invocation link
     * @param ratio The ratio
     * @param container The container
     * @param insertPoint The insertion point
     */
    protected void generateInvocationLink(InvocationLink il, double ratio,
                    org.w3c.dom.Element container, org.w3c.dom.Node insertPoint) {
        
        // Check that layout information is available
        if (!il.getSource().getProperties().containsKey(ServiceGraphLayout.WIDTH)) {
            if (LOG.isLoggable(Level.FINER)) {
                LOG.fine("Source node on invocation link with definition '"+il.getSource().getService()+"' does not have layout information");
            }
            return;
        }        
        
        if (!il.getTarget().getProperties().containsKey(ServiceGraphLayout.WIDTH)) {
            if (LOG.isLoggable(Level.FINER)) {
                LOG.fine("Target node on invocation link with definition '"+il.getTarget().getService()+"' does not have layout information");
            }
            return;
        }        
        
        int x1=(int)(((Integer)il.getSource().getProperties().get(ServiceGraphLayout.X_POSITION)
                +(Integer)il.getSource().getProperties().get(ServiceGraphLayout.WIDTH)) * ratio);
        
        int y1=(int)(((Integer)il.getSource().getProperties().get(ServiceGraphLayout.Y_POSITION)
                +(Integer)il.getSource().getProperties().get(ServiceGraphLayout.HEIGHT)/2) * ratio);
        
        int x2=(int)(((Integer)il.getTarget().getProperties().get(ServiceGraphLayout.X_POSITION)) * ratio);
        
        int y2=(int)(((Integer)il.getTarget().getProperties().get(ServiceGraphLayout.Y_POSITION)
                +(Integer)il.getTarget().getProperties().get(ServiceGraphLayout.HEIGHT)/2) * ratio);
        
        org.w3c.dom.Element line=
                container.getOwnerDocument().createElement("line");
        line.setAttribute("x1", ""+x1);
        line.setAttribute("y1", ""+y1);
        line.setAttribute("x2", ""+x2);
        line.setAttribute("y2", ""+y2);
        
        Severity severity=getInvocationSeverity(il.getInvocations());
        String color=getColor(severity);
        
        line.setAttribute("style", "stroke:"+color+";stroke-width:3");
        
        container.insertBefore(line, insertPoint);
        
        if (ratio >= 1.0) {
            // Generate tooltip
            generateMetrics(line, getDescription(il), 
                    ServiceDependencyBuilder.getMergedMetrics(il.getInvocations()));
        }
    }
    
    /**
     * This method returns the description to be used for the
     * invocation link.
     * 
     * @param il The invocation link
     * @return The description
     */
    protected String getDescription(InvocationLink il) {
        return (il.getTarget().getService().getServiceType()
                +" -> "+il.getTarget().getOperation().getName());
    }
    
    /**
     * This method generates the service node.
     * 
     * @param sn The service node
     * @param ratio The ratio, 1 if normal size
     * @param container The container
     * @param insertPoint The insertion point
     */
    protected void generateService(ServiceNode sn, double ratio, org.w3c.dom.Element container,
                        org.w3c.dom.Node insertPoint) {
        
        // Check that layout information is available
        if (!sn.getProperties().containsKey(ServiceGraphLayout.WIDTH)) {
            if (LOG.isLoggable(Level.FINER)) {
                LOG.fine("Service node with definition '"+sn.getService()+"' does not have layout information");
            }
            return;
        }
        
        org.w3c.dom.Element rect=
                    container.getOwnerDocument().createElement("rect");
        
        int width=(int)((Integer)sn.getProperties().get(ServiceGraphLayout.WIDTH) * ratio);
        rect.setAttribute("width", ""+width);
        
        int height=(int)((Integer)sn.getProperties().get(ServiceGraphLayout.HEIGHT) * ratio);
        rect.setAttribute("height", ""+height);
        
        int x=(int)((Integer)sn.getProperties().get(ServiceGraphLayout.X_POSITION) * ratio);
        rect.setAttribute("x", ""+x);
        
        int y=(int)((Integer)sn.getProperties().get(ServiceGraphLayout.Y_POSITION) * ratio);
        rect.setAttribute("y", ""+y);
        
        rect.setAttribute("fill", "#B8DBFF");
        
        // Derive the color
        Severity severity=null;        
        if (getSeverityAnalyzer() != null) {
            severity = getSeverityAnalyzer().getSeverity(sn, sn.getService().getMetrics(),
                            sn.getService().getHistory());
        }
        
        String color=getColor(severity);
        rect.setAttribute("stroke", color);

        rect.setAttribute("stroke-width", "2");
        rect.setAttribute("filter", "url(#f1)");
        
        container.insertBefore(rect, insertPoint);
        
        x= (Integer)sn.getProperties().get(ServiceGraphLayout.X_POSITION);
        x += 5;
        x *= ratio;
        
        y=(Integer)sn.getProperties().get(ServiceGraphLayout.Y_POSITION);
        y += 10;
        y *= ratio;
        
        if (isGenerateToolTips(ratio)) {
            // Generate tooltip
            generateMetrics(rect, sn.getService().getServiceType(),
                    sn.getService().getMetrics());
        }
        
        // Generate text
        org.w3c.dom.Element text=
                container.getOwnerDocument().createElement("text");
        
        text.setAttribute("x", ""+x);
        text.setAttribute("y", ""+y);
        
        text.setAttribute("font-family", "Verdana");
        text.setAttribute("font-size", "10");
        text.setAttribute("fill", "#00008F");
    
        String localname=getLocalName(sn.getService().getServiceType());
        
        org.w3c.dom.Text value=
                container.getOwnerDocument().createTextNode(
                        localname);
        text.appendChild(value);
        
        container.insertBefore(text, insertPoint);

        if (sn.getSituations().size() > 0) {
            generateSituations(container, insertPoint, x+(int)(width * 0.9), y, ratio,
                    sn.getSituations());
        }
    
        // Generate operations
        for (OperationNode opn : sn.getOperations()) {
            generateOperation(opn, ratio, container, insertPoint);
        }
    }
    
    /**
     * This method returns the local name associated with the supplied
     * fully qualified name.
     * 
     * @param qname The fully qualified name
     * @return The local name
     */
    protected String getLocalName(String qname) {
        String ret=qname;
        
        if (qname.length() > 0) {
            
            if (qname.charAt(0) == '{') {
                ret = QName.valueOf(qname).getLocalPart();
            } else {
                int pos=qname.lastIndexOf('.');
                
                if (pos != -1) {
                    ret = qname.substring(pos+1);
                }
            }
        }
        
        return (ret);
    }
    
    /**
     * This method determines whether to generate the tool tips.
     * 
     * @param ratio The ratio
     * @return Whether to generate tool tips
     */
    protected boolean isGenerateToolTips(double ratio) {
        return (ratio >= 1.0);
    }
    
    /**
     * This method determines whether to generate the text.
     * 
     * @param ratio The ratio
     * @return Whether to generate text
     */
    protected boolean isGenerateText(double ratio) {
        return (ratio >= 1.0);
    }
    
    /**
     * This method generates the situations associated with the supplied
     * position and list.
     * 
     * @param container The container
     * @param x The x position
     * @param y The y position
     * @param ratio The ratio
     * @param situations The list of situations
     */
    protected void generateSituations(org.w3c.dom.Element container,
                org.w3c.dom.Node insertPoint, int x, int y, double ratio,
                        java.util.List<Situation> situations) {
        int radius=(int)((double)6 * ratio);
        
        org.w3c.dom.Element circle=
                container.getOwnerDocument().createElement("circle");
        circle.setAttribute("cx", ""+x);
        circle.setAttribute("cy", ""+y);
        circle.setAttribute("r", ""+radius);
        //circle.setAttribute("stroke-width", "1");
        //circle.setAttribute("stroke", "black");
        
        container.insertBefore(circle, insertPoint);

        Situation.Severity severity=Situation.getHighestSeverity(situations);

        if (severity != null) {
            String color=getSituationSeverityColor(severity);
    
            circle.setAttribute("fill", color);
        }
        
        if (isGenerateToolTips(ratio)) {

            // Generate the situation elements
            org.w3c.dom.Element title=
                    container.getOwnerDocument().createElement("desc");
            circle.appendChild(title);
        
            org.w3c.dom.Text titleText=
                    container.getOwnerDocument().createTextNode("Situations");
            title.appendChild(titleText);                

            java.util.List<Situation> critical=Situation.getSituationsForSeverity(
                            Situation.Severity.Critical, situations);
            
            if (critical.size() > 0) {
                org.w3c.dom.Element desc=
                        container.getOwnerDocument().createElement(
                                Situation.Severity.Critical.name());
                circle.appendChild(desc);
            
                org.w3c.dom.Text descText=
                        container.getOwnerDocument().createTextNode(
                                getSituationText(critical.get(0)));
                desc.appendChild(descText);                
            }

            java.util.List<Situation> high=Situation.getSituationsForSeverity(
                    Situation.Severity.High, situations);
    
            if (high.size() > 0) {
                org.w3c.dom.Element desc=
                        container.getOwnerDocument().createElement(
                                Situation.Severity.High.name());
                circle.appendChild(desc);
            
                org.w3c.dom.Text descText=
                        container.getOwnerDocument().createTextNode(
                                getSituationText(high.get(0)));
                desc.appendChild(descText);                
            }

            java.util.List<Situation> medium=Situation.getSituationsForSeverity(
                    Situation.Severity.Medium, situations);
    
            if (medium.size() > 0) {
                org.w3c.dom.Element desc=
                        container.getOwnerDocument().createElement(
                                Situation.Severity.Medium.name());
                circle.appendChild(desc);
            
                org.w3c.dom.Text descText=
                        container.getOwnerDocument().createTextNode(
                                getSituationText(medium.get(0)));
                desc.appendChild(descText);                
            }

            java.util.List<Situation> low=Situation.getSituationsForSeverity(
                    Situation.Severity.Low, situations);
    
            if (low.size() > 0) {
                org.w3c.dom.Element desc=
                        container.getOwnerDocument().createElement(
                                Situation.Severity.Low.name());
                circle.appendChild(desc);
            
                org.w3c.dom.Text descText=
                        container.getOwnerDocument().createTextNode(
                                getSituationText(low.get(0)));
                desc.appendChild(descText);                
            }
        }
    }
    
    /**
     * This method returns the text to display for the supplied
     * situation.
     * 
     * @param s The situation
     * @return The text
     */
    protected String getSituationText(Situation s) {
        return (s.getType()+": "+s.getDescription()+" ["
                    +new java.util.Date(s.getTimestamp())+"]");
    }
    
    /**
     * This method returns the color associated with the supplied
     * situation severity.
     * 
     * @param severity The situation severity
     * @return The color
     */
    protected String getSituationSeverityColor(Situation.Severity severity) {
        String ret=null;
        
        if (severity == Situation.Severity.Critical) {
            ret = "red";
        } else if (severity == Situation.Severity.High) {
            ret = "orange";
        } else if (severity == Situation.Severity.Medium) {
            ret = "yellow";
        } else {
            ret = "green";
        }
        
        return (ret);
    }
    
    /**
     * This method returns the invocation metrics associated with the
     * supplied invocation definitions.
     * 
     * @param invocations The invocation definitions
     * @return The severity
     */
    protected Severity getInvocationSeverity(java.util.List<InvocationDefinition> invocations) {
        
        if (getSeverityAnalyzer() == null) {
            return (Severity.Normal);
        }
        
        Severity[] severities=new Severity[invocations.size()];
        
        for (int i=0; i < invocations.size(); i++) {
            InvocationDefinition id=invocations.get(i);
            
            severities[i] = getSeverityAnalyzer().getSeverity(id, id.getMetrics(), id.getHistory());
        }

        return (getAverageSeverity(severities));
    }
    
    /**
     * Determine the average severity based on the supplied array.
     * 
     * @param severities The severities
     * @return The average
     */
    protected static Severity getAverageSeverity(Severity[] severities) {
        Severity ret=Severity.Normal;
        
        int sum=0;
        for (Severity s : severities) {
            sum = s.ordinal();
        }
        
        if (sum > 0) {
            float result=sum / severities.length;
            
            ret = Severity.values()[Math.round(result)];
        }
        
        return (ret);
    }

    /**
     * This method generates the tooltip information to show
     * metrics.
     * 
     * @param container The container
     * @param description The description
     * @param metrics The metrics
     */
    protected void generateMetrics(org.w3c.dom.Element container,
            String description, InvocationMetric metrics) {
        org.w3c.dom.Element desc=
                container.getOwnerDocument().createElement("desc");
        container.appendChild(desc);
    
        org.w3c.dom.Text descText=
                container.getOwnerDocument().createTextNode(description);
        desc.appendChild(descText);

        String countValue="Count "+metrics.getCount();
        String avgValue="Avg "+metrics.getAverage();
        String minValue="Min "+metrics.getMin();
        String maxValue="Max "+metrics.getMax();
        
        if (metrics.getFaults() > 0) {
            countValue += " [faults "+metrics.getFaults()+"]";
        }
        
        if (metrics.getAverageChange() != 0 || metrics.getMaxChange() != 0
                || metrics.getMinChange() != 0 || metrics.getCountChange() != 0) {
            countValue += " ("+metrics.getCountChange()+"%)";
            avgValue += " ("+metrics.getAverageChange()+"%)";
            minValue += " ("+metrics.getMinChange()+"%)";
            maxValue += " ("+metrics.getMaxChange()+"%)";
        }
        
        org.w3c.dom.Element count=
                container.getOwnerDocument().createElement("count");
        container.appendChild(count);
    
        org.w3c.dom.Text countText=
                container.getOwnerDocument().createTextNode(countValue);
        count.appendChild(countText);

        org.w3c.dom.Element avg=
                container.getOwnerDocument().createElement("avg");
        container.appendChild(avg);
    
        org.w3c.dom.Text avgText=
                container.getOwnerDocument().createTextNode(avgValue);
        avg.appendChild(avgText);

        org.w3c.dom.Element max=
                container.getOwnerDocument().createElement("max");
        container.appendChild(max);
    
        org.w3c.dom.Text maxText=
                container.getOwnerDocument().createTextNode(maxValue);
        max.appendChild(maxText);

        org.w3c.dom.Element min=
                container.getOwnerDocument().createElement("min");
        container.appendChild(min);
    
        org.w3c.dom.Text minText=
                container.getOwnerDocument().createTextNode(minValue);
        min.appendChild(minText);

    }
    
    /**
     * This method generates the operation node.
     * 
     * @param opn The operation node
     * @param ratio The ratio
     * @param doc The svg document
     * @param insertPoint The insertion point
     */
    protected void generateOperation(OperationNode opn, double ratio,
            org.w3c.dom.Element container, org.w3c.dom.Node insertPoint) {
        
        org.w3c.dom.Element rect=
                    container.getOwnerDocument().createElement("rect");
        
        int width=(int)((Integer)opn.getProperties().get(ServiceGraphLayout.WIDTH) * ratio);
        rect.setAttribute("width", ""+width);
        
        int height=(int)((Integer)opn.getProperties().get(ServiceGraphLayout.HEIGHT) * ratio);
        rect.setAttribute("height", ""+height);
        
        int x=(int)((Integer)opn.getProperties().get(ServiceGraphLayout.X_POSITION) * ratio);
        rect.setAttribute("x", ""+x);
        
        int y=(int)((Integer)opn.getProperties().get(ServiceGraphLayout.Y_POSITION) * ratio);
        rect.setAttribute("y", ""+y);
        
        rect.setAttribute("fill", "#85D6FF");

        // Derive the colour
        Severity severity=null;
        if (getSeverityAnalyzer() != null) {
            severity = getSeverityAnalyzer().getSeverity(opn, opn.getOperation().getMetrics(),
                    opn.getOperation().getHistory());
        }
        String color=getColor(severity);
        
        rect.setAttribute("stroke", color);
        rect.setAttribute("stroke-width", "1");
        
        container.insertBefore(rect, insertPoint);
        
        x = (Integer)opn.getProperties().get(ServiceGraphLayout.X_POSITION);
        x += 5;
        x *= ratio;
        
        y = (Integer)opn.getProperties().get(ServiceGraphLayout.Y_POSITION);
        y += 14;
        y *= ratio;
        
        if (isGenerateText(ratio)) {
            org.w3c.dom.Element text=
                    container.getOwnerDocument().createElement("text");
            
            text.setAttribute("x", ""+x);
            text.setAttribute("y", ""+y);
            
            text.setAttribute("font-family", "Verdana");
            text.setAttribute("font-size", "12");
            text.setAttribute("fill", "#00008F");
            
            org.w3c.dom.Text value=
                    container.getOwnerDocument().createTextNode(
                            opn.getOperation().getName());
            text.appendChild(value);
            
            container.insertBefore(text, insertPoint);
        }
         
        if (isGenerateToolTips(ratio)) {
            // Generate tooltip
            generateMetrics(rect, opn.getOperation().getName(),
                    opn.getOperation().getMetrics());
        }
        
        if (opn.getSituations().size() > 0) {
            generateSituations(container, insertPoint, x+(int)(width * 0.9), y-4, ratio,
                        opn.getSituations());
        }
    }
    
    /**
     * This method saves the supplied document to the output stream.
     * 
     * @param doc The SVG document
     * @param os The output stream
     * @throws Exception Failed to save SVG document
     */
    protected void saveDocument(org.w3c.dom.Document doc, java.io.OutputStream os) 
                        throws Exception {
        
        // Write out SVG document
        javax.xml.transform.Transformer transformer=
                javax.xml.transform.TransformerFactory.newInstance().newTransformer();
        
        transformer.transform(new javax.xml.transform.dom.DOMSource(doc),
                new javax.xml.transform.stream.StreamResult(os));

    }
    
    /**
     * This method returns the named template file.
     * 
     * @param name The svg template name
     * @return The template, or null if failed to load
     */
    protected org.w3c.dom.Document loadTemplate(String name) {
        org.w3c.dom.Document ret=null;
        
        try {
            String template="templates/" + name + ".svg";
            
            java.io.InputStream is=
                    Thread.currentThread().getContextClassLoader().getResourceAsStream("/"+template);
            
            if (is == null) {
                is = Thread.currentThread().getContextClassLoader().getResourceAsStream(template);
            }

            DocumentBuilder builder=DocumentBuilderFactory.newInstance().newDocumentBuilder();
            
            ret = builder.parse(is);

            is.close();
            
        } catch (Exception e) {
            LOG.log(Level.SEVERE, MessageFormat.format(java.util.PropertyResourceBundle.getBundle(
                    "service-dependency-svg.Messages").getString("SERVICE-DEPENDENCY-SVG-1"),
                    name), e);
        }
        
        return (ret);
    }
}
