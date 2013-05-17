package org.hisp.dhis.dxf2;

/*
 * Copyright (c) 2004-2013, University of Oslo
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

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.i18n.I18nManager;
import org.hisp.dhis.i18n.I18nManagerException;
import org.hisp.dhis.i18n.locale.LocaleManager;
import org.hisp.dhis.system.util.MathUtils;
import org.hisp.dhis.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class DefaultValidationService implements ValidationService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private I18nManager i18nManager;

    @Autowired
    private UserService userService;

    // -------------------------------------------------------------------------
    // ValidationService Implementation
    // -------------------------------------------------------------------------

    @Override
    public ValidationStatus validateDataElement( DataElement dataElement, String value )
    {
        I18nFormat format;

        try
        {
            format = i18nManager.getI18nFormat();
        }
        catch ( I18nManagerException ex )
        {
            return new ValidationStatus( false, ex.getMessage() );
        }

        value = value.trim();

        if ( value.length() >= 255 )
        {
            return new ValidationStatus( false, value + " is more than 255 characters." );
        }

        if ( dataElement.getType().equals( DataElement.VALUE_TYPE_BOOL ) )
        {
            if ( !(value.equalsIgnoreCase( "true" ) || value.equalsIgnoreCase( "false" )) )
            {
                return new ValidationStatus( false, value + " is not a valid boolean expression." );
            }
        }
        else if ( dataElement.getType().equals( DataElement.VALUE_TYPE_TRUE_ONLY ) )
        {
            if ( !value.equalsIgnoreCase( "true" ) )
            {
                return new ValidationStatus( false, value + " can only be true." );
            }
        }
        else if ( dataElement.getType().equals( DataElement.VALUE_TYPE_DATE ) )
        {
            boolean dateIsValidated = format.parseDate( value ) != null;

            if ( !dateIsValidated )
            {
                return new ValidationStatus( false, value + " is not a valid date expression." );
            }
        }
        else if ( dataElement.getType().equals( DataElement.VALUE_TYPE_USER_NAME ) )
        {
            if ( userService.getUserCredentialsByUsername( value ) == null )
            {
                return new ValidationStatus( false, value + " is not a valid username." );
            }
        }
        else if ( dataElement.getType().equals( DataElement.VALUE_TYPE_STRING ) )
        {
            if ( dataElement.getOptionSet() != null )
            {
                if ( !dataElement.getOptionSet().getOptions().contains( value ) )
                {
                    return new ValidationStatus( false, value + " is not a valid option for this optionSet." );
                }
            }
            else if ( dataElement.getTextType().equals( DataElement.VALUE_TYPE_TEXT ) ||
                dataElement.getTextType().equals( DataElement.VALUE_TYPE_LONG_TEXT ) )
            {
                // no validation for this right now, we already to length validation
            }
        }
        else if ( dataElement.getType().equals( DataElement.VALUE_TYPE_NUMBER ) )
        {
            if ( !MathUtils.isNumeric( value ) )
            {
                return new ValidationStatus( false, value + " is not a valid number." );
            }

            if ( dataElement.getOptionSet() != null )
            {
                if ( !dataElement.getOptionSet().getOptions().contains( value ) )
                {
                    return new ValidationStatus( false, value + " is not a valid option for this optionSet." );
                }
            }

            if ( dataElement.getNumberType().equals( DataElement.VALUE_TYPE_INT ) )
            {
                if ( !MathUtils.isInteger( value ) )
                {
                    return new ValidationStatus( false, value + " is not a valid integer." );
                }
            }
            else if ( dataElement.getNumberType().equals( DataElement.VALUE_TYPE_POSITIVE_INT ) )
            {
                if ( !MathUtils.isPositiveInteger( value ) )
                {
                    return new ValidationStatus( false, value + " is not a valid positive integer." );
                }
            }
            else if ( dataElement.getNumberType().equals( DataElement.VALUE_TYPE_NEGATIVE_INT ) )
            {
                if ( !MathUtils.isNegativeInteger( value ) )
                {
                    return new ValidationStatus( false, value + " is not a valid negative integer." );
                }
            }
        }

        return new ValidationStatus( true );
    }
}
