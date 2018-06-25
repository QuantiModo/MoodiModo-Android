package io.swagger.client.model;


import io.swagger.annotations.*;
import com.google.gson.annotations.SerializedName;


@ApiModel(description = "")
public class ValueObject  {
  
  @SerializedName("timestamp")
  private Long timestamp = null;
  @SerializedName("value")
  private Double value = null;
  @SerializedName("note")
  private String note = null;

  
  /**
   * Timestamp for the measurement event in epoch time (unixtime)
   **/
  @ApiModelProperty(required = true, value = "Timestamp for the measurement event in epoch time (unixtime)")
  public Long getTimestamp() {
    return timestamp;
  }
  public void setTimestamp(Long timestamp) {
    this.timestamp = timestamp;
  }

  
  /**
   * Measurement value
   **/
  @ApiModelProperty(required = true, value = "Measurement value")
  public Double getValue() {
    return value;
  }
  public void setValue(Double value) {
    this.value = value;
  }

  
  /**
   * Optional note to include with the measurement
   **/
  @ApiModelProperty(value = "Optional note to include with the measurement")
  public String getNote() {
    return note;
  }
  public void setNote(String note) {
    this.note = note;
  }

  

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class ValueObject {\n");
    
    sb.append("  timestamp: ").append(timestamp).append("\n");
    sb.append("  value: ").append(value).append("\n");
    sb.append("  note: ").append(note).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
