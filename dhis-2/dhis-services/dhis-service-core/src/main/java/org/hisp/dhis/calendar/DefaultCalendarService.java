package org.hisp.dhis.calendar;

/*
 * Copyright (c) 2004-2015, University of Oslo
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

import static org.apache.commons.lang.StringUtils.isEmpty;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.hisp.dhis.calendar.impl.Iso8601Calendar;
import org.hisp.dhis.period.Cal;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.setting.SystemSettingManager;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class DefaultCalendarService 
    implements CalendarService
{
    @Autowired
    private SystemSettingManager settingManager;

    @Autowired
    private Set<Calendar> calendars = Sets.newHashSet();

    private Map<String, Calendar> calendarMap = Maps.newHashMap();

    private List<DateFormat> dateFormats = Lists.newArrayList(
        new DateFormat( "yyyy-MM-dd", "yyyy-MM-dd", "yyyy-MM-dd", "yyyy-mm-dd" ),
        new DateFormat( "dd-MM-yyyy", "dd-MM-yyyy", "dd-MM-yyyy", "dd-mm-yyyy" )
    );

    /**
     * Memory cache for calendar and date format keys.
     */
    private final Map<String, String> keyCache = new HashMap<>();

    // -------------------------------------------------------------------------
    // CalendarService implementation
    // -------------------------------------------------------------------------

    @PostConstruct
    public void init()
    {
        for ( Calendar calendar : calendars )
        {
            calendarMap.put( calendar.name(), calendar );
        }

        PeriodType.setCalendarService( this );
        Cal.setCalendarService( this );
        DateUnitPeriodTypeParser.setCalendarService( this );
    }

    @Override
    public List<Calendar> getAllCalendars()
    {
        List<Calendar> sortedCalendars = Lists.newArrayList( calendarMap.values() );
        Collections.sort( sortedCalendars, CalendarComparator.INSTANCE );
        return sortedCalendars;
    }

    @Override
    public List<DateFormat> getAllDateFormats()
    {
        return dateFormats;
    }

    @Override
    public Calendar getSystemCalendar()
    {
        String calendarKey = getSystemCalendarKey();
        String dateFormat = getSystemDateFormatKey();

        Calendar calendar;

        if ( calendarMap.containsKey( calendarKey ) )
        {
            calendar = calendarMap.get( calendarKey );
        }
        else
        {
            calendar = Iso8601Calendar.getInstance();
        }

        calendar.setDateFormat( dateFormat );

        return calendar;
    }
    
    @Override
    public DateFormat getSystemDateFormat()
    {
        String dateFormatKey = getSystemDateFormatKey();

        for ( DateFormat dateFormat : dateFormats )
        {
            if ( dateFormat.name().equals( dateFormatKey ) )
            {
                return dateFormat;
            }
        }

        return dateFormats.get( 0 );
    }

    // -------------------------------------------------------------------------
    // Calendar key
    // -------------------------------------------------------------------------

    @Override
    public String getSystemCalendarKey()
    {
        String key = keyCache.get( KEY_CALENDAR );        
        key = !isEmpty( key ) ? key : (String) settingManager.getSystemSetting( KEY_CALENDAR, DEFAULT_CALENDAR );        
        keyCache.put( KEY_CALENDAR, key );        
        return key;
    }
    
    @Override
    public void setSystemCalendarKey( String calendarKey )
    {
        keyCache.put( KEY_CALENDAR, calendarKey );
        settingManager.saveSystemSetting( KEY_CALENDAR, calendarKey );
    }
    
    @Override
    public String getSystemDateFormatKey()
    {
        String key = keyCache.get( KEY_DATE_FORMAT );
        key = !isEmpty( key ) ? key : (String) settingManager.getSystemSetting( KEY_DATE_FORMAT, DEFAULT_DATE_FORMAT );
        keyCache.put( KEY_DATE_FORMAT, key );
        return key;
    }

    @Override
    public void setSystemDateFormatKey( String dateFormatKey )
    {
        keyCache.put( KEY_DATE_FORMAT, dateFormatKey );
        settingManager.saveSystemSetting( KEY_DATE_FORMAT, dateFormatKey );
    }
}
