package org.hisp.dhis.dxf2.datavalueset;

/*
 * Copyright (c) 2011, University of Oslo
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

import static org.hisp.dhis.importexport.ImportStrategy.NEW;
import static org.hisp.dhis.importexport.ImportStrategy.NEW_AND_UPDATES;
import static org.hisp.dhis.importexport.ImportStrategy.UPDATES;
import static org.hisp.dhis.system.util.ConversionUtils.wrap;
import static org.hisp.dhis.system.util.DateUtils.getDefaultDate;
import static org.hisp.dhis.system.notification.NotificationCategory.DATAVALUE_IMPORT;
import static org.hisp.dhis.system.notification.NotificationLevel.INFO;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Map;

import org.amplecode.quick.BatchHandler;
import org.amplecode.quick.BatchHandlerFactory;
import org.amplecode.staxwax.factory.XMLFactory;
import org.hisp.dhis.common.IdentifiableObject.IdentifiableProperty;
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataset.CompleteDataSetRegistration;
import org.hisp.dhis.dataset.CompleteDataSetRegistrationService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.dxf2.importsummary.ImportConflict;
import org.hisp.dhis.dxf2.importsummary.ImportCount;
import org.hisp.dhis.dxf2.importsummary.ImportSummary;
import org.hisp.dhis.dxf2.metadata.ImportOptions;
import org.hisp.dhis.importexport.ImportStrategy;
import org.hisp.dhis.jdbc.batchhandler.DataValueBatchHandler;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.notification.Notifier;
import org.hisp.dhis.system.util.DateUtils;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Lars Helge Overland
 */
@Transactional
public class DefaultDataValueSetService
    implements DataValueSetService
{
    private static final String ERROR_INVALID_DATA_SET = "Invalid data set: ";
    private static final String ERROR_INVALID_PERIOD = "Invalid period: ";
    private static final String ERROR_INVALID_ORG_UNIT = "Invalid org unit: ";
    private static final String ERROR_OBJECT_NEEDED_TO_COMPLETE = "Must be provided to complete data set";
        
    @Autowired
    private IdentifiableObjectManager identifiableObjectManager;
    
    @Autowired
    private DataElementCategoryService categoryService;
    
    @Autowired
    private DataSetService dataSetService;
    
    @Autowired
    private OrganisationUnitService organisationUnitService;
    
    @Autowired
    private PeriodService periodService;
    
    @Autowired
    private BatchHandlerFactory batchHandlerFactory;
    
    @Autowired
    private CompleteDataSetRegistrationService registrationService;
    
    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private DataValueSetStore dataValueSetStore;
    
    @Autowired
    private Notifier notifier;
    
    //--------------------------------------------------------------------------
    // DataValueSet implementation
    //--------------------------------------------------------------------------

    public void writeDataValueSet( String dataSet, String period, String orgUnit, OutputStream out )
    {
        DataSet dataSet_ = dataSetService.getDataSet( dataSet );
        Period period_ = PeriodType.getPeriodFromIsoString( period );
        OrganisationUnit orgUnit_ = organisationUnitService.getOrganisationUnit( orgUnit );
        
        if ( dataSet_ == null )
        {
            throw new IllegalArgumentException( ERROR_INVALID_DATA_SET + dataSet );
        }
        
        if ( period_ == null )
        {
            throw new IllegalArgumentException( ERROR_INVALID_PERIOD + period );
        }
        
        if ( orgUnit_ == null )
        {
            throw new IllegalArgumentException( ERROR_INVALID_ORG_UNIT + orgUnit );
        }
        
        CompleteDataSetRegistration registration = registrationService.getCompleteDataSetRegistration( dataSet_, period_, orgUnit_ );
        
        Date completeDate = registration != null ? registration.getDate() : null;
        
        period_ = periodService.reloadPeriod( period_ );
        
        dataValueSetStore.writeDataValueSet( dataSet_, completeDate, orgUnit_, period_, dataSet_.getDataElements(), wrap( period_ ), wrap( orgUnit_ ), out );
    }
    
    public ImportSummary saveDataValueSet( InputStream in )
    {
        return saveDataValueSet( in, ImportOptions.getDefaultImportOptions() );
    }
    
    public ImportSummary saveDataValueSet( InputStream in, ImportOptions importOptions )
    {
        notifier.clear( DATAVALUE_IMPORT ).notify( DATAVALUE_IMPORT, "Process started" );
        
        ImportSummary summary = new ImportSummary();
        
        DataValueSet dataValueSet = new StreamingDataValueSet( XMLFactory.getXMLReader( in ) );
        
        IdentifiableProperty dataElementIdScheme = dataValueSet.getDataElementIdScheme() != null ? IdentifiableProperty.valueOf( dataValueSet.getDataElementIdScheme().toUpperCase() ) : importOptions.getDataElementIdScheme();
        IdentifiableProperty orgUnitIdScheme = dataValueSet.getOrgUnitIdScheme() != null ? IdentifiableProperty.valueOf( dataValueSet.getOrgUnitIdScheme().toUpperCase() ) : importOptions.getOrgUnitIdScheme();
        ImportStrategy strategy = importOptions.getImportStrategy();
        
        Map<String, DataElement> dataElementMap = identifiableObjectManager.getIdMap( DataElement.class, dataElementIdScheme );
        Map<String, OrganisationUnit> orgUnitMap = identifiableObjectManager.getIdMap( OrganisationUnit.class, orgUnitIdScheme );
        Map<String, DataElementCategoryOptionCombo> categoryOptionComboMap = identifiableObjectManager.getIdMap( DataElementCategoryOptionCombo.class, IdentifiableProperty.UID );
        
        DataSet dataSet = dataValueSet.getDataSet() != null ? identifiableObjectManager.getObject( DataSet.class, IdentifiableProperty.UID, dataValueSet.getDataSet() ) : null;
        Date completeDate = getDefaultDate( dataValueSet.getCompleteDate() );
        
        Period period = PeriodType.getPeriodFromIsoString( dataValueSet.getPeriod() );
        OrganisationUnit orgUnit = dataValueSet.getOrgUnit() != null ? identifiableObjectManager.getObject( OrganisationUnit.class, orgUnitIdScheme, dataValueSet.getOrgUnit() ) : null;
        
        if ( dataSet != null && completeDate != null )
        {
            handleComplete( dataSet, completeDate, orgUnit, period, summary );
        }
        else
        {
            summary.setDataSetComplete( Boolean.FALSE.toString() );
        }
        
        DataElementCategoryOptionCombo fallbackCategoryOptionCombo = categoryService.getDefaultDataElementCategoryOptionCombo();

        BatchHandler<DataValue> batchHandler = batchHandlerFactory.createBatchHandler( DataValueBatchHandler.class ).init();

        int importCount = 0;
        int updateCount = 0;
        int totalCount = 0;
        
        notifier.notify( DATAVALUE_IMPORT, "Importing data values" );
        
        while ( dataValueSet.hasNextDataValue() )
        {
            org.hisp.dhis.dxf2.datavalue.DataValue dataValue = dataValueSet.getNextDataValue();
            
            DataValue internalValue = new DataValue();

            totalCount++;
            
            DataElement dataElement = dataElementMap.get( dataValue.getDataElement() );
            DataElementCategoryOptionCombo categoryOptionCombo = categoryOptionComboMap.get( dataValue.getCategoryOptionCombo() );
            period = period != null ? period : PeriodType.getPeriodFromIsoString( dataValue.getPeriod() );
            orgUnit = orgUnit != null ? orgUnit : orgUnitMap.get( dataValue.getOrgUnit() );
            
            if ( dataElement == null )
            {
                summary.getConflicts().add( new ImportConflict( DataElement.class.getSimpleName(), dataValue.getDataElement() ) );
                continue;
            }

            if ( period == null )
            {
                summary.getConflicts().add( new ImportConflict( Period.class.getSimpleName(), dataValue.getPeriod() ) );
                continue;
            }
            
            if ( orgUnit == null )
            {
                summary.getConflicts().add( new ImportConflict( OrganisationUnit.class.getSimpleName(), dataValue.getOrgUnit() ) );
                continue;
            }

            if ( categoryOptionCombo == null )
            {
                categoryOptionCombo = fallbackCategoryOptionCombo;
            }
            
            if ( dataValue.getValue() == null && dataValue.getComment() == null )
            {
                continue;
            }
            
            internalValue.setDataElement( dataElement );
            internalValue.setPeriod( periodService.reloadPeriod( period ) );
            internalValue.setSource( orgUnit );
            internalValue.setOptionCombo( categoryOptionCombo );
            internalValue.setValue( dataValue.getValue() );
            internalValue.setStoredBy( dataValue.getStoredBy() );
            internalValue.setTimestamp( getDefaultDate( dataValue.getTimestamp() ) );
            internalValue.setComment( dataValue.getComment() );
            internalValue.setFollowup( dataValue.getFollowup() );
            
            if ( batchHandler.objectExists( internalValue ) )
            {
                if ( NEW_AND_UPDATES.equals( strategy ) || UPDATES.equals( strategy ) )
                {
                    if ( !importOptions.isDryRun() )
                    {
                        batchHandler.updateObject( internalValue );
                    }

                    updateCount++;
                }                
            }
            else
            {
                if ( NEW_AND_UPDATES.equals( strategy ) || NEW.equals( strategy ) )
                {
                    if ( !importOptions.isDryRun() )
                    {
                        batchHandler.addObject( internalValue );
                    }
                    
                    importCount++;
                }
            }
        }
        
        int ignores = totalCount - importCount - updateCount;
        
        summary.getCounts().add( new ImportCount( DataValue.class.getSimpleName(), importCount, updateCount, ignores ) );
        
        batchHandler.flush();
        
        notifier.notify( INFO, DATAVALUE_IMPORT, "Import done", true ).addTaskSummary( DATAVALUE_IMPORT, summary );
        
        return summary;
    }

    //--------------------------------------------------------------------------
    // Supportive methods
    //--------------------------------------------------------------------------

    private void handleComplete( DataSet dataSet, Date completeDate, OrganisationUnit orgUnit, Period period, ImportSummary summary )
    {
        notifier.notify( DATAVALUE_IMPORT, "Completing data set" );
        
        if ( orgUnit == null )
        {
            summary.getConflicts().add( new ImportConflict( OrganisationUnit.class.getSimpleName(), ERROR_OBJECT_NEEDED_TO_COMPLETE ) );
            return;
        }
        
        if ( period == null )
        {
            summary.getConflicts().add( new ImportConflict( Period.class.getSimpleName(), ERROR_OBJECT_NEEDED_TO_COMPLETE ) );
            return;
        }

        CompleteDataSetRegistration completeAlready = registrationService.getCompleteDataSetRegistration( dataSet, period, orgUnit );

        String username = currentUserService.getCurrentUsername();
        
        if ( completeAlready != null )
        {
            completeAlready.setStoredBy( username );
            completeAlready.setDate( completeDate );
            
            registrationService.updateCompleteDataSetRegistration( completeAlready );
        }        
        else
        {
            CompleteDataSetRegistration registration = new CompleteDataSetRegistration( dataSet, period, orgUnit, completeDate, username );
            
            registrationService.saveCompleteDataSetRegistration( registration );
        }
        
        summary.setDataSetComplete( DateUtils.getMediumDateString( completeDate ) );
    }
}
