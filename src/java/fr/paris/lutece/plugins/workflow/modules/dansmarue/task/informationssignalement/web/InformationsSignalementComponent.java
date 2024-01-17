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
package fr.paris.lutece.plugins.workflow.modules.dansmarue.task.informationssignalement.web;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.dansmarue.business.entities.Adresse;
import fr.paris.lutece.plugins.dansmarue.business.entities.PhotoDMR;
import fr.paris.lutece.plugins.dansmarue.business.entities.Signalement;
import fr.paris.lutece.plugins.dansmarue.business.entities.Signaleur;
import fr.paris.lutece.plugins.dansmarue.service.IAdresseService;
import fr.paris.lutece.plugins.dansmarue.service.IPhotoService;
import fr.paris.lutece.plugins.dansmarue.service.ISignalementService;
import fr.paris.lutece.plugins.dansmarue.service.ISignaleurService;
import fr.paris.lutece.plugins.workflow.web.task.NoConfigTaskComponent;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.service.action.IActionService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.html.HtmlTemplate;

/**
 * The notification component.
 */
public class InformationsSignalementComponent extends NoConfigTaskComponent
{

    /** The Constant MARK_PHOTOS. */
    // MARKERS
    private static final String MARK_PHOTOS = "photos";

    /** The Constant MARK_ADRESSE. */
    private static final String MARK_ADRESSE = "adresse";

    /** The Constant MARK_SIGNALEMENT. */
    private static final String MARK_SIGNALEMENT = "signalement";

    /** The Constant MARK_SIGNALEUR. */
    private static final String MARK_SIGNALEUR = "signaleur";

    /** The Constant MARK_IS_ACTION_SF. */
    private static final String MARK_IS_ACTION_SF = "isActionSF";

    // PARAMETERS

    // MESSAGES

    /** The Constant TEMPLATE_TASK_INFORMATIONS_SIGNALEMENT_FORM. */
    // TEMPLATES
    private static final String TEMPLATE_TASK_INFORMATIONS_SIGNALEMENT_FORM = "admin/plugins/workflow/modules/signalement/task_informations_signalement_form.html";

    // CONSTANTS

    // JSP

    /** The signalement service. */
    // SERVICES
    @Inject
    @Named( "signalementService" )
    private ISignalementService _signalementService;

    /** The adresse service. */
    @Inject
    @Named( "adresseSignalementService" )
    private IAdresseService _adresseService;

    /** The signaleur service. */
    @Inject
    @Named( "signaleurService" )
    private ISignaleurService _signaleurService;

    /** The photo service. */
    @Inject
    @Named( "photoService" )
    private IPhotoService _photoService;

    @Inject
    @Named( "workflow.actionService" )
    private IActionService _actionService;

    /**
     * Gets the display task form.
     *
     * @param nIdResource
     *            the n id resource
     * @param strResourceType
     *            the str resource type
     * @param request
     *            the request
     * @param locale
     *            the locale
     * @param task
     *            the task
     * @return the display task form
     */
    @Override
    public String getDisplayTaskForm( int nIdResource, String strResourceType, HttpServletRequest request, Locale locale, ITask task )
    {
        Map<String, Object> model = new HashMap<>( );

        Adresse adresse = null;
        Signaleur signaleur = null;
        List<PhotoDMR> photos = null;
        Signalement signalement = _signalementService.getSignalement( nIdResource );

        adresse = _adresseService.loadByIdSignalement( nIdResource );
        signaleur = _signaleurService.loadByIdSignalement( nIdResource );
        photos = _photoService.findBySignalementId( nIdResource );
        Action action = _actionService.findByPrimaryKey( task.getAction( ).getId( ) );
        int idStatutServiceFait = AppPropertiesService.getPropertyInt( "signalement.idStateServiceFait", -1 );

        model.put( MARK_IS_ACTION_SF, action.getStateAfter( ).getId( ) == idStatutServiceFait );
        model.put( MARK_SIGNALEMENT, signalement );
        model.put( MARK_PHOTOS, photos );
        model.put( MARK_ADRESSE, adresse );
        model.put( MARK_SIGNALEUR, signaleur );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_INFORMATIONS_SIGNALEMENT_FORM, locale, model );
        return template.getHtml( );
    }

    /**
     * Gets the display task information.
     *
     * @param nIdHistory
     *            the n id history
     * @param request
     *            the request
     * @param locale
     *            the locale
     * @param task
     *            the task
     * @return the display task information
     */
    @Override
    public String getDisplayTaskInformation( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        return null;
    }

    /**
     * Gets the task information xml.
     *
     * @param nIdHistory
     *            the n id history
     * @param request
     *            the request
     * @param locale
     *            the locale
     * @param task
     *            the task
     * @return the task information xml
     */
    @Override
    public String getTaskInformationXml( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        return null;
    }

    /**
     * Do validate task.
     *
     * @param nIdResource
     *            the n id resource
     * @param strResourceType
     *            the str resource type
     * @param request
     *            the request
     * @param locale
     *            the locale
     * @param task
     *            the task
     * @return the string
     */
    @Override
    public String doValidateTask( int nIdResource, String strResourceType, HttpServletRequest request, Locale locale, ITask task )
    {
        return null;
    }

}
