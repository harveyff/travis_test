package redis;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import manager.UsersManager;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class MyJedisPool {
	private static JedisPool readPool = null;
    private static JedisPool writePool = null;
    public static Logger log4j = LogManager.getLogger(MyJedisPool.class);
    //静态代码初始化池配置
    public static void init (){
        try{
            Properties props = new Properties();
            InputStream in = MyJedisPool.class.getResourceAsStream("./redis.properties");
            props.load(in);
 
            //创建jedis池配置实例
            JedisPoolConfig config = new JedisPoolConfig();
 
            //设置池配置项值
            config.setMaxTotal(Integer.valueOf(props.getProperty("redis.pool.maxActive")));
            config.setMaxIdle(Integer.valueOf(props.getProperty("redis.pool.maxIdle")));
            config.setMaxWaitMillis(Long.valueOf(props.getProperty("redis.pool.maxWait")));
            config.setTestOnBorrow(Boolean.valueOf(props.getProperty("redis.pool.testOnBorrow")));
            config.setTestOnReturn(Boolean.valueOf(props.getProperty("redis.pool.testOnReturn")));
            //根据配置实例化jedis池
            readPool = new JedisPool(config, props.getProperty("redis.ip"), Integer.valueOf(props.getProperty("redis.port")));
            writePool = new JedisPool(config, props.getProperty("redis.ip"), Integer.valueOf(props.getProperty("redis.port")));
 
        }catch (IOException e) {
           log4j.info("redis连接池异常:"+e.getMessage());
        }
    }
 
 
 
    /**获得jedis对象*/
    public static Jedis getReadJedisObject(){
        return readPool.getResource();
    }
    /**获得jedis对象*/
    public static Jedis getWriteJedisObject(){
        return writePool.getResource();
    }
 
    /**归还jedis对象*/
    public static void returnJedisOjbect(Jedis jedis){
        if (jedis != null) {
            jedis.close();
        }
    }
}
