package io.github.pyvesb.alexarevolutionarycalendar.handlers;

import static com.amazon.ask.request.Predicates.requestType;

import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.LaunchRequest;
import com.amazon.ask.model.RequestEnvelope;
import com.amazon.ask.model.Response;

public class LaunchRequestHandler implements RequestHandler {

	private static final Logger LOGGER = LogManager.getLogger(LaunchRequestHandler.class);

	@Override
	public boolean canHandle(HandlerInput input) {
		return input.matches(requestType(LaunchRequest.class));
	}

	@Override
	public Optional<Response> handle(HandlerInput input) {
		RequestEnvelope envelope = input.getRequestEnvelope();
		String locale = envelope.getRequest().getLocale();
		LOGGER.info("Launch request (session={}, locale={})", envelope.getSession().getSessionId(), locale);
		ResourceBundle messages = ResourceBundle.getBundle("messages", Locale.forLanguageTag(locale));
		String speechText = messages.getString("launch");
		return input.getResponseBuilder()
				.withSpeech(speechText)
				.withSimpleCard(messages.getString("card-title"), messages.getString("card-history"))
				.withReprompt(speechText)
				.build();
	}

}
