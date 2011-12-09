package org.hisp.dhis.api.utils;

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

import org.hisp.dhis.attribute.Attribute;
import org.hisp.dhis.chart.Chart;
import org.hisp.dhis.dataelement.*;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorGroupSet;
import org.hisp.dhis.indicator.IndicatorType;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.user.User;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public interface ObjectPersister
{
    public void persistAttribute( Attribute attribute );

    public void persistDataElement( DataElement dataElement );

    public void persistDataElementGroup( DataElementGroup dataElementGroup );

    public void persistDataElementGroupSet( DataElementGroupSet dataElementGroupSet );

    public void persistCategory( DataElementCategory category );

    public void persistCategoryOption( DataElementCategoryOption categoryOption );

    public void persistCategoryCombo( DataElementCategoryCombo categoryCombo );

    public void persistCategoryOptionCombo( DataElementCategoryOptionCombo categoryOptionCombo );

    public void persistIndicator( Indicator indicator );

    public void persistIndicatorType( IndicatorType indicatorType );

    public void persistIndicatorGroup( IndicatorGroup indicatorGroup );

    public void persistIndicatorGroupSet( IndicatorGroupSet indicatorGroupSet );

    public void persistOrganisationUnit( OrganisationUnit organisationUnit );

    public void persistOrganisationUnitLevel( OrganisationUnitLevel organisationUnitLevel );

    public void persistOrganisationUnitGroup( OrganisationUnitGroup organisationUnitGroup );

    public void persistOrganisationUnitGroupSet( OrganisationUnitGroupSet organisationUnitGroupSet );

    public void persistDataSet( DataSet dataSet );

    public void persistChart( Chart chart );

    public void persistUser( User user );
}
