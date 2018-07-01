# AOPDemo

使用AspectJ实现AOP编程

1. [app：性能检测](https://github.com/JamFF/AOPDemo/tree/master/app)
2. [thread：线程切换](https://github.com/JamFF/AOPDemo/tree/master/thread)
3. [permission：申请动态权限](https://github.com/JamFF/AOPDemo/tree/master/permission)

### AOP介绍

* 通过预编译方式和运行期动态代理实现程序功能的统一维护
* 降低耦合度，统一调用，便于维护，对OOP编程是一种十分有益的补充

### AOP编程的使用场景

* 权限验证，100个功能，其中有10个需要登录或会员才能使用，可以在这十个方法执行前使用，如果没有登录跳转到登录或充值界面
* 操作文件，释放资源，可以在所有方法执行后调用
* 性能检测，在方法执行前、执行后调用
* 用户行为统计、线程切换...处理业务多，但是重复的业务，都可以使用到AOP

### 两种方式

1. LTW (Load Time Weaver)——加载期间 类加载 动态代理
2. CTW (Compile Time Weaver)——编译时注入 APT、操作字节码(ASM)

### AspectJ介绍

1. 第一步 `.java`通过`javac`编译为`.class`
2. 第二步 `.class`再通过`AspectJ`(编译器)对`.class`修改注入、生成