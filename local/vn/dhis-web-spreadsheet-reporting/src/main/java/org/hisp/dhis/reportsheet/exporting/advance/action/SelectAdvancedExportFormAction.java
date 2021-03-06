/*
 * Copyright (c) 2004-2011, University of Oslo
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
package org.hisp.dhis.reportsheet.exporting.advance.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.reportsheet.ExportReportService;
import org.hisp.dhis.reportsheet.period.db.PeriodDatabaseService;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * @version $Id$
 */
public class SelectAdvancedExportFormAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ExportReportService exportReportService;

    public void setExportReportService( ExportReportService exportReportService )
    {
        this.exportReportService = exportReportService;
    }

    private PeriodDatabaseService periodDatabaseService;

    public void setPeriodDatabaseService( PeriodDatabaseService periodDatabaseService )
    {
        this.periodDatabaseService = periodDatabaseService;
    }

    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private List<OrganisationUnitGroup> organisationUnitGroups;

    private List<Period> periods;

    private List<String> exportReportGroups;

    // -------------------------------------------------------------------------
    // Getters && Setters
    // -------------------------------------------------------------------------

    public List<OrganisationUnitGroup> getOrganisationUnitGroups()
    {
        return organisationUnitGroups;
    }

    public List<Period> getPeriods()
    {
        return periods;
    }

    public List<String> getExportReportGroups()
    {
        return exportReportGroups;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        // Report groups list
        exportReportGroups = new ArrayList<String>( exportReportService.getExportReportGroups() );

        // Periods list
        periodDatabaseService.setSelectedPeriodTypeName( MonthlyPeriodType.NAME );

        periods = periodDatabaseService.getPeriodList();

        // Organisation groups list
        organisationUnitGroups = new ArrayList<OrganisationUnitGroup>( organisationUnitGroupService
            .getAllOrganisationUnitGroups() );

        Collections.sort( organisationUnitGroups, new IdentifiableObjectNameComparator() );

        return SUCCESS;
    }

}
