#!/bin/bash
uname="sdpuser"
password=$1
rcommand="ttisql -connStr 'DSN=sdp_db' -e \"select OFFER_DEFINITION_ID,ATTRIBUTE_DEF_NAME,ATTRIBUTE_DEF_ID,VALUE_TYPE,VALUE,SETTINGS from offer_attribute_definition;quit;\""
delcommand="/opt/sdp/DataTool/bin/subscriberDumpTool delete"
workdir=$2
attrfile="$workdir/config/dynamic/OFFER_ATTRIBUTE_DEFINITION.csv"

sudo -k
if sudo -lS &> /dev/null << EOF
$password
EOF
then
	echo $password | sudo -S su - $uname -c "$rcommand" | grep '<' | awk -F" " -v ofile="$attrfile" '{print $2$3$4$5$6$7 > ofile}'
	if [ ! -f $attrfile ]; then
        echo "File [ $attrfile ] not found!!!"
    else
        echo "File [ $attrfile ] updated!!!"
    fi
else 
    echo 'Wrong password.'
fi
