
package com.ericsson.jibx.beans;

import java.util.ArrayList;
import java.util.List;

/** 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:element xmlns:xs="http://www.w3.org/2001/XMLSchema" name="subscriber_xml">
 *   &lt;xs:complexType>
 *     &lt;xs:sequence>
 *       &lt;xs:element name="subscriber_info">
 *         &lt;xs:complexType>
 *           &lt;xs:sequence>
 *             &lt;xs:element name="SUBSCRIBER">
 *               &lt;xs:complexType>
 *                 &lt;xs:all>
 *                   &lt;xs:element type="xs:string" name="MSISDN"/>
 *                   &lt;xs:element type="xs:string" name="CCS_ACCT_TYPE_NAME"/>
 *                   &lt;xs:element type="xs:string" name="CCS_ACCT_TYPE_ID"/>
 *                   &lt;xs:element type="xs:string" name="SERVICE_STATE"/>
 *                   &lt;xs:element type="xs:string" name="CREATION_DATE"/>
 *                   &lt;xs:element type="xs:string" name="WALLET_EXPIRY"/>
 *                   &lt;xs:element type="xs:string" name="WALLET_ID"/>
 *                   &lt;xs:element type="xs:string" name="WALLET_TYPE"/>
 *                   &lt;xs:element type="xs:string" name="BE_ACCT_ENGINE_ID"/>
 *                 &lt;/xs:all>
 *               &lt;/xs:complexType>
 *             &lt;/xs:element>
 *           &lt;/xs:sequence>
 *         &lt;/xs:complexType>
 *       &lt;/xs:element>
 *       &lt;xs:element name="balancesdump_info">
 *         &lt;xs:complexType>
 *           &lt;xs:sequence>
 *             &lt;xs:element name="schemasubscriberbalancesdump_info" minOccurs="0" maxOccurs="unbounded">
 *               &lt;!-- Reference to inner class SchemasubscriberbalancesdumpInfo -->
 *             &lt;/xs:element>
 *           &lt;/xs:sequence>
 *         &lt;/xs:complexType>
 *       &lt;/xs:element>
 *       &lt;xs:element name="cugclidump_info">
 *         &lt;xs:complexType>
 *           &lt;xs:sequence>
 *             &lt;xs:element name="schemasubscribercugclidump_info" minOccurs="0" maxOccurs="unbounded">
 *               &lt;!-- Reference to inner class SchemasubscribercugclidumpInfo -->
 *             &lt;/xs:element>
 *           &lt;/xs:sequence>
 *         &lt;/xs:complexType>
 *       &lt;/xs:element>
 *       &lt;xs:element name="usmsdump_info">
 *         &lt;xs:complexType>
 *           &lt;xs:sequence>
 *             &lt;xs:element name="schemasubscriberusmsdump_info" minOccurs="0" maxOccurs="unbounded">
 *               &lt;!-- Reference to inner class SchemasubscriberusmsdumpInfo -->
 *             &lt;/xs:element>
 *           &lt;/xs:sequence>
 *         &lt;/xs:complexType>
 *       &lt;/xs:element>
 *       &lt;xs:element name="profiledump_info">
 *         &lt;xs:complexType>
 *           &lt;xs:sequence>
 *             &lt;xs:element name="schemasubscriberprofiledump_info" minOccurs="0" maxOccurs="unbounded">
 *               &lt;!-- Reference to inner class SchemasubscriberprofiledumpInfo -->
 *             &lt;/xs:element>
 *           &lt;/xs:sequence>
 *         &lt;/xs:complexType>
 *       &lt;/xs:element>
 *     &lt;/xs:sequence>
 *   &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 */
public class SubscriberXml
{
    private String subscriberInfoMSISDN;
    private String subscriberInfoCCSACCTTYPENAME;
    private String subscriberInfoCCSACCTTYPEID;
    private String subscriberInfoSERVICESTATE;
    private String subscriberInfoCREATIONDATE;
    private String subscriberInfoWALLETEXPIRY;
    private String subscriberInfoWALLETID;
    private String subscriberInfoWALLETTYPE;
    private String subscriberInfoBEACCTENGINEID;
    private List<SchemasubscriberbalancesdumpInfo> balancesdumpInfoList = new ArrayList<SchemasubscriberbalancesdumpInfo>();
    private List<SchemasubscribercugclidumpInfo> cugclidumpInfoList = new ArrayList<SchemasubscribercugclidumpInfo>();
    private List<SchemasubscriberusmsdumpInfo> usmsdumpInfoList = new ArrayList<SchemasubscriberusmsdumpInfo>();
    private List<SchemasubscriberprofiledumpInfo> profiledumpInfoList = new ArrayList<SchemasubscriberprofiledumpInfo>();

    /** 
     * Get the 'MSISDN' element value.
     * 
     * @return value
     */
    public String getSubscriberInfoMSISDN() {
        return subscriberInfoMSISDN;
    }

    /** 
     * Set the 'MSISDN' element value.
     * 
     * @param subscriberInfoMSISDN
     */
    public void setSubscriberInfoMSISDN(String subscriberInfoMSISDN) {
        this.subscriberInfoMSISDN = subscriberInfoMSISDN;
    }

    /** 
     * Get the 'CCS_ACCT_TYPE_NAME' element value.
     * 
     * @return value
     */
    public String getSubscriberInfoCCSACCTTYPENAME() {
        return subscriberInfoCCSACCTTYPENAME;
    }

    /** 
     * Set the 'CCS_ACCT_TYPE_NAME' element value.
     * 
     * @param subscriberInfoCCSACCTTYPENAME
     */
    public void setSubscriberInfoCCSACCTTYPENAME(
            String subscriberInfoCCSACCTTYPENAME) {
        this.subscriberInfoCCSACCTTYPENAME = subscriberInfoCCSACCTTYPENAME;
    }

    /** 
     * Get the 'CCS_ACCT_TYPE_ID' element value.
     * 
     * @return value
     */
    public String getSubscriberInfoCCSACCTTYPEID() {
        return subscriberInfoCCSACCTTYPEID;
    }

    /** 
     * Set the 'CCS_ACCT_TYPE_ID' element value.
     * 
     * @param subscriberInfoCCSACCTTYPEID
     */
    public void setSubscriberInfoCCSACCTTYPEID(
            String subscriberInfoCCSACCTTYPEID) {
        this.subscriberInfoCCSACCTTYPEID = subscriberInfoCCSACCTTYPEID;
    }

    /** 
     * Get the 'SERVICE_STATE' element value.
     * 
     * @return value
     */
    public String getSubscriberInfoSERVICESTATE() {
        return subscriberInfoSERVICESTATE;
    }

    /** 
     * Set the 'SERVICE_STATE' element value.
     * 
     * @param subscriberInfoSERVICESTATE
     */
    public void setSubscriberInfoSERVICESTATE(String subscriberInfoSERVICESTATE) {
        this.subscriberInfoSERVICESTATE = subscriberInfoSERVICESTATE;
    }

    /** 
     * Get the 'CREATION_DATE' element value.
     * 
     * @return value
     */
    public String getSubscriberInfoCREATIONDATE() {
        return subscriberInfoCREATIONDATE;
    }

    /** 
     * Set the 'CREATION_DATE' element value.
     * 
     * @param subscriberInfoCREATIONDATE
     */
    public void setSubscriberInfoCREATIONDATE(String subscriberInfoCREATIONDATE) {
        this.subscriberInfoCREATIONDATE = subscriberInfoCREATIONDATE;
    }

    /** 
     * Get the 'WALLET_EXPIRY' element value.
     * 
     * @return value
     */
    public String getSubscriberInfoWALLETEXPIRY() {
        return subscriberInfoWALLETEXPIRY;
    }

    /** 
     * Set the 'WALLET_EXPIRY' element value.
     * 
     * @param subscriberInfoWALLETEXPIRY
     */
    public void setSubscriberInfoWALLETEXPIRY(String subscriberInfoWALLETEXPIRY) {
        this.subscriberInfoWALLETEXPIRY = subscriberInfoWALLETEXPIRY;
    }

    /** 
     * Get the 'WALLET_ID' element value.
     * 
     * @return value
     */
    public String getSubscriberInfoWALLETID() {
        return subscriberInfoWALLETID;
    }

    /** 
     * Set the 'WALLET_ID' element value.
     * 
     * @param subscriberInfoWALLETID
     */
    public void setSubscriberInfoWALLETID(String subscriberInfoWALLETID) {
        this.subscriberInfoWALLETID = subscriberInfoWALLETID;
    }

    /** 
     * Get the 'WALLET_TYPE' element value.
     * 
     * @return value
     */
    public String getSubscriberInfoWALLETTYPE() {
        return subscriberInfoWALLETTYPE;
    }

    /** 
     * Set the 'WALLET_TYPE' element value.
     * 
     * @param subscriberInfoWALLETTYPE
     */
    public void setSubscriberInfoWALLETTYPE(String subscriberInfoWALLETTYPE) {
        this.subscriberInfoWALLETTYPE = subscriberInfoWALLETTYPE;
    }

    /** 
     * Get the 'BE_ACCT_ENGINE_ID' element value.
     * 
     * @return value
     */
    public String getSubscriberInfoBEACCTENGINEID() {
        return subscriberInfoBEACCTENGINEID;
    }

    /** 
     * Set the 'BE_ACCT_ENGINE_ID' element value.
     * 
     * @param subscriberInfoBEACCTENGINEID
     */
    public void setSubscriberInfoBEACCTENGINEID(
            String subscriberInfoBEACCTENGINEID) {
        this.subscriberInfoBEACCTENGINEID = subscriberInfoBEACCTENGINEID;
    }

    /** 
     * Get the list of 'schemasubscriberbalancesdump_info' element items.
     * 
     * @return list
     */
    public List<SchemasubscriberbalancesdumpInfo> getBalancesdumpInfoList() {
        return balancesdumpInfoList;
    }

    /** 
     * Set the list of 'schemasubscriberbalancesdump_info' element items.
     * 
     * @param list
     */
    public void setBalancesdumpInfoList(
            List<SchemasubscriberbalancesdumpInfo> list) {
        balancesdumpInfoList = list;
    }

    /** 
     * Get the list of 'schemasubscribercugclidump_info' element items.
     * 
     * @return list
     */
    public List<SchemasubscribercugclidumpInfo> getCugclidumpInfoList() {
        return cugclidumpInfoList;
    }

    /** 
     * Set the list of 'schemasubscribercugclidump_info' element items.
     * 
     * @param list
     */
    public void setCugclidumpInfoList(List<SchemasubscribercugclidumpInfo> list) {
        cugclidumpInfoList = list;
    }

    /** 
     * Get the list of 'schemasubscriberusmsdump_info' element items.
     * 
     * @return list
     */
    public List<SchemasubscriberusmsdumpInfo> getUsmsdumpInfoList() {
        return usmsdumpInfoList;
    }

    /** 
     * Set the list of 'schemasubscriberusmsdump_info' element items.
     * 
     * @param list
     */
    public void setUsmsdumpInfoList(List<SchemasubscriberusmsdumpInfo> list) {
        usmsdumpInfoList = list;
    }

    /** 
     * Get the list of 'schemasubscriberprofiledump_info' element items.
     * 
     * @return list
     */
    public List<SchemasubscriberprofiledumpInfo> getProfiledumpInfoList() {
        return profiledumpInfoList;
    }

    /** 
     * Set the list of 'schemasubscriberprofiledump_info' element items.
     * 
     * @param list
     */
    public void setProfiledumpInfoList(
            List<SchemasubscriberprofiledumpInfo> list) {
        profiledumpInfoList = list;
    }
    /** 
     * Schema fragment(s) for this class:
     * <pre>
     * &lt;xs:element xmlns:xs="http://www.w3.org/2001/XMLSchema" name="schemasubscriberbalancesdump_info" minOccurs="0" maxOccurs="unbounded">
     *   &lt;xs:complexType>
     *     &lt;xs:all>
     *       &lt;xs:element type="xs:string" name="MSISDN"/>
     *       &lt;xs:element type="xs:string" name="WALLET_ID"/>
     *       &lt;xs:element type="xs:string" name="BALANCE_TYPE"/>
     *       &lt;xs:element type="xs:string" name="BE_BUCKET_ID"/>
     *       &lt;xs:element type="xs:string" name="BALANCE_TYPE_NAME"/>
     *       &lt;xs:element type="xs:string" name="BE_BUCKET_VALUE"/>
     *       &lt;xs:element type="xs:string" name="BE_BUCKET_START_DATE"/>
     *       &lt;xs:element type="xs:string" name="BE_NEVER_EXPIRES"/>
     *       &lt;xs:element type="xs:string" name="BE_EXPIRY"/>
     *       &lt;xs:element type="xs:string" name="BE_BUCKET_NEVER_USED"/>
     *       &lt;xs:element type="xs:string" name="BE_BUCKET_LAST_USE"/>
     *       &lt;xs:element type="xs:string" name="BE_BUCKET_REFERENCE"/>
     *       &lt;xs:element type="xs:string" name="WALLET_TYPE"/>
     *       &lt;xs:element type="xs:string" name="CCS_BALANCE_UNIT_ID"/>
     *       &lt;xs:element type="xs:string" name="CCS_BALANCE_UNIT_NAME"/>
     *       &lt;xs:element type="xs:string" name="BE_ACCT_ENGINE_ID"/>
     *     &lt;/xs:all>
     *   &lt;/xs:complexType>
     * &lt;/xs:element>
     * </pre>
     */
    public static class SchemasubscriberbalancesdumpInfo
    {
        private String MSISDN;
        private String WALLETID;
        private String BALANCETYPE;
        private String BEBUCKETID;
        private String BALANCETYPENAME;
        private String BEBUCKETVALUE;
        private String BEBUCKETSTARTDATE;
        private String BENEVEREXPIRES;
        private String BEEXPIRY;
        private String BEBUCKETNEVERUSED;
        private String BEBUCKETLASTUSE;
        private String BEBUCKETREFERENCE;
        private String WALLETTYPE;
        private String CCSBALANCEUNITID;
        private String CCSBALANCEUNITNAME;
        private String BEACCTENGINEID;

        /** 
         * Get the 'MSISDN' element value.
         * 
         * @return value
         */
        public String getMSISDN() {
            return MSISDN;
        }

        /** 
         * Set the 'MSISDN' element value.
         * 
         * @param MSISDN
         */
        public void setMSISDN(String MSISDN) {
            this.MSISDN = MSISDN;
        }

        /** 
         * Get the 'WALLET_ID' element value.
         * 
         * @return value
         */
        public String getWALLETID() {
            return WALLETID;
        }

        /** 
         * Set the 'WALLET_ID' element value.
         * 
         * @param WALLETID
         */
        public void setWALLETID(String WALLETID) {
            this.WALLETID = WALLETID;
        }

        /** 
         * Get the 'BALANCE_TYPE' element value.
         * 
         * @return value
         */
        public String getBALANCETYPE() {
            return BALANCETYPE;
        }

        /** 
         * Set the 'BALANCE_TYPE' element value.
         * 
         * @param BALANCETYPE
         */
        public void setBALANCETYPE(String BALANCETYPE) {
            this.BALANCETYPE = BALANCETYPE;
        }

        /** 
         * Get the 'BE_BUCKET_ID' element value.
         * 
         * @return value
         */
        public String getBEBUCKETID() {
            return BEBUCKETID;
        }

        /** 
         * Set the 'BE_BUCKET_ID' element value.
         * 
         * @param BEBUCKETID
         */
        public void setBEBUCKETID(String BEBUCKETID) {
            this.BEBUCKETID = BEBUCKETID;
        }

        /** 
         * Get the 'BALANCE_TYPE_NAME' element value.
         * 
         * @return value
         */
        public String getBALANCETYPENAME() {
            return BALANCETYPENAME;
        }

        /** 
         * Set the 'BALANCE_TYPE_NAME' element value.
         * 
         * @param BALANCETYPENAME
         */
        public void setBALANCETYPENAME(String BALANCETYPENAME) {
            this.BALANCETYPENAME = BALANCETYPENAME;
        }

        /** 
         * Get the 'BE_BUCKET_VALUE' element value.
         * 
         * @return value
         */
        public String getBEBUCKETVALUE() {
            return BEBUCKETVALUE;
        }

        /** 
         * Set the 'BE_BUCKET_VALUE' element value.
         * 
         * @param BEBUCKETVALUE
         */
        public void setBEBUCKETVALUE(String BEBUCKETVALUE) {
            this.BEBUCKETVALUE = BEBUCKETVALUE;
        }

        /** 
         * Get the 'BE_BUCKET_START_DATE' element value.
         * 
         * @return value
         */
        public String getBEBUCKETSTARTDATE() {
            return BEBUCKETSTARTDATE;
        }

        /** 
         * Set the 'BE_BUCKET_START_DATE' element value.
         * 
         * @param BEBUCKETSTARTDATE
         */
        public void setBEBUCKETSTARTDATE(String BEBUCKETSTARTDATE) {
            this.BEBUCKETSTARTDATE = BEBUCKETSTARTDATE;
        }

        /** 
         * Get the 'BE_NEVER_EXPIRES' element value.
         * 
         * @return value
         */
        public String getBENEVEREXPIRES() {
            return BENEVEREXPIRES;
        }

        /** 
         * Set the 'BE_NEVER_EXPIRES' element value.
         * 
         * @param BENEVEREXPIRES
         */
        public void setBENEVEREXPIRES(String BENEVEREXPIRES) {
            this.BENEVEREXPIRES = BENEVEREXPIRES;
        }

        /** 
         * Get the 'BE_EXPIRY' element value.
         * 
         * @return value
         */
        public String getBEEXPIRY() {
            return BEEXPIRY;
        }

        /** 
         * Set the 'BE_EXPIRY' element value.
         * 
         * @param BEEXPIRY
         */
        public void setBEEXPIRY(String BEEXPIRY) {
            this.BEEXPIRY = BEEXPIRY;
        }

        /** 
         * Get the 'BE_BUCKET_NEVER_USED' element value.
         * 
         * @return value
         */
        public String getBEBUCKETNEVERUSED() {
            return BEBUCKETNEVERUSED;
        }

        /** 
         * Set the 'BE_BUCKET_NEVER_USED' element value.
         * 
         * @param BEBUCKETNEVERUSED
         */
        public void setBEBUCKETNEVERUSED(String BEBUCKETNEVERUSED) {
            this.BEBUCKETNEVERUSED = BEBUCKETNEVERUSED;
        }

        /** 
         * Get the 'BE_BUCKET_LAST_USE' element value.
         * 
         * @return value
         */
        public String getBEBUCKETLASTUSE() {
            return BEBUCKETLASTUSE;
        }

        /** 
         * Set the 'BE_BUCKET_LAST_USE' element value.
         * 
         * @param BEBUCKETLASTUSE
         */
        public void setBEBUCKETLASTUSE(String BEBUCKETLASTUSE) {
            this.BEBUCKETLASTUSE = BEBUCKETLASTUSE;
        }

        /** 
         * Get the 'BE_BUCKET_REFERENCE' element value.
         * 
         * @return value
         */
        public String getBEBUCKETREFERENCE() {
            return BEBUCKETREFERENCE;
        }

        /** 
         * Set the 'BE_BUCKET_REFERENCE' element value.
         * 
         * @param BEBUCKETREFERENCE
         */
        public void setBEBUCKETREFERENCE(String BEBUCKETREFERENCE) {
            this.BEBUCKETREFERENCE = BEBUCKETREFERENCE;
        }

        /** 
         * Get the 'WALLET_TYPE' element value.
         * 
         * @return value
         */
        public String getWALLETTYPE() {
            return WALLETTYPE;
        }

        /** 
         * Set the 'WALLET_TYPE' element value.
         * 
         * @param WALLETTYPE
         */
        public void setWALLETTYPE(String WALLETTYPE) {
            this.WALLETTYPE = WALLETTYPE;
        }

        /** 
         * Get the 'CCS_BALANCE_UNIT_ID' element value.
         * 
         * @return value
         */
        public String getCCSBALANCEUNITID() {
            return CCSBALANCEUNITID;
        }

        /** 
         * Set the 'CCS_BALANCE_UNIT_ID' element value.
         * 
         * @param CCSBALANCEUNITID
         */
        public void setCCSBALANCEUNITID(String CCSBALANCEUNITID) {
            this.CCSBALANCEUNITID = CCSBALANCEUNITID;
        }

        /** 
         * Get the 'CCS_BALANCE_UNIT_NAME' element value.
         * 
         * @return value
         */
        public String getCCSBALANCEUNITNAME() {
            return CCSBALANCEUNITNAME;
        }

        /** 
         * Set the 'CCS_BALANCE_UNIT_NAME' element value.
         * 
         * @param CCSBALANCEUNITNAME
         */
        public void setCCSBALANCEUNITNAME(String CCSBALANCEUNITNAME) {
            this.CCSBALANCEUNITNAME = CCSBALANCEUNITNAME;
        }

        /** 
         * Get the 'BE_ACCT_ENGINE_ID' element value.
         * 
         * @return value
         */
        public String getBEACCTENGINEID() {
            return BEACCTENGINEID;
        }

        /** 
         * Set the 'BE_ACCT_ENGINE_ID' element value.
         * 
         * @param BEACCTENGINEID
         */
        public void setBEACCTENGINEID(String BEACCTENGINEID) {
            this.BEACCTENGINEID = BEACCTENGINEID;
        }
    }
    /** 
     * Schema fragment(s) for this class:
     * <pre>
     * &lt;xs:element xmlns:xs="http://www.w3.org/2001/XMLSchema" name="schemasubscribercugclidump_info" minOccurs="0" maxOccurs="unbounded">
     *   &lt;xs:complexType>
     *     &lt;xs:all>
     *       &lt;xs:element type="xs:string" name="MSISDN"/>
     *       &lt;xs:element type="xs:string" name="CUG_NAME"/>
     *       &lt;xs:element type="xs:string" name="CHANGE_DATE"/>
     *       &lt;xs:element type="xs:string" name="TARIFF_PLAN"/>
     *       &lt;xs:element type="xs:string" name="TARIFF_PLAN_NAME"/>
     *     &lt;/xs:all>
     *   &lt;/xs:complexType>
     * &lt;/xs:element>
     * </pre>
     */
    public static class SchemasubscribercugclidumpInfo
    {
        private String MSISDN;
        private String CUGNAME;
        private String CHANGEDATE;
        private String TARIFFPLAN;
        private String TARIFFPLANNAME;

        /** 
         * Get the 'MSISDN' element value.
         * 
         * @return value
         */
        public String getMSISDN() {
            return MSISDN;
        }

        /** 
         * Set the 'MSISDN' element value.
         * 
         * @param MSISDN
         */
        public void setMSISDN(String MSISDN) {
            this.MSISDN = MSISDN;
        }

        /** 
         * Get the 'CUG_NAME' element value.
         * 
         * @return value
         */
        public String getCUGNAME() {
            return CUGNAME;
        }

        /** 
         * Set the 'CUG_NAME' element value.
         * 
         * @param CUGNAME
         */
        public void setCUGNAME(String CUGNAME) {
            this.CUGNAME = CUGNAME;
        }

        /** 
         * Get the 'CHANGE_DATE' element value.
         * 
         * @return value
         */
        public String getCHANGEDATE() {
            return CHANGEDATE;
        }

        /** 
         * Set the 'CHANGE_DATE' element value.
         * 
         * @param CHANGEDATE
         */
        public void setCHANGEDATE(String CHANGEDATE) {
            this.CHANGEDATE = CHANGEDATE;
        }

        /** 
         * Get the 'TARIFF_PLAN' element value.
         * 
         * @return value
         */
        public String getTARIFFPLAN() {
            return TARIFFPLAN;
        }

        /** 
         * Set the 'TARIFF_PLAN' element value.
         * 
         * @param TARIFFPLAN
         */
        public void setTARIFFPLAN(String TARIFFPLAN) {
            this.TARIFFPLAN = TARIFFPLAN;
        }

        /** 
         * Get the 'TARIFF_PLAN_NAME' element value.
         * 
         * @return value
         */
        public String getTARIFFPLANNAME() {
            return TARIFFPLANNAME;
        }

        /** 
         * Set the 'TARIFF_PLAN_NAME' element value.
         * 
         * @param TARIFFPLANNAME
         */
        public void setTARIFFPLANNAME(String TARIFFPLANNAME) {
            this.TARIFFPLANNAME = TARIFFPLANNAME;
        }
    }
    /** 
     * Schema fragment(s) for this class:
     * <pre>
     * &lt;xs:element xmlns:xs="http://www.w3.org/2001/XMLSchema" name="schemasubscriberusmsdump_info" minOccurs="0" maxOccurs="unbounded">
     *   &lt;xs:complexType>
     *     &lt;xs:all>
     *       &lt;xs:element type="xs:string" name="MSISDN"/>
     *       &lt;xs:element type="xs:string" name="INITIAL_ACTIVATION_DATE"/>
     *       &lt;xs:element type="xs:string" name="INITIAL_ACCOUNT_BALANCE"/>
     *       &lt;xs:element type="xs:string" name="LAST_ACCOUNT_EXPIRY_PERIOD"/>
     *       &lt;xs:element type="xs:string" name="LAST_BALANCE_BEFORE_RECHARGE"/>
     *       &lt;xs:element type="xs:string" name="LAST_BALANCE_EXPIRY_PERIOD"/>
     *       &lt;xs:element type="xs:string" name="LAST_EXPIRED_AMOUNT"/>
     *       &lt;xs:element type="xs:string" name="LAST_EXPIRY_DATE"/>
     *       &lt;xs:element type="xs:string" name="LAST_PRODUCT_TYPE_SWAP_DATE"/>
     *       &lt;xs:element type="xs:string" name="LAST_RECHARGE_AMOUNT"/>
     *       &lt;xs:element type="xs:string" name="FIRST_RECHARGE_DATE"/>
     *       &lt;xs:element type="xs:string" name="LAST_RECHARGE_DATE"/>
     *       &lt;xs:element type="xs:string" name="LAST_STATE_CHANGE_DATE"/>
     *       &lt;xs:element type="xs:string" name="TOTAL_EXPIRED_AMOUNT"/>
     *       &lt;xs:element type="xs:string" name="LAST_STATE_CHANGE_REASON"/>
     *       &lt;xs:element type="xs:string" name="WALLET_ID"/>
     *       &lt;xs:element type="xs:string" name="WALLET_TYPE"/>
     *       &lt;xs:element type="xs:string" name="BE_ACCT_ENGINE_ID"/>
     *     &lt;/xs:all>
     *   &lt;/xs:complexType>
     * &lt;/xs:element>
     * </pre>
     */
    public static class SchemasubscriberusmsdumpInfo
    {
        private String MSISDN;
        private String INITIALACTIVATIONDATE;
        private String INITIALACCOUNTBALANCE;
        private String LASTACCOUNTEXPIRYPERIOD;
        private String LASTBALANCEBEFORERECHARGE;
        private String LASTBALANCEEXPIRYPERIOD;
        private String LASTEXPIREDAMOUNT;
        private String LASTEXPIRYDATE;
        private String LASTPRODUCTTYPESWAPDATE;
        private String LASTRECHARGEAMOUNT;
        private String FIRSTRECHARGEDATE;
        private String LASTRECHARGEDATE;
        private String LASTSTATECHANGEDATE;
        private String TOTALEXPIREDAMOUNT;
        private String LASTSTATECHANGEREASON;
        private String WALLETID;
        private String WALLETTYPE;
        private String BEACCTENGINEID;

        /** 
         * Get the 'MSISDN' element value.
         * 
         * @return value
         */
        public String getMSISDN() {
            return MSISDN;
        }

        /** 
         * Set the 'MSISDN' element value.
         * 
         * @param MSISDN
         */
        public void setMSISDN(String MSISDN) {
            this.MSISDN = MSISDN;
        }

        /** 
         * Get the 'INITIAL_ACTIVATION_DATE' element value.
         * 
         * @return value
         */
        public String getINITIALACTIVATIONDATE() {
            return INITIALACTIVATIONDATE;
        }

        /** 
         * Set the 'INITIAL_ACTIVATION_DATE' element value.
         * 
         * @param INITIALACTIVATIONDATE
         */
        public void setINITIALACTIVATIONDATE(String INITIALACTIVATIONDATE) {
            this.INITIALACTIVATIONDATE = INITIALACTIVATIONDATE;
        }

        /** 
         * Get the 'INITIAL_ACCOUNT_BALANCE' element value.
         * 
         * @return value
         */
        public String getINITIALACCOUNTBALANCE() {
            return INITIALACCOUNTBALANCE;
        }

        /** 
         * Set the 'INITIAL_ACCOUNT_BALANCE' element value.
         * 
         * @param INITIALACCOUNTBALANCE
         */
        public void setINITIALACCOUNTBALANCE(String INITIALACCOUNTBALANCE) {
            this.INITIALACCOUNTBALANCE = INITIALACCOUNTBALANCE;
        }

        /** 
         * Get the 'LAST_ACCOUNT_EXPIRY_PERIOD' element value.
         * 
         * @return value
         */
        public String getLASTACCOUNTEXPIRYPERIOD() {
            return LASTACCOUNTEXPIRYPERIOD;
        }

        /** 
         * Set the 'LAST_ACCOUNT_EXPIRY_PERIOD' element value.
         * 
         * @param LASTACCOUNTEXPIRYPERIOD
         */
        public void setLASTACCOUNTEXPIRYPERIOD(String LASTACCOUNTEXPIRYPERIOD) {
            this.LASTACCOUNTEXPIRYPERIOD = LASTACCOUNTEXPIRYPERIOD;
        }

        /** 
         * Get the 'LAST_BALANCE_BEFORE_RECHARGE' element value.
         * 
         * @return value
         */
        public String getLASTBALANCEBEFORERECHARGE() {
            return LASTBALANCEBEFORERECHARGE;
        }

        /** 
         * Set the 'LAST_BALANCE_BEFORE_RECHARGE' element value.
         * 
         * @param LASTBALANCEBEFORERECHARGE
         */
        public void setLASTBALANCEBEFORERECHARGE(
                String LASTBALANCEBEFORERECHARGE) {
            this.LASTBALANCEBEFORERECHARGE = LASTBALANCEBEFORERECHARGE;
        }

        /** 
         * Get the 'LAST_BALANCE_EXPIRY_PERIOD' element value.
         * 
         * @return value
         */
        public String getLASTBALANCEEXPIRYPERIOD() {
            return LASTBALANCEEXPIRYPERIOD;
        }

        /** 
         * Set the 'LAST_BALANCE_EXPIRY_PERIOD' element value.
         * 
         * @param LASTBALANCEEXPIRYPERIOD
         */
        public void setLASTBALANCEEXPIRYPERIOD(String LASTBALANCEEXPIRYPERIOD) {
            this.LASTBALANCEEXPIRYPERIOD = LASTBALANCEEXPIRYPERIOD;
        }

        /** 
         * Get the 'LAST_EXPIRED_AMOUNT' element value.
         * 
         * @return value
         */
        public String getLASTEXPIREDAMOUNT() {
            return LASTEXPIREDAMOUNT;
        }

        /** 
         * Set the 'LAST_EXPIRED_AMOUNT' element value.
         * 
         * @param LASTEXPIREDAMOUNT
         */
        public void setLASTEXPIREDAMOUNT(String LASTEXPIREDAMOUNT) {
            this.LASTEXPIREDAMOUNT = LASTEXPIREDAMOUNT;
        }

        /** 
         * Get the 'LAST_EXPIRY_DATE' element value.
         * 
         * @return value
         */
        public String getLASTEXPIRYDATE() {
            return LASTEXPIRYDATE;
        }

        /** 
         * Set the 'LAST_EXPIRY_DATE' element value.
         * 
         * @param LASTEXPIRYDATE
         */
        public void setLASTEXPIRYDATE(String LASTEXPIRYDATE) {
            this.LASTEXPIRYDATE = LASTEXPIRYDATE;
        }

        /** 
         * Get the 'LAST_PRODUCT_TYPE_SWAP_DATE' element value.
         * 
         * @return value
         */
        public String getLASTPRODUCTTYPESWAPDATE() {
            return LASTPRODUCTTYPESWAPDATE;
        }

        /** 
         * Set the 'LAST_PRODUCT_TYPE_SWAP_DATE' element value.
         * 
         * @param LASTPRODUCTTYPESWAPDATE
         */
        public void setLASTPRODUCTTYPESWAPDATE(String LASTPRODUCTTYPESWAPDATE) {
            this.LASTPRODUCTTYPESWAPDATE = LASTPRODUCTTYPESWAPDATE;
        }

        /** 
         * Get the 'LAST_RECHARGE_AMOUNT' element value.
         * 
         * @return value
         */
        public String getLASTRECHARGEAMOUNT() {
            return LASTRECHARGEAMOUNT;
        }

        /** 
         * Set the 'LAST_RECHARGE_AMOUNT' element value.
         * 
         * @param LASTRECHARGEAMOUNT
         */
        public void setLASTRECHARGEAMOUNT(String LASTRECHARGEAMOUNT) {
            this.LASTRECHARGEAMOUNT = LASTRECHARGEAMOUNT;
        }

        /** 
         * Get the 'FIRST_RECHARGE_DATE' element value.
         * 
         * @return value
         */
        public String getFIRSTRECHARGEDATE() {
            return FIRSTRECHARGEDATE;
        }

        /** 
         * Set the 'FIRST_RECHARGE_DATE' element value.
         * 
         * @param FIRSTRECHARGEDATE
         */
        public void setFIRSTRECHARGEDATE(String FIRSTRECHARGEDATE) {
            this.FIRSTRECHARGEDATE = FIRSTRECHARGEDATE;
        }

        /** 
         * Get the 'LAST_RECHARGE_DATE' element value.
         * 
         * @return value
         */
        public String getLASTRECHARGEDATE() {
            return LASTRECHARGEDATE;
        }

        /** 
         * Set the 'LAST_RECHARGE_DATE' element value.
         * 
         * @param LASTRECHARGEDATE
         */
        public void setLASTRECHARGEDATE(String LASTRECHARGEDATE) {
            this.LASTRECHARGEDATE = LASTRECHARGEDATE;
        }

        /** 
         * Get the 'LAST_STATE_CHANGE_DATE' element value.
         * 
         * @return value
         */
        public String getLASTSTATECHANGEDATE() {
            return LASTSTATECHANGEDATE;
        }

        /** 
         * Set the 'LAST_STATE_CHANGE_DATE' element value.
         * 
         * @param LASTSTATECHANGEDATE
         */
        public void setLASTSTATECHANGEDATE(String LASTSTATECHANGEDATE) {
            this.LASTSTATECHANGEDATE = LASTSTATECHANGEDATE;
        }

        /** 
         * Get the 'TOTAL_EXPIRED_AMOUNT' element value.
         * 
         * @return value
         */
        public String getTOTALEXPIREDAMOUNT() {
            return TOTALEXPIREDAMOUNT;
        }

        /** 
         * Set the 'TOTAL_EXPIRED_AMOUNT' element value.
         * 
         * @param TOTALEXPIREDAMOUNT
         */
        public void setTOTALEXPIREDAMOUNT(String TOTALEXPIREDAMOUNT) {
            this.TOTALEXPIREDAMOUNT = TOTALEXPIREDAMOUNT;
        }

        /** 
         * Get the 'LAST_STATE_CHANGE_REASON' element value.
         * 
         * @return value
         */
        public String getLASTSTATECHANGEREASON() {
            return LASTSTATECHANGEREASON;
        }

        /** 
         * Set the 'LAST_STATE_CHANGE_REASON' element value.
         * 
         * @param LASTSTATECHANGEREASON
         */
        public void setLASTSTATECHANGEREASON(String LASTSTATECHANGEREASON) {
            this.LASTSTATECHANGEREASON = LASTSTATECHANGEREASON;
        }

        /** 
         * Get the 'WALLET_ID' element value.
         * 
         * @return value
         */
        public String getWALLETID() {
            return WALLETID;
        }

        /** 
         * Set the 'WALLET_ID' element value.
         * 
         * @param WALLETID
         */
        public void setWALLETID(String WALLETID) {
            this.WALLETID = WALLETID;
        }

        /** 
         * Get the 'WALLET_TYPE' element value.
         * 
         * @return value
         */
        public String getWALLETTYPE() {
            return WALLETTYPE;
        }

        /** 
         * Set the 'WALLET_TYPE' element value.
         * 
         * @param WALLETTYPE
         */
        public void setWALLETTYPE(String WALLETTYPE) {
            this.WALLETTYPE = WALLETTYPE;
        }

        /** 
         * Get the 'BE_ACCT_ENGINE_ID' element value.
         * 
         * @return value
         */
        public String getBEACCTENGINEID() {
            return BEACCTENGINEID;
        }

        /** 
         * Set the 'BE_ACCT_ENGINE_ID' element value.
         * 
         * @param BEACCTENGINEID
         */
        public void setBEACCTENGINEID(String BEACCTENGINEID) {
            this.BEACCTENGINEID = BEACCTENGINEID;
        }
    }
    /** 
     * Schema fragment(s) for this class:
     * <pre>
     * &lt;xs:element xmlns:xs="http://www.w3.org/2001/XMLSchema" name="schemasubscriberprofiledump_info" minOccurs="0" maxOccurs="unbounded">
     *   &lt;xs:complexType>
     *     &lt;xs:all>
     *       &lt;xs:element type="xs:string" name="MSISDN"/>
     *       &lt;xs:element type="xs:string" name="Language_ID"/>
     *       &lt;xs:element type="xs:string" name="IMEI"/>
     *       &lt;xs:element type="xs:string" name="IMSI"/>
     *       &lt;xs:element type="xs:string" name="HLRAddress"/>
     *       &lt;xs:element type="xs:string" name="Wallet_Type"/>
     *       &lt;xs:element type="xs:string" name="BstrVceIntNumTree"/>
     *       &lt;xs:element type="xs:string" name="BstrVceNatNumTree"/>
     *       &lt;xs:element type="xs:string" name="PeriodicChargeStatus"/>
     *       &lt;xs:element type="xs:string" name="CSBAR"/>
     *       &lt;xs:element type="xs:string" name="crbtActReqDate"/>
     *       &lt;xs:element type="xs:string" name="crtCnfrmd"/>
     *       &lt;xs:element type="xs:string" name="crbtActConfirmDate"/>
     *       &lt;xs:element type="xs:string" name="crbtDeact"/>
     *       &lt;xs:element type="xs:string" name="crbtDeActReqDate"/>
     *       &lt;xs:element type="xs:string" name="crbtDeActConfirmDate"/>
     *       &lt;xs:element type="xs:string" name="cbActConfirmDate"/>
     *       &lt;xs:element type="xs:string" name="cbExpDate"/>
     *       &lt;xs:element type="xs:string" name="cbCnfdAct"/>
     *       &lt;xs:element type="xs:string" name="cbWarn1"/>
     *       &lt;xs:element type="xs:string" name="cbWarn2"/>
     *       &lt;xs:element type="xs:string" name="cbDeActReqDate"/>
     *       &lt;xs:element type="xs:string" name="cbDeActConfirmDate"/>
     *       &lt;xs:element type="xs:string" name="cfActConfirmDate"/>
     *       &lt;xs:element type="xs:string" name="cfConfActive"/>
     *       &lt;xs:element type="xs:string" name="cfDeActConfirmDate"/>
     *       &lt;xs:element type="xs:string" name="cfDeActReqDate"/>
     *       &lt;xs:element type="xs:string" name="cfLastFailedDate"/>
     *       &lt;xs:element type="xs:string" name="cfActReqDate"/>
     *       &lt;xs:element type="xs:string" name="avmActConfirmDate"/>
     *       &lt;xs:element type="xs:string" name="avmCnfrmd"/>
     *       &lt;xs:element type="xs:string" name="avmDeActReqDate"/>
     *       &lt;xs:element type="xs:string" name="avmDeActConfirmDate"/>
     *       &lt;xs:element type="xs:string" name="SprssNtfctn"/>
     *       &lt;xs:element type="xs:string" name="avm"/>
     *       &lt;xs:element type="xs:string" name="avmActReqDate"/>
     *       &lt;xs:element type="xs:string" name="MssdCallNot"/>
     *       &lt;xs:element type="xs:string" name="VMCIMEI"/>
     *       &lt;xs:element type="xs:string" name="VMCName"/>
     *       &lt;xs:element type="xs:string" name="AcctActiveMonth"/>
     *       &lt;xs:element type="xs:string" name="AcctActiveYear"/>
     *       &lt;xs:element type="xs:string" name="TopXCountr1"/>
     *       &lt;xs:element type="xs:string" name="TopXCountr2"/>
     *       &lt;xs:element type="xs:string" name="TopXCountr3"/>
     *       &lt;xs:element type="xs:string" name="TopXCountr4"/>
     *       &lt;xs:element type="xs:string" name="TopXCountr5"/>
     *       &lt;xs:element type="xs:string" name="Prepaid"/>
     *       &lt;xs:element type="xs:string" name="entBsnssCrclActv"/>
     *       &lt;xs:element type="xs:string" name="BusMobPayg50"/>
     *       &lt;xs:element type="xs:string" name="BusMobTopUp"/>
     *       &lt;xs:element type="xs:string" name="MercuryOptInOutDate"/>
     *       &lt;xs:element type="xs:string" name="HardCapLabel"/>
     *       &lt;xs:element type="xs:string" name="HardCapAddonLabel"/>
     *       &lt;xs:element type="xs:string" name="Active"/>
     *       &lt;xs:element type="xs:string" name="Absher"/>
     *       &lt;xs:element type="xs:string" name="avmInfSub"/>
     *       &lt;xs:element type="xs:string" name="avmPin"/>
     *       &lt;xs:element type="xs:string" name="avmProf"/>
     *       &lt;xs:element type="xs:string" name="BronzeMPromoActDate"/>
     *       &lt;xs:element type="xs:string" name="BronzeMPromoDeactDate"/>
     *       &lt;xs:element type="xs:string" name="BstrVceIntNumAct"/>
     *       &lt;xs:element type="xs:string" name="BstrVceNatNumAct"/>
     *       &lt;xs:element type="xs:string" name="BstrVINActReqDate"/>
     *       &lt;xs:element type="xs:string" name="BstrVINConfirmDate"/>
     *       &lt;xs:element type="xs:string" name="BstrVINDeActConfDate"/>
     *       &lt;xs:element type="xs:string" name="BstrVINDeActReqDate"/>
     *       &lt;xs:element type="xs:string" name="BstrVINRecur"/>
     *       &lt;xs:element type="xs:string" name="BstrVNNActReqDate"/>
     *       &lt;xs:element type="xs:string" name="BstrVNNConfirmDate"/>
     *       &lt;xs:element type="xs:string" name="BstrVNNDeActConfDate"/>
     *       &lt;xs:element type="xs:string" name="BstrVNNDeActReqDate"/>
     *       &lt;xs:element type="xs:string" name="BstrVNNRecur"/>
     *       &lt;xs:element type="xs:string" name="cbActReqDate"/>
     *       &lt;xs:element type="xs:string" name="cbAll"/>
     *       &lt;xs:element type="xs:string" name="cbIncom"/>
     *       &lt;xs:element type="xs:string" name="cbInt"/>
     *       &lt;xs:element type="xs:string" name="cbIntExc"/>
     *       &lt;xs:element type="xs:string" name="cbWR"/>
     *       &lt;xs:element type="xs:string" name="cfBusy"/>
     *       &lt;xs:element type="xs:string" name="cfData"/>
     *       &lt;xs:element type="xs:string" name="cfFax"/>
     *       &lt;xs:element type="xs:string" name="cfInt"/>
     *       &lt;xs:element type="xs:string" name="cfNoReply"/>
     *       &lt;xs:element type="xs:string" name="cfNotReach"/>
     *       &lt;xs:element type="xs:string" name="cfVoice"/>
     *       &lt;xs:element type="xs:string" name="clir"/>
     *       &lt;xs:element type="xs:string" name="cllrRngTne"/>
     *       &lt;xs:element type="xs:string" name="CVM"/>
     *       &lt;xs:element type="xs:string" name="CVMCounter"/>
     *       &lt;xs:element type="xs:string" name="dataGeneric"/>
     *       &lt;xs:element type="xs:string" name="Date1"/>
     *       &lt;xs:element type="xs:string" name="DisabilityActivationDate"/>
     *       &lt;xs:element type="xs:string" name="DisabilityDectivationDate"/>
     *       &lt;xs:element type="xs:string" name="EmiratiPlan"/>
     *       &lt;xs:element type="xs:string" name="EntCust"/>
     *       &lt;xs:element type="xs:string" name="entMssgingActv"/>
     *       &lt;xs:element type="xs:string" name="faxGroup3"/>
     *       &lt;xs:element type="xs:string" name="GlobalZoneOptIn"/>
     *       &lt;xs:element type="xs:string" name="GlobalZoneOptOut"/>
     *       &lt;xs:element type="xs:string" name="gprsPmail"/>
     *       &lt;xs:element type="xs:string" name="IDD2Act"/>
     *       &lt;xs:element type="xs:string" name="IDD2ActDate"/>
     *       &lt;xs:element type="xs:string" name="IDD2DeactDate"/>
     *       &lt;xs:element type="xs:string" name="IDDCutRateAct"/>
     *       &lt;xs:element type="xs:string" name="IDDCutRateActDate"/>
     *       &lt;xs:element type="xs:string" name="InternationalMin"/>
     *       &lt;xs:element type="xs:string" name="Language"/>
     *       &lt;xs:element type="xs:string" name="odbAll"/>
     *       &lt;xs:element type="xs:string" name="odbIncom"/>
     *       &lt;xs:element type="xs:string" name="odbInt"/>
     *       &lt;xs:element type="xs:string" name="odbIntExc"/>
     *       &lt;xs:element type="xs:string" name="odbOnlyVoice"/>
     *       &lt;xs:element type="xs:string" name="odbOutVoice"/>
     *       &lt;xs:element type="xs:string" name="odbWR"/>
     *       &lt;xs:element type="xs:string" name="PAYGDataFNL"/>
     *       &lt;xs:element type="xs:string" name="PAYGMet"/>
     *       &lt;xs:element type="xs:string" name="PCNGlobalOptin"/>
     *       &lt;xs:element type="xs:string" name="PCNGlobalOptout"/>
     *       &lt;xs:element type="xs:string" name="Plan"/>
     *       &lt;xs:element type="xs:string" name="ReceivePostCallSMS"/>
     *       &lt;xs:element type="xs:string" name="SocialBndlActChannel"/>
     *       &lt;xs:element type="xs:string" name="SocialBndlDeActChannel"/>
     *       &lt;xs:element type="xs:string" name="BstrVceIntNumExp"/>
     *       &lt;xs:element type="xs:string" name="BstrVceNatNumExp"/>
     *       &lt;xs:element type="xs:string" name="BstrVINChngsAllwd"/>
     *       &lt;xs:element type="xs:string" name="BstrVNNChngsAllwd"/>
     *       &lt;xs:element type="xs:string" name="TranslatedNumber"/>
     *       &lt;xs:element type="xs:string" name="TransNumber"/>
     *       &lt;xs:element type="xs:string" name="SmsBndl1Recur"/>
     *       &lt;xs:element type="xs:string" name="SmsExpDate"/>
     *       &lt;xs:element type="xs:string" name="SmsBndl2Recur"/>
     *       &lt;xs:element type="xs:string" name="PriceShout"/>
     *       &lt;xs:element type="xs:string" name="BlckBrryAct"/>
     *       &lt;xs:element type="xs:string" name="BlckBrryCnfdAct"/>
     *       &lt;xs:element type="xs:string" name="BlckBrryActCnfrmDate"/>
     *       &lt;xs:element type="xs:string" name="BlckBrryExpDate"/>
     *       &lt;xs:element type="xs:string" name="SmsBndl3Recur"/>
     *       &lt;xs:element type="xs:string" name="Bespoke"/>
     *       &lt;xs:element type="xs:string" name="ManRenDateLess1Y"/>
     *       &lt;xs:element type="xs:string" name="DataBndl1Recur"/>
     *       &lt;xs:element type="xs:string" name="DataBndlType"/>
     *       &lt;xs:element type="xs:string" name="DataExpDate"/>
     *       &lt;xs:element type="xs:string" name="DataGraceAct"/>
     *       &lt;xs:element type="xs:string" name="DataGraceEnd"/>
     *       &lt;xs:element type="xs:string" name="DataBndl2Recur"/>
     *       &lt;xs:element type="xs:string" name="DataBndl3Recur"/>
     *       &lt;xs:element type="xs:string" name="DataBndl4Recur"/>
     *       &lt;xs:element type="xs:string" name="DataBndl5Recur"/>
     *       &lt;xs:element type="xs:string" name="DataBndl6Recur"/>
     *       &lt;xs:element type="xs:string" name="DataBndl8Recur"/>
     *       &lt;xs:element type="xs:string" name="DataBndl9Recur"/>
     *       &lt;xs:element type="xs:string" name="MBB"/>
     *       &lt;xs:element type="xs:string" name="MBBExpDate"/>
     *       &lt;xs:element type="xs:string" name="MBBUnlimited"/>
     *       &lt;xs:element type="xs:string" name="MBBUnlimExpDate"/>
     *       &lt;xs:element type="xs:string" name="MBB2GB"/>
     *       &lt;xs:element type="xs:string" name="MBB2GBExpDate"/>
     *       &lt;xs:element type="xs:string" name="MBB10GB"/>
     *       &lt;xs:element type="xs:string" name="MBB10GBExpDate"/>
     *       &lt;xs:element type="xs:string" name="MBBGraceAct"/>
     *       &lt;xs:element type="xs:string" name="MBBGraceEnd"/>
     *       &lt;xs:element type="xs:string" name="MBBOfferExpDate"/>
     *       &lt;xs:element type="xs:string" name="TP_Social_Deact_Conf"/>
     *       &lt;xs:element type="xs:string" name="cbAct"/>
     *       &lt;xs:element type="xs:string" name="PokeSMSOptoutDate"/>
     *       &lt;xs:element type="xs:string" name="PokeSMSOptinDate"/>
     *       &lt;xs:element type="xs:string" name="BlckBrryActReqDate"/>
     *       &lt;xs:element type="xs:string" name="BlckBrryDeActReqDate"/>
     *       &lt;xs:element type="xs:string" name="BlckBrryDeactRbkDate"/>
     *       &lt;xs:element type="xs:string" name="BronzeDeactDate"/>
     *       &lt;xs:element type="xs:string" name="BronzeActDate"/>
     *       &lt;xs:element type="xs:string" name="umsCnfrmd"/>
     *       &lt;xs:element type="xs:string" name="umsInfSub"/>
     *       &lt;xs:element type="xs:string" name="ums"/>
     *       &lt;xs:element type="xs:string" name="umsF2M"/>
     *       &lt;xs:element type="xs:string" name="umsSMS2F"/>
     *       &lt;xs:element type="xs:string" name="umsVm2MMS"/>
     *       &lt;xs:element type="xs:string" name="TravSumOptInDate"/>
     *       &lt;xs:element type="xs:string" name="PassOptInDate"/>
     *       &lt;xs:element type="xs:string" name="PassOptOutDate"/>
     *       &lt;xs:element type="xs:string" name="TwinSIMNumber"/>
     *       &lt;xs:element type="xs:string" name="FstTimeActivation"/>
     *       &lt;xs:element type="xs:string" name="Anniversarytopuppromo"/>
     *       &lt;xs:element type="xs:string" name="AnniversarytopuppromoEnd"/>
     *       &lt;xs:element type="xs:string" name="SmartPhoneExpiry"/>
     *       &lt;xs:element type="xs:string" name="DSP_Promo"/>
     *       &lt;xs:element type="xs:string" name="guiBalAdjDate"/>
     *       &lt;xs:element type="xs:string" name="guiBalAdjVal"/>
     *       &lt;xs:element type="xs:string" name="BBBundName"/>
     *       &lt;xs:element type="xs:string" name="BBPromo"/>
     *       &lt;xs:element type="xs:string" name="BSCSAct"/>
     *       &lt;xs:element type="xs:string" name="BBPromoSubDate"/>
     *       &lt;xs:element type="xs:string" name="BBPromoEndDate"/>
     *       &lt;xs:element type="xs:string" name="BlckBrrySprssSMS"/>
     *       &lt;xs:element type="xs:string" name="BlckBrryDeActCnfrmDt"/>
     *       &lt;xs:element type="xs:string" name="Payg_Life_Optin"/>
     *       &lt;xs:element type="xs:string" name="gprsBasicSer"/>
     *       &lt;xs:element type="xs:string" name="gprsWap"/>
     *       &lt;xs:element type="xs:string" name="smsMtPp"/>
     *       &lt;xs:element type="xs:string" name="gprsMms"/>
     *       &lt;xs:element type="xs:string" name="smsMoPp"/>
     *       &lt;xs:element type="xs:string" name="callhold"/>
     *       &lt;xs:element type="xs:string" name="callwait"/>
     *       &lt;xs:element type="xs:string" name="multiParty"/>
     *       &lt;xs:element type="xs:string" name="IntRoam"/>
     *       &lt;xs:element type="xs:string" name="clip"/>
     *       &lt;xs:element type="xs:string" name="VdioCall"/>
     *       &lt;xs:element type="xs:string" name="DuCS"/>
     *       &lt;xs:element type="xs:string" name="BSCS_account_num"/>
     *       &lt;xs:element type="xs:string" name="CS_notification_num"/>
     *       &lt;xs:element type="xs:string" name="cbGraceAct"/>
     *       &lt;xs:element type="xs:string" name="cbGraceEnd"/>
     *       &lt;xs:element type="xs:string" name="cbGraceRenew"/>
     *       &lt;xs:element type="xs:string" name="gprsCarrier"/>
     *       &lt;xs:element type="xs:string" name="PAYGDataLineOffer"/>
     *       &lt;xs:element type="xs:string" name="PAYGDataWHSP"/>
     *       &lt;xs:element type="xs:string" name="PAYGDataWH"/>
     *       &lt;xs:element type="xs:string" name="msisdnChgDate"/>
     *       &lt;xs:element type="xs:string" name="mainMSISDN"/>
     *       &lt;xs:element type="xs:string" name="CashReturn"/>
     *       &lt;xs:element type="xs:string" name="NewTopUpValue"/>
     *       &lt;xs:element type="xs:string" name="bdgtCntrlTopUp"/>
     *       &lt;xs:element type="xs:string" name="BlckBrryBundle"/>
     *       &lt;xs:element type="xs:string" name="MBB10GBWelcome"/>
     *       &lt;xs:element type="xs:string" name="MBB2GBWelcome"/>
     *       &lt;xs:element type="xs:string" name="MBBUnlimWelcome"/>
     *       &lt;xs:element type="xs:string" name="MBBWelcome"/>
     *       &lt;xs:element type="xs:string" name="guiBalAdjCount"/>
     *       &lt;xs:element type="xs:string" name="LastChangeDateExpiry"/>
     *       &lt;xs:element type="xs:string" name="NewPPBundle"/>
     *       &lt;xs:element type="xs:string" name="SmsBndlType"/>
     *       &lt;xs:element type="xs:string" name="PCN1stCall"/>
     *       &lt;xs:element type="xs:string" name="RLHAct_ReqDate"/>
     *       &lt;xs:element type="xs:string" name="RLHDeact_Date"/>
     *       &lt;xs:element type="xs:string" name="entPDIntNum"/>
     *       &lt;xs:element type="xs:string" name="AloRchgBonusOptinDt"/>
     *       &lt;xs:element type="xs:string" name="Customer_ID"/>
     *       &lt;xs:element type="xs:string" name="IDDBundleActDate"/>
     *       &lt;xs:element type="xs:string" name="ICPBundle"/>
     *       &lt;xs:element type="xs:string" name="Mercury2DOptinoutDate"/>
     *       &lt;xs:element type="xs:string" name="Mercury2MOptinoutDate"/>
     *       &lt;xs:element type="xs:string" name="FamSponsr"/>
     *       &lt;xs:element type="xs:string" name="FamsponsorEtisalat"/>
     *       &lt;xs:element type="xs:string" name="NPPSocBndlDAPAct"/>
     *       &lt;xs:element type="xs:string" name="NPPBISDAPMod"/>
     *       &lt;xs:element type="xs:string" name="NPPBBSocialDAPMod"/>
     *       &lt;xs:element type="xs:string" name="NPPBBSocialDAPDeact"/>
     *       &lt;xs:element type="xs:string" name="MercuryDOptinDate"/>
     *       &lt;xs:element type="xs:string" name="MercuryDOptoutDate"/>
     *       &lt;xs:element type="xs:string" name="CVMDeactDate"/>
     *       &lt;xs:element type="xs:string" name="IDDRateCuttersOptinDate"/>
     *       &lt;xs:element type="xs:string" name="IDDRateCuttersOptoutDate"/>
     *       &lt;xs:element type="xs:string" name="NonstopSocActDate"/>
     *       &lt;xs:element type="xs:string" name="NonstopSocDeactDate"/>
     *       &lt;xs:element type="xs:string" name="OWFnFLabel"/>
     *       &lt;xs:element type="xs:string" name="OneWayFnF"/>
     *       &lt;xs:element type="xs:string" name="OneWayFnFModCount"/>
     *       &lt;xs:element type="xs:string" name="AutoRechargeValue"/>
     *     &lt;/xs:all>
     *   &lt;/xs:complexType>
     * &lt;/xs:element>
     * </pre>
     */
    public static class SchemasubscriberprofiledumpInfo
    {
        private String MSISDN;
        private String languageID;
        private String IMEI;
        private String IMSI;
        private String HLRAddress;
        private String walletType;
        private String bstrVceIntNumTree;
        private String bstrVceNatNumTree;
        private String periodicChargeStatus;
        private String CSBAR;
        private String crbtActReqDate;
        private String crtCnfrmd;
        private String crbtActConfirmDate;
        private String crbtDeact;
        private String crbtDeActReqDate;
        private String crbtDeActConfirmDate;
        private String cbActConfirmDate;
        private String cbExpDate;
        private String cbCnfdAct;
        private String cbWarn1;
        private String cbWarn2;
        private String cbDeActReqDate;
        private String cbDeActConfirmDate;
        private String cfActConfirmDate;
        private String cfConfActive;
        private String cfDeActConfirmDate;
        private String cfDeActReqDate;
        private String cfLastFailedDate;
        private String cfActReqDate;
        private String avmActConfirmDate;
        private String avmCnfrmd;
        private String avmDeActReqDate;
        private String avmDeActConfirmDate;
        private String sprssNtfctn;
        private String avm;
        private String avmActReqDate;
        private String mssdCallNot;
        private String VMCIMEI;
        private String VMCName;
        private String acctActiveMonth;
        private String acctActiveYear;
        private String topXCountr1;
        private String topXCountr2;
        private String topXCountr3;
        private String topXCountr4;
        private String topXCountr5;
        private String prepaid;
        private String entBsnssCrclActv;
        private String busMobPayg50;
        private String busMobTopUp;
        private String mercuryOptInOutDate;
        private String hardCapLabel;
        private String hardCapAddonLabel;
        private String active;
        private String absher;
        private String avmInfSub;
        private String avmPin;
        private String avmProf;
        private String bronzeMPromoActDate;
        private String bronzeMPromoDeactDate;
        private String bstrVceIntNumAct;
        private String bstrVceNatNumAct;
        private String bstrVINActReqDate;
        private String bstrVINConfirmDate;
        private String bstrVINDeActConfDate;
        private String bstrVINDeActReqDate;
        private String bstrVINRecur;
        private String bstrVNNActReqDate;
        private String bstrVNNConfirmDate;
        private String bstrVNNDeActConfDate;
        private String bstrVNNDeActReqDate;
        private String bstrVNNRecur;
        private String cbActReqDate;
        private String cbAll;
        private String cbIncom;
        private String cbInt;
        private String cbIntExc;
        private String cbWR;
        private String cfBusy;
        private String cfData;
        private String cfFax;
        private String cfInt;
        private String cfNoReply;
        private String cfNotReach;
        private String cfVoice;
        private String clir;
        private String cllrRngTne;
        private String CVM;
        private String CVMCounter;
        private String dataGeneric;
        private String date1;
        private String disabilityActivationDate;
        private String disabilityDectivationDate;
        private String emiratiPlan;
        private String entCust;
        private String entMssgingActv;
        private String faxGroup3;
        private String globalZoneOptIn;
        private String globalZoneOptOut;
        private String gprsPmail;
        private String IDD2Act;
        private String IDD2ActDate;
        private String IDD2DeactDate;
        private String IDDCutRateAct;
        private String IDDCutRateActDate;
        private String internationalMin;
        private String language;
        private String odbAll;
        private String odbIncom;
        private String odbInt;
        private String odbIntExc;
        private String odbOnlyVoice;
        private String odbOutVoice;
        private String odbWR;
        private String PAYGDataFNL;
        private String PAYGMet;
        private String PCNGlobalOptin;
        private String PCNGlobalOptout;
        private String plan;
        private String receivePostCallSMS;
        private String socialBndlActChannel;
        private String socialBndlDeActChannel;
        private String bstrVceIntNumExp;
        private String bstrVceNatNumExp;
        private String bstrVINChngsAllwd;
        private String bstrVNNChngsAllwd;
        private String translatedNumber;
        private String transNumber;
        private String smsBndl1Recur;
        private String smsExpDate;
        private String smsBndl2Recur;
        private String priceShout;
        private String blckBrryAct;
        private String blckBrryCnfdAct;
        private String blckBrryActCnfrmDate;
        private String blckBrryExpDate;
        private String smsBndl3Recur;
        private String bespoke;
        private String manRenDateLess1Y;
        private String dataBndl1Recur;
        private String dataBndlType;
        private String dataExpDate;
        private String dataGraceAct;
        private String dataGraceEnd;
        private String dataBndl2Recur;
        private String dataBndl3Recur;
        private String dataBndl4Recur;
        private String dataBndl5Recur;
        private String dataBndl6Recur;
        private String dataBndl8Recur;
        private String dataBndl9Recur;
        private String MBB;
        private String MBBExpDate;
        private String MBBUnlimited;
        private String MBBUnlimExpDate;
        private String MBB2GB;
        private String MBB2GBExpDate;
        private String MBB10GB;
        private String MBB10GBExpDate;
        private String MBBGraceAct;
        private String MBBGraceEnd;
        private String MBBOfferExpDate;
        private String TPSocialDeactConf;
        private String cbAct;
        private String pokeSMSOptoutDate;
        private String pokeSMSOptinDate;
        private String blckBrryActReqDate;
        private String blckBrryDeActReqDate;
        private String blckBrryDeactRbkDate;
        private String bronzeDeactDate;
        private String bronzeActDate;
        private String umsCnfrmd;
        private String umsInfSub;
        private String ums;
        private String umsF2M;
        private String umsSMS2F;
        private String umsVm2MMS;
        private String travSumOptInDate;
        private String passOptInDate;
        private String passOptOutDate;
        private String twinSIMNumber;
        private String fstTimeActivation;
        private String anniversarytopuppromo;
        private String anniversarytopuppromoEnd;
        private String smartPhoneExpiry;
        private String DSPPromo;
        private String guiBalAdjDate;
        private String guiBalAdjVal;
        private String BBBundName;
        private String BBPromo;
        private String BSCSAct;
        private String BBPromoSubDate;
        private String BBPromoEndDate;
        private String blckBrrySprssSMS;
        private String blckBrryDeActCnfrmDt;
        private String paygLifeOptin;
        private String gprsBasicSer;
        private String gprsWap;
        private String smsMtPp;
        private String gprsMms;
        private String smsMoPp;
        private String callhold;
        private String callwait;
        private String multiParty;
        private String intRoam;
        private String clip;
        private String vdioCall;
        private String duCS;
        private String BSCSAccountNum;
        private String CSNotificationNum;
        private String cbGraceAct;
        private String cbGraceEnd;
        private String cbGraceRenew;
        private String gprsCarrier;
        private String PAYGDataLineOffer;
        private String PAYGDataWHSP;
        private String PAYGDataWH;
        private String msisdnChgDate;
        private String mainMSISDN;
        private String cashReturn;
        private String newTopUpValue;
        private String bdgtCntrlTopUp;
        private String blckBrryBundle;
        private String MBB10GBWelcome;
        private String MBB2GBWelcome;
        private String MBBUnlimWelcome;
        private String MBBWelcome;
        private String guiBalAdjCount;
        private String lastChangeDateExpiry;
        private String newPPBundle;
        private String smsBndlType;
        private String PCN1stCall;
        private String RLHActReqDate;
        private String RLHDeactDate;
        private String entPDIntNum;
        private String aloRchgBonusOptinDt;
        private String customerID;
        private String IDDBundleActDate;
        private String ICPBundle;
        private String mercury2DOptinoutDate;
        private String mercury2MOptinoutDate;
        private String famSponsr;
        private String famsponsorEtisalat;
        private String NPPSocBndlDAPAct;
        private String NPPBISDAPMod;
        private String NPPBBSocialDAPMod;
        private String NPPBBSocialDAPDeact;
        private String mercuryDOptinDate;
        private String mercuryDOptoutDate;
        private String CVMDeactDate;
        private String IDDRateCuttersOptinDate;
        private String IDDRateCuttersOptoutDate;
        private String nonstopSocActDate;
        private String nonstopSocDeactDate;
        private String OWFnFLabel;
        private String oneWayFnF;
        private String oneWayFnFModCount;
        private String autoRechargeValue;

        /** 
         * Get the 'MSISDN' element value.
         * 
         * @return value
         */
        public String getMSISDN() {
            return MSISDN;
        }

        /** 
         * Set the 'MSISDN' element value.
         * 
         * @param MSISDN
         */
        public void setMSISDN(String MSISDN) {
            this.MSISDN = MSISDN;
        }

        /** 
         * Get the 'Language_ID' element value.
         * 
         * @return value
         */
        public String getLanguageID() {
            return languageID;
        }

        /** 
         * Set the 'Language_ID' element value.
         * 
         * @param languageID
         */
        public void setLanguageID(String languageID) {
            this.languageID = languageID;
        }

        /** 
         * Get the 'IMEI' element value.
         * 
         * @return value
         */
        public String getIMEI() {
            return IMEI;
        }

        /** 
         * Set the 'IMEI' element value.
         * 
         * @param IMEI
         */
        public void setIMEI(String IMEI) {
            this.IMEI = IMEI;
        }

        /** 
         * Get the 'IMSI' element value.
         * 
         * @return value
         */
        public String getIMSI() {
            return IMSI;
        }

        /** 
         * Set the 'IMSI' element value.
         * 
         * @param IMSI
         */
        public void setIMSI(String IMSI) {
            this.IMSI = IMSI;
        }

        /** 
         * Get the 'HLRAddress' element value.
         * 
         * @return value
         */
        public String getHLRAddress() {
            return HLRAddress;
        }

        /** 
         * Set the 'HLRAddress' element value.
         * 
         * @param HLRAddress
         */
        public void setHLRAddress(String HLRAddress) {
            this.HLRAddress = HLRAddress;
        }

        /** 
         * Get the 'Wallet_Type' element value.
         * 
         * @return value
         */
        public String getWalletType() {
            return walletType;
        }

        /** 
         * Set the 'Wallet_Type' element value.
         * 
         * @param walletType
         */
        public void setWalletType(String walletType) {
            this.walletType = walletType;
        }

        /** 
         * Get the 'BstrVceIntNumTree' element value.
         * 
         * @return value
         */
        public String getBstrVceIntNumTree() {
            return bstrVceIntNumTree;
        }

        /** 
         * Set the 'BstrVceIntNumTree' element value.
         * 
         * @param bstrVceIntNumTree
         */
        public void setBstrVceIntNumTree(String bstrVceIntNumTree) {
            this.bstrVceIntNumTree = bstrVceIntNumTree;
        }

        /** 
         * Get the 'BstrVceNatNumTree' element value.
         * 
         * @return value
         */
        public String getBstrVceNatNumTree() {
            return bstrVceNatNumTree;
        }

        /** 
         * Set the 'BstrVceNatNumTree' element value.
         * 
         * @param bstrVceNatNumTree
         */
        public void setBstrVceNatNumTree(String bstrVceNatNumTree) {
            this.bstrVceNatNumTree = bstrVceNatNumTree;
        }

        /** 
         * Get the 'PeriodicChargeStatus' element value.
         * 
         * @return value
         */
        public String getPeriodicChargeStatus() {
            return periodicChargeStatus;
        }

        /** 
         * Set the 'PeriodicChargeStatus' element value.
         * 
         * @param periodicChargeStatus
         */
        public void setPeriodicChargeStatus(String periodicChargeStatus) {
            this.periodicChargeStatus = periodicChargeStatus;
        }

        /** 
         * Get the 'CSBAR' element value.
         * 
         * @return value
         */
        public String getCSBAR() {
            return CSBAR;
        }

        /** 
         * Set the 'CSBAR' element value.
         * 
         * @param CSBAR
         */
        public void setCSBAR(String CSBAR) {
            this.CSBAR = CSBAR;
        }

        /** 
         * Get the 'crbtActReqDate' element value.
         * 
         * @return value
         */
        public String getCrbtActReqDate() {
            return crbtActReqDate;
        }

        /** 
         * Set the 'crbtActReqDate' element value.
         * 
         * @param crbtActReqDate
         */
        public void setCrbtActReqDate(String crbtActReqDate) {
            this.crbtActReqDate = crbtActReqDate;
        }

        /** 
         * Get the 'crtCnfrmd' element value.
         * 
         * @return value
         */
        public String getCrtCnfrmd() {
            return crtCnfrmd;
        }

        /** 
         * Set the 'crtCnfrmd' element value.
         * 
         * @param crtCnfrmd
         */
        public void setCrtCnfrmd(String crtCnfrmd) {
            this.crtCnfrmd = crtCnfrmd;
        }

        /** 
         * Get the 'crbtActConfirmDate' element value.
         * 
         * @return value
         */
        public String getCrbtActConfirmDate() {
            return crbtActConfirmDate;
        }

        /** 
         * Set the 'crbtActConfirmDate' element value.
         * 
         * @param crbtActConfirmDate
         */
        public void setCrbtActConfirmDate(String crbtActConfirmDate) {
            this.crbtActConfirmDate = crbtActConfirmDate;
        }

        /** 
         * Get the 'crbtDeact' element value.
         * 
         * @return value
         */
        public String getCrbtDeact() {
            return crbtDeact;
        }

        /** 
         * Set the 'crbtDeact' element value.
         * 
         * @param crbtDeact
         */
        public void setCrbtDeact(String crbtDeact) {
            this.crbtDeact = crbtDeact;
        }

        /** 
         * Get the 'crbtDeActReqDate' element value.
         * 
         * @return value
         */
        public String getCrbtDeActReqDate() {
            return crbtDeActReqDate;
        }

        /** 
         * Set the 'crbtDeActReqDate' element value.
         * 
         * @param crbtDeActReqDate
         */
        public void setCrbtDeActReqDate(String crbtDeActReqDate) {
            this.crbtDeActReqDate = crbtDeActReqDate;
        }

        /** 
         * Get the 'crbtDeActConfirmDate' element value.
         * 
         * @return value
         */
        public String getCrbtDeActConfirmDate() {
            return crbtDeActConfirmDate;
        }

        /** 
         * Set the 'crbtDeActConfirmDate' element value.
         * 
         * @param crbtDeActConfirmDate
         */
        public void setCrbtDeActConfirmDate(String crbtDeActConfirmDate) {
            this.crbtDeActConfirmDate = crbtDeActConfirmDate;
        }

        /** 
         * Get the 'cbActConfirmDate' element value.
         * 
         * @return value
         */
        public String getCbActConfirmDate() {
            return cbActConfirmDate;
        }

        /** 
         * Set the 'cbActConfirmDate' element value.
         * 
         * @param cbActConfirmDate
         */
        public void setCbActConfirmDate(String cbActConfirmDate) {
            this.cbActConfirmDate = cbActConfirmDate;
        }

        /** 
         * Get the 'cbExpDate' element value.
         * 
         * @return value
         */
        public String getCbExpDate() {
            return cbExpDate;
        }

        /** 
         * Set the 'cbExpDate' element value.
         * 
         * @param cbExpDate
         */
        public void setCbExpDate(String cbExpDate) {
            this.cbExpDate = cbExpDate;
        }

        /** 
         * Get the 'cbCnfdAct' element value.
         * 
         * @return value
         */
        public String getCbCnfdAct() {
            return cbCnfdAct;
        }

        /** 
         * Set the 'cbCnfdAct' element value.
         * 
         * @param cbCnfdAct
         */
        public void setCbCnfdAct(String cbCnfdAct) {
            this.cbCnfdAct = cbCnfdAct;
        }

        /** 
         * Get the 'cbWarn1' element value.
         * 
         * @return value
         */
        public String getCbWarn1() {
            return cbWarn1;
        }

        /** 
         * Set the 'cbWarn1' element value.
         * 
         * @param cbWarn1
         */
        public void setCbWarn1(String cbWarn1) {
            this.cbWarn1 = cbWarn1;
        }

        /** 
         * Get the 'cbWarn2' element value.
         * 
         * @return value
         */
        public String getCbWarn2() {
            return cbWarn2;
        }

        /** 
         * Set the 'cbWarn2' element value.
         * 
         * @param cbWarn2
         */
        public void setCbWarn2(String cbWarn2) {
            this.cbWarn2 = cbWarn2;
        }

        /** 
         * Get the 'cbDeActReqDate' element value.
         * 
         * @return value
         */
        public String getCbDeActReqDate() {
            return cbDeActReqDate;
        }

        /** 
         * Set the 'cbDeActReqDate' element value.
         * 
         * @param cbDeActReqDate
         */
        public void setCbDeActReqDate(String cbDeActReqDate) {
            this.cbDeActReqDate = cbDeActReqDate;
        }

        /** 
         * Get the 'cbDeActConfirmDate' element value.
         * 
         * @return value
         */
        public String getCbDeActConfirmDate() {
            return cbDeActConfirmDate;
        }

        /** 
         * Set the 'cbDeActConfirmDate' element value.
         * 
         * @param cbDeActConfirmDate
         */
        public void setCbDeActConfirmDate(String cbDeActConfirmDate) {
            this.cbDeActConfirmDate = cbDeActConfirmDate;
        }

        /** 
         * Get the 'cfActConfirmDate' element value.
         * 
         * @return value
         */
        public String getCfActConfirmDate() {
            return cfActConfirmDate;
        }

        /** 
         * Set the 'cfActConfirmDate' element value.
         * 
         * @param cfActConfirmDate
         */
        public void setCfActConfirmDate(String cfActConfirmDate) {
            this.cfActConfirmDate = cfActConfirmDate;
        }

        /** 
         * Get the 'cfConfActive' element value.
         * 
         * @return value
         */
        public String getCfConfActive() {
            return cfConfActive;
        }

        /** 
         * Set the 'cfConfActive' element value.
         * 
         * @param cfConfActive
         */
        public void setCfConfActive(String cfConfActive) {
            this.cfConfActive = cfConfActive;
        }

        /** 
         * Get the 'cfDeActConfirmDate' element value.
         * 
         * @return value
         */
        public String getCfDeActConfirmDate() {
            return cfDeActConfirmDate;
        }

        /** 
         * Set the 'cfDeActConfirmDate' element value.
         * 
         * @param cfDeActConfirmDate
         */
        public void setCfDeActConfirmDate(String cfDeActConfirmDate) {
            this.cfDeActConfirmDate = cfDeActConfirmDate;
        }

        /** 
         * Get the 'cfDeActReqDate' element value.
         * 
         * @return value
         */
        public String getCfDeActReqDate() {
            return cfDeActReqDate;
        }

        /** 
         * Set the 'cfDeActReqDate' element value.
         * 
         * @param cfDeActReqDate
         */
        public void setCfDeActReqDate(String cfDeActReqDate) {
            this.cfDeActReqDate = cfDeActReqDate;
        }

        /** 
         * Get the 'cfLastFailedDate' element value.
         * 
         * @return value
         */
        public String getCfLastFailedDate() {
            return cfLastFailedDate;
        }

        /** 
         * Set the 'cfLastFailedDate' element value.
         * 
         * @param cfLastFailedDate
         */
        public void setCfLastFailedDate(String cfLastFailedDate) {
            this.cfLastFailedDate = cfLastFailedDate;
        }

        /** 
         * Get the 'cfActReqDate' element value.
         * 
         * @return value
         */
        public String getCfActReqDate() {
            return cfActReqDate;
        }

        /** 
         * Set the 'cfActReqDate' element value.
         * 
         * @param cfActReqDate
         */
        public void setCfActReqDate(String cfActReqDate) {
            this.cfActReqDate = cfActReqDate;
        }

        /** 
         * Get the 'avmActConfirmDate' element value.
         * 
         * @return value
         */
        public String getAvmActConfirmDate() {
            return avmActConfirmDate;
        }

        /** 
         * Set the 'avmActConfirmDate' element value.
         * 
         * @param avmActConfirmDate
         */
        public void setAvmActConfirmDate(String avmActConfirmDate) {
            this.avmActConfirmDate = avmActConfirmDate;
        }

        /** 
         * Get the 'avmCnfrmd' element value.
         * 
         * @return value
         */
        public String getAvmCnfrmd() {
            return avmCnfrmd;
        }

        /** 
         * Set the 'avmCnfrmd' element value.
         * 
         * @param avmCnfrmd
         */
        public void setAvmCnfrmd(String avmCnfrmd) {
            this.avmCnfrmd = avmCnfrmd;
        }

        /** 
         * Get the 'avmDeActReqDate' element value.
         * 
         * @return value
         */
        public String getAvmDeActReqDate() {
            return avmDeActReqDate;
        }

        /** 
         * Set the 'avmDeActReqDate' element value.
         * 
         * @param avmDeActReqDate
         */
        public void setAvmDeActReqDate(String avmDeActReqDate) {
            this.avmDeActReqDate = avmDeActReqDate;
        }

        /** 
         * Get the 'avmDeActConfirmDate' element value.
         * 
         * @return value
         */
        public String getAvmDeActConfirmDate() {
            return avmDeActConfirmDate;
        }

        /** 
         * Set the 'avmDeActConfirmDate' element value.
         * 
         * @param avmDeActConfirmDate
         */
        public void setAvmDeActConfirmDate(String avmDeActConfirmDate) {
            this.avmDeActConfirmDate = avmDeActConfirmDate;
        }

        /** 
         * Get the 'SprssNtfctn' element value.
         * 
         * @return value
         */
        public String getSprssNtfctn() {
            return sprssNtfctn;
        }

        /** 
         * Set the 'SprssNtfctn' element value.
         * 
         * @param sprssNtfctn
         */
        public void setSprssNtfctn(String sprssNtfctn) {
            this.sprssNtfctn = sprssNtfctn;
        }

        /** 
         * Get the 'avm' element value.
         * 
         * @return value
         */
        public String getAvm() {
            return avm;
        }

        /** 
         * Set the 'avm' element value.
         * 
         * @param avm
         */
        public void setAvm(String avm) {
            this.avm = avm;
        }

        /** 
         * Get the 'avmActReqDate' element value.
         * 
         * @return value
         */
        public String getAvmActReqDate() {
            return avmActReqDate;
        }

        /** 
         * Set the 'avmActReqDate' element value.
         * 
         * @param avmActReqDate
         */
        public void setAvmActReqDate(String avmActReqDate) {
            this.avmActReqDate = avmActReqDate;
        }

        /** 
         * Get the 'MssdCallNot' element value.
         * 
         * @return value
         */
        public String getMssdCallNot() {
            return mssdCallNot;
        }

        /** 
         * Set the 'MssdCallNot' element value.
         * 
         * @param mssdCallNot
         */
        public void setMssdCallNot(String mssdCallNot) {
            this.mssdCallNot = mssdCallNot;
        }

        /** 
         * Get the 'VMCIMEI' element value.
         * 
         * @return value
         */
        public String getVMCIMEI() {
            return VMCIMEI;
        }

        /** 
         * Set the 'VMCIMEI' element value.
         * 
         * @param VMCIMEI
         */
        public void setVMCIMEI(String VMCIMEI) {
            this.VMCIMEI = VMCIMEI;
        }

        /** 
         * Get the 'VMCName' element value.
         * 
         * @return value
         */
        public String getVMCName() {
            return VMCName;
        }

        /** 
         * Set the 'VMCName' element value.
         * 
         * @param VMCName
         */
        public void setVMCName(String VMCName) {
            this.VMCName = VMCName;
        }

        /** 
         * Get the 'AcctActiveMonth' element value.
         * 
         * @return value
         */
        public String getAcctActiveMonth() {
            return acctActiveMonth;
        }

        /** 
         * Set the 'AcctActiveMonth' element value.
         * 
         * @param acctActiveMonth
         */
        public void setAcctActiveMonth(String acctActiveMonth) {
            this.acctActiveMonth = acctActiveMonth;
        }

        /** 
         * Get the 'AcctActiveYear' element value.
         * 
         * @return value
         */
        public String getAcctActiveYear() {
            return acctActiveYear;
        }

        /** 
         * Set the 'AcctActiveYear' element value.
         * 
         * @param acctActiveYear
         */
        public void setAcctActiveYear(String acctActiveYear) {
            this.acctActiveYear = acctActiveYear;
        }

        /** 
         * Get the 'TopXCountr1' element value.
         * 
         * @return value
         */
        public String getTopXCountr1() {
            return topXCountr1;
        }

        /** 
         * Set the 'TopXCountr1' element value.
         * 
         * @param topXCountr1
         */
        public void setTopXCountr1(String topXCountr1) {
            this.topXCountr1 = topXCountr1;
        }

        /** 
         * Get the 'TopXCountr2' element value.
         * 
         * @return value
         */
        public String getTopXCountr2() {
            return topXCountr2;
        }

        /** 
         * Set the 'TopXCountr2' element value.
         * 
         * @param topXCountr2
         */
        public void setTopXCountr2(String topXCountr2) {
            this.topXCountr2 = topXCountr2;
        }

        /** 
         * Get the 'TopXCountr3' element value.
         * 
         * @return value
         */
        public String getTopXCountr3() {
            return topXCountr3;
        }

        /** 
         * Set the 'TopXCountr3' element value.
         * 
         * @param topXCountr3
         */
        public void setTopXCountr3(String topXCountr3) {
            this.topXCountr3 = topXCountr3;
        }

        /** 
         * Get the 'TopXCountr4' element value.
         * 
         * @return value
         */
        public String getTopXCountr4() {
            return topXCountr4;
        }

        /** 
         * Set the 'TopXCountr4' element value.
         * 
         * @param topXCountr4
         */
        public void setTopXCountr4(String topXCountr4) {
            this.topXCountr4 = topXCountr4;
        }

        /** 
         * Get the 'TopXCountr5' element value.
         * 
         * @return value
         */
        public String getTopXCountr5() {
            return topXCountr5;
        }

        /** 
         * Set the 'TopXCountr5' element value.
         * 
         * @param topXCountr5
         */
        public void setTopXCountr5(String topXCountr5) {
            this.topXCountr5 = topXCountr5;
        }

        /** 
         * Get the 'Prepaid' element value.
         * 
         * @return value
         */
        public String getPrepaid() {
            return prepaid;
        }

        /** 
         * Set the 'Prepaid' element value.
         * 
         * @param prepaid
         */
        public void setPrepaid(String prepaid) {
            this.prepaid = prepaid;
        }

        /** 
         * Get the 'entBsnssCrclActv' element value.
         * 
         * @return value
         */
        public String getEntBsnssCrclActv() {
            return entBsnssCrclActv;
        }

        /** 
         * Set the 'entBsnssCrclActv' element value.
         * 
         * @param entBsnssCrclActv
         */
        public void setEntBsnssCrclActv(String entBsnssCrclActv) {
            this.entBsnssCrclActv = entBsnssCrclActv;
        }

        /** 
         * Get the 'BusMobPayg50' element value.
         * 
         * @return value
         */
        public String getBusMobPayg50() {
            return busMobPayg50;
        }

        /** 
         * Set the 'BusMobPayg50' element value.
         * 
         * @param busMobPayg50
         */
        public void setBusMobPayg50(String busMobPayg50) {
            this.busMobPayg50 = busMobPayg50;
        }

        /** 
         * Get the 'BusMobTopUp' element value.
         * 
         * @return value
         */
        public String getBusMobTopUp() {
            return busMobTopUp;
        }

        /** 
         * Set the 'BusMobTopUp' element value.
         * 
         * @param busMobTopUp
         */
        public void setBusMobTopUp(String busMobTopUp) {
            this.busMobTopUp = busMobTopUp;
        }

        /** 
         * Get the 'MercuryOptInOutDate' element value.
         * 
         * @return value
         */
        public String getMercuryOptInOutDate() {
            return mercuryOptInOutDate;
        }

        /** 
         * Set the 'MercuryOptInOutDate' element value.
         * 
         * @param mercuryOptInOutDate
         */
        public void setMercuryOptInOutDate(String mercuryOptInOutDate) {
            this.mercuryOptInOutDate = mercuryOptInOutDate;
        }

        /** 
         * Get the 'HardCapLabel' element value.
         * 
         * @return value
         */
        public String getHardCapLabel() {
            return hardCapLabel;
        }

        /** 
         * Set the 'HardCapLabel' element value.
         * 
         * @param hardCapLabel
         */
        public void setHardCapLabel(String hardCapLabel) {
            this.hardCapLabel = hardCapLabel;
        }

        /** 
         * Get the 'HardCapAddonLabel' element value.
         * 
         * @return value
         */
        public String getHardCapAddonLabel() {
            return hardCapAddonLabel;
        }

        /** 
         * Set the 'HardCapAddonLabel' element value.
         * 
         * @param hardCapAddonLabel
         */
        public void setHardCapAddonLabel(String hardCapAddonLabel) {
            this.hardCapAddonLabel = hardCapAddonLabel;
        }

        /** 
         * Get the 'Active' element value.
         * 
         * @return value
         */
        public String getActive() {
            return active;
        }

        /** 
         * Set the 'Active' element value.
         * 
         * @param active
         */
        public void setActive(String active) {
            this.active = active;
        }

        /** 
         * Get the 'Absher' element value.
         * 
         * @return value
         */
        public String getAbsher() {
            return absher;
        }

        /** 
         * Set the 'Absher' element value.
         * 
         * @param absher
         */
        public void setAbsher(String absher) {
            this.absher = absher;
        }

        /** 
         * Get the 'avmInfSub' element value.
         * 
         * @return value
         */
        public String getAvmInfSub() {
            return avmInfSub;
        }

        /** 
         * Set the 'avmInfSub' element value.
         * 
         * @param avmInfSub
         */
        public void setAvmInfSub(String avmInfSub) {
            this.avmInfSub = avmInfSub;
        }

        /** 
         * Get the 'avmPin' element value.
         * 
         * @return value
         */
        public String getAvmPin() {
            return avmPin;
        }

        /** 
         * Set the 'avmPin' element value.
         * 
         * @param avmPin
         */
        public void setAvmPin(String avmPin) {
            this.avmPin = avmPin;
        }

        /** 
         * Get the 'avmProf' element value.
         * 
         * @return value
         */
        public String getAvmProf() {
            return avmProf;
        }

        /** 
         * Set the 'avmProf' element value.
         * 
         * @param avmProf
         */
        public void setAvmProf(String avmProf) {
            this.avmProf = avmProf;
        }

        /** 
         * Get the 'BronzeMPromoActDate' element value.
         * 
         * @return value
         */
        public String getBronzeMPromoActDate() {
            return bronzeMPromoActDate;
        }

        /** 
         * Set the 'BronzeMPromoActDate' element value.
         * 
         * @param bronzeMPromoActDate
         */
        public void setBronzeMPromoActDate(String bronzeMPromoActDate) {
            this.bronzeMPromoActDate = bronzeMPromoActDate;
        }

        /** 
         * Get the 'BronzeMPromoDeactDate' element value.
         * 
         * @return value
         */
        public String getBronzeMPromoDeactDate() {
            return bronzeMPromoDeactDate;
        }

        /** 
         * Set the 'BronzeMPromoDeactDate' element value.
         * 
         * @param bronzeMPromoDeactDate
         */
        public void setBronzeMPromoDeactDate(String bronzeMPromoDeactDate) {
            this.bronzeMPromoDeactDate = bronzeMPromoDeactDate;
        }

        /** 
         * Get the 'BstrVceIntNumAct' element value.
         * 
         * @return value
         */
        public String getBstrVceIntNumAct() {
            return bstrVceIntNumAct;
        }

        /** 
         * Set the 'BstrVceIntNumAct' element value.
         * 
         * @param bstrVceIntNumAct
         */
        public void setBstrVceIntNumAct(String bstrVceIntNumAct) {
            this.bstrVceIntNumAct = bstrVceIntNumAct;
        }

        /** 
         * Get the 'BstrVceNatNumAct' element value.
         * 
         * @return value
         */
        public String getBstrVceNatNumAct() {
            return bstrVceNatNumAct;
        }

        /** 
         * Set the 'BstrVceNatNumAct' element value.
         * 
         * @param bstrVceNatNumAct
         */
        public void setBstrVceNatNumAct(String bstrVceNatNumAct) {
            this.bstrVceNatNumAct = bstrVceNatNumAct;
        }

        /** 
         * Get the 'BstrVINActReqDate' element value.
         * 
         * @return value
         */
        public String getBstrVINActReqDate() {
            return bstrVINActReqDate;
        }

        /** 
         * Set the 'BstrVINActReqDate' element value.
         * 
         * @param bstrVINActReqDate
         */
        public void setBstrVINActReqDate(String bstrVINActReqDate) {
            this.bstrVINActReqDate = bstrVINActReqDate;
        }

        /** 
         * Get the 'BstrVINConfirmDate' element value.
         * 
         * @return value
         */
        public String getBstrVINConfirmDate() {
            return bstrVINConfirmDate;
        }

        /** 
         * Set the 'BstrVINConfirmDate' element value.
         * 
         * @param bstrVINConfirmDate
         */
        public void setBstrVINConfirmDate(String bstrVINConfirmDate) {
            this.bstrVINConfirmDate = bstrVINConfirmDate;
        }

        /** 
         * Get the 'BstrVINDeActConfDate' element value.
         * 
         * @return value
         */
        public String getBstrVINDeActConfDate() {
            return bstrVINDeActConfDate;
        }

        /** 
         * Set the 'BstrVINDeActConfDate' element value.
         * 
         * @param bstrVINDeActConfDate
         */
        public void setBstrVINDeActConfDate(String bstrVINDeActConfDate) {
            this.bstrVINDeActConfDate = bstrVINDeActConfDate;
        }

        /** 
         * Get the 'BstrVINDeActReqDate' element value.
         * 
         * @return value
         */
        public String getBstrVINDeActReqDate() {
            return bstrVINDeActReqDate;
        }

        /** 
         * Set the 'BstrVINDeActReqDate' element value.
         * 
         * @param bstrVINDeActReqDate
         */
        public void setBstrVINDeActReqDate(String bstrVINDeActReqDate) {
            this.bstrVINDeActReqDate = bstrVINDeActReqDate;
        }

        /** 
         * Get the 'BstrVINRecur' element value.
         * 
         * @return value
         */
        public String getBstrVINRecur() {
            return bstrVINRecur;
        }

        /** 
         * Set the 'BstrVINRecur' element value.
         * 
         * @param bstrVINRecur
         */
        public void setBstrVINRecur(String bstrVINRecur) {
            this.bstrVINRecur = bstrVINRecur;
        }

        /** 
         * Get the 'BstrVNNActReqDate' element value.
         * 
         * @return value
         */
        public String getBstrVNNActReqDate() {
            return bstrVNNActReqDate;
        }

        /** 
         * Set the 'BstrVNNActReqDate' element value.
         * 
         * @param bstrVNNActReqDate
         */
        public void setBstrVNNActReqDate(String bstrVNNActReqDate) {
            this.bstrVNNActReqDate = bstrVNNActReqDate;
        }

        /** 
         * Get the 'BstrVNNConfirmDate' element value.
         * 
         * @return value
         */
        public String getBstrVNNConfirmDate() {
            return bstrVNNConfirmDate;
        }

        /** 
         * Set the 'BstrVNNConfirmDate' element value.
         * 
         * @param bstrVNNConfirmDate
         */
        public void setBstrVNNConfirmDate(String bstrVNNConfirmDate) {
            this.bstrVNNConfirmDate = bstrVNNConfirmDate;
        }

        /** 
         * Get the 'BstrVNNDeActConfDate' element value.
         * 
         * @return value
         */
        public String getBstrVNNDeActConfDate() {
            return bstrVNNDeActConfDate;
        }

        /** 
         * Set the 'BstrVNNDeActConfDate' element value.
         * 
         * @param bstrVNNDeActConfDate
         */
        public void setBstrVNNDeActConfDate(String bstrVNNDeActConfDate) {
            this.bstrVNNDeActConfDate = bstrVNNDeActConfDate;
        }

        /** 
         * Get the 'BstrVNNDeActReqDate' element value.
         * 
         * @return value
         */
        public String getBstrVNNDeActReqDate() {
            return bstrVNNDeActReqDate;
        }

        /** 
         * Set the 'BstrVNNDeActReqDate' element value.
         * 
         * @param bstrVNNDeActReqDate
         */
        public void setBstrVNNDeActReqDate(String bstrVNNDeActReqDate) {
            this.bstrVNNDeActReqDate = bstrVNNDeActReqDate;
        }

        /** 
         * Get the 'BstrVNNRecur' element value.
         * 
         * @return value
         */
        public String getBstrVNNRecur() {
            return bstrVNNRecur;
        }

        /** 
         * Set the 'BstrVNNRecur' element value.
         * 
         * @param bstrVNNRecur
         */
        public void setBstrVNNRecur(String bstrVNNRecur) {
            this.bstrVNNRecur = bstrVNNRecur;
        }

        /** 
         * Get the 'cbActReqDate' element value.
         * 
         * @return value
         */
        public String getCbActReqDate() {
            return cbActReqDate;
        }

        /** 
         * Set the 'cbActReqDate' element value.
         * 
         * @param cbActReqDate
         */
        public void setCbActReqDate(String cbActReqDate) {
            this.cbActReqDate = cbActReqDate;
        }

        /** 
         * Get the 'cbAll' element value.
         * 
         * @return value
         */
        public String getCbAll() {
            return cbAll;
        }

        /** 
         * Set the 'cbAll' element value.
         * 
         * @param cbAll
         */
        public void setCbAll(String cbAll) {
            this.cbAll = cbAll;
        }

        /** 
         * Get the 'cbIncom' element value.
         * 
         * @return value
         */
        public String getCbIncom() {
            return cbIncom;
        }

        /** 
         * Set the 'cbIncom' element value.
         * 
         * @param cbIncom
         */
        public void setCbIncom(String cbIncom) {
            this.cbIncom = cbIncom;
        }

        /** 
         * Get the 'cbInt' element value.
         * 
         * @return value
         */
        public String getCbInt() {
            return cbInt;
        }

        /** 
         * Set the 'cbInt' element value.
         * 
         * @param cbInt
         */
        public void setCbInt(String cbInt) {
            this.cbInt = cbInt;
        }

        /** 
         * Get the 'cbIntExc' element value.
         * 
         * @return value
         */
        public String getCbIntExc() {
            return cbIntExc;
        }

        /** 
         * Set the 'cbIntExc' element value.
         * 
         * @param cbIntExc
         */
        public void setCbIntExc(String cbIntExc) {
            this.cbIntExc = cbIntExc;
        }

        /** 
         * Get the 'cbWR' element value.
         * 
         * @return value
         */
        public String getCbWR() {
            return cbWR;
        }

        /** 
         * Set the 'cbWR' element value.
         * 
         * @param cbWR
         */
        public void setCbWR(String cbWR) {
            this.cbWR = cbWR;
        }

        /** 
         * Get the 'cfBusy' element value.
         * 
         * @return value
         */
        public String getCfBusy() {
            return cfBusy;
        }

        /** 
         * Set the 'cfBusy' element value.
         * 
         * @param cfBusy
         */
        public void setCfBusy(String cfBusy) {
            this.cfBusy = cfBusy;
        }

        /** 
         * Get the 'cfData' element value.
         * 
         * @return value
         */
        public String getCfData() {
            return cfData;
        }

        /** 
         * Set the 'cfData' element value.
         * 
         * @param cfData
         */
        public void setCfData(String cfData) {
            this.cfData = cfData;
        }

        /** 
         * Get the 'cfFax' element value.
         * 
         * @return value
         */
        public String getCfFax() {
            return cfFax;
        }

        /** 
         * Set the 'cfFax' element value.
         * 
         * @param cfFax
         */
        public void setCfFax(String cfFax) {
            this.cfFax = cfFax;
        }

        /** 
         * Get the 'cfInt' element value.
         * 
         * @return value
         */
        public String getCfInt() {
            return cfInt;
        }

        /** 
         * Set the 'cfInt' element value.
         * 
         * @param cfInt
         */
        public void setCfInt(String cfInt) {
            this.cfInt = cfInt;
        }

        /** 
         * Get the 'cfNoReply' element value.
         * 
         * @return value
         */
        public String getCfNoReply() {
            return cfNoReply;
        }

        /** 
         * Set the 'cfNoReply' element value.
         * 
         * @param cfNoReply
         */
        public void setCfNoReply(String cfNoReply) {
            this.cfNoReply = cfNoReply;
        }

        /** 
         * Get the 'cfNotReach' element value.
         * 
         * @return value
         */
        public String getCfNotReach() {
            return cfNotReach;
        }

        /** 
         * Set the 'cfNotReach' element value.
         * 
         * @param cfNotReach
         */
        public void setCfNotReach(String cfNotReach) {
            this.cfNotReach = cfNotReach;
        }

        /** 
         * Get the 'cfVoice' element value.
         * 
         * @return value
         */
        public String getCfVoice() {
            return cfVoice;
        }

        /** 
         * Set the 'cfVoice' element value.
         * 
         * @param cfVoice
         */
        public void setCfVoice(String cfVoice) {
            this.cfVoice = cfVoice;
        }

        /** 
         * Get the 'clir' element value.
         * 
         * @return value
         */
        public String getClir() {
            return clir;
        }

        /** 
         * Set the 'clir' element value.
         * 
         * @param clir
         */
        public void setClir(String clir) {
            this.clir = clir;
        }

        /** 
         * Get the 'cllrRngTne' element value.
         * 
         * @return value
         */
        public String getCllrRngTne() {
            return cllrRngTne;
        }

        /** 
         * Set the 'cllrRngTne' element value.
         * 
         * @param cllrRngTne
         */
        public void setCllrRngTne(String cllrRngTne) {
            this.cllrRngTne = cllrRngTne;
        }

        /** 
         * Get the 'CVM' element value.
         * 
         * @return value
         */
        public String getCVM() {
            return CVM;
        }

        /** 
         * Set the 'CVM' element value.
         * 
         * @param CVM
         */
        public void setCVM(String CVM) {
            this.CVM = CVM;
        }

        /** 
         * Get the 'CVMCounter' element value.
         * 
         * @return value
         */
        public String getCVMCounter() {
            return CVMCounter;
        }

        /** 
         * Set the 'CVMCounter' element value.
         * 
         * @param CVMCounter
         */
        public void setCVMCounter(String CVMCounter) {
            this.CVMCounter = CVMCounter;
        }

        /** 
         * Get the 'dataGeneric' element value.
         * 
         * @return value
         */
        public String getDataGeneric() {
            return dataGeneric;
        }

        /** 
         * Set the 'dataGeneric' element value.
         * 
         * @param dataGeneric
         */
        public void setDataGeneric(String dataGeneric) {
            this.dataGeneric = dataGeneric;
        }

        /** 
         * Get the 'Date1' element value.
         * 
         * @return value
         */
        public String getDate1() {
            return date1;
        }

        /** 
         * Set the 'Date1' element value.
         * 
         * @param date1
         */
        public void setDate1(String date1) {
            this.date1 = date1;
        }

        /** 
         * Get the 'DisabilityActivationDate' element value.
         * 
         * @return value
         */
        public String getDisabilityActivationDate() {
            return disabilityActivationDate;
        }

        /** 
         * Set the 'DisabilityActivationDate' element value.
         * 
         * @param disabilityActivationDate
         */
        public void setDisabilityActivationDate(String disabilityActivationDate) {
            this.disabilityActivationDate = disabilityActivationDate;
        }

        /** 
         * Get the 'DisabilityDectivationDate' element value.
         * 
         * @return value
         */
        public String getDisabilityDectivationDate() {
            return disabilityDectivationDate;
        }

        /** 
         * Set the 'DisabilityDectivationDate' element value.
         * 
         * @param disabilityDectivationDate
         */
        public void setDisabilityDectivationDate(
                String disabilityDectivationDate) {
            this.disabilityDectivationDate = disabilityDectivationDate;
        }

        /** 
         * Get the 'EmiratiPlan' element value.
         * 
         * @return value
         */
        public String getEmiratiPlan() {
            return emiratiPlan;
        }

        /** 
         * Set the 'EmiratiPlan' element value.
         * 
         * @param emiratiPlan
         */
        public void setEmiratiPlan(String emiratiPlan) {
            this.emiratiPlan = emiratiPlan;
        }

        /** 
         * Get the 'EntCust' element value.
         * 
         * @return value
         */
        public String getEntCust() {
            return entCust;
        }

        /** 
         * Set the 'EntCust' element value.
         * 
         * @param entCust
         */
        public void setEntCust(String entCust) {
            this.entCust = entCust;
        }

        /** 
         * Get the 'entMssgingActv' element value.
         * 
         * @return value
         */
        public String getEntMssgingActv() {
            return entMssgingActv;
        }

        /** 
         * Set the 'entMssgingActv' element value.
         * 
         * @param entMssgingActv
         */
        public void setEntMssgingActv(String entMssgingActv) {
            this.entMssgingActv = entMssgingActv;
        }

        /** 
         * Get the 'faxGroup3' element value.
         * 
         * @return value
         */
        public String getFaxGroup3() {
            return faxGroup3;
        }

        /** 
         * Set the 'faxGroup3' element value.
         * 
         * @param faxGroup3
         */
        public void setFaxGroup3(String faxGroup3) {
            this.faxGroup3 = faxGroup3;
        }

        /** 
         * Get the 'GlobalZoneOptIn' element value.
         * 
         * @return value
         */
        public String getGlobalZoneOptIn() {
            return globalZoneOptIn;
        }

        /** 
         * Set the 'GlobalZoneOptIn' element value.
         * 
         * @param globalZoneOptIn
         */
        public void setGlobalZoneOptIn(String globalZoneOptIn) {
            this.globalZoneOptIn = globalZoneOptIn;
        }

        /** 
         * Get the 'GlobalZoneOptOut' element value.
         * 
         * @return value
         */
        public String getGlobalZoneOptOut() {
            return globalZoneOptOut;
        }

        /** 
         * Set the 'GlobalZoneOptOut' element value.
         * 
         * @param globalZoneOptOut
         */
        public void setGlobalZoneOptOut(String globalZoneOptOut) {
            this.globalZoneOptOut = globalZoneOptOut;
        }

        /** 
         * Get the 'gprsPmail' element value.
         * 
         * @return value
         */
        public String getGprsPmail() {
            return gprsPmail;
        }

        /** 
         * Set the 'gprsPmail' element value.
         * 
         * @param gprsPmail
         */
        public void setGprsPmail(String gprsPmail) {
            this.gprsPmail = gprsPmail;
        }

        /** 
         * Get the 'IDD2Act' element value.
         * 
         * @return value
         */
        public String getIDD2Act() {
            return IDD2Act;
        }

        /** 
         * Set the 'IDD2Act' element value.
         * 
         * @param IDD2Act
         */
        public void setIDD2Act(String IDD2Act) {
            this.IDD2Act = IDD2Act;
        }

        /** 
         * Get the 'IDD2ActDate' element value.
         * 
         * @return value
         */
        public String getIDD2ActDate() {
            return IDD2ActDate;
        }

        /** 
         * Set the 'IDD2ActDate' element value.
         * 
         * @param IDD2ActDate
         */
        public void setIDD2ActDate(String IDD2ActDate) {
            this.IDD2ActDate = IDD2ActDate;
        }

        /** 
         * Get the 'IDD2DeactDate' element value.
         * 
         * @return value
         */
        public String getIDD2DeactDate() {
            return IDD2DeactDate;
        }

        /** 
         * Set the 'IDD2DeactDate' element value.
         * 
         * @param IDD2DeactDate
         */
        public void setIDD2DeactDate(String IDD2DeactDate) {
            this.IDD2DeactDate = IDD2DeactDate;
        }

        /** 
         * Get the 'IDDCutRateAct' element value.
         * 
         * @return value
         */
        public String getIDDCutRateAct() {
            return IDDCutRateAct;
        }

        /** 
         * Set the 'IDDCutRateAct' element value.
         * 
         * @param IDDCutRateAct
         */
        public void setIDDCutRateAct(String IDDCutRateAct) {
            this.IDDCutRateAct = IDDCutRateAct;
        }

        /** 
         * Get the 'IDDCutRateActDate' element value.
         * 
         * @return value
         */
        public String getIDDCutRateActDate() {
            return IDDCutRateActDate;
        }

        /** 
         * Set the 'IDDCutRateActDate' element value.
         * 
         * @param IDDCutRateActDate
         */
        public void setIDDCutRateActDate(String IDDCutRateActDate) {
            this.IDDCutRateActDate = IDDCutRateActDate;
        }

        /** 
         * Get the 'InternationalMin' element value.
         * 
         * @return value
         */
        public String getInternationalMin() {
            return internationalMin;
        }

        /** 
         * Set the 'InternationalMin' element value.
         * 
         * @param internationalMin
         */
        public void setInternationalMin(String internationalMin) {
            this.internationalMin = internationalMin;
        }

        /** 
         * Get the 'Language' element value.
         * 
         * @return value
         */
        public String getLanguage() {
            return language;
        }

        /** 
         * Set the 'Language' element value.
         * 
         * @param language
         */
        public void setLanguage(String language) {
            this.language = language;
        }

        /** 
         * Get the 'odbAll' element value.
         * 
         * @return value
         */
        public String getOdbAll() {
            return odbAll;
        }

        /** 
         * Set the 'odbAll' element value.
         * 
         * @param odbAll
         */
        public void setOdbAll(String odbAll) {
            this.odbAll = odbAll;
        }

        /** 
         * Get the 'odbIncom' element value.
         * 
         * @return value
         */
        public String getOdbIncom() {
            return odbIncom;
        }

        /** 
         * Set the 'odbIncom' element value.
         * 
         * @param odbIncom
         */
        public void setOdbIncom(String odbIncom) {
            this.odbIncom = odbIncom;
        }

        /** 
         * Get the 'odbInt' element value.
         * 
         * @return value
         */
        public String getOdbInt() {
            return odbInt;
        }

        /** 
         * Set the 'odbInt' element value.
         * 
         * @param odbInt
         */
        public void setOdbInt(String odbInt) {
            this.odbInt = odbInt;
        }

        /** 
         * Get the 'odbIntExc' element value.
         * 
         * @return value
         */
        public String getOdbIntExc() {
            return odbIntExc;
        }

        /** 
         * Set the 'odbIntExc' element value.
         * 
         * @param odbIntExc
         */
        public void setOdbIntExc(String odbIntExc) {
            this.odbIntExc = odbIntExc;
        }

        /** 
         * Get the 'odbOnlyVoice' element value.
         * 
         * @return value
         */
        public String getOdbOnlyVoice() {
            return odbOnlyVoice;
        }

        /** 
         * Set the 'odbOnlyVoice' element value.
         * 
         * @param odbOnlyVoice
         */
        public void setOdbOnlyVoice(String odbOnlyVoice) {
            this.odbOnlyVoice = odbOnlyVoice;
        }

        /** 
         * Get the 'odbOutVoice' element value.
         * 
         * @return value
         */
        public String getOdbOutVoice() {
            return odbOutVoice;
        }

        /** 
         * Set the 'odbOutVoice' element value.
         * 
         * @param odbOutVoice
         */
        public void setOdbOutVoice(String odbOutVoice) {
            this.odbOutVoice = odbOutVoice;
        }

        /** 
         * Get the 'odbWR' element value.
         * 
         * @return value
         */
        public String getOdbWR() {
            return odbWR;
        }

        /** 
         * Set the 'odbWR' element value.
         * 
         * @param odbWR
         */
        public void setOdbWR(String odbWR) {
            this.odbWR = odbWR;
        }

        /** 
         * Get the 'PAYGDataFNL' element value.
         * 
         * @return value
         */
        public String getPAYGDataFNL() {
            return PAYGDataFNL;
        }

        /** 
         * Set the 'PAYGDataFNL' element value.
         * 
         * @param PAYGDataFNL
         */
        public void setPAYGDataFNL(String PAYGDataFNL) {
            this.PAYGDataFNL = PAYGDataFNL;
        }

        /** 
         * Get the 'PAYGMet' element value.
         * 
         * @return value
         */
        public String getPAYGMet() {
            return PAYGMet;
        }

        /** 
         * Set the 'PAYGMet' element value.
         * 
         * @param PAYGMet
         */
        public void setPAYGMet(String PAYGMet) {
            this.PAYGMet = PAYGMet;
        }

        /** 
         * Get the 'PCNGlobalOptin' element value.
         * 
         * @return value
         */
        public String getPCNGlobalOptin() {
            return PCNGlobalOptin;
        }

        /** 
         * Set the 'PCNGlobalOptin' element value.
         * 
         * @param PCNGlobalOptin
         */
        public void setPCNGlobalOptin(String PCNGlobalOptin) {
            this.PCNGlobalOptin = PCNGlobalOptin;
        }

        /** 
         * Get the 'PCNGlobalOptout' element value.
         * 
         * @return value
         */
        public String getPCNGlobalOptout() {
            return PCNGlobalOptout;
        }

        /** 
         * Set the 'PCNGlobalOptout' element value.
         * 
         * @param PCNGlobalOptout
         */
        public void setPCNGlobalOptout(String PCNGlobalOptout) {
            this.PCNGlobalOptout = PCNGlobalOptout;
        }

        /** 
         * Get the 'Plan' element value.
         * 
         * @return value
         */
        public String getPlan() {
            return plan;
        }

        /** 
         * Set the 'Plan' element value.
         * 
         * @param plan
         */
        public void setPlan(String plan) {
            this.plan = plan;
        }

        /** 
         * Get the 'ReceivePostCallSMS' element value.
         * 
         * @return value
         */
        public String getReceivePostCallSMS() {
            return receivePostCallSMS;
        }

        /** 
         * Set the 'ReceivePostCallSMS' element value.
         * 
         * @param receivePostCallSMS
         */
        public void setReceivePostCallSMS(String receivePostCallSMS) {
            this.receivePostCallSMS = receivePostCallSMS;
        }

        /** 
         * Get the 'SocialBndlActChannel' element value.
         * 
         * @return value
         */
        public String getSocialBndlActChannel() {
            return socialBndlActChannel;
        }

        /** 
         * Set the 'SocialBndlActChannel' element value.
         * 
         * @param socialBndlActChannel
         */
        public void setSocialBndlActChannel(String socialBndlActChannel) {
            this.socialBndlActChannel = socialBndlActChannel;
        }

        /** 
         * Get the 'SocialBndlDeActChannel' element value.
         * 
         * @return value
         */
        public String getSocialBndlDeActChannel() {
            return socialBndlDeActChannel;
        }

        /** 
         * Set the 'SocialBndlDeActChannel' element value.
         * 
         * @param socialBndlDeActChannel
         */
        public void setSocialBndlDeActChannel(String socialBndlDeActChannel) {
            this.socialBndlDeActChannel = socialBndlDeActChannel;
        }

        /** 
         * Get the 'BstrVceIntNumExp' element value.
         * 
         * @return value
         */
        public String getBstrVceIntNumExp() {
            return bstrVceIntNumExp;
        }

        /** 
         * Set the 'BstrVceIntNumExp' element value.
         * 
         * @param bstrVceIntNumExp
         */
        public void setBstrVceIntNumExp(String bstrVceIntNumExp) {
            this.bstrVceIntNumExp = bstrVceIntNumExp;
        }

        /** 
         * Get the 'BstrVceNatNumExp' element value.
         * 
         * @return value
         */
        public String getBstrVceNatNumExp() {
            return bstrVceNatNumExp;
        }

        /** 
         * Set the 'BstrVceNatNumExp' element value.
         * 
         * @param bstrVceNatNumExp
         */
        public void setBstrVceNatNumExp(String bstrVceNatNumExp) {
            this.bstrVceNatNumExp = bstrVceNatNumExp;
        }

        /** 
         * Get the 'BstrVINChngsAllwd' element value.
         * 
         * @return value
         */
        public String getBstrVINChngsAllwd() {
            return bstrVINChngsAllwd;
        }

        /** 
         * Set the 'BstrVINChngsAllwd' element value.
         * 
         * @param bstrVINChngsAllwd
         */
        public void setBstrVINChngsAllwd(String bstrVINChngsAllwd) {
            this.bstrVINChngsAllwd = bstrVINChngsAllwd;
        }

        /** 
         * Get the 'BstrVNNChngsAllwd' element value.
         * 
         * @return value
         */
        public String getBstrVNNChngsAllwd() {
            return bstrVNNChngsAllwd;
        }

        /** 
         * Set the 'BstrVNNChngsAllwd' element value.
         * 
         * @param bstrVNNChngsAllwd
         */
        public void setBstrVNNChngsAllwd(String bstrVNNChngsAllwd) {
            this.bstrVNNChngsAllwd = bstrVNNChngsAllwd;
        }

        /** 
         * Get the 'TranslatedNumber' element value.
         * 
         * @return value
         */
        public String getTranslatedNumber() {
            return translatedNumber;
        }

        /** 
         * Set the 'TranslatedNumber' element value.
         * 
         * @param translatedNumber
         */
        public void setTranslatedNumber(String translatedNumber) {
            this.translatedNumber = translatedNumber;
        }

        /** 
         * Get the 'TransNumber' element value.
         * 
         * @return value
         */
        public String getTransNumber() {
            return transNumber;
        }

        /** 
         * Set the 'TransNumber' element value.
         * 
         * @param transNumber
         */
        public void setTransNumber(String transNumber) {
            this.transNumber = transNumber;
        }

        /** 
         * Get the 'SmsBndl1Recur' element value.
         * 
         * @return value
         */
        public String getSmsBndl1Recur() {
            return smsBndl1Recur;
        }

        /** 
         * Set the 'SmsBndl1Recur' element value.
         * 
         * @param smsBndl1Recur
         */
        public void setSmsBndl1Recur(String smsBndl1Recur) {
            this.smsBndl1Recur = smsBndl1Recur;
        }

        /** 
         * Get the 'SmsExpDate' element value.
         * 
         * @return value
         */
        public String getSmsExpDate() {
            return smsExpDate;
        }

        /** 
         * Set the 'SmsExpDate' element value.
         * 
         * @param smsExpDate
         */
        public void setSmsExpDate(String smsExpDate) {
            this.smsExpDate = smsExpDate;
        }

        /** 
         * Get the 'SmsBndl2Recur' element value.
         * 
         * @return value
         */
        public String getSmsBndl2Recur() {
            return smsBndl2Recur;
        }

        /** 
         * Set the 'SmsBndl2Recur' element value.
         * 
         * @param smsBndl2Recur
         */
        public void setSmsBndl2Recur(String smsBndl2Recur) {
            this.smsBndl2Recur = smsBndl2Recur;
        }

        /** 
         * Get the 'PriceShout' element value.
         * 
         * @return value
         */
        public String getPriceShout() {
            return priceShout;
        }

        /** 
         * Set the 'PriceShout' element value.
         * 
         * @param priceShout
         */
        public void setPriceShout(String priceShout) {
            this.priceShout = priceShout;
        }

        /** 
         * Get the 'BlckBrryAct' element value.
         * 
         * @return value
         */
        public String getBlckBrryAct() {
            return blckBrryAct;
        }

        /** 
         * Set the 'BlckBrryAct' element value.
         * 
         * @param blckBrryAct
         */
        public void setBlckBrryAct(String blckBrryAct) {
            this.blckBrryAct = blckBrryAct;
        }

        /** 
         * Get the 'BlckBrryCnfdAct' element value.
         * 
         * @return value
         */
        public String getBlckBrryCnfdAct() {
            return blckBrryCnfdAct;
        }

        /** 
         * Set the 'BlckBrryCnfdAct' element value.
         * 
         * @param blckBrryCnfdAct
         */
        public void setBlckBrryCnfdAct(String blckBrryCnfdAct) {
            this.blckBrryCnfdAct = blckBrryCnfdAct;
        }

        /** 
         * Get the 'BlckBrryActCnfrmDate' element value.
         * 
         * @return value
         */
        public String getBlckBrryActCnfrmDate() {
            return blckBrryActCnfrmDate;
        }

        /** 
         * Set the 'BlckBrryActCnfrmDate' element value.
         * 
         * @param blckBrryActCnfrmDate
         */
        public void setBlckBrryActCnfrmDate(String blckBrryActCnfrmDate) {
            this.blckBrryActCnfrmDate = blckBrryActCnfrmDate;
        }

        /** 
         * Get the 'BlckBrryExpDate' element value.
         * 
         * @return value
         */
        public String getBlckBrryExpDate() {
            return blckBrryExpDate;
        }

        /** 
         * Set the 'BlckBrryExpDate' element value.
         * 
         * @param blckBrryExpDate
         */
        public void setBlckBrryExpDate(String blckBrryExpDate) {
            this.blckBrryExpDate = blckBrryExpDate;
        }

        /** 
         * Get the 'SmsBndl3Recur' element value.
         * 
         * @return value
         */
        public String getSmsBndl3Recur() {
            return smsBndl3Recur;
        }

        /** 
         * Set the 'SmsBndl3Recur' element value.
         * 
         * @param smsBndl3Recur
         */
        public void setSmsBndl3Recur(String smsBndl3Recur) {
            this.smsBndl3Recur = smsBndl3Recur;
        }

        /** 
         * Get the 'Bespoke' element value.
         * 
         * @return value
         */
        public String getBespoke() {
            return bespoke;
        }

        /** 
         * Set the 'Bespoke' element value.
         * 
         * @param bespoke
         */
        public void setBespoke(String bespoke) {
            this.bespoke = bespoke;
        }

        /** 
         * Get the 'ManRenDateLess1Y' element value.
         * 
         * @return value
         */
        public String getManRenDateLess1Y() {
            return manRenDateLess1Y;
        }

        /** 
         * Set the 'ManRenDateLess1Y' element value.
         * 
         * @param manRenDateLess1Y
         */
        public void setManRenDateLess1Y(String manRenDateLess1Y) {
            this.manRenDateLess1Y = manRenDateLess1Y;
        }

        /** 
         * Get the 'DataBndl1Recur' element value.
         * 
         * @return value
         */
        public String getDataBndl1Recur() {
            return dataBndl1Recur;
        }

        /** 
         * Set the 'DataBndl1Recur' element value.
         * 
         * @param dataBndl1Recur
         */
        public void setDataBndl1Recur(String dataBndl1Recur) {
            this.dataBndl1Recur = dataBndl1Recur;
        }

        /** 
         * Get the 'DataBndlType' element value.
         * 
         * @return value
         */
        public String getDataBndlType() {
            return dataBndlType;
        }

        /** 
         * Set the 'DataBndlType' element value.
         * 
         * @param dataBndlType
         */
        public void setDataBndlType(String dataBndlType) {
            this.dataBndlType = dataBndlType;
        }

        /** 
         * Get the 'DataExpDate' element value.
         * 
         * @return value
         */
        public String getDataExpDate() {
            return dataExpDate;
        }

        /** 
         * Set the 'DataExpDate' element value.
         * 
         * @param dataExpDate
         */
        public void setDataExpDate(String dataExpDate) {
            this.dataExpDate = dataExpDate;
        }

        /** 
         * Get the 'DataGraceAct' element value.
         * 
         * @return value
         */
        public String getDataGraceAct() {
            return dataGraceAct;
        }

        /** 
         * Set the 'DataGraceAct' element value.
         * 
         * @param dataGraceAct
         */
        public void setDataGraceAct(String dataGraceAct) {
            this.dataGraceAct = dataGraceAct;
        }

        /** 
         * Get the 'DataGraceEnd' element value.
         * 
         * @return value
         */
        public String getDataGraceEnd() {
            return dataGraceEnd;
        }

        /** 
         * Set the 'DataGraceEnd' element value.
         * 
         * @param dataGraceEnd
         */
        public void setDataGraceEnd(String dataGraceEnd) {
            this.dataGraceEnd = dataGraceEnd;
        }

        /** 
         * Get the 'DataBndl2Recur' element value.
         * 
         * @return value
         */
        public String getDataBndl2Recur() {
            return dataBndl2Recur;
        }

        /** 
         * Set the 'DataBndl2Recur' element value.
         * 
         * @param dataBndl2Recur
         */
        public void setDataBndl2Recur(String dataBndl2Recur) {
            this.dataBndl2Recur = dataBndl2Recur;
        }

        /** 
         * Get the 'DataBndl3Recur' element value.
         * 
         * @return value
         */
        public String getDataBndl3Recur() {
            return dataBndl3Recur;
        }

        /** 
         * Set the 'DataBndl3Recur' element value.
         * 
         * @param dataBndl3Recur
         */
        public void setDataBndl3Recur(String dataBndl3Recur) {
            this.dataBndl3Recur = dataBndl3Recur;
        }

        /** 
         * Get the 'DataBndl4Recur' element value.
         * 
         * @return value
         */
        public String getDataBndl4Recur() {
            return dataBndl4Recur;
        }

        /** 
         * Set the 'DataBndl4Recur' element value.
         * 
         * @param dataBndl4Recur
         */
        public void setDataBndl4Recur(String dataBndl4Recur) {
            this.dataBndl4Recur = dataBndl4Recur;
        }

        /** 
         * Get the 'DataBndl5Recur' element value.
         * 
         * @return value
         */
        public String getDataBndl5Recur() {
            return dataBndl5Recur;
        }

        /** 
         * Set the 'DataBndl5Recur' element value.
         * 
         * @param dataBndl5Recur
         */
        public void setDataBndl5Recur(String dataBndl5Recur) {
            this.dataBndl5Recur = dataBndl5Recur;
        }

        /** 
         * Get the 'DataBndl6Recur' element value.
         * 
         * @return value
         */
        public String getDataBndl6Recur() {
            return dataBndl6Recur;
        }

        /** 
         * Set the 'DataBndl6Recur' element value.
         * 
         * @param dataBndl6Recur
         */
        public void setDataBndl6Recur(String dataBndl6Recur) {
            this.dataBndl6Recur = dataBndl6Recur;
        }

        /** 
         * Get the 'DataBndl8Recur' element value.
         * 
         * @return value
         */
        public String getDataBndl8Recur() {
            return dataBndl8Recur;
        }

        /** 
         * Set the 'DataBndl8Recur' element value.
         * 
         * @param dataBndl8Recur
         */
        public void setDataBndl8Recur(String dataBndl8Recur) {
            this.dataBndl8Recur = dataBndl8Recur;
        }

        /** 
         * Get the 'DataBndl9Recur' element value.
         * 
         * @return value
         */
        public String getDataBndl9Recur() {
            return dataBndl9Recur;
        }

        /** 
         * Set the 'DataBndl9Recur' element value.
         * 
         * @param dataBndl9Recur
         */
        public void setDataBndl9Recur(String dataBndl9Recur) {
            this.dataBndl9Recur = dataBndl9Recur;
        }

        /** 
         * Get the 'MBB' element value.
         * 
         * @return value
         */
        public String getMBB() {
            return MBB;
        }

        /** 
         * Set the 'MBB' element value.
         * 
         * @param MBB
         */
        public void setMBB(String MBB) {
            this.MBB = MBB;
        }

        /** 
         * Get the 'MBBExpDate' element value.
         * 
         * @return value
         */
        public String getMBBExpDate() {
            return MBBExpDate;
        }

        /** 
         * Set the 'MBBExpDate' element value.
         * 
         * @param MBBExpDate
         */
        public void setMBBExpDate(String MBBExpDate) {
            this.MBBExpDate = MBBExpDate;
        }

        /** 
         * Get the 'MBBUnlimited' element value.
         * 
         * @return value
         */
        public String getMBBUnlimited() {
            return MBBUnlimited;
        }

        /** 
         * Set the 'MBBUnlimited' element value.
         * 
         * @param MBBUnlimited
         */
        public void setMBBUnlimited(String MBBUnlimited) {
            this.MBBUnlimited = MBBUnlimited;
        }

        /** 
         * Get the 'MBBUnlimExpDate' element value.
         * 
         * @return value
         */
        public String getMBBUnlimExpDate() {
            return MBBUnlimExpDate;
        }

        /** 
         * Set the 'MBBUnlimExpDate' element value.
         * 
         * @param MBBUnlimExpDate
         */
        public void setMBBUnlimExpDate(String MBBUnlimExpDate) {
            this.MBBUnlimExpDate = MBBUnlimExpDate;
        }

        /** 
         * Get the 'MBB2GB' element value.
         * 
         * @return value
         */
        public String getMBB2GB() {
            return MBB2GB;
        }

        /** 
         * Set the 'MBB2GB' element value.
         * 
         * @param MBB2GB
         */
        public void setMBB2GB(String MBB2GB) {
            this.MBB2GB = MBB2GB;
        }

        /** 
         * Get the 'MBB2GBExpDate' element value.
         * 
         * @return value
         */
        public String getMBB2GBExpDate() {
            return MBB2GBExpDate;
        }

        /** 
         * Set the 'MBB2GBExpDate' element value.
         * 
         * @param MBB2GBExpDate
         */
        public void setMBB2GBExpDate(String MBB2GBExpDate) {
            this.MBB2GBExpDate = MBB2GBExpDate;
        }

        /** 
         * Get the 'MBB10GB' element value.
         * 
         * @return value
         */
        public String getMBB10GB() {
            return MBB10GB;
        }

        /** 
         * Set the 'MBB10GB' element value.
         * 
         * @param MBB10GB
         */
        public void setMBB10GB(String MBB10GB) {
            this.MBB10GB = MBB10GB;
        }

        /** 
         * Get the 'MBB10GBExpDate' element value.
         * 
         * @return value
         */
        public String getMBB10GBExpDate() {
            return MBB10GBExpDate;
        }

        /** 
         * Set the 'MBB10GBExpDate' element value.
         * 
         * @param MBB10GBExpDate
         */
        public void setMBB10GBExpDate(String MBB10GBExpDate) {
            this.MBB10GBExpDate = MBB10GBExpDate;
        }

        /** 
         * Get the 'MBBGraceAct' element value.
         * 
         * @return value
         */
        public String getMBBGraceAct() {
            return MBBGraceAct;
        }

        /** 
         * Set the 'MBBGraceAct' element value.
         * 
         * @param MBBGraceAct
         */
        public void setMBBGraceAct(String MBBGraceAct) {
            this.MBBGraceAct = MBBGraceAct;
        }

        /** 
         * Get the 'MBBGraceEnd' element value.
         * 
         * @return value
         */
        public String getMBBGraceEnd() {
            return MBBGraceEnd;
        }

        /** 
         * Set the 'MBBGraceEnd' element value.
         * 
         * @param MBBGraceEnd
         */
        public void setMBBGraceEnd(String MBBGraceEnd) {
            this.MBBGraceEnd = MBBGraceEnd;
        }

        /** 
         * Get the 'MBBOfferExpDate' element value.
         * 
         * @return value
         */
        public String getMBBOfferExpDate() {
            return MBBOfferExpDate;
        }

        /** 
         * Set the 'MBBOfferExpDate' element value.
         * 
         * @param MBBOfferExpDate
         */
        public void setMBBOfferExpDate(String MBBOfferExpDate) {
            this.MBBOfferExpDate = MBBOfferExpDate;
        }

        /** 
         * Get the 'TP_Social_Deact_Conf' element value.
         * 
         * @return value
         */
        public String getTPSocialDeactConf() {
            return TPSocialDeactConf;
        }

        /** 
         * Set the 'TP_Social_Deact_Conf' element value.
         * 
         * @param TPSocialDeactConf
         */
        public void setTPSocialDeactConf(String TPSocialDeactConf) {
            this.TPSocialDeactConf = TPSocialDeactConf;
        }

        /** 
         * Get the 'cbAct' element value.
         * 
         * @return value
         */
        public String getCbAct() {
            return cbAct;
        }

        /** 
         * Set the 'cbAct' element value.
         * 
         * @param cbAct
         */
        public void setCbAct(String cbAct) {
            this.cbAct = cbAct;
        }

        /** 
         * Get the 'PokeSMSOptoutDate' element value.
         * 
         * @return value
         */
        public String getPokeSMSOptoutDate() {
            return pokeSMSOptoutDate;
        }

        /** 
         * Set the 'PokeSMSOptoutDate' element value.
         * 
         * @param pokeSMSOptoutDate
         */
        public void setPokeSMSOptoutDate(String pokeSMSOptoutDate) {
            this.pokeSMSOptoutDate = pokeSMSOptoutDate;
        }

        /** 
         * Get the 'PokeSMSOptinDate' element value.
         * 
         * @return value
         */
        public String getPokeSMSOptinDate() {
            return pokeSMSOptinDate;
        }

        /** 
         * Set the 'PokeSMSOptinDate' element value.
         * 
         * @param pokeSMSOptinDate
         */
        public void setPokeSMSOptinDate(String pokeSMSOptinDate) {
            this.pokeSMSOptinDate = pokeSMSOptinDate;
        }

        /** 
         * Get the 'BlckBrryActReqDate' element value.
         * 
         * @return value
         */
        public String getBlckBrryActReqDate() {
            return blckBrryActReqDate;
        }

        /** 
         * Set the 'BlckBrryActReqDate' element value.
         * 
         * @param blckBrryActReqDate
         */
        public void setBlckBrryActReqDate(String blckBrryActReqDate) {
            this.blckBrryActReqDate = blckBrryActReqDate;
        }

        /** 
         * Get the 'BlckBrryDeActReqDate' element value.
         * 
         * @return value
         */
        public String getBlckBrryDeActReqDate() {
            return blckBrryDeActReqDate;
        }

        /** 
         * Set the 'BlckBrryDeActReqDate' element value.
         * 
         * @param blckBrryDeActReqDate
         */
        public void setBlckBrryDeActReqDate(String blckBrryDeActReqDate) {
            this.blckBrryDeActReqDate = blckBrryDeActReqDate;
        }

        /** 
         * Get the 'BlckBrryDeactRbkDate' element value.
         * 
         * @return value
         */
        public String getBlckBrryDeactRbkDate() {
            return blckBrryDeactRbkDate;
        }

        /** 
         * Set the 'BlckBrryDeactRbkDate' element value.
         * 
         * @param blckBrryDeactRbkDate
         */
        public void setBlckBrryDeactRbkDate(String blckBrryDeactRbkDate) {
            this.blckBrryDeactRbkDate = blckBrryDeactRbkDate;
        }

        /** 
         * Get the 'BronzeDeactDate' element value.
         * 
         * @return value
         */
        public String getBronzeDeactDate() {
            return bronzeDeactDate;
        }

        /** 
         * Set the 'BronzeDeactDate' element value.
         * 
         * @param bronzeDeactDate
         */
        public void setBronzeDeactDate(String bronzeDeactDate) {
            this.bronzeDeactDate = bronzeDeactDate;
        }

        /** 
         * Get the 'BronzeActDate' element value.
         * 
         * @return value
         */
        public String getBronzeActDate() {
            return bronzeActDate;
        }

        /** 
         * Set the 'BronzeActDate' element value.
         * 
         * @param bronzeActDate
         */
        public void setBronzeActDate(String bronzeActDate) {
            this.bronzeActDate = bronzeActDate;
        }

        /** 
         * Get the 'umsCnfrmd' element value.
         * 
         * @return value
         */
        public String getUmsCnfrmd() {
            return umsCnfrmd;
        }

        /** 
         * Set the 'umsCnfrmd' element value.
         * 
         * @param umsCnfrmd
         */
        public void setUmsCnfrmd(String umsCnfrmd) {
            this.umsCnfrmd = umsCnfrmd;
        }

        /** 
         * Get the 'umsInfSub' element value.
         * 
         * @return value
         */
        public String getUmsInfSub() {
            return umsInfSub;
        }

        /** 
         * Set the 'umsInfSub' element value.
         * 
         * @param umsInfSub
         */
        public void setUmsInfSub(String umsInfSub) {
            this.umsInfSub = umsInfSub;
        }

        /** 
         * Get the 'ums' element value.
         * 
         * @return value
         */
        public String getUms() {
            return ums;
        }

        /** 
         * Set the 'ums' element value.
         * 
         * @param ums
         */
        public void setUms(String ums) {
            this.ums = ums;
        }

        /** 
         * Get the 'umsF2M' element value.
         * 
         * @return value
         */
        public String getUmsF2M() {
            return umsF2M;
        }

        /** 
         * Set the 'umsF2M' element value.
         * 
         * @param umsF2M
         */
        public void setUmsF2M(String umsF2M) {
            this.umsF2M = umsF2M;
        }

        /** 
         * Get the 'umsSMS2F' element value.
         * 
         * @return value
         */
        public String getUmsSMS2F() {
            return umsSMS2F;
        }

        /** 
         * Set the 'umsSMS2F' element value.
         * 
         * @param umsSMS2F
         */
        public void setUmsSMS2F(String umsSMS2F) {
            this.umsSMS2F = umsSMS2F;
        }

        /** 
         * Get the 'umsVm2MMS' element value.
         * 
         * @return value
         */
        public String getUmsVm2MMS() {
            return umsVm2MMS;
        }

        /** 
         * Set the 'umsVm2MMS' element value.
         * 
         * @param umsVm2MMS
         */
        public void setUmsVm2MMS(String umsVm2MMS) {
            this.umsVm2MMS = umsVm2MMS;
        }

        /** 
         * Get the 'TravSumOptInDate' element value.
         * 
         * @return value
         */
        public String getTravSumOptInDate() {
            return travSumOptInDate;
        }

        /** 
         * Set the 'TravSumOptInDate' element value.
         * 
         * @param travSumOptInDate
         */
        public void setTravSumOptInDate(String travSumOptInDate) {
            this.travSumOptInDate = travSumOptInDate;
        }

        /** 
         * Get the 'PassOptInDate' element value.
         * 
         * @return value
         */
        public String getPassOptInDate() {
            return passOptInDate;
        }

        /** 
         * Set the 'PassOptInDate' element value.
         * 
         * @param passOptInDate
         */
        public void setPassOptInDate(String passOptInDate) {
            this.passOptInDate = passOptInDate;
        }

        /** 
         * Get the 'PassOptOutDate' element value.
         * 
         * @return value
         */
        public String getPassOptOutDate() {
            return passOptOutDate;
        }

        /** 
         * Set the 'PassOptOutDate' element value.
         * 
         * @param passOptOutDate
         */
        public void setPassOptOutDate(String passOptOutDate) {
            this.passOptOutDate = passOptOutDate;
        }

        /** 
         * Get the 'TwinSIMNumber' element value.
         * 
         * @return value
         */
        public String getTwinSIMNumber() {
            return twinSIMNumber;
        }

        /** 
         * Set the 'TwinSIMNumber' element value.
         * 
         * @param twinSIMNumber
         */
        public void setTwinSIMNumber(String twinSIMNumber) {
            this.twinSIMNumber = twinSIMNumber;
        }

        /** 
         * Get the 'FstTimeActivation' element value.
         * 
         * @return value
         */
        public String getFstTimeActivation() {
            return fstTimeActivation;
        }

        /** 
         * Set the 'FstTimeActivation' element value.
         * 
         * @param fstTimeActivation
         */
        public void setFstTimeActivation(String fstTimeActivation) {
            this.fstTimeActivation = fstTimeActivation;
        }

        /** 
         * Get the 'Anniversarytopuppromo' element value.
         * 
         * @return value
         */
        public String getAnniversarytopuppromo() {
            return anniversarytopuppromo;
        }

        /** 
         * Set the 'Anniversarytopuppromo' element value.
         * 
         * @param anniversarytopuppromo
         */
        public void setAnniversarytopuppromo(String anniversarytopuppromo) {
            this.anniversarytopuppromo = anniversarytopuppromo;
        }

        /** 
         * Get the 'AnniversarytopuppromoEnd' element value.
         * 
         * @return value
         */
        public String getAnniversarytopuppromoEnd() {
            return anniversarytopuppromoEnd;
        }

        /** 
         * Set the 'AnniversarytopuppromoEnd' element value.
         * 
         * @param anniversarytopuppromoEnd
         */
        public void setAnniversarytopuppromoEnd(String anniversarytopuppromoEnd) {
            this.anniversarytopuppromoEnd = anniversarytopuppromoEnd;
        }

        /** 
         * Get the 'SmartPhoneExpiry' element value.
         * 
         * @return value
         */
        public String getSmartPhoneExpiry() {
            return smartPhoneExpiry;
        }

        /** 
         * Set the 'SmartPhoneExpiry' element value.
         * 
         * @param smartPhoneExpiry
         */
        public void setSmartPhoneExpiry(String smartPhoneExpiry) {
            this.smartPhoneExpiry = smartPhoneExpiry;
        }

        /** 
         * Get the 'DSP_Promo' element value.
         * 
         * @return value
         */
        public String getDSPPromo() {
            return DSPPromo;
        }

        /** 
         * Set the 'DSP_Promo' element value.
         * 
         * @param DSPPromo
         */
        public void setDSPPromo(String DSPPromo) {
            this.DSPPromo = DSPPromo;
        }

        /** 
         * Get the 'guiBalAdjDate' element value.
         * 
         * @return value
         */
        public String getGuiBalAdjDate() {
            return guiBalAdjDate;
        }

        /** 
         * Set the 'guiBalAdjDate' element value.
         * 
         * @param guiBalAdjDate
         */
        public void setGuiBalAdjDate(String guiBalAdjDate) {
            this.guiBalAdjDate = guiBalAdjDate;
        }

        /** 
         * Get the 'guiBalAdjVal' element value.
         * 
         * @return value
         */
        public String getGuiBalAdjVal() {
            return guiBalAdjVal;
        }

        /** 
         * Set the 'guiBalAdjVal' element value.
         * 
         * @param guiBalAdjVal
         */
        public void setGuiBalAdjVal(String guiBalAdjVal) {
            this.guiBalAdjVal = guiBalAdjVal;
        }

        /** 
         * Get the 'BBBundName' element value.
         * 
         * @return value
         */
        public String getBBBundName() {
            return BBBundName;
        }

        /** 
         * Set the 'BBBundName' element value.
         * 
         * @param BBBundName
         */
        public void setBBBundName(String BBBundName) {
            this.BBBundName = BBBundName;
        }

        /** 
         * Get the 'BBPromo' element value.
         * 
         * @return value
         */
        public String getBBPromo() {
            return BBPromo;
        }

        /** 
         * Set the 'BBPromo' element value.
         * 
         * @param BBPromo
         */
        public void setBBPromo(String BBPromo) {
            this.BBPromo = BBPromo;
        }

        /** 
         * Get the 'BSCSAct' element value.
         * 
         * @return value
         */
        public String getBSCSAct() {
            return BSCSAct;
        }

        /** 
         * Set the 'BSCSAct' element value.
         * 
         * @param BSCSAct
         */
        public void setBSCSAct(String BSCSAct) {
            this.BSCSAct = BSCSAct;
        }

        /** 
         * Get the 'BBPromoSubDate' element value.
         * 
         * @return value
         */
        public String getBBPromoSubDate() {
            return BBPromoSubDate;
        }

        /** 
         * Set the 'BBPromoSubDate' element value.
         * 
         * @param BBPromoSubDate
         */
        public void setBBPromoSubDate(String BBPromoSubDate) {
            this.BBPromoSubDate = BBPromoSubDate;
        }

        /** 
         * Get the 'BBPromoEndDate' element value.
         * 
         * @return value
         */
        public String getBBPromoEndDate() {
            return BBPromoEndDate;
        }

        /** 
         * Set the 'BBPromoEndDate' element value.
         * 
         * @param BBPromoEndDate
         */
        public void setBBPromoEndDate(String BBPromoEndDate) {
            this.BBPromoEndDate = BBPromoEndDate;
        }

        /** 
         * Get the 'BlckBrrySprssSMS' element value.
         * 
         * @return value
         */
        public String getBlckBrrySprssSMS() {
            return blckBrrySprssSMS;
        }

        /** 
         * Set the 'BlckBrrySprssSMS' element value.
         * 
         * @param blckBrrySprssSMS
         */
        public void setBlckBrrySprssSMS(String blckBrrySprssSMS) {
            this.blckBrrySprssSMS = blckBrrySprssSMS;
        }

        /** 
         * Get the 'BlckBrryDeActCnfrmDt' element value.
         * 
         * @return value
         */
        public String getBlckBrryDeActCnfrmDt() {
            return blckBrryDeActCnfrmDt;
        }

        /** 
         * Set the 'BlckBrryDeActCnfrmDt' element value.
         * 
         * @param blckBrryDeActCnfrmDt
         */
        public void setBlckBrryDeActCnfrmDt(String blckBrryDeActCnfrmDt) {
            this.blckBrryDeActCnfrmDt = blckBrryDeActCnfrmDt;
        }

        /** 
         * Get the 'Payg_Life_Optin' element value.
         * 
         * @return value
         */
        public String getPaygLifeOptin() {
            return paygLifeOptin;
        }

        /** 
         * Set the 'Payg_Life_Optin' element value.
         * 
         * @param paygLifeOptin
         */
        public void setPaygLifeOptin(String paygLifeOptin) {
            this.paygLifeOptin = paygLifeOptin;
        }

        /** 
         * Get the 'gprsBasicSer' element value.
         * 
         * @return value
         */
        public String getGprsBasicSer() {
            return gprsBasicSer;
        }

        /** 
         * Set the 'gprsBasicSer' element value.
         * 
         * @param gprsBasicSer
         */
        public void setGprsBasicSer(String gprsBasicSer) {
            this.gprsBasicSer = gprsBasicSer;
        }

        /** 
         * Get the 'gprsWap' element value.
         * 
         * @return value
         */
        public String getGprsWap() {
            return gprsWap;
        }

        /** 
         * Set the 'gprsWap' element value.
         * 
         * @param gprsWap
         */
        public void setGprsWap(String gprsWap) {
            this.gprsWap = gprsWap;
        }

        /** 
         * Get the 'smsMtPp' element value.
         * 
         * @return value
         */
        public String getSmsMtPp() {
            return smsMtPp;
        }

        /** 
         * Set the 'smsMtPp' element value.
         * 
         * @param smsMtPp
         */
        public void setSmsMtPp(String smsMtPp) {
            this.smsMtPp = smsMtPp;
        }

        /** 
         * Get the 'gprsMms' element value.
         * 
         * @return value
         */
        public String getGprsMms() {
            return gprsMms;
        }

        /** 
         * Set the 'gprsMms' element value.
         * 
         * @param gprsMms
         */
        public void setGprsMms(String gprsMms) {
            this.gprsMms = gprsMms;
        }

        /** 
         * Get the 'smsMoPp' element value.
         * 
         * @return value
         */
        public String getSmsMoPp() {
            return smsMoPp;
        }

        /** 
         * Set the 'smsMoPp' element value.
         * 
         * @param smsMoPp
         */
        public void setSmsMoPp(String smsMoPp) {
            this.smsMoPp = smsMoPp;
        }

        /** 
         * Get the 'callhold' element value.
         * 
         * @return value
         */
        public String getCallhold() {
            return callhold;
        }

        /** 
         * Set the 'callhold' element value.
         * 
         * @param callhold
         */
        public void setCallhold(String callhold) {
            this.callhold = callhold;
        }

        /** 
         * Get the 'callwait' element value.
         * 
         * @return value
         */
        public String getCallwait() {
            return callwait;
        }

        /** 
         * Set the 'callwait' element value.
         * 
         * @param callwait
         */
        public void setCallwait(String callwait) {
            this.callwait = callwait;
        }

        /** 
         * Get the 'multiParty' element value.
         * 
         * @return value
         */
        public String getMultiParty() {
            return multiParty;
        }

        /** 
         * Set the 'multiParty' element value.
         * 
         * @param multiParty
         */
        public void setMultiParty(String multiParty) {
            this.multiParty = multiParty;
        }

        /** 
         * Get the 'IntRoam' element value.
         * 
         * @return value
         */
        public String getIntRoam() {
            return intRoam;
        }

        /** 
         * Set the 'IntRoam' element value.
         * 
         * @param intRoam
         */
        public void setIntRoam(String intRoam) {
            this.intRoam = intRoam;
        }

        /** 
         * Get the 'clip' element value.
         * 
         * @return value
         */
        public String getClip() {
            return clip;
        }

        /** 
         * Set the 'clip' element value.
         * 
         * @param clip
         */
        public void setClip(String clip) {
            this.clip = clip;
        }

        /** 
         * Get the 'VdioCall' element value.
         * 
         * @return value
         */
        public String getVdioCall() {
            return vdioCall;
        }

        /** 
         * Set the 'VdioCall' element value.
         * 
         * @param vdioCall
         */
        public void setVdioCall(String vdioCall) {
            this.vdioCall = vdioCall;
        }

        /** 
         * Get the 'DuCS' element value.
         * 
         * @return value
         */
        public String getDuCS() {
            return duCS;
        }

        /** 
         * Set the 'DuCS' element value.
         * 
         * @param duCS
         */
        public void setDuCS(String duCS) {
            this.duCS = duCS;
        }

        /** 
         * Get the 'BSCS_account_num' element value.
         * 
         * @return value
         */
        public String getBSCSAccountNum() {
            return BSCSAccountNum;
        }

        /** 
         * Set the 'BSCS_account_num' element value.
         * 
         * @param BSCSAccountNum
         */
        public void setBSCSAccountNum(String BSCSAccountNum) {
            this.BSCSAccountNum = BSCSAccountNum;
        }

        /** 
         * Get the 'CS_notification_num' element value.
         * 
         * @return value
         */
        public String getCSNotificationNum() {
            return CSNotificationNum;
        }

        /** 
         * Set the 'CS_notification_num' element value.
         * 
         * @param CSNotificationNum
         */
        public void setCSNotificationNum(String CSNotificationNum) {
            this.CSNotificationNum = CSNotificationNum;
        }

        /** 
         * Get the 'cbGraceAct' element value.
         * 
         * @return value
         */
        public String getCbGraceAct() {
            return cbGraceAct;
        }

        /** 
         * Set the 'cbGraceAct' element value.
         * 
         * @param cbGraceAct
         */
        public void setCbGraceAct(String cbGraceAct) {
            this.cbGraceAct = cbGraceAct;
        }

        /** 
         * Get the 'cbGraceEnd' element value.
         * 
         * @return value
         */
        public String getCbGraceEnd() {
            return cbGraceEnd;
        }

        /** 
         * Set the 'cbGraceEnd' element value.
         * 
         * @param cbGraceEnd
         */
        public void setCbGraceEnd(String cbGraceEnd) {
            this.cbGraceEnd = cbGraceEnd;
        }

        /** 
         * Get the 'cbGraceRenew' element value.
         * 
         * @return value
         */
        public String getCbGraceRenew() {
            return cbGraceRenew;
        }

        /** 
         * Set the 'cbGraceRenew' element value.
         * 
         * @param cbGraceRenew
         */
        public void setCbGraceRenew(String cbGraceRenew) {
            this.cbGraceRenew = cbGraceRenew;
        }

        /** 
         * Get the 'gprsCarrier' element value.
         * 
         * @return value
         */
        public String getGprsCarrier() {
            return gprsCarrier;
        }

        /** 
         * Set the 'gprsCarrier' element value.
         * 
         * @param gprsCarrier
         */
        public void setGprsCarrier(String gprsCarrier) {
            this.gprsCarrier = gprsCarrier;
        }

        /** 
         * Get the 'PAYGDataLineOffer' element value.
         * 
         * @return value
         */
        public String getPAYGDataLineOffer() {
            return PAYGDataLineOffer;
        }

        /** 
         * Set the 'PAYGDataLineOffer' element value.
         * 
         * @param PAYGDataLineOffer
         */
        public void setPAYGDataLineOffer(String PAYGDataLineOffer) {
            this.PAYGDataLineOffer = PAYGDataLineOffer;
        }

        /** 
         * Get the 'PAYGDataWHSP' element value.
         * 
         * @return value
         */
        public String getPAYGDataWHSP() {
            return PAYGDataWHSP;
        }

        /** 
         * Set the 'PAYGDataWHSP' element value.
         * 
         * @param PAYGDataWHSP
         */
        public void setPAYGDataWHSP(String PAYGDataWHSP) {
            this.PAYGDataWHSP = PAYGDataWHSP;
        }

        /** 
         * Get the 'PAYGDataWH' element value.
         * 
         * @return value
         */
        public String getPAYGDataWH() {
            return PAYGDataWH;
        }

        /** 
         * Set the 'PAYGDataWH' element value.
         * 
         * @param PAYGDataWH
         */
        public void setPAYGDataWH(String PAYGDataWH) {
            this.PAYGDataWH = PAYGDataWH;
        }

        /** 
         * Get the 'msisdnChgDate' element value.
         * 
         * @return value
         */
        public String getMsisdnChgDate() {
            return msisdnChgDate;
        }

        /** 
         * Set the 'msisdnChgDate' element value.
         * 
         * @param msisdnChgDate
         */
        public void setMsisdnChgDate(String msisdnChgDate) {
            this.msisdnChgDate = msisdnChgDate;
        }

        /** 
         * Get the 'mainMSISDN' element value.
         * 
         * @return value
         */
        public String getMainMSISDN() {
            return mainMSISDN;
        }

        /** 
         * Set the 'mainMSISDN' element value.
         * 
         * @param mainMSISDN
         */
        public void setMainMSISDN(String mainMSISDN) {
            this.mainMSISDN = mainMSISDN;
        }

        /** 
         * Get the 'CashReturn' element value.
         * 
         * @return value
         */
        public String getCashReturn() {
            return cashReturn;
        }

        /** 
         * Set the 'CashReturn' element value.
         * 
         * @param cashReturn
         */
        public void setCashReturn(String cashReturn) {
            this.cashReturn = cashReturn;
        }

        /** 
         * Get the 'NewTopUpValue' element value.
         * 
         * @return value
         */
        public String getNewTopUpValue() {
            return newTopUpValue;
        }

        /** 
         * Set the 'NewTopUpValue' element value.
         * 
         * @param newTopUpValue
         */
        public void setNewTopUpValue(String newTopUpValue) {
            this.newTopUpValue = newTopUpValue;
        }

        /** 
         * Get the 'bdgtCntrlTopUp' element value.
         * 
         * @return value
         */
        public String getBdgtCntrlTopUp() {
            return bdgtCntrlTopUp;
        }

        /** 
         * Set the 'bdgtCntrlTopUp' element value.
         * 
         * @param bdgtCntrlTopUp
         */
        public void setBdgtCntrlTopUp(String bdgtCntrlTopUp) {
            this.bdgtCntrlTopUp = bdgtCntrlTopUp;
        }

        /** 
         * Get the 'BlckBrryBundle' element value.
         * 
         * @return value
         */
        public String getBlckBrryBundle() {
            return blckBrryBundle;
        }

        /** 
         * Set the 'BlckBrryBundle' element value.
         * 
         * @param blckBrryBundle
         */
        public void setBlckBrryBundle(String blckBrryBundle) {
            this.blckBrryBundle = blckBrryBundle;
        }

        /** 
         * Get the 'MBB10GBWelcome' element value.
         * 
         * @return value
         */
        public String getMBB10GBWelcome() {
            return MBB10GBWelcome;
        }

        /** 
         * Set the 'MBB10GBWelcome' element value.
         * 
         * @param MBB10GBWelcome
         */
        public void setMBB10GBWelcome(String MBB10GBWelcome) {
            this.MBB10GBWelcome = MBB10GBWelcome;
        }

        /** 
         * Get the 'MBB2GBWelcome' element value.
         * 
         * @return value
         */
        public String getMBB2GBWelcome() {
            return MBB2GBWelcome;
        }

        /** 
         * Set the 'MBB2GBWelcome' element value.
         * 
         * @param MBB2GBWelcome
         */
        public void setMBB2GBWelcome(String MBB2GBWelcome) {
            this.MBB2GBWelcome = MBB2GBWelcome;
        }

        /** 
         * Get the 'MBBUnlimWelcome' element value.
         * 
         * @return value
         */
        public String getMBBUnlimWelcome() {
            return MBBUnlimWelcome;
        }

        /** 
         * Set the 'MBBUnlimWelcome' element value.
         * 
         * @param MBBUnlimWelcome
         */
        public void setMBBUnlimWelcome(String MBBUnlimWelcome) {
            this.MBBUnlimWelcome = MBBUnlimWelcome;
        }

        /** 
         * Get the 'MBBWelcome' element value.
         * 
         * @return value
         */
        public String getMBBWelcome() {
            return MBBWelcome;
        }

        /** 
         * Set the 'MBBWelcome' element value.
         * 
         * @param MBBWelcome
         */
        public void setMBBWelcome(String MBBWelcome) {
            this.MBBWelcome = MBBWelcome;
        }

        /** 
         * Get the 'guiBalAdjCount' element value.
         * 
         * @return value
         */
        public String getGuiBalAdjCount() {
            return guiBalAdjCount;
        }

        /** 
         * Set the 'guiBalAdjCount' element value.
         * 
         * @param guiBalAdjCount
         */
        public void setGuiBalAdjCount(String guiBalAdjCount) {
            this.guiBalAdjCount = guiBalAdjCount;
        }

        /** 
         * Get the 'LastChangeDateExpiry' element value.
         * 
         * @return value
         */
        public String getLastChangeDateExpiry() {
            return lastChangeDateExpiry;
        }

        /** 
         * Set the 'LastChangeDateExpiry' element value.
         * 
         * @param lastChangeDateExpiry
         */
        public void setLastChangeDateExpiry(String lastChangeDateExpiry) {
            this.lastChangeDateExpiry = lastChangeDateExpiry;
        }

        /** 
         * Get the 'NewPPBundle' element value.
         * 
         * @return value
         */
        public String getNewPPBundle() {
            return newPPBundle;
        }

        /** 
         * Set the 'NewPPBundle' element value.
         * 
         * @param newPPBundle
         */
        public void setNewPPBundle(String newPPBundle) {
            this.newPPBundle = newPPBundle;
        }

        /** 
         * Get the 'SmsBndlType' element value.
         * 
         * @return value
         */
        public String getSmsBndlType() {
            return smsBndlType;
        }

        /** 
         * Set the 'SmsBndlType' element value.
         * 
         * @param smsBndlType
         */
        public void setSmsBndlType(String smsBndlType) {
            this.smsBndlType = smsBndlType;
        }

        /** 
         * Get the 'PCN1stCall' element value.
         * 
         * @return value
         */
        public String getPCN1stCall() {
            return PCN1stCall;
        }

        /** 
         * Set the 'PCN1stCall' element value.
         * 
         * @param PCN1stCall
         */
        public void setPCN1stCall(String PCN1stCall) {
            this.PCN1stCall = PCN1stCall;
        }

        /** 
         * Get the 'RLHAct_ReqDate' element value.
         * 
         * @return value
         */
        public String getRLHActReqDate() {
            return RLHActReqDate;
        }

        /** 
         * Set the 'RLHAct_ReqDate' element value.
         * 
         * @param RLHActReqDate
         */
        public void setRLHActReqDate(String RLHActReqDate) {
            this.RLHActReqDate = RLHActReqDate;
        }

        /** 
         * Get the 'RLHDeact_Date' element value.
         * 
         * @return value
         */
        public String getRLHDeactDate() {
            return RLHDeactDate;
        }

        /** 
         * Set the 'RLHDeact_Date' element value.
         * 
         * @param RLHDeactDate
         */
        public void setRLHDeactDate(String RLHDeactDate) {
            this.RLHDeactDate = RLHDeactDate;
        }

        /** 
         * Get the 'entPDIntNum' element value.
         * 
         * @return value
         */
        public String getEntPDIntNum() {
            return entPDIntNum;
        }

        /** 
         * Set the 'entPDIntNum' element value.
         * 
         * @param entPDIntNum
         */
        public void setEntPDIntNum(String entPDIntNum) {
            this.entPDIntNum = entPDIntNum;
        }

        /** 
         * Get the 'AloRchgBonusOptinDt' element value.
         * 
         * @return value
         */
        public String getAloRchgBonusOptinDt() {
            return aloRchgBonusOptinDt;
        }

        /** 
         * Set the 'AloRchgBonusOptinDt' element value.
         * 
         * @param aloRchgBonusOptinDt
         */
        public void setAloRchgBonusOptinDt(String aloRchgBonusOptinDt) {
            this.aloRchgBonusOptinDt = aloRchgBonusOptinDt;
        }

        /** 
         * Get the 'Customer_ID' element value.
         * 
         * @return value
         */
        public String getCustomerID() {
            return customerID;
        }

        /** 
         * Set the 'Customer_ID' element value.
         * 
         * @param customerID
         */
        public void setCustomerID(String customerID) {
            this.customerID = customerID;
        }

        /** 
         * Get the 'IDDBundleActDate' element value.
         * 
         * @return value
         */
        public String getIDDBundleActDate() {
            return IDDBundleActDate;
        }

        /** 
         * Set the 'IDDBundleActDate' element value.
         * 
         * @param IDDBundleActDate
         */
        public void setIDDBundleActDate(String IDDBundleActDate) {
            this.IDDBundleActDate = IDDBundleActDate;
        }

        /** 
         * Get the 'ICPBundle' element value.
         * 
         * @return value
         */
        public String getICPBundle() {
            return ICPBundle;
        }

        /** 
         * Set the 'ICPBundle' element value.
         * 
         * @param ICPBundle
         */
        public void setICPBundle(String ICPBundle) {
            this.ICPBundle = ICPBundle;
        }

        /** 
         * Get the 'Mercury2DOptinoutDate' element value.
         * 
         * @return value
         */
        public String getMercury2DOptinoutDate() {
            return mercury2DOptinoutDate;
        }

        /** 
         * Set the 'Mercury2DOptinoutDate' element value.
         * 
         * @param mercury2DOptinoutDate
         */
        public void setMercury2DOptinoutDate(String mercury2DOptinoutDate) {
            this.mercury2DOptinoutDate = mercury2DOptinoutDate;
        }

        /** 
         * Get the 'Mercury2MOptinoutDate' element value.
         * 
         * @return value
         */
        public String getMercury2MOptinoutDate() {
            return mercury2MOptinoutDate;
        }

        /** 
         * Set the 'Mercury2MOptinoutDate' element value.
         * 
         * @param mercury2MOptinoutDate
         */
        public void setMercury2MOptinoutDate(String mercury2MOptinoutDate) {
            this.mercury2MOptinoutDate = mercury2MOptinoutDate;
        }

        /** 
         * Get the 'FamSponsr' element value.
         * 
         * @return value
         */
        public String getFamSponsr() {
            return famSponsr;
        }

        /** 
         * Set the 'FamSponsr' element value.
         * 
         * @param famSponsr
         */
        public void setFamSponsr(String famSponsr) {
            this.famSponsr = famSponsr;
        }

        /** 
         * Get the 'FamsponsorEtisalat' element value.
         * 
         * @return value
         */
        public String getFamsponsorEtisalat() {
            return famsponsorEtisalat;
        }

        /** 
         * Set the 'FamsponsorEtisalat' element value.
         * 
         * @param famsponsorEtisalat
         */
        public void setFamsponsorEtisalat(String famsponsorEtisalat) {
            this.famsponsorEtisalat = famsponsorEtisalat;
        }

        /** 
         * Get the 'NPPSocBndlDAPAct' element value.
         * 
         * @return value
         */
        public String getNPPSocBndlDAPAct() {
            return NPPSocBndlDAPAct;
        }

        /** 
         * Set the 'NPPSocBndlDAPAct' element value.
         * 
         * @param NPPSocBndlDAPAct
         */
        public void setNPPSocBndlDAPAct(String NPPSocBndlDAPAct) {
            this.NPPSocBndlDAPAct = NPPSocBndlDAPAct;
        }

        /** 
         * Get the 'NPPBISDAPMod' element value.
         * 
         * @return value
         */
        public String getNPPBISDAPMod() {
            return NPPBISDAPMod;
        }

        /** 
         * Set the 'NPPBISDAPMod' element value.
         * 
         * @param NPPBISDAPMod
         */
        public void setNPPBISDAPMod(String NPPBISDAPMod) {
            this.NPPBISDAPMod = NPPBISDAPMod;
        }

        /** 
         * Get the 'NPPBBSocialDAPMod' element value.
         * 
         * @return value
         */
        public String getNPPBBSocialDAPMod() {
            return NPPBBSocialDAPMod;
        }

        /** 
         * Set the 'NPPBBSocialDAPMod' element value.
         * 
         * @param NPPBBSocialDAPMod
         */
        public void setNPPBBSocialDAPMod(String NPPBBSocialDAPMod) {
            this.NPPBBSocialDAPMod = NPPBBSocialDAPMod;
        }

        /** 
         * Get the 'NPPBBSocialDAPDeact' element value.
         * 
         * @return value
         */
        public String getNPPBBSocialDAPDeact() {
            return NPPBBSocialDAPDeact;
        }

        /** 
         * Set the 'NPPBBSocialDAPDeact' element value.
         * 
         * @param NPPBBSocialDAPDeact
         */
        public void setNPPBBSocialDAPDeact(String NPPBBSocialDAPDeact) {
            this.NPPBBSocialDAPDeact = NPPBBSocialDAPDeact;
        }

        /** 
         * Get the 'MercuryDOptinDate' element value.
         * 
         * @return value
         */
        public String getMercuryDOptinDate() {
            return mercuryDOptinDate;
        }

        /** 
         * Set the 'MercuryDOptinDate' element value.
         * 
         * @param mercuryDOptinDate
         */
        public void setMercuryDOptinDate(String mercuryDOptinDate) {
            this.mercuryDOptinDate = mercuryDOptinDate;
        }

        /** 
         * Get the 'MercuryDOptoutDate' element value.
         * 
         * @return value
         */
        public String getMercuryDOptoutDate() {
            return mercuryDOptoutDate;
        }

        /** 
         * Set the 'MercuryDOptoutDate' element value.
         * 
         * @param mercuryDOptoutDate
         */
        public void setMercuryDOptoutDate(String mercuryDOptoutDate) {
            this.mercuryDOptoutDate = mercuryDOptoutDate;
        }

        /** 
         * Get the 'CVMDeactDate' element value.
         * 
         * @return value
         */
        public String getCVMDeactDate() {
            return CVMDeactDate;
        }

        /** 
         * Set the 'CVMDeactDate' element value.
         * 
         * @param CVMDeactDate
         */
        public void setCVMDeactDate(String CVMDeactDate) {
            this.CVMDeactDate = CVMDeactDate;
        }

        /** 
         * Get the 'IDDRateCuttersOptinDate' element value.
         * 
         * @return value
         */
        public String getIDDRateCuttersOptinDate() {
            return IDDRateCuttersOptinDate;
        }

        /** 
         * Set the 'IDDRateCuttersOptinDate' element value.
         * 
         * @param IDDRateCuttersOptinDate
         */
        public void setIDDRateCuttersOptinDate(String IDDRateCuttersOptinDate) {
            this.IDDRateCuttersOptinDate = IDDRateCuttersOptinDate;
        }

        /** 
         * Get the 'IDDRateCuttersOptoutDate' element value.
         * 
         * @return value
         */
        public String getIDDRateCuttersOptoutDate() {
            return IDDRateCuttersOptoutDate;
        }

        /** 
         * Set the 'IDDRateCuttersOptoutDate' element value.
         * 
         * @param IDDRateCuttersOptoutDate
         */
        public void setIDDRateCuttersOptoutDate(String IDDRateCuttersOptoutDate) {
            this.IDDRateCuttersOptoutDate = IDDRateCuttersOptoutDate;
        }

        /** 
         * Get the 'NonstopSocActDate' element value.
         * 
         * @return value
         */
        public String getNonstopSocActDate() {
            return nonstopSocActDate;
        }

        /** 
         * Set the 'NonstopSocActDate' element value.
         * 
         * @param nonstopSocActDate
         */
        public void setNonstopSocActDate(String nonstopSocActDate) {
            this.nonstopSocActDate = nonstopSocActDate;
        }

        /** 
         * Get the 'NonstopSocDeactDate' element value.
         * 
         * @return value
         */
        public String getNonstopSocDeactDate() {
            return nonstopSocDeactDate;
        }

        /** 
         * Set the 'NonstopSocDeactDate' element value.
         * 
         * @param nonstopSocDeactDate
         */
        public void setNonstopSocDeactDate(String nonstopSocDeactDate) {
            this.nonstopSocDeactDate = nonstopSocDeactDate;
        }

        /** 
         * Get the 'OWFnFLabel' element value.
         * 
         * @return value
         */
        public String getOWFnFLabel() {
            return OWFnFLabel;
        }

        /** 
         * Set the 'OWFnFLabel' element value.
         * 
         * @param OWFnFLabel
         */
        public void setOWFnFLabel(String OWFnFLabel) {
            this.OWFnFLabel = OWFnFLabel;
        }

        /** 
         * Get the 'OneWayFnF' element value.
         * 
         * @return value
         */
        public String getOneWayFnF() {
            return oneWayFnF;
        }

        /** 
         * Set the 'OneWayFnF' element value.
         * 
         * @param oneWayFnF
         */
        public void setOneWayFnF(String oneWayFnF) {
            this.oneWayFnF = oneWayFnF;
        }

        /** 
         * Get the 'OneWayFnFModCount' element value.
         * 
         * @return value
         */
        public String getOneWayFnFModCount() {
            return oneWayFnFModCount;
        }

        /** 
         * Set the 'OneWayFnFModCount' element value.
         * 
         * @param oneWayFnFModCount
         */
        public void setOneWayFnFModCount(String oneWayFnFModCount) {
            this.oneWayFnFModCount = oneWayFnFModCount;
        }

        /** 
         * Get the 'AutoRechargeValue' element value.
         * 
         * @return value
         */
        public String getAutoRechargeValue() {
            return autoRechargeValue;
        }

        /** 
         * Set the 'AutoRechargeValue' element value.
         * 
         * @param autoRechargeValue
         */
        public void setAutoRechargeValue(String autoRechargeValue) {
            this.autoRechargeValue = autoRechargeValue;
        }
    }
}
