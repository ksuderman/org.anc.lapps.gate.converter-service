VERSION=$(shell cat VERSION)
#TOMCAT_HOME=/usr/share/tomcat/service-manager
TOMCAT=/Applications/Servers/tomcat
LOCAL=$(TOMCAT)/server-1/webapps
SERVER=/usr/share/tomcat/server-1/webapps
WAR=GateConverter\#$(VERSION).war

include ../master.mk
