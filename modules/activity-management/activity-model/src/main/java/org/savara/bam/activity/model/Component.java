/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-11, Red Hat Middleware LLC, and others contributors as indicated
 * by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package org.savara.bam.activity.model;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * This class represents information about the component that is associated
 * with the activity.
 *
 */
public class Component implements java.io.Externalizable {

    private static final int VERSION = 1;

    private String _service=null;
	private String _processDefinition=null;
	private String _processInstance=null;
	private String _task=null;
	
	public Component() {
	}
	
	public Component(Component comp) {
		_service = comp._service;
		_processDefinition = comp._processDefinition;
		_processInstance = comp._processInstance;
		_task = comp._task;
	}
	
	public void setService(String service) {
		_service = service;
	}
	
	public String getService() {
		return (_service);
	}
	
	public void setProcessDefinition(String pd) {
		_processDefinition = pd;
	}
	
	public String getProcessDefinition() {
		return (_processDefinition);
	}
	
	public void setProcessInstance(String pi) {
		_processInstance = pi;
	}
	
	public String getProcessInstance() {
		return (_processInstance);
	}
	
	public void setTask(String task) {
		_task = task;
	}
	
	public String getTask() {
		return (_task);
	}

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(VERSION);
        
        out.writeUTF(_service);
        out.writeUTF(_processDefinition);
        out.writeUTF(_processInstance);
        out.writeUTF(_task);
    }

    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        in.readInt(); // Consume version, as not required for now
        
        _service = in.readUTF();
        _processDefinition = in.readUTF();
        _processInstance = in.readUTF();
        _task = in.readUTF();
    }
}
