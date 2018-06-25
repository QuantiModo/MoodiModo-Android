package io.swagger.client.model;

import java.util.Date;

import io.swagger.annotations.*;
import com.google.gson.annotations.SerializedName;


@ApiModel(description = "")
public class TrackingReminder  {
  
  @SerializedName("id")
  private Integer id = null;
  @SerializedName("clientId")
  private String clientId = null;
  @SerializedName("userId")
  private Integer userId = null;
  @SerializedName("variableId")
  private Integer variableId = null;
  @SerializedName("defaultValue")
  private Float defaultValue = null;
  @SerializedName("reminderStartTime")
  private String reminderStartTime = null;
  @SerializedName("reminderEndTime")
  private String reminderEndTime = null;
  @SerializedName("reminderSound")
  private String reminderSound = null;
  @SerializedName("reminderFrequency")
  private Integer reminderFrequency = null;
  @SerializedName("popUp")
  private Boolean popUp = null;
  @SerializedName("sms")
  private Boolean sms = null;
  @SerializedName("email")
  private Boolean email = null;
  @SerializedName("notificationBar")
  private Boolean notificationBar = null;
  @SerializedName("lastReminded")
  private Date lastReminded = null;
  @SerializedName("lastTracked")
  private Date lastTracked = null;
  @SerializedName("firstDailyReminderTime")
  private String firstDailyReminderTime = null;
  @SerializedName("secondDailyReminderTime")
  private String secondDailyReminderTime = null;
  @SerializedName("thirdDailyReminderTime")
  private String thirdDailyReminderTime = null;
  @SerializedName("startTrackingDate")
  private Date startTrackingDate = null;
  @SerializedName("stopTrackingDate")
  private Date stopTrackingDate = null;
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
   * id
   **/
  @ApiModelProperty(value = "id")
  public Integer getId() {
    return id;
  }
  public void setId(Integer id) {
    this.id = id;
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
  @ApiModelProperty(required = true, value = "Id for the variable to be tracked")
  public Integer getVariableId() {
    return variableId;
  }
  public void setVariableId(Integer variableId) {
    this.variableId = variableId;
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
   * Earliest time of day at which reminders should appear
   **/
  @ApiModelProperty(value = "Earliest time of day at which reminders should appear")
  public String getReminderStartTime() {
    return reminderStartTime;
  }
  public void setReminderStartTime(String reminderStartTime) {
    this.reminderStartTime = reminderStartTime;
  }

  
  /**
   * Latest time of day at which reminders should appear
   **/
  @ApiModelProperty(value = "Latest time of day at which reminders should appear")
  public String getReminderEndTime() {
    return reminderEndTime;
  }
  public void setReminderEndTime(String reminderEndTime) {
    this.reminderEndTime = reminderEndTime;
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
   * Number of seconds between one reminder and the next
   **/
  @ApiModelProperty(value = "Number of seconds between one reminder and the next")
  public Integer getReminderFrequency() {
    return reminderFrequency;
  }
  public void setReminderFrequency(Integer reminderFrequency) {
    this.reminderFrequency = reminderFrequency;
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
   * ISO 8601 timestamp for the last time a reminder was sent
   **/
  @ApiModelProperty(value = "ISO 8601 timestamp for the last time a reminder was sent")
  public Date getLastReminded() {
    return lastReminded;
  }
  public void setLastReminded(Date lastReminded) {
    this.lastReminded = lastReminded;
  }

  
  /**
   * ISO 8601 timestamp for the last time a measurement was received for this user and variable
   **/
  @ApiModelProperty(value = "ISO 8601 timestamp for the last time a measurement was received for this user and variable")
  public Date getLastTracked() {
    return lastTracked;
  }
  public void setLastTracked(Date lastTracked) {
    this.lastTracked = lastTracked;
  }

  
  /**
   * Specific first time of day that the user should be reminded to track in HH:MM:SS format
   **/
  @ApiModelProperty(value = "Specific first time of day that the user should be reminded to track in HH:MM:SS format")
  public String getFirstDailyReminderTime() {
    return firstDailyReminderTime;
  }
  public void setFirstDailyReminderTime(String firstDailyReminderTime) {
    this.firstDailyReminderTime = firstDailyReminderTime;
  }

  
  /**
   * Specific second time of day that the user should be reminded to track in HH:MM:SS format
   **/
  @ApiModelProperty(value = "Specific second time of day that the user should be reminded to track in HH:MM:SS format")
  public String getSecondDailyReminderTime() {
    return secondDailyReminderTime;
  }
  public void setSecondDailyReminderTime(String secondDailyReminderTime) {
    this.secondDailyReminderTime = secondDailyReminderTime;
  }

  
  /**
   * Specific third time of day that the user should be reminded to track in HH:MM:SS format
   **/
  @ApiModelProperty(value = "Specific third time of day that the user should be reminded to track in HH:MM:SS format")
  public String getThirdDailyReminderTime() {
    return thirdDailyReminderTime;
  }
  public void setThirdDailyReminderTime(String thirdDailyReminderTime) {
    this.thirdDailyReminderTime = thirdDailyReminderTime;
  }

  
  /**
   * Earliest date on which the user should be reminded to track in YYYY-MM-DD format
   **/
  @ApiModelProperty(value = "Earliest date on which the user should be reminded to track in YYYY-MM-DD format")
  public Date getStartTrackingDate() {
    return startTrackingDate;
  }
  public void setStartTrackingDate(Date startTrackingDate) {
    this.startTrackingDate = startTrackingDate;
  }

  
  /**
   * Latest date on which the user should be reminded to track in YYYY-MM-DD format
   **/
  @ApiModelProperty(value = "Latest date on which the user should be reminded to track in YYYY-MM-DD format")
  public Date getStopTrackingDate() {
    return stopTrackingDate;
  }
  public void setStopTrackingDate(Date stopTrackingDate) {
    this.stopTrackingDate = stopTrackingDate;
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
    sb.append("class TrackingReminder {\n");
    
    sb.append("  id: ").append(id).append("\n");
    sb.append("  clientId: ").append(clientId).append("\n");
    sb.append("  userId: ").append(userId).append("\n");
    sb.append("  variableId: ").append(variableId).append("\n");
    sb.append("  defaultValue: ").append(defaultValue).append("\n");
    sb.append("  reminderStartTime: ").append(reminderStartTime).append("\n");
    sb.append("  reminderEndTime: ").append(reminderEndTime).append("\n");
    sb.append("  reminderSound: ").append(reminderSound).append("\n");
    sb.append("  reminderFrequency: ").append(reminderFrequency).append("\n");
    sb.append("  popUp: ").append(popUp).append("\n");
    sb.append("  sms: ").append(sms).append("\n");
    sb.append("  email: ").append(email).append("\n");
    sb.append("  notificationBar: ").append(notificationBar).append("\n");
    sb.append("  lastReminded: ").append(lastReminded).append("\n");
    sb.append("  lastTracked: ").append(lastTracked).append("\n");
    sb.append("  firstDailyReminderTime: ").append(firstDailyReminderTime).append("\n");
    sb.append("  secondDailyReminderTime: ").append(secondDailyReminderTime).append("\n");
    sb.append("  thirdDailyReminderTime: ").append(thirdDailyReminderTime).append("\n");
    sb.append("  startTrackingDate: ").append(startTrackingDate).append("\n");
    sb.append("  stopTrackingDate: ").append(stopTrackingDate).append("\n");
    sb.append("  updatedAt: ").append(updatedAt).append("\n");
    sb.append("  variableName: ").append(variableName).append("\n");
    sb.append("  variableCategoryName: ").append(variableCategoryName).append("\n");
    sb.append("  abbreviatedUnitName: ").append(abbreviatedUnitName).append("\n");
    sb.append("  combinationOperation: ").append(combinationOperation).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
