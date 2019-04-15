package com.wangyb.utildemo.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.wangyb.utildemo.entity.BaseResp;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.IOException;
import java.util.Iterator;

/**
 * @author wangyb
 * @Date 2019/4/15 14:35
 * Modified By:
 * Description:  一些常用的工具类
 */
@Slf4j
public class XmlParseUtil {

    /**
     * 根据xml文件路径解析xml
     * @param xmlPath xml文件路径
     */
    public static void parseXml(String xmlPath) {
        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(xmlPath);
            // 获取根节点
            Element programSeries = document.getRootElement();
            for (Iterator programSerialIterator = programSeries.elementIterator(); programSerialIterator.hasNext(); ) {
                Element programSerial = (Element) programSerialIterator.next();
                // 获取节点信息
                System.out.println(programSerial.elementText("id"));
                System.out.println(programSerial.elementText("name"));
                Element programsElement = programSerial.element("programs");
                for (Iterator programsIterator = programsElement.elementIterator("program"); programsIterator.hasNext(); ) {
                    Element program = (Element) programsIterator.next();
                    System.out.println(program.elementText("programSeriesId"));
                    System.out.println(program.elementText("partNum"));
                    Element mediasElement = program.element("medias");
                    for (Iterator mediasIterator = mediasElement.elementIterator("media"); mediasIterator.hasNext(); ) {
                        Element medias = (Element) mediasIterator.next();
                        System.out.println(medias.elementText("cpCode"));
                        System.out.println(medias.elementText("mid"));
                    }
                }
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将xml数据解析成指定类型的java bean
     * @param data xml数据
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T parseXmlToJavaBean(String data, Class<T> clazz) {
        XmlMapper mapper = new XmlMapper();
        try {
            BaseResp baseResp = mapper.readValue(data, BaseResp.class);
            if (baseResp.getRet() != 0) {
                log.error("TransCode Error. Code: {}, Msg: {}", baseResp.getRet(), baseResp.getRetMsg());
                return null;
            }
            return mapper.readValue(data, clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将指定的java bean 转化成xml
     * @param data
     * @param <T>
     * @return
     */
    public static <T> String convertJavaBeanToXml(T data) {
        XmlMapper mapper = new XmlMapper();
        try {
            mapper.setDefaultUseWrapper(false);
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            mapper.setPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE);
            mapper.enable(MapperFeature.USE_STD_BEAN_NAMING);
            mapper.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true);
            String result = mapper.writeValueAsString(data);
            System.out.println(result);
            return result;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
