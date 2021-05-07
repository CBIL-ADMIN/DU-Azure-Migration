#!/bin/bash
#This is starting of script
OS="`uname`"
uname="sdpuser"
password=$1
snapcommand="/opt/sdp/SnapShot/bin/dumpmig.ksh"
workdir=$2
snapdir="$workdir/snapshot"

sudo -k
if sudo -lS &> /dev/null << EOF
$password
EOF
then
    case $OS in
      'Linux')
        OS='Linux'
        echo "OS=${OS}, Node= ${HOSTNAME} , snapshot Command=${snapcommand}"
            setfacl -R -m u:$uname:rwx $snapdir
            echo $password | sudo -S su - $uname -c "$snapcommand $snapdir"
            echo $password | sudo -S chown -R migration:migration $snapdir
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
else 
    echo 'Wrong password.'
fi

    
