x=1
y=`expr $1+1`
while [[ "$x" -lt "$y" ]]; do
    mkdir -p ../input/batch_${x}
    ((x++))
done

awk -F"|" '{if($1 in arr){brr[$1]}arr[$1]}END{for(i in brr)print i}' $2/*INFILE_Subscriber_Dump_BE*.csv > ../input/be_dup.lst

touch ../input/be_dup.lst

awk -F"|" -v fp=$1 -v t_dir="../input" -v l_dir="../logs" 'function basename(file) {
        sub(".*/", "", file)
        return file
    }BEGIN{OFS="|"}
{
    if(FILENAME ~ /be_dup/)
    {
        dup[$1]
    }
    else if(FILENAME ~ /INFILE_Subscriber_Dump_BE/)
    {
        if(!($1 in dup))
        {
            if($1 ~ /[0-9]+/)
            {
                msi[$1]
                be_msi[$1]++
                if(filepart==fp)
                {
                    filepart=1
                }
                else
                {
                    filepart++
                }
                msi_file[$1]=filepart
                print $0 > t_dir"/batch_"msi_file[$1]"/meydvvmsdp"sprintf("%02d",msi_file[$1])"_subscriber_be_dump.csv"
                if(FNR%100000==0)
                {
                    print "["basename(FILENAME)"] record processed: " FNR
                }
            }
        }
    }
    else if(FILENAME ~ /INFILE_Subscriber_Dump_USMS/)
    {
        if(!($1 in dup))
        {
            if($1 ~ /[0-9]+/)
            {
                msi[$1]
                usms_msi[$1]
                if(!($1 in msi_file))
                {
                    if(filepart==fp)
                    {
                        filepart=1
                    }
                    else
                    {
                        filepart++
                    }
                    msi_file[$1]=filepart
                }
                print $0 > t_dir"/batch_"msi_file[$1]"/meydvvmsdp"sprintf("%02d",msi_file[$1])"_subscriber_usms_dump.csv"
                if(FNR%100000==0)
                {
                    print "["basename(FILENAME)"] record processed: " FNR
                }
            }
        }
    }
    else if(FILENAME ~ /INFILE_Subscriber_Profile/)
    {
        if(!($1 in dup))
        {
            if($1 ~ /[0-9]+/)
            {
                msi[$1]
                profile_msi[$1]
                if(!($1 in msi_file))
                {
                    if(filepart==fp)
                    {
                        filepart=1
                    }
                    else
                    {
                        filepart++
                    }
                    msi_file[$1]=filepart
                }
                print $0 > t_dir"/batch_"msi_file[$1]"/meydvvmsdp"sprintf("%02d",msi_file[$1])"_subscriber_profile_dump.csv"
                if(FNR%100000==0)
                {
                    print "["basename(FILENAME)"] record processed: " FNR
                }
            }
        }
            
    }
    else if(FILENAME ~ /INFILE_Subscriber_Balances/)
    {
        if(!($1 in dup))
        {
            if($1 ~ /[0-9]+/)
            {
                msi[$1]
                if(!($1 in msi_file))
                {
                    if(filepart==fp)
                    {
                        filepart=1
                    }
                    else
                    {
                        filepart++
                    }
                    msi_file[$1]=filepart
                }
                if(balance_file==4)
                {
                    balance_file=1
                }
                else
                {
                    balance_file++
                }
                print $0 > t_dir"/batch_"msi_file[$1]"/meydvvmsdp"sprintf("%02d",msi_file[$1])"_subscriber_balances_dump" sprintf("%02d",balance_file) ".csv"
                if(FNR%100000==0)
                {
                    print "["basename(FILENAME)"] record processed: " FNR
                }
            }
        }
    }
    else
    {
        if(!($1 in dup))
        {
            if($1 ~ /[0-9]+/)
            {
                msi[$1]
                if(!($1 in msi_file))
                {
                    if(filepart==fp)
                    {
                        filepart=1
                    }
                    else
                    {
                        filepart++
                    }
                    msi_file[$1]=filepart
                }
                print $0 > t_dir"/batch_"msi_file[$1]"/meydvvmsdp"sprintf("%02d",msi_file[$1])"_subscriber_cugcli_dump.csv"
                if(FNR%100000==0)
                {
                    print "["basename(FILENAME)"] record processed: " FNR
                }
            }
        }
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
            print i,inc0001_msg > l_dir"/INC0001.lst"
        }
    }
    for(j in be_msi)
    {
        if(be_msi[j]>1)
        {
            print j,be_msi[j] > l_dir"/INC0002.lst"
        }
    }
}' ../input/be_dup.lst $2/*INFILE_Subscriber_Dump_BE*.csv $2/*INFILE_Subscriber_Dump_USMS*.csv $2/*INFILE_Subscriber_Profile*.csv $2/*INFILE_Subscriber_Balances*.csv $2/*INFILE_SUB_CUG_CLI*.csv

