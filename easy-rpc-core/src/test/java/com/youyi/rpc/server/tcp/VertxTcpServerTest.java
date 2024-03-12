package com.youyi.rpc.server.tcp;


/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class VertxTcpServerTest {

    public static void main(String[] args) {
        new VertxTcpServer().doStart(8888);
    }
}