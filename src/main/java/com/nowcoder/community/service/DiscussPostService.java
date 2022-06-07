package com.nowcoder.community.service;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.enity.DiscussPost;
import com.nowcoder.community.util.SensitiveFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class DiscussPostService {

    private static final Logger logger= LoggerFactory.getLogger(DiscussPostService.class);

    @Value("${caffeine.posts.max-size}")
    private int maxSize;

    @Value("${caffeine.posts.expire-seconds}")
    private int expireSeconds;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    //Caffeine核心接口：Cache,LoadingCache,AsyncLoadingCache

    //帖子列表缓存
    private LoadingCache<String,List<DiscussPost>> postListCache;

    //帖子总数缓存
    private LoadingCache<Integer,Integer> postRowsCache;

    @PostConstruct
    public void init(){
        //初始化帖子列表缓存
        postListCache = Caffeine.newBuilder()
                .maximumSize(maxSize)//指的是缓存空间最多存多少个对象
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)//缓存过期时间
                .build(new CacheLoader<String, List<DiscussPost>>() {
                    @Override
                    public List<DiscussPost> load(String key) throws Exception {
                        //声明缓存的数据是从哪里来的
                        if(key==null||key.length()==0){
                            throw new IllegalArgumentException("参数错误！");
                        }
                        String[] params=key.split(":");
                        if(params==null||params.length!=2){
                            throw new IllegalArgumentException("参数错误！");
                        }
                        int offset=Integer.valueOf(params[0]);
                        int limit=Integer.valueOf(params[1]);
                        //这中间可以再加二级缓存

                        logger.debug("load post list from DB");
                        return discussPostMapper.selectDiscussPosts(0,offset,limit,1);

                    }
                });
        //初始化帖子总数缓存
        postRowsCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds,TimeUnit.SECONDS)
                .build(new CacheLoader<Integer, Integer>() {
                    @Override
                    public Integer load(Integer key) throws Exception {

                        logger.debug("load post list from DB");
                        return discussPostMapper.selectDiscussPostRows(key);
                    }
                });
    }
    public List<DiscussPost> findDiscussPosts(int userId,int offset,int limit,int orderModel){
        //只缓存热帖和首页  热帖orderModel=1 首页用户为不存在 userID=0

        if(userId==0&&orderModel==1){
            //启用缓存
            return postListCache.get(offset+":"+limit);

        }
        //访问数据库
        logger.debug("load post list from DB");
        return discussPostMapper.selectDiscussPosts(userId,offset,limit,orderModel);
    }
    public int findDiscussPostRows(int userId){
        //启用缓存
        if(userId==0){
            return postRowsCache.get(userId);
        }
        //访问数据库
        logger.debug("load post rows from DB");
        return discussPostMapper.selectDiscussPostRows(userId);
    }


    public int addDiscussPost(DiscussPost post){
        if(post==null){
            throw new IllegalArgumentException("参数不能为空");
        }
        //转义HTML标记
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));
        //过滤敏感词
        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));

        return discussPostMapper.insertDiscussPost(post);
    }
    public DiscussPost findDiscussPostById(int id){
        return discussPostMapper.selectDiscussPostById(id);
    }

    //更新评论数量
    public int updateCommentCount(int id,int commentCount){
        return discussPostMapper.updateCommentCount(id,commentCount);
    }

    public int updateType(int id,int type){
        return discussPostMapper.updateType(id,type);
    }
    public int updateStatus(int id,int status){
        return discussPostMapper.updateStatus(id,status);
    }

    public int updateScore(int id, double score) {
        return discussPostMapper.updateScore(id, score);
    }
}
