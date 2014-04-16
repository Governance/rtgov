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
package org.overlord.rtgov.ui.client.local.services.rpc;

import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Remote;
import org.overlord.rtgov.ui.client.local.services.NotificationService;


/**
 * An async handler interface for making service invocations.
 *
 * @author eric.wittmann@redhat.com
 */
public interface IRpcServiceInvocationHandler<T> {

    /**
     * Called when the RPC call successfully returns data.
     * @param data
     */
    public void onReturn(T data);

    /**
     * Called when the RPC call fails.
     * @param error
     */
    public void onError(Throwable error);
    
    /**
     * The result of IRpcServiceInvocationHandler call's.
     */
    interface RpcResult<T> {

        /**
         * 
         * @return the Data of the Rpc Call or null in case of
         *         {@link #isError()}
         */
        T getData();

        /**
         * 
         * @return the Error of the Rpc Call or null if successful
         */
        Throwable getError();

        /**
         * 
         * @return true in case of any Error
         */
        boolean isError();

        /**
         * The default implementation for RPC Result's.
         * 
         * @param <T>
         *            The expected Data Type.
         */
        class DefaultResult<T> implements RpcResult<T> {
            private T data;
            private Throwable error;

            public DefaultResult(T data, Throwable error) {
                super();
                this.data = data;
                this.error = error;
            }

            @Override
            public T getData() {
                return data;
            }

            @Override
            public Throwable getError() {
                return error;
            }

            @Override
            public boolean isError() {
                return getError() != null;
            }
        }
    }

    /**
     * 
     * Convenience {@link IRpcServiceInvocationHandler Adapter} implementation
     * providing empty stubs for {@link #onReturn(Object)},
     * {@link #onError(Throwable)} and {@link #doOnComplete(RpcResult)} for a
     * consolidated response handling of success and error cases.
     * 
     * @param <T>
     */
    class RpcServiceInvocationHandlerAdapter<T> implements IRpcServiceInvocationHandler<T> {

        @Override
        public void onReturn(T data) {
            try {
                doOnReturn(data);
            } finally {
                doOnComplete(new RpcResult.DefaultResult<T>(data, null));
            }
        }

        /**
         * Empty stub implementation of
         * {@link IRpcServiceInvocationHandler#onReturn(Object)}
         * 
         * @param data
         */
        public void doOnReturn(T data) {
        }

        @Override
        public void onError(Throwable error) {
            try {
                doOnError(error);
            } finally {
                doOnComplete(new RpcResult.DefaultResult<T>(null, error));
            }
        }

        /**
         * Empty stub implementation of
         * {@link IRpcServiceInvocationHandler#onError(Throwable)}
         * 
         * @param error
         */
        public void doOnError(Throwable error) {
        }

        /**
         * Called when the RPC call complete's.
         * 
         * @param result
         *            The result of the RPC Call
         */
        public void doOnComplete(RpcResult<T> result) {
        }

    }

    /**
     * Convenience implementation of {@link IRpcServiceInvocationHandler} for
     * {@link Remote} method invocation's without the requirement of return
     * (reponse) type handling.
     * 
     */
	class VoidInvocationHandler implements IRpcServiceInvocationHandler<Void> {
		@Inject
		private NotificationService notificationService;
		private String title;

		public VoidInvocationHandler() {
			super();
		}

		public VoidInvocationHandler(String title) {
			super();
			this.title = title;
		}

		@Override
		public void onReturn(Void data) {
		}

		@Override
		public void onError(Throwable error) {
			notificationService.sendErrorNotification(title, error);
		}

	}



}
