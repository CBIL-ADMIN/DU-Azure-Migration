#!/bin/bash

declare -A array
array["account"]="Account.csv"
array["accumulator"]="Accumulator.csv"
array["dedicated_account"]="DedicatedAccount.csv"
array["offer_attribute"]="OfferAttribute.csv"
array["offer"]="Offer.csv"
array["pam_account"]="PamAccount.csv"
array["subscriber"]="Subscriber.csv"
array["subscriber_faf"]="SubscriberFaf.csv"
array["usage_counter"]="UsageCounter.csv"

loginname="sdpuser"
password=$1
load_file_path="$2/output"
log_file_path="$2/logs"

sudo -k
if sudo -lS &> /dev/null << EOF
$password
EOF
then
        echo
        printf '%*s\n' "116" | tr ' ' "-" | tee $log_file_path/load_check_results.txt
        printf "|\e[1m%30s\e[0m|\e[1m%20s\e[0m|\e[1m%20s\e[0m|\e[1m%30s\e[0m|\e[1m%10s\e[0m|\n" "FILE_NAME" "FILE_COUNT" "DB_COUNT" "RESULT" "DIFF" | tee -a  $log_file_path/load_check_results.txt
        printf '%*s\n' "116" | tr ' ' "-" | tee -a  $log_file_path/load_check_results.txt
        for i in "${!array[@]}"
        do
                err=""
                result=""
                diff_rec=""
                load_file=${load_file_path}/${array[${i}]}

                rcommand="ttisql -connStr 'DSN=sdp_db' -e 'desc ${i};exit;'"
                #prep_sql="echo $password | sudo -S su - $loginname -c "ttisql -connStr 'DSN=sdp_db' -e 'desc ${i};exit;'"
                echo $password | sudo -S su - $loginname -c "$rcommand" | grep "table found" > $2/tmp/chk_table_file
                if [[ ! -f ${load_file} ]]; then
                        count_file=0
                        result="LOADING_FILE_NOT_FOUND;";
                        err=1;
                else
                        count_file=`wc -l ${load_file} | awk -F" " '{print $1}'`
                fi
                if [ ! -f $2/tmp/chk_table_file ]; then
                        count_db=0;
                        if [ "$result" == "" ]; then
                                result="DB_TABLE_NOT_FOUND"
                        else
                                continue
                        fi
                        err=1;
                else
                        rcommand="ttisql -connStr 'DSN=sdp_db' -e 'select count(*) from ${i};exit;'"
                        echo $password | sudo -S su - $loginname -c "$rcommand" | grep "<" | grep ">" | sed 's/[< > ]//g' > $2/tmp/count_db_file
                        count_db=`cat $2/tmp/count_db_file`
                fi

                diff_rec=`expr $count_file - $count_db`
                if [[ "$err" -eq "" ]]; then
                        if [[ "$count_file" -ne "$count_db" ]]; then
                                result="NOT_SAME"
                        else
                                result="SAME"
                        fi
                fi

                if [[ "$diff_rec" -eq "0" ]]; then
                        printf "|%30s|%20s|%20s|\e[32m%30s\e[0m|%10s|\n" ${i} ${count_file} ${count_db} ${result} ${diff_rec} | tee -a  $log_file_path/load_check_results.txt
                else
                        printf "|%30s|%20s|%20s|\e[31m%30s\e[0m|\e[33;1m%10s\e[0m|\n" ${i} ${count_file} ${count_db} ${result} ${diff_rec} | tee -a  $log_file_path/load_check_results.txt
                fi
        done
        printf '%*s\n' "116" | tr ' ' "-" | tee -a  $log_file_path/load_check_results.txt
else 
    echo 'Wrong password.'
fi

        
