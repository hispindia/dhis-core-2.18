package org.hisp.dhis.system.util;

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

import static org.hisp.dhis.system.util.DateUtils.dateIsValid;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.joda.time.DateTime;
import org.junit.Test;

/**
 * @author Lars Helge Overland
 */
public class DateUtilsTest
{
    @Test
    public void testDateIsValid()
    {
        assertTrue( dateIsValid( "2000-01-01" ) );
        assertTrue( dateIsValid( "1067-04-28" ) );
        assertFalse( dateIsValid( "07-07-2000" ) );
        assertFalse( dateIsValid( "2000-03-40" ) );
        assertFalse( dateIsValid( "20d20-03-01" ) );
        assertTrue( dateIsValid( "0000-12-32" ) );
        assertTrue( dateIsValid( "2014-01-01" ) );
        assertFalse( dateIsValid( "2014-12-33" ) );
        assertFalse( dateIsValid( "2014-13-32" ) );
        assertFalse( dateIsValid( "2014-ab-cd" ) );
        assertFalse( dateIsValid( "201-01-01" ) );
        assertFalse( dateIsValid( "01-01-01" ) );
        assertFalse( dateIsValid( "abcd-01-01" ) );
    }
    
    @Test
    public void testDaysBetween()
    {
        assertEquals( 6, DateUtils.daysBetween( new DateTime( 2014, 3, 1, 0, 0 ).toDate(), new DateTime( 2014, 3, 7, 0, 0 ).toDate() ) );
    }
    
    @Test
    public void testMax()
    {
        Date date1 = new DateTime( 2014, 5, 15, 3, 3 ).toDate();
        Date date2 = new DateTime( 2014, 5, 18, 1, 1 ).toDate();
        Date date3 = null;
        Date date4 = null;
        
        assertEquals( date2, DateUtils.max( date1, date2 ) );
        assertEquals( date2, DateUtils.max( date2, date1 ) );
        assertEquals( date1, DateUtils.max( date1, date3 ) );
        assertEquals( date1, DateUtils.max( date3, date1 ) );
        
        assertNull( DateUtils.max( date3, date4 ) );
    }
    
    @Test
    public void testMaxArray()
    {
        Date date1 = new DateTime( 2014, 5, 15, 3, 3 ).toDate();
        Date date2 = new DateTime( 2014, 5, 18, 1, 1 ).toDate();
        Date date3 = new DateTime( 2014, 6, 10, 1, 1 ).toDate();
        Date date4 = null;
        Date date5 = null;
        Date date6 = null;
        
        assertEquals( date2, DateUtils.max( date1, date2, date4 ) );
        assertEquals( date2, DateUtils.max( date2, date1, date4 ) );
        assertEquals( date3, DateUtils.max( date1, date2, date3 ) );
        assertEquals( date3, DateUtils.max( date1, date2, date3 ) );
        assertEquals( date3, DateUtils.max( date3, date4, date5 ) );
        assertEquals( date4, DateUtils.max( date4, date5, date6 ) );
        assertEquals( date1, DateUtils.max( date1, date5, date4 ) );
        
        assertNull( DateUtils.max( date4, date5, date6 ) );
    }
    
    @Test
    public void testGetPrettyInterval()
    {
        Date start = new DateTime( 2014, 5, 18, 15, 10, 5, 12 ).toDate();
        Date end = new DateTime( 2014, 5, 19, 11, 45, 42, 56 ).toDate();
        
        String interval = DateUtils.getPrettyInterval( start, end );
        
        assertNotNull( interval );
    }
}
