package io.swagger.client.model;

import io.swagger.client.model.ConversionStep;
import java.util.*;

import io.swagger.annotations.*;
import com.google.gson.annotations.SerializedName;


@ApiModel(description = "")
public class Unit  {
  
  @SerializedName("name")
  private String name = null;
  @SerializedName("abbreviatedName")
  private String abbreviatedName = null;
  public enum CategoryEnum {
     Distance,  Duration,  Energy,  Frequency,  Miscellany,  Pressure,  Proportion,  Rating,  Temperature,  Volume,  Weight, 
  };
  @SerializedName("category")
  private CategoryEnum category = null;
  @SerializedName("minimumValue")
  private Double minimumValue = null;
  @SerializedName("maximumValue")
  private Double maximumValue = null;
  @SerializedName("conversionSteps")
  private List<ConversionStep> conversionSteps = null;

  
  /**
   * Unit name
   **/
  @ApiModelProperty(required = true, value = "Unit name")
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }

  
  /**
   * Unit abbreviation
   **/
  @ApiModelProperty(required = true, value = "Unit abbreviation")
  public String getAbbreviatedName() {
    return abbreviatedName;
  }
  public void setAbbreviatedName(String abbreviatedName) {
    this.abbreviatedName = abbreviatedName;
  }

  
  /**
   * Unit category
   **/
  @ApiModelProperty(required = true, value = "Unit category")
  public CategoryEnum getCategory() {
    return category;
  }
  public void setCategory(CategoryEnum category) {
    this.category = category;
  }

  
  /**
   * The smallest acceptable value for measurements using this unit
   **/
  @ApiModelProperty(value = "The smallest acceptable value for measurements using this unit")
  public Double getMinimumValue() {
    return minimumValue;
  }
  public void setMinimumValue(Double minimumValue) {
    this.minimumValue = minimumValue;
  }

  
  /**
   * The largest acceptable value for measurements using this unit
   **/
  @ApiModelProperty(value = "The largest acceptable value for measurements using this unit")
  public Double getMaximumValue() {
    return maximumValue;
  }
  public void setMaximumValue(Double maximumValue) {
    this.maximumValue = maximumValue;
  }

  
  /**
   * Conversion steps list
   **/
  @ApiModelProperty(required = true, value = "Conversion steps list")
  public List<ConversionStep> getConversionSteps() {
    return conversionSteps;
  }
  public void setConversionSteps(List<ConversionStep> conversionSteps) {
    this.conversionSteps = conversionSteps;
  }

  

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class Unit {\n");
    
    sb.append("  name: ").append(name).append("\n");
    sb.append("  abbreviatedName: ").append(abbreviatedName).append("\n");
    sb.append("  category: ").append(category).append("\n");
    sb.append("  minimumValue: ").append(minimumValue).append("\n");
    sb.append("  maximumValue: ").append(maximumValue).append("\n");
    sb.append("  conversionSteps: ").append(conversionSteps).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
