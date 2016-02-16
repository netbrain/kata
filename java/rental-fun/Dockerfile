FROM netbrain/base-dev-java

ADD . $DEV_PATH

#MAVEN
RUN curl -k http://www.us.apache.org/dist/maven/maven-3/3.3.3/binaries/apache-maven-3.3.3-bin.tar.gz | tar xz
ENV PATH $PATH:$TOOLS_PATH/apache-maven-3.3.3/bin

WORKDIR $DEV_PATH
EXPOSE 8080
CMD mvn exec:java -Dexec.mainClass="io.github.netbrain.rentalfun.App"
