package com.moodimodo.things;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import com.moodimodo.R;

import java.util.Set;

public class Question
{
	public final String name;
	public final String title;
	public final String description;
	public final String variableName;

	public boolean isRequired;
	public int resultType;

	public final String iconPrefix;

	public Question(String name, String title, String description, String variableName, String iconPrefix)
	{
		this.name = name;
		this.title = title;
		this.description = description;
		this.variableName = variableName;
		this.iconPrefix = iconPrefix;
		this.isRequired = false;
	}

	public static Question[] getAllQuestions(Context context)
	{
		Resources res = context.getResources();
		String[] questionTitles = res.getStringArray(R.array.questions);
		String[] questionDescriptions = res.getStringArray(R.array.questions_description);
		String[] variableNames = res.getStringArray(R.array.questions_variables);

		if (questionTitles.length != questionDescriptions.length || questionDescriptions.length != variableNames.length){
			throw new RuntimeException(new IllegalArgumentException("Defined questions should have same amount of fields."));
		}

		String[] selectedRequiredRatings;

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Set<String> required = prefs.getStringSet("requiredRatings", null);
		selectedRequiredRatings = required == null ? new String[0] : required.toArray(new String[required.size()]);

		Question[] questions = new Question[questionTitles.length];
		for(int i = 0; i < questionTitles.length; i++)
		{
			int startLocation = questionTitles[i].indexOf("[");
			int endLocation = questionTitles[i].indexOf("]");

			int nameStartLocation = questionTitles[i].indexOf("{");
			int nameEndLocation = questionTitles[i].indexOf("}");

			String iconPrefix = questionTitles[i].substring(startLocation + 1, endLocation);
			String moodName = questionTitles[i].substring(nameStartLocation + 1, nameEndLocation);
			questionTitles[i] = questionTitles[i].substring(endLocation + 1, questionTitles[i].length());

			questions[i] = new Question(moodName, questionTitles[i], questionDescriptions[i], variableNames[i], iconPrefix);
		}

		questions[0].isRequired = true;
		questions[0].resultType = 1;

		for(String requiredRatingType : selectedRequiredRatings)
		{
			for(int i = 1; i < questions.length; i++)
			{
				if(requiredRatingType.equals(questions[i].variableName))
				{
					questions[i].isRequired = true;
					break;
				}
			}
		}

		return questions;
	}
}
