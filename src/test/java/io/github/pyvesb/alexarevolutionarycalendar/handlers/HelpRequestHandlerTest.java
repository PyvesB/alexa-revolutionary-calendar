package io.github.pyvesb.alexarevolutionarycalendar.handlers;

import static java.util.Locale.UK;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static utils.InputBuilder.buildIntentInput;
import static utils.InputBuilder.buildLaunchInput;
import static utils.ResponseAssertions.assertSimpleCard;
import static utils.ResponseAssertions.assertSpeech;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.amazon.ask.model.Response;

import utils.UnexpectedEmptyOptional;

class HelpRequestHandlerTest {

	private final HelpIntentHandler underTest = new HelpIntentHandler();

	@Test
	void shouldHandleIntentRequestsWithHelpIntentName() {
		assertTrue(underTest.canHandle(buildIntentInput("AMAZON.HelpIntent", UK)));
	}

	@Test
	void shouldNotHandleIntentRequestsWithDifferentName() {
		assertFalse(underTest.canHandle(buildIntentInput("RevolutionaryDateOfTheDay", UK)));
	}

	@Test
	void shouldNotHandleOtherRequests() {
		assertFalse(underTest.canHandle(buildLaunchInput(UK)));
	}

	@Test
	@Tag("en_GB-locale")
	void shouldReturnHelpResponse() {
		Response response = underTest.handle(buildIntentInput("AMAZON.HelpIntent", UK))
				.orElseThrow(UnexpectedEmptyOptional::new);

		assertFalse(response.getShouldEndSession());
		assertSpeech(response, "Ask for today's date or a specific date to get its revolutionary equivalent! For instance: "
				+ "\"convert 2018-03-05\".");
		assertSimpleCard(response, "Revolutionary Calendar",
				"Examples:\n\"what's today's date\"\n\"convert the 5th of March 2018\"");
	}

}
