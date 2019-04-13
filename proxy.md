### 一、Proxy.newProxyInstance()

```
/**
     *
     * @param loader  用于定义代理类的类加载器
     * @param interfaces 要实现的代理类的接口列表
     * @param h  代理类
     * @return  具有由指定的类装入器定义并实现指定接口的代理类的指定调用处理程序的代理实例
     * @throws IllegalArgumentException
     */
 public static Object newProxyInstance(ClassLoader loader,
                                          Class<?>[] interfaces,
                                          InvocationHandler h)
            throws IllegalArgumentException
    {

        /*
         * 1、检查h是否为空，为空抛异常
         */
        Objects.requireNonNull(h);

        final Class<?>[] intfs = interfaces.clone();
        // Android-removed: SecurityManager calls

        /*
         * 2、查找或生成指定的代理类
         * 实现动态代理的核心方法，动态代理的思路便是生成一个新类，刚刚getProxyClass0便成为了生成新类
         */
        Class<?> cl = getProxyClass0(loader, intfs);

        /*
         * 3、根据生成的class类通过反射获取构造函数对象并生成代理类实例
         */
        try {
            // Android-removed: SecurityManager / permission checks.
            final Constructor<?> cons = cl.getConstructor(constructorParams);
            final InvocationHandler ih = h;
            if (!Modifier.isPublic(cl.getModifiers())) {
               cons.setAccessible(true);
            }
            return cons.newInstance(new Object[]{h});
        } catch (IllegalAccessException|InstantiationException e) {
            throw new InternalError(e.toString(), e);
        } catch (InvocationTargetException e) {
            Throwable t = e.getCause();
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            } else {
                throw new InternalError(t.toString(), t);
            }
        } catch (NoSuchMethodException e) {
            throw new InternalError(e.toString(), e);
        }
    }
```

### 二、proxyClassCache

proxyClassCache是一个代理类的缓存变量，getProxyClass0()方法里获取的是proxyClassCache中的缓存变量，如果缓存里有个代理类，则直接返回，否则通过ProxyClassFactory创建代理对象

```
private static Class<?> getProxyClass0(ClassLoader loader,
                                           Class<?>... interfaces) {
        if (interfaces.length > 65535) {
            throw new IllegalArgumentException("interface limit exceeded");
        }

        return proxyClassCache.get(loader, interfaces);
    }
```
### 三、ProxyClassFactory

ProxyClassFactory是Proxy里的一个内部类，用来生成代理类，
... 
private static final class ProxyClassFactory
        implements BiFunction<ClassLoader, Class<?>[], Class<?>>
    {
       
        private static final String proxyClassNamePrefix = "$Proxy";
        private static final AtomicLong nextUniqueNumber = new AtomicLong();

        @Override
        public Class<?> apply(ClassLoader loader, Class<?>[] interfaces) {
            ...
            return generateProxy(proxyName, interfaces, loader, methodsArray,
                                                 exceptionsArray);
        }
    }
...

### 四、example

1、业务接口：BusinessProcessor

```
package com.zf.proxydemo.proxys;

/**
 * 业务接口
 */
public interface BusinessProcessor {
    void processBusiness();
}
```

2、业务实现类：BusinessProcessorImpl

```
package com.zf.proxydemo.proxys;

import android.util.Log;

/**
 * 业务实现类
 */
public class BusinessProcessorImpl implements BusinessProcessor {
    @Override
    public void processBusiness() {
        System.out.println("===============>processBusiness");
    }
}
```
3、业务代理类：BusinessProcessorHandler

```
package com.zf.proxydemo.proxys;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 业务代理类
 */
public class BusinessProcessorHandler implements InvocationHandler {

    private Object target;

    public BusinessProcessorHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("===============>proxy before");
        Object result = method.invoke(target,args);
        System.out.println("===============>proxy after");
        return result;
    }
}

```
4、测试类：Test

```
public class Test {
    public static void main(String[] args) {
        BusinessProcessorImpl businessProcessor = new BusinessProcessorImpl();
        BusinessProcessorHandler handler = new BusinessProcessorHandler(businessProcessor);
        BusinessProcessor processor = (BusinessProcessor) Proxy.newProxyInstance(
                businessProcessor.getClass().getClassLoader(),
                businessProcessor.getClass().getInterfaces(),
                handler
        );
        processor.processBusiness();
    }

}
```
输出结果：

```
===============>proxy before
===============>processBusiness
===============>proxy after
```




