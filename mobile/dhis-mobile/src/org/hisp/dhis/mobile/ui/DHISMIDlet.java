package org.hisp.dhis.mobile.ui;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.DateField;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDlet;
import javax.microedition.rms.RecordStoreException;

import org.hisp.dhis.mobile.connection.DownloadManager;
import org.hisp.dhis.mobile.db.ModelRecordStore;
import org.hisp.dhis.mobile.db.SettingsRectordStore;
import org.hisp.dhis.mobile.model.AbstractModel;
import org.hisp.dhis.mobile.model.Activity;
import org.hisp.dhis.mobile.model.DataElement;
import org.hisp.dhis.mobile.model.OrgUnit;
import org.hisp.dhis.mobile.model.ProgramStageForm;
import org.hisp.dhis.mobile.model.User;
import org.hisp.dhis.mobile.util.AlertUtil;

public class DHISMIDlet
    extends MIDlet
    implements CommandListener
{

    private String serverUrl = "http://localhost:8080/api/";

    private static final String DOWNLOAD_FORM = "Download Form";

    private boolean midletPaused = false;

    private User user;

    private DownloadManager downloadManager;

    private Hashtable formElements = new Hashtable();

    private Vector programStagesVector = new Vector();

    private OrgUnit orgUnit;

    private Vector activitiesVector = new Vector();

    private ProgramStageForm programStageForm;

    private List mainMenuList;

    private List formDownloadList;

    private Form activityList;

    private Form settingsForm;

    private TextField url;

    private TextField adminPass;

    private Form dataEntryForm;

    private Form form;

    private Form loginForm;

    // add one more form to handle downloaded form list
    private List downloadedFormsList;

    private TextField userName;

    private TextField password;

    private Command exitCommand;

    private Command mnuListDnldCmd;

    private Command mnuListExtCmd;

    private Command frmDnldCmd;

    private Command frmDnldListBakCmd;

    private Command actvyPlnListBakCmd;

    private Command stngsOkCmd;

    private Command setngsBakCmd;

    private Command setngsSaveCmd;

    private Command deFrmBakCmd;

    private Command deFrmSavCmd;

    private Command screenCommand;

    private Command backCommand;

    private Command okCommand;

    private Command lgnFrmExtCmd;

    private Command lgnFrmLgnCmd;

    private Command orgUnitBackCmd;

    // add one more back command for the downloaded forms list
    private Command downloadedBckCmd;

    private Image logo;

    /**
     * The DHISMIDlet constructor.
     */
    public DHISMIDlet()
    {
    }

    /**
     * Initilizes the application. It is called only once when the MIDlet is started. The method is called before the
     * <code>startMIDlet</code> method.
     */
    private void initialize()
    {
    }

    /**
     * Performs an action assigned to the Mobile Device - MIDlet Started point.
     */
    public void startMIDlet()
    {
        new SplashScreen( getLogo(), getDisplay(), (Displayable) getLoginForm() );

    }

    /**
     * Performs an action assigned to the Mobile Device - MIDlet Resumed point.
     */
    public void resumeMIDlet()
    {
    }

    /**
     * Switches a current displayable in a display. The <code>display</code> instance is taken from
     * <code>getDisplay</code> method. This method is used by all actions in the design for switching displayable.
     * 
     * @param alert the Alert which is temporarily set to the display; if <code>null</code>, then
     *        <code>nextDisplayable</code> is set immediately
     * @param nextDisplayable the Displayable to be set
     */
    public void switchDisplayable( Alert alert, Displayable nextDisplayable )
    {

        Display display = getDisplay();
        if ( alert == null )
        {
            display.setCurrent( nextDisplayable );
        }
        else
        {
            display.setCurrent( alert, nextDisplayable );
        }
    }

    /**
     * Called by a system to indicated that a command has been invoked on a particular displayable.
     * 
     * @param command the Command that was invoked
     * @param displayable the Displayable where the command was invoked
     */
    public void commandAction( Command command, Displayable displayable )
    {

        if ( displayable == dataEntryForm )
        {
            if ( command == deFrmBakCmd )
            {
                switchDisplayable( null, getMainMenuList() );
            }
            else if ( command == deFrmSavCmd )
            {
                // need to send the recorded data
                sendRecordedData();
            }
        }
        else if ( displayable == form )
        {
            if ( command == backCommand )
            {
                switchDisplayable( null, getDownloadedFormsList() );
            }
            else if ( command == screenCommand )
            {
            }
        }
        else if ( displayable == formDownloadList )
        {
            if ( command == List.SELECT_COMMAND )
            {
                frmDnldListAction();
            }
            else if ( command == frmDnldListBakCmd )
            {
                switchDisplayable( null, getMainMenuList() );
            }
        }
        else if ( displayable == loginForm )
        {
            if ( command == lgnFrmExtCmd )
            {
                exitMIDlet();
            }
            else if ( command == lgnFrmLgnCmd )
            {
                login();
                if ( user != null )
                {
//                    DownloadManager downloadManager = new DownloadManager( this, serverUrl + "user", user, DownloadManager.DOWNLOAD_ALL );
//                    downloadManager.start();
//                    Form waitForm = new Form( "Making connection" );
//                    waitForm.append( "Please wait........" );
//                    switchDisplayable( null, waitForm );
                    switchDisplayable( null, getMainMenuList() );
                }
                else
                {
                    getDisplay().setCurrent( new Alert( "You must login" ), loginForm );
                }
            }
        }
        else if ( displayable == mainMenuList )
        {
            if ( command == List.SELECT_COMMAND )
            {
                mainMenuListAction();
            }
            else if ( command == mnuListExtCmd )
            {
                exitMIDlet();
            }
        }
        else if ( displayable == settingsForm )
        {
            if ( command == setngsBakCmd )
            {
                switchDisplayable( null, getMainMenuList() );
            }
            else if ( command == setngsSaveCmd )
            {
                // should try to save global parameters.......
                saveSettings();
                switchDisplayable( null, getMainMenuList() );
            }
        }
        else if ( displayable == activityList )
        {
            if ( command == actvyPlnListBakCmd )
            {
                switchDisplayable( null, getMainMenuList() );
            }
        }
        else if ( displayable == downloadedFormsList )
        {
            if ( command == List.SELECT_COMMAND )
            {
                this.downloadedFormsSelectAction();
            }
            else if ( command == downloadedBckCmd )
            {
                this.switchDisplayable( null, mainMenuList );
            }
        }
    }

    /**
     * Returns an initiliazed instance of exitCommand component.
     * 
     * @return the initialized component instance
     */
    public Command getExitCommand()
    {
        if ( exitCommand == null )
        {
            exitCommand = new Command( "Exit", Command.EXIT, 0 );
        }
        return exitCommand;
    }

    /**
     * Returns an initiliazed instance of mainMenuList component.
     * 
     * @return the initialized component instance
     */
    // Method to initialize the downloaded forms screen.
    public List getDownloadedFormsList()
    {
        if ( downloadedFormsList == null )
        {
            downloadedFormsList = new List( "Downloaded Forms List", Choice.IMPLICIT );
            downloadedFormsList.addCommand( this.getDownloadedFormListBckCmd() );
            downloadedFormsList.setCommandListener( this );
            downloadedFormsList.setSelectedFlags( new boolean[] {} );
        }
        return downloadedFormsList;
    }

    // Action when user select a downloaded form
    private void downloadedFormsSelectAction()
    {
        AbstractModel downloadedProgramStage = (AbstractModel) getAllForm().elementAt(
            ((List) getDownloadedFormsList()).getSelectedIndex() );
        System.out.println( "Selected ID: " + downloadedProgramStage.getId() );
        System.out.println( "Name: " + fetchForm( downloadedProgramStage.getId() ).getName() );
        this.getForm( fetchForm( downloadedProgramStage.getId() ) );
    }

    // the "Back" command of the downloaded forms list
    private Command getDownloadedFormListBckCmd()
    {
        if ( downloadedBckCmd == null )
        {
            downloadedBckCmd = new Command( "Back", Command.BACK, 0 );
        }
        return downloadedBckCmd;
    }

    // Get all Form from RMS
    public Vector getAllForm()
    {
        ModelRecordStore modelRecordStore = null;
        Vector downloadedFormVector = null;
        try
        {
            modelRecordStore = new ModelRecordStore( ModelRecordStore.FORM_DB );
            downloadedFormVector = modelRecordStore.getAllRecord();
        }
        catch ( RecordStoreException rse )
        {

        }
        return downloadedFormVector;
    }

    public void displayDownloadedForms( Vector downloadedForms )
    {
        if ( downloadedForms == null )
        {
            getDownloadedFormsList().append( "No form available", null );
        }
        else
        {
            getDownloadedFormsList().deleteAll();
            for ( int i = 0; i < downloadedForms.size(); i++ )
            {
                AbstractModel programStage = (AbstractModel) downloadedForms.elementAt( i );
                getDownloadedFormsList().insert( i, programStage.getName(), null );
            }
        }
        switchDisplayable( null, this.getDownloadedFormsList() );
    }

    public List getMainMenuList()
    {
        if ( mainMenuList == null )
        {
            mainMenuList = new List( "Menu", Choice.IMPLICIT );
            mainMenuList.append( DOWNLOAD_FORM, null );
            mainMenuList.append( "Download Activity Plan", null );
            mainMenuList.append( "Record Data", null );
            mainMenuList.append( "Settings", null );

            mainMenuList.addCommand( getMnuListExtCmd() );

            mainMenuList.setCommandListener( this );
            mainMenuList.setFitPolicy( Choice.TEXT_WRAP_DEFAULT );
            mainMenuList.setSelectedFlags( new boolean[] { false, false, false, false } );
        }
        return mainMenuList;
    }

    /**
     * Performs an action assigned to the selected list element in the mainMenuList component.
     */
    public void mainMenuListAction()
    {
        String __selectedString = getMainMenuList().getString( getMainMenuList().getSelectedIndex() );
        if ( __selectedString != null )
        {
            if ( __selectedString.equals( DOWNLOAD_FORM ) )
            {
                browseForms();
                Form waitForm = new Form( "Making connection" );
                waitForm.append( "Please wait........" );
                switchDisplayable( null, waitForm );
            }
            else if ( __selectedString.equals( "Download Activity Plan" ) )
            {
                browseActivities();
                Form waitForm = new Form( "Making connection" );
                waitForm.append( "Please wait........" );
                switchDisplayable( null, waitForm );
                System.out.println( "I will download activity plans from here" );
            }
            else if ( __selectedString.equals( "Record Data" ) )
            {
                this.displayDownloadedForms( this.getAllForm() );
            }
            else if ( __selectedString.equals( "Settings" ) )
            {
                loadSettings();
                switchDisplayable( null, getSettingsForm() );
            }
        }
    }

    /**
     * Returns an initiliazed instance of mnuListExtCmd component.
     * 
     * @return the initialized component instance
     */

    public Command getMnuListExtCmd()
    {
        if ( mnuListExtCmd == null )
        {
            mnuListExtCmd = new Command( "Exit", Command.EXIT, 0 );
        }
        return mnuListExtCmd;
    }

    /**
     * Returns an initiliazed instance of mnuListDnldCmd component.
     * 
     * @return the initialized component instance
     */
    public Command getMnuListDnldCmd()
    {
        if ( mnuListDnldCmd == null )
        {
            mnuListDnldCmd = new Command( "Download", Command.SCREEN, 0 );
        }
        return mnuListDnldCmd;
    }

    public List getFormDownloadList()
    {
        if ( formDownloadList == null )
        {
            formDownloadList = new List( "Select form to download", Choice.IMPLICIT );
            formDownloadList.addCommand( getFrmDnldListBakCmd() );
            formDownloadList.setCommandListener( this );
            formDownloadList.setSelectedFlags( new boolean[] {} );
        }
        return formDownloadList;
    }

    public void downloadActivities()
    {
        String urlDownloadActivities = orgUnit.getActivitiesLink();

        downloadManager = new DownloadManager( this, urlDownloadActivities, user, DownloadManager.DOWNLOAD_ACTIVITYPLAN );
        downloadManager.start();

        Form form = new Form( "Downloading" );
        form.append( "Please wait" );
        switchDisplayable( null, form );
    }

    /**
     * Performs an action assigned to the selected list element in the frmDnldList component.
     */
    public void frmDnldListAction()
    {
        ProgramStageForm programStage = (ProgramStageForm) programStagesVector
            .elementAt( ((List) getFormDownloadList()).getSelectedIndex() );
        downloadForm( programStage.getId() );
    }

    public Command getFrmDnldListBakCmd()
    {
        if ( frmDnldListBakCmd == null )
        {
            frmDnldListBakCmd = new Command( "Back", Command.BACK, 0 );
        }
        return frmDnldListBakCmd;
    }

    /**
     * Returns an initiliazed instance of frmDnldCmd component.
     * 
     * @return the initialized component instance
     */
    public Command getFrmDnldCmd()
    {
        if ( frmDnldCmd == null )
        {
            frmDnldCmd = new Command( "Download", Command.SCREEN, 0 );
        }
        return frmDnldCmd;
    }

    /**
     * Returns an initiliazed instance of actvyPlnListBakCmd component.
     * 
     * @return the initialized component instance
     */
    public Command getActvyPlnListBakCmd()
    {
        if ( actvyPlnListBakCmd == null )
        {
            actvyPlnListBakCmd = new Command( "Back", Command.BACK, 0 );
        }
        return actvyPlnListBakCmd;
    }

    /**
     * Returns an initiliazed instance of settingsForm component.
     * 
     * @return the initialized component instance
     */
    public Form getSettingsForm()
    {
        if ( settingsForm == null )
        {
            settingsForm = new Form( "Configurable Parameters", new Item[] { getUrl(), getAdminPass() } );
            settingsForm.addCommand( getSetngsBakCmd() );
            settingsForm.addCommand( getSetngsSaveCmd() );
            settingsForm.setCommandListener( this );
        }
        return settingsForm;
    }

    /**
     * Returns an initiliazed instance of stngsOkCmd component.
     * 
     * @return the initialized component instance
     */
    public Command getStngsOkCmd()
    {
        if ( stngsOkCmd == null )
        {
            stngsOkCmd = new Command( "Save", Command.OK, 0 );
        }
        return stngsOkCmd;
    }

    /**
     * Returns an initiliazed instance of setngsBakCmd component.
     * 
     * @return the initialized component instance
     */
    public Command getSetngsBakCmd()
    {
        if ( setngsBakCmd == null )
        {
            setngsBakCmd = new Command( "Back", Command.BACK, 0 );
        }
        return setngsBakCmd;
    }

    /**
     * Returns an initiliazed instance of setngsSaveCmd component.
     * 
     * @return the initialized component instance
     */
    public Command getSetngsSaveCmd()
    {
        if ( setngsSaveCmd == null )
        {
            setngsSaveCmd = new Command( "Save", Command.SCREEN, 0 );
        }
        return setngsSaveCmd;
    }

    /**
     * Returns an initiliazed instance of url component.
     * 
     * @return the initialized component instance
     */
    public TextField getUrl()
    {
        if ( url == null )
        {
            url = new TextField( "Server Location", serverUrl, 64, TextField.URL );
        }
        return url;
    }

    /**
     * Returns an initiliazed instance of adminPass component.
     * 
     * @return the initialized component instance
     */
    public TextField getAdminPass()
    {
        if ( adminPass == null )
        {
            adminPass = new TextField( "Admin Password", "", 32, TextField.ANY | TextField.PASSWORD );
        }
        return adminPass;
    }

    /**
     * Returns an initiliazed instance of dataEntryForm component.
     * 
     * @return the initialized component instance
     */
    public Form getDataEntryForm()
    {
        if ( dataEntryForm == null )
        {
            dataEntryForm = new Form( "form", new Item[] {} );
            dataEntryForm.addCommand( getDeFrmBakCmd() );
            dataEntryForm.addCommand( getDeFrmSavCmd() );
            dataEntryForm.setCommandListener( this );
        }
        return dataEntryForm;
    }

    /**
     * Returns an initiliazed instance of deFrmBakCmd component.
     * 
     * @return the initialized component instance
     */
    public Command getDeFrmBakCmd()
    {
        if ( deFrmBakCmd == null )
        {
            deFrmBakCmd = new Command( "Back", Command.BACK, 0 );
        }
        return deFrmBakCmd;
    }

    /**
     * Returns an initiliazed instance of deFrmSavCmd component.
     * 
     * @return the initialized component instance
     */
    public Command getDeFrmSavCmd()
    {
        if ( deFrmSavCmd == null )
        {
            deFrmSavCmd = new Command( "Save", Command.SCREEN, 0 );
        }
        return deFrmSavCmd;
    }

    /**
     * Returns an initiliazed instance of form component.
     * 
     * @return the initialized component instance
     */
    public Form getForm()
    {
        if ( form == null )
        {
            form = new Form( "form" );
            form.addCommand( getBackCommand() );
            form.addCommand( getScreenCommand() );
            form.setCommandListener( this );

            // This is just for test .....
            ProgramStageForm frm = fetchForm( 1 );
            renderForm( frm, form );
        }
        return form;
    }

    public void afterInit()
    {
        switchDisplayable( null, getMainMenuList() );
    }

    // Real downloaded forms select
    public Form getForm( ProgramStageForm selectedForm )
    {
        form = new Form( "From" );
        form.addCommand( getBackCommand() );
        form.addCommand( getScreenCommand() );
        form.setCommandListener( this );
        renderForm( selectedForm, form );

        return form;
    }

    /**
     * Returns an initiliazed instance of backCommand component.
     * 
     * @return the initialized component instance
     */
    public Command getBackCommand()
    {
        if ( backCommand == null )
        {
            backCommand = new Command( "Back", Command.BACK, 0 );
        }
        return backCommand;
    }

    /**
     * Returns an initiliazed instance of screenCommand component.
     * 
     * @return the initialized component instance
     */
    public Command getScreenCommand()
    {
        if ( screenCommand == null )
        {
            screenCommand = new Command( "Save", Command.SCREEN, 0 );
        }
        return screenCommand;
    }

    public Form getLoginForm()
    {
        if ( loginForm == null )
        {
            loginForm = new Form( "Please login", new Item[] { getUserName(), getPassword() } );
            loginForm.addCommand( getLgnFrmExtCmd() );
            loginForm.addCommand( getLgnFrmLgnCmd() );
            loginForm.setCommandListener( this );
        }
        return loginForm;
    }

    public TextField getUserName()
    {
        if ( userName == null )
        {
            userName = new TextField( "Username", "", 32, TextField.ANY | TextField.SENSITIVE );
        }
        return userName;
    }

    public TextField getPassword()
    {
        if ( password == null )
        {
            password = new TextField( "Password", null, 32, TextField.ANY | TextField.PASSWORD );
        }
        return password;
    }

    /**
     * Returns an initiliazed instance of lgnFrmExtCmd component.
     * 
     * @return the initialized component instance
     */
    public Command getLgnFrmExtCmd()
    {
        if ( lgnFrmExtCmd == null )
        {
            lgnFrmExtCmd = new Command( "Exit", Command.EXIT, 0 );
        }
        return lgnFrmExtCmd;
    }

    public Command getOkCommand()
    {
        if ( okCommand == null )
        {
            okCommand = new Command( "Ok", Command.OK, 0 );
        }
        return okCommand;
    }

    public Command getLgnFrmLgnCmd()
    {
        if ( lgnFrmLgnCmd == null )
        {
            lgnFrmLgnCmd = new Command( "Login", Command.SCREEN, 0 );
        }
        return lgnFrmLgnCmd;
    }

    public Image getLogo()
    {
        if ( logo == null )
        {
            try
            {
                logo = Image.createImage( "/logos/dhis2.png" );
            }
            catch ( java.io.IOException e )
            {
                e.printStackTrace();
            }
        }
        return logo;
    }

    public Display getDisplay()
    {
        return Display.getDisplay( this );
    }

    public void exitMIDlet()
    {
        switchDisplayable( null, null );
        destroyApp( true );
        notifyDestroyed();
    }

    /**
     * Called when MIDlet is started. Checks whether the MIDlet have been already started and initialize/starts or
     * resumes the MIDlet.
     */
    public void startApp()
    {
        if ( midletPaused )
        {
            resumeMIDlet();
        }
        else
        {
            initialize();
            startMIDlet();
        }
        midletPaused = false;
    }

    /**
     * Called when MIDlet is paused.
     */
    public void pauseApp()
    {
        midletPaused = true;
    }

    /**
     * Called to signal the MIDlet to terminate.
     * 
     * @param unconditional if true, then the MIDlet has to be unconditionally terminated and all resources has to be
     *        released.
     */
    public void destroyApp( boolean unconditional )
    {
    }

    private void login()
    {

        if ( getUserName().getString() != null && getPassword().getString() != null )
        {
            String username = getUserName().getString().trim();
            String password = getPassword().getString().trim();
            if ( username.length() != 0 && password.length() != 0 )
            {
                user = new User( username, password );
            }
        }
        // Take action based on login value
        if ( user != null )
        {
            System.out.println( "Login successfull" );

        }
        else
        {
            System.out.println( "Login failed..." );
        }
    }

    private void saveSettings()
    {

        SettingsRectordStore settingsRecord;

        try
        {
            settingsRecord = new SettingsRectordStore( "SETTINGS" );
            settingsRecord.put( "url", url.getString() );
            settingsRecord.put( "adminPass", adminPass.getString() );
            settingsRecord.save();
        }
        catch ( RecordStoreException rse )
        {
        }
    }

    private void loadSettings()
    {
        SettingsRectordStore settingsRecord;

        try
        {
            settingsRecord = new SettingsRectordStore( "SETTINGS" );

            getUrl().setString( settingsRecord.get( "url" ) );
            getAdminPass().setString( settingsRecord.get( "adminPass" ) );
        }
        catch ( RecordStoreException rse )
        {
        }
    }

    private void browseActivities()
    {
        if ( activitiesVector == null || activitiesVector.size() == 0 ) {
            downloadManager = new DownloadManager( this, serverUrl + "user", user, DownloadManager.DOWNLOAD_ORGUNIT );
            downloadManager.start();
        }
        else {
            displayCurActivities();
        }
    
    }

    private void browseForms()
    {
        loadSettings();
        downloadManager = new DownloadManager( this, serverUrl  + "forms", user, DownloadManager.DOWNLOAD_FORMS );
        downloadManager.start();
    }

    public void displayFormsForDownload( Vector forms )
    {
        programStagesVector = forms;

        if ( forms == null )
        {
            getFormDownloadList().append( "No forms available", null );
        }
        else
        {
            getFormDownloadList().deleteAll();
            for ( int i = 0; i < forms.size(); i++ )
            {
                AbstractModel programStage = (AbstractModel) forms.elementAt( i );
                getFormDownloadList().insert( i, programStage.getName(), null );
            }
        }

        switchDisplayable( null, getFormDownloadList() );
    }

    private void downloadForm( int formId )
    {
        loadSettings();
        downloadManager = new DownloadManager( this, serverUrl + "forms/" + formId , user, DownloadManager.DOWNLOAD_FORM );
        downloadManager.start();
    }

    public ProgramStageForm fetchForm( int formId )
    {
        ModelRecordStore modelRecordStore = null;
        ProgramStageForm frm = null;

        try
        {
            modelRecordStore = new ModelRecordStore( ModelRecordStore.FORM_DB );
            byte rec[] = modelRecordStore.getRecord( formId );
            if ( rec != null )
                frm = ProgramStageForm.recordToProgramStageForm( rec );
        }
        catch ( RecordStoreException rse )
        {
        }

        return frm;
    }

    public void saveActivities( Vector activitiesVector )
    {
        this.activitiesVector = activitiesVector;
        ModelRecordStore modelRecordStore = new ModelRecordStore( ModelRecordStore.ACTIVITY_DB );
        Enumeration activities = activitiesVector.elements();
        Activity activity = null;
        int i = 0;
        while ( activities.hasMoreElements() )
        {
            try
            {
                activity = (Activity) activities.nextElement();
                modelRecordStore.addRecord( Activity.activityToRecord( activity ) );
                i += 1;
            }
            catch ( RecordStoreException rse )
            {
            }
        }
    }

    public void displayCurActivities()
    {
        if ( activitiesVector == null || activitiesVector.size() == 0 )
        {
            getActivitiesList().append( "No Activity Available" );
        }
        else
        {
            getActivitiesList().deleteAll();
            for ( int i = 0; i < activitiesVector.size(); i++ )
            {
                Activity activity = (Activity) activitiesVector.elementAt( i );
                getActivitiesList().append( "\n-------\n" );
                getActivitiesList().append( "Beneficiary: " + activity.getBeneficiary().getFullName() );
                getActivitiesList().append( "Due Date: " + activity.getDueDate().toString() );
                activity = null;
            }
        }
        switchDisplayable( null, activityList );
    }

    public Form getActivitiesList()
    {
        if ( activityList == null )
        {
            activityList = new Form( "Current Activities" );
            activityList.addCommand( getActvyPlnListBakCmd() );
            activityList.setCommandListener( this );
        }
        return activityList;
    }

    public void saveForm( ProgramStageForm programStageForm )
    {
        ModelRecordStore modelRecordStore;
        try
        {
            modelRecordStore = new ModelRecordStore( ModelRecordStore.FORM_DB );
            modelRecordStore.addRecord( ProgramStageForm.programStageFormToRecord( programStageForm ) );
        }
        catch ( RecordStoreException rse )
        {
        }

        try
        {
            modelRecordStore = new ModelRecordStore( ModelRecordStore.DATAELEMENT_DB );
            modelRecordStore.AddDataElementRecords( programStageForm.getDataElements() );
        }
        catch ( RecordStoreException rse )
        {
        }

    }

    public void renderForm( ProgramStageForm prStgFrm, Form form )
    {

        programStageForm = prStgFrm;

        if ( prStgFrm == null )
        {
            form.append( "The requested form is not available" );
        }
        else
        {
            form.deleteAll();

            form.setTitle( prStgFrm.getName() );
            Vector des = prStgFrm.getDataElements();

            for ( int i = 0; i < des.size(); i++ )
            {
                DataElement de = (DataElement) des.elementAt( i );
                if ( de.getType() == DataElement.TYPE_DATE )
                {
                    DateField dateField = new DateField( de.getName(), DateField.DATE );
                    form.append( dateField );
                    formElements.put( de, dateField );
                }
                else if ( de.getType() == DataElement.TYPE_INT )
                {
                    TextField intField = new TextField( de.getName(), "", 32, TextField.NUMERIC );
                    form.append( intField );
                    formElements.put( de, intField );
                }
                else
                {
                    TextField txtField = new TextField( de.getName(), "", 32, TextField.ANY );
                    form.append( txtField );
                    formElements.put( de, txtField );
                }
            }
        }

        switchDisplayable( null, form );
    }

    public void saveOrgUnit( OrgUnit orgunit )
    {
        this.orgUnit = orgunit;
        ModelRecordStore modelRecordStore;
        try
        {
            modelRecordStore = new ModelRecordStore( ModelRecordStore.ORGUNIT_DB );
            modelRecordStore.addRecord( OrgUnit.orgUnitToRecord( orgunit ) );
        }
        catch ( RecordStoreException rse )
        {
        }
    }

    public void sendRecordedData()
    {
        System.out.println( "The form is:  " + programStageForm.getName() + "  with an ID of:  "
            + programStageForm.getId() );

        Vector des = programStageForm.getDataElements();

        for ( int i = 0; i < des.size(); i++ )
        {
            DataElement de = (DataElement) des.elementAt( i );
            if ( de.getType() == DataElement.TYPE_DATE )
            {
                DateField dateField = (DateField) formElements.get( de );
                System.out.println( de.getName() + " or  " + de.getId() + "   val   " + dateField.getDate() );
            }
            else if ( de.getType() == DataElement.TYPE_INT )
            {
                TextField intField = (TextField) formElements.get( de );
                System.out.println( de.getName() + " or  " + de.getId() + "   val   " + intField.getString() );
            }
            else
            {
                TextField txtField = (TextField) formElements.get( de );
                System.out.println( de.getName() + " or  " + de.getId() + "   val   " + txtField.getString() );
            }
        }
    }

    public void error( String error )
    {
        switchDisplayable( AlertUtil.getErrorAlert( "Problem with server", error ), getLoginForm() );
    }

    public void loginNeeded() {
        switchDisplayable( AlertUtil.getInfoAlert( "Login failed", "Username/password was wrong" ), getLoginForm());   
    }
}
