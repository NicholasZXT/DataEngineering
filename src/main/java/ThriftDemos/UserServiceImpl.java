package ThriftDemos;

import ThriftDemos.helloThrift.service.UserService;
import ThriftDemos.helloThrift.domain.User;
import ThriftDemos.helloThrift.exception.UserNotFoundException;
import org.apache.thrift.TException;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;


public class UserServiceImpl implements UserService.Iface {

    private Map<String, User> users;
    private Map<Integer, String> user_ids;

    public UserServiceImpl(){
        this.users = new HashMap<>();
        this.user_ids = new HashMap<>();
    }

    @Override
    public String hello() throws TException {
        String hello_words = "Hello to UserService written by Thrift";
        System.out.println(hello_words);
        return hello_words;
    }

    @Override
    public List<User> listUser() throws TException {
        System.out.println("listUser called...");
        List<User> users = new ArrayList<User>(this.users.values());
        return users;
    }

    @Override
    public boolean save(User user) throws TException {
        if (this.users.containsKey(user.name)){
            System.out.println("user [" + user.userId + ":" + user.name + "] exists !!!");
            return false;
        }else {
            this.users.put(user.name, user);
            this.user_ids.put(user.userId, user.name);
            System.out.println("save new user [" + user.userId + ":" + user.name + "]");
            return true;
        }
    }

    @Override
    public User findUsersByName(String name) throws TException {
        if (this.users.containsKey(name)){
            System.out.println("found user :" + name);
            return this.users.get(name);
        }else {
            System.out.println("not found user :" + name);
            return null;
        }
    }

    @Override
    public User deleteByUserId(int userId) throws UserNotFoundException, TException {
        User user = null;
        if (this.user_ids.containsKey(userId)){
            String userName = this.user_ids.get(userId);
            user = this.users.get(userName);
            this.user_ids.remove(userId);
            this.users.remove(userName);
            System.out.println("user [" + user.userId + ":" + user.name + "] was deleted.");
        }else {
            throw new UserNotFoundException("500","user [" + userId + "] not found");
        }
        return user;
    }

    @Override
    public void userException() throws UserNotFoundException, TException {
        System.out.println("raised UserNotFoundException for test");
        throw new UserNotFoundException("400","test");
    }
}
