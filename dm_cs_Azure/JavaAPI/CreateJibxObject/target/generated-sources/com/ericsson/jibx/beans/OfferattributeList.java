
package com.ericsson.jibx.beans;

import java.util.ArrayList;
import java.util.List;

/** 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:element xmlns:xs="http://www.w3.org/2001/XMLSchema" name="offerattribute_list">
 *   &lt;xs:complexType>
 *     &lt;xs:sequence>
 *       &lt;xs:element name="offerattribute_info" minOccurs="0" maxOccurs="unbounded">
 *         &lt;!-- Reference to inner class OfferattributeInfo -->
 *       &lt;/xs:element>
 *     &lt;/xs:sequence>
 *   &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 */
public class OfferattributeList
{
    private List<OfferattributeInfo> offerattributeInfoList = new ArrayList<OfferattributeInfo>();

    /** 
     * Get the list of 'offerattribute_info' element items.
     * 
     * @return list
     */
    public List<OfferattributeInfo> getOfferattributeInfoList() {
        return offerattributeInfoList;
    }

    /** 
     * Set the list of 'offerattribute_info' element items.
     * 
     * @param list
     */
    public void setOfferattributeInfoList(List<OfferattributeInfo> list) {
        offerattributeInfoList = list;
    }
    /** 
     * Schema fragment(s) for this class:
     * <pre>
     * &lt;xs:element xmlns:xs="http://www.w3.org/2001/XMLSchema" name="offerattribute_info" minOccurs="0" maxOccurs="unbounded">
     *   &lt;xs:complexType>
     *     &lt;xs:all>
     *       &lt;xs:element type="xs:string" name="account_id"/>
     *       &lt;xs:element type="xs:int" name="offer_id"/>
     *       &lt;xs:element type="xs:int" name="attribute_def_id"/>
     *       &lt;xs:element type="xs:string" name="value"/>
     *     &lt;/xs:all>
     *   &lt;/xs:complexType>
     * &lt;/xs:element>
     * </pre>
     */
    public static class OfferattributeInfo
    {
        private String accountId;
        private int offerId;
        private int attributeDefId;
        private String value;

        /** 
         * Get the 'account_id' element value.
         * 
         * @return value
         */
        public String getAccountId() {
            return accountId;
        }

        /** 
         * Set the 'account_id' element value.
         * 
         * @param accountId
         */
        public void setAccountId(String accountId) {
            this.accountId = accountId;
        }

        /** 
         * Get the 'offer_id' element value.
         * 
         * @return value
         */
        public int getOfferId() {
            return offerId;
        }

        /** 
         * Set the 'offer_id' element value.
         * 
         * @param offerId
         */
        public void setOfferId(int offerId) {
            this.offerId = offerId;
        }

        /** 
         * Get the 'attribute_def_id' element value.
         * 
         * @return value
         */
        public int getAttributeDefId() {
            return attributeDefId;
        }

        /** 
         * Set the 'attribute_def_id' element value.
         * 
         * @param attributeDefId
         */
        public void setAttributeDefId(int attributeDefId) {
            this.attributeDefId = attributeDefId;
        }

        /** 
         * Get the 'value' element value.
         * 
         * @return value
         */
        public String getValue() {
            return value;
        }

        /** 
         * Set the 'value' element value.
         * 
         * @param value
         */
        public void setValue(String value) {
            this.value = value;
        }
    }
}
