package org.hisp.dhis.light.interpretation.action;

import org.hisp.dhis.interpretation.Interpretation;
import org.hisp.dhis.interpretation.InterpretationService;

import com.opensymphony.xwork2.Action;

/**
 * @author Paul Mark Castillo
 */
public class GetInterpretation
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private InterpretationService interpretationService;

    public void setInterpretationService( InterpretationService interpretationService )
    {
        this.interpretationService = interpretationService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private int interpretationId;

    public int getInterpretationId()
    {
        return interpretationId;
    }

    public void setInterpretationId( int interpretationId )
    {
        this.interpretationId = interpretationId;
    }

    private Interpretation interpretation;

    public Interpretation getInterpretation()
    {
        return interpretation;
    }

    public void setInterpretation( Interpretation interpretation )
    {
        this.interpretation = interpretation;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        setInterpretation( interpretationService.getInterpretation( interpretationId ) );
        
        return SUCCESS;
    }
}