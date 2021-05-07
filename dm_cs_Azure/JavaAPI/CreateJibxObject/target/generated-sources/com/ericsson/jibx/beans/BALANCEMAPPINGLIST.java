
package com.ericsson.jibx.beans;

import java.util.ArrayList;
import java.util.List;

/** 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:element xmlns:xs="http://www.w3.org/2001/XMLSchema" name="BALANCE_MAPPING_LIST">
 *   &lt;xs:complexType>
 *     &lt;xs:sequence>
 *       &lt;xs:element name="BALANCE_MAPPING_INFO" maxOccurs="unbounded">
 *         &lt;!-- Reference to inner class BALANCEMAPPINGINFO -->
 *       &lt;/xs:element>
 *     &lt;/xs:sequence>
 *   &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 */
public class BALANCEMAPPINGLIST
{
    private List<BALANCEMAPPINGINFO> BALANCEMAPPINGINFOList = new ArrayList<BALANCEMAPPINGINFO>();

    /** 
     * Get the list of 'BALANCE_MAPPING_INFO' element items.
     * 
     * @return list
     */
    public List<BALANCEMAPPINGINFO> getBALANCEMAPPINGINFOList() {
        return BALANCEMAPPINGINFOList;
    }

    /** 
     * Set the list of 'BALANCE_MAPPING_INFO' element items.
     * 
     * @param list
     */
    public void setBALANCEMAPPINGINFOList(List<BALANCEMAPPINGINFO> list) {
        BALANCEMAPPINGINFOList = list;
    }
    /** 
     * Schema fragment(s) for this class:
     * <pre>
     * &lt;xs:element xmlns:xs="http://www.w3.org/2001/XMLSchema" name="BALANCE_MAPPING_INFO" maxOccurs="unbounded">
     *   &lt;xs:complexType>
     *     &lt;xs:all>
     *       &lt;xs:element type="xs:string" name="Balance_Type_Name"/>
     *       &lt;xs:element type="xs:string" name="BT_ID"/>
     *       &lt;xs:element type="xs:string" name="BT_TYPE"/>
     *       &lt;xs:element type="xs:string" name="Symbols"/>
     *       &lt;xs:element type="xs:string" name="BT_Value"/>
     *       &lt;xs:element type="xs:string" name="SubState"/>
     *       &lt;xs:element type="xs:string" name="SC_Identifier"/>
     *       &lt;xs:element type="xs:string" name="RP_ID"/>
     *       &lt;xs:element type="xs:string" name="PT_Name"/>
     *       &lt;xs:element type="xs:string" name="BT_Group_Identifier"/>
     *       &lt;xs:element type="xs:string" name="Resource"/>
     *       &lt;xs:element type="xs:string" name="Ignore_Flag"/>
     *       &lt;xs:element type="xs:string" name="Product_Private"/>
     *       &lt;xs:element type="xs:string" name="Offer_Type"/>
     *       &lt;xs:element type="xs:string" name="Offer_ID"/>
     *       &lt;xs:element type="xs:string" name="Offer_Flag"/>
     *       &lt;xs:element type="xs:string" name="Offer_Start_Date"/>
     *       &lt;xs:element type="xs:string" name="Offer_Expiry_Date"/>
     *       &lt;xs:element type="xs:string" name="PAM_CLASS_ID"/>
     *       &lt;xs:element type="xs:string" name="PAM_SERV_ID"/>
     *       &lt;xs:element type="xs:string" name="Attr_Offer_Id"/>
     *       &lt;xs:element type="xs:string" name="Attr_Name"/>
     *       &lt;xs:element type="xs:string" name="Attr_Type"/>
     *       &lt;xs:element type="xs:string" name="Attr_Value"/>
     *       &lt;xs:element type="xs:string" name="DA_Type"/>
     *       &lt;xs:element type="xs:string" name="DA_ID"/>
     *       &lt;xs:element type="xs:string" name="DA_Start_Date"/>
     *       &lt;xs:element type="xs:string" name="DA_Expiry_Date"/>
     *       &lt;xs:element type="xs:string" name="UC_Type"/>
     *       &lt;xs:element type="xs:string" name="UC_ID"/>
     *       &lt;xs:element type="xs:string" name="UT_ID"/>
     *       &lt;xs:element type="xs:string" name="UT_Value"/>
     *       &lt;xs:element type="xs:string" name="UA_Type"/>
     *       &lt;xs:element type="xs:string" name="UA_ID"/>
     *       &lt;xs:element type="xs:string" name="UA_Value"/>
     *       &lt;xs:element type="xs:string" name="CIS_Reference"/>
     *       &lt;xs:element type="xs:string" name="Add_Offer"/>
     *       &lt;xs:element type="xs:string" name="Add_UA"/>
     *       &lt;xs:element type="xs:string" name="Add_DA"/>
     *       &lt;xs:element type="xs:string" name="Add_UC"/>
     *       &lt;xs:element type="xs:string" name="Add_PAM"/>
     *       &lt;xs:element type="xs:string" name="Comment"/>
     *     &lt;/xs:all>
     *   &lt;/xs:complexType>
     * &lt;/xs:element>
     * </pre>
     */
    public static class BALANCEMAPPINGINFO
    {
        private String balanceTypeName;
        private String BTID;
        private String BTTYPE;
        private String symbols;
        private String BTValue;
        private String subState;
        private String SCIdentifier;
        private String RPID;
        private String PTName;
        private String BTGroupIdentifier;
        private String resource;
        private String ignoreFlag;
        private String productPrivate;
        private String offerType;
        private String offerID;
        private String offerFlag;
        private String offerStartDate;
        private String offerExpiryDate;
        private String PAMCLASSID;
        private String PAMSERVID;
        private String attrOfferId;
        private String attrName;
        private String attrType;
        private String attrValue;
        private String DAType;
        private String DAID;
        private String DAStartDate;
        private String DAExpiryDate;
        private String UCType;
        private String UCID;
        private String UTID;
        private String UTValue;
        private String UAType;
        private String UAID;
        private String UAValue;
        private String CISReference;
        private String addOffer;
        private String addUA;
        private String addDA;
        private String addUC;
        private String addPAM;
        private String comment;

        /** 
         * Get the 'Balance_Type_Name' element value.
         * 
         * @return value
         */
        public String getBalanceTypeName() {
            return balanceTypeName;
        }

        /** 
         * Set the 'Balance_Type_Name' element value.
         * 
         * @param balanceTypeName
         */
        public void setBalanceTypeName(String balanceTypeName) {
            this.balanceTypeName = balanceTypeName;
        }

        /** 
         * Get the 'BT_ID' element value.
         * 
         * @return value
         */
        public String getBTID() {
            return BTID;
        }

        /** 
         * Set the 'BT_ID' element value.
         * 
         * @param BTID
         */
        public void setBTID(String BTID) {
            this.BTID = BTID;
        }

        /** 
         * Get the 'BT_TYPE' element value.
         * 
         * @return value
         */
        public String getBTTYPE() {
            return BTTYPE;
        }

        /** 
         * Set the 'BT_TYPE' element value.
         * 
         * @param BTTYPE
         */
        public void setBTTYPE(String BTTYPE) {
            this.BTTYPE = BTTYPE;
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
         * Get the 'BT_Value' element value.
         * 
         * @return value
         */
        public String getBTValue() {
            return BTValue;
        }

        /** 
         * Set the 'BT_Value' element value.
         * 
         * @param BTValue
         */
        public void setBTValue(String BTValue) {
            this.BTValue = BTValue;
        }

        /** 
         * Get the 'SubState' element value.
         * 
         * @return value
         */
        public String getSubState() {
            return subState;
        }

        /** 
         * Set the 'SubState' element value.
         * 
         * @param subState
         */
        public void setSubState(String subState) {
            this.subState = subState;
        }

        /** 
         * Get the 'SC_Identifier' element value.
         * 
         * @return value
         */
        public String getSCIdentifier() {
            return SCIdentifier;
        }

        /** 
         * Set the 'SC_Identifier' element value.
         * 
         * @param SCIdentifier
         */
        public void setSCIdentifier(String SCIdentifier) {
            this.SCIdentifier = SCIdentifier;
        }

        /** 
         * Get the 'RP_ID' element value.
         * 
         * @return value
         */
        public String getRPID() {
            return RPID;
        }

        /** 
         * Set the 'RP_ID' element value.
         * 
         * @param RPID
         */
        public void setRPID(String RPID) {
            this.RPID = RPID;
        }

        /** 
         * Get the 'PT_Name' element value.
         * 
         * @return value
         */
        public String getPTName() {
            return PTName;
        }

        /** 
         * Set the 'PT_Name' element value.
         * 
         * @param PTName
         */
        public void setPTName(String PTName) {
            this.PTName = PTName;
        }

        /** 
         * Get the 'BT_Group_Identifier' element value.
         * 
         * @return value
         */
        public String getBTGroupIdentifier() {
            return BTGroupIdentifier;
        }

        /** 
         * Set the 'BT_Group_Identifier' element value.
         * 
         * @param BTGroupIdentifier
         */
        public void setBTGroupIdentifier(String BTGroupIdentifier) {
            this.BTGroupIdentifier = BTGroupIdentifier;
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
         * Get the 'Offer_ID' element value.
         * 
         * @return value
         */
        public String getOfferID() {
            return offerID;
        }

        /** 
         * Set the 'Offer_ID' element value.
         * 
         * @param offerID
         */
        public void setOfferID(String offerID) {
            this.offerID = offerID;
        }

        /** 
         * Get the 'Offer_Flag' element value.
         * 
         * @return value
         */
        public String getOfferFlag() {
            return offerFlag;
        }

        /** 
         * Set the 'Offer_Flag' element value.
         * 
         * @param offerFlag
         */
        public void setOfferFlag(String offerFlag) {
            this.offerFlag = offerFlag;
        }

        /** 
         * Get the 'Offer_Start_Date' element value.
         * 
         * @return value
         */
        public String getOfferStartDate() {
            return offerStartDate;
        }

        /** 
         * Set the 'Offer_Start_Date' element value.
         * 
         * @param offerStartDate
         */
        public void setOfferStartDate(String offerStartDate) {
            this.offerStartDate = offerStartDate;
        }

        /** 
         * Get the 'Offer_Expiry_Date' element value.
         * 
         * @return value
         */
        public String getOfferExpiryDate() {
            return offerExpiryDate;
        }

        /** 
         * Set the 'Offer_Expiry_Date' element value.
         * 
         * @param offerExpiryDate
         */
        public void setOfferExpiryDate(String offerExpiryDate) {
            this.offerExpiryDate = offerExpiryDate;
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
         * Get the 'Attr_Value' element value.
         * 
         * @return value
         */
        public String getAttrValue() {
            return attrValue;
        }

        /** 
         * Set the 'Attr_Value' element value.
         * 
         * @param attrValue
         */
        public void setAttrValue(String attrValue) {
            this.attrValue = attrValue;
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
         * Get the 'DA_Start_Date' element value.
         * 
         * @return value
         */
        public String getDAStartDate() {
            return DAStartDate;
        }

        /** 
         * Set the 'DA_Start_Date' element value.
         * 
         * @param DAStartDate
         */
        public void setDAStartDate(String DAStartDate) {
            this.DAStartDate = DAStartDate;
        }

        /** 
         * Get the 'DA_Expiry_Date' element value.
         * 
         * @return value
         */
        public String getDAExpiryDate() {
            return DAExpiryDate;
        }

        /** 
         * Set the 'DA_Expiry_Date' element value.
         * 
         * @param DAExpiryDate
         */
        public void setDAExpiryDate(String DAExpiryDate) {
            this.DAExpiryDate = DAExpiryDate;
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

        /** 
         * Get the 'Add_PAM' element value.
         * 
         * @return value
         */
        public String getAddPAM() {
            return addPAM;
        }

        /** 
         * Set the 'Add_PAM' element value.
         * 
         * @param addPAM
         */
        public void setAddPAM(String addPAM) {
            this.addPAM = addPAM;
        }

        /** 
         * Get the 'Comment' element value.
         * 
         * @return value
         */
        public String getComment() {
            return comment;
        }

        /** 
         * Set the 'Comment' element value.
         * 
         * @param comment
         */
        public void setComment(String comment) {
            this.comment = comment;
        }
    }
}
