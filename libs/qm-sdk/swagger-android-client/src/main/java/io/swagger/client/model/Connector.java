package io.swagger.client.model;


import io.swagger.annotations.*;
import com.google.gson.annotations.SerializedName;


@ApiModel(description = "")
public class Connector  {
  
  @SerializedName("id")
  private Integer id = null;
  @SerializedName("name")
  private String name = null;
  @SerializedName("displayName")
  private String displayName = null;
  @SerializedName("image")
  private String image = null;
  @SerializedName("getItUrl")
  private String getItUrl = null;
  @SerializedName("connected")
  private String connected = null;
  @SerializedName("connectInstructions")
  private String connectInstructions = null;
  @SerializedName("lastUpdate")
  private Integer lastUpdate = null;
  @SerializedName("totalMeasurementsInLastUpdate")
  private Integer totalMeasurementsInLastUpdate = null;
  @SerializedName("noDataYet")
  private Boolean noDataYet = null;

  
  /**
   * Connector ID number
   **/
  @ApiModelProperty(required = true, value = "Connector ID number")
  public Integer getId() {
    return id;
  }
  public void setId(Integer id) {
    this.id = id;
  }

  
  /**
   * Connector lowercase system name
   **/
  @ApiModelProperty(required = true, value = "Connector lowercase system name")
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }

  
  /**
   * Connector pretty display name
   **/
  @ApiModelProperty(required = true, value = "Connector pretty display name")
  public String getDisplayName() {
    return displayName;
  }
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  
  /**
   * URL to the image of the connector logo
   **/
  @ApiModelProperty(required = true, value = "URL to the image of the connector logo")
  public String getImage() {
    return image;
  }
  public void setImage(String image) {
    this.image = image;
  }

  
  /**
   * URL to a site where one can get this device or application
   **/
  @ApiModelProperty(required = true, value = "URL to a site where one can get this device or application")
  public String getGetItUrl() {
    return getItUrl;
  }
  public void setGetItUrl(String getItUrl) {
    this.getItUrl = getItUrl;
  }

  
  /**
   * True if the authenticated user has this connector enabled
   **/
  @ApiModelProperty(required = true, value = "True if the authenticated user has this connector enabled")
  public String getConnected() {
    return connected;
  }
  public void setConnected(String connected) {
    this.connected = connected;
  }

  
  /**
   * URL and parameters used when connecting to a service
   **/
  @ApiModelProperty(required = true, value = "URL and parameters used when connecting to a service")
  public String getConnectInstructions() {
    return connectInstructions;
  }
  public void setConnectInstructions(String connectInstructions) {
    this.connectInstructions = connectInstructions;
  }

  
  /**
   * Epoch timestamp of last sync
   **/
  @ApiModelProperty(required = true, value = "Epoch timestamp of last sync")
  public Integer getLastUpdate() {
    return lastUpdate;
  }
  public void setLastUpdate(Integer lastUpdate) {
    this.lastUpdate = lastUpdate;
  }

  
  /**
   * Number of measurements obtained during latest update
   **/
  @ApiModelProperty(required = true, value = "Number of measurements obtained during latest update")
  public Integer getTotalMeasurementsInLastUpdate() {
    return totalMeasurementsInLastUpdate;
  }
  public void setTotalMeasurementsInLastUpdate(Integer totalMeasurementsInLastUpdate) {
    this.totalMeasurementsInLastUpdate = totalMeasurementsInLastUpdate;
  }

  
  /**
   * True if user has no measurements for this connector
   **/
  @ApiModelProperty(required = true, value = "True if user has no measurements for this connector")
  public Boolean getNoDataYet() {
    return noDataYet;
  }
  public void setNoDataYet(Boolean noDataYet) {
    this.noDataYet = noDataYet;
  }

  

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class Connector {\n");
    
    sb.append("  id: ").append(id).append("\n");
    sb.append("  name: ").append(name).append("\n");
    sb.append("  displayName: ").append(displayName).append("\n");
    sb.append("  image: ").append(image).append("\n");
    sb.append("  getItUrl: ").append(getItUrl).append("\n");
    sb.append("  connected: ").append(connected).append("\n");
    sb.append("  connectInstructions: ").append(connectInstructions).append("\n");
    sb.append("  lastUpdate: ").append(lastUpdate).append("\n");
    sb.append("  totalMeasurementsInLastUpdate: ").append(totalMeasurementsInLastUpdate).append("\n");
    sb.append("  noDataYet: ").append(noDataYet).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
