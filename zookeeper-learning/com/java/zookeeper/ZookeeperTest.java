package com.java.zookeeper;

import org.apache.zookeeper.ZooKeeper;

public class ZookeeperTest {
	public static void main(String[] args) {
		ZooKeeper zookeeper=new ZooKeeper(connectString, sessionTimeout, watcher);
	}
}
