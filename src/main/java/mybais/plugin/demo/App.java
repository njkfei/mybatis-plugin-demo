package mybais.plugin.demo;


import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Hello world!
 *
 */
@Log4j2(topic = "application")
@Component
public class App
{

    public static void main( String[] args )
    {
        log.warn("application start==>");
        ApplicationContext ac = new ClassPathXmlApplicationContext("classpath:ApplicationContext.xml");

     /*   recommendFootHcMapper = ac.getBean(RecommendFootHcMapper.class);


        RecommendFootHc recommendFootHc = recommendFootHcMapper.selectById(109L);

        if(recommendFootHc != null){
            log.info(new Gson().toJson(recommendFootHc));
        }else {
            log.info("null ");
        }

        recommendFootHc =  recommendFootHcMapper.selectByPrimaryKey(100L);
        if(recommendFootHc != null){
            log.info(new Gson().toJson(recommendFootHc));
        }else {
            log.info("null ");
        }*/

        TMapper tMapper = ac.getBean(TMapper.class);

        T t =  tMapper.selectByUid(109l);


        if(t != null){
            log.info(new Gson().toJson(t));

            t.setUid(110l);
            tMapper.insert2(t);
        }else {
            log.info("null ");
        }
        t =  tMapper.selectByUid(110l);

        if(t != null){
            log.info(new Gson().toJson(t));
            t.setUid(197l);
            tMapper.insert2(t);
        }else {
            log.info("null ");
        }
        t =  tMapper.selectByUid(197l);

        if(t != null){
            log.info(new Gson().toJson(t));
        }else {
            log.info("null ");
        }
    }
}
