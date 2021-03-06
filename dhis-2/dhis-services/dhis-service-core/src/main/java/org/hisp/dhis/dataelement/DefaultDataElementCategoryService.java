package org.hisp.dhis.dataelement;

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

import static org.hisp.dhis.i18n.I18nUtils.i18n;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.common.GenericDimensionalObjectStore;
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.common.IdentifiableObjectUtils;
import org.hisp.dhis.common.IdentifiableProperty;
import org.hisp.dhis.i18n.I18nService;
import org.hisp.dhis.system.util.Filter;
import org.hisp.dhis.system.util.FilterUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Abyot Asalefew
 */
@Transactional
public class DefaultDataElementCategoryService
    implements DataElementCategoryService
{
    private static final Log log = LogFactory.getLog( DefaultDataElementCategoryService.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private CategoryStore categoryStore;

    public void setCategoryStore( CategoryStore categoryStore )
    {
        this.categoryStore = categoryStore;
    }

    private CategoryOptionStore categoryOptionStore;

    public void setCategoryOptionStore( CategoryOptionStore categoryOptionStore )
    {
        this.categoryOptionStore = categoryOptionStore;
    }

    private CategoryComboStore categoryComboStore;

    public void setCategoryComboStore( CategoryComboStore categoryComboStore )
    {
        this.categoryComboStore = categoryComboStore;
    }

    private CategoryOptionComboStore categoryOptionComboStore;

    public void setCategoryOptionComboStore( CategoryOptionComboStore categoryOptionComboStore )
    {
        this.categoryOptionComboStore = categoryOptionComboStore;
    }

    private CategoryOptionGroupStore categoryOptionGroupStore;

    public void setCategoryOptionGroupStore( CategoryOptionGroupStore categoryOptionGroupStore )
    {
        this.categoryOptionGroupStore = categoryOptionGroupStore;
    }

    private GenericDimensionalObjectStore<CategoryOptionGroupSet> categoryOptionGroupSetStore;

    public void setCategoryOptionGroupSetStore( GenericDimensionalObjectStore<CategoryOptionGroupSet> categoryOptionGroupSetStore )
    {
        this.categoryOptionGroupSetStore = categoryOptionGroupSetStore;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }
    
    private IdentifiableObjectManager idObjectManager;

    public void setIdObjectManager( IdentifiableObjectManager idObjectManager )
    {
        this.idObjectManager = idObjectManager;
    }

    private I18nService i18nService;

    public void setI18nService( I18nService service )
    {
        i18nService = service;
    }

    // -------------------------------------------------------------------------
    // Category
    // -------------------------------------------------------------------------

    @Override
    public int addDataElementCategory( DataElementCategory dataElementCategory )
    {
        return categoryStore.save( dataElementCategory );
    }

    @Override
    public void updateDataElementCategory( DataElementCategory dataElementCategory )
    {
        categoryStore.update( dataElementCategory );
    }

    @Override
    public void deleteDataElementCategory( DataElementCategory dataElementCategory )
    {
        categoryStore.delete( dataElementCategory );
    }

    @Override
    public Collection<DataElementCategory> getAllDataElementCategories()
    {
        return i18n( i18nService, categoryStore.getAll() );
    }

    @Override
    public DataElementCategory getDataElementCategory( int id )
    {
        return i18n( i18nService, categoryStore.get( id ) );
    }

    @Override
    public DataElementCategory getDataElementCategory( int id, boolean i18nCategoryOptions )
    {
        DataElementCategory category = getDataElementCategory( id );
        
        if ( category != null )
        {
            if ( i18nCategoryOptions )
            {
                i18n( i18nService, category.getCategoryOptions() );
            }
        }
        
        return category;
    }

    @Override
    public DataElementCategory getDataElementCategory( String uid )
    {
        return i18n( i18nService, categoryStore.getByUid( uid ) );
    }

    @Override
    public Collection<DataElementCategory> getDataElementCategories( final Collection<Integer> identifiers )
    {
        Collection<DataElementCategory> categories = getAllDataElementCategories();

        return identifiers == null ? categories : FilterUtils.filter( categories, new Filter<DataElementCategory>()
        {
            @Override
            public boolean retain( DataElementCategory object )
            {
                return identifiers.contains( object.getId() );
            }
        } );
    }

    @Override
    public Collection<DataElementCategory> getDataElementCategoriesByUid( Collection<String> uids )
    {
        return categoryStore.getByUid( uids );
    }

    @Override
    public DataElementCategory getDataElementCategoryByName( String name )
    {
        List<DataElementCategory> dataElementCategories = new ArrayList<>(
            categoryStore.getAllEqName( name ) );

        if ( dataElementCategories.isEmpty() )
        {
            return null;
        }

        return i18n( i18nService, dataElementCategories.get( 0 ) );
    }

    @Override
    public Collection<DataElementCategory> getDisaggregationCategories()
    {
        return i18n( i18nService, categoryStore.getCategoriesByDimensionType( DataElementCategoryCombo.DIMENSION_TYPE_DISAGGREGATION ) );
    }

    @Override
    public Collection<DataElementCategory> getDisaggregationDataDimensionCategoriesNoAcl()
    {
        return categoryStore.getCategoriesNoAcl( DataElementCategoryCombo.DIMENSION_TYPE_DISAGGREGATION, true );
    }

    @Override
    public Collection<DataElementCategory> getAttributeCategories()
    {
        return i18n( i18nService, categoryStore.getCategoriesByDimensionType( DataElementCategoryCombo.DIMENSION_TYPE_ATTTRIBUTE ) );
    }

    @Override
    public Collection<DataElementCategory> getAttributeDataDimensionCategoriesNoAcl()
    {
        return categoryStore.getCategoriesNoAcl( DataElementCategoryCombo.DIMENSION_TYPE_ATTTRIBUTE, true );
    }

    @Override
    public Collection<DataElementCategory> getDataElementCategoryBetween( int first, int max )
    {
        return i18n( i18nService, categoryStore.getAllOrderedName( first, max ) );
    }

    @Override
    public Collection<DataElementCategory> getDataElementCategoryBetweenByName( String name, int first, int max )
    {
        return i18n( i18nService, categoryStore.getAllLikeName( name, first, max ) );
    }

    @Override
    public Collection<DataElementCategory> getDataElementCategoriesBetween( int first, int max )
    {
        return i18n( i18nService, categoryStore.getAllOrderedName( first, max ) );
    }

    @Override
    public Collection<DataElementCategory> getDataElementCategoriesBetweenByName( String name, int first, int max )
    {
        return i18n( i18nService, categoryStore.getAllLikeName( name, first, max ) );
    }

    @Override
    public int getDataElementCategoryCount()
    {
        return categoryStore.getCount();
    }

    @Override
    public int getDataElementCategoryCountByName( String name )
    {
        return categoryStore.getCountLikeName( name );
    }

    // -------------------------------------------------------------------------
    // CategoryOption
    // -------------------------------------------------------------------------

    @Override
    public int addDataElementCategoryOption( DataElementCategoryOption dataElementCategoryOption )
    {
        return categoryOptionStore.save( dataElementCategoryOption );
    }

    @Override
    public void updateDataElementCategoryOption( DataElementCategoryOption dataElementCategoryOption )
    {
        categoryOptionStore.update( dataElementCategoryOption );
    }

    @Override
    public void deleteDataElementCategoryOption( DataElementCategoryOption dataElementCategoryOption )
    {
        categoryOptionStore.delete( dataElementCategoryOption );
    }

    @Override
    public DataElementCategoryOption getDataElementCategoryOption( int id )
    {
        return i18n( i18nService, categoryOptionStore.get( id ) );
    }

    @Override
    public DataElementCategoryOption getDataElementCategoryOption( String uid )
    {
        return i18n( i18nService, categoryOptionStore.getByUid( uid ) );
    }

    @Override
    public DataElementCategoryOption getDataElementCategoryOptionByName( String name )
    {
        return i18n( i18nService, categoryOptionStore.getByName( name ) );
    }

    @Override
    public DataElementCategoryOption getDataElementCategoryOptionByShortName( String shortName )
    {
        return i18n( i18nService, categoryOptionStore.getByShortName( shortName ) );
    }
    
    @Override
    public DataElementCategoryOption getDataElementCategoryOptionByCode( String code )
    {
        return i18n( i18nService, categoryOptionStore.getByCode( code ) );
    }

    @Override
    public Collection<DataElementCategoryOption> getDataElementCategoryOptions( final Collection<Integer> identifiers )
    {
        Collection<DataElementCategoryOption> categoryOptions = getAllDataElementCategoryOptions();

        return identifiers == null ? categoryOptions : FilterUtils.filter( categoryOptions,
            new Filter<DataElementCategoryOption>()
            {
                @Override
                public boolean retain( DataElementCategoryOption object )
                {
                    return identifiers.contains( object.getId() );
                }
            } );
    }

    @Override
    public Collection<DataElementCategoryOption> getDataElementCategoryOptionsByUid( Collection<String> uids )
    {
        return categoryOptionStore.getByUid( uids );
    }

    @Override
    public Collection<DataElementCategoryOption> getAllDataElementCategoryOptions()
    {
        return i18n( i18nService, categoryOptionStore.getAll() );
    }

    @Override
    public Collection<DataElementCategoryOption> getDataElementCategoryOptions( DataElementCategory category )
    {
        return i18n( i18nService, categoryOptionStore.getCategoryOptions( category ) );
    }

    @Override
    public Collection<DataElementCategoryOption> getDataElementCategoryOptionsBetween( int first, int max )
    {
        return i18n( i18nService, categoryOptionStore.getAllOrderedName( first, max ) );
    }

    @Override
    public Collection<DataElementCategoryOption> getDataElementCategoryOptionsBetweenByName( String name, int first,
        int max )
    {
        return i18n( i18nService, categoryOptionStore.getAllLikeName( name, first, max ) );
    }

    @Override
    public int getDataElementCategoryOptionCount()
    {
        return categoryOptionStore.getCount();
    }

    @Override
    public int getDataElementCategoryOptionCountByName( String name )
    {
        return categoryOptionStore.getCountLikeName( name );
    }

    // -------------------------------------------------------------------------
    // CategoryCombo
    // -------------------------------------------------------------------------

    @Override
    public int addDataElementCategoryCombo( DataElementCategoryCombo dataElementCategoryCombo )
    {
        return categoryComboStore.save( dataElementCategoryCombo );
    }

    @Override
    public void updateDataElementCategoryCombo( DataElementCategoryCombo dataElementCategoryCombo )
    {
        categoryComboStore.save( dataElementCategoryCombo );
    }

    @Override
    public void deleteDataElementCategoryCombo( DataElementCategoryCombo dataElementCategoryCombo )
    {
        categoryComboStore.delete( dataElementCategoryCombo );
    }

    @Override
    public Collection<DataElementCategoryCombo> getAllDataElementCategoryCombos()
    {
        return i18n( i18nService, categoryComboStore.getAll() );
    }

    @Override
    public DataElementCategoryCombo getDataElementCategoryCombo( int id )
    {
        return i18n( i18nService, categoryComboStore.get( id ) );
    }

    @Override
    public DataElementCategoryCombo getDataElementCategoryCombo( String uid )
    {
        return i18n( i18nService, categoryComboStore.getByUid( uid ) );
    }

    @Override
    public Collection<DataElementCategoryCombo> getDataElementCategoryCombos( final Collection<Integer> identifiers )
    {
        Collection<DataElementCategoryCombo> categoryCombos = getAllDataElementCategoryCombos();

        return identifiers == null ? categoryCombos : FilterUtils.filter( categoryCombos,
            new Filter<DataElementCategoryCombo>()
            {
                @Override
                public boolean retain( DataElementCategoryCombo object )
                {
                    return identifiers.contains( object.getId() );
                }
            } );
    }

    @Override
    public DataElementCategoryCombo getDataElementCategoryComboByName( String name )
    {
        return i18n( i18nService, categoryComboStore.getByName( name ) );
    }

    @Override
    public DataElementCategoryCombo getDefaultDataElementCategoryCombo()
    {
        return getDataElementCategoryComboByName( DataElementCategoryCombo.DEFAULT_CATEGORY_COMBO_NAME );
    }

    @Override
    public int getDataElementCategoryComboCount()
    {
        return categoryComboStore.getCount();
    }

    @Override
    public int getDataElementCategoryComboCountByName( String name )
    {
        return categoryComboStore.getCountLikeName( name );
    }

    @Override
    public Collection<DataElementCategoryCombo> getDataElementCategoryCombosBetween( int first, int max )
    {
        return i18n( i18nService, categoryComboStore.getAllOrderedName( first, max ) );
    }

    @Override
    public Collection<DataElementCategoryCombo> getDataElementCategoryCombosBetweenByName( String name, int first,
        int max )
    {
        return i18n( i18nService, categoryComboStore.getAllLikeName( name, first, max ) );
    }

    @Override
    public Collection<DataElementCategoryCombo> getDisaggregationCategoryCombos()
    {
        return i18n( i18nService,
            categoryComboStore
                .getCategoryCombosByDimensionType( DataElementCategoryCombo.DIMENSION_TYPE_DISAGGREGATION ) );
    }

    @Override
    public Collection<DataElementCategoryCombo> getAttributeCategoryCombos()
    {
        return i18n( i18nService,
            categoryComboStore.getCategoryCombosByDimensionType( DataElementCategoryCombo.DIMENSION_TYPE_ATTTRIBUTE ) );
    }

    // -------------------------------------------------------------------------
    // CategoryOptionCombo
    // -------------------------------------------------------------------------

    @Override
    public int addDataElementCategoryOptionCombo( DataElementCategoryOptionCombo dataElementCategoryOptionCombo )
    {
        return categoryOptionComboStore.save( dataElementCategoryOptionCombo );
    }

    @Override
    public void updateDataElementCategoryOptionCombo( DataElementCategoryOptionCombo dataElementCategoryOptionCombo )
    {
        categoryOptionComboStore.update( dataElementCategoryOptionCombo );
    }

    @Override
    public void deleteDataElementCategoryOptionCombo( DataElementCategoryOptionCombo dataElementCategoryOptionCombo )
    {
        categoryOptionComboStore.delete( dataElementCategoryOptionCombo );
    }

    @Override
    public DataElementCategoryOptionCombo getDataElementCategoryOptionCombo( int id )
    {
        return categoryOptionComboStore.get( id );
    }

    @Override
    public DataElementCategoryOptionCombo getDataElementCategoryOptionCombo( String uid )
    {
        return categoryOptionComboStore.getByUid( uid );
    }
    
    @Override
    public DataElementCategoryOptionCombo getDataElementCategoryOptionComboByCode( String code )
    {
        return categoryOptionComboStore.getByCode( code );
    }

    @Override
    public Collection<DataElementCategoryOptionCombo> getDataElementCategoryOptionCombos(
        final Collection<Integer> identifiers )
    {
        Collection<DataElementCategoryOptionCombo> categoryOptionCombos = getAllDataElementCategoryOptionCombos();

        return identifiers == null ? categoryOptionCombos : FilterUtils.filter( categoryOptionCombos,
            new Filter<DataElementCategoryOptionCombo>()
            {
                @Override
                public boolean retain( DataElementCategoryOptionCombo object )
                {
                    return identifiers.contains( object.getId() );
                }
            } );
    }

    @Override
    public Collection<DataElementCategoryOptionCombo> getDataElementCategoryOptionCombosByUid( Collection<String> uids )
    {
        return categoryOptionComboStore.getByUid( uids );
    }

    @Override
    public DataElementCategoryOptionCombo getDataElementCategoryOptionCombo(
        Collection<DataElementCategoryOption> categoryOptions )
    {
        for ( DataElementCategoryOptionCombo categoryOptionCombo : getAllDataElementCategoryOptionCombos() )
        {
            if ( CollectionUtils.isEqualCollection( categoryOptions, categoryOptionCombo.getCategoryOptions() ) )
            {
                return categoryOptionCombo;
            }
        }

        return null;
    }

    @Override
    public DataElementCategoryOptionCombo getDataElementCategoryOptionCombo( DataElementCategoryOptionCombo categoryOptionCombo )
    {
        for ( DataElementCategoryOptionCombo dcoc : getAllDataElementCategoryOptionCombos() )
        {
            // -----------------------------------------------------------------
            // Hibernate puts proxies on associations and makes the native
            // equals methods unusable
            // -----------------------------------------------------------------

            if ( dcoc.equalsOnName( categoryOptionCombo ) )
            {
                return dcoc;
            }
        }

        return null;
    }

    @Override
    public DataElementCategoryOptionCombo getDataElementCategoryOptionCombo( DataElementCategoryCombo categoryCombo,
        Set<DataElementCategoryOption> categoryOptions )
    {
        return categoryOptionComboStore.getCategoryOptionCombo( categoryCombo, categoryOptions );
    }

    @Override
    public Collection<DataElementCategoryOptionCombo> getAllDataElementCategoryOptionCombos()
    {
        return categoryOptionComboStore.getAll();
    }

    @Override
    public Collection<DataElementCategoryOptionCombo> getOptionCombosBetween( int min, int max )
    {
        return categoryOptionComboStore.getAllOrderedLastUpdated( min, max );
    }
    
    @Override
    public Integer getOptionComboCount()
    {
        return categoryOptionComboStore.getCount();
    }
    
    @Override
    public void generateDefaultDimension()
    {
        // ---------------------------------------------------------------------
        // DataElementCategoryOption
        // ---------------------------------------------------------------------

        DataElementCategoryOption categoryOption = new DataElementCategoryOption(
            DataElementCategoryOption.DEFAULT_NAME );

        addDataElementCategoryOption( categoryOption );

        // ---------------------------------------------------------------------
        // DataElementCategory
        // ---------------------------------------------------------------------

        DataElementCategory category = new DataElementCategory( DataElementCategory.DEFAULT_NAME );
        category.addDataElementCategoryOption( categoryOption );
        addDataElementCategory( category );

        // ---------------------------------------------------------------------
        // DataElementCategoryCombo
        // ---------------------------------------------------------------------

        DataElementCategoryCombo categoryCombo = new DataElementCategoryCombo(
            DataElementCategoryCombo.DEFAULT_CATEGORY_COMBO_NAME );
        categoryCombo.addDataElementCategory( category );
        addDataElementCategoryCombo( categoryCombo );

        // ---------------------------------------------------------------------
        // DataElementCategoryOptionCombo
        // ---------------------------------------------------------------------

        DataElementCategoryOptionCombo categoryOptionCombo = new DataElementCategoryOptionCombo();

        categoryOptionCombo.setCategoryCombo( categoryCombo );
        categoryOptionCombo.addDataElementCategoryOption( categoryOption );

        addDataElementCategoryOptionCombo( categoryOptionCombo );

        Set<DataElementCategoryOptionCombo> categoryOptionCombos = new HashSet<>();
        categoryOptionCombos.add( categoryOptionCombo );
        categoryCombo.setOptionCombos( categoryOptionCombos );

        updateDataElementCategoryCombo( categoryCombo );

        categoryOption.setCategoryOptionCombos( categoryOptionCombos );
        updateDataElementCategoryOption( categoryOption );
    }

    @Override
    public DataElementCategoryOptionCombo getDefaultDataElementCategoryOptionCombo()
    {
        DataElementCategoryCombo categoryCombo = getDataElementCategoryComboByName( DataElementCategoryCombo.DEFAULT_CATEGORY_COMBO_NAME );

        return categoryCombo != null && categoryCombo.hasOptionCombos() ? categoryCombo.getOptionCombos().iterator().next() : null;
    }

    @Override
    public Collection<DataElementOperand> populateOperands( Collection<DataElementOperand> operands )
    {
        for ( DataElementOperand operand : operands )
        {
            DataElement dataElement = dataElementService.getDataElement( operand.getDataElementId() );
            DataElementCategoryOptionCombo categoryOptionCombo = getDataElementCategoryOptionCombo( operand
                .getOptionComboId() );

            operand.updateProperties( dataElement, categoryOptionCombo );
        }

        return operands;
    }

    @Override
    public Collection<DataElementOperand> getOperands( Collection<DataElement> dataElements )
    {
        return getOperands( dataElements, false );
    }

    @Override
    public Collection<DataElementOperand> getOperands( Collection<DataElement> dataElements, boolean includeTotals )
    {
        List<DataElementOperand> operands = new ArrayList<>();

        for ( DataElement dataElement : dataElements )
        {
            if ( dataElement != null && dataElement.getCategoryCombo() != null )
            {
                if ( !dataElement.getCategoryCombo().isDefault() && includeTotals )
                {
                    DataElementOperand operand = new DataElementOperand();
                    operand.updateProperties( dataElement );
    
                    operands.add( operand );
                }
    
                for ( DataElementCategoryOptionCombo categoryOptionCombo : dataElement.getCategoryCombo().getSortedOptionCombos() )
                {
                    DataElementOperand operand = new DataElementOperand();
                    operand.updateProperties( dataElement, categoryOptionCombo );
    
                    operands.add( operand );
                }
            }
        }

        return operands;
    }

    @Override
    public Collection<DataElementOperand> getOperandsLikeName( String name )
    {
        Collection<DataElement> dataElements = dataElementService.getDataElementsLikeName( name );

        return getOperands( dataElements );
    }

    @Override
    public Collection<DataElementOperand> getFullOperands( Collection<DataElement> dataElements )
    {
        Collection<DataElementOperand> operands = new ArrayList<>();

        for ( DataElement dataElement : dataElements )
        {
            if ( dataElement != null && dataElement.getCategoryCombo() != null )
            {
                for ( DataElementCategoryOptionCombo categoryOptionCombo : dataElement.getCategoryCombo().getOptionCombos() )
                {
                    DataElementOperand dataElementOperand = new DataElementOperand( dataElement, categoryOptionCombo );
                    dataElementOperand.updateProperties( dataElement, categoryOptionCombo );
    
                    operands.add( dataElementOperand );
                }
            }
        }

        return operands;
    }

    @Override
    public void generateOptionCombos( DataElementCategoryCombo categoryCombo )
    {
        categoryCombo.generateOptionCombos();

        for ( DataElementCategoryOptionCombo optionCombo : categoryCombo.getOptionCombos() )
        {
            categoryCombo.getOptionCombos().add( optionCombo );
            addDataElementCategoryOptionCombo( optionCombo );
        }

        updateDataElementCategoryCombo( categoryCombo );
    }

    @Override
    public void updateOptionCombos( DataElementCategory category )
    {
        for ( DataElementCategoryCombo categoryCombo : getAllDataElementCategoryCombos() )
        {
            if ( categoryCombo.getCategories().contains( category ) )
            {
                updateOptionCombos( categoryCombo );
            }
        }
    }

    @Override
    public void updateOptionCombos( DataElementCategoryCombo categoryCombo )
    {
        List<DataElementCategoryOptionCombo> generatedOptionCombos = categoryCombo.generateOptionCombosList();
        Set<DataElementCategoryOptionCombo> persistedOptionCombos = categoryCombo.getOptionCombos();

        boolean modified = false;

        for ( DataElementCategoryOptionCombo optionCombo : generatedOptionCombos )
        {
            if ( !persistedOptionCombos.contains( optionCombo ) )
            {
                categoryCombo.getOptionCombos().add( optionCombo );
                addDataElementCategoryOptionCombo( optionCombo );

                log.info( "Added missing category option combo: " + optionCombo + " for category combo: "
                    + categoryCombo.getName() );
                modified = true;
            }
        }

        if ( modified )
        {
            updateDataElementCategoryCombo( categoryCombo );
        }
    }

    @Override
    public void updateAllOptionCombos()
    {
        Collection<DataElementCategoryCombo> categoryCombos = getAllDataElementCategoryCombos();

        for ( DataElementCategoryCombo categoryCombo : categoryCombos )
        {
            updateOptionCombos( categoryCombo );
        }
    }

    @Override
    public Map<String, Integer> getDataElementCategoryOptionComboUidIdMap()
    {
        Map<String, Integer> map = new HashMap<>();

        for ( DataElementCategoryOptionCombo coc : getAllDataElementCategoryOptionCombos() )
        {
            map.put( coc.getUid(), coc.getId() );
        }

        return map;
    }

    @Override
    public int getDataElementCategoryOptionComboCount()
    {
        return categoryOptionComboStore.getCount();
    }

    @Override
    public int getDataElementCategoryOptionComboCountByName( String name )
    {
        return categoryOptionComboStore.getCountLikeName( name );
    }

    @Override
    public DataElementCategoryOptionCombo getDataElementCategoryOptionComboAcl( IdentifiableProperty property, String id )
    {
        DataElementCategoryOptionCombo coc = idObjectManager.getObject( DataElementCategoryOptionCombo.class, property, id );
        
        return canReadDataElementCategoryOptionCombo( coc ) ? coc : null;
    }
        
    private boolean canReadDataElementCategoryOptionCombo( DataElementCategoryOptionCombo categoryOptionCombo )
    {
        if ( categoryOptionCombo == null )
        {
            return false;
        }
        
        List<DataElementCategoryOption> options = categoryOptionStore.getByUid( 
            IdentifiableObjectUtils.getUids( categoryOptionCombo.getCategoryOptions() ) );
        
        return options.size() == categoryOptionCombo.getCategoryOptions().size();
    }
    
    // -------------------------------------------------------------------------
    // CategoryOptionGroup
    // -------------------------------------------------------------------------

    @Override
    public int saveCategoryOptionGroup( CategoryOptionGroup group )
    {
        return categoryOptionGroupStore.save( group );
    }

    @Override
    public void updateCategoryOptionGroup( CategoryOptionGroup group )
    {
        categoryOptionGroupStore.update( group );
    }

    @Override
    public CategoryOptionGroup getCategoryOptionGroup( int id )
    {
        return categoryOptionGroupStore.get( id );
    }

    @Override
    public CategoryOptionGroup getCategoryOptionGroup( String uid )
    {
        return categoryOptionGroupStore.getByUid( uid );
    }

    @Override
    public List<CategoryOptionGroup> getCategoryOptionGroupsByUid( Collection<String> uids )
    {
        return categoryOptionGroupStore.getByUid( uids );
    }
    
    @Override
    public void deleteCategoryOptionGroup( CategoryOptionGroup group )
    {
        categoryOptionGroupStore.delete( group );
    }

    @Override
    public Collection<CategoryOptionGroup> getCategoryOptionGroupsBetween( int first, int max )
    {
        return categoryOptionGroupStore.getAllOrderedName( first, max );
    }

    @Override
    public Collection<CategoryOptionGroup> getCategoryOptionGroupsBetweenByName( int first, int max, String name )
    {
        return categoryOptionGroupStore.getAllLikeName( name, first, max );
    }

    @Override
    public Collection<CategoryOptionGroup> getAllCategoryOptionGroups()
    {
        return categoryOptionGroupStore.getAll();
    }

    @Override
    public List<CategoryOptionGroup> getCategoryOptionGroups( CategoryOptionGroupSet groupSet )
    {
        return categoryOptionGroupStore.getCategoryOptionGroups( groupSet );
    }
    
    @Override
    public CategoryOptionGroup getCategoryOptionGroupByName( String name )
    {
        return i18n( i18nService, categoryOptionGroupStore.getByName( name ) );
    }

    @Override
    public CategoryOptionGroup getCategoryOptionGroupByCode( String code )
    {
        return i18n( i18nService, categoryOptionGroupStore.getByCode( code ) );
    }

    @Override
    public CategoryOptionGroup getCategoryOptionGroupByShortName( String shortName )
    {
        List<CategoryOptionGroup> categoryOptionGroups = new ArrayList<>(
            categoryOptionGroupStore.getAllEqShortName( shortName ) );

        if ( categoryOptionGroups.isEmpty() )
        {
            return null;
        }

        return i18n( i18nService, categoryOptionGroups.get( 0 ) );
    }

    @Override
    public int getCategoryOptionGroupCount()
    {
        return categoryOptionGroupStore.getCount();
    }

    @Override
    public int getCategoryOptionGroupCountByName( String name )
    {
        return categoryOptionGroupStore.getCountLikeName( name );
    }

    // -------------------------------------------------------------------------
    // CategoryOptionGroupSet
    // -------------------------------------------------------------------------

    @Override
    public int saveCategoryOptionGroupSet( CategoryOptionGroupSet group )
    {
        return categoryOptionGroupSetStore.save( group );
    }

    @Override
    public void updateCategoryOptionGroupSet( CategoryOptionGroupSet group )
    {
        categoryOptionGroupSetStore.update( group );
    }

    @Override
    public CategoryOptionGroupSet getCategoryOptionGroupSet( int id )
    {
        return categoryOptionGroupSetStore.get( id );
    }

    @Override
    public CategoryOptionGroupSet getCategoryOptionGroupSet( String uid )
    {
        return categoryOptionGroupSetStore.getByUid( uid );
    }
    
    @Override
    public List<CategoryOptionGroupSet> getCategoryOptionGroupSetsByUid( Collection<String> uids )
    {
        return categoryOptionGroupSetStore.getByUid( uids );
    }

    @Override
    public void deleteCategoryOptionGroupSet( CategoryOptionGroupSet group )
    {
        categoryOptionGroupSetStore.delete( group );
    }

    @Override
    public Collection<CategoryOptionGroupSet> getCategoryOptionGroupSetsBetween( int first, int max )
    {
        return categoryOptionGroupSetStore.getAllOrderedName( first, max );
    }

    @Override
    public Collection<CategoryOptionGroupSet> getCategoryOptionGroupSetsBetweenByName( int first, int max, String name )
    {
        return categoryOptionGroupSetStore.getAllLikeName( name, first, max );
    }

    @Override
    public Collection<CategoryOptionGroupSet> getAllCategoryOptionGroupSets()
    {
        return categoryOptionGroupSetStore.getAll();
    }

    @Override
    public CategoryOptionGroupSet getCategoryOptionGroupSetByName( String name )
    {
        return categoryOptionGroupSetStore.getByName( name );
    }

    @Override
    public int getCategoryOptionGroupSetCount()
    {
        return categoryOptionGroupSetStore.getCount();
    }

    @Override
    public int getCategoryOptionGroupSetCountByName( String name )
    {
        return categoryOptionGroupSetStore.getCountLikeName( name );
    }
}
