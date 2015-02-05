FROM jboss/wildfly:8.1.0.Final

RUN $JBOSS_HOME/bin/add-user.sh admin admin123! --silent

EXPOSE 8787

ADD overlord-rtgov-dist-all-wildfly8.zip /tmp/

RUN cd $HOME \
    && bsdtar -xf /tmp/overlord-rtgov-dist-all-wildfly8.zip \
    && cd overlord-rtgov-dist-all-wildfly8 \
    && ./install.sh -Dpath=$JBOSS_HOME \
    && cp dist/governance-realm.json $JBOSS_HOME \
    && echo 'JAVA_OPTS="$JAVA_OPTS -Dkeycloak.import=$JBOSS_HOME/governance-realm.json"' >> $JBOSS_HOME/bin/standalone.conf \
    && cd $HOME \
    && rm -rf overlord-rtgov-dist-all-wildfly8

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0", "-c", "standalone-full.xml", "--debug"]
