package xyz.staffjoy.common.env;

public class EnvConstant {
    public static final String ENV_DEV = "dev";  //开发环境 ： 开发调试，开发人员远程登录上去做开发
    public static final String ENV_TEST = "test"; //测试环境：独立隔离的，又一套，甚至多套，。功能测试环境，集成测试环境，性能测试环境，也可以按照业务线划分
    public static final String ENV_UAT = "uat"; // similar to staging  用户接受（验收）测试环境User Acceptance Tes    一般独立隔离，一般一套或者两套，供上线前最后一次集成测试的环境，常常被称为 “准生产环境”
    public static final String ENV_PROD = "prod";//生产环境
}
