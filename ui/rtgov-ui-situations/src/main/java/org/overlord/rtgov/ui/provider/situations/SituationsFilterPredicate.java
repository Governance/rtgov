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
package org.overlord.rtgov.ui.provider.situations;

import org.overlord.rtgov.active.collection.ActiveCollectionContext;
import org.overlord.rtgov.active.collection.predicate.Predicate;
import org.overlord.rtgov.analytics.situation.Situation;
import org.overlord.rtgov.ui.client.model.SituationsFilterBean;

/**
 * This class provides the 'situations filter' based predicate implementation.
 *
 */
public class SituationsFilterPredicate extends Predicate {

	private SituationsFilterBean _filter=null;

	/**
	 * The situations filter predicate constructor.
	 *
	 * @param filter The filter
	 */
	public SituationsFilterPredicate(SituationsFilterBean filter) {
		_filter = filter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean evaluate(ActiveCollectionContext context, Object item) {

		if (item instanceof Situation) {
			Situation situation=(Situation)item;

			if (_filter != null) {
				// Check severity
				if (_filter.getSeverity() != null
						&& _filter.getSeverity().trim().length() > 0
						&& !_filter.getSeverity().equalsIgnoreCase(situation.getSeverity().name())) {
					return (false);
				}

				// Check type
				if (_filter.getType() != null
						&& _filter.getType().trim().length() > 0
						&& !_filter.getType().equals(situation.getType())) {
					return (false);
				}

				// Check start date/time
				if (_filter.getTimestampFrom() != null
						&& situation.getTimestamp() < _filter.getTimestampFrom().getTime()) {
					return (false);
				}

				// Check end date/time
				if (_filter.getTimestampTo() != null
						&& situation.getTimestamp() > _filter.getTimestampTo().getTime()) {
					return (false);
				}
			}

			return (true);
		}

		return (false);
	}

}

