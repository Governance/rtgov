package org.overlord.rtgov.common.service;

/**
 * Created with IntelliJ IDEA.
 * User: imk@redhat.com
 * Date: 19/03/14
 * Time: 22:29
 */
public abstract class KeyValueStore extends Service {
    public abstract <V> void add(String id, V document);

    public abstract void remove(String id);

    public abstract <V> void update(String id, V document);

    public abstract <V> void get(String id);
}
