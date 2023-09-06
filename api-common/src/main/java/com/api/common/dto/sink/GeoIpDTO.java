package com.api.common.dto.sink;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GeoIpDTO {

    private String country;

//    private String city;
//
//    private Double latitude;
//
//    private Double longitude;

    private String ip;

}
