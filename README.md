#* 分布式事务的解决方案*

walker is a useful library designed to resolve cross transaction of RDBMS in complex distributed systems


```java
  I am a slow walker
  But I never stop walking
  The important thing in life is to have a great aim and the determination to attain it
```

|Author|宋大侠|
|---|---
|E-mail|1422204321@qq.com

****
## 目录
* [由来](#由来)
* [模块](#模块)
* [数据结构](#数据结构)
    - [分支事务](#分支事务)
      *  [分支事务状态](#分支事务状态)  
    - [分支补偿](#分支事务)
      *  [分支补偿状态](#分支补偿状态)  
* [原理](#原理) 
    * [应用侧](#应用侧) 
    * [协调者](#协调者) 
* [代码示例](#代码示例) 
* [接入方法](#接入方法)
* [重要核心](#重要核心)
* [性能优化](#性能优化)
* [扩展用法](#扩展用法)
* [思想参考](#思想参考) 


### 由来

### 模块
|模块名称|主要功能|
|---|---
|walker|项目介绍
|walker-admin|[项目介绍](walker-admin/README.md)

### 数据结构
* core
    * 语言
        * Java
* bridge
    * 语言
        * Java
* protocol
    * 语言
        * Java
* rabbitmq
    * 语言
        * Java
* coordinator
    * 语言
        * Java
        
### 原理

### 代码示例
```java
   
    @Autowired
    private RemoteFeignService remoteFeignService;

    @TccTransaction(id = "must be unique id",
                needJudge = false,
                mode = CompensateMode.ASYNC,
                commitMethodName = "diy_commit", commitMethodArgs = {"#yourBusinessModel.gid", "#yourBusinessModel.boolParam"},
                cancelMethodName = "diy_cancel", cancelMethodArgs = {"#yourBusinessModel.doubleParam", "#yourBusinessModel.longParam", "#p0.gid"})
    @Transactional(rollbackFor = {Exception.class})
    public void aroundWithWalkerTransactionManagement(YourBusinessModel yourBusinessModel) {

        /**
         * input your business code
         */
        aroundWithSpringTransactionManagement(yourBusinessModel);
    }

    @Transactional(rollbackFor = {Exception.class})
    public void aroundWithSpringTransactionManagement(YourBusinessModel yourBusinessModel) {

        /**
         * input your business code
         */
        remoteFeignService.pay(yourBusinessModel);

        System.out.println("I will rollback with my brother");

        withWalkerTransactionManagement(yourBusinessModel.getGid(), yourBusinessModel.isBoolParam(), yourBusinessModel.getDoubleParam(), yourBusinessModel.getLongParam());
    }


    @TccTransaction(id = "must be unique id!",
            needJudge = false,
            mode = CompensateMode.ASYNC,
            commitMethodName = "diy_commit", commitMethodArgs = {"#gid", "#b"},
            cancelMethodName = "diy_cancel", cancelMethodArgs = {"#paramOfDouble", "#typeOfLong", "#gid"})
    @Transactional(rollbackFor = {Exception.class})
    public void withWalkerTransactionManagement(String gid, boolean b, double paramOfDouble, long typeOfLong) {
        /**
         * do you understand ?
         *
         */
        System.out.println("I will rollback with my brother, too");
    }



    @Transactional
    public Participant diy_commit(String gid, boolean boolParam) {
        WalkerContext walkerContext = WalkerContextlManager.getContext();
        Map<String, Object> commitBody = Maps.newLinkedHashMap();
        commitBody.putIfAbsent("cancel_active_gid", gid);
        commitBody.putIfAbsent("根据需要填写你的参数1", boolParam);
        return Participant.create(walkerContext.getMasterGid(), walkerContext.getCurrentBranchGid(), "your_business_commit_url", commitBody, "something that need callback param from coordinator");
    }

    @Transactional
    public Participant diy_cancel(double paramOfDouble, long typeOfLong, String businessGid) {
        WalkerContext walkerContext = WalkerContextlManager.getContext();
        Map<String, Object> cancelBody = Maps.newLinkedHashMap();
        cancelBody.putIfAbsent("cancel_active_gid", businessGid);
        cancelBody.putIfAbsent("根据需要填写你的参数1", paramOfDouble);
        cancelBody.putIfAbsent("根据需要填写你的参数2", typeOfLong);
        return Participant.create(walkerContext.getMasterGid(), walkerContext.getCurrentBranchGid(), "your_business_cancel_url", cancelBody, "something that need callback param from coordinator");
    }

```
### 接入方法

### 重要核心

### 性能优化

### 扩展用法

### 思想参考
- 支付宝分布式事务设计草案:  [支付宝分布式事务设计草案PDF](_doc/支付宝分布式事务设计草案.pdf)
