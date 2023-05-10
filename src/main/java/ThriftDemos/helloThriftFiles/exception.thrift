namespace java ThriftDemos.helloThrift.exception
namespace py ThriftDemos.helloThrift.exception


exception UserNotFoundException {
   1: string code;
   2: string message;
}