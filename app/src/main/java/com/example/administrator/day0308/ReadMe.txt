数据库加密：把libs和assets东西复制一下，把so文件复制一份到jniLibs文件夹下，然后把so文件加入到项目中，在build.gradle加入
sourceSets {
        main {
            jniLibs.srcDirs = ['libs']
        }
    }