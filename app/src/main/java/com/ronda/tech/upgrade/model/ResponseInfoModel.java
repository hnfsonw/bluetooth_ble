package com.ronda.tech.upgrade.model;


import java.util.List;

/**
 * @author snow.huang
 * created 2021/1/23 16:19
 * 网络请求结果
 */
public class ResponseInfoModel {

    private int sessionflag;
    private Data data;
    private String method;
    private String ret;
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRet() {
        return ret;
    }

    public void setRet(String ret) {
        this.ret = ret;
    }

    public void setSessionflag(int sessionflag) {
        this.sessionflag = sessionflag;
    }
    public int getSessionflag() {
        return sessionflag;
    }

    public void setData(Data data) {
        this.data = data;
    }
    public Data getData() {
        return data;
    }

    public void setMethod(String method) {
        this.method = method;
    }
    public String getMethod() {
        return method;
    }

    public class Data {

        private String loginname;
        private String password;
        private String open_method;
        private String token_code;
        private String code;
        private String cabinet_id;
        private String mac;
        private String memberName;
        private int count;

        private int pagesize;

        private List<Rows> rows ;

        private int pagenum;

        public void setCount(int count){
            this.count = count;
        }
        public int getCount(){
            return this.count;
        }
        public void setPagesize(int pagesize){
            this.pagesize = pagesize;
        }
        public int getPagesize(){
            return this.pagesize;
        }
        public void setRows(List<Rows> rows){
            this.rows = rows;
        }
        public List<Rows> getRows(){
            return this.rows;
        }
        public void setPagenum(int pagenum){
            this.pagenum = pagenum;
        }
        public int getPagenum(){
            return this.pagenum;
        }

        public String getMac() {
            return mac;
        }

        public void setMac(String mac) {
            this.mac = mac;
        }

        public String getMemberName() {
            return memberName;
        }

        public void setMemberName(String memberName) {
            this.memberName = memberName;
        }

        public String getCabinet_id() {
            return cabinet_id;
        }

        public void setCabinet_id(String cabinet_id) {
            this.cabinet_id = cabinet_id;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getToken_code() {
            return token_code;
        }

        public void setToken_code(String token_code) {
            this.token_code = token_code;
        }

        public void setLoginname(String loginname) {
            this.loginname = loginname;
        }
        public String getLoginname() {
            return loginname;
        }

        public void setPassword(String password) {
            this.password = password;
        }
        public String getPassword() {
            return password;
        }

        public void setOpen_method(String open_method) {
            this.open_method = open_method;
        }
        public String getOpen_method() {
            return open_method;
        }

        public class Rows {
            private String note;

            private long binpackc_id;

            private String vername;

            private String add_time;

            private String url;

            public void setNote(String note){
                this.note = note;
            }
            public String getNote(){
                return this.note;
            }
            public void setBinpackc_id(int binpackc_id){
                this.binpackc_id = binpackc_id;
            }
            public long getBinpackc_id(){
                return this.binpackc_id;
            }
            public void setVername(String vername){
                this.vername = vername;
            }
            public String getVername(){
                return this.vername;
            }
            public void setAdd_time(String add_time){
                this.add_time = add_time;
            }
            public String getAdd_time(){
                return this.add_time;
            }
            public void setUrl(String url){
                this.url = url;
            }
            public String getUrl(){
                return this.url;
            }
        }
    }

}
