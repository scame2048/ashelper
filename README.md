# ashelper

这是一个简单的 android studio 帮助工程， 使用方式

在 project 工程下 build.gradle 添加如下内容

```build.gradle
......
allprojects {
    repositories {
        ......
        maven { url 'https://jitpack.io' }
    }
}
......
```

然后在 lib module build.gradle 下添加如下内容:

```
dependencies {
    ......
    implementation 'com.github.emacs1024.ashelper:annotation:x.x.x'
    annotationProcessor 'com.github.emacs1024.ashelper:compiler:x.x.x'
    ......
}
```

# version
当前版本信息:
```
dependencies {
    implementation 'com.github.emacs1024.ashelper:annotation:1.0.8'
    annotationProcessor 'com.github.emacs1024.ashelper:compiler:1.0.8'
}
```


# fix
