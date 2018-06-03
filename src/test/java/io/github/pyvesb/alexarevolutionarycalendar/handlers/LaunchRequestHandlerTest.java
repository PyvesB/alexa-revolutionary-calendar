package io.github.pyvesb.alexarevolutionarycalendar.handlers;

import static java.util.Locale.UK;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static utils.InputBuilder.buildLaunchInput;
import static utils.ResponseAssertions.assertSimpleCard;
import static utils.ResponseAssertions.assertSpeech;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.amazon.ask.model.Response;

import utils.InputBuilder;
import utils.UnexpectedEmptyOptional;

class LaunchRequestHandlerTest {

	private final LaunchRequestHandler underTest = new LaunchRequestHandler();

	@Test
	void shouldHandleLaunchRequests() {
		assertTrue(underTest.canHandle(buildLaunchInput(UK)));
	}

	@Test
	void shouldNotHandleOtherRequests() {
		assertFalse(underTest.canHandle(InputBuilder.buildIntentInput("RevolutionaryDateOfTheDay", UK)));
	}

	@Test
	@Tag("en_GB-locale")
	void shouldReturnLaunchResponse() {
		Response response = underTest.handle(buildLaunchInput(UK)).orElseThrow(UnexpectedEmptyOptional::new);

		assertFalse(response.getShouldEndSession());
		assertSpeech(response, "Welcome! I can convert any date using the French Revolutionary Calendar. Say \"help\" to "
				+ "get the instructions.");
		assertSimpleCard(response, "Revolutionary Calendar", "Calendar conceived by Gilbert Romme and Claude Joseph Ferry. "
				+ "Officially used from 1793 to 1805, as well as in 1871 during the Paris Commune.");
	}

}
