package com.admin.server.model.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

public class EventAnalysisResultDto {

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<DataGroupDto> getDataGroups() {
        return dataGroups;
    }

    public void setDataGroups(List<DataGroupDto> dataGroups) {
        this.dataGroups = dataGroups;
    }

    private long total;

    private List<DataGroupDto> dataGroups;

    @Override
    public String toString() {
        return "EventAnalysisResultDto{" +
                "total=" + total +
                ", dataGroups=" + dataGroups +
                '}';
    }

    public static class DataGroupDto {

        public Map<String, Object> getItem() {
            return item;
        }

        public void setItem(Map<String, Object> item) {
            this.item = item;
        }

        private Map<String, Object> item;

        @Override
        public String toString() {
            return "DataGroupDto{" +
                    "item=" + item +
                    '}';
        }
    }
}
