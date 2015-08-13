#
# JBoss, Home of Professional Open Source.
# Copyright 2010, Red Hat, Inc., and individual contributors
# as indicated by the @author tags. See the copyright.txt file in the
# distribution for a full listing of individual contributors.
#
# This is free software; you can redistribute it and/or modify it
# under the terms of the GNU Lesser General Public License as
# published by the Free Software Foundation; either version 2.1 of
# the License, or (at your option) any later version.
#
# This software is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
# Lesser General Public License for more details.
#
# You should have received a copy of the GNU Lesser General Public
# License along with this software; if not, write to the Free
# Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
# 02110-1301 USA, or see the FSF site: http://www.fsf.org.
#

# Environment script for setting up the JAVA_OPTS property for client instrumentation

export BTM_CLIENT_JAR_PATH=$JBOSS_HOME/standalone/lib/hawkular-btm-client-rest.jar

export JAVA_OPTS="-Dorg.jboss.byteman.transform.all \
    -javaagent:$BTM_CLIENT_JAR_PATH=manager:org.hawkular.btm.client.manager.ClientManager,boot:$BTM_CLIENT_JAR_PATH \
    -Dorg.jboss.byteman.compileToBytecode \
    -Dhawkular-btm.config=$JBOSS_HOME/standalone/data/btmconfig \
    -Dhawkular-btm.base-uri=http://localhost:8080/hawkular/btm \
    -Dhawkular-btm.username=admin \
    -Dhawkular-btm.password=admin"

# Wildfly specific
export JAVA_OPTS="$JAVA_OPTS -Djboss.modules.system.pkgs=org.jboss.byteman,org.hawkular.btm.client.manager,org.hawkular.btm.client.api"
