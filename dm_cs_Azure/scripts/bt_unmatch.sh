mkdir -p err/batch_$1
if [[ $2 == "offer" ]] ; then
    awk -F"|" -v batchid=$1 'BEGIN{
        OFS=","
    }{
        if(FILENAME ~ /t_resource.lst/){
            if(FNR==1)
            {
                    bt_nm=$1
                    gsub(" ","_",bt_nm);
                    ofile="err/batch_"batchid"/unmatch_BT_" bt_nm ".err"
                    print "Error Filename:" ofile
            }
            arr_bt[$2]
            arr_offer[$3]
        }
        else if(FILENAME ~ /INC1003/){
            gsub(",","|",$0);
            gsub("=","|",$0);
            gsub(",","|",$0);
            disc_sub[$1]
        }
        else if(FILENAME ~ /Discarded/){
            gsub(":","|",$0);
            gsub("=","|",$0);
            disc_sub[$4]
        }
        else if(FILENAME ~ /Exception/){
            gsub(":","|",$0);
            gsub("=","|",$0);
            if(($6 in arr_bt) && ($1=="INC4001" || $1=="INC4002" || $1=="INC4003" || $1=="INC4004"  || $1=="INC4010")){
                buck[$4"|"$10]++
            }
        }
        else if(FILENAME ~ /Offer/){
            gsub(",","|",$0);
            if($2 in arr_offer){
                mig[$1]++
                cndr[$1]++
            }
        }
        else if(FILENAME ~ /Track/){
            gsub(":","|",$0);
            gsub("=","|",$0);
            if(($1=="INC7001") && ($8 in arr_offer)){
                mig[$4]--
                cndr[$4]--
            }
        }
        else{
            if($3 in arr_bt){
                    src[$1]++
                    if(!($1 in disc_sub) && !($1"|"$4 in buck) && !($1 in mig)){
                            print "MISSING",$0 > ofile
                    }
                    else
                    {
                        if($1 in disc_sub)
                        {
                            cndr[$1]++
                        }
                        if($1"|"$4 in buck)
                        {
                            cndr[$1]+=buck[$1"|"$4]
                        }
                    }
            }
        }
    }END{
        for(i in src){
            if(i in cndr)
            {
                if(src[i]!=cndr[i]){
                        print "COUNT_GAP",i,src[i],cndr[i] > ofile
                }

            }
        }
    }' t_resource.lst \
    logs/batch_$1/INC1003.log \
    logs/batch_$1/Discarded.log \
    logs/batch_$1/Exception.log \
    output/batch_$1/Offer.csv \
    logs/batch_$1/Track.log \
    input/batch_$1/*_subscriber_balances_dump*.csv
elif [[ $2 == "da" ]]; then
    awk -F"|" -v batchid=$1 'function ceil(x, y){
        y=int(x); 
        return(x>y?y+1:y)
    }
    function conv_da(type,val){
        if(type=="DA")
        {
            return sprintf("%0.4f",val/100)
        }
        else if(type=="DATO1")
        {
            return sprintf("%0.4f",((val*10000)*0.1667)/60)
        }
        else if(type=="DATO2")
        {
            return sprintf("%0.4f",val/1024)
        }
        else if(type=="DATO3")
        {
            return (val*10000)/1048576
        }
        else if(type=="DATO4")
        {
            return val*100
        }
        else if(type=="DATO5")
        {
            return val
        }
        else if(type=="DATO6")
        {
            return sprintf("%0.4f",val/100)
        }
        else if(type=="DA1")
        {
            return (500-val)*10000
        }
    }
    BEGIN{
        OFS=","
    }{
        if(FILENAME ~ /t_resource.lst/){
            if(FNR==1)
            {
                bt_nm=$1
                gsub(" ","_",bt_nm);
                ofile="err/batch_" batchid "/unmatch_BT_" bt_nm ".err"
                print "Error Filename:" ofile
            }
            arr_bt[$2]=$4
            arr_da[$3]=$4
        }
        else if(FILENAME ~ /INC1003/){
            gsub(",","|",$0);
            gsub("=","|",$0);
            gsub(",","|",$0);
            disc_sub[$1]
        }
        else if(FILENAME ~ /Discarded/){
            gsub(":","|",$0);
            gsub("=","|",$0);
            disc_sub[$4]
        }
        else if(FILENAME ~ /Exception/){
            gsub(":","|",$0);
            gsub("=","|",$0);
            if($6 in arr_bt){
                if($1=="INC4001" || $1=="INC4002" || $1=="INC4003" || $1=="INC4004"  || $1=="INC4010"){
                    buck[$4"|"$10]++
                }
                else if($1=="INC4005"){
                    if(arr_bt[$6] != "")
                    {
                        mig_bal[$4]+=$8
                        mig_da[$4]=mig_da[$4] $6"|"$10 ":" $8 ";"
                    }
                }
                else if($1=="INC4006"){
                    if(arr_bt[$6] != "")
                    {
                        inc4006[$4"|"$12]++
                        mig_bal[$4]+=$8
                        mig_da[$4]=mig_da[$4] $6"|"$10 ":" $8 ";"
                    }
                }
                else if($1=="INC4007"){
                    if(arr_bt[$6] != "")
                    {
                        mig_der_bal[$4]+=conv_da(arr_da[$12],$16)
                        mig_der_round_bal[$4]+=conv_da(arr_da[$12],ceil($12,0))
                    }
                }
            }
        }
        else if(FILENAME ~ /DedicatedAccount/){
            gsub(",","|",$0);

            if($3!=0){da_id[$1][1]=$3;da_val[$1][1]=$4;p_id[$1][1]=$34}else{delete da_id[$1][1]}
            if($6!=0){da_id[$1][2]=$6;da_val[$1][2]=$7;p_id[$1][2]=$35}else{delete da_id[$1][2]}
            if($9!=0){da_id[$1][3]=$9;da_val[$1][3]=$10;p_id[$1][3]=$36}else{delete da_id[$1][3]}
            if($12!=0){da_id[$1][4]=$12;da_val[$1][4]=$13;p_id[$1][4]=$37}else{delete da_id[$1][4]}
            if($15!=0){da_id[$1][5]=$15;da_val[$1][5]=$16;p_id[$1][5]=$40}else{delete da_id[$1][5]}
            if($18!=0){da_id[$1][6]=$18;da_val[$1][6]=$19;p_id[$1][6]=$42}else{delete da_id[$1][6]}
            if($21!=0){da_id[$1][7]=$21;da_val[$1][7]=$22;p_id[$1][7]=$44}else{delete da_id[$1][7]}
            if($24!=0){da_id[$1][8]=$24;da_val[$1][8]=$25;p_id[$1][8]=$46}else{delete da_id[$1][8]}
            if($27!=0){da_id[$1][9]=$27;da_val[$1][9]=$28;p_id[$1][9]=$48}else{delete da_id[$1][9]}
            if($30!=0){da_id[$1][10]=$30;da_val[$1][10]=$31;p_id[$1][10]=$39}else{delete da_id[$1][10]}

            for(i in da_id[$1])
            {
                if(da_id[$1][i] in arr_da){
                    mig[$1]++
                    cndr[$1]++
                    if(arr_da[da_id[$1][i]] != "")
                    {
                        if(inc4006[$1"|"da_id[$1][i]]>0 && da_val[$1][i]==0)
                        {
                            inc4006[$1"|"da_id[$1][i]]--
                        }
                        else
                        {
                            mig_bal[$1]+=conv_da(arr_da[da_id[$1][i]],da_val[$1][i])
                            mig_da[$1]=mig_da[$1] da_id[$1][i]"|"p_id[$1][i] ":" conv_da(arr_da[da_id[$1][i]],da_val[$1][i]) ";"
                        }
                    }
                }
            }
        }
        else if(FILENAME ~ /Track/){
            gsub(":","|",$0);
            gsub("=","|",$0);
            if(($8 in arr_da) && $1=="INC7002"){
                mig[$4]--
                cndr[$4]--
                if(arr_da[$8] != "")
                {
                    mig_bal[$1]-=conv_da(arr_da[$8],12)
                    gsub("$2\"|\"$5 \":\" conv_da(arr_da[$8],$4) \";\"","",mig_da[$1])
                }
            }
        }
        else{
            if($3 in arr_bt){
                src[$1]++
                if(!($1 in disc_sub) && !($1"|"$4 in buck) && !($1 in mig)){
                    print "MISSING",$0 > ofile
                }
                else
                {
                    if($1 in disc_sub)
                    {
                        cndr[$1]++
                        if(arr_bt[$3] != "")
                        {
                            mig_bal[$1]+=$6
                            mig_da[$1]=mig_da[$1] $3"|"$4 ":" $6 ";"    
                        }
                    }
                    if($1"|"$4 in buck)
                    {
                        cndr[$1]+=buck[$1"|"$4]
                        if(arr_bt[$3] != "")
                        {
                            mig_bal[$1]+=$6
                            mig_da[$1]=mig_da[$1] $3"|"$4 ":" $6 ";"    
                        }
                    }
                    if(arr_bt[$3] != "")
                    {
                        src_bal[$1]+=$6
                        src_da[$1]=src_da[$1] $3"|"$4 ":" $6 ";"
                    }
                }
            }
        }
    }END{
        for(i in src){
            if(i in cndr){
                if(src[i]!=cndr[i]){
                    print "COUNT_GAP",i,src[i],cndr[i] > ofile
                }
                else{
                    if(i in mig_bal){
                        if(i in mig_der_round_bal){
                            if(src_bal[i]!=mig_der_bal[i]){
                                print "BALANCE_GAP",i,src_bal[i]"[" src_da[i] "]",mig_bal[i]"[" mig_da[i] "]" > ofile
                            }
                            else if(mig_der_round_bal[i]!=mig_bal[i]){
                               print "BALANCE_ROUND_GAP",i,src_bal[i]"[" src_da[i] "]",mig_bal[i]"[" mig_da[i] "]" > ofile
                            }
                        }
                        else{
                            if(src_bal[i]!=mig_bal[i]){
                                print "BALANCE_GAP",i,src_bal[i]"[" src_da[i] "]",mig_bal[i]"[" mig_da[i] "]" > ofile
                            }
                        }
                    }
                }
            }
        }
    }' t_resource.lst \
    logs/batch_$1/INC1003.log \
    logs/batch_${1}/Discarded.log \
    logs/batch_${1}/Exception.log \
    output/batch_${1}/DedicatedAccount.csv \
    logs/batch_${1}/Track.log \
    input/batch_${1}/*_subscriber_balances_dump*
elif [[ $2 == "uc" ]]; then
    awk -F"|" -v batchid=$1 'function conv_uc(type,ut_val,val){
        if(type=="TOUC1")
        {
            return (ut_val-val)/1024
        }
        else if(type=="TOUC2")
        {
            return (ut_val-val)*100
        }
        else if(type=="TOUC3")
        {
            return ut_val-val
        }
        else if(type=="TOUC4")
        {
            return ut_val-((val+10000)/1048576)
        }
        else if(type=="UC")
        {
            return val*10000
        }
        else if(type=="UCTO1")
        {
            return val/100
        }
        else if(type=="UCTO2")
        {
            return val
        }
    }
    BEGIN{
        OFS=","
    }{
        if(FILENAME ~ /t_resource.lst/){
            if(FNR==1)
            {
                bt_nm=$1
                gsub(" ","_",bt_nm);
                ofile="err/batch_" batchid "/unmatch_BT_" bt_nm ".err"
                print "Error Filename:" ofile
            }
            arr_bt[$2]=$4
            arr_uc[$3]=$4
            arr_utval[$3]=$5
        }
        else if(FILENAME ~ /INC1003/){
            gsub(",","|",$0);
            gsub("=","|",$0);
            gsub(",","|",$0);
            disc_sub[$1]
        }
        else if(FILENAME ~ /Discarded/){
            gsub(":","|",$0);
            gsub("=","|",$0);
            disc_sub[$4]
        }
        else if(FILENAME ~ /Exception/){
            gsub(":","|",$0);
            gsub("=","|",$0);
            if($6 in arr_bt){
                if($1=="INC4001" || $1=="INC4002" || $1=="INC4003" || $1=="INC4004"  || $1=="INC4010"){
                    buck[$4"|"$10]++
                }
                else if($1=="INC4005"){
                    if(arr_bt[$6] != "")
                    {
                        inc4005[$4"|"$12]++
                        mig_bal[$4]+=$8
                        mig_uc[$4]=mig_uc[$4] $6"|"$10 ":" $8 ";"
                    }
                }
                else if($1=="INC4006"){
                    if(arr_bt[$6] != "")
                    {
                        inc4006[$4"|"$12]++
                        mig_bal[$4]+=$8
                        mig_uc[$4]=mig_uc[$4] $6"|"$10 ":" $8 ";"
                    }
                }
            }
        }
        else if(FILENAME ~ /UsageCounter/){
            gsub(",","|",$0);
            if($2 in arr_uc){
                mig[$1]++
                cndr[$1]++
                if(arr_uc[$2] != "")
                {
                    if(inc4006[$1"|"$2]>0 && $4==0)
                    {
                        inc4006[$1"|"$2]--
                    }
                    else
                    {
                        mig_bal[$1]+=conv_uc(arr_uc[$2],arr_utval[$2],$4)
                        mig_uc[$1]=mig_uc[$1] $2"|"$5 ":" conv_uc(arr_uc[$2],arr_utval[$2],$4) ";"      
                    }
                    
                }
            }
        }
        else if(FILENAME ~ /Track/){
            gsub(":","|",$0);
            gsub("=","|",$0);
            if(($8 in arr_uc) && $1=="INC7003"){
                mig[$4]--
                cndr[$4]--
                if(arr_uc[$8] != "")
                {
                    mig_bal[$1]-=conv_uc(arr_uc[$8],arr_utval[$8],$12)
                    gsub("$2\"|\"$5 \":\" conv_uc(arr_uc[$8],arr_utval[$8],$4) \";\"","",mig_uc[$1])
                }
            }
        }
        else{
            if($3 in arr_bt){
                src[$1]++
                if(!($1 in disc_sub) && !($1"|"$4 in buck) && !($1 in mig)){
                    print "MISSING",$0 > ofile
                }
                else
                {
                    if($1 in disc_sub)
                    {
                        cndr[$1]++
                        if(arr_bt[$3]!="")
                        {
                            mig_bal[$1]+=$6
                            mig_uc[$1]=mig_uc[$1] $3"|"$4 ":" $6 ";"    
                        }
                    }
                    if($1"|"$4 in buck)
                    {
                        cndr[$1]+=buck[$1"|"$4]
                        if(arr_bt[$3]!="")
                        {
                            mig_bal[$1]+=$6
                            mig_uc[$1]=mig_uc[$1] $3"|"$4 ":" $6 ";"    
                        }
                    }
                    if(arr_bt[$3]!="")
                    {
                        src_bal[$1]+=$6
                        src_uc[$1]=src_uc[$1] $3"|"$4 ":" $6 ";"    
                    }
                }
            }
        }
    }END{
        for(i in src){
            if(i in cndr)
            {
                if(src[i]!=cndr[i]){
                    print "COUNT_GAP",i,src[i],cndr[i] > ofile
                }
                else if(i in src_bal){
                    if(src_bal[i]!=mig_bal[i]){
                        print "BALANCE_GAP",i,src_bal[i]"[" src_uc[i] "]",mig_bal[i]"[" mig_uc[i] "]" > ofile
                    }
                }

            }
        }
    }' t_resource.lst \
    logs/batch_$1/INC1003.log \
    logs/batch_${1}/Discarded.log \
    logs/batch_${1}/Exception.log \
    output/batch_${1}/UsageCounter.csv \
    logs/batch_${1}/Track.log \
    input/batch_${1}/*_subscriber_balances_dump*
elif [[ $2 == "ua" ]]; then
    awk -F"|" -v batchid=$1 'function conv_ua(type,val){
            if(type=="UA")
            {
                return val
            }
        }
        BEGIN{
            OFS=","
        }{
        if(FILENAME ~ /t_resource.lst/){
            if(FNR==1)
            {
                bt_nm=$1
                gsub(" ","_",bt_nm);
                ofile="err/batch_" batchid "/unmatch_BT_" bt_nm ".err"
                print "Error Filename:" ofile
            }
            arr_bt[$2]=$4
            arr_ua[$3]=$4
        }
        else if(FILENAME ~ /INC1003/){
            gsub(",","|",$0);
            gsub("=","|",$0);
            gsub(",","|",$0);
            disc_sub[$1]
        }
        else if(FILENAME ~ /Discarded/){
            gsub(":","|",$0);
            gsub("=","|",$0);
            disc_sub[$4]
        }
        else if(FILENAME ~ /Exception/){
            gsub(":","|",$0);
            gsub("=","|",$0);
            if($6 in arr_bt){
                if($1=="INC4001" || $1=="INC4002" || $1=="INC4003" || $1=="INC4004"  || $1=="INC4010"){
                    buck[$4"|"$10]++
                }
                else if($1=="INC4005"){
                    if(arr_bt[$6] != "")
                    {
                        mig_bal[$4]+=$8
                        mig_ua[$4]=mig_ua[$4] $6"|"$10 ":" $8 ";"
                    }
                }
                else if($1=="INC4006"){
                    if(arr_bt[$6] != "")
                    {
                        inc4006[$4"|"$12]++
                        mig_bal[$4]+=$8
                        mig_ua[$4]=mig_ua[$4] $6"|"$10 ":" $8 ";"
                    }
                }
            }    
        }
        else if(FILENAME ~ /Accumulator/){
            gsub(",","|",$0);
            if($3!=0){ua_id[$1][1]=$3;ua_val[$1][1]=$4}else{delete ua_id[$1][1]}
            if($6!=0){ua_id[$1][2]=$6;ua_val[$1][2]=$7}else{delete ua_id[$1][2]}
            if($9!=0){ua_id[$1][3]=$9;ua_val[$1][3]=$10}else{delete ua_id[$1][3]}
            if($12!=0){ua_id[$1][4]=$12;ua_val[$1][4]=$13}else{delete ua_id[$1][4]}
            if($15!=0){ua_id[$1][5]=$15;ua_val[$1][5]=$16}else{delete ua_id[$1][5]}
            if($18!=0){ua_id[$1][6]=$18;ua_val[$1][6]=$19}else{delete ua_id[$1][6]}
            if($21!=0){ua_id[$1][7]=$21;ua_val[$1][7]=$22}else{delete ua_id[$1][7]}
            if($24!=0){ua_id[$1][8]=$24;ua_val[$1][8]=$25}else{delete ua_id[$1][8]}
            if($27!=0){ua_id[$1][9]=$27;ua_val[$1][9]=$28}else{delete ua_id[$1][9]}
            if($30!=0){ua_id[$1][10]=$30;ua_val[$1][10]=$31}else{delete ua_id[$1][10]}

            for(i in ua_id[$1])
            {
                if(ua_id[$1][i] in arr_ua){
                    mig[$1]++
                    cndr[$1]++
                    if(arr_ua[ua_id[$1][i]] != "")
                    {
                        if(inc4006[$1"|"ua_id[$1][i]]>0 && ua_val[$1][i]==0)
                        {
                            inc4006[$1"|"ua_id[$1][i]]--
                        }
                        else
                        {
                            mig_bal[$1]+=conv_ua(arr_ua[ua_id[$1][i]],ua_val[$1][i])
                            mig_ua[$1]=mig_ua[$1] ua_id[$1][i] ":" conv_ua(arr_ua[ua_id[$1][i]],ua_val[$1][i]) ";"
                        }
                    }
                }
            }
        }
        else{
            if($3 in arr_bt){
                src[$1]++
                if(!($1 in disc_sub) && !($1"|"$4 in buck) && !($1 in mig)){
                    print "MISSING",$0 > ofile
                }
                else
                {
                    if($1 in disc_sub)
                    {
                        cndr[$1]++
                    }
                    if($1"|"$4 in buck)
                    {
                        cndr[$1]+=buck[$1"|"$4]
                    }
                }
            }
        }
    }END{
        for(i in src){
            if(i in cndr)
            {
                if(src[i]!=cndr[i]){
                    print "COUNT_GAP",i,src[i],cndr[i] > ofile
                }
            }
        }
    }' t_resource.lst \
       logs/batch_$1/INC1003.log \
       logs/batch_${1}/Discarded.log \
       logs/batch_${1}/Exception.log \
       output/batch_${1}/Accumulator.csv \
       input/batch_${1}/*_subscriber_balances_dump*.csv
elif [[ $2 == "attr" ]]; then
    awk -F"|" -v batchid=$1 'BEGIN{
            OFS=","
        }{
            if(FILENAME ~ /t_resource.lst/){
                if(FNR==1)
                {
                    bt_nm=$1
                    gsub(" ","_",bt_nm);
                    ofile="err/batch_" batchid "/unmatch_BT_" bt_nm ".err"
                    print "Error Filename:" ofile
                }
                arr_bt[$2]
                arr_attr[$3]
            }
            else if(FILENAME ~ /INC1003/){
                gsub(",","|",$0);
                gsub("=","|",$0);
                gsub(",","|",$0);
                disc_sub[$1]
            }
            else if(FILENAME ~ /Discarded/){
                gsub(":","|",$0);
                gsub("=","|",$0);
                disc_sub[$4]
            }
            else if(FILENAME ~ /Exception/){
                gsub(":","|",$0);
                gsub("=","|",$0);
                if(($6 in arr_bt) && ($1=="INC4001" || $1=="INC4002" || $1=="INC4003" || $1=="INC4004"  || $1=="INC4010")){
                    buck[$4"|"$10]++
                }
            }
            else if(FILENAME ~ /offer_attributes/){
                gsub(",","|",$0);
                if($2"-"$3 in arr_attr){
                    mig[$1]++
                    cndr[$1]++
                }
            }
            else{
                if($3 in arr_bt){
                    src[$1]++
                    if(!($1 in disc_sub) && !($1"|"$4 in buck) && !($1 in mig)){
                        print "MISSING",$0 > ofile
                    }
                    else
                    {
                        if($1 in disc_sub)
                        {
                            cndr[$1]++
                        }
                        if($1"|"$4 in buck)
                        {
                            cndr[$1]+=buck[$1"|"$4]
                        }
                    }
                }
            }
        }END{
            for(i in src){
                if(i in cndr)
                {
                        if(src[i]!=cndr[i]){
                        print "COUNT_GAP",i,src[i],cndr[i] > ofile
                    }

                }
            }
        }' t_resource.lst \
        logs/batch_$1/INC1003.log \
        logs/batch_${1}/Discarded.log \
        logs/batch_${1}/Exception.log \
        snapshot/batch_${1}/*offer_attributes* \
        input/batch_${1}/*_subscriber_balances_dump*
fi

