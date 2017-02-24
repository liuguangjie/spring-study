package com.springframework.core.test;

import com.spring.study.beans.*;
import com.spring.study.collection.InjectionCollections;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.SourceFilteringListener;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.Constants;
import org.springframework.core.io.*;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.CollectionUtils;
import org.springframework.util.xml.XmlValidationModeDetector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2016/11/15.
 */
public class XmlTest {


    /**


     */


    private String config= "spel/spring-spel.xml";

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
        factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage","http://www.w3.org/2001/XMLSchema");
        DocumentBuilder docBuilder = factory.newDocumentBuilder();
        docBuilder.setEntityResolver(new EntityResolver() {

            public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {

                if (systemId != null) {
                    String resourceLocation = getSchemaMappings().get(systemId);
                    if (resourceLocation != null) {
                        Resource resource = new ClassPathResource(resourceLocation, resourceLoader.getClassLoader());
                        try {
                            InputSource source = new InputSource(resource.getInputStream());
                            source.setPublicId(publicId);
                            source.setSystemId(systemId);
                            return source;
                        }
                        catch (FileNotFoundException ex) {
                        }
                    }
                }
                return null;
            }



        });
        Document document=docBuilder.parse(resource.getInputStream());
        Element root=document.getDocumentElement();

        //Element rootElement=root.getDocumentElement();

        System.out.println(root.getTagName());
        System.out.println(root.getNamespaceURI());

        NodeList nl = root.getChildNodes();
        System.out.println(nl.getLength());
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if (node instanceof Element) {
                System.out.println(((Element) node).getTagName());
                System.out.println(node.getNamespaceURI());
            }
        }


    }
    private volatile Map<String, String> schemaMappings;
    private  String schemaMappingsLocation="META-INF/spring.schemas";
    private Map<String, String> getSchemaMappings() {
        if (this.schemaMappings == null) {
            synchronized (this) {
                if (this.schemaMappings == null) {
                    try {
                        Properties mappings =
                                PropertiesLoaderUtils.loadAllProperties(this.schemaMappingsLocation, resourceLoader.getClassLoader());
                        Map<String, String> schemaMappings = new ConcurrentHashMap<String, String>();
                        CollectionUtils.mergePropertiesIntoMap(mappings, schemaMappings);
                        this.schemaMappings = schemaMappings;
                    }
                    catch (IOException ex) {
                        throw new IllegalStateException(
                                "Unable to load schema mappings from location [" + this.schemaMappingsLocation + "]", ex);
                    }
                }
            }
        }
        return this.schemaMappings;
    }

    @Test
    public void testGetInputStream() throws IOException {
        Resource resource = new ClassPathResource("org/springframework/beans/factory/xml/spring-beans-3.1.xsd");
        InputStream inputStream=resource.getInputStream();
        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
        String line=null;

        while(true){
            line=bufferedReader.readLine();
            if(line==null){
                break;
            }
            System.out.println(line);

        }

        System.out.println(inputStream);
    }

    @Test
    public void testLookupMothed(){
        ClassPathResource resource=new ClassPathResource("spring-context.xml");
        XmlBeanFactory beanFactory=new XmlBeanFactory(resource);
        ConnManager connManager=beanFactory.getBean("connManager", ConnManager.class);

        ConnectionExample connectionExample1=connManager.createConnection();
        ConnectionExample connectionExample2=connManager.createConnection();
        System.out.println(connectionExample1 +" equals "+ connectionExample2);
        connectionExample1.execConnection();


        /*XmlBeanFactory beanFactory=new XmlBeanFactory(resource);
        FruitPlate fruitPlate1=beanFactory.getBean("fruitPlate1",FruitPlate.class);
        fruitPlate1.getFruit();*/

    }

    @Test
    public void testReplacedMethod(){

        XmlBeanFactory beanFactory=new XmlBeanFactory(resource);
        Person person=beanFactory.getBean(Person.class);

        System.out.println(person);

        person.show();

    }
    @Test
    public void test1() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Class clazz=Apple.class;
        try {
            Constructor constructor= clazz.getDeclaredConstructor(new Class[]{String.class,Fruit.class});
            Apple apple= (Apple) constructor.newInstance(new Object[]{"zzz",new Bananer()});
            System.out.println(apple.getFruit());
            System.out.println(constructor);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testSpringXml() throws Exception{

        /*ClassPathResource resource=new ClassPathResource("spring-context.xml");*/

        //System.out.println(resource);
        //ApplicationContext applicationContext=new ClassPathXmlApplicationContext(config);
        //System.out.println(applicationContext);

        /** 初级查看代码 */
        /*XmlBeanFactory beanFactory=new XmlBeanFactory(resource);
        InjectionCollections injectionCollections=beanFactory.getBean(InjectionCollections.class);
        List<String> list=injectionCollections.getList();
        System.out.println(list);
        beanFactory.destroySingletons();*/

       //beanFactory.addBeanPostProcessor(beanFactory.getBean("testBeanPostProcessor", BeanPostProcessor.class));
        //beanFactory.addBeanPostProcessor(new TestBeanPostProcessor());
        /*Happy happy0=beanFactory.getBean("happy",Happy.class);
        System.out.println(happy0);
        happy0.say();*/
        /*Apple apple=beanFactory.getBean("apple",Apple.class);
        System.out.println(apple.getIDCode());
        beanFactory.destroySingletons();*/
        //ConnectionExample connectionExample= (ConnectionExample) beanFactory.getBean("connectionExample");
        //connectionExample.execConnection();


        /*for (String s : list){
            System.out.println(s);
        }
        System.out.println("----------------------");*/
        /*Map<String,String> map=injectionCollections.getMap();
        for (Map.Entry<String,String> entry:map.entrySet()){
            System.out.println(entry.getKey()+"="+entry.getValue());
        }
        System.out.println("----------------------");
        List<Properties> propertiesList=injectionCollections.getPropertiesList();
        for (Properties properties: propertiesList){
            Enumeration<?> enumeration=properties.propertyNames();
            while(enumeration.hasMoreElements()){
                String key=(String)enumeration.nextElement();
                System.out.println(key+"="+properties.get(key));
            }

        }*/
        /** 高级查看代码 */
        ClassPathXmlApplicationContext applicationContext=new ClassPathXmlApplicationContext();
        applicationContext.addApplicationListener(new TestApplicationListener());
        applicationContext.setConfigLocation("spring-context.xml");
        applicationContext.refresh();

        InjectionCollections collections=applicationContext.getBean(InjectionCollections.class);
        System.out.println(collections.getList());
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
         第二波*/
         PathMatchingResourcePatternResolver patternResolver=new PathMatchingResourcePatternResolver();
         System.out.println(patternResolver);
         String locationPattern = "classpath*:"+config;
         Resource[] resources=patternResolver.getResources(locationPattern);

         System.out.println(resources.length);


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
        System.out.println("===================================================================");
        /**
        2 10   0
        2  5   1
        2  2   0
        2  1   1
         */

    }

    private class  TestApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

        @Override
        public void onApplicationEvent(ContextRefreshedEvent event) {
            XmlTest.this.testBitSum();
        }
    }

}
