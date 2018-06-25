package io.swagger.client.model;

import io.swagger.client.model.Variable;
import java.util.*;

import io.swagger.annotations.*;
import com.google.gson.annotations.SerializedName;


@ApiModel(description = "")
public class Variable  {
  
  @SerializedName("id")
  private Integer id = null;
  @SerializedName("name")
  private String name = null;
  @SerializedName("originalName")
  private String originalName = null;
  @SerializedName("category")
  private String category = null;
  @SerializedName("abbreviatedUnitName")
  private String abbreviatedUnitName = null;
  @SerializedName("abbreviatedUnitId")
  private Integer abbreviatedUnitId = null;
  @SerializedName("sources")
  private String sources = null;
  @SerializedName("minimumValue")
  private Double minimumValue = null;
  @SerializedName("maximumValue")
  private Double maximumValue = null;
  public enum CombinationOperationEnum {
     MEAN,  SUM, 
  };
  @SerializedName("combinationOperation")
  private CombinationOperationEnum combinationOperation = null;
  @SerializedName("fillingValue")
  private Double fillingValue = null;
  @SerializedName("joinWith")
  private String joinWith = null;
  @SerializedName("joinedVariables")
  private List<Variable> joinedVariables = null;
  @SerializedName("parent")
  private Integer parent = null;
  @SerializedName("subVariables")
  private List<Variable> subVariables = null;
  @SerializedName("onsetDelay")
  private Integer onsetDelay = null;
  @SerializedName("durationOfAction")
  private Integer durationOfAction = null;
  @SerializedName("earliestMeasurementTime")
  private Integer earliestMeasurementTime = null;
  @SerializedName("latestMeasurementTime")
  private Integer latestMeasurementTime = null;
  @SerializedName("updated")
  private Integer updated = null;
  @SerializedName("causeOnly")
  private Integer causeOnly = null;
  @SerializedName("numberOfCorrelations")
  private Integer numberOfCorrelations = null;
  @SerializedName("outcome")
  private Integer outcome = null;
  @SerializedName("measurementsAtLastAnalysis")
  private Integer measurementsAtLastAnalysis = null;
  @SerializedName("numberOfMeasurements")
  private Integer numberOfMeasurements = null;
  @SerializedName("lastUnit")
  private String lastUnit = null;
  @SerializedName("lastValue")
  private Integer lastValue = null;
  @SerializedName("mostCommonValue")
  private Integer mostCommonValue = null;
  @SerializedName("mostCommonUnit")
  private String mostCommonUnit = null;
  @SerializedName("lastSource")
  private Integer lastSource = null;

  
  /**
   * Variable ID
   **/
  @ApiModelProperty(value = "Variable ID")
  public Integer getId() {
    return id;
  }
  public void setId(Integer id) {
    this.id = id;
  }

  
  /**
   * User-defined variable display name.
   **/
  @ApiModelProperty(required = true, value = "User-defined variable display name.")
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }

  
  /**
   * Name used when the variable was originally created in the `variables` table.
   **/
  @ApiModelProperty(required = true, value = "Name used when the variable was originally created in the `variables` table.")
  public String getOriginalName() {
    return originalName;
  }
  public void setOriginalName(String originalName) {
    this.originalName = originalName;
  }

  
  /**
   * Variable category like Mood, Sleep, Physical Activity, Treatment, Symptom, etc.
   **/
  @ApiModelProperty(required = true, value = "Variable category like Mood, Sleep, Physical Activity, Treatment, Symptom, etc.")
  public String getCategory() {
    return category;
  }
  public void setCategory(String category) {
    this.category = category;
  }

  
  /**
   * Abbreviated name of the default unit for the variable
   **/
  @ApiModelProperty(required = true, value = "Abbreviated name of the default unit for the variable")
  public String getAbbreviatedUnitName() {
    return abbreviatedUnitName;
  }
  public void setAbbreviatedUnitName(String abbreviatedUnitName) {
    this.abbreviatedUnitName = abbreviatedUnitName;
  }

  
  /**
   * Id of the default unit for the variable
   **/
  @ApiModelProperty(required = true, value = "Id of the default unit for the variable")
  public Integer getAbbreviatedUnitId() {
    return abbreviatedUnitId;
  }
  public void setAbbreviatedUnitId(Integer abbreviatedUnitId) {
    this.abbreviatedUnitId = abbreviatedUnitId;
  }

  
  /**
   * Comma-separated list of source names to limit variables to those sources
   **/
  @ApiModelProperty(required = true, value = "Comma-separated list of source names to limit variables to those sources")
  public String getSources() {
    return sources;
  }
  public void setSources(String sources) {
    this.sources = sources;
  }

  
  /**
   * Minimum reasonable value for this variable (uses default unit)
   **/
  @ApiModelProperty(required = true, value = "Minimum reasonable value for this variable (uses default unit)")
  public Double getMinimumValue() {
    return minimumValue;
  }
  public void setMinimumValue(Double minimumValue) {
    this.minimumValue = minimumValue;
  }

  
  /**
   * Maximum reasonable value for this variable (uses default unit)
   **/
  @ApiModelProperty(required = true, value = "Maximum reasonable value for this variable (uses default unit)")
  public Double getMaximumValue() {
    return maximumValue;
  }
  public void setMaximumValue(Double maximumValue) {
    this.maximumValue = maximumValue;
  }

  
  /**
   * Way to aggregate measurements over time. Options are \"MEAN\" or \"SUM\".  SUM should be used for things like minutes of exercise.  If you use MEAN for exercise, then a person might exercise more minutes in one day but add separate measurements that were smaller.  So when we are doing correlational analysis, we would think that the person exercised less that day even though they exercised more.  Conversely, we must use MEAN for things such as ratings which cannot be SUMMED.
   **/
  @ApiModelProperty(required = true, value = "Way to aggregate measurements over time. Options are \"MEAN\" or \"SUM\".  SUM should be used for things like minutes of exercise.  If you use MEAN for exercise, then a person might exercise more minutes in one day but add separate measurements that were smaller.  So when we are doing correlational analysis, we would think that the person exercised less that day even though they exercised more.  Conversely, we must use MEAN for things such as ratings which cannot be SUMMED.")
  public CombinationOperationEnum getCombinationOperation() {
    return combinationOperation;
  }
  public void setCombinationOperation(CombinationOperationEnum combinationOperation) {
    this.combinationOperation = combinationOperation;
  }

  
  /**
   * Value for replacing null measurements
   **/
  @ApiModelProperty(required = true, value = "Value for replacing null measurements")
  public Double getFillingValue() {
    return fillingValue;
  }
  public void setFillingValue(Double fillingValue) {
    this.fillingValue = fillingValue;
  }

  
  /**
   * The Variable this Variable should be joined with. If the variable is joined with some other variable then it is not shown to user in the list of variables.
   **/
  @ApiModelProperty(required = true, value = "The Variable this Variable should be joined with. If the variable is joined with some other variable then it is not shown to user in the list of variables.")
  public String getJoinWith() {
    return joinWith;
  }
  public void setJoinWith(String joinWith) {
    this.joinWith = joinWith;
  }

  
  /**
   * Array of Variables that are joined with this Variable
   **/
  @ApiModelProperty(required = true, value = "Array of Variables that are joined with this Variable")
  public List<Variable> getJoinedVariables() {
    return joinedVariables;
  }
  public void setJoinedVariables(List<Variable> joinedVariables) {
    this.joinedVariables = joinedVariables;
  }

  
  /**
   * Id of the parent variable if this variable has any parent
   **/
  @ApiModelProperty(required = true, value = "Id of the parent variable if this variable has any parent")
  public Integer getParent() {
    return parent;
  }
  public void setParent(Integer parent) {
    this.parent = parent;
  }

  
  /**
   * Array of Variables that are sub variables to this Variable
   **/
  @ApiModelProperty(required = true, value = "Array of Variables that are sub variables to this Variable")
  public List<Variable> getSubVariables() {
    return subVariables;
  }
  public void setSubVariables(List<Variable> subVariables) {
    this.subVariables = subVariables;
  }

  
  /**
   * How long it takes for a measurement in this variable to take effect
   **/
  @ApiModelProperty(required = true, value = "How long it takes for a measurement in this variable to take effect")
  public Integer getOnsetDelay() {
    return onsetDelay;
  }
  public void setOnsetDelay(Integer onsetDelay) {
    this.onsetDelay = onsetDelay;
  }

  
  /**
   * How long the effect of a measurement in this variable lasts
   **/
  @ApiModelProperty(required = true, value = "How long the effect of a measurement in this variable lasts")
  public Integer getDurationOfAction() {
    return durationOfAction;
  }
  public void setDurationOfAction(Integer durationOfAction) {
    this.durationOfAction = durationOfAction;
  }

  
  /**
   * Earliest measurement time
   **/
  @ApiModelProperty(required = true, value = "Earliest measurement time")
  public Integer getEarliestMeasurementTime() {
    return earliestMeasurementTime;
  }
  public void setEarliestMeasurementTime(Integer earliestMeasurementTime) {
    this.earliestMeasurementTime = earliestMeasurementTime;
  }

  
  /**
   * Latest measurement time
   **/
  @ApiModelProperty(required = true, value = "Latest measurement time")
  public Integer getLatestMeasurementTime() {
    return latestMeasurementTime;
  }
  public void setLatestMeasurementTime(Integer latestMeasurementTime) {
    this.latestMeasurementTime = latestMeasurementTime;
  }

  
  /**
   * When this variable or its settings were last updated
   **/
  @ApiModelProperty(required = true, value = "When this variable or its settings were last updated")
  public Integer getUpdated() {
    return updated;
  }
  public void setUpdated(Integer updated) {
    this.updated = updated;
  }

  
  /**
   * A value of 1 indicates that this variable is generally a cause in a causal relationship.  An example of a causeOnly variable would be a variable such as Cloud Cover which would generally not be influenced by the behaviour of the user.
   **/
  @ApiModelProperty(required = true, value = "A value of 1 indicates that this variable is generally a cause in a causal relationship.  An example of a causeOnly variable would be a variable such as Cloud Cover which would generally not be influenced by the behaviour of the user.")
  public Integer getCauseOnly() {
    return causeOnly;
  }
  public void setCauseOnly(Integer causeOnly) {
    this.causeOnly = causeOnly;
  }

  
  /**
   * Number of correlations
   **/
  @ApiModelProperty(required = true, value = "Number of correlations")
  public Integer getNumberOfCorrelations() {
    return numberOfCorrelations;
  }
  public void setNumberOfCorrelations(Integer numberOfCorrelations) {
    this.numberOfCorrelations = numberOfCorrelations;
  }

  
  /**
   * Outcome variables (those with `outcome` == 1) are variables for which a human would generally want to identify the influencing factors.  These include symptoms of illness, physique, mood, cognitive performance, etc.  Generally correlation calculations are only performed on outcome variables.
   **/
  @ApiModelProperty(required = true, value = "Outcome variables (those with `outcome` == 1) are variables for which a human would generally want to identify the influencing factors.  These include symptoms of illness, physique, mood, cognitive performance, etc.  Generally correlation calculations are only performed on outcome variables.")
  public Integer getOutcome() {
    return outcome;
  }
  public void setOutcome(Integer outcome) {
    this.outcome = outcome;
  }

  
  /**
   * The number of measurements that a given user had for this variable the last time a correlation calculation was performed. Generally correlation values are only updated once the current number of measurements for a variable is more than 10% greater than the measurementsAtLastAnalysis.  This avoids a computationally-demanding recalculation when there's not enough new data to make a significant difference in the correlation.
   **/
  @ApiModelProperty(required = true, value = "The number of measurements that a given user had for this variable the last time a correlation calculation was performed. Generally correlation values are only updated once the current number of measurements for a variable is more than 10% greater than the measurementsAtLastAnalysis.  This avoids a computationally-demanding recalculation when there's not enough new data to make a significant difference in the correlation.")
  public Integer getMeasurementsAtLastAnalysis() {
    return measurementsAtLastAnalysis;
  }
  public void setMeasurementsAtLastAnalysis(Integer measurementsAtLastAnalysis) {
    this.measurementsAtLastAnalysis = measurementsAtLastAnalysis;
  }

  
  /**
   * Number of measurements
   **/
  @ApiModelProperty(required = true, value = "Number of measurements")
  public Integer getNumberOfMeasurements() {
    return numberOfMeasurements;
  }
  public void setNumberOfMeasurements(Integer numberOfMeasurements) {
    this.numberOfMeasurements = numberOfMeasurements;
  }

  
  /**
   * Last unit
   **/
  @ApiModelProperty(required = true, value = "Last unit")
  public String getLastUnit() {
    return lastUnit;
  }
  public void setLastUnit(String lastUnit) {
    this.lastUnit = lastUnit;
  }

  
  /**
   * Last value
   **/
  @ApiModelProperty(required = true, value = "Last value")
  public Integer getLastValue() {
    return lastValue;
  }
  public void setLastValue(Integer lastValue) {
    this.lastValue = lastValue;
  }

  
  /**
   * Most common value
   **/
  @ApiModelProperty(required = true, value = "Most common value")
  public Integer getMostCommonValue() {
    return mostCommonValue;
  }
  public void setMostCommonValue(Integer mostCommonValue) {
    this.mostCommonValue = mostCommonValue;
  }

  
  /**
   * Most common unit
   **/
  @ApiModelProperty(required = true, value = "Most common unit")
  public String getMostCommonUnit() {
    return mostCommonUnit;
  }
  public void setMostCommonUnit(String mostCommonUnit) {
    this.mostCommonUnit = mostCommonUnit;
  }

  
  /**
   * Last source
   **/
  @ApiModelProperty(required = true, value = "Last source")
  public Integer getLastSource() {
    return lastSource;
  }
  public void setLastSource(Integer lastSource) {
    this.lastSource = lastSource;
  }

  

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class Variable {\n");
    
    sb.append("  id: ").append(id).append("\n");
    sb.append("  name: ").append(name).append("\n");
    sb.append("  originalName: ").append(originalName).append("\n");
    sb.append("  category: ").append(category).append("\n");
    sb.append("  abbreviatedUnitName: ").append(abbreviatedUnitName).append("\n");
    sb.append("  abbreviatedUnitId: ").append(abbreviatedUnitId).append("\n");
    sb.append("  sources: ").append(sources).append("\n");
    sb.append("  minimumValue: ").append(minimumValue).append("\n");
    sb.append("  maximumValue: ").append(maximumValue).append("\n");
    sb.append("  combinationOperation: ").append(combinationOperation).append("\n");
    sb.append("  fillingValue: ").append(fillingValue).append("\n");
    sb.append("  joinWith: ").append(joinWith).append("\n");
    sb.append("  joinedVariables: ").append(joinedVariables).append("\n");
    sb.append("  parent: ").append(parent).append("\n");
    sb.append("  subVariables: ").append(subVariables).append("\n");
    sb.append("  onsetDelay: ").append(onsetDelay).append("\n");
    sb.append("  durationOfAction: ").append(durationOfAction).append("\n");
    sb.append("  earliestMeasurementTime: ").append(earliestMeasurementTime).append("\n");
    sb.append("  latestMeasurementTime: ").append(latestMeasurementTime).append("\n");
    sb.append("  updated: ").append(updated).append("\n");
    sb.append("  causeOnly: ").append(causeOnly).append("\n");
    sb.append("  numberOfCorrelations: ").append(numberOfCorrelations).append("\n");
    sb.append("  outcome: ").append(outcome).append("\n");
    sb.append("  measurementsAtLastAnalysis: ").append(measurementsAtLastAnalysis).append("\n");
    sb.append("  numberOfMeasurements: ").append(numberOfMeasurements).append("\n");
    sb.append("  lastUnit: ").append(lastUnit).append("\n");
    sb.append("  lastValue: ").append(lastValue).append("\n");
    sb.append("  mostCommonValue: ").append(mostCommonValue).append("\n");
    sb.append("  mostCommonUnit: ").append(mostCommonUnit).append("\n");
    sb.append("  lastSource: ").append(lastSource).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
