package redis;

import java.util.Set;

import com.alibaba.fastjson.JSONObject;

import redis.clients.jedis.Jedis;


public class RedisUtil {
	 /**
     * 获取hash表中所有key
     * @param name
     * @return
     */
    public static Set<String> getHashAllKey(String name){
        Jedis jedis = null;
        try {
            jedis = MyJedisPool.getReadJedisObject();
            return jedis.hkeys(name);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            MyJedisPool.returnJedisOjbect(jedis);
        }
        return null;
    }
 
    /**
     * 从redis hash表中获取
     * @param hashName
     * @param key
     * @return
     */
    public static String getHashKV(String hashName,String key){
        Jedis jedis = null;
        try {
            jedis = MyJedisPool.getReadJedisObject();
            return jedis.hget(hashName, key);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            MyJedisPool.returnJedisOjbect(jedis);
        }
        return null;
    }
 
    /**
     * 删除hash表的键值对
     * @param hashName
     * @param key
     */
    public static Long delHashKV(String hashName,String key){
        Jedis jedis = null;
        try {
            jedis = MyJedisPool.getWriteJedisObject();
            return jedis.hdel(hashName,key);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            MyJedisPool.returnJedisOjbect(jedis);
        }
        return null;
    }
 
    /**
     * 存放hash表键值对
     * @param hashName
     * @param key
     * @param value
     */
    public static Long setHashKV(String hashName,String key,String value){
        Jedis jedis = null;
        try {
            jedis = MyJedisPool.getWriteJedisObject();
            return jedis.hset(hashName,key,value);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            MyJedisPool.returnJedisOjbect(jedis);
        }
        return null;
    }
 
    /**
     * 删除键值对
     * @param k
     * @return
     */
    public static Long delKV(String k){
        Jedis jedis = null;
        try {
            jedis = MyJedisPool.getWriteJedisObject();
            return jedis.del(k);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            MyJedisPool.returnJedisOjbect(jedis);
        }
        return null;
    }
 
    /**
     * 放键值对
     * 永久
     * @param k
     * @param v
     */
    public static String setKV(String k, String v)
    {
        Jedis jedis = null;
        try {
            jedis = MyJedisPool.getWriteJedisObject();
            return jedis.set(k, v);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            MyJedisPool.returnJedisOjbect(jedis);
        }
        return null;
    }
 
 
    /**
     * 放键值对
     *
     * @param k
     * @param v
     */
    public static String setKV(String k,int second, String v)
    {
        Jedis jedis = null;
        try {
            jedis = MyJedisPool.getWriteJedisObject();
            return jedis.setex(k,second, v);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            MyJedisPool.returnJedisOjbect(jedis);
        }
        return null;
    }
 
    /**
     * 根据key取value
     *
     * @param k
     * @return
     */
    public static String getKV(String k)
    {
        Jedis jedis = null;
        try {
            jedis = MyJedisPool.getReadJedisObject();
            return jedis.get(k);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            MyJedisPool.returnJedisOjbect(jedis);
        }
        return null;
    }
    public static Long expireKV(String k,int second)
    {
        Jedis jedis = null;
        try {
            jedis = MyJedisPool.getReadJedisObject();
             return jedis.expire(k,second);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            MyJedisPool.returnJedisOjbect(jedis);
        }
        return null;
    }
    public static Long inceKV(String hashName){
        Jedis jedis = null;
        try {
            jedis = MyJedisPool.getReadJedisObject();
            return jedis.incr(hashName);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            MyJedisPool.returnJedisOjbect(jedis);
        }
        return 0L;
    }
    public static void publish(String params){
    	Jedis jedis = null;
        try {
            jedis = MyJedisPool.getReadJedisObject();
            jedis.publish("sendEmail",params);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            MyJedisPool.returnJedisOjbect(jedis);
        }
    }
}
