<h1 align="center">Easy RPC Framework</h1>
<p align="center">
  <img src="https://img.shields.io/github/languages/code-size/yoyocraft/easy-rpc" alt="code size"/>
  <img src="https://img.shields.io/badge/vertx-4.5.1-brightgreen" alt="Vertx"/>
  <img alt="GitHub language count" src="https://img.shields.io/github/languages/count/yoyocraft/easy-rpc">
  <img src="https://img.shields.io/badge/Java-17-blue" alt="Java"/>
  <img src="https://img.shields.io/github/last-commit/yoyocraft/easy-rpc" alt="last commit"/> <br />
  <img src="https://img.shields.io/badge/Author-yoyocraft-orange" alt="Author" />
</p>
<hr>




## RPC Processing

![](./doc/assets/easy-rpc-rpc_seq.drawio.png)

![](./doc/assets/easy-rpc-rpc-framework.drawio.png)

## Document

- [PDD](./doc/pdd.md)
- [TDD](./doc/tdd.md)
- [Optimize](./doc/optimize.md)
- [TODO](./TODO.md)
- [Config File](./config/conf.example.properties)

## Quick Start

[Example 示例工程](./example)

```
.
├── example-common
├── example-consumer
├── example-provider
├── example-springboot-consumer
├── example-springboot-provider
```

- `example-common`：POJO、接口定义
- `example-provider` && `example-consumer`：配置代码驱动示例
- `example-springboot-provider` && `example-springboot-consumer`：注解驱动示例

启动流程：

1. 先启动注册中心：etcd、zookeeper、redis 皆可；
2. 修改配置：注册中心、项目监听端口、项目名称等等，`conf.properties`；
3. 启动生产者；
4. 启动消费者。

## Commit

如果需要提交新的代码，需要先执行根目录下的 `init.sh` 文件，该文件会注册一个 git hook，用于提交前的代码检测。