awk -F"|" 'function basename(file){
            sub(".*/", "", file)
            return file
	}BEGIN{OFS="|"}{
	if(FILENAME ~ /BT_pending.lst/){
		pending[$1]
	}
	else if(FILENAME ~ /BALANCE_MAP.txt/){
		if($3=="P")
		{
			pbt[$2]
		}
	}
	else if(FILENAME ~ /Exception/){
		gsub(":","|",$0);
		gsub("=","|",$0);
		if($1=="INC4002"){
			inc4002[$4"|"$10]
		}
		else if($1=="INC4004")
		{
			inc4004[$4"|"$10]
		}
		
	}
	else{
		if($1"|"$4 in inc4004)
		{
			if($3 in pbt)
			{
				print $0 > "logs/inc4004_missing_vbt_pc_recs.txt"
				inc4004_missing_vbt_pc[$5"|"$3]++
			}
			else
			{
				inc4004_bt[$5"|"$3]++
				print $0 > "logs/inc4004_bt_recs.txt"
			}
		}
		else if($1"|"$4 in inc4002)
		{
			if($3 in pending)
			{
				print $0 > "logs/inc4002_pending_bt_recs.txt"
				inc4002_pending_bt[$5"|"$3]++
			}
			else if(tolower($5) ~  /_pc_/)
			{
				if($6>=3)
				{
					print $0 > "logs/inc4002_active_pc_recs.txt"
					inc4002_active_pc[$5"|"$3]++
				}
				else
				{
					inc4002_not_active_pc[$5"|"$3]++
					print $0 > "logs/inc4002_not_active_pc_recs.txt"
				}
			}
			else
			{
				inc4002_bt[$5"|"$3]++
				print $0 > "logs/inc4002_bt_recs.txt"
			}
		}
		
	}
	if(FNR%100000==0)
    {
        print "["basename(FILENAME)"] record processed : "FNR
    }

    if(BAK_FILENAME != "" && BAK_FILENAME != FILENAME)
    {
    	print "["basename(BAK_FILENAME)"] record processed : "BAK_FNR
    }
    BAK_FILENAME=FILENAME
    BAK_FNR=FNR
}END{
		for(i in inc4002_active_pc){
			print i"|"inc4002_active_pc[i] > "logs/inc4002_active_pc_list.txt"
		}
		for(j in inc4002_not_active_pc){
			print j"|"inc4002_not_active_pc[j] > "logs/inc4002_not_active_pc.txt"
		}
		for(k in inc4002_bt){
			print k"|"inc4002_bt[k] > "logs/inc4002_bt.txt"
		}
		for(l in inc4002_pending_bt){
			print l"|"inc4002_pending_bt[l] > "logs/inc4002_pending_bt.txt"
		}
		for(m in inc4004_missing_vbt_pc){
			print m"|"inc4004_missing_vbt_pc[m] > "logs/inc4004_missing_vbt_pc.txt"
		}
		for(n in inc4004_bt){
			print n"|"inc4004_bt[n] > "logs/inc4004_bt.txt"
		}
	}' BT_pending.lst config/BALANCE_MAP.txt logs/batch_*/Exception.log input/batch_*/*balance*
