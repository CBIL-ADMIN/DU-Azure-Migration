<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:dm="http://www.ericsson.com/datamigration"
	exclude-result-prefixes="dm xs">

	<xsl:import href="Generate_output_csv.xslt"/>
	
	<xsl:output method="text" omit-xml-declaration="yes" encoding="utf-8" indent="yes"/>
	<xsl:strip-space elements="*"/>

	<!-- ************************* -->
	<!--  CONSTANTS -->
	<!-- ************************* -->
	<xsl:variable name="BalanceMapping" select="'BalanceMapping.xml'"/>
	<xsl:variable name="CommonConfigPath" select="'CommonConfigMapping.xml'"/>
	<xsl:variable name="ConversionFactor" select="'ConversionFactorMapping.xml'"/>
	<xsl:variable name="LanguageMapping" select="'LanguageMapping.xml'"/>
	<xsl:variable name="LifeCycleMapping" select="'LifeCycleMapping.xml'"/>
	<xsl:variable name="LoggingMapping" select="'LoggingMapping.xml'"/>	
	<xsl:variable name="OutputFilesMapping" select="'OutputFilesMapping.xml'"/>	
	<xsl:variable name="ServiceClassMapping" select="'ServiceClassMapping.xml'"/>
	<xsl:variable name="DefaultServicesMapping" select="'DefaultServicesMapping.xml'"/>
	<xsl:variable name="DefaultProfileMapping" select="'profile.xml'"/>
	<xsl:variable name="ConversionLogicMapping" select="'ConversionLogicMapping.xml'"/>
	<xsl:variable name="ProfileTagMapping" select="'ProfileTagMapping.xml'"/>
	<xsl:variable name="Offer_Attribute_Defination" select="'Offer_Attribute_Defination.xml'"/>
	<xsl:variable name="Product_Mapping" select="'Product_Mapping.xml'"/>
	
	
	<xsl:variable name="docBalanceMapping" select="document($BalanceMapping)"/>
	<xsl:variable name="docCommonConfigPath" select="document($CommonConfigPath)"/>
	<xsl:variable name="docConversionFactor" select="document($ConversionFactor)"/>
	<xsl:variable name="docServiceClassMapping" select="document($ServiceClassMapping)"/>
	<xsl:variable name="docLanguageMapping" select="document($LanguageMapping)"/>
	<xsl:variable name="docLifeCycleMapping" select="document($LifeCycleMapping)"/>
	<xsl:variable name="docLoggingMapping" select="document($LoggingMapping)"/>
	<xsl:variable name="docOutputFilesMapping" select="document($OutputFilesMapping)"/>
	<xsl:variable name="docDefaultServices" select="document($DefaultServicesMapping)"/>
	<xsl:variable name="docDefaultProfileMapping" select="document($DefaultProfileMapping)"/>
	<xsl:variable name="docConversionLogicMapping" select="document($ConversionLogicMapping)"/>
	<xsl:variable name="docProfileTagMapping" select="document($ProfileTagMapping)"/>
	<xsl:variable name="docOffer_Attribute_Defination" select="document($Offer_Attribute_Defination)"/>
	<xsl:variable name="docProduct_Mapping" select="document($Product_Mapping)"/>
	
	<xsl:variable name="ROOT_TAG" select="/subscriber_xml"/>
	
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

	<!-- Input_Value_Attribute -->
	
	<xsl:variable name="MSISDN" select="subscriber_xml/subscriber_info/SUBSCRIBER/MSISDN"/>
	<xsl:variable name="CCS_ACCT_TYPE_ID" select="subscriber_xml/subscriber_info/SUBSCRIBER/CCS_ACCT_TYPE_ID"/>
	<xsl:variable name="SERVICE_STATE" select="subscriber_xml/subscriber_info/SUBSCRIBER/SERVICE_STATE"/>
	<xsl:variable name="WALLET_EXPIRY" select="subscriber_xml/subscriber_info/SUBSCRIBER/WALLET_EXPIRY"/>
	
	<!-- Default services iterator -->
	<xsl:variable name="DefaultService" >
		<xsl:for-each select="$docDefaultServices/DEFAULTSERVICES_MAPPING_LIST/DEFAULTSERVICES_MAPPING_INFO">
			<xsl:value-of select="concat(Default_ID,';')"/>
		</xsl:for-each>
	</xsl:variable>	
	<xsl:variable name="DefaultServiceList" select="tokenize($DefaultService, ';')"/>	
	
	<xsl:variable name="ProductPrivate">
		<xsl:for-each select="/subscriber_xml/balancesdump_info/schemasubscriberbalancesdump_info">
			<xsl:variable name="Balance_Type_ID" select="BALANCE_TYPE"/>
			<!--<xsl:variable name="DA_ID" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and CCS_ACCT_TYPE_ID eq $CCS_ACCT_TYPE_ID and Ignore_Flag eq 'N']/DA_ID" /> 
			<xsl:variable name="Private" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and CCS_ACCT_TYPE_ID eq $CCS_ACCT_TYPE_ID and Ignore_Flag eq 'N']/Product_Private" /> -->		
				
			<xsl:choose>
				<xsl:when test="string-length($docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and CCS_ACCT_TYPE_ID eq $CCS_ACCT_TYPE_ID  and All_SC eq 'N' and Ignore_Flag eq 'N' and upper-case(Product_Private) eq 'YES' ]/DA_ID)">
					<xsl:value-of select="concat($docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and CCS_ACCT_TYPE_ID eq $CCS_ACCT_TYPE_ID  and All_SC eq 'N' and Ignore_Flag eq 'N' and upper-case(Product_Private) eq 'YES']/DA_ID,';')"/>			
				</xsl:when>
			</xsl:choose>
			
			<xsl:choose>
				<xsl:when test="string-length($docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID  and All_SC eq 'Y' and Ignore_Flag eq 'N' and upper-case(Product_Private) eq 'YES']/DA_ID)">
					<xsl:value-of select="concat($docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and All_SC eq 'Y' and Ignore_Flag eq 'N' and upper-case(Product_Private) eq 'YES']/DA_ID,';')"/>		
				</xsl:when>
			</xsl:choose>
			<!--<xsl:choose>
				<xsl:when test="string-length($docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and All_SC eq 'N' and Ignore_Flag eq 'N']/DA_ID)">
					<xsl:variable name="Private" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and All_SC eq 'N' and Ignore_Flag eq 'N']/Product_Private" /> 			
					<xsl:choose>
						<xsl:when test="upper-case($Private) eq 'YES'">
							<xsl:value-of select="concat($docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and All_SC eq 'N' and Ignore_Flag eq 'N']/DA_ID,';')"/>
						</xsl:when>
					</xsl:choose>			
				</xsl:when>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="string-length($docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and All_SC eq 'Y' and Ignore_Flag eq 'N']/DA_ID)">
					<xsl:variable name="Private" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and All_SC eq 'Y' and Ignore_Flag eq 'N']/Product_Private" /> 			
					<xsl:choose>
						<xsl:when test="upper-case($Private) eq 'YES'">
							<xsl:value-of select="concat($docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and All_SC eq 'Y' and Ignore_Flag eq 'N']/DA_ID,';')"/>
						</xsl:when>
					</xsl:choose>			
				</xsl:when>
			</xsl:choose>-->
							
			
			
		</xsl:for-each>
	</xsl:variable>
	
	<xsl:variable name="ProductPrivateList" select="tokenize($ProductPrivate,';')"/>
	
	<xsl:variable name="ProductPrivateForDA">
		<xsl:for-each select="$ProductPrivateList">
			<xsl:choose>
				<xsl:when test="string-length(.)">
					<xsl:value-of select="concat(.,',',position() + 100,';' )"/>
				</xsl:when>
			</xsl:choose>			
		</xsl:for-each>
	</xsl:variable>
	
	<xsl:variable name="ProductPrivateForDAList" select="tokenize($ProductPrivateForDA,';')"/>
		
	<xsl:variable name="UCProductPrivate">
		<xsl:for-each select="/subscriber_xml/balancesdump_info/schemasubscriberbalancesdump_info">
			<xsl:variable name="Balance_Type_ID" select="Balance_Type_ID"/>
			<xsl:variable name="UC_ID" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and CCS_ACCT_TYPE_ID eq $CCS_ACCT_TYPE_ID and Ignore_Flag eq 'N']/UC_ID" /> 
			<xsl:variable name="Private" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and CCS_ACCT_TYPE_ID eq $CCS_ACCT_TYPE_ID and Ignore_Flag eq 'N']/Product_Private" /> 			
			<xsl:choose>
				<xsl:when test="upper-case($Private) eq 'YES'">
					<xsl:value-of select="concat($UC_ID,';')"/>
				</xsl:when>
			</xsl:choose>
		</xsl:for-each>
	</xsl:variable>
	
	<xsl:variable name="UCProductPrivateList" select="tokenize($UCProductPrivate,';')"/>
	
	<xsl:variable name="ProductPrivateForUC">
		<xsl:for-each select="$UCProductPrivateList">
			<xsl:choose>
				<xsl:when test="string-length(.)">
					<xsl:value-of select="concat(.,',',position() + 100,';' )"/>
				</xsl:when>
			</xsl:choose>			
		</xsl:for-each>
	</xsl:variable>
	
	<xsl:variable name="ProductPrivateForUCList" select="tokenize($ProductPrivateForUC,';')"/>
	
	<xsl:variable name="BalanceValueListDA">
		<xsl:for-each select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[ BE_BUCKET_VALUE ne '' and DA_ID ne '']">
			<!--			<xsl:message select="Balance_Type_ID"></xsl:message>-->
			<xsl:value-of select="concat(Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
		</xsl:for-each>
	</xsl:variable>
	
	<xsl:variable name="BalanceValueListOffer">
		<xsl:for-each select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[ BE_BUCKET_VALUE ne '' and Offer_ID ne '']">
			<!--			<xsl:message select="Balance_Type_ID"></xsl:message>-->
			<xsl:value-of select="concat(Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
		</xsl:for-each>
	</xsl:variable>
	
	<!-- Collection for Logging -->
	<xsl:variable name="BalanceLoggingEmptyBucketValueNoSC">
		<xsl:for-each select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[BE_BUCKET_VALUE eq '' and All_SC eq 'N' and Ignore_Flag eq 'N']">
			<xsl:choose>
				<xsl:when test="position() eq 1">
					<xsl:value-of select="concat(';',Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat(Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:variable>
	<xsl:variable name="BalanceLoggingEmptyBucketValueNoSCIgnoreFlag">
		<xsl:for-each select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[BE_BUCKET_VALUE eq '' and All_SC eq 'N' and Ignore_Flag eq 'Y']">
			<xsl:choose>
				<xsl:when test="position() eq 1">
					<xsl:value-of select="concat(';',Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat(Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:variable>
	
	<xsl:variable name="BalanceLoggingEmptyBucketValueYesSC">
		<xsl:for-each select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[BE_BUCKET_VALUE eq '' and All_SC eq 'Y' and Ignore_Flag eq 'N']">
			<xsl:choose>
				<xsl:when test="position() eq 1">
					<xsl:value-of select="concat(';',Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat(Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:variable>
	<xsl:variable name="BalanceLoggingEmptyBucketValueYesSCIgnoreFlag">
		<xsl:for-each select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[BE_BUCKET_VALUE eq '' and All_SC eq 'Y' and Ignore_Flag eq 'Y']">
			<xsl:choose>
				<xsl:when test="position() eq 1">
					<xsl:value-of select="concat(';',Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat(Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:variable>
	
	<xsl:variable name="BalanceLoggingAvailableBucketValueNoSC">
		<xsl:for-each select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[BE_BUCKET_VALUE ne '' and All_SC eq 'N' and Ignore_Flag eq 'N']">
			<xsl:choose>
				<xsl:when test="position() eq 1">
					<xsl:value-of select="concat(';',Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat(Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:variable>
	<xsl:variable name="BalanceLoggingAvailableBucketValueNoSCIgnoreFlag">
		<xsl:for-each select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[BE_BUCKET_VALUE ne '' and All_SC eq 'N' and Ignore_Flag eq 'Y']">
			<xsl:choose>
				<xsl:when test="position() eq 1">
					<xsl:value-of select="concat(';',Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat(Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:variable>
			
	<xsl:variable name="BalanceLoggingAvailableBucketValueYesSC">
		<xsl:for-each select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[BE_BUCKET_VALUE ne '' and All_SC eq 'Y' and Ignore_Flag eq 'N']">
			<xsl:choose>
				<xsl:when test="position() eq 1">
					<xsl:value-of select="concat(';',Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat(Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:variable>
	<xsl:variable name="BalanceLoggingAvailableBucketValueYesSCIgnoreFlag">
		<xsl:for-each select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[BE_BUCKET_VALUE ne '' and All_SC eq 'Y' and Ignore_Flag eq 'Y']">
			<xsl:choose>
				<xsl:when test="position() eq 1">
					<xsl:value-of select="concat(';',Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat(Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:variable>
		
	<!-- Collection for Balance UsageCounter all 3 combinations-->
	<xsl:variable name="BalanceUCEmptyBucketValueNoSC">
		<xsl:for-each select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[BE_BUCKET_VALUE eq '' and All_SC eq 'N' and UC_ID ne '' and Ignore_Flag eq 'N']">
			<xsl:choose>
				<xsl:when test="position() eq 1">
					<xsl:value-of select="concat(';',Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat(Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:variable>
	<xsl:variable name="BalanceUCEmptyBucketValueYesSC">
		<xsl:for-each select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[BE_BUCKET_VALUE eq '' and All_SC eq 'Y' and UC_ID ne '' and Ignore_Flag eq 'N']">
			<xsl:choose>
				<xsl:when test="position() eq 1">
					<xsl:value-of select="concat(';',Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat(Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:variable>
	<xsl:variable name="BalanceUCAvailableBucketValueYesSC">
		<xsl:for-each select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[BE_BUCKET_VALUE ne '' and All_SC eq 'Y' and UC_ID ne '' and Ignore_Flag eq 'N']">
			<xsl:choose>
				<xsl:when test="position() eq 1">
					<xsl:value-of select="concat(';',Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat(Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:variable>
	
	<!-- Collection for Balance ACCUMULATOR all 2 combinations-->
	<xsl:variable name="BalanceACCEmptyBucketValueNoSC">
		<xsl:for-each select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[BE_BUCKET_VALUE eq '' and All_SC eq 'N' and UA_ID ne '' and Ignore_Flag eq 'N']">
			<xsl:choose>
				<xsl:when test="position() eq 1">
					<xsl:value-of select="concat(';',Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat(Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:variable>
	<xsl:variable name="BalanceACCEmptyBucketValueYesSC">
		<xsl:for-each select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[BE_BUCKET_VALUE eq '' and All_SC eq 'Y' and UA_ID ne '' and Ignore_Flag eq 'N']">
			<xsl:choose>
				<xsl:when test="position() eq 1">
					<xsl:value-of select="concat(';',Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat(Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:variable>
	
	<!-- Collection for Balance DA all 3 combinations-->	
	<xsl:variable name="BalanceDAEmptyBucketValueNoSC">
		<xsl:for-each select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[BE_BUCKET_VALUE eq '' and All_SC eq 'N' and DA_ID ne '' and Ignore_Flag eq 'N']">
			<xsl:choose>
				<xsl:when test="position() eq 1">
					<xsl:value-of select="concat(';',Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat(Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
				</xsl:otherwise>
			</xsl:choose>			
		</xsl:for-each>
	</xsl:variable>
	<xsl:variable name="BalanceDAEmptyBucketValueNoSCIgnoreFlag">
		<xsl:for-each select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[BE_BUCKET_VALUE eq '' and All_SC eq 'N' and DA_ID ne '' and Ignore_Flag eq 'Y']">
			<xsl:choose>
				<xsl:when test="position() eq 1">
					<xsl:value-of select="concat(';',Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat(Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
				</xsl:otherwise>
			</xsl:choose>			
		</xsl:for-each>
	</xsl:variable>
	
	<xsl:variable name="BalanceDAEmptyBucketValueYesSC">
		<xsl:for-each select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[BE_BUCKET_VALUE eq '' and All_SC eq 'Y' and DA_ID ne '' and Ignore_Flag eq 'N']">
			<xsl:choose>
				<xsl:when test="position() eq 1">
					<xsl:value-of select="concat(';',Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat(Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:variable>
	<xsl:variable name="BalanceDAEmptyBucketValueYesSCIgnoreFlag">
		<xsl:for-each select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[BE_BUCKET_VALUE eq '' and All_SC eq 'Y' and DA_ID ne '' and Ignore_Flag eq 'Y']">
			<xsl:choose>
				<xsl:when test="position() eq 1">
					<xsl:value-of select="concat(';',Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat(Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:variable>
	
	<xsl:variable name="BalanceDAAvailableBucketValueNoSC">
		<xsl:for-each select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[BE_BUCKET_VALUE ne '' and All_SC eq 'N' and DA_ID ne '' and Ignore_Flag eq 'N']">
			<xsl:choose>
				<xsl:when test="position() eq 1">
					<xsl:value-of select="concat(';',Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat(Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:variable>
	<xsl:variable name="BalanceDAAvailableBucketValueNoSCIgnoreFlag">
		<xsl:for-each select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[BE_BUCKET_VALUE ne '' and All_SC eq 'N' and DA_ID ne '' and Ignore_Flag eq 'Y']">
			<xsl:choose>
				<xsl:when test="position() eq 1">
					<xsl:value-of select="concat(';',Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat(Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:variable>
	
	<xsl:variable name="BalanceDAAvailableBucketValueYesSC">
		<xsl:for-each select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[BE_BUCKET_VALUE ne '' and All_SC eq 'Y' and DA_ID ne '' and Ignore_Flag eq 'N']">
			<xsl:choose>
				<xsl:when test="position() eq 1">
					<xsl:value-of select="concat(';',Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat(Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:variable>
	<xsl:variable name="BalanceDAAvailableBucketValueYesSCIgnoreFlag">
		<xsl:for-each select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[BE_BUCKET_VALUE ne '' and All_SC eq 'Y' and DA_ID ne '' and Ignore_Flag eq 'Y']">
			<xsl:choose>
				<xsl:when test="position() eq 1">
					<xsl:value-of select="concat(';',Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat(Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:variable>
		
	<!-- Collection for Balance Offer all 3 combinations-->
	
	<xsl:variable name="BalanceOfferEmptyBucketValueNoSC">
		<xsl:for-each select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[BE_BUCKET_VALUE eq '' and All_SC eq 'N' and Offer_ID ne '' and Ignore_Flag eq 'N']">
			<xsl:choose>
				<xsl:when test="position() eq 1">
					<xsl:value-of select="concat(';',Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat(Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:variable>
	<xsl:variable name="BalanceOfferEmptyBucketValueNoSCIgnoreFlag">
		<xsl:for-each select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[BE_BUCKET_VALUE eq '' and All_SC eq 'N' and Offer_ID ne '' and Ignore_Flag eq 'Y']">
			<xsl:choose>
				<xsl:when test="position() eq 1">
					<xsl:value-of select="concat(';',Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat(Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:variable>
	
	<xsl:variable name="BalanceOfferEmptyBucketValueYesSC">
		<xsl:for-each select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[BE_BUCKET_VALUE eq '' and All_SC eq 'Y' and Offer_ID ne '' and Ignore_Flag eq 'N']">
			<xsl:choose>
				<xsl:when test="position() eq 1">
					<xsl:value-of select="concat(';',Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat(Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:variable>
	<xsl:variable name="BalanceOfferEmptyBucketValueYesSCIgnoreFlag">
		<xsl:for-each select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[BE_BUCKET_VALUE eq '' and All_SC eq 'Y' and Offer_ID ne '' and Ignore_Flag eq 'Y']">
			<xsl:choose>
				<xsl:when test="position() eq 1">
					<xsl:value-of select="concat(';',Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat(Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:variable>
	
	<xsl:variable name="BalanceOfferAvailableBucketValueNoSC">
		<xsl:for-each select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[BE_BUCKET_VALUE ne '' and All_SC eq 'N' and Offer_ID ne '' and Ignore_Flag eq 'N']">
			<xsl:choose>
				<xsl:when test="position() eq 1">
					<xsl:value-of select="concat(';',Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat(Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:variable>
	<xsl:variable name="BalanceOfferAvailableBucketValueNoSCIgnoreFlag">
		<xsl:for-each select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[BE_BUCKET_VALUE ne '' and All_SC eq 'N' and Offer_ID ne '' and Ignore_Flag eq 'Y']">
			<xsl:choose>
				<xsl:when test="position() eq 1">
					<xsl:value-of select="concat(';',Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat(Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:variable>
	
	<xsl:variable name="BalanceOfferAvailableBucketValueYesSC">
		<xsl:for-each select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[BE_BUCKET_VALUE ne '' and All_SC eq 'Y' and Offer_ID ne '' and Ignore_Flag eq 'N']">
			<xsl:choose>
				<xsl:when test="position() eq 1">
					<xsl:value-of select="concat(';',Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat(Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:variable>
	<xsl:variable name="BalanceOfferAvailableBucketValueYesSCIgnoreFlag">
		<xsl:for-each select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[BE_BUCKET_VALUE ne '' and All_SC eq 'Y' and Offer_ID ne '' and Ignore_Flag eq 'Y']">
			<xsl:choose>
				<xsl:when test="position() eq 1">
					<xsl:value-of select="concat(';',Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat(Balance_Type_ID,'|', BE_BUCKET_VALUE, '|',CCS_ACCT_TYPE_ID,';')"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:variable>
	
	<!-- Reading the product mapping -->	
	
	<xsl:variable name="ProductMappingOnlyBalanceIDValue">
		<xsl:for-each select="$docProduct_Mapping/PRODUCT_MAPPING_LIST/PRODUCT_MAPPING_INFO[BT_PC_ID ne '' and PC_BT_Value ne '' and BT_ID eq '']">
			<xsl:choose>
				<xsl:when test="position() eq 1">
					<xsl:value-of select="concat(';',BT_PC_ID,'|', PC_BT_Value, '|',BT_ID,';')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat(BT_PC_ID,'|', PC_BT_Value, '|',BT_ID,';')"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:variable>
	
	<xsl:variable name="ProductMappingOnlyBalanceIDValueSecondBalance">
		<xsl:for-each select="$docProduct_Mapping/PRODUCT_MAPPING_LIST/PRODUCT_MAPPING_INFO[BT_PC_ID ne '' and PC_BT_Value ne '' and BT_ID ne '']">
			<xsl:choose>
				<xsl:when test="position() eq 1">
					<xsl:value-of select="concat(';',BT_PC_ID,'|', PC_BT_Value,';')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat(BT_PC_ID,'|', PC_BT_Value,';')"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:variable>
	
	<xsl:variable name="ProductMappingSecondBalance">
		<xsl:for-each select="/subscriber_xml/balancesdump_info/schemasubscriberbalancesdump_info">
			<xsl:variable name="Balance_Type_ID" select="BALANCE_TYPE"/>
			<xsl:variable name="DA_NAME" select="BALANCE_TYPE_NAME"/>
			<xsl:variable name="BALANCE" select="BE_BUCKET_VALUE" />
			<xsl:choose>
				<xsl:when test="position() eq 1">
					<xsl:value-of select="concat(';',$Balance_Type_ID,';')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat($Balance_Type_ID,';')"/>
				</xsl:otherwise>
			</xsl:choose>
		
		</xsl:for-each>
		<!--<xsl:for-each select="$docProduct_Mapping/PRODUCT_MAPPING_LIST/PRODUCT_MAPPING_INFO[BT_PC_ID ne '' and PC_BT_Value ne '' and BT_ID ne '']">
			<xsl:choose>
				<xsl:when test="position() eq 1">
					<xsl:value-of select="concat(';',BT_PC_ID,'|', BT_ID,';')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat(BT_PC_ID,'|', BT_ID,';')"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>-->
	</xsl:variable>
<!--	and Resource in ('BT','MBT')-->
	<xsl:variable name="ProductMappingOnlySecondBalanceEmptyIdentifier">
		<xsl:for-each select="$docProduct_Mapping/PRODUCT_MAPPING_LIST/PRODUCT_MAPPING_INFO[BT_PC_ID eq '' and PC_BT_Value eq '' and BT_ID ne '' and BT_Group_Identifier eq ''] ">
			<xsl:choose>
				<xsl:when test="position() eq 1">
					<xsl:value-of select="concat(';',BT_ID,';')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat(';',BT_ID,';')"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:variable>
	
	<xsl:variable name="ProductMappingOnlySecondBalanceIdentifier">
		<xsl:for-each-group select="$docProduct_Mapping/PRODUCT_MAPPING_LIST/PRODUCT_MAPPING_INFO[BT_PC_ID eq '' and PC_BT_Value eq '' and BT_ID ne '' and BT_Group_Identifier ne '']" group-by="BT_Group_Identifier">
			<xsl:variable name="BalanceIDGroup">
				<xsl:for-each select="current-group()/(BT_ID)">
					<xsl:value-of select="concat(.,',')"/>
				</xsl:for-each>				
			</xsl:variable>
			<xsl:choose>
				<xsl:when test="position() eq 1">
					<xsl:value-of select="concat(';', $BalanceIDGroup,';')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat( $BalanceIDGroup,';')"/>
				</xsl:otherwise>
			</xsl:choose>			
		</xsl:for-each-group>
	</xsl:variable>
	
	<xsl:variable name="ProductMappingOnlySecondBalanceIdentifier2Group">
		<xsl:for-each-group select="$docProduct_Mapping/PRODUCT_MAPPING_LIST/PRODUCT_MAPPING_INFO[BT_PC_ID eq '' and PC_BT_Value eq '' and BT_ID ne '' and BT_Group_Identifier ne '']" group-by="BT_Group_Identifier">
			<xsl:choose>
				<xsl:when test="count(current-group()) eq 2">
					<xsl:variable name="BalanceIDGroup">
						<xsl:for-each select="current-group()/(BT_ID)">
							<xsl:value-of select="concat(.,',')"/>
						</xsl:for-each>				
					</xsl:variable>
					
					<xsl:variable name="BT_ValueGroup">
						<xsl:for-each select="current-group()/(BT_Value)">
							<xsl:value-of select="concat(.,',')"/>
						</xsl:for-each>				
					</xsl:variable>
					<!--<xsl:choose>
						<xsl:when test="position() eq 1">
							<xsl:value-of select="concat(';', $BalanceIDGroup,':',BT_Group_Identifier,':',$BT_ValueGroup,';')"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="concat(';', $BalanceIDGroup,':',BT_Group_Identifier,':',$BT_ValueGroup,';')"/>
						</xsl:otherwise>
					</xsl:choose>-->	
					<xsl:value-of select="concat( $BalanceIDGroup,':',BT_Group_Identifier,':',$BT_ValueGroup,':',count(current-group()),';')"/>
				</xsl:when>
			</xsl:choose>
		</xsl:for-each-group>
	</xsl:variable>
	
	<xsl:variable name="tokenProductMappingOnlySecondBalanceIdentifier2Group" select="tokenize(substring($ProductMappingOnlySecondBalanceIdentifier2Group,1),';')"/>
	
	<xsl:variable name="ProductMappingOnlySecondBalanceIdentifier3Group">
		<xsl:for-each-group select="$docProduct_Mapping/PRODUCT_MAPPING_LIST/PRODUCT_MAPPING_INFO[BT_PC_ID eq '' and PC_BT_Value eq '' and BT_ID ne '' and BT_Group_Identifier ne '']" group-by="BT_Group_Identifier">
			<xsl:choose>
				<xsl:when test="count(current-group()) eq 3">
					<xsl:variable name="BalanceIDGroup">
						<xsl:for-each select="current-group()/(BT_ID)">
							<xsl:value-of select="concat(.,',')"/>
						</xsl:for-each>				
					</xsl:variable>
					
					<xsl:variable name="BT_ValueGroup">
						<xsl:for-each select="current-group()/(BT_Value)">
							<xsl:value-of select="concat(.,',')"/>
						</xsl:for-each>				
					</xsl:variable>
					<!--<xsl:choose>
						<xsl:when test="position() eq 1">
							<xsl:value-of select="concat(';', $BalanceIDGroup,':',BT_Group_Identifier,':',$BT_ValueGroup,';')"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="concat(';', $BalanceIDGroup,':',BT_Group_Identifier,':',$BT_ValueGroup,';')"/>
						</xsl:otherwise>
					</xsl:choose>-->	
					<xsl:value-of select="concat( $BalanceIDGroup,':',BT_Group_Identifier,':',$BT_ValueGroup,':',count(current-group()),';')"/>
				</xsl:when>
			</xsl:choose>
		</xsl:for-each-group>
	</xsl:variable>
	
	<xsl:variable name="tokenProductMappingOnlySecondBalanceIdentifier3Group" select="tokenize(substring($ProductMappingOnlySecondBalanceIdentifier3Group,1),';')"/>
	<!--<xsl:variable name="ProductMappingOnlySecondBalanceID">
		<xsl:for-each-group select="$docProduct_Mapping/PRODUCT_MAPPING_LIST/PRODUCT_MAPPING_INFO[BT_PC_ID eq '' and PC_BT_Value eq '' and BT_ID ne '' and BT_Group_Identifier ne '']" group-by="BT_Group_Identifier">
			<xsl:variable name="BalanceIDGroup">
				<xsl:for-each select="current-group()/(BT_ID)">
					<xsl:value-of select="concat(.,',')"/>
				</xsl:for-each>				
			</xsl:variable>
			<xsl:choose>
				<xsl:when test="position() eq 1">
					<xsl:value-of select="concat(';',BT_ID,';')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat(';',BT_ID,';')"/>
				</xsl:otherwise>
			</xsl:choose>			
		</xsl:for-each-group>
	</xsl:variable>-->
	
	
	
	<!-- Reading from subscriber usms file -->
<!--	<xsl:variable name="INITIAL_ACTIVATION_DATE" select=" subscriber_xml/usmsdump_info/schemasubscriberusmsdump_info/INITIAL_ACTIVATION_DATE"/>-->
	<xsl:variable name="LAST_ACCOUNT_EXPIRY_PERIOD" select="subscriber_xml/usmsdump_info/schemasubscriberusmsdump_info/LAST_ACCOUNT_EXPIRY_PERIOD"/>
	
	<xsl:variable name="INITIAL_ACTIVATION_DATE">
		<xsl:choose>
			<xsl:when test="subscriber_xml/usmsdump_info/schemasubscriberusmsdump_info/INITIAL_ACTIVATION_DATE">
				<xsl:value-of select="subscriber_xml/usmsdump_info/schemasubscriberusmsdump_info/INITIAL_ACTIVATION_DATE"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="''"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
		
	<xsl:variable name="INITIAL_ACTIVATION_DATE_FLAG">
		<xsl:choose>
			<xsl:when test="string-length($INITIAL_ACTIVATION_DATE)">
				<xsl:value-of select="'N'"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="'Y'"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
		
	<!-- Profile input just loading for temporary purpose -->
	<xsl:variable name="LANGUAGEID" select="subscriber_xml/profiledump_info/schemasubscriberprofiledump_info/LanguageID"/>
	<xsl:variable name="IMEI" select="subscriber_xml/profiledump_info/schemasubscriberprofiledump_info/IMEI"/>
	<xsl:variable name="HLRADDR" select="subscriber_xml/profiledump_info/schemasubscriberprofiledump_info/hlrAddr"/>
	<xsl:variable name="IMSI" select="subscriber_xml/profiledump_info/schemasubscriberprofiledump_info/IMSI"/>
	<xsl:variable name="TOPXCOUNTR1" select="subscriber_xml/profiledump_info/schemasubscriberprofiledump_info/TopXCountr1"/>
	<xsl:variable name="TOPXCOUNTR2" select="subscriber_xml/profiledump_info/schemasubscriberprofiledump_info/TopXCountr2"/>
	<xsl:variable name="TOPXCOUNTR3" select="subscriber_xml/profiledump_info/schemasubscriberprofiledump_info/TopXCountr3"/>
	<xsl:variable name="TOPXCOUNTR4" select="subscriber_xml/profiledump_info/schemasubscriberprofiledump_info/TopXCountr4"/>
	<xsl:variable name="TOPXCOUNTR5" select="subscriber_xml/profiledump_info/schemasubscriberprofiledump_info/TopXCountr5"/>
	<xsl:variable name="PREPAID" select="subscriber_xml/profiledump_info/schemasubscriberprofiledump_info/Prepaid"/>
	<xsl:variable name="ENTBSNSSCRCLACTV" select="subscriber_xml/profiledump_info/schemasubscriberprofiledump_info/entBsnssCrclActv"/>
	
	<!-- =========================================================================== -->
	<!-- =========================================================================== -->
	
	<!-- ************************* -->
	<!--  MAPPING TEMPLATES -->
	<!-- ************************* -->

	<xsl:template match="subscriber_xml">
		<xsl:variable name="OUTPUT">
			<xsl:call-template name="ValidateInputs"/>
			
			<SUBSCRIBER_RECORD>
				<!-- CALL TEMPLATES FOR CSV GENERATION -->
				<SDP_DATA>
					<!-- ........................... -->
					<!-- *-*-*-*-*- SDP -*-*-*-*-*-* -->
					<!-- ........................... -->
					<xsl:variable name="Account_Flag">
						<xsl:value-of select="$docOutputFilesMapping/OUTPUT_MAPPING_LIST/OUTPUT_MAPPING_INFO[Filename eq 'Account.csv']/Flag" />
					</xsl:variable>
					<xsl:if test="$Account_Flag eq 'Y'">
						<xsl:call-template name="generate_Account.csv"/>						
					</xsl:if>
					<xsl:variable name="Subscriber_Flag">
						<xsl:value-of select="$docOutputFilesMapping/OUTPUT_MAPPING_LIST/OUTPUT_MAPPING_INFO[Filename eq 'Subscriber.csv']/Flag" />
					</xsl:variable>
					<xsl:if test="$Subscriber_Flag eq 'Y'">
						<xsl:call-template name="generate_Subscriber.csv"/>
					</xsl:if>
					<xsl:variable name="Dedicated_Flag">
						<xsl:value-of select="$docOutputFilesMapping/OUTPUT_MAPPING_LIST/OUTPUT_MAPPING_INFO[Filename eq 'DedicatedAccount.csv']/Flag" />
					</xsl:variable>
					<xsl:if test="$Dedicated_Flag eq 'Y'">
						<xsl:call-template name="generate_DedicatedAccounts.csv"/>
					</xsl:if>
					<xsl:variable name="PamAccount_Flag">
						<xsl:value-of select="$docOutputFilesMapping/OUTPUT_MAPPING_LIST/OUTPUT_MAPPING_INFO[Filename eq 'PamAccount.csv']/Flag" />
					</xsl:variable>
					<xsl:if test="$PamAccount_Flag eq 'Y'">
						<xsl:call-template name="generate_PamAccounts.csv"/>
					</xsl:if>
					<xsl:variable name="Offer_Flag">
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
					</xsl:if>					
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
		<xsl:choose>
			<xsl:when test="string-length($docServiceClassMapping/SERVICE_CLASS_LIST/SERVICE_CLASS_INFO[CCS_ACCT_TYPE_ID eq $CCS_ACCT_TYPE_ID and Ignore_Flag eq 'N']/Target) eq 0">
				<xsl:call-template name="dm:addIncHCoded">
					<xsl:with-param name="errorCode" select="'INC1001'"/>
					<xsl:with-param name="errorString1" select="'MSISDN'"/>
					<xsl:with-param name="errorField1" select="$MSISDN"/>
					<xsl:with-param name="errorString2" select="'CCS_ACCT_TYPE_ID'"/>
					<xsl:with-param name="errorField2" select="$CCS_ACCT_TYPE_ID"/>
					<xsl:with-param name="errorString3" select="'SERVICE_STATE'"/>
					<xsl:with-param name="errorField3" select="$SERVICE_STATE"/>
				</xsl:call-template>
			</xsl:when>							
		</xsl:choose>
		
		<xsl:choose>
			<xsl:when test="$INITIAL_ACTIVATION_DATE_FLAG eq 'N'">
				<xsl:choose>
					<xsl:when test="contains('A,S,F,D,T', upper-case($SERVICE_STATE))">
						
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="dm:addIncHCoded">
							<xsl:with-param name="errorCode" select="'INC1002'"/>
							<xsl:with-param name="errorString1" select="'MSISDN'"/>
							<xsl:with-param name="errorField1" select="$MSISDN"/>
							<xsl:with-param name="errorString2" select="'SERVICE_STATE'"/>
							<xsl:with-param name="errorField2" select="$SERVICE_STATE"/>
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="contains('P,S', upper-case($SERVICE_STATE))">
						
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="dm:addIncHCoded">
							<xsl:with-param name="errorCode" select="'INC1002'"/>
							<xsl:with-param name="errorString1" select="'MSISDN'"/>
							<xsl:with-param name="errorField1" select="$MSISDN"/>
							<xsl:with-param name="errorString2" select="'SERVICE_STATE'"/>
							<xsl:with-param name="errorField2" select="$SERVICE_STATE"/>
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
		
		<!--<xsl:message select="$BalanceLoggingAvailableBucketValueNoSC"/>
			<xsl:message select="$BalanceLoggingAvailableBucketValueNoSCIgnoreFlag"/>
			<xsl:message select="$BalanceLoggingAvailableBucketValueYesSC"/>
			<xsl:message select="$BalanceLoggingAvailableBucketValueYesSCIgnoreFlag"/>
			<xsl:message select="$BalanceLoggingEmptyBucketValueNoSC"/>
			<xsl:message select="$BalanceLoggingEmptyBucketValueNoSCIgnoreFlag"/>
			<xsl:message select="$BalanceLoggingEmptyBucketValueYesSC"/>
			<xsl:message select="$BalanceLoggingEmptyBucketValueYesSCIgnoreFlag"/>-->
		<xsl:for-each select="/subscriber_xml/balancesdump_info/schemasubscriberbalancesdump_info">
			<xsl:variable name="Balance_Type_ID" select="BALANCE_TYPE"/>
			<xsl:variable name="DA_NAME" select="BALANCE_TYPE_NAME"/>
			<xsl:variable name="BALANCE" select="BE_BUCKET_VALUE" /> 
			<xsl:variable name="START_DATE" select="BE_BUCKET_START_DATE" /> 
			<xsl:variable name="EXPIRY_DATE" select="BE_EXPIRY" />
			
			
	
			<xsl:choose>
				<xsl:when test="contains($BalanceLoggingAvailableBucketValueYesSCIgnoreFlag,concat(';',$Balance_Type_ID,'|', $BALANCE, '|',0,';'))">
<!--					<xsl:message select="concat($Balance_Type_ID,'|', $BALANCE, '|',0,';')"/>-->
					<xsl:call-template name="dm:addIncHCoded">
						<xsl:with-param name="errorCode" select="'INC4003'"/>
						<xsl:with-param name="errorString1" select="'MSISDN'"/>
						<xsl:with-param name="errorField1" select="$MSISDN"/>
						<xsl:with-param name="errorString2" select="'CCS_ACCT_TYPE_ID'"/>
						<xsl:with-param name="errorField2" select="$CCS_ACCT_TYPE_ID"/>
						<xsl:with-param name="errorString3" select="'Balance_Type_ID'"/>
						<xsl:with-param name="errorField3" select="$Balance_Type_ID"/>
						<xsl:with-param name="errorString4" select="'BE_BUCKET_VALUE'"/>
						<xsl:with-param name="errorField4" select="$BALANCE"/>
					</xsl:call-template>
				</xsl:when>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="contains($BalanceLoggingAvailableBucketValueYesSCIgnoreFlag,concat(';',$Balance_Type_ID,'|', $BALANCE, '|',0,';'))">
<!--					<xsl:message select="concat($Balance_Type_ID,'|', $BALANCE, '|',0,';')"/>-->
					<xsl:call-template name="dm:addIncHCoded">
						<xsl:with-param name="errorCode" select="'INC4003'"/>
						<xsl:with-param name="errorString1" select="'MSISDN'"/>
						<xsl:with-param name="errorField1" select="$MSISDN"/>
						<xsl:with-param name="errorString2" select="'CCS_ACCT_TYPE_ID'"/>
						<xsl:with-param name="errorField2" select="$CCS_ACCT_TYPE_ID"/>
						<xsl:with-param name="errorString3" select="'Balance_Type_ID'"/>
						<xsl:with-param name="errorField3" select="$Balance_Type_ID"/>
						<xsl:with-param name="errorString4" select="'BE_BUCKET_VALUE'"/>
						<xsl:with-param name="errorField4" select="$BALANCE"/>
					</xsl:call-template>
				</xsl:when>		
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="contains($BalanceLoggingEmptyBucketValueNoSCIgnoreFlag,concat(';',$Balance_Type_ID,'|', '', '|',$CCS_ACCT_TYPE_ID,';'))">
<!--					<xsl:message select="concat($Balance_Type_ID,'|', '', '|',$CCS_ACCT_TYPE_ID,';')"/>-->
					<xsl:call-template name="dm:addIncHCoded">
						<xsl:with-param name="errorCode" select="'INC4003'"/>
						<xsl:with-param name="errorString1" select="'MSISDN'"/>
						<xsl:with-param name="errorField1" select="$MSISDN"/>
						<xsl:with-param name="errorString2" select="'CCS_ACCT_TYPE_ID'"/>
						<xsl:with-param name="errorField2" select="$CCS_ACCT_TYPE_ID"/>
						<xsl:with-param name="errorString3" select="'Balance_Type_ID'"/>
						<xsl:with-param name="errorField3" select="$Balance_Type_ID"/>
						<xsl:with-param name="errorString4" select="'BE_BUCKET_VALUE'"/>
						<xsl:with-param name="errorField4" select="$BALANCE"/>
					</xsl:call-template>
				</xsl:when>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="contains($BalanceOfferEmptyBucketValueYesSCIgnoreFlag,concat(';',$Balance_Type_ID,'|', '', '|',0,';'))">
					<!--<xsl:message select="concat($Balance_Type_ID,'|', '', '|',0,';')"/>-->
					<xsl:call-template name="dm:addIncHCoded">
						<xsl:with-param name="errorCode" select="'INC4003'"/>
						<xsl:with-param name="errorString1" select="'MSISDN'"/>
						<xsl:with-param name="errorField1" select="$MSISDN"/>
						<xsl:with-param name="errorString2" select="'CCS_ACCT_TYPE_ID'"/>
						<xsl:with-param name="errorField2" select="$CCS_ACCT_TYPE_ID"/>
						<xsl:with-param name="errorString3" select="'Balance_Type_ID'"/>
						<xsl:with-param name="errorField3" select="$Balance_Type_ID"/>
						<xsl:with-param name="errorString4" select="'BE_BUCKET_VALUE'"/>
						<xsl:with-param name="errorField4" select="$BALANCE"/>
					</xsl:call-template>
				</xsl:when>
			</xsl:choose>
			
			<!-- Check for Valid balance type -->
			
			<xsl:choose>
				<xsl:when test="contains($BalanceLoggingAvailableBucketValueYesSC,concat(';',$Balance_Type_ID,'|', $BALANCE, '|',0,';'))">
				</xsl:when>
				<xsl:otherwise>
					<xsl:choose>
						<xsl:when test="contains($BalanceLoggingAvailableBucketValueNoSC,concat(';',$Balance_Type_ID,'|', $BALANCE, '|',$CCS_ACCT_TYPE_ID,';'))">
						</xsl:when>						
						<xsl:otherwise>
							<xsl:choose>
								<xsl:when test="contains($BalanceLoggingEmptyBucketValueNoSC,concat(';',$Balance_Type_ID,'|', '', '|',$CCS_ACCT_TYPE_ID,';'))">
									
								</xsl:when>
								<xsl:otherwise>
									<xsl:choose>
										<xsl:when test="contains($BalanceLoggingEmptyBucketValueYesSC,concat(';',$Balance_Type_ID,'|', '', '|',0,';'))">
										</xsl:when>
										<xsl:otherwise>
											<xsl:call-template name="dm:addIncHCoded">
												<xsl:with-param name="errorCode" select="'INC4004'"/>
												<xsl:with-param name="errorString1" select="'MSISDN'"/>
												<xsl:with-param name="errorField1" select="$MSISDN"/>
												<xsl:with-param name="errorString2" select="'CCS_ACCT_TYPE_ID'"/>
												<xsl:with-param name="errorField2" select="$CCS_ACCT_TYPE_ID"/>
												<xsl:with-param name="errorString3" select="'Balance_Type_ID'"/>
												<xsl:with-param name="errorField3" select="$Balance_Type_ID"/>
												<xsl:with-param name="errorString4" select="'BE_BUCKET_VALUE'"/>
												<xsl:with-param name="errorField4" select="$BALANCE"/>
											</xsl:call-template>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:otherwise>
			</xsl:choose>			
			<!--<xsl:call-template name="dm:addIncHCoded">
				<xsl:with-param name="errorCode" select="'INC4003'"/>
				<xsl:with-param name="errorString1" select="'MSISDN'"/>
				<xsl:with-param name="errorField1" select="$MSISDN"/>
				<xsl:with-param name="errorString2" select="'CCS_ACCT_TYPE_ID'"/>
				<xsl:with-param name="errorField2" select="$CCS_ACCT_TYPE_ID"/>
				<xsl:with-param name="errorString3" select="'Balance_Type_ID'"/>
				<xsl:with-param name="errorField3" select="$Balance_Type_ID"/>
			</xsl:call-template>-->
				
		</xsl:for-each>
		
		
		<xsl:choose>
			<xsl:when test="string-length($IMEI) eq 0 or  string-length($IMSI) eq 0 or string-length($HLRADDR) eq 0">
				<xsl:call-template name="dm:addIncHCoded">
					<xsl:with-param name="errorCode" select="'INC4002'"/>
					<xsl:with-param name="errorString1" select="'MSISDN'"/>
					<xsl:with-param name="errorField1" select="$MSISDN"/>
					<xsl:with-param name="errorString2" select="'IMEI'"/>
					<xsl:with-param name="errorField2" select="$IMEI"/>
					<xsl:with-param name="errorString3" select="'IMSI'"/>
					<xsl:with-param name="errorField3" select="$IMSI"/>
					<xsl:with-param name="errorString4" select="'HLR ARRDESS'"/>
					<xsl:with-param name="errorField4" select="$HLRADDR"/>
				</xsl:call-template>			
			</xsl:when>					
		</xsl:choose>
		
		<!-- <xsl:choose>
			<xsl:when test="string-length($MSISDN) lt 9 or string-length($MSISDN) gt 10">				
				<xsl:call-template name="dm:addInc">
					<xsl:with-param name="errorCode" select="'INC2006'"/>
				</xsl:call-template>
			</xsl:when>		
		</xsl:choose>
		-->
	</xsl:template>


	<!---=================================================-->
	<!-- Final computation for the mapping sheet -->
	
	<!---=================================================-->
					<!--Account.csv-->
	<!---=================================================-->
	
	<xsl:template name="generate_Account.csv">
		<ACCOUNT>
			<Account_MSISDN>
				<xsl:value-of select="$MSISDN"/>
			</Account_MSISDN>
			<account_class>
				<xsl:call-template name="RULE_ACC_B"/>
			</account_class>
			<orig_account_class>
				<xsl:call-template name="RULE_ACC_B"/>
			</orig_account_class>
			<account_class_expiry>
				<xsl:value-of select="$default_NULL"/>
			</account_class_expiry>
			<units>
				<xsl:value-of select="$default_ZERO"/>
			</units>
			<activated>
				<xsl:call-template name="RULE_ACC_E"/>
			</activated>
			<sfee_expiry_date>
				<xsl:call-template name="RULE_ACC_SFEE_EXPIRY_DATE"/>
			</sfee_expiry_date>
			<sup_expiry_date>
				<xsl:call-template name="RULE_ACC_SUP_EXPIRY_DATE"/>
			</sup_expiry_date>
			<sfee_done_date>
				<xsl:value-of select="$docCommonConfigPath/COMMON_CONFIG_MAPPING_LIST/COMMON_CONFIG_MAPPING_INFO[Element eq  'default_SFEE_DONE_DATE']/Value"/>
			</sfee_done_date>
			<previous_sfee_done_date>
				<xsl:value-of select="$docCommonConfigPath/COMMON_CONFIG_MAPPING_LIST/COMMON_CONFIG_MAPPING_INFO[Element eq  'default_PREVIOUS_SFEE_DONE_DATE']/Value"/>				
			</previous_sfee_done_date>
			<sfee_status>
				<xsl:call-template name="RULE_ACC_SFEE_STATUS"/>
			</sfee_status>
			<sup_status>
				<xsl:call-template name="RULE_ACC_SUP_STATUS"/>
			</sup_status>
			<neg_balance_start>
				<xsl:value-of select="$docCommonConfigPath/COMMON_CONFIG_MAPPING_LIST/COMMON_CONFIG_MAPPING_INFO[Element eq  'default_NEG_BALANCE_START']/Value"/>				
			</neg_balance_start>
			<neg_balance_barred>
				<xsl:value-of select="$docCommonConfigPath/COMMON_CONFIG_MAPPING_LIST/COMMON_CONFIG_MAPPING_INFO[Element eq  'default_NEG_BALANCE_BARRED']/Value"/>
			</neg_balance_barred>
			<account_disconnect>
				<xsl:value-of select="$docCommonConfigPath/COMMON_CONFIG_MAPPING_LIST/COMMON_CONFIG_MAPPING_INFO[Element eq  'default_ACCOUNT_DISCONNECT']/Value"/>
			</account_disconnect>
			<account_status>
				<xsl:value-of select="$docCommonConfigPath/COMMON_CONFIG_MAPPING_LIST/COMMON_CONFIG_MAPPING_INFO[Element eq  'default_ACCOUNT_STATUS']/Value"/>
			</account_status>
			<prom_notification>
				<xsl:value-of select="$docCommonConfigPath/COMMON_CONFIG_MAPPING_LIST/COMMON_CONFIG_MAPPING_INFO[Element eq  'default_PROM_NOTIFICATION']/Value"/>
			</prom_notification>
			<service_offerings>
				<xsl:value-of select="$docCommonConfigPath/COMMON_CONFIG_MAPPING_LIST/COMMON_CONFIG_MAPPING_INFO[Element eq  'default_SERVICE_OFFERINGS']/Value"/>
			</service_offerings>
			<account_group_id>
				<xsl:value-of select="$docCommonConfigPath/COMMON_CONFIG_MAPPING_LIST/COMMON_CONFIG_MAPPING_INFO[Element eq  'default_ACCOUNT_GROUP_ID']/Value"/>			
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
				<xsl:value-of select="$docCommonConfigPath/COMMON_CONFIG_MAPPING_LIST/COMMON_CONFIG_MAPPING_INFO[Element eq  'default_ACCOUNT_HOME_REGION']/Value"/>	
			</account_home_region>
			<account_lock>
				<xsl:value-of select="$docCommonConfigPath/COMMON_CONFIG_MAPPING_LIST/COMMON_CONFIG_MAPPING_INFO[Element eq  'default_ACCOUNT_LOCK']/Value"/>	
			</account_lock>
			<product_id_counter>
				<xsl:call-template name="RULE_ACC_G"/>
			</product_id_counter>
		</ACCOUNT>
	</xsl:template>
	
	<xsl:template name="RULE_ACC_B">
		<xsl:value-of select="$docServiceClassMapping/SERVICE_CLASS_LIST/SERVICE_CLASS_INFO[CCS_ACCT_TYPE_ID eq $CCS_ACCT_TYPE_ID and Ignore_Flag eq 'N']/Target"/>
	</xsl:template>
	
	<xsl:template name="RULE_ACC_E">
		<xsl:choose>
			<xsl:when test="$INITIAL_ACTIVATION_DATE_FLAG eq 'Y' and ($SERVICE_STATE eq 'P' or $SERVICE_STATE eq 'S')">
			</xsl:when>
			<xsl:otherwise>	
				<xsl:value-of select="dm:dateToNumberOfDays(dm:convertTELDateToXSLTDate($INITIAL_ACTIVATION_DATE))"/>								
			</xsl:otherwise>
		</xsl:choose>
		<!--<xsl:variable name="MappingValue">
				<xsl:choose>
					<xsl:when test="$docLifeCycleMapping/LIFECYCLE_MAPPING_LIST/LIFECYCLE_MAPPING_INFO[System_State eq $SERVICE_STATE and $INITIAL_ACTIVATION_DATE_FLAG eq 'N' ]/Ignore_flag eq 'N'">
						<xsl:value-of select="$docLifeCycleMapping/LIFECYCLE_MAPPING_LIST/LIFECYCLE_MAPPING_INFO[System_State eq $SERVICE_STATE and $INITIAL_ACTIVATION_DATE_FLAG eq 'N']/ACC_ACTIVATED"/>
					</xsl:when>				
				</xsl:choose>
			</xsl:variable>
			
			<xsl:choose>
				<xsl:when test="($MappingValue ne '0')">
					<xsl:value-of select="dm:dateToNumberOfDays(dm:convertTELDateToXSLTDate($INITIAL_ACTIVATION_DATE))"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="'0'"/>
				</xsl:otherwise>
			</xsl:choose>-->
	</xsl:template>
	
	<xsl:template name="RULE_ACC_SFEE_EXPIRY_DATE">
		<xsl:choose>
			<xsl:when test="$INITIAL_ACTIVATION_DATE_FLAG eq 'Y' and ($SERVICE_STATE eq 'P' or $SERVICE_STATE eq 'S')">
				<xsl:value-of select="'0'"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="dm:dateToNumberOfDays(dm:convertTELDateToXSLTDate($WALLET_EXPIRY)) + 10"/>
			</xsl:otherwise>
		</xsl:choose>
		<!--<xsl:variable name="MappingValue">
			<xsl:choose>
				<xsl:when test="$docLifeCycleMapping/LIFECYCLE_MAPPING_LIST/LIFECYCLE_MAPPING_INFO[System_State eq $SERVICE_STATE ]/Ignore_flag eq 'N'">
					<xsl:value-of select="$docLifeCycleMapping/LIFECYCLE_MAPPING_LIST/LIFECYCLE_MAPPING_INFO[System_State eq $SERVICE_STATE]/ACC_SFEE_EXPIRY_DATE"/>
				</xsl:when>				
			</xsl:choose>
		</xsl:variable>
		
		<xsl:choose>
			<xsl:when test="($MappingValue ne '0')">
				<xsl:value-of select="dm:dateToNumberOfDays(dm:convertTELDateToXSLTDate($LAST_ACCOUNT_EXPIRY_PERIOD)) + 10"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="'0'"/>
			</xsl:otherwise>
		</xsl:choose>-->
	</xsl:template>
	
	<xsl:template name="RULE_ACC_SUP_EXPIRY_DATE">
		<xsl:choose>
			<xsl:when test="$INITIAL_ACTIVATION_DATE_FLAG eq 'Y' and ($SERVICE_STATE eq 'P' or $SERVICE_STATE eq 'S')">
				<xsl:value-of select="'0'"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="dm:dateToNumberOfDays(dm:convertTELDateToXSLTDate($WALLET_EXPIRY))"/>
			</xsl:otherwise>
		</xsl:choose>
		<!--<xsl:variable name="MappingValue">
			<xsl:choose>
				<xsl:when test="$docLifeCycleMapping/LIFECYCLE_MAPPING_LIST/LIFECYCLE_MAPPING_INFO[System_State eq $SERVICE_STATE ]/Ignore_flag eq 'N'">
					<xsl:value-of select="$docLifeCycleMapping/LIFECYCLE_MAPPING_LIST/LIFECYCLE_MAPPING_INFO[System_State eq $SERVICE_STATE]/ACC_SUP_EXPIRY_DATE"/>
				</xsl:when>				
			</xsl:choose>
		</xsl:variable>
		
		<xsl:choose>
			<xsl:when test="($MappingValue ne '0')">
				<xsl:value-of select="dm:dateToNumberOfDays(dm:convertTELDateToXSLTDate($WALLET_EXPIRY))"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="'0'"/>
			</xsl:otherwise>
		</xsl:choose>-->
	</xsl:template>
	
	<xsl:template name="RULE_ACC_SFEE_STATUS">
		<xsl:choose>
			<xsl:when test="$INITIAL_ACTIVATION_DATE_FLAG eq 'Y' and ($SERVICE_STATE eq 'P' or $SERVICE_STATE eq 'S')">
				<xsl:value-of select="'0'"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$docLifeCycleMapping/LIFECYCLE_MAPPING_LIST/LIFECYCLE_MAPPING_INFO[System_State eq $SERVICE_STATE and Initial_Activation_Date eq 'N']/ACC_SFEE_STATUS"/>
			</xsl:otherwise>
		</xsl:choose>
		<!--<xsl:choose>
			<xsl:when test="$docLifeCycleMapping/LIFECYCLE_MAPPING_LIST/LIFECYCLE_MAPPING_INFO[System_State eq $SERVICE_STATE ]/Ignore_flag eq 'N'">
				<xsl:value-of select="$docLifeCycleMapping/LIFECYCLE_MAPPING_LIST/LIFECYCLE_MAPPING_INFO[System_State eq $SERVICE_STATE]/ACC_SFEE_STATUS"/>
			</xsl:when>				
		</xsl:choose>-->
	</xsl:template>
	
	<xsl:template name="RULE_ACC_SUP_STATUS">
		<xsl:choose>
			<xsl:when test="$INITIAL_ACTIVATION_DATE_FLAG eq 'Y' and ($SERVICE_STATE eq 'P' or $SERVICE_STATE eq 'S')">
				<xsl:value-of select="'0'"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$docLifeCycleMapping/LIFECYCLE_MAPPING_LIST/LIFECYCLE_MAPPING_INFO[System_State eq $SERVICE_STATE and Initial_Activation_Date eq 'N']/ACC_SUP_STATUS"/>
			</xsl:otherwise>
		</xsl:choose>
		<!--<xsl:choose>
			<xsl:when test="$docLifeCycleMapping/LIFECYCLE_MAPPING_LIST/LIFECYCLE_MAPPING_INFO[System_State eq $SERVICE_STATE ]/Ignore_flag eq 'N'">
				<xsl:value-of select="$docLifeCycleMapping/LIFECYCLE_MAPPING_LIST/LIFECYCLE_MAPPING_INFO[System_State eq $SERVICE_STATE]/ACC_SUP_STATUS"/>
			</xsl:when>				
		</xsl:choose>-->
	</xsl:template>
	
	<xsl:template name="RULE_ACC_G">
		<!--<xsl:message select="$ProductPrivateList"/>
		<xsl:message select="$ProductPrivateForDA"/>-->
		<xsl:choose>
			<xsl:when test="count($ProductPrivateList) gt 1">
				<xsl:value-of select="'500'"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="0"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	
	<!---=================================================-->
					<!--Subscriber.csv-->
	<!---=================================================-->
	
	
	<xsl:template name="generate_Subscriber.csv">
		<SUBSCRIBER>
			<Subscriber_MSISDN>
				<xsl:value-of select="$MSISDN"/>
			</Subscriber_MSISDN>
			<Account_MSISDN>
				<xsl:value-of select="$MSISDN"/>
			</Account_MSISDN>
			<subscriber_status>
				<xsl:call-template name="RULE_SUBS_STATUS"/>
			</subscriber_status>
			<refill_failed>
				<xsl:value-of select="$docCommonConfigPath/COMMON_CONFIG_MAPPING_LIST/COMMON_CONFIG_MAPPING_INFO[Element eq  'default_refill_failed']/Value"/>
			</refill_failed>
			<refill_bar_end>
				<xsl:value-of select="$docCommonConfigPath/COMMON_CONFIG_MAPPING_LIST/COMMON_CONFIG_MAPPING_INFO[Element eq  'default_refill_bar_end']/Value"/>
			</refill_bar_end>
			<first_ivr_call_done>
				<xsl:call-template name="RULE_SUBS_FIRST_IVR_CALL_DONE"/>
			</first_ivr_call_done>
			<first_call_done>
				<xsl:call-template name="RULE_SUBS_FIRST_CALL_DONE"/>
			</first_call_done>
			<language>
				<xsl:call-template name="RULE_SUBS_LANGUAGE"/>				
			</language>
			<special_announc_played>
				<xsl:value-of select="$docCommonConfigPath/COMMON_CONFIG_MAPPING_LIST/COMMON_CONFIG_MAPPING_INFO[Element eq  'default_special_announc_played']/Value"/>
			</special_announc_played>
			<sfee_warn_played>
				<xsl:value-of select="$docCommonConfigPath/COMMON_CONFIG_MAPPING_LIST/COMMON_CONFIG_MAPPING_INFO[Element eq  'default_sfee_warn_played']/Value"/>
			</sfee_warn_played>
			<sup_warn_played>
				<xsl:value-of select="$docCommonConfigPath/COMMON_CONFIG_MAPPING_LIST/COMMON_CONFIG_MAPPING_INFO[Element eq  'default_sup_warn_played']/Value"/>
			</sup_warn_played>
			<low_level_warn_played>
				<xsl:value-of select="$docCommonConfigPath/COMMON_CONFIG_MAPPING_LIST/COMMON_CONFIG_MAPPING_INFO[Element eq  'default_low_level_warn_played']/Value"/>				
			</low_level_warn_played>
			<wanted_block_status>
				<xsl:value-of select="$docCommonConfigPath/COMMON_CONFIG_MAPPING_LIST/COMMON_CONFIG_MAPPING_INFO[Element eq  'default_wanted_block_status']/Value"/>
			</wanted_block_status>
			<actual_block_status>
				<xsl:value-of select="$docCommonConfigPath/COMMON_CONFIG_MAPPING_LIST/COMMON_CONFIG_MAPPING_INFO[Element eq  'default_actual_block_status']/Value"/>
			</actual_block_status>
			<eoc_selection_id>
				<xsl:value-of select="$docCommonConfigPath/COMMON_CONFIG_MAPPING_LIST/COMMON_CONFIG_MAPPING_INFO[Element eq  'default_eoc_selection_id']/Value"/>
			</eoc_selection_id>
			<pin_code>
				<xsl:value-of select="$docCommonConfigPath/COMMON_CONFIG_MAPPING_LIST/COMMON_CONFIG_MAPPING_INFO[Element eq  'default_pin_code']/Value"/>
			</pin_code>
			<usage_statistic_flags>
				<xsl:value-of select="$docCommonConfigPath/COMMON_CONFIG_MAPPING_LIST/COMMON_CONFIG_MAPPING_INFO[Element eq  'default_usage_statistic_flags']/Value"/>
			</usage_statistic_flags>
		</SUBSCRIBER>
	</xsl:template>
	
	<xsl:template name="RULE_SUBS_STATUS">
		<xsl:choose>
			<xsl:when test="$INITIAL_ACTIVATION_DATE_FLAG eq 'Y' and ($SERVICE_STATE eq 'P' or $SERVICE_STATE eq 'S')">
				<xsl:value-of select="'0'"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$docLifeCycleMapping/LIFECYCLE_MAPPING_LIST/LIFECYCLE_MAPPING_INFO[System_State eq $SERVICE_STATE and Initial_Activation_Date eq 'N']/SUBS_SUBSCRIBER_STATUS"/>				
			</xsl:otherwise>
		</xsl:choose>
		<!--<xsl:choose>
			<xsl:when test="$docLifeCycleMapping/LIFECYCLE_MAPPING_LIST/LIFECYCLE_MAPPING_INFO[System_State eq $SERVICE_STATE ]/Ignore_flag eq 'N'">
				<xsl:value-of select="$docLifeCycleMapping/LIFECYCLE_MAPPING_LIST/LIFECYCLE_MAPPING_INFO[System_State eq $SERVICE_STATE]/SUBS_SUBSCRIBER_STATUS"/>
			</xsl:when>				
		</xsl:choose>-->
	</xsl:template>
	
	<xsl:template name="RULE_SUBS_FIRST_IVR_CALL_DONE">
		<xsl:choose>
			<xsl:when test="$INITIAL_ACTIVATION_DATE_FLAG eq 'Y' and ($SERVICE_STATE eq 'P' or $SERVICE_STATE eq 'S')">
				<xsl:value-of select="'0'"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$docLifeCycleMapping/LIFECYCLE_MAPPING_LIST/LIFECYCLE_MAPPING_INFO[System_State eq $SERVICE_STATE and Initial_Activation_Date eq 'N']/SUBS_FIRST_IVR_CALL_DONE"/>				
			</xsl:otherwise>
		</xsl:choose>
		<!--<xsl:choose>
			<xsl:when test="$docLifeCycleMapping/LIFECYCLE_MAPPING_LIST/LIFECYCLE_MAPPING_INFO[System_State eq $SERVICE_STATE ]/Ignore_flag eq 'N'">
				<xsl:value-of select="$docLifeCycleMapping/LIFECYCLE_MAPPING_LIST/LIFECYCLE_MAPPING_INFO[System_State eq $SERVICE_STATE]/SUBS_FIRST_IVR_CALL_DONE"/>
			</xsl:when>				
		</xsl:choose>-->
	</xsl:template>
	
	<xsl:template name="RULE_SUBS_FIRST_CALL_DONE">
		<xsl:choose>
			<xsl:when test="$INITIAL_ACTIVATION_DATE_FLAG eq 'Y' and ($SERVICE_STATE eq 'P' or $SERVICE_STATE eq 'S')">
				<xsl:value-of select="'0'"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="dm:dateToNumberOfDays(dm:convertTELDateToXSLTDate($INITIAL_ACTIVATION_DATE))"/>				
			</xsl:otherwise>
		</xsl:choose>
		
		<!--<xsl:variable name="MappingValue">
			<xsl:choose>
				<xsl:when test="$docLifeCycleMapping/LIFECYCLE_MAPPING_LIST/LIFECYCLE_MAPPING_INFO[System_State eq $SERVICE_STATE ]/Ignore_flag eq 'N'">
					<xsl:value-of select="$docLifeCycleMapping/LIFECYCLE_MAPPING_LIST/LIFECYCLE_MAPPING_INFO[System_State eq $SERVICE_STATE]/SUBS_FIRST_CALL_DONE"/>
				</xsl:when>				
			</xsl:choose>
		</xsl:variable>
		
		<xsl:choose>
			<xsl:when test="($MappingValue ne '0')">
				<xsl:value-of select="dm:dateToNumberOfDays(dm:convertTELDateToXSLTDate($INITIAL_ACTIVATION_DATE))"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="'0'"/>
			</xsl:otherwise>
		</xsl:choose>-->
	</xsl:template>
	
	<xsl:template name="RULE_SUBS_LANGUAGE">
		<xsl:variable name="TargetValue" select="$docLanguageMapping/LANGUAGE_LIST/LANGUAGE_INFO[Source_Language eq $LANGUAGEID]/Target_Language"/>
		<xsl:choose>
			<xsl:when test="string-length($TargetValue)">
				<xsl:value-of select="$TargetValue"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$default_language"/>
				<xsl:call-template name="dm:addIncHCoded">
					<xsl:with-param name="errorCode" select="'INC4001'"/>
					<xsl:with-param name="errorString1" select="'MSISDN'"/>
					<xsl:with-param name="errorField1" select="$MSISDN"/>
					<xsl:with-param name="errorString2" select="'LANGUAGE_ID'"/>
					<xsl:with-param name="errorField2" select="$LANGUAGEID"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	
	<!---=================================================-->
					<!--DedicatedAccounts.csv-->
	<!---=================================================-->
	<xsl:template name="generate_DedicatedAccounts.csv">
		<xsl:variable name="DA_OUTPUT">
			<xsl:call-template name="generate_DedicatedAccounts"/>
		</xsl:variable>	
		
		<DEDICATED_ACCOUNT_LIST>
			<Account_MSISDN>
				<xsl:value-of select="$MSISDN"/>
			</Account_MSISDN>
			<xsl:call-template name="Transform_DedicatedAccount_CSV">
				<xsl:with-param name="SUBSCRIBER_RECORD" select="$DA_OUTPUT"/>
			</xsl:call-template>
		</DEDICATED_ACCOUNT_LIST>
	</xsl:template>
	
	<xsl:template name="generate_DedicatedAccounts">
		<DEDICATED_ACCOUNT_LIST>
			<Account_MSISDN>
				<xsl:value-of select="$MSISDN"/>
			</Account_MSISDN>
			
			<!--For Balance Bucket List -->
			<xsl:call-template name="RULE_DA_A"/>		
			
			<!-- For Default Services -->
			<xsl:call-template name="RULE_DA_B"/>	
		</DEDICATED_ACCOUNT_LIST>
	</xsl:template>
	
	<xsl:template name="Transform_DedicatedAccount_CSV" >
		<xsl:param name="SUBSCRIBER_RECORD"/>
		<xsl:for-each-group select="$SUBSCRIBER_RECORD/DEDICATED_ACCOUNT_LIST/DEDICATED_ACCOUNT" group-by="id">
			<xsl:variable name="DEDICATED_ID" select="id"/>	
			<xsl:variable name="product_id" select="product_id"/>
			<xsl:variable name="Final_Bal" select="format-number(sum(current-group()/number(balance)), '#0')"/>
			<xsl:variable name="Final_Balance">
				<xsl:choose>
					<xsl:when test="number($Final_Bal) lt 0">
						<xsl:value-of select="'0'"/>
					</xsl:when>					
				</xsl:choose>
			</xsl:variable>
			
			<xsl:variable name="Start_Date">
				<xsl:for-each select="current-group()">
					<xsl:sort select="start_date" order="ascending"/>
					<xsl:if test="(position() eq 1) and (upper-case(start_date) ne $default_NULL) and (start_date ne $EMPTY_STRING)">
						<xsl:value-of select="start_date"/>
					</xsl:if>
				</xsl:for-each>
			</xsl:variable>
			<xsl:variable name="Final_Start_Date">
				<xsl:value-of select="if(string-length($Start_Date) eq 0) 
					then $default_NULL
					else $Start_Date"/>
			</xsl:variable>
			
			<xsl:variable name="Expiry_Date">
				<xsl:for-each select="current-group()">
					<xsl:sort select="expiry_date" order="ascending"/>
					<xsl:if test="(position() = last()) and (upper-case(expiry_date) ne $default_NULL) and (expiry_date ne $EMPTY_STRING)">
						<xsl:value-of select="expiry_date"/>
					</xsl:if>
				</xsl:for-each>
			</xsl:variable>
			<xsl:variable name="Final_Expiry_Date">
				<xsl:value-of select="if(string-length($Expiry_Date) eq 0) 
					then $default_NULL
					else $Expiry_Date"/>
			</xsl:variable>
			<xsl:variable name="FinalProductID">
				<xsl:value-of select="if(string-length($product_id) eq 0) 
					then $default_ZERO
					else $product_id"/>
			</xsl:variable>
			
			<DA>
				<id>
					<xsl:value-of select="$DEDICATED_ID"/>
				</id>
				<balance>
					<xsl:value-of select="$Final_Bal"/>
				</balance>
				<start_date>
					<xsl:value-of select="$Final_Start_Date"/>
				</start_date>
				<expiry_date>
					<xsl:value-of select="$Final_Expiry_Date"/>
				</expiry_date>
				<product_id>											
					<xsl:value-of select="$FinalProductID"/>
				</product_id>
			</DA>
		</xsl:for-each-group>
	</xsl:template>
	
	<xsl:template name="RULE_DA_A">
		<xsl:for-each select="/subscriber_xml/balancesdump_info/schemasubscriberbalancesdump_info">
			<xsl:variable name="Balance_Type_ID" select="BALANCE_TYPE"/>
			<xsl:variable name="DA_NAME" select="BALANCE_TYPE_NAME"/>
			<xsl:variable name="BALANCE" select="BE_BUCKET_VALUE" /> 
			<xsl:variable name="START_DATE" select="BE_BUCKET_START_DATE" /> 
			<xsl:variable name="EXPIRY_DATE" select="BE_EXPIRY" />
			<xsl:message select="$BalanceDAEmptyBucketValueNoSC"/>
			
			<xsl:choose>
				<xsl:when test="contains($BalanceDAEmptyBucketValueNoSC,concat(';',$Balance_Type_ID,'|', '', '|',$CCS_ACCT_TYPE_ID,';'))">
					<xsl:variable name="DA_ID" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and  CCS_ACCT_TYPE_ID eq $CCS_ACCT_TYPE_ID and Ignore_Flag eq 'N']/DA_ID" /> 
					<xsl:variable name="Product_Private" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and CCS_ACCT_TYPE_ID eq $CCS_ACCT_TYPE_ID and Ignore_Flag eq 'N']/Product_Private" /> 			
					<xsl:choose>
						<xsl:when test="string-length($DA_ID)">
							<xsl:variable name="Resource" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and CCS_ACCT_TYPE_ID eq $CCS_ACCT_TYPE_ID and Ignore_Flag eq 'N']/Resource"/>
							<xsl:variable name="Factor" select="$docConversionLogicMapping/CONVERSION_LOGIC_MAPPING_LIST/CONVERSION_LOGIC_MAPPING_INFO[Resource eq $Resource]/Type"/>
							<xsl:variable name="Conversion_Enabled" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and CCS_ACCT_TYPE_ID eq $CCS_ACCT_TYPE_ID and Ignore_Flag eq 'N']/Conversion_Enabled"/>
							
							<!--<xsl:message select="$Balance_Type_ID"/>
							<xsl:message select="$CCS_ACCT_TYPE_ID"/>
							<xsl:message select="$DA_ID"/>
							<xsl:message select="$BalanceDAEmptyBucketValueNoSC"/>-->
							
							<xsl:call-template name="DA_XML_CREATION">
								<xsl:with-param name="DA_ID" select="$DA_ID"/>
								<xsl:with-param name="BALANCE" select="$BALANCE"/>
								<xsl:with-param name="DA_NAME" select="$DA_NAME"/>
								<xsl:with-param name="START_DATE" select="$START_DATE"/>
								<xsl:with-param name="EXPIRY_DATE" select="$EXPIRY_DATE"/>
								<xsl:with-param name="Resource" select="$Resource"/>
								<xsl:with-param name="Conversion_Enabled" select="$Conversion_Enabled"/>
							</xsl:call-template>
						</xsl:when>										
					</xsl:choose>
				</xsl:when>
			</xsl:choose>
			
			<xsl:choose>
				<xsl:when test="contains($BalanceDAEmptyBucketValueYesSC,concat(';',$Balance_Type_ID,'|', '', '|',0,';'))">
					<xsl:variable name="DA_ID" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and  number(CCS_ACCT_TYPE_ID) eq 0 and Ignore_Flag eq 'N']/DA_ID" /> 
					<xsl:variable name="Product_Private" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and number(CCS_ACCT_TYPE_ID) eq 0 and Ignore_Flag eq 'N']/Product_Private" /> 			
					<xsl:choose>
						<xsl:when test="string-length($DA_ID)">
							<xsl:variable name="Resource" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and number(CCS_ACCT_TYPE_ID) eq 0 and Ignore_Flag eq 'N']/Resource"/>
							<xsl:variable name="Factor" select="$docConversionLogicMapping/CONVERSION_LOGIC_MAPPING_LIST/CONVERSION_LOGIC_MAPPING_INFO[Resource eq $Resource]/Type"/>
							<xsl:variable name="Conversion_Enabled" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and number(CCS_ACCT_TYPE_ID) eq 0 and Ignore_Flag eq 'N']/Conversion_Enabled"/>
							
							<!--<xsl:message select="$Balance_Type_ID"/>
							<xsl:message select="$CCS_ACCT_TYPE_ID"/>
							<xsl:message select="$DA_ID"/>
							<xsl:message select="$BalanceDAEmptyBucketValueYesSC"/>-->
							
							<xsl:call-template name="DA_XML_CREATION">
								<xsl:with-param name="DA_ID" select="$DA_ID"/>
								<xsl:with-param name="BALANCE" select="$BALANCE"/>
								<xsl:with-param name="DA_NAME" select="$DA_NAME"/>
								<xsl:with-param name="START_DATE" select="$START_DATE"/>
								<xsl:with-param name="EXPIRY_DATE" select="$EXPIRY_DATE"/>
								<xsl:with-param name="Resource" select="$Resource"/>
								<xsl:with-param name="Conversion_Enabled" select="$Conversion_Enabled"/>
							</xsl:call-template>
						</xsl:when>										
					</xsl:choose>
				</xsl:when>
			</xsl:choose>
			
			<xsl:choose>
				<xsl:when test="contains($BalanceDAAvailableBucketValueNoSC,concat(';',$Balance_Type_ID,'|', $BALANCE, '|',$CCS_ACCT_TYPE_ID,';'))">
					<xsl:variable name="DA_ID" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and (CCS_ACCT_TYPE_ID) eq $CCS_ACCT_TYPE_ID and Ignore_Flag eq 'N']/DA_ID" /> 
					<xsl:variable name="Product_Private" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and (CCS_ACCT_TYPE_ID) eq $CCS_ACCT_TYPE_ID and Ignore_Flag eq 'N']/Product_Private" /> 			
					<xsl:choose>
						<xsl:when test="string-length($DA_ID)">
							<xsl:variable name="Resource" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and (CCS_ACCT_TYPE_ID) eq $CCS_ACCT_TYPE_ID and Ignore_Flag eq 'N']/Resource"/>
							<xsl:variable name="Factor" select="$docConversionLogicMapping/CONVERSION_LOGIC_MAPPING_LIST/CONVERSION_LOGIC_MAPPING_INFO[Resource eq $Resource]/Type"/>
							<xsl:variable name="Conversion_Enabled" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and (CCS_ACCT_TYPE_ID) eq $CCS_ACCT_TYPE_ID and Ignore_Flag eq 'N']/Conversion_Enabled"/>
							
							<!--<xsl:message select="$Balance_Type_ID"/>
							<xsl:message select="$CCS_ACCT_TYPE_ID"/>
							<xsl:message select="$DA_ID"/>
							<xsl:message select="$BalanceDAAvailableBucketValueNoSC"/>-->
							
							<xsl:call-template name="DA_XML_CREATION">
								<xsl:with-param name="DA_ID" select="$DA_ID"/>
								<xsl:with-param name="BALANCE" select="$BALANCE"/>
								<xsl:with-param name="DA_NAME" select="$DA_NAME"/>
								<xsl:with-param name="START_DATE" select="$START_DATE"/>
								<xsl:with-param name="EXPIRY_DATE" select="$EXPIRY_DATE"/>
								<xsl:with-param name="Resource" select="$Resource"/>
								<xsl:with-param name="Conversion_Enabled" select="$Conversion_Enabled"/>
							</xsl:call-template>
						</xsl:when>										
					</xsl:choose>
				</xsl:when>				
			</xsl:choose>
			
			<xsl:choose>
				<xsl:when test="contains($BalanceDAAvailableBucketValueYesSC,concat(';',$Balance_Type_ID,'|', $BALANCE, '|',0,';'))">
					<xsl:variable name="DA_ID" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID  and BE_BUCKET_VALUE eq $BALANCE and number(CCS_ACCT_TYPE_ID) eq 0  and Ignore_Flag eq 'N']/DA_ID" /> 
					<xsl:variable name="Product_Private" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and BE_BUCKET_VALUE eq $BALANCE and number(CCS_ACCT_TYPE_ID) eq 0  and Ignore_Flag eq 'N']/Product_Private" /> 			
					<xsl:choose>
						<xsl:when test="string-length($DA_ID)">
							<xsl:variable name="Resource" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and BE_BUCKET_VALUE eq $BALANCE and number(CCS_ACCT_TYPE_ID) eq 0  and Ignore_Flag eq 'N']/Resource"/>
							<xsl:variable name="Factor" select="$docConversionLogicMapping/CONVERSION_LOGIC_MAPPING_LIST/CONVERSION_LOGIC_MAPPING_INFO[Resource eq $Resource]/Type"/>
							<xsl:variable name="Conversion_Enabled" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and BE_BUCKET_VALUE eq $BALANCE and number(CCS_ACCT_TYPE_ID) eq 0 and Ignore_Flag eq 'N']/Conversion_Enabled"/>
							
							<!--<xsl:message select="$Balance_Type_ID"/>
							<xsl:message select="$CCS_ACCT_TYPE_ID"/>
							<xsl:message select="$DA_ID"/>
							<xsl:message select="$BalanceDAAvailableBucketValueYesSC"/>-->
							
							<xsl:call-template name="DA_XML_CREATION">
								<xsl:with-param name="DA_ID" select="$DA_ID"/>
								<xsl:with-param name="BALANCE" select="$BALANCE"/>
								<xsl:with-param name="DA_NAME" select="$DA_NAME"/>
								<xsl:with-param name="START_DATE" select="$START_DATE"/>
								<xsl:with-param name="EXPIRY_DATE" select="$EXPIRY_DATE"/>
								<xsl:with-param name="Resource" select="$Resource"/>
								<xsl:with-param name="Conversion_Enabled" select="$Conversion_Enabled"/>
							</xsl:call-template>
						</xsl:when>										
					</xsl:choose>
				</xsl:when>
			</xsl:choose>
		
			<!-- This is exclusively for 120.024 320.064 Balance Value -->
			<xsl:choose>
				<xsl:when test="(number($Balance_Type_ID) eq 21) and CCS_ACCT_TYPE_ID eq $CCS_ACCT_TYPE_ID and (number($BALANCE) &lt;= 120.024 or number($BALANCE) &gt;= 320.064) ">
					<xsl:variable name="DA_ID" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and (number(BE_BUCKET_VALUE) &lt;= $BALANCE or number(BE_BUCKET_VALUE) &gt;= 320.064) and CCS_ACCT_TYPE_ID eq $CCS_ACCT_TYPE_ID and Ignore_Flag eq 'N']/DA_ID" /> 
					<xsl:variable name="Product_Private" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and (number(BE_BUCKET_VALUE) &lt;= $BALANCE or number(BE_BUCKET_VALUE) &gt;= 320.064) and CCS_ACCT_TYPE_ID eq $CCS_ACCT_TYPE_ID and Ignore_Flag eq 'N']/Product_Private" /> 			
					<xsl:choose>
						<xsl:when test="string-length($DA_ID)">
							<xsl:variable name="Resource" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and (number(BE_BUCKET_VALUE) &lt;= $BALANCE or number(BE_BUCKET_VALUE) &gt;= 320.064) and CCS_ACCT_TYPE_ID eq $CCS_ACCT_TYPE_ID and Ignore_Flag eq 'N']/Resource"/>
							<xsl:variable name="Factor" select="$docConversionLogicMapping/CONVERSION_LOGIC_MAPPING_LIST/CONVERSION_LOGIC_MAPPING_INFO[Resource eq $Resource]/Type"/>
							<xsl:variable name="Conversion_Enabled" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and (number(BE_BUCKET_VALUE) &lt;= $BALANCE or number(BE_BUCKET_VALUE) &gt;= 320.064) and CCS_ACCT_TYPE_ID eq $CCS_ACCT_TYPE_ID and Ignore_Flag eq 'N']/Conversion_Enabled"/>
							<xsl:call-template name="DA_XML_CREATION">
								<xsl:with-param name="DA_ID" select="$DA_ID"/>
								<xsl:with-param name="BALANCE" select="$BALANCE"/>
								<xsl:with-param name="DA_NAME" select="$DA_NAME"/>
								<xsl:with-param name="START_DATE" select="$START_DATE"/>
								<xsl:with-param name="EXPIRY_DATE" select="$EXPIRY_DATE"/>
								<xsl:with-param name="Resource" select="$Resource"/>
								<xsl:with-param name="Conversion_Enabled" select="$Conversion_Enabled"/>
							</xsl:call-template>
						</xsl:when>										
					</xsl:choose>
				</xsl:when>
			</xsl:choose>			
		</xsl:for-each>
	</xsl:template>
	
	<!--For default services input file -->
	<xsl:template name="RULE_DA_B">	
		<xsl:choose>
			<xsl:when test="string-length($INITIAL_ACTIVATION_DATE) and string-length($CCS_ACCT_TYPE_ID)">
				<xsl:choose>
					<xsl:when test="$docDefaultServices/DEFAULTSERVICES_MAPPING_LIST/DEFAULTSERVICES_MAPPING_INFO[CCS_ACCT_TYPE_ID eq $CCS_ACCT_TYPE_ID]/DA_ID">
						<xsl:variable name="DA_ID" select="$docDefaultServices/DEFAULTSERVICES_MAPPING_LIST/DEFAULTSERVICES_MAPPING_INFO[CCS_ACCT_TYPE_ID eq $CCS_ACCT_TYPE_ID]/DA_ID"/>
						<xsl:variable name="BALANCE" select="$docDefaultServices/DEFAULTSERVICES_MAPPING_LIST/DEFAULTSERVICES_MAPPING_INFO[CCS_ACCT_TYPE_ID eq $CCS_ACCT_TYPE_ID]/DA_Default_Value"/>
						<xsl:variable name="DAType" select="$docDefaultServices/DEFAULTSERVICES_MAPPING_LIST/DEFAULTSERVICES_MAPPING_INFO[CCS_ACCT_TYPE_ID eq $CCS_ACCT_TYPE_ID]/DA_Type"/>
						<DEDICATED_ACCOUNT>
							<id>
								<xsl:value-of select="$DA_ID"/>
							</id>
							<balance>
								<xsl:value-of select="$BALANCE"/>
							</balance>									
							<start_date>
								<xsl:value-of select="$default_EMPTY"/>
							</start_date>
							<expiry_date>
								<xsl:value-of select="$default_NULL"/>
							</expiry_date>
							<product_id>
								<xsl:value-of select="$default_ZERO"/>
							</product_id>
						</DEDICATED_ACCOUNT>
					</xsl:when>
				</xsl:choose>
				<!--<xsl:for-each select="$DefaultServiceList">
					<xsl:variable name="temp" select="."/>
					
				</xsl:for-each>-->
			</xsl:when>
		</xsl:choose>		
	</xsl:template>
		
	<xsl:template name="DA_XML_CREATION">
		<xsl:param name="DA_ID"/>
		<xsl:param name="BALANCE"/>
		<xsl:param name="DA_NAME"/>
		<xsl:param name="START_DATE"/>
		<xsl:param name="EXPIRY_DATE"/>
		<xsl:param name="Resource"/>
		<xsl:param name="Conversion_Enabled"/>
		<xsl:variable name="Factor" select="$docConversionLogicMapping/CONVERSION_LOGIC_MAPPING_LIST/CONVERSION_LOGIC_MAPPING_INFO[Resource eq $Resource]/Type"/>
		
		<xsl:variable name="Final_Amount">
			<xsl:choose>
				<xsl:when test="$Conversion_Enabled eq 'N'">
					<xsl:value-of select="$BALANCE"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:choose>
						<xsl:when test="number($BALANCE) eq 0">
							<xsl:value-of select="0"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:choose>
								<xsl:when test="upper-case($Factor) eq 'MONEY'">
									<xsl:choose>
										<xsl:when test="upper-case($Resource) eq 'DA'">
											
											<xsl:variable name="FinalBalance" select="format-number($BALANCE * 100, '#.##')"/>
											<xsl:value-of select="$FinalBalance"/>
											<xsl:variable name="FinalBalanceNoRounding" select="$BALANCE * 100"/>
											
											<xsl:choose>
												<xsl:when test="$docConversionLogicMapping/CONVERSION_LOGIC_MAPPING_LIST/CONVERSION_LOGIC_MAPPING_INFO[RoundOff eq 'Y']">
													<xsl:call-template name="GenerateRoundOff_Records.csv">
														<xsl:with-param name="errorCode" select="'INC9999'"/>
														<xsl:with-param name="DA_ID" select="$DA_ID"/>
														<xsl:with-param name="DA_Name" select="$DA_NAME"/>
														<xsl:with-param name="BALANCE" select="$BALANCE"/>
														<xsl:with-param name="FinalBalance" select="$FinalBalance"/>
														<xsl:with-param name="FinalBalanceNoRounding" select="$FinalBalanceNoRounding"/>															
													</xsl:call-template>															
												</xsl:when>
											</xsl:choose>
										</xsl:when>
										<xsl:when test="upper-case($Resource) eq 'UC'">
											<xsl:value-of select="format-number(($BALANCE div 10000), '#0')"/>
										</xsl:when>
										<xsl:when test="upper-case($Resource) eq 'DA1'">
											<xsl:variable name="FinalBalance" select="format-number(500 - ($BALANCE div 10000), '#0')"/>
											<xsl:value-of select="$FinalBalance"/>
											<xsl:variable name="FinalBalanceNoRounding" select="500 - ($BALANCE div 10000)"/>
											<xsl:choose>
												<xsl:when test="$docConversionLogicMapping/CONVERSION_LOGIC_MAPPING_LIST/CONVERSION_LOGIC_MAPPING_INFO[RoundOff eq 'Y']">
													<xsl:call-template name="GenerateRoundOff_Records.csv">
														<xsl:with-param name="errorCode" select="'INC9999'"/>
														<xsl:with-param name="DA_ID" select="$DA_ID"/>
														<xsl:with-param name="DA_Name" select="$DA_NAME"/>
														<xsl:with-param name="BALANCE" select="$BALANCE"/>
														<xsl:with-param name="FinalBalance" select="$FinalBalance"/>															
													</xsl:call-template>															
												</xsl:when>
											</xsl:choose>
										</xsl:when>
									</xsl:choose>
								</xsl:when>
								<xsl:when test="upper-case($Factor) eq 'VOLUME'">
									<xsl:choose>
										<xsl:when test="upper-case($Resource) eq 'DATO2'">
											<xsl:value-of select="format-number($BALANCE * 1024, '#0')"/>
										</xsl:when>
										<xsl:when test="upper-case($Resource) eq 'DATO3'">
											<xsl:value-of select="format-number(($BALANCE div 10000) * 1048576, '#0')"/>
										</xsl:when>
									</xsl:choose>
								</xsl:when>
								<xsl:when test="upper-case($Factor) eq 'TIME'">
									<xsl:choose>
										<xsl:when test="upper-case($Resource) eq 'DATO1'">
											<xsl:value-of select="format-number(($BALANCE div 0.1667) * 60, '#0')"/>
										</xsl:when>
										<xsl:when test="upper-case($Resource) eq 'DATO4'">
											<xsl:value-of select="format-number(($BALANCE div 100), '#0')"/>
										</xsl:when>
									</xsl:choose>
								</xsl:when>
								<xsl:when test="upper-case($Factor) eq 'UNITS'">
									<xsl:choose>
										<xsl:when test="upper-case($Resource) eq 'DATO5'">
											<xsl:value-of select="format-number(($BALANCE), '#0')"/>
										</xsl:when>
									</xsl:choose>
								</xsl:when>
							</xsl:choose>
						</xsl:otherwise>
					</xsl:choose>				
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="Final_Start_Date">
			<xsl:choose>
				<xsl:when test="string-length($START_DATE) and (upper-case($START_DATE) ne $default_NULL) and $START_DATE ne '1970-01-01 00:00:00'">
					<xsl:value-of select="dm:dateToNumberOfDays(dm:convertTELDateToXSLTDate($START_DATE))"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$default_EMPTY"/>
				</xsl:otherwise>
			</xsl:choose>								
		</xsl:variable>
		<xsl:variable name="Final_Expiry_Date">
			<xsl:choose>
				<xsl:when test="string-length($EXPIRY_DATE) and (upper-case($EXPIRY_DATE) ne $default_NULL) and $EXPIRY_DATE ne '1970-01-01 00:00:00' ">
					<xsl:value-of select="dm:dateToNumberOfDays(dm:convertTELDateToXSLTDate($EXPIRY_DATE))"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$default_NULL"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<DEDICATED_ACCOUNT>
			<id>
				<xsl:value-of select="$DA_ID"/>
			</id>
			<balance>
				<xsl:value-of select="$Final_Amount"/>
			</balance>									
			<start_date>
				<xsl:value-of select="$Final_Start_Date"/>
			</start_date>
			<expiry_date>
				<xsl:value-of select="$Final_Expiry_Date"/>
			</expiry_date>
			<product_id>
				<xsl:choose>
					<xsl:when test="count($ProductPrivateForDAList)">
						<xsl:for-each select="$ProductPrivateForDAList">
							<xsl:choose>
								<xsl:when test="string-length(.)">
									<xsl:variable name="DA_VALUE" select="tokenize(.,',')[1]"/>
									<xsl:variable name="DA_PRODUCT" select="tokenize(.,',')[2]"/>
									<xsl:choose>
										<xsl:when test="$DA_ID eq $DA_VALUE">
											<xsl:value-of select="$DA_PRODUCT"/>
										</xsl:when>
										<!--<xsl:otherwise>
											<xsl:value-of select="$default_ZERO"/>
										</xsl:otherwise>-->
									</xsl:choose>
								</xsl:when>								
							</xsl:choose>																
						</xsl:for-each>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$default_ZERO"/>
					</xsl:otherwise>
				</xsl:choose>
			</product_id>
		</DEDICATED_ACCOUNT>
	</xsl:template>
	
	<!---=================================================-->
		<!--RoundOff_Records.csv-->
	<!---=================================================-->
	<xsl:template name="GenerateRoundOff_Records.csv">
		<xsl:param name="errorCode"/>
		<xsl:param name="DA_ID"/>
		<xsl:param name="DA_Name"/>
		<xsl:param name="BALANCE"/>
		<xsl:param name="FinalBalance"/>
		<xsl:param name="FinalBalanceNoRounding"/>
		
		<!--<xsl:message select="current-dateTime()"/>
		<xsl:message select="format-dateTime(current-dateTime(), '[Y0001][M01][D01][H01][M01]')"/>
		<xsl:message select="format-dateTime(current-dateTime(), '[Y0001][M01][D01][H01][M01][s]')"/>
		<xsl:message select="format-number(number($FinalBalance)-number($BALANCE),'#.##')"/>-->
		
		<xsl:variable name="Difference" select="format-number(number($FinalBalance) - number($FinalBalanceNoRounding),'#.###')"/>
		<xsl:choose>
			<xsl:when test="$errorCode= 'INC9999' ">
				<xsl:message terminate="no">
					<xsl:value-of select="concat($errorCode,':','BILLING_ENGINE_ID',',','',',',',','5',',',format-dateTime(current-dateTime(),'[Y0001][M01][D01][H01][M01][s]'),
						',','',',','',',','',',','',',','',',','',',','',',',$DA_Name,',','','S',',',format-dateTime(current-dateTime(), '[Y0001][M01][D01][H01][M01][s]'),
						',',$DA_ID,',',$BALANCE,',',$Difference,',',$SERVICE_STATE,',','Account',',','',',',$Difference,',','NULL',',','NULL',',','NULL',',', 'NULL',',',
						'NULL',',','NULL',',','',',',$MSISDN)"/>
					</xsl:message>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
		
	<!---=================================================-->
				<!--PAM Account.csv-->
	<!---=================================================-->
	<xsl:template name="generate_PamAccounts.csv">
		<xsl:variable name="PAM_OUTPUT">			
			<xsl:call-template name="generate_PamAccount"/>
		</xsl:variable>				
		<PERIODIC_ACCOUNT_LIST>
			<Account_MSISDN>
				<xsl:value-of select="$MSISDN"/>
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
				<!--Offer.csv-->
	<!---=================================================-->
		
	
	<xsl:template name="generate_Offer.csv">
		<xsl:variable name="OFFER_OUTPUT">
			<xsl:call-template name="generate_Offer"/>
		</xsl:variable>
		
		<OFFER_LIST>			
			<xsl:call-template name="Transform_Offer_CSV">
				<xsl:with-param name="SUBSCRIBER_RECORD" select="$OFFER_OUTPUT"/>
			</xsl:call-template>			
		</OFFER_LIST>
	</xsl:template>
	
	<xsl:template name="Transform_Offer_CSV">
		<xsl:param name="SUBSCRIBER_RECORD"/>
<!--		<xsl:message select="$SUBSCRIBER_RECORD/OF_LIST/OFFER"/>-->
		<xsl:for-each-group select="$SUBSCRIBER_RECORD/OF_LIST/OFFER" group-by="offer_id">
			<xsl:variable name="product_id" select="product_id"/>
			<xsl:variable name="offer_id" select="offer_id"/>
			<xsl:variable name="Start_Date">
				<xsl:for-each select="current-group()">					
					<xsl:sort select="start_date" order="ascending"/>
					<xsl:if test="(position() eq 1) and (upper-case(start_date) ne $default_NULL) and (start_date ne $EMPTY_STRING)">
						<xsl:value-of select="start_date"/>	
					</xsl:if>
				</xsl:for-each>
			</xsl:variable>
			<xsl:variable name="Final_Start_Date">
				<xsl:value-of select="if(string-length($Start_Date) eq 0) 
					then $default_NULL
					else $Start_Date"/>
			</xsl:variable>
			<xsl:variable name="Expiry_Date">
				<xsl:for-each select="current-group()">
					<xsl:sort select="expiry_date" order="ascending"/>					
					<xsl:if test="(position() = last()) and (upper-case(expiry_date) ne $default_NULL) and (expiry_date ne $EMPTY_STRING)">
						<xsl:value-of select="expiry_date"/>						
					</xsl:if>
				</xsl:for-each>
			</xsl:variable>
			<xsl:variable name="Final_Expiry_Date">
				<xsl:value-of select="if(string-length($Expiry_Date) eq 0) 
					then $default_NULL
					else $Expiry_Date"/>
			</xsl:variable>
			
			<xsl:variable name="Final_Expiry_second">
				<xsl:value-of select="if(string-length(expiry_seconds) eq 0) 
					then $default_NULL
					else expiry_seconds"/>
			</xsl:variable>
			
			<xsl:variable name="Final_Start_second">
				<xsl:value-of select="if(string-length(start_seconds) eq 0) 
					then $default_NULL
					else start_seconds"/>
			</xsl:variable>
			
			<xsl:variable name="FinalProductID">
				<xsl:value-of select="if(string-length($product_id) eq 0) 
					then $default_ZERO
					else $product_id"/>
			</xsl:variable>
			
			<OFFER_VALUE>
				<Account_ID>
					<xsl:value-of select="$MSISDN"/>
				</Account_ID>
				<offer_id>
					<xsl:value-of select="$offer_id"/>
				</offer_id>
				<start_date>
					<xsl:value-of select="$Final_Start_Date"/>
				</start_date>
				<expiry_date>
					<xsl:value-of select="$Final_Expiry_Date"/>
				</expiry_date>
				<start_seconds>
					<xsl:value-of select="$Final_Start_second"/>
				</start_seconds>
				<expiry_seconds>
					<xsl:value-of select="$Final_Expiry_second"/>
				</expiry_seconds>
				<flags>
					<xsl:value-of select="$default_NULL"/>
				</flags>
				<pam_service_id>
					<xsl:value-of select="$default_NULL"/>
				</pam_service_id>
				<product_id>
					<xsl:value-of select="$FinalProductID"/>
				</product_id>				
			</OFFER_VALUE>
		</xsl:for-each-group>
	</xsl:template>
	
	<xsl:template name="generate_Offer">
		<OF_LIST>			
			<!--OfferID form balance mapping sheet using product ID --> 	
			<xsl:call-template name="RULE_OFFER_A"/>
			
			<!-- OfferID from Default Sheet -->
			<xsl:call-template name="RULE_OFFER_B"/>
			
			<!-- OfferID from Profile Tag-->
			<xsl:call-template name="RULE_OFFER_C"/>
			
			<!-- Create Offer from Lifecycle -->
			<xsl:call-template name="RULE_OFFER_D"/>
						
			<!-- Create Offer from ProductMapping -->
			<xsl:call-template name="RULE_OFFER_F"/>
			
			<!-- Create Offer from ProductMapping -->
			<xsl:call-template name="RULE_OFFER_F_2Group"/>
			
			<!-- Create Offer from ProductMapping -->
			<xsl:call-template name="RULE_OFFER_F_3Group"/>
			
		</OF_LIST>
	</xsl:template>
	
	<xsl:template name="RULE_OFFER_A">
		<xsl:for-each select="/subscriber_xml/balancesdump_info/schemasubscriberbalancesdump_info">
			<xsl:variable name="Balance_Type_ID" select="BALANCE_TYPE"/>
			<xsl:variable name="BALANCE" select="BE_BUCKET_VALUE" /> 
			<xsl:variable name="START_DATE" select="BE_BUCKET_START_DATE" /> 
			<xsl:variable name="EXPIRY_DATE" select="BE_EXPIRY" />			
			<xsl:choose>
				<xsl:when test="contains($BalanceOfferEmptyBucketValueNoSC,concat(';',$Balance_Type_ID,'|', '', '|',$CCS_ACCT_TYPE_ID,';'))">
					<xsl:variable name="Offer_ID" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and CCS_ACCT_TYPE_ID eq $CCS_ACCT_TYPE_ID and Ignore_Flag eq 'N']/Offer_ID" />
					<xsl:variable name="Offer_Type" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and CCS_ACCT_TYPE_ID eq $CCS_ACCT_TYPE_ID and Ignore_Flag eq 'N']/Offer_Type" />
					<!--<xsl:message select="'singh'"/>
					<xsl:message select="$Balance_Type_ID"/>
					<xsl:message select="$CCS_ACCT_TYPE_ID"/>
					<xsl:message select="$Offer_ID"/>
					<xsl:message select="$BalanceMapEmptyBucketValueNoSC"/>-->					
					<xsl:choose>
						<xsl:when test="string-length($Offer_ID)">
							<xsl:call-template name="OFFER_XML_CREATION">
								<xsl:with-param name="Offer_ID" select="$Offer_ID"/>
								<xsl:with-param name="Offer_Type" select="$Offer_Type"/>
								<xsl:with-param name="START_DATE" select="$START_DATE"/>
								<xsl:with-param name="EXPIRY_DATE" select="$EXPIRY_DATE"/>
								<xsl:with-param name="AlwaysONNeverExpiry" select="''"/>
							</xsl:call-template>	
						</xsl:when>
					</xsl:choose>	
				</xsl:when>
			</xsl:choose>
						
			<xsl:choose>
				<xsl:when test="contains($BalanceOfferEmptyBucketValueYesSC,concat(';',$Balance_Type_ID,'|', '', '|',0,';'))">
					<xsl:variable name="Offer_ID" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and number(CCS_ACCT_TYPE_ID) eq 0 and Ignore_Flag eq 'N']/Offer_ID" />
					<xsl:variable name="Offer_Type" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and number(CCS_ACCT_TYPE_ID) eq 0 and Ignore_Flag eq 'N']/Offer_Type" />
					<!--<xsl:message select="'singh-YesSC'"/>
					<xsl:message select="$Balance_Type_ID"/>
					<xsl:message select="$CCS_ACCT_TYPE_ID"/>
					<xsl:message select="$Offer_ID"/>	
					<xsl:message select="$BalanceMapEmptyBucketValueYesSC"/>-->
					<xsl:choose>
						<xsl:when test="string-length($Offer_ID)">
							<xsl:call-template name="OFFER_XML_CREATION">
								<xsl:with-param name="Offer_ID" select="$Offer_ID"/>
								<xsl:with-param name="Offer_Type" select="$Offer_Type"/>
								<xsl:with-param name="START_DATE" select="$START_DATE"/>
								<xsl:with-param name="EXPIRY_DATE" select="$EXPIRY_DATE"/>
								<xsl:with-param name="AlwaysONNeverExpiry" select="''"/>
							</xsl:call-template>	
						</xsl:when>
					</xsl:choose>	
				</xsl:when>
			</xsl:choose>
			
			<xsl:choose>
				<xsl:when test="contains($BalanceOfferAvailableBucketValueNoSC,concat(';',$Balance_Type_ID,'|', $BALANCE, '|',$CCS_ACCT_TYPE_ID,';'))">
					<xsl:variable name="Offer_ID" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and BE_BUCKET_VALUE eq $BALANCE and (CCS_ACCT_TYPE_ID) eq $CCS_ACCT_TYPE_ID and Ignore_Flag eq 'N']/Offer_ID" />
					<xsl:variable name="Offer_Type" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and BE_BUCKET_VALUE eq $BALANCE and (CCS_ACCT_TYPE_ID) eq $CCS_ACCT_TYPE_ID and Ignore_Flag eq 'N']/Offer_Type" />
					<!--<xsl:message select="'vipin'"/>
					<xsl:message select="$Balance_Type_ID"/>
					<xsl:message select="$CCS_ACCT_TYPE_ID"/>
					<xsl:message select="BE_BUCKET_VALUE"/>
					<xsl:message select="$Offer_ID"/>			
					<xsl:message select="$BalanceOfferAvailableBucketValueNoSC"/>-->
					<xsl:choose>
						<xsl:when test="string-length($Offer_ID)">
							<xsl:call-template name="OFFER_XML_CREATION">
								<xsl:with-param name="Offer_ID" select="$Offer_ID"/>
								<xsl:with-param name="Offer_Type" select="$Offer_Type"/>
								<xsl:with-param name="START_DATE" select="$START_DATE"/>
								<xsl:with-param name="EXPIRY_DATE" select="$EXPIRY_DATE"/>
								<xsl:with-param name="AlwaysONNeverExpiry" select="''"/>
							</xsl:call-template>	
						</xsl:when>
					</xsl:choose>	
				</xsl:when>
			</xsl:choose>
			
			<xsl:choose>
				<xsl:when test="contains($BalanceOfferAvailableBucketValueYesSC,concat(';',$Balance_Type_ID,'|', $BALANCE, '|',0,';'))">
					<xsl:variable name="Offer_ID" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and BE_BUCKET_VALUE eq $BALANCE and number(CCS_ACCT_TYPE_ID) eq 0 and Ignore_Flag eq 'N']/Offer_ID" />
					<xsl:variable name="Offer_Type" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and BE_BUCKET_VALUE eq $BALANCE and number(CCS_ACCT_TYPE_ID) eq 0 and Ignore_Flag eq 'N']/Offer_Type" />
					<!--<xsl:message select="'vipin-YesSC'"/>
					<xsl:message select="$Balance_Type_ID"/>
					<xsl:message select="$CCS_ACCT_TYPE_ID"/>
					<xsl:message select="$Offer_ID"/>			
					<xsl:message select="$BalanceMapAvailableBucketValueYesSC"/>-->
					<xsl:choose>
						<xsl:when test="string-length($Offer_ID)">
							<xsl:call-template name="OFFER_XML_CREATION">
								<xsl:with-param name="Offer_ID" select="$Offer_ID"/>
								<xsl:with-param name="Offer_Type" select="$Offer_Type"/>
								<xsl:with-param name="START_DATE" select="$START_DATE"/>
								<xsl:with-param name="EXPIRY_DATE" select="$EXPIRY_DATE"/>
								<xsl:with-param name="AlwaysONNeverExpiry" select="''"/>
							</xsl:call-template>	
						</xsl:when>
					</xsl:choose>	
				</xsl:when>
			</xsl:choose>
			
			<!-- This is exclusively for 120.024 320.064 Balance Value -->
			<xsl:choose>
				<xsl:when test="(number($Balance_Type_ID) eq 21) and CCS_ACCT_TYPE_ID eq $CCS_ACCT_TYPE_ID and (number($BALANCE) &lt;= 120.024 or number($BALANCE) &gt;= 320.064) ">
					<xsl:variable name="Offer_ID" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and (number(BE_BUCKET_VALUE) &lt;= $BALANCE or number(BE_BUCKET_VALUE) &gt;= 320.064) and CCS_ACCT_TYPE_ID eq $CCS_ACCT_TYPE_ID and Ignore_Flag eq 'N']/Offer_ID" />
					<xsl:variable name="Offer_Type" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and (number(BE_BUCKET_VALUE) &lt;= $BALANCE or number(BE_BUCKET_VALUE) &gt;= 320.064) and CCS_ACCT_TYPE_ID eq $CCS_ACCT_TYPE_ID and Ignore_Flag eq 'N']/Offer_Type" />
					<!--<xsl:message select="'vipin'"/>
					<xsl:message select="$Balance_Type_ID"/>
					<xsl:message select="$CCS_ACCT_TYPE_ID"/>
					<xsl:message select="$Offer_ID"/>-->
					<xsl:choose>
						<xsl:when test="string-length($Offer_ID)">
							<xsl:call-template name="OFFER_XML_CREATION">
								<xsl:with-param name="Offer_ID" select="$Offer_ID"/>
								<xsl:with-param name="Offer_Type" select="$Offer_Type"/>
								<xsl:with-param name="START_DATE" select="$START_DATE"/>
								<xsl:with-param name="EXPIRY_DATE" select="$EXPIRY_DATE"/>
								<xsl:with-param name="AlwaysONNeverExpiry" select="''"/>
							</xsl:call-template>											
						</xsl:when>
					</xsl:choose>									
				</xsl:when>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>
			
	<xsl:template name="RULE_OFFER_B">
		<xsl:for-each select="$docDefaultServices/DEFAULTSERVICES_MAPPING_LIST/DEFAULTSERVICES_MAPPING_INFO">			
			<xsl:choose>
				<xsl:when test="(string-length(Offer_Id) gt 0) and (Ignore_Flag eq 'N')">
					<xsl:variable name="Offer_ID" select="Offer_Id"/>
					<xsl:variable name="Offer_Type" select="Offer_Type"/>
					<xsl:variable name="Offer_Rule_Id" select="Offer_Rule_Id"/>
					<xsl:call-template name="OFFER_XML_CREATION">
						<xsl:with-param name="Offer_ID" select="$Offer_ID"/>
						<xsl:with-param name="Offer_Type" select="$Offer_Type"/>
						<xsl:with-param name="START_DATE" select="''"/>
						<xsl:with-param name="EXPIRY_DATE" select="''"/>
						<xsl:with-param name="AlwaysONNeverExpiry" select="''"/>
					</xsl:call-template>
				</xsl:when>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template name="RULE_OFFER_C">
		<!-- For topxcountr1 -->
		<xsl:choose> 		
			<xsl:when test="$TOPXCOUNTR1 eq '1'">
				<xsl:choose>
					<xsl:when test="$docProfileTagMapping/PROFILETAG_MAPPING_LIST/PROFILETAG_MAPPING_INFO[upper-case(Profile_Tag_Name) eq 'TOPXCOUNTR1']/Ignore_Flag eq 'N'">
						<xsl:variable name="AlwaysONNeverExpiry" select="$docProfileTagMapping/PROFILETAG_MAPPING_LIST/PROFILETAG_MAPPING_INFO[upper-case(Profile_Tag_Name) eq 'TOPXCOUNTR1' and Ignore_Flag eq 'N']/Offer_Always_No_Never_Expiry"/>
						<xsl:variable name="Offer_Type" select="$docProfileTagMapping/PROFILETAG_MAPPING_LIST/PROFILETAG_MAPPING_INFO[upper-case(Profile_Tag_Name) eq 'TOPXCOUNTR1' and Ignore_Flag eq 'N']/Offer_Type"/>
						<xsl:variable name="Offer_ID" select="$docProfileTagMapping/PROFILETAG_MAPPING_LIST/PROFILETAG_MAPPING_INFO[upper-case(Profile_Tag_Name) eq 'TOPXCOUNTR1' and Ignore_Flag eq 'N']/Offer_Id"/>
						<xsl:call-template name="OFFER_XML_CREATION">
							<xsl:with-param name="Offer_ID" select="$Offer_ID"/>
							<xsl:with-param name="Offer_Type" select="$Offer_Type"/>
							<xsl:with-param name="START_DATE" select="''"/>
							<xsl:with-param name="EXPIRY_DATE" select="''"/>
							<xsl:with-param name="AlwaysONNeverExpiry" select="$AlwaysONNeverExpiry"/>
						</xsl:call-template>						
					</xsl:when>
				</xsl:choose>
			</xsl:when>
		</xsl:choose>
		
		<!-- For topxcountr2 -->
		<xsl:choose>
			<xsl:when test="$TOPXCOUNTR2 eq '1'">
				<xsl:choose>					
					<xsl:when test="$docProfileTagMapping/PROFILETAG_MAPPING_LIST/PROFILETAG_MAPPING_INFO[upper-case(Profile_Tag_Name) eq 'TOPXCOUNTR2']/Ignore_Flag eq 'N'">
						<xsl:variable name="AlwaysONNeverExpiry" select="$docProfileTagMapping/PROFILETAG_MAPPING_LIST/PROFILETAG_MAPPING_INFO[upper-case(Profile_Tag_Name) eq 'TOPXCOUNTR2' and Ignore_Flag eq 'N']/Offer_Always_No_Never_Expiry"/>
						<xsl:variable name="Offer_Type" select="$docProfileTagMapping/PROFILETAG_MAPPING_LIST/PROFILETAG_MAPPING_INFO[upper-case(Profile_Tag_Name) eq 'TOPXCOUNTR2' and Ignore_Flag eq 'N']/Offer_Type"/>
						<xsl:variable name="Offer_ID" select="$docProfileTagMapping/PROFILETAG_MAPPING_LIST/PROFILETAG_MAPPING_INFO[upper-case(Profile_Tag_Name) eq 'TOPXCOUNTR2' and Ignore_Flag eq 'N']/Offer_Id"/>
						<xsl:call-template name="OFFER_XML_CREATION">
							<xsl:with-param name="Offer_ID" select="$Offer_ID"/>
							<xsl:with-param name="Offer_Type" select="$Offer_Type"/>
							<xsl:with-param name="START_DATE" select="''"/>
							<xsl:with-param name="EXPIRY_DATE" select="''"/>
							<xsl:with-param name="AlwaysONNeverExpiry" select="$AlwaysONNeverExpiry"/>
						</xsl:call-template>
					</xsl:when>
				</xsl:choose>
			</xsl:when>
		</xsl:choose>
		
		<!-- For topxcountr3 -->
		<xsl:choose>	
			<xsl:when test="$TOPXCOUNTR3 eq '1'">
				<xsl:choose>
					<xsl:when test="$docProfileTagMapping/PROFILETAG_MAPPING_LIST/PROFILETAG_MAPPING_INFO[upper-case(Profile_Tag_Name) eq 'TOPXCOUNTR3']/Ignore_Flag eq 'N'">
						<xsl:variable name="AlwaysONNeverExpiry" select="$docProfileTagMapping/PROFILETAG_MAPPING_LIST/PROFILETAG_MAPPING_INFO[upper-case(Profile_Tag_Name) eq 'TOPXCOUNTR3' and Ignore_Flag eq 'N']/Offer_Always_No_Never_Expiry"/>
						<xsl:variable name="Offer_Type" select="$docProfileTagMapping/PROFILETAG_MAPPING_LIST/PROFILETAG_MAPPING_INFO[upper-case(Profile_Tag_Name) eq 'TOPXCOUNTR3' and Ignore_Flag eq 'N']/Offer_Type"/>
						<xsl:variable name="Offer_ID" select="$docProfileTagMapping/PROFILETAG_MAPPING_LIST/PROFILETAG_MAPPING_INFO[upper-case(Profile_Tag_Name) eq 'TOPXCOUNTR3' and Ignore_Flag eq 'N']/Offer_Id"/>
						<xsl:call-template name="OFFER_XML_CREATION">
							<xsl:with-param name="Offer_ID" select="$Offer_ID"/>
							<xsl:with-param name="Offer_Type" select="$Offer_Type"/>
							<xsl:with-param name="START_DATE" select="''"/>
							<xsl:with-param name="EXPIRY_DATE" select="''"/>
							<xsl:with-param name="AlwaysONNeverExpiry" select="$AlwaysONNeverExpiry"/>
						</xsl:call-template>
					</xsl:when>
				</xsl:choose>
			</xsl:when>
		</xsl:choose>
		
		<!-- For topxcountr4 -->
		<xsl:choose>
			<xsl:when test="$TOPXCOUNTR4 eq '1'">
				<xsl:choose>
					<xsl:when test="$docProfileTagMapping/PROFILETAG_MAPPING_LIST/PROFILETAG_MAPPING_INFO[upper-case(Profile_Tag_Name) eq 'TOPXCOUNTR4']/Ignore_Flag eq 'N'">
						<xsl:variable name="AlwaysONNeverExpiry" select="$docProfileTagMapping/PROFILETAG_MAPPING_LIST/PROFILETAG_MAPPING_INFO[upper-case(Profile_Tag_Name) eq 'TOPXCOUNTR4' and Ignore_Flag eq 'N']/Offer_Always_No_Never_Expiry"/>
						<xsl:variable name="Offer_Type" select="$docProfileTagMapping/PROFILETAG_MAPPING_LIST/PROFILETAG_MAPPING_INFO[upper-case(Profile_Tag_Name) eq 'TOPXCOUNTR4' and Ignore_Flag eq 'N']/Offer_Type"/>
						<xsl:variable name="Offer_ID" select="$docProfileTagMapping/PROFILETAG_MAPPING_LIST/PROFILETAG_MAPPING_INFO[upper-case(Profile_Tag_Name) eq 'TOPXCOUNTR4' and Ignore_Flag eq 'N']/Offer_Id"/>
						<xsl:call-template name="OFFER_XML_CREATION">
							<xsl:with-param name="Offer_ID" select="$Offer_ID"/>
							<xsl:with-param name="Offer_Type" select="$Offer_Type"/>
							<xsl:with-param name="START_DATE" select="''"/>
							<xsl:with-param name="EXPIRY_DATE" select="''"/>
							<xsl:with-param name="AlwaysONNeverExpiry" select="$AlwaysONNeverExpiry"/>
						</xsl:call-template>
					</xsl:when>
				</xsl:choose>
			</xsl:when>
		</xsl:choose>
		
		<!-- For topxcountr5 -->
		<xsl:choose>
			<xsl:when test="$TOPXCOUNTR5 eq '1'">
				<xsl:choose>
					<xsl:when test="$docProfileTagMapping/PROFILETAG_MAPPING_LIST/PROFILETAG_MAPPING_INFO[upper-case(Profile_Tag_Name) eq 'TOPXCOUNTR5']/Ignore_Flag eq 'N'">
						<xsl:variable name="AlwaysONNeverExpiry" select="$docProfileTagMapping/PROFILETAG_MAPPING_LIST/PROFILETAG_MAPPING_INFO[upper-case(Profile_Tag_Name) eq 'TOPXCOUNTR5' and Ignore_Flag eq 'N']/Offer_Always_No_Never_Expiry"/>
						<xsl:variable name="Offer_Type" select="$docProfileTagMapping/PROFILETAG_MAPPING_LIST/PROFILETAG_MAPPING_INFO[upper-case(Profile_Tag_Name) eq 'TOPXCOUNTR5' and Ignore_Flag eq 'N']/Offer_Type"/>
						<xsl:variable name="Offer_ID" select="$docProfileTagMapping/PROFILETAG_MAPPING_LIST/PROFILETAG_MAPPING_INFO[upper-case(Profile_Tag_Name) eq 'TOPXCOUNTR5' and Ignore_Flag eq 'N']/Offer_Id"/>
						<xsl:call-template name="OFFER_XML_CREATION">
							<xsl:with-param name="Offer_ID" select="$Offer_ID"/>
							<xsl:with-param name="Offer_Type" select="$Offer_Type"/>
							<xsl:with-param name="START_DATE" select="''"/>
							<xsl:with-param name="EXPIRY_DATE" select="''"/>
							<xsl:with-param name="AlwaysONNeverExpiry" select="$AlwaysONNeverExpiry"/>
						</xsl:call-template>
					</xsl:when>
				</xsl:choose>
			</xsl:when>
		</xsl:choose>
		
		<!-- For Prepaid -->
		<xsl:choose>
			<xsl:when test="$PREPAID eq '1'">
				<xsl:choose>
					<xsl:when test="$docProfileTagMapping/PROFILETAG_MAPPING_LIST/PROFILETAG_MAPPING_INFO[upper-case(Profile_Tag_Name) eq 'PREPAID']/Ignore_Flag eq 'N'">
						<xsl:variable name="AlwaysONNeverExpiry" select="$docProfileTagMapping/PROFILETAG_MAPPING_LIST/PROFILETAG_MAPPING_INFO[upper-case(Profile_Tag_Name) eq 'PREPAID' and Ignore_Flag eq 'N']/Offer_Always_No_Never_Expiry"/>
						<xsl:variable name="Offer_Type" select="$docProfileTagMapping/PROFILETAG_MAPPING_LIST/PROFILETAG_MAPPING_INFO[upper-case(Profile_Tag_Name) eq 'PREPAID' and Ignore_Flag eq 'N']/Offer_Type"/>
						<xsl:variable name="Offer_ID" select="$docProfileTagMapping/PROFILETAG_MAPPING_LIST/PROFILETAG_MAPPING_INFO[upper-case(Profile_Tag_Name) eq 'PREPAID' and Ignore_Flag eq 'N']/Offer_Id"/>
						<xsl:call-template name="OFFER_XML_CREATION">
							<xsl:with-param name="Offer_ID" select="$Offer_ID"/>
							<xsl:with-param name="Offer_Type" select="$Offer_Type"/>
							<xsl:with-param name="START_DATE" select="''"/>
							<xsl:with-param name="EXPIRY_DATE" select="''"/>
							<xsl:with-param name="AlwaysONNeverExpiry" select="$AlwaysONNeverExpiry"/>
						</xsl:call-template>
					</xsl:when>
				</xsl:choose>
			</xsl:when>
		</xsl:choose>
		
		<!-- For ENTBSNSSCRCLACTV -->
		<xsl:choose>
			<xsl:when test="$ENTBSNSSCRCLACTV eq '1'">
				<xsl:choose>
					<xsl:when test="$docProfileTagMapping/PROFILETAG_MAPPING_LIST/PROFILETAG_MAPPING_INFO[upper-case(Profile_Tag_Name) eq 'ENTBSNSSCRCLACTV']/Ignore_Flag eq 'N'">
						<xsl:variable name="AlwaysONNeverExpiry" select="$docProfileTagMapping/PROFILETAG_MAPPING_LIST/PROFILETAG_MAPPING_INFO[upper-case(Profile_Tag_Name) eq 'ENTBSNSSCRCLACTV' and Ignore_Flag eq 'N']/Offer_Always_No_Never_Expiry"/>
						<xsl:variable name="Offer_Type" select="$docProfileTagMapping/PROFILETAG_MAPPING_LIST/PROFILETAG_MAPPING_INFO[upper-case(Profile_Tag_Name) eq 'ENTBSNSSCRCLACTV' and Ignore_Flag eq 'N']/Offer_Type"/>
						<xsl:variable name="Offer_ID" select="$docProfileTagMapping/PROFILETAG_MAPPING_LIST/PROFILETAG_MAPPING_INFO[upper-case(Profile_Tag_Name) eq 'ENTBSNSSCRCLACTV' and Ignore_Flag eq 'N']/Offer_Id"/>
						<xsl:call-template name="OFFER_XML_CREATION">
							<xsl:with-param name="Offer_ID" select="$Offer_ID"/>
							<xsl:with-param name="Offer_Type" select="$Offer_Type"/>
							<xsl:with-param name="START_DATE" select="''"/>
							<xsl:with-param name="EXPIRY_DATE" select="''"/>
							<xsl:with-param name="AlwaysONNeverExpiry" select="$AlwaysONNeverExpiry"/>
						</xsl:call-template>
					</xsl:when>
				</xsl:choose>
			</xsl:when>
		</xsl:choose>		
	</xsl:template>
	
	<xsl:template name="RULE_OFFER_D">
		<xsl:choose>
			<xsl:when test="$SERVICE_STATE eq 'S' and $INITIAL_ACTIVATION_DATE_FLAG eq 'N'">
				<xsl:call-template name="OFFER_XML_CREATION">
					<xsl:with-param name="Offer_ID" select="553"/>
					<xsl:with-param name="Offer_Type" select="'Timer'"/>
					<xsl:with-param name="START_DATE" select="''"/>
					<xsl:with-param name="EXPIRY_DATE" select="''"/>
					<xsl:with-param name="AlwaysONNeverExpiry" select="''"/>
				</xsl:call-template>				
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="$SERVICE_STATE eq 'F'">
						<xsl:call-template name="OFFER_XML_CREATION">
							<xsl:with-param name="Offer_ID" select="552"/>
							<xsl:with-param name="Offer_Type" select="'Timer'"/>
							<xsl:with-param name="START_DATE" select="''"/>
							<xsl:with-param name="EXPIRY_DATE" select="''"/>
							<xsl:with-param name="AlwaysONNeverExpiry" select="''"/>
						</xsl:call-template>				
					</xsl:when>
					<xsl:otherwise>
						<xsl:choose>
							<xsl:when test="$SERVICE_STATE eq 'D'">
								<xsl:call-template name="OFFER_XML_CREATION">
									<xsl:with-param name="Offer_ID" select="554"/>
									<xsl:with-param name="Offer_Type" select="'Timer'"/>
									<xsl:with-param name="START_DATE" select="''"/>
									<xsl:with-param name="EXPIRY_DATE" select="''"/>
									<xsl:with-param name="AlwaysONNeverExpiry" select="''"/>
								</xsl:call-template>				
							</xsl:when>
						</xsl:choose>
					</xsl:otherwise>
				</xsl:choose>		
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template name="RULE_OFFER_F">
		<!--<xsl:message select="$ProductMappingOnlyBalanceIDValue"/>
		<xsl:message select="$ProductMappingOnlyBalanceIDValueSecondBalance"/>
		<xsl:message select="$ProductMappingOnlySecondBalanceEmptyIdentifier"/>-->

		<xsl:for-each select="/subscriber_xml/balancesdump_info/schemasubscriberbalancesdump_info">
			<xsl:variable name="Balance_Type_ID" select="BALANCE_TYPE"/>
			<xsl:variable name="BALANCE" select="BE_BUCKET_VALUE" /> 
			<xsl:variable name="START_DATE" select="BE_BUCKET_START_DATE" /> 
			<xsl:variable name="EXPIRY_DATE" select="BE_EXPIRY" />			
			
			<xsl:choose>
				<xsl:when test="contains($ProductMappingOnlyBalanceIDValue,concat(';',$Balance_Type_ID,'|', $BALANCE, '|','',';'))">				
					<xsl:variable name="Offer_ID" select="$docProduct_Mapping//PRODUCT_MAPPING_LIST/PRODUCT_MAPPING_INFO[BT_PC_ID eq $Balance_Type_ID and Ignore_Flag eq 'N']/Offer_ID" />
					<xsl:variable name="Offer_Type" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[BT_PC_ID eq $Balance_Type_ID and Ignore_Flag eq 'N']/Offer_Type" />
					<!--<xsl:message select="'singh-YesSC'"/>
					<xsl:message select="$Balance_Type_ID"/>
					<xsl:message select="$CCS_ACCT_TYPE_ID"/>
					<xsl:message select="$Offer_ID"/>	
					<xsl:message select="$BalanceMapEmptyBucketValueYesSC"/>-->
					<xsl:choose>
						<xsl:when test="string-length($Offer_ID)">
							<xsl:call-template name="OFFER_XML_CREATION">
								<xsl:with-param name="Offer_ID" select="$Offer_ID"/>
								<xsl:with-param name="Offer_Type" select="$Offer_Type"/>
								<xsl:with-param name="START_DATE" select="$START_DATE"/>
								<xsl:with-param name="EXPIRY_DATE" select="$EXPIRY_DATE"/>
								<xsl:with-param name="AlwaysONNeverExpiry" select="''"/>
							</xsl:call-template>	
						</xsl:when>						
					</xsl:choose>
				</xsl:when>				
			</xsl:choose>
			
			<xsl:choose>
				<xsl:when test="contains($ProductMappingOnlyBalanceIDValueSecondBalance,concat(';',$Balance_Type_ID,'|',$BALANCE,';'))">
					<xsl:variable name="SecondBalanceList" select="$docProduct_Mapping/PRODUCT_MAPPING_LIST/PRODUCT_MAPPING_INFO[BT_PC_ID eq $Balance_Type_ID and PC_BT_Value eq $BALANCE]/BT_ID"/>
						<xsl:for-each select="$SecondBalanceList">
							<xsl:variable name="SecondBalance" select="."/>
							<xsl:choose>
								<xsl:when test="contains($ProductMappingSecondBalance,concat(';',$SecondBalance,';'))">
									<xsl:variable name="Offer_ID" select="$docProduct_Mapping/PRODUCT_MAPPING_LIST/PRODUCT_MAPPING_INFO[BT_PC_ID eq $Balance_Type_ID and BT_ID eq $SecondBalance  and Ignore_Flag eq 'N']/Offer_ID" />
									<xsl:variable name="Offer_Type" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[BT_PC_ID eq $Balance_Type_ID and BT_ID eq $SecondBalance and Ignore_Flag eq 'N']/Offer_Type" />
									<xsl:choose>
										<xsl:when test="string-length($Offer_ID)">
											<!--<xsl:message select="'*********************************'"/>
											<xsl:message select="$Balance_Type_ID"/>
											<xsl:message select="$SecondBalance"/>											
											<xsl:message select="$Offer_ID"/>
											<xsl:message select="'++++++++++++++++++++++++++++++++++'"/>-->
											<xsl:call-template name="OFFER_XML_CREATION">
												<xsl:with-param name="Offer_ID" select="$Offer_ID"/>
												<xsl:with-param name="Offer_Type" select="$Offer_Type"/>
												<xsl:with-param name="START_DATE" select="$START_DATE"/>
												<xsl:with-param name="EXPIRY_DATE" select="$EXPIRY_DATE"/>
												<xsl:with-param name="AlwaysONNeverExpiry" select="''"/>
											</xsl:call-template>	
										</xsl:when>						
									</xsl:choose>	
								</xsl:when>
							</xsl:choose>
						</xsl:for-each>
						
						
						
						
						
						<!--<xsl:for-each select="/subscriber_xml/balancesdump_info/schemasubscriberbalancesdump_info">
							<xsl:message select="'singh'"/>
							<xsl:variable name="Second_Balance" select="BALANCE_TYPE"/>
							
							<xsl:choose>
								<xsl:when test="$Second_Balance eq $SecondBalanceID">
									<xsl:variable name="Offer_ID" select="$docProduct_Mapping/PRODUCT_MAPPING_LIST/PRODUCT_MAPPING_INFO[BT_PC_ID eq $Balance_Type_ID and BT_ID eq $SecondBalanceID  and Ignore_Flag eq 'N']/Offer_ID" />
									<xsl:variable name="Offer_Type" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[BT_PC_ID eq $Balance_Type_ID and BT_ID eq $SecondBalanceID and Ignore_Flag eq 'N']/Offer_Type" />
									
									<!-\-<xsl:message select="'singh-YesSC'"/>
									<xsl:message select="$Balance_Type_ID"/>
									<xsl:message select="$CCS_ACCT_TYPE_ID"/>
									<xsl:message select="$Offer_ID"/>	
									<xsl:message select="$BalanceMapEmptyBucketValueYesSC"/>-\->
									<xsl:choose>
										<xsl:when test="string-length($Offer_ID)">
											<xsl:call-template name="OFFER_XML_CREATION">
												<xsl:with-param name="Offer_ID" select="$Offer_ID"/>
												<xsl:with-param name="Offer_Type" select="$Offer_Type"/>
												<xsl:with-param name="START_DATE" select="$START_DATE"/>
												<xsl:with-param name="EXPIRY_DATE" select="$EXPIRY_DATE"/>
												<xsl:with-param name="AlwaysONNeverExpiry" select="''"/>
											</xsl:call-template>	
										</xsl:when>						
									</xsl:choose>
								</xsl:when>
							</xsl:choose>				
						</xsl:for-each>-->
				</xsl:when>
			</xsl:choose>
			
			<xsl:choose>
				<xsl:when test="contains($ProductMappingOnlySecondBalanceEmptyIdentifier,concat(';',$Balance_Type_ID,';'))">				
					<xsl:variable name="Symbols" select="$docProduct_Mapping/PRODUCT_MAPPING_LIST/PRODUCT_MAPPING_INFO[BT_ID eq $Balance_Type_ID and Ignore_Flag eq 'N']/Symbols"/>
					<xsl:variable name="BT_Value" select="$docProduct_Mapping/PRODUCT_MAPPING_LIST/PRODUCT_MAPPING_INFO[BT_ID eq $Balance_Type_ID and Ignore_Flag eq 'N']/BT_Value"/>
					<xsl:variable name="Resource" select="$docProduct_Mapping/PRODUCT_MAPPING_LIST/PRODUCT_MAPPING_INFO[BT_ID eq $Balance_Type_ID and Ignore_Flag eq 'N']/Resource"/>
					<xsl:choose>
						<xsl:when test="$Resource eq 'BT'">
							<xsl:choose>
								<xsl:when test="$Symbols eq '>'">
									<xsl:choose>
										<xsl:when test="$BALANCE > $BT_Value">	
											<xsl:variable name="Offer_ID" select="$docProduct_Mapping//PRODUCT_MAPPING_LIST/PRODUCT_MAPPING_INFO[BT_ID eq $Balance_Type_ID and Ignore_Flag eq 'N']/Offer_ID" />
											<xsl:variable name="Offer_Type" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[BT_ID eq $Balance_Type_ID and Ignore_Flag eq 'N']/Offer_Type" />
											
											<xsl:choose>
												<xsl:when test="string-length($Offer_ID)">
													<xsl:call-template name="OFFER_XML_CREATION">
														<xsl:with-param name="Offer_ID" select="$Offer_ID"/>
														<xsl:with-param name="Offer_Type" select="$Offer_Type"/>
														<xsl:with-param name="START_DATE" select="$START_DATE"/>
														<xsl:with-param name="EXPIRY_DATE" select="$EXPIRY_DATE"/>
														<xsl:with-param name="AlwaysONNeverExpiry" select="''"/>
													</xsl:call-template>	
												</xsl:when>
											</xsl:choose>
										</xsl:when>
									</xsl:choose>
								</xsl:when>								
							</xsl:choose>							
						</xsl:when>
						<xsl:otherwise>
							<xsl:choose>
								<xsl:when test="$Resource eq 'MBTV'">
									<xsl:choose>
										<xsl:when test="$Symbols eq 'or'">	
											<xsl:variable name="tokeniseBT_VALUE" select="tokenize($BT_Value,',')"/>
											<xsl:for-each select="$tokeniseBT_VALUE">
												<xsl:choose>
													<xsl:when test="$BALANCE eq .">
														<xsl:variable name="Offer_ID" select="$docProduct_Mapping//PRODUCT_MAPPING_LIST/PRODUCT_MAPPING_INFO[BT_ID eq $Balance_Type_ID and Ignore_Flag eq 'N']/Offer_ID" />
														<xsl:variable name="Offer_Type" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[BT_ID eq $Balance_Type_ID and Ignore_Flag eq 'N']/Offer_Type" />
																									
														<xsl:choose>
															<xsl:when test="string-length($Offer_ID)">
																<xsl:call-template name="OFFER_XML_CREATION">
																	<xsl:with-param name="Offer_ID" select="$Offer_ID"/>
																	<xsl:with-param name="Offer_Type" select="$Offer_Type"/>
																	<xsl:with-param name="START_DATE" select="$START_DATE"/>
																	<xsl:with-param name="EXPIRY_DATE" select="$EXPIRY_DATE"/>
																	<xsl:with-param name="AlwaysONNeverExpiry" select="''"/>
																</xsl:call-template>	
															</xsl:when>
														</xsl:choose>
													</xsl:when>
												</xsl:choose>
											</xsl:for-each>
										</xsl:when>
									</xsl:choose>									
								</xsl:when>
							</xsl:choose>							
						</xsl:otherwise>						
					</xsl:choose>
				</xsl:when>
			</xsl:choose>	
		
		</xsl:for-each>
	</xsl:template>
		
	<xsl:template name="RULE_OFFER_F_2Group">
		<!--<xsl:message select="$ProductMappingOnlySecondBalanceIdentifier"/>
		<xsl:message select="$ProductMappingOnlySecondBalanceIdentifierFullList"/>
		<xsl:message select="$tokenProductMappingOnlySecondBalanceIdentifierFullList"/>-->
		<xsl:for-each select="/subscriber_xml/balancesdump_info/schemasubscriberbalancesdump_info">
			<xsl:variable name="Balance_Type_ID" select="BALANCE_TYPE"/>
			<xsl:variable name="First_Balance" select="BE_BUCKET_VALUE"/>
			<xsl:variable name="START_DATE" select="BE_BUCKET_START_DATE" /> 
			<xsl:variable name="EXPIRY_DATE" select="BE_EXPIRY" />				
			<xsl:for-each select="/subscriber_xml/balancesdump_info/schemasubscriberbalancesdump_info">
				<xsl:variable name="Second_Balance_Type_ID" select="BALANCE_TYPE"/>
				<xsl:variable name="Second_Balance" select="BE_BUCKET_VALUE"/>	
				<xsl:variable name="JointBalance" select="concat(';',$Balance_Type_ID,',',$Second_Balance_Type_ID,',;')"/>
<!--			<xsl:message select="$JointBalance"/>-->
				<xsl:choose>
					<xsl:when test="contains($ProductMappingOnlySecondBalanceIdentifier, $JointBalance)">
						<xsl:for-each select="$tokenProductMappingOnlySecondBalanceIdentifier2Group">							
							<xsl:variable name="tempBalanceID" select="tokenize(.,':')[1]"/>
							<xsl:variable name="tempID1" select="substring($tempBalanceID,0,string-length($tempBalanceID)-1)"/>
							<xsl:variable name="temp" select="replace($JointBalance,';','')"/>	
							<xsl:variable name="tempID2" select="substring($temp,0,string-length($temp)-1)"/>	
							<xsl:choose>
								<xsl:when test="$tempID1 eq $tempID2">
									<xsl:variable name="tempGroupIdentifier" select="tokenize(.,':')[2]"/>
									<xsl:variable name="tempBalanceValue" select="tokenize(.,':')[3]"/>
									<xsl:variable name="tempCount" select="tokenize(.,':')[4]"/>									
									<xsl:variable name="tempBalance1" select="tokenize($tempBalanceValue,',')[1]"/>
									<xsl:variable name="tempBalance2" select="tokenize($tempBalanceValue,',')[2]"/>
								
									<xsl:variable name="MappingBalance1" select="$docProduct_Mapping/PRODUCT_MAPPING_LIST/PRODUCT_MAPPING_INFO[BT_ID eq $Balance_Type_ID and BT_Group_Identifier eq $tempGroupIdentifier and Ignore_Flag eq 'N']/BT_Value"/>
									<xsl:variable name="MappingBalance2" select="$docProduct_Mapping/PRODUCT_MAPPING_LIST/PRODUCT_MAPPING_INFO[BT_ID eq $Second_Balance_Type_ID and BT_Group_Identifier eq $tempGroupIdentifier and Ignore_Flag eq 'N']/BT_Value"/>
									
									<xsl:variable name="MappingSymbol1" select="$docProduct_Mapping/PRODUCT_MAPPING_LIST/PRODUCT_MAPPING_INFO[BT_ID eq $Balance_Type_ID and BT_Group_Identifier eq $tempGroupIdentifier and Ignore_Flag eq 'N']/Symbols"/>
									<xsl:variable name="MappingSymbol2" select="$docProduct_Mapping/PRODUCT_MAPPING_LIST/PRODUCT_MAPPING_INFO[BT_ID eq $Second_Balance_Type_ID and BT_Group_Identifier eq $tempGroupIdentifier and Ignore_Flag eq 'N']/Symbols"/>
									<xsl:choose>
										<xsl:when test="$MappingSymbol1 eq '>' and string($MappingSymbol2) eq '='">
											<xsl:choose>
												<xsl:when test="$First_Balance > $tempBalance1 and $Second_Balance eq $tempBalance2">
													<xsl:variable name="Offer_ID" select="$docProduct_Mapping//PRODUCT_MAPPING_LIST/PRODUCT_MAPPING_INFO[BT_ID eq $Balance_Type_ID and BT_Group_Identifier eq $tempGroupIdentifier and Ignore_Flag eq 'N']/Offer_ID" />
													<xsl:variable name="Offer_Type" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[BT_ID eq $Balance_Type_ID and BT_Group_Identifier eq $tempGroupIdentifier and  Ignore_Flag eq 'N']/Offer_Type" />
													<!--<xsl:message select="$Balance_Type_ID"/>
													<xsl:message select="$First_Balance"/>													
													<xsl:message select="$Second_Balance_Type_ID"/>	
													<xsl:message select="$Second_Balance"/>
													<xsl:message select="$Offer_ID"/>
													<xsl:message select="'============'"/>									
													<xsl:message select="$tempBalance1"/>
													<xsl:message select="$tempBalance2"/>
													<xsl:message select="'*****************'"/>-->
													<xsl:variable name="Offer_ID" select="$docProduct_Mapping//PRODUCT_MAPPING_LIST/PRODUCT_MAPPING_INFO[BT_ID eq $Balance_Type_ID and BT_Group_Identifier eq $tempGroupIdentifier and Ignore_Flag eq 'N']/Offer_ID" />
													<xsl:variable name="Offer_Type" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[BT_ID eq $Balance_Type_ID and BT_Group_Identifier eq $tempGroupIdentifier and  Ignore_Flag eq 'N']/Offer_Type" />
													<xsl:choose>
														<xsl:when test="string-length($Offer_ID)">
															<xsl:call-template name="OFFER_XML_CREATION">
																<xsl:with-param name="Offer_ID" select="$Offer_ID"/>
																<xsl:with-param name="Offer_Type" select="$Offer_Type"/>
																<xsl:with-param name="START_DATE" select="$START_DATE"/>
																<xsl:with-param name="EXPIRY_DATE" select="$EXPIRY_DATE"/>
																<xsl:with-param name="AlwaysONNeverExpiry" select="''"/>
															</xsl:call-template>	
														</xsl:when>						
													</xsl:choose>
												</xsl:when>
												<!--<xsl:when test="$First_Balance = $tempBalance1 and $Second_Balance > $tempBalance2">
													<xsl:variable name="Offer_ID" select="$docProduct_Mapping//PRODUCT_MAPPING_LIST/PRODUCT_MAPPING_INFO[BT_ID eq $Balance_Type_ID and BT_Group_Identifier eq $tempGroupIdentifier and Ignore_Flag eq 'N']/Offer_ID" />
													<xsl:variable name="Offer_Type" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[BT_ID eq $Balance_Type_ID and BT_Group_Identifier eq $tempGroupIdentifier and  Ignore_Flag eq 'N']/Offer_Type" />
													<xsl:message select="$Balance_Type_ID"/>
													<xsl:message select="$Second_Balance_Type_ID"/>													
													<xsl:message select="$Offer_ID"/>
													<xsl:message select="$First_Balance"/>
													<xsl:message select="$tempBalance1"/>
													<xsl:message select="$Second_Balance"/>
													<xsl:message select="$tempBalance2"/>
													<xsl:message select="'-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-'"/>
													
													<xsl:choose>
														<xsl:when test="string-length($Offer_ID)">
															<xsl:call-template name="OFFER_XML_CREATION">
																<xsl:with-param name="Offer_ID" select="concat('kumar',$Offer_ID)"/>
																<xsl:with-param name="Offer_Type" select="$Offer_Type"/>
																<xsl:with-param name="START_DATE" select="$START_DATE"/>
																<xsl:with-param name="EXPIRY_DATE" select="$EXPIRY_DATE"/>
																<xsl:with-param name="AlwaysONNeverExpiry" select="''"/>
															</xsl:call-template>	
														</xsl:when>						
													</xsl:choose>
												</xsl:when>-->
											</xsl:choose>
										</xsl:when>
										
										<!--<xsl:when test="$MappingSymbol1 eq '=' and string($MappingSymbol2) eq '>'">
											<xsl:choose>
												<xsl:when test="$First_Balance = $tempBalance1 and $Second_Balance > $tempBalance2">
													<xsl:variable name="Offer_ID" select="$docProduct_Mapping//PRODUCT_MAPPING_LIST/PRODUCT_MAPPING_INFO[BT_ID eq $Balance_Type_ID and BT_Group_Identifier eq $tempGroupIdentifier and Ignore_Flag eq 'N']/Offer_ID" />
													<xsl:variable name="Offer_Type" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[BT_ID eq $Balance_Type_ID and BT_Group_Identifier eq $tempGroupIdentifier and  Ignore_Flag eq 'N']/Offer_Type" />
													
													<xsl:choose>
														<xsl:when test="string-length($Offer_ID)">
															<xsl:call-template name="OFFER_XML_CREATION">
																<xsl:with-param name="Offer_ID" select="concat('singh',$Offer_ID)"/>
																<xsl:with-param name="Offer_Type" select="$Offer_Type"/>
																<xsl:with-param name="START_DATE" select="$START_DATE"/>
																<xsl:with-param name="EXPIRY_DATE" select="$EXPIRY_DATE"/>
																<xsl:with-param name="AlwaysONNeverExpiry" select="''"/>
															</xsl:call-template>	
														</xsl:when>						
													</xsl:choose>
												</xsl:when>
												<xsl:when test="$First_Balance > $tempBalance1 and $Second_Balance = $tempBalance2">
													<xsl:variable name="Offer_ID" select="$docProduct_Mapping//PRODUCT_MAPPING_LIST/PRODUCT_MAPPING_INFO[BT_ID eq $Balance_Type_ID and BT_Group_Identifier eq $tempGroupIdentifier and Ignore_Flag eq 'N']/Offer_ID" />
													<xsl:variable name="Offer_Type" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[BT_ID eq $Balance_Type_ID and BT_Group_Identifier eq $tempGroupIdentifier and  Ignore_Flag eq 'N']/Offer_Type" />
													<xsl:choose>
														<xsl:when test="string-length($Offer_ID)">
															<xsl:call-template name="OFFER_XML_CREATION">
																<xsl:with-param name="Offer_ID" select="concat('vipinsingh',$Offer_ID)"/>
																<xsl:with-param name="Offer_Type" select="$Offer_Type"/>
																<xsl:with-param name="START_DATE" select="$START_DATE"/>
																<xsl:with-param name="EXPIRY_DATE" select="$EXPIRY_DATE"/>
																<xsl:with-param name="AlwaysONNeverExpiry" select="''"/>
															</xsl:call-template>	
														</xsl:when>						
													</xsl:choose>
												</xsl:when>
											</xsl:choose>
										</xsl:when>-->
									</xsl:choose>
								</xsl:when>
							</xsl:choose>
						</xsl:for-each>
					</xsl:when>
				</xsl:choose>
			</xsl:for-each>
		</xsl:for-each>
		
	</xsl:template>
	
	<xsl:template name="RULE_OFFER_F_3Group">
<!--		<xsl:message select="$tokenProductMappingOnlySecondBalanceIdentifier3Group"/>-->
		<xsl:for-each select="/subscriber_xml/balancesdump_info/schemasubscriberbalancesdump_info">
			<xsl:variable name="First_Balance_ID" select="BALANCE_TYPE"/>
			<xsl:variable name="First_Balance" select="BE_BUCKET_VALUE"/>
			<xsl:variable name="START_DATE" select="BE_BUCKET_START_DATE" /> 
			<xsl:variable name="EXPIRY_DATE" select="BE_EXPIRY" />				
			<xsl:for-each select="/subscriber_xml/balancesdump_info/schemasubscriberbalancesdump_info">
				<xsl:variable name="Second_Balance_ID" select="BALANCE_TYPE"/>
				<xsl:variable name="Second_Balance" select="BE_BUCKET_VALUE"/>	
				<xsl:for-each select="/subscriber_xml/balancesdump_info/schemasubscriberbalancesdump_info">
					<xsl:variable name="Third_Balance_ID" select="BALANCE_TYPE"/>
					<xsl:variable name="Third_Balance" select="BE_BUCKET_VALUE"/>	
					<xsl:variable name="JointBalance" select="concat(';',$First_Balance_ID,',',$Second_Balance_ID,',',$Third_Balance_ID,',;')"/>
					<xsl:choose>
						<xsl:when test="contains($ProductMappingOnlySecondBalanceIdentifier, $JointBalance)">
							<xsl:for-each select="$tokenProductMappingOnlySecondBalanceIdentifier3Group">							
								<xsl:variable name="tempBalanceID" select="tokenize(.,':')[1]"/>
								<xsl:variable name="tempID1" select="substring($tempBalanceID,0,string-length($tempBalanceID)-1)"/>
								<xsl:variable name="temp" select="replace($JointBalance,';','')"/>	
								<xsl:variable name="tempID2" select="substring($temp,0,string-length($temp)-1)"/>
								<xsl:choose>
									<xsl:when test="$tempID1 eq $tempID2">
										<xsl:variable name="tempGroupIdentifier" select="tokenize(.,':')[2]"/>
										<xsl:variable name="tempBalanceValue" select="tokenize(.,':')[3]"/>
										<xsl:variable name="tempCount" select="tokenize(.,':')[4]"/>									
										<xsl:variable name="tempBalance1" select="tokenize($tempBalanceValue,',')[1]"/>
										<xsl:variable name="tempBalance2" select="tokenize($tempBalanceValue,',')[2]"/>
										<xsl:variable name="tempBalance3" select="tokenize($tempBalanceValue,',')[3]"/>
									
										<xsl:variable name="MappingBalance1" select="tokenize($docProduct_Mapping/PRODUCT_MAPPING_LIST/PRODUCT_MAPPING_INFO[BT_ID eq $First_Balance_ID and BT_Group_Identifier eq $tempGroupIdentifier and Ignore_Flag eq 'N']/BT_Value ,',')"/>
										<xsl:variable name="MappingBalance2" select="tokenize($docProduct_Mapping/PRODUCT_MAPPING_LIST/PRODUCT_MAPPING_INFO[BT_ID eq $Second_Balance_ID and BT_Group_Identifier eq $tempGroupIdentifier and Ignore_Flag eq 'N']/BT_Value,',')"/>
										<xsl:variable name="MappingBalance3" select="tokenize($docProduct_Mapping/PRODUCT_MAPPING_LIST/PRODUCT_MAPPING_INFO[BT_ID eq $Third_Balance_ID and BT_Group_Identifier eq $tempGroupIdentifier and Ignore_Flag eq 'N']/BT_Value,',')"/>
										
										<xsl:variable name="MappingSymbol1" select="$docProduct_Mapping/PRODUCT_MAPPING_LIST/PRODUCT_MAPPING_INFO[BT_ID eq $First_Balance_ID and BT_Group_Identifier eq $tempGroupIdentifier and Ignore_Flag eq 'N']/Symbols"/>
										<xsl:variable name="MappingSymbol2" select="$docProduct_Mapping/PRODUCT_MAPPING_LIST/PRODUCT_MAPPING_INFO[BT_ID eq $Second_Balance_ID and BT_Group_Identifier eq $tempGroupIdentifier and Ignore_Flag eq 'N']/Symbols"/>
										<xsl:variable name="MappingSymbol3" select="$docProduct_Mapping/PRODUCT_MAPPING_LIST/PRODUCT_MAPPING_INFO[BT_ID eq $Third_Balance_ID and BT_Group_Identifier eq $tempGroupIdentifier and Ignore_Flag eq 'N']/Symbols"/>
									<!--<xsl:message select="$First_Balance"/>
										<xsl:message select="$Second_Balance"/>
										<xsl:message select="$Third_Balance"/>
										
										<xsl:message select="$MappingBalance1"/>
										<xsl:message select="$MappingBalance2"/>
										<xsl:message select="$MappingBalance3"/>-->
										<xsl:for-each select="$MappingBalance1">
											<xsl:variable name="Bal1" select="."/>
											<xsl:for-each select="$MappingBalance2">
												<xsl:variable name="Bal2" select="."/>
												<xsl:for-each select="$MappingBalance3">
													<xsl:variable name="Bal3" select="."/>
													<xsl:choose>
														<xsl:when test="$First_Balance eq normalize-space($Bal1) and $Second_Balance eq normalize-space($Bal2) and $Third_Balance eq normalize-space($Bal3)">
															<xsl:variable name="Offer_ID" select="$docProduct_Mapping//PRODUCT_MAPPING_LIST/PRODUCT_MAPPING_INFO[BT_ID eq $First_Balance_ID and BT_Group_Identifier eq $tempGroupIdentifier and Ignore_Flag eq 'N']/Offer_ID" />
															<xsl:variable name="Offer_Type" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[BT_ID eq $First_Balance_ID and BT_Group_Identifier eq $tempGroupIdentifier and  Ignore_Flag eq 'N']/Offer_Type" />
															<xsl:choose>
																<xsl:when test="string-length($Offer_ID)">
																	<xsl:call-template name="OFFER_XML_CREATION">
																		<xsl:with-param name="Offer_ID" select="$Offer_ID"/>
																		<xsl:with-param name="Offer_Type" select="$Offer_Type"/>
																		<xsl:with-param name="START_DATE" select="$START_DATE"/>
																		<xsl:with-param name="EXPIRY_DATE" select="$EXPIRY_DATE"/>
																		<xsl:with-param name="AlwaysONNeverExpiry" select="''"/>
																	</xsl:call-template>	
																</xsl:when>						
															</xsl:choose>
															
															
														</xsl:when>
													</xsl:choose>
												</xsl:for-each>
											</xsl:for-each>
										</xsl:for-each>
										
									</xsl:when>
								</xsl:choose>
							</xsl:for-each>
						</xsl:when>
					</xsl:choose>
					
				</xsl:for-each>
			</xsl:for-each>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template name="OFFER_XML_CREATION">
		<xsl:param name="Offer_ID"/>
		<xsl:param name="Offer_Type"/>
		<xsl:param name="START_DATE"/>
		<xsl:param name="EXPIRY_DATE"/>
		<xsl:param name="AlwaysONNeverExpiry"/>		
		<OFFER>
			<offer_id>
				<xsl:value-of select="$Offer_ID"/>
			</offer_id>
			<start_date>
				<xsl:choose>
					<xsl:when test="string-length($START_DATE) and $START_DATE ne '1970-01-01 00:00:00'">
						<xsl:choose>
							<xsl:when test="upper-case($Offer_Type) eq 'TIMER'">
								<xsl:value-of select="dm:ConvertBinaryToDecimal(dm:getMSBFromBinary(dm:decimalToBinary(dm:dateTimeToEPOCHTimeStamp(dm:convertTELDateTimeToXSLTDate($START_DATE)))),0)" />
							</xsl:when>
							<xsl:when test="upper-case($Offer_Type) eq 'ACCOUNT'">
								<xsl:value-of select="dm:dateTimeToEPOCHTimeStamp($START_DATE)" />
							</xsl:when>
						</xsl:choose>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$default_EMPTY"/>
					</xsl:otherwise>
				</xsl:choose>
			</start_date>
			<expiry_date>
				<xsl:choose>
					<xsl:when test="string-length($EXPIRY_DATE) and $EXPIRY_DATE ne '1970-01-01 00:00:00'">
						<xsl:choose>
							<xsl:when test="upper-case($Offer_Type) eq 'TIMER'">
								<!--<xsl:message select="dm:dateTimeToEPOCHTimeStampExpirtyTime(dm:convertTELDateTimeToXSLTDate($EXPIRY_DATE))"/>
									<xsl:message select="dm:convertTELDateTimeToXSLTDate($EXPIRY_DATE)"/>
									<xsl:message select="dm:decimalToBinary(dm:dateTimeToEPOCHTimeStampExpirtyTime(dm:convertTELDateTimeToXSLTDate($EXPIRY_DATE)))"></xsl:message>
									<xsl:message select="dm:getMSBFromBinary(dm:decimalToBinary(dm:dateTimeToEPOCHTimeStampExpirtyTime(dm:convertTELDateTimeToXSLTDate($EXPIRY_DATE))))"></xsl:message>
									<xsl:message select="dm:ConvertBinaryToDecimal(dm:getMSBFromBinary(dm:decimalToBinary(dm:dateTimeToEPOCHTimeStampExpirtyTime(dm:convertTELDateTimeToXSLTDate($EXPIRY_DATE)))),0)"></xsl:message>
									-->											
								<xsl:value-of select="dm:ConvertBinaryToDecimal(dm:getMSBFromBinary(dm:decimalToBinary(dm:dateTimeToEPOCHTimeStampExpirtyTime(dm:convertTELDateTimeToXSLTDate($EXPIRY_DATE)))),0)" />
							</xsl:when>
							<xsl:when test="upper-case($Offer_Type) eq 'ACCOUNT'">
								<xsl:value-of select="dm:dateTimeToEPOCHTimeStampExpirtyTime($EXPIRY_DATE)" />
							</xsl:when>
						</xsl:choose>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$default_EMPTY"/>
					</xsl:otherwise>
				</xsl:choose>							
			</expiry_date>
			<start_seconds>
				<xsl:choose>
					<xsl:when test="string-length($START_DATE) and $START_DATE ne '1970-01-01 00:00:00' ">
						<xsl:choose>
							<xsl:when test="upper-case($Offer_Type) eq 'TIMER'">
								<xsl:value-of select="dm:binaryToOfferStartSeconds(dm:getLSBFromBinary(dm:decimalToBinary(dm:dateTimeToEPOCHTimeStamp(dm:convertTELDateTimeToXSLTDate($START_DATE)))))" />
							</xsl:when>
							<xsl:when test="upper-case($Offer_Type) eq 'ACCOUNT'">
								<xsl:value-of select="$default_NULL" />
							</xsl:when>
						</xsl:choose>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$default_EMPTY"/>
					</xsl:otherwise>
				</xsl:choose>							
			</start_seconds>
			<expiry_seconds>
				<xsl:choose>
					<xsl:when test="string-length($EXPIRY_DATE) and $EXPIRY_DATE ne '1970-01-01 00:00:00'">
						<xsl:choose>
							<xsl:when test="upper-case($Offer_Type) eq 'TIMER'">
								<xsl:value-of select="dm:binaryToOfferExpirySeconds(dm:getLSBFromBinary(dm:decimalToBinary(dm:dateTimeToEPOCHTimeStampExpirtyTime(dm:convertTELDateTimeToXSLTDate($EXPIRY_DATE)))))" />
							</xsl:when>
							<xsl:when test="upper-case($Offer_Type) eq 'ACCOUNT'">
								<xsl:value-of select="$default_NULL" />
							</xsl:when>
						</xsl:choose>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$default_EMPTY"/>
					</xsl:otherwise>
				</xsl:choose>							
			</expiry_seconds>
			<flags>
				<xsl:value-of select="$default_NULL"/>
			</flags>
			<pam_service_id>
				<xsl:value-of select="$default_NULL"/>
			</pam_service_id>
			<product_id>
				<xsl:choose>
					<xsl:when test="count($ProductPrivateForDAList)">
						<xsl:for-each select="$ProductPrivateForDAList">
							<xsl:choose>
								<xsl:when test="string-length(.)">
									<xsl:variable name="OFFER_VALUE" select="tokenize(.,',')[1]"/>
									<xsl:variable name="OFFER_PRODUCT" select="tokenize(.,',')[2]"/>
									<xsl:choose>
										<xsl:when test="number($Offer_ID) eq number($OFFER_VALUE)">
											<xsl:value-of select="$OFFER_PRODUCT"/>
										</xsl:when>
										<!--<xsl:otherwise>
											<xsl:value-of select="$default_ZERO"/>
										</xsl:otherwise>-->
									</xsl:choose>
								</xsl:when>
							</xsl:choose>																
						</xsl:for-each>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$default_ZERO"/>
					</xsl:otherwise>
				</xsl:choose>
			</product_id>
		</OFFER>
	</xsl:template>
	
	<!---=================================================-->
			<!--Offer_Attribute.csv-->
	<!---=================================================-->
	<xsl:template name="generate_OfferAttributes.csv">
		<OFFER_ATTRIBUTE_LIST>
			<Account_MSISDN>
				<xsl:value-of select="$MSISDN"/>
			</Account_MSISDN>			
			
			<!--OfferAttr from Default table -->
			<xsl:call-template name="RULE_OFFER_ATT_A"/>
		</OFFER_ATTRIBUTE_LIST>
	</xsl:template>
	
	<xsl:template name="RULE_OFFER_ATT_A">
		<xsl:for-each select="$docDefaultServices/DEFAULTSERVICES_MAPPING_LIST/DEFAULTSERVICES_MAPPING_INFO">				
			<xsl:choose>
				<xsl:when test="(string-length(Attr_Offer_Id) gt 0) and (Ignore_Flag eq 'N')">					
					<xsl:variable name="AttrOffer_Id" select="Attr_Offer_Id"/>
					<xsl:variable name="Attr_Name" select="Attr_Name"/>
					<xsl:variable name="Attr_Type" select="Attr_Type"/>
					<xsl:variable name="Attr_Source" select="Attr_Source"/>
					<OFFER_ATTRIBUTE>
						<offer_id>
							<xsl:value-of select="$AttrOffer_Id"/>
						</offer_id>
						<attribute_def_id>
							<xsl:value-of select="$docOffer_Attribute_Defination/OFFER_ATTRIBUTE_DEFINITION_LIST/OFFER_ATTRIBUTE_DEFINITION_INFO[OFFER_DEFINITION_ID eq $AttrOffer_Id and ATTRIBUTE_DEF_NAME eq $Attr_Name]/ATTRIBUTE_DEF_ID"/>
						</attribute_def_id>
						<value>
							<xsl:choose>
								<xsl:when test="Is_Attr_Default eq 'Y'">
									
								</xsl:when>
								<xsl:otherwise>
									<!--<xsl:message select="$IMSI"/>
									<xsl:message select="$IMEI"/>
									<xsl:message select="$HLRADDR"/>-->
									<xsl:choose>
										<xsl:when test="upper-case($Attr_Name) eq 'IMEI'">
											<xsl:choose>
												<xsl:when test="number($Attr_Type) eq 4">													
													<xsl:call-template name="RULE_OFFER_VAL_4">
														<xsl:with-param name="String" select="string($IMEI)"/>
													</xsl:call-template>
												</xsl:when>
											</xsl:choose>
										</xsl:when>
										<xsl:when test="upper-case($Attr_Name) eq 'IMSI'">
											<xsl:choose>
												<xsl:when test="number($Attr_Type) eq 4">
													<xsl:call-template name="RULE_OFFER_VAL_4">
														<xsl:with-param name="String" select="$IMSI"/>
													</xsl:call-template>
												</xsl:when>
											</xsl:choose>
										</xsl:when>
										<xsl:when test="upper-case($Attr_Name) eq 'HLRADDRESS'">
											<xsl:choose>
												<xsl:when test="number($Attr_Type) eq 4">
													<xsl:call-template name="RULE_OFFER_VAL_4">
														<xsl:with-param name="String" select="$HLRADDR"/>
													</xsl:call-template>
												</xsl:when>
											</xsl:choose>
										</xsl:when>
									</xsl:choose>
								</xsl:otherwise>
							</xsl:choose>
						</value>
						<product_id>
							<xsl:value-of select="$default_ZERO"/>
						</product_id>
					</OFFER_ATTRIBUTE>								
				</xsl:when>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>
	
	<!---=================================================-->
			<!--Accumulator.csv-->
	<!---=================================================-->
	
	<xsl:template name="generate_Accumulator.csv">
		<ACCUMLATOR_ACCOUNT_LIST>
			<Account_MSISDN>
				<xsl:value-of select="$MSISDN"/>
			</Account_MSISDN>			
			<!--For balance Bucket -->
			<xsl:call-template name="RULE_ACM_A"/>

		</ACCUMLATOR_ACCOUNT_LIST>		
	</xsl:template>
	
	<xsl:template name="RULE_ACM_A">
		<!--<xsl:message select="$BalanceACCEmptyBucketValueNoSC"/>
		<xsl:message select="$BalanceACCEmptyBucketValueYesSC"/>-->
		<xsl:for-each select="/subscriber_xml/balancesdump_info/schemasubscriberbalancesdump_info">
			<!--<xsl:variable name="Balance_Type_ID" select="BALANCE_TYPE"/>
			<xsl:variable name="BE_BUCKET_VALUE" select="BE_BUCKET_VALUE"/>-->
			<xsl:variable name="Balance_Type_ID" select="BALANCE_TYPE"/>
			<xsl:variable name="DA_NAME" select="BALANCE_TYPE_NAME"/>
			<xsl:variable name="BALANCE" select="BE_BUCKET_VALUE" /> 
			<xsl:variable name="START_DATE" select="BE_BUCKET_START_DATE" /> 
			<xsl:variable name="EXPIRY_DATE" select="BE_EXPIRY" />
			<xsl:choose>
				<xsl:when test="contains($BalanceACCEmptyBucketValueNoSC,concat($Balance_Type_ID,'|','','|', $CCS_ACCT_TYPE_ID,';'))">
					<xsl:variable name="AC_ID" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and CCS_ACCT_TYPE_ID eq $CCS_ACCT_TYPE_ID and Ignore_Flag eq 'N']/UA_ID" />
					<xsl:variable name="AC_Type" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and CCS_ACCT_TYPE_ID eq $CCS_ACCT_TYPE_ID and Ignore_Flag eq 'N']/UA_Type" />
					<xsl:variable name="AC_Value" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and CCS_ACCT_TYPE_ID eq $CCS_ACCT_TYPE_ID and Ignore_Flag eq 'N']/UA_Value" />
					<xsl:choose>
						<xsl:when test="string-length($AC_ID)">
							<ACCUMLATOR_ACCOUNT>
								<id>
									<xsl:value-of select="$AC_ID"/>
								</id>
								<balance>	
									<xsl:choose>
										<xsl:when test="$AC_Value eq 'INFILE_Subscriber_Balances.csv.BE_BUCKET_VALUE'">
											<xsl:value-of select="$BALANCE"/>
										</xsl:when>
									</xsl:choose>
								</balance>
								<clearing_date>	
									<xsl:value-of select="$docCommonConfigPath/COMMON_CONFIG_MAPPING_LIST/COMMON_CONFIG_MAPPING_INFO[Element = 'default_last_reset_date']/Value"/>
								</clearing_date>
							</ACCUMLATOR_ACCOUNT>
						</xsl:when>
					</xsl:choose>
				</xsl:when>
			</xsl:choose>
			
			<xsl:choose>
				<xsl:when test="contains($BalanceACCEmptyBucketValueYesSC,concat($Balance_Type_ID,'|','','|', 0,';'))">
					<xsl:variable name="AC_ID" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and number(CCS_ACCT_TYPE_ID) eq 0 and Ignore_Flag eq 'N']/UA_ID" />
					<xsl:variable name="AC_Type" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and number(CCS_ACCT_TYPE_ID) eq 0 and Ignore_Flag eq 'N']/UA_Type" />
					<xsl:variable name="AC_Value" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and number(CCS_ACCT_TYPE_ID) eq 0 and Ignore_Flag eq 'N']/UA_Value" />
					<xsl:choose>
						<xsl:when test="string-length($AC_ID)">
							<ACCUMLATOR_ACCOUNT>
								<id>
									<xsl:value-of select="$AC_ID"/>
								</id>
								<balance>	
									<xsl:choose>
										<xsl:when test="$AC_Value eq 'INFILE_Subscriber_Balances.csv.BE_BUCKET_VALUE'">
											<xsl:value-of select="$BALANCE"/>
										</xsl:when>
									</xsl:choose>
								</balance>
								<clearing_date>	
									<xsl:value-of select="$docCommonConfigPath/COMMON_CONFIG_MAPPING_LIST/COMMON_CONFIG_MAPPING_INFO[Element = 'default_last_reset_date']/Value"/>
								</clearing_date>
							</ACCUMLATOR_ACCOUNT>
						</xsl:when>
					</xsl:choose>
				</xsl:when>
			</xsl:choose>
		</xsl:for-each>		
	</xsl:template>
	
	<!---=================================================-->
		<!--Calling Card.csv-->
	<!---=================================================-->
	
	

	<!---=================================================-->
			<!--Usage Counter.csv-->
	<!---=================================================-->
	
	
	<xsl:template name="generate_UsageCounter.csv">
		<xsl:variable name="UC_OUTPUT">
			<xsl:call-template name="generate_UsageCounter"/>
		</xsl:variable>		
		<USAGE_COUNTER_LIST>
			<xsl:call-template name="Transform_UsageCounter_CSV">
				<xsl:with-param name="USAGE_RECORD" select="$UC_OUTPUT"/>
			</xsl:call-template>
		</USAGE_COUNTER_LIST>		
	</xsl:template>
	
	<xsl:template name="Transform_UsageCounter_CSV" >
		<xsl:param name="USAGE_RECORD"/>		
		<xsl:for-each-group select="$USAGE_RECORD/UC_LIST/USAGE_COUNTER" group-by="usage_counter_id">			
			<xsl:variable name="MSISDN" select="Account_ID"/>	
			<xsl:variable name="product_ID" select="product_id"/>
			<xsl:variable name="UC_ID" select="usage_counter_id"/>
			<xsl:variable name="Final_Val" select="sum(current-group()/number(value))"/>
			<xsl:variable name="Final_Value">
				<xsl:choose>
					<xsl:when test="number($Final_Val) lt 0">
						<xsl:value-of select="'0'"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$Final_Val"/>						
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			
			<xsl:variable name="Final_NomVal" select="sum(current-group()/number(nominal_value))"/>
			<xsl:variable name="Final_NominalValue">
				<xsl:choose>
					<xsl:when test="number($Final_NomVal) lt 0">
						<xsl:value-of select="'0'"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$Final_NomVal"/>						
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			
			<USAGE_COUNTER>
				<Account_ID>
					<xsl:value-of select="$MSISDN"/>
				</Account_ID>
				<usage_counter_id>
					<xsl:value-of select="$UC_ID"/>
				</usage_counter_id>
				<associated_id>
					<xsl:value-of select="$MSISDN"/>
				</associated_id>
				<value>								
					<xsl:value-of select="format-number( $Final_Value, '#0')"/>
				</value>
				<product_id>						
					<xsl:value-of select="$product_ID"/>
				</product_id>	
				<nominal_value>
					<!--<xsl:value-of select="format-number( $Final_NominalValue, '#0')"/>-->
					<xsl:value-of select="format-number( $Final_Value, '#0')"/>
				</nominal_value>
				<value_decimals>
					<xsl:value-of select="$DEFAULT_VALUE_ZERO"/>
				</value_decimals>
			</USAGE_COUNTER>
		</xsl:for-each-group>
	</xsl:template>
	
	<xsl:template name="generate_UsageCounter">
		<UC_LIST>	
			<!-- From Balance Mapping Sheet -->
			<xsl:call-template name="RULE_UC_A"/>	
			<!-- From product Mapping Sheet -->
			<xsl:call-template name="RULE_UC_B"/>	
		</UC_LIST>
	</xsl:template>
	
	<xsl:template name="RULE_UC_A">
		<!--<xsl:message select="$BalanceUCEmptyBucketValueNoSC"/>
		<xsl:message select="$BalanceUCEmptyBucketValueYesSC"/>
		<xsl:message select="$BalanceUCAvailableBucketValueYesSC"/>-->
		<xsl:for-each select="/subscriber_xml/balancesdump_info/schemasubscriberbalancesdump_info">
			<xsl:variable name="Balance_Type_ID" select="BALANCE_TYPE"/>
			<xsl:variable name="BALANCE_MSISDN" select="MSISDN"/>
			<xsl:variable name="BALANCE" select="BE_BUCKET_VALUE" /> 
			<xsl:variable name="START_DATE" select="BE_BUCKET_START_DATE" /> 
			<xsl:variable name="EXPIRY_DATE" select="BE_EXPIRY" />
			<xsl:choose>
				<xsl:when test="contains($BalanceUCEmptyBucketValueNoSC,concat($Balance_Type_ID,'|','','|', $CCS_ACCT_TYPE_ID,';'))">
					<xsl:variable name="UC_ID" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and CCS_ACCT_TYPE_ID eq $CCS_ACCT_TYPE_ID and Ignore_Flag eq 'N']/UC_ID" /> 
					<xsl:variable name="Product_Private" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and CCS_ACCT_TYPE_ID eq $CCS_ACCT_TYPE_ID and Ignore_Flag eq 'N']/Product_Private" /> 			
					
					<!--<xsl:message select="'vipin'"/>
					<xsl:message select="$Balance_Type_ID"/>
					<xsl:message select="$UC_ID"/>-->
					<xsl:choose>
						<xsl:when test="string-length($UC_ID)">
							<xsl:variable name="Resource" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and CCS_ACCT_TYPE_ID eq $CCS_ACCT_TYPE_ID and Ignore_Flag eq 'N']/Resource"/>
							<xsl:variable name="Factor" select="$docConversionLogicMapping/CONVERSION_LOGIC_MAPPING_LIST/CONVERSION_LOGIC_MAPPING_INFO[Resource eq $Resource]/Type"/>
							<xsl:variable name="UT_Value" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and CCS_ACCT_TYPE_ID eq $CCS_ACCT_TYPE_ID and Ignore_Flag eq 'N']/UT_Value"/>
							<xsl:call-template name="UC_XML_CREATION">
								<xsl:with-param name="UC_ID" select="$UC_ID"/>
								<xsl:with-param name="BALANCE_MSISDN" select="$BALANCE_MSISDN"/>
								<xsl:with-param name="UT_Value" select="$UT_Value"/>
								<xsl:with-param name="BALANCE" select="$BALANCE"/>
								<xsl:with-param name="Factor" select="$Factor"/>
								<xsl:with-param name="Resource" select="$Resource"/>								
							</xsl:call-template>
						</xsl:when>
					</xsl:choose>
				</xsl:when>
			</xsl:choose>
			
			<xsl:choose>
				<xsl:when test="contains($BalanceUCEmptyBucketValueYesSC,concat($Balance_Type_ID,'|','','|', 0,';'))">
					<xsl:variable name="UC_ID" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and number(CCS_ACCT_TYPE_ID) eq 0 and Ignore_Flag eq 'N']/UC_ID" /> 
					<xsl:variable name="Product_Private" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and number(CCS_ACCT_TYPE_ID) eq 0 and Ignore_Flag eq 'N']/Product_Private" /> 			
					
					<!--<xsl:message select="'kumar'"/>
					<xsl:message select="$Balance_Type_ID"/>
					<xsl:message select="$UC_ID"/>-->
					<xsl:choose>
						<xsl:when test="string-length($UC_ID)">
							<xsl:variable name="Resource" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and number(CCS_ACCT_TYPE_ID) eq 0 and Ignore_Flag eq 'N']/Resource"/>
							<xsl:variable name="Factor" select="$docConversionLogicMapping/CONVERSION_LOGIC_MAPPING_LIST/CONVERSION_LOGIC_MAPPING_INFO[Resource eq $Resource]/Type"/>
							<xsl:variable name="UT_Value" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and number(CCS_ACCT_TYPE_ID) eq 0 and Ignore_Flag eq 'N']/UT_Value"/>
							
							<xsl:call-template name="UC_XML_CREATION">
								<xsl:with-param name="UC_ID" select="$UC_ID"/>
								<xsl:with-param name="BALANCE_MSISDN" select="$BALANCE_MSISDN"/>
								<xsl:with-param name="UT_Value" select="$UT_Value"/>
								<xsl:with-param name="BALANCE" select="$BALANCE"/>
								<xsl:with-param name="Factor" select="$Factor"/>
								<xsl:with-param name="Resource" select="$Resource"/>								
							</xsl:call-template>
						</xsl:when>
					</xsl:choose>
				</xsl:when>
			</xsl:choose>
			
			<xsl:choose>
				<xsl:when test="contains($BalanceUCAvailableBucketValueYesSC,concat($Balance_Type_ID,'|',$BALANCE,'|', 0,';'))">
					<xsl:variable name="UC_ID" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and number(CCS_ACCT_TYPE_ID) eq 0 and Ignore_Flag eq 'N']/UC_ID" /> 
					<xsl:variable name="Product_Private" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and number(CCS_ACCT_TYPE_ID) eq 0 and Ignore_Flag eq 'N']/Product_Private" /> 			
					
					<!--<xsl:message select="'singh'"/>
					<xsl:message select="$Balance_Type_ID"/>
					<xsl:message select="$UC_ID"/>-->
					<xsl:choose>
						<xsl:when test="string-length($UC_ID)">
							<xsl:variable name="Resource" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and number(CCS_ACCT_TYPE_ID) eq 0 and Ignore_Flag eq 'N']/Resource"/>
							<xsl:variable name="Factor" select="$docConversionLogicMapping/CONVERSION_LOGIC_MAPPING_LIST/CONVERSION_LOGIC_MAPPING_INFO[Resource eq $Resource]/Type"/>
							<xsl:variable name="UT_Value" select="$docBalanceMapping/BALANCE_MAPPING_LIST/BALANCE_MAPPING_INFO[Balance_Type_ID eq $Balance_Type_ID and number(CCS_ACCT_TYPE_ID) eq 0 and Ignore_Flag eq 'N']/UT_Value"/>
							<xsl:call-template name="UC_XML_CREATION">
								<xsl:with-param name="UC_ID" select="$UC_ID"/>
								<xsl:with-param name="BALANCE_MSISDN" select="$BALANCE_MSISDN"/>
								<xsl:with-param name="UT_Value" select="$UT_Value"/>
								<xsl:with-param name="BALANCE" select="$BALANCE"/>
								<xsl:with-param name="Factor" select="$Factor"/>
								<xsl:with-param name="Resource" select="$Resource"/>								
							</xsl:call-template>
						</xsl:when>
					</xsl:choose>
				</xsl:when>
			</xsl:choose>
						
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template name="RULE_UC_B">
		<!--<xsl:message select="$ProductMappingOnlyBalanceIDValue"/>
		<xsl:message select="$ProductMappingOnlyBalanceIDValueSecondBalance"/>
		<xsl:message select="$ProductMappingSecondBalance"/>
		<xsl:message select="$ProductMappingOnlySecondBalanceEmptyIdentifier"/>-->

		<xsl:for-each select="/subscriber_xml/balancesdump_info/schemasubscriberbalancesdump_info">
			<xsl:variable name="Balance_Type_ID" select="BALANCE_TYPE"/>
			<xsl:variable name="First_Balance" select="BE_BUCKET_VALUE" /> 
			<xsl:variable name="BALANCE_MSISDN" select="MSISDN" /> 
			<xsl:variable name="EXPIRY_DATE" select="BE_EXPIRY" />		
			<xsl:for-each select="/subscriber_xml/balancesdump_info/schemasubscriberbalancesdump_info">
				<xsl:variable name="SecondBalance_ID" select="BALANCE_TYPE"/>
				<xsl:variable name="SecondBalance" select="BE_BUCKET_VALUE"/>
				<xsl:variable name="UC_ID" select="$docProduct_Mapping/PRODUCT_MAPPING_LIST/PRODUCT_MAPPING_INFO[BT_PC_ID eq $Balance_Type_ID and PC_BT_Value eq $First_Balance and BT_ID eq $SecondBalance_ID  and Ignore_Flag eq 'N']/UC_ID" />
				<xsl:variable name="UT_Value" select="$docProduct_Mapping/PRODUCT_MAPPING_LIST/PRODUCT_MAPPING_INFO[BT_PC_ID eq $Balance_Type_ID and PC_BT_Value eq $First_Balance and BT_ID eq $SecondBalance_ID  and Ignore_Flag eq 'N']/UT_Value"/>
				
				<xsl:choose>
					<xsl:when test="string-length($UC_ID)">
						<!--<xsl:message select="$UC_ID"/>
						<xsl:message select="$Balance_Type_ID"/>
						<xsl:message select="$SecondBalance_ID"/>
						<xsl:message select="$First_Balance"/>
						<xsl:message select="$SecondBalance"/>
						<xsl:message select="$UT_Value"/>
						<xsl:message select="format-number(number($UT_Value) -number($SecondBalance),'#0')"/>
						<xsl:message select="'-\-\-\-\-\-\-\-\-\-\-'"/>-->
						<USAGE_COUNTER>
							<Account_ID>
								<xsl:value-of select="$BALANCE_MSISDN"/>
							</Account_ID>
							<usage_counter_id>
								<xsl:value-of select="$UC_ID"/>
							</usage_counter_id>
							<associated_party_id>
								<xsl:value-of select="$MSISDN"/>
							</associated_party_id>
							<value>								
								<xsl:value-of select="number($UT_Value) -number($SecondBalance)"/>
							</value>
							<product_id>
								<xsl:choose>
									<xsl:when test="count($ProductPrivateForUCList)">
										<xsl:for-each select="$ProductPrivateForUCList">
											<xsl:choose>
												<xsl:when test="string-length(.)">
													<xsl:variable name="UC_VALUE" select="tokenize(.,',')[1]"/>
													<xsl:variable name="UC_PRODUCT" select="tokenize(.,',')[2]"/>
													<xsl:choose>
														<xsl:when test="$UC_ID eq $UC_VALUE">
															<xsl:value-of select="$UC_PRODUCT"/>
														</xsl:when>																		
													</xsl:choose>
												</xsl:when>
											</xsl:choose>																
										</xsl:for-each>
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="$default_ZERO"/>
									</xsl:otherwise>
								</xsl:choose>
							</product_id>			
							<nominal_value>
								<xsl:value-of select="number($UT_Value) - number($SecondBalance)"/>
							</nominal_value>
							<value_decimals>
								<xsl:value-of select="$default_ZERO"/>
							</value_decimals>
						</USAGE_COUNTER>
					</xsl:when>
				</xsl:choose>	
			</xsl:for-each>	
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template name="UC_XML_CREATION">
		<xsl:param name="UC_ID"/>
		<xsl:param name="BALANCE_MSISDN"/>
		<xsl:param name="UT_Value"/>
		<xsl:param name="BALANCE"/>
		<xsl:param name="Factor"/>
		<xsl:param name="Resource"/>
		
		<!--TOUC1	VOLUME	UT_VALUE - (X*1024)
			TOUC2	TIME	UT_VALUE - (X/100)
			TOUC3	SMS	UT_VALUE - X
			TOUC4	VOLUME	UT_VALUE - ((X-10000)*1048576)-->

		<xsl:variable name="UC_Source">
			<xsl:choose>
				<xsl:when test="upper-case($Factor) eq 'MONEY'">
					<xsl:choose>
						<xsl:when test="upper-case($Resource) eq 'UC'">		
							<xsl:value-of select="format-number($BALANCE div 10000, '#0')"/>						
						</xsl:when>
					</xsl:choose>
				</xsl:when>
				<xsl:when test="upper-case($Factor) eq 'SMS'">
					<xsl:value-of select="format-number($UT_Value - $BALANCE, '#0')"/>
				</xsl:when>
				<xsl:when test="upper-case($Factor) eq 'VOLUME'">
					<xsl:choose>
						<xsl:when test="upper-case($Resource) eq 'TOUC1'">
							<xsl:value-of select="format-number($UT_Value -( $BALANCE *  1024), '#0')"/>
						</xsl:when>
						<xsl:when test="upper-case($Resource) eq 'TOUC4'">
							<xsl:value-of select="format-number($UT_Value -(($BALANCE div 10000) * 1048576), '#0')"/>
						</xsl:when>
					</xsl:choose>
				</xsl:when>
				<xsl:when test="upper-case($Factor) eq 'TIME'">
					<xsl:choose>
						<xsl:when test="upper-case($Resource) eq 'TOUC2'">
							<xsl:value-of select="format-number($UT_Value - ($BALANCE div 100) * 60, '#0')"/>
						</xsl:when>											
					</xsl:choose>
				</xsl:when>
			</xsl:choose>							
		</xsl:variable>
		<USAGE_COUNTER>
			<Account_ID>
				<xsl:value-of select="$BALANCE_MSISDN"/>
			</Account_ID>
			<usage_counter_id>
				<xsl:value-of select="$UC_ID"/>
			</usage_counter_id>
			<associated_party_id>
				<xsl:value-of select="$MSISDN"/>
			</associated_party_id>
			<value>								
				<xsl:value-of select="$UC_Source"/>
			</value>
			<product_id>
				<xsl:choose>
					<xsl:when test="count($ProductPrivateForUCList)">
						<xsl:for-each select="$ProductPrivateForUCList">
							<xsl:choose>
								<xsl:when test="string-length(.)">
									<xsl:variable name="UC_VALUE" select="tokenize(.,',')[1]"/>
									<xsl:variable name="UC_PRODUCT" select="tokenize(.,',')[2]"/>
									<xsl:choose>
										<xsl:when test="$UC_ID eq $UC_VALUE">
											<xsl:value-of select="$UC_PRODUCT"/>
										</xsl:when>
										<!--<xsl:otherwise>
											<xsl:value-of select="$default_ZERO"/>
										</xsl:otherwise>-->
									</xsl:choose>
								</xsl:when>
							</xsl:choose>																
						</xsl:for-each>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$default_ZERO"/>
					</xsl:otherwise>
				</xsl:choose>
			</product_id>			
			<nominal_value>
				<xsl:value-of select="$UC_Source"/>
			</nominal_value>
			<value_decimals>
				<xsl:value-of select="$default_ZERO"/>
			</value_decimals>
		</USAGE_COUNTER>		
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
		
<!--		<xsl:variable name="tmp" select="concat(substring($INPUT_STR_DT, 7, 4), '-', substring($INPUT_STR_DT, 1, 2), '-', substring($INPUT_STR_DT, 4, 2))"/>-->
		<xsl:variable name="tmp" select="concat(substring($INPUT_STR_DT, 1, 4), '-', substring($INPUT_STR_DT, 6, 2), '-', substring($INPUT_STR_DT, 9, 2))"/>
		
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
