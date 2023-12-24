package com.admin.server.model.dto;

import java.util.List;
import java.util.Map;

/**
 * @author heqin
 */
public class RetentionAnalysisResultDto {

    private List<DataGroupDto> dataGroups;

    public List<DataGroupDto> getDataGroups() {
        return dataGroups;
    }

    public void setDataGroups(List<DataGroupDto> dataGroups) {
        this.dataGroups = dataGroups;
    }

    public static class DataGroupDto {

        private String date;

        private List<Long> nums;

        private List<List<String>> uids;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public List<Long> getNums() {
            return nums;
        }

        public void setNums(List<Long> nums) {
            this.nums = nums;
        }

        public List<List<String>> getUids() {
            return uids;
        }

        public void setUids(List<List<String>> uids) {
            this.uids = uids;
        }
    }
}
