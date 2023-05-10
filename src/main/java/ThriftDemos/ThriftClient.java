package ThriftDemos;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import java.util.List;

import ThriftDemos.helloThrift.service.UserService;
import ThriftDemos.helloThrift.domain.User;
import ThriftDemos.helloThrift.exception.UserNotFoundException;


public class ThriftClient {
    private static final int SERVER_PORT = 8765;

    public static void main(String[] args) throws TTransportException {
        try {
            TTransport transport = new TSocket("localhost", SERVER_PORT);
            TProtocol protocol = new TBinaryProtocol(transport);
            UserService.Client client = new UserService.Client(protocol);
            transport.open();

            // 业务方法
            System.out.println("------ hello -------");
            System.out.println(client.hello());
            System.out.println("------ listUser -------");
            List<User> users = client.listUser();
            for(User user: users)
                System.out.println(user);
            System.out.println("------ save -------");
            User u1 = new User(101, "Python");
            User u2 = new User(102, "C");
            User u3 = new User(103, "C++");
            User u4 = new User(104, "Java");
            System.out.println(client.save(u1));
            System.out.println(client.save(u2));
            System.out.println(client.save(u3));
            System.out.println(client.save(u4));
            System.out.println(client.save(u4));
            System.out.println("------ listUser -------");
            users = client.listUser();
            for(User user: users)
                System.out.println(user);
            System.out.println("------ findUsersByName -------");
            User user = client.findUsersByName(u1.name);
            System.out.println(user);
            System.out.println("------ deleteByUserId -------");
            user = client.deleteByUserId(u4.userId);
            System.out.println(user);
            user = client.deleteByUserId(u4.userId);
            user = client.deleteByUserId(105);
            System.out.println("------ userException -------");
            client.userException();

            transport.close();
        } catch (TTransportException e) {
            System.out.println("TTransportException==>" + e.getLocalizedMessage());
        } catch (UserNotFoundException e) {
            System.out.println("UserNotFoundException==>" + e.getLocalizedMessage());
        } catch (TException e) {
            System.out.println("TException==>" + e.getLocalizedMessage());
        }
    }
}
