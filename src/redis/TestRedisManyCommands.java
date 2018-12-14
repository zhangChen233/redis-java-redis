package redis;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class TestRedisManyCommands {
	JedisPool pool;
	Jedis jedis;
	
	@Before
	public void setUp(){
		jedis = new Jedis("localhost");
	}
	/**
	 * Redis 存储初级的字符串
	 * 	CRUD
	 */
	
	@Test
	public void testBasicString(){
		//添加数据
		jedis.set("name", "meepo");
		System.out.println("第一次向String类型添加数据"+jedis.get("name"));
		
		//修改数据
		//1.在原有的基础上修改
		jedis.append("name", "dota");//添加到数据的后面
		System.out.println("在原有数据上进行修改操作，在后面添加"+jedis.get("name"));
		
		//2.覆盖原来的数据
		jedis.set("name", "poofu");
		System.out.println("覆盖原来的内容"+jedis.get("name"));
		
		//3.删除key对应的记录
		jedis.del("name");
		System.out.println(jedis.get("name"));
		/**
		 * mset相当于
		 * jedis.set("name","meepo");
		 * jedis.set("dota","poofu");
		 */
		jedis.mset("name","meepo","dota","poofu");
		System.out.println(jedis.mget("name","dota"));
	}
	/**
	 * redis 对map的测试
	 */
	@Test
	public void testMap(){
		Map<String,String> user = new HashMap<String,String>();
		user.put("name", "meepo");
		user.put("pwd", "password");
		jedis.hmset("user", user);
		
		//取出user的name，结果是一个泛型的list
		//第一个参数是存入redis中map对象的key，后面跟的是放入map中的对象的key，后面的key可以跟多个，是可变参数
		List<String> rsmap = jedis.hmget("user", "name");
		System.out.println("map的结果"+rsmap);
		
		//jedis.hdel("user", "pwd");
		System.out.println(jedis.hmget("user", "pwd"));
		System.out.println("放回key为uers的键中存放值的个数"+jedis.hlen("user"));
		System.out.println("是否存在key为user的记录"+jedis.exists("user"));
		System.out.println("返回map对象中所有的key"+jedis.hkeys("user"));
		System.out.println("返回map对象中所有的value"+jedis.hvals("user"));
		
		Iterator<String> iter = jedis.hkeys("user").iterator();
		while(iter.hasNext()){
			String key = iter.next();
			System.out.println(key+":"+jedis.hmget("user", key));
		}
	}
	/**
	 * jedis操作List
	 */
	@Test
	public void testList(){
		//开始之前，先移除所有的内容
		jedis.del("java framework");
		//第一个是key，第二个是起始位置，第三个是结束位置，jedis.llen获取长度 -1表示获取所有
		System.out.println(jedis.lrange("java framework", 0, -1));
		// 先放入三条数据java framework，在前方放入 lpush
		jedis.lpush("java framework", "spring");
		jedis.lpush("java framework", "struts");
		jedis.lpush("java framework", "hibernate");
		//再取出所有的数据
		//第一个是key，第二个是起始位置，第三个是结束位置，jedis.llen获取长度 -1表示获取所有
				System.out.println(jedis.lrange("java framework", 0, -1));
	}
	
	/**
	 * jedis操作set
	 */
	@Test
	public void testSet(){
		//添加
		jedis.sadd("sname", "meepo1");
		jedis.sadd("sname", "dota1");
		jedis.sadd("sname", "poofu1");
		jedis.sadd("sname", "noname1");
		//移除noname
		//jedis.srem("sname","noname");
		//以下内容已经存入但是无法输出
		
		System.out.println("获取所有加入的value"+jedis.smembers("sname"));
		System.out.println("判断meepo是否是sname的元素"+jedis.sismember("sname", "meepo1"));
		System.out.println(jedis.srandmember("sname"));
		System.out.println("返回集合的元素个数"+jedis.scard("sname"));
		

	}
	
	@Test
	public void test() throws InterruptedException{
		//keys中可以传入通配符
		System.out.println(jedis.keys("*"));
		System.out.println(jedis.keys("*name"));
		System.out.println("删除key为sanmdde的对象，如果成功返回1，如果失败或者不存在返回0"+jedis.del("sanmdde"));
		System.out.println("返回指定key存活的有效时间，时间为秒"+jedis.ttl("sname"));
		
		jedis.setex("timekey", 10, "min");//指定key存活的有效时间为秒
		Thread.sleep(5000);//睡眠5秒以后，剩余时间将为《=5
		System.out.println("timekey有效的存活时间"+jedis.ttl("timekey"));
		
		jedis.setex("timekey", 1, "min");//设定为1后，再看剩余时间就是1
		System.out.println(jedis.ttl("timekey"));
		System.out.println("检查key 是否存在"+jedis.exists("key"));
		System.out.println(jedis.rename("timekey", "time"));
		System.out.println(jedis.get("timekey"));
		System.out.println(jedis.get("time"));
		//jedis 排序操作
		//注意，此处的rpush和lpush是list的操作，是一个双向链表
		jedis.del("a");
		jedis.rpush("a", "1");
		jedis.lpush("a", "6");
		jedis.lpush("a", "3");
		jedis.lpush("a", "9");
		System.out.println(jedis.lrange("a", 0, -1));
		System.out.println(jedis.sort("a"));
		System.out.println(jedis.lrange("a", 0, -1));
	}

}
