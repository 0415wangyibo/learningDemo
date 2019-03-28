package com.wangyb.ftpdemo.pojo;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2019/3/27 15:37
 * Modified By:
 * Description:
 */
public enum JobCommon {

    JOB_COMMON;

    //下载信息列表，线程安全的list
    private List<DayDownLoadInfo> allDownLoadInfo = new CopyOnWriteArrayList<>();

    /**
     * 添加下载信息
     *
     * @param dayDownLoadInfo 下载信息
     */
    public void addDayDownLoadInfo(DayDownLoadInfo dayDownLoadInfo) {
        if (allDownLoadInfo.size() != 0) {
            for (DayDownLoadInfo dayDownLoad : allDownLoadInfo) {
                if (dayDownLoad.getDownName().equals(dayDownLoadInfo.getDownName())) {
                    allDownLoadInfo.remove(dayDownLoad);
                    allDownLoadInfo.add(dayDownLoadInfo);
                    return;
                }
            }
        }
        allDownLoadInfo.add(dayDownLoadInfo);
        //为了防止办公电脑卡顿，仅仅保存20条数据，超过的数据会被删除
        while (allDownLoadInfo.size() > 20) {
            for (int i = 0; i < 10; i++) {
                if (allDownLoadInfo.get(i).getStatus() != 1 && allDownLoadInfo.get(i).getUploadStatus() != 1 &&
                        allDownLoadInfo.get(i).getStatus() != 2 && allDownLoadInfo.get(i).getUploadStatus() != 2) {
                    allDownLoadInfo.remove(i);
                    break;
                }
            }
        }
    }

    /**
     * 删除指定名称的下载信息
     *
     * @param downName 任务名称
     */
    public void removeDayDownLoadInfo(String downName) {
        if (StringUtils.isEmpty(downName)) {
            return;
        }
        if (allDownLoadInfo.size() != 0) {
            for (DayDownLoadInfo dayDownLoad : allDownLoadInfo) {
                if (dayDownLoad.getDownName().equals(downName)) {
                    allDownLoadInfo.remove(dayDownLoad);
                }
            }
        }
    }

    /**
     * 获得同名的下载信息
     *
     * @param downName 下载信息主键
     * @return
     */
    public DayDownLoadInfo getDownNameSameInfo(String downName) {
        if (allDownLoadInfo.size() != 0) {
            for (DayDownLoadInfo dayDownLoad : allDownLoadInfo) {
                if (dayDownLoad.getDownName().equals(downName)) {
                    return dayDownLoad;
                }
            }
        }
        return null;
    }

    /**
     * 重置下载信息列表
     *
     * @param dayDownLoadInfoList 下载信息列表
     */
    public void resetDownLoadInfo(List<DayDownLoadInfo> dayDownLoadInfoList) {
        allDownLoadInfo.clear();
        allDownLoadInfo.addAll(dayDownLoadInfoList);
    }

    public List<DayDownLoadInfo> getAllDownLoadInfo() {
        return allDownLoadInfo;
    }

    public List<DayDownLoadInfo> getDayDownLoadInfoByStatus(Integer status) {
        List<DayDownLoadInfo> list = new ArrayList<>();
        if (allDownLoadInfo.size() != 0) {
            for (DayDownLoadInfo dayDownLoad : allDownLoadInfo) {
                if (dayDownLoad.getStatus().equals(status)) {
                    list.add(dayDownLoad);
                }
            }
        }
        return list;
    }

    public List<DayDownLoadInfo> getListByStatusAndUploadStatus(Integer status, Integer uploadStatus) {
        List<DayDownLoadInfo> list = new ArrayList<>();
        if (allDownLoadInfo.size() != 0) {
            for (DayDownLoadInfo dayDownLoad : allDownLoadInfo) {
                if (dayDownLoad.getStatus().equals(status) && dayDownLoad.getUploadStatus().equals(uploadStatus)) {
                    list.add(dayDownLoad);
                }
            }
        }
        return list;
    }

}
