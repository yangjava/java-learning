package com.zookeeper.test;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.junit.Test;

public class ZookeeperTest {
	@Test
	public void testZk1() throws Exception {
		ZooKeeper zk = new ZooKeeper("127.0.0.1:2181", 60000, new Watcher() { // 监控所有被触发的事件
					// //
					// 当对目录节点监控状态打开时，一旦目录节点的状态发生变化，Watcher 对象的 process 方法就会被调用。
					public void process(WatchedEvent event) {
						System.out.println("EVENT:" + event.getType());
					}

				});
		System.out.println(zk);
		System.out.println("ls / => " + zk.getChildren("/", true));
	}
	@Test
	public void testZk2() throws Exception{
		ZooKeeper zk = new ZooKeeper("127.0.0.1:2181", 5000, new Watcher() {
			CountDownLatch down = new CountDownLatch(1);// 同步阻塞状态

			@Override
			public void process(WatchedEvent event) {
				if (event.getState() == Event.KeeperState.SyncConnected) {
					down.countDown();// 连接上之后，释放计数器
				}

			}
		});

		System.out.println("连接成功：" + zk.toString());
		System.out.println("ls / => " + zk.getChildren("/", true));
//		zk.close();// 关闭连接
	}
	
	public static void main(String[] args) throws Exception {
		ZooKeeper zk = new ZooKeeper("127.0.0.1:2181", 5000, new Watcher() {
			CountDownLatch down = new CountDownLatch(1);// 同步阻塞状态

			@Override
			public void process(WatchedEvent event) {
				if (event.getState() == Event.KeeperState.SyncConnected) {
					down.countDown();// 连接上之后，释放计数器
				}

			}
		});

		System.out.println("连接成功：" + zk.toString());
		System.out.println("ls / => " + zk.getChildren("/", true));
	}


}
