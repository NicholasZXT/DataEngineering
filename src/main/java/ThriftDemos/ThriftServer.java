package ThriftDemos;

import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;

import ThriftDemos.helloThrift.service.UserService;
import ThriftDemos.UserServiceImpl;

import java.net.InetSocketAddress;

public class ThriftServer {
    private static final int SERVER_PORT = 8765;

    public static void main(String[] args) {
        try {

            // 1. 创建Transport
            InetSocketAddress address = new InetSocketAddress("localhost", SERVER_PORT);
            TServerSocket serverTransport = new TServerSocket(address);
            TServer.Args tArgs = new TServer.Args(serverTransport);

            // 2. 为Transport创建Protocol
            tArgs.protocolFactory(new TBinaryProtocol.Factory());
            // tArgs.protocolFactory(new TCompactProtocol.Factory());
            // tArgs.protocolFactory(new TJSONProtocol.Factory());

            // 3. 为Protocol创建Processor
            TProcessor tprocessor = new UserService.Processor<UserService.Iface>(new UserServiceImpl());
            tArgs.processor(tprocessor);

            // 4. 创建Server并启动
            // org.apache.thrift.server.TSimpleServer - 简单的单线程服务模型，一般用于测试
            TServer server = new TSimpleServer(tArgs);
            System.out.println("UserService TSimpleServer start ....");
            server.serve();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
