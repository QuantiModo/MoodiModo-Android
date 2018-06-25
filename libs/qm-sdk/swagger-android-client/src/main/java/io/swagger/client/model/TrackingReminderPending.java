package io.swagger.client.model;

import java.util.Date;

import io.swagger.annotations.*;
import com.google.gson.annotations.SerializedName;


@ApiModel(description = "")
public class TrackingReminderPending  {
  
  @SerializedName("id")
  private Integer id = null;
  @SerializedName("trackingReminderId")
  private Integer trackingReminderId = null;
  @SerializedName("clientId")
  private String clientId = null;
  @SerializedName("userId")
  private Integer userId = null;
  @SerializedName("variableId")
  private Integer variableId = null;
  @SerializedName("pendingReminderTime")
  private Date pendingReminderTime = null;
  @SerializedName("defaultValue")
  private Float defaultValue = null;
  @SerializedName("reminderSound")
  private String reminderSound = null;
  @SerializedName("popUp")
  private Boolean popUp = null;
  @SerializedName("sms")
  private Boolean sms = null;
  @SerializedName("email")
  private Boolean email = null;
  @SerializedName("notificationBar")
  private Boolean notificationBar = null;
  @SerializedName("updatedAt")
  private Date updatedAt = null;
  @SerializedName("variableName")
  private String variableName = null;
  @SerializedName("variableCategoryName")
  private String variableCategoryName = null;
  @SerializedName("abbreviatedUnitName")
  private String abbreviatedUnitName = null;
  public enum CombinationOperationEnum {
     MEAN,  SUM, 
  };
  @SerializedName("combinationOperation")
  private CombinationOperationEnum combinationOperation = null;

  
  /**
   * id for the specific PENDING tracking remidner
   **/
  @ApiModelProperty(required = true, value = "id for the specific PENDING tracking remidner")
  public Integer getId() {
    return id;
  }
  public void setId(Integer id) {
    this.id = id;
  }

  
  /**
   * id for the repeating tracking remidner
   **/
  @ApiModelProperty(required = true, value = "id for the repeating tracking remidner")
  public Integer getTrackingReminderId() {
    return trackingReminderId;
  }
  public void setTrackingReminderId(Integer trackingReminderId) {
    this.trackingReminderId = trackingReminderId;
  }

  
  /**
   * clientId
   **/
  @ApiModelProperty(value = "clientId")
  public String getClientId() {
    return clientId;
  }
  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  
  /**
   * ID of User
   **/
  @ApiModelProperty(value = "ID of User")
  public Integer getUserId() {
    return userId;
  }
  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  
  /**
   * Id for the variable to be tracked
   **/
  @ApiModelProperty(value = "Id for the variable to be tracked")
  public Integer getVariableId() {
    return variableId;
  }
  public void setVariableId(Integer variableId) {
    this.variableId = variableId;
  }

  
  /**
   * ISO 8601 timestamp for the specific time the variable should be tracked in UTC.  This will be used for the measurement startTime if the track endpoint is used.
   **/
  @ApiModelProperty(value = "ISO 8601 timestamp for the specific time the variable should be tracked in UTC.  This will be used for the measurement startTime if the track endpoint is used.")
  public Date getPendingReminderTime() {
    return pendingReminderTime;
  }
  public void setPendingReminderTime(Date pendingReminderTime) {
    this.pendingReminderTime = pendingReminderTime;
  }

  
  /**
   * Default value to use for the measurement when tracking
   **/
  @ApiModelProperty(value = "Default value to use for the measurement when tracking")
  public Float getDefaultValue() {
    return defaultValue;
  }
  public void setDefaultValue(Float defaultValue) {
    this.defaultValue = defaultValue;
  }

  
  /**
   * String identifier for the sound to accompany the reminder
   **/
  @ApiModelProperty(value = "String identifier for the sound to accompany the reminder")
  public String getReminderSound() {
    return reminderSound;
  }
  public void setReminderSound(String reminderSound) {
    this.reminderSound = reminderSound;
  }

  
  /**
   * True if the reminders should appear as a popup notification
   **/
  @ApiModelProperty(value = "True if the reminders should appear as a popup notification")
  public Boolean getPopUp() {
    return popUp;
  }
  public void setPopUp(Boolean popUp) {
    this.popUp = popUp;
  }

  
  /**
   * True if the reminders should be delivered via SMS
   **/
  @ApiModelProperty(value = "True if the reminders should be delivered via SMS")
  public Boolean getSms() {
    return sms;
  }
  public void setSms(Boolean sms) {
    this.sms = sms;
  }

  
  /**
   * True if the reminders should be delivered via email
   **/
  @ApiModelProperty(value = "True if the reminders should be delivered via email")
  public Boolean getEmail() {
    return email;
  }
  public void setEmail(Boolean email) {
    this.email = email;
  }

  
  /**
   * True if the reminders should appear in the notification bar
   **/
  @ApiModelProperty(value = "True if the reminders should appear in the notification bar")
  public Boolean getNotificationBar() {
    return notificationBar;
  }
  public void setNotificationBar(Boolean notificationBar) {
    this.notificationBar = notificationBar;
  }

  
  /**
   * When the record in the database was last updated. Use ISO 8601 datetime format. Time zone should be UTC and not local.
   **/
  @ApiModelProperty(value = "When the record in the database was last updated. Use ISO 8601 datetime format. Time zone should be UTC and not local.")
  public Date getUpdatedAt() {
    return updatedAt;
  }
  public void setUpdatedAt(Date updatedAt) {
    this.updatedAt = updatedAt;
  }

  
  /**
   * Name of the variable to be used when sending measurements
   **/
  @ApiModelProperty(value = "Name of the variable to be used when sending measurements")
  public String getVariableName() {
    return variableName;
  }
  public void setVariableName(String variableName) {
    this.variableName = variableName;
  }

  
  /**
   * Name of the variable category to be used when sending measurements
   **/
  @ApiModelProperty(value = "Name of the variable category to be used when sending measurements")
  public String getVariableCategoryName() {
    return variableCategoryName;
  }
  public void setVariableCategoryName(String variableCategoryName) {
    this.variableCategoryName = variableCategoryName;
  }

  
  /**
   * Abbreviated name of the unit to be used when sending measurements
   **/
  @ApiModelProperty(value = "Abbreviated name of the unit to be used when sending measurements")
  public String getAbbreviatedUnitName() {
    return abbreviatedUnitName;
  }
  public void setAbbreviatedUnitName(String abbreviatedUnitName) {
    this.abbreviatedUnitName = abbreviatedUnitName;
  }

  
  /**
   * The way multiple measurements are aggregated over time
   **/
  @ApiModelProperty(value = "The way multiple measurements are aggregated over time")
  public CombinationOperationEnum getCombinationOperation() {
    return combinationOperation;
  }
  public void setCombinationOperation(CombinationOperationEnum combinationOperation) {
    this.combinationOperation = combinationOperation;
  }

  

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class TrackingReminderPending {\n");
    
    sb.append("  id: ").append(id).append("\n");
    sb.append("  trackingReminderId: ").append(trackingReminderId).append("\n");
    sb.append("  clientId: ").append(clientId).append("\n");
    sb.append("  userId: ").append(userId).append("\n");
    sb.append("  variableId: ").append(variableId).append("\n");
    sb.append("  pendingReminderTime: ").append(pendingReminderTime).append("\n");
    sb.append("  defaultValue: ").append(defaultValue).append("\n");
    sb.append("  reminderSound: ").append(reminderSound).append("\n");
    sb.append("  popUp: ").append(popUp).append("\n");
    sb.append("  sms: ").append(sms).append("\n");
    sb.append("  email: ").append(email).append("\n");
    sb.append("  notificationBar: ").append(notificationBar).append("\n");
    sb.append("  updatedAt: ").append(updatedAt).append("\n");
    sb.append("  variableName: ").append(variableName).append("\n");
    sb.append("  variableCategoryName: ").append(variableCategoryName).append("\n");
    sb.append("  abbreviatedUnitName: ").append(abbreviatedUnitName).append("\n");
    sb.append("  combinationOperation: ").append(combinationOperation).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
