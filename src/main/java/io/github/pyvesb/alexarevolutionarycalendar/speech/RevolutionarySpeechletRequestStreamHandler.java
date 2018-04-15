package io.github.pyvesb.alexarevolutionarycalendar.speech;

import java.util.Collections;

import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;

/**
 * Class used as a handler for AWS Lambda function calls. Delegates all processing to a Speechlet instance. The handler
 * field in the AWS Lambda console needs to be set to the following:
 * io.github.pyvesb.alexarevolutionarycalendar.speech.RevolutionarySpeechletRequestStreamHandler
 * 
 * @author Pyves
 *
 */
public class RevolutionarySpeechletRequestStreamHandler extends SpeechletRequestStreamHandler {

	private static final String SUPPORTED_ID = "amzn1.ask.skill.d3dcc36a-fd4f-4367-95b2-f734f961d827";

	public RevolutionarySpeechletRequestStreamHandler() {
		super(new RevolutionarySpeechlet(), Collections.singleton(SUPPORTED_ID));
	}

}
