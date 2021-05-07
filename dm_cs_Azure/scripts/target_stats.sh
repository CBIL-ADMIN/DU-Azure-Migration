#!/bin/ksh
hostnm=`hostname`

awk -F"|" -v curr_date=$1 -v o_sna_file=$2"/report/subscriber/"${hostnm}"_target_sna_stats.txt" -v o_sc_file=$2"/report/subscriber/"${hostnm}"_target_sc_stats.txt" -v o_offer_file=$2"/report/subscriber/"${hostnm}"_target_offer_stats.txt" -v o_state_file=$2"/report/subscriber/"${hostnm}"_target_state_stats.txt" -v o_sc_state_file=$2"/report/subscriber/"${hostnm}"_target_sc_state_stats.txt" -v o_cug_file=$2"/report/subscriber/"${hostnm}"_target_cug_stats.txt" -v o_da_file=$2"/report/subscriber/"${hostnm}"_target_da_stats.txt" -v o_uc_file=$2"/report/subscriber/"${hostnm}"_target_uc_stats.txt" -v o_accm_file=$2"/report/subscriber/"${hostnm}"_target_accm_stats.txt" -v o_faf_file=$2"/report/subscriber/"${hostnm}"_target_faf_stats.txt" -v o_attr_file=$2"/report/subscriber/"${hostnm}"_target_attr_stats.txt" -v o_cis_file=$2"/report/subscriber/"${hostnm}"_target_cis_stats.txt" 'function basename(file){
            sub(".*/", "", file)
            return file
	}
	function get_timestamp(date_var){
		timestamp=mktime(substr(date_var,1,4)" "substr(date_var,6,2)" "substr(date_var,9,2)" 00 00 00");
		return timestamp;
	}
	BEGIN{
		OFS=",";
		sna_sc[1451];
		sna_sc[1452];
		sna_sc[1453];
		sna_sc[1454];
		curr_date=substr(curr_date,1,4) "-" substr(curr_date,5,2) "-" substr(curr_date,7,8)
	}{
	if(FILENAME ~ /Track.log/)
	{
		gsub("=","|", $0)
		gsub(":","|", $0)
		if($1=="INC7001")
		{
			track_master[$8]++
		}
		else if($1=="INC7002")
		{
			track_add_da_cnt[$10]++
			track_add_da_val[$10]+=$12
		}
		else if($1=="INC7003")
		{
			if($6!=92 && $6!=239 && $6 != 259)
			{
				track_add_uc_cnt[$10]++
				track_add_uc_val[$10]+=$12
			}
		}
	}
	else if(FILENAME ~ /Service_Number_Application./)
	{
		gsub(",","|", $0)
		sna_cnt[substr($2,1,6)"-"$5]++
		if($4=="Active")
		{
			state_cnt["A"]++
		}
		else if($4=="Deactive")
		{
			state_cnt["T"]++
		}
	}
	else if(FILENAME ~ /CIS_OnceOff_Bundles./)
	{
		cis_cnt["OnceOff"][$3]++
	}
	else if(FILENAME ~ /CIS_Renewal_Bundles./)
	{
		cis_cnt["Renewal"][$12]++
	}
	else if(FILENAME ~ /CIS_Parking_Products./)
	{
		cis_cnt["Parking"][$3]++
	}
	else if(FILENAME ~ /DUMP_offer_attributes_account./)
	{
		gsub(",","|", $0)
		attr_cnt[$2][$3]++
	}
	else if(FILENAME ~ /DUMP_offer./)
	{
		gsub(",","|", $0)
		offer_cnt[$2]++;
		if($2==552)
		{
			msi_552[$1]
		}
		else if($2==553)
		{
			msi_553[$1]
		}
		else if($2==554)
		{
			msi_554[$1]
		}
		
	}
	else if(FILENAME ~ /DUMP_subscriber./)
	{
		gsub(",","|", $0)
		sc_cnt[$19]++;
		if($41 != 0 && $41 != "")
		{
			cug_cnt[$41]++
		}
		if($42 != 0 && $42 != "")
		{
			cug_cnt[$42]++
		}
		if($43 != 0 && $43 != "")
		{
			cug_cnt[$43]++
		}

		if($3==1)
		{
			state_cnt["S"]++
			sc_state_cnt[$19"-S"]++
		}
		else
		{
			if($44=="")
			{
				state_cnt["P"]++
				sc_state_cnt[$19"-P"]++
			}
			else if($1 in msi_552)
			{
				state_cnt["F"]++
				sc_state_cnt[$19"-F"]++
			}
			else if($1 in msi_553)
			{
				state_cnt["S"]++
				sc_state_cnt[$19"-S"]++
			}
			else if($1 in msi_554)
			{
				state_cnt["D"]++
				sc_state_cnt[$19"-D"]++
			}
			else
			{
				if(get_timestamp($26) > get_timestamp(curr_date))
				{
					state_cnt["A"]++
					sc_state_cnt[$19"-A"]++
				}
				else
				{
					state_cnt["T"]++
					sc_state_cnt[$19"-T"]++
				}
			}
		}
	}
	else if(FILENAME ~ /DedicatedAccount/)
	{
		gsub(",","|", $0)
        if($3!=0){da_cnt[$3]++;if($3 in inc4007){da_val[$3]+=inc4007[$3]}else{da_val[$3]+=$4}}
        if($6!=0){da_cnt[$6]++;if($3 in inc4007){da_val[$6]+=inc4007[$6]}else{da_val[$6]+=$7}}
        if($9!=0){da_cnt[$9]++;if($3 in inc4007){da_val[$9]+=inc4007[$9]}else{da_val[$9]+=$10}}
        if($12!=0){da_cnt[$12]++;if($3 in inc4007){da_val[$12]+=inc4007[$12]}else{da_val[$12]+=$13}}
        if($15!=0){da_cnt[$15]++;if($3 in inc4007){da_val[$15]+=inc4007[$15]}else{da_val[$15]+=$16}}
        if($18!=0){da_cnt[$18]++;if($3 in inc4007){da_val[$18]+=inc4007[$18]}else{da_val[$18]+=$19}}
        if($21!=0){da_cnt[$21]++;if($3 in inc4007){da_val[$21]+=inc4007[$21]}else{da_val[$21]+=$22}}
        if($24!=0){da_cnt[$24]++;if($3 in inc4007){da_val[$24]+=inc4007[$24]}else{da_val[$24]+=$25}}
        if($27!=0){da_cnt[$27]++;if($3 in inc4007){da_val[$27]+=inc4007[$27]}else{da_val[$27]+=$28}}
        if($30!=0){da_cnt[$30]++;if($3 in inc4007){da_val[$30]+=inc4007[$30]}else{da_val[$30]+=$31}}
	}
	else if(FILENAME ~ /UsageCounter./)
	{
		gsub(",","|", $0)
		uc_cnt[$2]++;
		if($2 in inc4007)
		{
			uc_val[$2]+=inc4007[$2];
		}
		else
		{
			uc_val[$2]+=$4;
		}
	}
	else if(FILENAME ~ /DUMP_accumulator./)
	{
		gsub(",","|", $0)
		accm_cnt[$2]++;
		if($2 in inc4007)
		{
			accm_val[$2]+=inc4007[$2];
		}
		else
		{
			accm_val[$2]+=$3;
		}
		
	}
	else if(FILENAME ~ /DUMP_faflistSub./)
	{
		gsub(",","|", $0)
		faf_cnt[$3]++;
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
	print "Generating stats for Target State..."
	header=0
	for(i in state_cnt)
	{
		header++
		if(header==1)
		{
			print "STATE,TARGET_CNT" > o_state_file
		}
		printf("%s,%d\n",i,state_cnt[i]) > o_state_file
	}

	print "Generating stats for Target ServiceClass..."
	header=0
	for(i in sc_cnt)
	{
		header++
		if(header==1)
		{
			print "SERVICE_CLASS,TARGET_CNT" > o_sc_file
		}
		printf("%d,%d\n",i,sc_cnt[i]) > o_sc_file
	}

	print "Generating stats for Target ServiceClass-State..."
	header=0
	for(i in sc_state_cnt)
	{
		header++
		if(header==1)
		{
			print "SERVICE_CLASS-STATE,TARGET_CNT" > o_sc_state_file
		}
		printf("%s,%d\n",i,sc_state_cnt[i]) > o_sc_state_file
	}

	print "Generating stats for Target Offer..."
	header=0
	for(i in offer_cnt)
	{
		header++
		if(header==1)
		{
			print "OFFER_ID,TARGET_CNT,MASTER_MAPPED_CNT" > o_offer_file
		}
		printf("%d,%d,%d\n",i,offer_cnt[i],track_master[i]) > o_offer_file
	}

	print "Generating stats for Target CUG..."
	header=0
	for(i in cug_cnt)
	{
		header++
		if(header==1)
		{
			print "CUG_ID,TARGET_CUG_CNT" > o_cug_file
		}
		printf("%d,%d\n",i,cug_cnt[i]) > o_cug_file
	}

	print "Generating stats for Target SNASeries-ServiceClass..."
	header=0
	for(i in sna_cnt)
	{
		header++
		if(header==1)
		{
			print "SNASeries-RatePlanName,TARGET_SNA_COUNT" > o_sna_file
		}
		printf("%s,%d\n",i,sna_cnt[i]) > o_sna_file
	}

	print "Generating stats for Target DA..."
	header=0
	for(i in da_cnt)
	{
		header++
		if(header==1)
		{
			print "DA_ID,TARGET_CNT,TARGET_BALANCE_TOTAL,ADD_DA_CNT,ADD_DA_BALANCE_TOTAL" > o_da_file
		}
		printf("%d,%d,%.6f,%d,%.6f\n",i,da_cnt[i],da_val[i],track_add_da_cnt[i],track_add_da_val[i]) > o_da_file
	}

	print "Generating stats for Target UsageCounter..."
	header=0
	for(i in uc_cnt)
	{
		header++
		if(header==1)
		{
			print "UC_ID,TARGET_CNT,TARGET_VALUE_TOTAL,ADD_UC_CNT,ADD_UC_VALUE_TOTAL" > o_uc_file
		}
		printf("%d,%d,%d,%d,%d\n",i,uc_cnt[i],uc_val[i],track_add_uc_cnt[i],track_add_uc_val[i]) > o_uc_file
	}

	print "Generating stats for Target Accumulator..."
	header=0
	for(i in accm_cnt)
	{
		header++
		if(header==1)
		{
			print "ACCM_ID,TARGET_CNT,TARGET_VALUE_TOTAL" > o_accm_file
		}
		printf("%d,%d,%d\n",i,accm_cnt[i],accm_val[i]) > o_accm_file
	}

	print "Generating stats for Target FaF..."
	header=0
	for(i in faf_cnt)
	{
		header++
		if(header==1)
		{
			print "FAF_INDICATOR,TARGET_COUNT" > o_faf_file
		}
		printf("%d,%d\n",i,faf_cnt[i]) > o_faf_file
	}

	print "Generating stats for Target Attributes..."
	header=0
	for(i in attr_cnt)
	{
		for(j in attr_cnt[i])
		{
			header++
			if(header==1)
			{
				print "OFFERID-ATTRNAME,TARGET_COUNT" > o_attr_file
			}
			printf("%s,%d\n",i"-"j,attr_cnt[i][j]) > o_attr_file
		}
	}

	print "Generating stats for Target CIS..."
	header=0
	for(i in cis_cnt)
	{
		for(j in cis_cnt[i])
		{
			header++
			if(header==1)
			{
				print "BUNDLE_TYPE","PRODUCTID,TARGET_COUNT" > o_cis_file
			}
			printf("%s,%d,%d\n",i,j,cis_cnt[i][j]) > o_cis_file
		}
	}
}' $2/logs/Track.log \
$2/output/Service_Number_Application.csv \
$2/output/CIS_OnceOff_Bundles.csv \
$2/output/CIS_Renewal_Bundles.csv \
$2/output/CIS_Parking_Products.csv \
$2/snapshot/*DUMP_offer_attributes_account.v3.csv \
$2/snapshot/*DUMP_offer.v3.csv \
$2/snapshot/*DUMP_subscriber.v3.csv \
$2/output/*DedicatedAccount.csv \
$2/output/*UsageCounter.csv \
$2/snapshot/*DUMP_accumulator.v3.csv \
$2/snapshot/*DUMP_faflistSub.v3.csv

touch $2/report/subscriber/${hostnm}_target_state_stats.txt
touch $2/report/subscriber/${hostnm}_target_sc_stats.txt
touch $2/report/subscriber/${hostnm}_target_sc_state_stats.txt
touch $2/report/subscriber/${hostnm}_target_offer_stats.txt
touch $2/report/subscriber/${hostnm}_target_attr_stats.txt 
touch $2/report/subscriber/${hostnm}_target_da_stats.txt 
touch $2/report/subscriber/${hostnm}_target_uc_stats.txt 
touch $2/report/subscriber/${hostnm}_target_accm_stats.txt
touch $2/report/subscriber/${hostnm}_target_cug_stats.txt 
touch $2/report/subscriber/${hostnm}_target_faf_stats.txt 
touch $2/report/subscriber/${hostnm}_target_sna_stats.txt 
touch $2/report/subscriber/${hostnm}_target_cis_stats.txt
