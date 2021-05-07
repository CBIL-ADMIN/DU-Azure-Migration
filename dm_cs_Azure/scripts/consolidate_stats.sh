#!/bin/ksh

awk -F, -v migtool_dir=$1 -v o_state_file=$1"/report/subscriber/source_state_stats.txt" -v o_rateplan_file=$1"/report/subscriber/source_rateplan_stats.txt" -v o_rateplan_state_file=$1"/report/subscriber/source_rateplan_state_stats.txt" -v o_bal_file=$1"/report/subscriber/source_bal_stats.txt" -v o_pt_file=$1"/report/subscriber/source_profiletag_stats.txt" -v o_cug_file=$1"/report/subscriber/source_cug_stats.txt" -v o_faf_file=$1"/report/subscriber/source_faf_stats.txt" -v o_sna_file=$1"/report/subscriber/source_sna_stats.txt" -v o_cis_file=$1"/report/subscriber/source_cis_stats.txt" 'BEGIN{OFS=","}{
        if(FILENAME ~ /_source_state_stats/)
        {
                if(FNR!=1)
                {
                        #STATE,SOURCE_CNT,DISCARDED_CNT,DISCARDED_REMARK
                        STATE_SOURCE_CNT[$1]+=$2
                        STATE_DISCARDED_CNT[$1]+=$3
                        split($4,remark_rec,";")
                        for(rec in remark_rec)
                        {
                                if(remark_rec[rec]!="")
                                {
                                        split(remark_rec[rec],rec_ele,"-")
                                        STATE_DISCARDED_REMARK[$1][rec_ele[1]]+=rec_ele[2]
                                }
                        }
                }
        }
        else if(FILENAME ~ /_source_rateplan_stats/)
        {
                if(FNR!=1)
                {
                        #RATEPLAN_NAME,RATE_PLAN_ID,SOURCE_CNT,DISCARDED_CNT,DISCARDED_REMARK
                        RP[$2]=$1
                        RP_SOURCE_CNT[$2]+=$3
                        RP_DISCARDED_CNT[$2]+=$4
                        split($5,remark_rec,";")
                        for(rec in remark_rec)
                        {
                                if(remark_rec[rec]!="")
                                {
                                        split(remark_rec[rec],rec_ele,"-")
                                        RP_DISCARDED_REMARK[$2][rec_ele[1]]+=rec_ele[2]
                                }
                        }
                }
        }
        else if(FILENAME ~ /_source_rateplan_state_stats/)
        {
                if(FNR!=1)
                {
                        #RATEPLAN_NAME,RATE_PLAN_ID-STATE,SOURCE_CNT,DISCARDED_CNT,DISCARDED_REMARK
                        RPSTATE[$2]=$1
                        RPSTATE_SOURCE_CNT[$2]+=$3
                        RPSTATE_DISCARDED_CNT[$2]+=$4
                        split($5,remark_rec,";")
                        for(rec in remark_rec)
                        {
                                if(remark_rec[rec]!="")
                                {
                                        split(remark_rec[rec],rec_ele,"-")
                                        RPSTATE_DISCARDED_REMARK[$2][rec_ele[1]]+=rec_ele[2]
                                }
                        }
                }
        }
        else if(FILENAME ~ /_source_bal_stats/)
        {
                if(FNR!=1)
                {
                        #BALANCE_TYPE_NAME,BT_ID,SOURCE_CNT,SOURCE_VAL,DISCARDED_CNT,DISCARDED_VAL,BT-CONDITION,CONDITIONAL_SOURCE_CNT,CONDITIONAL_SOURCE_VAL,CONDITIONAL_DISCARDED_CNT,CONDITIONAL_DISCARDED_VAL,TRACK_EXTRA,DISCARDED_REMARK
                        BT[$2]=$1

                        if(!($2","FILENAME in BT_UNIQUE))
                        {
                                BT_UNIQUE[$2","FILENAME]
                                BT_SOURCE_CNT[$1]+=$3
                                BT_SOURCE_VAL[$1]+=$4
                                BT_DISCARDED_CNT[$1]+=$5
                                BT_DISCARDED_VAL[$1]+=$6
                                BT_UNIT_TYPE[$1]=$14
                                BT_NEGATIVE_SOURCE_VAL[$1]+=$15
                                BT_NEGATIVE_DISCARDED_VAL[$1]+=$16
                                split($12,track_rec,";")
                                for(rec in track_rec)
                                {
                                        if(track_rec[rec]!="")
                                        {
                                                split(track_rec[rec],rec_ele,"-")
                                                BT_TRACK_OFFER[$1][rec_ele[1]]+=rec_ele[2]
                                        }
                                }
                                split($13,remark_rec,";")
                                for(rec in remark_rec)
                                {
                                        if(remark_rec[rec]!="")
                                        {
                                                split(remark_rec[rec],rec_ele,"-")
                                                BT_DISCARDED_REMARK[$1][rec_ele[1]]+=rec_ele[2]
                                        }
                                }
                        }

                        BT_CONDITIONAL_SOURCE_CNT[$2][$7]+=$8
                        BT_CONDITIONAL_SOURCE_VAL[$2][$7]+=$9
                        BT_CONDITIONAL_DISCARDED_CNT[$2][$7]+=$10
                        BT_CONDITIONAL_DISCARDED_VAL[$2][$7]+=$11
                }
        }
        else if(FILENAME ~ /_source_profiletag_stats/)
        {
                if(FNR!=1)
                {
                        #PROFILE_TAG_NAME,SOURCE_CNT,DISCARDED_CNT,PT-CONDITION,CONDITIONAL_SOURCE_CNT,CONDITIONAL_DISCARDED_CNT,DISCARDED_REMARK
                        PT[$1]
                        PT_CONDITIONAL_SOURCE_CNT[$1][$4]+=$5
                        PT_CONDITIONAL_DISCARDED_CNT[$1][$4]+=$6
                        if(!($1","FILENAME in PT_UNIQUE))
                        {
                                PT_UNIQUE[$1","FILENAME]
                                PT_SOURCE_CNT[$1]+=$2
                                PT_DISCARDED_CNT[$1]+=$3
                                split($7,remark_rec,";")
                                for(rec in remark_rec)
                                {
                                        if(remark_rec[rec]!="")
                                        {
                                                split(remark_rec[rec],rec_ele,"-")
                                                PT_DISCARDED_REMARK[$1][rec_ele[1]]+=rec_ele[2]
                                        }
                                }
                                split($8,track_rec,";")
                                for(rec in track_rec)
                                {
                                        if(track_rec[rec]!="")
                                        {
                                                split(track_rec[rec],rec_ele,"-")
                                                PT_TRACK_OFFER[$1][rec_ele[1]]+=rec_ele[2]
                                        }
                                }
                        }
                }
        }
        else if(FILENAME ~ /_source_cug_stats/)
        {
                if(FNR!=1)
                {
                        #CUG_ID,SRC_COUNT,DISCARDED_COUNT,DISCARDED_REASON
                        CUG_SOURCE_CNT[$1]+=$2
                        CUG_DISCARDED_CNT[$1]+=$3
                        split($4,remark_rec,";")
                        for(rec in remark_rec)
                        {
                                if(remark_rec[rec]!="")
                                {
                                        split(remark_rec[rec],rec_ele,"-")
                                        CUG_DISCARDED_REMARK[$1][rec_ele[1]]+=rec_ele[2]
                                }
                        }
                }
        }
        else if(FILENAME ~ /_source_faf_stats/)
        {
                if(FNR!=1)
                {
                        #FAF_TYPE,SOURCE_COUNT,DISCARDED_COUNT,DISCARDED_REMARK
                        FAF_SOURCE_CNT[$1]+=$2
                        FAF_DISCARDED_CNT[$1]+=$3
                        split($4,remark_rec,";")
                        for(rec in remark_rec)
                        {
                                if(remark_rec[rec]!="")
                                {
                                        split(remark_rec[rec],rec_ele,"-")
                                        FAF_DISCARDED_REMARK[$1][rec_ele[1]]+=rec_ele[2]
                                }
                        }
                }
        }
        else if(FILENAME ~ /_source_sna_stats/)
        {
                if(FNR!=1)
                {
                        #SNA_SERIES-RATEPLANID,SOURCE_COUNT,DISCARDED_COUNT,DISCARDED_REMARK
                        SNA_SOURCE_CNT[$1]+=$2
                        SNA_DISCARDED_CNT[$1]+=$3
                        split($4,remark_rec,";")
                        for(rec in remark_rec)
                        {
                                if(remark_rec[rec]!="")
                                {
                                        split(remark_rec[rec],rec_ele,"-")
                                        SNA_DISCARDED_REMARK[$1][rec_ele[1]]+=rec_ele[2]
                                }
                        }
                }
        }
        else if(FILENAME ~ /_source_cis_stats/)
        {
                if(FNR!=1)
                {
                        #BALANCE_TYPE_NAME,BT_ID,PROFILE_TAG,CONDITION,SOURCE_CNT,DISCARDED_CNT,DISCARDED_REMARK
                        CIS_SOURCE_CNT[$1","$2","$3","$4]+=$5
                        CIS_DISCARDED_CNT[$1","$2","$3","$4]+=$6
                        split($7,remark_rec,";")
                        for(rec in remark_rec)
                        {
                                if(remark_rec[rec]!="")
                                {
                                        split(remark_rec[rec],rec_ele,"-")
                                        CIS_DISCARDED_REMARK[$1","$2","$3","$4][rec_ele[1]]+=rec_ele[2]
                                }
                        }
                }
        }
        else if(FILENAME ~ /_subs_rejection_stats/)
        {
                if(FNR!=1)
                {
                        subs_rejection_stats[$1]+=$2
                }
        }
        else if(FILENAME ~ /_bt_rejection_stats/)
        {
                if(FNR!=1)
                {
                        bt_rejection[$1]+=$2
                }
        }
        else if(FILENAME ~ /_pt_rejection_stats/)
        {
                if(FNR!=1)
                {
                        pt_rejection[$1]+=$2
                }
        }
        else if(FILENAME ~ /_cug_rejection_stats/)
        {
                if(FNR!=1)
                {
                        rejection_cug[$1]+=$2
                }
        }
        else if(FILENAME ~ /_faf_rejection_stats/)
        {
                if(FNR!=1)
                {
                        rejection_faf[$1]+=$2
                }
        }
        else if(FILENAME ~ /_sna_rejection_stats/)
        {
                if(FNR!=1)
                {
                        rejection_sna[$1]+=$2
                }
        }
        else if(FILENAME ~ /_cis_rejection_stats/)
        {
                if(FNR!=1)
                {
                        rejection_cis[$1]+=$2
                }
        }
        else if(FILENAME ~ /_ignore_rateplan/)
        {
                if(FNR!=1)
                {
                        ignore_rateplan[$1]+=$2
                }
        }
        else if(FILENAME ~ /_unmatch_rateplan/)
        {
                if(FNR!=1)
                {
                        unmatch_rateplan[$1]+=$2
                }
        }
        else if(FILENAME ~ /_unmatch_lfc/)
        {
                if(FNR!=1)
                {
                        unmatch_lfc[$1]+=$2
                }
        }
        else if(FILENAME ~ /_unmatch_cugid/)
        {
                if(FNR!=1)
                {
                        unmatch_cugid[$1]+=$2
                }
        }
}END{
        print "REJECTION_REASON-REJECTION_COUNT" > migtool_dir"/report/subscriber/subs_rejection_stats.txt"
        for(i in subs_rejection_stats)
        {
                print i"-"subs_rejection_stats[i] > migtool_dir"/report/subscriber/subs_rejection_stats.txt"
        }

        print "REJECTION_REASON-REJECTION_COUNT" > migtool_dir"/report/subscriber/bt_rejection_stats.txt"
        for(i in bt_rejection)
        {
                print i"-"bt_rejection[i] > migtool_dir"/report/subscriber/bt_rejection_stats.txt"
        }

        print "REJECTION_REASON-REJECTION_COUNT" > migtool_dir"/report/subscriber/pt_rejection_stats.txt"
        for(i in pt_rejection)
        {
                print i"-"pt_rejection[i] > migtool_dir"/report/subscriber/pt_rejection_stats.txt"
        }

        print "REJECTION_REASON-REJECTION_COUNT" > migtool_dir"/report/subscriber/cug_rejection_stats.txt"
        for(i in rejection_cug)
        {
                print i"-"rejection_cug[i] > migtool_dir"/report/subscriber/cug_rejection_stats.txt"
        }

        print "REJECTION_REASON-REJECTION_COUNT" > migtool_dir"/report/subscriber/faf_rejection_stats.txt"
        for(i in rejection_faf)
        {
                print i"-"rejection_faf[i] > migtool_dir"/report/subscriber/faf_rejection_stats.txt"
        }

        print "REJECTION_REASON-REJECTION_COUNT" > migtool_dir"/report/subscriber/sna_rejection_stats.txt"
        for(i in rejection_sna)
        {
                print i"-"rejection_sna[i] > migtool_dir"/report/subscriber/sna_rejection_stats.txt"
        }

        print "REJECTION_REASON-REJECTION_COUNT" > migtool_dir"/report/subscriber/cis_rejection_stats.txt"
        for(i in rejection_cis)
        {
                print i"-"rejection_cis[i] > migtool_dir"/report/subscriber/cis_rejection_stats.txt"
        }

        print "RATEPLAN_ID,STATE,REJECTION_COUNT" > migtool_dir"/report/subscriber/ignore_rateplan.txt"
        for(i in ignore_rateplan)
        {
                cnt=ignore_rateplan[i]
                gsub("-",",",i);
                print i,cnt > migtool_dir"/report/subscriber/ignore_rateplan.txt"
        }

        print "RATEPLAN_ID,STATE,REJECTION_COUNT" > migtool_dir"/report/subscriber/unmatch_rateplan.txt"
        for(i in unmatch_rateplan)
        {
                cnt=unmatch_rateplan[i]
                gsub("-",",",i);
                print i,cnt > migtool_dir"/report/subscriber/unmatch_rateplan.txt"
        }

        print "STATE,REJECTION_COUNT" > migtool_dir"/report/subscriber/unmatch_lfc.txt"
        for(i in unmatch_lfc)
        {
                print i,unmatch_lfc[i] > migtool_dir"/report/subscriber/unmatch_lfc.txt"
        }

        print "CUG ID,REJECTION COUNT" > migtool_dir"/report/subscriber/unmatch_cugid.txt"
        for(i in unmatch_cugid)
        {
                print i,unmatch_cugid[i] > migtool_dir"/report/subscriber/unmatch_cugid.txt"
        }
        
        print "Consolidating Source State stats"
        print "STATE,SOURCE_CNT,DISCARDED_CNT,DISCARDED_REMARK" > o_state_file
        for(i in STATE_SOURCE_CNT)
        {
                if(i in STATE_DISCARDED_REMARK)
                {
                        for(j in STATE_DISCARDED_REMARK[i])
                        {
                                final_STATE_DISCARDED_REMARK[i]=        final_STATE_DISCARDED_REMARK[i] j "-" STATE_DISCARDED_REMARK[i][j] ";"
                        }
                }
                print i,STATE_SOURCE_CNT[i],STATE_DISCARDED_CNT[i],final_STATE_DISCARDED_REMARK[i] > o_state_file
        }

        print "Consolidating Source RatePlan stats"
        print "RATEPLAN_NAME,RATE_PLAN_ID,SOURCE_CNT,DISCARDED_CNT,DISCARDED_REMARK" > o_rateplan_file
        for(i in RP)
        {
                if(i in RP_DISCARDED_REMARK)
                {
                        for(j in RP_DISCARDED_REMARK[i])
                        {
                                final_RP_DISCARDED_REMARK[i]=   final_RP_DISCARDED_REMARK[i] j "-" RP_DISCARDED_REMARK[i][j] ";"
                        }
                }
                print RP[i],i,RP_SOURCE_CNT[i],RP_DISCARDED_CNT[i],final_RP_DISCARDED_REMARK[i] > o_rateplan_file
        }

        print "Consolidating Source RatePlan-State stats"
        print "RATEPLAN_NAME,RATE_PLAN_ID-STATE,SOURCE_CNT,DISCARDED_CNT,DISCARDED_REMARK" > o_rateplan_state_file
        for(i in RPSTATE)
        {
                if(i in RPSTATE_DISCARDED_REMARK)
                {
                        for(j in RPSTATE_DISCARDED_REMARK[i])
                        {
                                final_RPSTATE_DISCARDED_REMARK[i]=      final_RPSTATE_DISCARDED_REMARK[i] j "-" RPSTATE_DISCARDED_REMARK[i][j] ";"
                        }
                }
                print RPSTATE[i],i,RPSTATE_SOURCE_CNT[i],RPSTATE_DISCARDED_CNT[i],final_RPSTATE_DISCARDED_REMARK[i] > o_rateplan_state_file
        }

        print "Consolidating Source Balance stats"
        print "BALANCE_TYPE_NAME,BT_ID,SOURCE_CNT,SOURCE_VAL,DISCARDED_CNT,DISCARDED_VAL,BT-CONDITION,CONDITIONAL_SOURCE_CNT,CONDITIONAL_SOURCE_VAL,CONDITIONAL_DISCARDED_CNT,CONDITIONAL_DISCARDED_VAL,TRACK_EXTRA,DISCARDED_REMARK,BT_UNIT_TYPE,SOURCE_NEGATIVE_BALANCE,DISCARDED_NEGATIVE_BALANCE" > o_bal_file
        for(i in BT)
        {
                if(BT[i] in BT_DISCARDED_REMARK)
                {
                        for(j in BT_DISCARDED_REMARK[BT[i]])
                        {
                                final_BT_DISCARDED_REMARK[BT[i]]=final_BT_DISCARDED_REMARK[BT[i]] j "-" BT_DISCARDED_REMARK[BT[i]][j] ";" 
                        }
                }
                if(BT[i] in BT_TRACK_OFFER)
                {
                        for(x in BT_TRACK_OFFER[BT[i]])
                        {
                                final_BT_TRACK_OFFER[BT[i]]=final_BT_TRACK_OFFER[BT[i]] ";" x "-" BT_TRACK_OFFER[BT[i]][x]
                        }
                        final_BT_TRACK_OFFER[BT[i]] = final_BT_TRACK_OFFER[BT[i]] ";"
                }
                for(k in BT_CONDITIONAL_SOURCE_CNT[i])
                {
                        print BT[i],i,BT_SOURCE_CNT[BT[i]],BT_SOURCE_VAL[BT[i]],BT_DISCARDED_CNT[BT[i]],BT_DISCARDED_VAL[BT[i]],k,BT_CONDITIONAL_SOURCE_CNT[i][k],BT_CONDITIONAL_SOURCE_VAL[i][k],BT_CONDITIONAL_DISCARDED_CNT[i][k],BT_CONDITIONAL_DISCARDED_VAL[i][k],final_BT_TRACK_OFFER[BT[i]],final_BT_DISCARDED_REMARK[BT[i]],BT_UNIT_TYPE[BT[i]],BT_NEGATIVE_SOURCE_VAL[BT[i]],BT_NEGATIVE_DISCARDED_VAL[BT[i]] > o_bal_file
                }
                final_BT_DISCARDED_REMARK[BT[i]]=""
                final_BT_TRACK_OFFER[BT[i]]=""

        }

        print "Consolidating Source ProfileTag stats"
        print "PROFILE_TAG_NAME,SOURCE_CNT,DISCARDED_CNT,PT-CONDITION,CONDITIONAL_SOURCE_CNT,CONDITIONAL_DISCARDED_CNT,DISCARDED_REMARK,TRACK_GRACE" > o_pt_file
        for(i in PT)
        {
                if(i in PT_DISCARDED_REMARK)
                {
                        for(j in PT_DISCARDED_REMARK[i])
                        {
                                final_PT_DISCARDED_REMARK[i]=   final_PT_DISCARDED_REMARK[i] j "-" PT_DISCARDED_REMARK[i][j] ";"
                        }
                }
                if(i in PT_TRACK_OFFER)
                {
                        for(x in PT_TRACK_OFFER[i])
                        {
                                final_PT_TRACK_OFFER[i]=final_PT_TRACK_OFFER[i] ";" x "-" PT_TRACK_OFFER[i][x]
                        }
                        final_PT_TRACK_OFFER[i] = final_PT_TRACK_OFFER[i] ";"
                }
                for(k in PT_CONDITIONAL_SOURCE_CNT[i])
                {
                        print i,PT_SOURCE_CNT[i],PT_DISCARDED_CNT[i],k,PT_CONDITIONAL_SOURCE_CNT[i][k],PT_CONDITIONAL_DISCARDED_CNT[i][k],final_PT_DISCARDED_REMARK[i],final_PT_TRACK_OFFER[i] > o_pt_file
                }
        }

        print "Consolidating Source CUG stats"
        print "CUG_ID,SRC_COUNT,DISCARDED_COUNT,DISCARDED_REASON" > o_cug_file
        for(i in CUG_SOURCE_CNT)
        {
                if(i in CUG_DISCARDED_REMARK)
                {
                        for(j in CUG_DISCARDED_REMARK[i])
                        {
                                final_CUG_DISCARDED_REMARK[i]=  final_CUG_DISCARDED_REMARK[i] j "-" CUG_DISCARDED_REMARK[i][j] ";"
                        }
                }
                print i,CUG_SOURCE_CNT[i],CUG_DISCARDED_CNT[i],final_CUG_DISCARDED_REMARK[i] > o_cug_file
        }

        print "Consolidating Source FaF stats"
        print "FAF_TYPE,SOURCE_COUNT,DISCARDED_COUNT,DISCARDED_REMARK" > o_faf_file
        for(i in FAF_SOURCE_CNT)
        {
                if(i in FAF_DISCARDED_REMARK)
                {
                        for(j in FAF_DISCARDED_REMARK[i])
                        {
                                final_FAF_DISCARDED_REMARK[i]=  final_FAF_DISCARDED_REMARK[i] j "-" FAF_DISCARDED_REMARK[i][j] ";"
                        }
                }
                print i,FAF_SOURCE_CNT[i],FAF_DISCARDED_CNT[i],final_FAF_DISCARDED_REMARK[i] > o_faf_file
        }

        print "Consolidating Source SNA stats"
        print "SNA_SERIES-RATEPLANID,SOURCE_COUNT,DISCARDED_COUNT,DISCARDED_REMARK" > o_sna_file
        for(i in SNA_SOURCE_CNT)
        {
                if(i in SNA_DISCARDED_REMARK)
                {
                        for(j in SNA_DISCARDED_REMARK[i])
                        {
                                final_SNA_DISCARDED_REMARK[i]=  final_SNA_DISCARDED_REMARK[i] j "-" SNA_DISCARDED_REMARK[i][j] ";"
                        }
                }
                print i,SNA_SOURCE_CNT[i],SNA_DISCARDED_CNT[i],final_SNA_DISCARDED_REMARK[i] > o_sna_file
        }

        print "Consolidating Source CIS stats"
        print "BALANCE_TYPE_NAME,BT_ID,PROFILE_TAG,CONDITION,SOURCE_CNT,DISCARDED_CNT,DISCARDED_REMARK" > o_cis_file
        for(i in CIS_SOURCE_CNT)
        {
                if(i in CIS_DISCARDED_REMARK)
                {
                        for(j in CIS_DISCARDED_REMARK[i])
                        {
                                final_CIS_DISCARDED_REMARK[i]=  final_CIS_DISCARDED_REMARK[i] j "-" CIS_DISCARDED_REMARK[i][j] ";"
                        }
                }
                print i,CIS_SOURCE_CNT[i],CIS_DISCARDED_CNT[i],final_CIS_DISCARDED_REMARK[i] > o_cis_file
        }
}' $1/report/subscriber/rpt_*/*_source_state_stats.txt \
$1/report/subscriber/rpt_*/*_source_rateplan_stats.txt \
$1/report/subscriber/rpt_*/*_source_rateplan_state_stats.txt \
$1/report/subscriber/rpt_*/*_source_bal_stats.txt \
$1/report/subscriber/rpt_*/*_source_profiletag_stats.txt \
$1/report/subscriber/rpt_*/*_source_cug_stats.txt \
$1/report/subscriber/rpt_*/*_source_faf_stats.txt \
$1/report/subscriber/rpt_*/*_source_sna_stats.txt \
$1/report/subscriber/rpt_*/*_source_cis_stats.txt \
$1/report/subscriber/rpt_*/*_subs_rejection_stats.txt \
$1/report/subscriber/rpt_*/*_bt_rejection_stats.txt \
$1/report/subscriber/rpt_*/*_pt_rejection_stats.txt \
$1/report/subscriber/rpt_*/*_cug_rejection_stats.txt \
$1/report/subscriber/rpt_*/*_faf_rejection_stats.txt \
$1/report/subscriber/rpt_*/*_sna_rejection_stats.txt \
$1/report/subscriber/rpt_*/*_cis_rejection_stats.txt \
$1/report/subscriber/rpt_*/*_ignore_rateplan.txt \
$1/report/subscriber/rpt_*/*_unmatch_rateplan.txt \
$1/report/subscriber/rpt_*/*_unmatch_lfc.txt \
$1/report/subscriber/rpt_*/*_unmatch_cugid.txt 

awk -F, -v migtool_dir=$1 -v o_sna_file=$1"/report/subscriber/target_sna_stats.txt" -v o_sc_file=$1"/report/subscriber/target_sc_stats.txt" -v o_offer_file=$1"/report/subscriber/target_offer_stats.txt" -v o_state_file=$1"/report/subscriber/target_state_stats.txt" -v o_sc_state_file=$1"/report/subscriber/target_sc_state_stats.txt" -v o_cug_file=$1"/report/subscriber/target_cug_stats.txt" -v o_da_file=$1"/report/subscriber/target_da_stats.txt" -v o_uc_file=$1"/report/subscriber/target_uc_stats.txt" -v o_accm_file=$1"/report/subscriber/target_accm_stats.txt" -v o_faf_file=$1"/report/subscriber/target_faf_stats.txt" -v o_attr_file=$1"/report/subscriber/target_attr_stats.txt" -v o_cis_file=$1"/report/subscriber/target_cis_stats.txt" 'BEGIN{OFS=","}{
        if(FILENAME ~ /target_state_stats/)
        {
                if(FNR!=1)
                {
                        #print "STATE,TARGET_CNT" > o_state_file
                        STATE_TARGET_CNT[$1]+=$2
                }
        }
        else if(FILENAME ~ /target_sc_stats/)
        {
                if(FNR!=1)
                {
                        #print "SERVICE_CLASS,TARGET_CNT" > o_sc_file
                        SC_TARGET_CNT[$1]+=$2
                }
        }
        else if(FILENAME ~ /target_sc_state_stats/)
        {
                if(FNR!=1)
                {
                        #print "SERVICE_CLASS-STATE,TARGET_CNT" > o_sc_state_file
                        SCSTATE_TARGET_CNT[$1]+=$2
                }
        }
        else if(FILENAME ~ /target_offer_stats/)
        {
                if(FNR!=1)
                {
                        #print "OFFER_ID,TARGET_CNT,MASTER_MAPPED_CNT" > o_offer_file
                        OFFER_TARGET_CNT[$1]+=$2
                        OFFER_TARGET_MASTER_CNT[$1]+=$3
                }
        }
        else if(FILENAME ~ /target_attr_stats/)
        {
                if(FNR!=1)
                {
                        #print "OFFERID-ATTRNAME,TARGET_COUNT" > o_attr_file
                        ATTR_TARGET_CNT[$1]+=$2
                }
        }
        else if(FILENAME ~ /target_da_stats/)
        {
                if(FNR!=1)
                {
                        #print "DA_ID,TARGET_CNT,TARGET_BALANCE_TOTAL,ADD_DA_CNT,ADD_DA_BALANCE_TOTAL" > o_da_file
                        DA_TARGET_CNT[$1]+=$2
                        DA_TARGET_BAL[$1]+=$3
                        DA_TARGET_ADD_CNT[$1]+=$4
                        DA_TARGET_ADD_BAL[$1]+=$5
                }
        }
        else if(FILENAME ~ /target_uc_stats/)
        {
                if(FNR!=1)
                {
                        #print "UC_ID,TARGET_CNT,TARGET_VALUE_TOTAL,ADD_UC_CNT,ADD_UC_VALUE_TOTAL" > o_uc_file
                        UC_TARGET_CNT[$1]+=$2
                        UC_TARGET_VAL[$1]+=$3
                        UC_TARGET_ADD_CNT[$1]+=$4
                        UC_TARGET_ADD_VAL[$1]+=$5
                }
        }
        else if(FILENAME ~ /target_accm_stats/)
        {
                if(FNR!=1)
                {
                        #print "ACCM_ID,TARGET_CNT,TARGET_VALUE_TOTAL" > o_accm_file
                        UA_TARGET_CNT[$1]+=$2
                        UA_TARGET_VAL[$1]+=$3
                }
        }
        else if(FILENAME ~ /target_cug_stats/)
        {
                if(FNR!=1)
                {
                        #print "CUG_ID,TARGET_CUG_CNT" > o_cug_file
                        CUG_TARGET_CNT[$1]+=$2
                }
        }
        else if(FILENAME ~ /target_faf_stats/)
        {
                if(FNR!=1)
                {
                        #print "FAF_INDICATOR,TARGET_COUNT" > o_faf_file
                        FAF_TARGET_CNT[$1]+=$2
                }
        }
        else if(FILENAME ~ /target_sna_stats/)
        {
                if(FNR!=1)
                {
                        #print "SNASeries-RatePlanName,TARGET_SNA_COUNT" > o_sna_file
                        SNA_TARGET_CNT[$1]+=$2
                }
        }
        else if(FILENAME ~ /target_cis_stats/)
        {
                if(FNR!=1)
                {
                        #print "BUNDLE_TYPE-PRODUCTID",TARGET_COUNT" > o_cis_file
                        CIS_TARGET_CNT[$1"-"$2]+=$3
                        BUNDLE_CNT[$1]+=$3
                }
        }
}END{
        print "Consolidating Target SERVICE_CLASS stats"
        print "STATE,TARGET_CNT" > o_state_file
        for(i in STATE_TARGET_CNT)
        {
                print i,STATE_TARGET_CNT[i]  > o_state_file
        }

        print "Consolidating Target SERVICE_CLASS stats"
        print "SERVICE_CLASS,TARGET_CNT" > o_sc_file
        for(i in SC_TARGET_CNT)
        {
                print i,SC_TARGET_CNT[i]  > o_sc_file
        }

        print "Consolidating Target SERVICE_CLASS-STATE stats"
        print "SERVICE_CLASS-STATE,TARGET_CNT" > o_sc_state_file
        for(i in SCSTATE_TARGET_CNT)
        {
                print i,SCSTATE_TARGET_CNT[i]  > o_sc_state_file
        }

        print "Consolidating Target OFFER stats"
        print "OFFER_ID,TARGET_CNT,MASTER_MAPPED_CNT" > o_offer_file
        for(i in OFFER_TARGET_CNT)
        {
                print i,OFFER_TARGET_CNT[i],OFFER_TARGET_MASTER_CNT[i]  > o_offer_file
        }

        print "Consolidating Target OFFER-ATTRIBUITE stats"
        print "OFFERID-ATTRNAME,TARGET_COUNT" > o_attr_file
        for(i in ATTR_TARGET_CNT)
        {
                print i,ATTR_TARGET_CNT[i]  > o_attr_file
        }

        print "Consolidating Target DA stats"
        print "DA_ID,TARGET_CNT,TARGET_BALANCE_TOTAL,ADD_DA_CNT,ADD_DA_BALANCE_TOTAL" > o_da_file
        for(i in DA_TARGET_CNT)
        {
                print i,DA_TARGET_CNT[i],DA_TARGET_BAL[i],DA_TARGET_ADD_CNT[i],DA_TARGET_ADD_BAL[i]  > o_da_file
        }

        print "Consolidating Target UC stats"
        print "UC_ID,TARGET_CNT,TARGET_VALUE_TOTAL,ADD_UC_CNT,ADD_UC_VALUE_TOTAL" > o_uc_file
        for(i in UC_TARGET_CNT)
        {
                print i,UC_TARGET_CNT[i],UC_TARGET_VAL[i],UC_TARGET_ADD_CNT[i],UC_TARGET_ADD_VAL[i]  > o_uc_file
        }

        print "Consolidating Target UA stats"
        print "ACCM_ID,TARGET_CNT,TARGET_VALUE_TOTAL" > o_accm_file
        for(i in UA_TARGET_CNT)
        {
                print i,UA_TARGET_CNT[i],UA_TARGET_VAL[i]  > o_accm_file
        }

        print "Consolidating Target CUG stats"
        print "CUG_ID,TARGET_CUG_CNT" > o_cug_file
        for(i in CUG_TARGET_CNT)
        {
                print i,CUG_TARGET_CNT[i]  > o_cug_file
        }

        print "Consolidating Target FAF stats"
        print "FAF_INDICATOR,TARGET_COUNT" > o_faf_file
        for(i in FAF_TARGET_CNT)
        {
                print i,FAF_TARGET_CNT[i]  > o_faf_file
        }

        print "Consolidating Target SNA stats"
        print "SNASeries-RatePlanName,TARGET_SNA_COUNT" > o_sna_file
        for(i in SNA_TARGET_CNT)
        {
                print i,SNA_TARGET_CNT[i]  > o_sna_file
        }

        print "Consolidating Target CIS stats"
        print "BUNDLE_TYPE,PRODUCTID,TARGET_COUNT" > o_cis_file
        for(i in CIS_TARGET_CNT)
        {
                print i,CIS_TARGET_CNT[i]  > o_cis_file
        }
        for(i in BUNDLE_CNT)
        {
                print i,BUNDLE_CNT[i] > migtool_dir"/report/subscriber/cis_bundle_stats.txt"
        }
}' $1/report/subscriber/rpt_*/*target_state_stats.txt \
$1/report/subscriber/rpt_*/*target_sc_stats.txt \
$1/report/subscriber/rpt_*/*target_sc_state_stats.txt \
$1/report/subscriber/rpt_*/*target_offer_stats.txt \
$1/report/subscriber/rpt_*/*target_attr_stats.txt \
$1/report/subscriber/rpt_*/*target_da_stats.txt \
$1/report/subscriber/rpt_*/*target_uc_stats.txt \
$1/report/subscriber/rpt_*/*target_accm_stats.txt \
$1/report/subscriber/rpt_*/*target_cug_stats.txt \
$1/report/subscriber/rpt_*/*target_faf_stats.txt \
$1/report/subscriber/rpt_*/*target_sna_stats.txt \
$1/report/subscriber/rpt_*/*target_cis_stats.txt
