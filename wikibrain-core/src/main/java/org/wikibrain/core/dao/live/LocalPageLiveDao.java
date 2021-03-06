package org.wikibrain.core.dao.live;

import com.typesafe.config.Config;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.wikibrain.conf.Configuration;
import org.wikibrain.conf.ConfigurationException;
import org.wikibrain.conf.Configurator;
import org.wikibrain.core.dao.DaoException;
import org.wikibrain.core.dao.DaoFilter;
import org.wikibrain.core.dao.LocalPageDao;
import org.wikibrain.core.lang.Language;
import org.wikibrain.core.lang.LanguageSet;
import org.wikibrain.core.model.NameSpace;
import org.wikibrain.core.model.LocalPage;

import org.wikibrain.core.model.Title;

import java.util.*;

/**
 * A Live Wiki API Implementation of LocalPageDao
 * @author Toby "Jiajun" Li
 */

/**
 * Fetch a LocalPage object from Wikipedia Server
 * @param <T> LocalPage object fetched
 */

public class LocalPageLiveDao<T extends LocalPage> implements LocalPageDao<T> {
    /**
     * Sets if we should try to follow the redirects or not. Default is true (to following them).
     * @param followRedirects
     */

    private boolean followRedirects = true;

    public LocalPageLiveDao() throws DaoException {
    }

    //Notice: A DaoException will be thrown if you call the methods below!
    public void clear()throws DaoException{
       throw new UnsupportedOperationException("Can't use this method for remote wiki server!");
    }
    public void beginLoad()throws DaoException{
        throw new UnsupportedOperationException("Can't use this method for remote wiki server!");
    }
    public void endLoad()throws DaoException{
        throw new UnsupportedOperationException("Can't use this method for remote wiki server!");
    }
    public void save(T a)throws DaoException{
        throw new UnsupportedOperationException("Can't use this method for remote wiki server!");
    }
    public int getCount(DaoFilter a)throws DaoException{
        throw new UnsupportedOperationException("Can't use this method for remote wiki server!");
    }
    public Iterable<T> get(DaoFilter a)throws DaoException{
        throw new UnsupportedOperationException("Can't use this method for remote wiki server!");
    }
    public LanguageSet getLoadedLanguages() throws DaoException {
        throw new UnsupportedOperationException("Can't use this method for remote wiki server!");

    }

    public void setFollowRedirects(boolean followRedirects) throws DaoException {
        this.followRedirects = followRedirects;
    }

    /**
     * Get a single page by its title
     *
     * @param title the page's title
     * @param ns the page's namespace
     * @return the requested LocalPage
     * @throws org.wikibrain.core.dao.DaoException if there was an error retrieving the page
     */

    public T getByTitle(Title title, NameSpace ns) throws DaoException{
        Language lang = title.getLanguage();
        LiveAPIQuery.LiveAPIQueryBuilder builder = new LiveAPIQuery.LiveAPIQueryBuilder("INFO", lang)
                .addTitle(title.getCanonicalTitle().replace(" ", "_")).setRedirects(followRedirects);
        QueryReply info = builder.build().getValuesFromQueryResult().get(0);
        return (T)info.getLocalPage(lang);
    }


    public T getById(Language language, int pageId) throws DaoException{
        LiveAPIQuery.LiveAPIQueryBuilder builder = new LiveAPIQuery.LiveAPIQueryBuilder("INFO", language)
                .addPageid(pageId).setRedirects(followRedirects);
        QueryReply info = builder.build().getValuesFromQueryResult().get(0);
        return (T)info.getLocalPage(language);
    }

    /**
     * Get a set of pages by their ids
     * @param language the language of the pages
     * @param pageIds a Collection of page ids
     * @return a map of ids to pages
     * @throws org.wikibrain.core.dao.DaoException if there was an error retrieving the pages
     */
    public Map<Integer, T> getByIds(Language language, Collection<Integer> pageIds) throws DaoException{
        Map<Integer,T> pageMap = new HashMap<Integer, T>();
        for(Integer pageId : pageIds){
            LiveAPIQuery.LiveAPIQueryBuilder builder = new LiveAPIQuery.LiveAPIQueryBuilder("INFO", language)
                    .addPageid(pageId).setRedirects(followRedirects);
            QueryReply info = builder.build().getValuesFromQueryResult().get(0);
            pageMap.put(pageId, (T)info.getLocalPage(language));
        }
        return pageMap;
    }

    /**
     * Get a map of pages by their titles
     * @param language the language of the pages
     * @param titles a Collection of page titles
     * @param ns the namespace of the pages
     * @return a map of titles to pages
     * @throws org.wikibrain.core.dao.DaoException if there was an error retrieving the pages
     */
    public Map<Title, T> getByTitles(Language language, Collection<Title> titles, NameSpace ns) throws DaoException{
        Map<Title, T> pageMap = new HashMap<Title, T>();
        for(Title title : titles){
            LiveAPIQuery.LiveAPIQueryBuilder builder = new LiveAPIQuery.LiveAPIQueryBuilder("INFO", language)
                    .addTitle(title.getCanonicalTitle().replace(" ", "_")).setRedirects(followRedirects);
            QueryReply info = builder.build().getValuesFromQueryResult().get(0);
            pageMap.put(title, (T)info.getLocalPage(language));
        }
        return pageMap;
    }


    /**
     * Get an id from a title. Returns -1 if it doesn't exist.
     * @param title
     * @param language
     * @param nameSpace
     * @return
     */
    public int getIdByTitle(String title, Language language, NameSpace nameSpace) throws DaoException{
        LiveAPIQuery.LiveAPIQueryBuilder builder = new LiveAPIQuery.LiveAPIQueryBuilder("INFO", language)
                .addTitle(title).setRedirects(followRedirects);
        QueryReply info = builder.build().getValuesFromQueryResult().get(0);
        return info.getId();
    }

    /**
     * Get an id from a title. Returns -1 if it doesn't exist.
     * @param title
     * @return
     */
    public int getIdByTitle(Title title) throws DaoException{
        LiveAPIQuery.LiveAPIQueryBuilder builder = new LiveAPIQuery.LiveAPIQueryBuilder("INFO", title.getLanguage())
                .addTitle(title.getCanonicalTitle().replace(" ", "_")).setRedirects(followRedirects);
        QueryReply info = builder.build().getValuesFromQueryResult().get(0);
        return info.getId();
    }

    /**
     * Gets the list of all local page ids for lang = langId and a given namespace
     * @param lang
     * @return
     * @throws DaoException
     */
    public TIntList getAllPageIdsInNamespace(Language lang, NameSpace ns) throws DaoException {
        TIntList pages = new TIntArrayList();
        LiveAPIQuery.LiveAPIQueryBuilder builder = new LiveAPIQuery.LiveAPIQueryBuilder("ALLPAGES", lang);
        builder.setNamespace(ns.getValue());
        LiveAPIQuery query = builder.build();
        List<QueryReply> replyObjects = query.getValuesFromQueryResult();

        for (QueryReply reply : replyObjects) {
            pages.add(reply.getId());
        }

        return  pages;
    }

    /**
     * Gets the local page id -> namespace mappings for lang = langId
     * @param lang
     * @return
     * @throws DaoException
     */
    public TIntIntMap getAllPageIdNamespaceMappings(Language lang) throws DaoException {
        TIntIntMap pages = new TIntIntHashMap();
        LiveAPIQuery.LiveAPIQueryBuilder builder = new LiveAPIQuery.LiveAPIQueryBuilder("ALLPAGES", lang);
        LiveAPIQuery query = builder.build();
        List<QueryReply> replyObjects = query.getValuesFromQueryResult();

        for (QueryReply reply : replyObjects) {
            pages.put(reply.pageId, reply.nameSpace);
        }

        return  pages;
    }

    public static class Provider extends org.wikibrain.conf.Provider<LocalPageDao> {
        public Provider(Configurator configurator, Configuration config) throws ConfigurationException {
            super(configurator, config);
        }

        @Override
        public Class getType() {
            return LocalPageDao.class;
        }

        @Override
        public String getPath() {
            return "dao.localPage";
        }

        @Override
        public LocalPageDao get(String name, Config config, Map<String, String> runtimeParams) throws ConfigurationException {
            if (!config.getString("type").equals("live")) {
                return null;
            }
            try {
                return new LocalPageLiveDao();

            } catch (DaoException e) {
                throw new ConfigurationException(e);
            }
        }
    }
}

