#!/bin/bash
export TGS_VERSION=0.3
export PRIVATE_VERSION=3
rm -f bin/AndroidTGS-$TGS_VERSION-debug.apk && ./build.py --package org.theglobalsquare.app --name "The Global Square" --orientation unspecified --version $TGS_VERSION --private app/python --sdk 17 --minsdk 9 --permission ACCESS_NETWORK_STATE --permission ACCESS_WIFI_STATE --permission INTERNET debug
#--private-version 3 debug
