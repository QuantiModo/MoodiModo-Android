package io.swagger.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import io.swagger.client.model.*;

public class JsonUtil {
  public static GsonBuilder gsonBuilder;

  static {
    gsonBuilder = new GsonBuilder();
    gsonBuilder.serializeNulls();
    gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss");
  }

  public static Gson getGson() {
    return gsonBuilder.create();
  }

  public static String serialize(Object obj){
    return getGson().toJson(obj);
  }

  public static <T> T deserializeToList(String jsonString, Class cls){
    return getGson().fromJson(jsonString, getListTypeForDeserialization(cls));
  }

  public static <T> T deserializeToObject(String jsonString, Class cls){
    return getGson().fromJson(jsonString, getTypeForDeserialization(cls));
  }

  public static Type getListTypeForDeserialization(Class cls) {
    String className = cls.getSimpleName();
    
    if ("CommonResponse".equalsIgnoreCase(className)) {
      return new TypeToken<List<CommonResponse>>(){}.getType();
    }
    
    if ("HumanTime".equalsIgnoreCase(className)) {
      return new TypeToken<List<HumanTime>>(){}.getType();
    }
    
    if ("ConnectorInstruction".equalsIgnoreCase(className)) {
      return new TypeToken<List<ConnectorInstruction>>(){}.getType();
    }
    
    if ("Connector".equalsIgnoreCase(className)) {
      return new TypeToken<List<Connector>>(){}.getType();
    }
    
    if ("ConnectorInfo".equalsIgnoreCase(className)) {
      return new TypeToken<List<ConnectorInfo>>(){}.getType();
    }
    
    if ("ConnectorInfoHistoryItem".equalsIgnoreCase(className)) {
      return new TypeToken<List<ConnectorInfoHistoryItem>>(){}.getType();
    }
    
    if ("ConversionStep".equalsIgnoreCase(className)) {
      return new TypeToken<List<ConversionStep>>(){}.getType();
    }
    
    if ("Correlation".equalsIgnoreCase(className)) {
      return new TypeToken<List<Correlation>>(){}.getType();
    }
    
    if ("JsonErrorResponse".equalsIgnoreCase(className)) {
      return new TypeToken<List<JsonErrorResponse>>(){}.getType();
    }
    
    if ("MeasurementSet".equalsIgnoreCase(className)) {
      return new TypeToken<List<MeasurementSet>>(){}.getType();
    }
    
    if ("Measurement".equalsIgnoreCase(className)) {
      return new TypeToken<List<Measurement>>(){}.getType();
    }
    
    if ("MeasurementDelete".equalsIgnoreCase(className)) {
      return new TypeToken<List<MeasurementDelete>>(){}.getType();
    }
    
    if ("MeasurementRange".equalsIgnoreCase(className)) {
      return new TypeToken<List<MeasurementRange>>(){}.getType();
    }
    
    if ("MeasurementSource".equalsIgnoreCase(className)) {
      return new TypeToken<List<MeasurementSource>>(){}.getType();
    }
    
    if ("Pairs".equalsIgnoreCase(className)) {
      return new TypeToken<List<Pairs>>(){}.getType();
    }
    
    if ("Permission".equalsIgnoreCase(className)) {
      return new TypeToken<List<Permission>>(){}.getType();
    }
    
    if ("PostVote".equalsIgnoreCase(className)) {
      return new TypeToken<List<PostVote>>(){}.getType();
    }
    
    if ("PostCorrelation".equalsIgnoreCase(className)) {
      return new TypeToken<List<PostCorrelation>>(){}.getType();
    }
    
    if ("TrackingReminderDelete".equalsIgnoreCase(className)) {
      return new TypeToken<List<TrackingReminderDelete>>(){}.getType();
    }
    
    if ("TrackingReminderPendingSkip".equalsIgnoreCase(className)) {
      return new TypeToken<List<TrackingReminderPendingSkip>>(){}.getType();
    }
    
    if ("TrackingReminderPendingSnooze".equalsIgnoreCase(className)) {
      return new TypeToken<List<TrackingReminderPendingSnooze>>(){}.getType();
    }
    
    if ("TrackingReminderPendingTrack".equalsIgnoreCase(className)) {
      return new TypeToken<List<TrackingReminderPendingTrack>>(){}.getType();
    }
    
    if ("TrackingReminder".equalsIgnoreCase(className)) {
      return new TypeToken<List<TrackingReminder>>(){}.getType();
    }
    
    if ("TrackingReminderPending".equalsIgnoreCase(className)) {
      return new TypeToken<List<TrackingReminderPending>>(){}.getType();
    }
    
    if ("Unit".equalsIgnoreCase(className)) {
      return new TypeToken<List<Unit>>(){}.getType();
    }
    
    if ("UnitCategory".equalsIgnoreCase(className)) {
      return new TypeToken<List<UnitCategory>>(){}.getType();
    }
    
    if ("User".equalsIgnoreCase(className)) {
      return new TypeToken<List<User>>(){}.getType();
    }
    
    if ("UserTag".equalsIgnoreCase(className)) {
      return new TypeToken<List<UserTag>>(){}.getType();
    }
    
    if ("UserTokenRequest".equalsIgnoreCase(className)) {
      return new TypeToken<List<UserTokenRequest>>(){}.getType();
    }
    
    if ("UserTokenSuccessfulResponse".equalsIgnoreCase(className)) {
      return new TypeToken<List<UserTokenSuccessfulResponse>>(){}.getType();
    }
    
    if ("UserTokenFailedResponse".equalsIgnoreCase(className)) {
      return new TypeToken<List<UserTokenFailedResponse>>(){}.getType();
    }
    
    if ("UserTokenRequestInnerUserField".equalsIgnoreCase(className)) {
      return new TypeToken<List<UserTokenRequestInnerUserField>>(){}.getType();
    }
    
    if ("UserTokenSuccessfulResponseInnerUserField".equalsIgnoreCase(className)) {
      return new TypeToken<List<UserTokenSuccessfulResponseInnerUserField>>(){}.getType();
    }
    
    if ("ValueObject".equalsIgnoreCase(className)) {
      return new TypeToken<List<ValueObject>>(){}.getType();
    }
    
    if ("Variable".equalsIgnoreCase(className)) {
      return new TypeToken<List<Variable>>(){}.getType();
    }
    
    if ("VariableCategory".equalsIgnoreCase(className)) {
      return new TypeToken<List<VariableCategory>>(){}.getType();
    }
    
    if ("UserVariables".equalsIgnoreCase(className)) {
      return new TypeToken<List<UserVariables>>(){}.getType();
    }
    
    if ("VariableNew".equalsIgnoreCase(className)) {
      return new TypeToken<List<VariableNew>>(){}.getType();
    }
    
    if ("VariablesNew".equalsIgnoreCase(className)) {
      return new TypeToken<List<VariablesNew>>(){}.getType();
    }
    
    if ("VoteDelete".equalsIgnoreCase(className)) {
      return new TypeToken<List<VoteDelete>>(){}.getType();
    }
    
    if ("InlineResponse200".equalsIgnoreCase(className)) {
      return new TypeToken<List<InlineResponse200>>(){}.getType();
    }
    
    if ("InlineResponse2001".equalsIgnoreCase(className)) {
      return new TypeToken<List<InlineResponse2001>>(){}.getType();
    }
    
    if ("InlineResponse2002".equalsIgnoreCase(className)) {
      return new TypeToken<List<InlineResponse2002>>(){}.getType();
    }
    
    if ("InlineResponse2003".equalsIgnoreCase(className)) {
      return new TypeToken<List<InlineResponse2003>>(){}.getType();
    }
    
    if ("InlineResponse2004".equalsIgnoreCase(className)) {
      return new TypeToken<List<InlineResponse2004>>(){}.getType();
    }
    
    return new TypeToken<List<Object>>(){}.getType();
  }

  public static Type getTypeForDeserialization(Class cls) {
    String className = cls.getSimpleName();
    
    if ("CommonResponse".equalsIgnoreCase(className)) {
      return new TypeToken<CommonResponse>(){}.getType();
    }
    
    if ("HumanTime".equalsIgnoreCase(className)) {
      return new TypeToken<HumanTime>(){}.getType();
    }
    
    if ("ConnectorInstruction".equalsIgnoreCase(className)) {
      return new TypeToken<ConnectorInstruction>(){}.getType();
    }
    
    if ("Connector".equalsIgnoreCase(className)) {
      return new TypeToken<Connector>(){}.getType();
    }
    
    if ("ConnectorInfo".equalsIgnoreCase(className)) {
      return new TypeToken<ConnectorInfo>(){}.getType();
    }
    
    if ("ConnectorInfoHistoryItem".equalsIgnoreCase(className)) {
      return new TypeToken<ConnectorInfoHistoryItem>(){}.getType();
    }
    
    if ("ConversionStep".equalsIgnoreCase(className)) {
      return new TypeToken<ConversionStep>(){}.getType();
    }
    
    if ("Correlation".equalsIgnoreCase(className)) {
      return new TypeToken<Correlation>(){}.getType();
    }
    
    if ("JsonErrorResponse".equalsIgnoreCase(className)) {
      return new TypeToken<JsonErrorResponse>(){}.getType();
    }
    
    if ("MeasurementSet".equalsIgnoreCase(className)) {
      return new TypeToken<MeasurementSet>(){}.getType();
    }
    
    if ("Measurement".equalsIgnoreCase(className)) {
      return new TypeToken<Measurement>(){}.getType();
    }
    
    if ("MeasurementDelete".equalsIgnoreCase(className)) {
      return new TypeToken<MeasurementDelete>(){}.getType();
    }
    
    if ("MeasurementRange".equalsIgnoreCase(className)) {
      return new TypeToken<MeasurementRange>(){}.getType();
    }
    
    if ("MeasurementSource".equalsIgnoreCase(className)) {
      return new TypeToken<MeasurementSource>(){}.getType();
    }
    
    if ("Pairs".equalsIgnoreCase(className)) {
      return new TypeToken<Pairs>(){}.getType();
    }
    
    if ("Permission".equalsIgnoreCase(className)) {
      return new TypeToken<Permission>(){}.getType();
    }
    
    if ("PostVote".equalsIgnoreCase(className)) {
      return new TypeToken<PostVote>(){}.getType();
    }
    
    if ("PostCorrelation".equalsIgnoreCase(className)) {
      return new TypeToken<PostCorrelation>(){}.getType();
    }
    
    if ("TrackingReminderDelete".equalsIgnoreCase(className)) {
      return new TypeToken<TrackingReminderDelete>(){}.getType();
    }
    
    if ("TrackingReminderPendingSkip".equalsIgnoreCase(className)) {
      return new TypeToken<TrackingReminderPendingSkip>(){}.getType();
    }
    
    if ("TrackingReminderPendingSnooze".equalsIgnoreCase(className)) {
      return new TypeToken<TrackingReminderPendingSnooze>(){}.getType();
    }
    
    if ("TrackingReminderPendingTrack".equalsIgnoreCase(className)) {
      return new TypeToken<TrackingReminderPendingTrack>(){}.getType();
    }
    
    if ("TrackingReminder".equalsIgnoreCase(className)) {
      return new TypeToken<TrackingReminder>(){}.getType();
    }
    
    if ("TrackingReminderPending".equalsIgnoreCase(className)) {
      return new TypeToken<TrackingReminderPending>(){}.getType();
    }
    
    if ("Unit".equalsIgnoreCase(className)) {
      return new TypeToken<Unit>(){}.getType();
    }
    
    if ("UnitCategory".equalsIgnoreCase(className)) {
      return new TypeToken<UnitCategory>(){}.getType();
    }
    
    if ("User".equalsIgnoreCase(className)) {
      return new TypeToken<User>(){}.getType();
    }
    
    if ("UserTag".equalsIgnoreCase(className)) {
      return new TypeToken<UserTag>(){}.getType();
    }
    
    if ("UserTokenRequest".equalsIgnoreCase(className)) {
      return new TypeToken<UserTokenRequest>(){}.getType();
    }
    
    if ("UserTokenSuccessfulResponse".equalsIgnoreCase(className)) {
      return new TypeToken<UserTokenSuccessfulResponse>(){}.getType();
    }
    
    if ("UserTokenFailedResponse".equalsIgnoreCase(className)) {
      return new TypeToken<UserTokenFailedResponse>(){}.getType();
    }
    
    if ("UserTokenRequestInnerUserField".equalsIgnoreCase(className)) {
      return new TypeToken<UserTokenRequestInnerUserField>(){}.getType();
    }
    
    if ("UserTokenSuccessfulResponseInnerUserField".equalsIgnoreCase(className)) {
      return new TypeToken<UserTokenSuccessfulResponseInnerUserField>(){}.getType();
    }
    
    if ("ValueObject".equalsIgnoreCase(className)) {
      return new TypeToken<ValueObject>(){}.getType();
    }
    
    if ("Variable".equalsIgnoreCase(className)) {
      return new TypeToken<Variable>(){}.getType();
    }
    
    if ("VariableCategory".equalsIgnoreCase(className)) {
      return new TypeToken<VariableCategory>(){}.getType();
    }
    
    if ("UserVariables".equalsIgnoreCase(className)) {
      return new TypeToken<UserVariables>(){}.getType();
    }
    
    if ("VariableNew".equalsIgnoreCase(className)) {
      return new TypeToken<VariableNew>(){}.getType();
    }
    
    if ("VariablesNew".equalsIgnoreCase(className)) {
      return new TypeToken<VariablesNew>(){}.getType();
    }
    
    if ("VoteDelete".equalsIgnoreCase(className)) {
      return new TypeToken<VoteDelete>(){}.getType();
    }
    
    if ("InlineResponse200".equalsIgnoreCase(className)) {
      return new TypeToken<InlineResponse200>(){}.getType();
    }
    
    if ("InlineResponse2001".equalsIgnoreCase(className)) {
      return new TypeToken<InlineResponse2001>(){}.getType();
    }
    
    if ("InlineResponse2002".equalsIgnoreCase(className)) {
      return new TypeToken<InlineResponse2002>(){}.getType();
    }
    
    if ("InlineResponse2003".equalsIgnoreCase(className)) {
      return new TypeToken<InlineResponse2003>(){}.getType();
    }
    
    if ("InlineResponse2004".equalsIgnoreCase(className)) {
      return new TypeToken<InlineResponse2004>(){}.getType();
    }
    
    return new TypeToken<Object>(){}.getType();
  }

};
