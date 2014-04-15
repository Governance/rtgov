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

import static com.google.common.collect.Iterables.any;
import static com.google.common.collect.Iterables.tryFind;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.overlord.rtgov.active.collection.ActiveChangeListener;
import org.overlord.rtgov.active.collection.ActiveCollection;
import org.overlord.rtgov.active.collection.ActiveCollectionListener;
import org.overlord.rtgov.active.collection.ActiveCollectionManager;
import org.overlord.rtgov.active.collection.ActiveCollectionManagerAccessor;
import org.overlord.rtgov.active.collection.ActiveList;
import org.overlord.rtgov.active.collection.predicate.Predicate;
import org.overlord.rtgov.activity.model.ActivityType;
import org.overlord.rtgov.activity.model.ActivityTypeId;
import org.overlord.rtgov.activity.model.ActivityUnit;
import org.overlord.rtgov.activity.model.Context;
import org.overlord.rtgov.activity.model.soa.RPCActivityType;
import org.overlord.rtgov.activity.server.ActivityServer;
import org.overlord.rtgov.activity.server.ActivityStore;
import org.overlord.rtgov.activity.server.QuerySpec;
import org.overlord.rtgov.analytics.situation.Situation;
import org.overlord.rtgov.analytics.situation.store.SituationStore;
import org.overlord.rtgov.analytics.situation.store.SituationsQuery;
import org.overlord.rtgov.call.trace.CallTraceService;
import org.overlord.rtgov.call.trace.model.Call;
import org.overlord.rtgov.call.trace.model.CallTrace;
import org.overlord.rtgov.call.trace.model.Task;
import org.overlord.rtgov.call.trace.model.TraceNode;
import org.overlord.rtgov.ui.client.model.BatchRetryResult;
import org.overlord.rtgov.ui.client.model.CallTraceBean;
import org.overlord.rtgov.ui.client.model.MessageBean;
import org.overlord.rtgov.ui.client.model.ResolutionState;
import org.overlord.rtgov.ui.client.model.SituationBean;
import org.overlord.rtgov.ui.client.model.SituationEventBean;
import org.overlord.rtgov.ui.client.model.SituationSummaryBean;
import org.overlord.rtgov.ui.client.model.SituationsFilterBean;
import org.overlord.rtgov.ui.client.model.TraceNodeBean;
import org.overlord.rtgov.ui.client.model.UiException;
import org.overlord.rtgov.ui.provider.ServicesProvider;
import org.overlord.rtgov.ui.provider.SituationEventListener;
import org.overlord.rtgov.ui.provider.SituationsProvider;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;

/**
 * Concrete implementation of the faults service using RTGov situations.
 *
 */
public class RTGovSituationsProvider implements SituationsProvider, ActiveChangeListener {

	private static final String PROVIDER_NAME = "rtgov"; //$NON-NLS-1$
	
	// Active collection name
	private static final String SITUATIONS = "Situations"; //$NON-NLS-1$
	
    protected static final int MILLISECONDS_PER_DAY = 86400000;

	private static volatile Messages i18n = new Messages();
	
	@Inject
	private CallTraceService _callTraceService;

	@Inject
	private ActivityStore _activityStore;

	@Inject
	private SituationStore _situationStore;

	@Inject
	private Instance<ServicesProvider> _injectedProviders;
	
	private java.util.List<ServicesProvider> _providers=new java.util.ArrayList<ServicesProvider>();
	
	private java.util.List<SituationEventListener> _listeners=new java.util.ArrayList<SituationEventListener>();

	private ActiveList _situations;
	private ActiveCollectionManager _acmManager;
	
    /**
     * Constructor.
     */
    public RTGovSituationsProvider() {
    }
    
    /**
     * This method returns the list of services providers.
     * 
     * @return The providers
     */
    protected java.util.List<ServicesProvider> getProviders() {
    	return (_providers);
    }
    
    /**
     * This method sets the activity store.
     * 
     * @param acts The activity store
     */
    protected void setActivityStore(ActivityStore acts) {
    	_activityStore = acts;
    }
    
    /**
     * This method returns the activity store.
     * 
     * @return The activity store
     */
    protected ActivityStore getActivityStore() {
    	return (_activityStore);
    }

    /**
     * This method sets the situation store.
     * 
     * @param sits The situation store
     */
    protected void setSituationStore(SituationStore sits) {
    	_situationStore = sits;
    }
    
    /**
     * This method returns the situation store.
     * 
     * @return The situation store
     */
    protected SituationStore getSituationStore() {
    	return (_situationStore);
    }

    /**
     * This method sets the call trace service.
     * 
     * @param cts The call trace service
     */
    protected void setCallTraceService(CallTraceService cts) {
    	_callTraceService = cts;
    }
    
    /**
     * This method returns the call trace service.
     * 
     * @return The call trace service
     */
    protected CallTraceService getCallTraceService() {
    	return (_callTraceService);
    }

    /**
     * This method sets the situations active list.
     * 
     * @param cts The situations active list
     */
    protected void setSituations(ActiveList situations) {
    	_situations = situations;
    }
    
    /**
     * This method returns the situations active list.
     * 
     * @return The situations active list
     */
    protected ActiveList getSituations() {
    	return (_situations);
    }

    @PostConstruct
    public void init() {
    	
    	if (_injectedProviders != null) {
    		// Copy any injected providers into the provider list
    		for (ServicesProvider sp : _injectedProviders) {
    			_providers.add(sp);
    		}
    	}
    	
    	if (_callTraceService != null) {
    		// Overwrite any existing activity server to ensure using the
    		// same activity store as the situations provider
    		_callTraceService.setActivityServer(new ActivityServerAdapter()); 
    	}

    	if (_situations == null) {
	    	_acmManager = ActiveCollectionManagerAccessor.getActiveCollectionManager();
	
	    	_acmManager.addActiveCollectionListener(new ActiveCollectionListener() {
	
				@Override
				public void registered(ActiveCollection ac) {
					if (ac.getName().equals(SITUATIONS)) {
						synchronized (SITUATIONS) {
							if (_situations == null) {
						    	_situations = (ActiveList)ac;
						    	_situations.addActiveChangeListener(RTGovSituationsProvider.this);		
							}
						}
					}
				}
	
				@Override
				public void unregistered(ActiveCollection ac) {
				}
	    		
	    	});
    	}
    	
    	// TEMPORARY WORKAROUND: Currently hen active collection listener is registered, existing
    	// collections are not notified to the listener, thus potentially causing a situation where
    	// a collection may be missed if registered prior to the listener being established (RTGOV-286).
		synchronized (SITUATIONS) {
			if (_situations == null) {
		    	_situations = (ActiveList)_acmManager.getActiveCollection(SITUATIONS);
			}
			
	    	if (_situations != null) {
	    		_situations.addActiveChangeListener(RTGovSituationsProvider.this);	
	    	}
		}
    }

    /**
     * {@inheritDoc}
     */
	public String getName() {
		return PROVIDER_NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	public void addSituationEventListener(SituationEventListener l) {
		synchronized (_listeners) {
			_listeners.add(l);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void removeSituationEventListener(SituationEventListener l) {
		synchronized (_listeners) {
			_listeners.remove(l);
		}		
	}
	
	/**
	 * This method fires the situation event to any registered listeners.
	 * 
	 * @param event The situation event
	 */
	protected void fireSituationEvent(SituationEventBean event) {
		synchronized (_listeners) {
			for (int i=0; i < _listeners.size(); i++) {
				_listeners.get(i).onSituationEvent(event);
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
    public java.util.List<SituationSummaryBean> search(SituationsFilterBean filters) throws UiException {
        ArrayList<SituationSummaryBean> situations = new ArrayList<SituationSummaryBean>();

        try {
        	SituationsQuery query=createQuery(filters);
        	
	    	java.util.List<Situation> results=_situationStore.getSituations(query);
	
	    	for (Situation item : results) {
	        	situations.add(RTGovSituationsUtil.getSituationBean(item));
	        }
        } catch (Exception e) {
        	throw new UiException(e);
        }

        return (situations);
    }
    
    /**
     * This method creates a query from the supplied filter.
     * 
     * @param filter The filter
     * @return The query
     */
    protected static SituationsQuery createQuery(SituationsFilterBean filters) {
    	SituationsQuery ret=new SituationsQuery();

    	ret.setType(filters.getType());
    	
    	if (filters.getSeverity() != null && filters.getSeverity().trim().length() > 0) {
    		String severityName=Character.toUpperCase(filters.getSeverity().charAt(0))
    				+filters.getSeverity().substring(1);
    		ret.setSeverity(Situation.Severity.valueOf(severityName));
    	}
    	
    	if (filters.getTimestampFrom() != null) {
    		ret.setFromTimestamp(filters.getTimestampFrom().getTime());
    	}
    	
    	if (filters.getTimestampTo() != null) {
    		ret.setToTimestamp(filters.getTimestampTo().getTime());
    	}
    	ret.setDescription(filters.getDescription());
        ret.setResolutionState(filters.getResolutionState());
        ret.setSubject(filters.getSubject());
        ret.setHost(filters.getHost());

    	return (ret);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SituationBean getSituation(String situationId) throws UiException {
    	SituationBean ret=null;

    	try {
	    	Situation situation=_situationStore.getSituation(situationId);

	    	if (situation == null) {
	            throw new UiException(i18n.format("RTGovSituationsProvider.SitNotFound", situationId)); //$NON-NLS-1$
	    	}

	    	ret = RTGovSituationsUtil.getSituationBean(situation);
	    	
	    	MessageBean message = getMessage(situation);
	    	ret.setMessage(message);

	        CallTraceBean callTrace = getCallTrace(situation);
	        ret.setCallTrace(callTrace);
	        ret.setResubmitPossible(any(_providers, new IsResubmitSupported(situation)));

    	} catch (UiException uie) {
    		throw uie;

    	} catch (Exception e) {
    		throw new UiException("Failed to retrieve situation", e); //$NON-NLS-1$
    	}

    	return (ret);
    }

    /**
     * This method checks whether a request message exists for the supplied
     * situation and if so, returns a MessageBean to represent it's content.
     * 
     * @param situation The situation
     * @return The message, or null if not found
     * @throws UiException Failed to get message
     */
    protected MessageBean getMessage(Situation situation) throws UiException {
    	MessageBean ret=null;
    	
		for (ActivityTypeId id : situation.getActivityTypeIds()) {
			try {
		        ActivityType at=null;
		        
		        ActivityUnit au=_activityStore.getActivityUnit(id.getUnitId());
		        
		        if (au != null && id.getUnitIndex() < au.getActivityTypes().size()) {
		        	at = au.getActivityTypes().get(id.getUnitIndex());
		        }
    			
    			if (at instanceof RPCActivityType && ((RPCActivityType)at).isRequest()
    					&& ((RPCActivityType)at).getContent() != null) {
    				ret = new MessageBean();
    				ret.setContent(((RPCActivityType)at).getContent());
    				break;
    			}
			} catch (Exception e) {
	    		throw new UiException("Failed to get message for activity type id '"+id+"'", e);
			}
		}
		
    	return (ret);
    }
    
    /**
     * This method retrieves the call trace for the supplied situation.
     *
     * @param situation The situation
     * @return The call trace
     */
    protected CallTraceBean getCallTrace(Situation situation) throws UiException {
        CallTraceBean ret = new CallTraceBean();
        
        // Obtain call trace
        Context context=null;
        
        for (Context c : situation.getContext()) {
        	if (c.getType() == Context.Type.Conversation) {
        		context = c;
        		break;
        	}
        }
        
        if (context == null && situation.getContext().size() > 0) {
        	// If no conversation context available, then use any other
        	context = situation.getContext().iterator().next();
        }
        
        if (context != null && _callTraceService != null) {
        	try {
        		CallTrace ct=_callTraceService.createCallTrace(context);
        		
        		if (ct != null) {
        			for (TraceNode tn : ct.getTasks()) {
        				ret.getTasks().add(createTraceNode(tn));
        			}
        			
        		}
        	} catch (Exception e) {
        		throw new UiException("Failed to get call trace for context '"+context+"'", e);
        	}
        }

        return (ret);
    }
    
    /**
     * This method creates a UI bean from the supplied trace node.
     * 
     * @param node The trace node
     * @return The trace node bean
     */
    protected TraceNodeBean createTraceNode(TraceNode node) {
    	TraceNodeBean ret=new TraceNodeBean();
    	
    	ret.setType(node.getClass().getSimpleName());
    	ret.setStatus(node.getStatus().name());
    	
    	if (node instanceof Task) {
    		Task task=(Task)node;
    		
    		ret.setDescription(task.getDescription());
    		
    	} else if (node instanceof Call) {
    		Call call=(Call)node;
    		
        	ret.setIface(call.getInterface());
            ret.setOperation(call.getOperation());
            ret.setDuration(call.getDuration());
            ret.setPercentage(call.getPercentage());
            ret.setComponent(call.getComponent());
            ret.setFault(call.getFault());
            ret.setPrincipal(call.getPrincipal());
            ret.setRequest(call.getRequest());
            ret.setResponse(call.getResponse());
            ret.setRequestLatency(call.getRequestLatency());
            ret.setResponseLatency(call.getResponseLatency());
            
            ret.setProperties(call.getProperties());
        	
        	for (TraceNode child : call.getTasks()) {
				ret.getTasks().add(createTraceNode(child));
        	}
    	}
    	
    	return (ret);
    }
    
    /**
     * @see org.overlord.rtgov.ui.server.services.ISituationsServiceImpl#resubmit(java.lang.String, java.lang.String)
     */
    @Override
    public void resubmit(String situationId, MessageBean message) throws UiException {
        Situation situation=_situationStore.getSituation(situationId);
        if (situation == null) {
            throw new UiException(i18n.format("RTGovSituationsProvider.SitNotFound", situationId)); //$NON-NLS-1$
        }
        resubmitInternal(situation, message);
    }

    private void resubmitInternal(Situation situation, MessageBean message) throws UiException {
        final ServiceOperationName operationName = getServiceOperationName(situation);
        Optional<ServicesProvider> serviceProvider = tryFind(_providers, new IsResubmitSupported(
                operationName));
        if (!serviceProvider.isPresent()) {
            throw new UiException(i18n.format("RTGovSituationsProvider.ResubmitProviderNotFound", situation.getId())); //$NON-NLS-1$
        }
        try {
            // TODO: Change to specify service, rather than situation - also
            // possibly locate the provider appropriate for the service rather than situation
            serviceProvider.get().resubmit(operationName.getService(), operationName.getOperation(), message);
            _situationStore.recordSuccessfulResubmit(situation.getId());
        } catch (Exception exception) {
            _situationStore.recordResubmitFailure(situation.getId(), Throwables.getStackTraceAsString(exception));
            throw new UiException(
                    i18n.format(
                            "RTGovSituationsProvider.ResubmitFailed", situation.getId() + ":" + exception.getLocalizedMessage()), exception); //$NON-NLS-1$
        }
    }
    
    @Override
    public BatchRetryResult resubmit(SituationsFilterBean situationsFilterBean) throws UiException {
        int processedCount = 0, failedCount = 0, ignoredCount = 0;
        List<Situation> situationIdToactivityTypeIds = _situationStore
                .getSituations(createQuery(situationsFilterBean));
        for (Situation situation : situationIdToactivityTypeIds) {
            MessageBean message = getMessage(situation);
            if (message == null) {
                ignoredCount++;
                continue;
            }
            try {
                processedCount++;
                resubmitInternal(situation, message);
            } catch (UiException uiException) {
                failedCount++;
            }
        }
        return new BatchRetryResult(processedCount, failedCount, ignoredCount);
    }
    
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void inserted(Object key, Object value) {
		if (value instanceof Situation) {
			SituationEventBean event=RTGovSituationsUtil.getSituationEventBean((Situation)value);
			
			fireSituationEvent(event);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updated(Object key, Object value) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removed(Object key, Object value) {
	}

    /**
     * This method builds a list of situations, associated with the supplied filter,
     * from the 'situations' active collection.
     *
     * @param filters The filter
     * @param situations The result list
     * @throws Exception Failed to get situations list
     */
    protected void getActiveSituationList(SituationsFilterBean filters, java.util.List<SituationSummaryBean> situations) throws Exception {
        Predicate predicate=new org.overlord.rtgov.ui.provider.situations.SituationsFilterPredicate(filters);

        ActiveCollection acol=_acmManager.create(filters.toString(), _situations, predicate, null);

        for (Object item : acol) {
        	if (item instanceof Situation) {
        		situations.add(RTGovSituationsUtil.getSituationBean((Situation)item));
        	}
        }
    }

	@Override
	public void assign(String situationId,String userName) throws UiException {
		try {
			_situationStore.assignSituation(situationId, userName);
		} catch (Exception e) {
			throw new UiException(e);
		}
	}

	@Override
	public void close(String situationId) throws UiException {
		try {
			_situationStore.closeSituation(situationId);
		} catch (Exception e) {
			throw new UiException(e);
		}
	}

	@Override
	public void updateResolutionState(String situationId, ResolutionState resolutionState) throws UiException {
		try {
			_situationStore.updateResolutionState(situationId, resolutionState);
		} catch (Exception e) {
			throw new UiException(e);
		}
	}

    /**
     * This class provides a simple activity server adapter that passes requests
     * through to the activity store.
     *
     */
    protected class ActivityServerAdapter implements ActivityServer {

    	/**
    	 * {@inheritDoc}
    	 */
		@Override
		public void store(List<ActivityUnit> activities) throws Exception {
		}

    	/**
    	 * {@inheritDoc}
    	 */
		@Override
		public ActivityUnit getActivityUnit(String id) throws Exception {
			return (_activityStore.getActivityUnit(id));
		}

    	/**
    	 * {@inheritDoc}
    	 */
		@Override
		public List<ActivityType> getActivityTypes(Context context)
				throws Exception {
			return (_activityStore.getActivityTypes(context));
		}

    	/**
    	 * {@inheritDoc}
    	 */
		@Override
		public List<ActivityType> getActivityTypes(Context context, long from,
				long to) throws Exception {
			return (_activityStore.getActivityTypes(context, from, to));
		}

    	/**
    	 * {@inheritDoc}
    	 */
		@Override
		public List<ActivityType> query(QuerySpec query) throws Exception {
			return (_activityStore.query(query));
		}
    	
    }
    
    private static ServiceOperationName getServiceOperationName(Situation situation) throws UiException {
        if (situation == null) {
            throw new IllegalArgumentException("parameter 'situation' must not be null");
        }
        String parts[] = Strings.nullToEmpty(situation.getSubject()).split("\\x7C");
        if (parts.length < 2 || parts.length > 3) {
            throw new UiException(i18n.format("RTGovSituationsProvider.InvalidSubject",
                    situation.getSubject(), parts.length));
        }
        return new ServiceOperationName(parts[0], parts[1]);
    }
    
    /**
     * Predicate to test
     * {@link ServicesProvider#isResubmitSupported(String, String)}
     */
    private final class IsResubmitSupported implements com.google.common.base.Predicate<ServicesProvider> {
        private final ServiceOperationName operationName;

        private IsResubmitSupported(Situation situation) throws UiException {
            this(getServiceOperationName(situation));
        }

        private IsResubmitSupported(ServiceOperationName operationName) {
            this.operationName = operationName;
        }

        @Override
        public boolean apply(ServicesProvider input) {
            return input.isResubmitSupported(operationName.getService(), operationName.getOperation());
        }
    }
    
    /**
     * Simple value object for service and operation name.
     * 
     */
    private static class ServiceOperationName {
        private String service;
        private String operation;

        private ServiceOperationName(String service, String operation) {
            super();
            this.service = service;
            this.operation = operation;
        }

        /**
         * @return the service
         */
        public String getService() {
            return service;
        }

        /**
         * @return the operation
         */
        public String getOperation() {
            return operation;
        }

    }
}
