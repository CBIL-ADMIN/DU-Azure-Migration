
package com.ericsson.jibx.beans;

import java.util.ArrayList;
import java.util.List;

/** 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:element xmlns:xs="http://www.w3.org/2001/XMLSchema" name="DEFAULTSERVICES_MAPPING_LIST">
 *   &lt;xs:complexType>
 *     &lt;xs:sequence>
 *       &lt;xs:element name="DEFAULTSERVICES_MAPPING_INFO" minOccurs="0" maxOccurs="unbounded">
 *         &lt;!-- Reference to inner class DEFAULTSERVICESMAPPINGINFO -->
 *       &lt;/xs:element>
 *     &lt;/xs:sequence>
 *   &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 */
public class DEFAULTSERVICESMAPPINGLIST
{
    private List<DEFAULTSERVICESMAPPINGINFO> DEFAULTSERVICESMAPPINGINFOList = new ArrayList<DEFAULTSERVICESMAPPINGINFO>();

    /** 
     * Get the list of 'DEFAULTSERVICES_MAPPING_INFO' element items.
     * 
     * @return list
     */
    public List<DEFAULTSERVICESMAPPINGINFO> getDEFAULTSERVICESMAPPINGINFOList() {
        return DEFAULTSERVICESMAPPINGINFOList;
    }

    /** 
     * Set the list of 'DEFAULTSERVICES_MAPPING_INFO' element items.
     * 
     * @param list
     */
    public void setDEFAULTSERVICESMAPPINGINFOList(
            List<DEFAULTSERVICESMAPPINGINFO> list) {
        DEFAULTSERVICESMAPPINGINFOList = list;
    }
    /** 
     * Schema fragment(s) for this class:
     * <pre>
     * &lt;xs:element xmlns:xs="http://www.w3.org/2001/XMLSchema" name="DEFAULTSERVICES_MAPPING_INFO" minOccurs="0" maxOccurs="unbounded">
     *   &lt;xs:complexType>
     *     &lt;xs:sequence>
     *       &lt;xs:element type="xs:string" name="Default_ID"/>
     *       &lt;xs:element type="xs:string" name="Ignore_Flag"/>
     *       &lt;xs:element type="xs:string" name="OFFER_Always_ON_Never_Expiry"/>
     *       &lt;xs:element type="xs:string" name="DA_Always_ON_Never_Expiry"/>
     *       &lt;xs:element type="xs:string" name="Is_DA_Default"/>
     *       &lt;xs:element type="xs:string" name="Is_Attr_Default"/>
     *       &lt;xs:element type="xs:string" name="Offer_Id" minOccurs="0"/>
     *       &lt;xs:element type="xs:string" name="Offer_Type" minOccurs="0"/>
     *       &lt;xs:element type="xs:string" name="Offer_Rule_Id" minOccurs="0"/>
     *       &lt;xs:element type="xs:string" name="Attr_Offer_Id" minOccurs="0"/>
     *       &lt;xs:element type="xs:string" name="Attr_Name" minOccurs="0"/>
     *       &lt;xs:element type="xs:string" name="Attr_Type" minOccurs="0"/>
     *       &lt;xs:element type="xs:string" name="Attr_Source" minOccurs="0"/>
     *       &lt;xs:element type="xs:string" name="PAM_Class_ID" minOccurs="0"/>
     *       &lt;xs:element type="xs:string" name="PAM_Service_ID" minOccurs="0"/>
     *       &lt;xs:element type="xs:string" name="Schedule_ID" minOccurs="0"/>
     *       &lt;xs:element type="xs:string" name="Current_PAM_Period" minOccurs="0"/>
     *       &lt;xs:element type="xs:string" name="Resource" minOccurs="0"/>
     *       &lt;xs:element type="xs:string" name="DA_ID" minOccurs="0"/>
     *       &lt;xs:element type="xs:string" name="DA_Type" minOccurs="0"/>
     *       &lt;xs:element type="xs:string" name="DA_Default_Value" minOccurs="0"/>
     *       &lt;xs:element type="xs:string" name="Offer_Start" minOccurs="0"/>
     *       &lt;xs:element type="xs:string" name="UA_ID" minOccurs="0"/>
     *       &lt;xs:element type="xs:string" name="UA_Value" minOccurs="0"/>
     *     &lt;/xs:sequence>
     *   &lt;/xs:complexType>
     * &lt;/xs:element>
     * </pre>
     */
    public static class DEFAULTSERVICESMAPPINGINFO
    {
        private String defaultID;
        private String ignoreFlag;
        private String OFFERAlwaysONNeverExpiry;
        private String DAAlwaysONNeverExpiry;
        private String isDADefault;
        private String isAttrDefault;
        private String offerId;
        private String offerType;
        private String offerRuleId;
        private String attrOfferId;
        private String attrName;
        private String attrType;
        private String attrSource;
        private String PAMClassID;
        private String PAMServiceID;
        private String scheduleID;
        private String currentPAMPeriod;
        private String resource;
        private String DAID;
        private String DAType;
        private String DADefaultValue;
        private String offerStart;
        private String UAID;
        private String UAValue;

        /** 
         * Get the 'Default_ID' element value.
         * 
         * @return value
         */
        public String getDefaultID() {
            return defaultID;
        }

        /** 
         * Set the 'Default_ID' element value.
         * 
         * @param defaultID
         */
        public void setDefaultID(String defaultID) {
            this.defaultID = defaultID;
        }

        /** 
         * Get the 'Ignore_Flag' element value.
         * 
         * @return value
         */
        public String getIgnoreFlag() {
            return ignoreFlag;
        }

        /** 
         * Set the 'Ignore_Flag' element value.
         * 
         * @param ignoreFlag
         */
        public void setIgnoreFlag(String ignoreFlag) {
            this.ignoreFlag = ignoreFlag;
        }

        /** 
         * Get the 'OFFER_Always_ON_Never_Expiry' element value.
         * 
         * @return value
         */
        public String getOFFERAlwaysONNeverExpiry() {
            return OFFERAlwaysONNeverExpiry;
        }

        /** 
         * Set the 'OFFER_Always_ON_Never_Expiry' element value.
         * 
         * @param OFFERAlwaysONNeverExpiry
         */
        public void setOFFERAlwaysONNeverExpiry(String OFFERAlwaysONNeverExpiry) {
            this.OFFERAlwaysONNeverExpiry = OFFERAlwaysONNeverExpiry;
        }

        /** 
         * Get the 'DA_Always_ON_Never_Expiry' element value.
         * 
         * @return value
         */
        public String getDAAlwaysONNeverExpiry() {
            return DAAlwaysONNeverExpiry;
        }

        /** 
         * Set the 'DA_Always_ON_Never_Expiry' element value.
         * 
         * @param DAAlwaysONNeverExpiry
         */
        public void setDAAlwaysONNeverExpiry(String DAAlwaysONNeverExpiry) {
            this.DAAlwaysONNeverExpiry = DAAlwaysONNeverExpiry;
        }

        /** 
         * Get the 'Is_DA_Default' element value.
         * 
         * @return value
         */
        public String getIsDADefault() {
            return isDADefault;
        }

        /** 
         * Set the 'Is_DA_Default' element value.
         * 
         * @param isDADefault
         */
        public void setIsDADefault(String isDADefault) {
            this.isDADefault = isDADefault;
        }

        /** 
         * Get the 'Is_Attr_Default' element value.
         * 
         * @return value
         */
        public String getIsAttrDefault() {
            return isAttrDefault;
        }

        /** 
         * Set the 'Is_Attr_Default' element value.
         * 
         * @param isAttrDefault
         */
        public void setIsAttrDefault(String isAttrDefault) {
            this.isAttrDefault = isAttrDefault;
        }

        /** 
         * Get the 'Offer_Id' element value.
         * 
         * @return value
         */
        public String getOfferId() {
            return offerId;
        }

        /** 
         * Set the 'Offer_Id' element value.
         * 
         * @param offerId
         */
        public void setOfferId(String offerId) {
            this.offerId = offerId;
        }

        /** 
         * Get the 'Offer_Type' element value.
         * 
         * @return value
         */
        public String getOfferType() {
            return offerType;
        }

        /** 
         * Set the 'Offer_Type' element value.
         * 
         * @param offerType
         */
        public void setOfferType(String offerType) {
            this.offerType = offerType;
        }

        /** 
         * Get the 'Offer_Rule_Id' element value.
         * 
         * @return value
         */
        public String getOfferRuleId() {
            return offerRuleId;
        }

        /** 
         * Set the 'Offer_Rule_Id' element value.
         * 
         * @param offerRuleId
         */
        public void setOfferRuleId(String offerRuleId) {
            this.offerRuleId = offerRuleId;
        }

        /** 
         * Get the 'Attr_Offer_Id' element value.
         * 
         * @return value
         */
        public String getAttrOfferId() {
            return attrOfferId;
        }

        /** 
         * Set the 'Attr_Offer_Id' element value.
         * 
         * @param attrOfferId
         */
        public void setAttrOfferId(String attrOfferId) {
            this.attrOfferId = attrOfferId;
        }

        /** 
         * Get the 'Attr_Name' element value.
         * 
         * @return value
         */
        public String getAttrName() {
            return attrName;
        }

        /** 
         * Set the 'Attr_Name' element value.
         * 
         * @param attrName
         */
        public void setAttrName(String attrName) {
            this.attrName = attrName;
        }

        /** 
         * Get the 'Attr_Type' element value.
         * 
         * @return value
         */
        public String getAttrType() {
            return attrType;
        }

        /** 
         * Set the 'Attr_Type' element value.
         * 
         * @param attrType
         */
        public void setAttrType(String attrType) {
            this.attrType = attrType;
        }

        /** 
         * Get the 'Attr_Source' element value.
         * 
         * @return value
         */
        public String getAttrSource() {
            return attrSource;
        }

        /** 
         * Set the 'Attr_Source' element value.
         * 
         * @param attrSource
         */
        public void setAttrSource(String attrSource) {
            this.attrSource = attrSource;
        }

        /** 
         * Get the 'PAM_Class_ID' element value.
         * 
         * @return value
         */
        public String getPAMClassID() {
            return PAMClassID;
        }

        /** 
         * Set the 'PAM_Class_ID' element value.
         * 
         * @param PAMClassID
         */
        public void setPAMClassID(String PAMClassID) {
            this.PAMClassID = PAMClassID;
        }

        /** 
         * Get the 'PAM_Service_ID' element value.
         * 
         * @return value
         */
        public String getPAMServiceID() {
            return PAMServiceID;
        }

        /** 
         * Set the 'PAM_Service_ID' element value.
         * 
         * @param PAMServiceID
         */
        public void setPAMServiceID(String PAMServiceID) {
            this.PAMServiceID = PAMServiceID;
        }

        /** 
         * Get the 'Schedule_ID' element value.
         * 
         * @return value
         */
        public String getScheduleID() {
            return scheduleID;
        }

        /** 
         * Set the 'Schedule_ID' element value.
         * 
         * @param scheduleID
         */
        public void setScheduleID(String scheduleID) {
            this.scheduleID = scheduleID;
        }

        /** 
         * Get the 'Current_PAM_Period' element value.
         * 
         * @return value
         */
        public String getCurrentPAMPeriod() {
            return currentPAMPeriod;
        }

        /** 
         * Set the 'Current_PAM_Period' element value.
         * 
         * @param currentPAMPeriod
         */
        public void setCurrentPAMPeriod(String currentPAMPeriod) {
            this.currentPAMPeriod = currentPAMPeriod;
        }

        /** 
         * Get the 'Resource' element value.
         * 
         * @return value
         */
        public String getResource() {
            return resource;
        }

        /** 
         * Set the 'Resource' element value.
         * 
         * @param resource
         */
        public void setResource(String resource) {
            this.resource = resource;
        }

        /** 
         * Get the 'DA_ID' element value.
         * 
         * @return value
         */
        public String getDAID() {
            return DAID;
        }

        /** 
         * Set the 'DA_ID' element value.
         * 
         * @param DAID
         */
        public void setDAID(String DAID) {
            this.DAID = DAID;
        }

        /** 
         * Get the 'DA_Type' element value.
         * 
         * @return value
         */
        public String getDAType() {
            return DAType;
        }

        /** 
         * Set the 'DA_Type' element value.
         * 
         * @param DAType
         */
        public void setDAType(String DAType) {
            this.DAType = DAType;
        }

        /** 
         * Get the 'DA_Default_Value' element value.
         * 
         * @return value
         */
        public String getDADefaultValue() {
            return DADefaultValue;
        }

        /** 
         * Set the 'DA_Default_Value' element value.
         * 
         * @param DADefaultValue
         */
        public void setDADefaultValue(String DADefaultValue) {
            this.DADefaultValue = DADefaultValue;
        }

        /** 
         * Get the 'Offer_Start' element value.
         * 
         * @return value
         */
        public String getOfferStart() {
            return offerStart;
        }

        /** 
         * Set the 'Offer_Start' element value.
         * 
         * @param offerStart
         */
        public void setOfferStart(String offerStart) {
            this.offerStart = offerStart;
        }

        /** 
         * Get the 'UA_ID' element value.
         * 
         * @return value
         */
        public String getUAID() {
            return UAID;
        }

        /** 
         * Set the 'UA_ID' element value.
         * 
         * @param UAID
         */
        public void setUAID(String UAID) {
            this.UAID = UAID;
        }

        /** 
         * Get the 'UA_Value' element value.
         * 
         * @return value
         */
        public String getUAValue() {
            return UAValue;
        }

        /** 
         * Set the 'UA_Value' element value.
         * 
         * @param UAValue
         */
        public void setUAValue(String UAValue) {
            this.UAValue = UAValue;
        }
    }
}
