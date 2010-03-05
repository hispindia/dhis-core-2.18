/*
 * Copyright (c) 2004-2009, University of Oslo
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

package org.hisp.dhis.patient.action.patientattribute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeGroup;
import org.hisp.dhis.patient.PatientAttributeGroupService;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;

import com.opensymphony.xwork2.Action;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class ShowUpdatePatientAttributeValueAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PatientService patientService;

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

     private PatientAttributeService patientAttributeService;
    
     public void setPatientAttributeService( PatientAttributeService
     patientAttributeService )
     {
     this.patientAttributeService = patientAttributeService;
     }

    private PatientAttributeGroupService patientAttributeGroupService;

    public void setPatientAttributeGroupService( PatientAttributeGroupService patientAttributeGroupService )
    {
        this.patientAttributeGroupService = patientAttributeGroupService;
    }

    private PatientAttributeValueService patientAttributeValueService;

    public void setPatientAttributeValueService( PatientAttributeValueService patientAttributeValueService )
    {
        this.patientAttributeValueService = patientAttributeValueService;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private int id;

    public void setId( int id )
    {
        this.id = id;
    }

    private Patient patient;

    public Patient getPatient()
    {
        return patient;
    }

    Collection<PatientAttribute> patientNoGroupAttributes;

    public Collection<PatientAttribute> getPatientNoGroupAttributes()
    {
        return patientNoGroupAttributes;
    }

    private Map<Integer, PatientAttributeValue> patientAttributeValueMap;

    public Map<Integer, PatientAttributeValue> getPatientAttributeValueMap()
    {
        return patientAttributeValueMap;
    }

    private Collection<PatientAttributeGroup> patientAttributeGroups;

    public Collection<PatientAttributeGroup> getPatientAttributeGroups()
    {
        return patientAttributeGroups;
    }

    public void setPatientAttributeGroups( Collection<PatientAttributeGroup> patientAttributeGroups )
    {
        this.patientAttributeGroups = patientAttributeGroups;
    }

    private int patientAttributeGroupId;

    public void setPatientAttributeGroupId( int patientAttributeGroupId )
    {
        this.patientAttributeGroupId = patientAttributeGroupId;
    }

    public int getPatientAttributeGroupId()
    {
        return patientAttributeGroupId;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        patient = patientService.getPatient( id );

//        if ( patientAttributeGroups == null )
            patientAttributeGroups = patientAttributeGroupService.getAllPatientAttributeGroups();

//        if ( patientAttributeGroupId != 0 )
//        {
//            patientAttributes = new ArrayList<PatientAttribute>();
//
//            patientAttributes = patientAttributeGroupService.getPatientAttributeGroup(
//                patientAttributeGroupId ).getAttributes();
//        }else 
//        {
//             patientAttributes = patientAttributeService.getAllPatientAttributes();
//        }
            
        patientNoGroupAttributes = patientAttributeService.getPatientAttributesNotGroup();

        Collection<PatientAttributeValue> patientAttributeValues = patientAttributeValueService
            .getPatientAttributeValues( patient );

        patientAttributeValueMap = new HashMap<Integer, PatientAttributeValue>( patientAttributeValues.size() );

        for ( PatientAttributeValue patientAttributeValue : patientAttributeValues )
        {
            patientAttributeValueMap.put( patientAttributeValue.getPatientAttribute().getId(), patientAttributeValue );
        }

        return SUCCESS;
    }
}
