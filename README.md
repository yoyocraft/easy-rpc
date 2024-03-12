# easy-rpc



## Document

- [产品设计文档 (PDD)](./doc/pdd.md)
- [技术设计文档 (TDD)](./doc/tdd.md)
- [迭代优化1](./doc/optimize-1.md)
- [TODO](./TODO.md)


## RPC Processing

![](./doc/assets/easy-rpc-rpc_seq.drawio.png)

## Project Structure
```
.
|-- LICENSE
|-- README.md
|-- config
|   |-- checkstyle.xml
|   `-- codestyle.xml
|-- doc
|-- easy-rpc-core  # RPC 框架
|   |-- README.md
|   |-- pom.xml
|   `-- src
|-- example             # 示例测试代码
|   |-- README.md
|   |-- example-common
|   |-- example-consumer
|   |-- example-provider
|   `-- pom.xml
`-- pom.xml
```