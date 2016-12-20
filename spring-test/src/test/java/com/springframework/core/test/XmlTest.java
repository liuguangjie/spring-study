package com.springframework.core.test;

import com.spring.study.beans.Happy;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.Constants;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.xml.XmlValidationModeDetector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2016/11/15.
 */
public class XmlTest {


    /**




     */


    private String config= "spring-context.xml";

    private ResourceLoader resourceLoader=null;
    private Resource resource=null;
    @Before
    public void loadSource(){
        resourceLoader=new DefaultResourceLoader();
        resource=resourceLoader.getResource(config);
    }

    /** 获得命名空间代码dome */
    @Test
    public void testXmlParse() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder docBuilder = factory.newDocumentBuilder();
        Document document=docBuilder.parse(resource.getInputStream());
        Element root=document.getDocumentElement();
        System.out.println(root.getTagName());
        System.out.println(root.getNamespaceURI());
        NodeList nl = root.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if (node instanceof Element) {
                System.out.println(((Element) node).getTagName());
                System.out.println(node.getNamespaceURI());
            }
        }


    }

    @Test
    public void testSpringXml(){
        /*ClassPathResource resource=new ClassPathResource(config);

        System.out.println(resource.exists());*/

        //System.out.println(resource);
        //ApplicationContext applicationContext=new ClassPathXmlApplicationContext(config);
        //System.out.println(applicationContext);
        XmlBeanFactory beanFactory=new XmlBeanFactory(resource);
        Object object=beanFactory.getBean("ssttr");
        System.out.println(object);
    }
    private String classpathConfig="classpath*:"+config;

    /** 测试 PathMatchingResourcePatternResolver 这个类的执行流程 */

    @Test
    public void testPathMatch() throws IOException {
        ResourcePatternResolver resourceLoader=new PathMatchingResourcePatternResolver();
        Resource[] resources=resourceLoader.getResources(classpathConfig);
        System.out.println(resources.length);

    }

    /** 验证org.springframework.util.xml.XmlValidationModeDetector 类的方法*/
    @Test
    public void testXmlValidation() throws IOException {
        InputStream stream=resource.getInputStream();
        XmlValidationModeDetector detector=new XmlValidationModeDetector();
        int type=detector.detectValidationMode(stream);
        System.out.println(type);

    }

    /** 测试 Classloader获取路径  还不是很清楚  */
    @Test
    public void testCLassLoader(){
        System.out.println(XmlBeanFactory.class.getResource(""));
        System.out.println(XmlBeanFactory.class.getClassLoader().getResourceAsStream("org/springframework/beans/factory/xml/spring-beans-3.1.xsd"));
        System.out.println(XmlBeanFactory.class.getClassLoader().getResource(""));
    }

    /** org.springframework.core.Constants 内的测试 */
    @Test
    public void  testXonstants(){
        Constants constants=new Constants(XmlBeanDefinitionReader.class);

        System.out.println(constants.getSize());
    }

    @Test
    public void testUrl(){
        try {
            URL url=new URL("");
            Resource resource=new UrlResource(config);
            System.out.println(resource);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSubString() throws IOException {

        /**
         第一波
        String s="classpath:ssss";
        System.out.println(s.substring("classpath:".length()));
        System.out.println(s);
        */

        /**
         第二波
         PathMatchingResourcePatternResolver patternResolver=new PathMatchingResourcePatternResolver();
         System.out.println(patternResolver);
         String locationPattern = "classpath*:"+config+",classpath*:spring-context-test.xml";
         Resource[] resources=patternResolver.getResources(locationPattern);

         System.out.println(resources.length);
         */

        /**
         第三波

         */
        Map<String,String> map = new ConcurrentHashMap<String,String>();
        map.put("sss","hello world");
        System.out.println(map.get("sss"));
        System.out.println(map.remove("sss"));




    }


    /**
     * 位运算
     */
    @Test
    public void testBitSum(){
        //101000
        System.out.println(10 >> 2);

        /**
        2 10   0
        2  5   1
        2  2   0
        2  1   1
         */

    }

}
