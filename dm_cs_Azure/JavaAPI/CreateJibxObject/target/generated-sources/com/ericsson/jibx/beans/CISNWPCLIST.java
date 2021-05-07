
package com.ericsson.jibx.beans;

import java.util.ArrayList;
import java.util.List;

/** 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:element xmlns:xs="http://www.w3.org/2001/XMLSchema" name="CIS_NWPC_LIST">
 *   &lt;xs:complexType>
 *     &lt;xs:sequence>
 *       &lt;xs:element name="CIS_NWPC_INFO" maxOccurs="unbounded">
 *         &lt;!-- Reference to inner class CISNWPCINFO -->
 *       &lt;/xs:element>
 *     &lt;/xs:sequence>
 *   &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 */
public class CISNWPCLIST
{
    private List<CISNWPCINFO> CISNWPCINFOList = new ArrayList<CISNWPCINFO>();

    /** 
     * Get the list of 'CIS_NWPC_INFO' element items.
     * 
     * @return list
     */
    public List<CISNWPCINFO> getCISNWPCINFOList() {
        return CISNWPCINFOList;
    }

    /** 
     * Set the list of 'CIS_NWPC_INFO' element items.
     * 
     * @param list
     */
    public void setCISNWPCINFOList(List<CISNWPCINFO> list) {
        CISNWPCINFOList = list;
    }
    /** 
     * Schema fragment(s) for this class:
     * <pre>
     * &lt;xs:element xmlns:xs="http://www.w3.org/2001/XMLSchema" name="CIS_NWPC_INFO" maxOccurs="unbounded">
     *   &lt;xs:complexType>
     *     &lt;xs:all>
     *       &lt;xs:element type="xs:string" name="BT_Name"/>
     *       &lt;xs:element type="xs:string" name="BT_ID"/>
     *       &lt;xs:element type="xs:string" name="BT_Value"/>
     *       &lt;xs:element type="xs:string" name="status"/>
     *       &lt;xs:element type="xs:string" name="networkStatus"/>
     *       &lt;xs:element type="xs:string" name="OffSet_Days"/>
     *       &lt;xs:element type="xs:string" name="renewal_count"/>
     *       &lt;xs:element type="xs:string" name="Period"/>
     *     &lt;/xs:all>
     *   &lt;/xs:complexType>
     * &lt;/xs:element>
     * </pre>
     */
    public static class CISNWPCINFO
    {
        private String BTName;
        private String BTID;
        private String BTValue;
        private String status;
        private String networkStatus;
        private String offSetDays;
        private String renewalCount;
        private String period;

        /** 
         * Get the 'BT_Name' element value.
         * 
         * @return value
         */
        public String getBTName() {
            return BTName;
        }

        /** 
         * Set the 'BT_Name' element value.
         * 
         * @param BTName
         */
        public void setBTName(String BTName) {
            this.BTName = BTName;
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
         * Get the 'status' element value.
         * 
         * @return value
         */
        public String getStatus() {
            return status;
        }

        /** 
         * Set the 'status' element value.
         * 
         * @param status
         */
        public void setStatus(String status) {
            this.status = status;
        }

        /** 
         * Get the 'networkStatus' element value.
         * 
         * @return value
         */
        public String getNetworkStatus() {
            return networkStatus;
        }

        /** 
         * Set the 'networkStatus' element value.
         * 
         * @param networkStatus
         */
        public void setNetworkStatus(String networkStatus) {
            this.networkStatus = networkStatus;
        }

        /** 
         * Get the 'OffSet_Days' element value.
         * 
         * @return value
         */
        public String getOffSetDays() {
            return offSetDays;
        }

        /** 
         * Set the 'OffSet_Days' element value.
         * 
         * @param offSetDays
         */
        public void setOffSetDays(String offSetDays) {
            this.offSetDays = offSetDays;
        }

        /** 
         * Get the 'renewal_count' element value.
         * 
         * @return value
         */
        public String getRenewalCount() {
            return renewalCount;
        }

        /** 
         * Set the 'renewal_count' element value.
         * 
         * @param renewalCount
         */
        public void setRenewalCount(String renewalCount) {
            this.renewalCount = renewalCount;
        }

        /** 
         * Get the 'Period' element value.
         * 
         * @return value
         */
        public String getPeriod() {
            return period;
        }

        /** 
         * Set the 'Period' element value.
         * 
         * @param period
         */
        public void setPeriod(String period) {
            this.period = period;
        }
    }
}
