/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.overlord.rtgov.analytics.situation.store;

import org.overlord.rtgov.analytics.situation.Situation;

/**
 * This class provides identifies the situations query criteria.
 *
 */
public class SituationsQuery {

    private String _type;
    private String _description;
	private String _subject;
	private Situation.Severity _severity;
	private long _fromTimestamp;
	private long _toTimestamp;
    private String _resolutionState;
    private String _host;
	
	/**
	 * The default constructor.
	 */
	public SituationsQuery() {
	}
	
	/**
	 * This method returns the type.
	 * 
	 * @return The type
	 */
	public String getType() {
		return (_type);
	}
	
	/**
	 * This method sets the type.
	 * 
	 * @param type The type
	 */
	public void setType(String type) {
		_type = type;
	}
	
	/**
	 * This method returns the subject.
	 * 
	 * @return The subject
	 */
	public String getSubject() {
		return (_subject);
	}
	
	/**
	 * This method sets the subject.
	 * 
	 * @param subject The subject
	 */
	public void setSubject(String subject) {
		_subject = subject;
	}
	
	/**
	 * This method returns the severity.
	 * 
	 * @return The severity
	 */
	public Situation.Severity getSeverity() {
		return (_severity);
	}
	
	/**
	 * This method sets the severity.
	 * 
	 * @param severity The severity
	 */
	public void setSeverity(Situation.Severity severity) {
		_severity = severity;
	}
	
	/**
	 * This method returns the from timestamp.
	 * 
	 * @return The from timestamp
	 */
	public long getFromTimestamp() {
		return (_fromTimestamp);
	}
	
	/**
	 * This method sets the from timestamp.
	 * 
	 * @param fromTimestamp The from timestamp
	 */
	public void setFromTimestamp(long fromTimestamp) {
		_fromTimestamp = fromTimestamp;
	}
	
	/**
	 * This method returns the to timestamp.
	 * 
	 * @return The to timestamp
	 */
	public long getToTimestamp() {
		return (_toTimestamp);
	}
	
	/**
	 * This method sets the to timestamp.
	 * 
	 * @param toTimestamp The to timestamp
	 */
	public void setToTimestamp(long toTimestamp) {
		_toTimestamp = toTimestamp;
	}
	
    /**
     * This method returns the resolution state.
     * 
	 * @return The resolution state
	 */
	public String getResolutionState() {
		return (_resolutionState);
	}

	/**
	 * This method sets the resolution state.
	 * 
	 * @param resolutionState The resolution state
	 */
	public void setResolutionState(String resolutionState) {
		_resolutionState = resolutionState;
	}

    /**
     * @return the _description
     */
    public String getDescription() {
        return _description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this._description = description;
    }

    /**
     * @return the host
     */
    public String getHost() {
        return _host;
    }

    /**
     * @param host the host to set
     */
    public void setHost(String host) {
        this._host = host;
    }
	
}
