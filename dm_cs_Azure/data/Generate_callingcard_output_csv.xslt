<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:dm="http://www.ericsson.com/datamigration"
	exclude-result-prefixes="xs dm">
	
	<xsl:output method="text" omit-xml-declaration="yes" encoding="utf-8" indent="yes"/>
	<xsl:strip-space elements="*"/>
	
	
	<!-- ************************* -->
	<!--  CONSTANTS -->
	<!-- ************************* -->

	<xsl:variable name="ROW_SEPARATOR" select="'##'"/>
	<xsl:variable name="FILE_SEPARATOR" select="'#@@#'"/>
	<xsl:variable name="SDP_DELIMITER" select="','"/>
	<xsl:variable name="DEFAULT_VALUE_ZERO" select="'0'"/>
	<xsl:variable name="DEFAULT_VALUE_ONE" select="'1'"/>
	<xsl:variable name="NULL" select="'NULL'"/>
	<xsl:variable name="EMPTY_STRING" select="''"/>
	
	<!-- Uncomment or testing -->
	<xsl:template match="OUTPUT">
		<xsl:call-template name="generate_output_csv">
			<xsl:with-param name="SUBSCRIBER_RECORD" select="SUBSCRIBER_RECORD"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="generate_output_csv" >
		<xsl:param name="SUBSCRIBER_RECORD"/>
		
		<!-- GENERATE ROW FOR ACCOUNT -->
		<xsl:variable name="record" select="$SUBSCRIBER_RECORD/SDP_DATA/ACCOUNT"/>
		<xsl:if test="$record">
			<xsl:value-of select="concat($record/Account_MSISDN,$SDP_DELIMITER,
										 $record/account_class,$SDP_DELIMITER,
										 $record/orig_account_class,$SDP_DELIMITER,
									 	 $record/account_class_expiry,$SDP_DELIMITER,
										 $record/units,$SDP_DELIMITER,
										 $record/activated,$SDP_DELIMITER,
										 $record/sfee_expiry_date,$SDP_DELIMITER,
										 $record/sup_expiry_date,$SDP_DELIMITER,
										 $record/sfee_done_date,$SDP_DELIMITER,
										 $record/previous_sfee_done_date,$SDP_DELIMITER,
										 $record/sfee_status,$SDP_DELIMITER,
										 $record/sup_status,$SDP_DELIMITER,
										 $record/neg_balance_start,$SDP_DELIMITER,
										 $record/neg_balance_barred,$SDP_DELIMITER,
										 $record/account_disconnect,$SDP_DELIMITER,
										 $record/account_status,$SDP_DELIMITER,
										 $record/prom_notification,$SDP_DELIMITER,
										 $record/service_offerings,$SDP_DELIMITER,
										 $record/account_group_id,$SDP_DELIMITER,
										 $record/community_id1,$SDP_DELIMITER,
										 $record/community_id2,$SDP_DELIMITER,
										 $record/community_id3,$SDP_DELIMITER,
										 $record/account_home_region,$SDP_DELIMITER,
										 $record/account_lock,$SDP_DELIMITER,										 
										 $record/product_id_counter)"/>
		</xsl:if>
		
		<xsl:value-of select="$FILE_SEPARATOR"/>
		
		<!-- GENERATE ROW FOR SUBSCRIBER -->
		<xsl:variable name="record" select="$SUBSCRIBER_RECORD/SDP_DATA/SUBSCRIBER"/>		
		<xsl:variable name="msisdn" select="$record/Subscriber_MSISDN"/>
		<xsl:if test="$record">
			<xsl:value-of select="concat($record/Subscriber_MSISDN,$SDP_DELIMITER,
				$record/Account_MSISDN,$SDP_DELIMITER,
				$record/subscriber_status,$SDP_DELIMITER,
				$record/refill_failed,$SDP_DELIMITER,
				$record/refill_bar_end,$SDP_DELIMITER,
				$record/first_ivr_call_done,$SDP_DELIMITER,
				$record/first_call_done,$SDP_DELIMITER,
				$record/language,$SDP_DELIMITER,
				$record/special_announc_played,$SDP_DELIMITER,
				$record/sfee_warn_played,$SDP_DELIMITER,
				$record/sup_warn_played,$SDP_DELIMITER,
				$record/low_level_warn_played,$SDP_DELIMITER,
				$record/wanted_block_status,$SDP_DELIMITER,
				$record/actual_block_status,$SDP_DELIMITER,
				$record/eoc_selection_id,$SDP_DELIMITER,
				$record/pin_code,$SDP_DELIMITER,
				$record/usage_statistic_flags)"/>
		</xsl:if>
		
		<xsl:value-of select="$FILE_SEPARATOR"/>
		
		<!-- GENERATE ROW(S) FOR DEDICATED ACCOUNTS -->
		<xsl:variable name="sorted_da_set">
			<DA_SORTED>
				<xsl:for-each select="$SUBSCRIBER_RECORD/SDP_DATA/DEDICATED_ACCOUNT_LIST/DEDICATED_ACCOUNT">
					<xsl:sort select="id" data-type="number"/>
					<xsl:sequence select="."/>
				</xsl:for-each>
			</DA_SORTED>
		</xsl:variable>
		<xsl:variable name="record_set" select="$sorted_da_set"/>
		
		
		<!-- GENERATE ROW(S) FOR DEDICATED ACCOUNTS -->
		<xsl:if test="$record_set and (count($record_set) &gt; 0)">
			<xsl:variable name="msisdn" select="$SUBSCRIBER_RECORD/SDP_DATA/DEDICATED_ACCOUNT_LIST/Account_MSISDN"/>
			<xsl:variable name="total_SequenceId" select="xs:integer(floor(((count($record_set) - 1) div 10))+1)" />
			<xsl:for-each select="1 to $total_SequenceId">
				<xsl:variable name="sequenceId" select="position()"/>
				<xsl:if test="$sequenceId &gt; 1">
					<xsl:value-of select="$ROW_SEPARATOR"/>
				</xsl:if>
				<xsl:value-of select="concat($msisdn,$SDP_DELIMITER,$sequenceId)"/>
				<xsl:for-each select="1 to 10">
					
					<xsl:variable name="index" select="($sequenceId - 1)*10 + position()"/>
					<xsl:choose>
						<xsl:when test="$record_set[$index]">
							<xsl:value-of select="concat
								(
								$SDP_DELIMITER,$record_set[$index]/DA_SORTED/DEDICATED_ACCOUNT/id,
								$SDP_DELIMITER,$record_set[$index]/DA_SORTED/DEDICATED_ACCOUNT/balance,
								$SDP_DELIMITER,$record_set[$index]/DA_SORTED/DEDICATED_ACCOUNT/expiry_date
								)"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="concat
								(
								$SDP_DELIMITER,$DEFAULT_VALUE_ZERO,
								$SDP_DELIMITER,$DEFAULT_VALUE_ZERO,
								$SDP_DELIMITER,$NULL
								)"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:for-each>
				
				<xsl:value-of select="if ($record_set[10]) 
					then concat($SDP_DELIMITER,$record_set[10]/DA_SORTED/DEDICATED_ACCOUNT/start_date)
					else concat($SDP_DELIMITER,$NULL)"/>
				
				<xsl:for-each select="1 to 4">
					<xsl:variable name="index" select="($sequenceId - 1)*10 + position()"/>
					<xsl:choose>
						<xsl:when test="$record_set[$index]">
							<xsl:value-of select="concat($SDP_DELIMITER,$record_set[$index]/DA_SORTED/DEDICATED_ACCOUNT/product_id)"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="concat($SDP_DELIMITER,$DEFAULT_VALUE_ZERO)"/>
						</xsl:otherwise>
					</xsl:choose>
					<!--<xsl:value-of select="concat($SDP_DELIMITER,$NULL)"/>-->
				</xsl:for-each>
				<xsl:value-of select="if ($record_set[1]) 
					then concat($SDP_DELIMITER,$record_set[1]/DA_SORTED/DEDICATED_ACCOUNT/start_date)
					else concat($SDP_DELIMITER,$NULL)"/>
				
				<xsl:value-of select="if ($record_set[10]) 
					then concat($SDP_DELIMITER,$record_set[10]/DA_SORTED/DEDICATED_ACCOUNT/product_id)
					else concat($SDP_DELIMITER,$DEFAULT_VALUE_ZERO)"/>
				
				<xsl:value-of select="if ($record_set[5]) 
					then concat($SDP_DELIMITER,$record_set[5]/DA_SORTED/DEDICATED_ACCOUNT/product_id)
					else concat($SDP_DELIMITER,$DEFAULT_VALUE_ZERO)"/>
				
				<xsl:value-of select="if ($record_set[2]) 
					then concat($SDP_DELIMITER,$record_set[2]/DA_SORTED/DEDICATED_ACCOUNT/start_date)
					else concat($SDP_DELIMITER,$NULL)"/>
				
				<xsl:value-of select="if ($record_set[6]) 
					then concat($SDP_DELIMITER,$record_set[6]/DA_SORTED/DEDICATED_ACCOUNT/product_id)
					else concat($SDP_DELIMITER,$DEFAULT_VALUE_ZERO)"/>
				
				<xsl:value-of select="if ($record_set[3]) 
					then concat($SDP_DELIMITER,$record_set[3]/DA_SORTED/DEDICATED_ACCOUNT/start_date)
					else concat($SDP_DELIMITER,$NULL)"/>
				
				<xsl:value-of select="if ($record_set[7]) 
					then concat($SDP_DELIMITER,$record_set[7]/DA_SORTED/DEDICATED_ACCOUNT/product_id)
					else concat($SDP_DELIMITER,$DEFAULT_VALUE_ZERO)"/>
				
				<xsl:value-of select="if ($record_set[4]) 
					then concat($SDP_DELIMITER,$record_set[4]/DA_SORTED/DEDICATED_ACCOUNT/start_date)
					else concat($SDP_DELIMITER,$NULL)"/>
				
				<xsl:value-of select="if ($record_set[8]) 
					then concat($SDP_DELIMITER,$record_set[8]/DA_SORTED/DEDICATED_ACCOUNT/product_id)
					else concat($SDP_DELIMITER,$DEFAULT_VALUE_ZERO)"/>
				
				<xsl:value-of select="if ($record_set[5]) 
					then concat($SDP_DELIMITER,$record_set[5]/DA_SORTED/DEDICATED_ACCOUNT/start_date)
					else concat($SDP_DELIMITER,$NULL)"/>
				
				<xsl:value-of select="if ($record_set[9]) 
					then concat($SDP_DELIMITER,$record_set[9]/DA_SORTED/DEDICATED_ACCOUNT/product_id)
					else concat($SDP_DELIMITER,$DEFAULT_VALUE_ZERO)"/>
				
				<xsl:value-of select="if ($record_set[6]) 
					then concat($SDP_DELIMITER,$record_set[6]/DA_SORTED/DEDICATED_ACCOUNT/start_date)
					else concat($SDP_DELIMITER,$NULL)"/>				
				<xsl:value-of select="if ($record_set[7]) 
					then concat($SDP_DELIMITER,$record_set[7]/DA_SORTED/DEDICATED_ACCOUNT/start_date)
					else concat($SDP_DELIMITER,$NULL)"/>
				<xsl:value-of select="if ($record_set[8]) 
					then concat($SDP_DELIMITER,$record_set[8]/DA_SORTED/DEDICATED_ACCOUNT/start_date)
					else concat($SDP_DELIMITER,$NULL)"/>
				<xsl:value-of select="if ($record_set[9]) 
					then concat($SDP_DELIMITER,$record_set[9]/DA_SORTED/DEDICATED_ACCOUNT/start_date)
					else concat($SDP_DELIMITER,$NULL)"/>
				
				<xsl:for-each select="1 to 10">
					<xsl:value-of select="concat($SDP_DELIMITER,$NULL)"/>
				</xsl:for-each>
			</xsl:for-each>
		</xsl:if>
		
		<xsl:value-of select="$FILE_SEPARATOR"/>
		
		<xsl:variable name="record" select="$SUBSCRIBER_RECORD/SDP_DATA/calling_card"/>
		<xsl:if test="$record">
			<xsl:value-of select="concat($record/serial_number,$SDP_DELIMITER,
				$record/card_number,$SDP_DELIMITER,
				$record/activation,$SDP_DELIMITER,
				$record/sub_status,$SDP_DELIMITER,
				$record/service_class)"/>
		</xsl:if>
		
		<xsl:value-of select="$FILE_SEPARATOR"/>
		
		<!-- GENERATE ROW(S) FOR PAM -->
		<xsl:variable name="record_set" select="$SUBSCRIBER_RECORD/SDP_DATA/PERIODIC_ACCOUNT_LIST/PAM"/>
		<xsl:variable name="msisdn_pam" select="$SUBSCRIBER_RECORD/SDP_DATA/PERIODIC_ACCOUNT_LIST/Account_MSISDN"/>
		<!--CREATE A FILE AND NEW ROW ONLY IF AT LEAST ONE RECORD-->
		<xsl:if test="$record_set and (count($record_set) &gt; 0)">
			<xsl:for-each select="$record_set">
				<xsl:if test="position() gt 1">
					<xsl:value-of select="$ROW_SEPARATOR"/>
				</xsl:if>
				<xsl:variable name="current_pam" select="."/>
				<xsl:value-of select="concat($msisdn_pam,$SDP_DELIMITER,
					$current_pam/pam_service_id,$SDP_DELIMITER,
					$current_pam/pam_class_id,$SDP_DELIMITER,
					$current_pam/schedule_id,$SDP_DELIMITER,
					$current_pam/current_pam_period,$SDP_DELIMITER,
					$current_pam/deferred_to_date,$SDP_DELIMITER,
					$current_pam/last_evaluation_date,$SDP_DELIMITER,
					$current_pam/priority)"/>
			</xsl:for-each>
		</xsl:if>
				
	</xsl:template>
</xsl:stylesheet>
