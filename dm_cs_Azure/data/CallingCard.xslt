<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:dm="http://www.ericsson.com/datamigration"
	exclude-result-prefixes="dm xs">

	<xsl:import href="Generate_callingcard_output_csv.xslt"/>
	
	<xsl:output method="text" omit-xml-declaration="yes" encoding="utf-8" indent="yes"/>
	<xsl:strip-space elements="*"/>

	<!-- ************************* -->
	<!--  CONSTANTS -->
	<!-- ************************* -->
	
	<xsl:variable name="CommonConfigPath" select="'CommonConfigMapping.xml'"/>
	<xsl:variable name="ConversionFactor" select="'ConversionFactorMapping.xml'"/>
	<xsl:variable name="LanguageMapping" select="'LanguageMapping.xml'"/>
	<xsl:variable name="ServiceClassMapping" select="'ServiceClassMapping.xml'"/>
	<xsl:variable name="LifeCycleCallingCard" select="'LifeCycleCallingCardMapping.xml'"/>
	<xsl:variable name="OutputFilesMapping" select="'OutputFilesMapping.xml'"/>
	<xsl:variable name="DefaultServicesMapping" select="'DefaultServicesMapping.xml'"/>
	<xsl:variable name="LoggingMapping" select="'LoggingMapping.xml'"/>
	
	<xsl:variable name="docCommonConfigPath" select="document($CommonConfigPath)"/>
	<xsl:variable name="docConversionFactor" select="document($ConversionFactor)"/>
	<xsl:variable name="docLanguageMapping" select="document($LanguageMapping)"/>
	<xsl:variable name="docServiceClassMapping" select="document($ServiceClassMapping)"/>
	<xsl:variable name="docLifeCycleCallingCard" select="document($LifeCycleCallingCard)"/>
	<xsl:variable name="docOutputFilesMapping" select="document($OutputFilesMapping)"/>
	<xsl:variable name="docDefaultServices" select="document($DefaultServicesMapping)"/>
	<xsl:variable name="docLoggingMapping" select="document($LoggingMapping)"/>
	
	<xsl:variable name="CallingCardNameList">
		<xsl:for-each select="$docServiceClassMapping/SERVICE_CLASS_LIST/SERVICE_CLASS_INFO[Type eq 'Calling Card']">
			<xsl:value-of select="concat(CCS_ACCT_TYPE_NAME,';')"/>
		</xsl:for-each>
	</xsl:variable>
	
	
	<xsl:variable name="ROOT_TAG" select="/CALLING_CARD"/>
	
	<xsl:variable name="DT_FRMT_Y0001_M01_D01" select="format-date(current-date(), '[Y0001]-[M01]-[D01]')"/>
	<xsl:variable name="DT_FRMT_M01_D01_Y0001_H01_m01_s01" select="'[M01]/[D01]/[Y0001] [H01]:[m01]:[s01]'"/>
	<xsl:variable name="DT_FRMT_Y0001_M01" select="format-date(current-date(), '[Y0001]_[MNn,*-3]')"/>
	<xsl:variable name="DAYS_TO_MIG_DATE_DAY_1" select="dm:dateToNumberOfDays(xs:date(format-date(current-date(), '[Y0001]-[M01]-01')))"/>
	<xsl:variable name="UTC_OFFSET" select="'+0200'"/>
	<xsl:variable name="GMT_DIFF" as="xs:decimal" select="-25200"/>
	<xsl:variable name="EXPIRY_TIME" as="xs:decimal" select="86399"/>
	<xsl:variable name="DEFAULT_EXPIRY_TR" select="'T23:59:59'"/>
	<xsl:variable name="DEFAULT_START_TR" select="'T00:00:00'"/>
	
	<!--Used for String to hexadecimal conversion	-->
	<xsl:variable name="ascii"> !"#$%&amp;'()*+,-./0123456789:;&lt;=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\]^_`abcdefghijklmnopqrstuvwxyz{|}~</xsl:variable>
	<xsl:variable name="hex" >0123456789ABCDEF</xsl:variable>

	<!-- ************************* -->
	<!--  VARIABLES (CAN BE MODIFIED) -->
	<!-- ************************* -->

	<!-- MIGRATION DATE variables -->
	<xsl:variable name="MIGRATION_DATE" select="current-date()"/>
	<xsl:variable name="MIG_DATE_TEL" select="format-date($MIGRATION_DATE, '[Y0001][M01][D01]')"/>
	<xsl:variable name="DAYS_TO_MIG_DATE" select="dm:dateToNumberOfDays($MIGRATION_DATE)"/>
	
	<xsl:variable name="DEFAULT_VALUE_TEN" select="'10'"/>

	<!--Below parameters used to fetch from common config sheet,If changed in mapping sheet,it has to be changed	-->
	<xsl:variable name="Wanted_decimal_precision" select="$docCommonConfigPath/COMMON_CONFIG_MAPPING_LIST/COMMON_CONFIG_MAPPING_INFO[Element = 'wanted_decimal_precision']/Value"/>
	<xsl:variable name="default_EMPTY" select="$docCommonConfigPath/COMMON_CONFIG_MAPPING_LIST/COMMON_CONFIG_MAPPING_INFO[Element = 'default_EMPTY']/Value"/>
	<xsl:variable name="default_SDP_ID" select="$docCommonConfigPath/COMMON_CONFIG_MAPPING_LIST/COMMON_CONFIG_MAPPING_INFO[Element = 'default_SDP_ID']/Value"/>
	<xsl:variable name="default_usage_statistic_flags" select="$docCommonConfigPath/COMMON_CONFIG_MAPPING_LIST/COMMON_CONFIG_MAPPING_INFO[Element = 'default_usage_statistic_flags']/Value"/>
	<xsl:variable name="default_ZERO" select="$docCommonConfigPath/COMMON_CONFIG_MAPPING_LIST/COMMON_CONFIG_MAPPING_INFO[Element = 'default_ZERO']/Value"/>
	<xsl:variable name="default_ONE" select="$docCommonConfigPath/COMMON_CONFIG_MAPPING_LIST/COMMON_CONFIG_MAPPING_INFO[Element = 'default_ONE']/Value"/>
	<xsl:variable name="default_language" select="$docCommonConfigPath/COMMON_CONFIG_MAPPING_LIST/COMMON_CONFIG_MAPPING_INFO[Element = 'default_language']/Value"/>
	<xsl:variable name="default_NULL" select="$docCommonConfigPath/COMMON_CONFIG_MAPPING_LIST/COMMON_CONFIG_MAPPING_INFO[Element = 'default_NULL']/Value"/>

	<!--	<xsl:variable name="MAX_DATE" select="'2037-12-31'"/>-->
	
	<xsl:variable name="targetSystemBalancePrecision" select="dm:power(xs:integer($DEFAULT_VALUE_TEN), xs:integer($Wanted_decimal_precision))"/>

	<xsl:variable name="SERIAL_NUMBER" select="/CALLING_CARD/SERIAL_NUMBER"/>
	<xsl:variable name="VOUCHER_TYPE" select="/CALLING_CARD/VOUCHER_TYPE"/>
	<xsl:variable name="ACTIVATION_DATE" select="/CALLING_CARD/ACTIVATION_DATE"/>
	<xsl:variable name="BALANCE_EXPIRY" select="/CALLING_CARD/BALANCE_EXPIRY"/>
	<xsl:variable name="BALANCE_VALUE" select="/CALLING_CARD/BALANCE_VALUE"/>
	<xsl:variable name="HRN" select="/CALLING_CARD/HRN"/>
	<xsl:variable name="STATE" select="/CALLING_CARD/STATE"/>
	<!-- =========================================================================== -->
	<!-- =========================================================================== -->
	
	<!-- ************************* -->
	<!--  MAPPING TEMPLATES -->
	<!-- ************************* -->

	<xsl:template match="CALLING_CARD">
		<xsl:variable name="OUTPUT">
			<xsl:call-template name="ValidateInputs"/>
			
			<SUBSCRIBER_RECORD>
				<!-- CALL TEMPLATES FOR CSV GENERATION -->
				<SDP_DATA>
					<!-- ........................... -->
					<!-- *-*-*-*-*- SDP -*-*-*-*-*-* -->
					<!-- ........................... -->
					<xsl:variable name="Account_Flag">
						<xsl:value-of select="$docOutputFilesMapping/OUTPUT_MAPPING_LIST/OUTPUT_MAPPING_INFO[Filename eq 'Account.csv']/Calling_Card_Generation" />
					</xsl:variable>
					<xsl:if test="$Account_Flag eq 'Y'">
						<xsl:call-template name="generate_Account.csv"/>
						<xsl:call-template name="generate_prepaid_callingcard.csv"/>
					</xsl:if>
					<xsl:variable name="Subscriber_Flag">
						<xsl:value-of select="$docOutputFilesMapping/OUTPUT_MAPPING_LIST/OUTPUT_MAPPING_INFO[Filename eq 'Subscriber.csv']/Calling_Card_Generation" />
					</xsl:variable>
					<xsl:if test="$Subscriber_Flag eq 'Y'">
						<xsl:call-template name="generate_Subscriber.csv"/>
					</xsl:if>
					<xsl:variable name="Dedicated_Flag">
						<xsl:value-of select="$docOutputFilesMapping/OUTPUT_MAPPING_LIST/OUTPUT_MAPPING_INFO[Filename eq 'DedicatedAccount.csv']/Calling_Card_Generation" />
					</xsl:variable>
					<xsl:if test="$Dedicated_Flag eq 'Y'">
						<xsl:call-template name="generate_DedicatedAccounts.csv"/>
						</xsl:if>
					<xsl:variable name="PamAccount_Flag">
						<xsl:value-of select="$docOutputFilesMapping/OUTPUT_MAPPING_LIST/OUTPUT_MAPPING_INFO[Filename eq 'PamAccount.csv']/Calling_Card_Generation" />
					</xsl:variable>
					<xsl:if test="$PamAccount_Flag eq 'Y'">
						<xsl:call-template name="generate_PamAccounts.csv"/>
					</xsl:if>
					<!--<xsl:variable name="Offer_Flag">
						<xsl:value-of select="$docOutputFilesMapping/OUTPUT_MAPPING_LIST/OUTPUT_MAPPING_INFO[Filename eq 'Offer.csv']/Flag" />
					</xsl:variable>
					<xsl:if test="$Offer_Flag eq 'Y'">
						<xsl:call-template name="generate_Offer.csv"/>
					</xsl:if>
					<xsl:variable name="OfferAttributes_Flag">						
						<xsl:value-of select="$docOutputFilesMapping/OUTPUT_MAPPING_LIST/OUTPUT_MAPPING_INFO[Filename eq 'OfferAttribute.csv']/Flag"/>
					</xsl:variable>		
					<xsl:if test="$OfferAttributes_Flag eq 'Y'">						
						<xsl:call-template name="generate_OfferAttributes.csv"/>
					</xsl:if>
					<xsl:variable name="Accumulator_Flag">						
						<xsl:value-of select="$docOutputFilesMapping/OUTPUT_MAPPING_LIST/OUTPUT_MAPPING_INFO[Filename eq 'Accumulator.csv']/Flag"/>
					</xsl:variable>
					<xsl:if test="$Accumulator_Flag eq 'Y'">						
						<xsl:call-template name="generate_Accumulator.csv"/>
					</xsl:if>
					<xsl:variable name="UC_Flag">						
						<xsl:value-of select="$docOutputFilesMapping/OUTPUT_MAPPING_LIST/OUTPUT_MAPPING_INFO[Filename eq 'UsageCounter.csv']/Flag"/>
					</xsl:variable>
					<xsl:if test="$UC_Flag eq 'Y'">
						<xsl:call-template name="generate_UsageCounter.csv"/>
					</xsl:if>					-->
				</SDP_DATA>
			</SUBSCRIBER_RECORD>
		</xsl:variable>

		
		<!-- ************************* -->
		<!--  CSV OUTPUT DISTRIBTION  -->
		<!-- ************************* -->
		
		<xsl:call-template name="generate_output_csv">
			<xsl:with-param name="SUBSCRIBER_RECORD" select="$OUTPUT/SUBSCRIBER_RECORD"/>
		</xsl:call-template>

	</xsl:template>

	<!-- ************************* -->
	<!--  INPUT VALIDATION TEMPLATE-->
	<!-- ************************* -->

	<xsl:template name="ValidateInputs">
		<xsl:variable name="CallingCardName" select="$docServiceClassMapping/SERVICE_CLASS_LIST/SERVICE_CLASS_INFO[CCS_ACCT_TYPE_ID eq $VOUCHER_TYPE]/CCS_ACCT_TYPE_NAME"/>
		<xsl:choose>
			<xsl:when test="string-length($CallingCardName) eq 0">
				<xsl:call-template name="dm:addIncHCoded">
					<xsl:with-param name="errorCode" select="'INC8001'"/>
					<xsl:with-param name="errorString1" select="'SERIAL_NUMBER'"/>
					<xsl:with-param name="errorField1" select="$SERIAL_NUMBER"/>
					<xsl:with-param name="errorString2" select="'VOUCHER_TYPE'"/>
					<xsl:with-param name="errorField2" select="$VOUCHER_TYPE"/>
					<xsl:with-param name="errorString3" select="'STATE'"/>
					<xsl:with-param name="errorField3" select="$STATE"/>
					<xsl:with-param name="errorString4" select="'BALANCE_VALUE'"/>
					<xsl:with-param name="errorField4" select="$BALANCE_VALUE"/>
				</xsl:call-template>
			</xsl:when>	
		</xsl:choose>
		<xsl:choose>
			<xsl:when test="number($BALANCE_VALUE) &lt; 0">
				<xsl:call-template name="dm:addIncHCoded">
					<xsl:with-param name="errorCode" select="'INC8002'"/>
					<xsl:with-param name="errorString1" select="'SERIAL_NUMBER'"/>
					<xsl:with-param name="errorField1" select="$SERIAL_NUMBER"/>
					<xsl:with-param name="errorString2" select="'VOUCHER_TYPE'"/>
					<xsl:with-param name="errorField2" select="$VOUCHER_TYPE"/>
					<xsl:with-param name="errorString3" select="'STATE'"/>
					<xsl:with-param name="errorField3" select="$STATE"/>
					<xsl:with-param name="errorString4" select="'BALANCE_VALUE'"/>
					<xsl:with-param name="errorField4" select="$BALANCE_VALUE"/>
				</xsl:call-template>
			</xsl:when>
		</xsl:choose>
		<xsl:choose>
			<xsl:when test="number($BALANCE_VALUE) &lt; 0">
				<xsl:call-template name="dm:addIncHCoded">
					<xsl:with-param name="errorCode" select="'INC8003'"/>
					<xsl:with-param name="errorString1" select="'SERIAL_NUMBER'"/>
					<xsl:with-param name="errorField1" select="$SERIAL_NUMBER"/>
					<xsl:with-param name="errorString2" select="'VOUCHER_TYPE'"/>
					<xsl:with-param name="errorField2" select="$VOUCHER_TYPE"/>
					<xsl:with-param name="errorString3" select="'STATE'"/>
					<xsl:with-param name="errorField3" select="$STATE"/>
					<xsl:with-param name="errorString4" select="'BALANCE_VALUE'"/>
					<xsl:with-param name="errorField4" select="$BALANCE_VALUE"/>
					<xsl:with-param name="errorString5" select="'BALANCE_EXPIRY'"/>
					<xsl:with-param name="errorField5" select="$BALANCE_EXPIRY"/>
				</xsl:call-template>
			</xsl:when>
		</xsl:choose>
	</xsl:template>


	<!---=================================================-->
	<!-- Final computation for the mapping sheet -->
	
	<!---=================================================-->
					<!--Account.csv-->
	<!---=================================================-->
	
	<xsl:template name="generate_Account.csv">
		<ACCOUNT>
			<Account_MSISDN>
				<xsl:value-of select="$SERIAL_NUMBER"/>
			</Account_MSISDN>
			<account_class>
				<xsl:value-of select="$docServiceClassMapping/SERVICE_CLASS_LIST/SERVICE_CLASS_INFO[CCS_ACCT_TYPE_ID eq $VOUCHER_TYPE]/Target "/>
			</account_class>
			<orig_account_class>
				<xsl:value-of select="$docServiceClassMapping/SERVICE_CLASS_LIST/SERVICE_CLASS_INFO[CCS_ACCT_TYPE_ID eq $VOUCHER_TYPE]/Target "/>
			</orig_account_class>
			<account_class_expiry>
				<xsl:value-of select="$default_NULL"/>
			</account_class_expiry>
			<units>
				<xsl:value-of select="$default_ZERO"/>
			</units>
			<activated>
				<xsl:message select="$ACTIVATION_DATE"/>
				<xsl:value-of select="dm:dateToNumberOfDays(dm:convertTELDateToXSLTDate($ACTIVATION_DATE))"/>
			</activated>
			<sfee_expiry_date>
				<xsl:call-template name="RULE_SFEE_DATE"/>
			</sfee_expiry_date>
			<sup_expiry_date>
				<xsl:call-template name="RULE_SUP_DATE"/>
			</sup_expiry_date>
			<sfee_done_date>
				<xsl:value-of select="$docCommonConfigPath/COMMON_CONFIG_MAPPING_LIST/COMMON_CONFIG_MAPPING_INFO[Element eq  'Calling_card_default_CC_SFEE_DONE_DATE']/Value"/>
			</sfee_done_date>
			<previous_sfee_done_date>
				<xsl:value-of select="$docCommonConfigPath/COMMON_CONFIG_MAPPING_LIST/COMMON_CONFIG_MAPPING_INFO[Element eq  'Calling_card_default_CC_PREVIOUS_SFEE_DONE_DATE']/Value"/>				
			</previous_sfee_done_date>
			<sfee_status>
				<xsl:call-template name="RULE_SFEE_STATUS"/>
			</sfee_status>
			<sup_status>
				<xsl:call-template name="RULE_SUP_STATUS"/>
			</sup_status>
			<neg_balance_start>
				<xsl:value-of select="$docCommonConfigPath/COMMON_CONFIG_MAPPING_LIST/COMMON_CONFIG_MAPPING_INFO[Element eq  'Calling_card_default_CC_NEG_BALANCE_START']/Value"/>				
			</neg_balance_start>
			<neg_balance_barred>
				<xsl:value-of select="$docCommonConfigPath/COMMON_CONFIG_MAPPING_LIST/COMMON_CONFIG_MAPPING_INFO[Element eq  'Calling_card_default_CC_NEG_BALANCE_BARRED']/Value"/>
			</neg_balance_barred>
			<account_disconnect>
				<xsl:value-of select="$docCommonConfigPath/COMMON_CONFIG_MAPPING_LIST/COMMON_CONFIG_MAPPING_INFO[Element eq  'Calling_card_default_CC_ACCOUNT_DISCONNECT']/Value"/>
			</account_disconnect>
			<account_status>
				<xsl:value-of select="$docCommonConfigPath/COMMON_CONFIG_MAPPING_LIST/COMMON_CONFIG_MAPPING_INFO[Element eq  'Calling_card_default_CC_ACCOUNT_STATUS']/Value"/>
			</account_status>
			<prom_notification>
				<xsl:value-of select="$docCommonConfigPath/COMMON_CONFIG_MAPPING_LIST/COMMON_CONFIG_MAPPING_INFO[Element eq  'Calling_card_default_CC_PROM_NOTIFICATION']/Value"/>
			</prom_notification>
			<service_offerings>
				<xsl:value-of select="$docCommonConfigPath/COMMON_CONFIG_MAPPING_LIST/COMMON_CONFIG_MAPPING_INFO[Element eq  'Calling_card_default_CC_SERVICE_OFFERINGS']/Value"/>
			</service_offerings>
			<account_group_id>
				<xsl:value-of select="$docCommonConfigPath/COMMON_CONFIG_MAPPING_LIST/COMMON_CONFIG_MAPPING_INFO[Element eq  'Calling_card_default_CC_ACCOUNT_GROUP_ID']/Value"/>			
			</account_group_id>
			<community_id1>
				<xsl:value-of select="$default_ZERO"/>	
			</community_id1>
			<community_id2>
				<xsl:value-of select="$default_ZERO"/>
			</community_id2>
			<community_id3>
				<xsl:value-of select="$default_ZERO"/>
			</community_id3>
			<account_home_region>
				<xsl:value-of select="$docCommonConfigPath/COMMON_CONFIG_MAPPING_LIST/COMMON_CONFIG_MAPPING_INFO[Element eq  'Calling_card_default_CC_ACCOUNT_HOME_REGION']/Value"/>	
			</account_home_region>
			<account_lock>
				<xsl:value-of select="$docCommonConfigPath/COMMON_CONFIG_MAPPING_LIST/COMMON_CONFIG_MAPPING_INFO[Element eq  'Calling_card_default_CC_ACCOUNT_LOCK']/Value"/>	
			</account_lock>
			<product_id_counter>
				<xsl:value-of select="$default_ZERO"/>
			</product_id_counter>
		</ACCOUNT>
	</xsl:template>
	
	<xsl:template name="RULE_SFEE_DATE">
		<xsl:variable name="SFEE_EXP" select="$docLifeCycleCallingCard/LIFECYCLE_MAPPING_LIST/LIFECYCLE_MAPPING_INFO[VOUCHER_TYPE eq $VOUCHER_TYPE]/SFEE_EXPIRY_DATE"/>
		<xsl:choose>
			<xsl:when test="$SFEE_EXP eq 'BALANCE_EXPIRY.INFILE_Calling_Card.csv+14'">
				<xsl:value-of select="dm:dateToNumberOfDays(dm:convertTELDateToXSLTDate($BALANCE_EXPIRY))+14"/>
			</xsl:when>
		</xsl:choose>
		<xsl:choose>
			<xsl:when test="$SFEE_EXP eq 'BALANCE_EXPIRY.INFILE_Calling_Card.csv+30'">
				<xsl:value-of select="dm:dateToNumberOfDays(dm:convertTELDateToXSLTDate($BALANCE_EXPIRY))+30"/>
			</xsl:when>
		</xsl:choose>
		<xsl:choose>
			<xsl:when test="$SFEE_EXP eq 'BALANCE_EXPIRY.INFILE_Calling_Card.csv+45'">
				<xsl:value-of select="dm:dateToNumberOfDays(dm:convertTELDateToXSLTDate($BALANCE_EXPIRY))+45"/>
			</xsl:when>
		</xsl:choose>
		<xsl:choose>
			<xsl:when test="$SFEE_EXP eq 'BALANCE_EXPIRY.INFILE_Calling_Card.csv+60'">
				<xsl:value-of select="dm:dateToNumberOfDays(dm:convertTELDateToXSLTDate($BALANCE_EXPIRY))+60"/>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template name="RULE_SUP_DATE">
		<xsl:variable name="SFEE_EXP" select="$docLifeCycleCallingCard/LIFECYCLE_MAPPING_LIST/LIFECYCLE_MAPPING_INFO[VOUCHER_TYPE eq $VOUCHER_TYPE]/SUP_EXPIRY_DATE"/>
		<xsl:choose>
			<xsl:when test="$SFEE_EXP eq 'BALANCE_EXPIRY.INFILE_Calling_Card.csv'">
				<xsl:value-of select="dm:dateToNumberOfDays(dm:convertTELDateToXSLTDate($BALANCE_EXPIRY))"/>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template name="RULE_SUP_STATUS">
		<xsl:value-of select="$docLifeCycleCallingCard/LIFECYCLE_MAPPING_LIST/LIFECYCLE_MAPPING_INFO[VOUCHER_TYPE eq $VOUCHER_TYPE]/SUP_STATUS"/>
	</xsl:template>
	
	<xsl:template name="RULE_SFEE_STATUS">
		<xsl:value-of select="$docLifeCycleCallingCard/LIFECYCLE_MAPPING_LIST/LIFECYCLE_MAPPING_INFO[VOUCHER_TYPE eq $VOUCHER_TYPE]/SFEE_STATUS"/>	
	</xsl:template>
	
	<!---=================================================-->
					<!--Subuscriber.csv-->
	<!---=================================================-->
	
	<xsl:template name="generate_Subscriber.csv">
		<SUBSCRIBER>
			<Subscriber_MSISDN>
				<xsl:value-of select="$SERIAL_NUMBER"/>
			</Subscriber_MSISDN>
			<Account_MSISDN>
				<xsl:value-of select="$SERIAL_NUMBER"/>
			</Account_MSISDN>
			<subscriber_status>
				<xsl:call-template name="RULE_SUBS_STATUS"/>
			</subscriber_status>
			<refill_failed>
				<xsl:value-of select="$docCommonConfigPath/COMMON_CONFIG_MAPPING_LIST/COMMON_CONFIG_MAPPING_INFO[Element eq  'Calling_Card_default_refill_failed']/Value"/>
			</refill_failed>
			<refill_bar_end>
				<xsl:value-of select="$docCommonConfigPath/COMMON_CONFIG_MAPPING_LIST/COMMON_CONFIG_MAPPING_INFO[Element eq  'Calling_Card_default_refill_bar_end']/Value"/>
			</refill_bar_end>
			<first_ivr_call_done>
				<xsl:call-template name="RULE_SUBS_FIRST_IVR_CALL_DONE"/>
			</first_ivr_call_done>
			<first_call_done>
				<xsl:call-template name="RULE_SUBS_FIRST_CALL_DONE"/>
			</first_call_done>
			<language>			
				<xsl:value-of select="$docCommonConfigPath/COMMON_CONFIG_MAPPING_LIST/COMMON_CONFIG_MAPPING_INFO[Element eq  'Calling_Card_default_language_id']/Value"/>
			</language>
			<special_announc_played>
				<xsl:value-of select="$docCommonConfigPath/COMMON_CONFIG_MAPPING_LIST/COMMON_CONFIG_MAPPING_INFO[Element eq  'Calling_Card_default_special_announc_played']/Value"/>
			</special_announc_played>
			<sfee_warn_played>
				<xsl:value-of select="$docCommonConfigPath/COMMON_CONFIG_MAPPING_LIST/COMMON_CONFIG_MAPPING_INFO[Element eq  'Calling_Card_default_sfee_warn_played']/Value"/>
			</sfee_warn_played>
			<sup_warn_played>
				<xsl:value-of select="$docCommonConfigPath/COMMON_CONFIG_MAPPING_LIST/COMMON_CONFIG_MAPPING_INFO[Element eq  'Calling_Card_default_sup_warn_played']/Value"/>
			</sup_warn_played>
			<low_level_warn_played>
				<xsl:value-of select="$docCommonConfigPath/COMMON_CONFIG_MAPPING_LIST/COMMON_CONFIG_MAPPING_INFO[Element eq  'Calling_Card_default_low_level_warn_played']/Value"/>				
			</low_level_warn_played>
			<wanted_block_status>
				<xsl:value-of select="$docCommonConfigPath/COMMON_CONFIG_MAPPING_LIST/COMMON_CONFIG_MAPPING_INFO[Element eq  'Calling_Card_default_wanted_block_status']/Value"/>
			</wanted_block_status>
			<actual_block_status>
				<xsl:value-of select="$docCommonConfigPath/COMMON_CONFIG_MAPPING_LIST/COMMON_CONFIG_MAPPING_INFO[Element eq  'Calling_Card_default_actual_block_status']/Value"/>
			</actual_block_status>
			<eoc_selection_id>
				<xsl:value-of select="$docCommonConfigPath/COMMON_CONFIG_MAPPING_LIST/COMMON_CONFIG_MAPPING_INFO[Element eq  'Calling_Card_default_eoc_selection_id']/Value"/>
			</eoc_selection_id>
			<pin_code>
				<xsl:value-of select="$docCommonConfigPath/COMMON_CONFIG_MAPPING_LIST/COMMON_CONFIG_MAPPING_INFO[Element eq  'Calling_Card_default_pin_code']/Value"/>
			</pin_code>
			<usage_statistic_flags>
				<xsl:value-of select="$docCommonConfigPath/COMMON_CONFIG_MAPPING_LIST/COMMON_CONFIG_MAPPING_INFO[Element eq  'Calling_Card_default_usage_statistic_flags']/Value"/>
			</usage_statistic_flags>
		</SUBSCRIBER>
	</xsl:template>
	
	<xsl:template name="RULE_SUBS_STATUS">
		<xsl:value-of select="$docLifeCycleCallingCard/LIFECYCLE_MAPPING_LIST/LIFECYCLE_MAPPING_INFO[VOUCHER_TYPE eq $VOUCHER_TYPE]/SUBSCRIBER_STATUS"/>	
	</xsl:template>
	
	<xsl:template name="RULE_SUBS_FIRST_IVR_CALL_DONE">
		<xsl:value-of select="$docLifeCycleCallingCard/LIFECYCLE_MAPPING_LIST/LIFECYCLE_MAPPING_INFO[VOUCHER_TYPE eq $VOUCHER_TYPE]/FIRST_IVR_CALL_DONE"/>
	</xsl:template>
	
	<xsl:template name="RULE_SUBS_FIRST_CALL_DONE">
		<xsl:variable name="First_Call_Done" select="$docLifeCycleCallingCard/LIFECYCLE_MAPPING_LIST/LIFECYCLE_MAPPING_INFO[VOUCHER_TYPE eq $VOUCHER_TYPE]/FIRST_CALL_DONE"/>
		<xsl:choose>
			<xsl:when test="$First_Call_Done eq 'ACTIVATION_DATE.INFILE_Calling_Card.csv'">
				<xsl:value-of select="dm:dateToNumberOfDays(dm:convertTELDateToXSLTDate($ACTIVATION_DATE))"/>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
	
	<!---=================================================-->
			<!--DedicatedAccount.csv-->
	<!---=================================================-->
	<xsl:template name="generate_DedicatedAccounts.csv">
		<xsl:call-template name="generate_DedicatedAccounts"/>	
	</xsl:template>
	
	<xsl:template name="generate_DedicatedAccounts">
		<DEDICATED_ACCOUNT_LIST>
			<Account_MSISDN>
				<xsl:value-of select="$SERIAL_NUMBER"/>
			</Account_MSISDN>
			<DEDICATED_ACCOUNT>
				<id>
					<xsl:value-of select="$docLifeCycleCallingCard/LIFECYCLE_MAPPING_LIST/LIFECYCLE_MAPPING_INFO[VOUCHER_TYPE eq $VOUCHER_TYPE]/DEDICATEDDA"/>
				</id>
				<balance>
					<xsl:choose>
						<xsl:when test="string-length($BALANCE_VALUE) ne 0">
							<xsl:value-of select="format-number($BALANCE_VALUE * 100,'#0')"/>
						</xsl:when>
					</xsl:choose>					
				</balance>									
				<start_date>
					<xsl:value-of select="dm:dateToNumberOfDays(dm:convertTELDateToXSLTDate($ACTIVATION_DATE))"/>
				</start_date>
				<expiry_date>
					<xsl:value-of select="dm:dateToNumberOfDays(dm:convertTELDateToXSLTDate($BALANCE_EXPIRY))"/>
				</expiry_date>
				<product_id>
					<xsl:value-of select="$default_ZERO"/>
				</product_id>
			</DEDICATED_ACCOUNT>			
		</DEDICATED_ACCOUNT_LIST>
	</xsl:template>
	
	<!---=================================================-->
			<!--PAMAccount.csv-->
	<!---=================================================-->
	
	<xsl:template name="generate_PamAccounts.csv">
		<xsl:variable name="PAM_OUTPUT">			
			<xsl:call-template name="generate_PamAccount"/>
		</xsl:variable>				
		<PERIODIC_ACCOUNT_LIST>
			<Account_MSISDN>
				<xsl:value-of select="$SERIAL_NUMBER"/>
			</Account_MSISDN>
			<xsl:call-template name="Transform_PAMAccount">				
				<xsl:with-param name="SUBSCRIBER_RECORD" select="$PAM_OUTPUT"/>
			</xsl:call-template>
		</PERIODIC_ACCOUNT_LIST>
	</xsl:template>
	
	<xsl:template name="Transform_PAMAccount">
		<xsl:param name="SUBSCRIBER_RECORD"/>		
		<xsl:for-each-group select="$SUBSCRIBER_RECORD/PAM_LIST/PAM" group-by="pam_class_id">
			<xsl:variable name="PAM_Service_ID" select="pam_service_id"/>	
			<xsl:variable name="PAM_Class_ID" select="pam_class_id"/>
			<xsl:variable name="PAM_Scheduled_ID" select="schedule_id"/>
			<xsl:variable name="Current_PAM_Period" select="current_pam_period"/>
			<xsl:variable name="Deferred_to_date" select="deferred_to_date"/>
			<xsl:variable name="priority" select="priority"/>
			
			<xsl:variable name="Last_evaluation_date">
				<xsl:for-each select="current-group()">
					<xsl:sort select="last_evaluation_date" order="ascending"/>
					<xsl:if test="(position() eq 1) and (upper-case(last_evaluation_date) ne $default_NULL) and (last_evaluation_date ne $EMPTY_STRING)">
						<xsl:value-of select="last_evaluation_date"/>
					</xsl:if>
				</xsl:for-each>
			</xsl:variable>			
			<PAM>
				<pam_service_id>
					<xsl:value-of select="$PAM_Service_ID" />
				</pam_service_id>
				<pam_class_id>
					<xsl:value-of select="$PAM_Class_ID" />												
				</pam_class_id>
				<schedule_id>
					<xsl:value-of select="$PAM_Scheduled_ID" />												
				</schedule_id>
				<current_pam_period>
					<xsl:value-of select="$Current_PAM_Period" />												
				</current_pam_period>
				<deferred_to_date>
					<xsl:value-of select="$Deferred_to_date"/>
				</deferred_to_date>
				<last_evaluation_date>
					<xsl:value-of select="$Last_evaluation_date"/>
				</last_evaluation_date>
				<priority>
					<xsl:value-of select="$priority" />
				</priority>
			</PAM>
		</xsl:for-each-group>
	</xsl:template>
	
	<xsl:template name="generate_PamAccount">
		<PAM_LIST>					
			<!--PAM account Calculation form Default Sheet-->	
			<xsl:call-template name="RULE_PAM_A"/>			
		</PAM_LIST>
	</xsl:template>
	
	<xsl:template name="RULE_PAM_A">
		<xsl:for-each select="$docDefaultServices/DEFAULTSERVICES_MAPPING_LIST/DEFAULTSERVICES_MAPPING_INFO">
			<xsl:choose>
				<xsl:when test="(string-length(PAM_Class_ID) gt 0) and (Ignore_Flag eq 'N')">					
					<xsl:variable name="PAM_Class_ID" select="PAM_Class_ID"/>
					<xsl:variable name="PAM_Service_ID" select="PAM_Service_ID"/>
					<xsl:variable name="PAM_Scheduled_ID" select="Schedule_ID"/>
					<xsl:variable name="Current_PAM_Period" select="Current_PAM_Period"/>
					<xsl:variable name="PRIORITY" select="$default_ZERO"/>
					<PAM>
						<pam_service_id>
							<xsl:value-of select="$PAM_Service_ID" />
						</pam_service_id>
						<pam_class_id>
							<xsl:value-of select="$PAM_Class_ID" />												
						</pam_class_id>
						<schedule_id>
							<xsl:value-of select="$PAM_Scheduled_ID" />												
						</schedule_id>
						<current_pam_period>
							<xsl:value-of select="$Current_PAM_Period"/>												
						</current_pam_period>
						<deferred_to_date>
							<xsl:value-of select="$default_ZERO"/>
						</deferred_to_date>
						<last_evaluation_date>							
							<xsl:choose>
								<xsl:when test="upper-case($Current_PAM_Period) eq 'DAILY PAM'">
									<!--<xsl:message select="format-date($MIGRATION_DATE - xs:dayTimeDuration('P1D'), '[Y0001]-[M01]-[D01]')"/>
									<xsl:message select="dm:convertTELDateToXSLTDate(format-date($MIGRATION_DATE - xs:dayTimeDuration('P1D'), '[Y0001]-[M01]-[D01]'))"/>-->
									<xsl:value-of select="dm:dateToNumberOfDays(dm:convertTELDateToXSLTDate(format-date($MIGRATION_DATE - xs:dayTimeDuration('P1D'), '[Y0001]-[M01]-[D01]')))" />
								</xsl:when>
								<xsl:otherwise>
									<xsl:variable name="Start_Of_Month" select="concat(year-from-date(xs:date($MIGRATION_DATE)),'-',format-number(month-from-date(xs:date($MIGRATION_DATE)), '00'),'-','01')"/>
									<!--<xsl:message select="dm:convertTELDateToXSLTDate($Start_Of_Month)"/>-->
									<xsl:value-of select="dm:dateToNumberOfDays(dm:convertTELDateToXSLTDate($Start_Of_Month))" />
								</xsl:otherwise>											
							</xsl:choose>
						</last_evaluation_date>
						<priority>
							<xsl:value-of select="$PRIORITY" />
						</priority>
					</PAM>	
				</xsl:when>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>
	
	<!---=================================================-->
			<!--PrepaidCallingCard.csv-->
	<!---=================================================-->
	
	<xsl:template name="generate_prepaid_callingcard.csv">
		<calling_card>
			<serial_number>
				<xsl:value-of select="$SERIAL_NUMBER"/>
			</serial_number>
			<card_number>
				<xsl:value-of select="$HRN"/>
			</card_number>
			<activation>
				<xsl:value-of select="$ACTIVATION_DATE"/>			
			</activation>
			<sub_status>
				<xsl:value-of select="$STATE"/>
			</sub_status>
			<service_class>
				<xsl:value-of select="$docServiceClassMapping/SERVICE_CLASS_LIST/SERVICE_CLASS_INFO[CCS_ACCT_TYPE_ID eq $VOUCHER_TYPE]/Target "/>
			</service_class>			
		</calling_card>
	</xsl:template>
	
	<!--==========================================OfferAtr Calculation=========================================-->
	
	<!--For Value Type 4 used in offer attributes,String to Hexadecimal	-->	
	<xsl:template name="RULE_OFFER_VAL_4">
		<xsl:param name="String"/>		
		<xsl:if test="string-length($String) gt 0">
			<xsl:variable name="first-char" select="substring($String,1,1)"/>
			<xsl:variable name="ascii-value" select="string-length(substring-before($ascii,$first-char)) + 32"/>
			<xsl:variable name="hex-digit1" select="substring($hex,floor($ascii-value div 16) + 1,1)"/>
			<xsl:variable name="hex-digit2" select="substring($hex,$ascii-value mod 16 + 1,1)"/>
			<xsl:value-of select="concat($hex-digit1,$hex-digit2)"/>
			<xsl:if test="string-length($String) > 1">
				<xsl:call-template name="RULE_OFFER_VAL_4">
					<xsl:with-param name="String" select="substring($String,2)"/>
				</xsl:call-template>
			</xsl:if>
		</xsl:if>		
	</xsl:template>
	
	<!--For Value type 2 used in Offer Attributes,Date to hexadecimal [VALUE =hex(YYYY, 4 digits) + hex(MM, 2 digits) + hex(DD, 2digits)]	-->
	<xsl:template name="RULE_OFFER_VAL_2">
		<xsl:param name="Date"/>
		<xsl:choose>
			<xsl:when test="upper-case($Date) ne $NULL and $Date ne $EMPTY_STRING and string-length($Date) gt 9">
				<xsl:variable name="Year" select="dm:intToHexFinal(dm:int-to-hex(xs:integer(substring($Date,1,4))),4)"/>
				<xsl:variable name="Month" select="dm:intToHexFinal(dm:int-to-hex(xs:integer(substring($Date,5,2))),2)"/>
				<xsl:variable name="Day" select="dm:intToHexFinal(dm:int-to-hex(xs:integer(substring($Date,7,2))),2)"/>
				<xsl:value-of select="concat($Year,$Month,$Day)"/>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
	
	<!--For Value type 3 used in Offer Attributes,Decimal to hexadecimal [VALUE = 01 + hex(input, 16 digits) + hex(10^decimals, 16 digits)]	-->
	<xsl:template name="RULE_OFFER_VAL_3">
		<xsl:param name="number"/>
		<xsl:if test="string(number($number)) != 'NaN'"> <!-- EROBSCH 2015-04-, it is a number -->
			<xsl:variable name="decimals" select="string-length(substring-after($number,'.'))"/>			
			<xsl:variable name="val" select="xs:integer(replace($number,'\.',''))"/>
			<xsl:value-of select="concat('01',dm:int-to-hex($val,64),dm:int-to-hex(dm:power(xs:integer($DEFAULT_VALUE_TEN),$decimals),64))"/>
		</xsl:if>
	</xsl:template>	
		
	<!--==========================================Raise Exceptions=========================================-->
	<xsl:template name="dm:addInc">
		<xsl:param name="errorCode"/>
		<xsl:param name="errorString1"/>
		<xsl:param name="errorField1"/>
		<xsl:param name="errorString2"/>
		<xsl:param name="errorField2"/>
		<xsl:param name="errorString3"/>
		<xsl:param name="errorField3"/>
		<xsl:param name="errorString4"/>
		<xsl:param name="errorField4"/>
		
		<xsl:variable name="errorCodeCheck" select="$docLoggingMapping/LOGGING_LIST/LOGGING_INFO[ErrorCode eq $errorCode]/ErrorCode"/>  
		<xsl:variable name="message" select="$docLoggingMapping/LOGGING_LIST/LOGGING_INFO[ErrorCode eq $errorCode]/ErrorDef"/>
		<xsl:variable name="field1" select="$docLoggingMapping/LOGGING_LIST/LOGGING_INFO[ErrorCode eq $errorCode]/OutputFields"/>
		<xsl:variable name="action" select="$docLoggingMapping/LOGGING_LIST/LOGGING_INFO[ErrorCode eq $errorCode]/Action"/>
		<!--<xsl:variable name="field2" select="$docLoggingMapping/LOGGING_LIST/LOGGING_INFO[ErrorCode eq $errorCode]/FieldstoLogValue2"/>-->	
		
		<xsl:variable name="fields_message">
			<xsl:for-each select="tokenize($field1, ',')">
				<xsl:variable name="field" select="."/>
				<xsl:variable name="field_value" select="$ROOT_TAG/*[name() = (normalize-space($field))]"/>				
				<xsl:value-of select="concat(normalize-space($field), '=', $field_value, ':')"/>
			</xsl:for-each>
			
			<xsl:if test="string-length($errorString1) gt 0 and string-length($errorField1) gt 0">
				<xsl:value-of select="concat($errorString1, '=', $errorField1, ':')"/>
			</xsl:if>
			<xsl:if test="string-length($errorString2) gt 0 and string-length($errorField2) gt 0">
				<xsl:value-of select="concat($errorString2, '=', $errorField2, ':')"/>
			</xsl:if>
			<xsl:if test="string-length($errorString3) gt 0 and string-length($errorField3) gt 0">
				<xsl:value-of select="concat($errorString3, '=', $errorField3, ':')"/>
			</xsl:if>
			<xsl:if test="string-length($errorString4) gt 0 and string-length($errorField4) gt 0">
				<xsl:value-of select="concat($errorString4, '=', $errorField4, ':')"/>
			</xsl:if>
		</xsl:variable>
		
		<xsl:variable name="p_action">
			<xsl:variable name="p_Severity" select="$docLoggingMapping/LOGGING_LIST/LOGGING_INFO[ErrorCode eq $errorCode]/Severity"/>
			<xsl:choose>
				<xsl:when test="upper-case($p_Severity) eq 'E'">
					<xsl:value-of select="'yes'"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="'no'"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="string-length($errorCodeCheck) gt 0 and string-length($p_action) gt 0 and $p_action eq 'yes' and string-length($message) gt 0">
				<xsl:message terminate="yes">
					<xsl:value-of select="concat($errorCode, ':', $message, ':', substring($fields_message, 0, string-length($fields_message)), ':ACTION=', $action)"/>
				</xsl:message>
			</xsl:when>
			<xsl:when test="string-length($errorCodeCheck) gt 0 and string-length($p_action) gt 0 and $p_action eq 'no' and string-length($message) gt 0">
				<xsl:message terminate="no">
					<xsl:value-of select="concat($errorCode, ':', $message, ':', substring($fields_message, 0, string-length($fields_message)), ':ACTION=', $action)"/>
				</xsl:message>
			</xsl:when>
			<xsl:otherwise> <xsl:message terminate="yes">
				<!--<xsl:value-of
						select="concat($errorCode, ':', $MSISDN, ':', 'ERROR IN STYLESHEET, ERROR CODE ', $errorCode, ' UNKNOWN')"
					/>-->
			</xsl:message>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template name="dm:addIncHCoded">
		<xsl:param name="errorCode"/>
		<xsl:param name="errorString1"/>
		<xsl:param name="errorField1"/>
		<xsl:param name="errorString2"/>
		<xsl:param name="errorField2"/>
		<xsl:param name="errorString3"/>
		<xsl:param name="errorField3"/>
		<xsl:param name="errorString4"/>
		<xsl:param name="errorField4"/>	
		<xsl:param name="errorString5"/>
		<xsl:param name="errorField5"/>
		
		<xsl:variable name="errorCodeCheck" select="$docLoggingMapping/LOGGING_LIST/LOGGING_INFO[ErrorCode eq $errorCode]/ErrorCode"/>  
		<xsl:variable name="message" select="$docLoggingMapping/LOGGING_LIST/LOGGING_INFO[ErrorCode eq $errorCode]/ErrorDef"/>
		<xsl:variable name="field1" select="$docLoggingMapping/LOGGING_LIST/LOGGING_INFO[ErrorCode eq $errorCode]/OutputField"/>
		<xsl:variable name="action" select="$docLoggingMapping/LOGGING_LIST/LOGGING_INFO[ErrorCode eq $errorCode]/Action"/>
		<xsl:variable name="field2" select="$docLoggingMapping/LOGGING_LIST/LOGGING_INFO[ErrorCode eq $errorCode]/OutputField"/>
		
		
		<xsl:variable name="fields_message">
			<!--<xsl:for-each select="tokenize($field1, ',')">
				<xsl:variable name="field" select="."/>
				<xsl:variable name="field_value" select="$ROOT_TAG/*[name() = ($field)]"/>				
				<xsl:value-of select="concat($field, '=', $field_value, ':')"/>
			</xsl:for-each>-->
			
			<xsl:if test="string-length($errorString1) gt 0 and string-length($errorField1) gt 0">
				<xsl:value-of select="concat($errorString1, '=', $errorField1, ':')"/>				
			</xsl:if>
			<xsl:if test="string-length($errorString2) gt 0 and string-length($errorField2) gt 0">
				<xsl:value-of select="concat($errorString2, '=', $errorField2, ':')"/>				
			</xsl:if>
			<xsl:if test="string-length($errorString3) gt 0 and string-length($errorField3) gt 0">
				<xsl:value-of select="concat($errorString3, '=', $errorField3, ':')"/>				
			</xsl:if>
			<xsl:if test="string-length($errorString4) gt 0 and string-length($errorField4) gt 0">
				<xsl:value-of select="concat($errorString4, '=', $errorField4, ':')"/>				
			</xsl:if>
			<xsl:if test="string-length($errorString5) gt 0 and string-length($errorField5) gt 0">
				<xsl:value-of select="concat($errorString5, '=', $errorField5, ':')"/>				
			</xsl:if>
		</xsl:variable>		
		<xsl:variable name="p_action">
			<xsl:variable name="p_Severity" select="$docLoggingMapping/LOGGING_LIST/LOGGING_INFO[ErrorCode eq $errorCode]/Severity"/>
			<xsl:choose>
				<xsl:when test="upper-case($p_Severity) eq 'E'">
					<xsl:value-of select="'yes'"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="'no'"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:choose>			
			<xsl:when
				test="string-length($errorCodeCheck) gt 0 and string-length($p_action) gt 0 and $p_action eq 'yes' and string-length($message) gt 0">				
				<xsl:message terminate="yes">
					<xsl:value-of select="concat($errorCode, ':', $message, ':', substring($fields_message, 0, string-length($fields_message)), ':ACTION=', $action)" />
				</xsl:message>
			</xsl:when>
			<xsl:when
				test="string-length($errorCodeCheck) gt 0 and string-length($p_action) gt 0 and $p_action eq 'no' and string-length($message) gt 0">
				<xsl:message terminate="no">
					<xsl:value-of select="concat($errorCode, ':', $message, ':', substring($fields_message, 0, string-length($fields_message)), ':ACTION=', $action)" />
				</xsl:message>
			</xsl:when>
			<xsl:otherwise>
				<!--<xsl:message terminate="yes">
					<xsl:value-of select="concat($errorCode, ':', $MSISDN, ':', 'ERROR IN STYLESHEET, ERROR CODE ', $errorCode, ' UNKNOWN')" />
				</xsl:message>-->
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!---=================================================-->
	<!-- CONVERSION TEMPLATE-->
	<!---=================================================-->
	
	<xsl:template name="last-day-of-month">
		<xsl:param name="date"/>
		<xsl:variable name="cal" select="'312831303130313130313031'"/>	
		<xsl:variable name="year" select="substring($date, 1, 4)"/>
		<xsl:variable name="month" select="substring($date, 6, 2)"/>
		<xsl:variable name="leap" as="xs:boolean" select="not(xs:integer($year) mod 4) and xs:integer($year) mod 100 or not(xs:integer($year) mod 400)"/>
		
		<!--<xsl:variable name="month-length" select="substring($cal, 2*($month - 1) + 1, 2) + ($month=2 and $leap)" />-->
		<xsl:variable name="month-length" select="xs:integer(substring($cal, 2*(xs:integer($month) - 1) + 1, 2)) + xs:integer(( xs:integer($month)=2 and $leap))" />
	
		<xsl:value-of select="concat($year, '-',$month , '-', $month-length)" />
	</xsl:template>
	
	<xsl:template name="offerInputToDateTimeFormat">
		<xsl:param name="inputDateTime" as="xs:string"/>
		
		<xsl:choose>
			<!--when empty offer expiry fill MIG-DATE -1-->
			<xsl:when test="string-length($inputDateTime) eq 0">
				<xsl:variable name="inputDateTime_temp">
					<xsl:value-of select="current-dateTime() - xs:dayTimeDuration('P1D')"/>  <!--this will be  in the format 2014-05-25T11:26:51.409+05:30-->
				</xsl:variable>
				<xsl:value-of select="xs:dateTime(concat(substring($inputDateTime_temp,1,10),'T',substring($inputDateTime_temp,12,8)))"	/>					
			</xsl:when>
			<xsl:when test="not(string(number($inputDateTime)) eq 'NaN')"> <!--logic for millisec-->
				<xsl:value-of select="(xs:dateTime('1970-01-01T00:00:00') + (xs:double($inputDateTime) * xs:dayTimeDuration('PT0.001S')))"	/>			
			</xsl:when>			
			<xsl:when test="string-length($inputDateTime) gt 18">
				<!--				<xsl:message select="$inputDateTime"></xsl:message>-->
				<xsl:choose>
					<xsl:when test="number(substring($inputDateTime,1,4)) gt 1900">
						<xsl:value-of select="xs:dateTime(concat(substring($inputDateTime,1,4),'-',substring($inputDateTime,6,2),'-',substring($inputDateTime,9,2),'T',substring($inputDateTime,12,8)))"	/>
					</xsl:when>	
					<xsl:otherwise>						
						<xsl:value-of select="xs:dateTime(concat(substring($inputDateTime,7,4),'-',substring($inputDateTime,4,2),'-',substring($inputDateTime,1,2),'T',substring($inputDateTime,12,8)))" />	
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$default_NULL"/>
			</xsl:otherwise>	
		</xsl:choose>
	</xsl:template>

	<!---=================================================-->
	<!-- CONVERSION FUNCTIONS-->
	<!---=================================================-->
	
	<!---=================================================-->
	<!--INTEGER TO HEXADECIMAL FUNCTION	-->
	
	<xsl:function name="dm:int-to-hex" as="xs:string">
		<xsl:param name="in" as="xs:integer"/>
		<xsl:sequence
			select="if ($in eq 0)
			then '0'
			else concat(if ($in ge 16)
			then
			dm:int-to-hex($in idiv 16)
			else '',
			substring($hex,($in mod 16) + 1, 1))"
		/>
	</xsl:function>
	
	<xsl:function name="dm:fill-left" as="xs:string">
		<xsl:param name="input" as="xs:string?"/>
		<xsl:param name="fill-with" as="xs:string?"/>
		<xsl:param name="length" as="xs:integer"/>
		<xsl:choose>
			<xsl:when test="string-length($fill-with) eq 0">
				<xsl:sequence select="concat('', $input)"/>
			</xsl:when>
			<xsl:when test="string-length($input) lt $length">
				<xsl:sequence select="string-join((for $i in 1 to $length - string-length($input) return substring($fill-with, 1, 1), $input), '')"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="concat('', $input)"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
		
	<xsl:function name="dm:int-to-hex" as="xs:string">
		<xsl:param name="input" as="xs:integer"/>
		<xsl:param name="n" as="xs:integer"/>
		<xsl:choose>
			<xsl:when test="$n gt 1">
				<xsl:variable name="temp" as="xs:integer" select="dm:power(2, $n - 1)"/>
				<xsl:variable name="max" as="xs:integer" select="$temp - 1"/>
				<xsl:variable name="min" as="xs:integer" select="- 1 * $temp"/>
				<xsl:choose>
					<xsl:when test="($input le $max) and ($input ge $min)">
						<xsl:choose>
							<xsl:when test="$input lt 0">
								<!-- two-complements = 2^n + input (remember input is negative)-->
								<xsl:sequence select="dm:fill-left(dm:int-to-hex(2 * $temp + $input),'F', ceiling($n idiv 4))"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:sequence select="dm:fill-left(dm:int-to-hex($input), '0', ceiling($n idiv 4))"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:when>
					<xsl:otherwise>
						<xsl:sequence select="'NaN'"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="'NaN'"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
		
	<!--Concat with Zero left padding for heaxadecimal output	-->
	
	<xsl:function name="dm:intToHexFinal">
		<xsl:param name="hexDate" as="xs:string"/>
		<xsl:param name="digitFlag" as="xs:integer"/>
		<xsl:choose>
			<xsl:when test="string-length($hexDate) lt $digitFlag ">
				<xsl:value-of select="concat('0',$hexDate)"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$hexDate"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>

	<xsl:function name="dm:checkNotEmptyDate" as="xs:boolean">
		<xsl:param name="Input"/>
		<xsl:value-of
			select="string-length($Input) ne 0 and $Input ne $EMPTY_STRING and upper-case($Input) ne $default_NULL and $Input ne $DEFAULT_VALUE_ZERO"
		/>
	</xsl:function>

	<xsl:function name="dm:checkEmptyDate" as="xs:boolean">
		<xsl:param name="Input"/>
		<xsl:value-of
			select="string-length($Input) eq 0 or $Input eq $EMPTY_STRING or upper-case($Input) eq $default_NULL or $Input eq $DEFAULT_VALUE_ZERO"
		/>
	</xsl:function>

	<!--Binary to Integer	-->
	<xsl:function name="dm:binaryToInteger">
		<!-- Function to convert binary to decimal -->
		<xsl:param name="binaryString"/>
		<xsl:param name="integer"/>
		<xsl:choose>
			<xsl:when test="$binaryString = ''">
				<xsl:value-of select="$integer"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of
					select="dm:binaryToInteger(substring($binaryString, 2), (2 * number($integer) + number(substring($binaryString, 1, 1))))"
				/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>

	<xsl:function name="dm:power" as="xs:integer">
		<xsl:param name="base" as="xs:integer"/>
		<xsl:param name="exp" as="xs:integer"/>
		<xsl:choose>
			<xsl:when test="$base eq 2 and $exp le 64">
				<xsl:value-of
					select="
						(1,
						2,
						4,
						8,
						16,
						32,
						64,
						128,
						256,
						512,
						1024,
						2048,
						4096,
						8192,
						16384,
						32768,
						65536,
						131072,
						262144,
						524288,
						1048576,
						2097152,
						4194304,
						8388608,
						16777216,
						33554432,
						67108864,
						134217728,
						268435456,
						536870912,
						1073741824,
						2147483648,
						4294967296,
						8589934592,
						17179869184,
						34359738368,
						68719476736,
						137438953472,
						274877906944,
						549755813888,
						1099511627776,
						2199023255552,
						4398046511104,
						8796093022208,
						17592186044416,
						35184372088832,
						70368744177664,
						140737488355328,
						281474976710656,
						562949953421312,
						1125899906842620,
						2251799813685250,
						4503599627370500,
						9007199254740990,
						18014398509482000,
						36028797018964000,
						72057594037927900,
						144115188075856000,
						288230376151712000,
						576460752303423000,
						1152921504606850000,
						2305843009213690000,
						4611686018427390000,
						9223372036854780000,
						18446744073709600000)[$exp + 1]"
				/>
			</xsl:when>
			<xsl:when test="$base eq 10 and $exp le 10">
				<xsl:value-of
					select="
						(1,
						10,
						100,
						1000,
						10000,
						100000,
						1000000,
						10000000,
						100000000,
						1000000000,
						10000000000)[$exp + 1]"
				/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence
					select="
						if ($exp lt 0) then
							dm:power(1 div $base, -$exp)
						else
							if ($exp eq 0)
							then
								1
							else
								$base * dm:power($base, $exp - 1)"
				/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>

	<!-- yyyymmdd to yyyy-mm-dd -->
	<xsl:function name="dm:convertTELDateToXSLTDate" as="xs:date">
		<xsl:param name="INPUT_STR_DT" as="xs:string"/>
		
		<xsl:variable name="tmp" select="concat(substring($INPUT_STR_DT, 1, 4), '-', substring($INPUT_STR_DT, 5, 2), '-', substring($INPUT_STR_DT, 7, 2))"/>
<!--		<xsl:variable name="tmp" select="concat(substring($INPUT_STR_DT, 1, 4), '-', substring($INPUT_STR_DT, 6, 2), '-', substring($INPUT_STR_DT, 9, 2))"/>-->
		<xsl:choose>
			<xsl:when test="$tmp castable as xs:date">
				<xsl:value-of select="$tmp"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="'2038-01-01'"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>

	<!-- yyyy/mm/dd hh:mm:ss+gmt to yyyy-mm-ddThh:mm:ss -->
	<xsl:function name="dm:convertTELDateTimeToXSLTDate" as="xs:dateTime">
		<xsl:param name="INPUT_STR_DT" as="xs:string"/>
		<xsl:value-of select="concat(substring($INPUT_STR_DT, 1, 4), '-', substring($INPUT_STR_DT, 6, 2), '-', substring($INPUT_STR_DT, 9, 2), 'T', substring($INPUT_STR_DT, 12, 2), ':', substring($INPUT_STR_DT, 15, 2), ':', substring($INPUT_STR_DT, 18, 2))"/>
	</xsl:function>

	<xsl:function name="dm:dateToNumberOfDays">
		<!-- Convert result_date into number of day since 1970-01-01      -->
		<xsl:param name="date" as="xs:date"/>
		<xsl:value-of select="($date - xs:date('1970-01-01')) div xs:dayTimeDuration('P1D')"/>
	</xsl:function>

	<xsl:function name="dm:dateTimeToNumberOfDays">
		<!-- Convert result_date into number of day since 1970-01-01      -->
		<xsl:param name="date" as="xs:dateTime"/>
		<xsl:value-of select="xs:integer(($date - xs:dateTime('1970-01-01T00:00:00')) div xs:dayTimeDuration('P1D'))" />
	</xsl:function>

	<xsl:function name="dm:addToDate">
		<xsl:param name="date" as="xs:date"/>
		<xsl:param name="daysValue" as="xs:integer"/>
		<!-- returns: xs:date -->
		<!-- Get date by adding/subtracting the number of days.
			Positive input param indicates adding number of days to current date
			Negative input param indicates subtracting number of days from current date
		-->
		<xsl:value-of select="format-date($date + $daysValue * xs:dayTimeDuration('P1D'), '[Y0001]-[M01]-[D01]')"/>
	</xsl:function>
	
	<xsl:function name="dm:decimalToBinary">
		<!-- Convert number/decimal to binary. -->
		<xsl:param name="p_dec" as="xs:decimal"/>
		<xsl:variable name="binary">
			<xsl:value-of select="dm:toBinaryValue($p_dec)"/>
		</xsl:variable>
		<xsl:value-of select="codepoints-to-string(reverse(string-to-codepoints($binary)))"/>
	</xsl:function>
	
	<xsl:function name="dm:getLSBFromBinary">
		<!-- Function to get LSB from binary string. -->
		<xsl:param name="p_str" as="xs:string"/>
		<xsl:choose>
			<xsl:when test="(string-length($p_str) gt 16)">
				<xsl:value-of
					select="substring($p_str, string-length($p_str)-15, string-length($p_str))"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$p_str"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	
	<xsl:function name="dm:binaryToDecimal">
		<!-- Function to convert binary to decimal -->
		<xsl:param name="binaryString"/>
		<xsl:param name="integer"/>
		<xsl:choose>
			<xsl:when test="$binaryString = ''">
				<xsl:value-of select="$integer"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="dm:binaryToDecimal(substring($binaryString, 2), (2 * $integer + number(substring($binaryString, 1, 1))))" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	
	<xsl:function name="dm:ConvertBinaryToDecimal">
		<!-- Function to convert binary to decimal -->
		<xsl:param name="binaryString"/>
		<xsl:param name="integer"/>
		<xsl:choose>
			<xsl:when test="$binaryString=''">
				<xsl:value-of select="$integer"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="dm:binaryToDecimal(substring($binaryString,2),(2*$integer + number(substring($binaryString,1,1))))"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	
	<xsl:function name="dm:getMSBFromBinary">
		<!-- Function to get MSB from binary string -->
		<xsl:param name="p_str" as="xs:string"/>
		<xsl:choose>
			<xsl:when test="(string-length($p_str) gt 16)">
				<xsl:value-of select="substring($p_str,0,string-length($p_str)-15)"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$p_str"/>
			</xsl:otherwise>
		</xsl:choose>				
	</xsl:function>

	<xsl:function name="dm:dateTimeToEPOCHTimeStamp">
		<!-- Convert result_date into number of day since 1970-01-01      -->
		<xsl:param name="dateTime" as="xs:dateTime"/>
		<xsl:value-of select="floor((( $dateTime - xs:dateTime('1970-01-01T00:00:00') )    div     xs:dayTimeDuration('PT1S'))+$GMT_DIFF)" />
	</xsl:function>
	
	<xsl:function name="dm:dateTimeToEPOCHTimeStampExpirtyTime">
		<!-- Convert result_date into number of day since 1970-01-01      -->
		<xsl:param name="dateTime" as="xs:dateTime"/>
		<xsl:value-of select="floor((( $dateTime - xs:dateTime('1970-01-01T00:00:00') )    div     xs:dayTimeDuration('PT1S'))+$GMT_DIFF + $EXPIRY_TIME)" />
	</xsl:function>
		
	<xsl:function name="dm:toBinaryValue">
		<!-- Convert number/decimal to binary. -->
		<xsl:param name="p_dec" as="xs:decimal"/>
		<xsl:variable name="car" as="xs:decimal" select="$p_dec mod 2"/>
		<xsl:variable name="cdr" as="xs:integer" select="($p_dec div 2) cast as xs:integer"/>
		<xsl:value-of select="string($car)"/>
		<xsl:if test="not($cdr lt 1)">
			<xsl:value-of select="dm:toBinaryValue($cdr)"/>
		</xsl:if>
	</xsl:function>

	<xsl:function name="dm:binaryToOfferStartSeconds">
		<!-- Function to convert binary string to expiry seconds -->
		<xsl:param name="binaryString" as="xs:string"/>
		<xsl:choose>
			<xsl:when test="string($binaryString) = ''">
				<xsl:value-of select="'0'"/>
			</xsl:when>
			<xsl:when test="substring($binaryString, 1, 1) = '1'">
				<xsl:value-of
					select="-1 * (32767 - dm:binaryToDecimal(substring($binaryString, 2), 0) + 1)"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="dm:binaryToDecimal($binaryString, 0)"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	
	<xsl:function name="dm:binaryToOfferExpirySeconds">
		<!-- Function to convert binary string to expiry seconds -->
		<xsl:param name="binaryString" as="xs:string"/>
		<xsl:choose>
			<xsl:when test="string($binaryString) = ''">
				<xsl:value-of select="'0'"/>
			</xsl:when>
			<xsl:when test="substring($binaryString, 1, 1) = '1'">
				<xsl:value-of
					select="-1 * (32767 - dm:binaryToDecimal(substring($binaryString, 2), 0) + 1)"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="dm:binaryToDecimal($binaryString, 0)"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>

</xsl:stylesheet>
