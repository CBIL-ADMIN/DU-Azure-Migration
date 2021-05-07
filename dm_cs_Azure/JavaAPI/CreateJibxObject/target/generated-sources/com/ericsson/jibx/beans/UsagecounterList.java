
package com.ericsson.jibx.beans;

import java.util.ArrayList;
import java.util.List;

/** 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:element xmlns:xs="http://www.w3.org/2001/XMLSchema" name="usagecounter_list">
 *   &lt;xs:complexType>
 *     &lt;xs:sequence>
 *       &lt;xs:element name="usagecounter_info" maxOccurs="unbounded">
 *         &lt;!-- Reference to inner class UsagecounterInfo -->
 *       &lt;/xs:element>
 *     &lt;/xs:sequence>
 *   &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 */
public class UsagecounterList
{
    private List<UsagecounterInfo> usagecounterInfoList = new ArrayList<UsagecounterInfo>();

    /** 
     * Get the list of 'usagecounter_info' element items.
     * 
     * @return list
     */
    public List<UsagecounterInfo> getUsagecounterInfoList() {
        return usagecounterInfoList;
    }

    /** 
     * Set the list of 'usagecounter_info' element items.
     * 
     * @param list
     */
    public void setUsagecounterInfoList(List<UsagecounterInfo> list) {
        usagecounterInfoList = list;
    }
    /** 
     * Schema fragment(s) for this class:
     * <pre>
     * &lt;xs:element xmlns:xs="http://www.w3.org/2001/XMLSchema" name="usagecounter_info" maxOccurs="unbounded">
     *   &lt;xs:complexType>
     *     &lt;xs:all>
     *       &lt;xs:element type="xs:string" name="account_id"/>
     *       &lt;xs:element type="xs:string" name="usage_counter_id"/>
     *       &lt;xs:element type="xs:string" name="associated_id"/>
     *       &lt;xs:element type="xs:string" name="value"/>
     *       &lt;xs:element type="xs:string" name="product_id"/>
     *     &lt;/xs:all>
     *   &lt;/xs:complexType>
     * &lt;/xs:element>
     * </pre>
     */
    public static class UsagecounterInfo
    {
        private String accountId;
        private String usageCounterId;
        private String associatedId;
        private String value;
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
         * Get the 'usage_counter_id' element value.
         * 
         * @return value
         */
        public String getUsageCounterId() {
            return usageCounterId;
        }

        /** 
         * Set the 'usage_counter_id' element value.
         * 
         * @param usageCounterId
         */
        public void setUsageCounterId(String usageCounterId) {
            this.usageCounterId = usageCounterId;
        }

        /** 
         * Get the 'associated_id' element value.
         * 
         * @return value
         */
        public String getAssociatedId() {
            return associatedId;
        }

        /** 
         * Set the 'associated_id' element value.
         * 
         * @param associatedId
         */
        public void setAssociatedId(String associatedId) {
            this.associatedId = associatedId;
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
