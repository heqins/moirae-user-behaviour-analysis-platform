#!/bin/bash
echo "Start agents..."
 
flume-ng agent -c conf/ -n log_monitor -f conf/flume.conf -Dflume.root.logger=INFO,console > logs/log_monitor.out 2>&1
 
#如果使用docker run 的方式启动容器，都要记得加上 exec "$@" 或者  tail -f /dev/null ；否则执行完脚本容器就自动关闭了；而docker-compose方式因为有tty:true，所以这里就不需要了
#exec "$@"
#tail -f /dev/null