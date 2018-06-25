package io.swagger.client.model;

import java.math.BigDecimal;

import io.swagger.annotations.*;
import com.google.gson.annotations.SerializedName;


@ApiModel(description = "")
public class Correlation  {
  
  @SerializedName("correlationCoefficient")
  private BigDecimal correlationCoefficient = null;
  @SerializedName("cause")
  private String cause = null;
  @SerializedName("originalCause")
  private String originalCause = null;
  @SerializedName("effect")
  private String effect = null;
  @SerializedName("originalEffect")
  private String originalEffect = null;
  @SerializedName("onsetDelay")
  private Double onsetDelay = null;
  @SerializedName("durationOfAction")
  private BigDecimal durationOfAction = null;
  @SerializedName("numberOfPairs")
  private BigDecimal numberOfPairs = null;
  @SerializedName("effectSize")
  private String effectSize = null;
  @SerializedName("statisticalSignificance")
  private String statisticalSignificance = null;
  @SerializedName("timestamp")
  private BigDecimal timestamp = null;
  @SerializedName("reverseCorrelation")
  private BigDecimal reverseCorrelation = null;
  @SerializedName("causalityFactor")
  private BigDecimal causalityFactor = null;
  @SerializedName("causeCategory")
  private String causeCategory = null;
  @SerializedName("effectCategory")
  private String effectCategory = null;
  @SerializedName("valuePredictingHighOutcome")
  private BigDecimal valuePredictingHighOutcome = null;
  @SerializedName("valuePredictingLowOutcome")
  private BigDecimal valuePredictingLowOutcome = null;
  @SerializedName("optimalPearsonProduct")
  private BigDecimal optimalPearsonProduct = null;
  @SerializedName("averageVote")
  private BigDecimal averageVote = null;
  @SerializedName("userVote")
  private BigDecimal userVote = null;
  @SerializedName("causeUnit")
  private String causeUnit = null;
  @SerializedName("causeUnitId")
  private Integer causeUnitId = null;

  
  /**
   * Pearson correlation coefficient between cause and effect measurements
   **/
  @ApiModelProperty(required = true, value = "Pearson correlation coefficient between cause and effect measurements")
  public BigDecimal getCorrelationCoefficient() {
    return correlationCoefficient;
  }
  public void setCorrelationCoefficient(BigDecimal correlationCoefficient) {
    this.correlationCoefficient = correlationCoefficient;
  }

  
  /**
   * ORIGINAL variable name of the cause variable for which the user desires correlations.
   **/
  @ApiModelProperty(required = true, value = "ORIGINAL variable name of the cause variable for which the user desires correlations.")
  public String getCause() {
    return cause;
  }
  public void setCause(String cause) {
    this.cause = cause;
  }

  
  /**
   * original name of the cause.
   **/
  @ApiModelProperty(value = "original name of the cause.")
  public String getOriginalCause() {
    return originalCause;
  }
  public void setOriginalCause(String originalCause) {
    this.originalCause = originalCause;
  }

  
  /**
   * ORIGINAL variable name of the effect variable for which the user desires correlations.
   **/
  @ApiModelProperty(required = true, value = "ORIGINAL variable name of the effect variable for which the user desires correlations.")
  public String getEffect() {
    return effect;
  }
  public void setEffect(String effect) {
    this.effect = effect;
  }

  
  /**
   * effect variable original name.
   **/
  @ApiModelProperty(value = "effect variable original name.")
  public String getOriginalEffect() {
    return originalEffect;
  }
  public void setOriginalEffect(String originalEffect) {
    this.originalEffect = originalEffect;
  }

  
  /**
   * User estimated or default time after cause measurement before a perceivable effect is observed
   **/
  @ApiModelProperty(required = true, value = "User estimated or default time after cause measurement before a perceivable effect is observed")
  public Double getOnsetDelay() {
    return onsetDelay;
  }
  public void setOnsetDelay(Double onsetDelay) {
    this.onsetDelay = onsetDelay;
  }

  
  /**
   * Time over which the cause is expected to produce a perceivable effect following the onset delay
   **/
  @ApiModelProperty(required = true, value = "Time over which the cause is expected to produce a perceivable effect following the onset delay")
  public BigDecimal getDurationOfAction() {
    return durationOfAction;
  }
  public void setDurationOfAction(BigDecimal durationOfAction) {
    this.durationOfAction = durationOfAction;
  }

  
  /**
   * Number of points that went into the correlation calculation
   **/
  @ApiModelProperty(required = true, value = "Number of points that went into the correlation calculation")
  public BigDecimal getNumberOfPairs() {
    return numberOfPairs;
  }
  public void setNumberOfPairs(BigDecimal numberOfPairs) {
    this.numberOfPairs = numberOfPairs;
  }

  
  /**
   * Magnitude of the effects of a cause indicating whether it's practically meaningful.
   **/
  @ApiModelProperty(value = "Magnitude of the effects of a cause indicating whether it's practically meaningful.")
  public String getEffectSize() {
    return effectSize;
  }
  public void setEffectSize(String effectSize) {
    this.effectSize = effectSize;
  }

  
  /**
   * A function of the effect size and sample size
   **/
  @ApiModelProperty(value = "A function of the effect size and sample size")
  public String getStatisticalSignificance() {
    return statisticalSignificance;
  }
  public void setStatisticalSignificance(String statisticalSignificance) {
    this.statisticalSignificance = statisticalSignificance;
  }

  
  /**
   * Time at which correlation was calculated
   **/
  @ApiModelProperty(required = true, value = "Time at which correlation was calculated")
  public BigDecimal getTimestamp() {
    return timestamp;
  }
  public void setTimestamp(BigDecimal timestamp) {
    this.timestamp = timestamp;
  }

  
  /**
   * Correlation when cause and effect are reversed. For any causal relationship, the forward correlation should exceed the reverse correlation.
   **/
  @ApiModelProperty(value = "Correlation when cause and effect are reversed. For any causal relationship, the forward correlation should exceed the reverse correlation.")
  public BigDecimal getReverseCorrelation() {
    return reverseCorrelation;
  }
  public void setReverseCorrelation(BigDecimal reverseCorrelation) {
    this.reverseCorrelation = reverseCorrelation;
  }

  
  /**
   * 
   **/
  @ApiModelProperty(value = "")
  public BigDecimal getCausalityFactor() {
    return causalityFactor;
  }
  public void setCausalityFactor(BigDecimal causalityFactor) {
    this.causalityFactor = causalityFactor;
  }

  
  /**
   * Variable category of the cause variable.
   **/
  @ApiModelProperty(value = "Variable category of the cause variable.")
  public String getCauseCategory() {
    return causeCategory;
  }
  public void setCauseCategory(String causeCategory) {
    this.causeCategory = causeCategory;
  }

  
  /**
   * Variable category of the effect variable.
   **/
  @ApiModelProperty(value = "Variable category of the effect variable.")
  public String getEffectCategory() {
    return effectCategory;
  }
  public void setEffectCategory(String effectCategory) {
    this.effectCategory = effectCategory;
  }

  
  /**
   * cause value that predicts an above average effect value (in default unit for cause variable)
   **/
  @ApiModelProperty(value = "cause value that predicts an above average effect value (in default unit for cause variable)")
  public BigDecimal getValuePredictingHighOutcome() {
    return valuePredictingHighOutcome;
  }
  public void setValuePredictingHighOutcome(BigDecimal valuePredictingHighOutcome) {
    this.valuePredictingHighOutcome = valuePredictingHighOutcome;
  }

  
  /**
   * cause value that predicts a below average effect value (in default unit for cause variable)
   **/
  @ApiModelProperty(value = "cause value that predicts a below average effect value (in default unit for cause variable)")
  public BigDecimal getValuePredictingLowOutcome() {
    return valuePredictingLowOutcome;
  }
  public void setValuePredictingLowOutcome(BigDecimal valuePredictingLowOutcome) {
    this.valuePredictingLowOutcome = valuePredictingLowOutcome;
  }

  
  /**
   * Optimal Pearson Product
   **/
  @ApiModelProperty(value = "Optimal Pearson Product")
  public BigDecimal getOptimalPearsonProduct() {
    return optimalPearsonProduct;
  }
  public void setOptimalPearsonProduct(BigDecimal optimalPearsonProduct) {
    this.optimalPearsonProduct = optimalPearsonProduct;
  }

  
  /**
   * Average Vote
   **/
  @ApiModelProperty(value = "Average Vote")
  public BigDecimal getAverageVote() {
    return averageVote;
  }
  public void setAverageVote(BigDecimal averageVote) {
    this.averageVote = averageVote;
  }

  
  /**
   * User Vote
   **/
  @ApiModelProperty(value = "User Vote")
  public BigDecimal getUserVote() {
    return userVote;
  }
  public void setUserVote(BigDecimal userVote) {
    this.userVote = userVote;
  }

  
  /**
   * Unit of the predictor variable
   **/
  @ApiModelProperty(value = "Unit of the predictor variable")
  public String getCauseUnit() {
    return causeUnit;
  }
  public void setCauseUnit(String causeUnit) {
    this.causeUnit = causeUnit;
  }

  
  /**
   * Unit Id of the predictor variable
   **/
  @ApiModelProperty(value = "Unit Id of the predictor variable")
  public Integer getCauseUnitId() {
    return causeUnitId;
  }
  public void setCauseUnitId(Integer causeUnitId) {
    this.causeUnitId = causeUnitId;
  }

  

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class Correlation {\n");
    
    sb.append("  correlationCoefficient: ").append(correlationCoefficient).append("\n");
    sb.append("  cause: ").append(cause).append("\n");
    sb.append("  originalCause: ").append(originalCause).append("\n");
    sb.append("  effect: ").append(effect).append("\n");
    sb.append("  originalEffect: ").append(originalEffect).append("\n");
    sb.append("  onsetDelay: ").append(onsetDelay).append("\n");
    sb.append("  durationOfAction: ").append(durationOfAction).append("\n");
    sb.append("  numberOfPairs: ").append(numberOfPairs).append("\n");
    sb.append("  effectSize: ").append(effectSize).append("\n");
    sb.append("  statisticalSignificance: ").append(statisticalSignificance).append("\n");
    sb.append("  timestamp: ").append(timestamp).append("\n");
    sb.append("  reverseCorrelation: ").append(reverseCorrelation).append("\n");
    sb.append("  causalityFactor: ").append(causalityFactor).append("\n");
    sb.append("  causeCategory: ").append(causeCategory).append("\n");
    sb.append("  effectCategory: ").append(effectCategory).append("\n");
    sb.append("  valuePredictingHighOutcome: ").append(valuePredictingHighOutcome).append("\n");
    sb.append("  valuePredictingLowOutcome: ").append(valuePredictingLowOutcome).append("\n");
    sb.append("  optimalPearsonProduct: ").append(optimalPearsonProduct).append("\n");
    sb.append("  averageVote: ").append(averageVote).append("\n");
    sb.append("  userVote: ").append(userVote).append("\n");
    sb.append("  causeUnit: ").append(causeUnit).append("\n");
    sb.append("  causeUnitId: ").append(causeUnitId).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
