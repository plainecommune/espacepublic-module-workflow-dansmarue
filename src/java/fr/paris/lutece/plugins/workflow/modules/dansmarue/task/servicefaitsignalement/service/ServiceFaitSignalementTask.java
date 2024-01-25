/*
 * Copyright (c) 2002-2021, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.workflow.modules.dansmarue.task.servicefaitsignalement.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.dansmarue.business.entities.PhotoDMR;
import fr.paris.lutece.plugins.dansmarue.business.entities.Signalement;
import fr.paris.lutece.plugins.dansmarue.service.DepotManager;
import fr.paris.lutece.plugins.dansmarue.service.IPhotoService;
import fr.paris.lutece.plugins.dansmarue.service.ISignalementService;
import fr.paris.lutece.plugins.dansmarue.util.constants.SignalementConstants;
import fr.paris.lutece.plugins.dansmarue.utils.DateUtils;
import fr.paris.lutece.plugins.dansmarue.utils.ImgUtils;
import fr.paris.lutece.plugins.workflow.modules.dansmarue.task.AbstractSignalementTask;
import fr.paris.lutece.plugins.workflow.modules.dansmarue.utils.ServiceOption;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.portal.service.fileupload.FileUploadService;
import fr.paris.lutece.portal.service.image.ImageResource;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.util.image.ImageUtil;

/**
 * The Class ServiceFaitSignalementTask.
 */
public class ServiceFaitSignalementTask extends AbstractSignalementTask
{

    /** The Constant TASK_TITLE. */
    private static final String TASK_TITLE = "Service fait sur le signalement";

    /** The Constant PARAMETER_SERVICE_OPTION. */
    // PARAMETERS
    private static final String PARAMETER_SERVICE_OPTION = "serviceOption";

    /** le parameter pour le champ heureDePassage. */
    public static final String PARAMETER_HEURE_DE_PASSAGE = "heureDePassage";

    /** Le parametre pour le champ dateDePassage. */
    public static final String PARAMETER_DATE_DE_PASSAGE = "dateDePassage";

    /** Le parametre pour le champ photo SF. */
    public static final String PARAMETER_PHOTO_SF = "photoSF";

    /** Le parametre pour le champ signalement id. */
    public static final String PARAMETER_MARK_SIGNALEMENT_ID = "signalement_id";

    public static final Integer VUE_SERVICE_FAIT = 2;


    /** The signalement service. */
    // SERVICES
    @Inject
    @Named( "signalementService" )
    private ISignalementService _signalementService;

    /** The photo service. */
    @Inject
    @Named( "photoService" )
    private IPhotoService _photoService;


    /**
     * Process task.
     *
     * @param nIdResourceHistory
     *            the n id resource history
     * @param request
     *            the request
     * @param locale
     *            the locale
     */
    @Override
    public void processTask( int nIdResourceHistory, HttpServletRequest request, Locale locale )
    {
        String strServiceOption = request.getParameter( PARAMETER_SERVICE_OPTION );
        if ( StringUtils.isNotBlank( strServiceOption ) && StringUtils.isNumeric( strServiceOption ) )
        {
            int nServiceOption = Integer.parseInt( strServiceOption );
            if ( nServiceOption == ServiceOption.SIGNALE.getId( ) )
            {
                DepotManager.doCreate( request, getIdResourceFromIdHistory( nIdResourceHistory ) );
            }
            else
                if ( nServiceOption == ServiceOption.DOUBLON.getId( ) )
                {

                    _signalementService.setDoublon( getIdResourceFromIdHistory( nIdResourceHistory ) );
                }
            if ( ( nServiceOption == ServiceOption.RENDEZ_VOUS.getId( ) ) || ( nServiceOption == ServiceOption.SIGNALE.getId( ) ) )
            {
                String dateDePassage = request.getParameter( PARAMETER_DATE_DE_PASSAGE );
                String heureDePassage = request.getParameter( PARAMETER_HEURE_DE_PASSAGE );
                if ( StringUtils.isNotBlank( dateDePassage ) && StringUtils.isNotBlank( heureDePassage ) )
                {
                    _signalementService.setDateDePassage( dateDePassage, heureDePassage, (long) getIdResourceFromIdHistory( nIdResourceHistory ) );
                }

            }
        }
        else
        {
            Date dateDePassage = new Date( );
            String strDatePassage = DateUtils.getDateFr( dateDePassage );
            String strHourPassage = DateUtils.getHourWithSecondsFr( dateDePassage );
            _signalementService.setDateDePassage( strDatePassage, strHourPassage, (long) getIdResourceFromIdHistory( nIdResourceHistory ) );
        }

        try
        {
            if ( ( ( MultipartHttpServletRequest ) request ).getFile( PARAMETER_PHOTO_SF ) != null )
            {
                insertPhotoSF( Long.parseLong( request.getParameter( PARAMETER_MARK_SIGNALEMENT_ID ) ), ( ( MultipartHttpServletRequest ) request ).getFile( PARAMETER_PHOTO_SF ) );
            }
        }
        catch ( ClassCastException e )
        {
            AppLogService.info( "" );
        }
    }

    /**
     * Insert the report photo.
     *
     * @param idSignalement
     *            the signalement id
     * @param imageFile
     *            the image
     */
    private void insertPhotoSF( long idSignalement, FileItem imageFile )
    {
        String strImageName = FileUploadService.getFileNameOnly( imageFile );
        if ( StringUtils.isNotBlank( strImageName ) )
        {
            ImageResource image = new ImageResource( );
            String width = AppPropertiesService.getProperty( SignalementConstants.IMAGE_THUMBNAIL_RESIZE_WIDTH );
            String height = AppPropertiesService.getProperty( SignalementConstants.IMAGE_THUMBNAIL_RESIZE_HEIGHT );
            byte [ ] resizeImage = ImageUtil.resizeImage( imageFile.get( ), width, height, 1 );

            PhotoDMR photoSignalement = new PhotoDMR( );

            image.setImage( imageFile.get( ) );
            String mimeType = imageFile.getContentType( ).replace( "pjpeg", "jpeg" );
            mimeType = mimeType.replace( "x-png", "png" );
            image.setMimeType( mimeType );

            photoSignalement.setImage( image );
            photoSignalement.setImageContent( ImgUtils.checkQuality( image.getImage( ) ) );
            photoSignalement.setImageThumbnailWithBytes( resizeImage );

            Signalement signalement = new Signalement( );
            signalement.setId( idSignalement );

            photoSignalement.setSignalement( signalement );
            photoSignalement.setVue( VUE_SERVICE_FAIT  );
            SimpleDateFormat sdfDate = new SimpleDateFormat( "dd/MM/yyyy" );
            photoSignalement.setDate( sdfDate.format( Calendar.getInstance( ).getTime( ) ) );

            // creation of the image in the db linked to the report
            _photoService.insert( photoSignalement );
        }
    }


    /**
     * Gets the title.
     *
     * @param locale
     *            the locale
     * @return the title
     */
    @Override
    public String getTitle( Locale locale )
    {
        return TASK_TITLE;
    }

    /**
     * Get the id resource from a given id history.
     *
     * @param nIdHistory
     *            the id history
     * @return the id resource
     */
    private int getIdResourceFromIdHistory( int nIdHistory )
    {
        ResourceHistory history = _resourceHistoryService.findByPrimaryKey( nIdHistory );
        if ( history != null )
        {
            return history.getIdResource( );
        }
        return -1;
    }

    /**
     * Gets the task form entries.
     *
     * @param locale
     *            the locale
     * @return the task form entries
     */
    @Override
    public Map<String, String> getTaskFormEntries( Locale locale )
    {
        return null;
    }
}
