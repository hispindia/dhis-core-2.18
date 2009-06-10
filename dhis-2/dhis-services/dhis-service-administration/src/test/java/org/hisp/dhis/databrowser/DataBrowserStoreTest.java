package org.hisp.dhis.databrowser;

/*
 * Copyright (c) 2004-2008, University of Oslo
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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNotSame;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.DhisTest;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryComboService;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionComboService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Joakim Bjornstad
 * @version $Id$
 */
public class DataBrowserStoreTest
    extends DhisTest
{
    private DataBrowserStore dataBrowserStore;

    private final String T = "true";

    private final String F = "false";

    private DataElementCategoryCombo categoryCombo;

    private DataElementCategoryOptionCombo categoryOptionCombo;

    private Collection<Integer> dataElementIds;

    private Collection<Integer> periodIds;

    private Collection<Integer> organisationUnitIds;

    private DataSet dataSetA;
    private DataSet dataSetB;

    private DataElement dataElementA;
    private DataElement dataElementB;

    private Period periodA;
    private Period periodB;
    private Period periodC;
    private Period periodD;

    private OrganisationUnit unitA;
    private OrganisationUnit unitB;
    private OrganisationUnit unitC;
    private OrganisationUnit unitD;
    private OrganisationUnit unitE;
    private OrganisationUnit unitF;
    private OrganisationUnit unitG;
    private OrganisationUnit unitH;
    private OrganisationUnit unitI;

    @Override
    public void setUpTest()
        throws Exception
    {
        dataBrowserStore = (DataBrowserStore) getBean( DataBrowserStore.ID );

        categoryOptionComboService = (DataElementCategoryOptionComboService) getBean( DataElementCategoryOptionComboService.ID );

        categoryComboService = (DataElementCategoryComboService) getBean( DataElementCategoryComboService.ID );

        dataSetService = (DataSetService) getBean( DataSetService.ID );

        dataElementService = (DataElementService) getBean( DataElementService.ID );

        indicatorService = (IndicatorService) getBean( IndicatorService.ID );

        periodService = (PeriodService) getBean( PeriodService.ID );

        organisationUnitService = (OrganisationUnitService) getBean( OrganisationUnitService.ID );

        dataValueService = (DataValueService) getBean( DataValueService.ID );

        categoryCombo = categoryComboService
            .getDataElementCategoryComboByName( DataElementCategoryCombo.DEFAULT_CATEGORY_COMBO_NAME );

        categoryOptionCombo = categoryOptionComboService.getDefaultDataElementCategoryOptionCombo();

        // ---------------------------------------------------------------------
        // Setup identifier Collections
        // ---------------------------------------------------------------------

        dataElementIds = new HashSet<Integer>();
        periodIds = new HashSet<Integer>();
        organisationUnitIds = new HashSet<Integer>();

        // ---------------------------------------------------------------------
        // Setup DataElements
        // ---------------------------------------------------------------------

        dataElementA = createDataElement( 'A', DataElement.TYPE_INT, DataElement.AGGREGATION_OPERATOR_SUM, categoryCombo );
        dataElementB = createDataElement( 'B', DataElement.TYPE_BOOL, DataElement.AGGREGATION_OPERATOR_SUM, categoryCombo );

        dataElementIds.add( dataElementService.addDataElement( dataElementA ) );
        dataElementIds.add( dataElementService.addDataElement( dataElementB ) );

        Collection<DataElement> dataElementsA = new HashSet<DataElement>();
        dataElementsA.add( dataElementA );
        Collection<DataElement> dataElementsB = new HashSet<DataElement>();
        dataElementsB.add( dataElementB );

        // ---------------------------------------------------------------------
        // Setup Periods
        // ---------------------------------------------------------------------

        Iterator<PeriodType> periodTypeIt = periodService.getAllPeriodTypes().iterator();
        PeriodType periodTypeA = periodTypeIt.next();
        PeriodType periodTypeB = periodTypeIt.next();

        Date mar01 = getDate( 2005, 3, 1 );
        Date mar31 = getDate( 2005, 3, 31 );
        Date apr01 = getDate( 2005, 4, 1 );
        Date apr30 = getDate( 2005, 4, 30 );
        Date may01 = getDate( 2005, 5, 1 );
        Date may31 = getDate( 2005, 5, 31 );

        periodA = createPeriod( periodTypeA, mar01, mar31 );
        periodB = createPeriod( periodTypeA, apr01, apr30 );
        periodC = createPeriod( periodTypeB, may01, may31 );
        periodD = createPeriod( periodTypeB, mar01, may31 );

        periodIds.add( periodService.addPeriod( periodA ) );
        periodIds.add( periodService.addPeriod( periodB ) );
        periodIds.add( periodService.addPeriod( periodC ) );
        periodIds.add( periodService.addPeriod( periodD ) );

        // ---------------------------------------------------------------------
        // Setup DataSets
        // ---------------------------------------------------------------------

        dataSetA = createDataSet( 'A', periodTypeA );
        dataSetB = createDataSet( 'B', periodTypeB );

        dataSetA.setDataElements( dataElementsA );
        dataSetB.setDataElements( dataElementsB );

        dataSetService.addDataSet( dataSetA );
        dataSetService.addDataSet( dataSetB );

        // ---------------------------------------------------------------------
        // Setup OrganisationUnits
        // ---------------------------------------------------------------------

        unitA = createOrganisationUnit( 'A' );
        unitB = createOrganisationUnit( 'B', unitA );
        unitC = createOrganisationUnit( 'C', unitA );
        unitD = createOrganisationUnit( 'D', unitB );
        unitE = createOrganisationUnit( 'E', unitB );
        unitF = createOrganisationUnit( 'F', unitB );
        unitG = createOrganisationUnit( 'G', unitF );
        unitH = createOrganisationUnit( 'H', unitF );
        unitI = createOrganisationUnit( 'I' );

        organisationUnitIds.add( organisationUnitService.addOrganisationUnit( unitA ) );
        organisationUnitIds.add( organisationUnitService.addOrganisationUnit( unitB ) );
        organisationUnitIds.add( organisationUnitService.addOrganisationUnit( unitC ) );
        organisationUnitIds.add( organisationUnitService.addOrganisationUnit( unitD ) );
        organisationUnitIds.add( organisationUnitService.addOrganisationUnit( unitE ) );
        organisationUnitIds.add( organisationUnitService.addOrganisationUnit( unitF ) );
        organisationUnitIds.add( organisationUnitService.addOrganisationUnit( unitG ) );
        organisationUnitIds.add( organisationUnitService.addOrganisationUnit( unitH ) );
        organisationUnitIds.add( organisationUnitService.addOrganisationUnit( unitI ) );

        organisationUnitService.addOrganisationUnitHierarchy( new Date() ); // TODO

        // ---------------------------------------------------------------------
        // Setup DataValues
        // ---------------------------------------------------------------------

        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitC, "90", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitD, "10", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitE, "35", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitF, "25", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitG, "20", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitH, "60", categoryOptionCombo ) );

        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitC, "70", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitD, "40", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitE, "65", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitF, "55", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitG, "20", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitH, "15", categoryOptionCombo ) );

        dataValueService.addDataValue( createDataValue( dataElementA, periodC, unitC, "95", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodC, unitD, "40", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodC, unitE, "45", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodC, unitF, "30", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodC, unitG, "50", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodC, unitH, "70", categoryOptionCombo ) );

        dataValueService.addDataValue( createDataValue( dataElementB, periodA, unitC, T, categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodA, unitD, T, categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodA, unitE, F, categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodA, unitF, T, categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodA, unitG, F, categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodA, unitH, T, categoryOptionCombo ) );

        dataValueService.addDataValue( createDataValue( dataElementB, periodB, unitC, T, categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodB, unitD, F, categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodB, unitE, T, categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodB, unitF, T, categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodB, unitG, F, categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodB, unitH, T, categoryOptionCombo ) );

        dataValueService.addDataValue( createDataValue( dataElementB, periodC, unitC, F, categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodC, unitD, T, categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodC, unitE, F, categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodC, unitF, T, categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodC, unitG, T, categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodC, unitH, T, categoryOptionCombo ) );
    }

    @Ignore
    @Test
    public void testGetDataSetsInPeriod()
    {
        List<Integer> betweenPeriodIds = new ArrayList<Integer>();
        betweenPeriodIds.add( periodA.getId() );
        betweenPeriodIds.add( periodB.getId() );
        betweenPeriodIds.add( periodC.getId() );
        betweenPeriodIds.add( periodD.getId() );
        DataBrowserTable table = dataBrowserStore.getDataSetsInPeriod( betweenPeriodIds );

        assertNotNull( "DataBrowserTable not supposed to be null", table );
        assertEquals( "No. of queries", 1, table.getQueryCount() );
        assertNotSame( "Querytime more than 0", 0, table.getQueryTime() );

        assertEquals( "Metacolumns", 2, table.getColumns().size() );
        assertEquals( "DataSet", table.getColumns().get( 0 ).getName() );
        assertEquals( "Count", table.getColumns().get( 1 ).getName() );

        assertEquals( "Metarows", 2, table.getRows().size() );
        assertEquals( dataSetA.getName(), table.getRows().get( 0 ).getName() );
        assertEquals( dataSetA.getId(), table.getRows().get( 0 ).getId().intValue() );
        assertEquals( dataSetB.getName(), table.getRows().get( 1 ).getName() );
        assertEquals( dataSetB.getId(), table.getRows().get( 1 ).getId().intValue() );

        assertEquals( "Row count entries", 2, table.getCounts().size() );
        assertEquals( "DataValues in dataSetA", 18, table.getRowBasedOnRowName( dataSetA.getName() ).get( 0 )
            .intValue() );
        assertEquals( "DataValues in dataSetB", 18, table.getRowBasedOnRowName( dataSetB.getName() ).get( 0 )
            .intValue() );
    }

    @Ignore
    @Test
    public void testSetDataElementStructureForDataSetBetweenPeriods()
    {
        List<Integer> pList = new ArrayList<Integer>();
        pList.add( periodA.getId() );
        pList.add( periodB.getId() );
        pList.add( periodC.getId() );
        DataBrowserTable table = new DataBrowserTable();
        dataBrowserStore.setDataElementStructureForDataSetBetweenPeriods( table, dataSetA.getId(), pList );

        assertNotNull( "DataBrowserTable not supposed to be null", table );
        assertEquals( "No. of queries", 1, table.getQueryCount() );
        assertNotSame( "Querytime more than 0", 0, table.getQueryTime() );

        assertEquals( "Metarows", 1, table.getRows().size() );
        assertEquals( dataElementA.getName(), table.getRows().get( 0 ).getName() );
        assertEquals( dataElementA.getId(), table.getRows().get( 0 ).getId().intValue() );
    }

    @Ignore
    @Test
    public void testSetCountDataElementsInOnePeriod()
    {
        List<Integer> pList = new ArrayList<Integer>();
        pList.add( periodA.getId() );
        DataBrowserTable table = new DataBrowserTable();
        dataBrowserStore.setDataElementStructureForDataSetBetweenPeriods( table, dataSetA.getId(), pList );
        int results = dataBrowserStore.setCountDataElementsInOnePeriod( table, dataSetA.getId(), periodB.getId() );
        assertEquals( "DataValue results", 1, results );

        assertNotNull( "DataBrowserTable not supposed to be null", table );
        assertEquals( "No. of queries", 1 + 1, table.getQueryCount() );
        assertNotSame( "Querytime more than 0", 0, table.getQueryTime() );

        assertEquals( "Metarows", 1, table.getRows().size() );
        assertEquals( dataElementA.getName(), table.getRows().get( 0 ).getName() );
        assertEquals( dataElementA.getId(), table.getRows().get( 0 ).getId().intValue() );

        assertEquals( "Row count entries", 1, table.getCounts().size() );
        assertEquals( "DataValues in dataElementA for periodA", 6, table.getRowBasedOnRowName( dataElementA.getName() )
            .get( 0 ).intValue() );
    }

    @Ignore
    @Test
    public void testSetStructureForOrgUnitBetweenPeriods()
    {
        List<Integer> pList = new ArrayList<Integer>();
        pList.add( periodA.getId() );
        pList.add( periodB.getId() );
        pList.add( periodC.getId() );
        pList.add( periodD.getId() );
        DataBrowserTable table = new DataBrowserTable();
        dataBrowserStore.setStructureForOrgUnitBetweenPeriods( table, unitB.getId(), pList );

        assertNotNull( "DataBrowserTable not supposed to be null", table );
        assertEquals( "No. of queries", 1, table.getQueryCount() );
        assertNotSame( "Querytime more than 0", 0, table.getQueryTime() );

        assertEquals( "Metarows", 3, table.getRows().size() );
        assertEquals( unitD.getName(), table.getRows().get( 0 ).getName() );
        assertEquals( unitD.getId(), table.getRows().get( 0 ).getId().intValue() );
        assertEquals( unitE.getName(), table.getRows().get( 1 ).getName() );
        assertEquals( unitE.getId(), table.getRows().get( 1 ).getId().intValue() );
        assertEquals( unitF.getName(), table.getRows().get( 2 ).getName() );
        assertEquals( unitF.getId(), table.getRows().get( 2 ).getId().intValue() );

    }

    @Ignore
    @Test
    public void testSetCountOrgUnitsInOnePeriod()
    {
        List<Integer> pList = new ArrayList<Integer>();
        pList.add( periodA.getId() );
        DataBrowserTable table = new DataBrowserTable();
        dataBrowserStore.setStructureForOrgUnitBetweenPeriods( table, unitB.getId(), pList );
        int results = dataBrowserStore.setCountOrgUnitsInOnePeriod( table, unitB.getId(), periodA.getId() );
        assertEquals( "DataValue results", 3, results );

        assertNotNull( "DataBrowserTable not supposed to be null", table );
        assertEquals( "No. of queries", 2, table.getQueryCount() );
        assertNotSame( "Querytime more than 0", 0, table.getQueryTime() );

        assertEquals( "Metarows", 3, table.getRows().size() );
        assertEquals( unitD.getName(), table.getRows().get( 0 ).getName() );
        assertEquals( unitD.getId(), table.getRows().get( 0 ).getId().intValue() );
        assertEquals( unitF.getName(), table.getRows().get( 2 ).getName() );
        assertEquals( unitF.getId(), table.getRows().get( 2 ).getId().intValue() );

        assertEquals( "Row count entries", 3, table.getCounts().size() );
        assertEquals( "DataValues in unitD for periodA", 2, table.getRowBasedOnRowName( unitD.getName() ).get( 0 )
            .intValue() );
        assertEquals( "DataValues in unitF for periodA", 2, table.getRowBasedOnRowName( unitF.getName() ).get( 0 )
            .intValue() );
    }
}
