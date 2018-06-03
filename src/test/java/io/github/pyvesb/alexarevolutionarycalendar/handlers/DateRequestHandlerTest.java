package io.github.pyvesb.alexarevolutionarycalendar.handlers;

import static java.util.Locale.CANADA;
import static java.util.Locale.CANADA_FRENCH;
import static java.util.Locale.FRENCH;
import static java.util.Locale.UK;
import static java.util.Locale.US;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static utils.InputBuilder.buildIntentInput;
import static utils.InputBuilder.buildLaunchInput;
import static utils.ResponseAssertions.assertSimpleCard;
import static utils.ResponseAssertions.assertSpeech;
import static utils.ResponseAssertions.assertStandardCard;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.amazon.ask.model.Response;
import com.amazon.ask.model.Slot;

import utils.UnexpectedEmptyOptional;

class DateRequestHandlerTest {

	private static final Clock CLOCK = Clock.fixed(Instant.ofEpochMilli(1522426160067L), ZoneId.of("UTC"));

	private final DateIntentHandler underTest = new DateIntentHandler(CLOCK);

	@Test
	void shouldHandleIntentRequestsWithDateIntentName() {
		assertTrue(underTest.canHandle(buildIntentInput("RevolutionaryDateOfTheDay", UK)));
	}

	@Test
	void shouldNotHandleIntentRequestsWithDifferentName() {
		assertFalse(underTest.canHandle(buildIntentInput("AMAZON.HelpIntent", UK)));
	}

	@Test
	void shouldNotHandleOtherRequests() {
		assertFalse(underTest.canHandle(buildLaunchInput(FRENCH)));
	}

	@Test
	@Tag("en_GB-locale")
	void shouldReturnDateOfTheDayResponse() {
		Response response = underTest.handle(buildIntentInput("RevolutionaryDateOfTheDay", UK, null))
				.orElseThrow(UnexpectedEmptyOptional::new);

		assertTrue(response.getShouldEndSession());
		assertSpeech(response, "Today is Décadi the 10th of Germinal 226. The tool of the day is the Hatchery.");
		assertStandardCard(response, "Revolutionary Calendar", "Décadi, 10 Germinal 226\nHatchery");
	}

	@Test
	@Tag("fr_FR-locale")
	void shouldReturnDateOfTheDayResponseWithOtherLocale() {
		Response response = underTest.handle(buildIntentInput("RevolutionaryDateOfTheDay", FRENCH, null))
				.orElseThrow(UnexpectedEmptyOptional::new);

		assertTrue(response.getShouldEndSession());
		assertSpeech(response, "Nous sommes Décadi, le 10 Germinal 226. L'outil du jour est le Couvoir.");
		assertStandardCard(response, "Calendrier Révolutionnaire", "Décadi, 10 Germinal 226\nCouvoir");
	}

	@Test
	@Tag("en_GB-locale")
	void shouldReturnDateResponseIfDateProvidedViaSlot() {
		Slot slot = Slot.builder().withName("date").withValue("2018-03-29").build();
		Response response = underTest.handle(buildIntentInput("RevolutionaryDateWithSlot", UK, slot))
				.orElseThrow(UnexpectedEmptyOptional::new);

		assertTrue(response.getShouldEndSession());
		assertSpeech(response, "The revolutionary date is Nonidi the 9th of Germinal 226. The plant of the day is the "
				+ "Alder.");
		assertStandardCard(response, "Revolutionary Calendar", "Nonidi, 9 Germinal 226\nAlder");
	}

	@Test
	@Tag("fr_CA-locale")
	void shouldReturnDateResponseIfDateProvidedViaSlotWithDifferentLocale() {
		Slot slot = Slot.builder().withName("date").withValue("2018-02-19").build();
		Response response = underTest.handle(buildIntentInput("RevolutionaryDateWithSlot", CANADA_FRENCH, slot))
				.orElseThrow(UnexpectedEmptyOptional::new);

		assertTrue(response.getShouldEndSession());
		assertSpeech(response, "Il s'agit de Primidi, le premier Ventôse 226. La plante de ce jour est le Tussilage.");
		assertStandardCard(response, "Calendrier Révolutionnaire", "Primidi, 1 Ventôse 226\nTussilage");
	}

	@Test
	@Tag("en_US-locale")
	void shouldReturnErrorResponseIfSlotEmpty() {
		Response response = underTest.handle(buildIntentInput("RevolutionaryDateWithSlot", US, null))
				.orElseThrow(UnexpectedEmptyOptional::new);

		assertFalse(response.getShouldEndSession());
		assertSpeech(response, "Please try again by clearly stating a date after 1792-02-21. For example: "
				+ "\"convert 2018-03-05\".");
		assertSimpleCard(response, "Revolutionary Calendar",
				"Examples:\n\"what's today's date\"\n\"convert March 5th 2018\"");
	}

	@Test
	@Tag("en_CA-locale")
	void shouldReturnErrorResponseIfDateUnparsable() {
		Slot slot = Slot.builder().withName("date").withValue("2018-W26").build();
		Response response = underTest.handle(buildIntentInput("RevolutionaryDateWithSlot", CANADA, slot))
				.orElseThrow(UnexpectedEmptyOptional::new);

		assertFalse(response.getShouldEndSession());
		assertSpeech(response, "Please try again by clearly stating a date after 1792-02-21. For example: "
				+ "\"convert 2018-03-05\".");
		assertSimpleCard(response, "Revolutionary Calendar",
				"Examples:\n\"what's today's date\"\n\"convert March 5th 2018\"");
	}

}
