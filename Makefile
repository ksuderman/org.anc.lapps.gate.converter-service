VERSION=$(shell cat VERSION)
WAR=GateConverter\#$(VERSION).war
TGZ=GateConverter\#$(VERSION).tgz

include ../master.mk
