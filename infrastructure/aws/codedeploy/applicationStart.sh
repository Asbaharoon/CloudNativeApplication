#!/bin/bash
sudo systemctl stop tomcat.service
cd /opt/tomcat/webapps
sudo systemctl start tomcat.service
