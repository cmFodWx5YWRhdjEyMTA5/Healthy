package com.amsu.wear.bean;

/**
 * Created by HP on 2016/11/30.
 */
public class User {
    private String phone;
    private String username;
    private String birthday;
    private String sex;
    private String weight;
    private String height;
    private String area;
    private String email;
    private String icon;
    private String stillRate;
    private int age;

    /*{
                    "ret": "201",
                    "errDesc": {
                        "id": 9,
                        "userid": "18689463192",
                        "userpwd": null,
                        "username": "天空之城",
                        "type": 0,
                        "sex": 1,
                        "birthday": 308246400000,
                        "weight": 50,
                        "height": 160,
                        "address": "江西省南昌市",
                        "phone": "18689463192",
                        "email": "",
                        "icon": "usericons/d9cc909b8de4f858e6a19539b6e19274.png",
                        "signature": "",
                        "createtime": 1481744163000,
                        "updatetime": 1508194725000,
                        "lastlogintime": null,
                        "lastloginversion": "",
                        "state": false,
                        "mainaccount": "",
                        "message": "求助啊",
                        "contactsphone": 0
                    }
                }*/

    public User() {
    }

    public User(String phone, String username, String birthday, String sex, String weight, String height, String area) {
        this.phone = phone;
        this.username = username;
        this.birthday = birthday;
        this.sex = sex;
        this.weight = weight;
        this.height = height;
        this.area = area;
    }

    public User(String phone, String username, String birthday, String sex, String weight, String height, String area, String email) {
        this.phone = phone;
        this.username = username;
        this.birthday = birthday;
        this.sex = sex;
        this.weight = weight;
        this.height = height;
        this.area = area;
        this.email = email;
    }

    public User(String phone, String username, String birthday, String sex, String weight, String height, String area, String email, String icon, String stillRate) {
        this.phone = phone;
        this.username = username;
        this.birthday = birthday;
        this.sex = sex;
        this.weight = weight;
        this.height = height;
        this.area = area;
        this.email = email;
        this.icon = icon;
        this.stillRate = stillRate;
    }

    public String getStillRate() {
        return stillRate;
    }

    public void setStillRate(String stillRate) {
        this.stillRate = stillRate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public User(String phone, String username, String birthday, String sex, String weight, String height, String area, String email, String icon) {
        this.phone = phone;
        this.username = username;
        this.birthday = birthday;
        this.sex = sex;
        this.weight = weight;
        this.height = height;
        this.area = area;
        this.email = email;
        this.icon = icon;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "User{" +
                "phone='" + phone + '\'' +
                ", username='" + username + '\'' +
                ", birthday='" + birthday + '\'' +
                ", sex='" + sex + '\'' +
                ", weight='" + weight + '\'' +
                ", height='" + height + '\'' +
                ", area='" + area + '\'' +
                ", email='" + email + '\'' +
                ", icon='" + icon + '\'' +
                ", stillRate='" + stillRate + '\'' +
                ", age=" + age +
                '}';
    }
}
