package io.github.pyvesb.alexarevolutionarycalendar.speech;

import static java.util.Locale.CANADA;
import static java.util.Locale.CANADA_FRENCH;
import static java.util.Locale.FRENCH;
import static java.util.Locale.UK;
import static java.util.Locale.US;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletRequest;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.SimpleCard;
import com.amazon.speech.ui.StandardCard;

class RevolutionarySpeechletTest {

	private static final String REQUEST_ID = "amzn1.echo-api.request.3d4b1a8a-0082-49ca-b5c2-69e750a653ee";
	private static final String SESSION_ID = "amzn1.echo-api.session.f7c13f4e-7b37-422d-89ba-45bfb1b00eed";

	private RevolutionarySpeechlet underTest;

	@BeforeEach
	void setUp() {
		underTest = new RevolutionarySpeechlet(Clock.fixed(Instant.ofEpochMilli(1522426160067L), ZoneId.of("UTC")));
	}

	@Test
	@Tag("en_GB-locale")
	@SuppressWarnings("unchecked")
	void shouldReturnLaunchResponse() {
		LaunchRequest launchRequest = LaunchRequest.builder().withRequestId(REQUEST_ID).withLocale(UK).build();
		SpeechletResponse response = underTest
				.onLaunch((SpeechletRequestEnvelope<LaunchRequest>) buildEnvelope(launchRequest));

		assertFalse(response.getNullableShouldEndSession());
		assertEquals("Welcome! I can convert any date using the French Revolutionary Calendar. Say \"help\" to "
				+ "get the instructions.", ((PlainTextOutputSpeech) response.getOutputSpeech()).getText());

		SimpleCard card = (SimpleCard) response.getCard();
		assertEquals("Revolutionary Calendar", card.getTitle());
		assertEquals("Calendar conceived by Gilbert Romme. Officially used from 1793 to 1805.", card.getContent());
	}

	@Test
	@Tag("en_GB-locale")
	void shouldReturnDateOfTheDayResponse() {
		SpeechletResponse response = underTest.onIntent(buildIntentEnvelope("RevolutionaryDateOfTheDay", null, UK));

		assertTrue(response.getNullableShouldEndSession());
		assertEquals("Today is Décadi the 10th of Germinal 226. The tool of the day is the Hatchery.",
				((PlainTextOutputSpeech) response.getOutputSpeech()).getText());

		StandardCard card = (StandardCard) response.getCard();
		assertEquals("Revolutionary Calendar", card.getTitle());
		assertEquals("Décadi, 10 Germinal 226\nHatchery", card.getText());
		assertEquals("https://s3-eu-west-1.amazonaws.com/alexa-revolutionary-calendar/Germinal.png",
				card.getImage().getLargeImageUrl());
	}

	@Test
	@Tag("fr_FR-locale")
	void shouldReturnDateOfTheDayResponseWithOtherLocale() {
		SpeechletResponse response = underTest.onIntent(buildIntentEnvelope("RevolutionaryDateOfTheDay", null, FRENCH));

		assertTrue(response.getNullableShouldEndSession());
		assertEquals("Nous sommes Décadi, le 10 Germinal 226. L'outil du jour est le Couvoir.",
				((PlainTextOutputSpeech) response.getOutputSpeech()).getText());

		StandardCard card = (StandardCard) response.getCard();
		assertEquals("Calendrier Révolutionnaire", card.getTitle());
		assertEquals("Décadi, 10 Germinal 226\nCouvoir", card.getText());
		assertEquals("https://s3-eu-west-1.amazonaws.com/alexa-revolutionary-calendar/Germinal.png",
				card.getImage().getLargeImageUrl());
	}

	@Test
	@Tag("en_GB-locale")
	void shouldReturnDateResponseIfDateProvidedViaSlot() {
		Slot slot = Slot.builder().withName("date").withValue("2018-03-29").build();
		SpeechletResponse response = underTest.onIntent(buildIntentEnvelope("RevolutionaryDateWithSlot", slot, UK));

		assertTrue(response.getNullableShouldEndSession());
		assertEquals("The revolutionary date is Nonidi the 9th of Germinal 226. The plant of the day is the Alder.",
				((PlainTextOutputSpeech) response.getOutputSpeech()).getText());

		StandardCard card = (StandardCard) response.getCard();
		assertEquals("Revolutionary Calendar", card.getTitle());
		assertEquals("Nonidi, 9 Germinal 226\nAlder", card.getText());
		assertEquals("https://s3-eu-west-1.amazonaws.com/alexa-revolutionary-calendar/Germinal.png",
				card.getImage().getLargeImageUrl());
	}

	@Test
	@Tag("fr_CA-locale")
	void shouldReturnDateResponseIfDateProvidedViaSlotWithDifferentLocale() {
		Slot slot = Slot.builder().withName("date").withValue("2018-02-19").build();
		SpeechletResponse response = underTest
				.onIntent(buildIntentEnvelope("RevolutionaryDateWithSlot", slot, CANADA_FRENCH));

		assertTrue(response.getNullableShouldEndSession());
		assertEquals("Il s'agit de Primidi, le premier Ventôse 226. La plante de ce jour est le Tussilage.",
				((PlainTextOutputSpeech) response.getOutputSpeech()).getText());

		StandardCard card = (StandardCard) response.getCard();
		assertEquals("Calendrier Révolutionnaire", card.getTitle());
		assertEquals("Primidi, 1 Ventôse 226\nTussilage", card.getText());
		assertEquals("https://s3-eu-west-1.amazonaws.com/alexa-revolutionary-calendar/Ventôse.png",
				card.getImage().getLargeImageUrl());
	}

	@Test
	@Tag("en_US-locale")
	void shouldReturnErrorResponseIfSlotEmpty() {
		SpeechletResponse response = underTest.onIntent(buildIntentEnvelope("RevolutionaryDateWithSlot", null, US));

		assertFalse(response.getNullableShouldEndSession());
		assertEquals("Please try again by clearly stating a date after 1792-02-21. For example: \"convert 2018-03-05\".",
				((PlainTextOutputSpeech) response.getOutputSpeech()).getText());

		SimpleCard card = (SimpleCard) response.getCard();
		assertEquals("Revolutionary Calendar", card.getTitle());
		assertEquals("Examples:\n\"what's today's date\"\n\"convert March 5th 2018\"", card.getContent());
	}

	@Test
	@Tag("en_CA-locale")
	void shouldReturnErrorResponseIfDateUnparsable() {
		Slot slot = Slot.builder().withName("date").withValue("2018-W26").build();
		SpeechletResponse response = underTest.onIntent(buildIntentEnvelope("RevolutionaryDateWithSlot", slot, CANADA));

		assertFalse(response.getNullableShouldEndSession());
		assertEquals("Please try again by clearly stating a date after 1792-02-21. For example: \"convert 2018-03-05\".",
				((PlainTextOutputSpeech) response.getOutputSpeech()).getText());

		SimpleCard card = (SimpleCard) response.getCard();
		assertEquals("Revolutionary Calendar", card.getTitle());
		assertEquals("Examples:\n\"what's today's date\"\n\"convert March 5th 2018\"", card.getContent());
	}

	@Test
	@Tag("en_GB-locale")
	void shouldReturnHelpResponse() {
		SpeechletResponse response = underTest.onIntent(buildIntentEnvelope("AMAZON.HelpIntent", null, UK));

		assertFalse(response.getNullableShouldEndSession());
		assertEquals("Ask for today's date or a specific date to get its revolutionary equivalent! For instance: "
				+ "\"convert 2018-03-05\".", ((PlainTextOutputSpeech) response.getOutputSpeech()).getText());

		SimpleCard card = (SimpleCard) response.getCard();
		assertEquals("Revolutionary Calendar", card.getTitle());
		assertEquals("Examples:\n\"what's today's date\"\n\"convert the 5th of March 2018\"", card.getContent());
	}

	@ParameterizedTest
	@ValueSource(strings = { "AMAZON.CancelIntent", "AMAZON.StopIntent" })
	@Tag("en_GB-locale")
	void shouldReturnCancelOrStopResponse(String intentName) {
		SpeechletResponse response = underTest.onIntent(buildIntentEnvelope(intentName, null, UK));

		assertTrue(response.getNullableShouldEndSession());
		assertEquals("Okay. See you soon!", ((PlainTextOutputSpeech) response.getOutputSpeech()).getText());
	}

	@Test
	@Tag("en_GB-locale")
	void shouldReturnUnsupportedResponseIfIntentNameUnsupported() {
		SpeechletResponse response = underTest.onIntent(buildIntentEnvelope("UnsupportedIntent", null, UK));

		assertFalse(response.getNullableShouldEndSession());
		assertEquals("Sorry, I didn't quite get that. Try again or say \"help\" to get the instructions.",
				((PlainTextOutputSpeech) response.getOutputSpeech()).getText());

		SimpleCard card = (SimpleCard) response.getCard();
		assertEquals("Revolutionary Calendar", card.getTitle());
		assertEquals("Examples:\n\"what's today's date\"\n\"convert the 5th of March 2018\"", card.getContent());
	}

	@Test
	@Tag("en_GB-locale")
	@SuppressWarnings("unchecked")
	void shouldReturnUnsupportedResponseIfNoIntentInRequest() {
		IntentRequest intentRequest = IntentRequest.builder().withRequestId(REQUEST_ID).withLocale(UK).build();
		SpeechletResponse response = underTest
				.onIntent((SpeechletRequestEnvelope<IntentRequest>) buildEnvelope(intentRequest));

		assertFalse(response.getNullableShouldEndSession());
		assertEquals("Sorry, I didn't quite get that. Try again or say \"help\" to get the instructions.",
				((PlainTextOutputSpeech) response.getOutputSpeech()).getText());

		SimpleCard card = (SimpleCard) response.getCard();
		assertEquals("Revolutionary Calendar", card.getTitle());
		assertEquals("Examples:\n\"what's today's date\"\n\"convert the 5th of March 2018\"", card.getContent());
	}

	@SuppressWarnings("unchecked")
	private SpeechletRequestEnvelope<IntentRequest> buildIntentEnvelope(String intentName, Slot slot, Locale locale) {
		Intent.Builder builder = Intent.builder().withName(intentName);
		if (slot != null) {
			builder.withSlot(slot);
		}
		IntentRequest intentRequest = IntentRequest.builder().withRequestId(REQUEST_ID).withIntent(builder.build())
				.withLocale(locale).build();
		return (SpeechletRequestEnvelope<IntentRequest>) buildEnvelope(intentRequest);
	}

	private SpeechletRequestEnvelope<? extends SpeechletRequest> buildEnvelope(SpeechletRequest speechletRequest) {
		Session session = Session.builder().withSessionId(SESSION_ID).build();
		return SpeechletRequestEnvelope.builder().withRequest(speechletRequest).withSession(session).build();
	}

}
