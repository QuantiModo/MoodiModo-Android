package com.moodimodo.things;

import android.content.ContentValues;
import com.moodimodo.Global;
import com.moodimodo.Log;
import com.moodimodo.MoodVariable;
import com.moodimodo.Utils;
import com.quantimodo.android.sdk.model.Measurement;
import com.quantimodo.android.sdk.model.MeasurementSet;
import static com.moodimodo.databases.DatabaseWrapper.*;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;


@Deprecated
public class MoodThing implements Serializable
{
	public static final int NUM_RESULT_TYPES = 22;

	public static final int RATING_VALUE_NULL = 0;
	public static final int RATING_VALUE_1 = 1;
	public static final int RATING_VALUE_2 = 2;
	public static final int RATING_VALUE_3 = 3;
	public static final int RATING_VALUE_4 = 4;
	public static final int RATING_VALUE_5 = 5;

	public static final int RATING_MOOD = 0;
	public static final int RATING_GUILTY = 1;
	public static final int RATING_ALERT = 2;
	public static final int RATING_AFRAID = 3;
	public static final int RATING_EXCITED = 4;
	public static final int RATING_IRRITABLE = 5;
	public static final int RATING_ASHAMED = 6;
	public static final int RATING_ATTENTIVE = 7;
	public static final int RATING_HOSTILE = 8;
	public static final int RATING_ACTIVE = 9;
	public static final int RATING_NERVOUS = 10;
	public static final int RATING_INTERESTED = 11;
	public static final int RATING_ENTHUSIASTIC = 12;
	public static final int RATING_JITTERY = 13;
	public static final int RATING_STRONG = 14;
	public static final int RATING_DISTRESSED = 15;
	public static final int RATING_DETERMINED = 16;
	public static final int RATING_UPSET = 17;
	public static final int RATING_PROUD = 18;
	public static final int RATING_SCARED = 19;
	public static final int RATING_INSPIRED = 20;
	public static final int RATING_ACCURATE_MOOD = 21;

	public static int averageMood;

	public long timestamp;
	public long timestampMillis;
	public int id;
	public int[] ratings;

	String mNote = "";
	boolean mIsUpdateNeeded = false;

	public double normalizedTimestamp;

	/**
	 *
	 * @param id the identifier of the measurement on the remote db
	 * @param timestamp time where the event occurred
	 * @param ratings array of ratings, length 21
	 */
	public MoodThing(int id, long timestamp, int[] ratings)
	{
		this.id = id;
		this.timestamp = timestamp;
		this.timestampMillis = timestamp * 1000;
		
		if(ratings.length == NUM_RESULT_TYPES)
		{
			this.ratings = ratings;
		}
		else
		{
			throw new IllegalArgumentException("Length of \"ratings\" should be 22, but is " + ratings.length);
		}
	}

	public boolean isIsUpdateNeeded() {
		return mIsUpdateNeeded;
	}

	public void setIsUpdateNeeded(boolean mIsUpdateNeeded) {
		this.mIsUpdateNeeded = mIsUpdateNeeded;
	}

	public String getNote() {
		return mNote;
	}

	public void setNote(String mNote) {
		this.mNote = mNote;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getOneToHundredMood()
	{
		if(ratings[RATING_ACCURATE_MOOD] != RATING_VALUE_NULL)
		{
			return ratings[RATING_ACCURATE_MOOD];
		}
		else
		{
			return (int) (((double) (ratings[RATING_MOOD]- 1) / 4) * 100) ;
		}
	}
	public int getOneToFiveMood()
	{
		if(ratings[RATING_ACCURATE_MOOD] != RATING_VALUE_NULL)
		{
			return MoodThing.normalizeValue( Utils.roundToNearestInteger((((double) ratings[RATING_ACCURATE_MOOD] / 100) * 5) + 1));
		}
		else
		{
			return MoodThing.normalizeValue( ratings[RATING_MOOD]);
		}


	}

	public static int normalizeValue(double value){
		if (value < 1) {
			return 1;
		} else if (value > 5){
			return 5;
		}

		return (int)Math.round(value);
	}

	public void calculateAccurateRating()
	{
		Log.i("Calculating accurate mood");

		int totalScore = 50;
		boolean allFilledIn = true;
		for(int i = 2; i < NUM_RESULT_TYPES; i++)
		{
			if(ratings[i] == MoodThing.RATING_VALUE_NULL)
			{
				allFilledIn = false;
				break;
			}

			MoodVariable v = MoodVariable.values()[i];

			if (v.isPositive()){
				totalScore += ratings[i];
			} else {
				totalScore -= ratings[i];
			}
		}

		if(allFilledIn)
		{
			ratings[RATING_ACCURATE_MOOD] = totalScore;
			Log.i("Accurate mood: " + ratings[RATING_ACCURATE_MOOD] + "%");
		}
		else
		{
			Log.i("Not all questions were filled in, cannot calculate accurate mood");
		}
	}

	public HashMap<Integer, MeasurementSet> toMeasurementSets(HashMap<Integer, MeasurementSet> measurementSets)
	{
		// These two are a bit special, since only one of the two should be uploaded
		Measurement measurement;
		if(ratings[RATING_ACCURATE_MOOD] != RATING_VALUE_NULL)
		{
			if(!measurementSets.containsKey(RATING_ACCURATE_MOOD))
			{
				measurementSets.put(RATING_ACCURATE_MOOD, new MeasurementSet("Overall Mood", null, "Emotions", "%", MeasurementSet.COMBINE_MEAN, Global.QUANTIMODO_SOURCE_NAME));
			}
			measurement = new Measurement(this.timestamp, ratings[MoodThing.RATING_ACCURATE_MOOD]);
			measurement.setNote(mNote);
			measurementSets.get(RATING_ACCURATE_MOOD).measurements.add(measurement);
		}
		else
		{
			if(!measurementSets.containsKey(RATING_MOOD))
			{
				measurementSets.put(RATING_MOOD, new MeasurementSet("Overall Mood", null, "Emotions", "/5", MeasurementSet.COMBINE_MEAN, Global.QUANTIMODO_SOURCE_NAME));
			}
			measurement = new Measurement(this.timestamp, ratings[MoodThing.RATING_MOOD]);
			measurement.setNote(mNote);
			measurementSets.get(RATING_MOOD).measurements.add(measurement);
		}

		// Loop through remaining mood rating types
		for(int i = 1; i < MoodThing.NUM_RESULT_TYPES - 1; i++)
		{
			// If the user inputted this rating
			if(ratings[i] != RATING_VALUE_NULL)
			{
				// Check if we already have a measurement set for this rating, if not, create one.
				if(!measurementSets.containsKey(i))
				{
					MoodVariable v = MoodVariable.values()[i];
					measurementSets.put(i,new MeasurementSet(v.getVariableName(),null,"Emotions","/5", MeasurementSet.COMBINE_MEAN, Global.QUANTIMODO_SOURCE_NAME));
				}

				// Add the measurement to the proper measurement set
				Measurement newMeasurement = new Measurement(this.timestamp, ratings[i]);
				measurementSets.get(i).getMeasurements().add(newMeasurement);
			}
		}

		// Return the filled hashmap
		return measurementSets;
	}

	public ContentValues toCV(){
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_TIMESTAMP_NAME,timestamp);
		cv.put(COLUMN_NOTE_NAME,mNote);
		cv.put(COLUMN_NEED_UPDATE_NAME,mIsUpdateNeeded ? 1 : 0);
		for (int i = 0; i < MoodThing.NUM_RESULT_TYPES; i++){
			cv.put(MoodVariable.values()[i].getColumnName(),ratings[i]);
		}
		cv.put(COLUMN_ID_NAME, id);
		return cv;
	}

	public static int[] createNullRatings(){
		int[] ratings = new int[NUM_RESULT_TYPES];
		Arrays.fill(ratings,RATING_VALUE_NULL);
		return ratings;
	}
}
