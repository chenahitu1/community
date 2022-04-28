package com.nowcoder.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;


/**
 * 敏感词过滤器
 * 定义前缀树
 * 根据敏感词，初始化前缀树
 * 编写过滤敏感词方法
 */
@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    //替换符
    private static final String REPLACEMENT = "***";

    //根节点
    private TrieNode rootNode = new TrieNode();

    //初始化方法
    @PostConstruct
    public void init() {
        try (
                //加载字节流 读文件
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ) {
            String keyword;
            while ((keyword = reader.readLine()) != null) {
                //添加前缀树
                this.addKeyword(keyword);
            }

        } catch (Exception e) {
            logger.error("加载文件失败：" + e.getMessage());
        }
    }

    //将一个敏感词添加到前缀树中
    private void addKeyword(String keyword) {
        TrieNode tempNode = rootNode;
        for (int i = 0; i < keyword.length(); i++) {
            char c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);
            if (subNode == null) {
                //初始化子节点
                subNode = new TrieNode();
                tempNode.addSubNode(c, subNode);
            }

            //指针指向子节点 进入下一次循环
            tempNode = subNode;

            //设置结束标识
            if (i == keyword.length() - 1) {
                tempNode.setKeywordEnd(true);
            }
        }
    }

    /**
     * 过滤敏感词
     * <p>
     * 传入待过滤文本
     * 返回过滤后的文本
     */


    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }

        //指针1
        TrieNode tempNode = rootNode;
        //指针2
        int begin = 0;
        //指针3
        int position = 0;

        //变量 记录最终结果
        StringBuilder sb = new StringBuilder();

        //用指针二来判断结束
        while (begin < text.length()) {
            if (position < text.length()) {
                char c = text.charAt(position);

                //跳过符号 因为有些文本为了跳过过滤算法 会添加一些符号
                if (isSymbol(c)) {
                    //若指针一属于根节点，将此符号计入结果，让指针二向下走一步
                    if (tempNode == rootNode) {
                        sb.append(c);
                        begin++;
                    }
                    //无论指针在开头还是在中间指针二都向下面走一步
                    position++;
                    continue;
                }
                //检查下级节点
                tempNode = tempNode.getSubNode(c);
                if (tempNode == null) {
                    //以begin开头的字符串不是敏感词
                    sb.append(text.charAt(begin));
                    //进入下一个位置
                    begin++;
                    position = begin;
                    //重新指向根节点
                    tempNode = rootNode;
                } else if (tempNode.isKeywordEnd()) {
                    //发现敏感词，将begin-position字符串替换掉
                    sb.append(REPLACEMENT);
                    //进入下一个位置
                    begin=++position;
                    //重新指向根节点
                    tempNode = rootNode;
                } else {
                    //检查下一个字符
                    position++;
                }
            }
            //position遍历结束仍未匹配到敏感词
            else {
                sb.append(text.charAt(begin));
                position=++begin;
                tempNode=rootNode;
            }
        }
        return sb.toString();
    }

    //判断是否为符号
    private boolean isSymbol(Character c) {
        //c<0x2E80||c>0x9FFF表示是不是东亚文字 如果是东亚文字就不是符号
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    //前缀树  一棵树就是多个节点构成的  所以我们这边定义一个节点类 通过节点不断获取子节点最终成为一棵树
    private class TrieNode {
        //关键词结束标识，判断是不是到达了前缀树的叶子节点，如果到达了，代表是一个敏感词
        private boolean isKeywordEnd = false;

        //子节点（key是下级字符，value是下级节点）  这边为什么用map呢因为一个节点的子节点有多个 每个节点有值 和指向下一个节点的trieNode结构
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        //为某个节点添加字节点，构造敏感词的前缀树的时候使用
        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        //添加子节点  提供添加子节点的方法供外面调用
        public void addSubNode(Character c, TrieNode node) {
            subNodes.put(c, node);
        }

        //获取子节点  提供获取节点的方法供外面调用
        public TrieNode getSubNode(Character c) {
            return subNodes.get(c);
        }


    }
}
