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

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.source.Source;

/**
 * @author Kristian Nordal
 * @version $Id: PeriodService.java 5983 2008-10-17 17:42:44Z larshelg $
 */
public interface PeriodService
{
    String ID = PeriodService.class.getName();

    // -------------------------------------------------------------------------
    // Period
    // -------------------------------------------------------------------------

    /**
     * Adds a Period.
     * 
     * @param period the Period to add.
     * @return a generated unique id of the added Period.
     */
    int addPeriod( Period period );

    /**
     * Deletes a Period.
     * 
     * @param period the Period to delete.
     */
    void deletePeriod( Period period );

    /**
     * Returns a Period.
     * 
     * @param id the id of the Period to return.
     * @return the Period with the given id, or null if no match.
     */
    Period getPeriod( int id );

    /**
     * Returns a Period.
     * 
     * @param startDate the start date of the Period.
     * @param endDate the end date of the Period.
     * @param periodType the PeriodType of the Period
     * @return the Period matching the dates and periodtype, or null if no match.
     */
    Period getPeriod( Date startDate, Date endDate, PeriodType periodType );

    /**
     * Returns a Period.
     * 
     * @param startDate the start date of the Period.
     * @param endDate the end date of the Period.
     * @param periodType the PeriodType of the Period
     * @return the Period matching the dates and periodtype, or null if no match.
     */
    Period getPeriodFromDates( Date startDate, Date endDate, PeriodType periodType );
    
    /**
     * Returns all persisted Periods.
     * 
     * @return all persisted Periods.
     */
    Collection<Period> getAllPeriods();

    /**
     * Returns all Periods with corresponding identifiers.
     * 
     * @param identifiers a collection of identifiers.
     * @return a collection of Periods.
     */
    Collection<Period> getPeriods( Collection<Integer> identifiers );
    
    /**
     * Generates all Periods with the corresponding external identifiers in the
     * given Collection. The Periods which don't exist are created and persisted.
     * 
     * @param externalIds the external identifiers.
     * @return a Collection of Periods.
     */
    Collection<Period> getPeriodsByExternalIds( Collection<String> externalIds );
    
    /**
     * Returns all Periods with start date after or equal the specified start
     * date and end date before or equal the specified end date.
     * 
     * @param startDate the ultimate start date.
     * @param endDate the ultimate end date.
     * @return a collection of all Periods with start date after or equal the
     *         specified start date and end date before or equal the specified
     *         end date, or an empty collection if no Periods match.
     */
    Collection<Period> getPeriodsBetweenDates( Date startDate, Date endDate );

    /**
     * Returns all Periods of the specified PeriodType with start date after or 
     * equal the specified start date and end date before or equal the specified 
     * end date.
     * 
     * @param periodType the PeriodType.
     * @param startDate the ultimate start date.
     * @param endDate the ultimate end date.
     * @return a collection of all Periods with start date after or equal the
     *         specified start date and end date before or equal the specified
     *         end date, or an empty collection if no Periods match.
     */
    Collection<Period> getPeriodsBetweenDates( PeriodType periodType, Date startDate, Date endDate );

    Collection<Period> getPeriodsBetweenOrSpanningDates( Date startDate, Date endDate );
    
    /**
     * Returns all Intersecting Periods between the startDate and endDate based on PeriodType
     * For example if the startDate is 2007-05-01 and endDate is 2007-08-01 and periodType is Quartely
     * then it retuns the periods for Q2,Q3
     *  
     * @param periodType is the ultimate period type
     * @param startDate is intercepting startDate
     * @param endDate is intercepting endDate
     * @return
     */
    Collection<Period> getIntersectingPeriodsByPeriodType( PeriodType periodType, Date startDate, Date endDate );

    /**
     * Returns Periods where at least one its days are between the given start date and end date.
     * 
     * @param startDate the start date.
     * @param endDate the end date.
     * @return Periods where at least one its days are between the given start date and end date.
     */    
    Collection<Period> getIntersectingPeriods( Date startDate, Date endDate );
    
    /**
     * Returns all Periods from the given collection of Periods which span the border of either the
     * start date OR end date of the given Period. 
     * 
     * @param period the base Period.
     * @param periods the collection of Periods.
     * @return all Periods from the given collection of Periods which span the border of either the
     *         start date or end date of the given Period.
     */
    Collection<Period> getBoundaryPeriods( Period period, Collection<Period> periods );

    /**
     * Returns all Periods from the given collection of Periods which are completely within the
     * span of the of the given Period.
     * 
     * @param period the base Period.
     * @param periods the collection of Periods.
     * @return all Periods from the given collection of Periods which are completely within the
     *         span of the of the given Period.
     */
    Collection<Period> getInclusivePeriods( Period period, Collection<Period> periods );    
    
    /**
     * Returns all Periods with a given PeriodType.
     * 
     * @param periodType the PeriodType of the Periods to return.
     * @return all Periods with the given PeriodType, or an empty collection if
     *         no Periods match.
     */
    Collection<Period> getPeriodsByPeriodType( PeriodType periodType );

    /**
     * Returns all intersecting Periods for the given Period which have assosiated DataValues for
     * the given collection of DataElements and Sources.
     * 
     * @param period the Period.
     * @param dataElements the collection of DataElements.
     * @param sources the collection of Sources.
     * @return all intersecting Periods for the given Period which have assosiated DataValues for
     *         the given collection of DataElements and Sources.
     */
    Collection<Period> getPeriods( Period period, Collection<DataElement> dataElements, Collection<? extends Source> sources );
    
    /**
     * Enforces that each Period in the given collection is loaded in the current
     * session. Persists the Period if it does not exist.
     * 
     * @param periods the list of Periods.
     * @return the list of Periods.
     */
    List<Period> reloadPeriods( List<Period> periods );
    
    /**
     * Returns historyLength number of Periods chronologically ending with lastPeriod.
     * 
     * @param lastPeriod the last Period in the provided collection.
     * @param historyLength the number of Periods in the provided collection.
     * @return a collection of Periods.
     */
    List<Period> getPeriods( Period lastPeriod, int historyLength );
    
    // -------------------------------------------------------------------------
    // PeriodType
    // -------------------------------------------------------------------------

    /**
     * Returns a PeriodType.
     * 
     * @param id the id of the PeriodType to return.
     * @return the PeriodType with the given id, or null if no match.
     */
    PeriodType getPeriodType( int id );

    /**
     * Returns all PeriodTypes.
     * 
     * @return a list of all PeriodTypes, or an empty list if there are no
     *         PeriodTypes. The PeriodTypes have a natural order.
     */
    List<PeriodType> getAllPeriodTypes();

    /**
     * Returns a PeriodType with a given name.
     * 
     * @param name the name of the PeriodType to return.
     * @return the PeriodType with the given name, or null if no match.
     */
    PeriodType getPeriodTypeByName( String name );
    
    /**
     * Returns a PeriodType represented by the given Class.
     * 
     * @param periodType the Class type of the PeriodType.
     * @return a PeriodType instance.
     */
    PeriodType getPeriodTypeByClass( Class<? extends PeriodType> periodType );
}
