package io.swagger.client.model;


import io.swagger.annotations.*;
import com.google.gson.annotations.SerializedName;


@ApiModel(description = "")
public class HumanTime  {
  
  @SerializedName("date")
  private String date = null;
  @SerializedName("timezone_type")
  private Integer timezoneType = null;
  @SerializedName("timezone")
  private String timezone = null;

  
  /**
   * date time
   **/
  @ApiModelProperty(value = "date time")
  public String getDate() {
    return date;
  }
  public void setDate(String date) {
    this.date = date;
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  public Integer getTimezoneType() {
    return timezoneType;
  }
  public void setTimezoneType(Integer timezoneType) {
    this.timezoneType = timezoneType;
  }

  
  /**
   * timezone of date time
   **/
  @ApiModelProperty(value = "timezone of date time")
  public String getTimezone() {
    return timezone;
  }
  public void setTimezone(String timezone) {
    this.timezone = timezone;
  }

  

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class HumanTime {\n");
    
    sb.append("  date: ").append(date).append("\n");
    sb.append("  timezoneType: ").append(timezoneType).append("\n");
    sb.append("  timezone: ").append(timezone).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
