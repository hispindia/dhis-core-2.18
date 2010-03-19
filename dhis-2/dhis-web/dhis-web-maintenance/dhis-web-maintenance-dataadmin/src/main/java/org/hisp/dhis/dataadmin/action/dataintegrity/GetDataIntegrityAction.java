package org.hisp.dhis.dataadmin.action.dataintegrity;

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

import java.util.Collection;
import java.util.Map;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataintegrity.DataIntegrityService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.validation.ValidationRule;

import com.opensymphony.xwork2.Action;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class GetDataIntegrityAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataIntegrityService dataIntegrityService;

    public void setDataIntegrityService( DataIntegrityService dataIntegrityService )
    {
        this.dataIntegrityService = dataIntegrityService;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private Collection<DataElement> dataElementsWithoutDataSet;

    public Collection<DataElement> getDataElementsWithoutDataSet()
    {
        return dataElementsWithoutDataSet;
    }

    private Collection<DataElement> dataElementsWithoutGroups;

    public Collection<DataElement> getDataElementsWithoutGroups()
    {
        return dataElementsWithoutGroups;
    }

    private Collection<DataSet> dataSetsNotAssignedToOrganisationUnits; 

    public Collection<DataSet> getDataSetsNotAssignedToOrganisationUnits()
    {
        return dataSetsNotAssignedToOrganisationUnits;
    }
    
    private Map<DataElement, Collection<DataSet>> dataElementsAssignedToDataSetsWithDifferentPeriodTypes;

    public Map<DataElement, Collection<DataSet>> getDataElementsAssignedToDataSetsWithDifferentPeriodTypes()
    {
        return dataElementsAssignedToDataSetsWithDifferentPeriodTypes;
    }

    private Collection<Collection<Indicator>> indicatorsWithIdenticalFormulas;

    public Collection<Collection<Indicator>> getIndicatorsWithIdenticalFormulas()
    {
        return indicatorsWithIdenticalFormulas;
    }

    private Collection<Indicator> indicatorsWithoutGroups;

    public Collection<Indicator> getIndicatorsWithoutGroups()
    {
        return indicatorsWithoutGroups;
    }
    
    private Map<Indicator, String> invalidIndicatorNumerators;

    public Map<Indicator, String> getInvalidIndicatorNumerators()
    {
        return invalidIndicatorNumerators;
    }

    private Map<Indicator, String> invalidIndicatorDenominators;

    public Map<Indicator, String> getInvalidIndicatorDenominators()
    {
        return invalidIndicatorDenominators;
    }

    private Collection<OrganisationUnit> organisationUnitsWithCyclicReferences;

    public Collection<OrganisationUnit> getOrganisationUnitsWithCyclicReferences()
    {
        return organisationUnitsWithCyclicReferences;
    }

    private Collection<OrganisationUnit> orphanedOrganisationUnits;

    public Collection<OrganisationUnit> getOrphanedOrganisationUnits()
    {
        return orphanedOrganisationUnits;
    }

    private Collection<OrganisationUnit> organisationUnitsWithoutGroups;

    public Collection<OrganisationUnit> getOrganisationUnitsWithoutGroups()
    {
        return organisationUnitsWithoutGroups;
    }

    private Collection<OrganisationUnit> organisationUnitsViolatingCompulsoryGroupSets;

    public Collection<OrganisationUnit> getOrganisationUnitsViolatingCompulsoryGroupSets()
    {
        return organisationUnitsViolatingCompulsoryGroupSets;
    }

    private Collection<OrganisationUnit> organisationUnitsViolatingExclusiveGroupSets;

    public Collection<OrganisationUnit> getOrganisationUnitsViolatingExclusiveGroupSets()
    {
        return organisationUnitsViolatingExclusiveGroupSets;
    }

    private Collection<OrganisationUnitGroup> organisationUnitGroupsWithoutGroupSets;

    public Collection<OrganisationUnitGroup> getOrganisationUnitGroupsWithoutGroupSets()
    {
        return organisationUnitGroupsWithoutGroupSets;
    }

    private Collection<ValidationRule> validationRulesWithoutGroups;

    public Collection<ValidationRule> getValidationRulesWithoutGroups()
    {
        return validationRulesWithoutGroups;
    }

    private Map<ValidationRule, String> invalidValidationRuleLeftSideExpressions;
    
    public Map<ValidationRule, String> getInvalidValidationRuleLeftSideExpressions()
    {
        return invalidValidationRuleLeftSideExpressions;
    }

    private Map<ValidationRule, String> invalidValidationRuleRightSideExpressions;

    public Map<ValidationRule, String> getInvalidValidationRuleRightSideExpressions()
    {
        return invalidValidationRuleRightSideExpressions;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        dataElementsWithoutDataSet = dataIntegrityService.getDataElementsWithoutDataSet();
        dataElementsWithoutGroups = dataIntegrityService.getDataElementsWithoutGroups();
        dataElementsAssignedToDataSetsWithDifferentPeriodTypes = dataIntegrityService.getDataElementsAssignedToDataSetsWithDifferentPeriodTypes();
        
        dataSetsNotAssignedToOrganisationUnits = dataIntegrityService.getDataSetsNotAssignedToOrganisationUnits();
        
        indicatorsWithIdenticalFormulas = dataIntegrityService.getIndicatorsWithIdenticalFormulas();
        indicatorsWithoutGroups = dataIntegrityService.getIndicatorsWithoutGroups();
        invalidIndicatorNumerators = dataIntegrityService.getInvalidIndicatorNumerators();
        invalidIndicatorDenominators = dataIntegrityService.getInvalidIndicatorDenominators();
        
        organisationUnitsWithCyclicReferences = dataIntegrityService.getOrganisationUnitsWithCyclicReferences();
        orphanedOrganisationUnits = dataIntegrityService.getOrphanedOrganisationUnits();
        organisationUnitsWithoutGroups = dataIntegrityService.getOrganisationUnitsWithoutGroups();
        organisationUnitsViolatingCompulsoryGroupSets = dataIntegrityService.getOrganisationUnitsViolatingCompulsoryGroupSets();
        organisationUnitsViolatingExclusiveGroupSets = dataIntegrityService.getOrganisationUnitsViolatingExclusiveGroupSets();
        
        organisationUnitGroupsWithoutGroupSets = dataIntegrityService.getOrganisationUnitGroupsWithoutGroupSets();
        
        validationRulesWithoutGroups = dataIntegrityService.getValidationRulesWithoutGroups();
        invalidValidationRuleLeftSideExpressions = dataIntegrityService.getInvalidValidationRuleLeftSideExpressions();
        invalidValidationRuleRightSideExpressions = dataIntegrityService.getInvalidValidationRuleRightSideExpressions();
        
        return SUCCESS;
    }
}
