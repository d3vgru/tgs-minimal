#!/bin/bash
export TGS_VERSION=0.3
rm -f bin/AndroidTGS-$TGS_VERSION-debug.apk && ./build.py --package org.theglobalsquare.app --name "Android TGS" --orientation unspecified --version $TGS_VERSION --private app/python --sdk 14 --minsdk 9 --permission ACCESS_NETWORK_STATE --permission INTERNET debug
