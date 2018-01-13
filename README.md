# EasyCache

- EasyCache，最简单的方式，将数据保存在LruCache和DiskCache中。

- 快速使用，gradle依赖，compile 'com.hzh:easy-cache:1.0.3'。

### 文章地址
- [简书链接](http://www.jianshu.com/writer#/notebooks/3358144/notes/19237375)

## 引言
- App开发，当项目需求要实现缓存功能，是不是LruCache内存缓存和DiskCache中的Key到处存放，调用时判断内存和磁盘的逻辑，读取缓存的时候，到处写判断，实在不好维护。
- 其实，大家都一样，但是项目逐渐扩大，这种代码实在不好维护，就要进行抽取。

## 思想
- 一个操作类，提供基础的存取方法，相当于工具类,再根据场景在该类中添加相关业务方法，这也是我们最基础的方式。但是这样就会所有场景的方法都写进这个类，则会类膨胀，修改起来简直头晕，噩梦。

- 有没有方式可以将每个场景限制在不同的类中，但是又有相关的联系，这不禁想到面向对象的封装和继承，最好能用上多态。将基础操作放在工具类，还有一个类包裹该工具类，并且提供最基本的存取方法，转调该工具类，每个场景只需要新建子类，重写存取方法。

- 在使用的Activity中，创建缓存实例，调用get()方法获取缓存，put()方法保存缓存，简单2句。

## 该库特点
1. 只需要新建一个缓存类，例如UserInfoCache，继承BaseCache，重写put()方法存储数据，get（）方法读取数据，removeCache（）方法移除缓存。

2. 提供一个参数类，例如UserInfoCacheParams，继承BaseCacheParams，该类在BaseCache的子类重写removeCache()、put()、get()方法中传入，作为所有参数的容器。

3. 子类内部直接可以拿到mMemoryCache内存缓存对象，mDiskCache磁盘缓存对象，以及双缓存mOperate对象。可根据具体业务仅选用DiskCache缓存还是仅LruCache内存保存缓存，还是Operate进行内存、磁盘双缓存。

## 库结构
- CacheOperate，持有DiskCache、LruCache-ACache，提供基础的putData()存储数据，putListData()存储集合数据，getData（）获取数据。保存和获取都是先从内存取，没有再从磁盘取，有则取出并且保存到内存，再返回。

- BaseCache，实现ICache接口，缓存基类，每种缓存场景，新建一个类，继承BaseCache，例如UserInfoCacheParams，继承BaseCacheParams，重写put()方法存储数据，get（）方法读取数据，removeCache（）方法移除缓存，内部存储由CacheOperate来实现。

- BaseCacheParams，参数类，有什么作用呢？如名字所见，是作为参数用的，在put方法传入，本质是一个Map，存储传入的参数，统一传入的参数，为什么这么做？场景：缓存用户信息需要传入userId，缓存群组信息需要groupId，如果需要传入更多的参数来组合唯一标识Key，就很难做到重写一个put方法做到适合各种缓存的数据，在BaseCache类中可直接在类申明上用泛型指定其对应的Parmas类。

- CacheFactory，工厂类，负责构建Cache类，通过create()方法，使用反射构造BaseCache的子类，并且缓存进Map。

- ICache，BaseCache实现的接口，具有getOperate()方法，获取CacheOperate。

- ICacheParams，BaseCacheParams实现的接口。

- ACache，DiskCache磁盘缓存具体实现。

- CacheManager，init()方法初始化CacheOperate，removeAllCache()移除所有缓存的ICache对象实例，在应用内存紧张时，可清除这些缓存对象。

## 快速使用
```java
//调用后台接口，获取用户信息
String json = requestUserInfo();
JSONObject jsonObject = new JSONObject(json);
String userId = jsonObject.optString("id");
//保存用户信息的json到缓存
UserInfoCache cache = CacheFactory.create(UserInfoCache.class);
UserInfoCacheParams params = new UserInfoCacheParams();
params.putUserId(userId);
cache.put(params, json);
//读取缓存的json
String json = cache.get(new UserInfoCacheParams().putUserId(userId));
```

## 具体实现
- ACache就不贴代码了，就是网上的ACache，只是拓展了构造方法。
- LruCache则是系统自带的Lru。
- 首先是操作Cache的操作类，CacheOperate。

```java
/**
 * Created by Hezihao on 2017/8/4.
 * 数据缓存，提供内存缓存和磁盘缓存数据
 */

public class CacheOperate {
    private LruCache<String, Object> mLruCache;
    private ACache mDiskCache;
    private Context context;
    private int versionCode;
    private static boolean isInited = false;

    private CacheOperate() {
    }

    private static class SingletonHolder {
        private static final CacheOperate instance = new CacheOperate();
    }

    /**
     * 初始化，会保存Context，最好传入ApplicationContext
     *
     * @return
     */
    public static CacheOperate init(CacheConfig config) {
        if (isInited) {
            return SingletonHolder.instance;
        } else {
            CacheOperate instance = SingletonHolder.instance;
            instance.context = config.getContext();
            instance.versionCode = config.getVersionCode();
            instance.mLruCache = new LruCache<String, Object>(config.getMemoryMaxSize());
            instance.mDiskCache = ACache.get(config.getContext(), config.getCacheFileName()
                    , config.getDiskMaxSize(), config.getDiskMaxCount());
            isInited = true;
            return instance;
        }
    }

    public static CacheOperate getInstance() {
        if (!isInited) {
            throw new IllegalStateException("必须先调用init()，初始化");
        }
        return SingletonHolder.instance;
    }

    public LruCache<String, Object> getLruCache() {
        return mLruCache;
    }

    public ACache getDiskCache() {
        return mDiskCache;
    }

    public Context getContext() {
        return context;
    }

    public int getVersionCode() {
        return versionCode;
    }

    /**
     * 永久保存集合对象
     *
     * @param key
     * @param list
     * @param <T>
     */
    public <T extends Serializable> void putListData(@NonNull String key, @NonNull List<T> list) {
        putListData(key, list, -1);
    }

    public <T extends Serializable> void putListData(@NonNull String key, @NonNull List<T> list, @Nullable int saveTime) {
        ArrayList<T> dataList = new ArrayList<T>(list);
        mLruCache.put(key, list);
        mDiskCache.put(key, dataList, saveTime);
    }

    /**
     * 永久保存序列化数据
     *
     * @param key
     * @param data
     * @param <T>
     */
    public <T extends Serializable> void putData(@NonNull String key, @NonNull T data) {
        putData(key, data, -1);
    }

    public <T extends Serializable> void putData(@NonNull String key, @NonNull T data, @Nullable int saveTime) {
        mLruCache.put(key, data);
        mDiskCache.put(key, data, saveTime);
    }

    /**
     * 获取数据
     *
     * @param key
     * @param <T>
     * @return
     */
    public <T extends Serializable> T getData(@NonNull String key) {
        return getData(key, null);
    }

    public <T extends Serializable> T getData(@NonNull String key, @Nullable T defaultValue) {
        T result = (T) mLruCache.get(key);
        if (result == null) {
            result = (T) mDiskCache.getAsObject(key);
            if (result == null && defaultValue != null) {
                result = defaultValue;
            }
            if (result != null) {
                mLruCache.put(key, result);
            } else {
                return defaultValue;
            }
        }
        return result;
    }

    /**
     * 按key在内存缓存中移除
     *
     * @param key
     * @param <T>
     * @return
     */
    public <T extends Serializable> T removeMemoryCache(@NonNull String key) {
        return (T) mLruCache.remove(key);
    }

    /**
     * 移除磁盘缓存
     *
     * @param key
     * @return
     */
    public <T extends Serializable> T removeDiskCache(@NonNull String key) {
        Object cache = mDiskCache.getAsObject(key);
        if (cache != null) {
            mDiskCache.remove(key);
            return (T) cache;
        } else {
            return null;
        }
    }

    /**
     * 移除内存缓存和磁盘缓存
     */
    public void removeCache(@NonNull String key) {
        removeMemoryCache(key);
        removeDiskCache(key);
    }

    /**
     * 移除所有Lru内存缓存
     */
    public void removeAllMemoryCache() {
        mLruCache.evictAll();
    }

    /**
     * 移除所有Disk磁盘缓存
     */
    public void removeAllDiskCache() {
        mDiskCache.clear();
    }
}
```
- ICache，定义BaseCache使用的抽象方法。

```java
public interface ICache {
    /**
     * 获取操作对象
     *
     * @return
     */
    CacheOperate getOperate();

    /**
     * 获取VersionCode，通常为App的VersionCode
     *
     * @return
     */
    int getVersionCode();

    /**
     * 获取Version标志，通常为特定字符拼接VersionCode
     *
     * @return
     */
    String getVersionSymbol();
}
```

- BaseCache，基础缓存类

```java
/**
 * Created by Hezihao on 2017/8/4.
 */

public abstract class BaseCache<P extends BaseCacheParams> implements ICache {
    private String versionSymbol;

    public CacheOperate getOperate() {
        return CacheOperate.getInstance();
    }

    @Override
    public int getVersionCode() {
        return getOperate().getVersionCode();
    }

    @Override
    public String getVersionSymbol() {
        if (versionSymbol != null) {
            return versionSymbol;
        } else {
            new StringBuilder()
                    .append("v")
                    .append(getVersionCode())
                    .append("_");
            return versionSymbol;
        }
    }

    public abstract void removeCache(@Nullable P params);

    public abstract void put(@Nullable P params, Serializable target);

    public abstract <T extends Serializable> T get(@Nullable P params);
}
```

- 参数类接口

```java
/**
 * Created by Hezihao on 2017/9/8.
 * 参数类Map
 */

public interface ICacheParams {
    /**
     * 获取VersionCode，通常为App的VersionCode
     *
     * @return
     */
    int getVersionCode();

    /**
     * 获取Version标志，通常为特定字符拼接VersionCode
     *
     * @return
     */
    String getVersionSymbol();

    /**
     * 将参数存进容器，子类定义容器，通常为Map
     *
     * @param key    键
     * @param target 要存入的数据
     * @param <P>
     * @return
     */
    <P extends ICacheParams> P put(String key, Serializable target);

    /**
     * 从容器中取出参数
     *
     * @param key 键
     * @param <T>
     * @return
     */
    <T extends Serializable> T get(String key);

    /**
     * 从容器中取出参数，可设置默认值
     *
     * @param key          键
     * @param defaultValue 默认值
     * @param <T>
     * @return
     */
    <T extends Serializable> T get(String key, Serializable defaultValue);
}
```

- 参数类，作为缓存类所用的参数对象

```java
/**
 * Created by Hezihao on 2017/9/8.
 * Cache使用时，可附带参数
 */

public abstract class BaseCacheParams implements ICacheParams {
    private HashMap<String, Serializable> paramsMap;
    private String versionSymbol;

    @Override
    public int getVersionCode() {
        return CacheOperate.getInstance().getVersionCode();
    }

    @Override
    public String getVersionSymbol() {
        if (versionSymbol != null) {
            return versionSymbol;
        } else {
            new StringBuilder()
                    .append("v")
                    .append(getVersionCode())
                    .append("_");
            return versionSymbol;
        }
    }

    private HashMap<String, Serializable> getParamsMap() {
        if (paramsMap == null) {
            paramsMap = new HashMap<String, Serializable>();
        }
        return paramsMap;
    }

    @Override
    public <P extends ICacheParams> P put(String key, Serializable target) {
        getParamsMap().put(key, target);
        return (P) this;
    }

    @Override
    public <T extends Serializable> T get(String key) {
        return get(key, null);
    }

    @Override
    public <T extends Serializable> T get(String key, Serializable defaultValue) {
        T value = (T) getParamsMap().get(key);
        if (value == null) {
            return (T) defaultValue;
        } else {
            return value;
        }
    }
}
```

- 工厂类，构建BaseCache的子类。

```java
public class CacheFactory {
    /**
     * 缓存Map，缓存ICache的实例，以子类的Class作为Key，ICache实例作为Value
     */
    private static final ConcurrentHashMap<Class<?>, ICache> mCacheMap = new ConcurrentHashMap<Class<?>, ICache>();

    private CacheFactory() {
    }

    /**
     * 反射构造BaseCache子类，并存入到缓存Map中，如果在缓存中有，则直接返回缓存中缓存实例
     *
     * @param clazz 要构造的BaseCache的Class
     * @param <T>   泛型，限制为ICache的实现类
     * @return 返回要构造的BaseCache的子类
     */
    public static <T extends ICache> T create(Class<T> clazz) {
        ICache cache = mCacheMap.get(clazz);
        if (cache == null) {
            Constructor<T> constructor = null;
            try {
                constructor = clazz.getDeclaredConstructor();
                constructor.setAccessible(true);
                cache = constructor.newInstance();
                mCacheMap.put(clazz, cache);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return (T) cache;
    }

    /**
     * 从缓存中移除缓存实例
     *
     * @param clazz 缓存的子类Class
     * @param <T>   泛型，限制ICache的子类
     * @return 返回移除前缓存的实例
     */
    public static <T extends ICache> T removeCache(Class<T> clazz) {
        ICache cache = mCacheMap.get(clazz);
        if (cache != null) {
            mCacheMap.remove(clazz);
            return (T) cache;
        } else {
            return null;
        }
    }

    public static void removeAllCache() {
        mCacheMap.clear();
    }
}
```

- CacheConfig，配置类，配置初始化参数

```java
public class CacheConfig {
    private static final int M = 1024 * 1024;
    //默认磁盘缓存保存文件的文件名
    private static final String DEFAULT_CACHE_FILE_NAME = "ACache";
    //默认磁盘缓存存储大小，默认50 MB
    private static final int DEFAULT_DISK_CACHE_MAX_SIZE = 1000 * 1000 * 50;
    //默认磁盘缓存存储数量，默认不限制
    private static final int DEFAULT_DISK_CACHE_MAX_COUNT = Integer.MAX_VALUE;
    //默认内存缓存数量
    private static final int DEFAULT_MEMORY_CACHE_SIZE = 5 * M;

    private Context context;
    private int versionCode = 0;
    private String cacheFileName;
    private long diskMaxSize;
    private int diskMaxCount;
    private int memoryMaxSize;

    public CacheConfig(Builder builder) {
        this.context = builder.getContext();
        this.versionCode = builder.getVersionCode();
        this.cacheFileName = builder.getCacheFileName();
        this.diskMaxSize = builder.getDiskMaxSize();
        this.diskMaxCount = builder.getDiskMaxCount();
        this.memoryMaxSize = builder.getMemoryMaxSize();
    }

    public static Builder newBuilder(Context context, int versionCode) {
        return new Builder(context, versionCode);
    }

    public Context getContext() {
        return context;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public String getCacheFileName() {
        return cacheFileName;
    }

    public long getDiskMaxSize() {
        return diskMaxSize;
    }

    public int getDiskMaxCount() {
        return diskMaxCount;
    }

    public int getMemoryMaxSize() {
        return memoryMaxSize;
    }

    public static class Builder {
        private Context context;
        private int versionCode;
        private String cacheFileName = DEFAULT_CACHE_FILE_NAME;
        private long diskMaxSize = DEFAULT_DISK_CACHE_MAX_SIZE;
        private int diskMaxCount = DEFAULT_DISK_CACHE_MAX_COUNT;
        private int memoryMaxSize = DEFAULT_MEMORY_CACHE_SIZE;

        private Builder(Context context, int versionCode) {
            this.context = context;
            this.versionCode = versionCode;
        }

        public Builder withCacheFileName(String cacheFileName) {
            this.cacheFileName = cacheFileName;
            return this;
        }

        public Builder withDiskMaxSize(long maxSize) {
            this.diskMaxSize = maxSize;
            return this;
        }

        public Builder withDiskMaxCount(int maxCount) {
            this.diskMaxCount = maxCount;
            return this;
        }

        public Builder withMemoryMaxSize(int memoryMaxSize) {
            this.memoryMaxSize = memoryMaxSize;
            return this;
        }

        public Context getContext() {
            return context;
        }

        public int getVersionCode() {
            return versionCode;
        }

        public String getCacheFileName() {
            return cacheFileName;
        }

        public long getDiskMaxSize() {
            return diskMaxSize;
        }

        public int getDiskMaxCount() {
            return diskMaxCount;
        }

        public int getMemoryMaxSize() {
            return memoryMaxSize;
        }

        public CacheConfig build() {
            return new CacheConfig(this);
        }
    }
}
```

- CacheManager，初始化Cache和清除Cache实例的管理类

```java
public class CacheManager {
    private CacheManager() {
    }

    private static final class SingletonHolder {
        private static final CacheManager instance = new CacheManager();
    }

    public static CacheManager getInstance() {
        return SingletonHolder.instance;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        if (context == null) {
            throw new NullPointerException("context 不能为空");
        }
        int versionCode = 0;
        try {
            versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        CacheOperate.init(CacheConfig.newBuilder(context, versionCode).build());
    }

    /**
     * 移除所有内存中的缓存实例
     */
    public void removeAllCache() {
        CacheFactory.removeAllCache();
    }
}
```

## 结语
- 后续更继续改进改库，欢迎提交建议。
- Easy-Cache已经上传到了Jcentr，并且有一个Sample例子。下面GitHub提供地址和gradle依赖。
- [GitHub链接](https://github.com/hezihaog/EasyCache)
- 再次欢迎Start，issue,push request!
- gradle依赖，compile 'com.hzh:easy-cache:1.0.3'