#!/bin/bash
#This is starting of script
OS="`uname`"
uname="sdpuser"
password=$1
loadcommand="/opt/sdp/DataTool/bin/subscriberDumpTool load"
workdir=$2
loaddir="$workdir/output"

sudo -k
if sudo -lS &> /dev/null << EOF
$password
EOF
then
    case $OS in
      'Linux')
        OS='Linux'
        echo "OS=${OS}, Node= ${HOSTNAME} , Dumptool_Command=${loadcommand}"
            tar -zxvf $workdir/config/scheme.tar.gz -C $loaddir
            touch $loaddir/SubscriberOffer.csv
            setfacl -R -m user:$uname:rwx $loaddir
            echo $password | sudo -S su - $uname -c "$loadcommand $loaddir"
            echo $password | sudo -S chown -R migration:migration $loaddir/upgrade-*
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
    
