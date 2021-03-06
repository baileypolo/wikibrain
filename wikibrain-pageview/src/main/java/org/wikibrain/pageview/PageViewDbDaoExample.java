package org.wikibrain.pageview;

import org.joda.time.DateTime;
import org.wikibrain.conf.ConfigurationException;
import org.wikibrain.conf.Configurator;
import org.wikibrain.core.WikiBrainException;
import org.wikibrain.core.cmd.Env;
import org.wikibrain.core.cmd.EnvBuilder;
import org.wikibrain.core.dao.DaoException;
import org.wikibrain.core.dao.LocalLinkDao;
import org.wikibrain.core.dao.LocalPageDao;
import org.wikibrain.core.lang.Language;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Toby "Jiajun" Li
 */
public class PageViewDbDaoExample {

    public static void main(String args[]) throws ConfigurationException, DaoException, WikiBrainException {


        Language lang = Language.getByLangCode("simple");
        PageViewDbDao pageViewDbDao = new PageViewDbDao(lang);
        Env env = new EnvBuilder().build();
        Configurator configurator = env.getConfigurator();
        LocalPageDao pDao = configurator.get(LocalPageDao.class, "live");
        //Get the number of page views for PageID 47 from 2013-12-8 0:00 to 2013-12-9 0:00
        System.out.println(pageViewDbDao.getPageView(47, 2013, 12, 8, 0, 24));
        int sum = 0;
        //Get the hourly pageview
        for(int i = 0; i < 24; i++ ){
            System.out.printf("%d:00: %d\n", i, pageViewDbDao.getPageView(47, 2013, 12, 8, i));
            sum += pageViewDbDao.getPageView(47, 2013, 12, 8, i);
        }
        System.out.printf("sum: %d\n", sum);
        List<Integer> testList = new ArrayList();
        testList.add(47);
        testList.add(39);
        testList.add(10983);
        //Get the number of page views for PageID {47, 39, 10983} from 2013-12-8 0:00 to 2013-12-9 0:00
        System.out.println(pageViewDbDao.getPageView(testList, 2013, 12, 8, 0, 24));
    }
}
