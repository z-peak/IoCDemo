### 一、反射的相关api

API | 描述
---|---
getClass() | 获取类
getName()|获得类的完整名字。
getAnnotation(xxx.class) | 获取注解
getMethod() | 获所有（包括父类）public方法
getFields() | 获所有（包括父类）public属性
invoke(xx,yy) | 执行方法
getDeclaredMethods() | 获取当前类所有方法（包括private）
getDeclaredFields() | 获取当前类所有属性（包括private）
annotationType() | 获取注解类型
getConstructors() | 获取类的public类型的构造方法
getConstructor(Class[] parameterTypes) | 获得类的特定构造方法，parameterTypes 参数指定构造方法的参数类型。
newInstance() | 通过类的不带参数的构造方法创建这个类的一个对象。
setAccessible(true)|取消Java的访问权限检查机制


### 二、注解

#### 1、@Target注解的作用目标

type | 作用目标
---|---
ElementType.TYPE|接口、类、枚举、注解
ElementType.FIELD|字段、枚举的常量
ElementType.METHOD|方法
ElementType.PARAMETER|方法参数
ElementType.CONSTRUCTOR|构造函数
ElementType.LOCAL_VARIABLE|局部变量
ElementType.ANNOTATION_TYPE|注解
ElementType.PACKAGE|包

[Android ElementType APIs](http://tool.oschina.net/uploads/apidocs/android/reference/java/lang/annotation/ElementType.html)

#### 2、@Retention：注解的保留位置
type | 作用目标
---|---
RetentionPolicy.SOURCE|注解仅存在于源码中，在class字节码文件中不包含
RetentionPolicy.CLASS|默认的保留策略，注解会在class字节码文件中存在，但运行时无法获得
RetentionPolicy.RUNTIME|注解会在class字节码文件中存在，在运行时可以通过反射获取到

[Android RetentionPolicy APIs](http://tool.oschina.net/uploads/apidocs/android/reference/java/lang/annotation/RetentionPolicy.html)

### 三、AOP

面向切面编程
