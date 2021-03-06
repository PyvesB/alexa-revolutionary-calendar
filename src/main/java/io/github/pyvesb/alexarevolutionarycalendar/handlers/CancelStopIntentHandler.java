package io.github.pyvesb.alexarevolutionarycalendar.handlers;

import static com.amazon.ask.request.Predicates.intentName;

import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.RequestEnvelope;
import com.amazon.ask.model.Response;

public class CancelStopIntentHandler implements RequestHandler {

	private static final Logger LOGGER = LogManager.getLogger(CancelStopIntentHandler.class);

	@Override
	public boolean canHandle(HandlerInput input) {
		return input.matches(intentName("AMAZON.CancelIntent")) || input.matches(intentName("AMAZON.StopIntent"));
	}

	@Override
	public Optional<Response> handle(HandlerInput input) {
		RequestEnvelope envelope = input.getRequestEnvelope();
		String locale = envelope.getRequest().getLocale();
		LOGGER.info("Cancel/stop intent (session={}, locale={})", envelope.getSession().getSessionId(), locale);
		String speechText = ResourceBundle.getBundle("messages", Locale.forLanguageTag(locale)).getString("cancel-stop");
		return input.getResponseBuilder()
				.withSpeech(speechText)
				.withShouldEndSession(true)
				.build();
	}

}
