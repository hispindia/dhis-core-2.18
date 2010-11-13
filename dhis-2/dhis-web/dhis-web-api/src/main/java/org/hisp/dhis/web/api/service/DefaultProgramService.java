/**
 * 
 */
package org.hisp.dhis.web.api.service;

import static org.hisp.dhis.i18n.I18nUtils.i18n;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.web.api.model.AbstractModel;
import org.hisp.dhis.web.api.model.AbstractModelList;
import org.hisp.dhis.web.api.model.DataElement;
import org.hisp.dhis.web.api.model.Program;
import org.hisp.dhis.web.api.model.ProgramStage;
import org.hisp.dhis.web.api.utils.LocaleUtil;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author abyotag_adm
 * 
 */
public class DefaultProgramService
    implements IProgramService
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private org.hisp.dhis.program.ProgramService programService;

    @Autowired
    private org.hisp.dhis.i18n.I18nService i18nService;

    @Autowired
    private CurrentUserService currentUserService;

    // -------------------------------------------------------------------------
    // ProgramService
    // -------------------------------------------------------------------------

    // public AbstractModelList getAllProgramsForLocale(String localeString)
    // {
    // Collection<OrganisationUnit> units =
    // currentUserService.getCurrentUser().getOrganisationUnits();
    // OrganisationUnit unit = null;
    //
    // if( units.size() > 0 )
    // {
    // unit = units.iterator().next();
    // }
    // else
    // {
    // return null;
    // }
    //
    // Locale locale = LocaleUtil.getLocale(localeString);
    //
    // AbstractModelList abstractModelList = new AbstractModelList();
    //
    // List<AbstractModel> abstractModels = new ArrayList<AbstractModel>();
    //
    // for (org.hisp.dhis.program.Program program :
    // programService.getPrograms(unit))
    // {
    // program = i18n( i18nService, locale, program );
    //
    // AbstractModel abstractModel = new AbstractModel();
    //
    // abstractModel.setId( program.getId());
    // abstractModel.setName(program.getName());
    //
    // abstractModels.add(abstractModel);
    // }
    //
    // abstractModelList.setAbstractModels(abstractModels);
    //
    // return abstractModelList;
    // }

    public List<Program> getAllProgramsForLocale( String localeString )
    {
        List<Program> programs = new ArrayList<Program>();

        Collection<OrganisationUnit> units = currentUserService.getCurrentUser().getOrganisationUnits();
        OrganisationUnit unit = null;

        if ( units.size() > 0 )
        {
            unit = units.iterator().next();
        }
        else
        {
            return null;
        }
        for ( org.hisp.dhis.program.Program program : programService.getPrograms( unit ) )
        {
            programs.add( getProgramForLocale( program.getId(), localeString ) );
        }

        return programs;
    }

    public Program getProgramForLocale( int programId, String localeString )
    {
        Locale locale = LocaleUtil.getLocale( localeString );

        org.hisp.dhis.program.Program program = programService.getProgram( programId );

        program = i18n( i18nService, locale, program );

        Program pr = new Program();

        pr.setId( program.getId() );
        pr.setName( program.getName() );

        List<ProgramStage> prStgs = new ArrayList<ProgramStage>();

        for ( org.hisp.dhis.program.ProgramStage programStage : program.getProgramStages() )
        {
            programStage = i18n( i18nService, locale, programStage );

            ProgramStage prStg = new ProgramStage();
            prStg.setId( programStage.getId() );
            prStg.setName( programStage.getName() );

            List<DataElement> des = new ArrayList<DataElement>();

            for ( org.hisp.dhis.program.ProgramStageDataElement programStagedataElement : programStage
                .getProgramStageDataElements() )
            {
                AbstractModelList mobileCategpryOptCombos = new AbstractModelList();
                mobileCategpryOptCombos.setAbstractModels( new ArrayList<AbstractModel>() );
                programStagedataElement = i18n( i18nService, locale, programStagedataElement );
                Set<DataElementCategoryOptionCombo> deCatOptCombs = programStagedataElement.getDataElement()
                    .getCategoryCombo().getOptionCombos();

                for ( DataElementCategoryOptionCombo categoryOptCombo : deCatOptCombs )
                {
                    AbstractModel mobileCategpryOptCombo = new AbstractModel();
                    mobileCategpryOptCombo.setId( categoryOptCombo.getId() );
                    mobileCategpryOptCombo.setName( categoryOptCombo.getName() );
                    mobileCategpryOptCombos.getAbstractModels().add( mobileCategpryOptCombo );
                }

                DataElement de = new DataElement();
                de.setId( programStagedataElement.getDataElement().getId() );
                de.setName( programStagedataElement.getDataElement().getName() );
                de.setType( programStagedataElement.getDataElement().getType() );
                de.setCategoryOptionCombos( mobileCategpryOptCombos );

                des.add( de );
            }

            prStg.setDataElements( des );

            prStgs.add( prStg );

        }

        pr.setProgramStages( prStgs );

        return pr;
    }

}
