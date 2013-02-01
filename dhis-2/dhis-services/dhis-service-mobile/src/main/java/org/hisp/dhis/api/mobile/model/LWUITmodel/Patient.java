/*
 * Copyright (c) 2004-2012, University of Oslo
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

package org.hisp.dhis.api.mobile.model.LWUITmodel;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.hisp.dhis.api.mobile.model.Beneficiary;
import org.hisp.dhis.api.mobile.model.DataStreamSerializable;
import org.hisp.dhis.api.mobile.model.PatientAttribute;
import org.hisp.dhis.api.mobile.model.PatientIdentifier;

/**
 * @author Nguyen Kim Lai
 * 
 * @version $ Patient.java Jan 22, 2013 $
 */
public class Patient
    implements DataStreamSerializable
{

    private String clientVersion;

    private int id;

    private String firstName;

    private String middleName;

    private String lastName;

    private int age;

    private List<PatientAttribute> patientAttValues;

    private PatientAttribute groupAttribute;

    private List<PatientIdentifier> identifiers;

    private String gender;

    private Date birthDate;

    private Date registrationDate;

    private Character dobType;

    private List<Program> programs;

    private List<Relationship> relationships;

    public List<PatientIdentifier> getIdentifiers()
    {
        return identifiers;
    }

    public void setIdentifiers( List<PatientIdentifier> identifiers )
    {
        this.identifiers = identifiers;
    }

    public List<Program> getPrograms()
    {
        return programs;
    }

    public void setPrograms( List<Program> programs )
    {
        this.programs = programs;
    }

    public List<Relationship> getRelationships()
    {
        return relationships;
    }

    public void setRelationships( List<Relationship> relationships )
    {
        this.relationships = relationships;
    }

    public String getFullName()
    {
        boolean space = false;
        String name = "";

        if ( firstName != null && firstName.length() != 0 )
        {
            name = firstName;
            space = true;
        }
        if ( middleName != null && middleName.length() != 0 )
        {
            if ( space )
                name += " ";
            name += middleName;
            space = true;
        }
        if ( lastName != null && lastName.length() != 0 )
        {
            if ( space )
                name += " ";
            name += lastName;
        }
        return name;
    }

    public int getAge()
    {
        return age;
    }

    public String getGender()
    {
        return gender;
    }

    public void setGender( String gender )
    {
        this.gender = gender;
    }

    public Date getBirthDate()
    {
        return birthDate;
    }

    public void setBirthDate( Date birthDate )
    {
        this.birthDate = birthDate;
    }

    public Date getRegistrationDate()
    {
        return registrationDate;
    }

    public void setRegistrationDate( Date registrationDate )
    {
        this.registrationDate = registrationDate;
    }

    public Character getDobType()
    {
        return dobType;
    }

    public void setDobType( Character dobType )
    {
        this.dobType = dobType;
    }

    public void setAge( int age )
    {
        this.age = age;
    }

    public PatientAttribute getGroupAttribute()
    {
        return groupAttribute;
    }

    public void setGroupAttribute( PatientAttribute groupAttribute )
    {
        this.groupAttribute = groupAttribute;
    }

    public List<PatientAttribute> getPatientAttValues()
    {
        return patientAttValues;
    }

    public void setPatientAttValues( List<PatientAttribute> patientAttValues )
    {
        this.patientAttValues = patientAttValues;
    }

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName( String firstName )
    {
        this.firstName = firstName;
    }

    public String getMiddleName()
    {
        return middleName;
    }

    public void setMiddleName( String middleName )
    {
        this.middleName = middleName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName( String lastName )
    {
        this.lastName = lastName;
    }

    public String getClientVersion()
    {
        return clientVersion;
    }

    public void setClientVersion( String clientVersion )
    {
        this.clientVersion = clientVersion;
    }

    @Override
    public void serialize( DataOutputStream out )
        throws IOException
    {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream( bout );

        dout.writeInt( this.getId() );
        dout.writeUTF( this.getFirstName() );
        dout.writeUTF( this.getMiddleName() );
        dout.writeUTF( this.getLastName() );
        dout.writeInt( this.getAge() );

        if ( gender != null )
        {
            dout.writeBoolean( true );
            dout.writeUTF( gender );
        }
        else
        {
            dout.writeBoolean( false );
        }

        if ( dobType != null )
        {
            dout.writeBoolean( true );
            dout.writeChar( dobType );
        }
        else
        {
            dout.writeBoolean( false );
        }

        if ( birthDate != null )
        {
            dout.writeBoolean( true );
            dout.writeLong( birthDate.getTime() );
        }
        else
        {
            dout.writeBoolean( false );
        }
        // doesn't transfer blood group to client
        dout.writeBoolean( false );

        if ( registrationDate != null )
        {
            dout.writeBoolean( true );
            dout.writeLong( registrationDate.getTime() );
        }
        else
        {
            dout.writeBoolean( false );
        }

        /*
         * Write attribute which is used as group factor of beneficiary - false:
         * no group factor, true: with group factor
         */
        if ( this.getGroupAttribute() != null )
        {
            dout.writeBoolean( true );
            this.getGroupAttribute().serialize( dout );
        }
        else
        {
            dout.writeBoolean( false );
        }

        List<PatientAttribute> atts = this.getPatientAttValues();
        dout.writeInt( atts.size() );
        for ( PatientAttribute att : atts )
        {
            dout.writeUTF( att.getName() + ":" + att.getValue() );
        }

        // Write PatientIdentifier
        dout.writeInt( identifiers.size() );
        for ( PatientIdentifier each : identifiers )
        {
            each.serialize( dout );
        }

        // Write Enrolled Programs

        dout.writeInt( programs.size() );
        for ( Program each : programs )
        {
            each.serialize( dout );
        }

        // Write Relationships
        dout.writeInt( relationships.size() );
        for ( Relationship each : relationships )
        {
            each.serialize( dout );
        }

        bout.flush();
        bout.writeTo( out );
    }

    @Override
    public void deSerialize( DataInputStream dataInputStream )
        throws IOException
    {

    }

    @Override
    public boolean equals( Object otherObject )
    {
        Beneficiary otherBeneficiary = (Beneficiary) otherObject;
        return this.getId() == otherBeneficiary.getId();
    }

    @Override
    public void serializeVerssion2_8( DataOutputStream out )
        throws IOException
    {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream( bout );

        dout.writeInt( this.getId() );
        dout.writeUTF( this.getFirstName() );
        dout.writeUTF( this.getMiddleName() );
        dout.writeUTF( this.getLastName() );
        dout.writeInt( this.getAge() );

        if ( gender != null )
        {
            dout.writeBoolean( true );
            dout.writeUTF( gender );
        }
        else
        {
            dout.writeBoolean( false );
        }

        if ( dobType != null )
        {
            dout.writeBoolean( true );
            dout.writeChar( dobType );
        }
        else
        {
            dout.writeBoolean( false );
        }

        if ( birthDate != null )
        {
            dout.writeBoolean( true );
            dout.writeLong( birthDate.getTime() );
        }
        else
        {
            dout.writeBoolean( false );
        }
        // doesn't transfer blood group to client
        dout.writeBoolean( false );

        if ( registrationDate != null )
        {
            dout.writeBoolean( true );
            dout.writeLong( registrationDate.getTime() );
        }
        else
        {
            dout.writeBoolean( false );
        }

        /*
         * Write attribute which is used as group factor of beneficiary - false:
         * no group factor, true: with group factor
         */
        if ( this.getGroupAttribute() != null )
        {
            dout.writeBoolean( true );
            this.getGroupAttribute().serialize( dout );
        }
        else
        {
            dout.writeBoolean( false );
        }

        List<PatientAttribute> atts = this.getPatientAttValues();
        dout.writeInt( atts.size() );
        for ( PatientAttribute att : atts )
        {
            dout.writeUTF( att.getName() + ":" + att.getValue() );
        }

        // Write PatientIdentifier
        dout.writeInt( identifiers.size() );
        for ( PatientIdentifier each : identifiers )
        {
            each.serializeVerssion2_8( dout );
        }

        bout.flush();
        bout.writeTo( out );
    }

    @Override
    public void serializeVerssion2_9( DataOutputStream dout )
        throws IOException
    {
        dout.writeInt( this.getId() );
        dout.writeUTF( this.getFirstName() );
        dout.writeUTF( this.getMiddleName() );
        dout.writeUTF( this.getLastName() );
        dout.writeInt( this.getAge() );

        if ( gender != null )
        {
            dout.writeBoolean( true );
            dout.writeUTF( gender );
        }
        else
        {
            dout.writeBoolean( false );
        }

        if ( dobType != null )
        {
            dout.writeBoolean( true );
            dout.writeChar( dobType );
        }
        else
        {
            dout.writeBoolean( false );
        }

        if ( birthDate != null )
        {
            dout.writeBoolean( true );
            dout.writeLong( birthDate.getTime() );
        }
        else
        {
            dout.writeBoolean( false );
        }
        // doesn't transfer blood group to client
        dout.writeBoolean( false );

        if ( registrationDate != null )
        {
            dout.writeBoolean( true );
            dout.writeLong( registrationDate.getTime() );
        }
        else
        {
            dout.writeBoolean( false );
        }

        /*
         * Write attribute which is used as group factor of beneficiary - false:
         * no group factor, true: with group factor
         */
        if ( this.getGroupAttribute() != null )
        {
            dout.writeBoolean( true );
            this.getGroupAttribute().serialize( dout );
        }
        else
        {
            dout.writeBoolean( false );
        }

        List<PatientAttribute> atts = this.getPatientAttValues();
        dout.writeInt( atts.size() );
        for ( PatientAttribute att : atts )
        {
            dout.writeUTF( att.getName() + ":" + att.getValue() );
        }

        // Write PatientIdentifier
        dout.writeInt( identifiers.size() );
        for ( PatientIdentifier each : identifiers )
        {
            each.serializeVerssion2_9( dout );
        }
    }

    /* (non-Javadoc)
     * @see org.hisp.dhis.api.mobile.model.DataStreamSerializable#serializeVerssion2_10(java.io.DataOutputStream)
     */
    @Override
    public void serializeVerssion2_10( DataOutputStream dataOutputStream )
        throws IOException
    {
        // TODO Auto-generated method stub
        
    }

}
