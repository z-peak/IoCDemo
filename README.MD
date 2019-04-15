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

### 三、代理 Proxy
[参考【Java动态代理】](https://blog.csdn.net/androidzf/article/details/89284962)

### 四、LayoutView
通过注解自动帮Activity添加布局文件

注解：LayoutView

```
@Target(ElementType.TYPE)//该注解作用在...上
@Retention(RetentionPolicy.RUNTIME)//运行时通过反射获取注解的值
public @interface LayoutView {
    int value();
}
```
Activity中使用：

```
@LayoutView(R.layout.activity_main)
public class MainActivity extends BaseActivity implements View.OnClickListener {
    //...
}
```

通过反射给Activity 添加布局文件
```
/**
 * 给Activity注入布局
 * @param activity
 */
private static void bindLayout(Activity activity) {
    Class<? extends Activity> clazz = activity.getClass();
    //获取注解
    LayoutView layoutView = clazz.getAnnotation(LayoutView.class);
    if (layoutView!=null){
        int layoutId = layoutView.value();
        try {
            Method method = clazz.getMethod("setContentView", int.class);
            method.invoke(activity,layoutId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

### 五、BindView
通过注解自动findViewById

注解：BindView

```
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BindView {
    int value();
}
```
Activity中使用：

```
@BindView(R.id.textView)
private TextView textView;

@BindView(R.id.button)
private TextView button;
```

通过反射给国有添加了注解的View赋值

```
/**
 * 给Activity布局的View赋值
 * @param activity
 */
private static void bindViews(Activity activity) {
    Class<? extends Activity> clazz = activity.getClass();
    //获取类中的所有属性
    Field[] fields = clazz.getDeclaredFields();
    for (Field field: fields) {
        //获取属性的注解
        BindView bindView = field.getAnnotation(BindView.class);
        if (bindView!=null){//含有InjectView注解的属性进行下面操作
            //获取view id
            int viewId = bindView.value();
            try {
                Method method = clazz.getMethod("findViewById", int.class);
                //获得view
                Object view = method.invoke(activity, viewId);
                //取消Java访问权限检查
                field.setAccessible(true);
                //对Activity中的属性进行复制
                field.set(activity,view);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
```

### 六、事件绑定
1、通过@OnClick注解设置View的点击事件

```
@OnClick(R.id.textView)
public void aaabbbccc(View view){
    Toast.makeText(this,"我是自定义的点击事件",Toast.LENGTH_LONG).show();
}
```
2、注解@OnClick

```
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@EventBase(listenerSetter = "setOnClickListener",listenerType = View.OnClickListener.class,callBackFunction = "onClick")
public @interface OnClick {
    int[] value();
}
```
3、注解@OnClick中使用的注解@EventBase

```
@Target(ElementType.ANNOTATION_TYPE)//作用在注解之上
@Retention(RetentionPolicy.RUNTIME)
public @interface EventBase {

    //1、setOnxxxListener()
    String listenerSetter();

    //2、监听的对象
    Class<?> listenerType();

    //3、回调方法 onClick(View view)
    String callBackFunction();
}
```
4、事件注入核心代码：

```
/**
 * 事件注入
 * @param activity
 */
private static void bindEvent(Activity activity) {
    Class<? extends Activity> clazz = activity.getClass();
    //获取当前类的所有方法
    Method[] methods = clazz.getDeclaredMethods();
    for (Method method : methods) {
        //获取方法的所有注解（可能不止一个）
        Annotation[] annotations = method.getAnnotations();
        //遍历所有的注解
        for (Annotation annotation:annotations) {//OnClick注解
            if (annotation==null)return;
            //获取注解类型
            Class<? extends Annotation> annotationType = annotation.annotationType();
            if (annotationType!=null){
                //获取OnClick注解上的EventBase注解
                EventBase eventBase= annotationType.getAnnotation(EventBase.class);
                if (eventBase==null)return;
                String callBackListener = eventBase.callBackFunction();//onClick
                String listenerSetter = eventBase.listenerSetter();//setOnClickListener
                Class<?> listenerType = eventBase.listenerType();// View.OnClickListener.class

                try {
                    Method declaredMethod = annotationType.getDeclaredMethod("value");
                    int[] viewIds = (int[]) declaredMethod.invoke(annotation);

                    ListenerInvocationHandler listenerInvocationHandler = new ListenerInvocationHandler(activity);
                    listenerInvocationHandler.addMethod(callBackListener,method);
                    Object listener = Proxy.newProxyInstance(listenerType.getClassLoader(), new Class[]{listenerType}, listenerInvocationHandler);

                    for (int id:viewIds) {
                        //获取当前View
                       View view= activity.findViewById(id);
                       //得到View的setOnClickListener方法
                       Method onClickListenerMethod = view.getClass().getMethod(listenerSetter,listenerType);
                       //执行setOnClickListener方法
                         onClickListenerMethod.invoke(view,listener);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
```
[github源码链接](https://androidzhoufeng@github.com/androidzhoufeng/IoCDemo.git)


