
package io.collective;

import java.time.Clock;
import java.util.concurrent.ConcurrentHashMap;


public class SimpleAgedCache {
    private final Clock clock;
    private final ConcurrentHashMap<Object, CacheEntry> cacheMap;
    public SimpleAgedCache(Clock clock) {
        this.clock=clock;
        cacheMap=new ConcurrentHashMap<>();
    }

    public SimpleAgedCache() {
        this(Clock.systemDefaultZone());
    }

    public void put(Object key, Object value, int retentionInMillis) {
        if(key!=null && retentionInMillis>0){
            long expirationTime= this.clock.millis()+retentionInMillis;
            cacheMap.put(key,new CacheEntry(value, expirationTime));
        }
    }
    public boolean isEmpty() {
        return cacheMap.isEmpty();
    }

    public int size() {
        cleanExpiredRecords();
        return cacheMap.size();
    }

    public Object get(Object key) {
        cleanExpiredRecords();
        CacheEntry entry = cacheMap.get(key);
        return( entry!=null && !entry.isExpired()? entry.value: null);
    }

    private void cleanExpiredRecords() {
        cacheMap.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }

    private class CacheEntry {
       Object value;
       long expirationTime;

       CacheEntry(Object value, long expirationTime){
           this.value=value;
           this.expirationTime=expirationTime;
       }
       boolean isExpired(){
           return clock.millis()>=expirationTime;
       }
    }
}



