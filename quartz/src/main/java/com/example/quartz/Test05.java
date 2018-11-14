package com.example.quartz;

import org.hyperic.sigar.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/11/9 9:54
 * Modified By:
 * Description:
 */
public class Test05 {
    public static void main(String[] args) {
        try {
            InetAddress myip = InetAddress.getLocalHost();
            String myAddress = myip.getHostAddress();
            System.out.println("服务器IP地址：" + myip.getHostAddress());
            Sigar sigar = new Sigar();
            double cpuUsedPerc = sigar.getCpuPerc().getCombined();//cpu
            System.out.println(cpuUsedPerc);
            double memUsed = sigar.getMem().getActualUsed();//mem
            System.out.println(memUsed);
            double memTotal = sigar.getMem().getTotal();
            System.out.println(memTotal);
            double memUsedPerc = sigar.getMem().getUsedPercent();
            System.out.println(memUsedPerc);
            String[] ifNames = sigar.getNetInterfaceList();
            long rxbps = 0;
            long txbps = 0;
            if (null != ifNames && ifNames.length != 0) {
                for (String name : ifNames) {
                    System.out.println(name);
                    NetInterfaceConfig ifconfig = sigar.getNetInterfaceConfig(name);
                    System.out.println("address:" + ifconfig.getAddress());
                    if (ifconfig.getAddress().equals(myAddress)){
                        long start = System.currentTimeMillis();
                        NetInterfaceStat statStart = sigar.getNetInterfaceStat(name);
                        long rxBytesStart = statStart.getRxBytes();
                        System.out.println(rxBytesStart);
                        long txBytesStart = statStart.getTxBytes();
                        System.out.println(txBytesStart);
                        Thread.sleep(1000);
                        long end = System.currentTimeMillis();
                        NetInterfaceStat statEnd = sigar.getNetInterfaceStat(name);
                        long rxBytesEnd = statEnd.getRxBytes();
                        System.out.println(rxBytesEnd);
                        long txBytesEnd = statEnd.getTxBytes();
                        System.out.println(txBytesEnd);
                        rxbps = ((rxBytesEnd - rxBytesStart)*8/(end-start)*1000)/1024/8;//KB/s 接收速度
                        txbps = ((txBytesEnd - txBytesStart)*8/(end-start)*1000)/1024/8;//KB/s 发送速度
                        break;
                    }
                }
            }
            System.out.println(rxbps);
            System.out.println(txbps);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        String memUsedStr = String.format("%.2f", memUsed/1024/1024/1024)+"GB";// mem to string GB
//        String memTotalStr = String.format("%.2f", memTotal/1024/1024/1024)+"GB";
//        String memUsedPercStr = String.format("%.2f", memUsedPerc)+" %";
//        double diskUsed = sigar.getFileSystemUsage(PathKit.getWebRootPath()).getUsed();//disk
//        double diskTotal = sigar.getFileSystemUsage(PathKit.getWebRootPath()).getTotal();
//        double diskUsedPerc = sigar.getFileSystemUsage(PathKit.getWebRootPath()).getUsePercent();
//        String diskUsedStr = String.format("%.2f", diskUsed/1024/1024)+"GB";//disk to String GB
//        String diskTotalStr = String.format("%.2f", diskTotal/1024/1024)+"GB";
//        String diskUsedPercStr = String.format("%.2f", diskUsedPerc*100)+" %";
//        String fqdn = sigar.getFQDN();//FQDN
    }
}
