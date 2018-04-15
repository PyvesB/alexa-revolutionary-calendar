package io.github.pyvesb.alexarevolutionarycalendar.date;

import static ca.rmen.lfrc.FrenchRevolutionaryCalendar.CalculationMethod.EQUINOX;
import static java.util.Calendar.DECEMBER;
import static java.util.Calendar.SEPTEMBER;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ca.rmen.lfrc.FrenchRevolutionaryCalendar;
import ca.rmen.lfrc.FrenchRevolutionaryCalendarDate;

/**
 * Class used to provide FrenchRevolutionaryCalendarDate instances.
 * 
 * @author Pyves
 *
 */
public class RevolutionaryDateProvider {

	private static final GregorianCalendar CALENDAR_START = new GregorianCalendar(1792, SEPTEMBER, 22);
	// There's actually no calendar end, but the FrenchRevolutionaryCalendar library does not support dates after 3000.
	private static final GregorianCalendar CALENDAR_END = new GregorianCalendar(3000, DECEMBER, 31);
	private static final Pattern ISO8601_CALENDAR_PATTERN = Pattern
			.compile("^(?<year>[1-9][0-9]*)-(?<month>0[1-9]|1[0-2])-(?<day>0[1-9]|[12][0-9]|3[01])$");

	private final Clock clock;

	public RevolutionaryDateProvider(Clock clock) {
		this.clock = clock;
	}

	/**
	 * Parses an ISO8601 string and return a FrenchRevolutionaryCalendarDate. Only supports precise calendar dates (e.g.
	 * "2018-03-29").
	 * 
	 * @param date the string to parse.
	 * @param locale the locale to be used to compute the FrenchRevolutionaryCalendarDate instance.
	 * @return a FrenchRevolutionaryCalendarDate instance wrapped in an optional or an empty optional if the date could
	 *         not be parsed.
	 */
	public Optional<FrenchRevolutionaryCalendarDate> parseISO8601CalendarDate(String date, Locale locale) {
		if (date != null) {
			Matcher matcher = ISO8601_CALENDAR_PATTERN.matcher(date);
			if (matcher.matches()) {
				int year = Integer.parseInt(matcher.group("year"));
				int month = Integer.parseInt(matcher.group("month")) - 1; // Month is 0 based.
				int day = Integer.parseInt(matcher.group("day"));
				GregorianCalendar gregorianCalendar = new GregorianCalendar(year, month, day);
				if (gregorianCalendar.compareTo(CALENDAR_START) >= 0 && gregorianCalendar.compareTo(CALENDAR_END) <= 0) {
					FrenchRevolutionaryCalendar revolutionaryCalendar = new FrenchRevolutionaryCalendar(locale, EQUINOX);
					return Optional.of(revolutionaryCalendar.getDate(gregorianCalendar));
				}
			}
		}
		return Optional.empty();
	}

	/**
	 * Creates a FrenchRevolutionaryCalendarDate instance based on the current date.
	 * 
	 * @param locale the locale to be used to compute the FrenchRevolutionaryCalendarDate instance.
	 * @return the resulting a FrenchRevolutionaryCalendarDate instance.
	 */
	public FrenchRevolutionaryCalendarDate provideCurrentDate(Locale locale) {
		FrenchRevolutionaryCalendar revolutionaryCalendar = new FrenchRevolutionaryCalendar(locale, EQUINOX);
		return revolutionaryCalendar.getDate(GregorianCalendar.from(ZonedDateTime.now(clock)));
	}

}
