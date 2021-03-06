/*******************************************************************************
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 *******************************************************************************/

package com.liferay.ide.layouttpl.core.model;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.layouttpl.core.util.LayoutTplUtil;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public class PortletLayoutElement extends ModelElement implements PropertyChangeListener
{

    public static final String CHILD_COLUMN_WEIGHT_CHANGED_PROP = "PortletLayout.ChildColumnWeightChanged"; //$NON-NLS-1$

    public static final String COLUMN_ADDED_PROP = "PortletLayout.ColumnAdded"; //$NON-NLS-1$

    public static final String COLUMN_REMOVED_PROP = "PortletLayout.ColumnRemoved"; //$NON-NLS-1$

    public static PortletLayoutElement createFromElement( IDOMElement portletLayoutElement, ILayoutTplDiagramFactory factory )
    {
        if( portletLayoutElement == null )
        {
            return null;
        }

        PortletLayoutElement newPortletLayout = factory.newPortletLayout();

        String existingClassName = portletLayoutElement.getAttribute( "class" ); //$NON-NLS-1$

        if( ( !CoreUtil.isNullOrEmpty( existingClassName ) ) && existingClassName.contains( "portlet-layout" ) ) //$NON-NLS-1$
        {
            newPortletLayout.setClassName( existingClassName );
        }
        else
        {
            newPortletLayout.setClassName( "portlet-layout" ); //$NON-NLS-1$
        }

        IDOMElement[] portletColumnElements =
            LayoutTplUtil.findChildElementsByClassName( portletLayoutElement, "div", "portlet-column" ); //$NON-NLS-1$ //$NON-NLS-2$

        for( IDOMElement portletColumnElement : portletColumnElements )
        {
            PortletColumnElement newPortletColumn = factory.newPortletColumnFromElement( portletColumnElement );
            newPortletLayout.addColumn( newPortletColumn );
        }

        return newPortletLayout;
    }

    protected String className;

    protected List<ModelElement> columns = new ArrayList<ModelElement>();

    public PortletLayoutElement()
    {
        super();
        this.className = "portlet-layout"; //$NON-NLS-1$
    }

    public boolean addColumn( PortletColumnElement newColumn )
    {
        return addColumn( newColumn, -1 );
    }

    public boolean addColumn( PortletColumnElement newColumn, int index )
    {
        if( newColumn != null )
        {
            if( index < 0 )
            {
                columns.add( newColumn );
            }
            else
            {
                columns.add( index, newColumn );
            }

            newColumn.setParent( this );
            newColumn.addPropertyChangeListener( this );

            firePropertyChange( COLUMN_ADDED_PROP, null, newColumn );

            return true;
        }

        return false;
    }

    public String getClassName()
    {
        return className;
    }

    public List<ModelElement> getColumns()
    {
        return columns;
    }

    public void propertyChange( PropertyChangeEvent evt )
    {
        String prop = evt.getPropertyName();

        if( PortletColumnElement.WEIGHT_PROP.equals( prop ) )
        {
            firePropertyChange( CHILD_COLUMN_WEIGHT_CHANGED_PROP, null, evt.getSource() );
        }
    }

    @Override
    public void removeChild( ModelElement child )
    {
        if( columns.contains( child ) )
        {
            removeColumn( (PortletColumnElement) child );
        }
    }

    public boolean removeColumn( PortletColumnElement existingColumn )
    {
        if( existingColumn != null && columns.remove( existingColumn ) )
        {
            firePropertyChange( COLUMN_REMOVED_PROP, null, existingColumn );

            return true;
        }

        return false;
    }

    public void setClassName( String className )
    {
        this.className = className;
    }

}
