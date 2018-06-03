package io.github.pyvesb.alexarevolutionarycalendar;

import java.time.Clock;
import java.time.ZoneId;

import com.amazon.ask.Skill;
import com.amazon.ask.SkillStreamHandler;
import com.amazon.ask.builder.CustomSkillBuilder;

import io.github.pyvesb.alexarevolutionarycalendar.handlers.CancelStopIntentHandler;
import io.github.pyvesb.alexarevolutionarycalendar.handlers.DateIntentHandler;
import io.github.pyvesb.alexarevolutionarycalendar.handlers.HelpIntentHandler;
import io.github.pyvesb.alexarevolutionarycalendar.handlers.LaunchRequestHandler;

/**
 * Class used as a handler for AWS Lambda function calls. Delegates all processing to one of the request handler
 * instances. The handler field in the AWS Lambda console needs to be set to the following:
 * io.github.pyvesb.alexarevolutionarycalendar.RevolutionaryStreamHandler
 * 
 * @author Pyves
 *
 */
public class RevolutionaryStreamHandler extends SkillStreamHandler {

	private static final String SKILL_ID = "amzn1.ask.skill.d3dcc36a-fd4f-4367-95b2-f734f961d827";
	// When users ask for a specific date, Alexa will provide an ISO8601 date computed with the right timezone. When
	// they just ask for the date of the day (e.g. "give me the date"), there is no simple way to infer their
	// timezone. Default to Paris. See https://forums.developer.amazon.com/questions/8857/get-timezone.html
	private static final Clock CLOCK = Clock.system(ZoneId.of("Europe/Paris"));

	public RevolutionaryStreamHandler() {
		super(getSkill());
	}

	private static Skill getSkill() {
		return new CustomSkillBuilder()
				.addRequestHandlers(new CancelStopIntentHandler(),
						new HelpIntentHandler(),
						new LaunchRequestHandler(),
						new DateIntentHandler(CLOCK))
				.withSkillId(SKILL_ID)
				.build();
	}

}
