package io.github.pyvesb.alexarevolutionarycalendar.handlers;

import static com.amazon.ask.request.Predicates.intentName;

import java.time.Clock;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Intent;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.RequestEnvelope;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.Slot;
import com.amazon.ask.model.ui.Image;
import com.amazon.ask.response.ResponseBuilder;

import ca.rmen.lfrc.FrenchRevolutionaryCalendarDate;
import io.github.pyvesb.alexarevolutionarycalendar.date.FrenchObjectOfTheDay;
import io.github.pyvesb.alexarevolutionarycalendar.date.RevolutionaryDateProvider;

public class DateIntentHandler implements RequestHandler {

	private static final Logger LOGGER = LogManager.getLogger(DateIntentHandler.class);
	private static final String IMAGE_URL = "https://s3-eu-west-1.amazonaws.com/alexa-revolutionary-calendar/calendar.jpg";
	private static final Image IMAGE = Image.builder().withLargeImageUrl(IMAGE_URL).build();
	private static final String DATE_OF_THE_DAY = "RevolutionaryDateOfTheDay";
	private static final String DATE_WITH_SLOT = "RevolutionaryDateWithSlot";

	private final RevolutionaryDateProvider revolutionaryDateProvider;

	public DateIntentHandler(Clock clock) {
		revolutionaryDateProvider = new RevolutionaryDateProvider(clock);
	}

	@Override
	public boolean canHandle(HandlerInput input) {
		return input.matches(intentName(DATE_OF_THE_DAY)) || input.matches(intentName(DATE_WITH_SLOT));
	}

	@Override
	public Optional<Response> handle(HandlerInput input) {
		RequestEnvelope envelope = input.getRequestEnvelope();
		Locale locale = Locale.forLanguageTag(envelope.getRequest().getLocale());
		Intent intent = ((IntentRequest) envelope.getRequest()).getIntent();
		String intentName = intent.getName();
		LOGGER.info("Date intent (session={}, type={}, locale={})", envelope.getSession().getSessionId(), intentName,
				locale);
		ResourceBundle messages = ResourceBundle.getBundle("messages", locale);
		if (DATE_WITH_SLOT.equals(intentName)) {
			return handleIntent(input.getResponseBuilder(), intent, messages);
		}
		return getDateResponse(input.getResponseBuilder(), "date-of-the-day",
				revolutionaryDateProvider.provideCurrentDate(locale), messages);
	}

	/**
	 * Handles an intent which may contain a date slot.
	 * 
	 * @param responseBuilder used to build the response.
	 * @param intent the request intent
	 * @param messages the resource bundle to be used to construct the response.
	 * @return either a date response if the handling was successful or else an error response.
	 */
	private Optional<Response> handleIntent(ResponseBuilder responseBuilder, Intent intent, ResourceBundle messages) {
		Slot dateSlot = intent.getSlots().get("date");
		String dateValue = null;
		if (dateSlot != null) {
			dateValue = dateSlot.getValue();
			Optional<FrenchRevolutionaryCalendarDate> parsedDate = revolutionaryDateProvider
					.parseISO8601CalendarDate(dateValue, messages.getLocale());
			if (parsedDate.isPresent()) {
				LOGGER.info("Parsed date (date={}, locale={})", dateValue, messages.getLocale());
				return getDateResponse(responseBuilder, "date-with-slot", parsedDate.get(), messages);
			}
		}
		LOGGER.warn("Unparsable date (date={}, locale={})", dateValue, messages.getLocale());
		return getErrorResponse(responseBuilder, messages);
	}

	/**
	 * Creates a response containing a simple card and an error message.
	 * 
	 * @param responseBuilder used to build the response.
	 * @param messages the resource bundle to be used to construct the response.
	 * @return the resulting response.
	 */
	private Optional<Response> getErrorResponse(ResponseBuilder responseBuilder, ResourceBundle messages) {
		String errorText = messages.getString("error");
		return responseBuilder
				.withSpeech(errorText)
				.withSimpleCard(messages.getString("card-title"), messages.getString("card-examples"))
				.withReprompt(errorText)
				.build();
	}

	/**
	 * Creates a response containing a standard card and date information.
	 * 
	 * @param responseBuilder used to build the response
	 * @param responseKey the key to get a localised message containing date placeholders.
	 * @param date the revolutionary date to include in the response.
	 * @param messages the resource bundle to be used to construct the response.
	 * @return the resulting response.
	 */
	private Optional<Response> getDateResponse(ResponseBuilder responseBuilder, String responseKey,
			FrenchRevolutionaryCalendarDate date, ResourceBundle messages) {
		boolean isFrench = Locale.FRENCH.getLanguage().equals(messages.getLocale().getLanguage());
		String speech = String.format(messages.getString(responseKey), date.getWeekdayName(),
				getDayOfMonthOrdinal(date, isFrench), date.getMonthName(), date.year, date.getObjectTypeName(),
				getReadableObjectOfTheDay(date, isFrench));
		String cardText = date.getWeekdayName() + ", " + date.dayOfMonth + " " + date.getMonthName() + " " + date.year + "\n"
				+ date.getObjectOfTheDay();
		return responseBuilder
				.withSpeech(speech)
				.withStandardCard(messages.getString("card-title"), cardText, IMAGE)
				.withShouldEndSession(true)
				.build();
	}

	/**
	 * Creates an ordinal string representing the day of month for a given date. The returned string is not necessarily
	 * linguistically correct but should be the minimal form needed to be correctly interpreted by Alexa.
	 * 
	 * @param date the date to get the day of month from.
	 * @param isFrench true if the language to use is French.
	 * @return a string representing a number that can be interpreted as an ordinal by Alexa.
	 */
	private String getDayOfMonthOrdinal(FrenchRevolutionaryCalendarDate date, boolean isFrench) {
		if (isFrench) {
			return date.dayOfMonth == 1 ? "premier" : Integer.toString(date.dayOfMonth);
		}
		// Alexa is clever enough to correctly process ordinals such as "2th".
		return date.dayOfMonth + "th";
	}

	/**
	 * Creates a string representing the object of the day for a given date. The returned string contains an article so
	 * that it can naturally be read out by Alexa.
	 * 
	 * @param date the date to get the object of the day from.
	 * @param french true if the language to use is French.
	 * @return a string containing the object of that day and an article, e.g. "the Apple".
	 */
	private String getReadableObjectOfTheDay(FrenchRevolutionaryCalendarDate date, boolean french) {
		return french ? FrenchObjectOfTheDay.values()[date.getDayInYear() - 1].getArticle() + date.getObjectOfTheDay()
				: "the " + date.getObjectOfTheDay();
	}

}
