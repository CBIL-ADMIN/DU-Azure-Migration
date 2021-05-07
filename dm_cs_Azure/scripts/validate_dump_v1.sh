before="$(date +'%d%m%Y%H%M%S')"
echo "Script started At: $before" > ../validate_dump_${before}.log

gunzip ../input/*.csv.gz

awk -F[:=,] -v o_dir="../logs" 'BEGIN{OFS=","}
{
    if(FILENAME ~ /subscriber_be_dump/)
  {
        msi[$1]
        be_msi[$1]
  }
    else if(FILENAME ~ /subscriber_usms_dump/)
    {
        msi[$1]
        usms_msi[$1]
    }
    else if(FILENAME ~ /subscriber_profile_dump/)
    {
        msi[$1]
        profile_msi[$1]
    }
  else if(FILENAME ~ /subscriber_balances_dump/)
  {
        msi[$1]
  }
    else
    {
        msi[$1]
    }
}END{
   for(i in msi)
    {
        inc0001_msg="";
        inc0001_flag=0;
        if(!(i in be_msi))
        {
            inc0001_msg=inc0001_msg" BE_DUMP"
            inc0001_flag=1
        }
        if(!(i in usms_msi))
        {
            inc0001_msg=inc0001_msg" USMS_DUMP"
            inc0001_flag=1
        }
        if(!(i in profile_msi))
        {
            inc0001_msg=inc0001_msg" PROFILE_DUMP"
            inc0001_flag=1
        }
        if(inc0001_flag==1)
        {
            inc0001_msg = "MISSING IN [" inc0001_msg "]"
            print i,inc0001_msg > o_dir"/INC0001.lst"
        }
    } 
}' ../input/*subscriber_be_dump.csv ../input/*subscriber_usms_dump.csv ../input/*subscriber_profile_dump.csv ../input/*subscriber_balances_dump.csv ../input/*subscriber_cugcli_dump.csv

echo "INC0001 Checked"
touch ../logs/INC0001.lst

mkdir -p ../input/bak
mv ../input/*csv ../input/bak/.

be_f_cnt=`wc -l ../input/bak/*subscriber_be_dump.csv | awk '{print $1}'`
usms_f_cnt=`wc -l ../input/bak/*subscriber_usms_dump.csv | awk '{print $1}'`
profile_f_cnt=`wc -l ../input/bak/*subscriber_profile_dump.csv | awk '{print $1}'`
cugcli_f_cnt=`wc -l ../input/bak/*subscriber_cugcli_dump.csv | awk '{print $1}'`
balances_f_cnt=`wc -l ../input/bak/*subscriber_balances_dump.csv | awk '{print $1}'`
hostnm=`hostname`

awk -F, -v o_dir="../logs" -v i_dir="../input" -v hostnm=$hostnm 'function basename(file) {
        sub(".*/", "", file)
        return file
    }BEGIN{OFS=","}{
    if(FILENAME ~ /INC0001.lst/)
    {
        disc_inc[$1]="SUBSCRIBER RELATION MISSING"
        print "INC0001:SUBSCRIBER RELATION MISSING:MSISDN="$1":ERR_INFO="$2":ACTION=DISCARD AND LOG" > o_dir"/validated_discarded.log"
    }
    else if(FILENAME ~ /LIFECYCLE/)  
    {
        lfc_map[$1","$2]
    }
    else if(FILENAME ~ /SC_MAP/) 
    {
        sc_map[$2]=$4
    }
    else if(FILENAME ~ /LANGUAGE_MAP/) 
    {
        lang_map[$1]
    }
    else if(FILENAME ~ /BALANCE_MAP/) 
    {
        if($9=="Y")  #Ignore mapping??
        {
            if($4=="Y")     #All SC??
            {
                if($3=="")
                {
                    ignore_bal_map[$2]
                }
                else
                {
                    ignore_bal_val_map[$2","$3] 
                }
            }
            else
            {
                if($3=="")
                {
                    ignore_sc_bal_map[$6","$2]
                }
                else
                {
                    ignore_sc_bal_val_map[$6","$2","$3]
                }
            }
        }
        else
        {
            if($4=="Y")     #All SC??
            {
                if($3=="")
                {
                    bal_map[$2]
                }
                else
                {
                    bal_val_map[$2","$3]    
                }
            }
            else
            {
                if($3=="")
                {
                    sc_bal_map[$6","$2] 
                }
                else
                {
                    sc_bal_val_map[$6","$2","$3]    
                }
            }
        }
    }
    else if(FILENAME ~ /PRODUCT_MAP/) 
    {
        if($10=="N")
        {
            if($9=="PC" && ($2!="" && $3!=""))
            {
                gsub(" ","", $3)
                bt_map[$2]="=,"$3
            }
            else if($9=="PCBT" && ($2!="" && $3!="" && $5 !=""))
            {
                gsub(" ","", $3)
                pcbt_grpid++
                mbt_map[$2","pcbt_grpid] = ",=,"$3
                mbt_map[$5","pcbt_grpid] = $7","$8
                mbt_cnt[pcbt_grpid]=2
                mbt_bal_id[$2]
                mbt_bal_id[$5]
            }
            else if($9=="BT")
            {
                gsub(" ","", $8)
                bt_map[$5]=$7","$8
            }
            else if($9=="MBT")
            {
                gsub(" ","", $8)
                mbt_map[$5","$6] = $7","$8
                mbt_cnt[$6]++
                mbt_bal_id[$5]
            }
        }
    }
    else if(FILENAME ~ /subscriber_be_dump/)
    {
        if(!($1 in disc_inc))
        {
            if(!($3 in sc_map))
            {
                print "INC1001:CCS_ACCT_TYPE_ID LOOKUP FAILED:MSISDN=" $1 ":CCS_ACCT_TYPE_ID=" $3 ":SERVICE_STATE=" $4 ":ACTION=DISCARD AND LOG" > o_dir"/validated_discarded.log" 
                disc_inc[$1]="CCS_ACCT_TYPE_ID LOOKUP FAILED"
            }
            else if(sc_map[$3]=="Y")
            {
                print "INC1002:CCS_ACCT_TYPE_ID IGNORED:MSISDN=" $1 ":CCS_ACCT_TYPE_ID=" $3 ":SERVICE_STATE=" $4 ":ACTION=DISCARD AND LOG" > o_dir"/validated_discarded.log" 
                disc_inc[$1]="CCS_ACCT_TYPE_ID IGNORED"
            }
            else
            {
                be_msi[$1]=$0
            }
        }
        if(FNR%100000==0)
        {
            print "["basename(FILENAME)"] record processed: " FNR
        }
        BAK_FILENAME=FILENAME
    }
    else if(FILENAME ~ /subscriber_usms_dump/)
    {
        if(FNR==1)
        {
            close(BAK_FILENAME)
        }
        if(!($1 in disc_inc))
        {
            if($2=="")
            {
                msi_activation_dt_empty="Y"
            }
            else
            {
                msi_activation_dt_empty="N"   
            }
            split(be_msi[$1],be_msi_ele,",")
            if(!(be_msi_ele[4]","msi_activation_dt_empty in lfc_map))
            {
                print "INC1003:LIFECYCLE LOOKUP FAILED:MSISDN="$1":CCS_ACCT_TYPE_ID="be_msi_ele[3]":SERVICE_STATE="be_msi_ele[4]":ACTION=DISCARD AND LOG" > o_dir"/validated_discarded.log" 
                disc_inc[$1]="LIFECYCLE LOOKUP FAILED"
            }
            else
            {
                usms_msi[$1]=be_msi_ele[3]
                print be_msi[$1] > i_dir"/"hostnm"_subscriber_be_dump.csv"
                print $0 > i_dir"/"hostnm"_subscriber_usms_dump.csv"
            }
            delete be_msi[$1];
        }
        if(FNR%100000==0)
        {
            print "["basename(FILENAME)"] record processed: " FNR
        }
        BAK_FILENAME=FILENAME
    }
    else if(FILENAME ~ /subscriber_profile_dump/)
    {
        if(FNR==1)
        {
            close(BAK_FILENAME)
        }
        if(!($1 in disc_inc))
        {
            inc4001_msg=""
            inc4001_flag=0;
            if($2=="")
            {
                inc4001_msg=inc4001_msg" LANGUAGE_ID"
                inc4001_flag=1
            }
            else if(!($2 in lang_map))
            {
                print "INC4002:LANGUAGE_ID LOOKUP FAILED:MSISDN=" $1 ":LANGUAGE_ID=" $2 ":ACTION=PLACE DEFAULT AND LOG" > o_dir"/validated_exception.log" 
            }
            if($3=="")
            {
                inc4001_msg=inc4001_msg" IMSI"
                inc4001_flag=1
            }
            if($4=="")
            {
                inc4001_msg=inc4001_msg" IMEI"
                inc4001_flag=1
            }
            if($5=="")
            {
                inc4001_msg=inc4001_msg" hlrAddr"
                inc4001_flag=1
            }
            if(inc4001_flag==1)
            {
                inc4001_msg = "MISSING [" inc4001_msg "]"
                print "INC4001:IMSI/hlrAddr/IMEI/LANGUAGE_ID VALUE MISSING:MSISDN=" $1 ":ERR_INFO=" inc4001_msg ":ACTION=PLACE DEFAULT AND LOG" > o_dir"/validated_exception.log" 
            }

            print $0 > i_dir"/"hostnm"_subscriber_profile_dump.csv"
        }
        if(FNR%100000==0)
        {
            print "["basename(FILENAME)"] record processed: " FNR
        }
        BAK_FILENAME=FILENAME
    }
    else if(FILENAME ~ /subscriber_cugcli_dump/)
    {
        if(FNR==1)
        {
            close(BAK_FILENAME)
        }
        if(!($1 in disc_inc))
        {
            print $0 > i_dir"/"hostnm"_subscriber_cugcli_dump.csv"
        }
        if(FNR%100000==0)
        {
            print "["basename(FILENAME)"] record processed: " FNR
        }
        BAK_FILENAME=FILENAME
    }
    else if(FILENAME ~ /subscriber_balances_dump/)
    {
        if(FNR==1)
        {
            close(BAK_FILENAME)
        }
        if(!($1 in disc_inc))
        {
            if(($3 in ignore_bal_map) || ($3","$6 in ignore_bal_val_map) || (usms_msi[$1]","$3 in ignore_sc_bal_map) || (usms_msi[$1]","$3","$6 in ignore_sc_bal_val_map))
            {
                print "INC4003:BALANCE_TYPE IGNORED:MSISDN=" $1 ":CCS_ACCT_TYPE_ID=" usms_msi[$1] ":BALANCE_TYPE_ID=" $3 ":BE_BUCKET_VALUE=" $6 ":ACTION:DISCARD AND LOG" > o_dir"/validated_exception.log"  
            }
            else
            {
                t_bal_rec[$1","$3]=$6
                if(($3 in bal_map) || ($3","$6 in bal_val_map) || (usms_msi[$1]","$3 in sc_bal_map) || (usms_msi[$1]","$3","$6 in sc_bal_val_map))
                {
                    if(!($1","$3 in i_bal_rec))
                    {
                        print $0 > i_dir"/"hostnm"_subscriber_balances_dump.csv"
                        i_bal_rec[$1","$3]
                    }
                }

                if($3 in bt_map)
                {
                    split(bt_map[$3],bt_map_ele,",")
                    if(bt_map_ele[1] == "=")
                    {
                        if($6 == bt_map_ele[2])
                        {
                            if(!($1","$3 in i_bal_rec))
                            {
                                print $0 > i_dir"/"hostnm"_subscriber_balances_dump.csv"
                                i_bal_rec[$1","$3]
                            }
                        }
                    }
                    else if(bt_map_ele[1] == ">")
                    {
                        if($6 > bt_map_ele[2])
                        {
                            if(!($1","$3 in i_bal_rec))
                            {
                                print $0 > i_dir"/"hostnm"_subscriber_balances_dump.csv"
                                i_bal_rec[$1","$3]
                            }
                        }
                    }
                    else if(bt_map_ele[1] == "<")
                    {
                        if($6 < bt_map_ele[2])
                        {
                            if(!($1","$3 in i_bal_rec))
                            {
                                print $0 > i_dir"/"hostnm"_subscriber_balances_dump.csv"
                                i_bal_rec[$1","$3]
                            }
                        }
                    }
                    else if(bt_map_ele[1] == "<=")
                    {
                        if($6 <= bt_map_ele[2])
                        {
                            if(!($1","$3 in i_bal_rec))
                            {
                                print $0 > i_dir"/"hostnm"_subscriber_balances_dump.csv"
                                i_bal_rec[$1","$3]
                            }
                        }
                    }
                    else if(bt_map_ele[1] == ">=")
                    {
                        if($6 >= bt_map_ele[2])
                        {
                            if(!($1","$3 in i_bal_rec))
                            {
                                print $0 > i_dir"/"hostnm"_subscriber_balances_dump.csv"
                                i_bal_rec[$1","$3]
                            }
                        }
                    }
                    else if(bt_map_ele[1] == "or")
                    {
                        split(bt_map_ele[2],or_ele,"|")
                        for(i in or_ele)
                        {
                            if($6 == or_ele[i])
                            {
                                if(!($1","$3 in i_bal_rec))
                                {
                                    print $0 > i_dir"/"hostnm"_subscriber_balances_dump.csv"
                                    i_bal_rec[$1","$3]
                                }
                            }
                        }
                    }
                    else if(bt_map_ele[1] == "")
                    {
                        if(!($1","$3 in i_bal_rec))
                        {
                            print $0 > i_dir"/"hostnm"_subscriber_balances_dump.csv"
                            i_bal_rec[$1","$3]
                        }
                    }
                }
                if($3 in mbt_bal_id)
                {
                    for(i in mbt_map)
                    {
                        split(i,i_ele,",")
                        if(i_ele[1] == $3)
                        {
                            split(mbt_map[i],mbt_map_i_ele,",")
                            if(mbt_map_i_ele[1] == "=")
                            {
                                if($6 == mbt_map_i_ele[2])
                                {
                                    i_mbt_match[$1","i_ele[2]]++
                                    i_mbt_rec[$1][i_ele[2]][$3]=$0
                                    if(i_mbt_match[$1","i_ele[2]] == mbt_cnt[i_ele[2]])
                                    {
                                        for(i in i_mbt_rec)
                                        {
                                            if(i == $1)
                                            {
                                                for(j in i_mbt_rec[i])
                                                {
                                                    if(j == i_ele[2])
                                                    {
                                                        for(k in i_mbt_rec[i][j])
                                                        {
                                                            split(i_mbt_rec[i][j][k],i_mbt_rec_ele,",")
                                                            if(!(i_mbt_rec_ele[1]","i_mbt_rec_ele[3] in i_bal_rec))
                                                            {
                                                                print i_mbt_rec[i][j][k] > i_dir"/"hostnm"_subscriber_balances_dump.csv"
                                                                i_bal_rec[i_mbt_rec_ele[1]","i_mbt_rec_ele[3]]
                                                            }
                                                        }
                                                        delete i_mbt_rec[i][j]
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            else if(mbt_map_i_ele[1] == ">")
                            {
                                if($6 > mbt_map_i_ele[2])
                                {
                                    i_mbt_match[$1","i_ele[2]]++
                                    i_mbt_rec[$1][i_ele[2]][$3]=$0
                                    if(i_mbt_match[$1","i_ele[2]] == mbt_cnt[i_ele[2]])
                                    {
                                        for(i in i_mbt_rec)
                                        {
                                            if(i == $1)
                                            {
                                                for(j in i_mbt_rec[i])
                                                {
                                                    if(j == i_ele[2])
                                                    {
                                                        for(k in i_mbt_rec[i][j])
                                                        {
                                                            split(i_mbt_rec[i][j][k],i_mbt_rec_ele,",")
                                                            if(!(i_mbt_rec_ele[1]","i_mbt_rec_ele[3] in i_bal_rec))
                                                            {
                                                                print i_mbt_rec[i][j][k] > i_dir"/"hostnm"_subscriber_balances_dump.csv"
                                                                i_bal_rec[i_mbt_rec_ele[1]","i_mbt_rec_ele[3]]
                                                            }
                                                        }
                                                        delete i_mbt_rec[i][j]
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            else if(mbt_map_i_ele[1] == "<")
                            {
                                if($6 < mbt_map_i_ele[2])
                                {
                                    i_mbt_match[$1","i_ele[2]]++
                                    i_mbt_rec[$1][i_ele[2]][$3]=$0
                                    if(i_mbt_match[$1","i_ele[2]] == mbt_cnt[i_ele[2]])
                                    {
                                        for(i in i_mbt_rec)
                                        {
                                            if(i == $1)
                                            {
                                                for(j in i_mbt_rec[i])
                                                {
                                                    if(j == i_ele[2])
                                                    {
                                                        for(k in i_mbt_rec[i][j])
                                                        {
                                                            split(i_mbt_rec[i][j][k],i_mbt_rec_ele,",")
                                                            if(!(i_mbt_rec_ele[1]","i_mbt_rec_ele[3] in i_bal_rec))
                                                            {
                                                                print i_mbt_rec[i][j][k] > i_dir"/"hostnm"_subscriber_balances_dump.csv"
                                                                i_bal_rec[i_mbt_rec_ele[1]","i_mbt_rec_ele[3]]
                                                            }
                                                        }
                                                        delete i_mbt_rec[i][j]
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            else if(mbt_map_i_ele[1] == ">=")
                            {
                                if($6 >= mbt_map_i_ele[2])
                                {
                                    i_mbt_match[$1","i_ele[2]]++
                                    i_mbt_rec[$1][i_ele[2]][$3]=$0
                                    if(i_mbt_match[$1","i_ele[2]] == mbt_cnt[i_ele[2]])
                                    {
                                        for(i in i_mbt_rec)
                                        {
                                            if(i == $1)
                                            {
                                                for(j in i_mbt_rec[i])
                                                {
                                                    if(j == i_ele[2])
                                                    {
                                                        for(k in i_mbt_rec[i][j])
                                                        {
                                                            split(i_mbt_rec[i][j][k],i_mbt_rec_ele,",")
                                                            if(!(i_mbt_rec_ele[1]","i_mbt_rec_ele[3] in i_bal_rec))
                                                            {
                                                                print i_mbt_rec[i][j][k] > i_dir"/"hostnm"_subscriber_balances_dump.csv"
                                                                i_bal_rec[i_mbt_rec_ele[1]","i_mbt_rec_ele[3]]
                                                            }
                                                        }
                                                        delete i_mbt_rec[i][j]
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            else if(mbt_map_i_ele[1] == "<=")
                            {
                                if($6 <= mbt_map_i_ele[2])
                                {
                                    i_mbt_match[$1","i_ele[2]]++
                                    i_mbt_rec[$1][i_ele[2]][$3]=$0
                                    if(i_mbt_match[$1","i_ele[2]] == mbt_cnt[i_ele[2]])
                                    {
                                        for(i in i_mbt_rec)
                                        {
                                            if(i == $1)
                                            {
                                                for(j in i_mbt_rec[i])
                                                {
                                                    if(j == i_ele[2])
                                                    {
                                                        for(k in i_mbt_rec[i][j])
                                                        {
                                                            split(i_mbt_rec[i][j][k],i_mbt_rec_ele,",")
                                                            if(!(i_mbt_rec_ele[1]","i_mbt_rec_ele[3] in i_bal_rec))
                                                            {
                                                                print i_mbt_rec[i][j][k] > i_dir"/"hostnm"_subscriber_balances_dump.csv"
                                                                i_bal_rec[i_mbt_rec_ele[1]","i_mbt_rec_ele[3]]
                                                            }
                                                        }
                                                        delete i_mbt_rec[i][j]
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            else if(mbt_map_i_ele[1] == "or")
                            {
                                split(mbt_map_i_ele[2],or_ele,"|")
                                for(i in or_ele)
                                {
                                    if($6 == or_ele[i])
                                    {
                                        i_mbt_match[$1","i_ele[2]]++
                                        i_mbt_rec[$1][i_ele[2]][$3]=$0
                                        if(i_mbt_match[$1","i_ele[2]] == mbt_cnt[i_ele[2]])
                                        {
                                            for(i in i_mbt_rec)
                                            {
                                                if(i == $1)
                                                {
                                                    for(j in i_mbt_rec[i])
                                                    {
                                                        if(j == i_ele[2])
                                                        {
                                                            for(k in i_mbt_rec[i][j])
                                                            {
                                                                split(i_mbt_rec[i][j][k],i_mbt_rec_ele,",")
                                                                if(!(i_mbt_rec_ele[1]","i_mbt_rec_ele[3] in i_bal_rec))
                                                                {
                                                                    print i_mbt_rec[i][j][k] > i_dir"/"hostnm"_subscriber_balances_dump.csv"
                                                                    i_bal_rec[i_mbt_rec_ele[1]","i_mbt_rec_ele[3]]
                                                                }
                                                            }
                                                            delete i_mbt_rec[i][j]
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            else if(mbt_map_i_ele[1] == "")
                            {
                                i_mbt_match[$1","i_ele[2]]++
                                i_mbt_rec[$1][i_ele[2]][$3]=$0
                                if(i_mbt_match[$1","i_ele[2]] == mbt_cnt[i_ele[2]])
                                {
                                    for(i in i_mbt_rec)
                                    {
                                        if(i == $1)
                                        {
                                            for(j in i_mbt_rec[i])
                                            {
                                                if(j == i_ele[2])
                                                {
                                                    for(k in i_mbt_rec[i][j])
                                                    {
                                                        split(i_mbt_rec[i][j][k],i_mbt_rec_ele,",")
                                                        if(!(i_mbt_rec_ele[1]","i_mbt_rec_ele[3] in i_bal_rec))
                                                        {
                                                            print i_mbt_rec[i][j][k] > i_dir"/"hostnm"_subscriber_balances_dump.csv"
                                                            i_bal_rec[i_mbt_rec_ele[1]","i_mbt_rec_ele[3]]
                                                        }
                                                    }
                                                    delete i_mbt_rec[i][j]
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        else
        {
        	print "INC1004:" disc_inc[$1] ":MSISDN=" $1 ":CCS_ACCT_TYPE_ID=" usms_msi[$1] ":BALANCE_TYPE_ID=" $3 ":BE_BUCKET_VALUE=" $6 ":ACTION:DISCARD AND LOG" > o_dir"/validated_exception.log"  
        } 
        if(FNR%100000==0 || FNR==balances_f_cnt)
        {
            print "["basename(FILENAME)"] record processed: " FNR
        }
        BAK_FILENAME=FILENAME
    }
}END{
    close(BAK_FILENAME)
    for(i in t_bal_rec)
    {
        if(!(i in i_bal_rec))
        {
            split(i,i_ele,",")
            print "INC4004:BALANCE_TYPE LOOKUP FAILED:MSISDN=" i_ele[1] ":CCS_ACCT_TYPE_ID=" usms_msi[i_ele[1]] ":BALANCE_TYPE_ID=" i_ele[2] ":BE_BUCKET_VALUE=" t_bal_rec[i] ":ACTION:DISCARD AND LOG" > o_dir"/validated_exception.log"  
        }
    }
}' ../logs/INC0001.lst \
../config/LIFECYCLE_MAP.txt \
../config/SC_MAP.txt \
../config/LANGUAGE_MAP.txt \
../config/BALANCE_MAP.txt \
../config/PRODUCT_MAP.txt \
../input/bak/*subscriber_be_dump.csv \
../input/bak/*subscriber_usms_dump.csv \
../input/bak/*subscriber_profile_dump.csv \
../input/bak/*subscriber_cugcli_dump.csv \
../input/bak/*subscriber_balances_dump.csv

gzip ../input/bak/*.csv
gzip ../input/*csv

after="$(date +'%d%m%Y%H%M%S')"
echo "Script finished At: $after" >> ../validate_dump_${before}.log
