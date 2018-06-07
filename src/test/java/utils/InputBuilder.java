package utils;

import static java.util.Locale.UK;

import java.util.Collections;
import java.util.Locale;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.Intent;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.LaunchRequest;
import com.amazon.ask.model.Request;
import com.amazon.ask.model.RequestEnvelope;
import com.amazon.ask.model.Session;
import com.amazon.ask.model.SessionEndedRequest;
import com.amazon.ask.model.Slot;

public class InputBuilder {

	private static final String SESSION_ID = "amzn1.echo-api.session.f7c13f4e-7b37-422d-89ba-45bfb1b00eed";

	public static HandlerInput buildLaunchInput() {
		return buildLaunchInput(UK);
	}

	public static HandlerInput buildLaunchInput(Locale locale) {
		LaunchRequest launchRequest = LaunchRequest.builder().withLocale(locale.toLanguageTag()).build();
		return buildInput(launchRequest);
	}

	public static HandlerInput buildEndedInput() {
		return buildInput(SessionEndedRequest.builder().build());
	}

	public static HandlerInput buildIntentInput(String intentName) {
		return buildIntentInput(intentName, UK);
	}

	public static HandlerInput buildIntentInput(String intentName, Locale locale) {
		Intent intent = Intent.builder().withName(intentName).build();
		return buildIntentInput(intent, locale);
	}

	public static HandlerInput buildIntentInput(String intentName, Locale locale, Slot slot) {
		Intent intent = Intent.builder().withName(intentName).withSlots(Collections.singletonMap("date", slot)).build();
		return buildIntentInput(intent, locale);
	}

	private static HandlerInput buildIntentInput(Intent intent, Locale locale) {
		IntentRequest intentRequest = IntentRequest.builder().withIntent(intent).withLocale(locale.toLanguageTag()).build();
		return buildInput(intentRequest);
	}

	private static HandlerInput buildInput(Request speechletRequest) {
		Session session = Session.builder().withSessionId(SESSION_ID).build();
		RequestEnvelope envelope = RequestEnvelope.builder().withRequest(speechletRequest).withSession(session).build();
		return HandlerInput.builder().withRequestEnvelope(envelope).build();
	}

	private InputBuilder() {
		// Not used.
	}

}
