package org.hisp.dhis.period;

/*
 * Copyright (c) 2004-2010, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.hisp.dhis.common.IdentifiableObject;

/**
 * @author Kristian Nordal
 * @version $Id: Period.java 5277 2008-05-27 15:48:42Z larshelg $
 */
public class Period
    extends IdentifiableObject
{
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

    private static final String SEPARATOR = "_";
    
    /**
     * Required.
     */
    private PeriodType periodType;

    /**
     * Required. Must be unique together with endDate.
     */
    private Date startDate;

    /**
     * Required. Must be unique together with startDate.
     */
    private Date endDate;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public Period()
    {
    }

    public Period( Period period )
    {
        this.id = period.getId();
        this.periodType = period.getPeriodType();
        this.startDate = period.getStartDate();
        this.endDate = period.getEndDate();
        this.name = period.getName();
    }
    
    protected Period( PeriodType periodType, Date startDate, Date endDate )
    {
        this.periodType = periodType;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    
    public Period( String externalId )
    {
        final String[] id = externalId.split( SEPARATOR );
        
        this.periodType = PeriodType.getPeriodTypeByName( id[0] );
        this.startDate = getMediumDate( id[1] );
        this.endDate = getMediumDate( id[2] );
    }
    
    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    /**
     * Returns an ISO8601 formatted string version of the period
     *
     * @return the period string
     */
    public String getIsoDate() 
    {
        return periodType.getIsoDate(this);
    }
    
    /**
     * Copies the transient properties (name) from the argument Period
     * to this Period.
     * 
     * @param the Period to copy from.
     * @return this Period. 
     */
    public Period copyTransientProperties( Period other )
    {
        this.name = other.getName();
        
        return this;
    }

    /**
     * Generates an String which uniquely identifies this Period based on its
     * core properties.
     * 
     * @return an identifier String.
     */
    public String getExternalId()
    {
        return periodType.getName() + SEPARATOR + getMediumDateString( startDate ) + SEPARATOR + getMediumDateString( endDate );
    }

    /**
     * Returns the frequency order of the period type of the period.
     * 
     * @return the frequency order.
     */
    public int frequencyOrder()
    {
        return periodType != null ? periodType.getFrequencyOrder() : TwoYearlyPeriodType.FREQUENCY_ORDER;
    }

    /**
     * Formats a Date to the format YYYY-MM-DD.
     * 
     * @param date the Date to parse.
     * @return A formatted date string. Null if argument is null.
     */
    private String getMediumDateString( Date date )
    {
        final SimpleDateFormat format = new SimpleDateFormat();

        format.applyPattern( DEFAULT_DATE_FORMAT );

        return date != null ? format.format( date ) : null;
    }

    /**
     * Parses a date from a String on the format YYYY-MM-DD.
     * 
     * @param dateString the String to parse.
     * @return a Date based on the given String.
     */
    private Date getMediumDate( String dateString )
    {
        try
        {
            final SimpleDateFormat format = new SimpleDateFormat();
    
            format.applyPattern( DEFAULT_DATE_FORMAT );
    
            return dateString != null ? format.parse( dateString ) : null;
        }
        catch ( ParseException ex )
        {
            throw new RuntimeException( "Failed to parse medium date", ex );
        }
    }
    
    // -------------------------------------------------------------------------
    // hashCode, equals and toString
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        int prime = 31;
        int result = 1;

        result = result * prime + startDate.hashCode();
        result = result * prime + endDate.hashCode();
        result = result * prime + periodType.hashCode();
        
        return result;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( o == null )
        {
            return false;
        }

        if ( !(o instanceof Period) )
        {
            return false;
        }

        final Period other = (Period) o;

        return startDate.equals( other.getStartDate() ) && 
            endDate.equals( other.getEndDate() ) && 
            periodType.equals( other.getPeriodType() );
    }

    @Override
    public String toString()
    {
        return "[" + periodType.getName() + ": " + startDate + " - " + endDate + "]";
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public void setEndDate( Date endDate )
    {
        this.endDate = endDate;
    }

    public Date getEndDate()
    {
        return endDate;
    }

    public PeriodType getPeriodType()
    {
        return periodType;
    }

    public void setPeriodType( PeriodType periodType )
    {
        this.periodType = periodType;
    }

    public Date getStartDate()
    {
        return startDate;
    }

    public void setStartDate( Date startDate )
    {
        this.startDate = startDate;
    }   
    
}
