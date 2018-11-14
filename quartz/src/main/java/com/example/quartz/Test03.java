package com.example.quartz;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/11/1 14:42
 * Modified By:
 * Description:
 */
public class Test03 {
    public static void main(String[] args){
        TestService testService = new TestService();
        testService.addScheduleJob("00:05");
    }
}
