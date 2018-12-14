package redis;

import redis.clients.jedis.Jedis;

public class TestRedis {

	public static void main(String[] args) {
		Jedis jedis = new Jedis("localhost");
		jedis.set("foo", "bar");
		String value = jedis.get("foo");
		System.out.println(value);

	}

}
