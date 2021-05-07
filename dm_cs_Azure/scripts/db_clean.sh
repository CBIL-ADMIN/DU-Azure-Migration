#!/bin/bash
uname="sdpuser"
password="Ericssontest@567"
get_msi_command="ttisql -connStr 'DSN=sdp_db' -e \"select msisdn from subscriber;quit;\""
get_cnt_command="ttisql -connStr 'DSN=sdp_db' -e \"select count(1) from subscriber;quit;\""
del_msi_command="/opt/sdp/DataTool/bin/subscriberDumpTool delete"
workdir=$2
datfile="$workdir/tmp/SUBSCRIBER_00001.DAT"

sudo -k
if sudo -lS &> /dev/null << EOF
$password
EOF
then
	echo $password | sudo -S su - $uname -c "$get_msi_command" | awk -F" " -v ofile="$datfile" 'BEGIN{print "0,127.0.0.1,127.0.0.2" > ofile};{if($0 ~ /</){cnt++;print "1,"$2 > ofile}}; END{print "100,"cnt+1 > ofile}'        
	setfacl -R -m user:$uname:rwx $datfile
	echo $password | sudo -S su - $uname -c "$del_msi_command $datfile"
	
	count_db=`echo $password | sudo -S su - $uname -c "$get_cnt_command" | awk -F" " '{if($0 ~ /</){print $2}};'`
	if [[ ! -z $count_db ]];then
		echo "Subscriber in DB : $count_db"
	fi
else 
    echo 'Wrong password.'
fi
