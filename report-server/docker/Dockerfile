FROM my_centos
ADD start_agent.sh /opt/flume/conf/
RUN chmod +rwx /opt/flume/conf/start_agent.sh
ADD apache-flume-1.9.0-bin.tar.gz /opt/
RUN mv /opt/apache-flume-1.9.0-bin /opt/flume
#设置环境变量
ENV FLUME_HOME /opt/flume
ENV PATH $PATH:$FLUME_HOME/bin
WORKDIR /opt/flume
#ENTRYPOINT ["tail", "-f", "/dev/null"]
#ENTRYPOINT ["./conf/start_agents.sh"]
CMD ["flume-ng","version"]