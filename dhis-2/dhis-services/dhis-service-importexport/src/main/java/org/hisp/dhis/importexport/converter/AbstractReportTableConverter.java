package org.hisp.dhis.importexport.converter;

/*
 * Copyright (c) 2004-2007, University of Oslo
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

import org.hisp.dhis.reporttable.ReportTable;
import org.hisp.dhis.reporttable.ReportTableService;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class AbstractReportTableConverter
    extends AbstractConverter<ReportTable>
{
    protected ReportTableService reportTableService;

    // -------------------------------------------------------------------------
    // Overridden methods
    // -------------------------------------------------------------------------

    protected void importUnique( ReportTable object )
    {
        batchHandler.addObject( object );       
    }

    protected void importMatching( ReportTable object, ReportTable match )
    {
        match.setName( object.getName() );
        match.setTableName( object.getTableName() );
        match.setMode( object.getMode() );
        match.setRegression( object.isRegression() );
        
        match.setDoIndicators( match.isDoIndicators() );
        match.setDoPeriods( match.isDoPeriods() );
        match.setDoUnits( match.isDoUnits() );
        
        match.getRelatives().setReportingMonth( object.getRelatives().isReportingMonth() );
        match.getRelatives().setLast3Months( object.getRelatives().isLast3Months() );
        match.getRelatives().setLast6Months( object.getRelatives().isLast6Months() );
        match.getRelatives().setLast9Months( object.getRelatives().isLast9Months() );
        match.getRelatives().setLast12Months( object.getRelatives().isLast12Months() );
        match.getRelatives().setSoFarThisYear( object.getRelatives().isSoFarThisYear() );
        match.getRelatives().setSoFarThisFinancialYear( object.getRelatives().isSoFarThisFinancialYear() );
        match.getRelatives().setLast3To6Months( object.getRelatives().isLast3To6Months() );
        match.getRelatives().setLast6To9Months( object.getRelatives().isLast6To9Months() );
        match.getRelatives().setLast9To12Months( object.getRelatives().isLast9To12Months() );
        match.getRelatives().setLast12IndividualMonths( object.getRelatives().isLast12IndividualMonths() );
        match.getRelatives().setIndividualMonthsThisYear( object.getRelatives().isIndividualMonthsThisYear() );
        match.getRelatives().setIndividualQuartersThisYear( object.getRelatives().isIndividualQuartersThisYear() );
        
        match.getReportParams().setParamReportingMonth( object.getReportParams().isParamReportingMonth() );
        match.getReportParams().setParamParentOrganisationUnit( object.getReportParams().isParamParentOrganisationUnit() );
        match.getReportParams().setParamOrganisationUnit( object.getReportParams().isParamOrganisationUnit() );
        
        reportTableService.saveReportTable( match );
    }
    
    protected ReportTable getMatching( ReportTable object )
    {
        return reportTableService.getReportTableByName( object.getName() );
    }
    
    protected boolean isIdentical( ReportTable object, ReportTable existing )
    {
        if ( !object.getName().equals( existing.getName() ) )
        {
            return false;
        }
        if ( !object.getTableName().equals( existing.getTableName() ) )
        {
            return false;
        }
        if ( !isSimiliar( object.getMode(), existing.getMode() ) || ( isNotNull( object.getMode(), existing.getMode() ) && !object.getMode().equals( existing.getMode() ) ) )
        {
            return false;
        }
        if ( object.isRegression() != existing.isRegression() )
        {
            return false;
        }
        
        if ( object.isDoIndicators() != existing.isDoIndicators() )
        {
            return false;
        }
        if ( object.isDoPeriods() != existing.isDoPeriods() )
        {
            return false;
        }
        if ( object.isDoUnits() != existing.isDoUnits() )
        {
            return false;
        }
        
        if ( object.getRelatives().isReportingMonth() != existing.getRelatives().isReportingMonth() )
        {
            return false;
        }
        if ( object.getRelatives().isLast3Months() != existing.getRelatives().isLast3Months() )
        {
            return false;
        }
        if ( object.getRelatives().isLast6Months() != existing.getRelatives().isLast6Months() )
        {
            return false;
        }
        if ( object.getRelatives().isLast9Months() != existing.getRelatives().isLast9Months() )
        {
            return false;
        }
        if ( object.getRelatives().isLast12Months() != existing.getRelatives().isLast12Months() )
        {
            return false;
        }
        if ( object.getRelatives().isSoFarThisYear() != existing.getRelatives().isSoFarThisYear() )
        {
            return false;
        }
        if ( object.getRelatives().isSoFarThisFinancialYear() != existing.getRelatives().isSoFarThisFinancialYear() )
        {
            return false;
        }
        if ( object.getRelatives().isLast3To6Months() != existing.getRelatives().isLast3To6Months() )
        {
            return false;
        }
        if ( object.getRelatives().isLast6To9Months() != existing.getRelatives().isLast6To9Months() )
        {
            return false;
        }
        if ( object.getRelatives().isLast9To12Months() != existing.getRelatives().isLast9To12Months() )
        {
            return false;
        }
        if ( object.getRelatives().isLast12IndividualMonths() != existing.getRelatives().isLast12IndividualMonths() )
        {
            return false;
        }
        if ( object.getRelatives().isIndividualMonthsThisYear() != existing.getRelatives().isIndividualMonthsThisYear() )
        {
            return false;
        }
        if ( object.getRelatives().isIndividualQuartersThisYear() != existing.getRelatives().isIndividualQuartersThisYear() )
        {
            return false;
        }
        
        if ( object.getReportParams().isParamReportingMonth() != existing.getReportParams().isParamReportingMonth() )
        {
            return false;
        }
        if ( object.getReportParams().isParamParentOrganisationUnit() != existing.getReportParams().isParamParentOrganisationUnit() )
        {
            return false;
        }
        if ( object.getReportParams().isParamOrganisationUnit() != existing.getReportParams().isParamOrganisationUnit() )
        {
            return false;
        }
        
        return true;
    }
}
