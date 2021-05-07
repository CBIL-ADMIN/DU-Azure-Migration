#!/bin/ksh
gunzip $2/input/*_subscriber_be_dump.csv.gz 
gunzip $2/input/*_subscriber_balances_dump*.csv.gz 
gunzip $2/input/*_subscriber_cugcli_dump.csv.gz
gunzip $2/input/*_subscriber_profile_dump.csv.gz
# gunzip $2/logs/Discarded.log_*.gz
# gunzip $2/logs/Exception.log_*.gz
# gunzip $2/logs/Track.log_*.gz

hostnm=`hostname`

awk -F"|" -v MIGDATE=$1 -v hostnm=$hostnm -v migtool_dir=$2 -v o_state_file=$2"/report/subscriber/"${hostnm}"_source_state_stats.txt" -v o_rateplan_file=$2"/report/subscriber/"${hostnm}"_source_rateplan_stats.txt" -v o_rateplan_state_file=$2"/report/subscriber/"${hostnm}"_source_rateplan_state_stats.txt" -v o_bal_file=$2"/report/subscriber/"${hostnm}"_source_bal_stats.txt" -v o_pt_file=$2"/report/subscriber/"${hostnm}"_source_profiletag_stats.txt" -v o_cug_file=$2"/report/subscriber/"${hostnm}"_source_cug_stats.txt" -v o_faf_file=$2"/report/subscriber/"${hostnm}"_source_faf_stats.txt" -v o_sna_file=$2"/report/subscriber/"${hostnm}"_source_sna_stats.txt" -v o_cis_file=$2"/report/subscriber/"${hostnm}"_source_cis_stats.txt" 'function basename(file){
            sub(".*/", "", file)
            return file
	}
function conv_ut(type,ut_val)
{
    if(type=="TOUC1")
    {
        return ut_val/1024
    }
    else if(type=="TOUC2")
    {
        return ut_val*100
    }
    else if(type=="TOUC3")
    {
        return ut_val
    }
}
function func_bt_pt_match(name,sym,val,src_val)
{
	bt_pt_match=0
	if(sym=="")
	{
		bt_pt_match=1
	}
	if(sym=="<")
	{
		if(toupper(val) == "MIGDATE")
		{
			if(src_val < MIGDATE)
			{
				bt_pt_match=1
			}
		}
		else if(src_val < val)
		{
			bt_pt_match=1
		}
	}
	else if(sym==">")
	{
		if(toupper(val) == "MIGDATE")
		{
			if(src_val > MIGDATE)
			{
				bt_pt_match=1
			}
		}
		else if(src_val > val)
		{
			bt_pt_match=1
		}
	}
	else if(sym=="=")
	{
		if(toupper(val)=="EMPTY")
		{
			if(src_val=="" || src_val=="#")
			{
				bt_pt_match=1
			}
		}
		else if(toupper(val)!="EMPTY")
		{
			if(toupper(val) == "MIGDATE")
			{
				if(src_val == MIGDATE)
				{
					bt_pt_match=1
				}
			}
			else if(src_val == val)
			{
				bt_pt_match=1
			}
		}
	}
	else if(sym=="!=")
	{
		if(toupper(val)=="EMPTY")
		{
			if(src_val!="" && src_val!="#")
			{
				bt_pt_match=1
			}
		}
		else if(toupper(val)!="EMPTY")
		{
			if(toupper(val) == "MIGDATE")
			{
				if(src_val != MIGDATE)
				{
					bt_pt_match=1
				}
			}
			else if(src_val != val)
			{
				bt_pt_match=1
			}
		}
	}
	else if(sym==">=")
	{
		if(toupper(val) == "MIGDATE")
		{
			if(src_val >= MIGDATE)
			{
				bt_pt_match=1
			}
		}
		else if(src_val >= val)
		{
			bt_pt_match=1
		}
	}
	else if(sym=="<=")
	{
		if(toupper(val) == "MIGDATE")
		{
			if(src_val <= MIGDATE)
			{
				bt_pt_match=1
			}
		}
		else if(src_val <= val)
		{
			bt_pt_match=1
		}
	}
	else if(sym=="or")
	{
		split(val,or_ele,"#")
		for(k in or_ele)
		{
			if(src_val == or_ele[k])
			{
				bt_pt_match=1
				break;
			}
		}
	}
	return bt_pt_match;
}
function func_st_match(val,src_val)
{
	st_match=0
	if(val=="")
	{
		st_match=1
	}
	else if(val==src_val)
	{
		st_match=1
	}
	return st_match;
}
function func_rp_match(sym,val,src_val)
{
	rp_match=0
	if(sym=="")
	{
		rp_match=1
	}
	else if(sym=="=" && src_val == val)
	{
		rp_match=1
	}
	else if(sym=="!=" && src_val != val)
	{
		rp_match=1
	}
	else if(sym=="or")
	{
		split(val,or_ele,"#")
		for(x in or_ele)
		{
			if(src_val == or_ele[x])
			{
				rp_match=1
				break;
			}
		}
	}
	return rp_match;
}BEGIN{
	OFS=",";
	discard_bt_logtype["INC4001"]
	discard_bt_logtype["INC4002"]
	discard_bt_logtype["INC4003"]
	discard_bt_logtype["INC4004"]
	discard_bt_logtype["INC4005"]
	discard_bt_logtype["INC4010"]
	discard_bt_logtype["INC4011"]
	
	#discard_pt_logtype["INC6001"]
	discard_pt_logtype["INC6002"]
	discard_pt_logtype["INC6003"]
	discard_pt_logtype["INC6004"]
	sna_rp[374];
	sna_rp[275];
	sna_rp[582];
	sna_rp[497];

}{
	if(FILENAME ~ /BALANCE_MAP/)
	{
		if(FNR!=1)
		{
			if(($2 in bt_map) && ($4":"$5":"$7":"$8":;"$9 in bt_map) && bt_map[$2][$4":"$5":"$7":"$8":;"$9]=="")
			{
				if($36!="")
				{
					bt_map[$2][$4":"$5":"$7":"$8":;"$9]=$36
				}
			}
			else
			{
				bt_map[$2][$4":"$5":"$7":"$8":;"$9]=$36
			}
			
			if($36!="" && ($10=="M" || ($10 ~ /F-/ && $10 ~ /-M/) || $10 ~ /A-M/))
			{
				cis_master_excep[$2]
			}
			split($9,bt_pt_rec,"#")
			for(k in bt_pt_rec)
			{
				split(bt_pt_rec[k],bt_pt_ele,"-")
				bt_pt_list[bt_pt_ele[1]]
			}
			if($32!="")
			{
				ut_val[$30]=conv_ut($11,$32)
			}	
		}
	}
	else if(FILENAME ~ /PROFILETAG_MAP/)
	{
		if(FNR!=1)
        {
        	pt_map[$1][$2":"$3":"$5":"$6":"$7":;"$8]=$26
			pt_grp[$1][$2":"$3":"$5":"$6":"$7":;"$8]=$4
			if($4=="DEFAULT")
			{
				pt_grptype[$1]["DEFAULT"]
			}
			else
			{
				pt_grptype[$1]["NORMAL"]
			}
        }
	}
	else if(FILENAME ~ /PROFILETAG_DICT/)
	{
		if(FNR!=1)
		{
			pt_tagpos[$1]=$4
			pt_tagname[$4]=$1
		}
		
	}
	else if(FILENAME ~ /INC1003.log/)
	{
		gsub("=","|", $0)
		gsub(":","|", $0)

		disc_subs[$4]=$2
		arr_inc1003[$4]
	}
	else if(FILENAME ~ /Discarded/)
	{
		gsub("=","|", $0)
		gsub(":","|", $0)
		if($1 !~ /INC900/)
		{
			if($1 !~ /INC1002/ || ($1 ~ /INC1002/ && !($6 in sna_rp)))
			{
				disc_subs[$4]=$2
				if($1=="INC1001")
				{
					arr_inc1001[$4]
				}
				else if($1=="INC1002")
				{
					arr_inc1002[$4]
				}
				else if($1=="INC1003")
				{
					arr_inc1003[$4]
				}
				else if($1=="INC1004")
				{
					arr_inc1004[$4]
				}
			}
		}
		else if($1 ~ /INC90/)
		{
			disc_subs[$4]=$2
		} 
	}
	else if(FILENAME ~ /Exception/)
	{
		gsub("=","|", $0)
		gsub(":","|", $0)

		if($1 in discard_bt_logtype)
		{
			if(!($4 in disc_subs))
			{
				disc_bt_reason[$2]++
				disc_bt_bal_cnt[$6]++
				if($8>0)
				{
					disc_bt_bal_val[$6]+=$8
				}
				disc_bt_msg[$6][$2]++
				disc_bt_bucket[$4][$10]=$2
			}
		}
		else if($1 in discard_pt_logtype)
		{
			disc_pt_reason[$2]++
			disc_pt[$4][$6]=$2
			disc_pt_cnt[$6]++
			disc_pt_msg[$6][$2]++
		}
		else if($1=="INC5001")
		{
			disc_cug_reason[$2]++
			disc_cug_cnt[$6]++
			disc_cug_msg[$6][$2]++
			unmatch_cugid[$6]++
		}
		else if($1=="INC4006" || $1=="INC4007")
		{
			disc_cis_bt[$4][$10]=$2
		}
		else if($1=="INC6005")
		{
			disc_cis_pt[$4][$6]=$2
		}
		else if($1=="INC3001")
		{
			disc_pt_reason[$2]++
			disc_pt_def[$4][$6]
			disc_pt_cnt[$6]++
			disc_pt_msg[$6][$2]++
		}
	}
	else if(FILENAME ~ /Track/)
	{
		gsub("=","|", $0)
		gsub(":","|", $0)
		if($1=="INC7001")
		{
			track_extra[$6][$8]++
			if($6!=2924)
			{
				master_map_bt[$4"|"$10]
			}
		}
		else if($1=="INC7004")
		{
			track_extra[$6][$10]++	
		}
		else if($1=="INC7005")
		{
			track_extra[$6][$8]++	
		}
		else if($1=="INC7006")
		{
			disc_bt_bal_negative_val[$6]+=$8
		}
		else if($1=="INC7007")
		{
			inc7007[$4"|"$10]=$12
		}
		else if($1=="INC7010")
		{
			AM_map_bt[$1"|"$8]
		}
	}
	else if(FILENAME ~ /subscriber_be_dump/)
	{
		if($3 in sna_rp)
		{
			sna_msi[$1]
			sna_cnt[substr($1,1,6)"-"$3]++
			if($1 in disc_subs)
			{
				disc_sna_cnt[substr($1,1,6)"-"$3]++
				disc_sna_msg[substr($1,1,6)"-"$3][disc_subs[$1]]++
				disc_sna_reason[disc_subs[$1]]++
			}
		}
		else
		{
			rateplan_cnt[$3]++;
			rateplan_state_cnt[$3"-"$4]++
		}
		state_cnt[$4]++;
		rateplan_name[$3]=$2;
		be_rp[$1]=$3
		sub_state[$1]=$4

		if($1 in disc_subs)
		{
			disc_reason[disc_subs[$1]]++
			disc_subs_state_cnt[$4]++
			disc_subs_state_msg[$4][disc_subs[$1]]++
			if(!($3 in sna_rp))
			{
				disc_subs_rateplan_cnt[$3]++
				disc_subs_rateplan_msg[$3][disc_subs[$1]]++
				disc_subs_rateplan_state_cnt[$3"-"$4]++
				disc_subs_rateplan_state_msg[$3"-"$4][disc_subs[$1]]++
			}
		}
		if($1 in arr_inc1001)
		{
			unmatch_rateplan[$3"-"$4]++
		}
		else if($1 in arr_inc1002)
		{
			ignore_rateplan[$3"-"$4]++
		}
		else if($1 in arr_inc1003)
		{
			rel_miss[$3"-"$4]++
		}
		else if($1 in arr_inc1004)
		{
			unmatch_lfc[$4]++
		}
	}
	else if(FILENAME ~ /subscriber_profile_dump.csv/)
	{
		if($7!="" && $7!="#")
		{
			split($7,intn,"#")
			for(i in intn)
			{
				if(intn[i]!="")
				{
					if(($1 in disc_subs) || ($1 in sna_msi))
					{
						if($1 in sna_msi)
						{
							disc_faf_reason["SNA subs profiletag ignored"]++
							disc_faf_msg["International"]["SNA subs profiletag ignored"]++
						}
						else
						{
							disc_faf_reason[disc_subs[$1]]++
							disc_faf_msg["International"][disc_subs[$1]]++
						}
						disc_faf_cnt["International"]++
					}
					faf_cnt["International"]++
				}
			}
		}
		if($8!="" && $8!="#")
		{
			split($8,nat,"#")
			for(i in nat)
			{
				if(nat[i]!="")
				{
					if(($1 in disc_subs) || ($1 in sna_msi))
					{
						if($1 in sna_msi)
						{
							disc_faf_reason["SNA subs profiletag ignored"]++
							disc_faf_msg["National"]["SNA subs profiletag ignored"]++
						}
						else
						{
							disc_faf_reason[disc_subs[$1]]++
							disc_faf_msg["National"][disc_subs[$1]]++
						}
						disc_faf_cnt["National"]++
					}
					faf_cnt["National"]++
				}
			}
		}
		for(f=1;f<=NF;f++)
		{
			if($f != "" && $f != "#")
			{
				if(pt_tagname[f] in pt_map)
				{
					i=pt_tagname[f]
					
					if(i=="OneWayFnF")
					{
						OneWayFnF_child_cnt=0
						split($pt_tagpos[i],OneWayFnF_child,"#");
						for(z in OneWayFnF_child)
						{
							if(OneWayFnF_child[z]!="")
							{
								
								OneWayFnF_child_cnt++
							}
						}
						pt_cnt[i]+=OneWayFnF_child_cnt
					}
					else if(i=="entPDIntNum")
					{
						entPDIntNum_child_cnt=0
						split($pt_tagpos[i],entPDIntNum_child,"#");
						for(z in entPDIntNum_child)
						{
							if(entPDIntNum_child[z]!="")
							{
								
								entPDIntNum_child_cnt++
							}
						}
						pt_cnt[i]+=entPDIntNum_child_cnt
					}
					else
					{
						pt_cnt[i]++
					}
					
					if(($1 in disc_subs) || ($1 in sna_msi))
					{
						if(i=="OneWayFnF")
						{
							disc_pt_cnt[i]+=OneWayFnF_child_cnt
						}
						else if(i=="entPDIntNum")
						{
							disc_pt_cnt[i]+=entPDIntNum_child_cnt
						}
						else
						{
							disc_pt_cnt[i]++	
						}

						if($1 in sna_msi)
						{
							if(i=="OneWayFnF")
							{
								disc_pt_reason["SNA subs profiletag ignored"]+=OneWayFnF_child_cnt
								disc_pt_msg[i]["SNA subs profiletag ignored"]+=OneWayFnF_child_cnt
							}
							else if(i=="entPDIntNum")
							{
								disc_pt_reason["SNA subs profiletag ignored"]+=entPDIntNum_child_cnt
								disc_pt_msg[i]["SNA subs profiletag ignored"]+=entPDIntNum_child_cnt
							}
							else
							{
								disc_pt_reason["SNA subs profiletag ignored"]++
								disc_pt_msg[i]["SNA subs profiletag ignored"]++
							}
						}
						else
						{
							if(i=="OneWayFnF")
							{
								disc_pt_reason[disc_subs[$1]]+=OneWayFnF_child_cnt
								disc_pt_msg[i][disc_subs[$1]]+=OneWayFnF_child_cnt
							}
							else if(i=="entPDIntNum")
							{
								disc_pt_reason[disc_subs[$1]]+=entPDIntNum_child_cnt
								disc_pt_msg[i][disc_subs[$1]]+=entPDIntNum_child_cnt
							}
							else
							{
								disc_pt_reason[disc_subs[$1]]++
								disc_pt_msg[i][disc_subs[$1]]++	

							}
						}
					}
					if(("DEFAULT" in pt_grptype[i]) && ("NORMAL" in pt_grptype[i]))
					{
						pt_cnt[i]++
						if(($1 in disc_subs) || ($1 in sna_msi))
						{
							disc_pt_cnt[i]++
							if($1 in sna_msi)
							{
								disc_pt_reason["SNA subs profiletag ignored"]++
								disc_pt_msg[i]["SNA subs profiletag ignored"]++
							}
							else
							{
								disc_pt_reason[disc_subs[$1]]++
								disc_pt_msg[i][disc_subs[$1]]++	
							}
						}
					}
					
					for(j in pt_map[i])
					{
						pt_cnd_cnt[i][j]
						pt_match=0
						st_match=0
						rp_match=0
						add_pt_match=0
						split(j,j_ele,":")
						pt_match=func_bt_pt_match(i,j_ele[1],j_ele[2],$pt_tagpos[i])
						if(pt_match==1)
						{
							st_match=func_st_match(j_ele[3],sub_state[$1])
							if(st_match==1)
							{
								rp_match=func_rp_match(j_ele[4],j_ele[5],be_rp[$1])
								if(rp_match==1)
								{
									if(j_ele[6]==";")
									{
										add_pt_match=1
									}
									else
									{
										split(j_ele[6],add_pt_sec,";")
										split(add_pt_sec[2],add_pt_rec,"#")
										for(rec in add_pt_rec)
										{
											split(add_pt_rec[rec],add_pt_ele,"-")
											for(ele in add_pt_ele)
											{
												add_pt_match=func_bt_pt_match(add_pt_ele[1],add_pt_ele[2],add_pt_ele[3],$pt_tagpos[add_pt_ele[1]])
												if(add_pt_match==0)
												{
													break;
												}
											}
										}
									}
								}
							}
						}
						
						if(pt_match==1 && st_match==1 && rp_match==1 && add_pt_match==1)
						{
							if(i=="OneWayFnF")
							{
								pt_cnd_cnt[i][j]+=OneWayFnF_child_cnt
							}
							else if(i=="entPDIntNum")
							{
								pt_cnd_cnt[i][j]+=entPDIntNum_child_cnt
							}
							else
							{
								pt_cnd_cnt[i][j]++
							}
							
							
							if(pt_map[i][j]!="")
							{
								pt_cis_cnt[i][j]++
							}

							if(($1 in disc_subs) || ($1 in sna_msi))
							{
								if(i=="OneWayFnF")
								{
									disc_pt_cnd_cnt[i][j]+=OneWayFnF_child_cnt
								}
								else if(i=="entPDIntNum")
								{
									disc_pt_cnd_cnt[i][j]+=entPDIntNum_child_cnt
								}
								else
								{
									disc_pt_cnd_cnt[i][j]++
								}
								

								if(pt_map[i][j]!="")
								{
									disc_pt_cis_cnt[i][j]++
									if($1 in sna_msi)
									{
										disc_pt_cis_msg[i][j]["SNA subs profiletag ignored"]++
										disc_cis_reason["SNA subs profiletag ignored"]++
									}
									else
									{
										disc_pt_cis_msg[i][j][disc_subs[$1]]++
										disc_cis_reason[disc_subs[$1]]++
									}
								}
							}
							else if((pt_grp[i][j]!="DEFAULT") && ($1 in disc_pt) && (i in disc_pt[$1]))
							{
								if(i=="OneWayFnF")
								{
									disc_pt_cnd_cnt[i][j]+=OneWayFnF_child_cnt
								}
								else if(i=="entPDIntNum")
								{
									disc_pt_cnd_cnt[i][j]+=entPDIntNum_child_cnt
								}
								else
								{
									disc_pt_cnd_cnt[i][j]++
								}

								if(pt_map[i][j]!="")
								{
									disc_pt_cis_cnt[i][j]++
									disc_pt_cis_msg[i][j][disc_pt[$1][i]]++
									disc_cis_reason[disc_pt[$1][i]]++
								}
							}
							else if((pt_grp[i][j]=="DEFAULT") && ($1 in disc_pt_def) && (i in disc_pt_def[$1]))
							{
								disc_pt_cnd_cnt[i][j]++
							}
							if(pt_map[i][j]!="" && ($1 in disc_cis_pt) && (i in disc_cis_pt[$1]))
							{
								disc_pt_cis_cnt[i][j]++
								disc_pt_cis_msg[i][j][disc_cis_pt[$1][i]]++
								disc_cis_reason[disc_cis_pt[$1][i]]++
							}
						}
					}
				}
			}
		}
		for(x in bt_pt_list)
		{
			pt_val[$1][x]=$pt_tagpos[x]
		}
	}
	else if(FILENAME ~ /subscriber_balances_dump/)
	{
		bal_type_name[$3]=$5
		bal_cnt[$3]++
		bal_unit_type[$3]=$15
		if($6>0)
		{
			if($1"|"$4 in inc7007)
			{
				if(inc7007[$1"|"$4] in ut_val)
				{
					bal_val[$3]+=ut_val[inc7007[$1"|"$4]]
				}
				else
				{
					bal_val[$3]+=$6
				}
			}
			else
			{
				bal_val[$3]+=$6
			}
		}

		if(($1 in disc_subs) || ($1 in sna_msi))
		{
			disc_bt_bal_cnt[$3]++
			if($6>=0)
			{
				disc_bt_bal_val[$3]+=$6	
			}
			else
			{
				disc_bt_bal_negative_val[$3]+=$6	
			}
			if($1 in sna_msi)
			{
				disc_bt_reason["SNA subs balance record ignored"]++
				disc_bt_msg[$3]["SNA subs balance record ignored"]++
			}
			else
			{
				disc_bt_reason[disc_subs[$1]]++
				disc_bt_msg[$3][disc_subs[$1]]++
			}
		}

		if($3 in bt_map)
		{
			for(i in bt_map[$3])
			{
				bal_cnd_cnt[$3][i]
				bt_match=0
				rp_match=0
				pt_match=0
				split(i,i_ele,":")
				bt_match=func_bt_pt_match($3,i_ele[1],i_ele[2],$6)
				if(bt_match==1)
				{
					rp_match=func_rp_match(i_ele[3],i_ele[4],be_rp[$1])
					if(rp_match==1)
					{
						if(i_ele[5]==";")
						{
							pt_match=1
						}
						else
						{
							split(i_ele[5],pt_sec,";")
							split(pt_sec[2],pt_rec,"#")
							for(k in pt_rec)
							{
								split(pt_rec[k],pt_ele,"-")
								pt_match=func_bt_pt_match(pt_ele[1],pt_ele[2],pt_ele[3],pt_val[$1][pt_ele[1]])
								if(pt_match==0)
								{
									break;
								}	
							}
						}
					}
				}

				if(bt_match==1 && rp_match==1 && pt_match==1)
				{
					bal_cnd_cnt[$3][i]++
					bal_cnd_val[$3][i]+=$6

					if(bt_map[$3][i]!="")
					{
						if($3 in cis_master_excep){
							if($1"|"$4 in AM_map_bt)
							{
								bt_cis_cnt[$3][i]++
							}
							else if($1"|"$4 in master_map_bt)
							{
								bt_cis_cnt[$3][i]++
							}
						}
						else if(!($1"|"$4 in master_map_bt)){
							bt_cis_cnt[$3][i]++
						}
					}
					
					if(($1 in disc_subs) || ($1 in sna_msi))
					{
						disc_bt_bal_cnd_cnt[$3][i]++
						disc_bt_bal_cnd_val[$3][i]+=$6
						

						if(bt_map[$3][i]!="")
						{
							disc_bt_cis_flag=0
							if($3 in cis_master_excep){
								if($1"|"$4 in AM_map_bt)
								{
									disc_bt_cis_flag=1
								}
								else if($1"|"$4 in master_map_bt)
								{
									disc_bt_cis_flag=1
								}
							}
							else if(!($1"|"$4 in master_map_bt)){
								disc_bt_cis_flag=1
							}
							
							if(disc_bt_cis_flag==1){
								disc_bt_cis_cnt[$3][i]++
								if($1 in sna_msi)
								{
									disc_bt_cis_msg[$3][i]["SNA subs balance record ignored"]++
									disc_cis_reason["SNA subs balance record ignored"]++
								}
								else
								{
									disc_bt_cis_msg[$3][i][disc_subs[$1]]++
									disc_cis_reason[disc_subs[$1]]++
								}
							}
						}
					}
					else if(($1 in disc_bt_bucket) && ($4 in disc_bt_bucket[$1]))
					{
						disc_bt_bal_cnd_cnt[$3][i]++
						disc_bt_bal_cnd_val[$3][i]+=$6

						if(bt_map[$3][i]!="")
						{
							disc_bt_cis_flag=0
							if($3 in cis_master_excep){
								if($1"|"$4 in AM_map_bt)
								{
									disc_bt_cis_flag=1
								}
								else if($1"|"$4 in master_map_bt)
								{
									disc_bt_cis_flag=1
								}
							}
							else if(!($1"|"$4 in master_map_bt)){
								disc_bt_cis_flag=1
							}
							
							if(disc_bt_cis_flag==1){
								disc_bt_cis_cnt[$3][i]++
								disc_bt_cis_msg[$3][i][disc_bt_bucket[$1][$4]]++
								disc_cis_reason[disc_bt_bucket[$1][$4]]++
							}
						}
					}
					if(bt_map[$3][i]!="" && ($1 in disc_cis_bt) && ($4 in disc_cis_bt[$1]))
					{
						disc_bt_cis_cnt[$3][i]++
						disc_bt_cis_msg[$3][i][disc_cis_bt[$1][$4]]++
						disc_cis_reason[disc_cis_bt[$1][$4]]++
					}
				}
			}
		}
	}
	else if(FILENAME ~ /subscriber_cugcli_dump.csv/)
	{
		src_cug_cnt[$2]++
		if($1 in disc_subs)
		{
			disc_cug_reason[disc_subs[$1]]++
			disc_cug_cnt[$2]++
			disc_cug_msg[$2][disc_subs[$1]]++
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
	close(BAK_FILENAME)
    print "["basename(BAK_FILENAME)"] record processed: " BAK_FNR
    
    print "Generating stats for Source/Discarded State..."
	for(i in disc_subs_state_msg)
	{
		for(j in disc_subs_state_msg[i])
		{
			if(!(i in i_disc_subs_state_msg))
			{
				i_disc_subs_state_msg[i]=j "-" disc_subs_state_msg[i][j]
			}
			else
			{
				i_disc_subs_state_msg[i]=i_disc_subs_state_msg[i] ";" j "-" disc_subs_state_msg[i][j]
			}
		}
	}
	header=0
	for(i in state_cnt){
		header++
		if(header==1)
		{
			print "STATE,SOURCE_CNT,DISCARDER_CNT,DISCARDED_REMARK" > o_state_file
		}
		print i,state_cnt[i],disc_subs_state_cnt[i],i_disc_subs_state_msg[i] > o_state_file
	}
	
	header=0
	for(i in disc_reason)
	{
		header++
		if(header==1)
		{
			print "REJECTION REASON,REJECTION COUNT" > migtool_dir"/report/subscriber/"hostnm"_subs_rejection_stats.txt"
		}
		print i,disc_reason[i] > migtool_dir"/report/subscriber/"hostnm"_subs_rejection_stats.txt"
	}

    print "Generating stats for Source/Discarded RatePlan..."
	for(i in disc_subs_rateplan_msg)
	{
		for(j in disc_subs_rateplan_msg[i])
		{
			if(!(i in i_disc_subs_rateplan_msg))
			{
				i_disc_subs_rateplan_msg[i]=j "-" disc_subs_rateplan_msg[i][j]
			}
			else
			{
				i_disc_subs_rateplan_msg[i]=i_disc_subs_rateplan_msg[i] ";" j "-" disc_subs_rateplan_msg[i][j]
			}
		}
	}
	header=0
	for(i in rateplan_cnt)
	{
		header++
		if(header==1)
		{
			print "RATE_PLAN_NAME,RATE_PLAN_ID,SOURCE_CNT,DISCARDED_CNT,DISCARDED_REMARK" > o_rateplan_file
		}
		print rateplan_name[i],i,rateplan_cnt[i],disc_subs_rateplan_cnt[i],i_disc_subs_rateplan_msg[i] > o_rateplan_file
	}
	header=0	
	for(i in unmatch_rateplan)
	{
		header++
		if(header==1)
		{
			print "RATEPLAN_ID-STATE,REJECTION COUNT" > migtool_dir"/report/subscriber/"hostnm"_unmatch_rateplan.txt"
		}
		print i,unmatch_rateplan[i] > migtool_dir"/report/subscriber/"hostnm"_unmatch_rateplan.txt"
	}

	header=0
	for(i in ignore_rateplan)
	{
		header++
		if(header==1)
		{
			print "RATEPLAN_ID-STATE,REJECTION COUNT" > migtool_dir"/report/subscriber/"hostnm"_ignore_rateplan.txt"
		}
		print i,ignore_rateplan[i] > migtool_dir"/report/subscriber/"hostnm"_ignore_rateplan.txt"
	}
	
	header=0
	for(i in rel_miss)
	{
		header++
		if(header==1)
		{
			print "RATEPLAN_ID-STATE,REJECTION COUNT" > migtool_dir"/report/subscriber/"hostnm"_relation_miss.txt"
		}
		print i,rel_miss[i] > migtool_dir"/report/subscriber/"hostnm"_relation_miss.txt"
	}

	header=0
	for(i in unmatch_lfc)
	{
		header++
		if(header==1)
		{
			print "LIFECYCLE_STATE,REJECTION COUNT" > migtool_dir"/report/subscriber/"hostnm"_unmatch_lfc.txt"
		}
		print i,unmatch_lfc[i] > migtool_dir"/report/subscriber/"hostnm"_unmatch_lfc.txt"
	}

    print "Generating stats for Source/Discarded RatePlan-State..."
	for(i in disc_subs_rateplan_state_msg)
	{
		for(j in disc_subs_rateplan_state_msg[i])
		{
			if(!(i in i_disc_subs_rateplan_state_msg))
			{
				i_disc_subs_rateplan_state_msg[i]=j "-" disc_subs_rateplan_state_msg[i][j]
			}
			else
			{
				i_disc_subs_rateplan_state_msg[i]=i_disc_subs_rateplan_state_msg[i] ";" j "-" disc_subs_rateplan_state_msg[i][j]
			}
		}
	}
	header=0
	for(i in rateplan_state_cnt)
	{
		header++
		if(header==1)
		{
			print "RATE_PLAN_NAME,RATE_PLAN_ID-STATE,SOURCE_CNT,DISCARDED_CNT,DISCARDED_REMARK" > o_rateplan_state_file
		}
		split(i,i_ele,"-")
		print rateplan_name[i_ele[1]],i,rateplan_state_cnt[i],disc_subs_rateplan_state_cnt[i],i_disc_subs_rateplan_state_msg[i] > o_rateplan_state_file
	}

    print "Generating stats for Source/Discarded Balances..."
	for(i in disc_bt_msg)
	{
		for(j in disc_bt_msg[i])
		{
			if(!(i in i_disc_bt_msg))
			{
				i_disc_bt_msg[i]=j "-" disc_bt_msg[i][j]
			}
			else
			{
				i_disc_bt_msg[i]=i_disc_bt_msg[i] ";" j "-" disc_bt_msg[i][j]
			}
		}
	}
	header=0
	
	for(i in bal_cnt)
	{
		header++
		if(header==1)
		{
			print "BALANCE_TYPE_NAME,BT_ID,SOURCE_CNT,SOURCE_VAL,DISCARDED_CNT,DISCARDED_VAL,BT-CONDITION,CONDITIONAL_SOURCE_CNT,CONDITIONAL_SOURCE_VAL,CONDITIONAL_DISCARDED_CNT,CONDITIONAL_DISCARDED_VAL,TRACK_EXTRA,DISCARDED_REMARK,BT_UNIT_TYPE,SOURCE_NEGATIVE_BALANCE,DISCARDED_NEGATIVE_BALANCE" > o_bal_file
		}
		if(i in bal_cnd_cnt)
		{
			for(j in bal_cnd_cnt[i])
			{
				bt_condition=i":"j
				if(i in track_extra)
				{
					for(k in track_extra[i])
					{
						concat_track_extra[i]=";"k"-"track_extra[i][k]";"
					}
				}
				print bal_type_name[i],i,bal_cnt[i],bal_val[i],disc_bt_bal_cnt[i],disc_bt_bal_val[i],bt_condition,bal_cnd_cnt[i][j],bal_cnd_val[i][j],disc_bt_bal_cnd_cnt[i][j],disc_bt_bal_cnd_val[i][j],concat_track_extra[i],i_disc_bt_msg[i],bal_unit_type[i],disc_bt_bal_negative_val[i],extra_bal_migrated[i] > o_bal_file
			}
		}
		else
		{
			bt_condition=i ":::::;"
			if(i in track_extra)
			{
				for(k in track_extra[i])
				{
					concat_track_extra[i]=";"k"-"track_extra[i][k]";"
				}
			}
			print bal_type_name[i],i,bal_cnt[i],bal_val[i],disc_bt_bal_cnt[i],disc_bt_bal_val[i],bt_condition,"","","","",concat_track_extra[i],i_disc_bt_msg[i],bal_unit_type[i],disc_bt_bal_negative_val[i],extra_bal_migrated[i] > o_bal_file
		}
	}
	header=0
	for(i in disc_bt_reason)
	{
		header++
		if(header==1)
		{
			print "REJECTION REASON,REJECTION COUNT" > migtool_dir"/report/subscriber/"hostnm"_bt_rejection_stats.txt"
		}
		print i,disc_bt_reason[i] > migtool_dir"/report/subscriber/"hostnm"_bt_rejection_stats.txt"
	}
    print "Generating stats for Source/Discarded CUG..."
	for(i in disc_cug_msg)
	{
		for(j in disc_cug_msg[i])
		{
			if(!(i in i_disc_cug_msg))
			{
				i_disc_cug_msg[i]=j "-" disc_cug_msg[i][j]
			}
			else
			{
				i_disc_cug_msg[i]=i_disc_cug_msg[i] ";" j "-" disc_cug_msg[i][j]
			}
		}
	}
	header=0
	for(i in src_cug_cnt)
	{
		header++
		if(header==1)
		{
			print "CUG_ID,SRC_COUNT,DISCARDED_COUNT,DISCARDED_REASON" > o_cug_file
		}
		print i,src_cug_cnt[i],disc_cug_cnt[i],i_disc_cug_msg[i] > o_cug_file
	}
	header=0
	for(i in unmatch_cugid)
	{
		header++
		if(header==1)
		{
			print "CUG ID,REJECTION COUNT" > migtool_dir"/report/subscriber/"hostnm"_unmatch_cugid.txt"	
		}
		print i,unmatch_cugid[i] > migtool_dir"/report/subscriber/"hostnm"_unmatch_cugid.txt"	
	}
	header=0
	for(i in disc_cug_reason)
	{
		header++
		if(header==1)
		{
			print "REJECTION REASON,REJECTION COUNT" > migtool_dir"/report/subscriber/"hostnm"_cug_rejection_stats.txt"
		}
		print i,disc_cug_reason[i] > migtool_dir"/report/subscriber/"hostnm"_cug_rejection_stats.txt"	
	}

    print "Generating stats for Source/Discarded FaF..."
	for(i in disc_faf_msg)
	{
		for(j in disc_faf_msg[i])
		{
			if(!(i in i_disc_faf_msg))
			{
				i_disc_faf_msg[i]=j "-" disc_faf_msg[i][j]
			}
			else
			{
				i_disc_faf_msg[i]=i_disc_faf_msg[i] ";" j "-" disc_faf_msg[i][j]
			}
		}
	}
	header=0
	for(i in faf_cnt)
	{
		header++
		if(header==1)
		{
			print "FAF_TYPE,SOURCE_COUNT,DISCARDED_COUNT,DISCARDED_REMARK" > o_faf_file
		}
		print i,faf_cnt[i],disc_faf_cnt[i],i_disc_faf_msg[i] > o_faf_file
	}
	header=0
	for(i in disc_faf_reason)
	{
		header++
		if(header==1)
		{
			print "REJECTION REASON,REJECTION COUNT" > migtool_dir"/report/subscriber/"hostnm"_faf_rejection_stats.txt"
		}
		print i,disc_faf_reason[i] > migtool_dir"/report/subscriber/"hostnm"_faf_rejection_stats.txt"
	}

    print "Generating stats for Source/Discarded SNA..."
	for(i in disc_sna_msg)
	{
		for(j in disc_sna_msg[i])
		{
			if(!(i in i_disc_sna_msg))
			{
				i_disc_sna_msg[i]=j "-" disc_sna_msg[i][j]
			}
			else
			{
				i_disc_sna_msg[i]=i_disc_sna_msg[i_ele[1]] ";" j "-" disc_sna_msg[i][j]
			}
		}
	}
	header=0
	for(i in sna_cnt)
	{
		header++
		if(header==1)
		{
			print "SNA_SERIES-RATEPLANID,SOURCE_COUNT,DISCARDED_COUNT,DISCARDED_REMARK" > o_sna_file
		}
		print i,sna_cnt[i],disc_sna_cnt[i],i_disc_sna_msg[i] > o_sna_file
	}
	header=0
	for(i in disc_sna_reason)
	{
		header++
		if(header==1)
		{
			print "REJECTION REASON,REJECTION COUNT" > migtool_dir"/report/subscriber/"hostnm"_sna_rejection_stats.txt"
		}
		print i,disc_sna_reason[i] > migtool_dir"/report/subscriber/"hostnm"_sna_rejection_stats.txt"	
	}

	print "Generating stats for Source/Discarded ProfileTags..."
	for(i in disc_pt_msg)
	{
		for(j in disc_pt_msg[i])
		{
			if(!(i in i_disc_pt_msg))
			{
				i_disc_pt_msg[i]=j "-" disc_pt_msg[i][j]
			}
			else
			{
				i_disc_pt_msg[i]=i_disc_pt_msg[i] ";" j "-" disc_pt_msg[i][j]
			}
		}
	}
	header=0
	for(i in pt_cnt)
	{
		header++
		if(header==1)
		{
			print "PROFILE_TAG_NAME,SOURCE_CNT,DISCARDED_CNT,PT-CONDITION,CONDITIONAL_SOURCE_CNT,CONDITIONAL_DISCARDED_CNT,DISCARDED_REMARK,TRACK_GRACE" > o_pt_file
		}
		if(i in pt_cnd_cnt)
		{
			for(j in pt_cnd_cnt[i])
			{
				pt_condition=i":"j
				if(i in track_extra)
				{
					for(k in track_extra[i])
					{
						concat_track_extra[i]=";"k"-"track_extra[i][k]";"
					}
				}
				print i,pt_cnt[i],disc_pt_cnt[i],pt_condition,pt_cnd_cnt[i][j],disc_pt_cnd_cnt[i][j],i_disc_pt_msg[i],concat_track_extra[i] > o_pt_file
			}
		}
		else
		{
			pt_condition=i":::::;"
			if(i in track_extra)
			{
				for(k in track_extra[i])
				{
					concat_track_extra[i]=";"k"-"track_extra[i][k]";"
				}
			}
			print i,pt_cnt[i],disc_pt_cnt[i],pt_condition,"","",i_disc_pt_msg[i],concat_track_extra[i] > o_pt_file
		}	
	}
	header=0
	for(i in disc_pt_reason)
	{
		header++
		if(header==1)
		{
			print "REJECTION REASON,REJECTION COUNT" > migtool_dir"/report/subscriber/"hostnm"_pt_rejection_stats.txt"
		}
		print i,disc_pt_reason[i] > migtool_dir"/report/subscriber/"hostnm"_pt_rejection_stats.txt"
	}

	header=0
	for(i in disc_cis_reason)
	{
		header++
		if(header==1)
		{
			print "REJECTION REASON,REJECTION COUNT" > migtool_dir"/report/subscriber/"hostnm"_cis_rejection_stats.txt"
		}
		print i,disc_cis_reason[i] > migtool_dir"/report/subscriber/"hostnm"_cis_rejection_stats.txt"
	}

	print "Generating stats for Source/Discarded Balances/ProfileTag for CIS..."
	for(i in disc_pt_cis_msg)
	{
		for(j in disc_pt_cis_msg[i])
		{
			for(k in disc_pt_cis_msg[i][j])
			{
				if(!(i"-"j in i_disc_pt_cis_msg))
				{
					i_disc_pt_cis_msg[i"-"j]=k "-" disc_pt_cis_msg[i][j][k]
				}
				else
				{
					i_disc_pt_cis_msg[i"-"j]=i_disc_pt_cis_msg[i"-"j] ";" k "-" disc_pt_cis_msg[i][j][k]
				}
			}
		}
	}
	for(i in disc_bt_cis_msg)
	{
		for(j in disc_bt_cis_msg[i])
		{
			for(k in disc_bt_cis_msg[i][j])
			{
				if(!(i"-"j in i_disc_bt_cis_msg))
				{
					i_disc_bt_cis_msg[i"-"j]=k "-" disc_bt_cis_msg[i][j][k]
				}
				else
				{
					i_disc_bt_cis_msg[i"-"j]=i_disc_bt_cis_msg[i"-"j] ";" k "-" disc_bt_cis_msg[i][j][k]
				}
			}
		}
	}
	header=0
	print "BALANCE_TYPE_NAME,BT_ID,PROFILE_TAG,CONDITION,SOURCE_CNT,DISCARDED_CNT,DISCARDED_REMARK" > o_cis_file
	for(i in pt_cis_cnt)
	{
		for(j in pt_cis_cnt[i])
		{
			pt_condition=i":"j
			print "","",i,pt_condition,pt_cis_cnt[i][j],disc_pt_cis_cnt[i][j],i_disc_pt_cis_msg[i"-"j] > o_cis_file
		}
	}
	for(i in bt_cis_cnt)
	{
		for(j in bt_cis_cnt[i])
		{
			bt_condition=i":"j
			print bal_type_name[i],i,"",bt_condition,bt_cis_cnt[i][j],disc_bt_cis_cnt[i][j],i_disc_bt_cis_msg[i"-"j] > o_cis_file
		}
	}
}' $2/config/BALANCE_MAP.txt $2/config/PROFILETAG_MAP.txt $2/config/PROFILETAG_DICT.txt $2/logs/INC1003.log $2/logs/Discarded.log* $2/logs/Exception.log* $2/logs/Track.log* $2/input/*_subscriber_be_dump.csv $2/input/*_subscriber_profile_dump.csv $2/input/*_subscriber_balances_dump*.csv $2/input/*_subscriber_cugcli_dump.csv

touch $2/report/subscriber/${hostnm}_source_state_stats.txt
touch $2/report/subscriber/${hostnm}_source_rateplan_stats.txt
touch $2/report/subscriber/${hostnm}_source_rateplan_state_stats.txt
touch $2/report/subscriber/${hostnm}_source_bal_stats.txt
touch $2/report/subscriber/${hostnm}_source_profiletag_stats.txt
touch $2/report/subscriber/${hostnm}_source_cug_stats.txt
touch $2/report/subscriber/${hostnm}_source_faf_stats.txt
touch $2/report/subscriber/${hostnm}_source_sna_stats.txt
touch $2/report/subscriber/${hostnm}_source_cis_stats.txt
touch $2/report/subscriber/${hostnm}_subs_rejection_stats.txt
touch $2/report/subscriber/${hostnm}_bt_rejection_stats.txt
touch $2/report/subscriber/${hostnm}_pt_rejection_stats.txt
touch $2/report/subscriber/${hostnm}_cug_rejection_stats.txt
touch $2/report/subscriber/${hostnm}_faf_rejection_stats.txt
touch $2/report/subscriber/${hostnm}_sna_rejection_stats.txt
touch $2/report/subscriber/${hostnm}_ignore_rateplan.txt
touch $2/report/subscriber/${hostnm}_unmatch_rateplan.txt
touch $2/report/subscriber/${hostnm}_unmatch_lfc.txt
touch $2/report/subscriber/${hostnm}_unmatch_cugid.txt

gzip $2/input/*_subscriber_be_dump.csv
gzip $2/input/*_subscriber_balances_dump*.csv
gzip $2/input/*_subscriber_cugcli_dump.csv
gzip $2/input/*_subscriber_profile_dump.csv
# gzip $2/logs/Discarded.log*
# gzip $2/logs/Exception.log*
# gzip $2/logs/Track.log*

