#!/bin/bash
#This is starting of script
OS="`uname`"
case $OS in
  'Linux')
    OS='Linux'
    echo "OS=${OS}, Node= ${1} , snapshot Command=${2}"
        #tar -zxvf config/scheme.tar.gz -C output/
        echo "execute snapshot command ..."
        ${2}
        ;;
  'SunOS')
    OS='Solaris'
        echo "OS=${OS}, Node= ${1} , Dumptool Command=${2}"
        cp -r config/scheme.tar.gz output/${1}/SDP/
        cd Output/${1}/SDP/
        gunzip scheme.tar.gz
        tar -xf scheme.tar
        rm -rf scheme.tar
        echo "execute dumptool command ..."
        ${2}
        ;;
  *)
        echo "Node= ${1} , Dumptool Command=${2}"
        tar -zxvf config/scheme.tar.gz -C output/${1}/SDP/
        echo "execute dumptool command ..."
        ${2}
        ;;
esac
