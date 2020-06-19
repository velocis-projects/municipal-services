package org.egov.assets.model;

import static org.apache.commons.lang3.StringUtils.isEmpty;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class City {

    private Long id;
    private String name;
    private String localName;
    private String districtCode;
    private String districtName;
    private String regionCode;
    private String regionName;
    private String municipalityName;
    private Double longitude;
    private Double latitude;
    private String tenantCode;
    private String ulbGrade;
    private Long createdBy;
    private Date createdDate;
    private Long lastModifiedBy;
    private Date lastModifiedDate;
    private String shapeFileLocation;
    private String captcha;
    private String code;
   

    public boolean isValid() {
        return !isNameAbsent() && !isULBGradeAbsent();
    }

    public boolean isNameAbsent() {
        return isEmpty(name);
    }

    public boolean isULBGradeAbsent() {
        return isEmpty(ulbGrade);
    }
}
