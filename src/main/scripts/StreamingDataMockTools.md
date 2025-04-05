配置流数据调试常用的工具有两个：
+ nc(Netcat)，Linux平台
+ ncat，Nmap工具包提供

------
# Netcat工具
nc（Netcat）是一个功能强大的网络工具，广泛用于各种网络任务，包括创建和管理网络连接、数据传输、调试等。

> MobaXterm 里也提供了nc工具。

基本语法：
```shell
nc [选项] [主机名] [端口]
```

常用选项如下：   
**连接模式**
+ `-u`: 使用 UDP 而不是默认的 TCP。
+ `-4`: 强制使用 IPv4 地址。
+ `-6`: 强制使用 IPv6 地址。

**输入输出控制**
+ `-l, --listen`: 监听模式，作为服务器使用。
+ `-k, --keep-open`: 在接收到一个连接并处理完后，继续监听新的连接。如果没有这个选项，nc 在处理完第一个连接后会退出。
+ `-p <port>, --source-port <port>`: 指定本地源端口。
+ `-s <address>, --source-address <address>`: 指定本地源地址。
+ `-w <seconds>, --wait <seconds>`: 设置超时时间（秒）。
+ `-c <command>, --exec <command>`: 执行命令并将标准输入输出通过网络连接传递。
+ `-e <program>, --exec-after-connect <program>`: 在连接后执行程序。
+ `-x <proxy_address[:port]>, --proxy <proxy_address[:port]>`: 使用代理服务器。
+ `-X <protocol>, --proxy-type <protocol>`: 代理协议（支持 4, 5, http）。
+ `-D, --telnet-proxy`: 启用 Telnet 代理。
+ `-U, --unixsock`: 使用 Unix 域套接字。

**其他**
+ `-V, --verbose`: 启用详细模式，显示更多信息

使用示例：
```shell
# 监听模式（作为服务器），监听 12345 端口
nc -l 12345

# 连接到 HTTP 服务器并获取主页
echo -e "GET / HTTP/1.1\r\nHost: example.com\r\nConnection: close\r\n\r\n" | nc example.com 80

# 使用详细模式连接到 TCP 服务器
nc -v example.com 80

# 设置超时时间
nc -w 5 example.com 80

# 指定本地源端口
nc -p 12345 example.com 80
```

------
# ncat工具
ncat 是 Nmap 工具集（Linux、Windows下均有）中的一个多功能网络工具，类似于 nc（Netcat），但提供了更多的功能和选项。

基本语法：
```shell
ncat [选项] [目标主机] [端口]
```

常用选项如下：

连接模式
+ `-u, --udp`: 使用 UDP 而不是默认的 TCP。
+ `-4`: 强制使用 IPv4 地址。
+ `-6`: 强制使用 IPv6 地址。
+ `-c <command>`: 在连接后执行指定的命令。
+ `-e <program>`: 在连接后执行指定的程序。
+ `-l, --listen`: 监听模式，作为服务器使用。
+ `-k, --keep-open`: 保持监听状态，在处理完一个连接后继续监听新的连接。
+ `-x <proxy_address[:port]>`: 使用代理服务器。
+ `-X <protocol>`: 代理协议（支持 4, 5, http）。
+ `-D`: 启用 Telnet 代理。
+ `-w <seconds>`: 设置超时时间（秒）。

其他
+ `-h, --help`: 显示帮助信息。
+ `-V, --version`: 显示版本信息。
+ `-vv`: 更详细的输出。
+ `-vvv`: 最详细的输出。

使用示例：
```shell
# 监听模式（作为服务器）
ncat -lk 12345

# 使用详细模式连接到 TCP 服务器
ncat -v example.com 80

# 设置超时时间
ncat -w 5 example.com 80
```