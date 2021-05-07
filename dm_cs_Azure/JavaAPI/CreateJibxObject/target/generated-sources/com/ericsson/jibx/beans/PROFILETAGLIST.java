
package com.ericsson.jibx.beans;

import java.util.ArrayList;
import java.util.List;

/** 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:element xmlns:xs="http://www.w3.org/2001/XMLSchema" name="PROFILE_TAG_LIST">
 *   &lt;xs:complexType>
 *     &lt;xs:sequence>
 *       &lt;xs:element name="PROFILE_TAG_INFO" maxOccurs="unbounded">
 *         &lt;!-- Reference to inner class PROFILETAGINFO -->
 *       &lt;/xs:element>
 *     &lt;/xs:sequence>
 *   &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 */
public class PROFILETAGLIST
{
    private List<PROFILETAGINFO> PROFILETAGINFOList = new ArrayList<PROFILETAGINFO>();

    /** 
     * Get the list of 'PROFILE_TAG_INFO' element items.
     * 
     * @return list
     */
    public List<PROFILETAGINFO> getPROFILETAGINFOList() {
        return PROFILETAGINFOList;
    }

    /** 
     * Set the list of 'PROFILE_TAG_INFO' element items.
     * 
     * @param list
     */
    public void setPROFILETAGINFOList(List<PROFILETAGINFO> list) {
        PROFILETAGINFOList = list;
    }
    /** 
     * Schema fragment(s) for this class:
     * <pre>
     * &lt;xs:element xmlns:xs="http://www.w3.org/2001/XMLSchema" name="PROFILE_TAG_INFO" maxOccurs="unbounded">
     *   &lt;xs:complexType>
     *     &lt;xs:all>
     *       &lt;xs:element type="xs:string" name="Profile_Tag_Name"/>
     *       &lt;xs:element type="xs:string" name="Symbols"/>
     *       &lt;xs:element type="xs:string" name="Profile_Tag_Value"/>
     *       &lt;xs:element type="xs:string" name="PT_Group_Identifier"/>
     *       &lt;xs:element type="xs:string" name="Sub_State"/>
     *       &lt;xs:element type="xs:string" name="RatePlan_Operator"/>
     *       &lt;xs:element type="xs:string" name="RatePlan_ID"/>
     *       &lt;xs:element type="xs:string" name="Additional_PT_Check"/>
     *       &lt;xs:element type="xs:string" name="Resource"/>
     *       &lt;xs:element type="xs:string" name="Ignore_Flag"/>
     *       &lt;xs:element type="xs:string" name="Offer_Always_No_Never_Expiry"/>
     *       &lt;xs:element type="xs:string" name="Product_Private"/>
     *       &lt;xs:element type="xs:string" name="Offer_Id"/>
     *       &lt;xs:element type="xs:string" name="Offer_Type"/>
     *       &lt;xs:element type="xs:string" name="Offer_Start"/>
     *       &lt;xs:element type="xs:string" name="Offer_End"/>
     *       &lt;xs:element type="xs:string" name="Attr1_Offer_id"/>
     *       &lt;xs:element type="xs:string" name="Attr1_name"/>
     *       &lt;xs:element type="xs:string" name="Attr1_Type"/>
     *       &lt;xs:element type="xs:string" name="Attr1_Value"/>
     *       &lt;xs:element type="xs:string" name="UA_Type"/>
     *       &lt;xs:element type="xs:string" name="UA_ID"/>
     *       &lt;xs:element type="xs:string" name="UA_Value"/>
     *       &lt;xs:element type="xs:string" name="Faf_called_number"/>
     *       &lt;xs:element type="xs:string" name="Faf_Indicator"/>
     *       &lt;xs:element type="xs:string" name="CIS_Reference"/>
     *       &lt;xs:element type="xs:string" name="UC_Type"/>
     *       &lt;xs:element type="xs:string" name="UC_ID"/>
     *       &lt;xs:element type="xs:string" name="UT_ID"/>
     *       &lt;xs:element type="xs:string" name="UT_Value"/>
     *       &lt;xs:element type="xs:string" name="PAM_CLASS_ID"/>
     *       &lt;xs:element type="xs:string" name="PAM_SERV_ID"/>
     *       &lt;xs:element type="xs:string" name="Add_Offer"/>
     *       &lt;xs:element type="xs:string" name="Add_UA"/>
     *       &lt;xs:element type="xs:string" name="Add_DA"/>
     *       &lt;xs:element type="xs:string" name="Add_UC"/>
     *     &lt;/xs:all>
     *   &lt;/xs:complexType>
     * &lt;/xs:element>
     * </pre>
     */
    public static class PROFILETAGINFO
    {
        private String profileTagName;
        private String symbols;
        private String profileTagValue;
        private String PTGroupIdentifier;
        private String subState;
        private String ratePlanOperator;
        private String ratePlanID;
        private String additionalPTCheck;
        private String resource;
        private String ignoreFlag;
        private String offerAlwaysNoNeverExpiry;
        private String productPrivate;
        private String offerId;
        private String offerType;
        private String offerStart;
        private String offerEnd;
        private String attr1OfferId;
        private String attr1Name;
        private String attr1Type;
        private String attr1Value;
        private String UAType;
        private String UAID;
        private String UAValue;
        private String fafCalledNumber;
        private String fafIndicator;
        private String CISReference;
        private String UCType;
        private String UCID;
        private String UTID;
        private String UTValue;
        private String PAMCLASSID;
        private String PAMSERVID;
        private String addOffer;
        private String addUA;
        private String addDA;
        private String addUC;

        /** 
         * Get the 'Profile_Tag_Name' element value.
         * 
         * @return value
         */
        public String getProfileTagName() {
            return profileTagName;
        }

        /** 
         * Set the 'Profile_Tag_Name' element value.
         * 
         * @param profileTagName
         */
        public void setProfileTagName(String profileTagName) {
            this.profileTagName = profileTagName;
        }

        /** 
         * Get the 'Symbols' element value.
         * 
         * @return value
         */
        public String getSymbols() {
            return symbols;
        }

        /** 
         * Set the 'Symbols' element value.
         * 
         * @param symbols
         */
        public void setSymbols(String symbols) {
            this.symbols = symbols;
        }

        /** 
         * Get the 'Profile_Tag_Value' element value.
         * 
         * @return value
         */
        public String getProfileTagValue() {
            return profileTagValue;
        }

        /** 
         * Set the 'Profile_Tag_Value' element value.
         * 
         * @param profileTagValue
         */
        public void setProfileTagValue(String profileTagValue) {
            this.profileTagValue = profileTagValue;
        }

        /** 
         * Get the 'PT_Group_Identifier' element value.
         * 
         * @return value
         */
        public String getPTGroupIdentifier() {
            return PTGroupIdentifier;
        }

        /** 
         * Set the 'PT_Group_Identifier' element value.
         * 
         * @param PTGroupIdentifier
         */
        public void setPTGroupIdentifier(String PTGroupIdentifier) {
            this.PTGroupIdentifier = PTGroupIdentifier;
        }

        /** 
         * Get the 'Sub_State' element value.
         * 
         * @return value
         */
        public String getSubState() {
            return subState;
        }

        /** 
         * Set the 'Sub_State' element value.
         * 
         * @param subState
         */
        public void setSubState(String subState) {
            this.subState = subState;
        }

        /** 
         * Get the 'RatePlan_Operator' element value.
         * 
         * @return value
         */
        public String getRatePlanOperator() {
            return ratePlanOperator;
        }

        /** 
         * Set the 'RatePlan_Operator' element value.
         * 
         * @param ratePlanOperator
         */
        public void setRatePlanOperator(String ratePlanOperator) {
            this.ratePlanOperator = ratePlanOperator;
        }

        /** 
         * Get the 'RatePlan_ID' element value.
         * 
         * @return value
         */
        public String getRatePlanID() {
            return ratePlanID;
        }

        /** 
         * Set the 'RatePlan_ID' element value.
         * 
         * @param ratePlanID
         */
        public void setRatePlanID(String ratePlanID) {
            this.ratePlanID = ratePlanID;
        }

        /** 
         * Get the 'Additional_PT_Check' element value.
         * 
         * @return value
         */
        public String getAdditionalPTCheck() {
            return additionalPTCheck;
        }

        /** 
         * Set the 'Additional_PT_Check' element value.
         * 
         * @param additionalPTCheck
         */
        public void setAdditionalPTCheck(String additionalPTCheck) {
            this.additionalPTCheck = additionalPTCheck;
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
         * Get the 'Offer_Always_No_Never_Expiry' element value.
         * 
         * @return value
         */
        public String getOfferAlwaysNoNeverExpiry() {
            return offerAlwaysNoNeverExpiry;
        }

        /** 
         * Set the 'Offer_Always_No_Never_Expiry' element value.
         * 
         * @param offerAlwaysNoNeverExpiry
         */
        public void setOfferAlwaysNoNeverExpiry(String offerAlwaysNoNeverExpiry) {
            this.offerAlwaysNoNeverExpiry = offerAlwaysNoNeverExpiry;
        }

        /** 
         * Get the 'Product_Private' element value.
         * 
         * @return value
         */
        public String getProductPrivate() {
            return productPrivate;
        }

        /** 
         * Set the 'Product_Private' element value.
         * 
         * @param productPrivate
         */
        public void setProductPrivate(String productPrivate) {
            this.productPrivate = productPrivate;
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
         * Get the 'Offer_End' element value.
         * 
         * @return value
         */
        public String getOfferEnd() {
            return offerEnd;
        }

        /** 
         * Set the 'Offer_End' element value.
         * 
         * @param offerEnd
         */
        public void setOfferEnd(String offerEnd) {
            this.offerEnd = offerEnd;
        }

        /** 
         * Get the 'Attr1_Offer_id' element value.
         * 
         * @return value
         */
        public String getAttr1OfferId() {
            return attr1OfferId;
        }

        /** 
         * Set the 'Attr1_Offer_id' element value.
         * 
         * @param attr1OfferId
         */
        public void setAttr1OfferId(String attr1OfferId) {
            this.attr1OfferId = attr1OfferId;
        }

        /** 
         * Get the 'Attr1_name' element value.
         * 
         * @return value
         */
        public String getAttr1Name() {
            return attr1Name;
        }

        /** 
         * Set the 'Attr1_name' element value.
         * 
         * @param attr1Name
         */
        public void setAttr1Name(String attr1Name) {
            this.attr1Name = attr1Name;
        }

        /** 
         * Get the 'Attr1_Type' element value.
         * 
         * @return value
         */
        public String getAttr1Type() {
            return attr1Type;
        }

        /** 
         * Set the 'Attr1_Type' element value.
         * 
         * @param attr1Type
         */
        public void setAttr1Type(String attr1Type) {
            this.attr1Type = attr1Type;
        }

        /** 
         * Get the 'Attr1_Value' element value.
         * 
         * @return value
         */
        public String getAttr1Value() {
            return attr1Value;
        }

        /** 
         * Set the 'Attr1_Value' element value.
         * 
         * @param attr1Value
         */
        public void setAttr1Value(String attr1Value) {
            this.attr1Value = attr1Value;
        }

        /** 
         * Get the 'UA_Type' element value.
         * 
         * @return value
         */
        public String getUAType() {
            return UAType;
        }

        /** 
         * Set the 'UA_Type' element value.
         * 
         * @param UAType
         */
        public void setUAType(String UAType) {
            this.UAType = UAType;
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

        /** 
         * Get the 'Faf_called_number' element value.
         * 
         * @return value
         */
        public String getFafCalledNumber() {
            return fafCalledNumber;
        }

        /** 
         * Set the 'Faf_called_number' element value.
         * 
         * @param fafCalledNumber
         */
        public void setFafCalledNumber(String fafCalledNumber) {
            this.fafCalledNumber = fafCalledNumber;
        }

        /** 
         * Get the 'Faf_Indicator' element value.
         * 
         * @return value
         */
        public String getFafIndicator() {
            return fafIndicator;
        }

        /** 
         * Set the 'Faf_Indicator' element value.
         * 
         * @param fafIndicator
         */
        public void setFafIndicator(String fafIndicator) {
            this.fafIndicator = fafIndicator;
        }

        /** 
         * Get the 'CIS_Reference' element value.
         * 
         * @return value
         */
        public String getCISReference() {
            return CISReference;
        }

        /** 
         * Set the 'CIS_Reference' element value.
         * 
         * @param CISReference
         */
        public void setCISReference(String CISReference) {
            this.CISReference = CISReference;
        }

        /** 
         * Get the 'UC_Type' element value.
         * 
         * @return value
         */
        public String getUCType() {
            return UCType;
        }

        /** 
         * Set the 'UC_Type' element value.
         * 
         * @param UCType
         */
        public void setUCType(String UCType) {
            this.UCType = UCType;
        }

        /** 
         * Get the 'UC_ID' element value.
         * 
         * @return value
         */
        public String getUCID() {
            return UCID;
        }

        /** 
         * Set the 'UC_ID' element value.
         * 
         * @param UCID
         */
        public void setUCID(String UCID) {
            this.UCID = UCID;
        }

        /** 
         * Get the 'UT_ID' element value.
         * 
         * @return value
         */
        public String getUTID() {
            return UTID;
        }

        /** 
         * Set the 'UT_ID' element value.
         * 
         * @param UTID
         */
        public void setUTID(String UTID) {
            this.UTID = UTID;
        }

        /** 
         * Get the 'UT_Value' element value.
         * 
         * @return value
         */
        public String getUTValue() {
            return UTValue;
        }

        /** 
         * Set the 'UT_Value' element value.
         * 
         * @param UTValue
         */
        public void setUTValue(String UTValue) {
            this.UTValue = UTValue;
        }

        /** 
         * Get the 'PAM_CLASS_ID' element value.
         * 
         * @return value
         */
        public String getPAMCLASSID() {
            return PAMCLASSID;
        }

        /** 
         * Set the 'PAM_CLASS_ID' element value.
         * 
         * @param PAMCLASSID
         */
        public void setPAMCLASSID(String PAMCLASSID) {
            this.PAMCLASSID = PAMCLASSID;
        }

        /** 
         * Get the 'PAM_SERV_ID' element value.
         * 
         * @return value
         */
        public String getPAMSERVID() {
            return PAMSERVID;
        }

        /** 
         * Set the 'PAM_SERV_ID' element value.
         * 
         * @param PAMSERVID
         */
        public void setPAMSERVID(String PAMSERVID) {
            this.PAMSERVID = PAMSERVID;
        }

        /** 
         * Get the 'Add_Offer' element value.
         * 
         * @return value
         */
        public String getAddOffer() {
            return addOffer;
        }

        /** 
         * Set the 'Add_Offer' element value.
         * 
         * @param addOffer
         */
        public void setAddOffer(String addOffer) {
            this.addOffer = addOffer;
        }

        /** 
         * Get the 'Add_UA' element value.
         * 
         * @return value
         */
        public String getAddUA() {
            return addUA;
        }

        /** 
         * Set the 'Add_UA' element value.
         * 
         * @param addUA
         */
        public void setAddUA(String addUA) {
            this.addUA = addUA;
        }

        /** 
         * Get the 'Add_DA' element value.
         * 
         * @return value
         */
        public String getAddDA() {
            return addDA;
        }

        /** 
         * Set the 'Add_DA' element value.
         * 
         * @param addDA
         */
        public void setAddDA(String addDA) {
            this.addDA = addDA;
        }

        /** 
         * Get the 'Add_UC' element value.
         * 
         * @return value
         */
        public String getAddUC() {
            return addUC;
        }

        /** 
         * Set the 'Add_UC' element value.
         * 
         * @param addUC
         */
        public void setAddUC(String addUC) {
            this.addUC = addUC;
        }
    }
}
