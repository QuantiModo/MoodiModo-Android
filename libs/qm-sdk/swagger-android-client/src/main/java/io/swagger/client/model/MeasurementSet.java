package io.swagger.client.model;

import io.swagger.client.model.ValueObject;
import java.util.*;

import io.swagger.annotations.*;
import com.google.gson.annotations.SerializedName;


@ApiModel(description = "")
public class MeasurementSet  {
  
  @SerializedName("measurements")
  private List<ValueObject> measurements = null;
  @SerializedName("name")
  private String name = null;
  @SerializedName("source")
  private String source = null;
  @SerializedName("category")
  private String category = null;
  public enum CombinationOperationEnum {
     MEAN,  SUM, 
  };
  @SerializedName("combinationOperation")
  private CombinationOperationEnum combinationOperation = null;
  @SerializedName("unit")
  private String unit = null;

  
  /**
   * Array of timestamps, values, and optional notes
   **/
  @ApiModelProperty(required = true, value = "Array of timestamps, values, and optional notes")
  public List<ValueObject> getMeasurements() {
    return measurements;
  }
  public void setMeasurements(List<ValueObject> measurements) {
    this.measurements = measurements;
  }

  
  /**
   * ORIGINAL name of the variable for which we are creating the measurement records
   **/
  @ApiModelProperty(required = true, value = "ORIGINAL name of the variable for which we are creating the measurement records")
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }

  
  /**
   * Name of the application or device used to record the measurement values
   **/
  @ApiModelProperty(required = true, value = "Name of the application or device used to record the measurement values")
  public String getSource() {
    return source;
  }
  public void setSource(String source) {
    this.source = source;
  }

  
  /**
   * Variable category name
   **/
  @ApiModelProperty(value = "Variable category name")
  public String getCategory() {
    return category;
  }
  public void setCategory(String category) {
    this.category = category;
  }

  
  /**
   * Way to aggregate measurements over time. Options are \"MEAN\" or \"SUM\".  SUM should be used for things like minutes of exercise.  If you use MEAN for exercise, then a person might exercise more minutes in one day but add separate measurements that were smaller.  So when we are doing correlational analysis, we would think that the person exercised less that day even though they exercised more.  Conversely, we must use MEAN for things such as ratings which cannot be SUMMED.
   **/
  @ApiModelProperty(value = "Way to aggregate measurements over time. Options are \"MEAN\" or \"SUM\".  SUM should be used for things like minutes of exercise.  If you use MEAN for exercise, then a person might exercise more minutes in one day but add separate measurements that were smaller.  So when we are doing correlational analysis, we would think that the person exercised less that day even though they exercised more.  Conversely, we must use MEAN for things such as ratings which cannot be SUMMED.")
  public CombinationOperationEnum getCombinationOperation() {
    return combinationOperation;
  }
  public void setCombinationOperation(CombinationOperationEnum combinationOperation) {
    this.combinationOperation = combinationOperation;
  }

  
  /**
   * Unit of measurement
   **/
  @ApiModelProperty(required = true, value = "Unit of measurement")
  public String getUnit() {
    return unit;
  }
  public void setUnit(String unit) {
    this.unit = unit;
  }

  

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class MeasurementSet {\n");
    
    sb.append("  measurements: ").append(measurements).append("\n");
    sb.append("  name: ").append(name).append("\n");
    sb.append("  source: ").append(source).append("\n");
    sb.append("  category: ").append(category).append("\n");
    sb.append("  combinationOperation: ").append(combinationOperation).append("\n");
    sb.append("  unit: ").append(unit).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
