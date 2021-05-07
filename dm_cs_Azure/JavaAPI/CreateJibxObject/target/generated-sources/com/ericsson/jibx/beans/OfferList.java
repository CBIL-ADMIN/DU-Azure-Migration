
package com.ericsson.jibx.beans;

import java.util.ArrayList;
import java.util.List;

/** 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:element xmlns:xs="http://www.w3.org/2001/XMLSchema" name="offer_list">
 *   &lt;xs:complexType>
 *     &lt;xs:sequence>
 *       &lt;xs:element name="offer_info" minOccurs="0" maxOccurs="unbounded">
 *         &lt;!-- Reference to inner class OfferInfo -->
 *       &lt;/xs:element>
 *     &lt;/xs:sequence>
 *   &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 */
public class OfferList
{
    private List<OfferInfo> offerInfoList = new ArrayList<OfferInfo>();

    /** 
     * Get the list of 'offer_info' element items.
     * 
     * @return list
     */
    public List<OfferInfo> getOfferInfoList() {
        return offerInfoList;
    }

    /** 
     * Set the list of 'offer_info' element items.
     * 
     * @param list
     */
    public void setOfferInfoList(List<OfferInfo> list) {
        offerInfoList = list;
    }
    /** 
     * Schema fragment(s) for this class:
     * <pre>
     * &lt;xs:element xmlns:xs="http://www.w3.org/2001/XMLSchema" name="offer_info" minOccurs="0" maxOccurs="unbounded">
     *   &lt;xs:complexType>
     *     &lt;xs:all>
     *       &lt;xs:element type="xs:string" name="account_id"/>
     *       &lt;xs:element type="xs:string" name="offer_id"/>
     *       &lt;xs:element type="xs:string" name="start_date"/>
     *       &lt;xs:element type="xs:string" name="expiry_date"/>
     *       &lt;xs:element type="xs:string" name="start_seconds"/>
     *       &lt;xs:element type="xs:string" name="expiry_seconds"/>
     *       &lt;xs:element type="xs:string" name="flags"/>
     *       &lt;xs:element type="xs:string" name="pam_service_id"/>
     *       &lt;xs:element type="xs:string" name="product_id"/>
     *     &lt;/xs:all>
     *   &lt;/xs:complexType>
     * &lt;/xs:element>
     * </pre>
     */
    public static class OfferInfo
    {
        private String accountId;
        private String offerId;
        private String startDate;
        private String expiryDate;
        private String startSeconds;
        private String expirySeconds;
        private String flags;
        private String pamServiceId;
        private String productId;

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
        public String getOfferId() {
            return offerId;
        }

        /** 
         * Set the 'offer_id' element value.
         * 
         * @param offerId
         */
        public void setOfferId(String offerId) {
            this.offerId = offerId;
        }

        /** 
         * Get the 'start_date' element value.
         * 
         * @return value
         */
        public String getStartDate() {
            return startDate;
        }

        /** 
         * Set the 'start_date' element value.
         * 
         * @param startDate
         */
        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        /** 
         * Get the 'expiry_date' element value.
         * 
         * @return value
         */
        public String getExpiryDate() {
            return expiryDate;
        }

        /** 
         * Set the 'expiry_date' element value.
         * 
         * @param expiryDate
         */
        public void setExpiryDate(String expiryDate) {
            this.expiryDate = expiryDate;
        }

        /** 
         * Get the 'start_seconds' element value.
         * 
         * @return value
         */
        public String getStartSeconds() {
            return startSeconds;
        }

        /** 
         * Set the 'start_seconds' element value.
         * 
         * @param startSeconds
         */
        public void setStartSeconds(String startSeconds) {
            this.startSeconds = startSeconds;
        }

        /** 
         * Get the 'expiry_seconds' element value.
         * 
         * @return value
         */
        public String getExpirySeconds() {
            return expirySeconds;
        }

        /** 
         * Set the 'expiry_seconds' element value.
         * 
         * @param expirySeconds
         */
        public void setExpirySeconds(String expirySeconds) {
            this.expirySeconds = expirySeconds;
        }

        /** 
         * Get the 'flags' element value.
         * 
         * @return value
         */
        public String getFlags() {
            return flags;
        }

        /** 
         * Set the 'flags' element value.
         * 
         * @param flags
         */
        public void setFlags(String flags) {
            this.flags = flags;
        }

        /** 
         * Get the 'pam_service_id' element value.
         * 
         * @return value
         */
        public String getPamServiceId() {
            return pamServiceId;
        }

        /** 
         * Set the 'pam_service_id' element value.
         * 
         * @param pamServiceId
         */
        public void setPamServiceId(String pamServiceId) {
            this.pamServiceId = pamServiceId;
        }

        /** 
         * Get the 'product_id' element value.
         * 
         * @return value
         */
        public String getProductId() {
            return productId;
        }

        /** 
         * Set the 'product_id' element value.
         * 
         * @param productId
         */
        public void setProductId(String productId) {
            this.productId = productId;
        }
    }
}
