/**
 * Copyright (C) 2011  jtalks.org Team
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * Also add information on how to contact you by electronic and paper mail.
 * Creation date: Apr 12, 2011 / 8:05:19 PM
 * The jtalks.org Project
 */
package org.jtalks.poulpe.web.controller;

import java.util.List;


/**
 * The class which manages actions and represents information about component
 * displayed in administrator panel.
 * 
 * @author Dmitriy Sukharev
 * 
 */
public class ComponentViewListItem implements ComponentView {

    private long cid;
    private String name;
    private String description;
    private String componentType;

    /** {@inheritDoc} */
    @Override
    public String getDescription() {
        return description;
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return name;
    }

    /** {@inheritDoc} */
    @Override
    public String getComponentType() {
        return componentType;
    }

    /** {@inheritDoc} */
    @Override
    public long getCid() {
        return cid;
    }

    /** {@inheritDoc} */
    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    /** {@inheritDoc} */
    @Override
    public void setName(String compName) {
        this.name = compName;
    }

    /** {@inheritDoc} */
    @Override
    public void setComponentType(String type) {
        this.componentType = type;
    }

    /** {@inheritDoc} */
    @Override
    public void setCid(long cid) {
        this.cid = cid;
    }

    @Override
    public void updateList(List<ComponentView> list) {
        // TODO Maybe create additional interface instead. (?)
        throw new UnsupportedOperationException();
    }

    @Override
    public void showEditWindow(ComponentView component) {
        throw new UnsupportedOperationException();
    }

}