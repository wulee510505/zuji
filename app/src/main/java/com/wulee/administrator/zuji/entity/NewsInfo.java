package com.wulee.administrator.zuji.entity;

import java.util.List;

/**
 * Created by wulee on 2017/9/6 15:31
 */

public class NewsInfo {


    /**
     * code : 200
     * msg : success
     * newslist : [{"ctime":"2017-09-06 12:33","title":"金砖合作扬帆再出发","description":"搜狐国内","picUrl":"http://photocdn.sohu.com/20170906/Img509881807_ss.jpeg","url":"http://news.sohu.com/20170906/n509881956.shtml"},{"ctime":"2017-09-06 11:01","title":"习近平：老朋友要常来常往","description":"搜狐国内","picUrl":"http://photocdn.sohu.com/20170905/Img509727034_ss.gif","url":"http://news.sohu.com/20170906/n509874940.shtml"},{"ctime":"2017-09-06 11:20","title":"一图读懂习近平在新兴市场国家与发展中国家对话会上的发言","description":"搜狐国内","picUrl":"http://photocdn.sohu.com/20170905/Img509729926_ss.gif","url":"http://news.sohu.com/20170906/n509879341.shtml"},{"ctime":"2017-09-06 11:28","title":"【高端说金砖】泰总理：\u201c金砖+\u201d是创新模式","description":"搜狐国内","picUrl":"http://photocdn.sohu.com/20170906/Img509881268_ss.jpeg","url":"http://news.sohu.com/20170906/n509881267.shtml"},{"ctime":"2017-09-06 11:30","title":"彭丽媛出席艾滋病防治宣传校园行 走进厦门大学活动","description":"搜狐国内","picUrl":"http://photocdn.sohu.com/20170906/Img509881807_ss.jpeg","url":"http://news.sohu.com/20170906/n509881806.shtml"},{"ctime":"2017-09-05 19:15","title":"\u201c习主席主持金砖国家领导人厦门会晤\u201d漫评：开启金砖合作新航程","description":"搜狐国内","picUrl":"","url":"http://news.sohu.com/20170905/n509729925.shtml"},{"ctime":"2017-09-05 21:33","title":"李克强在山西考察时强调 加快新旧动能转换 促进经济转型升级 着力脱贫攻坚 推动民生改善","description":"搜狐国内","picUrl":"","url":"http://news.sohu.com/20170905/n509743644.shtml"},{"ctime":"2017-09-05 21:34","title":"俞正声会见布隆迪参议长","description":"搜狐国内","picUrl":"http://photocdn.sohu.com/20170905/Img509727034_ss.gif","url":"http://news.sohu.com/20170905/n509743796.shtml"},{"ctime":"2017-09-05 21:36","title":"刘云山在河北调研时强调 大力弘扬塞罕坝精神 把基层党组织建成坚强战斗堡垒","description":"搜狐国内","picUrl":"http://photocdn.sohu.com/20170905/Img509727034_ss.gif","url":"http://news.sohu.com/20170905/n509743948.shtml"},{"ctime":"2017-09-05 21:37","title":"王岐山在巡察工作座谈会上强调 推动从严治党向基层拓展 回应人民群众的期盼","description":"搜狐国内","picUrl":"http://photocdn.sohu.com/20170905/Img509729926_ss.gif","url":"http://news.sohu.com/20170905/n509744087.shtml"}]
     */

    private int code;
    private String msg;
    private List<NewsEntity> newslist;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<NewsEntity> getNewsEntityList() {
        return newslist;
    }

    public void setNewsEntityList(List<NewsEntity> newslist) {
        this.newslist = newslist;
    }

    public static class NewsEntity {
        /**
         * ctime : 2017-09-06 12:33
         * title : 金砖合作扬帆再出发
         * description : 搜狐国内
         * picUrl : http://photocdn.sohu.com/20170906/Img509881807_ss.jpeg
         * url : http://news.sohu.com/20170906/n509881956.shtml
         */

        private String ctime;
        private String title;
        private String description;
        private String picUrl;
        private String url;

        public String getCtime() {
            return ctime;
        }

        public void setCtime(String ctime) {
            this.ctime = ctime;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getPicUrl() {
            return picUrl;
        }

        public void setPicUrl(String picUrl) {
            this.picUrl = picUrl;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
