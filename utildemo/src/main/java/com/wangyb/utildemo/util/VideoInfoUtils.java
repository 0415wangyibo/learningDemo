package com.wangyb.utildemo.util;

import lombok.experimental.UtilityClass;
import ws.schild.jave.EncoderException;
import ws.schild.jave.InputFormatException;
import ws.schild.jave.MultimediaInfo;
import ws.schild.jave.MultimediaObject;

import java.io.File;

@UtilityClass
public class VideoInfoUtils {

    /**
     * 获取视频相关信息
     *
     * @return
     * @throws InputFormatException
     * @throws EncoderException
     */
    public void getVideoInfo(File videoFile) throws InputFormatException, EncoderException {
        MultimediaObject multimediaObject = new MultimediaObject(videoFile);
        MultimediaInfo multimediaInfo = multimediaObject.getInfo();

        //大小
        long fileSize = videoFile.length();
        //视频时长 分钟
        int duration = (int) Math.ceil(multimediaInfo.getDuration() / 60000D);
        //视频码率，单位千比特每秒
        int bitrate = multimediaInfo.getVideo().getBitRate();
        int width = multimediaInfo.getVideo().getSize().getWidth();
        int height = multimediaInfo.getVideo().getSize().getHeight();
    }

}
