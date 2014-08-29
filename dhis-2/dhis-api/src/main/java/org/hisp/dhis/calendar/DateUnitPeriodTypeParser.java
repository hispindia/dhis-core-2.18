package org.hisp.dhis.calendar;

/*
 * Copyright (c) 2004-2014, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import com.google.common.collect.Maps;
import org.hisp.dhis.calendar.impl.Iso8601Calendar;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class DateUnitPeriodTypeParser implements PeriodTypeParser
{
    private final Map<String, Pattern> compileCache = Maps.newHashMap();

    private static CalendarService calendarService;

    public static void setCalendarService( CalendarService calendarService )
    {
        DateUnitPeriodTypeParser.calendarService = calendarService;
    }

    public static CalendarService getCalendarService()
    {
        return calendarService;
    }

    public static org.hisp.dhis.calendar.Calendar getCalendar()
    {
        if ( calendarService != null )
        {
            return calendarService.getSystemCalendar();
        }

        return Iso8601Calendar.getInstance();
    }

    @Override
    public DateInterval parse( String period )
    {
        DateUnitType type = DateUnitType.find( period );

        if ( type == null )
        {
            return null;
        }

        if ( compileCache.get( type.getType() ) == null )
        {
            try
            {
                Pattern pattern = Pattern.compile( type.getFormat() );
                compileCache.put( type.getType(), pattern );
            }
            catch ( PatternSyntaxException ex )
            {
                return null;
            }
        }

        Pattern pattern = compileCache.get( type.getType() );
        Matcher matcher = pattern.matcher( period );
        boolean match = matcher.find();

        if ( !match )
        {
            return null;
        }

        Calendar calendar = getCalendar();

        if ( DateUnitType.DAILY.equals( type ) )
        {
            int year = Integer.parseInt( matcher.group( 1 ) );
            int month = Integer.parseInt( matcher.group( 2 ) );
            int day = Integer.parseInt( matcher.group( 3 ) );

            DateTimeUnit dateTimeUnit = new DateTimeUnit( year, month, day );
            dateTimeUnit.setDayOfWeek( calendar.weekday( dateTimeUnit ) );

            return new DateInterval( dateTimeUnit, dateTimeUnit );
        }
        else if ( DateUnitType.WEEKLY.equals( type ) )
        {
            int year = Integer.parseInt( matcher.group( 1 ) );
            int week = Integer.parseInt( matcher.group( 2 ) );

            if ( week < 1 || week > calendar.weeksInYear( year ) )
            {
                return null;
            }

            DateTimeUnit start = new DateTimeUnit( year, 1, 1 );
            start = calendar.minusDays( start, calendar.weekday( start ) - 1 ); // rewind to start of week

            // since we rewind to start of week, we might end up in the previous years weeks, so we check and forward if needed
            if ( calendar.isoWeek( start ) == calendar.weeksInYear( year ) )
            {
                start = calendar.plusWeeks( start, 1 );
            }

            start = calendar.plusWeeks( start, week - 1 );
            DateTimeUnit end = new DateTimeUnit( start );
            end = calendar.plusWeeks( end, 1 );
            end = calendar.minusDays( end, 1 );

            start.setDayOfWeek( calendar.weekday( start ) );
            end.setDayOfWeek( calendar.weekday( end ) );

            return new DateInterval( start, end );
        }
        else if ( DateUnitType.MONTHLY.equals( type ) )
        {
            int year = Integer.parseInt( matcher.group( 1 ) );
            int month = Integer.parseInt( matcher.group( 2 ) );

            DateTimeUnit start = new DateTimeUnit( year, month, 1 );
            DateTimeUnit end = new DateTimeUnit( year, month, calendar.daysInMonth( start.getYear(), start.getMonth() ) );

            start.setDayOfWeek( calendar.weekday( start ) );
            end.setDayOfWeek( calendar.weekday( end ) );

            return new DateInterval( start, end );
        }
        else if ( DateUnitType.BI_MONTHLY.equals( type ) )
        {
            int year = Integer.parseInt( matcher.group( 1 ) );
            int month = Integer.parseInt( matcher.group( 2 ) );

            if ( month < 1 || month > 6 )
            {
                return null;
            }

            DateTimeUnit start = new DateTimeUnit( year, (month * 2) - 1, 1 );
            DateTimeUnit end = new DateTimeUnit( start );
            end = calendar.plusMonths( end, 2 );
            end = calendar.minusDays( end, 1 );

            start.setDayOfWeek( calendar.weekday( start ) );
            end.setDayOfWeek( calendar.weekday( end ) );

            return new DateInterval( start, end );
        }
        else if ( DateUnitType.QUARTERLY.equals( type ) )
        {
            int year = Integer.parseInt( matcher.group( 1 ) );
            int quarter = Integer.parseInt( matcher.group( 2 ) );

            // valid quarters are from 1 - 4
            if ( quarter < 1 || quarter > 4 )
            {
                return null;
            }

            DateTimeUnit start = new DateTimeUnit( year, ((quarter - 1) * 3) + 1, 1 );
            DateTimeUnit end = new DateTimeUnit( start );
            end = calendar.plusMonths( end, 3 );
            end = calendar.minusDays( end, 1 );

            start.setDayOfWeek( calendar.weekday( start ) );
            end.setDayOfWeek( calendar.weekday( end ) );

            return new DateInterval( start, end );
        }
        else if ( DateUnitType.SIX_MONTHLY.equals( type ) )
        {
            int year = Integer.parseInt( matcher.group( 1 ) );
            int semester = Integer.parseInt( matcher.group( 2 ) );

            // valid six-monthly are from 1 - 2
            if ( semester < 1 || semester > 2 )
            {
                return null;
            }

            DateTimeUnit start = new DateTimeUnit( year, semester == 1 ? 1 : 7, 1 );
            DateTimeUnit end = new DateTimeUnit( start );
            end = calendar.plusMonths( end, 6 );
            end = calendar.minusDays( end, 1 );

            start.setDayOfWeek( calendar.weekday( start ) );
            end.setDayOfWeek( calendar.weekday( end ) );

            return new DateInterval( start, end );
        }
        else if ( DateUnitType.SIX_MONTHLY_APRIL.equals( type ) )
        {
            int year = Integer.parseInt( matcher.group( 1 ) );
            int semester = Integer.parseInt( matcher.group( 2 ) );

            // valid six-monthly are from 1 - 2
            if ( semester < 1 || semester > 2 )
            {
                return null;
            }

            DateTimeUnit start = new DateTimeUnit( year, semester == 1 ? 4 : 10, 1 );
            DateTimeUnit end = new DateTimeUnit( start );
            end = calendar.plusMonths( end, 6 );
            end = calendar.minusDays( end, 1 );

            start.setDayOfWeek( calendar.weekday( start ) );
            end.setDayOfWeek( calendar.weekday( end ) );

            return new DateInterval( start, end );
        }
        else if ( DateUnitType.YEARLY.equals( type ) )
        {
            int year = Integer.parseInt( matcher.group( 1 ) );

            DateTimeUnit start = new DateTimeUnit( year, 1, 1 );
            DateTimeUnit end = new DateTimeUnit( year, calendar.monthsInYear(),
                calendar.daysInMonth( start.getYear(), calendar.monthsInYear() ) );

            start.setDayOfWeek( calendar.weekday( start ) );
            end.setDayOfWeek( calendar.weekday( end ) );

            return new DateInterval( start, end );
        }
        else if ( DateUnitType.FINANCIAL_APRIL.equals( type ) )
        {
            int year = Integer.parseInt( matcher.group( 1 ) );

            DateTimeUnit start = new DateTimeUnit( year, 4, 1 );
            DateTimeUnit end = new DateTimeUnit( start );
            end = calendar.plusYears( end, 1 );
            end = calendar.minusDays( end, 1 );

            start.setDayOfWeek( calendar.weekday( start ) );
            end.setDayOfWeek( calendar.weekday( end ) );

            return new DateInterval( start, end );
        }
        else if ( DateUnitType.FINANCIAL_JULY.equals( type ) )
        {
            int year = Integer.parseInt( matcher.group( 1 ) );

            DateTimeUnit start = new DateTimeUnit( year, 7, 1 );
            DateTimeUnit end = new DateTimeUnit( start );
            end = calendar.plusYears( end, 1 );
            end = calendar.minusDays( end, 1 );

            start.setDayOfWeek( calendar.weekday( start ) );
            end.setDayOfWeek( calendar.weekday( end ) );

            return new DateInterval( start, end );
        }
        else if ( DateUnitType.FINANCIAL_OCTOBER.equals( type ) )
        {
            int year = Integer.parseInt( matcher.group( 1 ) );

            DateTimeUnit start = new DateTimeUnit( year, 10, 1 );
            DateTimeUnit end = new DateTimeUnit( start );
            end = calendar.plusYears( end, 1 );
            end = calendar.minusDays( end, 1 );

            start.setDayOfWeek( calendar.weekday( start ) );
            end.setDayOfWeek( calendar.weekday( end ) );

            return new DateInterval( start, end );
        }

        return null;
    }
}
